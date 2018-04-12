package com.rescribe.doctor.ui.activities.my_patients.add_new_patient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.rescribe.doctor.R;
import com.rescribe.doctor.helpers.doctor_patients.PatientList;
import com.rescribe.doctor.helpers.myappointments.AppointmentHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.patient.add_new_patient.AddNewPatient;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.ui.activities.my_patients.patient_history.PatientHistoryActivity;
import com.rescribe.doctor.ui.customesViews.CustomProgressDialog;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.Config;
import com.rescribe.doctor.util.NetworkUtil;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddNewPatientWebViewActivity extends AppCompatActivity implements HelperResponse {

    private static final String TAG = "AddPatient";
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

    private int hospitalId;
    private boolean isCalled = false;
    private String locationID;
    private AppointmentHelper mAppointmentHelper;
    private String cityID;
    private int docID;
    private String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.add_new_patient);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getBundleExtra(RescribeConstants.PATIENT_DETAILS);
        hospitalId = extras.getInt(RescribeConstants.CLINIC_ID);
        locationID = extras.getString(RescribeConstants.LOCATION_ID);
        cityID = extras.getString(RescribeConstants.CITY_ID);
        cityName = extras.getString(RescribeConstants.CITY_NAME);
        docID = Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, this));

        String urlData = Config.ADD_NEW_PATIENT_WEB_URL + docID + "/" +
                hospitalId + "/" + locationID + "/" + cityID;

        mWebViewTitle.setText(getString(R.string.new_patients));

        mAppointmentHelper = new AppointmentHelper(this, this);

        if (NetworkUtil.isInternetAvailable(this)) {
            mWebViewObject.setVisibility(View.VISIBLE);
            mMainParentScrollViewLayout.setVisibility(View.GONE);
            loadWebViewData(urlData);
        } else {
            CommonMethods.showToast(this, getString(R.string.add_patient_offline));
            mWebViewObject.setVisibility(View.GONE);
            mMainParentScrollViewLayout.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.backButton, R.id.btnAddPatientSubmit})
    public void back(View view) {
        switch (view.getId()) {
            case R.id.backButton:
                if (mMainParentScrollViewLayout.getVisibility() == View.VISIBLE) {
                    finish();
                } else {
                    if (mWebViewObject.canGoBack()) {
                        mWebViewObject.goBack();
                    } else {
                        super.onBackPressed();
                    }
                }

                break;
            case R.id.btnAddPatientSubmit:
                PatientList validate = validate();
                if (validate != null) {
                    mAppointmentHelper.addNewPatient(validate);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {

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

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {

        PatientList customResponse1 = (PatientList) customResponse;
        //todo : pending next work
        Bundle b = new Bundle();
        b.putString(RescribeConstants.PATIENT_NAME, customResponse1.getPatientName());
        b.putString(RescribeConstants.PATIENT_INFO, "" + customResponse1.getAge());
        b.putInt(RescribeConstants.CLINIC_ID, customResponse1.getClinicId());
        b.putString(RescribeConstants.PATIENT_ID, String.valueOf(customResponse1.getPatientId()));
        b.putString(RescribeConstants.PATIENT_HOS_PAT_ID, String.valueOf(customResponse1.getHospitalPatId()));
        Intent intent = new Intent(this, PatientHistoryActivity.class);
        intent.putExtra(RescribeConstants.PATIENT_INFO, b);
        startActivity(intent);
        finish();
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

    private PatientList validate() {
        String message = null;
        PatientList a = null;
        String enter = getString(R.string.enter);
        String firstName = mFirstName.getText().toString();
        String middleName = mMiddleName.getText().toString();
        String lastName = mLastName.getText().toString();
        String mob = mMobNo.getText().toString();
        String age = mAge.getText().toString();
        String refID = mReferenceID.getText().toString();
        if (firstName.isEmpty()) {
            message = enter + getString(R.string.first_name).toLowerCase(Locale.US);
            CommonMethods.showToast(this, message);
        } else if (middleName.isEmpty()) {
            message = enter + getString(R.string.middle_name).toLowerCase(Locale.US);
            CommonMethods.showToast(this, message);
        } else if (lastName.isEmpty()) {
            message = enter + getString(R.string.last_name);
            CommonMethods.showToast(this, message);
        } else if (mob.isEmpty() || mob.length() < 10) {
            message = enter + getString(R.string.enter_mobile_no);
            CommonMethods.showToast(this, message);
        } else if ((mob.trim().length() < 10) || !(mob.trim().startsWith("7") || mob.trim().startsWith("8") || mob.trim().startsWith("9"))) {
            message = getString(R.string.err_invalid_mobile_no);
            CommonMethods.showToast(this, message);
        } else if (age.isEmpty()) {
            message = enter + getString(R.string.age);
            CommonMethods.showToast(this, message);
        } else {
            a = new PatientList();
            int id = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
            a.setPatientId(id);
            a.setPatientName(firstName + " " + middleName + " " + lastName);
            a.setSalutation(0);
            a.setOutStandingAmount("0.00");
            a.setPatientImageUrl("");
            a.setPatientEmail("");
            a.setPatientPhone(mob);
            a.setAge(age);
            RadioButton viewById = (RadioButton) findViewById(mGenderRadioGroup.getCheckedRadioButtonId());
            a.setGender(viewById.getText().toString());
            a.setOfflineReferenceID(refID);
            a.setPatientInsertedOffline(true);
            a.setOfflinePatientSynced(false);
            a.setClinicId(hospitalId);
            a.setHospitalPatId(hospitalId);
            a.setPatientCity(cityName);
            a.setOfflinePatientCreatedTimeStamp(CommonMethods.getCurrentTimeStamp(RescribeConstants.DATE_PATTERN.YYYY_MM_DD_HH_mm_ss));
        }
        return a;
    }

}
