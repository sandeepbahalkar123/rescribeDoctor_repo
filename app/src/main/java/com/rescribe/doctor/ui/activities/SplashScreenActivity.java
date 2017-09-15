package com.rescribe.doctor.ui.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.rescribe.doctor.R;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.service.MQTTService;
import com.rescribe.doctor.util.RescribeConstants;

public class SplashScreenActivity extends AppCompatActivity {

    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // start mqtt Service
        // use this to start and trigger a service
        Intent serviceIntent = new Intent(this, MQTTService.class);
        // potentially add data to the serviceIntent
        serviceIntent.putExtra(MQTTService.IS_MESSAGE, false);
        startService(serviceIntent);

        mContext = SplashScreenActivity.this;
        doNext();

    }

    private void doNext() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.LOGIN_STATUS, mContext).equals(RescribeConstants.YES)) {
                    Intent intentObj = new Intent(SplashScreenActivity.this, HomePageActivity.class);
                    startActivity(intentObj);
                } else {
                    Intent intentObj = new Intent(SplashScreenActivity.this, LoginSignUpActivity.class);
                    startActivity(intentObj);
                }
                finish();
            }
        }, RescribeConstants.TIME_STAMPS.THREE_SECONDS);
    }
}
