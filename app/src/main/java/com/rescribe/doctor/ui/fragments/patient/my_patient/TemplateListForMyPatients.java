package com.rescribe.doctor.ui.fragments.patient.my_patient;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.my_patients.TemplateAdapter;
import com.rescribe.doctor.helpers.doctor_patients.PatientList;
import com.rescribe.doctor.helpers.myappointments.AppointmentHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.my_appointments.AppointmentList;
import com.rescribe.doctor.model.patient.template_sms.TemplateBaseModel;
import com.rescribe.doctor.model.patient.template_sms.TemplateList;
import com.rescribe.doctor.model.patient.template_sms.request_send_sms.ClinicListForSms;
import com.rescribe.doctor.model.patient.template_sms.request_send_sms.PatientInfoList;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.ui.activities.my_patients.SendSmsActivity;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jeetal on 27/2/18.
 */

public class TemplateListForMyPatients extends AppCompatActivity implements HelperResponse, TemplateAdapter.OnCardViewClickListener {

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
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.emptyListView)
    RelativeLayout emptyListView;
    private TemplateListForMyPatients mContext;
    private AppointmentHelper mAppointmentHelper;
    private TemplateAdapter mTemplateAdapter;
    private Intent intent;
    private ArrayList<PatientInfoList> patientLists = new ArrayList<>();
    private int mlocationId;
    private int mClinicId;
    private String mClinicName = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.template_base_layout);
        ButterKnife.bind(this);
        initialize();

    }

    private void initialize() {
        mContext = TemplateListForMyPatients.this;
        intent = getIntent();
        if (intent.getExtras() != null) {
            patientLists = intent.getParcelableArrayListExtra(RescribeConstants.PATIENT_LIST);
            mlocationId = intent.getIntExtra(RescribeConstants.LOCATION_ID, 0);
            mClinicId = intent.getIntExtra(RescribeConstants.CLINIC_ID,0);
            mClinicName= intent.getStringExtra(RescribeConstants.CLINIC_NAME);
        }
        mAppointmentHelper = new AppointmentHelper(mContext, this);
        mAppointmentHelper.doGetDoctorTemplate();
        titleTextView.setText(getString(R.string.template_list));

    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_GET_DOCTOR_SMS_TEMPLATE)) {
            TemplateBaseModel templateBaseModel = (TemplateBaseModel) customResponse;
            ArrayList<TemplateList> templateLists = templateBaseModel.getTemplateDataModel().getTemplateList();
            if (!templateLists.isEmpty()) {
                recyclerView.setVisibility(View.VISIBLE);
                emptyListView.setVisibility(View.GONE);
                mTemplateAdapter = new TemplateAdapter(mContext, templateLists, this);
                LinearLayoutManager linearlayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(linearlayoutManager);
                // off recyclerView Animation
                RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
                if (animator instanceof SimpleItemAnimator)
                    ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
                recyclerView.setAdapter(mTemplateAdapter);
            } else {
                recyclerView.setVisibility(View.GONE);
                emptyListView.setVisibility(View.VISIBLE);
            }

        }

    }

    @Override
    public void onParseError(String mOldDataTag, String errorMessage) {
        Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onServerError(String mOldDataTag, String serverErrorMessage) {
        Toast.makeText(mContext, serverErrorMessage, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNoConnectionError(String mOldDataTag, String serverErrorMessage) {
        Toast.makeText(mContext, serverErrorMessage, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onCardViewClick(TemplateList templateList) {

        Intent intent = new Intent(this,SendSmsPatientActivity.class);
        intent.putExtra(RescribeConstants.LOCATION_ID, mlocationId);
        intent.putExtra(RescribeConstants.CLINIC_ID, mClinicId);
        intent.putExtra(RescribeConstants.TEMPLATE_OBJECT,templateList);
        intent.putExtra(RescribeConstants.CLINIC_NAME,mClinicName);
        intent.putParcelableArrayListExtra(RescribeConstants.PATIENT_LIST, patientLists);
        startActivity(intent);

    }


    @OnClick({R.id.backImageView, R.id.titleTextView})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backImageView:
                finish();
                break;
            case R.id.titleTextView:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
