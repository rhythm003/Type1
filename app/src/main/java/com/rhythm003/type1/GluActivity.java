package com.rhythm003.type1;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;
import com.rhythm003.app.AppConfig;
import com.rhythm003.app.AppController;
import com.rhythm003.help.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GluActivity extends AppCompatActivity {
    private XYPlot xyplot;
    private EditText glu_etLevel;
    private Button glu_btUpdate;
    private SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glu);
        session = new SessionManager(getApplicationContext());
        glu_etLevel = (EditText) findViewById(R.id.et_glu_level);
        glu_btUpdate = (Button) findViewById(R.id.bt_glu_update);
        xyplot = (XYPlot) findViewById(R.id.glu_plot);
        Number[] levels = {200, 190, 150, 160, 175};
        long now = (new Date()).getTime();
        Number[] times = {now, now + 3600000, now + 7200000, now + 10800000, now + 14400000};
        XYSeries series = new SimpleXYSeries(Arrays.asList(times), Arrays.asList(levels), "Glucose level");
        xyplot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
        PointLabelFormatter pointLabelFormatter = new PointLabelFormatter(Color.BLACK);
        LineAndPointFormatter formatter = new LineAndPointFormatter(Color.rgb(0, 0, 0), Color.BLUE, Color.TRANSPARENT, pointLabelFormatter);
        formatter.setInterpolationParams(new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));
        xyplot.addSeries(series, formatter);
        xyplot.setDomainStep(XYStepMode.SUBDIVIDE, times.length);
        xyplot.setTicksPerRangeLabel(5);
        xyplot.setRangeBoundaries(100, 250, BoundaryMode.FIXED);

        xyplot.setDomainValueFormat(new Format() {
            private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            @Override
            public StringBuffer format(Object o, StringBuffer stringBuffer, FieldPosition fieldPosition) {
                Date date = new Date(((Number) o).longValue());
                return dateFormat.format(date, stringBuffer, fieldPosition);
            }

            @Override
            public Object parseObject(String s, ParsePosition parsePosition) {
                return null;
            }
        });
        glu_btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String level = glu_etLevel.getText().toString().trim();
                String devicetime = Long.toString((new Date()).getTime());
                Toast.makeText(getApplicationContext(), devicetime, Toast.LENGTH_SHORT).show();
                postGluLevel(level, devicetime);
                //getGluLevel();
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
                        Toast.makeText(getApplicationContext(), jObj.toString(), Toast.LENGTH_SHORT).show();
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
                header.put("authorization", session.getUSER_APIKEY());
                return  header;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, req_tag);
    }
    private void postGluLevel(final String level, final String devicetime) {
        String req_tag = "req_post_glulevel";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GLU, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<String, String>();
                header.put("authorization", session.getUSER_APIKEY());
                return  header;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("level", level);
                params.put("devicetime", devicetime);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, req_tag);
    }
}
