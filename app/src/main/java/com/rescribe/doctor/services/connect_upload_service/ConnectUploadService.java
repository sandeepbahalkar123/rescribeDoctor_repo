package com.rescribe.doctor.services.connect_upload_service;

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
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.google.gson.Gson;
import com.rescribe.doctor.R;
import com.rescribe.doctor.helpers.database.AppDBHelper;
import com.rescribe.doctor.model.chat.MQTTMessage;
import com.rescribe.doctor.model.chat.uploadfile.ChatFileUploadModel;
import com.rescribe.doctor.services.MQTTService;
import com.rescribe.doctor.singleton.RescribeApplication;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;
import java.util.HashMap;

import static com.rescribe.doctor.broadcast_receivers.ReplayBroadcastReceiver.MESSAGE_LIST;
import static com.rescribe.doctor.services.MQTTService.SEND_MESSAGE;

import static com.rescribe.doctor.util.RescribeConstants.APPOINTMENT_STATUS.COMPLETED;

public class ConnectUploadService extends Service {

    public static final String CONNECT_UPLOAD = "org.bmnepali.imageupload.service.receiver";
    public static final String RESULT = "result";

    private static final String PATIENT_CONNECT_UPLOAD = "patient_connect_file_upload";
    private static final int CONNECT_UPLOAD_FOREGROUND_ID = 12345;
    public static final String MQTT_MESSAGE = "mqtt_message";
    public static final String HEADERSLIST = "headerMap";
    private String BASE_URL;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;
    public int index = 0;
    ArrayList<MQTTMessage> mqttMessage = new ArrayList<>();
    private AppDBHelper appDBHelper;
    HashMap<String, String> mapHeaders;

    @Override
    public void onCreate() {
        super.onCreate();
        if (appDBHelper == null)
            appDBHelper = new AppDBHelper(this);

        getNotificationManager();
        createChannel();
        Intent notificationIntent = new Intent();
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Create the channel object with the unique ID CONNECT_CHANNEL
            NotificationChannel connectUploadChannel = new NotificationChannel(
                    PATIENT_CONNECT_UPLOAD, "Patient Connect File Upload",
                    NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the channel's initial settings
            connectUploadChannel.setLightColor(Color.GREEN);
            connectUploadChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            // Submit the mBuilder channel object to the mBuilder manager
            mNotificationManager.createNotificationChannel(connectUploadChannel);
        }

        mBuilder = new NotificationCompat.Builder(this, PATIENT_CONNECT_UPLOAD);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.logo);

