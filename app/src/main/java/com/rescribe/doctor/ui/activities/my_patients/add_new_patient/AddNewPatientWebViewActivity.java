package com.rescribe.doctor.ui.activities.my_patients.add_new_patient;

import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
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
import com.rescribe.doctor.model.patient.add_new_patient.address_other_details.area_details.AreaData;
import com.rescribe.doctor.model.patient.add_new_patient.address_other_details.area_details.AreaDetailsBaseModel;
import com.rescribe.doctor.model.patient.add_new_patient.address_other_details.reference_details.DoctorData;
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
import com.rescribe.doctor.ui.activities.my_patients.patient_history.PatientHistoryActivity;
import com.rescribe.doctor.ui.activities.waiting_list.WaitingMainListActivity;
import com.rescribe.doctor.ui.customesViews.CustomProgressDialog;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.Config;
import com.rescribe.doctor.util.NetworkUtil;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.rescribe.doctor.util.RescribeConstants.LOCATION_ID;
import static com.rescribe.doctor.util.RescribeConstants.SUCCESS;

public class AddNewPatientWebViewActivity extends AppCompatActivity implements HelperResponse {

    private static final String TAG = "AddPatient";


    private ArrayList<IdAndValueDataModel> mAreaListBasedOnCity = new ArrayList<>();

    public static final int ADD_PATIENT_REQUEST = 121;
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

    @BindView(R.id.genderRadioGroup)
    RadioGroup mGenderRadioGroup;
    //---------
    @BindView(R.id.addressLine)
    EditText mAddressLine;

    @BindView(R.id.addressDetailLayout)
    LinearLayout mAddressDetailLayout;
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
    @BindView(R.id.referredBy)
    EditText mReferredBy;
    @BindView(R.id.referredPhone)
    EditText mReferredPhone;
    @BindView(R.id.referredEmail)
    EditText mReferredEmail;
    @BindView(R.id.referenceDetailLayout)
    LinearLayout mReferenceDetailLayout;
    //-------
    @BindView(R.id.mainParentLayout)
    LinearLayout mainParentLayout;

    //---------
    private int hospitalId;
    private boolean isCalled = false;
    private String locationID;
    private int docID;
    private int cityID;
    private String cityName;
    private Context mContext;
    private boolean mAddPatientOfflineSetting;
    private PatientList mAddedPatientListData;
    //----
    private String mDoOperationTaskID;
    private int mAddNewPatientSelectedOption = -1;
    //------
    private AppointmentHelper mAppointmentHelper;


    //--------
    int mSelectedStateID = -1;
    int mSelectedCityID = -1;
    int mSelectedAreaID = -1;
    private int mSelectedReferenceTypeID = -1;
    private DoctorData mSelectedReferencePersonData;

    //--------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.add_new_patient);
        ButterKnife.bind(this);

