package com.rescribe.doctor.ui.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.dashboard.WaitingOrAppointmentListAdapter;
import com.rescribe.doctor.bottom_menus.BottomMenu;
import com.rescribe.doctor.bottom_menus.BottomMenuActivity;
import com.rescribe.doctor.helpers.database.AppDBHelper;
import com.rescribe.doctor.helpers.login.LoginHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.login.ActiveRequest;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.ui.activities.dashboard.SettingsActivity;
import com.rescribe.doctor.ui.activities.dashboard.SupportActivity;
import com.rescribe.doctor.ui.activities.my_appointments.MyAppointmentsActivity;
import com.rescribe.doctor.ui.activities.my_patients.MyPatientsActivity;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.ui.customesViews.SwitchButton;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.rescribe.doctor.util.RescribeConstants.ACTIVE_STATUS;

/**
 * Created by jeetal on 28/6/17.
 */

@RuntimePermissions
public class HomePageActivity extends BottomMenuActivity implements HelperResponse {

    private static final long MANAGE_ACCOUNT = 121;
    private static final long ADD_ACCOUNT = 122;
    private static final String TAG = "Home";

    @BindView(R.id.todayWaitingListOrAppointmentCount)
    CustomTextView todayWaitingListOrAppointmentCount;

    @BindView(R.id.todayWaitingListOrAppointmentTextView)
    CustomTextView todayWaitingListOrAppointmentTextView;

    @BindView(R.id.todayNewAppointmentCount)
    CustomTextView todayNewAppointmentCount;

    @BindView(R.id.todayNewAppointmentTextView)
    CustomTextView todayNewAppointmentTextView;

    @BindView(R.id.todayFollowAppointmentCount)
    CustomTextView todayFollowAppointmentCount;

    @BindView(R.id.todayFollowAppointmentTextView)
    CustomTextView todayFollowAppointmentTextView;

    @BindView(R.id.viewPagerDoctorItem)
    LinearLayout viewPagerDoctorItem;

    @BindView(R.id.locationImageView)
    ImageView locationImageView;

    @BindView(R.id.showCount)
    CustomTextView showCount;

    @BindView(R.id.welcomeTextView)
    CustomTextView welcomeTextView;

    @BindView(R.id.doctorNameTextView)
    CustomTextView doctorNameTextView;

    @BindView(R.id.aboutDoctorTextView)
    CustomTextView aboutDoctorTextView;

    @BindView(R.id.doctorInfoLayout)
    LinearLayout doctorInfoLayout;

    @BindView(R.id.dashBoradBgframeLayout)
    FrameLayout dashBoradBgframeLayout;
    @BindView(R.id.nestedScrollView)
    NestedScrollView nestedScrollView;

    ImageView menuImageView;
    CustomTextView appointmentTextView;
    CustomTextView viewTextView;
    RecyclerView recyclerView;

    ImageView menuImageWaitingList;
    CustomTextView menuNameTextView;
    ImageView dashboardArrowImageView;
    SwitchButton radioSwitch;
    @BindView(R.id.hostViewsLayout)
    LinearLayout hostViewsLayout;

