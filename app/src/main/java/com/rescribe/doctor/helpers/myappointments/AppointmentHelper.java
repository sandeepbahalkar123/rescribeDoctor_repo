package com.rescribe.doctor.helpers.myappointments;

import android.content.Context;

import com.android.volley.Request;
import com.google.android.gms.location.LocationRequest;
import com.rescribe.doctor.R;
import com.rescribe.doctor.helpers.database.AppDBHelper;
import com.rescribe.doctor.interfaces.ConnectionListener;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.my_appointments.RequestAppointmentData;
import com.rescribe.doctor.model.my_appointments.request_cancel_or_complete_appointment.RequestAppointmentCancelModel;
import com.rescribe.doctor.model.my_patient_filter.LocationsRequest;
import com.rescribe.doctor.model.patient.add_new_patient.AddNewPatient;
import com.rescribe.doctor.model.patient.template_sms.request_send_sms.ClinicListForSms;
import com.rescribe.doctor.model.patient.template_sms.request_send_sms.RequestSendSmsModel;
import com.rescribe.doctor.model.request_patients.RequestSearchPatients;
import com.rescribe.doctor.model.waiting_list.new_request_add_to_waiting_list.RequestToAddWaitingList;
import com.rescribe.doctor.model.waiting_list.request_delete_waiting_list.RequestDeleteBaseModel;
import com.rescribe.doctor.model.waiting_list.request_drag_drop.RequestForDragAndDropBaseModel;
import com.rescribe.doctor.network.ConnectRequest;
import com.rescribe.doctor.network.ConnectionFactory;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.Config;
import com.rescribe.doctor.util.NetworkUtil;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;

/**
 * Created by jeetal on 31/1/18.
 */

public class AppointmentHelper implements ConnectionListener {

    private String TAG = this.getClass().getName();
    private Context mContext;
    private HelperResponse mHelperResponseManager;

    public AppointmentHelper(Context context, HelperResponse loginActivity) {
        this.mContext = context;
        this.mHelperResponseManager = loginActivity;
    }

    @Override
    public void onResponse(int responseResult, CustomResponse customResponse, String mOldDataTag) {

        //CommonMethods.Log(TAG, customResponse.toString());
        switch (responseResult) {
            case ConnectionListener.RESPONSE_OK:
                mHelperResponseManager.onSuccess(mOldDataTag, customResponse);
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

    public void doGetAppointmentData(String userSelectedDate) {

        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_GET_APPOINTMENT_DATA, Request.Method.POST, true);
        RequestAppointmentData mRequestAppointmentData = new RequestAppointmentData();
        mRequestAppointmentData.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, mContext)));
        mRequestAppointmentData.setDate(userSelectedDate);
        mConnectionFactory.setPostParams(mRequestAppointmentData);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.GET_MY_APPOINTMENTS_LIST);
        mConnectionFactory.createConnection(RescribeConstants.TASK_GET_APPOINTMENT_DATA);
    }

    public void doGetSearchResult(RequestSearchPatients mRequestSearchPatients) {

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

        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_GET_WAITING_LIST, Request.Method.POST, true);
        RequestAppointmentData mRequestAppointmentData = new RequestAppointmentData();
        mRequestAppointmentData.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, mContext)));
        String date = CommonMethods.getCurrentDate(RescribeConstants.DATE_PATTERN.YYYY_MM_DD);
        mRequestAppointmentData.setDate(date);
        mConnectionFactory.setPostParams(mRequestAppointmentData);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.GET__WAITING_LIST);
        mConnectionFactory.createConnection(RescribeConstants.TASK_GET_WAITING_LIST);
    }

    public void doAddToWaitingListFromMyPatients(RequestToAddWaitingList mRequestForWaitingListPatients) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_ADD_TO_WAITING_LIST, Request.Method.POST, true);
        mConnectionFactory.setPostParams(mRequestForWaitingListPatients);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.ADD_TO_WAITING_LIST);
        mConnectionFactory.createConnection(RescribeConstants.TASK_ADD_TO_WAITING_LIST);
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

    public void doDargAndDropApi(RequestForDragAndDropBaseModel requestForDragAndDropBaseModel) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_DARG_DROP, Request.Method.POST, true);
        mConnectionFactory.setPostParams(requestForDragAndDropBaseModel);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.DRAG_AND_DROP_API);
        mConnectionFactory.createConnection(RescribeConstants.TASK_DARG_DROP);
    }

    public void doGetCompletedOpdList() {
        String date = CommonMethods.getCurrentDate(RescribeConstants.DATE_PATTERN.YYYY_MM_DD);
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_GET_COMPLETED_OPD, Request.Method.POST, true);
        RequestAppointmentData mRequestAppointmentData = new RequestAppointmentData();
        mRequestAppointmentData.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, mContext)));
        mRequestAppointmentData.setDate(date);
        mConnectionFactory.setPostParams(mRequestAppointmentData);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.GET_COMPELTED_OPD_URL);
        mConnectionFactory.createConnection(RescribeConstants.TASK_GET_COMPLETED_OPD);
    }

    public void doGetNewPatientList() {
        String date = CommonMethods.getCurrentDate(RescribeConstants.DATE_PATTERN.YYYY_MM_DD);
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_GET_NEW_PATIENT_LIST, Request.Method.POST, true);
        RequestAppointmentData mRequestAppointmentData = new RequestAppointmentData();
        mRequestAppointmentData.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, mContext)));
        mRequestAppointmentData.setDate(date);
        mConnectionFactory.setPostParams(mRequestAppointmentData);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.GET_NEW_PATIENTS_URL);
        mConnectionFactory.createConnection(RescribeConstants.TASK_GET_NEW_PATIENT_LIST);
    }

    public void getFilterLocationList() {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_GET_DOCTOR_PATIENT_CITY, Request.Method.POST, true);
        LocationsRequest locationsRequest = new LocationsRequest();
        locationsRequest.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, mContext)));
        mConnectionFactory.setPostParams(locationsRequest);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.GET_DOCTOR_PATIENT_CITY);
        mConnectionFactory.createConnection(RescribeConstants.TASK_GET_DOCTOR_PATIENT_CITY);
    }

    public void addNewPatient(AddNewPatient obj) {
        AppDBHelper.getInstance(mContext).addNewPatient(obj,mHelperResponseManager,RescribeConstants.TASK_ADD_NEW_PATIENT);
    }
}


