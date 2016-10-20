package com.rhythm003.help;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.rhythm003.app.AppConfig;
import com.rhythm003.app.AppController;
import com.rhythm003.type1.MainActivity;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Rhythm003 on 9/6/2016.
 */
public class PeriodicReceiver extends BroadcastReceiver {
    private final static String TAG = "com.rhythm003.type1.PERI_TASK";
    private SessionManager session;
    @Override
    public void onReceive(Context context, Intent intent) {
        session = new SessionManager(context);
        if(intent.getAction() != null) {
            if(intent.getAction().equals(TAG)) {
                doPeriodicTask(context);
            }
            if(intent.getAction().equals("android.intent.action.VIEW")) {
                Log.d("Catch Callback", intent.getData().toString());
            }
            refreshToken(context);
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
    private void refreshToken(final Context context) {

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.FITBIT_TOKEN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("GET RESPONSE", response);
                try {
                    JSONObject json = new JSONObject(response);
                    session.setToken((String) json.get("access_token"));
                    session.setRToken((String) json.get("refresh_token"));
                    //Log.d("TOKEN", session.getToken());
                }
                catch (Exception e) {
                    Log.e("REFRESH TOKEN", e.getMessage());
                };
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("REFRESH", "BAD");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<String, String>();
                try {
                    String secret = Util.getProperty("client_id", context) + ":" + Util.getProperty("client_secret", context);
                    byte[] secret_b = secret.getBytes();
                    header.put("authorization", "Basic " + Base64.encodeToString(secret_b, Base64.DEFAULT));
                }
                catch (Exception e){
                    Log.e("GET HEADER", e.getMessage());
                }
                return  header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("grant_type", "refresh_token");
                params.put("refresh_token", session.getRToken());
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);
    }
}
