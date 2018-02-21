package com.rescribe.doctor.util;


/**
 * @author Sandeep Bahalkar
 */
public class RescribeConstants {

    public static final String RESCRIBE_LOG_FOLDER = "Rescribe_LOG";
    public static final String RESCRIBE_LOG_FILE = "Rescribe_LOG_FILE.txt";

    public static final String MT_TABLET = "TABLET";
    public static final String MT_SYRUP = "SYRUP";
    public static final String MT_OINTMENT = "OINTMENT";
    public static final String DD_MM_YYYY = "dd-MM-yyyy";
    public static final String YES = "yes";
    public static final String DOCUMENTS = "documents";
    public static final String ALERT = "alert";
    public static final String ID = "_id";
    public static final String USER_ID = "User-ID";
    public static final String DEVICEID = "Device-Id";
    public static final String OS = "OS";
    public static final String OSVERSION = "OsVersion";
    public static final String DEVICE_TYPE = "DeviceType";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String TOKEN_TYPE = "token_type";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String LDPI = "/LDPI/";
    public static final String MDPI = "/MDPI/";
    public static final String HDPI = "/HDPI/";
    public static final String XHDPI = "/XHDPI/";
    public static final String XXHDPI = "/XXHDPI/";
    public static final String XXXHDPI = "/XXXHDPI/";
    public static final String TABLET = "Tablet";
    public static final String PHONE = "Phone";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String AUTHORIZATION = "Authorization";
    public static final String AUTHORIZATION_TOKEN = "authtoken";
    public static final String AUTH_KEY = "Auth-Key";
    public static final String CLIENT_SERVICE = "Client-Service";
    public static final String GRANT_TYPE_KEY = "grant_type";

    public static final String APPLICATION_FORM_DATA = "multipart/form-data";
    public static final String APPLICATION_URL_ENCODED = "application/x-www-form-urlencoded; charset=UTF-8";
    public static final String APPLICATION_JSON = "application/json; charset=utf-8";
    //--- Request Params
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String LOGIN_SUCCESS = "LOGIN_SUCCESS";
    public static final String CLIENT_ID_KEY = "client_id";
    public static final String CLIENT_ID_VALUE = "334a059d82304f4e9892ee5932f81425";
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    public static final String APPOINTMENT_TIME = "appointment_time";
    public static final String NOTIFICATION_TIME = "notification_time";
    public static final String APPOINTMENT_DATE = "appointment_date";
    public static final String NOTIFICATION_DATE = "notification_date";
    public static final String TIME = "time";

    // Connection codes
    public static final String MONTH = "month";
    public static final String YEAR = "year";
    public static final String DATE = "date";
    public static final String MEDICINE_NAME = "medicine_name";
    public static final String MEDICINE_SLOT = "edicine_slot";
    public static final String MEDICINE_TYPE = "medicine_type";
    public static final String NOTIFICATION_ID = "notification_id";

    public static final String APPOINTMENT_NOTIFICATION_ID = "appointment_notification_id";
    public static final String APPOINTMENT_MESSAGE = "appointment_message";
    public static final String BLANK = "";
    //Click codes

    public static final String TASK_LOGIN = RescribeConstants.BLANK + 1;

    public static final String TASK_SIGN_UP = RescribeConstants.BLANK + 2;
    public static final String TASK_VERIFY_SIGN_UP_OTP = RescribeConstants.BLANK + 3;


    public static final String TASK_LOGIN_WITH_PASSWORD = RescribeConstants.BLANK + 4;
    public static final String TASK_LOGIN_WITH_OTP = RescribeConstants.BLANK + 5;
    public static final String TASK_DOCTOR_CONNECT_CHAT = RescribeConstants.BLANK + 6;
    public static final String TASK_DOCTOR_CONNECT = RescribeConstants.BLANK + 7;
    public static final String TASK_DOCTOR_FILTER_DOCTOR_SPECIALITY_LIST = RescribeConstants.BLANK + 8;
    public static final String TASK_GET_PATIENT_LIST = RescribeConstants.BLANK + 9;
    public static final String SEND_MESSAGE = RescribeConstants.BLANK + 10;
    public static final String CHAT_HISTORY = RescribeConstants.BLANK + 11;
    public static final String CHAT_USERS = RescribeConstants.BLANK + 12;
    public static final String GET_PATIENT_CHAT_LIST = RescribeConstants.BLANK + 13;

    public static final String ACTIVE_STATUS = RescribeConstants.BLANK + 14;
    public static final String LOGOUT = RescribeConstants.BLANK + 15;
    public static final String TASK_GET_APPOINTMENT_DATA = BLANK + 16;
    public static final String TASK_GET_PATIENT_DATA = BLANK + 17;
    public static final String TASK_ONE_DAY_VISIT = BLANK + 18;
    public static final String MY_RECORDS_DOCTOR_LIST = RescribeConstants.BLANK + 19;
    public static final String MY_RECORDS_ADD_DOCTOR = RescribeConstants.BLANK + 20;
    public static final String TASK_PATIENT_HISTORY = RescribeConstants.BLANK + 21;


    public static final String DATA = "DATA";
    public static final Integer SUCCESS = 200;

    public static final String TAKEN_DATE = "takenDate";

    public static final String TITLE = "title";
    public static final int MAX_RETRIES = 3;

    public static final String GMAIL_LOGIN = "gmail_login";
    public static final String FACEBOOK_LOGIN = "facebook_login";

    public static final int UPLOADING = 1;
    public static final int DOWNLOADING = 1;
    public static final int FAILED = 0;
    public static final int COMPLETED = 3;

