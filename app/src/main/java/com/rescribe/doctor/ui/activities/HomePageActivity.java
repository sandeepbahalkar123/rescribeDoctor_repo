package com.rescribe.doctor.ui.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.heinrichreimersoftware.materialdrawer.DrawerActivity;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerItem;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerProfile;
import com.heinrichreimersoftware.materialdrawer.theme.DrawerTheme;
import com.rescribe.doctor.R;
import com.rescribe.doctor.helpers.database.AppDBHelper;
import com.rescribe.doctor.helpers.login.LoginHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.chat.StatusInfo;
import com.rescribe.doctor.model.login.ActiveRequest;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.services.MQTTService;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.rescribe.doctor.services.MQTTService.MESSAGE;
import static com.rescribe.doctor.services.MQTTService.SEND_MESSAGE;
import static com.rescribe.doctor.services.MQTTService.STATUS_INFO;
import static com.rescribe.doctor.ui.activities.ChatActivity.CHAT;
import static com.rescribe.doctor.util.RescribeConstants.ACTIVE_STATUS;
import static com.rescribe.doctor.util.RescribeConstants.USER_STATUS.ONLINE;

/**
 * Created by jeetal on 28/6/17.
 */

@RuntimePermissions
public class HomePageActivity extends DrawerActivity implements HelperResponse {

    private static final long MANAGE_ACCOUNT = 121;
    private static final long ADD_ACCOUNT = 122;
    private static final String TAG = "Home";
    private Context mContext;
    private Toolbar toolbar;
    private AppDBHelper appDBHelper;
    private String docId;
    private LoginHelper loginHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = HomePageActivity.this;
        HomePageActivityPermissionsDispatcher.getPermissionWithCheck(HomePageActivity.this);
        appDBHelper = new AppDBHelper(mContext);

