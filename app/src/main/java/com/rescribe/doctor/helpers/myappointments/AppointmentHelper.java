package com.rescribe.doctor.helpers.myappointments;

import android.content.Context;

import com.android.volley.Request;
import com.rescribe.doctor.R;
import com.rescribe.doctor.interfaces.ConnectionListener;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.my_appointments.RequestAppointmentData;
import com.rescribe.doctor.model.my_appointments.RequestAppointmentDeleteModel;
import com.rescribe.doctor.model.my_appointments.cancel_appointment_bulk.CancelAppointmentList;
import com.rescribe.doctor.model.my_appointments.request_cancel_or_complete_appointment.RequestAppointmentCancelModel;
import com.rescribe.doctor.model.my_patient_filter.LocationsRequest;
import com.rescribe.doctor.model.patient.add_new_patient.PatientDetail;
import com.rescribe.doctor.model.patient.add_new_patient.SyncPatientsRequest;
import com.rescribe.doctor.model.patient.add_new_patient.address_other_details.city_details.CityData;
import com.rescribe.doctor.model.patient.doctor_patients.PatientList;
import com.rescribe.doctor.model.patient.template_sms.request_send_sms.ClinicListForSms;
import com.rescribe.doctor.model.patient.template_sms.request_send_sms.RequestSendSmsModel;
import com.rescribe.doctor.model.request_appointment_confirmation.RequestAppointmentConfirmationModel;
import com.rescribe.doctor.model.request_appointment_confirmation.Reschedule;
import com.rescribe.doctor.model.request_patients.RequestSearchPatients;
import com.rescribe.doctor.model.select_slot_book_appointment.TimeSlotData;
import com.rescribe.doctor.model.waiting_list.new_request_add_to_waiting_list.RequestToAddWaitingList;
import com.rescribe.doctor.model.waiting_list.request_delete_waiting_list.RequestWaitingListStatusChangeBaseModel;
import com.rescribe.doctor.model.waiting_list.request_drag_drop.RequestForDragAndDropBaseModel;
import com.rescribe.doctor.network.ConnectRequest;
import com.rescribe.doctor.network.ConnectionFactory;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.Config;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;

