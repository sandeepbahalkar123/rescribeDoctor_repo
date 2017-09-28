package com.rescribe.doctor.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rescribe.doctor.helpers.database.AppDBHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.chat.MQTTMessage;
import com.rescribe.doctor.model.chat.SendMessageModel;
import com.rescribe.doctor.notification.MessageNotification;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.service.MQTTService;
import com.rescribe.doctor.util.RescribeConstants;

import static com.rescribe.doctor.service.MQTTService.REPLY_ACTION;
import static com.rescribe.doctor.service.MQTTService.SEND_MESSAGE;
import static com.rescribe.doctor.ui.activities.ChatActivity.CHAT;
import static com.rescribe.doctor.ui.activities.PatientConnectActivity.FREE;

public class ReplayBroadcastReceiver extends BroadcastReceiver implements HelperResponse {
    public static final String MESSAGE_LIST = "message_list";
    private MQTTMessage recievedMessage;
    private Context context;
    private AppDBHelper appDBHelper;

    public static Intent getReplyMessageIntent(Context context, MQTTMessage MQTTMessage) {
        Intent intent = new Intent(context, ReplayBroadcastReceiver.class);
        intent.setAction(REPLY_ACTION);
        intent.putExtra(MESSAGE_LIST, MQTTMessage);
        return intent;
    }

    public ReplayBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (REPLY_ACTION.equals(intent.getAction())) {
            // do whatever you want with the message. Send to the server or add to the db.
            // for this tutorial, we'll just show it in a toast;
            CharSequence message = MQTTService.getReplyMessage(intent);
            appDBHelper = new AppDBHelper(context);
            recievedMessage = intent.getParcelableExtra(MESSAGE_LIST);
            this.context = context;

            MQTTMessage messageL = new MQTTMessage();
            messageL.setTopic(MQTTService.TOPIC[0]);
            messageL.setSender(MQTTService.DOCTOR);
            messageL.setMsg(message.toString());

            String generatedId = CHAT + 0 + "_" + System.nanoTime();
            messageL.setMsgId(generatedId);

            messageL.setDocId(recievedMessage.getDocId());
            messageL.setPatId(recievedMessage.getPatId());

            String doctorName = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.USER_NAME, context);
            String imageUrl = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.PROFILE_PHOTO, context);
            String speciality = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.SPECIALITY, context);

            messageL.setName(doctorName);
            messageL.setImageUrl(imageUrl);
            messageL.setOnlineStatus(RescribeConstants.USER_STATUS.ONLINE);

            messageL.setFileUrl("");
            messageL.setSpecialization(speciality);
            messageL.setPaidStatus(FREE);
            messageL.setFileType("");

            // send msg by http api

//            ChatHelper chatHelper = new ChatHelper(context, ReplayBroadcastReceiver.this);
//            chatHelper.sendMsgToPatient(messageL);

            // send via mqtt
            Intent intentService = new Intent(context, MQTTService.class);
            intentService.putExtra(SEND_MESSAGE, true);
            intentService.putExtra(MESSAGE_LIST, messageL);
            context.startService(intentService);
            MessageNotification.cancel(context, recievedMessage.getPatId()); // Change
            appDBHelper.deleteUnreadMessage(recievedMessage.getPatId()); // Change

        }
    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        if (customResponse instanceof SendMessageModel) {
            if (recievedMessage != null) {
                MessageNotification.cancel(context, recievedMessage.getPatId()); // Change
                appDBHelper.deleteUnreadMessage(recievedMessage.getPatId()); // Change
            }
        }
    }

    @Override
    public void onParseError(String mOldDataTag, String errorMessage) {

    }

    @Override
    public void onServerError(String mOldDataTag, String serverErrorMessage) {

    }

    @Override
    public void onNoConnectionError(String mOldDataTag, String serverErrorMessage) {

    }
}