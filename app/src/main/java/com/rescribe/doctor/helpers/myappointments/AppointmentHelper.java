package com.rescribe.doctor.helpers.myappointments;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.rescribe.doctor.R;
import com.rescribe.doctor.helpers.doctor_patients.MyPatientBaseModel;
import com.rescribe.doctor.interfaces.ConnectionListener;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.my_appointments.MyAppointmentsBaseModel;
import com.rescribe.doctor.model.my_appointments.RequestAppointmentData;
import com.rescribe.doctor.model.my_appointments.request_cancel_or_complete_appointment.RequestAppointmentCancelModel;
import com.rescribe.doctor.model.patient.template_sms.request_send_sms.ClinicListForSms;
import com.rescribe.doctor.model.patient.template_sms.request_send_sms.RequestSendSmsModel;
import com.rescribe.doctor.model.request_patients.RequestSearchPatients;
import com.rescribe.doctor.model.waiting_list.WaitingListBaseModel;
import com.rescribe.doctor.model.waiting_list.request_add_waiting_list.RequestForWaitingListPatients;
import com.rescribe.doctor.model.waiting_list.request_delete_waiting_list.RequestDeleteBaseModel;
import com.rescribe.doctor.network.ConnectRequest;
import com.rescribe.doctor.network.ConnectionFactory;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.Config;
import com.rescribe.doctor.util.RescribeConstants;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by jeetal on 31/1/18.
 */

public class AppointmentHelper implements ConnectionListener {

    String TAG = this.getClass().getName();
    Context mContext;
    HelperResponse mHelperResponseManager;

    public AppointmentHelper(Context context, HelperResponse loginActivity) {
        this.mContext = context;
        this.mHelperResponseManager = loginActivity;
    }

