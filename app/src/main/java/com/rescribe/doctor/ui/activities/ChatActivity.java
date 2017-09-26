package com.rescribe.doctor.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.gson.Gson;
import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.chat.ChatAdapter;
import com.rescribe.doctor.helpers.chat.ChatHelper;
import com.rescribe.doctor.helpers.database.AppDBHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.chat.MQTTData;
import com.rescribe.doctor.model.chat.MQTTMessage;
import com.rescribe.doctor.model.chat.SendMessageModel;
import com.rescribe.doctor.model.chat.history.ChatHistory;
import com.rescribe.doctor.model.chat.history.ChatHistoryModel;
import com.rescribe.doctor.model.patient_connect.PatientData;
import com.rescribe.doctor.notification.MessageNotification;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.service.MQTTService;
import com.rescribe.doctor.singleton.Device;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.Config;
import com.rescribe.doctor.util.RescribeConstants;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadService;
import net.gotev.uploadservice.UploadServiceBroadcastReceiver;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.rescribe.doctor.ui.activities.PatientConnectActivity.FREE;
import static com.rescribe.doctor.util.RescribeConstants.COMPLETED;
import static com.rescribe.doctor.util.RescribeConstants.FAILED;
import static com.rescribe.doctor.util.RescribeConstants.FILE.DOC;
import static com.rescribe.doctor.util.RescribeConstants.FILE.IMG;
import static com.rescribe.doctor.util.RescribeConstants.SEND_MESSAGE;
import static com.rescribe.doctor.util.RescribeConstants.UPLOADING;

@RuntimePermissions
public class ChatActivity extends AppCompatActivity implements HelperResponse, ChatAdapter.ItemListener {

    private static final int MAX_ATTACHMENT_COUNT = 10;
    public static final String CHAT = "chat";
    @BindView(R.id.backButton)
    ImageView backButton;
    @BindView(R.id.profilePhoto)
    ImageView profilePhoto;
    @BindView(R.id.receiverName)
    CustomTextView receiverName;
    @BindView(R.id.onlineStatus)
    CustomTextView dateTime;
    @BindView(R.id.titleLayout)
    RelativeLayout titleLayout;
    @BindView(R.id.toolbar)
    RelativeLayout toolbar;
    @BindView(R.id.chatList)
    RecyclerView chatRecyclerView;
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

    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout swipeLayout;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            boolean delivered = intent.getBooleanExtra(MQTTService.DELIVERED, false);
            boolean isReceived = intent.getBooleanExtra(MQTTService.RECEIVED, false);

            if (delivered) {

                Log.d(TAG, "Delivery Complete");
                Log.d(TAG + " MESSAGE_ID", intent.getStringExtra(MQTTService.MESSAGE_ID));

            } else if (isReceived) {
                MQTTMessage message = intent.getParcelableExtra(MQTTService.MESSAGE);
                // Change
                if (message.getPatId() == chatList.getId()) {
                    if (chatAdapter != null) {
                        mqttMessage.add(message);
                        chatAdapter.notifyItemInserted(mqttMessage.size() - 1);
                        chatRecyclerView.smoothScrollToPosition(mqttMessage.size() - 1);
                    }
                } else {
                    // Other patient message

                }
            }
        }
    };

    private ChatHelper chatHelper;
    private boolean isSend = false;
    private boolean isExistInChat = false;

    private static final String TAG = "ChatActivity";
    private ChatAdapter chatAdapter;
    private ArrayList<MQTTMessage> mqttMessage = new ArrayList<>();

    private String docId;
    private TextDrawable doctorTextDrawable;

    // load more
    int next = 1;

    private AppDBHelper appDBHelper;
    private int isFirstTime = 0;
    private String docName;
    private String imageUrl = "";
    private String speciality = "";
    private String fileUrl = "";

    private PatientData chatList;
    private int statusColor;

    // Uploading
    private Device device;
    private String Url;
    private String authorizationString;
    private UploadNotificationConfig uploadNotificationConfig;
