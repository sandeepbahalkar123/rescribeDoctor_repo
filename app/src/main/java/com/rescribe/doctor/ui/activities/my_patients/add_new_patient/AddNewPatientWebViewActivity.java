package com.rescribe.doctor.ui.activities.my_patients.add_new_patient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.philliphsu.bottomsheetpickers.date.DatePickerDialog;
import com.rescribe.doctor.R;
import com.rescribe.doctor.helpers.database.AppDBHelper;
import com.rescribe.doctor.helpers.myappointments.AppointmentHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.Common;
import com.rescribe.doctor.model.CommonBaseModelContainer;
import com.rescribe.doctor.model.new_patient.BloodGroup;
import com.rescribe.doctor.model.new_patient.ReferenceBaseModel;
import com.rescribe.doctor.model.new_patient.ReferenceType;
import com.rescribe.doctor.model.new_patient.RegistrationField;
import com.rescribe.doctor.model.new_patient.ResponsePanAadharExist;
import com.rescribe.doctor.model.patient.add_new_patient.PatientDetail;
import com.rescribe.doctor.model.patient.add_new_patient.address_other_details.area_details.AreaData;
import com.rescribe.doctor.model.patient.add_new_patient.address_other_details.area_details.AreaDetailsBaseModel;
import com.rescribe.doctor.model.patient.add_new_patient.address_other_details.reference_details.DoctorData;
import com.rescribe.doctor.model.patient.doctor_patients.PatientAddressDetails;
import com.rescribe.doctor.model.patient.doctor_patients.PatientList;
import com.rescribe.doctor.model.patient.doctor_patients.PatientReferenceDetails;
import com.rescribe.doctor.model.patient.doctor_patients.sync_resp.PatientUpdateDetail;
import com.rescribe.doctor.model.patient.doctor_patients.sync_resp.SyncPatientsModel;
import com.rescribe.doctor.model.profile_photo.ProfilePhotoResponse;
import com.rescribe.doctor.model.waiting_list.new_request_add_to_waiting_list.AddToList;
import com.rescribe.doctor.model.waiting_list.new_request_add_to_waiting_list.PatientAddToWaitingList;
import com.rescribe.doctor.model.waiting_list.new_request_add_to_waiting_list.RequestToAddWaitingList;
import com.rescribe.doctor.model.waiting_list.response_add_to_waiting_list.AddToWaitingListBaseModel;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.services.MQTTService;
import com.rescribe.doctor.singleton.Device;
import com.rescribe.doctor.singleton.RescribeApplication;
import com.rescribe.doctor.ui.activities.book_appointment.SelectSlotToBookAppointmentBaseActivity;
import com.rescribe.doctor.ui.activities.my_patients.add_new_patient.dialog_fragment.CityAndAreaDialogFragment;
import com.rescribe.doctor.ui.activities.my_patients.add_new_patient.dialog_fragment.CityListViewDialogFragment;
import com.rescribe.doctor.ui.activities.my_patients.add_new_patient.dialog_fragment.DoctorListViewDialogFragment;
import com.rescribe.doctor.ui.activities.my_patients.add_new_patient.dialog_fragment.PatientListViewDialogFragment;
import com.rescribe.doctor.ui.activities.my_patients.add_new_patient.dialog_fragment.StateListViewDialogFragment;
import com.rescribe.doctor.ui.activities.my_patients.patient_history.PatientHistoryActivity;
import com.rescribe.doctor.ui.customesViews.CircularImageView;
import com.rescribe.doctor.ui.customesViews.CustomProgressDialog;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.Config;
import com.rescribe.doctor.util.ImageUtils;
import com.rescribe.doctor.util.NetworkUtil;
import com.rescribe.doctor.util.RescribeConstants;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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

import static com.rescribe.doctor.util.ImageUtils.FILEPATH;
import static com.rescribe.doctor.util.RescribeConstants.SUCCESS;

public class AddNewPatientWebViewActivity extends AppCompatActivity implements HelperResponse, DatePickerDialog.OnDateSetListener, ImageUtils.ImageAttachmentListener {

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
    @BindView(R.id.bloodGroupSpinner)
    Spinner bloodGroupSpinner;
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

    @BindView(R.id.layoutFirstName)
    LinearLayout layoutFirstName;
    @BindView(R.id.layoutMiddleName)
    LinearLayout layoutMiddleName;
    @BindView(R.id.layoutLastName)
    LinearLayout layoutLastName;
    @BindView(R.id.layoutContactNo)
    LinearLayout layoutContactNo;
    @BindView(R.id.layoutAltPhn)
    LinearLayout layoutAltPhn;
    @BindView(R.id.layoutDob)
    LinearLayout layoutDob;
    @BindView(R.id.layoutAge)
    LinearLayout layoutAge;

    @BindView(R.id.layoutGender)
    LinearLayout layoutGender;
    @BindView(R.id.layoutEmail)
    LinearLayout layoutEmail;
    @BindView(R.id.layoutBloodGroup)
    LinearLayout layoutBloodGroup;
    @BindView(R.id.layoutRelation)
    LinearLayout layoutRelation;
    @BindView(R.id.layoutPanNo)
    LinearLayout layoutPanNo;
    @BindView(R.id.layoutAadhaarNo)
    LinearLayout layoutAadhaarNo;

    @BindView(R.id.layoutRegisteredFor)
    LinearLayout layoutRegisteredFor;


    @BindView(R.id.dob)
    EditText dob;

