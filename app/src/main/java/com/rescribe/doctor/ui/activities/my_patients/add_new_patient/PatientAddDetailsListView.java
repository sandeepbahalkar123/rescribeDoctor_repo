package com.rescribe.doctor.ui.activities.my_patients.add_new_patient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.add_new_patient.address_other_details.IdAndValueDataAdapter;
import com.rescribe.doctor.helpers.database.AppDBHelper;
import com.rescribe.doctor.helpers.myappointments.AppointmentHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.patient.add_new_patient.address_other_details.area_details.AreaData;
import com.rescribe.doctor.model.patient.add_new_patient.address_other_details.area_details.AreaDetailsBaseModel;
import com.rescribe.doctor.model.patient.add_new_patient.address_other_details.city_details.CityData;
import com.rescribe.doctor.model.patient.add_new_patient.address_other_details.city_details.StateAndCityBaseModel;
import com.rescribe.doctor.model.patient.add_new_patient.address_other_details.city_details.StateDetailsModel;
import com.rescribe.doctor.services.job_creator_download_cities.CitySyncJob;
import com.rescribe.doctor.ui.customesViews.EditTextWithDeleteButton;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jeetal on 19/5/17.
 */

public class PatientAddDetailsListView extends AppCompatActivity implements IdAndValueDataAdapter.OnItemClicked, HelperResponse {
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

    private ArrayList<IdAndValueDataModel> list = new ArrayList<>();
    private int mStateID;
    private int mCityID;
    private IdAndValueDataAdapter mIdAndValueDataAdapter;
    private AppointmentHelper mAppointmentHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_recycler_view_with_searchtextview);
        ButterKnife.bind(this);
        mContext = PatientAddDetailsListView.this;
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mHeader = getIntent().getStringExtra(RescribeConstants.TITLE);
        mStateID = getIntent().getIntExtra(RescribeConstants.STATE_ID, -1);
        mCityID = getIntent().getIntExtra(RescribeConstants.CITY_ID, -1);
        mActionBar.setTitle(mHeader);

        initialize();
    }

    private void initialize() {

        mAppointmentHelper = new AppointmentHelper(this, this);


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
        AppDBHelper dbHelper = AppDBHelper.getInstance(this);
        if (mHeader.equalsIgnoreCase(getString(R.string.state)) || mHeader.equalsIgnoreCase(getString(R.string.city))) {
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
                        list.add(temp);
                    }
                    setAdapter(list);
                } else if (mHeader.equalsIgnoreCase(getString(R.string.city))) {
                    ArrayList<StateDetailsModel> stateDetailsMainList = stateAndCityBaseModel.getCityDetailsDataModel().getStateDetailsMainList();

                    for (StateDetailsModel obj :
                            stateDetailsMainList) {
                        if (mStateID == obj.getStateId()) {
                            ArrayList<CityData> cityDataList = obj.getCityDataList();
                            for (CityData cityObj :
                                    cityDataList) {
                                IdAndValueDataModel temp = new IdAndValueDataModel();
                                temp.setId(cityObj.getCityId());
                                temp.setIdValue(cityObj.getCityName());
                                list.add(temp);
                            }
                            break;
                        }
                    }
                    setAdapter(list);
                }

            }
        } else if (mHeader.equalsIgnoreCase(getString(R.string.area))) {
            mAppointmentHelper.getAreaOfSelectedCity(mCityID);
        }
    }

    private void setAdapter(ArrayList<IdAndValueDataModel> list) {

        if (list.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyListView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyListView.setVisibility(View.GONE);
            mIdAndValueDataAdapter = new IdAndValueDataAdapter(this, list, this);
            LinearLayoutManager linearlayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(linearlayoutManager);
            mRecyclerView.setAdapter(mIdAndValueDataAdapter);
        }
    }

    @Override
    public void onValueClicked(int id, String value) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(RescribeConstants.ID, id);
        resultIntent.putExtra(RescribeConstants.DATA, value);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {

        switch (mOldDataTag) {
            case RescribeConstants.TASK_GET_AREA_TO_ADD_NEW_PATIENT:
                AreaDetailsBaseModel areaModel = (AreaDetailsBaseModel) customResponse;
                AreaDetailsBaseModel.AreaDetailsDataModel areaDetailsDataModel = areaModel.getAreaDetailsDataModel();

                if (!areaDetailsDataModel.getAreaDataList().isEmpty()) {
                    ArrayList<AreaData> areaDataList = areaDetailsDataModel.getAreaDataList();
                    for (AreaData cityObj :
                            areaDataList) {
                        IdAndValueDataModel temp = new IdAndValueDataModel();
                        temp.setId(cityObj.getAreaId());
                        temp.setIdValue(cityObj.getAreaName());
                        list.add(temp);
                    }
                    setAdapter(list);
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
}
