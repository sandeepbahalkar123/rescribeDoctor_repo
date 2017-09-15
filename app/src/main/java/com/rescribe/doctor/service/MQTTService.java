package com.rescribe.doctor.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.rescribe.doctor.model.chat.MessageList;
import com.rescribe.doctor.notification.MessageNotification;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.util.RescribeConstants;
import com.rescribe.doctor.util.rxnetwork.RxNetwork;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
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
    public static final String RECEIVED = "delivery";
    public static final String DOCTOR_CONNECT = "doctorConnect";
    private static final String TOPIC[] = {"doctorConnect", "online"};
    public static final String DELIVERED = "delivered";

    public static final String DOCTOR = "user1";

    private MqttAsyncClient mqttClient;
    private static final String BROKER = "tcp://test.mosquitto.org:1883";

    private InternetState internetState;
    private Gson gson = new Gson();

    private Subscription sendStateSubscription;
    private int[] qos;

    @Override
    public void onCreate() {
        super.onCreate();

        initRxNetwork();

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
                                MQTTService.this.internetState = internetState;
                                try {
                                    if (internetState.isEnabled) {
                                        if (!mqttClient.isConnected()) {
                                            mqttClient.reconnect();
                                        } else Log.d(TAG, "Not Connected 2");
                                    } else
                                        mqttClient.disconnect();
                                } catch (MqttException ignored) {

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

    public class LocalBinder extends Binder {
        public MQTTService getServerInstance() {
            return MQTTService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    void initMqttCallback() {

        qos = new int[TOPIC.length];
        for (int index = 0; index < TOPIC.length; index++)
            qos[index] = 1;

        try {
            mqttClient.setCallback(new MqttCallback() {
                public void messageArrived(final String topic, final MqttMessage msg) {
                    Log.d(TAG + "Received:", topic + " " + new String(msg.getPayload()));

                    try {
                        if (!msg.isDuplicate()) {
                            MessageList messageL = gson.fromJson(new String(msg.getPayload()), MessageList.class);
                            String myid = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, MQTTService.this);
                            String userLogin = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.LOGIN_STATUS, MQTTService.this);

                            if (userLogin.equals(RescribeConstants.YES)) {
                                if (myid.equals(String.valueOf(messageL.getDocId())) && topic.equals(TOPIC[0])) {
                                    messageL.setMsgId(msg.getId());
                                    messageL.setTopic(topic);
                                    if (!messageL.getSender().equals(MQTTService.DOCTOR)) {
                                        MessageNotification.notify(MQTTService.this, messageL.getTopic(), messageL.getMsg(), 0, messageL.getMsgId());
                                        Intent intent = new Intent(NOTIFY);
                                        intent.putExtra(RECEIVED, true);
                                        intent.putExtra(MESSAGE, messageL);
                                        sendBroadcast(intent);
                                    } else Log.d(TAG + " DOCTOR_MES", new String(msg.getPayload()));
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

            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(false);
//            connOpts.setUserName("ganesh");
//            String password = "windows10";
//            connOpts.setPassword(password.toCharArray());


            IMqttActionListener mqttConnect = new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "Connected");
                    try {
                        mqttClient.subscribe(TOPIC, qos);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "Not Connected");
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

    public boolean getNetworkStatus() {
        return internetState.isEnabled;
    }

    public void passMessage(MessageList messageList) {
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
        if (mqttClient.isConnected()) {
            try {
                mqttClient.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        } else Log.d(TAG, "Not Connected 1");

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
