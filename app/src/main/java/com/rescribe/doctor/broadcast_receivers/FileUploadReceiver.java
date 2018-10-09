package com.rescribe.doctor.broadcast_receivers;

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
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.rescribe.doctor.R;
import com.rescribe.doctor.helpers.database.AppDBHelper;
import com.rescribe.doctor.model.chat.MQTTMessage;
import com.rescribe.doctor.model.chat.uploadfile.ChatFileUploadModel;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.services.MQTTService;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadServiceBroadcastReceiver;

import java.util.HashSet;
import java.util.Set;

import static com.rescribe.doctor.broadcast_receivers.ReplayBroadcastReceiver.MESSAGE_LIST;
import static com.rescribe.doctor.services.ChatBackUpService.STATUS;
import static com.rescribe.doctor.services.MQTTService.SEND_MESSAGE;
import static com.rescribe.doctor.services.SyncOfflineRecords.DOC_UPLOAD;
import static com.rescribe.doctor.services.SyncOfflineRecords.UPLOAD_INFO;
import static com.rescribe.doctor.util.RescribeConstants.FILE_STATUS.COMPLETED;
import static com.rescribe.doctor.util.RescribeConstants.FILE_STATUS.FAILED;

public class FileUploadReceiver extends UploadServiceBroadcastReceiver {
    public static final String CHANNEL_CONNECT_FILE_UPLOAD = "channelConnectFileUpload";
    public static final String ATTACHMENT_FILE_UPLOAD = "opdAttachmentFileUpload";
    private static final int CONNECT_CONNECT_NOTIFICATION_ID = 23232;
    private static final int ATTACHMENT_NOTIFICATION_ID = 29932;
    public static Set<String> attachmentUploads = new HashSet<>();
    public static Set<String> connectUploads = new HashSet<>();
    private static NotificationCompat.Builder mConnectBuilder;
    private static NotificationCompat.Builder mAttachmentBuilder;
    private static NotificationManager mNotificationManager;
    private Gson gson = new Gson();
    private AppDBHelper appDBHelper;

    private void init(Context context, UploadInfo uploadInfo) {
        if (mNotificationManager == null) {
            Log.i("FILE_UPLOAD_INIT", "INIT_NOTIFICATION_MANAGER");

            mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }


        if (uploadInfo.getUploadId().contains(ATTACHMENT_FILE_UPLOAD) && mAttachmentBuilder == null) {

            // Attachment

            Intent notificationIntent = new Intent();
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    notificationIntent, 0);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // ATTACHMENT CHANNEL

                // Create the channel object with the unique ID ATTACHMENT_CHANNEL
                NotificationChannel attachmentsChannel = new NotificationChannel(
                        ATTACHMENT_FILE_UPLOAD, "Attachment File Upload",
                        NotificationManager.IMPORTANCE_DEFAULT);

                // Configure the channel's initial settings
                attachmentsChannel.setLightColor(Color.GREEN);
                attachmentsChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

                // Submit the notification channel object to the notification manager
                mNotificationManager.createNotificationChannel(attachmentsChannel);
            }

