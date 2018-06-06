package com.rescribe.doctor.ui.activities.my_patients.add_new_patient.dialog_fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.add_new_patient.address_other_details.IdAndValueDataAdapter;
import com.rescribe.doctor.helpers.database.AppDBHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.patient.add_new_patient.address_other_details.city_details.CityData;
import com.rescribe.doctor.model.patient.add_new_patient.address_other_details.city_details.StateAndCityBaseModel;
import com.rescribe.doctor.model.patient.add_new_patient.address_other_details.city_details.StateDetailsModel;
import com.rescribe.doctor.services.job_creator_download_cities.CitySyncJob;
import com.rescribe.doctor.ui.activities.my_patients.add_new_patient.IdAndValueDataModel;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.ui.customesViews.EditTextWithDeleteButton;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jeetal on 19/5/17.
 */

public class CityAndAreaDialogFragment extends DialogFragment implements IdAndValueDataAdapter.OnItemClicked, HelperResponse {
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

    @BindView(R.id.tapToAddNewFab)
    CustomTextView tapToAddNewFab;

    private ArrayList<IdAndValueDataModel> mList = new ArrayList<>();
    private int mStateID;
    private int mCityID;
    private IdAndValueDataAdapter mIdAndValueDataAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.common_recycler_view_with_searchtextview, container);

        ButterKnife.bind(this, rootView);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mHeader = arguments.getString(RescribeConstants.TITLE);
            mStateID = arguments.getInt(RescribeConstants.STATE_ID, -1);
            mCityID = arguments.getInt(RescribeConstants.CITY_ID, -1);
            ArrayList<IdAndValueDataModel> parcelableArrayList = arguments.getParcelableArrayList(RescribeConstants.AREA_LIST);
            if (parcelableArrayList != null)
                mList.addAll(parcelableArrayList);
        }

        this.getDialog().setTitle(mHeader);
        initialize();

        return rootView;
    }

    public static CityAndAreaDialogFragment newInstance(Bundle bundle, OnItemClickedListener onItemClickedListener) {
        CityAndAreaDialogFragment myAppointmentsFragment = new CityAndAreaDialogFragment();
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
                if (mIdAndValueDataAdapter != null) {
                    mIdAndValueDataAdapter.getFilter().filter(s);
                }
            }
        });

        fetchData();
    }

    private void fetchData() {
        AppDBHelper dbHelper = AppDBHelper.getInstance(getActivity());
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
                        mList.add(temp);
                    }
                    setAdapter(mList);
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
                                mList.add(temp);
                            }
                            break;
                        }
                    }
                    setAdapter(mList);
                }

            }
        } else if (mHeader.equalsIgnoreCase(getString(R.string.area))) {
            tapToAddNewFab.setVisibility(View.VISIBLE);
            tapToAddNewFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogToAddNewArea();
                }
            });
            setAdapter(mList);
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

    public interface OnItemClickedListener {
        public void onItemClicked(int id, String value);
    }

    private void showDialogToAddNewArea() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(), R.style.addNewPatientDialogCustomTheme);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_new_reference_details, null);
        dialogBuilder.setView(dialogView);

        //------------
        final EditText referByField = (EditText) dialogView.findViewById(R.id.referredBy);

        TextInputLayout referredByTextInputLayout = (TextInputLayout) dialogView.findViewById(R.id.referredByTextInputLayout);
        referredByTextInputLayout.setHint(getString(R.string.area));
        TextInputLayout referredEmailTextInputLayout = (TextInputLayout) dialogView.findViewById(R.id.referredEmailTextInputLayout);
        TextInputLayout referredPhoneTextInputLayout = (TextInputLayout) dialogView.findViewById(R.id.referredPhoneTextInputLayout);
        referredEmailTextInputLayout.setVisibility(View.GONE);
        referredPhoneTextInputLayout.setVisibility(View.GONE);
        //-----------

        dialogBuilder.setTitle(getString(R.string.err_msg_no_area_entered))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String area = referByField.getText().toString().trim();

                        if (area.isEmpty()) {
                            CommonMethods.showToast(getActivity(), getString(R.string.err_msg_no_area_entered));
                        } else {
                            onValueClicked(0, area);
                            dismiss();
                        }
                    }
                }).setCancelable(true)
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
}