        Notification notification = mBuilder.setContentTitle("Connect File Uploading")
                .setTicker("Uploading")
                .setSmallIcon(R.drawable.logosmall)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent).build();

        startForeground(CONNECT_UPLOAD_FOREGROUND_ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
        MQTTMessage mqttM = intent.getParcelableExtra(MQTT_MESSAGE);
        mqttMessage.add(mqttM);
        mapHeaders = (HashMap<String, String>) intent.getSerializableExtra(HEADERSLIST);
        BASE_URL = intent.getStringExtra("URL");


        mBuilder.setContentText("Uploading " + mqttMessage.size() + (mqttMessage.size() == 1 ? " File" : " Files"));
        // Removes the progress bar
//                        .setProgress(connectUploads.size(), uploadInfo.getProgressPercent(), false);
        mNotificationManager.notify(CONNECT_UPLOAD_FOREGROUND_ID, mBuilder.build());

        // for (int i = index; i < mqttMessage.size(); i++) {
        if (index ==0) {
            MQTTMessage mqttMsg = mqttMessage.get(index);
            imageUpload(mqttMsg.getFileUrl(), mapHeaders);
        }
        //  }


        return Service.START_STICKY;
    }


    public void createChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Create the channel object with the unique ID CONNECT_CHANNEL
            NotificationChannel connectChannel = new NotificationChannel(
                    PATIENT_CONNECT_UPLOAD, "Connect",
                    NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the channel's initial settings
            connectChannel.setLightColor(Color.GREEN);
            connectChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            // Submit the mBuilder channel object to the mBuilder manager
            getNotificationManager().createNotificationChannel(connectChannel);
        }
    }


    public NotificationManager getNotificationManager() {
        if (mNotificationManager == null) {
            mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mNotificationManager;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }


    private void imageUpload(String imagePath, HashMap<String, String> mapHeaders) {

        appDBHelper.updateMessageUploadStatus(mqttMessage.get(index).getMsgId(), RescribeConstants.FILE_STATUS.UPLOADING);

        Log.e("imagePath122--", "-" + imagePath);
        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, BASE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response", response);
                        publishResults(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                String msg = "{\"common\":{\"success\":false,\"statusCode\":400,\"statusMessage\":\"" + error.getMessage() + "\"} }";
                publishResults(msg);
            }
        });
        smr.setHeaders(mapHeaders);
        smr.addFile("chatDoc", imagePath);
        RescribeApplication.getInstance().addToRequestQueue(smr);

    }

    private void publishResults(String result) {
        Log.d("chat upload---", result);

        Gson gson = new Gson();
        ChatFileUploadModel common = gson.fromJson(result, ChatFileUploadModel.class);
        if (common.getCommon().isSuccess()) {
            mBuilder.setContentText("Uploading " + (mqttMessage.size() - index) + ((mqttMessage.size() - index) == 1 ? " File" : " Files"));
            // Removes the progress bar
//                        .setProgress(connectUploads.size(), uploadInfo.getProgressPercent(), false);
            mNotificationManager.notify(CONNECT_UPLOAD_FOREGROUND_ID, mBuilder.build());

            Log.e("index sucess", "" + index);

            appDBHelper.updateMessageUploadStatus(mqttMessage.get(index).getMsgId(), RescribeConstants.FILE_STATUS.COMPLETED);

            String fileUrl = common.getData().getDocUrl();
            // send via mqtt
            mqttMessage.get(index).setFileUrl(fileUrl);
            mqttMessage.get(index).setUploadStatus(COMPLETED);

            Intent intentService = new Intent(this, MQTTService.class);
            intentService.putExtra(SEND_MESSAGE, true);
            intentService.putExtra(MESSAGE_LIST, mqttMessage.get(index));
            ContextCompat.startForegroundService(this, intentService);

            Intent intent = new Intent(CONNECT_UPLOAD);
            intent.putExtra(RESULT, result);
            intent.putExtra(MQTT_MESSAGE, mqttMessage.get(index));
            sendBroadcast(intent);

            if (mqttMessage.size() == (index + 1)) {
                mBuilder.setContentText("Connect File Uploaded Successfully");
                // Removes the progress bar
//                        .setProgress(connectUploads.size(), uploadInfo.getProgressPercent(), false);
                mNotificationManager.notify(CONNECT_UPLOAD_FOREGROUND_ID, mBuilder.build());
                stopSelf();

            } else {
                index += 1;
                imageUpload(mqttMessage.get(index).getFileUrl(), mapHeaders);
            }

        } else {

            mBuilder.setContentText("Uploading " + (mqttMessage.size() - index) + ((mqttMessage.size() - index) == 1 ? " File" : " Files"));
            // Removes the progress bar
//                        .setProgress(connectUploads.size(), uploadInfo.getProgressPercent(), false);
            mNotificationManager.notify(CONNECT_UPLOAD_FOREGROUND_ID, mBuilder.build());

            Log.e("index fail", "" + index);
            appDBHelper.updateMessageUploadStatus(mqttMessage.get(index).getMsgId(), RescribeConstants.FILE_STATUS.FAILED);

            Intent intent = new Intent(CONNECT_UPLOAD);
            intent.putExtra(RESULT, result);
            intent.putExtra(MQTT_MESSAGE, mqttMessage.get(index));
            sendBroadcast(intent);

            if (mqttMessage.size() == (index + 1)) {
                mBuilder.setContentText("File Uploaded Failed");
                // Removes the progress bar
//                        .setProgress(connectUploads.size(), uploadInfo.getProgressPercent(), false);
                mNotificationManager.notify(CONNECT_UPLOAD_FOREGROUND_ID, mBuilder.build());
                stopSelf();
            } else {
                index += 1;
                imageUpload(mqttMessage.get(index).getFileUrl(), mapHeaders);
            }
        }
    }

}