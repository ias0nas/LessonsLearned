package com.lessonslearned.lessonslearned;

import android.content.Context;

/**
 * Created by ias0nas on 27/04/14.
 */
public class AlarmSetter {
    private static Alarm alarm;

    public static void startAlarm(Context context){
        alarm = new Alarm();
        alarm.setAlarm(context);
    }
}
