package com.rescribe.doctor.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.rescribe.doctor.R;
import com.rescribe.doctor.helpers.doctor_connect.DoctorConnectSearchHelper;
import com.rescribe.doctor.helpers.profile.ProfileHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.doctor_connect_search.DoctorConnectSearchBaseModel;
import com.rescribe.doctor.model.login.DocDetail;
import com.rescribe.doctor.model.profile_photo.ProfilePhotoResponse;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.singleton.Device;
import com.rescribe.doctor.singleton.RescribeApplication;
import com.rescribe.doctor.ui.customesViews.CircularImageView;
import com.rescribe.doctor.ui.customesViews.CustomProgressDialog;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.Config;
import com.rescribe.doctor.util.ImageUtils;
import com.rescribe.doctor.util.RescribeConstants;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.rescribe.doctor.util.ImageUtils.FILEPATH;

public class UpdateProfileActivity extends AppCompatActivity implements HelperResponse, ImageUtils.ImageAttachmentListener {
    @BindView(R.id.backButton)
    AppCompatImageView backButton;
    @BindView(R.id.webViewTitle)
    TextView mWebViewTitle;
    @BindView(R.id.profileImage)
    CircularImageView profileImage;
    @BindView(R.id.editName)
    EditText editName;
    @BindView(R.id.layoutName)
    LinearLayout layoutName;
    @BindView(R.id.layoutGender)
    LinearLayout layoutGender;
    @BindView(R.id.mobNo)
    EditText mobNo;
    @BindView(R.id.layoutContactNo)
    LinearLayout layoutContactNo;
    @BindView(R.id.editWebUrl)
    EditText editWebUrl;
    @BindView(R.id.layoutWebUrl)
    LinearLayout layoutWebUrl;

    @BindView(R.id.layoutSpeciality)
    LinearLayout layoutSpeciality;
    @BindView(R.id.editEducation)
    EditText editEducation;
    @BindView(R.id.layoutEducation)
    LinearLayout layoutEducation;
    @BindView(R.id.editAbout)
    EditText editAbout;
    @BindView(R.id.layoutAbout)
    LinearLayout layoutAbout;
    @BindView(R.id.btnAddPatientSubmit)
    Button btnAddPatientSubmit;
    @BindView(R.id.mainParentLayout)
    LinearLayout mainParentLayout;
    @BindView(R.id.mainParentScrollViewLayout)
    ScrollView mainParentScrollViewLayout;
    @BindView(R.id.webViewLayout)
    WebView webViewLayout;
    @BindView(R.id.editExprince)
    EditText editExprince;
    @BindView(R.id.layoutExprince)
    LinearLayout layoutExprince;
    @BindView(R.id.editSpeciality)
    EditText editSpeciality;
    @BindView(R.id.genderSpinner)
    Spinner genderSpinner;
    private Context mContext;


