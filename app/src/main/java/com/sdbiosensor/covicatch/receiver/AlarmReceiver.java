package com.sdbiosensor.covicatch.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sdbiosensor.covicatch.events.CloseAllScreens;
import com.sdbiosensor.covicatch.screens.TimerActivity;

import org.greenrobot.eventbus.EventBus;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        EventBus.getDefault().post(new CloseAllScreens());

        Intent activityIntent = new Intent(context, TimerActivity.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(activityIntent);
    }

}