    public static final String UPLOADING_STATUS = "uploading_status";
    public static final String INVESTIGATION_NOTIFICATION_TIME = "9:00 AM";
    public static final String APPOINTMENT_NOTIFICATION_TIME = "9:00 AM";
    public static final String GMAIL_PACKAGE = "com.google.android.gm";
    public static final String EMAIL = "email";
    public static final String OPD_ID = "opd_id";
    public static final String MYRECORDDATAMODEL = "myrecorddatamodel";
    public static final String CAPTION = "caption";
    public static final String TYPE_OF_LOGIN = "";
    public static final String SENDERID = "EMROTP";
    public static final String IS_URL = "isUrl";
    public static final String CHAT_REQUEST = "chat_request";
    public static final String SEARCH__REQUEST = "search_request";
    public static final String CONNECT_REQUEST = "connect_request";
    public static final String PATIENT_INFO = "patient_info";
    public static final int PLACE_PICKER_REQUEST = 99;
    public static final String APPOINTMENT_DATA = "appointment_data";
    public static final String MYPATIENTS_DATA = "mypatients_data";
    public static final String DOCTOR_IMAGE_URL = "doctor_iamge_url";
    public static final String PATIENT_VISIT_DATE = "patient_visit_date";
    public static final String PATIENT_ID = "patient_id";
    public static final String NO = "no";
    public static final String DOCTORS_ID = "doctor_id";
    public static final String VISIT_DATE = "visitDate";
    public static final String PATIENT_OBJECT = "patient_object";
    public static final String PATIENT_NAME = "patient_name";
    public static final String LONGPRESSED = "long_pressed";
    public static final String DATES_LIST = "dates_list";
    public static final String DATES_INFO = "dates_info";
    public static final String FILTER_STATUS_LIST = "filter_status_list";


    public static String HEADER_COLOR = "#E4422C";
    public static String BUTTON_TEXT_COLOR = "#FFFFFF";
    public static String TEXT_COLOR = "#000000";

    public static class USER_STATUS {
        public static final String ONLINE = "Online";
        public static final String OFFLINE = "Offline";
        public static final String TYPING = "Typing";
        public static final String IDLE = "Idle";
    }

    // change
    public static class MESSAGE_STATUS {
        public static final String SEEN = "seen";
        public static final String REACHED = "reached";
        public static final String SENT = "sent";

        public static final int READ = 1;
        public static final int UNREAD = 0;
    }

    public static class PRESCRIPTION_LIST_PARAMS {
        public static final String PATIENT_NAME = "User-ID";
        public static final String FILE_TYPE = "fileType";
        public static final String DATE_TYPE = "dateType";
        public static final String FROM_DATE = "fromDate";
        public static final String TO_DATE = "toDate";
        public static final String ANNOTATION_TEXT = "annotationText";
        public static final String DOC_TYPE_ID = "DocTypeId";
    }

    public static class DATE_PATTERN {
        public static String YYYY_MM_DD_hh_mm_a = "yyyy-MM-dd hh:mm a";
        public static String DD_MM = "dd/MM";
        public static final String MM = "MM";
        public static final String MMM = "MMM";
        public static final String UTC_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        public final static String YYYY_MM_DD = "yyyy-MM-dd";
        public final static String DD_MM_YYYY = "dd-MM-yyyy";
        public final static String DD_MMMM_YYYY = "dd MMMM yyyy"; // 12-September-2017
        public final static String hh_mm_a = "hh:mm a";
        public static final String TOTIMEZONE = "Asia/Kolkata";
        public final static String EEEE_dd_MMM_yyyy_hh_mm_a = "EEEE dd MMM yyyy | hh:mm a";
        public static String HH_mm_ss = "HH:mm:ss";
        public static String DD_MM_YYYY_hh_mm = "dd/MM/yyyy hh:mm aa";
        public static String HH_MM = "hh:mm";
        public static String MMM_YYYY = "MMM, yyyy";
        public static String MMM_YY = "MMM, yy";
        public static String DD_MM_YYYY_hh_mm_ss = "dd-MM-yyyy hh:mm:ss";
        public static String YYYY_MM_DD_hh_mm_ss = "yyyy-MM-dd hh:mm:ss";
        public static String YYYY_MM_DD_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
    }

    public static class TIME_STAMPS {
        public static final int FIVE_FIFTY = 500;
        public static int ONE_SECONDS = 1000;
        public static int TWO_SECONDS = 2000;
        public static int THREE_SECONDS = 3000;
    }

    public static class FILE {
        public static final String IMG = "img";
        public static final String DOC = "doc";
        public static final String VID = "vid";
        public static final String AUD = "aud";
        public static final String LOC = "loc";
    }

    public static class FILE_EMOJI {
        public static final String IMG_FILE = "🏞 Image";
        public static final String DOC_FILE = "📒 Document";
        public static final String VID_FILE = "📹 Video";
        public static final String AUD_FILE = "🔊 Audio";
        public static final String LOC_FILE = "📍 Location";
    }

    public class INVESTIGATION_KEYS {
        public static final String INVESTIGATION_DATE = "investigation_date";
        public static final String INVESTIGATION_TIME = "investigation_time";
        public static final String INVESTIGATION_TEMP_DATA = "investigation_temp_data";
        public static final String INVESTIGATION_MESSAGE = "investigation_message";
        public static final String INVESTIGATION_DATA = "investigation_data";
        public static final String INVESTIGATION_NOTIFICATION_ID = "investigation_notification_id";
        public static final String IMAGE_ID = "imgId";
        public static final String INV_ID = "invId";
    }

    public class PATIENT_OPDS_STATUS {
        public static final String OPD_COMPLETED = "opd completed";
        public static final String OPD_SAVED = "opd saved";
        public static final String ONLY_ATTACHMENTS = "only attachments";
        public static final String NO_SHOW = "no show";
    }
}


