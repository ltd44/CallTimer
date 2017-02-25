package com.tishcn.calltimer;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by leona on 2/23/2017.
 */

public class PhoneStateReceiver extends BroadcastReceiver {

    private static SharedPreferences mPrefs;
    private static SharedPreferences.Editor mEditor;
    private static CountDownTimer mTimer;
    private static String mTag = "PhoneStateRec:";
    private static long mTimerTimeMillis;

    @SuppressLint("CommitPrefEdits")
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            mEditor = mPrefs.edit();
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            boolean timerEnabled = mPrefs.getBoolean(Constants.PREF_ENABLE_TIMER, Constants.DEFAULT_ENABLE_TIMER);
            Log.d(mTag, "Phone State = " + state);
            boolean timerRunning = mPrefs.getBoolean(Constants.PEFS_TIMER_RUNNING, false);
            if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK) && !timerRunning && timerEnabled){
                long timerMins = Long.parseLong(mPrefs.getString(Constants.PREF_CALL_LENGTH_MINS, Constants.DEFAULT_CALL_LENGTH_MINS));
                long timerSecs = Long.parseLong(mPrefs.getString(Constants.PREF_CALL_LENGTH_SECS, Constants.DEFAULT_CALL_LENGTH_SECS));
                startTimer(context, timerMins, timerSecs);
            } else if(state.equals(TelephonyManager.EXTRA_STATE_IDLE) && timerRunning) {
                try {
                    mTimer.cancel();
                } catch (Exception ignored){
                }
                mEditor.putBoolean(Constants.PEFS_TIMER_RUNNING, false);
                mEditor.apply();
                NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                nm.cancelAll();
            }
        } else if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
        }
    }

    private static void startTimer(final Context context, final long timerMins, final long timerSecs) {
        final Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        final ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, 90);
        long secsBefore = Long.parseLong(mPrefs.getString(Constants.PREF_SECS_BEFORE_END, Constants.DEFAULT_SECONDS_BEFORE_END));
        final long secMillis = 1000;
        final long[] timeMillis = {((timerMins * secMillis * 60) + (timerSecs * secMillis))};
        mTimerTimeMillis = timeMillis[0];
        final long secsBeforeMillis = secsBefore * secMillis;
        mTimer = new CountDownTimer(timeMillis[0], secMillis) {
            @Override
            public void onTick(long l) {
                timeMillis[0] = timeMillis[0] - secMillis;
                mTimerTimeMillis = timeMillis[0];
                mNotify(context, timeMillis[0]);
                if(timeMillis[0] == secsBeforeMillis){
                    vibrator.vibrate(secMillis);
                    toneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT);
                }
            }

            @Override
            public void onFinish() {
                killCall(context);
                mEditor.putBoolean(Constants.PEFS_TIMER_RUNNING, false);
                mEditor.apply();
                NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                nm.cancelAll();
            }
        };
        mTimer.start();
        mNotify(context, timeMillis[0]);
        mEditor.putBoolean(Constants.PEFS_TIMER_RUNNING, true);
        mEditor.apply();
    }

    public static void killTimer(Context context){
        try {
            mTimer.cancel();
        } catch (Exception ignored){
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constants.PEFS_TIMER_RUNNING, false);
        editor.apply();
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();
    }

    public static void extendTimer(Context context){
        try {
            mTimer.cancel();
        } catch (Exception ignored){
        }
        long timerMins = mTimerTimeMillis / 60000;
        long timerSecs = (mTimerTimeMillis - (timerMins * 60000)) / 1000;
        int extendMins = Integer.parseInt(mPrefs.getString(Constants.PREF_EXTEND_MINS, Constants.DEFAULT_EXTEND_MINS));
        timerMins = timerMins + extendMins;
        startTimer(context, timerMins, timerSecs);
    }

    public static boolean killCall(Context context) {
        try {
            // Get the boring old TelephonyManager
            TelephonyManager telephonyManager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            // Get the getITelephony() method
            Class classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");

            // Ignore that the method is supposed to be private
            methodGetITelephony.setAccessible(true);

            // Invoke getITelephony() to get the ITelephony interface
            Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);

            // Get the endCall method from ITelephony
            Class telephonyInterfaceClass =
                    Class.forName(telephonyInterface.getClass().getName());
            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");

            // Invoke endCall()
            methodEndCall.invoke(telephonyInterface);

        } catch (Exception ex) { // Many things can go wrong with reflection calls
            Log.d(mTag,"**KILLCALL - PhoneStateReceiver **" + ex.toString());
            return false;
        }
        return true;
    }

    private static void mNotify(Context context, long timeMillis){
        long secMillis = 1000;
        long minMillis = secMillis * 60;
        long mins = timeMillis / minMillis;
        long secs = (timeMillis - (mins * minMillis)) / secMillis;
        String strMins = "0".concat(String.valueOf(mins));
        String strSecs = "0".concat(String.valueOf(secs));
        String strTime = strMins.substring(strMins.length()-2) + ":" + strSecs.substring(strSecs.length() - 2);
        int extendMins = Integer.parseInt(mPrefs.getString(Constants.PREF_EXTEND_MINS, Constants.DEFAULT_EXTEND_MINS));
        String strExtendMins = "Extend " + extendMins;
        if(extendMins == 1){
            strExtendMins = strExtendMins + " Min";
        } else {
            strExtendMins = strExtendMins + " Mins";
        }
        int notifId = Constants.NOTIF_ID_COUNTDOWN;
        Intent openAppIntent = new Intent(context, MainActivity.class);
        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent openAppPendIntent = PendingIntent.getActivity(context, 0, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);;
        Intent btn1Intent = new Intent(Constants.NOTIF_KILL_BUTTON_CLICK_INTENT);
        Intent btn2Intent = new Intent(Constants.NOTIF_TIME_BUTTON_CLICK_INTENT);
        PendingIntent pendingIntentBtn1 = PendingIntent.getBroadcast(context, 1, btn1Intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntentBtn2 = PendingIntent.getBroadcast(context, 2, btn2Intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action action1 = new NotificationCompat.Action(0, "Stop Timer", pendingIntentBtn1);
        NotificationCompat.Action action2 = new NotificationCompat.Action(0, strExtendMins, pendingIntentBtn2);
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_timer);
        builder.setContentTitle(Constants.NOTIF_TITLE);
        builder.setContentText(strTime);
        builder.setContentIntent(openAppPendIntent);
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.addAction(action1);
        builder.addAction(action2);
        nm.notify(notifId, builder.build());
    }
}
