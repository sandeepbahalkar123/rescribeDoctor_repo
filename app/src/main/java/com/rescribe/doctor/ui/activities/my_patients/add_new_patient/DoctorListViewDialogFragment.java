package com.rescribe.doctor.ui.activities.my_patients.add_new_patient;

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
import com.rescribe.doctor.adapters.add_new_patient.address_other_details.IdAndValueDataAdapter;
import com.rescribe.doctor.helpers.myappointments.AddNewPatientHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.patient.add_new_patient.address_other_details.reference_details.DoctorData;
import com.rescribe.doctor.model.patient.add_new_patient.address_other_details.reference_details.DoctorListBaseModel;
import com.rescribe.doctor.ui.customesViews.EditTextWithDeleteButton;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jeetal on 19/5/17.
 */

public class DoctorListViewDialogFragment extends DialogFragment implements DoctorListForReference.OnItemClicked, HelperResponse {
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

    private DoctorListForReference doctorListForReferenceAdapter;
    private AddNewPatientHelper mAddNewPatientHelper;

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

    public static DoctorListViewDialogFragment newInstance(Bundle bundle, OnItemClickedListener onItemClickedListener) {
        DoctorListViewDialogFragment myAppointmentsFragment = new DoctorListViewDialogFragment();
        myAppointmentsFragment.setArguments(bundle);
        mItemClickedListener = onItemClickedListener;
        return myAppointmentsFragment;
    }

    private void initialize() {

        mSearchEditText.addTextChangedListener(new EditTextWithDeleteButton.TextChangedListener() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (doctorListForReferenceAdapter != null) {
                    doctorListForReferenceAdapter.getFilter().filter(s);
                }
            }
        });

        mAddNewPatientHelper = new AddNewPatientHelper(getActivity(), this);
        mAddNewPatientHelper.searchDocName("");
    }

    private void setAdapter(ArrayList<DoctorData> list) {

        if (list.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyListView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyListView.setVisibility(View.GONE);
            doctorListForReferenceAdapter = new DoctorListForReference(getActivity(), list, this);
            LinearLayoutManager linearlayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(linearlayoutManager);
            mRecyclerView.setAdapter(doctorListForReferenceAdapter);
        }
    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {

        switch (mOldDataTag) {
            case RescribeConstants.TASK_GET_DOC_LIST_FOR_REFERENCE_TO_ADD_PATIENT: {
                DoctorListBaseModel baseModel = (DoctorListBaseModel) customResponse;
                ArrayList<DoctorData> doctorDataList = baseModel.getDoctorListDataModel().getDoctorDataList();
                if (doctorDataList.isEmpty()) {

                } else {
                    setAdapter(doctorDataList);
                }

            }
            break;
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
    public void onValueClicked(int id, DoctorData data) {
        mItemClickedListener.onItemClicked(id, data);
        this.dismiss();
    }

    public interface OnItemClickedListener {
        public void onItemClicked(int id, DoctorData data);
    }
}
