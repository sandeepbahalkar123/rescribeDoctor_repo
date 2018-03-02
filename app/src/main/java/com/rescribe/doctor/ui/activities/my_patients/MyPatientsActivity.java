package com.rescribe.doctor.ui.activities.my_patients;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.rescribe.doctor.R;
import com.rescribe.doctor.helpers.doctor_patients.MyPatientBaseModel;
import com.rescribe.doctor.helpers.myappointments.AppointmentHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.request_patients.RequestSearchPatients;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.ui.customesViews.CustomTextView;

import com.rescribe.doctor.ui.fragments.patient.my_patient.DrawerForMyPatients;
import com.rescribe.doctor.ui.fragments.patient.my_patient.MyPatientsFragment;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jeetal on 31/1/18.
 */

public class MyPatientsActivity extends AppCompatActivity implements HelperResponse, DrawerForMyPatients.OnDrawerInteractionListener {
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
    private Context mContext;
    private AppointmentHelper mAppointmentHelper;
    private MyPatientsFragment mMyPatientsFragment;
    private boolean isLongPressed;
    private DrawerForMyPatients mDrawerForMyPatients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_patients_base_layout);
        ButterKnife.bind(this);
        initialize();
    }

    private void initialize() {
        mContext = MyPatientsActivity.this;
        titleTextView.setText(getString(R.string.my_patients));
        mAppointmentHelper = new AppointmentHelper(this, this);
        RequestSearchPatients mRequestSearchPatients = new RequestSearchPatients();
       // mRequestSearchPatients.setDocId(2462);
       mRequestSearchPatients.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, mContext)));
        mAppointmentHelper.doGetMyPatients(mRequestSearchPatients);
        setUpNavigationDrawer();
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
                mDrawerForMyPatients = DrawerForMyPatients.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_view, mDrawerForMyPatients).commit();
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
        }, 100);

    }

    public DrawerLayout getActivityDrawerLayout() {
        return drawerLayout;
    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_GET_PATIENT_DATA)) {

            if (customResponse != null) {
                MyPatientBaseModel myAppointmentsBaseModel = (MyPatientBaseModel) customResponse;
                Bundle bundle = new Bundle();
                bundle.putParcelable(RescribeConstants.MYPATIENTS_DATA, myAppointmentsBaseModel);
                mMyPatientsFragment = MyPatientsFragment.newInstance(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.viewContainer, mMyPatientsFragment).commit();
            }

        }
    }

    @Override
    public void onParseError(String mOldDataTag, String errorMessage) {
        CommonMethods.showToast(mContext, errorMessage);

    }

    @Override
    public void onServerError(String mOldDataTag, String serverErrorMessage) {
        CommonMethods.showToast(mContext, serverErrorMessage);
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
            isLongPressed = mMyPatientsFragment.callOnBackPressed();
            if (isLongPressed) {
                mMyPatientsFragment.removeCheckBox();
            } else {
                super.onBackPressed();
            }

        }
    }

    @Override
    public void onApply(RequestSearchPatients mRequestSearchPatients, boolean drawerRequired) {
        drawerLayout.closeDrawers();
        mRequestSearchPatients.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, mContext)));
        mAppointmentHelper.doGetMyPatients(mRequestSearchPatients);


    }

    @Override
    public void onReset(boolean drawerRequired) {

    }


}
