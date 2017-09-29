package com.rescribe.doctor.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
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
import com.rescribe.doctor.model.chat.TypeStatus;
import com.rescribe.doctor.model.chat.history.ChatHistory;
import com.rescribe.doctor.model.chat.history.ChatHistoryModel;
import com.rescribe.doctor.model.patient_connect.PatientData;
import com.rescribe.doctor.notification.MessageNotification;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.services.MQTTService;
import com.rescribe.doctor.singleton.Device;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.Config;
import com.rescribe.doctor.util.NetworkUtil;
import com.rescribe.doctor.util.RescribeConstants;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadService;
import net.gotev.uploadservice.UploadServiceBroadcastReceiver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import ng.max.slideview.SlideView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static android.app.DownloadManager.ACTION_DOWNLOAD_COMPLETE;
import static com.rescribe.doctor.services.MQTTService.DOCTOR;
import static com.rescribe.doctor.services.MQTTService.NOTIFY;
import static com.rescribe.doctor.ui.activities.PatientConnectActivity.FREE;
import static com.rescribe.doctor.util.RescribeConstants.COMPLETED;
import static com.rescribe.doctor.util.RescribeConstants.FAILED;
import static com.rescribe.doctor.util.RescribeConstants.FILE.AUD;
import static com.rescribe.doctor.util.RescribeConstants.FILE.DOC;
import static com.rescribe.doctor.util.RescribeConstants.FILE.IMG;
import static com.rescribe.doctor.util.RescribeConstants.SEND_MESSAGE;
import static com.rescribe.doctor.util.RescribeConstants.UPLOADING;
import static com.rescribe.doctor.util.RescribeConstants.USER_STATUS.TYPING;

@RuntimePermissions
public class ChatActivity extends AppCompatActivity implements HelperResponse, ChatAdapter.ItemListener {

    // Audio

    private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private ImageView audioIcon;
    private boolean isPlaying = false;

    // Audio End

    private static final int MAX_ATTACHMENT_COUNT = 10;
    public static final String CHAT = "chat";

    private static final String RESCRIBE_FILES = "/Rescribe/Files/";
    private static final String RESCRIBE_PHOTOS = "/Rescribe/Photos/";
    private static final String RESCRIBE_AUDIO = "/Rescribe/Audios/";

    private static final String RESCRIBE_UPLOAD_FILES = "/Rescribe/SentFiles/";
    private static final String RESCRIBE_UPLOAD_PHOTOS = "/Rescribe/SentPhotos/";
    private static final String RESCRIBE_UPLOAD_AUDIO = "/Rescribe/SentAudios/";

    private String filesFolder;
    private String photosFolder;
    private String audioFolder;

    private String filesUploadFolder;
    private String photosUploadFolder;
    private String audioUploadFolder;

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
    @BindView(R.id.sendButton)
    ImageView sendButton;
    @BindView(R.id.messageTypeLayout)
    RelativeLayout messageTypeLayout;

    @BindView(R.id.swipeLayout)
    SwipeRefreshLayout swipeLayout;

    @BindView(R.id.audioSlider)
    SlideView audioSlider;

    // Check Typing

    final int TYPING_TIMEOUT = 3000; // 5 seconds timeout
    private static final String TYPING_MESSAGE = "typing...";
    final Handler timeoutHandler = new Handler();
    private boolean isTyping;

    final Runnable typingTimeout = new Runnable() {
        public void run() {
            isTyping = false;
            typingStatus();
        }
    };

    private void typingStatus() {
        TypeStatus typeStatus = new TypeStatus();
        String generatedId = TYPING + mqttMessage.size() + "_" + System.nanoTime();
        typeStatus.setMsgId(generatedId);
        typeStatus.setDocId(Integer.parseInt(docId));
        typeStatus.setPatId(chatList.getId());
        typeStatus.setSender(DOCTOR);
        typeStatus.setTypeStatus(isTyping);
        if (mqttService != null)
            mqttService.typingStatus(typeStatus);
    }

