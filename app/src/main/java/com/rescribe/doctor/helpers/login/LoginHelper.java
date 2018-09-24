package com.rescribe.doctor.helpers.login;

import android.content.Context;

import com.android.volley.Request;
import com.rescribe.doctor.interfaces.ConnectionListener;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.CommonBaseModelContainer;
import com.rescribe.doctor.model.login.ActiveRequest;
import com.rescribe.doctor.model.login.ChangePasswordRequest;
import com.rescribe.doctor.model.login.LoginModel;
import com.rescribe.doctor.model.login.SignUpModel;
import com.rescribe.doctor.model.requestmodel.login.LoginRequestModel;
import com.rescribe.doctor.model.requestmodel.login.SignUpRequestModel;
import com.rescribe.doctor.model.requestmodel.login.SignUpVerifyOTPRequestModel;
import com.rescribe.doctor.network.ConnectRequest;
import com.rescribe.doctor.network.ConnectionFactory;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.Config;
import com.rescribe.doctor.util.RescribeConstants;

/**
 * Created by ganeshshirole on 10/7/17.
 */

public class LoginHelper implements ConnectionListener {
    private String TAG = this.getClass().getName();
    private Context mContext;
    private HelperResponse mHelperResponseManager;

    public LoginHelper(Context context, HelperResponse loginActivity) {
        this.mContext = context;
        this.mHelperResponseManager = loginActivity;
    }


    @Override
    public void onResponse(int responseResult, CustomResponse customResponse, String mOldDataTag) {

        //CommonMethods.Log(TAG, customResponse.toString());
        switch (responseResult) {
            case ConnectionListener.RESPONSE_OK:
                switch (mOldDataTag) {
                    case RescribeConstants.TASK_LOGIN:
                        LoginModel loginModel = (LoginModel) customResponse;
                        mHelperResponseManager.onSuccess(mOldDataTag, loginModel);
                        break;
                    case RescribeConstants.CHANGE_PASSWORD:
                        mHelperResponseManager.onSuccess(mOldDataTag, customResponse);
                        break;
                    case RescribeConstants.LOGOUT:
                        mHelperResponseManager.onSuccess(mOldDataTag, customResponse);
                        break;
                    case RescribeConstants.ACTIVE_STATUS:
                        mHelperResponseManager.onSuccess(mOldDataTag, customResponse);
                        break;
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
    public void doLogin(String email, String password) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_LOGIN, Request.Method.POST, true);
        mConnectionFactory.setHeaderParams();
        LoginRequestModel loginRequestModel = new LoginRequestModel();
        loginRequestModel.setEmailId(email);
        loginRequestModel.setPassword(password);
        mConnectionFactory.setPostParams(loginRequestModel);
        mConnectionFactory.setUrl(Config.LOGIN_URL);
        mConnectionFactory.createConnection(RescribeConstants.TASK_LOGIN);
    }

    // Logout
    public void doLogout(ActiveRequest activeRequest) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.LOGOUT, Request.Method.POST, false);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setPostParams(activeRequest);
        mConnectionFactory.setUrl(Config.LOGOUT);
        mConnectionFactory.createConnection(RescribeConstants.LOGOUT);
    }

    // ActiveStatus
    public void doActiveStatus(ActiveRequest activeRequest) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.ACTIVE_STATUS, Request.Method.POST, false);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setPostParams(activeRequest);
        mConnectionFactory.setUrl(Config.ACTIVE);
        mConnectionFactory.createConnection(RescribeConstants.ACTIVE_STATUS);
    }

    public void changePassword(String oldPassword, String newPassword) {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setCurrentPassword(oldPassword);
        changePasswordRequest.setNewPassword(newPassword);
        changePasswordRequest.setConfirmPassword(newPassword);
        changePasswordRequest.setDocId(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, mContext));
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.CHANGE_PASSWORD, Request.Method.POST, false);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setPostParams(changePasswordRequest);
        mConnectionFactory.setUrl(Config.CHANGE_PASSWORD);
        mConnectionFactory.createConnection(RescribeConstants.CHANGE_PASSWORD);
    }
}
