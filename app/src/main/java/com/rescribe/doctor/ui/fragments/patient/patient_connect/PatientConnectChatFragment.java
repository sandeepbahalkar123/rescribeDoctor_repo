package com.rescribe.doctor.ui.fragments.patient.patient_connect;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.patient_connect.PatientConnectAdapter;
import com.rescribe.doctor.adapters.patient_connect.SearchedMessagesAdapter;
import com.rescribe.doctor.helpers.database.AppDBHelper;
import com.rescribe.doctor.helpers.patient_connect.PatientConnectHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.chat.MQTTMessage;
import com.rescribe.doctor.model.patient.patient_connect.ChatPatientConnectModel;
import com.rescribe.doctor.model.patient.patient_connect.PatientData;
import com.rescribe.doctor.ui.activities.my_patients.ShowMyPatientsListActivity;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.rescribe.doctor.ui.activities.PatientConnectActivity.PATIENT_CONNECT_REQUEST;

/**
 * Created by jeetal on 6/9/17.
 */

public class PatientConnectChatFragment extends Fragment implements HelperResponse, PatientConnectAdapter.FilterListener {

    @BindView(R.id.patientRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.searchRecyclerView)
    RecyclerView searchRecyclerView;
    @BindView(R.id.emptyListView)
    RelativeLayout mEmptyListView;
    Unbinder unbinder;
    @BindView(R.id.leftFab)
    FloatingActionButton leftFab;
    private ArrayList<PatientData> mReceivedPatientDataList = new ArrayList<>();
    private PatientConnectAdapter mPatientConnectAdapter;
    private SearchedMessagesAdapter searchedMessagesAdapter;
    private ArrayList<MQTTMessage> mqttMessages = new ArrayList<>();

    public static PatientConnectChatFragment newInstance() {
        PatientConnectChatFragment fragment = new PatientConnectChatFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public PatientConnectChatFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.patient_connect_fragment, container, false);
        unbinder = ButterKnife.bind(this, mRootView);

