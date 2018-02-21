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
import android.widget.Spinner;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.patient_detail.SingleVisitAdapter;
import com.rescribe.doctor.helpers.patient_detail.PatientDetailHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.case_details.PatientHistory;
import com.rescribe.doctor.model.case_details.Range;
import com.rescribe.doctor.model.case_details.VisitCommonData;
import com.rescribe.doctor.model.case_details.VisitData;
import com.rescribe.doctor.model.case_details.Vital;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.rescribe.doctor.adapters.patient_detail.SingleVisitAdapter.TEXT_LIMIT;

/**
 * Created by jeetal on 14/6/17.
 */


public class SingleVisitDetailsActivity extends AppCompatActivity implements HelperResponse {

    @BindView(R.id.historyExpandableListView)
    ExpandableListView mHistoryExpandableListView;

    @BindView(R.id.emptyListView)
    RelativeLayout mNoRecordAvailable;

    public static final String DOCTOR_ID = "doctor_id";
    @BindView(R.id.backImageView)
    ImageView backImageView;
    @BindView(R.id.titleTextView)
    CustomTextView titleTextView;
    @BindView(R.id.userInfoTextView)
    CustomTextView userInfoTextView;
    @BindView(R.id.dateTextview)
    CustomTextView dateTextview;
    @BindView(R.id.year)
    Spinner year;
    @BindView(R.id.addImageView)
    ImageView addImageView;
    private int mLastExpandedPosition = -1;
    Intent mIntent;
    private SingleVisitAdapter mSingleVisitAdapter;
    private boolean isBpMin = false;
    private boolean isBpMax = false;
    private String mDocId;
    private Intent intent;
    private String month;
    private String mYear;
    private String mDateSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_detail_activity);
        ButterKnife.bind(this);
        initialize();
    }


    private void initialize() {

        Context mContext = SingleVisitDetailsActivity.this;
        intent = getIntent();
        userInfoTextView.setVisibility(View.VISIBLE);
        dateTextview.setVisibility(View.VISIBLE);
        if (intent.getExtras() != null) {
            titleTextView.setText(intent.getStringExtra(RescribeConstants.PATIENT_NAME));
            userInfoTextView.setText(intent.getStringExtra(RescribeConstants.PATIENT_INFO));
            mDateSelected = intent.getStringExtra(RescribeConstants.DATE);
            String timeToShow = CommonMethods.formatDateTime(mDateSelected, RescribeConstants.DATE_PATTERN.MMM_YY,
                    RescribeConstants.DATE_PATTERN.UTC_PATTERN, RescribeConstants.DATE).toLowerCase();
            String[] timeToShowSpilt = timeToShow.split(",");
            month = timeToShowSpilt[0].substring(0, 1).toUpperCase() + timeToShowSpilt[0].substring(1);
            mYear = timeToShowSpilt.length == 2 ? timeToShowSpilt[1] : "";
            Date date = CommonMethods.convertStringToDate(mDateSelected, RescribeConstants.DATE_PATTERN.UTC_PATTERN);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            timeToShow = timeToShow.substring(0, 1).toUpperCase() + timeToShow.substring(1);
            String toDisplay = cal.get(Calendar.DAY_OF_MONTH) + "<sup>" + CommonMethods.getSuffixForNumber(cal.get(Calendar.DAY_OF_MONTH)) + "</sup> " + month + "'" + mYear;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dateTextview.setText(Html.fromHtml(toDisplay, Html.FROM_HTML_MODE_LEGACY));
            } else {
                dateTextview.setText(Html.fromHtml(toDisplay));
            }
        }

        PatientDetailHelper mSingleVisitDetailHelper = new PatientDetailHelper(this, this);
        mSingleVisitDetailHelper.doGetOneDayVisit(/*mIntent.getStringExtra(getString(R.string.opd_id)),*/ mDocId);

        // title.setText(getString(R.string.visit_details));

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
        VisitData visitData = (VisitData) customResponse;
        if (visitData != null) {
            mHistoryExpandableListView.setVisibility(View.VISIBLE);
            mNoRecordAvailable.setVisibility(View.GONE);
        } else {
            mHistoryExpandableListView.setVisibility(View.GONE);
            mNoRecordAvailable.setVisibility(View.VISIBLE);
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
                        Vital vital;
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


        mSingleVisitAdapter = new SingleVisitAdapter(this, patientHistoryList);
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
        mNoRecordAvailable.setVisibility(View.VISIBLE);
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

    @OnClick({R.id.backImageView, R.id.userInfoTextView})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backImageView:
                finish();
                break;
            case R.id.userInfoTextView:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
