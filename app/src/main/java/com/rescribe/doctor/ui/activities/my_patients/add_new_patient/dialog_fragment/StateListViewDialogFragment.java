package com.rescribe.doctor.ui.activities.my_patients.add_new_patient.dialog_fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.database.Cursor;
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

import com.google.gson.Gson;
import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.add_new_patient.address_other_details.IdAndValueDataAdapter;
import com.rescribe.doctor.helpers.database.AppDBHelper;
import com.rescribe.doctor.helpers.myappointments.AddNewPatientHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.patient.add_new_patient.address_other_details.city_details.StateAndCityBaseModel;
import com.rescribe.doctor.model.patient.add_new_patient.address_other_details.city_details.StateDetailsModel;
import com.rescribe.doctor.model.patient.add_new_patient.address_other_details.states_details.StatesData;
import com.rescribe.doctor.model.patient.add_new_patient.address_other_details.states_details.StatesDetailsBaseModel;
import com.rescribe.doctor.services.job_creator_download_cities.CitySyncJob;
import com.rescribe.doctor.ui.activities.my_patients.add_new_patient.IdAndValueDataModel;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.ui.customesViews.EditTextWithDeleteButton;
import com.rescribe.doctor.util.NetworkUtil;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jeetal on 19/5/17.
 */

public class StateListViewDialogFragment extends DialogFragment implements IdAndValueDataAdapter.OnItemClicked, HelperResponse {
    private static CityAndAreaDialogFragment.OnItemClickedListener mItemClickedListener;
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

    @BindView(R.id.tapToAddNewFab)
    CustomTextView tapToAddNewFab;

    private ArrayList<IdAndValueDataModel> mList = new ArrayList<>();
    private int mStateID;
    private int mCityID;
    private IdAndValueDataAdapter mIdAndValueDataAdapter;
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

    public static StateListViewDialogFragment newInstance(Bundle bundle, CityAndAreaDialogFragment.OnItemClickedListener onItemClickedListener) {
        StateListViewDialogFragment myAppointmentsFragment = new StateListViewDialogFragment();
        myAppointmentsFragment.setArguments(bundle);
        mItemClickedListener = onItemClickedListener;
        return myAppointmentsFragment;
    }

    private void initialize() {

        mContext = this.getDialog().getContext();

        mAddNewPatientHelper = new AddNewPatientHelper(mContext, this);

        mSearchEditText.addTextChangedListener(new EditTextWithDeleteButton.TextChangedListener() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mIdAndValueDataAdapter != null) {
                    mIdAndValueDataAdapter.getFilter().filter(s);
                }
            }
        });

        fetchData();
    }

    private void fetchData() {

        boolean internetAvailableCheck = NetworkUtil.isInternetAvailable(getActivity());

        if (internetAvailableCheck) {
            mAddNewPatientHelper.searchState("");
        } else {
            AppDBHelper dbHelper = AppDBHelper.getInstance(getActivity());

            Gson gson = new Gson();
            if (dbHelper.dataTableNumberOfRows(CitySyncJob.TAG) > 0) {
                Cursor cursor = dbHelper.getData(CitySyncJob.TAG);
                cursor.moveToFirst();
                StateAndCityBaseModel stateAndCityBaseModel = gson.fromJson(cursor.getString(cursor.getColumnIndex(AppDBHelper.COLUMN_DATA)), StateAndCityBaseModel.class);

                if (mHeader.equalsIgnoreCase(getString(R.string.state))) {
                    ArrayList<StateDetailsModel> stateDetailsMainList = stateAndCityBaseModel.getCityDetailsDataModel().getStateDetailsMainList();

                    for (StateDetailsModel obj :
                            stateDetailsMainList) {
                        IdAndValueDataModel temp = new IdAndValueDataModel();
                        temp.setId(obj.getStateId());
                        temp.setIdValue(obj.getStateName());
                        mList.add(temp);
                    }
                    setAdapter(mList);
                }

            }
        }
    }

    private void setAdapter(ArrayList<IdAndValueDataModel> list) {

        if (list.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyListView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyListView.setVisibility(View.GONE);
            mIdAndValueDataAdapter = new IdAndValueDataAdapter(getActivity(), list, this);
            LinearLayoutManager linearlayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(linearlayoutManager);
            mRecyclerView.setAdapter(mIdAndValueDataAdapter);
        }
    }

    @Override
    public void onValueClicked(int id, String value) {
        mItemClickedListener.onItemClicked(id, value);
        this.dismiss();
    }


    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {

        StatesDetailsBaseModel data = (StatesDetailsBaseModel) customResponse;

        StatesDetailsBaseModel.StatesDetailsDataModel statesDetailsDataModel = data.getStatesDetailsDataModel();
        if (statesDetailsDataModel != null) {
            ArrayList<StatesData> statesDataList = statesDetailsDataModel.getStatesDataList();
            for (StatesData obj :
                    statesDataList) {
                IdAndValueDataModel temp = new IdAndValueDataModel();
                temp.setId(obj.getStateID());
                temp.setIdValue(obj.getStateName());
                mList.add(temp);
            }
            setAdapter(mList);
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

}
