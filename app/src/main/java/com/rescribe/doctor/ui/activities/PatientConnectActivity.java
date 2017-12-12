package com.rescribe.doctor.ui.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rescribe.doctor.R;
import com.rescribe.doctor.helpers.login.LoginHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.chat.MQTTMessage;
import com.rescribe.doctor.model.login.ActiveRequest;
import com.rescribe.doctor.model.patient_connect.PatientData;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.services.MQTTService;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.ui.customesViews.EditTextWithDeleteButton;
import com.rescribe.doctor.ui.customesViews.SwitchButton;
import com.rescribe.doctor.ui.fragments.patient_connect.PatientConnectChatFragment;
import com.rescribe.doctor.ui.fragments.patient_connect.PatientConnectFragment;
import com.rescribe.doctor.ui.fragments.patient_connect.PatientSearchFragment;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.rescribe.doctor.services.MQTTService.MESSAGE_TOPIC;
import static com.rescribe.doctor.services.MQTTService.NOTIFY;
import static com.rescribe.doctor.services.MQTTService.TOPIC;
import static com.rescribe.doctor.util.RescribeConstants.ACTIVE_STATUS;


/**
 * Created by jeetal on 5/9/17.
 */

public class PatientConnectActivity extends AppCompatActivity implements HelperResponse{

    private final static String TAG = "DoctorConnect";
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction() != null) {
                if (intent.getAction().equals(NOTIFY)) {

                    String topic = intent.getStringExtra(MQTTService.TOPIC_KEY);
                    topic = topic == null ? "" : topic;

                    if (intent.getBooleanExtra(MQTTService.DELIVERED, false)) {

                        Log.d(TAG, "Delivery Complete");
                        Log.d(TAG + " MSG_ID", intent.getStringExtra(MQTTService.MESSAGE_ID));

                    } else if (topic.equals(TOPIC[MESSAGE_TOPIC])) {
                        // User message
                        CommonMethods.Log(TAG, "User message");
                        MQTTMessage message = intent.getParcelableExtra(MQTTService.MESSAGE);
                        mPatientConnectChatFragment.notifyCount(message);
                    }
                }
            }
        }
    };

    @BindView(R.id.backButton)
    ImageView mBackButton;
    @BindView(R.id.tabsDoctorConnect)
    TabLayout mTabsPatientConnect;
    @BindView(R.id.doctorConnectViewpager)
    ViewPager mPatientConnectViewpager;
    String[] mFragmentTitleList = new String[3];
    @BindView(R.id.title)
    CustomTextView title;
    @BindView(R.id.searchView)
    EditTextWithDeleteButton mSearchView;
    @BindView(R.id.whiteUnderLine)
    TextView whiteUnderLine;

    @BindView(R.id.radioButton)
    SwitchButton radioButton;

    private LoginHelper loginHelper;

    public static final int PAID = 1;
    public static final int FREE = 0;

    private ViewPagerAdapter mAdapter;
    //-----
    private PatientConnectChatFragment mPatientConnectChatFragment;
    private PatientConnectFragment mPatientConnectFragment;
    private PatientSearchFragment mPatientSearchFragment;
    private ArrayList<PatientData> mReceivedConnectedPatientDataList;
    private String docId;
    //-----

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_connect);
        ButterKnife.bind(this);
        radioButton.setChecked(true);
        docId = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, PatientConnectActivity.this);
        title.setText("" + getString(R.string.patient_connect));
        mFragmentTitleList[0] = getString(R.string.chats);
        mFragmentTitleList[1] = getString(R.string.connect);
        mFragmentTitleList[2] = getString(R.string.search);
        setupViewPager();
        mTabsPatientConnect.setupWithViewPager(mPatientConnectViewpager);
        initialize();
    }

    private void initialize() {
        loginHelper = new LoginHelper(this, this);
        docId = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, PatientConnectActivity.this);

        mTabsPatientConnect.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabPosition = mTabsPatientConnect.getSelectedTabPosition();
                if (tabPosition == 2) {
                    mSearchView.setVisibility(View.VISIBLE);
                    whiteUnderLine.setVisibility(View.VISIBLE);
                    title.setVisibility(View.GONE);
                    radioButton.setVisibility(View.GONE);
                } else {
                    mSearchView.setVisibility(View.GONE);
                    title.setVisibility(View.VISIBLE);
                    radioButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabPosition = mTabsPatientConnect.getSelectedTabPosition();
                if (tabPosition == 2) {
                    whiteUnderLine.setVisibility(View.VISIBLE);

                    mSearchView.setVisibility(View.VISIBLE);
                    title.setVisibility(View.GONE);
                    radioButton.setVisibility(View.GONE);
                } else {
                    mSearchView.setVisibility(View.GONE);
                    title.setVisibility(View.VISIBLE);
                    radioButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int tabPosition = mTabsPatientConnect.getSelectedTabPosition();
                if (tabPosition == 2) {
                    whiteUnderLine.setVisibility(View.VISIBLE);
                    mSearchView.setVisibility(View.VISIBLE);
                    title.setVisibility(View.GONE);
                    radioButton.setVisibility(View.GONE);
                } else {
                    mSearchView.setVisibility(View.GONE);
                    title.setVisibility(View.VISIBLE);
                    radioButton.setVisibility(View.VISIBLE);
                }
            }
        });

        mSearchView.addTextChangedListener(editTextChanged());
        mSearchView.addClearTextButtonListener(new EditTextWithDeleteButton.OnClearButtonClickedInEditTextListener() {
            @Override
            public void onClearButtonClicked() {

            }
        });
    }


    private EditTextWithDeleteButton.TextChangedListener editTextChanged() {
        return new EditTextWithDeleteButton.TextChangedListener() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mPatientSearchFragment != null) {
                    mPatientSearchFragment.setOnClickOfSearchBar(mSearchView.getText().toString());
                }
            }
        };
    }

    private void setupViewPager() {
        //Doctor connect , chat and search fragment loaded here
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mPatientConnectChatFragment = PatientConnectChatFragment.newInstance();
        mPatientConnectFragment = PatientConnectFragment.newInstance();
        mPatientSearchFragment = PatientSearchFragment.newInstance();
        mAdapter.addFragment(mPatientConnectChatFragment, getString(R.string.chats));
        mAdapter.addFragment(mPatientConnectFragment, getString(R.string.connect));
        mAdapter.addFragment(mPatientSearchFragment, getString(R.string.search));
        mPatientConnectViewpager.setAdapter(mAdapter);
    }

    @OnClick(R.id.backButton)
    public void onViewClicked() {
        mSearchView.setText("");
        onBackPressed();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public interface OnClickOfSearchBar {
        void setOnClickOfSearchBar(String searchText);
    }

    public ArrayList<PatientData> getReceivedConnectedPatientDataList() {
        return mReceivedConnectedPatientDataList;
    }

    public void setReceivedConnectedPatientDataList(ArrayList<PatientData> mReceivedConnectedPatientDataList) {
        this.mReceivedConnectedPatientDataList = mReceivedConnectedPatientDataList;
    }

    // Recent

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(
                NOTIFY));

