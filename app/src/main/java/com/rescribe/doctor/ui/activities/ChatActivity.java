package com.rescribe.doctor.ui.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.ChatAdapter;
import com.rescribe.doctor.model.message.MessageList;
import com.rescribe.doctor.model.message.MessageModel;
import com.rescribe.doctor.service.MQTTService;
import com.rescribe.doctor.ui.customesViews.CustomTextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.backButton)
    ImageView backButton;
    @BindView(R.id.profilePhoto)
    ImageView profilePhoto;
    @BindView(R.id.receiverName)
    CustomTextView receiverName;
    @BindView(R.id.dateTime)
    CustomTextView dateTime;
    @BindView(R.id.titleLayout)
    RelativeLayout titleLayout;
    @BindView(R.id.toolbar)
    RelativeLayout toolbar;
    @BindView(R.id.chatList)
    RecyclerView chatList;
    @BindView(R.id.messageType)
    EditText messageType;
    @BindView(R.id.attachmentButton)
    ImageButton attachmentButton;
    @BindView(R.id.cameraButton)
    ImageButton cameraButton;
    @BindView(R.id.buttonLayout)
    LinearLayout buttonLayout;
    @BindView(R.id.messageTypeSubLayout)
    RelativeLayout messageTypeSubLayout;
    @BindView(R.id.recorderOrSendButton)
    ImageView recorderOrSendButton;
    @BindView(R.id.messageTypeLayout)
    RelativeLayout messageTypeLayout;

    private boolean isSend = false;
    private Intent serviceIntent;

    private static final String TAG = "ChatActivity";
    private ChatAdapter chatAdapter;
    private ArrayList<MessageList> messageList = new ArrayList<>();
    private Gson gson = new Gson();

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isFailed = intent.getBooleanExtra(MQTTService.FAILED, false);

            if (!isFailed) {
                MessageList message = intent.getParcelableExtra(MQTTService.MESSAGE);
                if (chatAdapter != null) {
                    messageList.add(message);
                    chatAdapter.notifyItemInserted(messageList.size() - 1);
                    chatList.smoothScrollToPosition(messageList.size() - 1);
                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        // startService

        // use this to start and trigger a service
        serviceIntent = new Intent(this, MQTTService.class);
        // potentially add data to the serviceIntent
        serviceIntent.putExtra(MQTTService.IS_MESSAGE, false);
        startService(serviceIntent);

        String data = "{ \"messageList\": [ { \"msg\": \"Hi Doc I am not good okey\", \"docId\": 999, \"patId\": 12, \"who\": 1 }, { \"msg\": \"Hi Doc I am not good okey\", \"docId\": 999, \"patId\": 12, \"who\": 0 }, { \"msg\": \"Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book\", \"docId\": 999, \"patId\": 12, \"who\": 1 }, { \"msg\": \"Hi Doc I am not good okey\", \"docId\": 999, \"patId\": 12, \"who\": 1 }, { \"msg\": \"There are many variations of passages\", \"docId\": 999, \"patId\": 12, \"who\": 0 }, { \"msg\": \"Hi Doc I am not good okey\", \"docId\": 999, \"patId\": 12, \"who\": 0 }, { \"msg\": \"There are many variations of passages of Lorem Ipsum available\", \"docId\": 999, \"patId\": 12, \"who\": 1 }, { \"msg\": \"Hi Doc I am not good okey\", \"docId\": 999, \"patId\": 12, \"who\": 1 }, { \"msg\": \"Hi Doc I am \\nnot good okey\", \"docId\": 999, \"patId\": 12, \"who\": 0 }, { \"msg\": \"Hmm\", \"docId\": 999, \"patId\": 12, \"who\": 1 }, { \"msg\": \"There are many variations of passages of Lorem Ipsum available\", \"docId\": 999, \"patId\": 12, \"who\": 0 } ] }";

        MessageModel messageModel = gson.fromJson(data, MessageModel.class);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        chatList.setLayoutManager(mLayoutManager);
        messageList.addAll(messageModel.getMessageList());
        chatAdapter = new ChatAdapter(messageList);
        chatList.setAdapter(chatAdapter);
        chatList.scrollToPosition(messageList.size() - 1);

        messageType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    recorderOrSendButton.setImageResource(R.drawable.speak);
                    cameraButton.setVisibility(View.VISIBLE);
                    isSend = false;
                } else {
                    recorderOrSendButton.setImageResource(R.drawable.send);
                    cameraButton.setVisibility(View.GONE);
                    isSend = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @OnClick({R.id.backButton, R.id.attachmentButton, R.id.cameraButton, R.id.recorderOrSendButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backButton:
                onBackPressed();
                break;
            case R.id.attachmentButton:
                break;
            case R.id.cameraButton:
                break;
            case R.id.recorderOrSendButton:
                if (isSend) {

                    // SendButton

                    String message = messageType.getText().toString();
                    message = message.trim();
                    if (!message.equals("")) {

                        MessageList messageL = new MessageList();
                        messageL.setTopic(MQTTService.DOCTOR_CONNECT);
                        messageL.setWho(ChatAdapter.SENDER);
                        messageL.setMsg(message);
                        messageL.setMsgId(0);
                        messageL.setDocId(123);
                        messageL.setPatId(123);

                        if (chatAdapter != null) {
                            messageList.add(messageL);
                            chatAdapter.notifyItemInserted(messageList.size() - 1);
                            chatList.smoothScrollToPosition(messageList.size() - 1);
                        }

                        serviceIntent.putExtra(MQTTService.IS_MESSAGE, true);
                        serviceIntent.putExtra(MQTTService.MESSAGE, messageL);
                        startService(serviceIntent);

                        messageType.setText("");
                    }
                } else {

                    // Record Button

                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(
                MQTTService.NOTIFY));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
}
