package com.rhythm003.help;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by Rhythm003 on 9/6/2016.
 */
public class PeriodicReceiver extends BroadcastReceiver {
    private final static String TAG = "com.rhythm003.type1.PERI_TASK";
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction() != null) {
            if(intent.getAction().equals(TAG)) {
                doPeriodicTask(context);
            }
        }
    }
    private void doPeriodicTask(Context context) {
        Log.d("PeriodicReceiver", "doTask");
        Intent intent = new Intent(context, FitbitService.class);
        context.startService(intent);
        intent = new Intent(context, DbService.class);
        intent.putExtra("ACTION", "INSERT_GLU");
        Random random = new Random();
        intent.putExtra("LEVEL", new DecimalFormat("#.##").format(random.nextFloat() * 80 + 110));
        Long dt = new Date().getTime();
        intent.putExtra("DEVICETIME", dt);
        context.startService(intent);

    }

    public void restartTask(Context context) {
        Intent alarmIntent = new Intent(context, PeriodicReceiver.class);
        boolean isAlarmUp = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_NO_CREATE) != null;
        if(!isAlarmUp) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmIntent.setAction(TAG);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 300000, pendingIntent);
        }
    }

    public void stopTask(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, PeriodicReceiver.class);
        alarmIntent.setAction(TAG);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
        alarmManager.cancel(pendingIntent);
        context.stopService(new Intent(context, DbService.class));
        context.stopService(new Intent(context, FitbitService.class));
    }
}
