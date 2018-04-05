package com.rescribe.doctor.ui.activities.my_patients.add_new_patient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.rescribe.doctor.R;
import com.rescribe.doctor.helpers.myappointments.AppointmentHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.patient.add_new_patient.AddNewPatient;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddNewPatientActivity extends AppCompatActivity implements HelperResponse {

    private static final String TAG = "AddPatient";

    @BindView(R.id.titleTextView)
    CustomTextView mTitleTextView;
    @BindView(R.id.userInfoTextView)
    CustomTextView mUserInfoTextView;

    @BindView(R.id.dateTextview)
    CustomTextView mDateTextview;
    @BindView(R.id.year)
    Spinner mYear;
    @BindView(R.id.addImageView)
    ImageView mAddImageView;


    private AppointmentHelper mAppointmentHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.add_new_patient);
        ButterKnife.bind(this);
        //------
        mTitleTextView.setText(getString(R.string.new_patients));
        mUserInfoTextView.setVisibility(View.INVISIBLE);
        mDateTextview.setVisibility(View.GONE);
        mYear.setVisibility(View.GONE);
        //------
        Bundle extras = getIntent().getBundleExtra(RescribeConstants.PATIENT_DETAILS);
        //hospitalId = extras.getString(RescribeConstants.CLINIC_ID);
        //-----

    }

    @OnClick({R.id.backImageView, })
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.backImageView:
                onBackPressed();
                break;
            case R.id.btnAddPatientSubmit:


        }

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
}
