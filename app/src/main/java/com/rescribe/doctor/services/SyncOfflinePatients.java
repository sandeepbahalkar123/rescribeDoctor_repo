package com.rescribe.doctor.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.rescribe.doctor.R;
import com.rescribe.doctor.helpers.database.AppDBHelper;
import com.rescribe.doctor.model.login.LoginModel;
import com.rescribe.doctor.model.patient.add_new_patient.PatientDetail;
import com.rescribe.doctor.model.patient.add_new_patient.SyncPatientsRequest;
import com.rescribe.doctor.model.patient.doctor_patients.sync_resp.PatientUpdateDetail;
import com.rescribe.doctor.model.patient.doctor_patients.sync_resp.SyncPatientsModel;
import com.rescribe.doctor.model.requestmodel.login.LoginRequestModel;
import com.rescribe.doctor.network.RequestPool;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.singleton.Device;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.Config;
import com.rescribe.doctor.util.RescribeConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Request.Method.POST;
import static com.rescribe.doctor.services.LoadAllPatientsService.STATUS;
import static com.rescribe.doctor.util.Config.ADD_PATIENTS_SYNC;
import static com.rescribe.doctor.util.Config.BASE_URL;
import static com.rescribe.doctor.util.RescribeConstants.INVALID_LOGIN_PASSWORD;
import static com.rescribe.doctor.util.RescribeConstants.SUCCESS;

public class SyncOfflinePatients {

    public static final String PATIENT_SYNC = "com.rescribe.doctor.PATIENT_SYNC";
    public static final String PATIENT_SYNC_LIST = "SyncList";
    private static final String LOG_TAG = "SyncOfflinePatients";
    private static final int SYNC_NOTIFICATION_ID = 122;
    private static final String CHANNEL_SYNC_PATIENT = "SYNC_patient";
    private static final String TAG = "SyncPatients";

    private Context mContext;
    private AppDBHelper appDBHelper;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotifyManager;
    private boolean isFailed = false;
    private Gson gson;

    SyncOfflinePatients() {
    }

