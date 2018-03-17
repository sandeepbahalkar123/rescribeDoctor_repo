package com.rescribe.doctor.util;

import android.content.Context;

/**
 * Created by Sandeep Bahalkar
 */
public class Config {
    public static final String HTTP = "http://";
    public static final String API = "/api/";
    public static final String TOKEN_TYPE = "Bearer";
    public static final String LOGIN_URL = "authApi/authenticate/doctorLogin";
    public static final String VERIFY_SIGN_UP_OTP = "authApi/authenticate/verifyOTP";
    public static final String SIGN_UP_URL = "authApi/authenticate/signUp";
    public static final String ONE_DAY_VISIT_URL = "api/patient/getPatientOneDayVisit?opdId=";
    public static final String GET_MY_APPOINTMENTS_LIST = "doctor/api/appointment/getAppointmentList";
    public static final String GET_SMS_TEMPLATE = "doctor/api/appointment/getDoctorSmsTemplate?docId=";
    public static final String GET__WAITING_LIST = "doctor/api/appointment/getWaitingList";
    public static final String GET_MY_PATIENTS_LIST = "doctor/api/patient/getPatientList";
    public static final String GET_PATIENT_HISTORY = "doctor/api/patient/getPatientOpdHistory";
    public static final String ADD_TO_WAITING_LIST = "doctor/api/appointment/addToWaitingList";
    public static final String GET_CLINIC_LOCATION_LIST = "doctor/api/appointment/getDoctorLocationList?docId=";
    public static final String GET_DASHBOARD_DATA = "doctor/api/dashboard/getDashboard";
    public static final String DELETE_WAITING_LIST = "doctor/api/appointment/deleteFromWaitingList";
    public static final String CANCEL_OR_COMPLETE_APPOINTMENT = "api/patient/updateAppointmentStatus";
    public static final String DRAG_AND_DROP_API = "doctor/api/appointment/dragDropPatientWaiting";
    public static final String GET_COMPELTED_OPD_URL = "doctor/api/appointment/getCompletedOpd";
    public static boolean DEV_BUILD = true;
    //Declared all URL used in app here

    //    public static final String BROKER = "tcp://ec2-13-126-175-156.ap-south-1.compute.amazonaws.com:1883";
    public static final String BROKER = "tcp://52.66.154.249:1883"; // Dr Rescribe IP

    public static String BASE_URL = "http://drrescribe.com:3003/";
    public static final String MY_RECORDS_DOCTOR_LIST = "api/doctors/getDoctorsWithPatientVisits";
    public static final String MY_RECORDS_ADD_DOCTOR = "api/doctors/addDoctor";
    public static final String REQUEST_SEND_SMS = "doctor/api/appointment/sendSmsToPatients";
//            public static String BASE_URL = "http://192.168.0.182:3003/";

    public Context mContext;
    //Declared all URL used in app here
    public static final String LOGIN_WITH_OTP_URL = "authApi/authenticate/otpLogin";
    public static final String GET_PATIENT_LIST = "api/patient/getChatPatientList?docId=";
    public static final String DOCTOR_LIST_FILTER_URL = "api/patient/searchDoctors";

    public static final String SEND_MSG_TO_PATIENT = "api/chat/sendMsgToPatient";
    public static final String CHAT_HISTORY = "api/chat/getChatHistory?";

    public static final String CHAT_FILE_UPLOAD = "api/upload/chatDoc";
    public static final String GET_PATIENT_CHAT_LIST = "api/chat/getChatTabUsers?user1id=";

    public static final String LOGOUT = "api/doctors/logDoctorSignOut";
    public static final String ACTIVE = "api/doctors/logDoctorActivity";
    public static final String MY_RECORDS_UPLOAD = "api/upload/addOpdAttachments";


}