    // End Check Typing

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(NOTIFY)) {
                boolean delivered = intent.getBooleanExtra(MQTTService.DELIVERED, false);
                boolean isReceived = intent.getBooleanExtra(MQTTService.IS_MESSAGE, false);

                if (delivered) {

                    Log.d(TAG, "Delivery Complete");
                    Log.d(TAG + " MESSAGE_ID", intent.getStringExtra(MQTTService.MESSAGE_ID));

                } else if (isReceived) {
                    MQTTMessage message = intent.getParcelableExtra(MQTTService.MESSAGE);
                    if (message.getPatId() == chatList.getId()) {
                        if (chatAdapter != null) {
                            mqttMessage.add(message);
                            chatAdapter.notifyItemInserted(mqttMessage.size() - 1);
                            chatRecyclerView.smoothScrollToPosition(mqttMessage.size() - 1);
                        }
                    } else {
                        // Other user message

                    }
                } else {
                    // Getting type status
                    TypeStatus typeStatus = intent.getParcelableExtra(MQTTService.MESSAGE);
                    if (typeStatus.getPatId() == chatList.getId()) {
                        if (typeStatus.isTyping()) {
                            dateTime.setText(TYPING_MESSAGE);
                            dateTime.setTextColor(Color.WHITE);
                        } else {
                            dateTime.setText(chatList.getOnlineStatus());
                            dateTime.setTextColor(statusColor);
                        }
                    } else {
                        // Other use message

                    }
                }
            } else if (intent.getAction().equals(ACTION_DOWNLOAD_COMPLETE)) {
                checkDownloaded();
            }
        }
    };

    void checkDownloaded() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
        Cursor c = downloadManager.query(query);

        if (c.moveToFirst()) {
            do {
                String fileUri = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                String fileName = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE));
                for (int index = mqttMessage.size() - 1; index >= 0; index--) {
                    if (mqttMessage.get(index).getMsg().equals(fileName)) {
                        mqttMessage.get(index).setDownloadStatus(COMPLETED);
                        mqttMessage.get(index).setFileUrl(fileUri);
                        chatAdapter.notifyItemChanged(index);
                        break;
                    }
                    Log.i(TAG, "downloaded file " + fileUri);
                }
            } while (c.moveToNext());
        }
    }

    private ChatHelper chatHelper;
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

    private PatientData chatList;
    private int statusColor;

    // Uploading
    private Device device;
    private String Url;
    private String authorizationString;
    private UploadNotificationConfig uploadNotificationConfig;
    private DownloadManager downloadManager;

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
        swipeLayout.setRefreshing(true);

        downloadInit();

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

        // off recyclerView Animation
        RecyclerView.ItemAnimator animator = chatRecyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator)
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);

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
                // reset the timeout
                timeoutHandler.removeCallbacks(typingTimeout);
                if (messageType.getText().toString().trim().length() > 0) {

                    audioSlider.setVisibility(View.INVISIBLE);
                    sendButton.setVisibility(View.VISIBLE);
                    cameraButton.setVisibility(View.GONE);
                    // Typing status
                    // schedule the timeout
                    timeoutHandler.postDelayed(typingTimeout, TYPING_TIMEOUT);
                    if (!isTyping) {
                        isTyping = true;
                        typingStatus();
                    }
                    // End Typing status
                } else {

                    audioSlider.setVisibility(View.VISIBLE);
                    sendButton.setVisibility(View.INVISIBLE);
                    cameraButton.setVisibility(View.VISIBLE);
                    // Typing status
                    isTyping = false;
                    typingStatus();
                    // End typing status
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        uploadInit();
        audioSliderInit();
        //----------
    }

    // Audio Code

    private void audioSliderInit() {
        // Get Audio Permission

        // Record to the external cache directory for visibility
        mFileName = audioUploadFolder;

        ChatActivityPermissionsDispatcher.getAudioPermissionWithCheck(ChatActivity.this);

        audioSlider.getTextView().setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mic_red_24dp, 0, 0, 0);
        audioSlider.getTextView().setCompoundDrawablePadding(CommonMethods.convertDpToPixel(5));
        audioSlider.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {
                messageTypeSubLayout.setVisibility(View.VISIBLE);
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(100);
                cntr_aCounter.cancel();
                stopRecording(false);
                File file = new File(mFileName);
                boolean deleted = file.delete();
                mFileName = audioUploadFolder;

            }
        });

        audioSlider.setOnActionDownListener(new SlideView.OnActionDownListener() {
            @Override
            public void OnActionDown(SlideView slideView) {
                Log.d("Start", "Track");
                messageTypeSubLayout.setVisibility(View.INVISIBLE);

                mFileName += "Aud_" + System.nanoTime() + ".mp3";
                cntr_aCounter.start();
                startRecording();
            }
        });

        audioSlider.setOnActionUpListener(new SlideView.OnActionUpListener() {
            @Override
            public void OnActionUp(SlideView slideView) {
                Log.d("Stop", "Track");
                messageTypeSubLayout.setVisibility(View.VISIBLE);
                cntr_aCounter.cancel();
                stopRecording(audioCounter > 2);
            }
        });
    }

    @NeedsPermission(Manifest.permission.RECORD_AUDIO)
    void getAudioPermission() {
        CommonMethods.Log(TAG, "asked permission");
    }

    private void startPlaying(String path) {
        mPlayer = new MediaPlayer();
        try {
            audioIcon.setImageResource(R.drawable.ic_stop_white_24dp);
            mPlayer.setDataSource(path);
            mPlayer.prepare();
            mPlayer.start();
            isPlaying = true;
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                audioIcon.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                isPlaying = false;
            }
        });
    }

    private void stopPlaying() {
        audioIcon.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        try {
            mPlayer.release();
            mPlayer = null;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            isPlaying = false;
        }

    }

    private int audioCounter = 0;
    CountDownTimer cntr_aCounter = new CountDownTimer(60_000, 1_000) {
        public void onTick(long millisUntilFinished) {
            // recodeing code
            NumberFormat f = new DecimalFormat("00");
            String time = "00:" + f.format(audioCounter) + "  " + getResources().getString(R.string.timing);
            audioSlider.getTextView().setText(time);
            audioCounter += 1;
        }

        public void onFinish() {
            //finish action
            try {
                stopRecording(true);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        try {
            mRecorder.start();
        } catch (Exception e) {
            Log.e(LOG_TAG, "prepare() start");
        }

    }

    private void stopRecording(boolean isSend) {
        CommonMethods.Log("isCanceled Recording : " + audioCounter, String.valueOf(isSend));
        audioCounter = 0;
        try {
            mRecorder.stop();
            mRecorder.release();
        } catch (RuntimeException ex) {
            //Ignore
        }
        mRecorder = null;

        if (isSend) {
            ArrayList<String> audioFile = new ArrayList<String>();
            audioFile.add(mFileName);
            uploadFiles(audioFile, RescribeConstants.FILE.AUD);
            mFileName = audioUploadFolder;
        }
    }

    // End Audio Code

    private void downloadInit() {
        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

        File sdCard = Environment.getExternalStorageDirectory();
        filesFolder = sdCard.getAbsolutePath() + RESCRIBE_FILES;
        photosFolder = sdCard.getAbsolutePath() + RESCRIBE_PHOTOS;
        audioFolder = sdCard.getAbsolutePath() + RESCRIBE_AUDIO;

        File dirFilesFolder = new File(filesFolder);
        if (!dirFilesFolder.exists()) {
            if (dirFilesFolder.mkdirs()) {
                Log.i(TAG, filesFolder + " Directory Created");
            }
        }
        File dirPhotosFolder = new File(photosFolder);
        if (!dirPhotosFolder.exists()) {
            if (dirPhotosFolder.mkdirs()) {
                Log.i(TAG, photosFolder + " Directory Created");
            }
        }
        File dirAudioFolder = new File(audioFolder);
        if (!dirAudioFolder.exists()) {
            if (dirAudioFolder.mkdirs()) {
                Log.i(TAG, audioFolder + " Directory Created");
            }
        }
    }

    private void uploadInit() {

        File sdCard = Environment.getExternalStorageDirectory();
        filesUploadFolder = sdCard.getAbsolutePath() + RESCRIBE_UPLOAD_FILES;
        photosUploadFolder = sdCard.getAbsolutePath() + RESCRIBE_UPLOAD_PHOTOS;
        audioUploadFolder = sdCard.getAbsolutePath() + RESCRIBE_UPLOAD_AUDIO;

        File dirFilesFolder = new File(filesUploadFolder);
        if (!dirFilesFolder.exists()) {
            if (dirFilesFolder.mkdirs()) {
                Log.i(TAG, filesUploadFolder + " Directory Created");
            }
        }
        File dirPhotosFolder = new File(photosUploadFolder);
        if (!dirPhotosFolder.exists()) {
            if (dirPhotosFolder.mkdirs()) {
                Log.i(TAG, photosUploadFolder + " Directory Created");
            }
        }
        File dirAudioFolder = new File(audioUploadFolder);
        if (!dirAudioFolder.exists()) {
            if (dirAudioFolder.mkdirs()) {
                Log.i(TAG, audioUploadFolder + " Directory Created");
            }
        }

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

    @OnClick({R.id.backButton, R.id.attachmentButton, R.id.cameraButton, R.id.sendButton})
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
            case R.id.sendButton:
                // SendButton
                String message = messageType.getText().toString();
                message = message.trim();
                if (!message.equals("")) {

                    MQTTMessage messageL = new MQTTMessage();
                    messageL.setTopic(MQTTService.TOPIC[0]);
                    messageL.setSender(DOCTOR);
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
                    if (NetworkUtil.getConnectivityStatusBoolean(ChatActivity.this)) {
                        if (chatAdapter != null) {
                            mqttService.passMessage(messageL);
                            messageType.setText("");
                            mqttMessage.add(messageL);
                            chatAdapter.notifyItemInserted(mqttMessage.size() - 1);
                            chatRecyclerView.smoothScrollToPosition(mqttMessage.size() - 1);
                        }
                    } else
                        CommonMethods.showToast(ChatActivity.this, getResources().getString(R.string.internet));
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
                    uploadFiles(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS), RescribeConstants.FILE.DOC);
                }
            }
        }
    }

    private void uploadFiles(ArrayList<String> files, String fileType) {
        int startPosition = mqttMessage.size() + 1;
        for (String file : files) {

            String fileForUpload = copyFile(CommonMethods.getFilePath(file), CommonMethods.getFileNameFromPath(file), filesUploadFolder);

            MQTTMessage messageL = new MQTTMessage();
            messageL.setTopic(MQTTService.TOPIC[0]);
            messageL.setSender(DOCTOR);

            String fileName = fileForUpload.substring(fileForUpload.lastIndexOf("/") + 1);

            messageL.setMsg(fileName);

            String generatedId = CHAT + mqttMessage.size() + "_" + System.nanoTime();

            messageL.setMsgId(generatedId);

            messageL.setFileUrl(fileForUpload);
            messageL.setFileType(fileType);

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

            String fileForUpload = copyFile(CommonMethods.getFilePath(file), CommonMethods.getFileNameFromPath(file), photosUploadFolder);

            MQTTMessage messageL = new MQTTMessage();
            messageL.setTopic(MQTTService.TOPIC[0]);
            messageL.setSender(DOCTOR);
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

            messageL.setFileUrl(fileForUpload);
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
            mqttService.setCurrentChatUser(chatList.getId()); // Change
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

        if (mRecorder != null) {
            try {
                mRecorder.release();
            } catch (Exception e) {
                // ignore
            }
            mRecorder = null;
        }

        if (mPlayer != null) {
            try {
                mPlayer.release();
            } catch (Exception e) {
                // ignore
            }
            mPlayer = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        broadcastReceiver.register(this);
        registerReceiver(receiver, new IntentFilter(
                MQTTService.NOTIFY));

        registerReceiver(receiver, new IntentFilter(
                ACTION_DOWNLOAD_COMPLETE));

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

        // Type status
        // reset the timeout
        timeoutHandler.removeCallbacks(typingTimeout);
        isTyping = false;
        typingStatus();
        // Type status End

        broadcastReceiver.unregister(this);
        unregisterReceiver(receiver);
        if (mqttService != null)
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

                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
                Cursor cu = downloadManager.query(query);

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
                    messageL.setImageUrl(chatH.getImageUrl());
                    messageL.setPaidStatus(chatH.getPaidStatus());
                    messageL.setFileType(chatH.getFileType());
                    messageL.setFileUrl(chatH.getFileUrl());

                    messageL.setUploadStatus(COMPLETED);

                    if (chatH.getSender().equals(DOCTOR)) {
                        if (chatH.getFileType().equals(AUD)) {
                            messageL.setFileUrl(audioUploadFolder + chatH.getMsg());
                        } else if (chatH.getFileType().equals(DOC)) {
                            messageL.setFileUrl(filesUploadFolder + chatH.getMsg());
                        }
                    }

                    // Check Download
                    if (chatH.getFileType().equals(DOC) || chatH.getFileType().equals(AUD)) {
                        if (cu.moveToFirst()) {
                            do {
                                String fileUri = cu.getString(cu.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                                String fileName = cu.getString(cu.getColumnIndex(DownloadManager.COLUMN_TITLE));
                                if (messageL.getMsg().equals(fileName)) {
                                    messageL.setDownloadStatus(COMPLETED);
                                    messageL.setFileUrl(fileUri);
                                }
                            } while (cu.moveToNext());
                        }
                    }
                    // End

                    String msgTime = "";
                    if (chatH.getMsgTime() != null)
                        msgTime = CommonMethods.getFormattedDate(chatH.getMsgTime(), RescribeConstants.DATE_PATTERN.YYYY_MM_DD_hh_mm_ss, RescribeConstants.DATE_PATTERN.YYYY_MM_DD_hh_mm_ss);
                    messageL.setMsgTime(msgTime);
                    mqttMessage.add(0, messageL);
                }

                cu.close();

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

            final int startPosition = mqttMessage.size() + 1;
            int addedCount = 0;

            MQTTData messageData = appDBHelper.getMessageUpload();
            ArrayList<MQTTMessage> mqttMessList = messageData.getMqttMessages();

            for (MQTTMessage mqttMess : mqttMessList) {
                if (chatList.getId() == mqttMess.getDocId()) { // Change
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

            swipeLayout.setRefreshing(false);
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

        appDBHelper.insertMessageUpload(mqttMessage.getMsgId(), RescribeConstants.UPLOADING, new Gson().toJson(mqttMessage));
    }

    // Download File

    @Override
    public long downloadFile(MQTTMessage mqttMessage) {
        long downloadReference;

        // For Test Big File Download
//        mqttMessage.setFileUrl("https://dl.google.com/dl/android/studio/ide-zips/2.3.3.0/android-studio-ide-162.4069837-linux.zip");

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mqttMessage.getFileUrl()));

        //Setting title of request
        request.setTitle(mqttMessage.getMsg());

        //Setting description of request
        request.setDescription("Rescribe File Downloading");

        request.allowScanningByMediaScanner();

        //Set the local destination for the downloaded file to a path
        //within the application's external files directory

        request.setDestinationInExternalPublicDir(RESCRIBE_FILES, CommonMethods.getFileNameFromPath(mqttMessage.getFileUrl()));

        // Keep notification after complete
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        //Enqueue download and save into referenceId
        downloadReference = downloadManager.enqueue(request);

        return downloadReference;
    }

    @Override
    public void openFile(MQTTMessage message, ImageView senderFileIcon) {

        Uri uriTemp = Uri.parse(message.getFileUrl());

        if (message.getFileType().equals(DOC)) {

            File file;
            if (uriTemp.toString().contains("file://"))
                file = new File(uriTemp.getPath());
            else file = new File(uriTemp.toString());

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            Uri uri = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".droidninja.filepicker.provider", file);
            } else {
                uri = Uri.fromFile(createImageFile(uriTemp));
            }

            // Check what kind of file you are trying to open, by comparing the uri with extensions.
            // When the if condition is matched, plugin sets the correct intent (mime) type,
            // so Android knew what application to use to open the file
            if (uri.toString().contains(".doc") || uri.toString().contains(".docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword");
            } else if (uri.toString().contains(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf");
            } else if (uri.toString().contains(".ppt") || uri.toString().contains(".pptx")) {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
            } else if (uri.toString().contains(".xls") || uri.toString().contains(".xlsx")) {
                // Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel");
            } else if (uri.toString().contains(".zip") || uri.toString().contains(".rar")) {
                // WAV audio file
                intent.setDataAndType(uri, "application/x-wav");
            } else if (uri.toString().contains(".rtf")) {
                // RTF file
                intent.setDataAndType(uri, "application/rtf");
            } else if (uri.toString().contains(".wav") || uri.toString().contains(".mp3")) {
                // WAV audio file
                intent.setDataAndType(uri, "audio/x-wav");
            } else if (uri.toString().contains(".gif")) {
                // GIF file
                intent.setDataAndType(uri, "image/gif");
            } else if (uri.toString().contains(".jpg") || uri.toString().contains(".jpeg") || uri.toString().contains(".png")) {
                // JPG file
                intent.setDataAndType(uri, "image/jpeg");
            } else if (uri.toString().contains(".txt")) {
                // Text file
                intent.setDataAndType(uri, "text/plain");
            } else if (uri.toString().contains(".3gp") || uri.toString().contains(".mpg") || uri.toString().contains(".mpeg") || uri.toString().contains(".mpe") || uri.toString().contains(".mp4") || uri.toString().contains(".avi")) {
                // Video files
                intent.setDataAndType(uri, "video/*");
            } else {
                //if you want you can also define the intent type for any other file

                //additionally use else clause below, to manage other unknown extensions
                //in this case, Android will show all applications installed on the device
                //so you can choose which application to use
                intent.setDataAndType(uri, "*/*");
            }

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                CommonMethods.showToast(ChatActivity.this, getResources().getString(R.string.doc_viewer_not_found));
            }
        } else if (message.getFileType().equals(AUD)) {

            if (this.audioIcon != null) {
                this.audioIcon.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                senderFileIcon.setImageResource(R.drawable.ic_stop_white_24dp);
            } else {
                senderFileIcon.setImageResource(R.drawable.ic_stop_white_24dp);
            }

            this.audioIcon = senderFileIcon;

            if (!isPlaying)
                startPlaying(message.getFileUrl());
            else {
                stopPlaying();
                startPlaying(message.getFileUrl());
            }
        }
    }

    private File createImageFile(Uri uriTemp) {
        return new File(filesFolder, CommonMethods.getFileNameFromPath(uriTemp.toString()));
    }

    // Broadcast

    private UploadServiceBroadcastReceiver broadcastReceiver = new UploadServiceBroadcastReceiver() {
        @Override
        public void onProgress(Context context, UploadInfo uploadInfo) {
        }

        @Override
        public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {

            CommonMethods.Log(TAG, "FaildUpload");

            if (uploadInfo.getUploadId().length() > CHAT.length()) {
                String prefix = uploadInfo.getUploadId().substring(0, 4);
                if (prefix.equals(CHAT)) {
                    appDBHelper.updateMessageUpload(uploadInfo.getUploadId(), FAILED);

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

            CommonMethods.Log(TAG, "onCompleted " + serverResponse.getBodyAsString());

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

    private String copyFile(String inputPath, String inputFile, String outputPath) {
        InputStream in;
        OutputStream out;
        try {
            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();

            // write the output file (You have now copied the file)
            out.flush();
            out.close();

        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
        return outputPath + inputFile;
    }
}