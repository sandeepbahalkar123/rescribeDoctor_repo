package com.rescribe.doctor.helpers.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.rescribe.doctor.model.chat.MQTTData;
import com.rescribe.doctor.model.chat.MQTTMessage;
import com.rescribe.doctor.util.CommonMethods;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static com.rescribe.doctor.util.RescribeConstants.MESSAGE_STATUS.READ;
import static com.rescribe.doctor.util.RescribeConstants.MESSAGE_STATUS.UNREAD;

public class AppDBHelper extends SQLiteOpenHelper {

    private final String TAG = "DrRescribe/AppDBHelper";

    private static final String MESSAGE_UPLOAD_ID = "message_upload_id";
    private static final String MESSAGE_UPLOAD_STATUS = "message_status";
    private static final String MESSAGE_FILE_UPLOAD = "message_file_data";
    private static final String MESSAGE_UPLOAD_TABLE = "my_message_table";

    private static final String MESSAGE_DOWNLOAD_ID = "message_download_id";
    private static final String MESSAGE_DOWNLOAD_STATUS = "message_download_status";
    private static final String MESSAGE_FILE_DOWNLOAD = "message_file_download";
    private static final String MESSAGE_DOWNLOAD_TABLE = "message_download_table";

    public static final String CHAT_USER_ID = "user_id";
    public static final String MESSAGE = "message";
    public static final String MESSAGE_TABLE = "unread_messages";

    private static final String DATABASE_NAME = "MyRescribe.sqlite";
    private static final String DB_PATH_SUFFIX = "/data/data/com.rescribe.doctor/databases/"; // Change
    private static final int DB_VERSION = 1;
    public static final String APP_DATA_TABLE = "PrescriptionData";
    public static final String COLUMN_ID = "dataId";
    public static final String COLUMN_DATA = "data";

    static AppDBHelper instance = null;
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

    // All About Chat

    public interface CHAT_MESSAGES {
        String CHAT_MESSAGES_TABLE = "chat_messages";

        String MSGID = "msgId";
        String MSG = "msg";
        String MSGTIME = "msgTime";
        String SENDER = "sender";
        String USER2ID = "user2id";
        String USER1ID = "user1id";
        String SENDERNAME = "senderName";
        String SPECIALITY = "speciality";
        String MSGSTATUS = "msgStatus";
        String SENDERIMGURL = "senderImgUrl";
        String FILEURL = "fileUrl";
        String FILETYPE = "fileType";
        String UPLOADSTATUS = "uploadStatus";
        String DOWNLOADSTATUS = "downloadStatus";
        String READSTATUS = "readStatus";
    }

    // New
    
    public boolean deleteChatMessageByMsgId(int messageId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(CHAT_MESSAGES.CHAT_MESSAGES_TABLE, CHAT_MESSAGES.MSGID + "=" + messageId, null) > 0;
    }

    public boolean deleteChatMessageByDoctorId(int doctorId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(CHAT_MESSAGES.CHAT_MESSAGES_TABLE, CHAT_MESSAGES.USER1ID + "=" + doctorId, null) > 0;
    }

    public boolean deleteChatMessageByPatientId(int patientId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(CHAT_MESSAGES.CHAT_MESSAGES_TABLE, CHAT_MESSAGES.USER2ID + "=" + patientId, null) > 0;
    }

    // New End
    
    // Old

    public boolean deleteUnreadMessage(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(MESSAGE_TABLE, CHAT_USER_ID + "=" + id, null) > 0;
    }

    // Old End

    // New

    public ArrayList<MQTTMessage> insertChatMessage(MQTTMessage mqttMessage) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(CHAT_MESSAGES.MSGID, mqttMessage.getMsgId());
        contentValues.put(CHAT_MESSAGES.MSG, mqttMessage.getMsg());
        contentValues.put(CHAT_MESSAGES.MSGTIME, mqttMessage.getMsgTime());
        contentValues.put(CHAT_MESSAGES.SENDER, mqttMessage.getSender());
        contentValues.put(CHAT_MESSAGES.USER2ID, mqttMessage.getPatId());
        contentValues.put(CHAT_MESSAGES.USER1ID, mqttMessage.getDocId());
        contentValues.put(CHAT_MESSAGES.SENDERNAME, mqttMessage.getName());
        contentValues.put(CHAT_MESSAGES.SPECIALITY, mqttMessage.getSpecialization());
        contentValues.put(CHAT_MESSAGES.MSGSTATUS, mqttMessage.getMsgStatus());
        contentValues.put(CHAT_MESSAGES.SENDERIMGURL, mqttMessage.getImageUrl());
        contentValues.put(CHAT_MESSAGES.FILEURL, mqttMessage.getFileUrl());
        contentValues.put(CHAT_MESSAGES.FILETYPE, mqttMessage.getFileType());
        contentValues.put(CHAT_MESSAGES.UPLOADSTATUS, mqttMessage.getUploadStatus());
        contentValues.put(CHAT_MESSAGES.DOWNLOADSTATUS, mqttMessage.getDownloadStatus());
        contentValues.put(CHAT_MESSAGES.READSTATUS, mqttMessage.getReadStatus());

