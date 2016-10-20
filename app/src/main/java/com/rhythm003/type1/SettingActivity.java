package com.rhythm003.type1;

import android.content.Intent;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.rhythm003.app.AppConfig;
import com.rhythm003.app.AppController;
import com.rhythm003.help.PeriodicService;
import com.rhythm003.help.SessionManager;
import com.rhythm003.help.Util;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SettingActivity extends AppCompatActivity {
    private Button btnFitbit, btnLogoff, btnStartTrack, btnStopTrack;
    private SessionManager session;
    private TextView tvLink;
    private String code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        btnFitbit = (Button) findViewById(R.id.set_btnFitbit);
        tvLink = (TextView) findViewById(R.id.set_tvLink);
        session = new SessionManager(getApplicationContext());
        Intent intent = getIntent();
        Uri data = intent.getData();
        if(data != null) {
            //Toast.makeText(getApplicationContext(), data.toString(), Toast.LENGTH_LONG).show();
            //Log.d("Return from oauth", data.toString());
            int idx1 = data.toString().indexOf("code=");
            int idx2 = data.toString().indexOf("#_=_");
            code = new String();
            code = data.toString().substring(idx1 + 5, idx2);
            Log.d("CODE: ", code);
            getToken();
            //session.setToken(token);
        }
        if(session.getToken() != "") {
            tvLink.setText("Account connected");
        }
        btnFitbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent intent = builder.build();
                intent.launchUrl(SettingActivity.this, Uri.parse(AppConfig.FITBIT_CODE));
            }
        });
        btnLogoff = (Button) findViewById(R.id.set_btnLogoff);
        btnLogoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.setLogoff();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnStartTrack = (Button) findViewById(R.id.set_btnStartTrack);
        btnStopTrack = (Button) findViewById(R.id.set_btnStopTrack);
        btnStartTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PeriodicService.class);
                startService(intent);
            }
        });
        btnStopTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PeriodicService.class);
                stopService(intent);
            }
        });
    }

    private void getToken() {

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.FITBIT_TOKEN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("GET TOKEN", response);
                try {
                    JSONObject json = new JSONObject(response);
                    session.setToken((String) json.get("access_token"));
                    session.setRToken((String) json.get("refresh_token"));
//                    Log.d("TOKEN", session.getToken());
//                    Log.d("RTOKEN", session.getRToken());
                }
                catch (Exception e) {
                    Log.e("GET TOKEN", e.getMessage());
                };
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("GET TOKEN", "BAD");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<String, String>();
                try {
                    String secret = Util.getProperty("client_id", getApplicationContext()) + ":" + Util.getProperty("client_secret", getApplicationContext());
                    byte[] secret_b = secret.getBytes();
                    header.put("authorization", "Basic " + Base64.encodeToString(secret_b, Base64.DEFAULT));
                }
                catch (Exception e) {
                    Log.e("GET HEADER", e.getMessage());
                }
                return  header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("grant_type", "authorization_code");
                try {
                    params.put("client_id", Util.getProperty("client_id", getApplicationContext()));
                }
                catch (Exception e) {
                    Log.e("GET PARAM", e.getMessage());
                }
                params.put("code", code);
                params.put("redirect_uri", "quickpredict://fitbit");
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq);
    }


}
