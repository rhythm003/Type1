package com.rhythm003.type1;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.util.Arrays;

public class HeartActivity extends AppCompatActivity {
    private XYPlot xyplot;
    private XYSeries rec;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart);
        xyplot = (XYPlot) findViewById(R.id.heart_plot);
        BarFormatter formatter = new BarFormatter(Color.BLUE, Color.BLACK);
        Number[] data = {80, 85, 80, 95, 100};
        rec = new SimpleXYSeries(Arrays.asList(data), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "HR");
        xyplot.addSeries(rec, formatter);
    }
}
