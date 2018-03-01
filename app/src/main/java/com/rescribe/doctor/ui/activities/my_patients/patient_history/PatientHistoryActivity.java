package com.rescribe.doctor.ui.activities.my_patients.patient_history;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import com.rescribe.doctor.R;
import com.rescribe.doctor.ui.fragments.my_appointments.MyAppointmentsFragment;
import com.rescribe.doctor.ui.fragments.patient.patient_history_fragment.PatientHistoryListFragmentContainer;
import com.rescribe.doctor.util.RescribeConstants;

import butterknife.ButterKnife;

/**
 * Created by jeetal on 31/7/17.
 */

public class PatientHistoryActivity extends AppCompatActivity {

    private boolean isLongPressed;
    private PatientHistoryListFragmentContainer mPatientHistoryListFragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_history);
        ButterKnife.bind(this);
        mPatientHistoryListFragmentContainer = PatientHistoryListFragmentContainer.newInstance(getIntent().getBundleExtra(RescribeConstants.PATIENT_INFO));
        getSupportFragmentManager().beginTransaction().replace(R.id.viewContainer, mPatientHistoryListFragmentContainer).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
