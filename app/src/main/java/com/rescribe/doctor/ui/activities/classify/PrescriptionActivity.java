package com.rescribe.doctor.ui.activities.classify;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;

import com.rescribe.doctor.R;
import com.rescribe.doctor.model.classify.ClassifyData;
import com.rescribe.doctor.util.RescribeConstants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PrescriptionActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.medicineType)
    TextInputEditText medicineType;
    @BindView(R.id.medicineName)
    TextInputEditText medicineName;
    @BindView(R.id.breakfast)
    TextInputEditText breakfast;
    @BindView(R.id.lunch)
    TextInputEditText lunch;
    @BindView(R.id.dinner)
    TextInputEditText dinner;
    @BindView(R.id.dose)
    TextInputEditText dose;
    @BindView(R.id.days)
    TextInputEditText days;
    @BindView(R.id.instruction)
    TextInputEditText instruction;
    @BindView(R.id.buttonOK)
    Button buttonOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ClassifyData prescription = getIntent().getParcelableExtra(RescribeConstants.PRESCRIPTION);
        medicineType.setText(prescription.getMedtype());
        medicineName.setText(prescription.getDrugname());
        breakfast.setText(prescription.getDose());
//        lunch.setText(prescription.getLunch());
//        dinner.setText(prescription.getDinner());
        dose.setText(prescription.getCapacity());
        days.setText(prescription.getDuration());
        instruction.setText(prescription.getWhentotake());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

}