    @BindView(R.id.panNo)
    EditText editPanNo;
    @BindView(R.id.aadhaarNo)
    EditText editAadhaarNo;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.alt_phn)
    EditText alt_phn;

    @BindView(R.id.pinCode)
    EditText pinCode;

    @BindView(R.id.registeredFor)
    EditText registeredFor;
    @BindView(R.id.addressLine2)
    EditText addressLine2;

    @BindView(R.id.TextInputLayoutMobileNo)
    TextInputLayout TextInputLayoutMobileNo;


    @BindView(R.id.patientProfileImage)
    CircularImageView profileImage;

    @BindView(R.id.checkboxDeceasedPatient)
    CheckBox checkboxDeceasedPatient;
    @BindView(R.id.layoutDeceasedPatient)
    LinearLayout layoutDeceasedPatient;

    @BindView(R.id.layoutIsPatientNoMore)
    LinearLayout layoutIsPatientNoMore;


    int mSelectedSalutationOfPatient = 1;
    String mSalutationNameOfPatient = "Mr.";
    int mSelectedSalutationOfPatientRef = 1;
    int mSelectedStateID = -1;
    int mSelectedCityID = -1;
    String mSelectedAreaID = "0";
    int mSelectedBloodGroupID = -1;
    List<ReferenceType> referenceTypes;
    List<BloodGroup> bloodGroups;


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
    private PatientDetail patientDetail;
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
    private Boolean isReferenceIdSetting = false;
    private ImageUtils imageutils;
    CustomProgressDialog mCustomProgressDialog;
    private String authorizationString;
    private Device device;
    private String docId;
    String panNumber;
    private String aadharNo;
    private boolean isPanValid = false;
    private boolean isAadharValid = false;
    private boolean isMobileNoMandatory = true;
    private boolean isReferenceNo = false;
    List<String> bloodGroupNames;
    List<String> listRelation;
    List<String> referenceNames;
    PatientList editPatientData = null;
    private boolean isPatientDead = false;
    ArrayAdapter<String> spinnerRelationArrayAdapter;

    public static boolean isEnteredRefIDIsValid(String str) {
        boolean isValid = false;
        if (str.isEmpty()) {
            return true;
        } else {
            String expression = "^[a-z_A-Z0-9]*$";
            Pattern pattern = Pattern.compile(expression);
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
    }

    @SuppressLint("CheckResult")
    private void initialize() {
        mAppointmentHelper = new AppointmentHelper(this, this);
        mContext = this;
        imageutils = new ImageUtils(this);
        authorizationString = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.AUTHTOKEN, AddNewPatientWebViewActivity.this);
        device = Device.getInstance(AddNewPatientWebViewActivity.this);
        docId = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, mContext);
        editPanNo.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        //------------
        listRelation = Arrays.asList(getResources().getStringArray(R.array.mr_relation_entries));
        setRelationSpinnerAdapter(listRelation);

        mActivityStartFrom = getIntent().getStringExtra(RescribeConstants.START_FROM);


        if (mActivityStartFrom.equalsIgnoreCase(RescribeConstants.WAITING_LIST) || mActivityStartFrom.equalsIgnoreCase(RescribeConstants.PATIENT_LIST)) {
            Bundle extras = getIntent().getBundleExtra(RescribeConstants.PATIENT_DETAILS);
            hospitalId = extras.getInt(RescribeConstants.CLINIC_ID);
            locationID = extras.getString(RescribeConstants.LOCATION_ID);
            cityID = extras.getInt(RescribeConstants.CITY_ID);
            cityName = extras.getString(RescribeConstants.CITY_NAME);
            mWebViewTitle.setText(getString(R.string.patient_registration));
            genderMale.setChecked(true);

        } else if (mActivityStartFrom.equalsIgnoreCase(RescribeConstants.EDIT_PATIENT)) {
            editPatientData = getIntent().getExtras().getParcelable(RescribeConstants.PATIENT_DETAILS);
            hospitalId = editPatientData.getClinicId();
            cityID = editPatientData.getPatientCityId();
            cityName = editPatientData.getPatientCity();
            mSubmit.setText(getString(R.string.update));
            mWebViewTitle.setText(getString(R.string.patient_update));
        }

        int docID = Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, this));
        mAppointmentHelper.getReferenceList(hospitalId, docID);
        Log.e("CLINIC_ID", "--" + hospitalId);

        //--------
        String urlData = Config.ADD_NEW_PATIENT_WEB_URL + docID + "/" +
                hospitalId + "/" + locationID + "/" + cityID;


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

        }

        if (!NetworkUtil.getConnectivityStatusBoolean(mContext)) {
            TextInputLayoutMobileNo.setHint(getString(R.string.enter_mobile_no));
        }
    }

    private void setRelationSpinnerAdapter(List<String> listRelation) {
        spinnerRelationArrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, listRelation);
        mRelationSpinner.setAdapter(spinnerRelationArrayAdapter);
    }

    private void enableDisableView(View view, boolean enabled) {
        view.setEnabled(enabled);

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;

            for (int idx = 0; idx < group.getChildCount(); idx++) {
                enableDisableView(group.getChildAt(idx), enabled);
            }
        }
    }

    private void setEditPatientData(PatientList patientList) {

        if (patientList.isDead()) {
            enableDisableView(mainParentLayout, false);
            layoutIsPatientNoMore.setVisibility(View.VISIBLE);
            checkboxDeceasedPatient.setChecked(true);
            mSubmit.setVisibility(View.GONE);
        }

        isPatientDead = patientList.isDead();
        ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
        String patName = patientList.getPatientName();
        String patientProfileImage = patientList.getPatientImageUrl();

        int color2 = mColorGenerator.getColor(patName);
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .width(Math.round(getResources().getDimension(R.dimen.dp40))) // width in px
                .height(Math.round(getResources().getDimension(R.dimen.dp40))) // height in px
                .endConfig()
                .buildRound(("" + patName.charAt(0)).toUpperCase(), color2);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.dontAnimate();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(true);
        requestOptions.placeholder(drawable);
        requestOptions.error(drawable);
        Glide.with(mContext)
                .load(patientProfileImage)
                .apply(requestOptions).thumbnail(0.5f)
                .into(profileImage);

        if (patientList.getSalutation() == 0) {
            mSalutationSpinner.setSelection(0);
            genderMale.setChecked(true);
            listRelation = Arrays.asList(getResources().getStringArray(R.array.mr_relation_entries));
            setRelationSpinnerAdapter(listRelation);
        } else if (patientList.getSalutation() == 1) {
            mSalutationSpinner.setSelection(0);
            genderMale.setChecked(true);
            listRelation = Arrays.asList(getResources().getStringArray(R.array.mr_relation_entries));
            setRelationSpinnerAdapter(listRelation);
        } else if (patientList.getSalutation() == 2) {
            mSalutationSpinner.setSelection(1);
            genderFemale.setChecked(true);
            listRelation = Arrays.asList(getResources().getStringArray(R.array.mrs_relation_entries));
            setRelationSpinnerAdapter(listRelation);
        } else if (patientList.getSalutation() == 3) {
            mSalutationSpinner.setSelection(2);
            genderFemale.setChecked(true);
            listRelation = Arrays.asList(getResources().getStringArray(R.array.mrs_relation_entries));
            setRelationSpinnerAdapter(listRelation);
        } else if (patientList.getSalutation() == 4) {
            mSalutationSpinner.setSelection(3);
            genderOther.setChecked(true);
            listRelation = Arrays.asList(getResources().getStringArray(R.array.other_relation_entries));
            setRelationSpinnerAdapter(listRelation);
        }


        String fName = patientList.getPatientFName();
        String mName = patientList.getPatientMName();
        String lName = patientList.getPatientLName();
        if (fName == null || lName == null) {
            String fullName = patientList.getPatientName();
            String[] separated = fullName.split(" ");
            if (separated.length == 3) {
                fName = separated[0];
                mName = separated[1];
                lName = separated[2];
            } else if (separated.length == 2) {
                fName = separated[0];
                mName = "";
                lName = separated[1];
            }

        }


        mFirstName.setText(CommonMethods.toCamelCase(fName));
        mMiddleName.setText(CommonMethods.toCamelCase(mName));
        mLastName.setText(CommonMethods.toCamelCase(lName));


        mMobNo.setText(patientList.getPatientPhone());
        alt_phn.setText(patientList.getPatAltPhoneNumber());
        mAge.setText(patientList.getAge());
        email.setText(patientList.getPatientEmail());
        editPanNo.setText(patientList.getPanNumber());
        editAadhaarNo.setText(patientList.getAadharNumber());

        if (patientList.getPanNumber() != null && !patientList.getPanNumber().isEmpty()) {
            isPanValid = true;
        }
        if (patientList.getAadharNumber() != null && !patientList.getAadharNumber().isEmpty()) {
            isAadharValid = true;
        }

        if (patientList.getReferenceID() != null && !patientList.getReferenceID().isEmpty()) {
            isReferenceNo = true;
        }
        if (patientList.getBloodGroup() != null && !patientList.getBloodGroup().isEmpty()) {
            int bloodGroupPos = 0;
            for (int i = 0; i < bloodGroupNames.size(); i++) {
                if (bloodGroupNames.get(i).equalsIgnoreCase(patientList.getBloodGroup())) {
                    bloodGroupPos = i;
                }

            }

            BloodGroup bloodGroup = bloodGroups.get(bloodGroupPos - 1);
            mSelectedBloodGroupID = bloodGroup.getId();
            bloodGroupSpinner.setSelection(bloodGroupPos);
        }
        mReferenceID.setText(patientList.getReferenceID());
        registeredFor.setText(patientList.getRegisterFor());

        if (patientList.getGender().equalsIgnoreCase("male")) {
            genderMale.setChecked(true);
            listRelation = Arrays.asList(getResources().getStringArray(R.array.mr_relation_entries));
            setRelationSpinnerAdapter(listRelation);
            mSalutationSpinner.setSelection(0);
        } else if (patientList.getGender().equalsIgnoreCase("female")) {
            genderFemale.setChecked(true);
            listRelation = Arrays.asList(getResources().getStringArray(R.array.mrs_relation_entries));
            setRelationSpinnerAdapter(listRelation);
            mSalutationSpinner.setSelection(1);

        } else if (patientList.getGender().equalsIgnoreCase("transgender")) {
            genderOther.setChecked(true);
            listRelation = Arrays.asList(getResources().getStringArray(R.array.other_relation_entries));
            setRelationSpinnerAdapter(listRelation);
            mSalutationSpinner.setSelection(3);

        } else {
            genderMale.setChecked(true);
            listRelation = Arrays.asList(getResources().getStringArray(R.array.mr_relation_entries));
            setRelationSpinnerAdapter(listRelation);
            mSalutationSpinner.setSelection(0);
        }


        if (patientList.getRelation() != null && !patientList.getRelation().isEmpty()) {
            for (int i = 0; i < listRelation.size(); i++) {
                if (listRelation.get(i).toLowerCase().equalsIgnoreCase(patientList.getRelation())) {
                    mRelationSpinner.setSelection(i);
                }
            }
            mRelation = patientList.getRelation();
        }

        mAddressLine.setText(CommonMethods.toCamelCase(patientList.getAddressDetails().getPatientAddress()));
        addressLine2.setText(CommonMethods.toCamelCase(patientList.getAddressDetails().getPatientAddress2()));
        pinCode.setText(patientList.getAddressDetails().getPinCode());
        mStateEditText.setText(patientList.getAddressDetails().getPatientStateName());
        mSelectedStateID = patientList.getAddressDetails().getPatientState();
        mCityEditText.setText(patientList.getAddressDetails().getPatientCityName());
        mSelectedCityID = patientList.getAddressDetails().getPatientCity();
        mAreaEditText.setText(patientList.getAddressDetails().getPatientAreaName());
        mSelectedAreaID = patientList.getAddressDetails().getPatientArea();


        if (patientList.getReferedDetails().getReferredTypeId() != 0) {
            for (int i = 0; i < referenceTypes.size(); i++) {
                if (referenceTypes.get(i).getId() == patientList.getReferedDetails().getReferredTypeId()) {
                    mReferenceBySpinner.setSelection(i + 1);
                }

            }
            mSelectedReferenceTypeID = patientList.getReferedDetails().getReferredTypeId();
        }

        if (patientList.getReferedDetails().getReferredTypeId() == 1)
            mSalutationSpinnerRef.setVisibility(View.GONE);


        if (patientList.getReferedDetails().getReferredTypeId() == 2 || patientList.getReferedDetails().getReferredTypeId() == 3) {
            if (patientList.getReferedDetails().getSalutation() == 1)
                mSalutationSpinnerRef.setSelection(0);
            else if (patientList.getSalutation() == 2)
                mSalutationSpinnerRef.setSelection(1);
            else if (patientList.getSalutation() == 3)
                mSalutationSpinnerRef.setSelection(2);
        }

        mReferredBy.setText(patientList.getReferedDetails().getName());
        mReferredPhone.setText(patientList.getReferedDetails().getPhoneNumber());
        mReferredEmail.setText(patientList.getReferedDetails().getEmailId());
        referredDetails.setText(patientList.getReferedDetails().getDescription());

        checkboxDeceasedPatient.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    showSetDeadDialog();
                } else {
                    isPatientDead = false;
                }
            }
        });
    }

    private void showSetDeadDialog() {
        // closeDrawer();
        final Dialog dialog = new Dialog(mContext);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_exit);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        TextView textView = dialog.findViewById(R.id.textview_sucess);
        textView.setText(R.string.deceased_patient_dialog_text);
        dialog.findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                isPatientDead = true;
            }
        });
        dialog.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                isPatientDead = false;
                checkboxDeceasedPatient.setChecked(false);
            }
        });

        dialog.show();

    }

    private void validateAadharCardNo() {
        editAadhaarNo.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable editable) {


                if (editable.length() == 12) {
                    String s = editable.toString(); // get your editext value here
                    Pattern pattern = Pattern.compile("^[0-9]{12}$");
                    Matcher matcher = pattern.matcher(s);
                    // Check if pattern matches
                    if (matcher.matches()) {
                        aadharNo = editable.toString();
                        mAppointmentHelper.checkAadharCardNo(aadharNo);
                        isAadharValid = true;
                    } else {
                        isAadharValid = false;
                        Toast.makeText(AddNewPatientWebViewActivity.this, getString(R.string.plz_enter_your_correct_aadhar_num), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }
        });
    }

