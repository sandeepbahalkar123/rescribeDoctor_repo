package com.rescribe.doctor.ui.activities.add_opd;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.rescribe.doctor.R;
import com.rescribe.doctor.helpers.patient_detail.PatientDetailHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.add_opd.OPDHeadersSearchDataBaseModel;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

public class AddOpdContainerFragment extends Fragment implements HelperResponse {


    @BindView(R.id.imageButtonMice)
    ImageButton imageButtonMice;
    @BindView(R.id.editSearch)
    AutoCompleteTextView editSearch;
    @BindView(R.id.imageButtonAddMore)
    ImageButton imageButtonAddMore;
    @BindView(R.id.imageButtonClare)
    ImageButton imageButtonClare;
    @BindView(R.id.layoutSearch)
    RelativeLayout layoutSearch;
    Unbinder unbinder;
    public static final int REQ_CODE_SPEECH_INPUT = 100;
    PatientDetailHelper patientDetailHelper;
    String opdName = "";

    public AddOpdContainerFragment() {
        // Required empty public constructor
    }

    public static Fragment getInstance(String opdName) {
        Bundle bundle = new Bundle();
        bundle.putString(RescribeConstants.OPD_NAME, opdName);
        AddOpdContainerFragment opdContainerFragment = new AddOpdContainerFragment();
        opdContainerFragment.setArguments(bundle);
        return opdContainerFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_opd_container, container, false);
        unbinder = ButterKnife.bind(this, view);
        patientDetailHelper = new PatientDetailHelper(getActivity(), this);
        opdName = getArguments().getString(RescribeConstants.OPD_NAME);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.imageButtonMice, R.id.editSearch, R.id.imageButtonAddMore, R.id.imageButtonClare})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imageButtonMice:
                promptSpeechInput();
                break;
            case R.id.editSearch:
                break;
            case R.id.imageButtonAddMore:
                break;
            case R.id.imageButtonClare:
                break;

        }
    }


    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "say something..");
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 20000000);

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(),
                    "ActivityNotFoundException",
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
                    editSearch.setText(result.get(0));
                    patientDetailHelper.getOpdHeadersList(opdName.toLowerCase(), result.get(0));

                }
        }
    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        OPDHeadersSearchDataBaseModel headersSearchDataBaseModel = (OPDHeadersSearchDataBaseModel) customResponse;
        if (headersSearchDataBaseModel.getCommon().isSuccess()) {

          //  editSearch.setAdapter()

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