        docId = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, mContext);
        loginHelper = new LoginHelper(mContext, HomePageActivity.this);
        ActiveRequest activeRequest = new ActiveRequest();
        activeRequest.setId(Integer.parseInt(docId));
        loginHelper.doActiveStatus(activeRequest);

        drawerConfiguration();
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void getPermission() {
        CommonMethods.Log(TAG, "asked permission");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        HomePageActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onBackPressed() {

        closeDrawer();

        super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        //noinspection SimplifiableIfStatement
        if (id == R.id.notification) {

        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        String mobileNoGmail = "";
        String passwordGmail = "";
        String mobileNoFacebook = "";
        String passwordFacebook = "";
        String gmailLogin = "";
        String facebookLogin = "";

        //Logout functionality
        if (RescribePreferencesManager.getString(RescribeConstants.GMAIL_LOGIN, mContext).equalsIgnoreCase(getString(R.string.login_with_gmail))) {
            gmailLogin = RescribePreferencesManager.getString(RescribeConstants.GMAIL_LOGIN, mContext);
            mobileNoGmail = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.MOBILE_NUMBER_GMAIL, mContext);
            passwordGmail = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.PASSWORD_GMAIL, mContext);

        }
        if (RescribePreferencesManager.getString(RescribeConstants.FACEBOOK_LOGIN, mContext).equalsIgnoreCase(getString(R.string.login_with_facebook))) {
            facebookLogin = RescribePreferencesManager.getString(RescribeConstants.FACEBOOK_LOGIN, mContext);
            mobileNoFacebook = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.MOBILE_NUMBER_FACEBOOK, mContext);
            passwordFacebook = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.PASSWORD_FACEBOOK, mContext);

        }

        RescribePreferencesManager.clearSharedPref(mContext);
        RescribePreferencesManager.putString(RescribeConstants.GMAIL_LOGIN, gmailLogin, mContext);
        RescribePreferencesManager.putString(RescribeConstants.FACEBOOK_LOGIN, facebookLogin, mContext);
        RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.MOBILE_NUMBER_GMAIL, mobileNoGmail, mContext);
        RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.PASSWORD_GMAIL, passwordGmail, mContext);
        RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.MOBILE_NUMBER_FACEBOOK, mobileNoFacebook, mContext);
        RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.PASSWORD_FACEBOOK, passwordFacebook, mContext);
        RescribePreferencesManager.putString(getString(R.string.logout), "" + 1, mContext);

        appDBHelper.deleteDatabase();

        Intent intent = new Intent(mContext, LoginSignUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    private void drawerConfiguration() {
        setDrawerTheme(
                new DrawerTheme(this)
                        .setBackgroundColorRes(R.color.drawer_bg)
                        .setTextColorPrimaryRes(R.color.drawer_menu_text_color)
                        .setTextColorSecondaryRes(R.color.drawer_menu_text_color)
        );

        addItems(
                new DrawerItem()
                        .setTextPrimary(getString(R.string.patient_connect))
                        .setImage(ContextCompat.getDrawable(this, R.drawable.menu_doctor_connect)),
                new DrawerItem()
                        .setTextPrimary(getString(R.string.logout))
                        .setImage(ContextCompat.getDrawable(this, R.drawable.menu_logout))
        );
        setOnItemClickListener(new DrawerItem.OnItemClickListener() {
            @Override
            public void onClick(DrawerItem item, long itemID, int position) {
                //  selectItem(position);

                closeDrawer();
                String id = item.getTextPrimary();
                /*if (id.equalsIgnoreCase(getString(R.string.chat))) {
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    startActivity(intent);
                } else */
                if (id.equalsIgnoreCase(getString(R.string.logout))) {
                    ActiveRequest activeRequest = new ActiveRequest();
                    activeRequest.setId(Integer.parseInt(docId));
                    loginHelper.doLogout(activeRequest);
                } else if (id.equalsIgnoreCase(getString(R.string.patient_connect))) {
                    Intent intent = new Intent(mContext, PatientConnectActivity.class);
                    startActivity(intent);
                }
            }
        });

        // TODO : HARDEDCODED will get remove once done with APIs.
        addProfile(new DrawerProfile()
                .setId(1)
                .setRoundedAvatar((BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.profile))
                .setBackground(ContextCompat.getDrawable(this, R.drawable.group_2))
                .setName("Mr.Avinash Deshpande")
                .setDescription("avinash_deshpande@gmail.com")
        );

        addProfile(new DrawerProfile()
                .setId(2)
                .setRoundedAvatar((BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.profile))
                .setBackground(ContextCompat.getDrawable(this, R.drawable.group_2))
                .setName("Mr.Sandeep Deshmukh ")
                .setDescription("sandeep_deshmukh@gmail.com")
        );

        addProfile(new DrawerProfile()
                .setId(ADD_ACCOUNT)
                .setRoundedAvatar((BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.add_account))
                .setBackground(ContextCompat.getDrawable(this, R.drawable.group_2))
                .setDescription("Add Patient").setProfile(false) // for fixed item set profile false
        );

        addProfile(new DrawerProfile()
                .setId(MANAGE_ACCOUNT)
                .setRoundedAvatar((BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.setting))
                .setBackground(ContextCompat.getDrawable(this, R.drawable.group_2))
                .setDescription("Manage Profile").setProfile(false) // for fixed item set profile false
        );

        setOnNonProfileClickListener(new DrawerProfile.OnNonProfileClickListener() {
            @Override
            public void onProfileItemClick(DrawerProfile profile, long id) {
                if (id == ADD_ACCOUNT) {

                    // Do stuff here

                    addProfile(new DrawerProfile()
                            .setId(3)
                            .setRoundedAvatar((BitmapDrawable) ContextCompat.getDrawable(mContext, R.drawable.profile))
                            .setBackground(ContextCompat.getDrawable(mContext, R.drawable.group_2))
                            .setName("Mr.Ganesh Deshmukh")
                            .setDescription("ganesh_deshmukh@gmail.com")
                    );
//                    CommonMethods.showToast(mContext, "Profile Added");

                } else if (id == MANAGE_ACCOUNT) {
                    // Do stuff here
//                    CommonMethods.showToast(mContext, profile.getDescription());
                }
                closeDrawer();
            }
        });

        setOnProfileSwitchListener(new DrawerProfile.OnProfileSwitchListener() {
            @Override
            public void onSwitch(DrawerProfile oldProfile, long oldId, DrawerProfile newProfile, long newId) {
                // do stuff here
//                CommonMethods.showToast(mContext, "Welcome " + newProfile.getName());
            }
        });
    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        if (mOldDataTag.equals(RescribeConstants.LOGOUT))
            logout();
        else if (mOldDataTag.equals(ACTIVE_STATUS))
            CommonMethods.Log(ACTIVE_STATUS, "active");

    }

    @Override
    public void onParseError(String mOldDataTag, String errorMessage) {

    }

    @Override
    public void onServerError(String mOldDataTag, String serverErrorMessage) {

    }

    @Override
    public void onNoConnectionError(String mOldDataTag, String serverErrorMessage) {

    }
}
