package com.rhythm003.type1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Button btLogoff, btGetLevel;
    private SessionManager session;
    private TextView tvHello;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvHello = (TextView) findViewById(R.id.tvHello);
        btGetLevel = (Button) findViewById(R.id.btGetLevel);
        btLogoff = (Button) findViewById(R.id.btLogoff);
        session = new SessionManager(getApplicationContext());
        tvHello.setText("Hello, " + session.getUSER_NAME() + "!");
        btLogoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.setLogoff();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btGetLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getGluLevel();
            }
        });
    }

    private void getGluLevel() {
        String req_tag = "req_glulevel";
        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_GLU, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Toast.makeText(getApplicationContext(), jObj.getString("level"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<String, String>();
                header.put("authorization", "97ca6fc0c9467c0fa9a182e499a8d693");
                return  header;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, req_tag);
    }
}
