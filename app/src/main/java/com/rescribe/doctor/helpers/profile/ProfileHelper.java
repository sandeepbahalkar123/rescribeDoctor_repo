package com.rescribe.doctor.helpers.profile;

import android.content.Context;

import com.android.volley.Request;
import com.rescribe.doctor.interfaces.ConnectionListener;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.login.ActiveRequest;
import com.rescribe.doctor.model.login.LoginModel;
import com.rescribe.doctor.network.ConnectRequest;
import com.rescribe.doctor.network.ConnectionFactory;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.Config;
import com.rescribe.doctor.util.RescribeConstants;

public class ProfileHelper implements ConnectionListener {
    private String TAG = this.getClass().getName();
    private Context mContext;
    private HelperResponse mHelperResponseManager;

    public ProfileHelper(Context context, HelperResponse loginActivity) {
        this.mContext = context;
        this.mHelperResponseManager = loginActivity;
    }


    @Override
    public void onResponse(int responseResult, CustomResponse customResponse, String mOldDataTag) {
        //CommonMethods.Log(TAG, customResponse.toString());
        switch (responseResult) {
            case ConnectionListener.RESPONSE_OK:
                switch (mOldDataTag) {
                    case RescribeConstants.TASK_DOCTOR_SPECIALIST_LIST:
                        LoginModel loginModel = (LoginModel) customResponse;
                        mHelperResponseManager.onSuccess(mOldDataTag, loginModel);
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


    public void getDoctorSpecialityList() {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_DOCTOR_SPECIALIST_LIST, Request.Method.GET, true);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.GET_DOCTOR_SPECIALIST_LIST);
        mConnectionFactory.createConnection(RescribeConstants.TASK_DOCTOR_SPECIALIST_LIST);
    }

    // Logout
    public void doLogout(ActiveRequest activeRequest) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.LOGOUT, Request.Method.POST, false);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setPostParams(activeRequest);
        mConnectionFactory.setUrl(Config.LOGOUT);
        mConnectionFactory.createConnection(RescribeConstants.LOGOUT);
    }

}

