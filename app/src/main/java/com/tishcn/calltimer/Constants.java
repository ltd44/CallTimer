package com.tishcn.calltimer;

/**
 * Created by leona on 2/23/2017.
 */

public class Constants {

    //DEFAULTS
    public static final String DEFAULT_CALL_LENGTH_MINS = "9";
    public static final String DEFAULT_CALL_LENGTH_SECS = "30";
    public static final boolean DEFAULT_ENABLE_TIMER = true;
    public static final String DEFAULT_SECONDS_BEFORE_END = "10";
    public static final String DEFAULT_EXTEND_MINS = "1";

    //PREFS
    public static final String PREF_CALL_LENGTH_MINS = "PREF_CALL_LENGTH_MINS";
    public static final String PREF_CALL_LENGTH_SECS = "PREF_CALL_LENGTH_SECS";
    public static final String PREF_ENABLE_TIMER = "PREF_ENABLE_TIMER";
    public static final String PREF_SECS_BEFORE_END = "PREF_SECS_BEFORE_END";
    public static final String PEFS_TIMER_RUNNING = "PREF_TIMER_RUNNING";
    public static final String PREF_EXTEND_MINS = "PREF_EXTEND_MINS";

    //NOTIF
    public static final String NOTIF_TITLE = "Call Time Remaining";
    public static final int NOTIF_ID_COUNTDOWN = 3644;
    public static final String NOTIF_KILL_BUTTON_CLICK_INTENT = "com.tishcn.calltracker.notifications.kill";
    public static final String NOTIF_TIME_BUTTON_CLICK_INTENT = "com.tishcn.calltracker.notifications.time";
}
