package com.rhythm003.type1;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

// Calorie activity page
public class CalActivity extends AppCompatActivity {
    private Button btn_fitbit;
    private EditText et_cal, et_date, et_time;
    private Button btn_send;
    private DbHelper dbHelper = new DbHelper(this);
    private int year, month, day;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private XYPlot xyPlot;
    // Setup ui and xyplot.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cal);
        et_cal = (EditText) findViewById(R.id.cal_et_calnum);
        btn_send = (Button) findViewById(R.id.cal_btn_send);
        btn_fitbit = (Button) findViewById(R.id.cal_btn_fitbit);
        et_date = (EditText) findViewById(R.id.cal_et_date);
        et_time = (EditText) findViewById(R.id.cal_et_time);
        xyPlot = (XYPlot) findViewById(R.id.cal_plot);
        xyPlot.setPlotMargins(0, 0, 0, 0);
        Intent intent = getIntent();
        String data = intent.getStringExtra("calories");
        if (data != "") {
            //Toast.makeText(this, data.getQueryParameter("calories"), Toast.LENGTH_LONG).show();
            et_cal.setText(data);
        }
        btn_fitbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CalActivity.this, FitbitCalActivity.class);
                startActivity(intent);
            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd/HH:mm");
                    String time = et_date.getText().toString() + "/" + et_time.getText().toString();
                    Date date = simpleDateFormat.parse(time);
                    dbHelper.insertCal(Integer.parseInt(et_cal.getText().toString().trim()), Long.toString(date.getTime()));
                    et_cal.setText("");
                    Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                    new CalActivity.LocalDbTask().execute();
                } catch (Exception e) {
                    Log.e("CAL_ACTIVITY", e.getMessage());
                }
            }
        });
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        String date = year + "/" + (month + 1) + "/" + day;
        et_date.setText(date);
        datePickerDialog = new DatePickerDialog(this, R.style.AppDialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = year + "/" + (month + 1) + "/" + dayOfMonth;
                et_date.setText(date);
            }
        }, year, month, day);
        Date now = new Date();
        datePickerDialog.getDatePicker().setMaxDate(now.getTime());
        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        timePickerDialog = new TimePickerDialog(this, R.style.AppDialog, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time = hourOfDay + ":" + minute;
                et_time.setText(time);
            }
        }, now.getHours(), now.getMinutes(), true);
        et_time.setText(now.getHours() + ":" + now.getMinutes());
        et_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show();
            }
        });


    }
    @Override
    protected void onResume() {
        super.onResume();
        new CalActivity.LocalDbTask().execute();
    }

    // An asyncTask to retrieve data from sqlite and update xyplot.
    private class LocalDbTask extends AsyncTask<Void, Void, Void> {
        private List<Pair<Integer, Long>> values;
        // Retrieve data using dbHelper.
        @Override
        protected Void doInBackground(Void... voids) {
            values = dbHelper.getCal();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setPlot(values);
        }
    }
    // Setup and redraw xyplot.
    private void setPlot(List<Pair<Integer, Long>> values) {
        if(values.size() == 0) return;
        xyPlot.clear();
        List<Number> level_list = new ArrayList<>();
        final List<Number> time_list = new ArrayList<>();
        for(int i = 0; i < values.size(); i++) {
            level_list.add(values.get(i).first);
            time_list.add(values.get(i).second);
        }
        if(level_list.size() == 1) {
            xyPlot.setDomainBoundaries(time_list.get(0).longValue() - 100, time_list.get(0).longValue() + 100, BoundaryMode.FIXED);
        }

//        XYSeries series = new SimpleXYSeries(time_list, level_list, "Calorie log");
        XYSeries series = new SimpleXYSeries(level_list, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Calorie log");
        xyPlot.getGraph().getGridBackgroundPaint().setColor(Color.WHITE);
        BarFormatter formatter = new BarFormatter(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark));
        xyPlot.addSeries(series, formatter);
        BarRenderer barRenderer = (BarRenderer) xyPlot.getRenderer(BarRenderer.class);
        barRenderer.setBarWidthMode(BarRenderer.BarWidthMode.FIXED_WIDTH);
        barRenderer.setBarWidth(40f);
        xyPlot.getLayoutManager().remove(xyPlot.getLegend());
        xyPlot.setDomainBoundaries(-1, 11, BoundaryMode.FIXED);
        xyPlot.setRangeBoundaries(0, 1000, BoundaryMode.FIXED);
        xyPlot.setRangeStep(StepMode.INCREMENT_BY_VAL, 100.0);
        xyPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new NumberFormat() {
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
        xyPlot.redraw();
    }
}
