package com.rescribe.doctor.helpers.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rescribe.doctor.helpers.doctor_patients.PatientList;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.chat.MQTTMessage;
import com.rescribe.doctor.model.chat.StatusInfo;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static com.rescribe.doctor.services.MQTTService.DOCTOR;
import static com.rescribe.doctor.services.MQTTService.PATIENT;
import static com.rescribe.doctor.util.RescribeConstants.FILE_STATUS.FAILED;
import static com.rescribe.doctor.util.RescribeConstants.MESSAGE_STATUS.REACHED;
import static com.rescribe.doctor.util.RescribeConstants.MESSAGE_STATUS.READ;
import static com.rescribe.doctor.util.RescribeConstants.MESSAGE_STATUS.SEEN;
import static com.rescribe.doctor.util.RescribeConstants.MESSAGE_STATUS.SENT;
import static com.rescribe.doctor.util.RescribeConstants.MESSAGE_STATUS.UNREAD;

public class AppDBHelper extends SQLiteOpenHelper {

    private final String TAG = "DrRescribe/AppDBHelper";

    private static final String DATABASE_NAME = "MyRescribe.sqlite";
    private static final String DB_PATH_SUFFIX = "/data/data/com.rescribe.doctor/databases/"; // Change
    private static final int DB_VERSION = 1;
    public static final String APP_DATA_TABLE = "PrescriptionData";
    public static final String COLUMN_ID = "dataId";
    public static final String COLUMN_DATA = "data";

    private static AppDBHelper instance = null;
    private Context mContext;