        initialize();
        return mRootView;
    }

    private void initialize() {
        PatientConnectHelper mPatientConnectHelper = new PatientConnectHelper(getActivity(), this);

        mRecyclerView.setNestedScrollingEnabled(false);
        searchRecyclerView.setNestedScrollingEnabled(false);

        mPatientConnectAdapter = new PatientConnectAdapter(getActivity(), mReceivedPatientDataList, this);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mRecyclerView.setAdapter(mPatientConnectAdapter);

        if (mReceivedPatientDataList.isEmpty()) {
            mPatientConnectHelper.doGetChatPatientList();
        } else {
            notifyDataChanged();
        }

        searchedMessagesAdapter = new SearchedMessagesAdapter(getContext(), mqttMessages);
        searchRecyclerView.setItemAnimator(new DefaultItemAnimator());
        searchRecyclerView.addItemDecoration(dividerItemDecoration);
        LinearLayoutManager mLayoutM = new LinearLayoutManager(getActivity());
        searchRecyclerView.setLayoutManager(mLayoutM);
        searchRecyclerView.setAdapter(searchedMessagesAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        if (mOldDataTag.equalsIgnoreCase(RescribeConstants.GET_PATIENT_CHAT_LIST)) {
            ChatPatientConnectModel patientConnectBaseModel = (ChatPatientConnectModel) customResponse;
            ChatPatientConnectModel.PatientListData patientListData = patientConnectBaseModel.getPatientListData();
            if (patientListData.getPatientDataList().isEmpty()) {
                mEmptyListView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            } else {

                AppDBHelper appDBHelper = new AppDBHelper(getContext());

                for (PatientData patientData : patientListData.getPatientDataList()) {
                    MQTTMessage mqttMessage = appDBHelper.getLastChatMessagesByPatientId(patientData.getId());
                    if (mqttMessage != null)
                        patientData.setLastChatTime(mqttMessage.getMsgTime());
                    mReceivedPatientDataList.add(patientData);
                }
                notifyDataChanged();
            }
        }
    }

    @Override
    public void onParseError(String mOldDataTag, String errorMessage) {
        mEmptyListView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onServerError(String mOldDataTag, String serverErrorMessage) {
        mEmptyListView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onNoConnectionError(String mOldDataTag, String serverErrorMessage) {
        mEmptyListView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    public void notifyDataChanged() {
        if (mPatientConnectAdapter != null)
            if (!mReceivedPatientDataList.isEmpty())
                mPatientConnectAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        notifyDataChanged();
    }

    /*public void isDataListViewVisible(boolean flag) {
        if (mEmptyListView != null) {
            if (flag) {
                mEmptyListView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            } else {
                mEmptyListView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            }
        }
    }*/

    public void notifyCount(MQTTMessage message) {
        boolean isThere = false;
        if (mReceivedPatientDataList != null && mPatientConnectAdapter != null) {
            for (int index = 0; index < mReceivedPatientDataList.size(); index++) {
                if (mReceivedPatientDataList.get(index).getId() == message.getPatId()) {
                    mPatientConnectAdapter.notifyItemChanged(index);
                    isThere = true;
                    break;
                }
            }

            if (!isThere) {
                PatientData patientData = new PatientData();
                patientData.setId(message.getPatId()); // Change
                patientData.setPatientName(message.getSenderName());
//                patientData.setSenderImgUrl(message.getSenderImgUrl());
                patientData.setUnreadMessages(1);
                patientData.setOnlineStatus(RescribeConstants.USER_STATUS.ONLINE);
                mReceivedPatientDataList.add(0, patientData);
                notifyDataChanged();
            }

            if (!mReceivedPatientDataList.isEmpty()) {
                mEmptyListView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }

        }
    }

    public void addItem(PatientData patientData) {

        boolean isThere = false;
        for (int index = 0; index < mReceivedPatientDataList.size(); index++) {
            if (mReceivedPatientDataList.get(index).getId().equals(patientData.getId())) {
                PatientData patientData1 = mReceivedPatientDataList.remove(index);
                patientData1.setLastChatTime(patientData.getLastChatTime());
                mReceivedPatientDataList.add(0, patientData1);
                isThere = true;
                break;
            }
        }

        if (!isThere)
            mReceivedPatientDataList.add(0, patientData);

        mPatientConnectAdapter.notifyDataSetChanged();
    }

    public void setOnClickOfSearchBar(String searchText) {

        if (mPatientConnectAdapter != null)
            mPatientConnectAdapter.getFilter().filter(searchText);

        if (searchText.isEmpty()) {
            searchRecyclerView.setVisibility(View.GONE);
            if (mReceivedPatientDataList.isEmpty()) {
                mEmptyListView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            } else {
                mRecyclerView.setVisibility(View.VISIBLE);
                mEmptyListView.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public void result(String searchText, ArrayList<PatientData> dataList) {
        searchRecyclerView.setVisibility(View.VISIBLE);

        mqttMessages.clear();

        AppDBHelper appDBHelper = new AppDBHelper(getContext());
        Cursor cursor = appDBHelper.searchChatMessagesByChars(searchText);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    MQTTMessage mqttMessage = new MQTTMessage();

                    mqttMessage.setMsgId(cursor.getString(cursor.getColumnIndex(AppDBHelper.CHAT_MESSAGES.MSG_ID)));
                    mqttMessage.setMsg(cursor.getString(cursor.getColumnIndex(AppDBHelper.CHAT_MESSAGES.MSG)));
                    mqttMessage.setMsgTime(cursor.getString(cursor.getColumnIndex(AppDBHelper.CHAT_MESSAGES.MSG_TIME)));
                    mqttMessage.setSender(cursor.getString(cursor.getColumnIndex(AppDBHelper.CHAT_MESSAGES.SENDER)));
                    mqttMessage.setPatId(cursor.getInt(cursor.getColumnIndex(AppDBHelper.CHAT_MESSAGES.USER2ID)));
                    mqttMessage.setDocId(cursor.getInt(cursor.getColumnIndex(AppDBHelper.CHAT_MESSAGES.USER1ID)));
                    mqttMessage.setSenderName(cursor.getString(cursor.getColumnIndex(AppDBHelper.CHAT_MESSAGES.SENDER_NAME)));
                    mqttMessage.setSpecialization(cursor.getString(cursor.getColumnIndex(AppDBHelper.CHAT_MESSAGES.SPECIALITY)));
                    mqttMessage.setMsgStatus(cursor.getString(cursor.getColumnIndex(AppDBHelper.CHAT_MESSAGES.MSG_STATUS)));
                    mqttMessage.setSenderImgUrl(cursor.getString(cursor.getColumnIndex(AppDBHelper.CHAT_MESSAGES.SENDER_IMG_URL)));
                    mqttMessage.setFileUrl(cursor.getString(cursor.getColumnIndex(AppDBHelper.CHAT_MESSAGES.FILE_URL)));
                    mqttMessage.setFileType(cursor.getString(cursor.getColumnIndex(AppDBHelper.CHAT_MESSAGES.FILE_TYPE)));

                    mqttMessage.setSalutation(cursor.getInt(cursor.getColumnIndex(AppDBHelper.CHAT_MESSAGES.SALUTATION)));
                    mqttMessage.setReceiverName(cursor.getString(cursor.getColumnIndex(AppDBHelper.CHAT_MESSAGES.RECEIVER_NAME)));
                    mqttMessage.setReceiverImgUrl(cursor.getString(cursor.getColumnIndex(AppDBHelper.CHAT_MESSAGES.RECEIVER_IMG_URL)));

                    mqttMessage.setUploadStatus(cursor.getInt(cursor.getColumnIndex(AppDBHelper.CHAT_MESSAGES.UPLOAD_STATUS)));
                    mqttMessage.setDownloadStatus(cursor.getInt(cursor.getColumnIndex(AppDBHelper.CHAT_MESSAGES.DOWNLOAD_STATUS)));
                    mqttMessage.setReadStatus(cursor.getInt(cursor.getColumnIndex(AppDBHelper.CHAT_MESSAGES.READ_STATUS)));

                    mqttMessages.add(mqttMessage);
                    cursor.moveToNext();
                }

                cursor.close();
            }

            appDBHelper.close();
        }

        if (!mqttMessages.isEmpty()) {
            mEmptyListView.setVisibility(View.GONE);
            searchedMessagesAdapter.setSearch(searchText);
        } else {
            if (dataList.isEmpty())
                mEmptyListView.setVisibility(View.VISIBLE);
            else mEmptyListView.setVisibility(View.GONE);

            searchRecyclerView.setVisibility(View.GONE);
        }

        searchedMessagesAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.searchRecyclerView, R.id.leftFab})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.searchRecyclerView:
                break;
            case R.id.leftFab:
                Intent intent = new Intent(getActivity(), ShowMyPatientsListActivity.class);
                getActivity().startActivityForResult(intent, PATIENT_CONNECT_REQUEST);
                break;
        }
    }
}