//    private DownloadManager downloadManager;

    @Override
    public void onBackPressed() {
        if (isExistInChat) {
            if (mqttMessage.isEmpty())
                setResult(Activity.RESULT_CANCELED);
            else {
                Intent in = new Intent();
                in.putExtra(RescribeConstants.CHAT_USERS, chatList);
                setResult(Activity.RESULT_OK, in);
            }
        } else setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        appDBHelper = new AppDBHelper(this);

        chatList = getIntent().getParcelableExtra(RescribeConstants.PATIENT_INFO);
        statusColor = getIntent().getIntExtra(RescribeConstants.STATUS_COLOR, ContextCompat.getColor(ChatActivity.this, R.color.green_light));

        receiverName.setText(chatList.getPatientName());
        String doctorName = chatList.getPatientName();

        if (doctorName != null) {
//            doctorName = doctorName.replace("Dr. ", "");
            int color2 = ColorGenerator.MATERIAL.getColor(doctorName);
            doctorTextDrawable = TextDrawable.builder()
                    .beginConfig()
                    .width(Math.round(getResources().getDimension(R.dimen.dp40)))  // width in px
                    .height(Math.round(getResources().getDimension(R.dimen.dp40))) // height in px
                    .endConfig()
                    .buildRound(("" + doctorName.charAt(0)).toUpperCase(), color2);
        }

        // Remove

        profilePhoto.setImageDrawable(doctorTextDrawable);

        /*if (chatList.getImageUrl() != null) {
            if (!chatList.getImageUrl().equals("")) {
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.dontAnimate();
                requestOptions.override(CommonMethods.convertDpToPixel(40), CommonMethods.convertDpToPixel(40));
                requestOptions.placeholder(doctorTextDrawable);
                requestOptions.error(doctorTextDrawable);

                Glide.with(ChatActivity.this)
                        .load(chatList.getImageUrl())
                        .apply(requestOptions).thumbnail(0.5f)
                        .into(profilePhoto);

            } else {
                profilePhoto.setImageDrawable(doctorTextDrawable);
            }
        } else {
            profilePhoto.setImageDrawable(doctorTextDrawable);
        }*/

        dateTime.setText(chatList.getOnlineStatus());
        dateTime.setTextColor(statusColor);

        chatHelper = new ChatHelper(this, this);
        docId = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, this);
        docName = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.USER_NAME, this);
        imageUrl = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.PROFILE_PHOTO, this);
        speciality = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.SPECIALITY, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        chatRecyclerView.setLayoutManager(mLayoutManager);
        chatAdapter = new ChatAdapter(mqttMessage, doctorTextDrawable, ChatActivity.this);
        chatRecyclerView.setAdapter(chatAdapter);

        chatHelper.getChatHistory(next, Integer.parseInt(docId), chatList.getId());

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                chatHelper.getChatHistory(next, Integer.parseInt(docId), chatList.getId());
            }
        });

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

        uploadInit();
        downloadInit();
        //----------
    }

    private void downloadInit() {

    }

    private void uploadInit() {
        // Uploading

        device = Device.getInstance(ChatActivity.this);

        Url = Config.BASE_URL + Config.CHAT_FILE_UPLOAD;

        authorizationString = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.AUTHTOKEN, ChatActivity.this);

        uploadNotificationConfig = new UploadNotificationConfig();
        uploadNotificationConfig.setTitleForAllStatuses("File Uploading");
        uploadNotificationConfig.setIconColorForAllStatuses(Color.parseColor("#04abdf"));
        uploadNotificationConfig.setClearOnActionForAllStatuses(true);

        UploadService.UPLOAD_POOL_SIZE = 10;
    }

    @OnClick({R.id.backButton, R.id.attachmentButton, R.id.cameraButton, R.id.recorderOrSendButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backButton:
                onBackPressed();
                break;
            case R.id.attachmentButton:
                ChatActivityPermissionsDispatcher.onPickDocWithCheck(ChatActivity.this);
                break;
            case R.id.cameraButton:
                ChatActivityPermissionsDispatcher.onPickPhotoWithCheck(ChatActivity.this);
                break;
            case R.id.recorderOrSendButton:
                if (isSend) {

                    // SendButton
                    String message = messageType.getText().toString();
                    message = message.trim();
                    if (!message.equals("")) {

                        MQTTMessage messageL = new MQTTMessage();
                        messageL.setTopic(MQTTService.DOCTOR_CONNECT);
                        messageL.setSender(MQTTService.DOCTOR);
                        messageL.setMsg(message);

                        String generatedId = CHAT + mqttMessage.size() + "_" + System.nanoTime();

                        messageL.setMsgId(generatedId);

                        messageL.setDocId(Integer.parseInt(docId));
                        messageL.setPatId(chatList.getId());
                        messageL.setName(docName);
                        messageL.setOnlineStatus(RescribeConstants.USER_STATUS.ONLINE);
                        messageL.setImageUrl(imageUrl);
                        messageL.setSpecialization(speciality);
                        messageL.setPaidStatus(FREE);

                        messageL.setFileUrl("");
                        messageL.setFileType("");

                        // send msg by http api
//                        chatHelper.sendMsgToPatient(messageL);

                        // send msg by mqtt
                        mqttService.passMessage(messageL);

                        if (mqttService.getNetworkStatus()) {
                            if (chatAdapter != null) {
                                messageType.setText("");
                                mqttMessage.add(messageL);
                                chatAdapter.notifyItemInserted(mqttMessage.size() - 1);
                                chatRecyclerView.smoothScrollToPosition(mqttMessage.size() - 1);
                            }
                        } else
                            CommonMethods.showToast(ChatActivity.this, getResources().getString(R.string.internet));
                    }
                } else {

                    // Record Button stuff here

                }
                break;
        }
    }

    // File Selecting

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void onPickPhoto() {
        FilePickerBuilder.getInstance().setMaxCount(MAX_ATTACHMENT_COUNT)
                .setSelectedFiles(new ArrayList<String>())
                .setActivityTheme(R.style.AppTheme)
                .enableVideoPicker(false)
                .enableCameraSupport(true)
                .showGifs(false)
                .showFolderView(true)
                .enableOrientation(true)
                .pickPhoto(this);
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void onPickDoc() {
        String[] documents = {".doc", ".docx", ".odt", ".pdf", ".xls", ".xlsx", ".ods", ".ppt", ".pptx"};
        FilePickerBuilder.getInstance().setMaxCount(MAX_ATTACHMENT_COUNT)
                .setSelectedFiles(new ArrayList<String>())
                .setActivityTheme(R.style.AppTheme)
                .addFileSupport(documents)
                .enableDocSupport(false)
                .enableOrientation(true)
                .pickFile(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ChatActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FilePickerConst.REQUEST_CODE_PHOTO) {
                if (!data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA).isEmpty()) {
                    uploadPhotos(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                }
            } else if (requestCode == FilePickerConst.REQUEST_CODE_DOC) {
                if (!data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS).isEmpty()) {
                    uploadFiles(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                }
            }
        }
    }

    private void uploadFiles(ArrayList<String> files) {
        int startPosition = mqttMessage.size() + 1;
        for (String file : files) {
            MQTTMessage messageL = new MQTTMessage();
            messageL.setTopic(MQTTService.DOCTOR_CONNECT);
            messageL.setSender(MQTTService.DOCTOR);

            String fileName = file.substring(file.lastIndexOf("/") + 1);

            messageL.setMsg(fileName);

            String generatedId = CHAT + mqttMessage.size() + "_" + System.nanoTime();

            messageL.setMsgId(generatedId);

            messageL.setFileUrl(file);
            messageL.setFileType(DOC);

            messageL.setDocId(Integer.parseInt(docId));
            messageL.setPatId(chatList.getId());
            messageL.setName(docName);
            messageL.setOnlineStatus(RescribeConstants.USER_STATUS.ONLINE);
            messageL.setImageUrl(imageUrl);
            messageL.setSpecialization(speciality);
            messageL.setPaidStatus(FREE);

            // send msg by mqtt
//            mqttService.passMessage(messageL);

            mqttMessage.add(messageL);

            uploadFile(messageL);
        }

        if (chatAdapter != null) {
            chatAdapter.notifyItemRangeInserted(startPosition, files.size());
            chatRecyclerView.scrollToPosition(mqttMessage.size() - 1);
        }
    }

    private void uploadPhotos(ArrayList<String> files) {
        int startPosition = mqttMessage.size() + 1;
        for (String file : files) {
            MQTTMessage messageL = new MQTTMessage();
            messageL.setTopic(MQTTService.DOCTOR_CONNECT);
            messageL.setSender(MQTTService.DOCTOR);
            messageL.setMsg("");

            String generatedId = CHAT + mqttMessage.size() + "_" + System.nanoTime();

            messageL.setMsgId(generatedId);
            messageL.setDocId(Integer.parseInt(docId));
            messageL.setPatId(chatList.getId());
            messageL.setName(docName);
            messageL.setOnlineStatus(RescribeConstants.USER_STATUS.ONLINE);
            messageL.setImageUrl(imageUrl);
            messageL.setSpecialization(speciality);
            messageL.setPaidStatus(FREE);

            messageL.setFileUrl(file);
            messageL.setFileType(IMG);

            messageL.setUploadStatus(UPLOADING);

// send msg by mqtt
//            mqttService.passMessage(messageL);

            mqttMessage.add(messageL);

            uploadFile(messageL);
        }

        if (chatAdapter != null) {
            chatAdapter.notifyItemRangeInserted(startPosition, files.size());

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    chatRecyclerView.scrollToPosition(mqttMessage.size() - 1);
                    CommonMethods.Log(TAG, "Scrolled");
                }
            }, 200);
        }
    }

    // End File Selecting

    boolean mBounded;
    MQTTService mqttService;

    ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
