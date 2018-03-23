package com.rescribe.doctor.helpers.patient_detail;

import android.content.Context;
import com.android.volley.Request;
import com.rescribe.doctor.R;
import com.rescribe.doctor.interfaces.ConnectionListener;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.case_details.CaseDetailsModel;
import com.rescribe.doctor.model.patient.patient_history.PatientHistoryBaseModel;
import com.rescribe.doctor.model.patient.patient_history.PatientHistoryDataModel;
import com.rescribe.doctor.model.patient.patient_history.PatientHistoryInfo;
import com.rescribe.doctor.model.patient.patient_history.PatientHistoryInfoMonthContainer;
import com.rescribe.doctor.model.patient.patient_history.RequestForPatientHistory;
import com.rescribe.doctor.network.ConnectRequest;
import com.rescribe.doctor.network.ConnectionFactory;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.Config;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class PatientDetailHelper implements ConnectionListener {

    private String TAG = this.getClass().getName();
    private Context mContext;
    private HelperResponse mHelperResponseManager;
    private Map<String, Map<String, ArrayList<PatientHistoryInfo>>> yearWiseSortedPatientHistoryInfo = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public PatientDetailHelper(Context context, HelperResponse oneDayVisitActivity) {
        this.mContext = context;
        this.mHelperResponseManager = oneDayVisitActivity;
    }


    public Map<String, Map<String, ArrayList<PatientHistoryInfo>>> getYearWiseSortedPatientHistoryInfo() {
        return yearWiseSortedPatientHistoryInfo;
    }

    @Override
    public void onResponse(int responseResult, CustomResponse customResponse, String mOldDataTag) {

        switch (responseResult) {
            case ConnectionListener.RESPONSE_OK:
                switch (mOldDataTag) {
                    case RescribeConstants.TASK_ONE_DAY_VISIT:
                        CaseDetailsModel model = (CaseDetailsModel) customResponse;
                        mHelperResponseManager.onSuccess(mOldDataTag, model.getData());
                        break;
                    case RescribeConstants.TASK_PATIENT_HISTORY: {

                        PatientHistoryBaseModel newBaseModel = (PatientHistoryBaseModel) customResponse;
                        PatientHistoryDataModel dataModel = newBaseModel.getPatientHistoryDataModel();
                        PatientHistoryInfoMonthContainer patientHistoryInfoMonthContainer = dataModel.getPatientHistoryInfoMonthContainer();

                        if (patientHistoryInfoMonthContainer != null) {
                            Map<String, ArrayList<PatientHistoryInfo>> monthWiseSortedPatientHistory = patientHistoryInfoMonthContainer.getMonthWiseSortedPatientHistory();
                            if(monthWiseSortedPatientHistory.size()>0)
                                yearWiseSortedPatientHistoryInfo.put(patientHistoryInfoMonthContainer.getYear(), monthWiseSortedPatientHistory);
                        }
                        mHelperResponseManager.onSuccess(mOldDataTag, customResponse);
                    }
                    break;
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

    //get case study list
    public void doGetOneDayVisit(String opdId, String patientID) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_ONE_DAY_VISIT, Request.Method.GET, false);
        mConnectionFactory.setHeaderParams();
        String docId = (RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, mContext));
        mConnectionFactory.setUrl(Config.ONE_DAY_VISIT_URL + opdId + "&patientId="+ patientID+"&docId="+docId);
        mConnectionFactory.createConnection(RescribeConstants.TASK_ONE_DAY_VISIT);
       /* try {
            InputStream is = mContext.getAssets().open("patient_details.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");

            CaseDetailsModel data = new Gson().fromJson(json, CaseDetailsModel.class);
            onResponse(ConnectionListener.RESPONSE_OK,data,RescribeConstants.TASK_ONE_DAY_VISIT);

        } catch (IOException ex) {
            ex.printStackTrace();
        }*/
    }

    public void doGetPatientHistory(String patientID , String year) {
      /*  try {
            InputStream is = mContext.getAssets().open("patient_history.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");

            PatientHistoryBaseModel data = new Gson().fromJson(json, PatientHistoryBaseModel.class);
            onResponse(ConnectionListener.RESPONSE_OK,data,RescribeConstants.TASK_PATIENT_HISTORY);

        } catch (IOException ex) {
            ex.printStackTrace();
        }*/
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_PATIENT_HISTORY, Request.Method.POST, true);
        RequestForPatientHistory mRequestForPatientHistory = new RequestForPatientHistory();
        mRequestForPatientHistory.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, mContext)));
        String date = CommonMethods.getCurrentDate(RescribeConstants.DATE_PATTERN.YYYY_MM_DD);
        mRequestForPatientHistory.setPatientId(Integer.valueOf(patientID));
        mRequestForPatientHistory.setYear(year);
        mConnectionFactory.setPostParams(mRequestForPatientHistory);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.GET_PATIENT_HISTORY);
        mConnectionFactory.createConnection(RescribeConstants.TASK_PATIENT_HISTORY);
    }
}

