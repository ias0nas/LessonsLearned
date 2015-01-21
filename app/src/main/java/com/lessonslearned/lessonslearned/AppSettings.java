package com.lessonslearned.lessonslearned;

public class AppSettings {

    public static boolean RELEASE_MODE = false;
    //minutes after which the same lessons that have been displayed the last three times can be re-used
    public static int REUSE_SAME_LESSON_ALERT_MINUTES = 60 * 24 * 4;
    //minimum of minutes after the next alert can be fired. Extra random hours will be added
    public static int NEXT_ALARM_MINUTES_MINIMUM = 60 * 24;
    //days after which a lesson can be used for an alert
    public static int LESSON_DAYS_AFTER_INSERT_USE_IN_ALERT = 3;
    public static String DATE_FORMAT_FULL = "yyyy-MM-dd HH:mm:ss";
    public static String DATE_FORMAT_DATE_ONLY = "dd MMM yyyy";
}