//            Toast.makeText(ChatActivity.this, "Service is disconnected", Toast.LENGTH_SHORT).show();
            mBounded = false;
            mqttService = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
//            Toast.makeText(ChatActivity.this, "Service is connected", Toast.LENGTH_SHORT).show();
            mBounded = true;
            MQTTService.LocalBinder mLocalBinder = (MQTTService.LocalBinder) service;
            mqttService = mLocalBinder.getServerInstance();

            // set Current Chat User
            mqttService.setCurrentChatUser(Integer.parseInt(docId));
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent mIntent = new Intent(this, MQTTService.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        broadcastReceiver.register(this);
        registerReceiver(receiver, new IntentFilter(
                MQTTService.NOTIFY));

        if (isFirstTime > 0) {
            ArrayList<MQTTMessage> unreadMessages = appDBHelper.getUnreadMessagesById(chatList.getId());
            if (unreadMessages.size() > 0) {
                mqttMessage.addAll(unreadMessages);
                chatAdapter.notifyItemRangeInserted(mqttMessage.size() - 1, unreadMessages.size());
                MessageNotification.cancel(this, chatList.getId());
                appDBHelper.deleteUnreadMessage(chatList.getId());
            }
        }

        isFirstTime += 1;
    }

    @Override
    protected void onPause() {
        super.onPause();
        broadcastReceiver.unregister(this);
        unregisterReceiver(receiver);
        mqttService.setCurrentChatUser(0);
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
                    mqttMessage.remove(mqttMessage.size() - 1);
                    chatAdapter.notifyItemRemoved(mqttMessage.size() - 1);
                }
                CommonMethods.showToast(ChatActivity.this, sendMessageModel.getCommon().getStatusMessage());
            }
        } else if (customResponse instanceof ChatHistoryModel) {
            ChatHistoryModel chatHistoryModel = (ChatHistoryModel) customResponse;
            if (chatHistoryModel.getCommon().getStatusCode().equals(RescribeConstants.SUCCESS)) {
                final List<ChatHistory> chatHistory = chatHistoryModel.getHistoryData().getChatHistory();

//                messageListTemp.clear();

                for (ChatHistory chatH : chatHistory) {
                    MQTTMessage messageL = new MQTTMessage();
                    messageL.setMsgId(chatH.getChatId());
                    messageL.setMsg(chatH.getMsg());
                    messageL.setDocId(chatH.getUser1Id());
                    messageL.setPatId(chatH.getUser2Id());
                    messageL.setSender(chatH.getSender());

                    messageL.setName(chatH.getName());
                    messageL.setSpecialization(chatH.getSpecialization());
                    messageL.setOnlineStatus(chatH.getOnlineStatus());
                    messageL.setAddress(chatH.getAddress());
                    messageL.setImageUrl(chatH.getImageUrl());
                    messageL.setPaidStatus(chatH.getPaidStatus());
                    messageL.setFileType(chatH.getFileType());
                    messageL.setUploadStatus(COMPLETED);

                    String msgTime = CommonMethods.getFormatedDate(chatH.getMsgTime(), RescribeConstants.DATE_PATTERN.UTC_PATTERN, RescribeConstants.DATE_PATTERN.YYYY_MM_DD_hh_mm_ss);
                    messageL.setMsgTime(msgTime);
                    mqttMessage.add(0, messageL);
                }

                if (next == 1) {
                    isExistInChat = mqttMessage.isEmpty();
                    chatRecyclerView.scrollToPosition(mqttMessage.size() - 1);
                    chatAdapter.notifyDataSetChanged();

                    // cancel notification
                    appDBHelper.deleteUnreadMessage(chatList.getId());
                    MessageNotification.cancel(this, chatList.getId());

                } else {
                    chatRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            // Notify adapter with appropriate notify methods
                            chatAdapter.notifyItemRangeInserted(0, chatHistory.size());
                        }
                    });
                }

                next += 1;
            }

            swipeLayout.setRefreshing(false);

            final int startPosition = mqttMessage.size() + 1;
            int addedCount = 0;

            MQTTData messageData = appDBHelper.getMessageData();
            ArrayList<MQTTMessage> mqttMessList = messageData.getMqttMessages();

            for (MQTTMessage mqttMess : mqttMessList) {
                if (chatList.getId() == mqttMess.getDocId()) {
                    mqttMessage.add(mqttMess);
                    addedCount += 1;
                }
            }
            final int finalAddedCount = addedCount;
            if (finalAddedCount > 0) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        chatAdapter.notifyItemRangeInserted(startPosition, finalAddedCount);
                        CommonMethods.Log(TAG, "Scrolled");
                    }
                }, 200);
            }
        }
    }

    @Override
    public void onParseError(String mOldDataTag, String errorMessage) {
        swipeLayout.setRefreshing(false);
        if (mOldDataTag.equals(SEND_MESSAGE)) {
            if (chatAdapter != null) {
                mqttMessage.remove(mqttMessage.size() - 1);
                chatAdapter.notifyItemRemoved(mqttMessage.size() - 1);
            }
        }
    }

    @Override
    public void onServerError(String mOldDataTag, String serverErrorMessage) {
        swipeLayout.setRefreshing(false);
        if (mOldDataTag.equals(SEND_MESSAGE)) {
            if (chatAdapter != null) {
                mqttMessage.remove(mqttMessage.size() - 1);
                chatAdapter.notifyItemRemoved(mqttMessage.size() - 1);
            }
        }
    }

    @Override
    public void onNoConnectionError(String mOldDataTag, String serverErrorMessage) {
        swipeLayout.setRefreshing(false);
        if (mOldDataTag.equals(SEND_MESSAGE)) {
            if (chatAdapter != null) {
                mqttMessage.remove(mqttMessage.size() - 1);
                chatAdapter.notifyItemRemoved(mqttMessage.size() - 1);
            }
        }
    }

    // Uploading

    @Override
    public void uploadFile(MQTTMessage mqttMessage) {
        try {

            MultipartUploadRequest uploadRequest = new MultipartUploadRequest(ChatActivity.this, String.valueOf(mqttMessage.getMsgId()), Url)
                    .setNotificationConfig(uploadNotificationConfig)
                    .setMaxRetries(RescribeConstants.MAX_RETRIES)

                    .addHeader(RescribeConstants.AUTHORIZATION_TOKEN, authorizationString)
                    .addHeader(RescribeConstants.DEVICEID, device.getDeviceId())
                    .addHeader(RescribeConstants.OS, device.getOS())
                    .addHeader(RescribeConstants.OSVERSION, device.getOSVersion())
                    .addHeader(RescribeConstants.DEVICE_TYPE, device.getDeviceType())

                    .addFileToUpload(mqttMessage.getFileUrl(), "chatDoc");

            uploadRequest.startUpload();

        } catch (FileNotFoundException | MalformedURLException e) {
            e.printStackTrace();
        }

        appDBHelper.insertMessageData(mqttMessage.getMsgId(), RescribeConstants.UPLOADING, new Gson().toJson(mqttMessage));
    }

    // Download File

    @Override
    public void downloadFile(MQTTMessage mqttMessage) {
        /*downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(mqttMessage.getFileUrl()));
        long enqueue = downloadManager.enqueue(request);*/
    }

    // Broadcast

    private UploadServiceBroadcastReceiver broadcastReceiver = new UploadServiceBroadcastReceiver() {
        @Override
        public void onProgress(Context context, UploadInfo uploadInfo) {
        }

        @Override
        public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {

            if (uploadInfo.getUploadId().length() > CHAT.length()) {
                String prefix = uploadInfo.getUploadId().substring(0, 4);
                if (prefix.equals(CHAT)) {
                    appDBHelper.updateMessageData(uploadInfo.getUploadId(), FAILED);

                    int position = getPositionById(uploadInfo.getUploadId());

                    mqttMessage.get(position).setUploadStatus(FAILED);
                    chatAdapter.notifyItemChanged(position);
                }
            }

        }

        private int getPositionById(String id) {
            int pos = 0;
            for (int position = mqttMessage.size() - 1; position >= 0; position--) {
                if (id.equals(mqttMessage.get(position).getMsgId())) {
                    pos = position;
                    break;
                }
            }
            return pos;
        }

        @Override
        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {

            if (uploadInfo.getUploadId().length() > CHAT.length()) {
                String prefix = uploadInfo.getUploadId().substring(0, 4);
                if (prefix.equals(CHAT)) {
//                    appDBHelper.deleteUploadedMessage(uploadInfo.getUploadId());

                    int position = getPositionById(uploadInfo.getUploadId());

                    mqttMessage.get(position).setUploadStatus(COMPLETED);
                    chatAdapter.notifyItemChanged(position);
                }
            }
        }

        @Override
        public void onCancelled(Context context, UploadInfo uploadInfo) {
        }
    };
}