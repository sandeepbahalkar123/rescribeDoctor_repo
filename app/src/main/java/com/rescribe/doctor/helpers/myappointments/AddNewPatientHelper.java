package com.rescribe.doctor.helpers.myappointments;

import android.content.Context;

import com.android.volley.Request;
import com.rescribe.doctor.R;
import com.rescribe.doctor.interfaces.ConnectionListener;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.my_appointments.RequestAppointmentData;
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

import static com.rescribe.doctor.util.RescribeConstants.TASK_GET_DOC_LIST_FOR_REFERENCE_TO_ADD_PATIENT;
import static com.rescribe.doctor.util.RescribeConstants.TASK_GET_TIME_SLOTS_TO_BOOK_APPOINTMENT;

/**
 * Created by jeetal on 31/1/18.
 */

public class AddNewPatientHelper implements ConnectionListener {

    private String TAG = this.getClass().getName();
    private Context mContext;
    private HelperResponse mHelperResponseManager;

    public AddNewPatientHelper(Context context, HelperResponse loginActivity) {
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

    public void searchDocName(String searchText) {

        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, TASK_GET_DOC_LIST_FOR_REFERENCE_TO_ADD_PATIENT, Request.Method.GET, false);
        mConnectionFactory.setHeaderParams();

        String url = Config.GET_DOC_LIST_FOR_REFERENCE_TO_ADD_PATIENT + "?searchText=" + searchText;

        mConnectionFactory.setUrl(url);
        mConnectionFactory.createConnection(TASK_GET_DOC_LIST_FOR_REFERENCE_TO_ADD_PATIENT);

    }

}


