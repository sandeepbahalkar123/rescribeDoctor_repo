package com.rescribe.doctor.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.rescribe.doctor.R;
import com.rescribe.doctor.model.patient.patient_connect.ChatPatientConnectModel;
import com.rescribe.doctor.model.patient.patient_connect.PatientData;
import com.rescribe.doctor.network.RequestPool;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.singleton.Device;
import com.rescribe.doctor.ui.activities.PatientConnectActivity;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.Config;
import com.rescribe.doctor.util.RescribeConstants;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Request.Method.GET;
import static com.rescribe.doctor.util.RescribeConstants.SUCCESS;

public class ChatBackUpService extends Service {
    private static final String LOG_TAG = "ChatBackUpService";
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {

        if (intent.getAction().equals(RescribeConstants.STARTFOREGROUND_ACTION)) {

            Log.i(LOG_TAG, "Received Start Foreground Intent ");
            Intent notificationIntent = new Intent(this, PatientConnectActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);

            mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder = new NotificationCompat.Builder(this);
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);

            Notification notification = mBuilder
                    .setContentTitle("Chat Backup")
                    .setTicker("Restoring messages")
                    .setContentText("Restoring messages")
                    .setSmallIcon(R.drawable.logosmall)
                    .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(pendingIntent).build();

            startForeground(RescribeConstants.FOREGROUND_SERVICE, notification);

            // Start Downloading
            try {
                request();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void request() throws JSONException {

        mBuilder.setContentText("Uploading")
                // Removes the progress bar
                .setProgress(0, 0, true);
        mNotifyManager.notify(RescribeConstants.FOREGROUND_SERVICE, mBuilder.build());

        String id = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, this);
        StringRequest stringRequest = new StringRequest(GET, Config.GET_PATIENT_LIST + id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ChatPatientConnectModel patientConnectModel = new Gson().fromJson(response, ChatPatientConnectModel.class);
                        if (patientConnectModel.getCommon().getStatusCode().equals(SUCCESS)) {
                            restoreMessages(patientConnectModel.getPatientListData().getPatientDataList());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mBuilder.setContentText("Restore Failed")
                                // Removes the progress bar
                                .setProgress(0, 0, false);
                        mNotifyManager.notify(RescribeConstants.FOREGROUND_SERVICE, mBuilder.build());
                    }
                }
        )

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Device device = Device.getInstance(ChatBackUpService.this);
                Map<String, String> headerParams = new HashMap<>();
                String authorizationString = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.AUTHTOKEN, ChatBackUpService.this);
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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(1000 * 60, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setTag("BackUpRequest");
        RequestPool.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void restoreMessages(ArrayList<PatientData> patientDataList) {
        mBuilder.setContentText("Uploading")
                // Removes the progress bar
                .setProgress(0, 0, true);
        mNotifyManager.notify(RescribeConstants.FOREGROUND_SERVICE, mBuilder.build());

        String id = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, this);
        StringRequest stringRequest = new StringRequest(GET, Config.GET_PATIENT_LIST + id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ChatPatientConnectModel patientConnectModel = new Gson().fromJson(response, ChatPatientConnectModel.class);
                        if (patientConnectModel.getCommon().getStatusCode().equals(SUCCESS)) {
                            restoreMessages(patientConnectModel.getPatientListData().getPatientDataList());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mBuilder.setContentText("Restore Failed")
                                // Removes the progress bar
                                .setProgress(0, 0, false);
                        mNotifyManager.notify(RescribeConstants.FOREGROUND_SERVICE, mBuilder.build());
                    }
                }
        )

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Device device = Device.getInstance(ChatBackUpService.this);
                Map<String, String> headerParams = new HashMap<>();
                String authorizationString = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.AUTHTOKEN, ChatBackUpService.this);
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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(1000 * 60, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setTag("BackUpRequest");
        RequestPool.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "In onDestroy");
    }
}
