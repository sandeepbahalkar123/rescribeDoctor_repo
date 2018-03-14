package com.rescribe.doctor.helpers.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rescribe.doctor.model.chat.MQTTMessage;
import com.rescribe.doctor.model.chat.StatusInfo;
import com.rescribe.doctor.util.CommonMethods;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static com.rescribe.doctor.services.MQTTService.DOCTOR;
import static com.rescribe.doctor.services.MQTTService.PATIENT;
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

    public int updateMessageUploadStatus(String messageId, int msgUploadStatus) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CHAT_MESSAGES.UPLOADSTATUS, msgUploadStatus);
        return db.update(CHAT_MESSAGES.CHAT_MESSAGES_TABLE, contentValues, CHAT_MESSAGES.MSGID + " = ? AND " + CHAT_MESSAGES.SENDER + " = ?", new String[]{messageId, DOCTOR});
    }

    public MQTTMessage getChatMessageByMessageId(String messageId) {
        SQLiteDatabase db = getReadableDatabase();
        String countQuery = "select * from " + CHAT_MESSAGES.CHAT_MESSAGES_TABLE + " where " + CHAT_MESSAGES.MSGID + " = '" + messageId + "'";
        Cursor cursor = db.rawQuery(countQuery, null);

        MQTTMessage mqttMessage = null;

        if (cursor.moveToFirst()) {
            mqttMessage = new MQTTMessage();

            mqttMessage.setMsgId(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.MSGID)));
            mqttMessage.setMsg(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.MSG)));
            mqttMessage.setMsgTime(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.MSGTIME)));
            mqttMessage.setSender(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.SENDER)));
            mqttMessage.setPatId(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.USER2ID)));
            mqttMessage.setDocId(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.USER1ID)));
            mqttMessage.setSenderName(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.SENDERNAME)));

            mqttMessage.setSpecialization(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.SPECIALITY)));
            mqttMessage.setMsgStatus(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.MSGSTATUS)));
            mqttMessage.setSenderImgUrl(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.SENDERIMGURL)));
            mqttMessage.setFileUrl(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.FILEURL)));
            mqttMessage.setFileType(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.FILETYPE)));

            mqttMessage.setUploadStatus(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.UPLOADSTATUS)));
            mqttMessage.setDownloadStatus(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.DOWNLOADSTATUS)));
            mqttMessage.setReadStatus(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.READSTATUS)));
        }
        cursor.close();
        db.close();

        return mqttMessage;
    }

    public ArrayList<MQTTMessage> getChatMessageByMessageStatus(String messageStatus) {
        SQLiteDatabase db = getReadableDatabase();
        String countQuery = "select * from " + CHAT_MESSAGES.CHAT_MESSAGES_TABLE + " where " + CHAT_MESSAGES.MSGSTATUS + " = '" + messageStatus + "' AND " + CHAT_MESSAGES.SENDER + " = '" + DOCTOR + "'";
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
                mqttMessage.setSenderName(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.SENDERNAME)));

                mqttMessage.setSpecialization(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.SPECIALITY)));
                mqttMessage.setMsgStatus(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.MSGSTATUS)));
                mqttMessage.setSenderImgUrl(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.SENDERIMGURL)));
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

    public void updateChatMessageStatus(StatusInfo statusInfo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CHAT_MESSAGES.MSGSTATUS, statusInfo.getMessageStatus());

        if (statusInfo.getMessageStatus().equals(SEEN))
            db.update(CHAT_MESSAGES.CHAT_MESSAGES_TABLE, contentValues, CHAT_MESSAGES.USER2ID + " = ? AND " + CHAT_MESSAGES.MSGSTATUS + " = ? OR " + CHAT_MESSAGES.MSGSTATUS + " = ?", new String[]{String.valueOf(statusInfo.getPatId()), SENT, REACHED});

        if (statusInfo.getMessageStatus().equals(REACHED))
            db.update(CHAT_MESSAGES.CHAT_MESSAGES_TABLE, contentValues, CHAT_MESSAGES.USER2ID + " = ? AND " + CHAT_MESSAGES.MSGSTATUS + " = ?", new String[]{String.valueOf(statusInfo.getPatId()), SENT});
    }

    // All About Chat

    public interface CHAT_MESSAGES {
        String CHAT_MESSAGES_TABLE = "chat_messages";

        String ID = "id";
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

    public ArrayList<MQTTMessage> insertChatMessage(MQTTMessage mqttMessage) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(CHAT_MESSAGES.MSGID, mqttMessage.getMsgId());
        contentValues.put(CHAT_MESSAGES.MSG, mqttMessage.getMsg());
        contentValues.put(CHAT_MESSAGES.MSGTIME, mqttMessage.getMsgTime());
        contentValues.put(CHAT_MESSAGES.SENDER, mqttMessage.getSender());
        contentValues.put(CHAT_MESSAGES.USER2ID, mqttMessage.getPatId());
        contentValues.put(CHAT_MESSAGES.USER1ID, mqttMessage.getDocId());
        contentValues.put(CHAT_MESSAGES.SENDERNAME, mqttMessage.getSenderName());
        contentValues.put(CHAT_MESSAGES.SPECIALITY, mqttMessage.getSpecialization());
        contentValues.put(CHAT_MESSAGES.MSGSTATUS, mqttMessage.getMsgStatus());
        contentValues.put(CHAT_MESSAGES.SENDERIMGURL, mqttMessage.getSenderImgUrl());
        contentValues.put(CHAT_MESSAGES.FILEURL, mqttMessage.getFileUrl());
        contentValues.put(CHAT_MESSAGES.FILETYPE, mqttMessage.getFileType());
        contentValues.put(CHAT_MESSAGES.UPLOADSTATUS, mqttMessage.getUploadStatus());
        contentValues.put(CHAT_MESSAGES.DOWNLOADSTATUS, mqttMessage.getDownloadStatus());
        contentValues.put(CHAT_MESSAGES.READSTATUS, mqttMessage.getReadStatus());

        if (getChatMessageCountByMessageId(mqttMessage.getMsgId()) == 0)
            db.insert(CHAT_MESSAGES.CHAT_MESSAGES_TABLE, null, contentValues);
        else
            db.update(CHAT_MESSAGES.CHAT_MESSAGES_TABLE, contentValues, CHAT_MESSAGES.MSGID + " = ?", new String[]{mqttMessage.getMsgId()});

        return getChatUnreadMessagesByPatientId(mqttMessage.getPatId());
    }

    private long getChatMessageCountByMessageId(String msgId) {
        SQLiteDatabase db = getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, CHAT_MESSAGES.CHAT_MESSAGES_TABLE, CHAT_MESSAGES.MSGID + " = '" + msgId + "'");
    }

    public int markAsAReadChatMessageByPatientId(int patientId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CHAT_MESSAGES.READSTATUS, READ);
        return db.update(CHAT_MESSAGES.CHAT_MESSAGES_TABLE, contentValues, CHAT_MESSAGES.USER2ID + " = ? AND " + CHAT_MESSAGES.READSTATUS + " = ? AND " + CHAT_MESSAGES.SENDER + " = ?", new String[]{String.valueOf(patientId), String.valueOf(UNREAD), PATIENT});
    }

    public long unreadChatMessageCountByPatientId(int patientId) {
        // Return Total Count
        SQLiteDatabase db = getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, CHAT_MESSAGES.CHAT_MESSAGES_TABLE, CHAT_MESSAGES.USER2ID + " = " + patientId + " AND " + CHAT_MESSAGES.READSTATUS + " = " + UNREAD + " AND " + CHAT_MESSAGES.SENDER + " = '" + PATIENT + "'");
    }

    public ArrayList<MQTTMessage> getChatUnreadMessagesByPatientId(int user2Id) {
        SQLiteDatabase db = getReadableDatabase();
        String countQuery = "select * from " + CHAT_MESSAGES.CHAT_MESSAGES_TABLE + " where " + CHAT_MESSAGES.USER2ID + " = " + user2Id + " AND " + CHAT_MESSAGES.READSTATUS + " = " + UNREAD + " AND " + CHAT_MESSAGES.SENDER + " = '" + PATIENT + "'";
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
                mqttMessage.setSenderName(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.SENDERNAME)));

                mqttMessage.setSpecialization(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.SPECIALITY)));
                mqttMessage.setMsgStatus(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.MSGSTATUS)));
                mqttMessage.setSenderImgUrl(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.SENDERIMGURL)));
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

    public Cursor searchChatMessagesByChars(String chars) {
        SQLiteDatabase db = getReadableDatabase();
        if (chars != null && chars.length() > 0) {
            String sql = "SELECT * FROM " + CHAT_MESSAGES.CHAT_MESSAGES_TABLE + " WHERE " + CHAT_MESSAGES.MSG + " LIKE '%" + chars + "%' ORDER BY " + CHAT_MESSAGES.MSGTIME + " DESC";
            return db.rawQuery(sql, null);
        } else return null;
    }

    public MQTTMessage getLastChatMessagesByPatientId(int patientId) {
        SQLiteDatabase db = getReadableDatabase();

        String countQuery = "SELECT * FROM " + CHAT_MESSAGES.CHAT_MESSAGES_TABLE + " WHERE " + CHAT_MESSAGES.USER2ID + " = " + patientId + " ORDER BY " + CHAT_MESSAGES.MSGTIME + " DESC LIMIT 1";
        Cursor cursor = db.rawQuery(countQuery, null);

        MQTTMessage mqttMessage = null;

        if (cursor.moveToFirst()) {
            mqttMessage = new MQTTMessage();

            mqttMessage.setMsgId(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.MSGID)));
            mqttMessage.setMsg(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.MSG)));
            mqttMessage.setMsgTime(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.MSGTIME)));
            mqttMessage.setSender(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.SENDER)));
            mqttMessage.setPatId(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.USER2ID)));
            mqttMessage.setDocId(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.USER1ID)));
            mqttMessage.setSenderName(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.SENDERNAME)));

            mqttMessage.setSpecialization(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.SPECIALITY)));
            mqttMessage.setMsgStatus(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.MSGSTATUS)));
            mqttMessage.setSenderImgUrl(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.SENDERIMGURL)));
            mqttMessage.setFileUrl(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.FILEURL)));
            mqttMessage.setFileType(cursor.getString(cursor.getColumnIndex(CHAT_MESSAGES.FILETYPE)));

            mqttMessage.setUploadStatus(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.UPLOADSTATUS)));
            mqttMessage.setDownloadStatus(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.DOWNLOADSTATUS)));
            mqttMessage.setReadStatus(cursor.getInt(cursor.getColumnIndex(CHAT_MESSAGES.READSTATUS)));
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
}