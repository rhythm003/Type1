package com.rhythm003.type1;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.rhythm003.app.AppConfig;
import com.rhythm003.app.AppController;
import com.rhythm003.help.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.SimpleFormatter;

public class FitbitCalActivity extends AppCompatActivity {
    private SessionManager session;
    private ListView lvCal;
    private ArrayList<String> items;
    private ArrayList<Integer> cals;
    private TextView tvStatus, tvTotal;
    private Button btnDone, btnLaunchFitbit;
    private int total;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitbit_cal);
        total = 0;
        tvStatus = (TextView) findViewById(R.id.fcal_tvStatus);
        lvCal = (ListView) findViewById(R.id.fcal_lvCal);
        tvTotal = (TextView) findViewById(R.id.fcal_tvTotal);
        btnDone = (Button) findViewById(R.id.fcal_btnDone);
        btnLaunchFitbit = (Button) findViewById(R.id.fcal_btnLaunchFitbit);
        session = new SessionManager(getApplicationContext());
        tvTotal.setText(Integer.toString(total));
        items = new ArrayList<>();
        cals = new ArrayList<>();
        getCal();
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FitbitCalActivity.this, CalActivity.class);
                intent.putExtra("calories", Integer.toString(total));
                startActivity(intent);
                finish();
            }
        });
        btnLaunchFitbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchFitbitApp();
            }
        });
    }

    private void launchFitbitApp() {
        PackageManager packageManager = getPackageManager();
        try {
            Intent intent = packageManager.getLaunchIntentForPackage("com.fitbit.FitbitMobile");
            if(intent == null) {
                return;
            }
            else {
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                startActivity(intent);
                return;
            }
        }
        catch (Exception e) {
            return;
        }
    }

    private void getCal() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String CALL_URL = AppConfig.FITBIT_CAL + sdf.format(date) + ".json";
        //Log.d("TEST", CALL_URL);
        StringRequest strReq = new StringRequest(Request.Method.GET,
                CALL_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("GET RESPONSE", "success");
                //tvStatus.setVisibility(View.GONE);
                try {

                    JSONObject jobj = new JSONObject(response);
                    JSONArray foodlog = jobj.getJSONArray("foods");
                    if(foodlog.length() > 0) tvStatus.setText("Recent Foods: ");
                    for(int i = 0; i < foodlog.length(); i++) {
                        JSONObject temp = foodlog.getJSONObject(i).getJSONObject("loggedFood");
                        items.add(temp.getString("name"));
                        cals.add(temp.getInt("calories"));
                    }
                }
                catch (Exception e) {
                    Log.e("CAL_RESPONSE", e.getMessage());
                }
                getYesterCal();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("GET RESPONSE", error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<String, String>();
                header.put("authorization", "Bearer " + session.getTOKEN());
                return  header;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);
    }

    private void getYesterCal() {
        Date date = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String CALL_URL = AppConfig.FITBIT_CAL + sdf.format(date) + ".json";
        //Log.d("TEST", CALL_URL);
        StringRequest strReq = new StringRequest(Request.Method.GET,
                CALL_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("GET RESPONSE", "success");
                //tvStatus.setVisibility(View.GONE);
                try {

                    JSONObject jobj = new JSONObject(response);
                    JSONArray foodlog = jobj.getJSONArray("foods");
                    for(int i = 0; i < foodlog.length(); i++) {
                        JSONObject temp = foodlog.getJSONObject(i).getJSONObject("loggedFood");
                        items.add(temp.getString("name"));
                        cals.add(temp.getInt("calories"));
                    }
                }
                catch (Exception e) {
                    Log.e("CAL_RESPONSE", e.getMessage());
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(FitbitCalActivity.this, android.R.layout.simple_list_item_multiple_choice, items);
                lvCal.setAdapter(arrayAdapter);
                lvCal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        //Toast.makeText(getApplicationContext(), cals.get(i).toString(), Toast.LENGTH_SHORT).show();
                        CheckedTextView ctvItem = (CheckedTextView) view;
                        if(!ctvItem.isChecked()) {
                            total += cals.get(i);
                            ctvItem.setChecked(true);
                        }
                        else {
                            total -= cals.get(i);
                            ctvItem.setChecked(false);
                        }
                        tvTotal.setText(Integer.toString(total));
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("GET RESPONSE", error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<String, String>();
                header.put("authorization", "Bearer " + session.getTOKEN());
                return  header;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);
    }
}