//    private void referenceNo() {
//
//
//        mReferenceID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                if (hasFocus) {
//                } else {
//                    String refno = mReferenceID.getText().toString();
//                    if (!refno.isEmpty()) {
//                        if (isEnteredRefIDIsValid(refno)) {
//                            mAppointmentHelper.checkReferenceNo(refno);
//                        }
//                    }
//                }
//            }
//        });
//
//
//    }

    private void validatePanCardNo() {
        editPanNo.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 10) {
                    String s = editable.toString(); // get your editext value here
                    Pattern pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");
                    Matcher matcher = pattern.matcher(s);
                    // Check if pattern matches
                    if (matcher.matches()) {
                        isPanValid = true;
                        panNumber = editable.toString();
                        mAppointmentHelper.checkPanCardNo(panNumber);
                    } else {
                        isPanValid = false;
                        Toast.makeText(AddNewPatientWebViewActivity.this, getString(R.string.plz_enter_your_correct_pan_num), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }
        });
    }

    public void setGenderChangeListner() {

        mGenderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.genderMale:
                        mSalutationSpinner.setSelection(0);
                        listRelation = Arrays.asList(getResources().getStringArray(R.array.mr_relation_entries));
                        setRelationSpinnerAdapter(listRelation);
                        break;
                    case R.id.genderFemale:
                        mSalutationSpinner.setSelection(1);
                        listRelation = Arrays.asList(getResources().getStringArray(R.array.mrs_relation_entries));
                        setRelationSpinnerAdapter(listRelation);
                        break;
                    case R.id.genderOther:
                        mSalutationSpinner.setSelection(3);
                        listRelation = Arrays.asList(getResources().getStringArray(R.array.other_relation_entries));
                        setRelationSpinnerAdapter(listRelation);
                        break;
                }


            }
        });
    }

    @OnClick({R.id.backButton, R.id.btnAddPatientSubmit, R.id.stateEditText, R.id.cityEditText, R.id.areaEditText, R.id.referredBy, R.id.referredByTextInputLayout, R.id.dob, R.id.patientProfileImage})
    public void back(View view) {

        final FragmentManager fm = getFragmentManager();

        switch (view.getId()) {
            case R.id.backButton:
                onBackPressed();
                break;
            case R.id.btnAddPatientSubmit:
                addpatient();
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
                            if (id == 0) {
                                mSelectedAreaID = value;
                            } else
                                mSelectedAreaID = String.valueOf(id);
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
            case R.id.dob:
                Calendar now = Calendar.getInstance();
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                        this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.setAccentColor(getResources().getColor(R.color.tagColor));
                datePickerDialog.setMaxDate(Calendar.getInstance());
                datePickerDialog.show(this.getSupportFragmentManager(), "AddRecords");
                break;
            case R.id.patientProfileImage:
                imageutils.imagepicker(1);
                break;
        }
    }

    private void addpatient() {
        mAddedPatientListData = validate();
        if (mAddedPatientListData != null) {
            if (RescribeConstants.WAITING_LIST.equalsIgnoreCase(mActivityStartFrom)) {
                mAddNewPatientSelectedOption = 1;
                doAddNewPatientOnSelectedOption();
            } else if (RescribeConstants.EDIT_PATIENT.equalsIgnoreCase(mActivityStartFrom)) {
                mAppointmentHelper.updatePatient(patientDetail);
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                //get image URI and set to create image of jpg format.
                Uri resultUri = result.getUri();
//                String path = Environment.getExternalStorageDirectory() + File.separator + "DrRescribe" + File.separator + "ProfilePhoto" + File.separator;
                imageutils.callImageCropMethod(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Exception error = result.getError();
            }
        } else {
            imageutils.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        imageutils.request_permission_result(requestCode, permissions, grantResults);
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
        patientDetail = null;
        String enter = getString(R.string.enter);
        String firstName = mFirstName.getText().toString().trim();
        String middleName = mMiddleName.getText().toString().trim();
        String lastName = mLastName.getText().toString().trim();
        String mob = mMobNo.getText().toString().trim();
        String age = mAge.getText().toString().trim();
        String refID = mReferenceID.getText().toString().trim();
        String emailID = email.getText().toString().trim();
        String panNo = editPanNo.getText().toString().trim();
        String aadharNo = editAadhaarNo.getText().toString().trim();
        String altPhnNo = alt_phn.getText().toString().trim();
        String pinCodeNo = pinCode.getText().toString().trim();

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
        } else if (isMobileNoMandatory && (mob.isEmpty() && mob.length() < 10)) {
            message = enter + getString(R.string.enter_mobile_no_error);
            CommonMethods.showToast(this, message);

        } else if (isMobileNoMandatory && ((mob.trim().length() < 10) || !(mob.trim().startsWith("6") || mob.trim().startsWith("7") || mob.trim().startsWith("8") || mob.trim().startsWith("9")))) {
            message = getString(R.string.err_invalid_mobile_no);
            CommonMethods.showToast(this, message);
        } else if (!isMobileNoMandatory && (!mob.isEmpty() && mob.length() < 10)) {
            message = enter + getString(R.string.enter_mobile_no_error);
            CommonMethods.showToast(this, message);

        } else if (!isMobileNoMandatory && (!mob.isEmpty() && !(mob.trim().startsWith("6") || mob.trim().startsWith("7") || mob.trim().startsWith("8") || mob.trim().startsWith("9")))) {
            message = getString(R.string.err_invalid_mobile_no);
            CommonMethods.showToast(this, message);
        } else if (!altPhnNo.isEmpty() && altPhnNo.length() < 10) {
            message = enter + getString(R.string.enter_mobile_no_alt_phn);
            CommonMethods.showToast(this, message);
        } else if (!altPhnNo.isEmpty() && !(altPhnNo.trim().startsWith("6") || altPhnNo.trim().startsWith("7") || altPhnNo.trim().startsWith("8") || altPhnNo.trim().startsWith("9"))) {
            message = getString(R.string.err_invalid_alter_mobile_no);
            CommonMethods.showToast(this, message);
        } else if ((!age.isEmpty()) && Integer.parseInt(age) > 101) {
            message = getString(R.string.age_err_msg);
            CommonMethods.showToast(this, message);
        } else if (!refMob.isEmpty() && ((refMob.trim().length() < 10) || !(refMob.trim().startsWith("6") || refMob.trim().startsWith("7") || refMob.trim().startsWith("8") || refMob.trim().startsWith("9")))) {
            message = getString(R.string.err_invalid_ref_mobile_no);
            CommonMethods.showToast(this, message);
        } else if (!emailID.isEmpty() && !CommonMethods.isValidEmail(emailID)) {
            message = getString(R.string.err_email_invalid);
            CommonMethods.showToast(this, message);
        } else if (!refEmail.isEmpty() && !CommonMethods.isValidEmail(refEmail)) {
            message = getString(R.string.err_ref_email_invalid);
            CommonMethods.showToast(this, message);
        } else if (!refID.isEmpty() && !enteredRefIDIsValid) {
            message = getString(R.string.reference_id_input_err_msg);
            CommonMethods.showToast(this, message);
        } else if (!refID.isEmpty() && !isReferenceNo) {
            mAppointmentHelper.checkReferenceNo(refID);
        } else if (!panNo.isEmpty() && ((panNo.trim().length() < 10))) {
            message = getString(R.string.plz_enter_your_correct_pan_num);
            CommonMethods.showToast(this, message);
        } else if ((!panNo.isEmpty() && !isPanValid)) {
            message = getString(R.string.plz_enter_your_correct_pan_num);
            CommonMethods.showToast(this, message);
        } else if (!aadharNo.isEmpty() && ((aadharNo.trim().length() < 12))) {
            message = getString(R.string.plz_enter_your_correct_aadhar_num);
            CommonMethods.showToast(this, message);
        } else if ((!aadharNo.isEmpty() && !isAadharValid)) {
            message = getString(R.string.plz_enter_your_correct_aadhar_num);
            CommonMethods.showToast(this, message);
        } else if (!pinCodeNo.isEmpty() && ((pinCodeNo.trim().length() < 6))) {
            message = getString(R.string.plz_enter_valid_pin_code);
            CommonMethods.showToast(this, message);
        } else {
            patientList = new PatientList();
            patientDetail = new PatientDetail();
            int id = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
            patientList.setPatientId(id);
            if (middleName.trim().length() == 0) {
                middleName = "";
            }
            patientList.setPatientName(firstName + " " + middleName + " " + lastName);
            patientDetail.setPatientName(firstName + " " + middleName + " " + lastName);
            patientDetail.setPatientFname(firstName);
            patientDetail.setPatientMname(middleName);
            patientDetail.setPatientLname(lastName);
            patientList.setSalutation(mSelectedSalutationOfPatient);
            patientDetail.setSalutation(mSelectedSalutationOfPatient);
            patientList.setOutStandingAmount("0.00");

            patientList.setPatientImageUrl("");
            patientList.setPatientEmail("");
            patientDetail.setPatientEmailId(emailID);
            patientList.setPatientPhone(mob);
            patientDetail.setPatientPhone(mob);
            patientList.setAge(age);
            patientDetail.setPatientAge(age);
            patientList.setRelation(mRelation);
            patientDetail.setRelation(mRelation);

            RadioButton viewById = (RadioButton) findViewById(mGenderRadioGroup.getCheckedRadioButtonId());
            if (viewById != null) {
                patientList.setGender(viewById.getText().toString());
                patientDetail.setPatientGender(viewById.getText().toString());
            } else {
                patientList.setGender("Male");
                patientDetail.setPatientGender("Male");
            }


            List<String> listSalutation = Arrays.asList(getResources().getStringArray(R.array.salutation_entries));
            mSalutationNameOfPatient = listSalutation.get(mSelectedSalutationOfPatient - 1);

            patientList.setReferenceID(refID);
            patientDetail.setReferenceId(refID);
            patientList.setOfflinePatientSynced(false);
            patientList.setClinicId(hospitalId);
            patientDetail.setClinicId(hospitalId);
            patientList.setHospitalPatId(id + 1);
            patientList.setPatientCity(cityName);
            patientList.setPatientCityId(cityID);
            patientList.setPatientArea("" + mAreaEditText.getText().toString().trim());
            patientList.setCreationDate(CommonMethods.getCurrentTimeStamp(RescribeConstants.DATE_PATTERN.UTC_PATTERN));
            patientDetail.setPatientAltNumber(altPhnNo);
            patientDetail.setAdharNumber(aadharNo);
            patientDetail.setPanNumber(panNo);
            patientDetail.setBloodGroup(mSelectedBloodGroupID);
            patientDetail.setRegisterFor(registeredFor.getText().toString());

            if (editPatientData != null) {
                patientDetail.setDead(isPatientDead);
                patientDetail.setDocId(Integer.parseInt(docId));
                patientDetail.setPatientId(editPatientData.getPatientId());
                patientDetail.setHospitalPatId(editPatientData.getHospitalPatId());
            }

            //------ reference details----
            if (mReferenceDetailLayout.getVisibility() == View.VISIBLE) {
                PatientReferenceDetails ref = new PatientReferenceDetails();
                if (editPatientData != null) {
                    ref.setDetailedId(editPatientData.getReferedDetails().getDetailedId());
                }
                ref.setName(refName);
                ref.setPhoneNumber(refMob);
                ref.setEmailId(refEmail);
                ref.setDescription(refDetails);

                ref.setReferredTypeId(mSelectedReferenceTypeID);
                if (mSelectedReferenceTypeName.toLowerCase().equalsIgnoreCase("doctor")) {
                    if (mSelectedDoctorReference != null) {
                        ref.setDocId(String.valueOf(mSelectedDoctorReference.getId()));
                    } else {
                        ref.setName("");
                        ref.setPhoneNumber("");
                        ref.setEmailId("");
                        ref.setReferredTypeId(0);
                    }
                } else if (mSelectedReferenceTypeName.toLowerCase().equalsIgnoreCase("patient")) {
                    if (mSelectedPatientReference != null) {
                        ref.setPatientId(String.valueOf(mSelectedPatientReference.getPatientId()));
                        ref.setSalutation(mSelectedSalutationOfPatientRef);
                    } else {
                        ref.setName("");
                        ref.setPhoneNumber("");
                        ref.setEmailId("");
                        ref.setReferredTypeId(0);
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
                patientDetail.setReferedDetails(ref);
            } else {

                //This is done, bzac wrong imeplementation at server
                PatientReferenceDetails ref = new PatientReferenceDetails();
                ref.setName("");
                ref.setPhoneNumber("");
                ref.setEmailId("");
                ref.setReferredTypeId(0);
                patientList.setReferedDetails(ref);
                patientDetail.setReferedDetails(ref);
            }
            //-----------
            //------ address details----
            if (mAddressDetailLayout.getVisibility() == View.VISIBLE) {
                PatientAddressDetails address = new PatientAddressDetails();
                address.setPatientAddress(mAddressLine.getText().toString().trim());
                address.setPatientAddress2(addressLine2.getText().toString().trim());

                if (mSelectedStateID != -1)
                    address.setPatientState(mSelectedStateID);
                else
                    address.setPatientState(0);

                if (mSelectedCityID != -1)
                    address.setPatientCity(mSelectedCityID);
                else
                    address.setPatientCity(0);
                if (!mSelectedAreaID.equalsIgnoreCase("0"))
                    address.setPatientArea(mSelectedAreaID);
                else
                    address.setPatientArea(mSelectedAreaID);

                address.setPinCode(pinCodeNo);
                patientList.setAddressDetails(address);
                patientDetail.setPatientAddressDetails(address);

                //THis is hack, to keep common city for online & offline patient.
                patientList.setPatientCity(mCityEditText.getText().toString().trim());
                patientList.setPatientCityId(mSelectedCityID);
            } else {

                //This is done, bzac wrong imeplementation at server
                PatientAddressDetails address = new PatientAddressDetails();
                address.setPatientAddress("");
                address.setPatientAddress2("");
                address.setPatientState(0);
                address.setPatientCity(0);
                address.setPatientArea("0");
                address.setPinCode("");
                patientList.setAddressDetails(address);
                patientDetail.setPatientAddressDetails(address);
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

                        if (FILEPATH != null)
                            uploadProfileImage(FILEPATH, patientUpdateDetail.getPatientId());
                        // start service if closed
                        Intent intentMQTT = new Intent(this, MQTTService.class);
                        ContextCompat.startForegroundService(mContext, intentMQTT);

                        Bundle bundle = new Bundle();
                        String replace = mSalutationNameOfPatient + " " + mAddedPatientListData.getPatientName().replace("|", "");
                        if (mSalutationNameOfPatient.equalsIgnoreCase("other"))
                            replace = mAddedPatientListData.getPatientName().replace("|", "");
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
                    } else
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

                    isReferenceIdSetting = referenceBaseModel.getReferenceData().getReferenceIdSetting();


                    referenceTypes = referenceBaseModel.getReferenceData().getReferenceTypeList();
                    referenceNames = new ArrayList<>();
                    referenceNames.add("Select reference");
                    for (ReferenceType referenceType : referenceTypes) {
                        referenceNames.add(referenceType.getType());
                    }
                    ArrayAdapter spinnerArrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, referenceNames);
                    mReferenceBySpinner.setAdapter(spinnerArrayAdapter);

                    bloodGroups = referenceBaseModel.getReferenceData().getBloodGroups();
                    bloodGroupNames = new ArrayList<>();
                    bloodGroupNames.add("Select Blood Group");
                    for (BloodGroup bloodGroup : bloodGroups) {
                        if (!bloodGroup.getBloodGroup().equalsIgnoreCase("Select one"))
                            bloodGroupNames.add(bloodGroup.getBloodGroup());
                    }
                    ArrayAdapter spinnerArrayAdapterBloodGroup = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, bloodGroupNames);
                    bloodGroupSpinner.setAdapter(spinnerArrayAdapterBloodGroup);
                    initializeView(referenceBaseModel.getReferenceData().getRegistrationFieldList());

                    if (mActivityStartFrom.equalsIgnoreCase(RescribeConstants.EDIT_PATIENT))
                        setEditPatientData(editPatientData);


                    validatePanCardNo();
                    validateAadharCardNo();
                   // referenceNo();
                    doSpinnerItemSelectedListener();
                    setGenderChangeListner();


                }
                // CommonMethods.showToast(this, referenceBaseModel.getCommon().getStatusMessage());

            }
            break;
            case RescribeConstants.TASK_VALIDATE_PAN_NO: {
                ResponsePanAadharExist panAadharExist = (ResponsePanAadharExist) customResponse;
                if (panAadharExist.getCommon().isSuccess()) {

                    boolean isExists = panAadharExist.getResponseIsExist().isIsExists();
                    if (isExists) {
                        CommonMethods.showToast(mContext, "This Pan no is already exist");
                        isPanValid = false;
                    } else {
                        isPanValid = true;
                    }

                }

            }
            break;
            case RescribeConstants.TASK_VALIDATE_ADDHAR_NO: {
                ResponsePanAadharExist panAadharExist = (ResponsePanAadharExist) customResponse;
                if (panAadharExist.getCommon().isSuccess()) {

                    boolean isExists = panAadharExist.getResponseIsExist().isIsExists();
                    if (isExists) {
                        CommonMethods.showToast(mContext, "This Aadhar no is already exist");
                        isAadharValid = false;
                    } else {
                        isAadharValid = true;
                    }

                }

            }
            break;

            case RescribeConstants.TASK_VALIDATE_REFERENCE_NO: {
                ResponsePanAadharExist panAadharExist = (ResponsePanAadharExist) customResponse;
                if (panAadharExist.getCommon().isSuccess()) {

                    boolean isExists = panAadharExist.getResponseIsExist().isIsExists();
                    if (isExists) {
                        CommonMethods.showToast(mContext, "Reference Id already exists");
                        isReferenceNo = false;
                    } else {
                        isReferenceNo = true;
                        addpatient();
                    }

                }

            }
            break;
            case RescribeConstants.TASK_UPDATE_PATIENT: {
                CommonBaseModelContainer container = (CommonBaseModelContainer) customResponse;
                if (container.getCommonRespose().isSuccess()) {
                    CommonMethods.showToast(mContext, container.getCommonRespose().getStatusMessage());
                    if (FILEPATH != null)
                        uploadProfileImage(FILEPATH, editPatientData.getPatientId());
                    onBackPressed();
                } else {
                    CommonMethods.showToast(mContext, container.getCommonRespose().getStatusMessage());
                }


            }
            break;
        }
    }

    private void initializeView(List<RegistrationField> registrationFieldList) {
        if (!registrationFieldList.isEmpty()) {
            for (RegistrationField registrationField : registrationFieldList) {
                switch (registrationField.getFieldName()) {
                    case "first_name":
                        if (registrationField.isFieldValue())
                            layoutFirstName.setVisibility(View.VISIBLE);
                        else
                            layoutFirstName.setVisibility(View.GONE);
                        break;
                    case "middle_name":
                        if (registrationField.isFieldValue())
                            layoutMiddleName.setVisibility(View.VISIBLE);
                        else
                            layoutMiddleName.setVisibility(View.GONE);
                        break;

                    case "last_name":
                        if (registrationField.isFieldValue())
                            layoutLastName.setVisibility(View.VISIBLE);
                        else
                            layoutLastName.setVisibility(View.GONE);
                        break;

                    case "mobile":
                        if (registrationField.isFieldValue()) {
                            isMobileNoMandatory = registrationField.isMandatory();
                            layoutContactNo.setVisibility(View.VISIBLE);
                            if (registrationField.isMandatory()) {
                                TextInputLayoutMobileNo.setHint(getString(R.string.enter_mobile_no));
                            } else {
                                TextInputLayoutMobileNo.setHint(getString(R.string.enter_ref_mobile_no));
                            }
                        } else
                            layoutContactNo.setVisibility(View.GONE);
                        break;

                    case "alt_phn":
                        if (registrationField.isFieldValue())
                            layoutAltPhn.setVisibility(View.VISIBLE);
                        else
                            layoutAltPhn.setVisibility(View.GONE);
                        break;

                    case "dob":
                        if (registrationField.isFieldValue())
                            layoutAge.setVisibility(View.VISIBLE);
                        else
                            layoutAge.setVisibility(View.GONE);
                        break;

                    case "gender":
                        if (registrationField.isFieldValue())
                            layoutGender.setVisibility(View.VISIBLE);
                        else
                            layoutGender.setVisibility(View.GONE);
                        break;

                    case "email":
                        if (registrationField.isFieldValue())
                            layoutEmail.setVisibility(View.VISIBLE);
                        else
                            layoutEmail.setVisibility(View.GONE);
                        break;

                    case "blood_group":
                        if (registrationField.isFieldValue())
                            layoutBloodGroup.setVisibility(View.VISIBLE);
                        else
                            layoutBloodGroup.setVisibility(View.GONE);
                        break;

                    case "reference_id":
                        if (registrationField.isFieldValue()) {
                            if (!isReferenceIdSetting)
                                mLayoutReferenceId.setVisibility(View.VISIBLE);
                        } else
                            mLayoutReferenceId.setVisibility(View.GONE);
                        break;

                    case "address_Details":
                        if (registrationField.isFieldValue())
                            mAddressDetailLayout.setVisibility(View.VISIBLE);
                        else {
                            mAddressDetailLayout.setVisibility(View.GONE);
                        }
                        break;

                    case "refere_Setting":
                        if (registrationField.isFieldValue())
                            mReferenceDetailLayout.setVisibility(View.VISIBLE);
                        else {
                            mReferenceDetailLayout.setVisibility(View.GONE);
                        }
                        break;

                    case "registeredFor":
                        if (registrationField.isFieldValue())
                            layoutRegisteredFor.setVisibility(View.VISIBLE);
                        else
                            layoutRegisteredFor.setVisibility(View.GONE);
                        break;

                    case "pan_number":
                        if (registrationField.isFieldValue())
                            layoutPanNo.setVisibility(View.VISIBLE);
                        else
                            layoutPanNo.setVisibility(View.GONE);
                        break;

                    case "aadhaar_number":
                        if (registrationField.isFieldValue())
                            layoutAadhaarNo.setVisibility(View.VISIBLE);
                        else
                            layoutAadhaarNo.setVisibility(View.GONE);
                        break;

                    case "is_dead":
                        if (mActivityStartFrom.equalsIgnoreCase(RescribeConstants.EDIT_PATIENT)) {
                            if (registrationField.isFieldValue())
                                layoutDeceasedPatient.setVisibility(View.VISIBLE);
                            else
                                layoutDeceasedPatient.setVisibility(View.GONE);
                        }
                        break;


                }
            }
        } else {
            isMobileNoMandatory = true;
            TextInputLayoutMobileNo.setHint(getString(R.string.enter_mobile_no));
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

                    // mReferredBy.setText("");
                    // mReferredEmail.setText("");
                    // mReferredPhone.setText("");
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


                switch (position) {
                    case 0:
                        genderMale.setChecked(true);
                        listRelation = Arrays.asList(getResources().getStringArray(R.array.mr_relation_entries));
                        spinnerRelationArrayAdapter.notifyDataSetChanged();
                        break;
                    case 1:
                    case 2:
                        genderFemale.setChecked(true);
                        listRelation = Arrays.asList(getResources().getStringArray(R.array.mrs_relation_entries));
                        spinnerRelationArrayAdapter.notifyDataSetChanged();
                        break;
                    case 3:
                        genderOther.setChecked(true);
                        listRelation = Arrays.asList(getResources().getStringArray(R.array.other_relation_entries));
                        spinnerRelationArrayAdapter.notifyDataSetChanged();
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


        bloodGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    BloodGroup bloodGroup = bloodGroups.get(position - 1);
                    mSelectedBloodGroupID = bloodGroup.getId();
                } else {


                }
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
                mAppointmentHelper.addNewPatient(patientDetail);
            } else {
                addOfflinePatient();
            }
        }
    }

    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {

        String selectedDate = String.valueOf(year) + "/" + String.valueOf(monthOfYear + 1) + "/" + String.valueOf(dayOfMonth);
        dob.setText(selectedDate);
    }

    @Override
    public void image_attachment(int from, Bitmap file, Uri uri) {
        String path = Environment.getExternalStorageDirectory() + File.separator + "DrRescribe" + File.separator + "PatientProfilePhoto" + File.separator;
        imageutils.createImage(file, path, false);
        mCustomProgressDialog = new CustomProgressDialog(this);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.dontAnimate();
        requestOptions.skipMemoryCache(true);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);

        Glide.with(mContext)
                .load(FILEPATH)
                .apply(requestOptions).thumbnail(0.5f)
                .into(profileImage);
    }


    public void uploadProfileImage(final String filePath, Integer patientId) {
        if (mCustomProgressDialog != null)
            mCustomProgressDialog.show();

        HashMap<String, String> mapHeaders = new HashMap<String, String>();
        mapHeaders.put(RescribeConstants.AUTHORIZATION_TOKEN, authorizationString);
        mapHeaders.put(RescribeConstants.DEVICEID, device.getDeviceId());
        mapHeaders.put(RescribeConstants.OS, device.getOS());
        mapHeaders.put(RescribeConstants.OSVERSION, device.getOSVersion());
        mapHeaders.put(RescribeConstants.DEVICE_TYPE, device.getDeviceType());
        mapHeaders.put("docid", String.valueOf(docId));
        mapHeaders.put("patientId", String.valueOf(patientId));
        String Url = Config.BASE_URL + Config.PROFILE_UPLOAD;
        SimpleMultiPartRequest profilePhotoUploadRequest = new SimpleMultiPartRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response profile photo", response);
                        //On Profile Image Upload on Server is completed that event is captured in this function.

                        String bodyAsString = response;
                        CommonMethods.Log(TAG, bodyAsString);

                        ProfilePhotoResponse profilePhotoResponse = new Gson().fromJson(bodyAsString, ProfilePhotoResponse.class);
                        if (profilePhotoResponse.getCommon().isSuccess()) {
                            RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.PROFILE_PHOTO, profilePhotoResponse.getData().getDocImgUrl(), mContext);
                            Toast.makeText(AddNewPatientWebViewActivity.this, profilePhotoResponse.getCommon().getStatusMessage(), Toast.LENGTH_SHORT).show();
                            RequestOptions requestOptions = new RequestOptions();
                            requestOptions.dontAnimate();
                            requestOptions.skipMemoryCache(true);
                            requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);

                            Glide.with(mContext)
                                    .load(filePath)
                                    .apply(requestOptions).thumbnail(0.5f)
                                    .into(profileImage);
                            mCustomProgressDialog.dismiss();
                            FILEPATH = null;
                        } else {
                            mCustomProgressDialog.dismiss();
                            Toast.makeText(AddNewPatientWebViewActivity.this, profilePhotoResponse.getCommon().getStatusMessage(), Toast.LENGTH_SHORT).show();
                            FILEPATH = null;
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mCustomProgressDialog.dismiss();
                FILEPATH = null;
                Toast.makeText(getApplicationContext(), getString(R.string.server_error), Toast.LENGTH_LONG).show();

            }
        });

        profilePhotoUploadRequest.setHeaders(mapHeaders);
        profilePhotoUploadRequest.addFile("patImage", filePath);
        RescribeApplication.getInstance().addToRequestQueue(profilePhotoUploadRequest);


    }

}