        db.insert(CHAT_MESSAGES.CHAT_MESSAGES_TABLE, null, contentValues);

        return getUnreadMessagesByPatientId(mqttMessage.getPatId());
    }

    public int markAsAReadChatMessageByPatientId(String patientId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CHAT_MESSAGES.READSTATUS, READ);
        return db.update(CHAT_MESSAGES.CHAT_MESSAGES_TABLE, contentValues, CHAT_MESSAGES.USER2ID + " = ? AND " + CHAT_MESSAGES.READSTATUS + " = ? ", new String[]{patientId, String.valueOf(UNREAD)});
    }

    // New End
    
    // Old

    public ArrayList<MQTTMessage> insertUnreadMessage(int id, String message) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(CHAT_USER_ID, id);
        contentValues.put(MESSAGE, message);

        db.insert(MESSAGE_TABLE, null, contentValues);

        return getUnreadMessagesById(id);
    }

    // Old End

    // New
    
    public int unreadChatMessageCountByPatientId(int patientId) {
        // Return Total Count
        SQLiteDatabase db = getReadableDatabase();
        String countQuery = "select * from " + CHAT_MESSAGES.CHAT_MESSAGES_TABLE + " where " + CHAT_MESSAGES.USER2ID + " = " + patientId + " AND " + CHAT_MESSAGES.READSTATUS + " = " + UNREAD;
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }
    
    // New End
    
    // Old

    public int unreadMessageCountById(int id) {
        // Return Total Count
        SQLiteDatabase db = getReadableDatabase();
        String countQuery = "select * from " + MESSAGE_TABLE + " where " + CHAT_USER_ID + " = " + id;
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    // Old End

    /*public int unreadMessageCount() {
        // Return Total Count
        SQLiteDatabase db = getReadableDatabase();
        String countQuery = "select * from " + MESSAGE_TABLE;
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }*/

    // New
    
    public ArrayList<MQTTMessage> getUnreadMessagesByPatientId(int user2Id) {
        SQLiteDatabase db = getReadableDatabase();
        String countQuery = "select * from " + CHAT_MESSAGES.CHAT_MESSAGES_TABLE + " where " + CHAT_MESSAGES.USER2ID + " = " + user2Id + " AND " + CHAT_MESSAGES.READSTATUS + " = " + UNREAD;
        Cursor cursor = db.rawQuery(countQuery, null);
        ArrayList<MQTTMessage> chatDoctors = new ArrayList<>();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                MQTTMessage mqttMessage = new MQTTMessage();

                mqttMessage.setMsgId(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.MSGID)));
                mqttMessage.setMsg(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.MSG)));
                mqttMessage.setMsgTime(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.MSGTIME)));
                mqttMessage.setSender(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.SENDER)));
                mqttMessage.setPatId(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.USER2ID)));
                mqttMessage.setDocId(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.USER1ID)));
                mqttMessage.setName(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.SENDERNAME)));

                mqttMessage.setSpecialization(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.SPECIALITY)));
                mqttMessage.setMsgStatus(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.MSGSTATUS)));
                mqttMessage.setImageUrl(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.SENDERIMGURL)));
                mqttMessage.setFileUrl(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.FILEURL)));
                mqttMessage.setFileType(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.FILETYPE)));

                mqttMessage.setUploadStatus(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.UPLOADSTATUS)));
                mqttMessage.setDownloadStatus(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.DOWNLOADSTATUS)));
                mqttMessage.setReadStatus(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.READSTATUS)));

                chatDoctors.add(mqttMessage);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();

        return chatDoctors;
    }
    
    // New
    
    // Old

    public ArrayList<MQTTMessage> getUnreadMessagesById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        String countQuery = "select * from " + MESSAGE_TABLE + " where " + CHAT_USER_ID + " = " + id;
        Cursor cursor = db.rawQuery(countQuery, null);
        ArrayList<MQTTMessage> chatDoctors = new ArrayList<>();
        Gson gson = new Gson();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String messageJson = cursor.getString(cursor.getColumnIndex(MESSAGE));
                MQTTMessage MQTTMessage = gson.fromJson(messageJson, MQTTMessage.class);
                chatDoctors.add(MQTTMessage);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();

        return chatDoctors;
    }
    
    // Old End

    /*public int unreadMessageUsersCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + MESSAGE_TABLE + " group by " + CHAT_USER_ID, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }*/

    // Chat Upload Data

    public boolean insertMessageUpload(String id, int status, String data) {
        if (messageUploadTableNumberOfRows(id) == 0) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(MESSAGE_UPLOAD_ID, id);
            contentValues.put(MESSAGE_UPLOAD_STATUS, status);
            contentValues.put(MESSAGE_FILE_UPLOAD, data);

            db.insert(MESSAGE_UPLOAD_TABLE, null, contentValues);
        }
        return true;
    }

    private int messageUploadTableNumberOfRows(String id) {
        SQLiteDatabase db = getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, MESSAGE_UPLOAD_TABLE, MESSAGE_UPLOAD_ID + " = ? ", new String[]{id});
    }

    public int updateMessageUpload(String id, int isUploaded) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MESSAGE_UPLOAD_STATUS, isUploaded);

        return db.update(MESSAGE_UPLOAD_TABLE, contentValues, MESSAGE_UPLOAD_ID + " = ? ", new String[]{id});
    }

    public MQTTData getMessageUpload() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + MESSAGE_UPLOAD_TABLE, null);

        MQTTData myMessageData = new MQTTData();
        ArrayList<MQTTMessage> mqttMessages = new ArrayList<>();

        Gson gson = new Gson();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String imageJson = cursor.getString(cursor.getColumnIndex(MESSAGE_FILE_UPLOAD));
                MQTTMessage mqttMessage = gson.fromJson(imageJson, MQTTMessage.class);
                mqttMessage.setUploadStatus(cursor.getInt(cursor.getColumnIndex(MESSAGE_UPLOAD_STATUS)));
                mqttMessages.add(mqttMessage);
                cursor.moveToNext();
            }
        }
        cursor.close();

        myMessageData.setMqttMessages(mqttMessages);

        return myMessageData;
    }

    public MQTTMessage getMessageUploadById(String id) {
        SQLiteDatabase db = getReadableDatabase();
        String countQuery = "select * from " + MESSAGE_UPLOAD_TABLE + " where " + MESSAGE_UPLOAD_ID + " = '" + id + "'";
        Cursor cursor = db.rawQuery(countQuery, null);

        Gson gson = new Gson();
        MQTTMessage mqttMessage = null;
        if (cursor.moveToFirst()) {
            String imageJson = cursor.getString(cursor.getColumnIndex(MESSAGE_FILE_UPLOAD));
            mqttMessage = gson.fromJson(imageJson, MQTTMessage.class);
            mqttMessage.setUploadStatus(cursor.getInt(cursor.getColumnIndex(MESSAGE_UPLOAD_STATUS)));
        }
        cursor.close();

        return mqttMessage;
    }

    /*public boolean deleteUploadedMessage(String id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(MESSAGE_UPLOAD_TABLE, MESSAGE_UPLOAD_ID + "='" + id + "'", null) > 0;
    }*/

    // Chat Upload Data End

    // Chat Download Data

    /*public boolean insertMessageDownload(String id, int status, String data) {
        if (messageDownloadTableNumberOfRows(id) == 0) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(MESSAGE_DOWNLOAD_ID, id);
            contentValues.put(MESSAGE_DOWNLOAD_STATUS, status);
            contentValues.put(MESSAGE_FILE_DOWNLOAD, data);

            db.insert(MESSAGE_DOWNLOAD_TABLE, null, contentValues);
        }
        return true;
    }

    private int messageDownloadTableNumberOfRows(String id) {
        SQLiteDatabase db = getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, MESSAGE_DOWNLOAD_TABLE, MESSAGE_DOWNLOAD_ID + " = ? ", new String[]{id});
    }

    public int updateMessageDownloading(String id, int isDownloaded) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MESSAGE_DOWNLOAD_STATUS, isDownloaded);

        return db.update(MESSAGE_DOWNLOAD_TABLE, contentValues, MESSAGE_DOWNLOAD_ID + " = ? ", new String[]{id});
    }

    public MQTTData getMessageDownload() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + MESSAGE_DOWNLOAD_TABLE, null);

        MQTTData myMessageData = new MQTTData();
        ArrayList<MQTTMessage> mqttMessages = new ArrayList<>();

        Gson gson = new Gson();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String imageJson = cursor.getString(cursor.getColumnIndex(MESSAGE_FILE_DOWNLOAD));
                MQTTMessage mqttMessage = gson.fromJson(imageJson, MQTTMessage.class);
                mqttMessage.setDownloadStatus(cursor.getInt(cursor.getColumnIndex(MESSAGE_DOWNLOAD_STATUS)));
                mqttMessages.add(mqttMessage);
                cursor.moveToNext();
            }
        }
        cursor.close();

        myMessageData.setMqttMessages(mqttMessages);

        return myMessageData;
    }

    public MQTTMessage getMessageDownloadById(String id) {
        SQLiteDatabase db = getReadableDatabase();
        String countQuery = "select * from " + MESSAGE_DOWNLOAD_TABLE + " where " + MESSAGE_DOWNLOAD_ID + " = '" + id + "'";
        Cursor cursor = db.rawQuery(countQuery, null);

        Gson gson = new Gson();
        MQTTMessage mqttMessage = null;
        if (cursor.moveToFirst()) {
            String imageJson = cursor.getString(cursor.getColumnIndex(MESSAGE_FILE_DOWNLOAD));
            mqttMessage = gson.fromJson(imageJson, MQTTMessage.class);
            mqttMessage.setDownloadStatus(cursor.getInt(cursor.getColumnIndex(MESSAGE_DOWNLOAD_STATUS)));
        }
        cursor.close();

        return mqttMessage;
    }

    public boolean deleteDownloadedMessage(String id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(MESSAGE_DOWNLOAD_TABLE, MESSAGE_DOWNLOAD_ID + "='" + id + "'", null) > 0;
    }*/

    // Chat Download Data End
}