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
    public static boolean DEV_BUILD = true;
    //Declared all URL used in app here

//      public static String BASE_URL = "http://192.168.0.182:3003/";
    public static String BASE_URL = "http://drrescribe.com:3003/";

    public Context mContext;
    //Declared all URL used in app here
    public static final String LOGIN_WITH_PASSWORD_URL = "";
    public static final String LOGIN_WITH_OTP_URL = "authApi/authenticate/otpLogin";
    public static final String GET_PATIENT_CHAT_LIST = "api/patient/getChatPatientList?docId=";
    public static final String DOCTOR_LIST_FILTER_URL = "api/patient/searchDoctors";

    public static final String SEND_MSG_TO_PATIENT = "api/chat/sendMsgToPatient";

}



