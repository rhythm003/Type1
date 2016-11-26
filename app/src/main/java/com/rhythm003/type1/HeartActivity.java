package com.rhythm003.type1;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.rhythm003.help.DbHelper;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
// Activity to show heart rate data.
public class HeartActivity extends AppCompatActivity {
    private XYPlot xyplot;
    private XYSeries rec;
    private DbHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart);
        dbHelper = new DbHelper(this);
        xyplot = (XYPlot) findViewById(R.id.heart_plot);
        xyplot.setPlotMargins(0, 0, 0, 0);
        
    }

    @Override
    protected void onResume() {
        super.onResume();
        new HeartActivity.LocalDbTask().execute();
    }
    // AsyncTask to fetch data from sqlite and call update xyplot.
    private class LocalDbTask extends AsyncTask<Void, Void, Void> {
        private List<Pair<Integer, Long>> values;
        @Override
        protected Void doInBackground(Void... voids) {
            values = dbHelper.getHR();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setPlot(values);
        }
    }
    // Update xyplot with input values.
    private void setPlot(List<Pair<Integer, Long>> values) {
        if(values.size() == 0) return;
        xyplot.clear();
        List<Number> level_list = new ArrayList<>();
        final List<Number> time_list = new ArrayList<>();
        for(int i = 0; i < values.size(); i++) {
            level_list.add(values.get(i).first);
            time_list.add(values.get(i).second);
        }
        if(level_list.size() == 1) {
            xyplot.setDomainBoundaries(time_list.get(0).longValue() - 100, time_list.get(0).longValue() + 100, BoundaryMode.FIXED);
        }

//        XYSeries series = new SimpleXYSeries(time_list, level_list, "Calorie log");
        XYSeries series = new SimpleXYSeries(level_list, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Calorie log");
        xyplot.getGraph().getGridBackgroundPaint().setColor(Color.WHITE);
        BarFormatter formatter = new BarFormatter(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark));
        xyplot.addSeries(series, formatter);
        BarRenderer barRenderer = (BarRenderer) xyplot.getRenderer(BarRenderer.class);
        barRenderer.setBarWidthMode(BarRenderer.BarWidthMode.FIXED_WIDTH);
        barRenderer.setBarWidth(40f);
        xyplot.getLayoutManager().remove(xyplot.getLegend());
        xyplot.setDomainBoundaries(-1, 11, BoundaryMode.FIXED);
        xyplot.setRangeBoundaries(0, 120, BoundaryMode.FIXED);
        xyplot.setRangeStep(StepMode.INCREMENT_BY_VAL, 10.0);
        xyplot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new NumberFormat() {
            @Override
            public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
                String res = "";
                if(number >= 0 && number < time_list.size()) {
                    long time = time_list.get((int)number).longValue();
                    Date now = new Date();
                    SimpleDateFormat simpleDateFormat;
                    if(Math.abs(now.getTime() - time) < 86400000) {
                        simpleDateFormat = new SimpleDateFormat("HH:mm");
                    }
                    else {
                        simpleDateFormat = new SimpleDateFormat("MM/dd");
                    }
                    Date date = new Date(time);

                    res = simpleDateFormat.format(date);
                }
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
