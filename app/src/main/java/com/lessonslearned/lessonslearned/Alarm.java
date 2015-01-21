package com.lessonslearned.lessonslearned;

import android.app.NotificationManager;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.splunk.mint.Mint;
import com.splunk.mint.MintLog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Alarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        try{
            if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
                AlarmSetter.startAlarm(context);
            }

            //get wake lock
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
            wl.acquire();

            //display random lesson
            LessonDataSource lessonDataSource = new LessonDataSource(context);
            lessonDataSource.open();
            Lesson randomLesson = lessonDataSource.getNextNotificationLesson();
            lessonDataSource.close();
            if (randomLesson != null){
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_action_go_to_today)
                                .setContentTitle(randomLesson.getName())
                                .setContentText(randomLesson.getDescription())
                                .setAutoCancel(true);

                Intent viewLessontIntent = new Intent(context, ViewLessonActivity.class);
                viewLessontIntent.putExtra(Extra.Lesson.LESSONID.toString(), randomLesson.getId());
                viewLessontIntent.putExtra(Extra.ViewLessonBackNewHome.NewHome.toString(),true);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(Home.class);
                stackBuilder.addNextIntent(viewLessontIntent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(1, mBuilder.build());
                LastAlertDataSource lastAlertDataSource = new LastAlertDataSource(context);
                lastAlertDataSource.open();
                lastAlertDataSource.setLastAlert(randomLesson.getId());
                lastAlertDataSource.close();
            }
            //release wake lock
            wl.release();

        }
        catch (Exception e){
            if (!AppSettings.RELEASE_MODE){
                Log.e("Lessons Learned", "Could not receive alarm broadcast " + e.getMessage() + ": " + e.getStackTrace());
            }
            else{
                Mint.logException(e);
            }
        }
    }

    public void setAlarm(Context context)
    {
        try{
            AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(context, Alarm.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
            long minutesToNextAlarm = getMinutesToNextAlarm();
            am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                                SystemClock.elapsedRealtime() + minutesToNextAlarm * 1000 * 60, minutesToNextAlarm * 1000 * 60, pi);
        }
        catch (Exception e){
            if (!AppSettings.RELEASE_MODE){
                Log.e("Lessons Learned", "Could not reset Last Alert table " + e.getMessage() + ": " + e.getStackTrace());
            }
            else{
                Mint.logException(e);
            }
        }
    }

    public static long getMinutesToNextAlarm(){
        try{
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat hourPicked = new SimpleDateFormat("k");
            boolean acceptableTimeFound = false;
            int minutesToAdd = AppSettings.NEXT_ALARM_MINUTES_MINIMUM;
            //add minimum minutes from config to now and keep adding 20 minutes until we hit time during day
            while (!acceptableTimeFound){
                //add random number of hours to minimum minutes so that alarm doesn't always go off at the same time
                Random r = new Random();
                int addedHours = r.nextInt(15);
                cal.setTime(new Date());
                cal.add(Calendar.MINUTE, minutesToAdd + (addedHours * 60));
                if (Integer.parseInt(hourPicked.format(cal.getTime())) > 7 && Integer.parseInt(hourPicked.format(cal.getTime())) < 22){
                    acceptableTimeFound = true;
                }
                minutesToAdd +=20;
            }
            Calendar now = Calendar.getInstance().getInstance();
            now.setTime(new Date());
            return getTimeDifference(now.getTime(), cal.getTime());
        }
        catch (Exception e){
            if (!AppSettings.RELEASE_MODE){
                Log.e("Lessons Learned", "Could not reset Last Alert table " + e.getMessage() + ": " + e.getStackTrace());
            }
            else{
                Mint.logException(e);
            }
            return 90000;
        }
    }

    private static long getTimeDifference(Date sessionStart, Date sessionEnd) {
        try{
            Calendar startDateTime = Calendar.getInstance();
            startDateTime.setTime(sessionStart);

            Calendar endDateTime = Calendar.getInstance();
            endDateTime.setTime(sessionEnd);

            long milliseconds1 = startDateTime.getTimeInMillis();
            long milliseconds2 = endDateTime.getTimeInMillis();
            long diff = milliseconds2 - milliseconds1;

            return diff / (60 * 1000);
        }
        catch(Exception e){
            if (!AppSettings.RELEASE_MODE){
                Log.e("Lessons Learned", "Could not get time difference " + e.getMessage() + ": " + e.getStackTrace());
            }
            else{
                Mint.logException(e);
            }
            return 90000;
        }

    }
}