    private ImageUtils imageutils;
    private String Url = Config.BASE_URL + Config.UPLOAD_PROFILE_PHOTO;
    private String authorizationString;
    private Device device;
    private String docId;
    CustomProgressDialog mCustomProgressDialog;
    private String mselectedGender;
    ProfileHelper profileHelper;
    DoctorConnectSearchHelper doctorConnectSearchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_update_profile);
        ButterKnife.bind(this);
        initialize();
    }

    private void initialize() {
        mContext = UpdateProfileActivity.this;
        profileHelper = new ProfileHelper(this, this);
        doctorConnectSearchHelper = new DoctorConnectSearchHelper(this, this);
        mWebViewTitle.setText(getString(R.string.update_profile));
        ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
        doctorConnectSearchHelper.getDoctorSpecialityList();
        imageutils = new ImageUtils(this);
        device = Device.getInstance(UpdateProfileActivity.this);
        docId = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, mContext);
        authorizationString = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.AUTHTOKEN, mContext);


        String mDoctorName = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.USER_NAME, mContext);
        if (mDoctorName.contains("Dr. ")) {
            mDoctorName = mDoctorName.replace("Dr. ", "");
        }

        editName.setText(mDoctorName);
        String doctorDetails = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_INFO, this);
        final DocDetail docDetail = new Gson().fromJson(doctorDetails, DocDetail.class);
        editEducation.setText(docDetail.getDocDegree());
        editExprince.setText(docDetail.getDocExperience());
        editAbout.setText(docDetail.getDocInfo());
        editWebUrl.setText("");
        editSpeciality.setText(docDetail.getDocSpaciality());
        editWebUrl.setText(docDetail.getWebsite());
        mobNo.setText(docDetail.getDocPhone());
        mselectedGender = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.USER_GENDER, this);
        ;
        if (mselectedGender.equalsIgnoreCase("male")) {
            genderSpinner.setSelection(1);
        } else if (mselectedGender.equalsIgnoreCase("female")) {
            genderSpinner.setSelection(2);
        } else if (mselectedGender.equalsIgnoreCase("Transgender")) {
            genderSpinner.setSelection(3);
        }

        int color2 = mColorGenerator.getColor(mDoctorName);
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .width(Math.round(getResources().getDimension(R.dimen.dp40))) // width in px
                .height(Math.round(getResources().getDimension(R.dimen.dp40))) // height in px
                .endConfig()
                .buildRound(("" + mDoctorName.charAt(0)).toUpperCase(), color2);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.dontAnimate();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(true);
        requestOptions.placeholder(drawable);
        requestOptions.error(drawable);

        Glide.with(mContext)
                .load(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.PROFILE_PHOTO, mContext))
                .apply(requestOptions).thumbnail(0.5f)
                .into(profileImage);


        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                List<String> listgender = Arrays.asList(getResources().getStringArray(R.array.mr_gender_entries));
                Log.e("listgender", listgender.get(position));

                if (position != 0) {
                    mselectedGender = listgender.get(position);
                } else {


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    @OnClick({R.id.backButton, R.id.btnAddPatientSubmit, R.id.profileImage})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backButton:
                onBackPressed();
                break;
            case R.id.profileImage:
                imageutils.imagepicker(1);
                break;
            case R.id.btnAddPatientSubmit:
                break;

        }
    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        switch (mOldDataTag) {
            case RescribeConstants.TASK_DOCTOR_FILTER_DOCTOR_SPECIALITY_LIST:
                DoctorConnectSearchBaseModel doctorConnectSearchBaseModel = (DoctorConnectSearchBaseModel) customResponse;
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

    @Override
    public void image_attachment(int from, Bitmap file, Uri uri) {
        //file path is given below to generate new image as required i.e jpg format
        String path = Environment.getExternalStorageDirectory() + File.separator + "DrRescribe" + File.separator + "ProfilePhoto" + File.separator;
        imageutils.createImage(file, path, false);
        mCustomProgressDialog = new CustomProgressDialog(this);
        uploadProfileImage(FILEPATH);

    }


    public void uploadProfileImage(final String filePath) {
        mCustomProgressDialog.show();
        HashMap<String, String> mapHeaders = new HashMap<String, String>();
        mapHeaders.put(RescribeConstants.AUTHORIZATION_TOKEN, authorizationString);
        mapHeaders.put(RescribeConstants.DEVICEID, device.getDeviceId());
        mapHeaders.put(RescribeConstants.OS, device.getOS());
        mapHeaders.put(RescribeConstants.OSVERSION, device.getOSVersion());
        mapHeaders.put(RescribeConstants.DEVICE_TYPE, device.getDeviceType());
        mapHeaders.put("docid", String.valueOf(docId));

        SimpleMultiPartRequest profilePhotoUploadRequest = new SimpleMultiPartRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response profile photo", response);
                        //On Profile Image Upload on Server is completed that event is captured in this function.

                        String bodyAsString = response;
                        CommonMethods.Log("bodyAsString", bodyAsString);

                        ProfilePhotoResponse profilePhotoResponse = new Gson().fromJson(bodyAsString, ProfilePhotoResponse.class);
                        if (profilePhotoResponse.getCommon().isSuccess()) {
                            RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.PROFILE_PHOTO, profilePhotoResponse.getData().getDocImgUrl(), mContext);
                            Toast.makeText(mContext, profilePhotoResponse.getCommon().getStatusMessage(), Toast.LENGTH_SHORT).show();
                            RequestOptions requestOptions = new RequestOptions();
                            requestOptions.dontAnimate();
                            requestOptions.skipMemoryCache(true);
                            requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);

                            Glide.with(mContext)
                                    .load(filePath)
                                    .apply(requestOptions).thumbnail(0.5f)
                                    .into(profileImage);
                            mCustomProgressDialog.dismiss();
                        } else {
                            mCustomProgressDialog.dismiss();
                            Toast.makeText(UpdateProfileActivity.this, profilePhotoResponse.getCommon().getStatusMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mCustomProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getString(R.string.server_error), Toast.LENGTH_LONG).show();

            }
        });

        profilePhotoUploadRequest.setHeaders(mapHeaders);
        profilePhotoUploadRequest.addFile("docImage", filePath);
        RescribeApplication.getInstance().addToRequestQueue(profilePhotoUploadRequest);


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


}