    void onCreate(Context mContext, NotificationManager notificationManager) {
        this.mContext = mContext;
        appDBHelper = new AppDBHelper(this.mContext);
        gson = new Gson();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Create the channel object with the unique ID CONNECT_CHANNEL
            NotificationChannel connectChannel = new NotificationChannel(
                    CHANNEL_SYNC_PATIENT, "SYNC Patients",
                    NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the channel's initial settings
            connectChannel.setLightColor(Color.GREEN);
            connectChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            // Submit the notification channel object to the notification manager
            notificationManager.createNotificationChannel(connectChannel);
        }

        if (RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.LOGIN_STATUS, mContext).equals(RescribeConstants.YES))
            check();
    }

    public void check() {
        ArrayList<PatientDetail> offlineAddedPatients = appDBHelper.getOfflinePatientsToUpload();
        Log.e(LOG_TAG, "Checking offline patients " + offlineAddedPatients.size());

        if (!offlineAddedPatients.isEmpty()) {

            Intent notificationIntent = new Intent();
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0,
                    notificationIntent, 0);

            mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder = new NotificationCompat.Builder(mContext, CHANNEL_SYNC_PATIENT);
            Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.logo);

            mBuilder.setContentTitle("Syncing patients")
                    .setTicker("Syncing")
                    .setSmallIcon(R.drawable.logosmall)
                    .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(pendingIntent).build();

            mBuilder.setContentText("Downloading patients")
                    // Removes the progress bar
                    .setProgress(0, 0, true);
            mNotifyManager.notify(SYNC_NOTIFICATION_ID, mBuilder.build());

            syncOfflinePatients(offlineAddedPatients);
        }
    }

    private void syncOfflinePatients(final ArrayList<PatientDetail> offlineAddedPatients) {
        SyncPatientsRequest mSyncPatientsRequest = new SyncPatientsRequest();
        String id = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, mContext);
        mSyncPatientsRequest.setDocId(id);
        mSyncPatientsRequest.setPatientDetails(offlineAddedPatients);

        JSONObject jsonObject = null;
        try {
            String jsonString = gson.toJson(mSyncPatientsRequest);
            CommonMethods.Log(LOG_TAG, "jsonRequest:--" + jsonString);
            if (!jsonString.equals("null"))
                jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(POST, BASE_URL + ADD_PATIENTS_SYNC, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        SyncPatientsModel mSyncPatientsModel = gson.fromJson(response.toString(), SyncPatientsModel.class);
                        if (mSyncPatientsModel.getCommon().getStatusCode().equals(SUCCESS)) {
                            ArrayList<PatientUpdateDetail> patientUpdateDetails = mSyncPatientsModel.getData().getPatientUpdateDetails();
                            if (!patientUpdateDetails.isEmpty()) {
                                appDBHelper.updateOfflinePatientANDRecords(mSyncPatientsModel.getData().getPatientUpdateDetails());
                                isFailed = false;
                                synced(patientUpdateDetails);
                            } else {
                                isFailed = true;
                                synced(null);
                            }
                        } else {
                            isFailed = true;
                            synced(null);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null) {
                    if (error instanceof TimeoutError) {
                        if (error.getMessage().equalsIgnoreCase("java.io.IOException: No authentication challenges found") || error.getMessage().equalsIgnoreCase("invalid_grant")) {
                            tokenRefreshRequest(offlineAddedPatients);
                        } else {
                            isFailed = true;
                            synced(null);
                        }
                    } else if (error instanceof AuthFailureError) {
                        tokenRefreshRequest(offlineAddedPatients);
                    } else {
                        isFailed = true;
                        synced(null);
                    }
                } else {
                    isFailed = true;
                    synced(null);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Device device = Device.getInstance(mContext);
                Map<String, String> headerParams = new HashMap<>();
                String authorizationString = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.AUTHTOKEN, mContext);
                headerParams.put(RescribeConstants.CONTENT_TYPE, RescribeConstants.APPLICATION_JSON);
                headerParams.put(RescribeConstants.AUTHORIZATION_TOKEN, authorizationString);
                headerParams.put(RescribeConstants.DEVICEID, device.getDeviceId());
                headerParams.put(RescribeConstants.OS, device.getOS());
                headerParams.put(RescribeConstants.OSVERSION, device.getOSVersion());
                headerParams.put(RescribeConstants.DEVICE_TYPE, device.getDeviceType());
                CommonMethods.Log(LOG_TAG, "setHeaderParams:" + headerParams.toString());
                return headerParams;
            }
        };
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(1000 * 60, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonRequest.setTag("SyncingPatientsRequest");
        RequestPool.getInstance(mContext).addToRequestQueue(jsonRequest);
    }


    private void tokenRefreshRequest(final ArrayList<PatientDetail> offlineAddedPatients) {
        CommonMethods.Log(TAG, "Refresh token while sending refresh token api: ");
        String url = Config.BASE_URL + Config.LOGIN_URL;

        LoginRequestModel loginRequestModel = new LoginRequestModel();

        loginRequestModel.setEmailId(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.EMAIL, mContext));
        loginRequestModel.setPassword(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.PASSWORD, mContext));
        if (!(RescribeConstants.BLANK.equalsIgnoreCase(loginRequestModel.getEmailId()) &&
                RescribeConstants.BLANK.equalsIgnoreCase(loginRequestModel.getPassword()))) {

            JSONObject jsonObject = null;
            try {
                String jsonString = gson.toJson(loginRequestModel);
                CommonMethods.Log(TAG, "jsonRequest:--" + jsonString);
                if (!jsonString.equals("null"))
                    jsonObject = new JSONObject(jsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonRequest = new JsonObjectRequest(POST, url, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            LoginModel loginModel = gson.fromJson(response.toString(), LoginModel.class);
                            if (loginModel.getCommon().getStatusCode().equals(SUCCESS)) {
                                RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.AUTHTOKEN, loginModel.getDoctorLoginData().getAuthToken(), mContext);
                                RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.LOGIN_STATUS, RescribeConstants.YES, mContext);
                                RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, String.valueOf(loginModel.getDoctorLoginData().getDocDetail().getDocId()), mContext);
                                RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.AUTHTOKEN, loginModel.getDoctorLoginData().getAuthToken(), mContext);
                                syncOfflinePatients(offlineAddedPatients);
                            } else if (!loginModel.getCommon().isSuccess() && loginModel.getCommon().getStatusCode().equals(INVALID_LOGIN_PASSWORD)) {
                                isFailed = true;
                                synced(null);
                                CommonMethods.showToast(mContext, loginModel.getCommon().getStatusMessage());
                                CommonMethods.logout(mContext, appDBHelper);
                            } else
                                CommonMethods.showToast(mContext, loginModel.getCommon().getStatusMessage());
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    CommonMethods.showToast(mContext, "Failed to refresh token.");
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Device device = Device.getInstance(mContext);
                    Map<String, String> headerParams = new HashMap<>();
                    headerParams.put(RescribeConstants.CONTENT_TYPE, RescribeConstants.APPLICATION_JSON);
                    headerParams.put(RescribeConstants.DEVICEID, device.getDeviceId());
                    headerParams.put(RescribeConstants.OS, device.getOS());
                    headerParams.put(RescribeConstants.OSVERSION, device.getOSVersion());
                    headerParams.put(RescribeConstants.DEVICE_TYPE, device.getDeviceType());
                    CommonMethods.Log(TAG, "setHeaderParams:" + headerParams.toString());
                    return headerParams;
                }
            };
            jsonRequest.setRetryPolicy(new DefaultRetryPolicy(1000 * 60, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            jsonRequest.setTag("LoginRequest");
            RequestPool.getInstance(mContext).addToRequestQueue(jsonRequest);
        }
    }

    private void synced(ArrayList<PatientUpdateDetail> list) {

        Intent intent = new Intent(PATIENT_SYNC);
        intent.putExtra(STATUS, isFailed);
        if (list != null)
            intent.putExtra(PATIENT_SYNC_LIST, list);
        mContext.sendBroadcast(intent);

        if (list != null) {
            mBuilder.setContentText(isFailed ? "Sync patients failed" : "Sync patients completed")
                    // Removes the progress bar
                    .setProgress(0, 100, false);
            mNotifyManager.notify(SYNC_NOTIFICATION_ID, mBuilder.build());
        } else
            mNotifyManager.cancel(SYNC_NOTIFICATION_ID);

        if (mContext instanceof MQTTService)
            ((MQTTService) mContext).checkPendingRecords();
    }

    void onDestroy() {
    }
}
