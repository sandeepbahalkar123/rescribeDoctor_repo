package com.rescribe.doctor.util;

import android.content.Context;

/**
 * Created by Sandeep Bahalkar
 */
public class Config {
    public static final String HTTP = "http://";
    public static final String API = "/api/";
    public static final String TOKEN_TYPE = "Bearer";
    public static final String LOGIN_URL = "authApi/authenticate/login";
    public static final String VERIFY_SIGN_UP_OTP = "authApi/authenticate/verifyOTP";
    public static final String SIGN_UP_URL = "authApi/authenticate/signUp";
    public static boolean DEV_BUILD = true;
    //Declared all URL used in app here

//    public static String BASE_URL = "http://drrescribe.com:3003/";
      public static String BASE_URL = "http://192.168.0.182:3003/";

    public Context mContext;
    //Declared all URL used in app here
    public static final String LOGIN_WITH_PASSWORD_URL = "";
    public static final String LOGIN_WITH_OTP_URL = "authApi/authenticate/otpLogin";
    public static final String PRESCRIPTION_URL = "api/patient/getPatientPrescriptions?patientId=";
    public static final String DOCTOR_LIST_URL = "api/patient/getDoctorList?patientId=";
    public static final String ONE_DAY_VISIT_URL = "api/patient/getPatientOneDayVisit?opdId=";
    public static final String DOCTOR_LIST_FILTER_URL = "api/patient/searchDoctors";
    public static final String NOTIFICATION_URL = "api/patient/getPrescriptionNotifications?patientId=";
    public static final String RESPOND_TO_NOTIFICATION_URL = "api/patient/logMedicinesTakenInfo";
    public static final String FILTER_DOCTOR_LIST = "api/patient/getDoctorNameList";
    public static final String FILTER_DOCTOR_SPECIALIST_LIST = "api/patient/getDoctorSpeciality";
    public static final String CASE_DETAILS_LIST = "api/patient/getVitalCaseHeadings";
    public static final String APPOINTMENTS = "api/patient/appointments";
    public static final String APPOINTMENTS_DETAILS_URL = "api/patient/appointments?patientId=";

    public static final String INVESTIGATION_LIST = "api/patient/getInvestigationNotifications";
    public static final String INVESTIGATION_UPLOAD_BY_GMAIL = "api/patient/updateGmailInvestigationUploadStatus";
    public static final String INVESTIGATION_UPLOAD = "api/upload/InvestigationDocs";

    public static final String MY_RECORDS_DOCTOR_LIST = "api/doctors/getDoctorsWithPatientVisits";
    public static final String LIST_ALL_MY_RECORD = "api/records/getRecordList?patientId=";

    public static final String MY_RECORDS_UPLOAD = "api/upload/myRecords";
    public static final String MY_RECORDS_ADD_DOCTOR = "api/doctors/addDoctor";

}



