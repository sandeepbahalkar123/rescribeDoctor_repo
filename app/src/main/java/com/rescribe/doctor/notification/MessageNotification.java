package com.rescribe.doctor.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.content.ContextCompat;

import com.rescribe.doctor.R;
import com.rescribe.doctor.model.chat.MQTTMessage;
import com.rescribe.doctor.services.MQTTService;
import com.rescribe.doctor.ui.activities.PatientConnectActivity;

import java.util.ArrayList;

/**
 * Helper class for showing and canceling new message
 * notifications.
 * <p>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class MessageNotification {
    /**
     * The unique identifier for this type of notification.
     */
    private static final String NOTIFICATION_TAG = "RescribeMessage";
    private static final String GROUP = "RescribeMessages";

    public static void notify(final Context context, final ArrayList<MQTTMessage> messageContent,
                              final String userName, final int unread, PendingIntent replyPendingIntent, final int notificationId) {
        final Resources res = context.getResources();

        // This image is used as the notification's large icon (thumbnail).
        // TODO: Remove this if your notification has no relevant thumbnail.
        final Bitmap picture = BitmapFactory.decodeResource(res, R.drawable.exercise);

        final String content = messageContent.get(messageContent.size() - 1).getMsg();
        String title;
        if (unread > 1)
            title = userName + " (" + unread + " messages)";
        else title = userName;

        Intent resultIntent = new Intent(context, PatientConnectActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle()
                .setBigContentTitle(title)
                .setSummaryText("Patient Message");

        for (MQTTMessage message : messageContent)
            inboxStyle.addLine(message.getMsg());

// Create the RemoteInput specifying above key
        RemoteInput remoteInput = new RemoteInput.Builder(MQTTService.KEY_REPLY)
                .setLabel("Replay")
                .build();

        // Add to your action, enabling Direct Reply
        NotificationCompat.Action mAction =
                new NotificationCompat.Action.Builder(R.drawable.ic_action_stat_reply, "Replay", replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .setAllowGeneratedReplies(true)
                        .build();

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)

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
}