    public AppDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
        this.mContext = context;
        checkDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        // db.execSQL("CREATE TABLE IF NOT EXISTS " + APP_DATA_TABLE + "(dataId integer, data text)");
        // db.execSQL("CREATE TABLE IF NOT EXISTS " + PREFERENCES_TABLE + "(userId integer, breakfastTime text, lunchTime text, dinnerTime text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
//        db.execSQL("DROP TABLE IF EXISTS " + APP_DATA_TABLE);
//        db.execSQL("DROP TABLE IF EXISTS " + PREFERENCES_TABLE);
        deleteDatabase();
        copyDataBase();
//        onCreate(db);
    }

    public static synchronized AppDBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new AppDBHelper(context);
        }
        return instance;
    }

    public boolean insertData(String dataId, String data) {
        if (dataTableNumberOfRows(dataId) == 0) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put("dataId", dataId);
            contentValues.put("data", data);

            db.insert(APP_DATA_TABLE, null, contentValues);
        } else {
            updateData(dataId, data);
        }
        return true;
    }

    public Cursor getData(String dataId) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("select * from " + APP_DATA_TABLE + " where dataId=" + dataId + "", null);
    }

    public int dataTableNumberOfRows(String dataId) {
        SQLiteDatabase db = getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, APP_DATA_TABLE, "dataId = ? ", new String[]{dataId});
    }

    private boolean updateData(String dataId, String data) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("data", data);

        db.update(APP_DATA_TABLE, contentValues, "dataId = ? ", new String[]{dataId});
        return true;
    }

    public Integer deleteData(Integer dataId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(APP_DATA_TABLE,
                "dataId = ? ",
                new String[]{Integer.toString(dataId)});
    }

    private void copyDataBase() {
        CommonMethods.Log(TAG,
                "New database is being copied to device!");
        byte[] buffer = new byte[1024];
        OutputStream myOutput = null;
        int length;
        // Open your local db as the input stream
        InputStream myInput = null;
        try {
            myInput = mContext.getAssets().open(DATABASE_NAME);
            // transfer bytes from the inputfile to the
            // outputfile

            // check if databases folder exists, if not create one and its subfolders
            File databaseFile = new File(DB_PATH_SUFFIX);
            if (!databaseFile.exists()) {
                databaseFile.mkdir();
            }

            String path = databaseFile.getAbsolutePath() + "/" + DATABASE_NAME;

            myOutput = new FileOutputStream(path);

            CommonMethods.Log(TAG,
                    "New database is being copied to device!" + path);
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myOutput.close();
            myOutput.flush();
            myInput.close();
            CommonMethods.Log(TAG,
                    "New database has been copied to device!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkDatabase() {
        File dbFile = mContext.getDatabasePath(DATABASE_NAME);
        CommonMethods.Log(TAG, "FILENAME " + dbFile.getAbsolutePath() + "|" + dbFile.getName());
        if (!dbFile.exists()) {
            copyDataBase();
        }
    }

    public void deleteDatabase() {
        File dbFile = mContext.getDatabasePath(DATABASE_NAME);
        dbFile.delete();

        CommonMethods.Log("DeletedOfflineDatabase", "APP_DATA , PREFERENCES TABLE, INVESTIGATION");
    }

    public int updateMessageUploadStatus(String messageId, int msgUploadStatus) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CHAT_MESSAGES.UPLOAD_STATUS, msgUploadStatus);
        return db.update(CHAT_MESSAGES.CHAT_MESSAGES_TABLE, contentValues, CHAT_MESSAGES.MSG_ID + " = ? AND " + CHAT_MESSAGES.SENDER + " = ?", new String[]{messageId, DOCTOR});
    }

    public MQTTMessage getChatMessageByMessageId(String messageId) {
        SQLiteDatabase db = getReadableDatabase();
        String countQuery = "select * from " + CHAT_MESSAGES.CHAT_MESSAGES_TABLE + " where " + CHAT_MESSAGES.MSG_ID + " = '" + messageId + "'";
        Cursor cursor = db.rawQuery(countQuery, null);

        MQTTMessage mqttMessage = null;

        if (cursor.moveToFirst()) {
            mqttMessage = new MQTTMessage();

            mqttMessage.setMsgId(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.MSG_ID)));
            mqttMessage.setMsg(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.MSG)));
            mqttMessage.setMsgTime(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.MSG_TIME)));
            mqttMessage.setSender(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.SENDER)));
            mqttMessage.setPatId(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.USER2ID)));
            mqttMessage.setDocId(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.USER1ID)));
            mqttMessage.setSenderName(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.SENDER_NAME)));

            mqttMessage.setSpecialization(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.SPECIALITY)));
            mqttMessage.setMsgStatus(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.MSG_STATUS)));
            mqttMessage.setSenderImgUrl(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.SENDER_IMG_URL)));
            mqttMessage.setFileUrl(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.FILE_URL)));
            mqttMessage.setFileType(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.FILE_TYPE)));

            mqttMessage.setSalutation(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.SALUTATION)));
            mqttMessage.setReceiverName(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.RECEIVER_NAME)));
            mqttMessage.setReceiverImgUrl(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.RECEIVER_IMG_URL)));

            mqttMessage.setUploadStatus(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.UPLOAD_STATUS)));
            mqttMessage.setDownloadStatus(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.DOWNLOAD_STATUS)));
            mqttMessage.setReadStatus(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.READ_STATUS)));
        }
        cursor.close();
        db.close();

        return mqttMessage;
    }

    public ArrayList<MQTTMessage> getChatMessageByMessageStatus(String messageStatus) {
        SQLiteDatabase db = getReadableDatabase();
        String countQuery = "select * from " + CHAT_MESSAGES.CHAT_MESSAGES_TABLE + " where " + CHAT_MESSAGES.MSG_STATUS + " = '" + messageStatus + "' AND " + CHAT_MESSAGES.SENDER + " = '" + DOCTOR + "'";
        Cursor cursor = db.rawQuery(countQuery, null);
        ArrayList<MQTTMessage> chatDoctors = new ArrayList<>();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                MQTTMessage mqttMessage = new MQTTMessage();

                mqttMessage.setMsgId(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.MSG_ID)));
                mqttMessage.setMsg(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.MSG)));
                mqttMessage.setMsgTime(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.MSG_TIME)));
                mqttMessage.setSender(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.SENDER)));
                mqttMessage.setPatId(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.USER2ID)));
                mqttMessage.setDocId(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.USER1ID)));
                mqttMessage.setSenderName(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.SENDER_NAME)));

                mqttMessage.setSpecialization(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.SPECIALITY)));
                mqttMessage.setMsgStatus(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.MSG_STATUS)));
                mqttMessage.setSenderImgUrl(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.SENDER_IMG_URL)));
                mqttMessage.setFileUrl(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.FILE_URL)));
                mqttMessage.setFileType(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.FILE_TYPE)));

                mqttMessage.setSalutation(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.SALUTATION)));
                mqttMessage.setReceiverName(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.RECEIVER_NAME)));
                mqttMessage.setReceiverImgUrl(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.RECEIVER_IMG_URL)));

                mqttMessage.setUploadStatus(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.UPLOAD_STATUS)));
                mqttMessage.setDownloadStatus(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.DOWNLOAD_STATUS)));
                mqttMessage.setReadStatus(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.READ_STATUS)));

                chatDoctors.add(mqttMessage);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();

        return chatDoctors;
    }

    public void updateChatMessageStatus(StatusInfo statusInfo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CHAT_MESSAGES.MSG_STATUS, statusInfo.getMessageStatus());

        if (statusInfo.getMessageStatus().equals(SEEN))
            db.update(CHAT_MESSAGES.CHAT_MESSAGES_TABLE, contentValues, CHAT_MESSAGES.USER2ID + " = ? AND " + CHAT_MESSAGES.MSG_STATUS + " = ? OR " + CHAT_MESSAGES.MSG_STATUS + " = ?", new String[]{String.valueOf(statusInfo.getPatId()), SENT, REACHED});

        if (statusInfo.getMessageStatus().equals(REACHED))
            db.update(CHAT_MESSAGES.CHAT_MESSAGES_TABLE, contentValues, CHAT_MESSAGES.USER2ID + " = ? AND " + CHAT_MESSAGES.MSG_STATUS + " = ?", new String[]{String.valueOf(statusInfo.getPatId()), SENT});
    }

    public Cursor getChatUsers() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + CHAT_MESSAGES.CHAT_MESSAGES_TABLE + " GROUP BY " + CHAT_MESSAGES.USER2ID + " ORDER BY " + CHAT_MESSAGES.MSG_TIME + " DESC";
        return db.rawQuery(sql, null);
    }

    public Cursor getRecordUploads() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + MY_RECORDS.MY_RECORDS_TABLE + " WHERE " + MY_RECORDS.UPLOAD_STATUS + " = " + FAILED;
        return db.rawQuery(sql, null);
    }

    public long insertRecordUploads(String uploadId, String patientId, int docId, String visitDate, String mOpdtime, String opdId, String mHospitalId, String mHospitalPatId, String mLocationId, String parentCaption, String imagePath) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(MY_RECORDS.UPLOAD_ID, uploadId);
        contentValues.put(MY_RECORDS.PATIENT_ID, patientId);
        contentValues.put(MY_RECORDS.DOC_ID, docId);
        contentValues.put(MY_RECORDS.VISIT_DATE, visitDate);
        contentValues.put(MY_RECORDS.OPD_TIME, mOpdtime);
        contentValues.put(MY_RECORDS.OPD_ID, opdId);
        contentValues.put(MY_RECORDS.HOSPITAL_ID, mHospitalId);
        contentValues.put(MY_RECORDS.HOSPITAL_PAT_ID, mHospitalPatId);
        contentValues.put(MY_RECORDS.LOCATION_ID, mLocationId);
        contentValues.put(MY_RECORDS.PARENT_CAPTION, parentCaption);
        contentValues.put(MY_RECORDS.IMAGE_PATH, imagePath);
        contentValues.put(MY_RECORDS.UPLOAD_STATUS, FAILED);

        return db.insert(MY_RECORDS.MY_RECORDS_TABLE, null, contentValues);
    }

    public long updateRecordUploads(String uploadId, int uploadStatus) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MY_RECORDS.UPLOAD_STATUS, uploadStatus);

        return db.update(MY_RECORDS.MY_RECORDS_TABLE, contentValues, MY_RECORDS.UPLOAD_ID + " = ?", new String[]{uploadId});
    }

    public interface MY_RECORDS {
        String MY_RECORDS_TABLE = "my_records";
        String ID = "id";
        String UPLOAD_ID = "uploadId";
        String PATIENT_ID = "patientId";
        String DOC_ID = "docId";
        String VISIT_DATE = "visitDate";
        String OPD_TIME = "opdTime";
        String OPD_ID = "opdId";
        String HOSPITAL_PAT_ID = "hospitalPatId";
        String HOSPITAL_ID = "hospitalId";
        String LOCATION_ID = "locationId";
        String PARENT_CAPTION = "parentCaption";
        String IMAGE_PATH = "imagePath";
        String UPLOAD_STATUS = "uploadStatus";
    }

    // All About Chat

    public interface CHAT_MESSAGES {
        String CHAT_MESSAGES_TABLE = "chat_messages";

        String ID = "id";
        String MSG_ID = "msgId";
        String MSG = "msg";
        String MSG_TIME = "msgTime";
        String SENDER = "sender";
        String USER2ID = "user2id";
        String USER1ID = "user1id";
        String SENDER_NAME = "senderName";
        String SPECIALITY = "speciality";
        String MSG_STATUS = "msgStatus";
        String SENDER_IMG_URL = "senderImgUrl";
        String FILE_URL = "fileUrl";
        String FILE_TYPE = "fileType";
        String UPLOAD_STATUS = "uploadStatus";
        String DOWNLOAD_STATUS = "downloadStatus";
        String READ_STATUS = "readStatus";

        String SALUTATION = "salutation";
        String RECEIVER_NAME = "receiverName";
        String RECEIVER_IMG_URL = "receiverImgUrl";
    }

    // New

    public boolean deleteChatMessageByMsgId(int messageId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(CHAT_MESSAGES.CHAT_MESSAGES_TABLE, CHAT_MESSAGES.MSG_ID + "=" + messageId, null) > 0;
    }

    public boolean deleteChatMessageByDoctorId(int doctorId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(CHAT_MESSAGES.CHAT_MESSAGES_TABLE, CHAT_MESSAGES.USER1ID + "=" + doctorId, null) > 0;
    }

    public boolean deleteChatMessageByPatientId(int patientId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(CHAT_MESSAGES.CHAT_MESSAGES_TABLE, CHAT_MESSAGES.USER2ID + "=" + patientId, null) > 0;
    }

    public ArrayList<MQTTMessage> insertChatMessage(MQTTMessage mqttMessage) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(CHAT_MESSAGES.MSG_ID, mqttMessage.getMsgId());
        contentValues.put(CHAT_MESSAGES.MSG, mqttMessage.getMsg());
        contentValues.put(CHAT_MESSAGES.MSG_TIME, mqttMessage.getMsgTime());
        contentValues.put(CHAT_MESSAGES.SENDER, mqttMessage.getSender());
        contentValues.put(CHAT_MESSAGES.USER2ID, mqttMessage.getPatId());
        contentValues.put(CHAT_MESSAGES.USER1ID, mqttMessage.getDocId());
        contentValues.put(CHAT_MESSAGES.SENDER_NAME, mqttMessage.getSenderName());
        contentValues.put(CHAT_MESSAGES.SPECIALITY, mqttMessage.getSpecialization());
        contentValues.put(CHAT_MESSAGES.MSG_STATUS, mqttMessage.getMsgStatus());
        contentValues.put(CHAT_MESSAGES.SENDER_IMG_URL, mqttMessage.getSenderImgUrl());
        contentValues.put(CHAT_MESSAGES.FILE_URL, mqttMessage.getFileUrl());
        contentValues.put(CHAT_MESSAGES.FILE_TYPE, mqttMessage.getFileType());
        contentValues.put(CHAT_MESSAGES.UPLOAD_STATUS, mqttMessage.getUploadStatus());
        contentValues.put(CHAT_MESSAGES.DOWNLOAD_STATUS, mqttMessage.getDownloadStatus());
        contentValues.put(CHAT_MESSAGES.READ_STATUS, mqttMessage.getReadStatus());

        contentValues.put(CHAT_MESSAGES.SALUTATION, mqttMessage.getSalutation());
        contentValues.put(CHAT_MESSAGES.RECEIVER_NAME, mqttMessage.getReceiverName());
        contentValues.put(CHAT_MESSAGES.RECEIVER_IMG_URL, mqttMessage.getReceiverImgUrl());

        if (getChatMessageCountByMessageId(mqttMessage.getMsgId()) == 0)
            db.insert(CHAT_MESSAGES.CHAT_MESSAGES_TABLE, null, contentValues);
        else
            db.update(CHAT_MESSAGES.CHAT_MESSAGES_TABLE, contentValues, CHAT_MESSAGES.MSG_ID + " = ?", new String[]{mqttMessage.getMsgId()});

        return getChatUnreadMessagesByPatientId(mqttMessage.getPatId());
    }

    private long getChatMessageCountByMessageId(String msgId) {
        SQLiteDatabase db = getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, CHAT_MESSAGES.CHAT_MESSAGES_TABLE, CHAT_MESSAGES.MSG_ID + " = '" + msgId + "'");
    }

    public int markAsAReadChatMessageByPatientId(int patientId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CHAT_MESSAGES.READ_STATUS, READ);
        return db.update(CHAT_MESSAGES.CHAT_MESSAGES_TABLE, contentValues, CHAT_MESSAGES.USER2ID + " = ? AND " + CHAT_MESSAGES.READ_STATUS + " = ? AND " + CHAT_MESSAGES.SENDER + " = ?", new String[]{String.valueOf(patientId), String.valueOf(UNREAD), PATIENT});
    }

    public long unreadChatMessageCountByPatientId(int patientId) {
        // Return Total Count
        SQLiteDatabase db = getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, CHAT_MESSAGES.CHAT_MESSAGES_TABLE, CHAT_MESSAGES.USER2ID + " = " + patientId + " AND " + CHAT_MESSAGES.READ_STATUS + " = " + UNREAD + " AND " + CHAT_MESSAGES.SENDER + " = '" + PATIENT + "'");
    }

    public ArrayList<MQTTMessage> getChatUnreadMessagesByPatientId(int user2Id) {
        SQLiteDatabase db = getReadableDatabase();
        String countQuery = "select * from " + CHAT_MESSAGES.CHAT_MESSAGES_TABLE + " where " + CHAT_MESSAGES.USER2ID + " = " + user2Id + " AND " + CHAT_MESSAGES.READ_STATUS + " = " + UNREAD + " AND " + CHAT_MESSAGES.SENDER + " = '" + PATIENT + "'";
        Cursor cursor = db.rawQuery(countQuery, null);
        ArrayList<MQTTMessage> chatDoctors = new ArrayList<>();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                MQTTMessage mqttMessage = new MQTTMessage();

                mqttMessage.setMsgId(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.MSG_ID)));
                mqttMessage.setMsg(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.MSG)));
                mqttMessage.setMsgTime(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.MSG_TIME)));
                mqttMessage.setSender(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.SENDER)));
                mqttMessage.setPatId(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.USER2ID)));
                mqttMessage.setDocId(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.USER1ID)));
                mqttMessage.setSenderName(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.SENDER_NAME)));

                mqttMessage.setSpecialization(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.SPECIALITY)));
                mqttMessage.setMsgStatus(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.MSG_STATUS)));
                mqttMessage.setSenderImgUrl(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.SENDER_IMG_URL)));
                mqttMessage.setFileUrl(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.FILE_URL)));
                mqttMessage.setFileType(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.FILE_TYPE)));

                mqttMessage.setSalutation(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.SALUTATION)));
                mqttMessage.setReceiverName(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.RECEIVER_NAME)));
                mqttMessage.setReceiverImgUrl(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.RECEIVER_IMG_URL)));

                mqttMessage.setUploadStatus(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.UPLOAD_STATUS)));
                mqttMessage.setDownloadStatus(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.DOWNLOAD_STATUS)));
                mqttMessage.setReadStatus(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.READ_STATUS)));

                chatDoctors.add(mqttMessage);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();

        return chatDoctors;
    }

    public Cursor searchChatMessagesByChars(String chars) {
        SQLiteDatabase db = getReadableDatabase();
        if (chars != null && !chars.isEmpty()) {
            String sql = "SELECT * FROM " + CHAT_MESSAGES.CHAT_MESSAGES_TABLE + " WHERE " + CHAT_MESSAGES.MSG + " LIKE '%" + chars + "%' ORDER BY " + CHAT_MESSAGES.MSG_TIME + " DESC LIMIT 50";
            return db.rawQuery(sql, null);
        } else return null;
    }

    public MQTTMessage getLastChatMessagesByPatientId(int patientId) {
        SQLiteDatabase db = getReadableDatabase();

        String countQuery = "SELECT * FROM " + CHAT_MESSAGES.CHAT_MESSAGES_TABLE + " WHERE " + CHAT_MESSAGES.USER2ID + " = " + patientId + " ORDER BY " + CHAT_MESSAGES.MSG_TIME + " DESC LIMIT 1";
        Cursor cursor = db.rawQuery(countQuery, null);

        MQTTMessage mqttMessage = null;

        if (cursor.moveToFirst()) {
            mqttMessage = new MQTTMessage();

            mqttMessage.setMsgId(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.MSG_ID)));
            mqttMessage.setMsg(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.MSG)));
            mqttMessage.setMsgTime(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.MSG_TIME)));
            mqttMessage.setSender(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.SENDER)));
            mqttMessage.setPatId(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.USER2ID)));
            mqttMessage.setDocId(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.USER1ID)));
            mqttMessage.setSenderName(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.SENDER_NAME)));

            mqttMessage.setSpecialization(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.SPECIALITY)));
            mqttMessage.setMsgStatus(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.MSG_STATUS)));
            mqttMessage.setSenderImgUrl(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.SENDER_IMG_URL)));
            mqttMessage.setFileUrl(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.FILE_URL)));
            mqttMessage.setFileType(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.FILE_TYPE)));

            mqttMessage.setSalutation(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.SALUTATION)));
            mqttMessage.setReceiverName(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.RECEIVER_NAME)));
            mqttMessage.setReceiverImgUrl(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.RECEIVER_IMG_URL)));

            mqttMessage.setUploadStatus(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.UPLOAD_STATUS)));
            mqttMessage.setDownloadStatus(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.DOWNLOAD_STATUS)));
            mqttMessage.setReadStatus(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.READ_STATUS)));
        }
        cursor.close();
        db.close();

        return mqttMessage;
    }

    public Cursor getChatMessagesByPatientId(int user2Id) {
        SQLiteDatabase db = getReadableDatabase();
        String countQuery = "select * from " + CHAT_MESSAGES.CHAT_MESSAGES_TABLE + " where " + CHAT_MESSAGES.USER2ID + " = " + user2Id;
        return db.rawQuery(countQuery, null);
    }

    //-----store patient in db : start----
    public void addNewPatient(PatientList newPatient, HelperResponse mHelperResponseManager, String taskAddNewPatient) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_ID, newPatient.getPatientId());
        contentValues.put(RescribeConstants.ADD_NEW_PATIENT.SALUTATION, newPatient.getSalutation());
        String patientName = newPatient.getPatientName();
        if (patientName.length() > 0 && patientName.contains(" ")) {
            String[] split = patientName.split(" ");
            contentValues.put(RescribeConstants.ADD_NEW_PATIENT.FIRST_NAME, split[0]);
            contentValues.put(RescribeConstants.ADD_NEW_PATIENT.MIDDLE_NAME, split[1]);
            contentValues.put(RescribeConstants.ADD_NEW_PATIENT.LAST_NAME, split[2]);
        } else {
            contentValues.put(RescribeConstants.ADD_NEW_PATIENT.FIRST_NAME, patientName);
            contentValues.put(RescribeConstants.ADD_NEW_PATIENT.MIDDLE_NAME, "");
            contentValues.put(RescribeConstants.ADD_NEW_PATIENT.LAST_NAME, "");
        }

        contentValues.put(RescribeConstants.ADD_NEW_PATIENT.MOBILE_NO, newPatient.getPatientPhone());
        contentValues.put(RescribeConstants.ADD_NEW_PATIENT.AGE, newPatient.getAge());
        contentValues.put(RescribeConstants.ADD_NEW_PATIENT.GENDER, newPatient.getGender());
        contentValues.put(RescribeConstants.ADD_NEW_PATIENT.REFERENCE_ID, newPatient.getOfflineReferenceID());
        contentValues.put(RescribeConstants.ADD_NEW_PATIENT.CLINIC_ID, newPatient.getClinicId());
        contentValues.put(RescribeConstants.ADD_NEW_PATIENT.CITY_NAME, newPatient.getPatientCity());
        contentValues.put(RescribeConstants.ADD_NEW_PATIENT.DOB, newPatient.getDateOfBirth());
        contentValues.put(RescribeConstants.ADD_NEW_PATIENT.OUTSTANDING_AMT, newPatient.getOutStandingAmount());
        contentValues.put(RescribeConstants.ADD_NEW_PATIENT.IMAGE_URL, newPatient.getPatientImageUrl());
        contentValues.put(RescribeConstants.ADD_NEW_PATIENT.EMAIL, newPatient.getPatientEmail());
        contentValues.put(RescribeConstants.ADD_NEW_PATIENT.IS_INSERTED_OFFLINE, newPatient.isPatientInsertedOffline() ? RescribeConstants.ADD_NEW_PATIENT.OFFLINE : RescribeConstants.ADD_NEW_PATIENT.ONLINE);
        contentValues.put(RescribeConstants.ADD_NEW_PATIENT.IS_SYNC, newPatient.isOfflinePatientSynced() ? RescribeConstants.ADD_NEW_PATIENT.IS_SYNC_WITH_SERVER : RescribeConstants.ADD_NEW_PATIENT.IS_NOT_SYNC_WITH_SERVER);
        contentValues.put(RescribeConstants.ADD_NEW_PATIENT.CREATED_TIME_STAMP, newPatient.getOfflinePatientCreatedTimeStamp());
        contentValues.put(RescribeConstants.ADD_NEW_PATIENT.HOSPITALPATID, newPatient.getHospitalPatId());

        long insert = db.insert(RescribeConstants.ADD_NEW_PATIENT.TABLE_NAME, null, contentValues);

        if (insert != -1) {
            mHelperResponseManager.onSuccess(taskAddNewPatient, newPatient);
        } else {
            mHelperResponseManager.onServerError(taskAddNewPatient, "");
        }
    }

    public ArrayList<PatientList> getOfflineAddedPatients() {
        SQLiteDatabase db = getReadableDatabase();
        String countQuery = "select * from " + RescribeConstants.ADD_NEW_PATIENT.TABLE_NAME;
        Cursor cursor = db.rawQuery(countQuery, null);
        ArrayList<PatientList> list = new ArrayList<>();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                PatientList patient = new PatientList();

                patient.setPatientId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));

                //-------
                String name = cursor.getString(cursor.getColumnIndex(RescribeConstants.ADD_NEW_PATIENT.FIRST_NAME)) + " " +
                        cursor.getString(cursor.getColumnIndex(RescribeConstants.ADD_NEW_PATIENT.MIDDLE_NAME)) + " " +
                        cursor.getString(cursor.getColumnIndex(RescribeConstants.ADD_NEW_PATIENT.LAST_NAME));
                patient.setPatientName(name);
                //-------
                patient.setSalutation(cursor.getInt(cursor.getColumnIndex(RescribeConstants.ADD_NEW_PATIENT.SALUTATION)));
                patient.setPatientPhone(cursor.getString(cursor.getColumnIndex(RescribeConstants.ADD_NEW_PATIENT.MOBILE_NO)));
                patient.setAge(cursor.getString(cursor.getColumnIndex(RescribeConstants.ADD_NEW_PATIENT.AGE)));
                patient.setGender(cursor.getString(cursor.getColumnIndex(RescribeConstants.ADD_NEW_PATIENT.GENDER)));
                patient.setOfflineReferenceID(cursor.getString(cursor.getColumnIndex(RescribeConstants.ADD_NEW_PATIENT.REFERENCE_ID)));
                patient.setClinicId(cursor.getInt(cursor.getColumnIndex(RescribeConstants.ADD_NEW_PATIENT.CLINIC_ID)));
                patient.setPatientCity(cursor.getString(cursor.getColumnIndex(RescribeConstants.ADD_NEW_PATIENT.CITY_NAME)));
                patient.setDateOfBirth(cursor.getString(cursor.getColumnIndex(RescribeConstants.ADD_NEW_PATIENT.DOB)));
                patient.setOutStandingAmount(cursor.getString(cursor.getColumnIndex(RescribeConstants.ADD_NEW_PATIENT.OUTSTANDING_AMT)));
                patient.setPatientImageUrl(cursor.getString(cursor.getColumnIndex(RescribeConstants.ADD_NEW_PATIENT.IMAGE_URL)));
                patient.setPatientEmail(cursor.getString(cursor.getColumnIndex(RescribeConstants.ADD_NEW_PATIENT.EMAIL)));

                //----------
                int anInt = cursor.getInt(cursor.getColumnIndex(RescribeConstants.ADD_NEW_PATIENT.IS_INSERTED_OFFLINE));
                patient.setPatientInsertedOffline(anInt == RescribeConstants.ADD_NEW_PATIENT.OFFLINE ? true : false);
                //----------
                anInt = cursor.getInt(cursor.getColumnIndex(RescribeConstants.ADD_NEW_PATIENT.IS_SYNC));
                patient.setOfflinePatientSynced(anInt == RescribeConstants.ADD_NEW_PATIENT.IS_SYNC_WITH_SERVER ? true : false);
                //----------
                patient.setOfflinePatientCreatedTimeStamp(cursor.getString(cursor.getColumnIndex(RescribeConstants.ADD_NEW_PATIENT.CREATED_TIME_STAMP)));
                patient.setHospitalPatId(cursor.getInt(cursor.getColumnIndex(RescribeConstants.ADD_NEW_PATIENT.HOSPITALPATID)));

                list.add(patient);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();

        return list;
    }

    //-----store patient in db : end----
}