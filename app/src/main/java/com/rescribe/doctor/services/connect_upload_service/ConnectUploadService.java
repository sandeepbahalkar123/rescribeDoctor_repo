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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.SimpleMultiPartRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.rescribe.doctor.R;
import com.rescribe.doctor.helpers.database.AppDBHelper;
import com.rescribe.doctor.model.chat.MQTTMessage;
import com.rescribe.doctor.model.chat.uploadfile.ChatFileUploadModel;
import com.rescribe.doctor.model.login.LoginModel;
import com.rescribe.doctor.model.requestmodel.login.LoginRequestModel;
import com.rescribe.doctor.network.RequestPool;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.services.MQTTService;
import com.rescribe.doctor.singleton.Device;
import com.rescribe.doctor.singleton.RescribeApplication;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.Config;
import com.rescribe.doctor.util.RescribeConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Request.Method.POST;
import static com.rescribe.doctor.broadcast_receivers.ReplayBroadcastReceiver.MESSAGE_LIST;
import static com.rescribe.doctor.services.MQTTService.SEND_MESSAGE;

import static com.rescribe.doctor.util.RescribeConstants.APPOINTMENT_STATUS.COMPLETED;
import static com.rescribe.doctor.util.RescribeConstants.INVALID_LOGIN_PASSWORD;
import static com.rescribe.doctor.util.RescribeConstants.SUCCESS;

public class ConnectUploadService extends Service {

    public static final String CONNECT_UPLOAD = "org.bmnepali.imageupload.service.receiver";
    public static final String RESULT = "result";

    private static final String PATIENT_CONNECT_UPLOAD = "patient_connect_file_upload";
    private static final int CONNECT_UPLOAD_FOREGROUND_ID = 12345;
    public static final String MQTT_MESSAGE = "mqtt_message";
    public static final String HEADERSLIST = "headerMap";
    private static final String TAG = "ConnectUpload";
    private String BASE_URL;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;
    public int index = 0;
    ArrayList<MQTTMessage> mqttMessage = new ArrayList<>();
    private AppDBHelper appDBHelper;
    HashMap<String, String> mapHeaders;
    private Gson gson = new Gson();
    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        if (appDBHelper == null)
            appDBHelper = new AppDBHelper(this);
        mContext = this;

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


    private void imageUpload(final String imagePath, final HashMap<String, String> mapHeaders) {

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

                if (error != null) {
                    if (error instanceof TimeoutError) {
                        if (error.getMessage().equalsIgnoreCase("java.io.IOException: No authentication challenges found") || error.getMessage().equalsIgnoreCase("invalid_grant")) {
                            tokenRefreshRequest(imagePath, mapHeaders);
                        } else {
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                            String msg = "{\"common\":{\"success\":false,\"statusCode\":400,\"statusMessage\":\"" + error.getMessage() + "\"} }";
                            publishResults(msg);
                        }
                    } else if (error instanceof AuthFailureError) {
                        tokenRefreshRequest(imagePath, mapHeaders);
                    } else {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        String msg = "{\"common\":{\"success\":false,\"statusCode\":400,\"statusMessage\":\"" + error.getMessage() + "\"} }";
                        publishResults(msg);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    String msg = "{\"common\":{\"success\":false,\"statusCode\":400,\"statusMessage\":\"" + error.getMessage() + "\"} }";
                    publishResults(msg);
                }
            }
        });
        smr.setHeaders(mapHeaders);
        smr.addFile("chatDoc", imagePath);
        RescribeApplication.getInstance().addToRequestQueue(smr);

    }

    private void tokenRefreshRequest(final String imagePath, final HashMap<String, String> mapHeaders) {
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
                                imageUpload(imagePath, mapHeaders);
                            } else if (!loginModel.getCommon().isSuccess() && loginModel.getCommon().getStatusCode().equals(INVALID_LOGIN_PASSWORD)) {
                                String msg = "{\"common\":{\"success\":false,\"statusCode\":400,\"statusMessage\":\"" + "invalid login" + "\"} }";
                                publishResults(msg);
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