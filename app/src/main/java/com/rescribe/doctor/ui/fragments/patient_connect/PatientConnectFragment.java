package com.rescribe.doctor.ui.fragments.patient_connect;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.patient_connect.PatientConnectAdapter;
import com.rescribe.doctor.helpers.patient_connect.PatientConnectHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.patient_connect.PatientConnectBaseModel;
import com.rescribe.doctor.model.patient_connect.PatientData;
import com.rescribe.doctor.ui.activities.PatientConnectActivity;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by jeetal on 5/9/17.
 */
public class PatientConnectFragment extends Fragment implements HelperResponse {
    private static final String DATA = "DATA";
    @BindView(R.id.listView)
    RecyclerView mRecyclerView;
    @BindView(R.id.emptyListView)
    RelativeLayout mEmptyListView;
    Unbinder unbinder;
    private View mRootView;
    private PatientConnectAdapter mPatientConnectAdapter;
    private PatientConnectHelper mPatientConnectHelper;
    private ArrayList<PatientData> mReceivedPatientDataList;

    public PatientConnectFragment() {
    }

    public static PatientConnectFragment newInstance() {
        PatientConnectFragment fragment = new PatientConnectFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.global_connect_recycle_view_layout, container, false);
        unbinder = ButterKnife.bind(this, mRootView);
        initialize();
        return mRootView;
    }

    private void initialize() {
        mPatientConnectHelper = new PatientConnectHelper(getActivity(), this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void onResume() {
        super.onResume();
        //Api call to get doctorChatList
        if (mReceivedPatientDataList == null) {
            mPatientConnectHelper.doGetChatPatientList();
        } else {
            setAdapter();
        }
    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_GET_PATIENT_LIST)) {
            PatientConnectBaseModel patientConnectBaseModel = (PatientConnectBaseModel) customResponse;
            PatientConnectBaseModel.PatientListData patientListData = patientConnectBaseModel.getPatientListData();
            mReceivedPatientDataList = patientListData.getPatientDataList();
            PatientConnectActivity activity = (PatientConnectActivity) getActivity();
            activity.setReceivedConnectedPatientDataList(mReceivedPatientDataList);
            setAdapter();
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

    public void setAdapter() {
        if (mReceivedPatientDataList != null) {
            if (mReceivedPatientDataList.size() > 0) {
                isDataListViewVisible(true);
                mPatientConnectAdapter = new PatientConnectAdapter(getActivity(), mReceivedPatientDataList,this);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                        DividerItemDecoration.VERTICAL);
                mRecyclerView.addItemDecoration(dividerItemDecoration);
                mRecyclerView.setAdapter(mPatientConnectAdapter);
            } else {
                isDataListViewVisible(false);
            }
        } else {
            isDataListViewVisible(false);
        }
    }

    public void isDataListViewVisible(boolean flag) {
        if (flag) {
            mEmptyListView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mEmptyListView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }

}

