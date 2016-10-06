package com.rhythm003.type1;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.rhythm003.help.DbHelper;

public class CalActivity extends AppCompatActivity {
    private Button btn_fitbit;
    private EditText et_cal;
    private Button btn_send;
    private TimePicker tp;
    private DbHelper dbHelper = new DbHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cal);
        et_cal = (EditText) findViewById(R.id.cal_et_calnum);
        btn_send = (Button) findViewById(R.id.cal_btn_send);
        btn_fitbit = (Button) findViewById(R.id.cal_btn_fitbit);
        tp = (TimePicker) findViewById(R.id.cal_tp);
        Intent intent = getIntent();
        String data = intent.getStringExtra("calories");
        if(data != "") {
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
                    dbHelper.insertCal(Integer.parseInt(et_cal.getText().toString().trim()), Integer.toString(tp.getHour()) + ":" + Integer.toString(tp.getMinute()));
                    et_cal.setText("");
                    Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                }catch (Exception e) {
                    Log.e("CAL_ACTIVITY", e.getMessage());
                }
            }
        });

    }
}
