package com.rescribe.doctor.ui.activities.patient_details;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.patient_detail.PatientDetailAdapter;
import com.rescribe.doctor.helpers.patient_detail.PatientDetailHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.case_details.PatientHistory;
import com.rescribe.doctor.model.case_details.Range;
import com.rescribe.doctor.model.case_details.VisitCommonData;
import com.rescribe.doctor.model.case_details.VisitData;
import com.rescribe.doctor.model.case_details.Vital;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.util.RescribeConstants;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import static com.rescribe.doctor.adapters.patient_detail.PatientDetailAdapter.TEXT_LIMIT;

/**
 * Created by jeetal on 6/2/18.
 */

public class PatientDetailActivity extends AppCompatActivity implements HelperResponse {

    @BindView(R.id.historyExpandableListView)
    ExpandableListView mHistoryExpandableListView;
    @BindView(R.id.backImageView)
    ImageView backImageView;
    @BindView(R.id.titleTextView)
    CustomTextView titleTextView;
    @BindView(R.id.userInfoTextView)
    CustomTextView userInfoTextView;
    @BindView(R.id.dateTextview)
    CustomTextView dateTextview;
    @BindView(R.id.emptyListView)
    RelativeLayout emptyListView;
    private int mLastExpandedPosition = -1;
    Intent mIntent;
    private String TAG = getClass().getName();
    private PatientDetailHelper mSingleVisitDetailHelper;
    private PatientDetailAdapter mSingleVisitAdapter;
    private Context mContext;
    private String mDocName;
    private String doctorName = "";
    private boolean isBpMin = false;
    private boolean isBpMax = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_detail_activity);
        ButterKnife.bind(this);
        initialize();
    }


    private void initialize() {

        mContext = PatientDetailActivity.this;
        mIntent = getIntent();
        if (getIntent().getExtras() != null) {
            mDocName = mIntent.getStringExtra(getString(R.string.name));
            if (mIntent.getStringExtra(getString(R.string.name)).contains("Dr.")) {
                doctorName = mIntent.getStringExtra(getString(R.string.name));
            } else {
                doctorName = "Dr. " + mIntent.getStringExtra(getString(R.string.name));
            }
            titleTextView.setText(doctorName);
            userInfoTextView.setText("36 yrs - Male");


            String stringExtra = mIntent.getStringExtra(RescribeConstants.PATIENT_VISIT_DATE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dateTextview.setText(Html.fromHtml(stringExtra, Html.FROM_HTML_MODE_LEGACY));
            } else {
                dateTextview.setText(Html.fromHtml(stringExtra));
            }
        }

        //---

        mSingleVisitDetailHelper = new PatientDetailHelper(this, this);
      //  mSingleVisitDetailHelper.doGetOneDayVisit(RescribeConstants.OPD_ID);


        mHistoryExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

                // this is done because if single element in child list , groupPosition will not expand, it will expand on advice even if it has only one element ,vitals will also expand
                List<PatientHistory> listDataList = mSingleVisitAdapter.getListDataList();
                List<VisitCommonData> childObject = listDataList.get(groupPosition).getCommonData();

                if (mSingleVisitAdapter.getListDataList().get(groupPosition).getCaseDetailName().equalsIgnoreCase("vitals")) {
                    if (mSingleVisitAdapter.getListDataList().get(groupPosition).getVitals().isEmpty()) {
                        mHistoryExpandableListView.collapseGroup(groupPosition);
                    }
                } else if (childObject.size() == 1) {
                    if (childObject.get(0).getName().length() <= TEXT_LIMIT)
                        mHistoryExpandableListView.collapseGroup(groupPosition);
                }

                collapseOther(groupPosition);
            }

            private void collapseOther(int groupPosition) {
                if (mLastExpandedPosition != -1 && mLastExpandedPosition != groupPosition)
                    mHistoryExpandableListView.collapseGroup(mLastExpandedPosition);
                mLastExpandedPosition = groupPosition;
            }
        });

        mHistoryExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener()

        {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                        int childPosition, long id) {

                mHistoryExpandableListView.collapseGroup(groupPosition);
                return false;
            }
        });

    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        String bpMin = "";
        VisitData visitData = (VisitData) customResponse;
        if (visitData != null) {
            mHistoryExpandableListView.setVisibility(View.VISIBLE);
            emptyListView.setVisibility(View.GONE);
        } else {
            mHistoryExpandableListView.setVisibility(View.GONE);
            emptyListView.setVisibility(View.VISIBLE);
        }
        List<PatientHistory> patientHistoryList = visitData.getPatientHistory();
        List<Vital> vitalSortedList = new ArrayList<>();
        // Bpmin and Bpmax is clubed together as Bp in vitals
        for (int i = 0; i < patientHistoryList.size(); i++) {
            if (patientHistoryList.get(i).getVitals() != null) {
                String pos = null;

                List<Vital> vitalList = patientHistoryList.get(i).getVitals();
                for (int j = 0; j < vitalList.size(); j++) {

                    Vital dataObject = vitalList.get(j);
                    if (dataObject.getUnitName().contains(getString(R.string.bp_max))) {
                        setBpMax(true);
                    }
                    if (dataObject.getUnitName().contains(getString(R.string.bp_min))) {
                        setBpMin(true);
                    }
                }

                for (int j = 0; j < vitalList.size(); j++) {
                    Vital dataObject = vitalList.get(j);
                    if (isBpMax() && isBpMin()) {
                        if (dataObject.getUnitName().contains(getString(R.string.bp_max)) || dataObject.getUnitName().contains(getString(R.string.bp_min))) {
                            Vital vital = new Vital();
                            if (pos == null) {
                                vital.setUnitName(getString(R.string.bp) + " " + dataObject.getUnitValue());
                                vital.setUnitValue(dataObject.getUnitValue());
                                vital.setCategory(dataObject.getCategory());
                                vital.setIcon(dataObject.getIcon());
                                for (int k = 0; k < dataObject.getRanges().size(); k++) {
                                    dataObject.getRanges().get(k).setNameOfVital(getString(R.string.bp_max));
                                }
                                vital.setRanges(dataObject.getRanges());
                                vital.setDisplayName(dataObject.getDisplayName());
                                vitalSortedList.add(vital);
                                pos = String.valueOf(j);
                            } else {
                                Vital previousData = vitalSortedList.get(Integer.parseInt(pos));
                                String unitValue = previousData.getUnitValue();
                                String unitCategory = previousData.getCategory();
                                unitCategory = unitCategory + getString(R.string.colon_sign) + dataObject.getCategory();
                                unitValue = unitValue + "/" + dataObject.getUnitValue();
                                previousData.setUnitName(getString(R.string.bp));
                                previousData.setUnitValue(unitValue);
                                previousData.setCategory(unitCategory);
                                List<Range> ranges = previousData.getRanges();
                                ranges.addAll(dataObject.getRanges());
                                vitalSortedList.set(Integer.parseInt(pos), previousData);
                            }
                        } else {
                            Vital vital = new Vital();
                            vital.setUnitName(vitalList.get(j).getUnitName());
                            vital.setUnitValue(vitalList.get(j).getUnitValue());
                            vital.setCategory(vitalList.get(j).getCategory());
                            vital.setRanges(vitalList.get(j).getRanges());
                            vital.setIcon(vitalList.get(j).getIcon());
                            vital.setDisplayName(vitalList.get(j).getDisplayName());
                            vitalSortedList.add(vital);
                        }

                    } else {
                        Vital vital = new Vital();
                        if (dataObject.getUnitName().contains(getString(R.string.bp_max))) {
                            vital = vitalList.get(j);
                            vital.setUnitName("Systolic BP" + " " + vital.getUnitValue());
                            vital.setDisplayName("Systolic BP");
                            vitalSortedList.add(vital);
                        } else if (dataObject.getUnitName().contains(getString(R.string.bp_min))) {
                            vital = vitalList.get(j);
                            vital.setUnitName("Diastolic BP" + " " + vital.getUnitValue());
                            vital.setDisplayName("Diastolic BP");
                            vitalSortedList.add(vital);
                        } else {
                            vital = vitalList.get(j);
                            vitalSortedList.add(vital);
                        }
                    }
                }
                patientHistoryList.get(i).setVitals(vitalSortedList);
            }
        }

        mSingleVisitAdapter = new PatientDetailAdapter(this, patientHistoryList);
        mHistoryExpandableListView.setAdapter(mSingleVisitAdapter);


    }

    @Override
    public void onParseError(String mOldDataTag, String errorMessage) {

    }

    @Override
    public void onServerError(String mOldDataTag, String serverErrorMessage) {

    }

    @Override
    public void onNoConnectionError(String mOldDataTag, String serverErrorMessage) {
        mHistoryExpandableListView.setVisibility(View.GONE);
        emptyListView.setVisibility(View.VISIBLE);
    }

    public boolean isBpMin() {
        return isBpMin;
    }

    public void setBpMin(boolean bpMin) {
        isBpMin = bpMin;
    }

    public boolean isBpMax() {
        return isBpMax;
    }

    public void setBpMax(boolean bpMax) {
        isBpMax = bpMax;
    }
}

