package com.rescribe.doctor.ui.activities.my_patients.patient_history;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.rescribe.doctor.R;
import com.rescribe.doctor.ui.fragments.patient.patient_history_fragment.PatientHistoryListFragmentContainer;
import com.rescribe.doctor.util.RescribeConstants;

import static com.rescribe.doctor.ui.fragments.patient.patient_history_fragment.PatientHistoryListFragmentContainer.SELECT_REQUEST_CODE;

/**
 * Created by jeetal on 31/7/17.
 */

public class PatientHistoryActivity extends AppCompatActivity {

    private PatientHistoryListFragmentContainer fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_history);
        addFragment();
    }

    private void addFragment() {
        fragment = new PatientHistoryListFragmentContainer();
        fragment.setArguments(getIntent().getBundleExtra(RescribeConstants.PATIENT_INFO));
        getSupportFragmentManager().beginTransaction().replace(R.id.viewContainer, fragment).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_REQUEST_CODE)
                fragment.initialize();
        }
    }
}
