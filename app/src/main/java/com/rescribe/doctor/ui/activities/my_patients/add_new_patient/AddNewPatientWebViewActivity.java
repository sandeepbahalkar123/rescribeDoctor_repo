package com.rescribe.doctor.ui.activities.my_patients.add_new_patient;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.rescribe.doctor.R;
import com.rescribe.doctor.helpers.database.AppDBHelper;
import com.rescribe.doctor.helpers.myappointments.AppointmentHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.Common;
import com.rescribe.doctor.model.new_patient.ReferenceBaseModel;
import com.rescribe.doctor.model.new_patient.ReferenceType;
import com.rescribe.doctor.model.patient.add_new_patient.address_other_details.area_details.AreaData;
import com.rescribe.doctor.model.patient.add_new_patient.address_other_details.area_details.AreaDetailsBaseModel;
import com.rescribe.doctor.model.patient.add_new_patient.address_other_details.reference_details.DoctorData;
import com.rescribe.doctor.model.patient.doctor_patients.PatientAddressDetails;
import com.rescribe.doctor.model.patient.doctor_patients.PatientList;
import com.rescribe.doctor.model.patient.doctor_patients.PatientReferenceDetails;
import com.rescribe.doctor.model.patient.doctor_patients.sync_resp.PatientUpdateDetail;
import com.rescribe.doctor.model.patient.doctor_patients.sync_resp.SyncPatientsModel;
import com.rescribe.doctor.model.waiting_list.new_request_add_to_waiting_list.AddToList;
import com.rescribe.doctor.model.waiting_list.new_request_add_to_waiting_list.PatientAddToWaitingList;
import com.rescribe.doctor.model.waiting_list.new_request_add_to_waiting_list.RequestToAddWaitingList;
import com.rescribe.doctor.model.waiting_list.response_add_to_waiting_list.AddToWaitingListBaseModel;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.services.MQTTService;
import com.rescribe.doctor.ui.activities.book_appointment.SelectSlotToBookAppointmentBaseActivity;
import com.rescribe.doctor.ui.activities.my_patients.add_new_patient.dialog_fragment.CityAndAreaDialogFragment;
import com.rescribe.doctor.ui.activities.my_patients.add_new_patient.dialog_fragment.CityListViewDialogFragment;
import com.rescribe.doctor.ui.activities.my_patients.add_new_patient.dialog_fragment.DoctorListViewDialogFragment;
import com.rescribe.doctor.ui.activities.my_patients.add_new_patient.dialog_fragment.PatientListViewDialogFragment;
import com.rescribe.doctor.ui.activities.my_patients.add_new_patient.dialog_fragment.StateListViewDialogFragment;
import com.rescribe.doctor.ui.activities.my_patients.patient_history.PatientHistoryActivity;
import com.rescribe.doctor.ui.customesViews.CustomProgressDialog;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.Config;
import com.rescribe.doctor.util.NetworkUtil;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.rescribe.doctor.util.RescribeConstants.SUCCESS;

public class AddNewPatientWebViewActivity extends AppCompatActivity implements HelperResponse {

    public static final int ADD_PATIENT_REQUEST = 121;
    private static final String TAG = "AddPatient";
    //---------
    @BindView(R.id.mainParentScrollViewLayout)
    ScrollView mMainParentScrollViewLayout;
    //----------
    @BindView(R.id.webViewLayout)
    WebView mWebViewObject;
    @BindView(R.id.backButton)
    AppCompatImageView backButton;
    @BindView(R.id.webViewTitle)
    TextView mWebViewTitle;
    //---------
    @BindView(R.id.salutationSpinner)
    Spinner mSalutationSpinner;
    @BindView(R.id.firstName)
    EditText mFirstName;
    @BindView(R.id.middleName)
    EditText mMiddleName;
    @BindView(R.id.lastName)
    EditText mLastName;
    @BindView(R.id.mobNo)
    EditText mMobNo;
    @BindView(R.id.age)
    EditText mAge;
    @BindView(R.id.referenceID)
    EditText mReferenceID;
    @BindView(R.id.btnAddPatientSubmit)
    Button mSubmit;
    //-------
    @BindView(R.id.genderRadioGroup)
    RadioGroup mGenderRadioGroup;
    @BindView(R.id.genderMale)
    RadioButton genderMale;
    @BindView(R.id.genderFemale)
    RadioButton genderFemale;
    @BindView(R.id.genderOther)
    RadioButton genderOther;
    //---------
    @BindView(R.id.addressLine)
    EditText mAddressLine;
    @BindView(R.id.addressDetailLayout)
    LinearLayout mAddressDetailLayout;
    @BindView(R.id.layoutReferenceId)
    LinearLayout mLayoutReferenceId;
    @BindView(R.id.referredDetailsTextInputLayout)
    TextInputLayout mReferredDetailsTextInputLayout;
    @BindView(R.id.referredDetails)
    EditText referredDetails;
    @BindView(R.id.salutationSpinnerRef)
    Spinner mSalutationSpinnerRef;
    //----------
    @BindView(R.id.stateEditText)
    EditText mStateEditText;
    //-------------
    @BindView(R.id.cityEditText)
    EditText mCityEditText;
    //---------
    @BindView(R.id.areaEditText)
    EditText mAreaEditText;
    @BindView(R.id.addressAreaTextInputLayout)
    TextInputLayout mAddressAreaTextInputLayout;
    //--------
    @BindView(R.id.referenceBySpinner)
    Spinner mReferenceBySpinner;
    @BindView(R.id.relationSpinner)
    Spinner mRelationSpinner;
    @BindView(R.id.referredBy)
    EditText mReferredBy;
    @BindView(R.id.referredByTextInputLayout)
    TextInputLayout mReferredByTextInputLayout;
    @BindView(R.id.referredPhone)
    EditText mReferredPhone;
    @BindView(R.id.referredEmail)
    EditText mReferredEmail;
    @BindView(R.id.referenceDetailLayout)
    LinearLayout mReferenceDetailLayout;
    //-------
    @BindView(R.id.mainParentLayout)
    LinearLayout mainParentLayout;
    @BindView(R.id.referenceDetailPerson)
    LinearLayout mReferenceDetailPerson;
    int mSelectedSalutationOfPatient = 1;
    int mSelectedSalutationOfPatientRef = 1;
    int mSelectedStateID = -1;
    int mSelectedCityID = -1;
    int mSelectedAreaID = -1;
    List<ReferenceType> referenceTypes;
    private ArrayList<IdAndValueDataModel> mAreaListBasedOnCity = new ArrayList<>();
    //---------
    private int hospitalId;
    private boolean isCalled = false;
    private String locationID;
    private int cityID;
    private String cityName;
    private Context mContext;
    private boolean mAddPatientOfflineSetting;
    private PatientList mAddedPatientListData;
    private String mDoOperationTaskID = null;
    private String mRelation = "Self";
    private int mAddNewPatientSelectedOption = -1;
    private AppointmentHelper mAppointmentHelper;
    private int mSelectedReferenceTypeID = 0;
    private String mSelectedReferenceTypeName = "";
    private DoctorData mSelectedDoctorReference;
    private PatientList mSelectedPatientReference;
    private String mActivityStartFrom;
    private HashMap<String, HashSet<String>> mOfflineCityAndAreaMap = null;

