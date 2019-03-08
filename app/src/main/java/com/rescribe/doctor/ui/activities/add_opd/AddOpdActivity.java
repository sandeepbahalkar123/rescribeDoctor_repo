package com.rescribe.doctor.ui.activities.add_opd;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rescribe.doctor.R;
import com.rescribe.doctor.helpers.patient_detail.PatientDetailHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.add_opd.OpdTabHeader;
import com.rescribe.doctor.model.add_opd.OpdTabHeadersBaseModel;
import com.rescribe.doctor.model.add_opd.SaveNewOpdResponseBaseModel;
import com.rescribe.doctor.model.add_opd.SaveOPDRequestBaseModel;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.ui.fragments.add_opd.ComplaintsFragment;
import com.rescribe.doctor.ui.fragments.add_opd.DiagnosisFragment;
import com.rescribe.doctor.ui.fragments.add_opd.LaboratoryFragment;
import com.rescribe.doctor.ui.fragments.add_opd.RadiologyFragment;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddOpdActivity extends AppCompatActivity implements HelperResponse {


    @BindView(R.id.backImageView)
    ImageView mBackArrow;
    @BindView(R.id.tabFragment)
    TabLayout mTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewpager;
    @BindView(R.id.titleTextView)
    CustomTextView titleTextView;
    @BindView(R.id.userInfoTextView)
    CustomTextView userInfoTextView;
    @BindView(R.id.opdSaveButton)
    Button opdSaveButton;
    @BindView(R.id.dateTextview)
    CustomTextView dateTextview;
    @BindView(R.id.footer)
    LinearLayout footer;


    private Context mContext;
    private ViewPagerAdapter mViewPagerAdapter;

    ArrayList<String> arrayListTabTitle = new ArrayList<>();
    ComplaintsFragment complaints;

    String currentDate;
    String currentTime;
    private String month;
    private String mYear;
    private String patientName;
    private String patientInfo;
    private String mHospitalPatId;
    private String mLocationId;
    private String mPatientId;
    private int mHospitalId;

    PatientDetailHelper patientDetailHelper;
    DiagnosisFragment diagnosis;
    RadiologyFragment radio;
    LaboratoryFragment lab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_opd);
        ButterKnife.bind(this);
        mContext = this;
        getIntentParams();
        setupViewPager();


    }

    private void addTabNames() {
        arrayListTabTitle.add("Complaints");
        arrayListTabTitle.add("Vitals");
        arrayListTabTitle.add("Diagnosis");
        arrayListTabTitle.add("Prescription");
        arrayListTabTitle.add("Radiology");
        arrayListTabTitle.add("Laboratory");
    }

    private void setupViewPager() {
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mTabLayout.setupWithViewPager(mViewpager);

        complaints = ComplaintsFragment.getInstance("Complaints");
        mViewPagerAdapter.addFragment(complaints, "Complaints");

        mViewPagerAdapter.addFragment(new Fragment(), "Vitals");

        diagnosis = DiagnosisFragment.getInstance("diagnosis");
        mViewPagerAdapter.addFragment(diagnosis, "Diagnosis");

        mViewPagerAdapter.addFragment(new Fragment(), "Prescription");

        radio = RadiologyFragment.getInstance("radio");
        mViewPagerAdapter.addFragment(radio, "Radiology");

        lab = LaboratoryFragment.getInstance("lab");
        mViewPagerAdapter.addFragment(lab, "Laboratory");
        mViewpager.setOffscreenPageLimit(6);
        mViewpager.setAdapter(mViewPagerAdapter);

    }

    private void getIntentParams() {
        patientDetailHelper = new PatientDetailHelper(this, this);
        patientName = getIntent().getStringExtra(RescribeConstants.PATIENT_NAME);
        patientInfo = getIntent().getStringExtra(RescribeConstants.PATIENT_INFO);
        mHospitalPatId = getIntent().getStringExtra(RescribeConstants.PATIENT_HOS_PAT_ID);
        mLocationId = getIntent().getStringExtra(RescribeConstants.LOCATION_ID);
        mPatientId = getIntent().getStringExtra(RescribeConstants.PATIENT_ID);
        mHospitalId = getIntent().getIntExtra(RescribeConstants.CLINIC_ID, 0);

        titleTextView.setText(patientName);
        dateTextview.setVisibility(View.VISIBLE);
        userInfoTextView.setVisibility(View.VISIBLE);
        userInfoTextView.setText(patientInfo);

        currentDate = CommonMethods.getCurrentDate(RescribeConstants.DATE_PATTERN.YYYY_MM_DD);
        currentTime = CommonMethods.getCurrentDateTime();
        String timeToShow = CommonMethods.formatDateTime(currentDate, RescribeConstants.DATE_PATTERN.MMM_YY,
                RescribeConstants.DATE_PATTERN.YYYY_MM_DD, RescribeConstants.DATE).toLowerCase();
        String[] timeToShowSpilt = timeToShow.split(",");
        month = timeToShowSpilt[0].substring(0, 1).toUpperCase() + timeToShowSpilt[0].substring(1);
        mYear = timeToShowSpilt.length == 2 ? timeToShowSpilt[1] : "";
        Date date = CommonMethods.convertStringToDate(currentDate, RescribeConstants.DATE_PATTERN.YYYY_MM_DD);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String toDisplay = cal.get(Calendar.DAY_OF_MONTH) + "<sup>" + CommonMethods.getSuffixForNumber(cal.get(Calendar.DAY_OF_MONTH)) + "</sup> " + month + "'" + mYear;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            dateTextview.setText(Html.fromHtml(toDisplay, Html.FROM_HTML_MODE_LEGACY));
        } else {
            dateTextview.setText(Html.fromHtml(toDisplay));
        }

        patientDetailHelper.getOpdTabHeadersList("opd");
    }

    @OnClick({R.id.backImageView, R.id.opdSaveButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backImageView:
                onBackPressed();
                break;
            case R.id.opdSaveButton:

//                if (complaints.isMandatory()){
//
//                }else if (diagnosis.isMandatory()){
//
//                }else if ()
//




                SaveOPDRequestBaseModel saveOPDRequestBaseModel = new SaveOPDRequestBaseModel();
                saveOPDRequestBaseModel.setHospitalId(mHospitalId);
                saveOPDRequestBaseModel.setHospitalPatId(Integer.parseInt(mHospitalPatId));
                saveOPDRequestBaseModel.setLocationId(Integer.parseInt(mLocationId));
                saveOPDRequestBaseModel.setOpdDate(currentDate);
                saveOPDRequestBaseModel.setOpdTime("");
                saveOPDRequestBaseModel.setPatientId(Integer.parseInt(mPatientId));
                saveOPDRequestBaseModel.setComplaints(complaints.getSelectedComplaintList());
                saveOPDRequestBaseModel.setDiagnosis(diagnosis.getDiagnosisModels());
                saveOPDRequestBaseModel.setRadioDetails(radio.getRadioDetailModels());
                saveOPDRequestBaseModel.setLabDetails(lab.getLabDetailModels());
                patientDetailHelper.saveNewOpd(saveOPDRequestBaseModel);
                break;
        }
    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        switch (mOldDataTag) {
            case RescribeConstants.TASK_SAVE_OPD:
                SaveNewOpdResponseBaseModel saveNewOpdResponseBaseModel = (SaveNewOpdResponseBaseModel) customResponse;
                if (saveNewOpdResponseBaseModel.getCommon().isSuccess()) {
                    showPrintDialog(saveNewOpdResponseBaseModel.getCommon().getStatusMessage());
                }
                break;

            case RescribeConstants.TASK_GET_OPD_TAB_HEADERS_LIST:
                OpdTabHeadersBaseModel headersBaseModel = (OpdTabHeadersBaseModel) customResponse;
                if (headersBaseModel.getCommon().isSuccess()) {

                    arrayListTabTitle.add("Complaints");
                    arrayListTabTitle.add("Vitals");
                    arrayListTabTitle.add("Diagnosis");
                    arrayListTabTitle.add("Prescription");
                    arrayListTabTitle.add("Radiology");
                    arrayListTabTitle.add("Laboratory");

                    ArrayList<OpdTabHeader> opdTabHeaders = headersBaseModel.getOpdTabHeaderArrayList();
                    if (!opdTabHeaders.isEmpty()) {
                        for (OpdTabHeader opdTabHeader : opdTabHeaders) {
                            String headerName = opdTabHeader.getHeaderLabel();
                            switch (headerName) {
                                case "Complaints":
                                    complaints.setdata(opdTabHeader);
                                    break;

                                case "Vitals":
                                    break;

                                case "Diagnosis":
                                    diagnosis.setdata(opdTabHeader);
                                    break;
                                case "Prescription":
                                    break;

                                case "Radiology":
                                    radio.setData(opdTabHeader);
                                    break;

                                case "Laboratory":
                                    lab.setData(opdTabHeader);
                                    break;


                            }

                        }
                    }


                }
                break;
        }
    }


    private void showPrintDialog(String msg) {
        final Dialog dialog = new Dialog(mContext);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_exit);
        TextView textView = (TextView) dialog.findViewById(R.id.textview_sucess);
        textView.setText(msg + getString(R.string.do_you_want_to_print));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);


        dialog.findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        dialog.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                setResult(Activity.RESULT_OK);
                finish();
            }
        });

        dialog.show();
    }


    @Override
    public void onParseError(String mOldDataTag, String errorMessage) {
        CommonMethods.showToast(AddOpdActivity.this, errorMessage);

    }

    @Override
    public void onServerError(String mOldDataTag, String serverErrorMessage) {
        CommonMethods.showToast(AddOpdActivity.this, serverErrorMessage);
    }

    @Override
    public void onNoConnectionError(String mOldDataTag, String serverErrorMessage) {
        CommonMethods.showToast(AddOpdActivity.this, serverErrorMessage);

    }


    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "" + mFragmentTitleList.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

}
