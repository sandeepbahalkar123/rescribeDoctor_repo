package com.rescribe.doctor.ui.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.rescribe.doctor.R;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.util.RescribeConstants;

public class SplashScreenActivity extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    private Context mContext;
    Dialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mContext = SplashScreenActivity.this;
        doNext();
        //  doAppCheckLogin();

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
/*
    private void doLogin() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (RescribePreferencesManager.getString(RescribeConstants.USERNAME, mContext).equals("") && RescribePreferencesManager.getString(RescribeConstants.PHONE, mContext).equals("")) {
                    Intent intentObj = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    //---- TO show login screen enable below line
                    //  Intent intentObj = new Intent(SplashScreenActivity.this, LoginMainActivity.class);
                    //------------
                    intentObj.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intentObj.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentObj.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intentObj);
                } else if (!RescribePreferencesManager.getString(RescribeConstants.USERNAME, mContext).equals("") && RescribePreferencesManager.getString(RescribeConstants.PHONE, mContext).equals("")) {
                    //TODO : UNCOMMET PhoneNoActivity to OTP screen
                    //    Intent intentObj = new Intent(SplashScreenActivity.this, PhoneNoActivity.class);
                    //        Intent intentObj = new Intent(SplashScreenActivity.this, HistoryActivity.class);
                    Intent intentObj = new Intent(SplashScreenActivity.this, DoctorListActivity.class);
                    intentObj.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intentObj.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentObj.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intentObj);
                } else if (!RescribePreferencesManager.getString(RescribeConstants.USERNAME, mContext).equals("") && (!RescribePreferencesManager.getString(RescribeConstants.PHONE, mContext).equals(""))) {
                    Intent intentObj = new Intent(SplashScreenActivity.this, PrescriptionActivity.class);
                    intentObj.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intentObj.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentObj.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intentObj);

                }
            }
        }, RescribeConstants.TIME_STAMPS.THREE_SECONDS);

    }

    private void doAppCheckLogin() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                String userName = RescribePreferencesManager.getString(RescribeConstants.USERNAME, mContext);
                String password = RescribePreferencesManager.getString(RescribeConstants.PASSWORD, mContext);

                Intent intentObj = null;

                if (RescribeConstants.BLANK.equalsIgnoreCase(userName) || RescribeConstants.BLANK.equalsIgnoreCase(password)) {
                    if (!RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.IS_VALID_IP_CONFIG, mContext).equals(RescribeConstants.TRUE)) {
                        //alert dialog for serverpath
                        CommonMethods.showAlertDialog(SplashScreenActivity.this, getString(R.string.server_path) + "\n" + getString(R.string.for_example_server_path), new CheckIpConnection() {
                            @Override
                            public void onOkButtonClickListner(String serverPath, Context context, Dialog dialog) {
                                mDialog = dialog;
                                mContext = context;
                                RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.SERVER_PATH, serverPath, context);

                                // mLoginHelper.checkConnectionToServer(serverPath);


                            }
                        });
                    } else {
                        intentObj = new Intent(mContext, LoginActivity.class);
                        intentObj.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intentObj.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intentObj.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intentObj);

                        finish();
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

                    }
                } else {
                    //------Check Remember ME first , then only move on next screen.
                    intentObj = new Intent(mContext, PrescriptionActivity.class);
                    intentObj.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intentObj.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentObj.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intentObj);

                    finish();
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                }


            }
        }, RescribeConstants.TIME_STAMPS.THREE_SECONDS);


        mContext = this;

        new Handler().postDelayed(new Runnable() {

            *//*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             *//*

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

                // close this activity
//                finish();
            }
        }, SPLASH_TIME_OUT);


    }*/

}
