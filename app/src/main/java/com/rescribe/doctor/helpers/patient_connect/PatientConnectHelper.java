package com.rescribe.doctor.helpers.patient_connect;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.rescribe.doctor.interfaces.ConnectionListener;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
 import com.rescribe.doctor.model.patient_connect.PatientConnectBaseModel;
import com.rescribe.doctor.network.ConnectRequest;
import com.rescribe.doctor.network.ConnectionFactory;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.Config;
import com.rescribe.doctor.util.RescribeConstants;


import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jeetal on 6/9/17.
 */

public class PatientConnectHelper implements ConnectionListener {

    String TAG = this.getClass().getName();
    Context mContext;
    HelperResponse mHelperResponseManager;

    public PatientConnectHelper(Context context, HelperResponse doctorConnectActivity) {
        this.mContext = context;
        this.mHelperResponseManager = doctorConnectActivity;
    }

    @Override
    public void onResponse(int responseResult, CustomResponse customResponse, String mOldDataTag) {

        //CommonMethods.Log(TAG, customResponse.toString());
        switch (responseResult) {
            case ConnectionListener.RESPONSE_OK:
                if (mOldDataTag == RescribeConstants.TASK_GET_PATIENT_LIST) {
                    mHelperResponseManager.onSuccess(mOldDataTag, customResponse);
                }
                break;
            case ConnectionListener.PARSE_ERR0R:
                CommonMethods.Log(TAG, "parse error");
                mHelperResponseManager.onParseError(mOldDataTag, "parse error");
                break;
            case ConnectionListener.SERVER_ERROR:
                CommonMethods.Log(TAG, "server error");
                mHelperResponseManager.onServerError(mOldDataTag, "server error");

                break;
            case ConnectionListener.NO_INTERNET:
                CommonMethods.Log(TAG, "no connection error");
                mHelperResponseManager.onNoConnectionError(mOldDataTag, "no connection error");

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

    public void doGetChatPatientList() {
     /* ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_GET_PATIENT_LIST, Request.Method.GET, true);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.GET_PATIENT_CHAT_LIST);
        mConnectionFactory.createConnection(RescribeConstants.TASK_GET_PATIENT_LIST);*/

        try {
            InputStream is = mContext.getAssets().open("patient_connect.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            Log.e(TAG, "doGetChatPatientList" + json);

            PatientConnectBaseModel doctorConnectBaseModel = new Gson().fromJson(json, PatientConnectBaseModel.class);
            onResponse(ConnectionListener.RESPONSE_OK, doctorConnectBaseModel, RescribeConstants.TASK_GET_PATIENT_LIST);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
