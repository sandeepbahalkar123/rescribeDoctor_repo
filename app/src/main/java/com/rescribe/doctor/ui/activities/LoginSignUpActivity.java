package com.rescribe.doctor.ui.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.rescribe.doctor.R;
import com.rescribe.doctor.helpers.database.AppDBHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.login.DocDetail;
import com.rescribe.doctor.model.login.LoginModel;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.ui.fragments.login.LoginFragment;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by jeetal on 18/8/17.
 */
@RuntimePermissions
public class LoginSignUpActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener, HelperResponse {
    private final String TAG = this.getClass().getName();
    @BindView(R.id.container)
    FrameLayout container;
    Intent intent;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_signup_layout);
        ButterKnife.bind(this);
        LoginSignUpActivityPermissionsDispatcher.askToReadMessageWithCheck(LoginSignUpActivity.this);
        String key = CommonMethods.printKeyHash(LoginSignUpActivity.this);
        CommonMethods.Log(TAG, key);
        init();
    }

    private void init() {
        mContext = LoginSignUpActivity.this;
        //Fragment  login is loaded in LoginSignUpActivity , Facebook and google Login click is handled in LoginSignUpActivity
        LoginFragment loginFragment = new LoginFragment();
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, loginFragment);
        fragmentTransaction.commit();
    }

    @NeedsPermission(Manifest.permission.READ_SMS)
    public void askToReadMessage() {
        //Do nothing
    }

    @OnPermissionDenied({Manifest.permission.READ_SMS})
    void deniedReadSms() {
        //Do nothing
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LoginSignUpActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        // User can login through gmail or facebook
        if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_LOGIN)) {

            LoginModel receivedModel = (LoginModel) customResponse;
            if (receivedModel.getCommon().isSuccess()) {

                DocDetail docDetail = receivedModel.getDoctorLoginData().getDocDetail();
                String authToken = receivedModel.getDoctorLoginData().getAuthToken();

                RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.AUTHTOKEN, authToken, mContext);
                RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, String.valueOf(docDetail.getDocId()), mContext);
                RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.USER_NAME, docDetail.getDocName(), mContext);
                RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.PROFILE_PHOTO, docDetail.getDocImgUrl(), mContext);
                RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.EMAIL, docDetail.getDocEmail(), mContext);
                RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.SPECIALITY, docDetail.getDocSpaciality(), mContext);
                RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.ADDRESS, docDetail.getDocAddress(), mContext);
                RescribePreferencesManager.putBoolean(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.PREMIUM, docDetail.isPremium(), mContext);

                RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.LOGIN_STATUS, RescribeConstants.YES, mContext);
                if (RescribePreferencesManager.getString(RescribeConstants.TYPE_OF_LOGIN, mContext).equalsIgnoreCase(getString(R.string.login_with_facebook))) {
                    RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.MOBILE_NUMBER, RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.MOBILE_NUMBER_FACEBOOK, mContext), mContext);
                    RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.PASSWORD, RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.PASSWORD_FACEBOOK, mContext), mContext);
                }
                if (RescribePreferencesManager.getString(RescribeConstants.TYPE_OF_LOGIN, mContext).equalsIgnoreCase(getString(R.string.login_with_gmail))) {
                    RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.MOBILE_NUMBER, RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.MOBILE_NUMBER_GMAIL, mContext), mContext);
                    RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.PASSWORD, RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.PASSWORD_GMAIL, mContext), mContext);
                }
                Intent intent = new Intent(this, HomePageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                new AppDBHelper(mContext);
                finish();
            } else {
                CommonMethods.showToast(mContext, receivedModel.getCommon().getStatusMessage());
            }
        }
    }

    @Override
    public void onParseError(String mOldDataTag, String errorMessage) {
        CommonMethods.Log(TAG, errorMessage);

    }

    @Override
    public void onServerError(String mOldDataTag, String serverErrorMessage) {
        CommonMethods.Log(TAG, serverErrorMessage);
    }

    @Override
    public void onNoConnectionError(String mOldDataTag, String serverErrorMessage) {
        CommonMethods.Log(TAG, serverErrorMessage);
    }

    @Override
    public void onClickGoogle(String login) {

    }

    @Override
    public void onClickFacebook(String login) {

    }
}
