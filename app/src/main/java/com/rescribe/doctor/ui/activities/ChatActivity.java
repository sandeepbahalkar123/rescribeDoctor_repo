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

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.chat.ChatAdapter;
import com.rescribe.doctor.helpers.chat.ChatHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.chat.MessageList;
import com.rescribe.doctor.model.chat.SendMessageModel;
import com.rescribe.doctor.model.chat.history.ChatHistory;
import com.rescribe.doctor.model.chat.history.ChatHistoryModel;
import com.rescribe.doctor.model.patient_connect.PatientData;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.service.MQTTService;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends AppCompatActivity implements HelperResponse {

    @BindView(R.id.backButton)
    ImageView backButton;
    @BindView(R.id.profilePhoto)
    ImageView profilePhoto;
    @BindView(R.id.receiverName)
    CustomTextView receiverName;
    @BindView(R.id.onlineStatus)
    CustomTextView mOnlineStatus;
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

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isFailed = intent.getBooleanExtra(MQTTService.FAILED, false);

            if (!isFailed) {
                MessageList message = intent.getParcelableExtra(MQTTService.MESSAGE);
                if (message.getPatId() == patientData.getPatientId()) {
                    if (chatAdapter != null) {
                        messageList.add(message);
                        chatAdapter.notifyItemInserted(messageList.size() - 1);
                        chatList.smoothScrollToPosition(messageList.size() - 1);
                    }
                } else {
                    // Other patient message
                }
            }

        }
    };

    private ChatHelper chatHelper;
    private boolean isSend = false;
    private Intent serviceIntent;

    private static final String TAG = "ChatActivity";
    private ChatAdapter chatAdapter;
    private ArrayList<MessageList> messageList = new ArrayList<>();

    private PatientData patientData;
    private String docId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        patientData = getIntent().getParcelableExtra(RescribeConstants.PATIENT_INFO);

        chatHelper = new ChatHelper(this, this);
        docId = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, this);
        chatHelper.getChatHistory(1, Integer.parseInt(docId), patientData.getPatientId());
        // startService

        // use this to start and trigger a service
        serviceIntent = new Intent(this, MQTTService.class);
        // potentially add data to the serviceIntent
        serviceIntent.putExtra(MQTTService.IS_MESSAGE, false);
        startService(serviceIntent);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        chatList.setLayoutManager(mLayoutManager);
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

        //------set values----
        receiverName.setText(patientData.getPatientName());
        mOnlineStatus.setText(patientData.getOnlineStatus());
        //--- TODO, PROFILE SHOULD BE HERE, added temperately
        String patientName = patientData.getPatientName();
        patientName = patientName.replace("Dr. ", "");
        if (patientName != null) {
            int color2 = ColorGenerator.MATERIAL.getColor(patientName);
            TextDrawable drawable = TextDrawable.builder()
                    .beginConfig()
                    .width(Math.round(getResources().getDimension(R.dimen.dp40)))  // width in px
                    .height(Math.round(getResources().getDimension(R.dimen.dp40))) // height in px
                    .endConfig()
                    .buildRound(("" + patientName.charAt(0)).toUpperCase(), color2);
            profilePhoto.setImageDrawable(drawable);
        }
        //---------
        //----------
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
                        messageL.setDocId(Integer.parseInt(docId));
                        messageL.setPatId(patientData.getPatientId());

                        if (chatAdapter != null) {
                            messageList.add(messageL);
                            chatAdapter.notifyItemInserted(messageList.size() - 1);
                            chatList.smoothScrollToPosition(messageList.size() - 1);
                        }

                        /*serviceIntent.putExtra(MQTTService.IS_MESSAGE, true);
                        serviceIntent.putExtra(MQTTService.MESSAGE, messageL);
                        startService(serviceIntent);

                        messageType.setText("");*/

                        chatHelper.sendMsgToPatient(messageL);

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

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        if (customResponse instanceof SendMessageModel) {
            SendMessageModel sendMessageModel = (SendMessageModel) customResponse;
            if (sendMessageModel.getCommon().getStatusCode().equals(RescribeConstants.SUCCESS)) {
                // message sent
                messageType.setText("");
            } else {
                if (chatAdapter != null) {
                    messageList.remove(messageList.size() - 1);
                    chatAdapter.notifyItemRemoved(messageList.size() - 1);
                }
                CommonMethods.showToast(ChatActivity.this, sendMessageModel.getCommon().getStatusMessage());
            }
        } else if (customResponse instanceof ChatHistoryModel) {
            ChatHistoryModel chatHistoryModel = (ChatHistoryModel) customResponse;
            if (chatHistoryModel.getCommon().getStatusCode().equals(RescribeConstants.SUCCESS)) {
                List<ChatHistory> chatHistory = chatHistoryModel.getHistoryData().getChatHistory();

                ArrayList<MessageList> messageListTemp = new ArrayList<>();

                for (ChatHistory chatH : chatHistory) {
                    MessageList messageL = new MessageList();
                    messageL.setMsgId(chatH.getChatId());
                    messageL.setMsg(chatH.getMsg());
                    messageL.setDocId(chatH.getUser1Id());
                    messageL.setPatId(chatH.getUser2Id());
                    messageL.setWho(chatH.getSender().equals("user1") ? ChatAdapter.SENDER : ChatAdapter.RECEIVER);
                    String msgTime = CommonMethods.getFormatedDate(chatH.getMsgTime(), RescribeConstants.DATE_PATTERN.UTC_PATTERN, RescribeConstants.DATE_PATTERN.YYYY_MM_DD_hh_mm_ss);
                    messageL.setMsgTime(msgTime);
                    messageListTemp.add(messageL);
                }

                messageList.addAll(0, messageListTemp);
                chatAdapter.notifyDataSetChanged();
                chatList.scrollToPosition(messageList.size() - 1);
            }
        }
    }

    @Override
    public void onParseError(String mOldDataTag, String errorMessage) {
        if (chatAdapter != null) {
            messageList.remove(messageList.size() - 1);
            chatAdapter.notifyItemRemoved(messageList.size() - 1);
        }
    }

    @Override
    public void onServerError(String mOldDataTag, String serverErrorMessage) {
        if (chatAdapter != null) {
            messageList.remove(messageList.size() - 1);
            chatAdapter.notifyItemRemoved(messageList.size() - 1);
        }
    }

    @Override
    public void onNoConnectionError(String mOldDataTag, String serverErrorMessage) {
        if (chatAdapter != null) {
            messageList.remove(messageList.size() - 1);
            chatAdapter.notifyItemRemoved(messageList.size() - 1);
        }
    }
}
