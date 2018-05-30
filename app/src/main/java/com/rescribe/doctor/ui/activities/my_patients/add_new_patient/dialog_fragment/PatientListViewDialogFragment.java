package com.rescribe.doctor.ui.activities.my_patients.add_new_patient.dialog_fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;

import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.add_new_patient.address_other_details.DoctorListForReference;
import com.rescribe.doctor.adapters.add_new_patient.address_other_details.PatientListForReference;
import com.rescribe.doctor.adapters.my_patients.MyPatientsAdapter;
import com.rescribe.doctor.helpers.database.AppDBHelper;
import com.rescribe.doctor.helpers.myappointments.AddNewPatientHelper;
import com.rescribe.doctor.helpers.myappointments.AppointmentHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.patient.add_new_patient.address_other_details.reference_details.DoctorData;
import com.rescribe.doctor.model.patient.add_new_patient.address_other_details.reference_details.DoctorListBaseModel;
import com.rescribe.doctor.model.patient.doctor_patients.MyPatientBaseModel;
import com.rescribe.doctor.model.patient.doctor_patients.PatientList;
import com.rescribe.doctor.model.request_patients.RequestSearchPatients;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.ui.activities.my_patients.MyPatientsActivity;
import com.rescribe.doctor.ui.customesViews.EditTextWithDeleteButton;
import com.rescribe.doctor.ui.customesViews.drag_drop_recyclerview_helper.EndlessRecyclerViewScrollListener;
import com.rescribe.doctor.util.NetworkUtil;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.rescribe.doctor.util.RescribeConstants.SUCCESS;

/**
 * Created by jeetal on 19/5/17.
 */

public class PatientListViewDialogFragment extends DialogFragment implements PatientListForReference.OnItemClicked, HelperResponse {
    private static OnItemClickedListener mItemClickedListener;
    private final String TAG = this.getClass().getName();
    @BindView(R.id.searchEditText)
    EditTextWithDeleteButton mSearchEditText;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.emptyListView)
    RelativeLayout mEmptyListView;
    private ActionBar mActionBar;
    Context mContext;
    private String mHeader;

    //--------
    private String searchText = "";
    private boolean isFiltered = false;
    //--------

    private AppointmentHelper mAppointmentHelper;
    private RequestSearchPatients mRequestSearchPatients = new RequestSearchPatients();
    private PatientListForReference mMyPatientsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.common_recycler_view_with_searchtextview, container);

        ButterKnife.bind(this, rootView);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mHeader = arguments.getString(RescribeConstants.TITLE);
        }

        this.getDialog().setTitle(mHeader);
        initialize();

        return rootView;
    }

    public static PatientListViewDialogFragment newInstance(Bundle bundle, OnItemClickedListener onItemClickedListener) {
        PatientListViewDialogFragment myAppointmentsFragment = new PatientListViewDialogFragment();
        myAppointmentsFragment.setArguments(bundle);
        mItemClickedListener = onItemClickedListener;
        return myAppointmentsFragment;
    }

    private void initialize() {

        mContext = this.getDialog().getContext();

        ArrayList<PatientList> patientLists = new ArrayList<>();
        LinearLayoutManager linearlayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearlayoutManager);

        mMyPatientsAdapter = new PatientListForReference(getActivity(), patientLists, this);
        mRecyclerView.setAdapter(mMyPatientsAdapter);

        nextPage(0, NetworkUtil.getConnectivityStatusBoolean(getActivity()));

        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearlayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                nextPage(page, NetworkUtil.getConnectivityStatusBoolean(getActivity()));
            }
        });

        mSearchEditText.addTextChangedListener(new EditTextWithDeleteButton.TextChangedListener() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchText = s.toString();

                if (NetworkUtil.getConnectivityStatusBoolean(getActivity())) {
                    if (searchText.length() >= 3) {
                        searchPatients(true);
                        isFiltered = true;
                    } else if (isFiltered) {
                        isFiltered = false;
                        searchText = "";
                        searchPatients(true);
                    }

                    if (s.toString().length() < 3)
                        mMyPatientsAdapter.getFilter().filter(s.toString());
                } else
                    searchPatients(false);
            }
        });

    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {

        if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_GET_SEARCH_RESULT_MY_PATIENT)) {
            MyPatientBaseModel myAppointmentsBaseModel = (MyPatientBaseModel) customResponse;

            if (myAppointmentsBaseModel.getCommon().getStatusCode().equals(SUCCESS)) {
                ArrayList<PatientList> mLoadedPatientList = myAppointmentsBaseModel.getPatientDataModel().getPatientList();
                mMyPatientsAdapter.addAll(mLoadedPatientList, searchText);

                if (!mMyPatientsAdapter.getGroupList().isEmpty()) {
                    mEmptyListView.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    mEmptyListView.setVisibility(View.VISIBLE);
                }

            }
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

    @Override
    public void onValueClicked(int id, PatientList data) {
        mItemClickedListener.onItemClicked(id, data);
        this.dismiss();
    }

    public interface OnItemClickedListener {
        public void onItemClicked(int id, PatientList data);
    }


    public void nextPage(int pageNo, boolean isInternetAvailable) {
        boolean isAllPatientDownloaded = RescribePreferencesManager.getBoolean(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.PATIENT_DOWNLOAD, getActivity());

        if (isInternetAvailable && !isAllPatientDownloaded) {
            mAppointmentHelper = new AppointmentHelper(mContext, this);
            mRequestSearchPatients.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, getActivity())));
            mRequestSearchPatients.setSearchText(searchText);
            mRequestSearchPatients.setPageNo(pageNo);
            mAppointmentHelper.doGetSearchResult(mRequestSearchPatients, mSearchEditText.getText().toString().isEmpty());
        } else {
            ArrayList<PatientList> offlineAddedPatients = AppDBHelper.getInstance(getActivity()).getOfflineAddedPatients(mRequestSearchPatients, pageNo, mSearchEditText.getText().toString());
            if (!offlineAddedPatients.isEmpty()) {
                mRecyclerView.setVisibility(View.VISIBLE);
                mEmptyListView.setVisibility(View.GONE);
                mMyPatientsAdapter.addAll(offlineAddedPatients, mSearchEditText.getText().toString());
                mMyPatientsAdapter.notifyDataSetChanged();
            } else {
                if (pageNo == 0) {
                    mRecyclerView.setVisibility(View.GONE);
                    mEmptyListView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void searchPatients(boolean isInternetAvailable) {
        mMyPatientsAdapter.clear();
        if (isInternetAvailable) {
            mRequestSearchPatients.setPageNo(0);
            mAppointmentHelper = new AppointmentHelper(mContext, this);
            mRequestSearchPatients.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, getActivity())));
            mRequestSearchPatients.setSearchText(searchText);
            mAppointmentHelper.doGetSearchResult(mRequestSearchPatients, mSearchEditText.getText().toString().isEmpty());
        } else {
            ArrayList<PatientList> offlineAddedPatients = AppDBHelper.getInstance(getActivity()).getOfflineAddedPatients(mRequestSearchPatients, 0, mSearchEditText.getText().toString());
            if (!offlineAddedPatients.isEmpty()) {
                mRecyclerView.setVisibility(View.VISIBLE);
                mEmptyListView.setVisibility(View.GONE);
                mMyPatientsAdapter.addAll(offlineAddedPatients, mSearchEditText.getText().toString());
                mMyPatientsAdapter.notifyDataSetChanged();
            } else {
                mRecyclerView.setVisibility(View.GONE);
                mEmptyListView.setVisibility(View.VISIBLE);
            }
        }
    }
}
