package com.rhythm003.help;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Rhythm003 on 9/6/2016.
 */
public class DbService extends Service {
    private DbHelper dbHelper = new DbHelper(this);
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getStringExtra("ACTION");
        if(action.equals("GET_GLU")) {
            Log.d("DbService", "GET_GLU");
            dbHelper.getGlu();
        }
        else if(action.equals("INSERT_GLU")) {
            Log.d("DbService", "INSERT_GLU");
            dbHelper.insertGlu(Float.parseFloat(intent.getStringExtra("LEVEL")), intent.getLongExtra("DEVICETIME", 0));
        }
        else {
            Log.d("DbService", "Nothing");
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}
