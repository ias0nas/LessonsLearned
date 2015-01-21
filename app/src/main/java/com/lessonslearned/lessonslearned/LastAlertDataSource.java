package com.lessonslearned.lessonslearned;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.splunk.mint.Mint;

public class LastAlertDataSource {
    private SQLiteDatabase database;
    private Db db;

    public LastAlertDataSource(Context context){
        db = new Db(context);
    }

    public void open() throws SQLiteException {
        database = db.getWritableDatabase();
    }

    public void close(){
        db.close();
    }

    public void setLastAlert(long lessonID){
        database.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Db.COLUMN_LAST_ALERT_ID, lessonID);
            database.insert(Db.TABLE_LAST_ALERT, null, contentValues);
            database.delete(Db.TABLE_LAST_ALERT, Db.COLUMN_LAST_ALERT_TIMESTAMP + " NOT IN " +
                                                " (SELECT " + Db.COLUMN_LAST_ALERT_TIMESTAMP + " FROM " + Db.TABLE_LAST_ALERT +
                                                " ORDER BY " + Db.COLUMN_LAST_ALERT_TIMESTAMP + " DESC" +
                                                " LIMIT 3)", null);
            Cursor cursor = database.rawQuery("SELECT * FROM " + Db.TABLE_LAST_ALERT, null);
            database.setTransactionSuccessful();
            while (cursor.moveToNext()){
                Log.e("Lessons Learned", "Last lessons date:" + cursor.getString(1) + " lesson: " + cursor.getString(0));
            }
        }
        catch (Exception e){
            if (!AppSettings.RELEASE_MODE){
                Log.e("Lessons Learned", "Could not reset Last Alert table " + e.getMessage() + ": " + e.getStackTrace());
            }
            else{
                Mint.logException(e);
            }
        } finally {
            database.endTransaction();
        }
    }

    public int minutesSinceLastAlert(){
        try{
            Cursor cursor = database.rawQuery("SELECT (strftime('%s', 'now') - strftime('%s', " + Db.COLUMN_LAST_ALERT_TIMESTAMP + ")) / 60 " +
                                        " FROM " + Db.TABLE_LAST_ALERT +
                                        " ORDER BY " + Db.COLUMN_LAST_ALERT_TIMESTAMP + " DESC " +
                                        " LIMIT 1", null);
            if (cursor.moveToFirst()){
                Log.e("Lessons Learned", "Minutes since last alert:" + String.valueOf(cursor.getInt(0)));
                int minutes = cursor.getInt(0);
                cursor.close();
                return minutes;
            }
            else{
                cursor.close();
                return 0;
            }
        }
        catch (Exception e){
            if (!AppSettings.RELEASE_MODE){
                Log.e("Lessons Learned", "Could not get hours since last alert " + e.getMessage() + " " + e.getStackTrace());
            }
            else{
                Mint.logException(e);
            }
            return 0;
        }
    }
}
