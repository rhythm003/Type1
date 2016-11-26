package com.rhythm003.type1;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.rhythm003.help.DbHelper;
import com.rhythm003.help.SessionManager;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
// Home page.
public class MainActivity extends AppCompatActivity {
    private Button btHeart, btGetLevel, btSetting, btCal;
    private SessionManager session;
    private TextView tvHello;
    private DbHelper dbHelper = new DbHelper(this);
    private XYPlot xyplot;
    private PointF minXY;
    private PointF maxXY;
    private float leftX;
    private float rightX;
    // Setup ui.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xyplot = (XYPlot) findViewById(R.id.home_plot);
        xyplot.getGraph().setMarginTop(10);
        xyplot.setPlotMargins(0, 0, 0, 0);
        xyplot.setRangeStep(StepMode.INCREMENT_BY_VAL, 20);
        xyplot.setRangeBoundaries(110, 230, BoundaryMode.FIXED);
        tvHello = (TextView) findViewById(R.id.tvHello);
        btGetLevel = (Button) findViewById(R.id.btGluLevel);
        btHeart = (Button) findViewById(R.id.btHeart);
        btSetting = (Button) findViewById(R.id.btSetting);
        btCal = (Button) findViewById(R.id.btCalLevel);
        session = new SessionManager(getApplicationContext());
        tvHello.setText("Hello, " + session.getUSER_NAME() + "!");
        btHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HeartActivity.class);
                startActivity(intent);
            }
        });

        btGetLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GluActivity.class);
                startActivity(intent);
            }
        });
        btSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
            }
        });
        btCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CalActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        new LocalDbTask().execute();
    }
    // AsyncTask to fetch data from sqlite and call update xyplot.
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
    // Update xyplot with input data.
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
        xyplot.getLayoutManager().remove(xyplot.getLegend());
        xyplot.addSeries(series, formatter);
        if(level_list.size() == 1) {
            xyplot.setDomainBoundaries(time_list.get(0).longValue() - 100, time_list.get(0).longValue() + 100, BoundaryMode.FIXED);
        }
        if(time_list.size() > 1) {
            xyplot.setDomainBoundaries(time_list.get(0), time_list.get(time_list.size() - 1), BoundaryMode.FIXED);
        }
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

}
