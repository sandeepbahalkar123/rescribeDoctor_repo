package com.rescribe.doctor.ui.fragments.add_opd;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.prescription.PrescriptionAdapter;
import com.rescribe.doctor.helpers.patient_detail.PatientDetailHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.classify.ClassifyData;
import com.rescribe.doctor.model.new_opd.Prescription;
import com.rescribe.doctor.ui.activities.classify.PrescriptionActivity;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;
import static com.rescribe.doctor.ui.fragments.add_opd.DiagnosisFragment.REQ_CODE_SPEECH_INPUT;
import static com.rescribe.doctor.util.RescribeConstants.PRESCRIPTION;

public class PrescriptionFragment extends Fragment implements HelperResponse {

    public static final int PRESCRIPTION_CODE = 292;

    @BindView(R.id.miceButton)
    ImageButton miceButton;
    @BindView(R.id.addButton)
    ImageButton addButton;
    Unbinder unbinder;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private PatientDetailHelper patientDetailHelper;
    private ArrayList<Prescription> prescriptionList = new ArrayList<>();
    private PrescriptionAdapter mAdapter;

    public PrescriptionFragment() {
        // Required empty public constructor
    }

    public static PrescriptionFragment newInstance() {
        PrescriptionFragment fragment = new PrescriptionFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_prescription, container, false);
        unbinder = ButterKnife.bind(this, view);
        patientDetailHelper = new PatientDetailHelper(getActivity(), this);
        mAdapter = new PrescriptionAdapter(prescriptionList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        return view;
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 20000000);

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT:
                if (resultCode == RESULT_OK) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Log.d("TEXT: ", result.get(0));
                    patientDetailHelper.classifyAPI(result.get(0));
                }
                break;
            case PRESCRIPTION_CODE:
                if (resultCode == RESULT_OK) {
                    ClassifyData classifyData = data.getParcelableExtra(PRESCRIPTION);
                    Prescription prescription = new Prescription();
                    prescription.setDays(classifyData.getDuration());
                    prescription.setQuantity("1");
                    prescription.setDosage(classifyData.getCapacity());
                    prescription.setFreqSchedule(classifyData.getDose());
                    prescription.setFrequency(classifyData.getDose());
                    prescription.setGenericName(classifyData.getDrugname());
                    prescription.setMedicineId(1);
                    prescription.setMedicineTypeId("1");
                    prescription.setName(classifyData.getDrugname());
                    prescription.setRemarks(classifyData.getWhentotake());
                    prescriptionList.add(prescription);
                    mAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.miceButton, R.id.addButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.miceButton:
                promptSpeechInput();
                break;
            case R.id.addButton:
                Intent intent = new Intent(getActivity(), PrescriptionActivity.class);
                intent.putExtra(PRESCRIPTION, new ClassifyData());
                startActivityForResult(intent, PRESCRIPTION_CODE);
                break;
        }
    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        if (mOldDataTag.equals(RescribeConstants.TASK_GET_CLASSIFY)) {
            ClassifyData classifyData = (ClassifyData) customResponse;
            if (classifyData.getTrigger().equalsIgnoreCase("prescription")) {
                Intent intent = new Intent(getActivity(), PrescriptionActivity.class);
                intent.putExtra(PRESCRIPTION, classifyData);
                startActivityForResult(intent, PRESCRIPTION_CODE);
            }
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
