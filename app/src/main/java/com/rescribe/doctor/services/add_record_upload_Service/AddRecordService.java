package com.rescribe.doctor.services.add_record_upload_Service;

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
import com.rescribe.doctor.model.CommonBaseModelContainer;
import com.rescribe.doctor.model.UploadStatus;
import com.rescribe.doctor.model.login.LoginModel;
import com.rescribe.doctor.model.requestmodel.login.LoginRequestModel;
import com.rescribe.doctor.network.RequestPool;
import com.rescribe.doctor.preference.RescribePreferencesManager;
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
import static com.rescribe.doctor.services.SyncOfflineRecords.ATTATCHMENT_DOC_UPLOAD;
import static com.rescribe.doctor.ui.activities.add_records.SelectedRecordsActivity.FILELIST;
import static com.rescribe.doctor.util.RescribeConstants.FILE_STATUS.COMPLETED;
import static com.rescribe.doctor.util.RescribeConstants.FILE_STATUS.FAILED;
import static com.rescribe.doctor.util.RescribeConstants.FILE_STATUS.UPLOADING;
import static com.rescribe.doctor.util.RescribeConstants.INVALID_LOGIN_PASSWORD;
import static com.rescribe.doctor.util.RescribeConstants.SUCCESS;

public class AddRecordService extends Service {

    public static final String RESULT = "result";
    private static final String ADD_RECORD_CHANNEL = "addrecord";
    private static final int ADD_RECORD_FOREGROUND_ID = 1634;
    private static final String TAG = "AddRecordService";
    public int index = 0;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private ArrayList<UploadStatus> uploadIdList = new ArrayList<>();
    private AppDBHelper appDBHelper;
    private ArrayList<String> documents = new ArrayList<String>() {{
        add("png");
        add("jpeg");
        add("jpg");
    }};
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
            // Create the channel object with the unique ID ADD_RECORD_CHANNEL
            NotificationChannel connectUploadChannel = new NotificationChannel(
                    ADD_RECORD_CHANNEL, "Add Records File Upload",
                    NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the channel's initial settings
            connectUploadChannel.setLightColor(Color.GREEN);
            connectUploadChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            // Submit the mBuilder channel object to the mBuilder manager
            mNotificationManager.createNotificationChannel(connectUploadChannel);
        }

