package com.rescribe.doctor.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
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
import com.rescribe.doctor.model.patient.doctor_patients.MyPatientBaseModel;
import com.rescribe.doctor.model.patient.doctor_patients.PatientList;
import com.rescribe.doctor.model.request_patients.RequestSearchPatients;
import com.rescribe.doctor.model.requestmodel.login.LoginRequestModel;
import com.rescribe.doctor.network.RequestPool;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.singleton.Device;
import com.rescribe.doctor.ui.activities.my_patients.MyPatientsActivity;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.Config;
import com.rescribe.doctor.util.RescribeConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Request.Method.POST;
import static com.rescribe.doctor.util.Config.BASE_URL;
import static com.rescribe.doctor.util.Config.GET_MY_PATIENTS_LIST;
import static com.rescribe.doctor.util.Config.GET_PATIENTS_SYNC;
import static com.rescribe.doctor.util.RescribeConstants.INVALID_LOGIN_PASSWORD;
import static com.rescribe.doctor.util.RescribeConstants.SUCCESS;

public class LoadAllPatientsService extends Service {
    public static final String STATUS = "status";
    public static final String LOAD_ALL_PATIENTS = "com.rescribe.doctor.LOAD_ALL_PATIENTS";
    private static final String CHANNEL_PATIENT_DOWNLOAD = "patient_download";
    private static final String TAG = "LOAD_ALL_PATIENT";
    private static final int RECORD_COUNT = 50;
    public static boolean RUNNING = false;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private int pageCount = 0;
    private boolean isFailed = true;
    private AppDBHelper appDBHelper;
    private Gson gson;
    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        Intent notificationIntent = new Intent(this, MyPatientsActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            mBuilder = new NotificationCompat.Builder(this, CHANNEL_PATIENT_DOWNLOAD);
        } else
            mBuilder = new NotificationCompat.Builder(this);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.logo);

        Notification notification = mBuilder
                .setContentTitle("Download all patients")
                .setTicker("Downloading")
                .setContentText("Downloading patients")
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent).build();

        startForeground(RescribeConstants.FOREGROUND_SERVICE, notification);

        appDBHelper = new AppDBHelper(this);
        gson = new Gson();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {

        if (intent.getAction() != null) {
            if (intent.getAction().equals(RescribeConstants.STARTFOREGROUND_ACTION)) {
                Log.i(TAG, "Received Start Foreground Intent ");
                // Start Downloading
                request(RescribePreferencesManager.getBoolean(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.PATIENT_DOWNLOAD, mContext));
            }
        } else stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannel() {
        // Create the channel object with the unique ID CONNECT_CHANNEL
        NotificationChannel connectChannel = new NotificationChannel(
                CHANNEL_PATIENT_DOWNLOAD, "SYNC Patients",
                NotificationManager.IMPORTANCE_DEFAULT);

        // Configure the channel's initial settings
        connectChannel.setLightColor(Color.GREEN);
        connectChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        // Submit the notification channel object to the notification manager
        mNotifyManager.createNotificationChannel(connectChannel);
    }

    private void request(final boolean isDownloaded) {

        Log.i(TAG, "Checking is patients downloaded" + isDownloaded);

        RUNNING = true;
        mBuilder.setContentText("Downloading patients")
                // Removes the progress bar
                .setProgress(0, 0, true);

        mNotifyManager.notify(RescribeConstants.FOREGROUND_SERVICE, mBuilder.build());

        RequestSearchPatients mRequestSearchPatients = new RequestSearchPatients();

        String id = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, this);
        mRequestSearchPatients.setPageNo(pageCount);
        mRequestSearchPatients.setDocId(Integer.valueOf(id));
        mRequestSearchPatients.setSearchText("");
        mRequestSearchPatients.setPaginationSize(RECORD_COUNT);

        String date = CommonMethods.getCurrentTimeStamp(RescribeConstants.DATE_PATTERN.UTC_PATTERN);
        if (isDownloaded) {
            date = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.PATIENT_DOWNLOAD_DATE, this);
        } else {
            date = CommonMethods.getCurrentTimeStamp(RescribeConstants.DATE_PATTERN.UTC_PATTERN);
        }
        Log.e("date", "--" + date);

        mRequestSearchPatients.setDate(date);

        JSONObject jsonObject = null;
        try {
            String jsonString = gson.toJson(mRequestSearchPatients);
            CommonMethods.Log(TAG, "jsonRequest:--" + jsonString);
            if (!jsonString.equals("null"))
                jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        String url = isDownloaded ? BASE_URL + GET_PATIENTS_SYNC : BASE_URL + GET_MY_PATIENTS_LIST;
        Log.e("URL:--", "--" + url);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        CommonMethods.Log(TAG, response.toString());
                        MyPatientBaseModel myPatientBaseModel = gson.fromJson(response.toString(), MyPatientBaseModel.class);
                        if (myPatientBaseModel.getCommon().getStatusCode().equals(SUCCESS)) {
                            ArrayList<PatientList> patientList = myPatientBaseModel.getPatientDataModel().getPatientList();
                            if (patientList.isEmpty()) {
                                isFailed = false;
                                restored(patientList.size());
                            } else {
                                // add in database
                                appDBHelper.addNewPatient(patientList, true);

                                if (patientList.size() < RECORD_COUNT) {
                                    // after add database
                                    isFailed = false;
                                    restored(patientList.size());
                                } else {
                                    request(isDownloaded);
                                    pageCount += 1;
                                }
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null) {
                    if (error instanceof TimeoutError) {
                        if (error.getMessage().equalsIgnoreCase("java.io.IOException: No authentication challenges found") || error.getMessage().equalsIgnoreCase("invalid_grant")) {
                            tokenRefreshRequest(isDownloaded);
                        } else {
                            isFailed = true;
                            restored(0);
                        }
                    } else if (error instanceof AuthFailureError) {
                        tokenRefreshRequest(isDownloaded);
                    } else {
                        isFailed = true;
                        restored(0);
                    }
                } else {
                    isFailed = true;
                    restored(0);
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
                CommonMethods.Log(TAG, "setHeaderParams:" + headerParams.toString());
                return headerParams;
            }
        };
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(1000 * 60, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonRequest.setTag("MyPatientRequest");
        RequestPool.getInstance(this).addToRequestQueue(jsonRequest);
    }

    private void tokenRefreshRequest(final boolean isDownloaded) {
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
                                request(isDownloaded);
                            } else if (!loginModel.getCommon().isSuccess() && loginModel.getCommon().getStatusCode().equals(INVALID_LOGIN_PASSWORD)) {
                                isFailed = true;
                                restored(0);
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
            RequestPool.getInstance(this).addToRequestQueue(jsonRequest);
        }
    }

    private void restored(int size) {

        Log.i(TAG, "Patients downloaded " + isFailed);
        if (size > 0)
            CommonMethods.showToast(mContext, !isFailed ? "Downloaded all patients" : "Patients download failed");

        pageCount = 0;
        RUNNING = false;

        Intent intent = new Intent(LOAD_ALL_PATIENTS);
        intent.putExtra(STATUS, isFailed);
        sendBroadcast(intent);

        mBuilder.setContentText(!isFailed ? "Downloaded all patients" : "Patients download failed")
                // Removes the progress bar
                .setProgress(0, 0, false);
        mNotifyManager.notify(RescribeConstants.FOREGROUND_SERVICE, mBuilder.build());

        RescribePreferencesManager.putBoolean(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.PATIENT_DOWNLOAD, !isFailed, mContext);
        if (size != 0) {
            RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.PATIENT_DOWNLOAD_DATE, CommonMethods.getCurrentTimeStamp(RescribeConstants.DATE_PATTERN.UTC_PATTERN), mContext);
        }
        stopForeground(true);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "In onDestroy");
    }
}
