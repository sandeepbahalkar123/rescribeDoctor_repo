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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.rescribe.doctor.R;
import com.rescribe.doctor.helpers.database.AppDBHelper;
import com.rescribe.doctor.model.chat.history.ChatHistory;
import com.rescribe.doctor.model.chat.history.ChatHistoryModel;
import com.rescribe.doctor.model.patient.patient_connect.ChatPatientConnectModel;
import com.rescribe.doctor.model.patient.patient_connect.PatientData;
import com.rescribe.doctor.network.RequestPool;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.singleton.Device;
import com.rescribe.doctor.ui.activities.PatientConnectActivity;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.Config;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.volley.Request.Method.GET;
import static com.rescribe.doctor.util.RescribeConstants.SUCCESS;

public class ChatBackUpService extends Service {
    private static final String CHANNEL_CHAT_BACKUP = "chat_backup";
    public static boolean RUNNING = false;

    private static final String LOG_TAG = "ChatBackUpService";

    public static final String STATUS = "status";
    public static final String CHAT_BACKUP = "com.rescribe.doctor.CHAT_BACKUP";

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private int patientIndex = 0;
    private boolean isFailed = true;
    private AppDBHelper appDBHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        Intent notificationIntent = new Intent(this, PatientConnectActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            mBuilder = new NotificationCompat.Builder(this, CHANNEL_CHAT_BACKUP);
        } else
            mBuilder = new NotificationCompat.Builder(this);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.logo);

        Notification notification = mBuilder
                .setContentTitle("Chat Backup")
                .setTicker("Restoring messages")
                .setContentText("Restoring messages")
                .setSmallIcon(R.drawable.logosmall)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent).build();

        startForeground(RescribeConstants.FOREGROUND_SERVICE, notification);
        appDBHelper = new AppDBHelper(this);
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
                Log.i(LOG_TAG, "Received Start Foreground Intent ");
                // Start Downloading
                request();
            }
        } else stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannel() {
        // Create the channel object with the unique ID CONNECT_CHANNEL
        NotificationChannel connectChannel = new NotificationChannel(
                CHANNEL_CHAT_BACKUP, "SYNC Patients",
                NotificationManager.IMPORTANCE_DEFAULT);

        // Configure the channel's initial settings
        connectChannel.setLightColor(Color.GREEN);
        connectChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        // Submit the notification channel object to the notification manager
        mNotifyManager.createNotificationChannel(connectChannel);
    }

    private void request() {

        RUNNING = true;

        mBuilder.setContentText("Backup Restoring")
                // Removes the progress bar
                .setProgress(0, 0, true);
        mNotifyManager.notify(RescribeConstants.FOREGROUND_SERVICE, mBuilder.build());

        String id = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, this);
        StringRequest stringRequest = new StringRequest(GET, Config.BASE_URL + Config.GET_PATIENT_LIST + id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ChatPatientConnectModel patientConnectModel = new Gson().fromJson(response, ChatPatientConnectModel.class);
                        if (patientConnectModel.getCommon().getStatusCode().equals(SUCCESS)) {
                            ArrayList<PatientData> patientDataList = patientConnectModel.getPatientListData().getPatientDataList();
                            if (patientDataList.isEmpty()) {
                                CommonMethods.showToast(ChatBackUpService.this, patientConnectModel.getCommon().getStatusMessage());
                                isFailed = false;
                                restored();
                            } else
                                restoreMessages(patientDataList);
                        } else {
                            restored();
                            CommonMethods.showToast(ChatBackUpService.this, patientConnectModel.getCommon().getStatusMessage());
                            RescribePreferencesManager.putBoolean(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.BACK_UP, true, ChatBackUpService.this);
                            stopSelf();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        restored();
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

    private void restoreMessages(final ArrayList<PatientData> patientDataList) {

        PatientData patientData = patientDataList.get(patientIndex);

        String docId = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, this);
        String url = Config.BASE_URL + Config.CHAT_HISTORY + "user1id=" + docId + "&user2id=" + patientData.getId();

        patientIndex += 1;

        StringRequest stringRequest = new StringRequest(GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ChatHistoryModel chatHistoryModel = new Gson().fromJson(response, ChatHistoryModel.class);
                        if (chatHistoryModel.getCommon().getStatusCode().equals(SUCCESS)) {
                            List<ChatHistory> chatHistory = chatHistoryModel.getHistoryData().getChatHistory();
                            appDBHelper.insertChatMessage(chatHistory);
                            if (patientDataList.size() > patientIndex) {
                                restoreMessages(patientDataList);
                            } else {
                                isFailed = false;
                                restored();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (patientDataList.size() > patientIndex) {
                            restoreMessages(patientDataList);
                        } else {
                            restored();
                        }
                    }
                }
        ) {
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

    private void restored() {

        RUNNING = false;

        patientIndex = 0;

        Intent intent = new Intent(CHAT_BACKUP);
        intent.putExtra(STATUS, isFailed);
        sendBroadcast(intent);

        mBuilder.setContentText(!isFailed ? "Backup Restored" : "Backup Restore Failed")
                // Removes the progress bar
                .setProgress(0, 0, false);
        mNotifyManager.notify(RescribeConstants.FOREGROUND_SERVICE, mBuilder.build());

        RescribePreferencesManager.putBoolean(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.BACK_UP, !isFailed, ChatBackUpService.this);

        stopForeground(true);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "In onDestroy");
    }
}