        mBuilder = new NotificationCompat.Builder(this, ADD_RECORD_CHANNEL);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.logo);

        Notification notification = mBuilder.setContentTitle("Add Records Uploading")
                .setTicker("Uploading")
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent).build();

        startForeground(ADD_RECORD_FOREGROUND_ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        ArrayList<UploadStatus> uploadStatusArrayList = intent.getParcelableArrayListExtra(FILELIST);
        uploadIdList.addAll(uploadStatusArrayList);

        mBuilder.setContentText("Uploading " + uploadIdList.size() + (uploadIdList.size() == 1 ? " File" : " Files"));
        // Removes the progress bar
//                        .setProgress(connectUploads.size(), uploadInfo.getProgressPercent(), false);
        mNotificationManager.notify(ADD_RECORD_FOREGROUND_ID, mBuilder.build());

        if (index == 0) {
            UploadStatus file = uploadIdList.get(index);
            if (!file.isUploading()) {
                file.setUploading(true);
                String path = file.getImagePath();
                String fileType = CommonMethods.getExtension(path).toLowerCase();
                String fileTypeName;
                if (documents.contains(fileType))
                    fileTypeName = "image";
                else
                    fileTypeName = "document";


                Log.e("fileTypeName", fileTypeName);

                String docCaption;

                if (!file.getParentCaption().isEmpty())
                    docCaption = file.getParentCaption();
                else
                    docCaption = CommonMethods.stripExtension(CommonMethods.getFileNameFromPath(file.getImagePath()));

                imageUpload(path, uploadIdList.get(index).getHeaderMap(), docCaption, fileTypeName);
            }
        }

        return Service.START_STICKY;
    }


    public void createChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Create the channel object with the unique ID CONNECT_CHANNEL
            NotificationChannel connectChannel = new NotificationChannel(
                    ADD_RECORD_CHANNEL, "Add Record",
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


    private void imageUpload(final String imagePath, final HashMap<String, String> mapHeaders, final String docCaption, final String fileTypeName) {

        appDBHelper.updateRecordUploads(uploadIdList.get(index).getUploadId(), UPLOADING, uploadIdList.get(index).getRecordType());

        String url = Config.BASE_URL + (uploadIdList.get(index).getRecordType().equals(RescribeConstants.NOTES) ? Config.ADD_OPD_NOTE : Config.MY_RECORDS_UPLOAD);

        Log.i("upload_url--", "-" + url);
        Log.i("imagePath122--", "-" + imagePath);
        Log.i("fileType--", "-" + fileTypeName);
        Log.i("HEADER_PARAMS", index + "_" + mapHeaders.toString());

        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response", response);
                        publishResults(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String message = "";
                if (error.getMessage() != null) {
                    if (!error.getMessage().isEmpty())
                        message = error.getMessage();
                    else
                        message = "Server Error";
                } else
                    message = "Server Error";

                String msg = "{\"common\":{\"success\":false,\"statusCode\":400,\"statusMessage\": \"" + message + "\"} }";

                if (error instanceof TimeoutError) {
                    if (error.getMessage().equalsIgnoreCase("java.io.IOException: No authentication challenges found") || error.getMessage().equalsIgnoreCase("invalid_grant")) {
                        tokenRefreshRequest(imagePath, mapHeaders, docCaption, fileTypeName);
                    } else {
                        publishResults(msg);
                    }
                } else if (error instanceof AuthFailureError) {
                    tokenRefreshRequest(imagePath, mapHeaders, docCaption, fileTypeName);
                } else {
                    publishResults(msg);
                }
            }
        });
        mapHeaders.put("captionname", docCaption);
        mapHeaders.put("fileType", fileTypeName);
        smr.setHeaders(mapHeaders);
        smr.addFile("attachment", imagePath);
        RescribeApplication.getInstance().addToRequestQueue(smr);

    }

    private void tokenRefreshRequest(final String imagePath, final HashMap<String, String> mapHeaders, final String docCaption, final String fileTypeName) {
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
                                imageUpload(imagePath, mapHeaders, docCaption, fileTypeName);
                            } else if (!loginModel.getCommon().isSuccess() && loginModel.getCommon().getStatusCode().equals(INVALID_LOGIN_PASSWORD)) {
                                String msg = "{\"common\":{\"success\":false,\"statusCode\":400,\"statusMessage\": \"" + "Server Error" + "\"} }";
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
        Gson gson = new Gson();
        CommonBaseModelContainer common = gson.fromJson(result, CommonBaseModelContainer.class);
        Toast.makeText(this, common.getCommonRespose().getStatusMessage(), Toast.LENGTH_SHORT).show();

        if (common.getCommonRespose().isSuccess()) {


            mBuilder.setContentText("Uploading " + (uploadIdList.size() - index) + ((uploadIdList.size() - index) == 1 ? " File" : " Files"));
            // Removes the progress bar
//                        .setProgress(connectUploads.size(), uploadInfo.getProgressPercent(), false);
            mNotificationManager.notify(ADD_RECORD_FOREGROUND_ID, mBuilder.build());

            Log.e("index sucess", "" + index);
            appDBHelper.updateRecordUploads(uploadIdList.get(index).getUploadId(), COMPLETED, "");


            if (uploadIdList.size() == (index + 1)) {

                mBuilder.setContentText("File Uploaded Successfully ");
                // Removes the progress bar
//                        .setProgress(connectUploads.size(), uploadInfo.getProgressPercent(), false);
                mNotificationManager.notify(ADD_RECORD_FOREGROUND_ID, mBuilder.build());
                Intent intent = new Intent(ATTATCHMENT_DOC_UPLOAD);
                intent.putExtra(RESULT, common);
                sendBroadcast(intent);
                stopSelf();
            } else {
                index += 1;

                UploadStatus file = uploadIdList.get(index);
                String path = file.getImagePath();
                String fileType = CommonMethods.getExtension(path);
                String fileTypeName = "image";
                if (documents.contains(fileType))
                    fileTypeName = "image";
                else
                    fileTypeName = "document";


                Log.e("fileTypeName", fileTypeName);

                String docCaption;

                if (!file.getParentCaption().isEmpty())
                    docCaption = file.getParentCaption();
                else
                    docCaption = CommonMethods.stripExtension(CommonMethods.getFileNameFromPath(file.getImagePath()));

                imageUpload(path, uploadIdList.get(index).getHeaderMap(), docCaption, fileTypeName);
            }


        } else {

            mBuilder.setContentText("Uploading " + (uploadIdList.size() - index) + ((uploadIdList.size() - index) == 1 ? " File" : " Files"));
            // Removes the progress bar
//                        .setProgress(connectUploads.size(), uploadInfo.getProgressPercent(), false);
            mNotificationManager.notify(ADD_RECORD_FOREGROUND_ID, mBuilder.build());

            Log.e("index fail", "" + index);
            appDBHelper.updateRecordUploads(uploadIdList.get(index).getUploadId(), FAILED, "");

            if (uploadIdList.size() == (index + 1)) {

                mBuilder.setContentText("File Uploaded Successfully");
                // Removes the progress bar
//                        .setProgress(connectUploads.size(), uploadInfo.getProgressPercent(), false);
                mNotificationManager.notify(ADD_RECORD_FOREGROUND_ID, mBuilder.build());
                Intent intent = new Intent(ATTATCHMENT_DOC_UPLOAD);
                intent.putExtra(RESULT, common);
                sendBroadcast(intent);
                stopSelf();
            } else {
                index += 1;

                UploadStatus file = uploadIdList.get(index);
                String path = file.getImagePath();
                String fileType = CommonMethods.getExtension(path);
                String fileTypeName;
                if (documents.contains(fileType))
                    fileTypeName = "image";
                else
                    fileTypeName = "document";


                Log.e("fileTypeName", fileTypeName);

                String docCaption;

                if (!file.getParentCaption().isEmpty())
                    docCaption = file.getParentCaption();
                else
                    docCaption = CommonMethods.stripExtension(CommonMethods.getFileNameFromPath(file.getImagePath()));

                imageUpload(path, uploadIdList.get(index).getHeaderMap(), docCaption, fileTypeName);
            }


        }
    }

}