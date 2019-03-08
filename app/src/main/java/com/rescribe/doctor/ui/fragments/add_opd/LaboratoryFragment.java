package com.rescribe.doctor.ui.fragments.add_opd;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.add_opd.OpdSearchHeaderAdapter;
import com.rescribe.doctor.adapters.add_opd.SelectedLaboratoryAdapter;
import com.rescribe.doctor.helpers.patient_detail.PatientDetailHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.add_opd.LabDetailModel;
import com.rescribe.doctor.model.add_opd.OPDHeadersSearchDataBaseModel;
import com.rescribe.doctor.model.add_opd.OpdSearch;
import com.rescribe.doctor.model.add_opd.OpdTabHeader;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

public class LaboratoryFragment extends Fragment implements HelperResponse, OpdSearchHeaderAdapter.OnSearchHeaderClicked, SelectedLaboratoryAdapter.OnCategoryClicked {


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

    @BindView(R.id.editNoOfDays)
    AutoCompleteTextView editNoOfDays;
    @BindView(R.id.spinnerDayPeriod)
    Spinner spinnerDayPeriod;
    @BindView(R.id.layoutDuration)
    LinearLayout layoutDuration;
    @BindView(R.id.TextInputLayoutSearch)
    TextInputLayout TextInputLayoutSearch;
    @BindView(R.id.TextInputLayoutNoOfDays)
    TextInputLayout TextInputLayoutNoOfDays;
    @BindView(R.id.recyclerViewOpdHeader)
    RecyclerView recyclerViewOpdHeader;

    ArrayList<LabDetailModel> labDetailModels = new ArrayList<>();

    SelectedLaboratoryAdapter selectedLaboratoryAdapter;
    @BindView(R.id.textOpdListHeader)
    TextView textOpdListHeader;

    OpdSearch selectedOpdSearch = null;
    @BindView(R.id.layoutUnableSettings)
    LinearLayout layoutUnableSettings;
    @BindView(R.id.layoutMain)
    RelativeLayout layoutMain;
    boolean isMandatory =false;
    public LaboratoryFragment() {
        // Required empty public constructor
    }

    public static LaboratoryFragment getInstance(String opdName) {
        Bundle bundle = new Bundle();
        bundle.putString(RescribeConstants.OPD_NAME, opdName);
        LaboratoryFragment laboratoryFragment = new LaboratoryFragment();
        laboratoryFragment.setArguments(bundle);
        return laboratoryFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_opd_container, container, false);
        unbinder = ButterKnife.bind(this, view);
        patientDetailHelper = new PatientDetailHelper(getActivity(), this);
        if (getArguments() != null) {
            opdName = getArguments().getString(RescribeConstants.OPD_NAME);
        }

        if (opdName != null && opdName.equals("lab")) {
            textOpdListHeader.setText("Laboratory");
        }

        selectedLaboratoryAdapter = new SelectedLaboratoryAdapter(labDetailModels, getActivity(), this, opdName);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewOpdHeader.setLayoutManager(layoutManager);
        recyclerViewOpdHeader.setAdapter(selectedLaboratoryAdapter);
        searchTextChangerListener();

        return view;
    }


    private void searchTextChangerListener() {
        editSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() >= 3) {
                    patientDetailHelper.getOpdHeadersList(opdName.toLowerCase(), editSearch.getText().toString().trim());
                } else {
                    imageButtonAddMore.setVisibility(View.GONE);
                    imageButtonClare.setVisibility(View.GONE);
                }
                if (editable.length() > 0) {
                    imageButtonAddMore.setVisibility(View.VISIBLE);
                    imageButtonClare.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }
        });
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
                textOpdListHeader.setVisibility(View.VISIBLE);
                LabDetailModel labDetailModel = new LabDetailModel();
                if (selectedOpdSearch != null) {
                    labDetailModel.setName(selectedOpdSearch.getName());
                    labDetailModel.setId(selectedOpdSearch.getId());
                    labDetailModel.setType(selectedOpdSearch.getType());
                } else {
                    labDetailModel.setName(editSearch.getText().toString());
                    labDetailModel.setId(0);
                    labDetailModel.setType("lab");
                }

                selectedLaboratoryAdapter.add(labDetailModel);
                imageButtonClare.setVisibility(View.GONE);
                imageButtonAddMore.setVisibility(View.GONE);
                editSearch.setText("");
                selectedOpdSearch = null;
                break;
            case R.id.imageButtonClare:
                imageButtonClare.setVisibility(View.GONE);
                imageButtonAddMore.setVisibility(View.GONE);
                editSearch.setText("");
                selectedOpdSearch = null;

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
                    imageButtonAddMore.setVisibility(View.VISIBLE);
                    imageButtonClare.setVisibility(View.VISIBLE);
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Log.d("TEXT: ", result.get(0));
                    editSearch.setText(result.get(0));
                    patientDetailHelper.getOpdHeadersList(opdName.toLowerCase(), editSearch.getText().toString().trim());

                }
        }
    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        OPDHeadersSearchDataBaseModel headersSearchDataBaseModel = (OPDHeadersSearchDataBaseModel) customResponse;
        if (headersSearchDataBaseModel.getCommon().isSuccess()) {
            if (!headersSearchDataBaseModel.getOpdSearchArrayList().isEmpty()) {
                ArrayList<OpdSearch> opdSearches = headersSearchDataBaseModel.getOpdSearchArrayList();
                OpdSearchHeaderAdapter opdSearchHeaderAdapter = new OpdSearchHeaderAdapter(opdSearches, getActivity(), LaboratoryFragment.this);
                editSearch.setAdapter(opdSearchHeaderAdapter);
                editSearch.showDropDown();
            }

        }
    }

    @Override
    public void onParseError(String mOldDataTag, String errorMessage) {
        CommonMethods.showToast(getActivity(), errorMessage);
    }

    @Override
    public void onServerError(String mOldDataTag, String serverErrorMessage) {
        CommonMethods.showToast(getActivity(), serverErrorMessage);

    }

    @Override
    public void onNoConnectionError(String mOldDataTag, String serverErrorMessage) {
        CommonMethods.showToast(getActivity(), serverErrorMessage);

    }

    @Override
    public void onHeaderItemClicked(OpdSearch opdSearch) {
        selectedOpdSearch = opdSearch;
        editSearch.setText(opdSearch.getName());
    }

    @Override
    public void onLaboratoryItemRemoveClicked(LabDetailModel labDetailModel) {
        selectedLaboratoryAdapter.removeItem(labDetailModel);
    }

    public ArrayList<LabDetailModel> getLabDetailModels() {
        return labDetailModels;
    }

    public void setLabDetailModels(ArrayList<LabDetailModel> labDetailModels) {
        this.labDetailModels = labDetailModels;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(boolean mandatory) {
        isMandatory = mandatory;
    }

    public void setData(OpdTabHeader opdTabHeader) {
        isMandatory =opdTabHeader.isMandatory();
        if (opdTabHeader.isVisible()) {
            layoutUnableSettings.setVisibility(View.GONE);
            layoutMain.setVisibility(View.VISIBLE);
        } else {
            layoutUnableSettings.setVisibility(View.VISIBLE);
            layoutMain.setVisibility(View.GONE);
        }
    }
}