    public static boolean isEnteredRefIDIsValid(String str) {
        boolean isValid = false;
        if (str.isEmpty()) {
            return true;
        } else {
            String expression = "^[a-z_A-Z0-9]*$";
            Pattern pattern = Pattern.implementation(expression);
            Matcher matcher = pattern.matcher(str);
            if (matcher.matches()) {
                isValid = true;
            }
        }
        return isValid;
    }

    //--------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.add_new_patient);
        ButterKnife.bind(this);

        initialize();
        doSpinnerItemSelectedListener();
    }

    private void initialize() {
        mAppointmentHelper = new AppointmentHelper(this, this);
        mContext = this;

        //------------
        mActivityStartFrom = getIntent().getStringExtra(RescribeConstants.START_FROM);

        Bundle extras = getIntent().getBundleExtra(RescribeConstants.PATIENT_DETAILS);
        hospitalId = extras.getInt(RescribeConstants.CLINIC_ID);
        locationID = extras.getString(RescribeConstants.LOCATION_ID);
        cityID = extras.getInt(RescribeConstants.CITY_ID);
        cityName = extras.getString(RescribeConstants.CITY_NAME);
        int docID = Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, this));
        mAppointmentHelper.getReferenceList(hospitalId);
        Log.e("CLINIC_ID", "--" + hospitalId);

        //--------
        String urlData = Config.ADD_NEW_PATIENT_WEB_URL + docID + "/" +
                hospitalId + "/" + locationID + "/" + cityID;

        mWebViewTitle.setText(getString(R.string.patient_registration));

        mAddPatientOfflineSetting = RescribePreferencesManager.getBoolean(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.ADD_PATIENT_OFFLINE_SETTINGS, this);
        //-------------

        boolean internetAvailable = NetworkUtil.isInternetAvailable(this);
        if (internetAvailable && !mAddPatientOfflineSetting) {
            mWebViewObject.setVisibility(View.VISIBLE);
            mMainParentScrollViewLayout.setVisibility(View.GONE);
            loadWebViewData(urlData);
        } else {
            if (!internetAvailable)
                CommonMethods.showToast(this, getString(R.string.add_patient_offline_msg));
            mWebViewObject.setVisibility(View.GONE);
            mMainParentScrollViewLayout.setVisibility(View.VISIBLE);

            //--- show addresss /referecens details based on setting done in SettingActivity. : START
            if (RescribePreferencesManager.getBoolean(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.ADD_PATIENT_OFFLINE_SETTINGS_ADDRESS_DETAILS, mContext)) {
                mAddressDetailLayout.setVisibility(View.VISIBLE);
            }
            if (internetAvailable && RescribePreferencesManager.getBoolean(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.ADD_PATIENT_OFFLINE_SETTINGS_REFERENCES_DETAILS, mContext)) {

                mReferenceDetailLayout.setVisibility(View.VISIBLE);
            }
            //--- show addresss /referecens details based on setting done in SettingActivity. : END
        }
    }

    @OnClick({R.id.backButton, R.id.btnAddPatientSubmit, R.id.stateEditText, R.id.cityEditText, R.id.areaEditText, R.id.referredBy, R.id.referredByTextInputLayout})
    public void back(View view) {

        final FragmentManager fm = getFragmentManager();

        switch (view.getId()) {
            case R.id.backButton:
                onBackPressed();
                break;
            case R.id.btnAddPatientSubmit:
                mAddedPatientListData = validate();
                if (mAddedPatientListData != null) {
                    if (RescribeConstants.WAITING_LIST.equalsIgnoreCase(mActivityStartFrom)) {
                        mAddNewPatientSelectedOption = 1;
                        doAddNewPatientOnSelectedOption();
                    } else {
                        if (NetworkUtil.isInternetAvailable(AddNewPatientWebViewActivity.this)) {
                            showDialogToSelectOption();
                        } else {
                            mAddNewPatientSelectedOption = 0;
                            mDoOperationTaskID = RescribeConstants.TASK_ADD_NEW_PATIENT;
                            addOfflinePatient();
                        }
                    }
                }
                break;
            case R.id.stateEditText:
                Bundle iState = new Bundle();
                iState.putString(RescribeConstants.TITLE, getString(R.string.state));

                StateListViewDialogFragment fState = StateListViewDialogFragment.newInstance(iState, new CityAndAreaDialogFragment.OnItemClickedListener() {

                    @Override
                    public void onItemClicked(int id, String value) {
                        mSelectedStateID = id;
                        mStateEditText.setText("" + value);
                    }
                });
                fState.show(fm, "");

                break;
            case R.id.cityEditText:
                if (mSelectedStateID == -1) {
                    CommonMethods.showToast(this, getString(R.string.plz_select_state));
                } else {

                    Bundle iCity = new Bundle();
                    iCity.putString(RescribeConstants.TITLE, getString(R.string.city));
                    iCity.putInt(RescribeConstants.STATE_ID, mSelectedStateID);

                    CityListViewDialogFragment fCity = CityListViewDialogFragment.newInstance(iCity, new CityAndAreaDialogFragment.OnItemClickedListener() {

                        @Override
                        public void onItemClicked(int id, String value) {
                            mSelectedCityID = id;
                            mCityEditText.setText("" + value);
                            if (NetworkUtil.isInternetAvailable(AddNewPatientWebViewActivity.this)) {
                                mAppointmentHelper.getAreaOfSelectedCity(mSelectedCityID);
                            } else {
                                if (mOfflineCityAndAreaMap == null)
                                    mOfflineCityAndAreaMap = AppDBHelper.getInstance(AddNewPatientWebViewActivity.this).doGetAreaDetails();

                                HashSet<String> areaList = mOfflineCityAndAreaMap.get(mCityEditText.getText().toString().toLowerCase().trim());
                                if (areaList != null) {
                                    // create an iterator
                                    Iterator iterator = areaList.iterator();

                                    // check values
                                    int count = 1;
                                    while (iterator.hasNext()) {
                                        IdAndValueDataModel i = new IdAndValueDataModel();
                                        i.setIdValue(String.valueOf(iterator.next()));
                                        i.setId(count);
                                        count = count + 1;
                                        mAreaListBasedOnCity.add(i);
                                    }
                                }
                            }
                        }
                    });
                    fCity.show(fm, "");
                }

                break;

            case R.id.areaEditText:
                if (mSelectedCityID == -1) {
                    CommonMethods.showToast(this, getString(R.string.plz_select_city));
                } else {

                    Bundle iArea = new Bundle();
                    iArea.putString(RescribeConstants.TITLE, getString(R.string.area));
                    iArea.putInt(RescribeConstants.CITY_ID, mSelectedCityID);
                    iArea.putParcelableArrayList(RescribeConstants.AREA_LIST, mAreaListBasedOnCity);

                    CityAndAreaDialogFragment fArea = CityAndAreaDialogFragment.newInstance(iArea, new CityAndAreaDialogFragment.OnItemClickedListener() {

                        @Override
                        public void onItemClicked(int id, String value) {
                            mSelectedAreaID = id;
                            mAreaEditText.setText("" + value);
                        }
                    });

                    fArea.show(fm, "");

                }
                break;
            case R.id.referredBy:
            case R.id.referredByTextInputLayout: {

                switch (mSelectedReferenceTypeName.toLowerCase()) {
                    case "":
                        CommonMethods.showToast(this, getString(R.string.err_msg_select_reffered_by));
                        break;
                    case "doctor": {
                        Bundle iArea = new Bundle();
                        iArea.putString(RescribeConstants.TITLE, getString(R.string.about_doctor));
                        DoctorListViewDialogFragment fArea = DoctorListViewDialogFragment.newInstance(iArea, new DoctorListViewDialogFragment.OnItemClickedListener() {

                            @Override
                            public void onItemClicked(int id, DoctorData data) {
                                mReferredEmail.setEnabled(false);
                                mReferredPhone.setEnabled(false);
                                mReferredBy.setText(data.getDocName());
                                mReferredEmail.setText(data.getDocEmail());
                                mReferredPhone.setText("" + data.getDocPhone());
                                mSelectedDoctorReference = data;
                            }
                        });

                        fArea.show(fm, "");
                    }
                    break;
                    case "patient": {
                        Bundle iArea = new Bundle();
                        iArea.putString(RescribeConstants.TITLE, getString(R.string.my_patients));
                        PatientListViewDialogFragment fArea = PatientListViewDialogFragment.newInstance(iArea, new PatientListViewDialogFragment.OnItemClickedListener() {

                            @Override
                            public void onItemClicked(int id, PatientList data) {
                                mReferredEmail.setEnabled(false);
                                mReferredPhone.setEnabled(false);
                                mSalutationSpinnerRef.setSelection(data.getSalutation());
                                mReferredBy.setText(data.getPatientName());
                                mReferredEmail.setText(data.getPatientEmail());
                                mReferredPhone.setText("" + data.getPatientPhone());
                                mSelectedPatientReference = data;
                                mSelectedSalutationOfPatientRef = data.getSalutation();

                            }
                        });

                        fArea.show(fm, "");
                    }
                    break;

                }
            }
            break;
        }
    }

    private void addOfflinePatient() {

        if (AppDBHelper.getInstance(mContext).addNewPatient(mAddedPatientListData) != -1) {

            CommonMethods.showToast(this, getString(R.string.patients_added_successfully));

            Bundle bundle = new Bundle();
            // this is done to replzce | with space, | used in case blank middle name.
            String replace = mAddedPatientListData.getPatientName().replace("|", "");
            bundle.putString(RescribeConstants.PATIENT_NAME, replace);

            //------------
            String patientInfo = "";
            if (!mAddedPatientListData.getAge().isEmpty() && !mAddedPatientListData.getGender().isEmpty())
                patientInfo = mAddedPatientListData.getAge() + " yrs - " + mAddedPatientListData.getGender();
            else if (!mAddedPatientListData.getAge().isEmpty())
                patientInfo = mAddedPatientListData.getAge() + " yrs";
            else if (!mAddedPatientListData.getGender().isEmpty())
                patientInfo = mAddedPatientListData.getGender();

            bundle.putString(RescribeConstants.PATIENT_INFO, patientInfo);
            //------------

            bundle.putInt(RescribeConstants.CLINIC_ID, mAddedPatientListData.getClinicId());
            bundle.putString(RescribeConstants.PATIENT_ID, String.valueOf(mAddedPatientListData.getPatientId()));
            bundle.putString(RescribeConstants.PATIENT_HOS_PAT_ID, String.valueOf(mAddedPatientListData.getHospitalPatId()));

            if (mDoOperationTaskID.equalsIgnoreCase(RescribeConstants.TASK_ADD_NEW_PATIENT)) {
                Intent intent = new Intent(this, PatientHistoryActivity.class);
                intent.putExtra(RescribeConstants.PATIENT_INFO, bundle);
                startActivity(intent);
            }
            finish();
        } else
            CommonMethods.showToast(mContext, "Failed to store");
    }

    @Override
    public void onBackPressed() {
        if (mMainParentScrollViewLayout.getVisibility() == View.VISIBLE) {
            finish();
        } else {
            if (mWebViewObject.canGoBack()) {
                mWebViewObject.goBack();
            } else {
                super.onBackPressed();
            }
        }

    }

    private void loadWebViewData(String url) {

        final CustomProgressDialog customProgressDialog = new CustomProgressDialog(this);
        customProgressDialog.show();

        if (url != null) {
            mWebViewObject.setVisibility(View.VISIBLE);

            WebSettings webSettings = mWebViewObject.getSettings();

            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setBuiltInZoomControls(true);
            webSettings.setSupportZoom(true);
            webSettings.setDefaultTextEncodingName("utf-8");

            mWebViewObject.setWebChromeClient(new WebChromeClient() {
                public void onProgressChanged(WebView view, int progress) {
                    // Activities and WebViews measure progress with different scales.
                    // The progress meter will automatically disappear when we reach 100%
                    setProgress(progress);
                    if (progress > 90)
                        customProgressDialog.dismiss();
                }
            });

            renderWebPage(url);

            mWebViewObject.loadUrl(url);
        }
    }

    // Custom method to render a web page
    protected void renderWebPage(String urlToRender) {
        mWebViewObject.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // Do something on page loading started
                Log.d(TAG + "Start", url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // Do something when page loading finished
                Log.d(TAG + "Finish", url);

                // https://drrescribe.com/app.html#/addpatientmobilesuccess/541170

                if (url.toLowerCase().contains(Config.ADD_NEW_PATIENT_WEB_URL_SUCCESS) && !isCalled) {
                    String[] split = url.split("/");
                    String patientId = split[split.length - 2];
                    String hospitalPatId = split[split.length - 1];
                    callNextActivity(patientId, hospitalPatId);
                }
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onLoadResource(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                Log.d(TAG, "ShouldOverride " + url);

                if (url.toLowerCase().contains(Config.ADD_NEW_PATIENT_WEB_URL_SUCCESS) && !isCalled) {

                    String[] split = url.split("/");
                    String patientId = split[split.length - 2];
                    String hospitalPatId = split[split.length - 1];

                    callNextActivity(patientId, hospitalPatId);
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }

    private void callNextActivity(String patientId, String hospitalPatId) {

        isCalled = true;

        Bundle b = new Bundle();
        b.putString(RescribeConstants.PATIENT_ID, patientId);
        b.putInt(RescribeConstants.CLINIC_ID, hospitalId);
        b.putString(RescribeConstants.PATIENT_HOS_PAT_ID, hospitalPatId);
        Intent intent = new Intent(this, PatientHistoryActivity.class);
        intent.putExtra(RescribeConstants.PATIENT_INFO, b);
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        startActivity(intent);
        finish();
    }

    private PatientList validate() {
        String message;
        PatientList patientList = null;
        String enter = getString(R.string.enter);
        String firstName = mFirstName.getText().toString().trim();
        String middleName = mMiddleName.getText().toString().trim();
        String lastName = mLastName.getText().toString().trim();
        String mob = mMobNo.getText().toString().trim();
        String age = mAge.getText().toString().trim();
        String refID = mReferenceID.getText().toString().trim();

        boolean enteredRefIDIsValid = isEnteredRefIDIsValid(refID);

        //---------
        String refName = mReferredBy.getText().toString().trim();
        String refMob = mReferredPhone.getText().toString().trim();
        String refEmail = mReferredEmail.getText().toString().trim();
        String refDetails = referredDetails.getText().toString().trim();
        //---------

        if (firstName.isEmpty()) {
            message = enter + getString(R.string.first_name_error).toLowerCase(Locale.US);
            CommonMethods.showToast(this, message);
        } else if (lastName.isEmpty()) {
            message = enter + getString(R.string.last_name_error);
            CommonMethods.showToast(this, message);
        } else if (mob.isEmpty() || mob.length() < 10) {
            message = enter + getString(R.string.enter_mobile_no_error);
            CommonMethods.showToast(this, message);
        } else if ((mob.trim().length() < 10) || !(mob.trim().startsWith("6") || mob.trim().startsWith("7") || mob.trim().startsWith("8") || mob.trim().startsWith("9"))) {
            message = getString(R.string.err_invalid_mobile_no);
            CommonMethods.showToast(this, message);
        } else if ((!age.isEmpty()) && Integer.parseInt(age) > 101) {
            message = getString(R.string.age_err_msg);
            CommonMethods.showToast(this, message);
        } else if (!refMob.isEmpty() && ((refMob.trim().length() < 10) || !(refMob.trim().startsWith("6") || refMob.trim().startsWith("7") || refMob.trim().startsWith("8") || refMob.trim().startsWith("9")))) {
            message = getString(R.string.err_invalid_ref_mobile_no);
            CommonMethods.showToast(this, message);
        } else if (!refEmail.isEmpty() && !CommonMethods.isValidEmail(refEmail)) {
            message = getString(R.string.err_ref_email_invalid);
            CommonMethods.showToast(this, message);
        } else if (!enteredRefIDIsValid) {
            message = getString(R.string.reference_id_input_err_msg);
            CommonMethods.showToast(this, message);
        } else {
            patientList = new PatientList();
            int id = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
            patientList.setPatientId(id);
            if (middleName.trim().length() == 0) {
                middleName = "|";
            }
            patientList.setPatientName(firstName + " " + middleName + " " + lastName);
            patientList.setSalutation(mSelectedSalutationOfPatient);
            patientList.setOutStandingAmount("0.00");
            patientList.setPatientImageUrl("");
            patientList.setPatientEmail("");
            patientList.setPatientPhone(mob);
            patientList.setAge(age);
            patientList.setRelation(mRelation);
            RadioButton viewById = (RadioButton) findViewById(mGenderRadioGroup.getCheckedRadioButtonId());
            if (viewById != null)
                patientList.setGender(viewById.getText().toString());
            else
                patientList.setGender("");

            patientList.setReferenceID(refID);
            patientList.setOfflinePatientSynced(false);
            patientList.setClinicId(hospitalId);
            patientList.setHospitalPatId(id + 1);
            patientList.setPatientCity(cityName);
            patientList.setPatientCityId(cityID);
            patientList.setPatientArea("" + mAreaEditText.getText().toString().trim());
            patientList.setCreationDate(CommonMethods.getCurrentTimeStamp(RescribeConstants.DATE_PATTERN.UTC_PATTERN));

            //------ reference details----
            if (mReferenceDetailLayout.getVisibility() == View.VISIBLE) {
                PatientReferenceDetails ref = new PatientReferenceDetails();
                ref.setName(refName);
                ref.setPhoneNumber(refMob);
                ref.setEmailId(refEmail);
                ref.setDescription(refDetails);

                ref.setReferredTypeId(String.valueOf(mSelectedReferenceTypeID));
                if (mSelectedReferenceTypeName.toLowerCase().equalsIgnoreCase("doctor")) {
                    if (mSelectedDoctorReference != null) {
                        ref.setDocId(String.valueOf(mSelectedDoctorReference.getId()));
                    } else {
                        ref.setName("");
                        ref.setPhoneNumber("");
                        ref.setEmailId("");
                        ref.setReferredTypeId("");
                    }
                } else if (mSelectedReferenceTypeName.toLowerCase().equalsIgnoreCase("patient")) {
                    if (mSelectedPatientReference != null) {
                        ref.setPatientId(String.valueOf(mSelectedPatientReference.getPatientId()));
                        ref.setSalutation(mSelectedSalutationOfPatientRef);
                    } else {
                        ref.setName("");
                        ref.setPhoneNumber("");
                        ref.setEmailId("");
                        ref.setReferredTypeId("");
                    }
                } else if (mSelectedReferenceTypeName.toLowerCase().equalsIgnoreCase("person")) {
                    ref.setDocId("");
                    ref.setPatientId("0");
                    ref.setSalutation(mSelectedSalutationOfPatientRef);
                } else {
                    ref.setDocId("");
                    ref.setPatientId("");
                    ref.setName("");
                    ref.setPhoneNumber("");
                    ref.setEmailId("");
                }
                patientList.setReferedDetails(ref);
            } else {

                //This is done, bzac wrong imeplementation at server
                PatientReferenceDetails ref = new PatientReferenceDetails();
                ref.setName("");
                ref.setPhoneNumber("");
                ref.setEmailId("");
                ref.setReferredTypeId("");
                patientList.setReferedDetails(ref);
            }
            //-----------
            //------ address details----
            if (mAddressDetailLayout.getVisibility() == View.VISIBLE) {
                PatientAddressDetails address = new PatientAddressDetails();
                address.setPatientAddress(mAddressLine.getText().toString().trim());
                address.setPatientState("" + mSelectedStateID);
                address.setPatientCity("" + mSelectedCityID);
                address.setPatientArea("" + mAreaEditText.getText().toString().trim());
                patientList.setAddressDetails(address);

                //THis is hack, to keep common city for online & offline patient.
                patientList.setPatientCity(mCityEditText.getText().toString().trim());
                patientList.setPatientCityId(mSelectedCityID);
            } else {

                //This is done, bzac wrong imeplementation at server
                PatientAddressDetails address = new PatientAddressDetails();
                address.setPatientAddress("");
                address.setPatientState("");
                address.setPatientCity("");
                address.setPatientArea("");
                patientList.setAddressDetails(address);
            }

        }

         /*else if (middleName.isEmpty()) {
            message = enter + getString(R.string.middle_name).toLowerCase(Locale.US);
            CommonMethods.showToast(this, message);
        }
         else if (age.isEmpty() ) {
            message = enter + " valid " + getString(R.string.age);
            CommonMethods.showToast(this, message);
        }*/
        return patientList;
    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        switch (mOldDataTag) {
            case RescribeConstants.TASK_ADD_NEW_PATIENT:
                SyncPatientsModel mSyncPatientsModel = (SyncPatientsModel) customResponse;
                Common common = mSyncPatientsModel.getCommon();
                if (common != null) {
                    CommonMethods.showToast(this, common.getStatusMessage());
                    if (common.getStatusCode().equals(SUCCESS)) {

                        //-----------
                        PatientUpdateDetail patientUpdateDetail = mSyncPatientsModel.getData().getPatientUpdateDetails().get(0);

                        mAddedPatientListData.setPatientId(patientUpdateDetail.getPatientId());
                        mAddedPatientListData.setHospitalPatId(patientUpdateDetail.getHospitalPatId());
                        mAddedPatientListData.setOfflinePatientSynced(true);
                        AppDBHelper.getInstance(mContext).addNewPatient(mAddedPatientListData);
                        //-----------

                        // start service if closed
                        Intent intentMQTT = new Intent(this, MQTTService.class);
                        ContextCompat.startForegroundService(mContext, intentMQTT);

                        Bundle bundle = new Bundle();
                        String replace = mAddedPatientListData.getPatientName().replace("|", "");

                        bundle.putString(RescribeConstants.PATIENT_NAME, replace);
                        //------------
                        String patientInfo = "";
                        if (!mAddedPatientListData.getAge().isEmpty() && !mAddedPatientListData.getGender().isEmpty())
                            patientInfo = mAddedPatientListData.getAge() + " yrs - " + mAddedPatientListData.getGender();
                        else if (!mAddedPatientListData.getAge().isEmpty())
                            patientInfo = mAddedPatientListData.getAge() + " yrs";
                        else if (!mAddedPatientListData.getGender().isEmpty())
                            patientInfo = mAddedPatientListData.getGender();

                        bundle.putString(RescribeConstants.PATIENT_INFO, patientInfo);
                        //------------
                        bundle.putInt(RescribeConstants.CLINIC_ID, mAddedPatientListData.getClinicId());
                        bundle.putString(RescribeConstants.PATIENT_ID, String.valueOf(mAddedPatientListData.getPatientId()));
                        bundle.putString(RescribeConstants.PATIENT_HOS_PAT_ID, String.valueOf(mAddedPatientListData.getHospitalPatId()));

                        if (mDoOperationTaskID.equalsIgnoreCase(RescribeConstants.TASK_GET_TIME_SLOTS_TO_BOOK_APPOINTMENT)) {

                            Intent intent = new Intent(this, SelectSlotToBookAppointmentBaseActivity.class);

                            //-----
                            String replacePatientName = mAddedPatientListData.getPatientName().replace("|", "");
                            mAddedPatientListData.setPatientName(replacePatientName);
                            //-----

                            intent.putExtra(RescribeConstants.PATIENT_INFO, mAddedPatientListData);
                            intent.putExtra(RescribeConstants.PATIENT_DETAILS, patientInfo);
                            intent.putExtra(RescribeConstants.IS_APPOINTMENT_TYPE_RESHEDULE, false);
                            intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                            startActivity(intent);

                            finish();
                        } else if (mDoOperationTaskID.equalsIgnoreCase(RescribeConstants.TASK_ADD_TO_WAITING_LIST)) {
                            callWaitingListApi(mAddedPatientListData);
                        } else {
                            Intent intent = new Intent(this, PatientHistoryActivity.class);
                            intent.putExtra(RescribeConstants.PATIENT_INFO, bundle);
                            startActivity(intent);
                            finish();
                        }

                    }
                }

                break;
            case RescribeConstants.TASK_ADD_TO_WAITING_LIST: {
                AddToWaitingListBaseModel addToWaitingListBaseModel = (AddToWaitingListBaseModel) customResponse;
                if (addToWaitingListBaseModel.getCommon().isSuccess()) {
                    if (addToWaitingListBaseModel.getAddToWaitingModel().getAddToWaitingResponse().get(0).getStatusMessage().toLowerCase().contains(getString(R.string.patients_added_successfully).toLowerCase()) || addToWaitingListBaseModel.getAddToWaitingModel().getAddToWaitingResponse().get(0).getStatusMessage().toLowerCase().contains(getString(R.string.added_to_waiting_list).toLowerCase())) {
                        Intent intent = new Intent();
                        intent.putExtra(RescribeConstants.PATIENT_ADDED, true);
                        intent.putExtra(RescribeConstants.LOCATION_ID, locationID);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }else
                    CommonMethods.showToast(this, addToWaitingListBaseModel.getCommon().getStatusMessage());
                    finish();
                }
            }
            break;
            case RescribeConstants.TASK_GET_AREA_TO_ADD_NEW_PATIENT: {
                AreaDetailsBaseModel areaModel = (AreaDetailsBaseModel) customResponse;
                AreaDetailsBaseModel.AreaDetailsDataModel areaDetailsDataModel = areaModel.getAreaDetailsDataModel();

                if (areaDetailsDataModel.getAreaDataList().isEmpty()) {
                    CommonMethods.showToast(this, getString(R.string.err_msg_no_area_found_for_city));

                } else {
                    ArrayList<AreaData> areaDataList = areaDetailsDataModel.getAreaDataList();
                    for (AreaData cityObj :
                            areaDataList) {
                        IdAndValueDataModel temp = new IdAndValueDataModel();
                        temp.setId(cityObj.getAreaId());
                        temp.setIdValue(cityObj.getAreaName());
                        mAreaListBasedOnCity.add(temp);
                    }

                }
                break;
            }
            case RescribeConstants.TASK_GET_REFERENCE_LIST: {
                ReferenceBaseModel referenceBaseModel = (ReferenceBaseModel) customResponse;
                if (referenceBaseModel.getCommon().isSuccess()) {
                    Log.e("getReferenceIdSetting", "" + referenceBaseModel.getReferenceData().getReferenceIdSetting());
                    if (referenceBaseModel.getReferenceData().getReferenceIdSetting()) {
                        mLayoutReferenceId.setVisibility(View.GONE);
                    }

                    referenceTypes = referenceBaseModel.getReferenceData().getReferenceTypeList();
                    List<String> referenceNames = new ArrayList<>();
                    referenceNames.add("Select reference");
                    for (ReferenceType referenceType : referenceTypes) {
                        referenceNames.add(referenceType.getType());
                    }
                    ArrayAdapter spinnerArrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, referenceNames);
                    mReferenceBySpinner.setAdapter(spinnerArrayAdapter);


                }
                // CommonMethods.showToast(this, referenceBaseModel.getCommon().getStatusMessage());

            }
            break;
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

    private void callWaitingListApi(PatientList mAddedPatientListData) {

        ArrayList<PatientAddToWaitingList> patientsListAddToWaitingLists = new ArrayList<>();
        PatientAddToWaitingList patientInfoListObject = new PatientAddToWaitingList();
        patientInfoListObject.setPatientName(mAddedPatientListData.getPatientName());
        patientInfoListObject.setPatientId(String.valueOf(mAddedPatientListData.getPatientId()));
        patientInfoListObject.setHospitalPatId(String.valueOf(mAddedPatientListData.getHospitalPatId()));
        patientsListAddToWaitingLists.add(patientInfoListObject);

        ArrayList<AddToList> addToArrayList = new ArrayList<>();
        AddToList addToListObject = new AddToList();

        //THis is hack, to keep common city for online & offline patient.
        if (mAddressDetailLayout.getVisibility() == View.VISIBLE) {
            addToListObject.setLocationDetails(mCityEditText.getText().toString().trim());
        } else {
            addToListObject.setLocationDetails(cityName);
        }

        addToListObject.setLocationId(Integer.parseInt(locationID));
        addToListObject.setPatientAddToWaitingList(patientsListAddToWaitingLists);
        addToArrayList.add(addToListObject);

        RequestToAddWaitingList requestForWaitingListPatients = new RequestToAddWaitingList();
        requestForWaitingListPatients.setAddToList(addToArrayList);
        requestForWaitingListPatients.setTime(CommonMethods.getCurrentTimeStamp(RescribeConstants.DATE_PATTERN.HH_mm_ss));
        requestForWaitingListPatients.setDate(CommonMethods.getCurrentDate(RescribeConstants.DATE_PATTERN.YYYY_MM_DD));
        requestForWaitingListPatients.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, this)));
        mAppointmentHelper.doAddToWaitingListFromMyPatients(requestForWaitingListPatients);
    }

    private void showDialogToSelectOption() {
        final String[] singleChoiceItems = getResources().getStringArray(R.array.add_patient_options);
        AlertDialog show = new AlertDialog.Builder(this, R.style.addNewPatientDialogCustomTheme)
                .setTitle(getString(R.string.plz_select_option))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doAddNewPatientOnSelectedOption();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setSingleChoiceItems(singleChoiceItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAddNewPatientSelectedOption = which;
                    }
                })
                .show();
    }

    private void doSpinnerItemSelectedListener() {

        mReferenceBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e("position", "" + position);

                if (position != 0) {

                    ReferenceType type = referenceTypes.get((position - 1));

                    Log.e("type_id", "" + type.getId());
                    Log.e("type_name", "" + type.getType());
                    mSelectedReferenceTypeID = type.getId();
                    mSelectedReferenceTypeName = type.getType();

                    if (type.getType().equalsIgnoreCase("Patient")) {
                        mReferredBy.setClickable(true);
                        mReferredBy.setFocusable(false);
                        mReferredByTextInputLayout.setClickable(true);
                        mReferredByTextInputLayout.setFocusable(false);
                        mReferredEmail.setEnabled(false);
                        mReferredPhone.setEnabled(false);
                        mReferenceDetailPerson.setVisibility(View.VISIBLE);
                        if (mSalutationSpinnerRef.getVisibility() != View.VISIBLE)
                            mSalutationSpinnerRef.setVisibility(View.VISIBLE);
                        mReferredDetailsTextInputLayout.setVisibility(View.GONE);

                    } else if (type.getType().equalsIgnoreCase("Doctor")) {
                        mReferredBy.setClickable(true);
                        mReferredBy.setFocusable(false);
                        mReferredByTextInputLayout.setClickable(true);
                        mReferredByTextInputLayout.setFocusable(false);

                        mReferredEmail.setEnabled(false);
                        mReferredPhone.setEnabled(false);
                        mReferenceDetailPerson.setVisibility(View.VISIBLE);
                        mSalutationSpinnerRef.setVisibility(View.GONE);
                        mReferredDetailsTextInputLayout.setVisibility(View.GONE);

                    } else if (type.getType().equalsIgnoreCase("Person")) {
                        mReferredBy.setClickable(true);
                        mReferredBy.setFocusable(true);
                        mReferredBy.setFocusableInTouchMode(true);
                        mReferredByTextInputLayout.setClickable(true);
                        mReferredByTextInputLayout.setFocusable(true);
                        mReferredByTextInputLayout.setFocusableInTouchMode(true);

                        mReferenceDetailPerson.setVisibility(View.VISIBLE);
                        if (mSalutationSpinnerRef.getVisibility() != View.VISIBLE)
                            mSalutationSpinnerRef.setVisibility(View.VISIBLE);
                        mReferredDetailsTextInputLayout.setVisibility(View.GONE);
                    } else {
                        mReferenceDetailPerson.setVisibility(View.GONE);
                        mReferredDetailsTextInputLayout.setVisibility(View.VISIBLE);

                    }

                    mReferredBy.setText("");
                    mReferredEmail.setText("");
                    mReferredPhone.setText("");
                } else {
                    Log.e("position else", "" + position);
                    mSelectedReferenceTypeID = 0;
                    mSelectedReferenceTypeName = "";
                    mReferenceDetailPerson.setVisibility(View.VISIBLE);
                    if (mSalutationSpinnerRef.getVisibility() != View.VISIBLE)
                        mSalutationSpinnerRef.setVisibility(View.VISIBLE);
                    mReferredDetailsTextInputLayout.setVisibility(View.GONE);
                    mReferredBy.setText("");
                    mReferredEmail.setText("");
                    mReferredPhone.setText("");
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSalutationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedSalutationOfPatient = position + 1;

                ArrayAdapter<String> spinnerArrayAdapter;

                switch (position) {
                    case 0:
                        genderMale.setChecked(true);
                        spinnerArrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.mr_relation_entries));
                        mRelationSpinner.setAdapter(spinnerArrayAdapter);
                        break;
                    case 1:
                    case 2:
                        genderFemale.setChecked(true);
                        spinnerArrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.mrs_relation_entries));
                        mRelationSpinner.setAdapter(spinnerArrayAdapter);
                        break;
                    case 3:
                        genderOther.setChecked(true);
                        spinnerArrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.other_relation_entries));
                        mRelationSpinner.setAdapter(spinnerArrayAdapter);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSalutationSpinnerRef.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedSalutationOfPatientRef = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mRelationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int Selectedrel = position;
                Log.e("position---", "" + position);
                List<String> listRelation;
                if (genderMale.isChecked()) {
                    listRelation = Arrays.asList(getResources().getStringArray(R.array.mr_relation_entries));
                    Log.e("mRelation MR", listRelation.get(Selectedrel));
                    mRelation = listRelation.get(Selectedrel);

                } else if (genderFemale.isChecked()) {
                    listRelation = Arrays.asList(getResources().getStringArray(R.array.mrs_relation_entries));
                    Log.e("mRelation MRS", listRelation.get(Selectedrel));
                    mRelation = listRelation.get(Selectedrel);
                } else if (genderOther.isChecked()) {
                    listRelation = Arrays.asList(getResources().getStringArray(R.array.other_relation_entries));
                    Log.e("mRelation Other", listRelation.get(Selectedrel));
                    mRelation = listRelation.get(Selectedrel);


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void doAddNewPatientOnSelectedOption() {
        switch (mAddNewPatientSelectedOption) {
            case 0:
                mDoOperationTaskID = RescribeConstants.TASK_ADD_NEW_PATIENT;
                break;
            case 1:
                mDoOperationTaskID = RescribeConstants.TASK_ADD_TO_WAITING_LIST;
                break;
            case 2:
                mDoOperationTaskID = RescribeConstants.TASK_GET_TIME_SLOTS_TO_BOOK_APPOINTMENT;
                break;
            default:
                CommonMethods.showToast(AddNewPatientWebViewActivity.this, getString(R.string.plz_select_option));
        }
        if (mDoOperationTaskID != null) {
            boolean internetAvailableCheck = NetworkUtil.isInternetAvailable(AddNewPatientWebViewActivity.this);
            if (internetAvailableCheck && mAddPatientOfflineSetting) {
                mAppointmentHelper.addNewPatient(mAddedPatientListData);
            } else {
                addOfflinePatient();
            }
        }
    }

}
