package com.rescribe.doctor.helpers.login;

import android.content.Context;

import com.android.volley.Request;
import com.rescribe.doctor.interfaces.ConnectionListener;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.login.LoginModel;
import com.rescribe.doctor.model.login.SignUpModel;
import com.rescribe.doctor.model.requestmodel.login.LoginRequestModel;
import com.rescribe.doctor.model.requestmodel.login.SignUpRequestModel;
import com.rescribe.doctor.model.requestmodel.login.SignUpVerifyOTPRequestModel;
import com.rescribe.doctor.network.ConnectRequest;
import com.rescribe.doctor.network.ConnectionFactory;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.Config;
import com.rescribe.doctor.util.RescribeConstants;

/**
 * Created by ganeshshirole on 10/7/17.
 */

public class LoginHelper implements ConnectionListener {
    String TAG = this.getClass().getName();
    Context mContext;
    HelperResponse mHelperResponseManager;

    public LoginHelper(Context context, HelperResponse loginActivity) {
        this.mContext = context;
        this.mHelperResponseManager = loginActivity;
    }


    @Override
    public void onResponse(int responseResult, CustomResponse customResponse, String mOldDataTag) {

        //CommonMethods.Log(TAG, customResponse.toString());
        switch (responseResult) {
            case ConnectionListener.RESPONSE_OK:
                if (mOldDataTag.equals(RescribeConstants.TASK_LOGIN)) {
                    LoginModel loginModel = (LoginModel) customResponse;
                    mHelperResponseManager.onSuccess(mOldDataTag, loginModel);
                } else if (mOldDataTag.equals(RescribeConstants.TASK_SIGN_UP)) {
                    SignUpModel signUpModel = (SignUpModel) customResponse;
                    mHelperResponseManager.onSuccess(mOldDataTag, signUpModel);
                } else if (mOldDataTag.equals(RescribeConstants.TASK_VERIFY_SIGN_UP_OTP)) {
                    mHelperResponseManager.onSuccess(mOldDataTag, customResponse);
                } else if (mOldDataTag.equals(RescribeConstants.TASK_LOGIN_WITH_PASSWORD)) {
                    mHelperResponseManager.onSuccess(mOldDataTag, customResponse);
                }else if (mOldDataTag.equals(RescribeConstants.TASK_LOGIN_WITH_OTP)) {
                    mHelperResponseManager.onSuccess(mOldDataTag, customResponse);
                }
                break;
            case ConnectionListener.PARSE_ERR0R:
                CommonMethods.Log(TAG, "parse error");
                break;
            case ConnectionListener.SERVER_ERROR:
                CommonMethods.Log(TAG, "server error");
                mHelperResponseManager.onServerError(mOldDataTag, "server error");
                break;
            case ConnectionListener.NO_CONNECTION_ERROR:
                CommonMethods.Log(TAG, "no connection error");
                mHelperResponseManager.onNoConnectionError(mOldDataTag, "no connection error");
                break;
            default:
                CommonMethods.Log(TAG, "default error");
                break;
        }
    }

    @Override
    public void onTimeout(ConnectRequest request) {

    }
     //Do login using mobileNo and password
    public void doLogin(String mobileNo,String password) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_LOGIN, Request.Method.POST, true);
        mConnectionFactory.setHeaderParams();
        LoginRequestModel loginRequestModel = new LoginRequestModel();
        loginRequestModel.setMobileNumber(mobileNo);
        loginRequestModel.setPassword(password);
        mConnectionFactory.setPostParams(loginRequestModel);
        mConnectionFactory.setUrl(Config.LOGIN_URL);
        mConnectionFactory.createConnection(RescribeConstants.TASK_LOGIN);
    }
   //Do login using Otp
    public void doLoginByOTP(String otp) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_LOGIN_WITH_OTP, Request.Method.POST, true);
        mConnectionFactory.setHeaderParams();
        LoginRequestModel loginRequestModel = new LoginRequestModel();
        loginRequestModel.setMobileNumber(otp);
        mConnectionFactory.setPostParams(loginRequestModel);
        mConnectionFactory.setUrl(Config.LOGIN_WITH_OTP_URL);
        mConnectionFactory.createConnection(RescribeConstants.TASK_LOGIN_WITH_OTP);
    }
    //Verify Otp sent
    public void doVerifyGeneratedSignUpOTP(SignUpVerifyOTPRequestModel requestModel) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_VERIFY_SIGN_UP_OTP, Request.Method.POST, false);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setPostParams(requestModel);
        mConnectionFactory.setUrl(Config.VERIFY_SIGN_UP_OTP);
        mConnectionFactory.createConnection(RescribeConstants.TASK_VERIFY_SIGN_UP_OTP);
    }

  //SignUp
    public void doSignUp(SignUpRequestModel signUpRequestModel) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_SIGN_UP, Request.Method.POST, false);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setPostParams(signUpRequestModel);
        mConnectionFactory.setUrl(Config.SIGN_UP_URL);
        mConnectionFactory.createConnection(RescribeConstants.TASK_SIGN_UP);
    }
}
