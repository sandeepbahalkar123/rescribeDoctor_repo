package com.rescribe.doctor.ui.activities.my_patients;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.rescribe.doctor.R;
import com.rescribe.doctor.model.request_patients.RequestSearchPatients;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.ui.fragments.patient.my_patient.DrawerForMyPatients;
import com.rescribe.doctor.ui.fragments.patient.patient_connect.ChatPatientListFragment;
import com.rescribe.doctor.util.RescribeConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jeetal on 5/3/18.
 */

public class ShowMyPatientsListActivity extends AppCompatActivity implements DrawerForMyPatients.OnDrawerInteractionListener {

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
    Intent mIntent;
    private String mActivityCalledFrom = "";
    private ChatPatientListFragment mMyPatientsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_patients_base_layout);
        ButterKnife.bind(this);
        initialize();
    }

    private void initialize() {

        // Load Drawer Fragment
        DrawerForMyPatients mDrawerForMyPatients = DrawerForMyPatients.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_view, mDrawerForMyPatients).commit();
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);

        mIntent = getIntent();
        if (mIntent.getExtras() != null) {
            mActivityCalledFrom = mIntent.getStringExtra(RescribeConstants.ACTIVITY_LAUNCHED_FROM);
        }
        mContext = ShowMyPatientsListActivity.this;
        titleTextView.setText(getString(R.string.my_patients));

        // Load Patient Fragment
        Bundle bundle = new Bundle();
        bundle.putString(RescribeConstants.ACTIVITY_LAUNCHED_FROM, mActivityCalledFrom);
        mMyPatientsFragment = ChatPatientListFragment.newInstance(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.viewContainer, mMyPatientsFragment).commit();
    }

    public void openDrawer() {
        if (drawerLayout != null)
            drawerLayout.openDrawer(GravityCompat.END);
    }

    @OnClick({R.id.backImageView})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backImageView:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onApply(RequestSearchPatients mRequestSearchPatients, boolean isReset) {
        mRequestSearchPatients.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, mContext)));
        mMyPatientsFragment.apply(mRequestSearchPatients, isReset);
        if (!isReset) {
            drawerLayout.closeDrawers();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            finish();
        }
    }
}
