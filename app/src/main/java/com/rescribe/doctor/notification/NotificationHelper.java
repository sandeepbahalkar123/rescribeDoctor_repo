/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rescribe.doctor.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.rescribe.doctor.R;
import com.rescribe.doctor.model.chat.MQTTMessage;
import com.rescribe.doctor.services.MQTTService;
import com.rescribe.doctor.ui.activities.ChatActivity;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;

import static com.rescribe.doctor.broadcast_receivers.ReplayBroadcastReceiver.MESSAGE_LIST;
import static com.rescribe.doctor.services.MQTTService.REPLY_ACTION;
import static com.rescribe.doctor.util.RescribeConstants.FILE.AUD;
import static com.rescribe.doctor.util.RescribeConstants.FILE.DOC;
import static com.rescribe.doctor.util.RescribeConstants.FILE.IMG;
import static com.rescribe.doctor.util.RescribeConstants.FILE.LOC;
import static com.rescribe.doctor.util.RescribeConstants.FILE.VID;
import static com.rescribe.doctor.util.RescribeConstants.SALUTATION;

/**
 * Helper class to manage notification channels, and create notifications.
 */
public class NotificationHelper extends ContextWrapper {
    private NotificationManager mNotificationManager;
    public static final String PATIENT_CONNECT_SERVICE_CHANNEL = "patient_connect_service";
    public static final String PATIENT_CONNECT_MESSAGE_CHANNEL = "patient_connect_message";

    /**
     * The unique identifier for this type of notification.
     */
    public static final String NOTIFICATION_TAG = "RescribeMessage";
    public static final String GROUP = "RescribeMessages";


    /**
     * Registers notification channels, which can be used later by individual notifications.
     *
     * @param context The application context
     */


    public NotificationHelper(Context context) {
        super(context);
        createChannel();
    }

    public void createChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Create the channel object with the unique ID CONNECT_CHANNEL
            NotificationChannel connectChannel = new NotificationChannel(
                    PATIENT_CONNECT_MESSAGE_CHANNEL, "Patient Connect Messages",
                    NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the channel's initial settings
            connectChannel.setLightColor(Color.GREEN);
            connectChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            // Submit the notification channel object to the notification manager
            getNotificationManager().createNotificationChannel(connectChannel);
        }
    }

    /**
     * Get the notification mNotificationManager.
     * <p>
     * <p>Utility method as this helper works with it a lot.
     *
     * @return The system service NotificationManager
     */
    public NotificationManager getNotificationManager() {
        if (mNotificationManager == null) {
            mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mNotificationManager;
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification, int notificationId) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_TAG, notificationId, notification);
    }


    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context, int notificationId) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(NOTIFICATION_TAG, notificationId);
    }


    private String getContent(MQTTMessage mqttMessage) {
        String content;

        if (mqttMessage.getFileType() != null) {
            switch (mqttMessage.getFileType()) {
                case DOC:
                    content = RescribeConstants.FILE_EMOJI.DOC_FILE;
                    break;
                case AUD:
                    content = RescribeConstants.FILE_EMOJI.AUD_FILE;
                    break;
                case VID:
                    content = RescribeConstants.FILE_EMOJI.VID_FILE;
                    break;
                case LOC:
                    content = RescribeConstants.FILE_EMOJI.LOC_FILE;
                    break;
                case IMG:
                    content = RescribeConstants.FILE_EMOJI.IMG_FILE;
                    break;
                default:
                    content = mqttMessage.getMsg();
                    break;
            }
        } else content = mqttMessage.getMsg();

        return content;
    }

    public void notify(final Context context, final ArrayList<MQTTMessage> messageContent,
                       final String userName, Bitmap picture, final int unread, PendingIntent replyPendingIntent, final int notificationId) {

        MQTTMessage lastMessage = messageContent.get(messageContent.size() - 1);
        String content = getContent(lastMessage);

        String salutation = "";
        if (lastMessage.getSalutation() != 0)
            salutation = SALUTATION[lastMessage.getSalutation() - 1];

        String title;
        if (unread > 1)
            title = salutation + userName + " (" + unread + " messages)";
        else title = salutation + userName;

        // start your activity for Android M and below
        Intent resultIntent = new Intent(context, ChatActivity.class);
        resultIntent.setAction(REPLY_ACTION);
        resultIntent.putExtra(MESSAGE_LIST, lastMessage);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        lastMessage.getDocId(),
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle()
                .setBigContentTitle(title)
                .setSummaryText("New Message");

        for (MQTTMessage message : messageContent)
            inboxStyle.addLine(getContent(message));

// Create the RemoteInput specifying above key
        android.support.v4.app.RemoteInput remoteInput = new android.support.v4.app.RemoteInput.Builder(MQTTService.KEY_REPLY)
                .setLabel("Reply")
                .build();

        // Add to your action, enabling Direct Reply
        NotificationCompat.Action mAction =
                new NotificationCompat.Action.Builder(R.drawable.ic_action_stat_reply, "Reply", replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .setAllowGeneratedReplies(true)
                        .build();

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PATIENT_CONNECT_MESSAGE_CHANNEL)

                // Set appropriate defaults for the notification light, sound,
                // and vibration.
                .setDefaults(Notification.DEFAULT_ALL)

                // Bundle Notification

                .setGroupSummary(true)
                .setGroup(GROUP)

                // Set required fields, including the small icon, the
                // notification title, and text.
                .setSmallIcon(R.drawable.logosmall)
                .setContentTitle(title)
                .setContentText(content)

                // Set Color
                .setColor(ContextCompat.getColor(context, R.color.tagColor))

                // All fields below this line are optional.

                // Use a default priority (recognized on devices running Android
                // 4.1 or later)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

                // Provide a large icon, shown with the notification in the
                // notification drawer on devices running Android 3.0 or later.
                .setLargeIcon(picture)

                // Set ticker text (preview) information for this notification.
                .setTicker(title)

                // Show a number. This is useful when stacking notifications of
                // a single type.
                .setNumber(unread)

                // Set Style and Action
                .setStyle(inboxStyle)
                .addAction(mAction)

                // Click Event on notification
                .setContentIntent(resultPendingIntent)

                // Automatically dismiss the notification when it is touched.
                .setAutoCancel(true);

        notify(context, builder.build(), notificationId);
    }
}