//        sendUserStatus(ONLINE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode) {
            PatientData patientData = data.getParcelableExtra(RescribeConstants.CHAT_USERS);
            mPatientConnectChatFragment.addItem(patientData);
        }
    }

    @OnClick({R.id.radioButton, R.id.searchView})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.radioButton:
                if (radioButton.isChecked()) {
                    ActiveRequest activeRequest = new ActiveRequest();
                    activeRequest.setId(Integer.parseInt(docId));
                    loginHelper.doActiveStatus(activeRequest);
                } else {
                    ActiveRequest activeRequest = new ActiveRequest();
                    activeRequest.setId(Integer.parseInt(docId));
                    loginHelper.doLogout(activeRequest);
                }
                break;
            case R.id.searchView:
                break;
        }
    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        if (mOldDataTag.equals(ACTIVE_STATUS))
            CommonMethods.Log(ACTIVE_STATUS, "active");
        else if (mOldDataTag.equals(RescribeConstants.LOGOUT)) {

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

    // change

    /*private void sendUserStatus(String userStatus) {
        // send user status via mqtt
        StatusInfo statusInfo = new StatusInfo();
        statusInfo.setPatId(-2);
        statusInfo.setDocId(Integer.parseInt(docId));
        statusInfo.setUserStatus(userStatus);
        String generatedId = CHAT + 0 + "_" + System.nanoTime();
        statusInfo.setMsgId(generatedId);

        Intent intentService = new Intent(PatientConnectActivity.this, MQTTService.class);
        intentService.putExtra(SEND_MESSAGE, true);
        intentService.putExtra(MESSAGE, false);
        intentService.putExtra(STATUS_INFO, statusInfo);
        startService(intentService);
    }*/
}