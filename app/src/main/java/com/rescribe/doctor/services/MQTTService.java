package com.rescribe.doctor.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.RemoteInput;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.rescribe.doctor.broadcast_receivers.ReplayBroadcastReceiver;
import com.rescribe.doctor.helpers.database.AppDBHelper;
import com.rescribe.doctor.model.chat.MQTTMessage;
import com.rescribe.doctor.model.chat.StatusInfo;
import com.rescribe.doctor.notification.MessageNotification;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.ui.activities.ChatActivity;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;
import com.rescribe.doctor.util.rxnetwork.RxNetwork;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

import static com.rescribe.doctor.broadcast_receivers.ReplayBroadcastReceiver.MESSAGE_LIST;
import static com.rescribe.doctor.util.Config.BROKER;
import static com.rescribe.doctor.util.RescribeConstants.MESSAGE_STATUS.REACHED;
import static com.rescribe.doctor.util.RescribeConstants.MESSAGE_STATUS.SEEN;

public class MQTTService extends Service {

    public static final String KEY_REPLY = "key_replay";
    public static final String REPLY_ACTION = "com.rescribe.doctor.REPLY_ACTION"; // Change
    public static final String SEND_MESSAGE = "send_message";
    public static final String STATUS_INFO = "status_info";

    private static int currentChatUser = -1;
    private static final String TAG = "MQTTService";
    public static final String MESSAGE = "message";
    public static final String NOTIFY = "com.rescribe.doctor.NOTIFY"; // Change
    public static final String IS_MESSAGE = "is_message";
    public static final String MESSAGE_ID = "message_id";
    public static final String[] TOPIC = {"doctorConnect", "doctor/status", "message/status"};
    public static final String DELIVERED = "delivered";

    public static final String DOCTOR = "user1";
//    public static final String PATIENT = "user2";

    private MqttAsyncClient mqttClient;

    private Gson gson = new Gson();

    private Subscription sendStateSubscription;
    private int[] qos;

    private AppDBHelper appDBHelper;
    private MqttConnectOptions connOpts;

    @Override
    public void onCreate() {
        super.onCreate();

        initRxNetwork();

        appDBHelper = new AppDBHelper(this);

        //MQTT client id to use for the device. "" will generate a client id automatically
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            mqttClient = new MqttAsyncClient(BROKER, MqttAsyncClient.generateClientId(), persistence);
            initMqttCallback();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void initRxNetwork() {
        final Observable<InternetState> sendStateStream =
                RxNetwork.stream(this).map(new Func1<Boolean, InternetState>() {
                    @Override
                    public InternetState call(Boolean hasInternet) {
                        if (hasInternet)
                            return new InternetState("Online", true);
                        return new InternetState("Offline", false);
                    }
                });

        sendStateSubscription =
                sendStateStream.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<InternetState>() {
                            @Override
                            public void call(InternetState internetState) {
                                // do stuff here for UI
                                try {
                                    if (internetState.isEnabled) {
                                        if (!mqttClient.isConnected()) {
                                            mqttClient.reconnect();
                                        } else Log.d(TAG, "Not Connected 2");
                                    } else
                                        mqttClient.disconnect();
                                } catch (MqttException ignored) {
                                    ignored.getStackTrace();
                                }
                            }
                        });
    }

    IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return mBinder;
    }

    public void setCurrentChatUser(int currentChatUser) {
        MQTTService.currentChatUser = currentChatUser;
    }

    public class LocalBinder extends Binder {
        public MQTTService getServerInstance() {
            return MQTTService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.getBooleanExtra(SEND_MESSAGE, false)) {
                if (mqttClient != null)
                    if (!mqttClient.isConnected()) {
                        try {
                            mqttClient.reconnect();
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    } else {// change
                        if (intent.getBooleanExtra(MESSAGE, true)) {
                            passMessage((MQTTMessage) intent.getParcelableExtra(MESSAGE_LIST));
                        } else passStatusInfo((StatusInfo) intent.getParcelableExtra(STATUS_INFO));
                    }
            } else {
                if (mqttClient != null)
                    if (!mqttClient.isConnected()) {
                        try {
                            mqttClient.reconnect();
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }
            }
        }
        return START_STICKY;
    }

    void initMqttCallback() {

        qos = new int[TOPIC.length];
        for (int index = 0; index < TOPIC.length; index++)
            qos[index] = 1;

        try {
            mqttClient.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    CommonMethods.Log("MqttCallbackExtended", String.valueOf(reconnect));
                    try {
                        mqttClient.subscribe(TOPIC, qos);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                public void messageArrived(final String topic, final MqttMessage msg) {
                    String payloadString = new String(msg.getPayload());
                    Log.d(TAG + "Received:", topic + " " + payloadString);

                    try {
                        if (!msg.isDuplicate()) {
                            String myid = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, MQTTService.this);
                            String userLogin = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.LOGIN_STATUS, MQTTService.this);

                            if (userLogin.equals(RescribeConstants.YES)) {
                                if (topic.equals(TOPIC[0])) {
                                    MQTTMessage messageL = gson.fromJson(payloadString, MQTTMessage.class);
                                    if (myid.equals(String.valueOf(messageL.getDocId()))) { // Change
                                        messageL.setTopic(topic);
                                        if (!messageL.getSender().equals(MQTTService.DOCTOR)) {

                                            // change
                                            StatusInfo statusInfo = new StatusInfo();
                                            statusInfo.setMsgId(messageL.getMsgId());
                                            statusInfo.setDocId(messageL.getDocId());
                                            statusInfo.setPatId(messageL.getPatId());

                                            if (currentChatUser != messageL.getPatId()) {

                                                ArrayList<MQTTMessage> messagesTemp = new ArrayList<>();
                                                ArrayList<MQTTMessage> messages = appDBHelper.insertUnreadMessage(messageL.getPatId(), payloadString); // Change

                                                if (messages.size() > 6) {
                                                    for (int index = messages.size() - 6; index < messages.size(); index++)
                                                        messagesTemp.add(messages.get(index));
                                                } else messagesTemp.addAll(messages);

                                                MessageNotification.notify(MQTTService.this, messagesTemp, String.valueOf(messageL.getName()), appDBHelper.unreadMessageCountById(messageL.getPatId()), getReplyPendingIntent(messageL), messageL.getPatId()); // Change

                                                // change
                                                statusInfo.setMessageStatus(REACHED);
                                            } else {
                                                // change
                                                statusInfo.setMessageStatus(SEEN);
                                            }

                                            passStatusInfo(statusInfo);

                                            Intent intent = new Intent(NOTIFY);
                                            intent.putExtra(IS_MESSAGE, true);
                                            intent.putExtra(MESSAGE, messageL);
                                            sendBroadcast(intent);
                                        } else Log.d(TAG + " DOCTOR_MES", payloadString);
                                    } else Log.d(TAG + " OTHERS_MES", payloadString);
                                } else if (topic.equals(TOPIC[1]))
                                    broadcastStatus(payloadString, topic); // change
                                else if (topic.equals(TOPIC[2]))
                                    broadcastStatus(payloadString, topic); // change
                            }
                        } else Log.d(TAG + " LOGOUT_MES", payloadString);
                    } catch (JsonSyntaxException e) {
                        Log.d(TAG + " MESSAGE", "JSON_EXCEPTION" + payloadString);
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d(TAG, "Delivery Complete");
                    Log.d(TAG + " MESSAGE_ID", String.valueOf(token.getMessageId()));

                    Intent intent = new Intent(NOTIFY);
                    intent.putExtra(MESSAGE_ID, String.valueOf(token.getMessageId()));
                    intent.putExtra(DELIVERED, true);
                    sendBroadcast(intent);
                }

                public void connectionLost(Throwable arg0) {
                    try {
                        Log.d(TAG, "Connection Lost");
                        mqttClient.reconnect();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            });

            connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(false);
            connOpts.setAutomaticReconnect(true);
//            connOpts.setWill(TOPIC[0], "Message Reached".getBytes(), 1, true);
//            connOpts.setWill(TOPIC[1], "TypeStatus Reached".getBytes(), 1, true);
//            connOpts.setKeepAliveInterval(120);
//            connOpts.setUserName("ganesh");
//            String password = "windows10";
//            connOpts.setPassword(password.toCharArray());

            IMqttActionListener mqttConnect = new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    CommonMethods.Log(TAG, "Connected");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "Not Connected");
                    try {
                        mqttClient.reconnect();
                        Log.d(TAG, "ReConnecting");
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            };
            mqttClient.connect(connOpts, MQTTService.this, mqttConnect);

        } catch (MqttException me) {
            Log.e(TAG + "reason ", "" + me.getReasonCode());
            Log.e(TAG + "msg ", "" + me.getMessage());
            Log.e(TAG + "loc ", "" + me.getLocalizedMessage());
            Log.e(TAG + "cause ", "" + me.getCause());
            Log.e(TAG + "excep ", "" + me);
            me.printStackTrace();
        }
    }

    // change
    private void broadcastStatus(String payloadString, String topic) {
        StatusInfo statusInfo = gson.fromJson(payloadString, StatusInfo.class);
            if (!statusInfo.getSender().equals(MQTTService.DOCTOR)) {
                Intent intent = new Intent(NOTIFY);
                intent.putExtra(IS_MESSAGE, false);
                intent.putExtra(MESSAGE, statusInfo);
                sendBroadcast(intent);
            }
    }

    // change
    public void passStatusInfo(StatusInfo statusInfo) {
        try {
            // 2017-10-13 13:08:07
            String msgTime = CommonMethods.getCurrentTimeStamp(RescribeConstants.DATE_PATTERN.YYYY_MM_DD_HH_mm_ss);
            statusInfo.setMsgTime(msgTime);
            String content = gson.toJson(statusInfo, StatusInfo.class);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(1);
            message.setRetained(true);
            if (mqttClient.isConnected()) {
                mqttClient.publish(TOPIC[2], message);
            } else {
                mqttClient.reconnect();
                mqttClient.publish(TOPIC[2], message);
            }
            CommonMethods.Log("passMessageStatus: ", content);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void typingStatus(StatusInfo statusInfo) {
        try {
            // 2017-10-13 13:08:07
            String msgTime = CommonMethods.getCurrentTimeStamp(RescribeConstants.DATE_PATTERN.YYYY_MM_DD_HH_mm_ss);
            statusInfo.setMsgTime(msgTime);
            String content = gson.toJson(statusInfo, StatusInfo.class);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(1);
            message.setRetained(true);
            if (mqttClient.isConnected()) {
                mqttClient.publish(TOPIC[1], message);
            } else {
                mqttClient.reconnect();
                mqttClient.publish(TOPIC[1], message);
            }
            CommonMethods.Log("passTypeStatus: ", content);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void passMessage(MQTTMessage mqttMessage) {
        try {
            mqttMessage.setSender(DOCTOR);
            String content = gson.toJson(mqttMessage, MQTTMessage.class);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(1);
            message.setRetained(true);
            if (mqttClient.isConnected()) {
                mqttClient.publish(TOPIC[0], message);
            } else {
                mqttClient.reconnect();
                mqttClient.publish(TOPIC[0], message);
            }
            CommonMethods.Log("passMessage: ", content);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mqttClient.isConnected()) {

            // change

            /*String myid = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, MQTTService.this);
            // send user status via mqtt
            StatusInfo statusInfo = new StatusInfo();
            statusInfo.setPatId(-2);
            statusInfo.setDocId(Integer.parseInt(myid));
            statusInfo.setUserStatus(OFFLINE);
            String generatedId = CHAT + 0 + "_" + System.nanoTime();
            statusInfo.setMsgId(generatedId);
            passStatusInfo(statusInfo);*/

            try {
                mqttClient.disconnect();
                CommonMethods.Log(TAG, "disconnect");
            } catch (MqttException e) {
                e.printStackTrace();
            }
        } else CommonMethods.Log(TAG, "Not Connected 1");
        sendStateSubscription.unsubscribe();
        sendStateSubscription = null;
    }


    private static class InternetState {
        final boolean isEnabled;
        final String state;

        InternetState(String state, boolean isEnabled) {
            this.isEnabled = isEnabled;
            this.state = state;
        }
    }
// new code

    private PendingIntent getReplyPendingIntent(MQTTMessage mqttMessage) {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // start a
            // (i)  broadcast receiver which runs on the UI thread or
            // (ii) service for a background task to b executed , but for the purpose of this codelab, will be doing a broadcast receiver
            intent = ReplayBroadcastReceiver.getReplyMessageIntent(this, mqttMessage);
            return PendingIntent.getBroadcast(getApplicationContext(), mqttMessage.getDocId(), intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            // start your activity for Android M and below
            intent = new Intent(MQTTService.this, ChatActivity.class);
            intent.setAction(REPLY_ACTION);
            intent.putExtra(MESSAGE_LIST, mqttMessage);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return PendingIntent.getActivity(this, mqttMessage.getDocId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    public static CharSequence getReplyMessage(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(KEY_REPLY);
        }
        return null;
    }
}
