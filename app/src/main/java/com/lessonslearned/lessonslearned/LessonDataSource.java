package com.lessonslearned.lessonslearned;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.splunk.mint.Mint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class LessonDataSource {
    private SQLiteDatabase database;
    private Context context;
    private Db db;
    private String[] allColumns = {db.COLUMN_LESSON_ID, db.COLUMN_LESSON_NAME, db.COLUMN_LESSON_DESCRIPTION, db.COLUMN_LESSON_TIMESTAMP};

    public LessonDataSource(Context context){
        this.context = context;
        db = new Db(context);
    }

    public void open() throws SQLiteException{
        database = db.getWritableDatabase();
    }

    public void close(){
        db.close();
    }

    private Lesson cursorToLesson(Cursor cursor){
        Lesson lesson = new Lesson();
        lesson.setId(cursor.getLong(cursor.getColumnIndex(db.COLUMN_LESSON_ID)));
        lesson.setName(cursor.getString(cursor.getColumnIndex(db.COLUMN_LESSON_NAME)));
        lesson.setDescription(cursor.getString(cursor.getColumnIndex(db.COLUMN_LESSON_DESCRIPTION)));
        SimpleDateFormat dateFormat = new SimpleDateFormat(AppSettings.DATE_FORMAT_FULL);
        try {
            lesson.setTimestamp(dateFormat.parse(cursor.getString(cursor.getColumnIndex(db.COLUMN_LESSON_TIMESTAMP))));
        } catch (Exception e){
            if (!AppSettings.RELEASE_MODE){
                Log.e("Lessons Learned", "Could not get convert date to set lesson timestamp " + e.getMessage() + ": " + e.getStackTrace());
            }
            else{
                Mint.logException(e);
            }
        }
        return lesson;
    }

    public Lesson createLesson(String lesson, String lessonDescription){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Db.COLUMN_LESSON_NAME, lesson);
        contentValues.put(Db.COLUMN_LESSON_DESCRIPTION, lessonDescription);
        SimpleDateFormat dateFormat = new SimpleDateFormat(AppSettings.DATE_FORMAT_FULL);
        Date date = new Date();
        contentValues.put(Db.COLUMN_LESSON_TIMESTAMP, dateFormat.format(date));
        contentValues.put(Db.COLUMN_LESSON_DELETED, 0);
        long insertId = database.insert(db.TABLE_LESSON, null, contentValues);
        Cursor cursor = database.query(db.TABLE_LESSON, allColumns, db.COLUMN_LESSON_ID + " = " + insertId,
                null, null, null, null);
        cursor.moveToFirst();
        Lesson newLesson = cursorToLesson(cursor);
        cursor.close();
        return newLesson;
    }

    public void updateLesson(long lessonId, String lessonName, String lessonDescription){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Db.COLUMN_LESSON_NAME, lessonName);
        contentValues.put(Db.COLUMN_LESSON_DESCRIPTION, lessonDescription);
        contentValues.put(Db.COLUMN_LESSON_DELETED, 0);
        database.update(db.TABLE_LESSON, contentValues, db.COLUMN_LESSON_ID + " = " + lessonId, null);
    }

    public void deleteLesson(Lesson lesson){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Db.COLUMN_LESSON_DELETED, 1);
        database.update(db.TABLE_LESSON, contentValues, db.COLUMN_LESSON_ID + " = " + lesson.getId(), null);
    }

    public void undeleteLesson(Lesson lesson){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Db.COLUMN_LESSON_DELETED, 0);
        database.update(db.TABLE_LESSON, contentValues, db.COLUMN_LESSON_ID + " = " + lesson.getId(), null);
    }

    public List<Lesson> getLessons(int deleted){
        List<Lesson> lessons = new ArrayList<>();

        Cursor cursor = database.query(db.TABLE_LESSON, allColumns, db.COLUMN_LESSON_DELETED + " = " + deleted, null, null, null, db.COLUMN_LESSON_ID + " DESC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Lesson lesson = cursorToLesson(cursor);
            lessons.add(lesson);
            cursor.moveToNext();
        }
        cursor.close();
        return lessons;
    }

    public Lesson getLesson(long lessonId){
        Cursor cursor = database.query(db.TABLE_LESSON, allColumns, db.COLUMN_LESSON_ID + " = " + lessonId, null, null, null, null);
        cursor.moveToFirst();
        Lesson lesson = cursorToLesson(cursor);
        cursor.close();
        return lesson;
    }

    public Lesson getNextNotificationLesson(){
        //try to get a lesson that was different from the last three used in alerts and that was added the days ago defined in settings
        Cursor cursor = database.query(db.TABLE_LESSON, allColumns,
                    db.COLUMN_LESSON_DELETED + " != 1 " +
                    " AND (strftime('%d', 'now') - strftime('%d', " + Db.COLUMN_LESSON_TIMESTAMP + ")) > " + AppSettings.LESSON_DAYS_AFTER_INSERT_USE_IN_ALERT +
                    " AND " + db.COLUMN_LESSON_ID + " NOT IN (SELECT " + db.COLUMN_LAST_ALERT_ID +
                                                                " FROM " + db.TABLE_LAST_ALERT +
                                                                " ORDER BY " + Db.COLUMN_LAST_ALERT_TIMESTAMP + " DESC " +
                                                                " LIMIT 3)",
                null, null, null, " RANDOM() LIMIT 1");
        if (cursor.getCount() != 0){
            cursor.moveToFirst();
            Lesson lesson = cursorToLesson(cursor);
            cursor.close();
            return lesson;
        }

        //if last alert was displayed more than whatever days defined in config use anyway
        LastAlertDataSource lastAlertDataSource = new LastAlertDataSource(context);
        lastAlertDataSource.open();
        if (lastAlertDataSource.minutesSinceLastAlert() > AppSettings.REUSE_SAME_LESSON_ALERT_MINUTES){
            cursor = database.query(db.TABLE_LESSON, allColumns,
                                    db.COLUMN_LESSON_DELETED + " != 1", null, null, null, " RANDOM()");
            if (cursor.getCount() != 0){
                cursor.moveToFirst();
                Lesson lesson = cursorToLesson(cursor);
                cursor.close();
                lastAlertDataSource.close();
                return lesson;
            }
        }
        lastAlertDataSource.close();

        return null;
    }

    public boolean lessonExists(){
        Cursor cursor = database.query(db.TABLE_LESSON, allColumns,
                db.COLUMN_LESSON_DELETED + " != 1",
                null, null, null, " RANDOM() LIMIT 1");
        if (cursor.getCount() != 0){
            return true;
        }
        return false;
    }

    public boolean deletedLessonExists(){
        Cursor cursor = database.query(db.TABLE_LESSON, allColumns,
                db.COLUMN_LESSON_DELETED + " = 1",
                null, null, null, " RANDOM() LIMIT 1");
        if (cursor.getCount() != 0){
            return true;
        }
        return false;
    }
}