        initialize();
        referenceBySpinnerConfig();
    }

    private void initialize() {
        mAppointmentHelper = new AppointmentHelper(this, this);
        mContext = this;

        //------------
        Bundle extras = getIntent().getBundleExtra(RescribeConstants.PATIENT_DETAILS);
        hospitalId = extras.getInt(RescribeConstants.CLINIC_ID);
        locationID = extras.getString(RescribeConstants.LOCATION_ID);
        cityID = extras.getInt(RescribeConstants.CITY_ID);
        cityName = extras.getString(RescribeConstants.CITY_NAME);
        docID = Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, this));
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
                showDialogToSelectOption();
                /* */
                break;
            case R.id.stateEditText:
                Bundle iState = new Bundle();
                iState.putString(RescribeConstants.TITLE, getString(R.string.state));

                PatientAddDetailsListViewDialogFragment fState = PatientAddDetailsListViewDialogFragment.newInstance(iState, new PatientAddDetailsListViewDialogFragment.OnItemClickedListener() {

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

                    PatientAddDetailsListViewDialogFragment fCity = PatientAddDetailsListViewDialogFragment.newInstance(iCity, new PatientAddDetailsListViewDialogFragment.OnItemClickedListener() {

                        @Override
                        public void onItemClicked(int id, String value) {
                            mSelectedCityID = id;
                            mCityEditText.setText("" + value);
                            if (NetworkUtil.isInternetAvailable(AddNewPatientWebViewActivity.this)) {
                                mAppointmentHelper.getAreaOfSelectedCity(mSelectedCityID);
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

                    if (!mAreaListBasedOnCity.isEmpty()) {
                        Bundle iArea = new Bundle();
                        iArea.putString(RescribeConstants.TITLE, getString(R.string.area));
                        iArea.putInt(RescribeConstants.CITY_ID, mSelectedCityID);
                        iArea.putParcelableArrayList(RescribeConstants.AREA_LIST, mAreaListBasedOnCity);

                        PatientAddDetailsListViewDialogFragment fArea = PatientAddDetailsListViewDialogFragment.newInstance(iArea, new PatientAddDetailsListViewDialogFragment.OnItemClickedListener() {

                            @Override
                            public void onItemClicked(int id, String value) {
                                mSelectedAreaID = id;
                                mAreaEditText.setText("" + value);
                            }
                        });

                        fArea.show(fm, "");
                    } else {
                        mAreaEditText.setFocusable(true);
                        mAddressAreaTextInputLayout.setFocusable(true);
                        mAreaEditText.setFocusableInTouchMode(true);
                        mAddressAreaTextInputLayout.setFocusableInTouchMode(true);
                    }
                }
                break;
            case R.id.referredBy:
            case R.id.referredByTextInputLayout: {

                Bundle iArea = new Bundle();
                iArea.putString(RescribeConstants.TITLE, getString(R.string.about_doctor));
                DoctorListViewDialogFragment fArea = DoctorListViewDialogFragment.newInstance(iArea, new DoctorListViewDialogFragment.OnItemClickedListener() {

                    @Override
                    public void onItemClicked(int id, DoctorData data) {
                        mReferredBy.setText(data.getDocName());
                        mReferredEmail.setText(data.getDocEmail());
                        mReferredPhone.setText(data.getDocPhone());

                        mSelectedReferencePersonData = data;
                    }
                });

                fArea.show(fm, "");
            }
            break;
        }
    }

    private void addOfflinePatient() {

        if (AppDBHelper.getInstance(mContext).addNewPatient(mAddedPatientListData) != -1) {
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
            Intent intent = new Intent(this, PatientHistoryActivity.class);
            intent.putExtra(RescribeConstants.PATIENT_INFO, bundle);
            startActivity(intent);
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

        if (firstName.isEmpty()) {
            message = enter + getString(R.string.first_name).toLowerCase(Locale.US);
            CommonMethods.showToast(this, message);
        } else if (lastName.isEmpty()) {
            message = enter + getString(R.string.last_name);
            CommonMethods.showToast(this, message);
        } else if (mob.isEmpty() || mob.length() < 10) {
            message = enter + getString(R.string.enter_mobile_no);
            CommonMethods.showToast(this, message);
        } else if ((mob.trim().length() < 10) || !(mob.trim().startsWith("6") || mob.trim().startsWith("7") || mob.trim().startsWith("8") || mob.trim().startsWith("9"))) {
            message = getString(R.string.err_invalid_mobile_no);
            CommonMethods.showToast(this, message);
        } else if ((!age.isEmpty()) && Integer.parseInt(age) > 101) {
            message = getString(R.string.age_err_msg);
            CommonMethods.showToast(this, message);
        } else if (!enteredRefIDIsValid) {
            message = getString(R.string.reference_id_input_err_msg);
            CommonMethods.showToast(this, message);
        } else if (mReferenceDetailLayout.getVisibility() == View.VISIBLE) {

            String refName = mReferredBy.getText().toString().trim();
            String refMob = mReferredPhone.getText().toString().trim();
            String refEmail = mReferredEmail.getText().toString().trim();

            if (refName.isEmpty()) {
                message = enter + getString(R.string.first_name).toLowerCase(Locale.US);
                CommonMethods.showToast(this, message);
            } else if ((refMob.trim().length() < 10) || !(refMob.trim().startsWith("6") || refMob.trim().startsWith("7") || refMob.trim().startsWith("8") || refMob.trim().startsWith("9"))) {
                message = getString(R.string.err_invalid_mobile_no);
                CommonMethods.showToast(this, message);
            } else if (!CommonMethods.isValidEmail(refEmail)) {
                message = getString(R.string.err_email_invalid);
                CommonMethods.showToast(this, message);
            }

        } else {
            patientList = new PatientList();
            int id = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
            patientList.setPatientId(id);
            if (middleName.trim().length() == 0) {
                middleName = "|";
            }
            patientList.setPatientName(firstName + " " + middleName + " " + lastName);
            patientList.setSalutation(0);
            patientList.setOutStandingAmount("0.00");
            patientList.setPatientImageUrl("");
            patientList.setPatientEmail("");
            patientList.setPatientPhone(mob);
            patientList.setAge(age);
            RadioButton viewById = (RadioButton) findViewById(mGenderRadioGroup.getCheckedRadioButtonId());
            if (viewById != null)
                patientList.setGender(viewById.getText().toString());
            else
                patientList.setGender("");


            patientList.setPatientAddress(mAddressLine.getText().toString().trim());

            patientList.setReferenceID(refID);
            patientList.setOfflinePatientSynced(false);
            patientList.setClinicId(hospitalId);
            patientList.setHospitalPatId(id + 1);
            patientList.setPatientCity(cityName);
            patientList.setPatientCityId(cityID);
            patientList.setCreationDate(CommonMethods.getCurrentTimeStamp(RescribeConstants.DATE_PATTERN.UTC_PATTERN));


            String refName = mReferredBy.getText().toString().trim();
            String refMob = mReferredPhone.getText().toString().trim();
            String refEmail = mReferredEmail.getText().toString().trim();

            //----------
            if (mReferenceDetailLayout.getVisibility() == View.VISIBLE) {
                PatientReferenceDetails ref = new PatientReferenceDetails();
                ref.setName(refName);
                ref.setPhoneNumber(Integer.parseInt(refMob));
                ref.setEmailId(refEmail);
                ref.setReferredTypeId(mSelectedReferenceTypeID);
                if (mSelectedReferenceTypeID == 1) {
                    ref.setDocId(mSelectedReferencePersonData.getId());
                    ref.setPatientId(0);
                } else if (mSelectedReferenceTypeID == 2) {
                    ref.setPatientId(mSelectedReferencePersonData.getId());
                    ref.setDocId(0);
                }
                //-----------

                patientList.setReferenceDetails(ref);
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

    public static boolean isEnteredRefIDIsValid(String str) {
        boolean isValid = false;
        if (str.isEmpty()) {
            return true;
        } else {
            String expression = "^[a-z_A-Z0-9]*$";
            CharSequence inputStr = str;
            Pattern pattern = Pattern.compile(expression);
            Matcher matcher = pattern.matcher(inputStr);
            if (matcher.matches()) {
                isValid = true;
            }
        }
        return isValid;
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
                        startService(intentMQTT);

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
                    if (addToWaitingListBaseModel.getAddToWaitingModel().getAddToWaitingResponse().get(0).getStatusMessage().toLowerCase().contains(getString(R.string.patients_added_to_waiting_list).toLowerCase()) || addToWaitingListBaseModel.getAddToWaitingModel().getAddToWaitingResponse().get(0).getStatusMessage().toLowerCase().contains(getString(R.string.added_to_waiting_list).toLowerCase())) {
                        Intent intent = new Intent(this, WaitingMainListActivity.class);
                        intent.putExtra(LOCATION_ID, locationID);
                        startActivity(intent);
                        finish();
                    }
                    CommonMethods.showToast(this, addToWaitingListBaseModel.getCommon().getStatusMessage());
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
        addToListObject.setLocationDetails(cityName);
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
                        switch (mAddNewPatientSelectedOption) {
                            case 0:
                                mAddedPatientListData = validate();
                                boolean internetAvailable = NetworkUtil.isInternetAvailable(AddNewPatientWebViewActivity.this);
                                if (mAddedPatientListData != null) {
                                    if (internetAvailable && mAddPatientOfflineSetting) {
                                        mDoOperationTaskID = RescribeConstants.TASK_ADD_NEW_PATIENT;
                                        mAppointmentHelper.addNewPatient(mAddedPatientListData);
                                    } else {
                                        addOfflinePatient();
                                    }
                                }
                                break;
                            case 1: {
                                mAddedPatientListData = validate();
                                boolean internetAvailableCheck = NetworkUtil.isInternetAvailable(AddNewPatientWebViewActivity.this);
                                if (mAddedPatientListData != null) {
                                    if (internetAvailableCheck && mAddPatientOfflineSetting) {
                                        //mDoOperationTaskID = RescribeConstants.TASK_GET_TIME_SLOTS_TO_BOOK_APPOINTMENT;
                                        mDoOperationTaskID = RescribeConstants.TASK_ADD_TO_WAITING_LIST;
                                        mAppointmentHelper.addNewPatient(mAddedPatientListData);
                                    } else {
                                        CommonMethods.showToast(AddNewPatientWebViewActivity.this, getString(R.string.add_patient_offline_and_book_apt));
                                    }
                                }
                            }
                            break;
                            case 2: {
                                mAddedPatientListData = validate();
                                boolean internetAvailableCheck = NetworkUtil.isInternetAvailable(AddNewPatientWebViewActivity.this);
                                if (mAddedPatientListData != null) {
                                    if (internetAvailableCheck && mAddPatientOfflineSetting) {
                                        //mDoOperationTaskID = RescribeConstants.TASK_GET_TIME_SLOTS_TO_BOOK_APPOINTMENT;
                                        mDoOperationTaskID = RescribeConstants.TASK_ADD_TO_WAITING_LIST;
                                        mAppointmentHelper.addNewPatient(mAddedPatientListData);
                                    } else {
                                        CommonMethods.showToast(AddNewPatientWebViewActivity.this, getString(R.string.add_patient_offline_and_book_apt));
                                    }
                                }
                            }
                            break;
                            default:
                                CommonMethods.showToast(AddNewPatientWebViewActivity.this, getString(R.string.plz_select_option));
                        }
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

    private void referenceBySpinnerConfig() {

        final String[] reffferdBy = getResources().getStringArray(R.array.reffferd_by);

        mReferenceBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedReferenceTypeID = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}
