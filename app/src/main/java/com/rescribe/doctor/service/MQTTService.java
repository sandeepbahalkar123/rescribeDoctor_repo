package com.rescribe.doctor.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.rescribe.doctor.adapters.chat.ChatAdapter;
import com.rescribe.doctor.model.chat.MessageList;
import com.rescribe.doctor.notification.MessageNotification;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.util.RescribeConstants;
import com.rescribe.doctor.util.rxnetwork.RxNetwork;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public class MQTTService extends Service {
    private static final String TAG = "MQTTService";
    public static final String MESSAGE = "message";
    public static final String NOTIFY = "com.rescribe";
    public static final String IS_MESSAGE = "is_message";
    public static final String MESSAGE_ID = "message_id";
    public static final String FAILED = "delivery";
    public static final String DOCTOR_CONNECT = "doctorConnect";
    private static final String TOPIC[] = {"doctorConnect", "online"};

    private MqttClient mqttClient;
    private static final String BROKER = "tcp://broker.hivemq.com:1883";
    private Gson gson = new Gson();

    private Subscription sendStateSubscription;
    private int[] qos;
    private MqttConnectOptions connOpts;

    @Override
    public void onCreate() {
        super.onCreate();

        initRxNetwork();

        //MQTT client id to use for the device. "" will generate a client id automatically
        String clientId = "rescribe";
        clientId = clientId + System.currentTimeMillis();
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            mqttClient = new MqttClient(BROKER, clientId, persistence);
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
                                            mqttClient.connect();
                                            mqttClient.subscribe(TOPIC);
                                        }
                                    } else
                                        mqttClient.disconnect();
                                } catch (MqttException ignored) {

                                }

//                                Toast.makeText(MQTTService.this, mqttClient.isConnected() + " " + internetState.state, Toast.LENGTH_SHORT).show();
                            }
                        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        MessageList message = intent.getParcelableExtra(MESSAGE);

        if (intent.getBooleanExtra(IS_MESSAGE, false)) {
            if (message != null)
                if (mqttClient.isConnected())
                    passMessage(message);
                else
                    initMqttCallback();
        } else {
            if (TOPIC != null) {
                if (!mqttClient.isConnected())
                    initMqttCallback();
            }
        }

        return START_REDELIVER_INTENT;
    }

    void initMqttCallback() {

        qos = new int[TOPIC.length];
        for (int index = 0; index < TOPIC.length; index++)
            qos[index] = 1;

        try {
            mqttClient.setCallback(new MqttCallback() {
                public void messageArrived(final String topic, final MqttMessage msg)
                        throws Exception {
                    Log.d(TAG + "Received:", topic + " " + new String(msg.getPayload()));

                    try {
                        if (!msg.isDuplicate()) {
                            MessageList messageL = gson.fromJson(new String(msg.getPayload()), MessageList.class);
                            String myid = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, MQTTService.this);
                            String userLogin = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.LOGIN_STATUS, MQTTService.this);

                            if (userLogin.equals(RescribeConstants.YES)) {
                                if (myid.equals(String.valueOf(messageL.getDocId())) && topic.equals(TOPIC[0])) {
                                    messageL.setMsgId(msg.getId());
                                    messageL.setWho(ChatAdapter.RECEIVER);
                                    messageL.setTopic(topic);

                                    MessageNotification.notify(MQTTService.this, messageL.getTopic(), messageL.getMsg(), 0, messageL.getMsgId());

                                    Intent intent = new Intent(NOTIFY);
                                    intent.putExtra(FAILED, false);
                                    intent.putExtra(MESSAGE, messageL);
                                    sendBroadcast(intent);
                                } else Log.d(TAG + " OTHERS_MES", new String(msg.getPayload()));
                            }
                        } else Log.d(TAG + " LOGOUT_MES", new String(msg.getPayload()));
                    } catch (JsonSyntaxException e) {
                        Log.d(TAG + " MESSAGE", new String(msg.getPayload()));
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d(TAG, "Delivery Complete");
                    Log.d(TAG + " MESSAGE_ID", String.valueOf(token.getMessageId()));

                    /*Intent intent = new Intent(NOTIFY);
                        intent.putExtra(MESSAGE_ID, messageId);
                        intent.putExtra(FAILED, true);
                        intent.putExtra(MESSAGE, message);
                        sendBroadcast(intent);*/
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
//            connOpts.setUserName("kevpfvww");
//            String password = "4nWoMhwCRlPi";
//            connOpts.setPassword(password.toCharArray());
            mqttClient.connect(connOpts);
            mqttClient.subscribe(this.TOPIC, qos);

        } catch (MqttException me) {
            Log.e(TAG + "reason ", "" + me.getReasonCode());
            Log.e(TAG + "msg ", "" + me.getMessage());
            Log.e(TAG + "loc ", "" + me.getLocalizedMessage());
            Log.e(TAG + "cause ", "" + me.getCause());
            Log.e(TAG + "excep ", "" + me);
            me.printStackTrace();
        }
    }

    private void passMessage(MessageList messageList) {

        String content = gson.toJson(messageList, MessageList.class);

        try {
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(1);
            message.setRetained(true);
            mqttClient.publish(DOCTOR_CONNECT, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mqttClient != null) {
            if (mqttClient.isConnected()) {
                try {
                    mqttClient.disconnect();
                    Log.d(TAG, "disconnect");
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }

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
}
