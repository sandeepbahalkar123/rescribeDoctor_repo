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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.dashboard.DashBoardAppointmentListAdapter;
import com.rescribe.doctor.adapters.dashboard.DashBoardWaitingList;
import com.rescribe.doctor.bottom_menus.BottomMenu;
import com.rescribe.doctor.bottom_menus.BottomMenuActivity;
import com.rescribe.doctor.helpers.dashboard.DashboardHelper;
import com.rescribe.doctor.helpers.database.AppDBHelper;
import com.rescribe.doctor.helpers.login.LoginHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.dashboard.DashboardBaseModel;
import com.rescribe.doctor.model.dashboard.DashboardDetails;
import com.rescribe.doctor.model.doctor_location.DoctorLocationBaseModel;
import com.rescribe.doctor.model.login.ActiveRequest;
import com.rescribe.doctor.model.my_appointments.AppointmentList;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.singleton.RescribeApplication;
import com.rescribe.doctor.ui.activities.completed_opd.CompletedOpdActivity;
import com.rescribe.doctor.ui.activities.dashboard.SettingsActivity;
import com.rescribe.doctor.ui.activities.dashboard.SupportActivity;
import com.rescribe.doctor.ui.activities.my_appointments.MyAppointmentsActivity;
import com.rescribe.doctor.ui.activities.my_patients.MyPatientsActivity;
import com.rescribe.doctor.ui.activities.waiting_list.WaitingMainListActivity;
import com.rescribe.doctor.ui.customesViews.CircularImageView;
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

    @BindView(R.id.welcomeTextView)
    CustomTextView welcomeTextView;

    @BindView(R.id.doctorNameTextView)
    CustomTextView doctorNameTextView;

    @BindView(R.id.aboutDoctorTextView)
    CustomTextView aboutDoctorTextView;

    @BindView(R.id.doctorInfoLayout)
    LinearLayout doctorInfoLayout;

    @BindView(R.id.dashBoradBgframeLayout)
    RelativeLayout dashBoradBgframeLayout;
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
    @BindView(R.id.doctorDashboardImage)
    CircularImageView doctorDashboardImage;
    @BindView(R.id.todayCompletedOpd)
    RelativeLayout todayCompletedOpd;
    @BindView(R.id.todayAppointmentsOrWaitingList)
    RelativeLayout todayAppointmentsOrWaitingList;
    @BindView(R.id.todayNewPatient)
    RelativeLayout todayNewPatient;
    private Context mContext;
    private AppDBHelper appDBHelper;
    private String docId;
    private LoginHelper loginHelper;
    private DashBoardAppointmentListAdapter mDashBoardAppointmentListAdapter;
    private LinearLayout menuOptionLinearLayout;
    private DashboardHelper mDashboardHelper;
    private DashboardDetails mDashboardDetails;
    private DashBoardWaitingList mDashBoardWaitingList;
    private String doctorNameToDisplay;
    private String mDoctorName;
    private ColorGenerator mColorGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page_layout);
        ButterKnife.bind(this);
        mContext = HomePageActivity.this;
        mColorGenerator = ColorGenerator.MATERIAL;
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

        mDashboardHelper = new DashboardHelper(this, this);
        mDashboardHelper.doDoctorGetLocationList();
        if (RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.USER_NAME, mContext).toLowerCase().contains("Dr.")) {
            doctorNameToDisplay = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.USER_NAME, mContext);
        } else {
            doctorNameToDisplay = "Dr. " + RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.USER_NAME, mContext);

        }
        doctorNameTextView.setText(doctorNameToDisplay);
        aboutDoctorTextView.setText(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_DEGREE, mContext));
        setUpImage();

        //setWaitingOrAppointmentLayoutHere

    }

    @Override
    protected void onResume() {
        mDashboardHelper.doGetDashboardResponse();
        super.onResume();
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
        menuImageWaitingList.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.patient));
        menuNameTextView.setText("My Patients");
        menuOptionLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MyPatientsActivity.class);
                intent.putExtra(RescribeConstants.ACTIVITY_LAUNCHED_FROM, RescribeConstants.HOME_PAGE);
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
        menuImageWaitingList.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.patientconnect));
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

    private void setLayoutForWaitingList(String waitingListCount) {
        LayoutInflater inflaterWaitingList = LayoutInflater.from(mContext);
        View inflatedLayoutWaitingList = inflaterWaitingList.inflate(R.layout.dashboard_menu_common_layout, null, false);
        hostViewsLayout.addView(inflatedLayoutWaitingList);
        menuOptionLinearLayout = (LinearLayout) inflatedLayoutWaitingList.findViewById(R.id.menuOptionLinearLayout);
        menuImageWaitingList = (ImageView) inflatedLayoutWaitingList.findViewById(R.id.menuImageView);
        menuNameTextView = (CustomTextView) inflatedLayoutWaitingList.findViewById(R.id.menuNameTextView);
        dashboardArrowImageView = (ImageView) inflatedLayoutWaitingList.findViewById(R.id.dashboardArrowImageView);
        radioSwitch = (SwitchButton) inflatedLayoutWaitingList.findViewById(R.id.radioSwitch);
        menuImageWaitingList.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.patientwaitinglist));
        menuNameTextView.setText("Waiting List - " + waitingListCount);
        dashboardArrowImageView.setVisibility(View.VISIBLE);
        menuOptionLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, WaitingMainListActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setLayoutForAppointment(boolean isRecyclerViewRequired) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View inflatedLayout = inflater.inflate(R.layout.waiting_todays_appointment_common_layout, null, false);
        hostViewsLayout.addView(inflatedLayout);
        recyclerView = (RecyclerView) inflatedLayout.findViewById(R.id.recyclerView);
        menuImageView = (ImageView) inflatedLayout.findViewById(R.id.menuImageView);
        appointmentTextView = (CustomTextView) inflatedLayout.findViewById(R.id.appointmentTextView);
        viewTextView = (CustomTextView) inflatedLayout.findViewById(R.id.viewTextView);
        menuImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.appointment));
        appointmentTextView.setText("Today's Appointments");
        viewTextView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        viewTextView.setText("VIEW");
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager linearlayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearlayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        // off recyclerView Animation
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator)
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        if (isRecyclerViewRequired) {
            mDashBoardAppointmentListAdapter = new DashBoardAppointmentListAdapter(mContext, mDashboardDetails.getDashboardAppointmentClinicList().getAppointmentClinicList());
            recyclerView.setAdapter(mDashBoardAppointmentListAdapter);
        } else {
            CommonMethods.Log(TAG, "Dont show recyclerView");
        }
        viewTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, MyAppointmentsActivity.class);
                startActivity(intent);
            }
        });


    }

    private void setLayoutForWaitingListIfAppointmentListEmpty() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View inflatedLayout = inflater.inflate(R.layout.waiting_todays_appointment_common_layout, null, false);
        hostViewsLayout.addView(inflatedLayout);
        recyclerView = (RecyclerView) inflatedLayout.findViewById(R.id.recyclerView);
        menuImageView = (ImageView) inflatedLayout.findViewById(R.id.menuImageView);
        appointmentTextView = (CustomTextView) inflatedLayout.findViewById(R.id.appointmentTextView);
        viewTextView = (CustomTextView) inflatedLayout.findViewById(R.id.viewTextView);
        menuImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.appointment));
        appointmentTextView.setText("Waiting List");
        viewTextView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        viewTextView.setText("VIEW");
        recyclerView.setNestedScrollingEnabled(false);
        mDashBoardWaitingList = new DashBoardWaitingList(mContext, mDashboardDetails.getDashboardWaitingList().getWaitingClinicList());
        viewTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, WaitingMainListActivity.class);
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
        recyclerView.setAdapter(mDashBoardWaitingList);
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
        else if (mOldDataTag.equals(ACTIVE_STATUS)) {
            CommonMethods.Log(ACTIVE_STATUS, "active");
        } else if (mOldDataTag.equals(RescribeConstants.TASK_GET_LOCATION_LIST)) {
            DoctorLocationBaseModel doctorLocationBaseModel = (DoctorLocationBaseModel) customResponse;
            RescribeApplication.setDoctorLocationModels(doctorLocationBaseModel.getDoctorLocationModel());
        } else if (mOldDataTag.equals(RescribeConstants.TASK_GET_DASHBOARD_RESPONSE)) {
            DashboardBaseModel mDashboardBaseModel = (DashboardBaseModel) customResponse;
            if (mDashboardBaseModel.getCommon().isSuccess()) {
                mDashboardDetails = new DashboardDetails();
                mDashboardDetails = mDashboardBaseModel.getDashboarddataModel().getDashboardDetails();
                if (mDashboardDetails.getDashboardAppointmentClinicList().getAppointmentClinicList().size() > 0) {
                    todayFollowAppointmentCount.setText(mDashboardDetails.getDashboardAppointmentClinicList().getTodayFollowUpCount() + "");
                    todayNewAppointmentCount.setText(mDashboardDetails.getDashboardAppointmentClinicList().getTodayNewPatientCount() + "");
                    todayWaitingListOrAppointmentCount.setText(mDashboardDetails.getDashboardAppointmentClinicList().getTodayAppointmentCount() + "");
                    todayFollowAppointmentTextView.setText(getString(R.string.today_completed_opd));
                    todayNewAppointmentTextView.setText(getString(R.string.today_new_patient));
                    todayWaitingListOrAppointmentTextView.setText(getString(R.string.today_appointment));
                    hostViewsLayout.removeAllViews();

                    setLayoutForAppointment(true);
                    // inflate waiting list layout
                    setLayoutForWaitingList(mDashboardDetails.getDashboardAppointmentClinicList().getWaitingListCount() + "");
                    // inflate patientConnect layout
                    setLayoutForPatientConnect();
                    // inflate MyPatientsActivity layout
                    setLayoutForMyPatients();


                } else if (mDashboardDetails.getDashboardWaitingList().getWaitingClinicList().size() > 0) {
                    todayFollowAppointmentCount.setText(mDashboardDetails.getDashboardWaitingList().getTodayFollowUpCount() + "");
                    todayNewAppointmentCount.setText(mDashboardDetails.getDashboardWaitingList().getTodayNewPatientCount() + "");
                    todayWaitingListOrAppointmentCount.setText(mDashboardDetails.getDashboardWaitingList().getTodayWaitingCount() + "");
                    todayFollowAppointmentTextView.setText(getString(R.string.today_completed_opd));
                    todayNewAppointmentTextView.setText(getString(R.string.today_new_patient));
                    todayWaitingListOrAppointmentTextView.setText(getString(R.string.today_waiting_list));

                    hostViewsLayout.removeAllViews();
                    setLayoutForWaitingListIfAppointmentListEmpty();
                    // inflate patientConnect layout
                    setLayoutForPatientConnect();
                    // inflate MyPatientsActivity layout
                    setLayoutForMyPatients();


                } else {
                    todayFollowAppointmentCount.setText("0");
                    todayNewAppointmentCount.setText("0");
                    todayWaitingListOrAppointmentCount.setText("0");
                    todayFollowAppointmentTextView.setText(getString(R.string.today_completed_opd));
                    todayNewAppointmentTextView.setText(getString(R.string.today_new_patient));
                    todayWaitingListOrAppointmentTextView.setText(getString(R.string.today_appointment));


                    setLayoutForAppointment(false);
                    // inflate waiting list layout
                    setLayoutForWaitingList("0");
                    // inflate patientConnect layout
                    setLayoutForPatientConnect();
                    // inflate MyPatientsActivity layout
                    setLayoutForMyPatients();
                }
            }
        }

    }

    private void setUpImage() {
        if (RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.PROFILE_PHOTO, mContext) != null) {

            mDoctorName = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.USER_NAME, mContext);
            if (mDoctorName.contains("Dr. ")) {
                mDoctorName = mDoctorName.replace("Dr. ", "");
            }
            int color2 = mColorGenerator.getColor(mDoctorName);
            TextDrawable drawable = TextDrawable.builder()
                    .beginConfig()
                    .width(Math.round(getResources().getDimension(R.dimen.dp40))) // width in px
                    .height(Math.round(getResources().getDimension(R.dimen.dp40))) // height in px
                    .endConfig()
                    .buildRound(("" + mDoctorName.charAt(0)).toUpperCase(), color2);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.dontAnimate();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
            requestOptions.skipMemoryCache(true);
            requestOptions.placeholder(drawable);
            requestOptions.error(drawable);

            Glide.with(mContext)
                    .load(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.PROFILE_PHOTO, mContext))
                    .apply(requestOptions).thumbnail(0.5f)
                    .into(doctorDashboardImage);

        }


    }

    @Override
    public void onParseError(String mOldDataTag, String errorMessage) {
        hostViewsLayout.removeAllViews();
        todayFollowAppointmentCount.setText("0");
        todayNewAppointmentCount.setText("0");
        todayWaitingListOrAppointmentCount.setText("0");
        todayFollowAppointmentTextView.setText(getString(R.string.today_completed_opd));
        todayNewAppointmentTextView.setText(getString(R.string.today_new_patient));
        todayWaitingListOrAppointmentTextView.setText(getString(R.string.today_appointment));

        Toast.makeText(mContext, errorMessage + "", Toast.LENGTH_SHORT).show();
        setLayoutForAppointment(false);
        setLayoutForWaitingList("0");
        setLayoutForPatientConnect();
        setLayoutForMyPatients();

    }

    @Override
    public void onServerError(String mOldDataTag, String serverErrorMessage) {
        hostViewsLayout.removeAllViews();
        todayFollowAppointmentCount.setText("0");
        todayNewAppointmentCount.setText("0");
        todayWaitingListOrAppointmentCount.setText("0");
        todayFollowAppointmentTextView.setText(getString(R.string.today_completed_opd));
        todayNewAppointmentTextView.setText(getString(R.string.today_new_patient));
        todayWaitingListOrAppointmentTextView.setText(getString(R.string.today_appointment));

        Toast.makeText(mContext, serverErrorMessage + "", Toast.LENGTH_SHORT).show();
        setLayoutForAppointment(false);
        setLayoutForWaitingList("0");
        setLayoutForPatientConnect();
        setLayoutForMyPatients();
    }

    @Override
    public void onNoConnectionError(String mOldDataTag, String serverErrorMessage) {
        Toast.makeText(mContext, serverErrorMessage + "", Toast.LENGTH_SHORT).show();

    }

    @OnClick({R.id.todayCompletedOpd, R.id.viewPagerDoctorItem, R.id.todayAppointmentsOrWaitingList, R.id.todayNewPatient})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.todayCompletedOpd:
                Intent intent = new Intent(this, CompletedOpdActivity.class);
                startActivity(intent);
                break;
            case R.id.viewPagerDoctorItem:
                break;
            case R.id.todayAppointmentsOrWaitingList:
                if (todayWaitingListOrAppointmentTextView.getText().toString().equals(getString(R.string.today_appointment))) {
                    Intent todayAppointmentsOrWaitingList = new Intent(this, MyAppointmentsActivity.class);
                    startActivity(todayAppointmentsOrWaitingList);
                } else {
                    Intent todayAppointmentsOrWaitingList = new Intent(this, WaitingMainListActivity.class);
                    startActivity(todayAppointmentsOrWaitingList);
                }
                break;
            case R.id.todayNewPatient:
                /*Intent todayNewPatient = new Intent(this, CompletedOpdActivity.class);
                startActivity(todayNewPatient);*/
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
        } else if (bottomMenu.getMenuName().equalsIgnoreCase(getString(R.string.profile))) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }
    }
}