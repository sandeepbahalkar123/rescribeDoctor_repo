package com.rescribe.doctor.broadcast_receivers;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.rescribe.doctor.helpers.database.AppDBHelper;
import com.rescribe.doctor.model.chat.MQTTMessage;
import com.rescribe.doctor.model.chat.uploadfile.ChatFileUploadModel;
import com.rescribe.doctor.notification.MessageNotification;
import com.rescribe.doctor.service.MQTTService;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import net.gotev.uploadservice.MultipartUploadTask;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadServiceBroadcastReceiver;

import static com.rescribe.doctor.broadcast_receivers.ReplayBroadcastReceiver.MESSAGE_LIST;
import static com.rescribe.doctor.service.MQTTService.SEND_MESSAGE;
import static com.rescribe.doctor.ui.activities.ChatActivity.CHAT;

public class FileUploadReceiver extends UploadServiceBroadcastReceiver {
    AppDBHelper instance;
    Gson gson = new Gson();

    @Override
    public void onProgress(Context context, UploadInfo uploadInfo) {
        CommonMethods.Log("ImagedUploadIdHome", uploadInfo.getUploadId() + " onProgress " + uploadInfo.getProgressPercent());
    }

    @Override
    public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {

        if (instance == null)
            instance = new AppDBHelper(context);

        if (uploadInfo.getUploadId().length() > CHAT.length()) {
            String prefix = uploadInfo.getUploadId().substring(0, 4);
            if (prefix.equals(CHAT)) {
                instance.updateMessageData(uploadInfo.getUploadId(), RescribeConstants.FAILED);
            }
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(uploadInfo.getNotificationID());

        CommonMethods.Log("ImagedUploadIdHome", uploadInfo.getUploadId() + " onError");
    }

    @Override
    public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
        if (instance == null)
            instance = new AppDBHelper(context);

        if (uploadInfo.getUploadId().length() > CHAT.length()) {
            String prefix = uploadInfo.getUploadId().substring(0, 4);
            if (prefix.equals(CHAT)) {

                MQTTMessage mqttMessage = instance.getMessageDataById(uploadInfo.getUploadId());

                String response = serverResponse.getBodyAsString();
                ChatFileUploadModel chatFileUploadModel = gson.fromJson(response, ChatFileUploadModel.class);

                String fileUrl = chatFileUploadModel.getData().getDocUrl();
                // send via mqtt
                if (mqttMessage != null) {

                    mqttMessage.setFileUrl(fileUrl);

                    Intent intentService = new Intent(context, MQTTService.class);
                    intentService.putExtra(SEND_MESSAGE, true);
                    intentService.putExtra(MESSAGE_LIST, mqttMessage);
                    context.startService(intentService);
                }

                instance.deleteUploadedMessage(uploadInfo.getUploadId());
            }
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(uploadInfo.getNotificationID());
        CommonMethods.Log("ImagedUploadIdHome", uploadInfo.getUploadId() + " onCompleted");
    }

    @Override
    public void onCancelled(Context context, UploadInfo uploadInfo) {
        CommonMethods.Log("ImagedUploadIdHome", uploadInfo.getUploadId() + " onCancelled");
    }
}