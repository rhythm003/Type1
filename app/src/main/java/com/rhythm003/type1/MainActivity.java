package com.rhythm003.type1;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;
import com.rhythm003.help.DbHelper;
import com.rhythm003.help.DbService;
import com.rhythm003.help.PeriodicService;
import com.rhythm003.help.SessionManager;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xyplot = (XYPlot) findViewById(R.id.home_plot);
        xyplot.getGraphWidget().setMarginTop(0);
        xyplot.setPlotMargins(0, 0, 0, 0);
        xyplot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 20);
        xyplot.setRangeValueFormat(new DecimalFormat("0"));
        xyplot.setRangeBoundaries(110, 230, BoundaryMode.FIXED);
        xyplot.getGraphWidget().setDomainLabelOrientation(-45);
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

    private void local_getGluLevel(List<Pair<Float, Long>> values) {
        if(values.size() == 0) return;
        xyplot.clear();
        List<Number> level_list = new ArrayList<>();
        List<Number> time_list = new ArrayList<>();
        for(int i = 0; i < values.size(); i++) {
            level_list.add(values.get(i).first);
            time_list.add(values.get(i).second);
        }
        if(level_list.size() == 1) {
            xyplot.setDomainBoundaries(time_list.get(0).longValue() - 100, time_list.get(0).longValue() + 100, BoundaryMode.FIXED);
        }

        XYSeries series = new SimpleXYSeries(time_list, level_list, "Glucose level");
        xyplot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
        PointLabelFormatter pointLabelFormatter = new PointLabelFormatter(Color.BLACK);
        LineAndPointFormatter formatter = new LineAndPointFormatter(Color.rgb(0, 0, 0), Color.BLUE, Color.TRANSPARENT, pointLabelFormatter);
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
        minXY = new PointF(xyplot.getCalculatedMinX().floatValue(), xyplot.getCalculatedMinY().floatValue());
        maxXY = new PointF(xyplot.getCalculatedMaxX().floatValue(), xyplot.getCalculatedMaxY().floatValue());
        leftX = minXY.x;
        rightX = maxXY.x;
        xyplot.setDomainStep(XYStepMode.SUBDIVIDE, 10);
        //xyplot.setDomainBoundaries(maxXY.x - 43600000, maxXY.x, BoundaryMode.AUTO);

        xyplot.setDomainValueFormat(new Format() {
            private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
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

        xyplot.redraw();

    }

}
