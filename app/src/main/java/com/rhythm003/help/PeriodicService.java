package com.rhythm003.help;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Rhythm003 on 9/6/2016.
 * Background service to start PeriodicReciever
 */
public class PeriodicService extends Service {
    PeriodicReceiver periodicReceiver = new PeriodicReceiver();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        periodicReceiver.restartTask(PeriodicService.this);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        periodicReceiver.stopTask(PeriodicService.this);
        super.onDestroy();
    }


}
