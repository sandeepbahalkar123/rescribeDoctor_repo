package com.rescribe.doctor.ui.activities;

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

        mContext = SplashScreenActivity.this;

        // Hard Coded
       /* RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.AUTHTOKEN, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtb2JpbGVOdW1iZXIiOjc3Mzg0NzczMDYsInBhc3N3b3JkIjoidGVzdDEyMzQiLCJpYXQiOjE1MDY0OTgxNjgsImV4cCI6MTUwNjU4NDU2OH0.-RDHe-fXjjDW6PQQfglnlxY2k03siwsyaUOIwGj_TjI", mContext);
        RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, "10", mContext);
        RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.USER_NAME, "Dr. Shalu Gupta", mContext);
        RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.PROFILE_PHOTO, "", mContext);
        RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.EMAIL, "shalu.gupta@scorgtechnologies.com", mContext);
        RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.SPECIALITY, "General Physician", mContext);
        RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.ADDRESS, "Pune", mContext);
        RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.LOGIN_STATUS, RescribeConstants.YES, mContext);
        RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.PASSWORD, "1234", mContext);*/
        // End Hard Coded

        // start mqtt Service
        // use this to start and trigger a service
        Intent serviceIntent = new Intent(this, MQTTService.class);
        // potentially add data to the serviceIntent
        serviceIntent.putExtra(MQTTService.IS_MESSAGE, false);
        startService(serviceIntent);

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
