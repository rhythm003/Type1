package com.rhythm003.help;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.rhythm003.app.AppConfig;
import com.rhythm003.app.AppController;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rhythm003 on 10/5/2016.
 * Background service to make queries to Fitbit API
 */

public class FitbitService extends Service {
    private SessionManager session;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        session = new SessionManager(getApplicationContext());
        getHRate();
        return START_NOT_STICKY;
    }

    private void getHRate() {
        // Setup Volley request
        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.FITBIT_HRATE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("HRATE RESPONSE", "got response");
                try {
                    JSONObject jobj = new JSONObject(response);
                    JSONArray intraday = jobj.getJSONObject("activities-heart-intraday").getJSONArray("dataset");
                    String day = jobj.getJSONArray("activities-heart").getJSONObject(0).getString("dateTime") + ":";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss");
                    // Get the most recent 5 intraday heart rate data.
                    for(int i = 1; i < 6 && i < intraday.length(); i++) {
                        try {
                            Date date = simpleDateFormat.parse(day + intraday.getJSONObject(intraday.length() - i).getString("time"));
                            Intent intent = new Intent(getApplicationContext(), DbService.class);
                            intent.putExtra("ACTION", "INSERT_HR");
                            intent.putExtra("LEVEL", intraday.getJSONObject(intraday.length() - i).getString("value"));
                            intent.putExtra("INTIME", Long.toString(date.getTime()));
                            startService(intent);
                        }
                        catch (Exception e) {
                            Log.e("DBINSERT", e.getMessage());
                        }
                    }
                    if(intraday.length() == 0) Log.d("HRATE", "No data");
                }
                catch (Exception e) {
                    Log.e("HRATE", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("HRATE RESPONSE", error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<String, String>();
                header.put("authorization", "Bearer " + session.getToken());
                return  header;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);
    }
}
