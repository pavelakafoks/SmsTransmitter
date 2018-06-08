package info.ininfo.smstransmitter.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import info.ininfo.smstransmitter.R;
import info.ininfo.smstransmitter.models.EnumLogType;
import info.ininfo.smstransmitter.models.EnumMessageStatus;
import info.ininfo.smstransmitter.models.Log;
import info.ininfo.smstransmitter.models.Message;
import info.ininfo.smstransmitter.models.MessagesAmount;
import info.ininfo.smstransmitter.models.Settings;

public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "contactsManager";
    private Context _context;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        _context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table IF NOT EXISTS Messages  " +
                "(Id integer primary key autoincrement " +
                ", Phone text not null " +
                ", Name text" +
                ", DtCreate datetime DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
                ", DtEvent datetime not null" +
                ", DtSend datetime" +
                ", StatusId integer" +
                ", Message text not null" +
                ", ServerId integer not null)");

        db.execSQL("create table IF NOT EXISTS Log  " +
                "(Id integer primary key autoincrement " +
                ", DtCreate datetime DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))" +
                ", Information text not null" +
                ", LogType integer not null)");   // 0 - Info, 1 - Error
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion == 1 && newVersion == 2){
            onCreate(db);
        }
        if(oldVersion == 2 && newVersion == 3){
            db.execSQL("drop table Messages");
            db.execSQL("drop table Log");
            onCreate(db);
        }
    }

    public int MessageInsert(String phone
            , String name
            , Date dtEvent
            , Date dtSend
            , int statusId
            , String message
            , int serverId){
        SQLiteDatabase db = getWritableDatabase();

        /*         String selectQuery = "delete FROM Messages";
                   db.execSQL(selectQuery);        */

        ContentValues insertValues = new ContentValues();
        insertValues.put("Phone", phone);
        insertValues.put("Name", name);
        insertValues.put("DtEvent", DateTimeHelper.ToString(dtEvent));
        insertValues.put("DtSend", DateTimeHelper.ToString(dtSend));
        insertValues.put("StatusId", statusId);
        insertValues.put("Message", message);
        insertValues.put("ServerId", serverId);
        //db.close();
        int toRet = (int) db.insert("Messages", null, insertValues);
        return toRet;
    }

    public List<Message> MessageGetAll() {
        List<Message> toOut = new ArrayList<>();

        if (Settings.IsDemo){
            Message message = new Message();
            message.Id = 5;
            message.Phone = _context.getString(R.string.demo_messages_5_phone);
            message.Name = _context.getString(R.string.demo_messages_5_name);
            message.DtCreate = DateTimeHelper.ToDate("2018-06-02 12:14:15");
            message.DtEvent = DateTimeHelper.ToDate("2018-06-02 17:00:00");
            message.DtSend = DateTimeHelper.ToDate("2018-06-01 10:54:12");
            message.StatusId = EnumMessageStatus.Sent;
            message.Message = _context.getString(R.string.demo_messages_5_message);
            message.ServerId = 5;
            toOut.add(message);
            message = new Message();
            message.Id = 4;
            message.Phone = _context.getString(R.string.demo_messages_4_phone);
            message.Name = _context.getString(R.string.demo_messages_4_name);
            message.DtCreate = DateTimeHelper.ToDate("2018-06-02 11:14:15");
            message.DtEvent = DateTimeHelper.ToDate("2018-06-02 18:00:00");
            message.DtSend = DateTimeHelper.ToDate("2018-06-01 10:47:12");
            message.StatusId = EnumMessageStatus.Sent;
            message.Message = _context.getString(R.string.demo_messages_4_message);
            message.ServerId = 4;
            toOut.add(message);
            message = new Message();
            message.Id = 3;
            message.Phone = _context.getString(R.string.demo_messages_3_phone);
            message.Name = _context.getString(R.string.demo_messages_3_name);
            message.DtCreate = DateTimeHelper.ToDate("2018-06-01 18:20:15");
            message.DtEvent = DateTimeHelper.ToDate("2018-06-02 17:00:00");
            message.DtSend = DateTimeHelper.ToDate("2018-06-01 09:47:12");
            message.StatusId = EnumMessageStatus.Sent;
            message.Message = _context.getString(R.string.demo_messages_3_message);
            message.ServerId = 3;
            toOut.add(message);
            message = new Message();
            message.Id = 2;
            message.Phone = _context.getString(R.string.demo_messages_2_phone);
            message.Name = _context.getString(R.string.demo_messages_2_name);
            message.DtCreate = DateTimeHelper.ToDate("2018-06-01 18:14:12");
            message.DtEvent = DateTimeHelper.ToDate("2018-06-02 16:00:00");
            message.DtSend = DateTimeHelper.ToDate("2018-06-01 09:40:11");
            message.StatusId = EnumMessageStatus.Sent;
            message.Message = _context.getString(R.string.demo_messages_2_message);
            message.ServerId = 2;
            toOut.add(message);
            message = new Message();
            message.Id = 1;
            message.Phone = _context.getString(R.string.demo_messages_1_phone);
            message.Name = _context.getString(R.string.demo_messages_1_name);
            message.DtCreate = DateTimeHelper.ToDate("2018-06-01 14:21:42");
            message.DtEvent = DateTimeHelper.ToDate("2018-06-01 15:00:00");
            message.DtSend = DateTimeHelper.ToDate("2018-06-01 09:04:11");
            message.StatusId = EnumMessageStatus.Sent;
            message.Message = _context.getString(R.string.demo_messages_1_message);
            message.ServerId = 1;
            toOut.add(message);
        }else {

            String selectQuery = "SELECT * FROM Messages order by DtCreate desc Limit 700";

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    Message message = new Message();
                    message.Id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Id")));
                    message.Phone = cursor.getString(cursor.getColumnIndex("Phone"));
                    message.Name = cursor.getString(cursor.getColumnIndex("Name"));
                    try {
                        message.DtCreate = DateTimeHelper.ToDate(cursor.getString(cursor.getColumnIndex("DtCreate")));
                        message.DtEvent = DateTimeHelper.ToDate(cursor.getString(cursor.getColumnIndex("DtEvent")));
                        message.DtSend = DateTimeHelper.ToDate(cursor.getString(cursor.getColumnIndex("DtSend")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    message.StatusId = EnumMessageStatus.SetValue(Integer.parseInt(cursor.getString(cursor.getColumnIndex("StatusId"))));
                    message.Message = cursor.getString(cursor.getColumnIndex("Message"));
                    message.ServerId = Integer.parseInt(cursor.getString(cursor.getColumnIndex("ServerId")));
                    toOut.add(message);
                } while (cursor.moveToNext());
            }
            cursor.close();
            //db.close();
        }

        return toOut;
    }

    public void MessageUpdateStatusId(int messageId, EnumMessageStatus statusId){
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("update Messages " +
                " set StatusId = " + statusId.GetValue() +
                " where Id = " + messageId);

        if (statusId == EnumMessageStatus.Sent){
            db.execSQL("update Messages " +
                    " set DtSend = DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME')" +
                    " where Id = " + messageId);
        }

        //db.close();
    }

    public MessagesAmount MessageGetAmount(){
        MessagesAmount toRet = new MessagesAmount();

        if (Settings.IsDemo){
            toRet.Amount7Day = 23;
            toRet.Amount30Day = 114;
        }else {

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor mCount = db.rawQuery("select count(*) from Messages where StatusId = 1 and DtCreate > date('now','-7 day')", null);
            mCount.moveToFirst();
            toRet.Amount7Day = mCount.getInt(0);
            mCount.close();
            mCount = db.rawQuery("select count(*) from Messages where StatusId = 1 and DtCreate > date('now','-30 day')", null);
            mCount.moveToFirst();
            toRet.Amount30Day = mCount.getInt(0);
            mCount.close();
            //db.close();
        }
        return toRet;
    }

    public void LogInsert(int messageId, EnumLogType logType){
        LogInsert(_context.getString(messageId), logType);
    }

    public void LogInsert(int messageId, EnumLogType logType, String replacement){
        LogInsert(_context.getString(messageId).replace("###", replacement), logType);
    }

    public void LogInsert(Exception exc){
        LogInsert(_context.getString(R.string.log_error) + exc.getMessage(), EnumLogType.Error);
    }

    public void LogInsert(String information, EnumLogType logType){
        android.util.Log.d("LogInsert: ", information);
        SQLiteDatabase db = getWritableDatabase();

        /*     String selectQuery = "delete FROM Log";
               db.execSQL(selectQuery);         */

        if (logType == EnumLogType.Error){
            information = _context.getString(R.string.log_error) + information;
        }
        ContentValues insertValues = new ContentValues();
        insertValues.put("Information", information);
        insertValues.put("LogType", logType.GetValue());
        db.insert("Log", null, insertValues);
        //db.close();
    }

    public List<Log> LogGetAll() {
        List<Log> toOut = new ArrayList<>();

        if (Settings.IsDemo){
            Log log = new Log();
            log.Id = 14;
            log.DtCreate = DateTimeHelper.ToDate("2018-05-31 12:00:17");
            log.Information = _context.getString(R.string.demo_log_14_information);
            log.LogType = EnumLogType.Info;
            toOut.add(log);
            log = new Log();
            log.Id = 13;
            log.DtCreate = DateTimeHelper.ToDate("2018-05-31 12:00:14");
            log.Information = _context.getString(R.string.demo_log_13_information);
            log.LogType = EnumLogType.Info;
            toOut.add(log);
            log = new Log();
            log.Id = 12;
            log.DtCreate = DateTimeHelper.ToDate("2018-05-31 12:00:04");
            log.Information = _context.getString(R.string.demo_log_12_information);
            log.LogType = EnumLogType.Info;
            toOut.add(log);
            log = new Log();
            log.Id = 11;
            log.DtCreate = DateTimeHelper.ToDate("2018-05-31 11:30:17");
            log.Information = _context.getString(R.string.demo_log_11_information);
            log.LogType = EnumLogType.Info;
            toOut.add(log);
            log = new Log();
            log.Id = 10;
            log.DtCreate = DateTimeHelper.ToDate("2018-05-31 11:30:15");
            log.Information = _context.getString(R.string.demo_log_10_information);
            log.LogType = EnumLogType.Info;
            toOut.add(log);
            log = new Log();
            log.Id = 9;
            log.DtCreate = DateTimeHelper.ToDate("2018-05-31 11:30:11");
            log.Information = _context.getString(R.string.demo_log_9_information);
            log.LogType = EnumLogType.Info;
            toOut.add(log);
            log = new Log();
            log.Id = 8;
            log.DtCreate = DateTimeHelper.ToDate("2018-05-31 11:01:03");
            log.Information = _context.getString(R.string.demo_log_8_information);
            log.LogType = EnumLogType.Info;
            toOut.add(log);
            log = new Log();
            log.Id = 7;
            log.DtCreate = DateTimeHelper.ToDate("2018-05-31 11:00:34");
            log.Information = _context.getString(R.string.demo_log_7_information);
            log.LogType = EnumLogType.Info;
            toOut.add(log);
            log = new Log();
            log.Id = 6;
            log.DtCreate = DateTimeHelper.ToDate("2018-05-29 15:52:50");
            log.Information = _context.getString(R.string.demo_log_6_information);
            log.LogType = EnumLogType.Info;
            toOut.add(log);
            log = new Log();
            log.Id = 5;
            log.DtCreate = DateTimeHelper.ToDate("2018-05-29 15:52:47");
            log.Information = _context.getString(R.string.demo_log_5_information);
            log.LogType = EnumLogType.Error;
            toOut.add(log);
            log = new Log();
            log.Id = 4;
            log.DtCreate = DateTimeHelper.ToDate("2018-05-29 15:52:43");
            log.Information = _context.getString(R.string.demo_log_4_information);
            log.LogType = EnumLogType.Info;
            toOut.add(log);
            log = new Log();
            log.Id = 3;
            log.DtCreate = DateTimeHelper.ToDate("2018-05-29 15:52:37");
            log.Information = _context.getString(R.string.demo_log_3_information);
            log.LogType = EnumLogType.Info;
            toOut.add(log);
            log = new Log();
            log.Id = 2;
            log.DtCreate = DateTimeHelper.ToDate("2018-05-29 15:32:01");
            log.Information = _context.getString(R.string.demo_log_2_information);
            log.LogType = EnumLogType.Info;
            toOut.add(log);
            log = new Log();
            log.Id = 1;
            log.DtCreate = DateTimeHelper.ToDate("2018-05-29 15:31:56");
            log.Information = _context.getString(R.string.demo_log_1_information);
            log.LogType = EnumLogType.Info;
            toOut.add(log);
        }else {
            String selectQuery = "SELECT * FROM Log order by Id desc Limit 500";
            String deleteQuery = "DELETE from Log where Id not in (select Id from Log order by Id desc Limit 3000)";

            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(deleteQuery);

            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    Log log = new Log();
                    log.Id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Id")));
                    try {
                        log.DtCreate = DateTimeHelper.ToDate(cursor.getString(cursor.getColumnIndex("DtCreate")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    log.Information = cursor.getString(cursor.getColumnIndex("Information"));
                    log.LogType = EnumLogType.SetValue(Integer.parseInt(cursor.getString(cursor.getColumnIndex("LogType"))));
                    toOut.add(log);
                } while (cursor.moveToNext());
            }
            cursor.close();
            //db.close();
        }
        return toOut;
    }
}