    @Override
    public void onResponse(int responseResult, CustomResponse customResponse, String mOldDataTag) {

        //CommonMethods.Log(TAG, customResponse.toString());
        switch (responseResult) {
            case ConnectionListener.RESPONSE_OK:
                if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_GET_APPOINTMENT_DATA)) {
                    mHelperResponseManager.onSuccess(mOldDataTag, customResponse);
                } else if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_GET_PATIENT_DATA)) {
                    mHelperResponseManager.onSuccess(mOldDataTag, customResponse);
                } else if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_GET_DOCTOR_SMS_TEMPLATE)) {
                    mHelperResponseManager.onSuccess(mOldDataTag, customResponse);
                } else if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_REQUEST_SEND_SMS)) {
                    mHelperResponseManager.onSuccess(mOldDataTag, customResponse);
                } else if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_GET_WAITING_LIST)) {
                    mHelperResponseManager.onSuccess(mOldDataTag, customResponse);
                } else if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_GET_SEARCH_RESULT_MY_PATIENT)) {
                    mHelperResponseManager.onSuccess(mOldDataTag, customResponse);
                } else if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_ADD_TO_WAITING_LIST)) {
                    mHelperResponseManager.onSuccess(mOldDataTag, customResponse);
                }else if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_DELETE_WAITING_LIST)) {
                    mHelperResponseManager.onSuccess(mOldDataTag, customResponse);
                }else if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_APPOINTMENT_CANCEL_OR_COMPLETE)) {
                    mHelperResponseManager.onSuccess(mOldDataTag, customResponse);
                }
                break;
            case ConnectionListener.PARSE_ERR0R:
                CommonMethods.Log(TAG, mContext.getString(R.string.parse_error));
                mHelperResponseManager.onParseError(mOldDataTag, mContext.getString(R.string.parse_error));
                break;
            case ConnectionListener.SERVER_ERROR:
                CommonMethods.Log(TAG, mContext.getString(R.string.server_error));
                mHelperResponseManager.onServerError(mOldDataTag, mContext.getString(R.string.server_error));
                break;
            case ConnectionListener.NO_INTERNET:
                CommonMethods.Log(TAG, mContext.getString(R.string.no_connection_error));
                mHelperResponseManager.onNoConnectionError(mOldDataTag, mContext.getString(R.string.no_connection_error));
                break;
            case ConnectionListener.NO_CONNECTION_ERROR:
                CommonMethods.Log(TAG, mContext.getString(R.string.no_connection_error));
                mHelperResponseManager.onNoConnectionError(mOldDataTag, mContext.getString(R.string.no_connection_error));
                break;
            default:
                CommonMethods.Log(TAG, mContext.getString(R.string.default_error));
                break;
        }
    }

    @Override
    public void onTimeout(ConnectRequest request) {

    }

    public void doGetAppointmentData() {
    /*  try {
            InputStream is = mContext.getAssets().open("my_appointments.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            Log.e(TAG, "my_appointments" + json);

            Gson gson = new Gson();
            MyAppointmentsBaseModel mMyAppointmentsBaseModel = gson.fromJson(json, MyAppointmentsBaseModel.class);
            onResponse(ConnectionListener.RESPONSE_OK, mMyAppointmentsBaseModel, RescribeConstants.TASK_GET_APPOINTMENT_DATA);

        } catch (IOException ex) {
            ex.printStackTrace();
        }*/
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_GET_APPOINTMENT_DATA, Request.Method.POST, true);
        RequestAppointmentData mRequestAppointmentData = new RequestAppointmentData();
        mRequestAppointmentData.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, mContext)));
        String date = CommonMethods.getFormattedDate(CommonMethods.getCurrentDate(), RescribeConstants.DD_MM_YYYY, RescribeConstants.DATE_PATTERN.YYYY_MM_DD);
        mRequestAppointmentData.setDate(date);
        mConnectionFactory.setPostParams(mRequestAppointmentData);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.GET_MY_APPOINTMENTS_LIST);
        mConnectionFactory.createConnection(RescribeConstants.TASK_GET_APPOINTMENT_DATA);
    }

    public void doGetMyPatients(RequestSearchPatients mRequestSearchPatients) {
       /* try {
            InputStream is = mContext.getAssets().open("patients.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            Log.e(TAG, "patients" + json);

            Gson gson = new Gson();
            MyPatientBaseModel mMyPatientBaseModel = gson.fromJson(json, MyPatientBaseModel.class);
            onResponse(ConnectionListener.RESPONSE_OK, mMyPatientBaseModel, RescribeConstants.TASK_GET_PATIENT_DATA);

        } catch (IOException ex) {
            ex.printStackTrace();
        }*/
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_GET_PATIENT_DATA, Request.Method.POST, true);
        mConnectionFactory.setPostParams(mRequestSearchPatients);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.GET_MY_PATIENTS_LIST);
        mConnectionFactory.createConnection(RescribeConstants.TASK_GET_PATIENT_DATA);
    }

    public void doGetSearchResult(RequestSearchPatients mRequestSearchPatients) {
       /* try {
            InputStream is = mContext.getAssets().open("patients.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            Log.e(TAG, "patients" + json);

            Gson gson = new Gson();
            MyPatientBaseModel mMyPatientBaseModel = gson.fromJson(json, MyPatientBaseModel.class);
            onResponse(ConnectionListener.RESPONSE_OK, mMyPatientBaseModel, RescribeConstants.TASK_GET_PATIENT_DATA);

        } catch (IOException ex) {
            ex.printStackTrace();
        }*/
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_GET_SEARCH_RESULT_MY_PATIENT, Request.Method.POST, true);
        mConnectionFactory.setPostParams(mRequestSearchPatients);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.GET_MY_PATIENTS_LIST);
        mConnectionFactory.createConnection(RescribeConstants.TASK_GET_SEARCH_RESULT_MY_PATIENT);
    }

    public void doGetDoctorTemplate() {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_GET_DOCTOR_SMS_TEMPLATE, Request.Method.GET, true);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.GET_SMS_TEMPLATE + Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, mContext)));
        mConnectionFactory.createConnection(RescribeConstants.TASK_GET_DOCTOR_SMS_TEMPLATE);
    }

    public void doRequestSendSMS(ArrayList<ClinicListForSms> clinicListForSms) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_REQUEST_SEND_SMS, Request.Method.POST, true);
        RequestSendSmsModel mRequestSendSmsModel = new RequestSendSmsModel();
        mRequestSendSmsModel.setClinicListForSms(clinicListForSms);
        mConnectionFactory.setPostParams(mRequestSendSmsModel);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.REQUEST_SEND_SMS);
        mConnectionFactory.createConnection(RescribeConstants.TASK_REQUEST_SEND_SMS);
    }

    public void doGetWaitingList() {
      /*  try {
            InputStream is = mContext.getAssets().open("waiting_list.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            Log.e(TAG, "patients" + json);

            Gson gson = new Gson();
            WaitingListBaseModel mWaitingListBaseModel = gson.fromJson(json, WaitingListBaseModel.class);
            onResponse(ConnectionListener.RESPONSE_OK, mWaitingListBaseModel, RescribeConstants.TASK_GET_PATIENT_DATA);

        } catch (IOException ex) {
            ex.printStackTrace();
        }*/
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_GET_WAITING_LIST, Request.Method.POST, true);
        RequestAppointmentData mRequestAppointmentData = new RequestAppointmentData();
        mRequestAppointmentData.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, mContext)));
        String date = CommonMethods.getFormattedDate(CommonMethods.getCurrentDate(), RescribeConstants.DD_MM_YYYY, RescribeConstants.DATE_PATTERN.YYYY_MM_DD);
        mRequestAppointmentData.setDate(date);
        mConnectionFactory.setPostParams(mRequestAppointmentData);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.GET__WAITING_LIST);
        mConnectionFactory.createConnection(RescribeConstants.TASK_GET_WAITING_LIST);
    }

    public void doAddToWaitingList(RequestForWaitingListPatients mRequestForWaitingListPatients) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_ADD_TO_WAITING_LIST, Request.Method.POST, true);
        mConnectionFactory.setPostParams(mRequestForWaitingListPatients);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.ADD_TO_WAITING_LIST);
        mConnectionFactory.createConnection(RescribeConstants.TASK_REQUEST_SEND_SMS);
    }
    public void doDeleteWaitingList(RequestDeleteBaseModel mRequestDeleteBaseModel) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_DELETE_WAITING_LIST, Request.Method.POST, true);
        mConnectionFactory.setPostParams(mRequestDeleteBaseModel);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.DELETE_WAITING_LIST);
        mConnectionFactory.createConnection(RescribeConstants.TASK_DELETE_WAITING_LIST);
    }

    public void doAppointmentCancelOrComplete(RequestAppointmentCancelModel mRequestAppointmentCancelModel) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_APPOINTMENT_CANCEL_OR_COMPLETE, Request.Method.POST, true);
        mConnectionFactory.setPostParams(mRequestAppointmentCancelModel);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.CANCEL_OR_COMPLETE_APPOINTMENT);
        mConnectionFactory.createConnection(RescribeConstants.TASK_APPOINTMENT_CANCEL_OR_COMPLETE);
    }


}


