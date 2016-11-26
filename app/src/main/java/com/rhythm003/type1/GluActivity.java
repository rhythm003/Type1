package com.rhythm003.type1;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
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
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.rhythm003.app.AppConfig;
import com.rhythm003.app.AppController;
import com.rhythm003.help.DbHelper;
import com.rhythm003.help.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Activity to show glucose data.
public class GluActivity extends AppCompatActivity implements View.OnTouchListener{
    private static final String TAG = GluActivity.class.getSimpleName();
    private XYPlot xyplot;
    private SessionManager session;
    private float leftX;
    private float rightX;
    private PointF minXY;
    private PointF maxXY;
    private DbHelper dbHelper = new DbHelper(this);
    // Setup ui.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glu);
        session = new SessionManager(getApplicationContext());
        xyplot = (XYPlot) findViewById(R.id.glu_plot);
        //xyplot.setOnTouchListener(this);
        //xyplot.setMarkupEnabled(true);
        xyplot.getGraph().setMarginTop(0);
        xyplot.setPlotMargins(0, 0, 0, 0);
        xyplot.setRangeStep(StepMode.INCREMENT_BY_VAL, 20);
        xyplot.setRangeBoundaries(110, 230, BoundaryMode.FIXED);
        //getGluLevel();
        //local_getGluLevel();
        new LocalDbTask().execute();

    }
    // AsyncTask to fetch data from sqlite and call update xyplot with new data.
    private class LocalDbTask extends AsyncTask<Void, Void, Void> {
        private List<Pair<Float, Long>> values;
        @Override
        protected Void doInBackground(Void... voids) {
            values = dbHelper.getGlu();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            local_getGluLevel(values);
        }
    }
    // Update xyplot with input values.
    private void local_getGluLevel(List<Pair<Float, Long>> values) {
        if(values.size() == 0) return;
        xyplot.clear();
        List<Number> level_list = new ArrayList<>();
        List<Number> time_list = new ArrayList<>();
        Date now = new Date();
        for(int i = 0; i < values.size(); i++) {
//            if(Math.abs(now.getTime() - values.get(i).second) < 86400000) {
                level_list.add(values.get(i).first);
                time_list.add(values.get(i).second);
//            }
        }
        if(level_list.size() == 1) {
            xyplot.setDomainBoundaries(time_list.get(0).longValue() - 100, time_list.get(0).longValue() + 100, BoundaryMode.FIXED);
        }

        XYSeries series = new SimpleXYSeries(time_list, level_list, "Glucose level");
        xyplot.getGraph().getGridBackgroundPaint().setColor(Color.WHITE);
        PointLabelFormatter pointLabelFormatter = new PointLabelFormatter(Color.BLACK);
        LineAndPointFormatter formatter = new LineAndPointFormatter(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorAccent), Color.TRANSPARENT, pointLabelFormatter);
        if(level_list.size() > 3) {
            formatter.setInterpolationParams(new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Uniform));
        }
//        if(level_list.size() <= 3) {
//            xyplot.setOnTouchListener(null);
//        }
//        else {
//            xyplot.setOnTouchListener(GluActivity.this);
//        }
        xyplot.addSeries(series, formatter);
        xyplot.calculateMinMaxVals();
        xyplot.setDomainBoundaries(time_list.get(0), time_list.get(time_list.size() - 1), BoundaryMode.FIXED);
        xyplot.setDomainStep(StepMode.SUBDIVIDE, time_list.size());

        xyplot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new NumberFormat() {
            @Override
            public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
                String res = "";
                SimpleDateFormat simpleDateFormat;
                long time = (long)number;
                Date now = new Date();
                if(Math.abs(now.getTime() - time) < 86400000) {
                    simpleDateFormat = new SimpleDateFormat("HH:mm");
                }
                else {
                    simpleDateFormat = new SimpleDateFormat("MM/dd");
                }
                Date date = new Date(time);
                res = simpleDateFormat.format(date);
                return new StringBuffer(res);
            }

            @Override
            public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
                return null;
            }

            @Override
            public Number parse(String source, ParsePosition parsePosition) {
                return null;
            }
        });

        xyplot.redraw();

    }
    // Get glucose data from remote server.
    private void getGluLevel() {
        String req_tag = "req_glulevel";
        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_GLU, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error && jObj.getJSONArray("glurec").length() > 0) {
                        //Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_SHORT).show();
                        xyplot.clear();
                        List<Number> level_list = new ArrayList<>();
                        List<Number> time_list = new ArrayList<>();
                        JSONArray glurec = jObj.getJSONArray("glurec");
                        for(int i = 0; i < glurec.length(); i++) {
                            level_list.add(glurec.getJSONObject(i).getDouble("level"));
                            time_list.add(glurec.getJSONObject(i).getLong("devicetime"));
                        }
                        if(level_list.size() == 1) {
                            xyplot.setDomainBoundaries(time_list.get(0).longValue() - 100, time_list.get(0).longValue() + 100, BoundaryMode.FIXED);
                        }

                        XYSeries series = new SimpleXYSeries(time_list, level_list, "Glucose level");
                        xyplot.getGraph().getGridBackgroundPaint().setColor(Color.WHITE);
                        PointLabelFormatter pointLabelFormatter = new PointLabelFormatter(Color.BLACK);
                        LineAndPointFormatter formatter = new LineAndPointFormatter(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorAccent), Color.TRANSPARENT, pointLabelFormatter);
