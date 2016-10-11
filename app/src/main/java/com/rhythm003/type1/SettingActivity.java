package com.rhythm003.type1;

import android.content.Intent;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rhythm003.app.AppConfig;
import com.rhythm003.help.PeriodicService;
import com.rhythm003.help.SessionManager;

public class SettingActivity extends AppCompatActivity {
    private Button btnFitbit, btnLogoff, btnStartTrack, btnStopTrack;
    private SessionManager session;
    private TextView tvLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        btnFitbit = (Button) findViewById(R.id.set_btnFitbit);
        tvLink = (TextView) findViewById(R.id.set_tvLink);
        session = new SessionManager(getApplicationContext());
        Intent intent = getIntent();
        Uri data = intent.getData();
        if(data != null) {
            //Toast.makeText(getApplicationContext(), data.toString(), Toast.LENGTH_LONG).show();
            //Log.d("Return from oauth", data.toString());
            String token = new String();
            int idx1 = data.toString().indexOf("access_token");
            int idx2 = data.toString().indexOf('&');
            token = data.toString().substring(idx1 + 13, idx2);
            Log.d("TOKEN: ", token);
            session.setToken(token);
        }
        if(session.getTOKEN() != "") {
            tvLink.setText("Account connected");
        }
        btnFitbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent intent = builder.build();
                intent.launchUrl(SettingActivity.this, Uri.parse(AppConfig.FITBIT_TOKEN));
            }
        });
        btnLogoff = (Button) findViewById(R.id.set_btnLogoff);
        btnLogoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.setLogoff();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnStartTrack = (Button) findViewById(R.id.set_btnStartTrack);
        btnStopTrack = (Button) findViewById(R.id.set_btnStopTrack);
        btnStartTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PeriodicService.class);
                startService(intent);
            }
        });
        btnStopTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PeriodicService.class);
                stopService(intent);
            }
        });
    }
}