import static com.rescribe.doctor.util.RescribeConstants.TASK_GET_TIME_SLOTS_TO_BOOK_APPOINTMENT;

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

        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_GET_APPOINTMENT_DATA, Request.Method.POST, false);
        RequestAppointmentData mRequestAppointmentData = new RequestAppointmentData();
        mRequestAppointmentData.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, mContext)));
        mRequestAppointmentData.setDate(userSelectedDate);
        mConnectionFactory.setPostParams(mRequestAppointmentData);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.GET_MY_APPOINTMENTS_LIST);
        mConnectionFactory.createConnection(RescribeConstants.TASK_GET_APPOINTMENT_DATA);
    }


    public void doGetSearchResult(RequestSearchPatients mRequestSearchPatients, boolean isProgressShow) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, isProgressShow, RescribeConstants.TASK_GET_SEARCH_RESULT_MY_PATIENT, Request.Method.POST, false);
        mConnectionFactory.setPostParams(mRequestSearchPatients);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.GET_MY_PATIENTS_LIST);
        mConnectionFactory.createConnection(RescribeConstants.TASK_GET_SEARCH_RESULT_MY_PATIENT);
    }

    public void doGetDoctorTemplate() {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_GET_DOCTOR_SMS_TEMPLATE, Request.Method.GET, false);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.GET_SMS_TEMPLATE + Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, mContext)));
        mConnectionFactory.createConnection(RescribeConstants.TASK_GET_DOCTOR_SMS_TEMPLATE);
    }

    public void doRequestSendSMS(ArrayList<ClinicListForSms> clinicListForSms) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_REQUEST_SEND_SMS, Request.Method.POST, false);
        RequestSendSmsModel mRequestSendSmsModel = new RequestSendSmsModel();
        mRequestSendSmsModel.setClinicListForSms(clinicListForSms);
        mConnectionFactory.setPostParams(mRequestSendSmsModel);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.REQUEST_SEND_SMS);
        mConnectionFactory.createConnection(RescribeConstants.TASK_REQUEST_SEND_SMS);
    }

    public void doGetWaitingList() {

        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_GET_WAITING_LIST, Request.Method.POST, false);
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
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_ADD_TO_WAITING_LIST, Request.Method.POST, false);
        mConnectionFactory.setPostParams(mRequestForWaitingListPatients);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.ADD_TO_WAITING_LIST);
        mConnectionFactory.createConnection(RescribeConstants.TASK_ADD_TO_WAITING_LIST);
    }

    //---------- Waiting list API : START-------
    public void doDeleteWaitingList(RequestWaitingListStatusChangeBaseModel mRequestDeleteBaseModel) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_DELETE_WAITING_LIST, Request.Method.POST, false);
        mConnectionFactory.setPostParams(mRequestDeleteBaseModel);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.DELETE_WAITING_LIST);
        mConnectionFactory.createConnection(RescribeConstants.TASK_DELETE_WAITING_LIST);
    }

    //Use to change waiting list status to in-consultation or completed.
    public void doUpdateWaitingListStatus(RequestWaitingListStatusChangeBaseModel mRequestDeleteBaseModel) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_INCONSULATION_OR_COMPLETED_WAITING_LIST, Request.Method.POST, false);
        mConnectionFactory.setPostParams(mRequestDeleteBaseModel);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.INCONSULATION_OR_COMPLETED_WAITING_LIST);
        mConnectionFactory.createConnection(RescribeConstants.TASK_INCONSULATION_OR_COMPLETED_WAITING_LIST);
    }

    //----------- Waiting list API :END------
    public void doAppointmentCancelOrComplete(RequestAppointmentCancelModel mRequestAppointmentCancelModel) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_APPOINTMENT_CANCEL_OR_COMPLETE, Request.Method.POST, false);
        mConnectionFactory.setPostParams(mRequestAppointmentCancelModel);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.CANCEL_OR_COMPLETE_APPOINTMENT);
        mConnectionFactory.createConnection(RescribeConstants.TASK_APPOINTMENT_CANCEL_OR_COMPLETE);
    }

    //----------- Waiting list API :END------

    public void doDargAndDropApi(RequestForDragAndDropBaseModel requestForDragAndDropBaseModel) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_DARG_DROP, Request.Method.POST, false);
        mConnectionFactory.setPostParams(requestForDragAndDropBaseModel);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.DRAG_AND_DROP_API);
        mConnectionFactory.createConnection(RescribeConstants.TASK_DARG_DROP);
    }

    public void doGetCompletedOpdList() {
        String date = CommonMethods.getCurrentDate(RescribeConstants.DATE_PATTERN.YYYY_MM_DD);
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_GET_COMPLETED_OPD, Request.Method.POST, false);
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
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_GET_NEW_PATIENT_LIST, Request.Method.POST, false);
        RequestAppointmentData mRequestAppointmentData = new RequestAppointmentData();
        mRequestAppointmentData.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, mContext)));
        mRequestAppointmentData.setDate(date);
        mConnectionFactory.setPostParams(mRequestAppointmentData);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.GET_NEW_PATIENTS_URL);
        mConnectionFactory.createConnection(RescribeConstants.TASK_GET_NEW_PATIENT_LIST);
    }

    public void getFilterLocationList() {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_GET_DOCTOR_PATIENT_CITY, Request.Method.POST, false);
        LocationsRequest locationsRequest = new LocationsRequest();
        locationsRequest.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, mContext)));
        mConnectionFactory.setPostParams(locationsRequest);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.GET_DOCTOR_PATIENT_CITY);
        mConnectionFactory.createConnection(RescribeConstants.TASK_GET_DOCTOR_PATIENT_CITY);
    }

    public void getTimeSlotToBookAppointmentWithDoctor(String docId, int locationID, String date, boolean isReqDoctorData, int patientID) {
        RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.SELECTED_APP_DATE, date, mContext);

        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, TASK_GET_TIME_SLOTS_TO_BOOK_APPOINTMENT, Request.Method.GET, false);
        mConnectionFactory.setHeaderParams();

        String currentTimeStamp = CommonMethods.getCurrentTimeStamp(RescribeConstants.DATE_PATTERN.HH_mm);
        String url = Config.TIME_SLOT_TO_BOOK_APPOINTMENT + "docId=" + docId + "&locationId=" + locationID + "&date=" + date + "&time=" + currentTimeStamp + "&patientId=" + patientID + "&docDetailReq=" + isReqDoctorData;

        mConnectionFactory.setUrl(url);
        mConnectionFactory.createConnection(TASK_GET_TIME_SLOTS_TO_BOOK_APPOINTMENT);//RescribeConstants.TASK_TIME_SLOT_TO_BOOK_APPOINTMENT

    }


    public void cancelAppointmentBulk(CancelAppointmentList cancelAppointmentList) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_CANCEL_APPOINTMENT_BULK, Request.Method.POST, false);
        mConnectionFactory.setPostParams(cancelAppointmentList);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.CANCEL_APPOINTMENT_BULK);
        mConnectionFactory.createConnection(RescribeConstants.TASK_CANCEL_APPOINTMENT_BULK);
    }

    public void doAppointmentDelete(RequestAppointmentDeleteModel mRequestAppointmentDeleteModel) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_APPOINTMENT_DELETE, Request.Method.PUT, false);
        mConnectionFactory.setPostParams(mRequestAppointmentDeleteModel);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.DELETE_APPOINTMENT);
        mConnectionFactory.createConnection(RescribeConstants.TASK_APPOINTMENT_DELETE);
    }

    public void doConfirmAppointmentRequest(int docId, int locationID, String date, TimeSlotData timeSlotData, Reschedule reschedule, int patientID) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_CONFIRM_APPOINTMENT, Request.Method.POST, false);
        mConnectionFactory.setHeaderParams();

        RequestAppointmentConfirmationModel mRequestAppointmentConfirmationModel = new RequestAppointmentConfirmationModel();
        mRequestAppointmentConfirmationModel.setDocId(docId);
        mRequestAppointmentConfirmationModel.setFromTime(timeSlotData.getFromTime());
        mRequestAppointmentConfirmationModel.setLocationId(locationID);
        mRequestAppointmentConfirmationModel.setToTime(timeSlotData.getToTime());
        mRequestAppointmentConfirmationModel.setDate(date);
        mRequestAppointmentConfirmationModel.setSlotId(timeSlotData.getSlotId());
        mRequestAppointmentConfirmationModel.setReschedule(reschedule);
        mRequestAppointmentConfirmationModel.setPatientId(patientID);
        mConnectionFactory.setPostParams(mRequestAppointmentConfirmationModel);
        mConnectionFactory.setUrl(Config.CONFIRM_APPOINTMENT);
        mConnectionFactory.createConnection(RescribeConstants.TASK_CONFIRM_APPOINTMENT);

    }

    public void addNewPatient(PatientList dataToAdd) {

        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_ADD_NEW_PATIENT, Request.Method.POST, false);
        PatientDetail patient = new PatientDetail();
        patient.setSalutation(dataToAdd.getSalutation());
        patient.setMobilePatientId(dataToAdd.getPatientId());

        //---------
        String[] split = dataToAdd.getPatientName().split(" ");
        patient.setPatientFname(split[0]);
        if (split.length > 1) {
            if (split[1].equalsIgnoreCase("|"))
                patient.setPatientMname("");
            else
                patient.setPatientMname(split[1]);
        }
        if (split.length > 2)
            patient.setPatientLname(split[2]);
        //---------
        patient.setPatientPhone(dataToAdd.getPatientPhone());
        patient.setPatientAge(dataToAdd.getAge());
        patient.setPatientGender(dataToAdd.getGender());
        patient.setClinicId(dataToAdd.getClinicId());
        patient.setPatientDob(dataToAdd.getDateOfBirth());
        patient.setOfflineReferenceID(dataToAdd.getReferenceID());
        patient.setRelation(dataToAdd.getRelation());

        patient.setAddressDetails(dataToAdd.getAddressDetails());

        patient.setReferedDetails(dataToAdd.getReferedDetails());

        SyncPatientsRequest mSyncPatientsRequest = new SyncPatientsRequest();
        String id = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, mContext);
        mSyncPatientsRequest.setDocId(id);

        ArrayList<PatientDetail> add = new ArrayList<PatientDetail>();
        add.add(patient);
        mSyncPatientsRequest.setPatientDetails(add);

        mConnectionFactory.setPostParams(mSyncPatientsRequest);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.ADD_PATIENTS_SYNC);
        mConnectionFactory.createConnection(RescribeConstants.TASK_ADD_NEW_PATIENT);
    }

    public void getAreaOfSelectedCity(int mCityID) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_GET_AREA_TO_ADD_NEW_PATIENT, Request.Method.POST, false);

        // send cityId to get area list based on city-id.
        CityData c = new CityData();
        c.setCityId(mCityID);

        mConnectionFactory.setPostParams(c);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.GET_ALL_AREA_OF_CITIES_TO_ADD_PATIENT);
        mConnectionFactory.createConnection(RescribeConstants.TASK_GET_AREA_TO_ADD_NEW_PATIENT);
    }


    public void getReferenceList(int mClinicId) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_GET_REFERENCE_LIST, Request.Method.GET, false);
        mConnectionFactory.setHeaderParams();
        String url = Config.GET_REFERENCE_LIST + "clinicId=" + mClinicId;
        mConnectionFactory.setUrl(url);
        mConnectionFactory.createConnection(RescribeConstants.TASK_GET_REFERENCE_LIST);
    }
}


