package com.tishcn.calltimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by leona on 2/24/2017.
 */

public class NotificationButtonClickReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Constants.NOTIF_KILL_BUTTON_CLICK_INTENT)){
            PhoneStateReceiver.killTimer(context);
        } else if(intent.getAction().equals(Constants.NOTIF_TIME_BUTTON_CLICK_INTENT)) {
            PhoneStateReceiver.extendTimer(context);
        }
    }
}
