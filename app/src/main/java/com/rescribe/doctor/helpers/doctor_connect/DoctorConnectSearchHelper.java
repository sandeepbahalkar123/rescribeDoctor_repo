package com.rescribe.doctor.helpers.doctor_connect;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.rescribe.doctor.interfaces.ConnectionListener;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.doctor_connect_search.DoctorConnectSearchBaseModel;
import com.rescribe.doctor.network.ConnectRequest;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jeetal on 6/9/17.
 */

public class DoctorConnectSearchHelper implements ConnectionListener {

    String TAG = this.getClass().getName();
    Context mContext;
    HelperResponse mHelperResponseManager;

    public DoctorConnectSearchHelper(Context context, HelperResponse doctorConnectSearchActivity) {
        this.mContext = context;
        this.mHelperResponseManager = doctorConnectSearchActivity;
    }


    @Override
    public void onResponse(int responseResult, CustomResponse customResponse, String mOldDataTag) {

        switch (responseResult) {
            case ConnectionListener.RESPONSE_OK:
                if (mOldDataTag == RescribeConstants.TASK_DOCTOR_FILTER_DOCTOR_SPECIALITY_LIST) {
                    mHelperResponseManager.onSuccess(mOldDataTag, customResponse);
                }
                break;
            case ConnectionListener.PARSE_ERR0R:
                CommonMethods.Log(TAG, "parse error");
                ((HelperResponse) mContext).onParseError(mOldDataTag, "parse error");
                break;
            case ConnectionListener.SERVER_ERROR:
                CommonMethods.Log(TAG, "server error");
                ((HelperResponse) mContext).onServerError(mOldDataTag, "server error");

                break;
            case ConnectionListener.NO_CONNECTION_ERROR:
                CommonMethods.Log(TAG, "no connection error");
                ((HelperResponse) mContext).onNoConnectionError(mOldDataTag, "no connection error");
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

      /*  ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_DOCTOR__FILTER_DOCTOR_SPECIALITY_LIST, Request.Method.GET, true);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.FILTER_DOCTOR_SPECIALIST_LIST);
        mConnectionFactory.createConnection(RescribeConstants.TASK_DOCTOR__FILTER_DOCTOR_SPECIALITY_LIST);*/

        try {
            InputStream is = mContext.getAssets().open("doctor_connect_search.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            Log.e(TAG, "doDoctorSearchList" + json);

            DoctorConnectSearchBaseModel doctorConnectBaseModel = new Gson().fromJson(json, DoctorConnectSearchBaseModel.class);
            onResponse(ConnectionListener.RESPONSE_OK, doctorConnectBaseModel, RescribeConstants.TASK_DOCTOR_FILTER_DOCTOR_SPECIALITY_LIST);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

}


