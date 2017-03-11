package com.riddhikakadia.brunchy2.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.riddhikakadia.brunchy2.R;

public class AboutUsActivity extends AppCompatActivity {

    TextView versionName;
    Button creditsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        versionName = (TextView) findViewById(R.id.app_version);
        creditsButton = (Button) findViewById(R.id.credits_button);

        String versionNameText = "";
        try {
            String appVersionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            int appVersionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;

            if (versionNameText != null) {
                versionNameText = versionName.getText() + " " + appVersionName;
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        versionName.setText(versionNameText);

        creditsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showCreditDialog = new Intent(getApplicationContext(), CreditsDialogActivity.class);
                startActivity(showCreditDialog);
            }
        });
    }
}
