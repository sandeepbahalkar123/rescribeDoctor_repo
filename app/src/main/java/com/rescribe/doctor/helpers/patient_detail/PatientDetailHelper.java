package com.rescribe.doctor.helpers.patient_detail;

import android.content.Context;

import com.android.volley.Request;
import com.rescribe.doctor.R;
import com.rescribe.doctor.interfaces.ConnectionListener;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.case_details.CaseDetailsModel;
import com.rescribe.doctor.model.case_details.VisitCommonData;
import com.rescribe.doctor.model.patient.delete_attachment_req_model.DeleteAttachmentReqModel;
import com.rescribe.doctor.model.patient.delete_notes_record.DeleteNotesReqModel;
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
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import static com.rescribe.doctor.util.RescribeConstants.SUCCESS;

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
                        if (newBaseModel.getCommon().getStatusCode().equals(SUCCESS)) {
                            PatientHistoryDataModel dataModel = newBaseModel.getPatientHistoryDataModel();
                            PatientHistoryInfoMonthContainer patientHistoryInfoMonthContainer = dataModel.getPatientHistoryInfoMonthContainer();
                            if (patientHistoryInfoMonthContainer != null) {
                                Map<String, ArrayList<PatientHistoryInfo>> monthWiseSortedPatientHistory = patientHistoryInfoMonthContainer.getMonthWiseSortedPatientHistory();
                                if (!monthWiseSortedPatientHistory.isEmpty())
                                    yearWiseSortedPatientHistoryInfo.put(patientHistoryInfoMonthContainer.getYear(), monthWiseSortedPatientHistory);
                            }
                            mHelperResponseManager.onSuccess(mOldDataTag, customResponse);
                        } else
                            CommonMethods.showToast(mContext, newBaseModel.getCommon().getStatusMessage());
                    }
                    break;
                    default:
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

    //get case study list
    public void doGetOneDayVisit(String opdId, String patientID) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_ONE_DAY_VISIT, Request.Method.GET, false);
        mConnectionFactory.setHeaderParams();
        String docId = (RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, mContext));
        mConnectionFactory.setUrl(Config.ONE_DAY_VISIT_URL + opdId + "&patientId=" + patientID + "&docId=" + docId + "&appName=doctor");
        mConnectionFactory.createConnection(RescribeConstants.TASK_ONE_DAY_VISIT);
    }

    public void doGetPatientHistory(String patientID, String year, boolean getPatientInfo, String hospitalPatid) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_PATIENT_HISTORY, Request.Method.POST, false);
        RequestForPatientHistory mRequestForPatientHistory = new RequestForPatientHistory();
        mRequestForPatientHistory.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, mContext)));
        String date = CommonMethods.getCurrentDate(RescribeConstants.DATE_PATTERN.YYYY_MM_DD);
        mRequestForPatientHistory.setPatientId(Integer.valueOf(patientID));
        mRequestForPatientHistory.setHospitalPatId(Integer.valueOf(hospitalPatid));
        mRequestForPatientHistory.setYear(year);
        mRequestForPatientHistory.setGetPatientInfo(getPatientInfo);
        mConnectionFactory.setPostParams(mRequestForPatientHistory);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.GET_PATIENT_HISTORY);
        mConnectionFactory.createConnection(RescribeConstants.TASK_PATIENT_HISTORY);
    }

    //get case study list
    public void deleteSelectedAttachments(HashSet<VisitCommonData> list, String patientID) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_DELETE_PATIENT_OPD_ATTCHMENTS, Request.Method.POST, false);

        ArrayList<DeleteAttachmentReqModel.AttachmentData> deleteAttList = new ArrayList<>();
        DeleteAttachmentReqModel delete = new DeleteAttachmentReqModel();

        for (VisitCommonData s : list) {
            DeleteAttachmentReqModel.AttachmentData i = new DeleteAttachmentReqModel.AttachmentData();
            i.setId("" + s.getId());
            String url = s.getUrl();
            i.setFileName("" + url.substring(url.lastIndexOf('/') + 1, url.length()));
            i.setPatientId(Integer.parseInt(patientID));
            deleteAttList.add(i);
        }
        delete.setAttachmentDetails(deleteAttList);

        mConnectionFactory.setPostParams(delete);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.DELETE_PATIENT_OPD_ATTCHMENTS);
        mConnectionFactory.createConnection(RescribeConstants.TASK_DELETE_PATIENT_OPD_ATTCHMENTS);
    }


    public void deleteSelectedNotes(HashSet<VisitCommonData> list) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_DELETE_PATIENT_OPD_NOTES, Request.Method.POST, false);

        ArrayList<DeleteNotesReqModel.NotesData> deleteNotesList = new ArrayList<>();
        DeleteNotesReqModel delete = new DeleteNotesReqModel();

        for (VisitCommonData s : list) {
            DeleteNotesReqModel.NotesData i = new DeleteNotesReqModel.NotesData();
            i.setId("" + s.getId());
            String url = s.getUrl();
            i.setFileName("" + url.substring(url.lastIndexOf('/') + 1, url.length()));
            deleteNotesList.add(i);
        }
        delete.setNotesDetails(deleteNotesList);
        mConnectionFactory.setPostParams(delete);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.DELETE_PATIENT_OPD_NOTES);
        mConnectionFactory.createConnection(RescribeConstants.TASK_DELETE_PATIENT_OPD_NOTES);
    }

    public void getOpdHeadersList(String opdName, String s) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, RescribeConstants.TASK_GET_OPD_HEADERS_LIST, Request.Method.GET, false);
        mConnectionFactory.setHeaderParams();
        String docId = (RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, mContext));
        mConnectionFactory.setUrl(Config.GET_OPD_HEADERS_LIST + docId + "&type=" + opdName + "&searchText=" + s);
        mConnectionFactory.createConnection(RescribeConstants.TASK_GET_OPD_HEADERS_LIST);
    }
}