//                        if(level_list.size() > 5) {
//                            formatter.setInterpolationParams(new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Uniform));
//                        }
                        if(level_list.size() <= 3) {
                            xyplot.setOnTouchListener(null);
                        }
                        else {
                            xyplot.setOnTouchListener(GluActivity.this);
                        }
                        xyplot.addSeries(series, formatter);
                        xyplot.calculateMinMaxVals();
//                        minXY = new PointF(xyplot.getDomainLeftMin().floatValue(), xyplot.getRangeBottomMin().floatValue());
//                        maxXY = new PointF(xyplot.getDomainRightMax().floatValue(), xyplot.getRangeTopMax().floatValue());
//                        leftX = minXY.x;
//                        rightX = maxXY.x;
                        xyplot.setDomainStep(StepMode.SUBDIVIDE, 10);
                        //xyplot.setDomainBoundaries(maxXY.x - 43600000, maxXY.x, BoundaryMode.AUTO);

//                        xyplot.setDomainValueFormat(new Format() {
//                            private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
//                            @Override
//                            public StringBuffer format(Object o, StringBuffer stringBuffer, FieldPosition fieldPosition) {
//                                Date date = new Date(((Number) o).longValue());
//                                return dateFormat.format(date, stringBuffer, fieldPosition);
//                            }
//
//                            @Override
//                            public Object parseObject(String s, ParsePosition parsePosition) {
//                                return null;
//                            }
//                        });

                        xyplot.redraw();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error fetching data");
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
    // Insert glucose record to remote server.
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
                        getGluLevel();

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

    // Definition of the touch states
    static final int NONE = 0;
    static final int ONE_FINGER_DRAG = 1;
    static final int TWO_FINGERS_DRAG = 2;
    int mode = NONE;

    PointF firstFinger;
    float distBetweenFingers;
    boolean stopThread = false;

    @Override
    public boolean onTouch(View arg0, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: // Start gesture
                firstFinger = new PointF(event.getX(), event.getY());
                mode = ONE_FINGER_DRAG;
                stopThread = true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_POINTER_DOWN: // second finger
                distBetweenFingers = spacing(event);
                // the distance check is done to avoid false alarms
                if (distBetweenFingers > 5f) {
                    mode = TWO_FINGERS_DRAG;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == ONE_FINGER_DRAG) {
                    PointF oldFirstFinger = firstFinger;
                    firstFinger = new PointF(event.getX(), event.getY());
                    scroll(oldFirstFinger.x - firstFinger.x);
                    xyplot.setDomainBoundaries(minXY.x, maxXY.x,
                            BoundaryMode.FIXED);
                    xyplot.redraw();

                } else if (mode == TWO_FINGERS_DRAG) {
                    float oldDist = distBetweenFingers;
                    distBetweenFingers = spacing(event);
                    zoom(oldDist / distBetweenFingers);
                    xyplot.setDomainBoundaries(minXY.x, maxXY.x,
                            BoundaryMode.FIXED);
                    xyplot.redraw();
                }
                break;
        }
        return true;
    }

    private void zoom(float scale) {
        float domainSpan = maxXY.x - minXY.x;
        float domainMidPoint = maxXY.x - domainSpan / 2.0f;
        float offset = domainSpan * scale / 2.0f;

        minXY.x = domainMidPoint - offset;
        maxXY.x = domainMidPoint + offset;
//        if(minXY.x < leftX) {
//            minXY.x = leftX;
//        }
//        if(maxXY.x > rightX) {
//            maxXY.x = rightX;
//        }
    }

    private void scroll(float pan) {
        float domainSpan = maxXY.x - minXY.x;
        float step = domainSpan / xyplot.getWidth();
        float offset = pan * step;
        minXY.x = minXY.x + offset;
        maxXY.x = maxXY.x + offset;
//        if(minXY.x < leftX) {
//            minXY.x = leftX;
//        }
//        if(maxXY.x > rightX) {
//            maxXY.x = rightX;
//        }
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.hypot(x, y);
    }
}