            mAttachmentBuilder = new NotificationCompat.Builder(context, ATTACHMENT_FILE_UPLOAD);
            Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);

            mAttachmentBuilder.setContentTitle("Attachment Uploading")
                    .setTicker("Uploading")
                    .setSmallIcon(R.drawable.logosmall)
                    .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(pendingIntent).build();

        } else if (mConnectBuilder == null) {

            // Connect

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // Create the channel object with the unique ID CONNECT_CHANNEL
                NotificationChannel connectUploadChannel = new NotificationChannel(
                        CHANNEL_CONNECT_FILE_UPLOAD, "Connect File Upload",
                        NotificationManager.IMPORTANCE_DEFAULT);

                // Configure the channel's initial settings
                connectUploadChannel.setLightColor(Color.GREEN);
                connectUploadChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

                // Submit the notification channel object to the notification manager
                mNotificationManager.createNotificationChannel(connectUploadChannel);
            }

            Intent notificationIntent = new Intent();
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    notificationIntent, 0);

            mConnectBuilder = new NotificationCompat.Builder(context, CHANNEL_CONNECT_FILE_UPLOAD);
            Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);

            mConnectBuilder.setContentTitle("Patient Connect Uploading")
                    .setTicker("Uploading")
                    .setSmallIcon(R.drawable.logosmall)
                    .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(pendingIntent).build();
        }
    }

    @Override
    public void onProgress(Context context, UploadInfo uploadInfo) {

        if (RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.LOGIN_STATUS, context).equals(RescribeConstants.YES)) {
            init(context, uploadInfo);

            if (uploadInfo.getUploadId().contains(ATTACHMENT_FILE_UPLOAD)) {
                attachmentUploads.add(uploadInfo.getUploadId());
                mAttachmentBuilder.setContentText("Uploading " + attachmentUploads.size() + (attachmentUploads.size() == 1 ? " File" : " Files"));
                // Removes the progress bar
//                        .setProgress(attachmentUploads.size(), uploadInfo.getProgressPercent(), false);
                mNotificationManager.notify(ATTACHMENT_NOTIFICATION_ID, mAttachmentBuilder.build());
            } else {
                connectUploads.add(uploadInfo.getUploadId());
                mConnectBuilder.setContentText("Uploading " + connectUploads.size() + (connectUploads.size() == 1 ? " File" : " Files"));
                // Removes the progress bar
//                        .setProgress(connectUploads.size(), uploadInfo.getProgressPercent(), false);
                mNotificationManager.notify(CONNECT_CONNECT_NOTIFICATION_ID, mConnectBuilder.build());
            }
        }
        CommonMethods.Log("ImagedUploadIdHome", uploadInfo.getUploadId() + " onProgress " + uploadInfo.getProgressPercent());
    }

    @Override
    public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
        if (RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.LOGIN_STATUS, context).equals(RescribeConstants.YES)) {

            if (appDBHelper == null)
                appDBHelper = new AppDBHelper(context);

            if (uploadInfo.getUploadId().contains(ATTACHMENT_FILE_UPLOAD)) {
                // Handle OPD Attachments
                appDBHelper.updateRecordUploads(uploadInfo.getUploadId(), FAILED);
                Intent intent = new Intent(DOC_UPLOAD);
                intent.putExtra(UPLOAD_INFO, uploadInfo);
                intent.putExtra(STATUS, FAILED);
                context.sendBroadcast(intent);

                attachmentUploads.remove(uploadInfo.getUploadId());
                if (attachmentUploads.isEmpty())
                    mAttachmentBuilder.setContentText("Files Uploaded Failed");
                else
                    mAttachmentBuilder.setContentText("Uploading " + attachmentUploads.size() + (attachmentUploads.size() == 1 ? " File" : " Files"));
                // Removes the progress bar
//                        .setProgress(attachmentUploads.size(), uploadInfo.getProgressPercent(), false);
                mNotificationManager.notify(ATTACHMENT_NOTIFICATION_ID, mAttachmentBuilder.build());

            } else {
                // Handle Connect Uploads
                String doctorId = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, context);
                String prefix[] = uploadInfo.getUploadId().split("_");
                if (prefix[0].equals(doctorId))
                    appDBHelper.updateMessageUploadStatus(uploadInfo.getUploadId(), RescribeConstants.FILE_STATUS.FAILED);

                connectUploads.remove(uploadInfo.getUploadId());
                if (connectUploads.isEmpty())
                    mConnectBuilder.setContentText("Files Uploaded Failed");
                else
                    mConnectBuilder.setContentText("Uploading " + connectUploads.size() + (connectUploads.size() == 1 ? " File" : " Files"));
                // Removes the progress bar
//                        .setProgress(connectUploads.size(), uploadInfo.getProgressPercent(), false);
                mNotificationManager.notify(CONNECT_CONNECT_NOTIFICATION_ID, mConnectBuilder.build());
            }
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.cancel(uploadInfo.getNotificationID());
        }
        CommonMethods.Log("ImagedUploadIdHome", uploadInfo.getUploadId() + " onError");
    }

    @Override
    public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {

        if (RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.LOGIN_STATUS, context).equals(RescribeConstants.YES)) {

            if (appDBHelper == null)
                appDBHelper = new AppDBHelper(context);

            if (uploadInfo.getUploadId().contains(ATTACHMENT_FILE_UPLOAD)) {
                // Handle OPD Attachments
                appDBHelper.updateRecordUploads(uploadInfo.getUploadId(), COMPLETED);
                Intent intent = new Intent(DOC_UPLOAD);
                intent.putExtra(UPLOAD_INFO, uploadInfo);
                intent.putExtra(STATUS, COMPLETED);
                context.sendBroadcast(intent);

                attachmentUploads.remove(uploadInfo.getUploadId());
                if (attachmentUploads.isEmpty())
                    mAttachmentBuilder.setContentText("Files Uploaded");
                else
                    mAttachmentBuilder.setContentText("Uploading " + attachmentUploads.size() + (attachmentUploads.size() == 1 ? " File" : " Files"));
                // Removes the progress bar
//                        .setProgress(attachmentUploads.size(), uploadInfo.getProgressPercent(), false);
                mNotificationManager.notify(ATTACHMENT_NOTIFICATION_ID, mAttachmentBuilder.build());

            } else {
                // Handle Connect Uploads
                String doctorId = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, context);

                String prefix[] = uploadInfo.getUploadId().split("_");
                if (prefix[0].equals(doctorId)) {
                    MQTTMessage mqttMessage = appDBHelper.getChatMessageByMessageId(uploadInfo.getUploadId());

                    if (mqttMessage != null) {
                        String response = serverResponse.getBodyAsString();
                        ChatFileUploadModel chatFileUploadModel = gson.fromJson(response, ChatFileUploadModel.class);

                        String fileUrl = chatFileUploadModel.getData().getDocUrl();
                        // send via mqtt

                        mqttMessage.setFileUrl(fileUrl);
                        mqttMessage.setUploadStatus(COMPLETED);

                        Intent intentService = new Intent(context, MQTTService.class);
                        intentService.putExtra(SEND_MESSAGE, true);
                        intentService.putExtra(MESSAGE_LIST, mqttMessage);
                        ContextCompat.startForegroundService(context, intentService);
                    }
                }

                connectUploads.remove(uploadInfo.getUploadId());
                if (connectUploads.isEmpty())
                    mConnectBuilder.setContentText("Files Uploaded");
                else
                    mConnectBuilder.setContentText("Uploading " + connectUploads.size() + (connectUploads.size() == 1 ? " File" : " Files"));
                // Removes the progress bar
//                        .setProgress(connectUploads.size(), uploadInfo.getProgressPercent(), false);
                mNotificationManager.notify(CONNECT_CONNECT_NOTIFICATION_ID, mConnectBuilder.build());
            }
        }

//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.cancel(uploadInfo.getNotificationID());
        CommonMethods.Log("ImagedUploadIdHome", uploadInfo.getUploadId() + " onCompleted");
    }

    @Override
    public void onCancelled(Context context, UploadInfo uploadInfo) {
        CommonMethods.Log("ImagedUploadIdHome", uploadInfo.getUploadId() + " onCancelled");
    }
}