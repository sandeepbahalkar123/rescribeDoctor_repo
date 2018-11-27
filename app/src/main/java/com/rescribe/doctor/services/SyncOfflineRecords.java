package com.rescribe.doctor.services;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;

import com.rescribe.doctor.helpers.database.AppDBHelper;
import com.rescribe.doctor.model.UploadStatus;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.services.add_record_upload_Service.AddRecordService;
import com.rescribe.doctor.singleton.Device;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.Config;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;
import java.util.HashMap;

import static com.rescribe.doctor.ui.activities.add_records.SelectedRecordsActivity.FILELIST;
import static com.rescribe.doctor.util.RescribeConstants.FILE_STATUS.FAILED;

/**
 * Created by ganeshs on 21/03/18.
 */

public class SyncOfflineRecords {
    public static final String ATTATCHMENT_DOC_UPLOAD = "com.rescribe.doctor.ATTATCHMENT_DOC_UPLOAD";
    private ArrayList<UploadStatus> uploadDataList = new ArrayList<>();
    private Context context;
    private AppDBHelper appDBHelper;

    SyncOfflineRecords() {
    }

    void check() {

        Cursor cursor = appDBHelper.getRecordUploads();


        if (cursor.getCount() > 0) {
            String authorizationString = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.AUTHTOKEN, context);
            Device device = Device.getInstance(context);


            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {

                    int uploadStatus = cursor.getInt(cursor.getColumnIndex(AppDBHelper.MY_RECORDS.UPLOAD_STATUS));
                    String patientId = cursor.getString(cursor.getColumnIndex(AppDBHelper.MY_RECORDS.PATIENT_ID));

                    if (uploadStatus == FAILED && appDBHelper.isPatientSynced(patientId)) {

//                      UploadNotificationConfig uploadConfig = getUploadConfig(context);

                        String uploadId = cursor.getString(cursor.getColumnIndex(AppDBHelper.MY_RECORDS.UPLOAD_ID));

                        appDBHelper.updateRecordUploads(uploadId, uploadStatus, "");

                        String mOpdtime = cursor.getString(cursor.getColumnIndex(AppDBHelper.MY_RECORDS.OPD_TIME));
                        String visitDate = cursor.getString(cursor.getColumnIndex(AppDBHelper.MY_RECORDS.VISIT_DATE));

                        int docId = cursor.getInt(cursor.getColumnIndex(AppDBHelper.MY_RECORDS.DOC_ID));
                        String opdId = cursor.getString(cursor.getColumnIndex(AppDBHelper.MY_RECORDS.OPD_ID));
                        String mHospitalId = cursor.getString(cursor.getColumnIndex(AppDBHelper.MY_RECORDS.HOSPITAL_ID));
                        String mHospitalPatId = cursor.getString(cursor.getColumnIndex(AppDBHelper.MY_RECORDS.HOSPITAL_PAT_ID));
                        String mLocationId = cursor.getString(cursor.getColumnIndex(AppDBHelper.MY_RECORDS.LOCATION_ID));
                        String imagePath = cursor.getString(cursor.getColumnIndex(AppDBHelper.MY_RECORDS.IMAGE_PATH));
                        String caption = cursor.getString(cursor.getColumnIndex(AppDBHelper.MY_RECORDS.PARENT_CAPTION));
                        String aptId = cursor.getString(cursor.getColumnIndex(AppDBHelper.MY_RECORDS.APT_ID));

                        String recordType = cursor.getString(cursor.getColumnIndex(AppDBHelper.MY_RECORDS.RECORD_TYPE));
                        String fileId = cursor.getString(cursor.getColumnIndex(AppDBHelper.MY_RECORDS.FILE_ID));
                        String orderId = cursor.getString(cursor.getColumnIndex(AppDBHelper.MY_RECORDS.ORDER_ID));

                        String currentOpdTime;

                        if (mOpdtime.equals(""))
                            currentOpdTime = CommonMethods.getCurrentTimeStamp(RescribeConstants.DATE_PATTERN.HH_mm_ss);
                        else
                            currentOpdTime = mOpdtime;

                        String visitDateToPass = CommonMethods.getFormattedDate(visitDate, RescribeConstants.DATE_PATTERN.DD_MM_YYYY, RescribeConstants.DATE_PATTERN.YYYY_MM_DD);


                        HashMap<String, String> headers = new HashMap<>();
                        headers.put(RescribeConstants.AUTHORIZATION_TOKEN, authorizationString);
                        headers.put(RescribeConstants.DEVICEID, device.getDeviceId());
                        headers.put(RescribeConstants.OS, device.getOS());
                        headers.put(RescribeConstants.OSVERSION, device.getOSVersion());
                        headers.put(RescribeConstants.DEVICE_TYPE, device.getDeviceType());
                        headers.put("patientid", patientId);
                        headers.put("docid", String.valueOf(docId));
                        headers.put("opddate", visitDateToPass);
                        headers.put("opdtime", currentOpdTime);
                        headers.put("opdid", opdId);
                        headers.put("hospitalid", String.valueOf(mHospitalId));
                        headers.put("hospitalpatid", mHospitalPatId);
                        headers.put("locationid", mLocationId);
                        headers.put("aptid", String.valueOf(aptId));

                        // Added in 5 to 6
                        headers.put("orderid", orderId);
                        headers.put("fileid", fileId);

                        UploadStatus images = new UploadStatus(uploadId, visitDate, mOpdtime, caption, imagePath, recordType, headers);
                        uploadDataList.add(images);
                    }

                    cursor.moveToNext();
                }


            }
            if (uploadDataList.size() != 0) {
                Intent intent = new Intent(context, AddRecordService.class);
                intent.putParcelableArrayListExtra(FILELIST, uploadDataList);
                ContextCompat.startForegroundService(context, intent);
            }

        }

        cursor.close();
        appDBHelper.close();
    }

    /*public static UploadNotificationConfig getUploadConfig(Context context) {
        UploadNotificationConfig uploadNotificationConfig = new UploadNotificationConfig();
        uploadNotificationConfig.setIconColorForAllStatuses(context.getResources().getColor(R.color.tagColor));

        uploadNotificationConfig.setTitleForAllStatuses(context.getString(R.string.app_name))
                .setRingToneEnabled(true);

        uploadNotificationConfig.getProgress().message = "Uploading record at " + UPLOAD_RATE + " - " + PROGRESS;
//        uploadNotificationConfig.getProgress().iconResourceID = R.drawable.ic_file_upload_white_24dp;
        uploadNotificationConfig.getProgress().iconColorResourceID = context.getResources().getColor(R.color.tagColor);

        uploadNotificationConfig.getCompleted().message = "Upload completed successfully in " + ELAPSED_TIME;
//        uploadNotificationConfig.getCompleted().iconResourceID = R.drawable.ic_file_upload_white_24dp;
        uploadNotificationConfig.getCompleted().iconColorResourceID = Color.GREEN;

        uploadNotificationConfig.getError().message = "Error while uploading";
//        uploadNotificationConfig.getError().iconResourceID = R.drawable.ic_file_upload_white_24dp;
        uploadNotificationConfig.getError().iconColorResourceID = Color.RED;

        uploadNotificationConfig.getCancelled().message = "Upload has been cancelled";
//        uploadNotificationConfig.getCancelled().iconResourceID = R.drawable.ic_file_upload_white_24dp;
        uploadNotificationConfig.getCancelled().iconColorResourceID = Color.YELLOW;

        return uploadNotificationConfig;
    }*/

    void onCreate(Context mContext) {
        this.context = mContext;
        appDBHelper = new AppDBHelper(context);
        if (RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.LOGIN_STATUS, mContext).equals(RescribeConstants.YES))
            check();
    }
}
