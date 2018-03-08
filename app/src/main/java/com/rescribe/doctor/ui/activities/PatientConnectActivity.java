package com.rescribe.doctor.ui.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.rescribe.doctor.R;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.chat.MQTTMessage;
import com.rescribe.doctor.model.patient.patient_connect.PatientData;
import com.rescribe.doctor.services.MQTTService;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.ui.customesViews.EditTextWithDeleteButton;
import com.rescribe.doctor.ui.fragments.patient.patient_connect.PatientConnectChatFragment;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;

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

public class PatientConnectActivity extends AppCompatActivity implements HelperResponse, SearchView.OnQueryTextListener {

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
    @BindView(R.id.title)
    CustomTextView title;
    @BindView(R.id.searchView)
    EditTextWithDeleteButton mSearchView;
    @BindView(R.id.whiteUnderLine)
    TextView whiteUnderLine;
    @BindView(R.id.toolbar)
    android.support.v7.widget.Toolbar toolbar;

    @BindView(R.id.container)
    FrameLayout container;

    public static final int PAID = 1;
    public static final int FREE = 0;
    //-----
    private PatientConnectChatFragment mPatientConnectChatFragment;
    private ArrayList<PatientData> mReceivedConnectedPatientDataList;
    //-----

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_connect);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title.setText("" + getString(R.string.patient_connect));
        initialize();
    }


    private void initialize() {

        mPatientConnectChatFragment = PatientConnectChatFragment.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.container, mPatientConnectChatFragment);
        ft.commit();
    }


    @OnClick(R.id.backButton)
    public void onViewClicked() {
        mSearchView.setText("");
        onBackPressed();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mPatientConnectChatFragment.setOnClickOfSearchBar(newText);
        return true;
    }

    public ArrayList<PatientData> getReceivedConnectedPatientDataList() {
        return mReceivedConnectedPatientDataList;
    }

    public void setReceivedConnectedPatientDataList(ArrayList<PatientData> mReceivedConnectedPatientDataList) {
        this.mReceivedConnectedPatientDataList = mReceivedConnectedPatientDataList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        return id == R.id.action_search || super.onOptionsItemSelected(item);

    }

    // Recent

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(
                NOTIFY));
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
                break;
            case R.id.searchView:
                break;
        }
    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        if (mOldDataTag.equals(ACTIVE_STATUS))
            CommonMethods.Log(ACTIVE_STATUS, "active");
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
}