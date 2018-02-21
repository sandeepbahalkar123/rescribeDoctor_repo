package com.rescribe.doctor.ui.fragments.patient.patient_connect;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.patient_connect.PatientConnectAdapter;
import com.rescribe.doctor.helpers.patient_connect.PatientConnectHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.chat.MQTTMessage;
import com.rescribe.doctor.model.patient.patient_connect.ChatPatientConnectModel;
import com.rescribe.doctor.model.patient.patient_connect.PatientData;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by jeetal on 6/9/17.
 */

public class PatientConnectChatFragment extends Fragment implements HelperResponse {

    @BindView(R.id.listView)
    RecyclerView mRecyclerView;
    @BindView(R.id.emptyListView)
    RelativeLayout mEmptyListView;
    
    Unbinder unbinder;
    private ArrayList<PatientData> mReceivedPatientDataList = new ArrayList<>();
    private PatientConnectAdapter mPatientConnectAdapter;

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
        View mRootView = inflater.inflate(R.layout.global_connect_recycle_view_layout, container, false);
        unbinder = ButterKnife.bind(this, mRootView);

        initialize();
        return mRootView;
    }

    private void initialize() {
        PatientConnectHelper mPatientConnectHelper = new PatientConnectHelper(getActivity(), this);

        mPatientConnectAdapter = new PatientConnectAdapter(getActivity(), mReceivedPatientDataList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
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
            }
            mReceivedPatientDataList.addAll(patientListData.getPatientDataList());
            notifyDataChanged();
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
            if (!mReceivedPatientDataList.isEmpty()) {
                mPatientConnectAdapter.notifyDataSetChanged();
                isDataListViewVisible(true);
            } else {
                isDataListViewVisible(false);
            }
    }

    @Override
    public void onResume() {
        super.onResume();
        notifyDataChanged();
    }

    public void isDataListViewVisible(boolean flag) {
        if (mEmptyListView != null) {
            if (flag) {
                mEmptyListView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            } else {
                mEmptyListView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            }
        }
    }

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
                patientData.setPatientName(message.getName());
//                patientData.setImageUrl(message.getImageUrl());
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

        String time = CommonMethods.getCurrentTimeStamp(RescribeConstants.DATE_PATTERN.UTC_PATTERN);
        patientData.setLastChatTime(time);

        boolean isThere = false;
        for (int index = 0; index < mReceivedPatientDataList.size(); index++) {
            if (mReceivedPatientDataList.get(index).getId().equals(patientData.getId())) {
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
    }
}

