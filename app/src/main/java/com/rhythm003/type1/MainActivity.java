package com.rhythm003.type1;

import android.content.Intent;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rhythm003.help.DbHelper;
import com.rhythm003.help.DbService;
import com.rhythm003.help.PeriodicService;
import com.rhythm003.help.SessionManager;

public class MainActivity extends AppCompatActivity {
    private Button btLogoff, btGetLevel, btSetting, btCal;
    private SessionManager session;
    private TextView tvHello;
    private DbHelper dbHelper = new DbHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvHello = (TextView) findViewById(R.id.tvHello);
        btGetLevel = (Button) findViewById(R.id.btGluLevel);
        btLogoff = (Button) findViewById(R.id.btLogoff);
        btSetting = (Button) findViewById(R.id.btSetting);
        btCal = (Button) findViewById(R.id.btCalLevel);
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
                Intent intent = new Intent(getApplicationContext(), GluActivity.class);
                startActivity(intent);
            }
        });
        btSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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


}
