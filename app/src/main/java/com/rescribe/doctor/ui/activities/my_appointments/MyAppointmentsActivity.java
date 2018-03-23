package com.rescribe.doctor.ui.activities.my_appointments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.philliphsu.bottomsheetpickers.date.DatePickerDialog;
import com.rescribe.doctor.R;
import com.rescribe.doctor.helpers.myappointments.AppointmentHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.Common;
import com.rescribe.doctor.model.my_appointments.AppointmentList;
import com.rescribe.doctor.model.my_appointments.ClinicList;
import com.rescribe.doctor.model.my_appointments.MyAppointmentsBaseModel;
import com.rescribe.doctor.model.my_appointments.MyAppointmentsDataModel;
import com.rescribe.doctor.model.my_appointments.PatientList;
import com.rescribe.doctor.model.my_appointments.StatusList;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.ui.fragments.my_appointments.DrawerForMyAppointment;
import com.rescribe.doctor.ui.fragments.my_appointments.MyAppointmentsFragment;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by jeetal on 31/1/18.
 */
@RuntimePermissions
public class MyAppointmentsActivity extends AppCompatActivity implements HelperResponse, DrawerForMyAppointment.OnDrawerInteractionListener, DatePickerDialog.OnDateSetListener {
    @BindView(R.id.backImageView)
    ImageView backImageView;
    @BindView(R.id.titleTextView)
    CustomTextView titleTextView;
    @BindView(R.id.userInfoTextView)
    CustomTextView userInfoTextView;
    @BindView(R.id.dateTextview)
    CustomTextView dateTextview;
    @BindView(R.id.viewContainer)
    FrameLayout viewContainer;
    @BindView(R.id.nav_view)
    FrameLayout navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.emptyListView)
    RelativeLayout emptyListView;
    private Context mContext;
    private MyAppointmentsFragment mMyAppointmentsFragment;
    private AppointmentHelper mAppointmentHelper;
    private Bundle bundle;
    private String month;
    private String mYear;
    private boolean isLongPressed;
    private DrawerForMyAppointment mDrawerForMyAppointment;
    private Bundle bundleOnApply;
    private ArrayList<StatusList> mStatusLists;
    private MyAppointmentsDataModel myAppointmentsBaseModelObject;
    private MyAppointmentsBaseModel myAppointmentsBaseMainModel;
    private ArrayList<ClinicList> mClinicListsFilter;
    private ArrayList<AppointmentList> mFilterAppointmentList;
    private String phoneNo;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_patients_base_layout);
        ButterKnife.bind(this);
        initialize();
    }

    private void initialize() {
        mContext = MyAppointmentsActivity.this;
        titleTextView.setText(getString(R.string.appointments));
        setDateInToolbar();
        //Call api for AppointmentData
        String date = CommonMethods.getCurrentDate(RescribeConstants.DATE_PATTERN.YYYY_MM_DD);
        mAppointmentHelper = new AppointmentHelper(this, this);
        mAppointmentHelper.doGetAppointmentData(date);


    }

    private void setUpNavigationDrawer() {
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //Called when a drawer's position changes.
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                //Called when a drawer has settled in a completely open state.
                //The drawer is interactive at this point.
                // If you have 2 drawers (left and right) you can distinguish
                // them by using id of the drawerView. int id = drawerView.getId();
                // id will be your layout's id: for example R.id.left_drawer
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                // Called when a drawer has settled in a completely closed state.
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // Called when the drawer motion state changes. The new state will be one of STATE_IDLE, STATE_DRAGGING or STATE_SETTLING.
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mDrawerForMyAppointment = DrawerForMyAppointment.newInstance(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_view, mDrawerForMyAppointment).commit();
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
        }, 100);

    }

    public DrawerLayout getActivityDrawerLayout() {
        return drawerLayout;
    }

    private void setDateInToolbar() {
        //Set Date in Required Format i.e 13thJuly'18
        dateTextview.setVisibility(View.VISIBLE);

        month = CommonMethods.getCurrentDate("MM");
        mYear = CommonMethods.getCurrentDate("yyyy");
        String day = CommonMethods.getCurrentDate("dd");

        String toDisplay = day + "<sup>" + CommonMethods.getSuffixForNumber(Integer.parseInt(day)) + "</sup> " + CommonMethods.getCurrentDate("MMM'' yyyy");

        Spanned dateTodisplay;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            dateTodisplay = Html.fromHtml(toDisplay, Html.FROM_HTML_MODE_LEGACY);
        else
            dateTodisplay = Html.fromHtml(toDisplay);

        dateTextview.setText(dateTodisplay);

    }


    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_GET_APPOINTMENT_DATA)) {

            if (customResponse != null) {
                myAppointmentsBaseMainModel = (MyAppointmentsBaseModel) customResponse;
                bundle = new Bundle();

                bundle.putParcelable(RescribeConstants.APPOINTMENT_DATA, myAppointmentsBaseMainModel.getMyAppointmentsDataModel());
                bundle.putBoolean(RescribeConstants.IS_BOOK_AND_CONFIRM_REQUIRED, true);
                mMyAppointmentsFragment = MyAppointmentsFragment.newInstance(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.viewContainer, mMyAppointmentsFragment).commit();
                setUpNavigationDrawer();

            }

        }
    }

    @Override
    public void onParseError(String mOldDataTag, String errorMessage) {

        CommonMethods.showToast(mContext, errorMessage);
        emptyListView.setVisibility(View.VISIBLE);

    }

    @Override
    public void onServerError(String mOldDataTag, String serverErrorMessage) {
        CommonMethods.showToast(mContext, serverErrorMessage);
        emptyListView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNoConnectionError(String mOldDataTag, String serverErrorMessage) {
        CommonMethods.showToast(mContext, serverErrorMessage);

    }

    @OnClick({R.id.backImageView, R.id.userInfoTextView, R.id.dateTextview, R.id.viewContainer, R.id.nav_view, R.id.drawer_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backImageView:
                onBackPressed();
                break;
            case R.id.userInfoTextView:
                break;
            case R.id.dateTextview:
                Calendar now = Calendar.getInstance();
// As of version 2.3.0, `BottomSheetDatePickerDialog` is deprecated.
                datePickerDialog = DatePickerDialog.newInstance(
                        this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.setAccentColor(getResources().getColor(R.color.tagColor));
                datePickerDialog.setMinDate(Calendar.getInstance());
                datePickerDialog.show(getSupportFragmentManager(), getResources().getString(R.string.select_date_text));

                break;
            case R.id.viewContainer:
                break;
            case R.id.nav_view:
                break;
            case R.id.drawer_layout:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            if (mMyAppointmentsFragment != null)
                isLongPressed = mMyAppointmentsFragment.callOnBackPressed();
            if (isLongPressed) {
                mMyAppointmentsFragment.removeCheckBox();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onApply(Bundle b, boolean drawerRequired) {
        drawerLayout.closeDrawers();
        bundleOnApply = new Bundle();

        mClinicListsFilter = b.getParcelableArrayList(RescribeConstants.FILTER_CLINIC_LIST);
        mStatusLists = b.getParcelableArrayList(RescribeConstants.FILTER_STATUS_LIST);
        myAppointmentsBaseModelObject = b.getParcelable(RescribeConstants.APPOINTMENT_DATA);
        ArrayList<AppointmentList> mAppointmentLists = new ArrayList<>();
        MyAppointmentsDataModel myAppointmentsDataModel = new MyAppointmentsDataModel();
        ArrayList<AppointmentList> appointmentLists = myAppointmentsBaseMainModel.getMyAppointmentsDataModel().getAppointmentList();
        if (!mStatusLists.isEmpty() && mClinicListsFilter.isEmpty()) {
            for (StatusList statusName : mStatusLists) {
                for (AppointmentList appointmentObject : appointmentLists) {
                    ArrayList<PatientList> mPatientListArrayList = new ArrayList<>();
                    AppointmentList tempAppointmentListObject = null;
                    try {
                        tempAppointmentListObject = (AppointmentList) appointmentObject.clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    for (PatientList patientList : appointmentObject.getPatientList()) {
                        if (statusName.getStatusName().equalsIgnoreCase(patientList.getAppointmentStatus())) {
                            mPatientListArrayList.add(patientList);
                            tempAppointmentListObject.setPatientList(mPatientListArrayList);
                        }
                    }
                    if (!mPatientListArrayList.isEmpty()) {
                        mAppointmentLists.add(tempAppointmentListObject);
                    }
                }

            }
            myAppointmentsDataModel.setAppointmentList(mAppointmentLists);
            myAppointmentsDataModel.setClinicList(myAppointmentsBaseMainModel.getMyAppointmentsDataModel().getClinicList());
            myAppointmentsDataModel.setStatusList(myAppointmentsBaseMainModel.getMyAppointmentsDataModel().getStatusList());
            bundleOnApply.putParcelable(RescribeConstants.APPOINTMENT_DATA, myAppointmentsDataModel);
            bundleOnApply.putBoolean(RescribeConstants.IS_BOOK_AND_CONFIRM_REQUIRED, false);
            mMyAppointmentsFragment = MyAppointmentsFragment.newInstance(bundleOnApply);
            getSupportFragmentManager().beginTransaction().replace(R.id.viewContainer, mMyAppointmentsFragment).commit();
            setUpNavigationDrawer();
        } else if (mStatusLists.isEmpty() && !mClinicListsFilter.isEmpty()) {

            mFilterAppointmentList = new ArrayList<>();
            for (ClinicList clinicList : mClinicListsFilter) {
                for (AppointmentList appointmentObject : appointmentLists) {
                    AppointmentList tempAppointmentListObject = null;
                    try {
                        tempAppointmentListObject = (AppointmentList) appointmentObject.clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    if (clinicList.getLocationId() == tempAppointmentListObject.getLocationId()) {
                        mFilterAppointmentList.add(tempAppointmentListObject);
                    }
                }
            }
            myAppointmentsDataModel.setAppointmentList(mFilterAppointmentList);
            myAppointmentsDataModel.setClinicList(myAppointmentsBaseMainModel.getMyAppointmentsDataModel().getClinicList());
            myAppointmentsDataModel.setStatusList(myAppointmentsBaseMainModel.getMyAppointmentsDataModel().getStatusList());
            bundleOnApply.putParcelable(RescribeConstants.APPOINTMENT_DATA, myAppointmentsDataModel);
            bundleOnApply.putBoolean(RescribeConstants.IS_BOOK_AND_CONFIRM_REQUIRED, false);
            mMyAppointmentsFragment = MyAppointmentsFragment.newInstance(bundleOnApply);
            getSupportFragmentManager().beginTransaction().replace(R.id.viewContainer, mMyAppointmentsFragment).commit();
            setUpNavigationDrawer();
        } else if (!mStatusLists.isEmpty() && !mClinicListsFilter.isEmpty()) {
            for (StatusList statusName : mStatusLists) {
                for (AppointmentList appointmentObject : appointmentLists) {
                    ArrayList<PatientList> mPatientListArrayList = new ArrayList<>();
                    AppointmentList tempAppointmentListObject = null;
                    try {
                        tempAppointmentListObject = (AppointmentList) appointmentObject.clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    for (PatientList patientList : appointmentObject.getPatientList()) {
                        if (statusName.getStatusName().equalsIgnoreCase(patientList.getAppointmentStatus())) {
                            mPatientListArrayList.add(patientList);
                            tempAppointmentListObject.setPatientList(mPatientListArrayList);
                        }
                    }
                    if (!mPatientListArrayList.isEmpty()) {
                        mAppointmentLists.add(tempAppointmentListObject);
                    }
                }

            }
            mFilterAppointmentList = new ArrayList<>();
            for (ClinicList clinicList : mClinicListsFilter) {
                for (AppointmentList appointmentObject : mAppointmentLists) {
                    AppointmentList tempAppointmentListObject = null;
                    try {
                        tempAppointmentListObject = (AppointmentList) appointmentObject.clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    if (clinicList.getLocationId() == appointmentObject.getLocationId()) {
                        mFilterAppointmentList.add(tempAppointmentListObject);
                    }
                }
            }
            myAppointmentsDataModel.setAppointmentList(mFilterAppointmentList);
            myAppointmentsDataModel.setClinicList(myAppointmentsBaseMainModel.getMyAppointmentsDataModel().getClinicList());
            myAppointmentsDataModel.setStatusList(myAppointmentsBaseMainModel.getMyAppointmentsDataModel().getStatusList());
            bundleOnApply.putParcelable(RescribeConstants.APPOINTMENT_DATA, myAppointmentsDataModel);
            bundleOnApply.putBoolean(RescribeConstants.IS_BOOK_AND_CONFIRM_REQUIRED, false);
            mMyAppointmentsFragment = MyAppointmentsFragment.newInstance(bundleOnApply);
            getSupportFragmentManager().beginTransaction().replace(R.id.viewContainer, mMyAppointmentsFragment).commit();
            setUpNavigationDrawer();
        } else {
            myAppointmentsDataModel.setAppointmentList(myAppointmentsBaseMainModel.getMyAppointmentsDataModel().getAppointmentList());
            myAppointmentsDataModel.setClinicList(myAppointmentsBaseMainModel.getMyAppointmentsDataModel().getClinicList());
            myAppointmentsDataModel.setStatusList(myAppointmentsBaseMainModel.getMyAppointmentsDataModel().getStatusList());
            bundleOnApply.putParcelable(RescribeConstants.APPOINTMENT_DATA, myAppointmentsDataModel);
            bundleOnApply.putBoolean(RescribeConstants.IS_BOOK_AND_CONFIRM_REQUIRED, true);
            mMyAppointmentsFragment = MyAppointmentsFragment.newInstance(bundleOnApply);
            getSupportFragmentManager().beginTransaction().replace(R.id.viewContainer, mMyAppointmentsFragment).commit();
            setUpNavigationDrawer();
        }


    }

    @Override
    public void onReset(boolean drawerRequired) {

    }

    public void callPatient(String patientPhone) {
        phoneNo = patientPhone;
        MyAppointmentsActivityPermissionsDispatcher.doCallSupportWithCheck(this);
    }

    @NeedsPermission(Manifest.permission.CALL_PHONE)
    void doCallSupport() {
        callSupport(phoneNo);
    }

    private void callSupport(String phoneNo) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNo));
        startActivity(callIntent);
    }


    public void onRequestPermssionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MyAppointmentsActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        int monthOfYearToShow = monthOfYear + 1;

        dateTextview.setVisibility(View.VISIBLE);
        String timeToShow = CommonMethods.formatDateTime(dayOfMonth + "-" + monthOfYearToShow + "-" + year, RescribeConstants.DATE_PATTERN.MMM_YY,
                RescribeConstants.DATE_PATTERN.DD_MM_YYYY, RescribeConstants.DATE).toLowerCase();
        String[] timeToShowSpilt = timeToShow.split(",");
        month = timeToShowSpilt[0].substring(0, 1).toUpperCase() + timeToShowSpilt[0].substring(1);
        mYear = timeToShowSpilt.length == 2 ? timeToShowSpilt[1] : "";
        Date date = CommonMethods.convertStringToDate(dayOfMonth + "-" + monthOfYearToShow + "-" + year, RescribeConstants.DATE_PATTERN.DD_MM_YYYY);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        String toDisplay = cal.get(Calendar.DAY_OF_MONTH) + "<sup>" + CommonMethods.getSuffixForNumber(cal.get(Calendar.DAY_OF_MONTH)) + "</sup> " + month + "' " + year;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            dateTextview.setText(Html.fromHtml(toDisplay, Html.FROM_HTML_MODE_LEGACY));
         else
            dateTextview.setText(Html.fromHtml(toDisplay));

        mAppointmentHelper = new AppointmentHelper(this, this);
        mAppointmentHelper.doGetAppointmentData(year + "-" + monthOfYearToShow + "-" + dayOfMonth);
    }
}
