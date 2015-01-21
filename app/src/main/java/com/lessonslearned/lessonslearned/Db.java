package com.lessonslearned.lessonslearned;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ias0nas on 02/03/14.
 */
public class Db extends SQLiteOpenHelper {

    //lesson table
    public static final String TABLE_LESSON = "lessons";
    public static final String COLUMN_LESSON_ID = "_id";
    public static final String COLUMN_LESSON_NAME = "name";
    public static final String COLUMN_LESSON_DESCRIPTION = "description";
    public static final String COLUMN_LESSON_TIMESTAMP = "timestamp";
    public static final String COLUMN_LESSON_DELETED = "deleted";

    //last alert table
    public static final String TABLE_LAST_ALERT = "last_alert";
    public static final String COLUMN_LAST_ALERT_ID = "_id";
    public static final String COLUMN_LAST_ALERT_TIMESTAMP = "timestamp";

    private static final int DB_VERSION = 3;


    public Db(Context context){
        super (context, "lessons.db", null, DB_VERSION);
    }

    private void createTables(SQLiteDatabase database){
        //lesson table
        database.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_LESSON + "(" +
                COLUMN_LESSON_ID + " integer primary key autoincrement, " +
                COLUMN_LESSON_NAME + " text not null, " +
                COLUMN_LESSON_DESCRIPTION + " text, " +
                COLUMN_LESSON_TIMESTAMP + " datetime default CURRENT_TIMESTAMP, " +
                COLUMN_LESSON_DELETED + " integer " +
                ");");

        //last alert table
        database.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_LAST_ALERT +"(" +
                COLUMN_LAST_ALERT_ID + " integer, " +
                COLUMN_LAST_ALERT_TIMESTAMP + " datetime default CURRENT_TIMESTAMP" +
                ");");
    }

    private void dropTables(SQLiteDatabase database){
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_LAST_ALERT);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_LESSON);
    }

    @Override
    public void onCreate(SQLiteDatabase database){
        createTables(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){
        dropTables(database);
        onCreate(database);
    }
}