    private Context mContext;
    private AppDBHelper appDBHelper;
    private String docId;
    private LoginHelper loginHelper;
    private WaitingOrAppointmentListAdapter mWaitingOrAppointmentListAdapter;
    private LinearLayout menuOptionLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page_layout);
        ButterKnife.bind(this);
        mContext = HomePageActivity.this;
        HomePageActivityPermissionsDispatcher.getPermissionWithCheck(HomePageActivity.this);
        appDBHelper = new AppDBHelper(mContext);
        docId = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, mContext);
        loginHelper = new LoginHelper(mContext, HomePageActivity.this);
        ActiveRequest activeRequest = new ActiveRequest();
        activeRequest.setId(Integer.parseInt(docId));
        loginHelper.doActiveStatus(activeRequest);
        initialize();
        setCurrentActivtyTab(getString(R.string.home));


        //drawerConfiguration();
    }

    private void initialize() {
        //Set values for upper dashboard blocks
        todayFollowAppointmentCount.setText("21");
        todayNewAppointmentCount.setText("21");
        todayWaitingListOrAppointmentCount.setText("21");
        todayFollowAppointmentTextView.setText("Today's Follow Ups");
        todayNewAppointmentTextView.setText("Today's New Patients");
        todayWaitingListOrAppointmentTextView.setText("Today's Appoinments");
        doctorNameTextView.setText("Dr. Rahul Kalyanpur");
        aboutDoctorTextView.setText("MBBS, MD - Medicine, Neurology");
        showCount.setVisibility(View.VISIBLE);
        showCount.setText("8");
        //setWaitingOrAppointmentLayoutHere
        setLayoutForWaitingOrAppointment();
        // inflate waiting list layout
        setLayoutForWaitingList();
        // inflate patientConnect layout
        setLayoutForPatientConnect();
        // inflate MyPatientsActivity layout
        setLayoutForMyPatients();

    }

    private void setLayoutForMyPatients() {
        LayoutInflater inflaterMyPatients = LayoutInflater.from(mContext);
        View inflaterMyPatientsLayout = inflaterMyPatients.inflate(R.layout.dashboard_menu_common_layout, null, false);
        hostViewsLayout.addView(inflaterMyPatientsLayout);
        menuOptionLinearLayout = (LinearLayout) inflaterMyPatientsLayout.findViewById(R.id.menuOptionLinearLayout);
        menuImageWaitingList = (ImageView) inflaterMyPatientsLayout.findViewById(R.id.menuImageView);
        menuNameTextView = (CustomTextView) inflaterMyPatientsLayout.findViewById(R.id.menuNameTextView);
        dashboardArrowImageView = (ImageView) inflaterMyPatientsLayout.findViewById(R.id.dashboardArrowImageView);
        radioSwitch = (SwitchButton) inflaterMyPatientsLayout.findViewById(R.id.radioSwitch);
        menuImageWaitingList.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.mypatients_icon));
        menuNameTextView.setText("My Patients");
        menuOptionLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MyPatientsActivity.class);
                startActivity(intent);
            }
        });
        dashboardArrowImageView.setVisibility(View.VISIBLE);
    }

    private void setLayoutForPatientConnect() {
        LayoutInflater inflaterPatientConnect = LayoutInflater.from(mContext);
        View inflaterPatientConnectLayout = inflaterPatientConnect.inflate(R.layout.dashboard_menu_common_layout, null, false);
        hostViewsLayout.addView(inflaterPatientConnectLayout);
        menuOptionLinearLayout = (LinearLayout) inflaterPatientConnectLayout.findViewById(R.id.menuOptionLinearLayout);
        menuImageWaitingList = (ImageView) inflaterPatientConnectLayout.findViewById(R.id.menuImageView);
        menuNameTextView = (CustomTextView) inflaterPatientConnectLayout.findViewById(R.id.menuNameTextView);
        dashboardArrowImageView = (ImageView) inflaterPatientConnectLayout.findViewById(R.id.dashboardArrowImageView);
        radioSwitch = (SwitchButton) inflaterPatientConnectLayout.findViewById(R.id.radioSwitch);
        menuImageWaitingList.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.patient_connect_icon));
        menuNameTextView.setText("Patient Connect");
        menuOptionLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ConnectSplashActivity.class);
                startActivity(intent);
            }
        });
        radioSwitch.setVisibility(View.VISIBLE);

        setRadioSwitchStatus();
    }

    private void setLayoutForWaitingList() {
        LayoutInflater inflaterWaitingList = LayoutInflater.from(mContext);
        View inflatedLayoutWaitingList = inflaterWaitingList.inflate(R.layout.dashboard_menu_common_layout, null, false);
        hostViewsLayout.addView(inflatedLayoutWaitingList);

        menuImageWaitingList = (ImageView) inflatedLayoutWaitingList.findViewById(R.id.menuImageView);
        menuNameTextView = (CustomTextView) inflatedLayoutWaitingList.findViewById(R.id.menuNameTextView);
        dashboardArrowImageView = (ImageView) inflatedLayoutWaitingList.findViewById(R.id.dashboardArrowImageView);
        radioSwitch = (SwitchButton) inflatedLayoutWaitingList.findViewById(R.id.radioSwitch);
        menuImageWaitingList.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.waitinglist_icon));
        menuNameTextView.setText("Waiting List - 6");
        dashboardArrowImageView.setVisibility(View.VISIBLE);
    }

    private void setLayoutForWaitingOrAppointment() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View inflatedLayout = inflater.inflate(R.layout.waiting_todays_appointment_common_layout, null, false);
        hostViewsLayout.addView(inflatedLayout);
        recyclerView = (RecyclerView) inflatedLayout.findViewById(R.id.recyclerView);
        menuImageView = (ImageView) inflatedLayout.findViewById(R.id.menuImageView);
        appointmentTextView = (CustomTextView) inflatedLayout.findViewById(R.id.appointmentTextView);
        viewTextView = (CustomTextView) inflatedLayout.findViewById(R.id.viewTextView);
        menuImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.today_s_appointments));
        appointmentTextView.setText("Today’s Appointments");
        viewTextView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        viewTextView.setText("VIEW");
        recyclerView.setNestedScrollingEnabled(false);
        mWaitingOrAppointmentListAdapter = new WaitingOrAppointmentListAdapter(mContext);
        viewTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, MyAppointmentsActivity.class);
                startActivity(intent);
            }
        });
        LinearLayoutManager linearlayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearlayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        // off recyclerView Animation
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator)
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(mWaitingOrAppointmentListAdapter);
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

        // closeDrawer();
        final Dialog dialog = new Dialog(mContext);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_exit);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);


        dialog.findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActiveRequest activeRequest = new ActiveRequest();
                RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.IS_EXIT, RescribeConstants.YES, mContext);
                activeRequest.setId(Integer.parseInt(docId));
                loginHelper.doLogout(activeRequest);
                dialog.dismiss();

            }
        });
        dialog.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        dialog.show();

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
/*

//        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {

        }
*/

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


    /*   private void drawerConfiguration() {
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
                   *//*if (id.equalsIgnoreCase(getString(R.string.chat))) {
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    startActivity(intent);
                } else *//*
                if (id.equalsIgnoreCase(getString(R.string.logout))) {
                    ActiveRequest activeRequest = new ActiveRequest();
                    activeRequest.setId(Integer.parseInt(docId));
                    loginHelper.doLogout(activeRequest);
                    RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.IS_EXIT, RescribeConstants.BLANK, mContext);
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
*/
    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        if (mOldDataTag.equals(RescribeConstants.LOGOUT))
            if (RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.IS_EXIT, mContext).equalsIgnoreCase(RescribeConstants.YES)) {
                finish();
            } else if (RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.IS_EXIT, mContext).equalsIgnoreCase(RescribeConstants.NO)) {
                //if user turns on radio button
            } else {
                logout();
            }
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

    @OnClick({R.id.todayNewAppointmentTextView, R.id.todayFollowAppointmentCount, R.id.todayFollowAppointmentTextView, R.id.viewPagerDoctorItem, R.id.locationImageView, R.id.showCount, R.id.welcomeTextView, R.id.doctorNameTextView, R.id.aboutDoctorTextView, R.id.doctorInfoLayout, R.id.dashBoradBgframeLayout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.todayNewAppointmentTextView:
                break;
            case R.id.todayFollowAppointmentCount:
                break;
            case R.id.todayFollowAppointmentTextView:
                break;
            case R.id.viewPagerDoctorItem:
                break;
            case R.id.locationImageView:
                break;
            case R.id.showCount:
                break;
            case R.id.welcomeTextView:
                break;
            case R.id.doctorNameTextView:
                break;
            case R.id.aboutDoctorTextView:
                break;
            case R.id.doctorInfoLayout:
                break;
            case R.id.dashBoradBgframeLayout:
                break;
        }
    }


    private void setRadioSwitchStatus() {

        radioSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                RescribePreferencesManager.putBoolean(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.CHAT_IS_CHECKED, isChecked, mContext);
                ActiveRequest activeRequest = new ActiveRequest();
                activeRequest.setId(Integer.parseInt(docId));

                if (isChecked) {
                    loginHelper.doActiveStatus(activeRequest);
                } else {
                    RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.IS_EXIT, RescribeConstants.NO, mContext);
                    loginHelper.doLogout(activeRequest);
                }
            }
        });


        //Radio Button functionality for chat online offline status

        boolean isExists = RescribePreferencesManager.getSharedPreference(mContext).contains(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.CHAT_IS_CHECKED);
        if (isExists) {
            radioSwitch.setChecked(RescribePreferencesManager.getBoolean(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.CHAT_IS_CHECKED, mContext));
        } else {
            radioSwitch.setChecked(true);
        }
    }

    @Override
    public void onBottomMenuClick(BottomMenu bottomMenu) {

        if (bottomMenu.getMenuName().equalsIgnoreCase(getString(R.string.support))) {
            Intent intent = new Intent(this, SupportActivity.class);
            startActivity(intent);
        } else if (bottomMenu.getMenuName().equalsIgnoreCase(getString(R.string.settings))) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }else if (bottomMenu.getMenuName().equalsIgnoreCase(getString(R.string.profile))) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }
    }
}