package com.rescribe.doctor.services;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;

import com.rescribe.doctor.helpers.database.AppDBHelper;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.singleton.Device;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.Config;
import com.rescribe.doctor.util.RescribeConstants;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadServiceBroadcastReceiver;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;

import static com.rescribe.doctor.util.RescribeConstants.FILE_STATUS.COMPLETED;
import static com.rescribe.doctor.util.RescribeConstants.FILE_STATUS.FAILED;
import static com.rescribe.doctor.util.RescribeConstants.FILE_STATUS.UPLOADING;

/**
 * Created by ganeshs on 21/03/18.
 */

public class CheckPendingUploads {
    private Context context;
    private AppDBHelper appDBHelper;

    public CheckPendingUploads() {
    }

    void check() {

        UploadNotificationConfig uploadNotificationConfig = new UploadNotificationConfig();
        uploadNotificationConfig.setTitleForAllStatuses("Document Uploading");
        uploadNotificationConfig.setIconColorForAllStatuses(Color.parseColor("#04abdf"));
        uploadNotificationConfig.setClearOnActionForAllStatuses(true);

        Cursor cursor = appDBHelper.getRecordUploads();

        String Url = Config.BASE_URL + Config.MY_RECORDS_UPLOAD;
        String authorizationString = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.AUTHTOKEN, context);
        Device device = Device.getInstance(context);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                int uploadStatus = cursor.getInt(cursor.getColumnIndex(AppDBHelper.MY_RECORDS.UPLOAD_STATUS));

                if (uploadStatus == FAILED) {

                    String uploadId = cursor.getString(cursor.getColumnIndex(AppDBHelper.MY_RECORDS.UPLOAD_ID));

                    appDBHelper.updateRecordUploads(uploadId, UPLOADING);

                    String mOpdtime = cursor.getString(cursor.getColumnIndex(AppDBHelper.MY_RECORDS.OPD_TIME));
                    String visitDate = cursor.getString(cursor.getColumnIndex(AppDBHelper.MY_RECORDS.VISIT_DATE));
                    String patientId = cursor.getString(cursor.getColumnIndex(AppDBHelper.MY_RECORDS.PATIENT_ID));
                    int docId = cursor.getInt(cursor.getColumnIndex(AppDBHelper.MY_RECORDS.DOC_ID));
                    String opdId = cursor.getString(cursor.getColumnIndex(AppDBHelper.MY_RECORDS.OPD_ID));
                    String mHospitalId = cursor.getString(cursor.getColumnIndex(AppDBHelper.MY_RECORDS.HOSPITAL_ID));
                    String mHospitalPatId = cursor.getString(cursor.getColumnIndex(AppDBHelper.MY_RECORDS.HOSPITAL_PAT_ID));
                    String mLocationId = cursor.getString(cursor.getColumnIndex(AppDBHelper.MY_RECORDS.LOCATION_ID));
                    String imagePath = cursor.getString(cursor.getColumnIndex(AppDBHelper.MY_RECORDS.IMAGE_PATH));
                    String caption = cursor.getString(cursor.getColumnIndex(AppDBHelper.MY_RECORDS.PARENT_CAPTION));

                    String currentOpdTime;

                    if (mOpdtime.equals(""))
                        currentOpdTime = CommonMethods.getCurrentTimeStamp(RescribeConstants.DATE_PATTERN.HH_mm_ss);
                    else
                        currentOpdTime = mOpdtime;

                    String visitDateToPass = CommonMethods.getFormattedDate(visitDate, RescribeConstants.DATE_PATTERN.DD_MM_YYYY, RescribeConstants.DATE_PATTERN.YYYY_MM_DD);

                    try {

                        MultipartUploadRequest uploadRequest = new MultipartUploadRequest(context, uploadId, Url)
                                .setNotificationConfig(uploadNotificationConfig)
                                .setMaxRetries(RescribeConstants.MAX_RETRIES)
                                .addHeader(RescribeConstants.AUTHORIZATION_TOKEN, authorizationString)
                                .addHeader(RescribeConstants.DEVICEID, device.getDeviceId())
                                .addHeader(RescribeConstants.OS, device.getOS())
                                .addHeader(RescribeConstants.OSVERSION, device.getOSVersion())
                                .addHeader(RescribeConstants.DEVICE_TYPE, device.getDeviceType())

                                .addHeader("patientid", patientId)
                                .addHeader("docid", String.valueOf(docId))
                                .addHeader("opddate", visitDateToPass)
                                .addHeader("opdtime", currentOpdTime)
                                .addHeader("opdid", opdId)
                                .addHeader("hospitalid", mHospitalId)
                                .addHeader("hospitalpatid", mHospitalPatId)
                                .addHeader("locationid", mLocationId)
                                .addFileToUpload(imagePath, "attachment");

                        if (!caption.isEmpty()) {
                            uploadRequest.addHeader("captionname", caption);
                        }

                        uploadRequest.startUpload();

                    } catch (MalformedURLException | FileNotFoundException fe) {
                        fe.printStackTrace();
                    }

                }
            }
        }

        cursor.close();
        appDBHelper.close();
    }

    void onCreate(Context mContext) {
        this.context = mContext;
        appDBHelper = new AppDBHelper(context);
        check();
        broadcastReceiver.register(context);
    }

    void onDestroy() {
        broadcastReceiver.unregister(context);
    }

    private UploadServiceBroadcastReceiver broadcastReceiver = new UploadServiceBroadcastReceiver() {
        @Override
        public void onProgress(Context context, UploadInfo uploadInfo) {
            // your implementation
        }

        @Override
        public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
            // your implementation
            appDBHelper.updateRecordUploads(uploadInfo.getUploadId(), FAILED);
        }

        @Override
        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
            // your implementation
            appDBHelper.updateRecordUploads(uploadInfo.getUploadId(), COMPLETED);
        }

        @Override
        public void onCancelled(Context context, UploadInfo uploadInfo) {
            // your implementation
        }
    };
}
