package com.rescribe.doctor.ui.activities.completed_opd;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.rescribe.doctor.R;
import com.rescribe.doctor.helpers.myappointments.AppointmentHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.completed_opd.CompletedOpdBaseModel;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.ui.fragments.completed_opd.CompletedOpdFragment;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;
import java.util.HashSet;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by jeetal on 17/3/18.
 */
@RuntimePermissions
public class CompletedOpdActivity extends AppCompatActivity implements HelperResponse {
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
    private AppointmentHelper mAppointmentHelper;
    private CompletedOpdFragment mCompletedOpdfragment;
    private boolean isLongPressed;
    Intent mIntent;
    private String mActivityCalledFrom = "";
    private boolean isFromDrawer;
    public HashSet<Integer> selectedDoctorId = new HashSet<>();
    private String phoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_patients_base_layout);
        ButterKnife.bind(this);
        initialize();
    }

    private void initialize() {
        mIntent = getIntent();
        if (mIntent.getExtras() != null) {
            mActivityCalledFrom = mIntent.getStringExtra(RescribeConstants.ACTIVITY_LAUNCHED_FROM);
        }
        mContext = CompletedOpdActivity.this;
        titleTextView.setText(getString(R.string.completed_opd));
        mAppointmentHelper = new AppointmentHelper(this, this);
        mAppointmentHelper.doGetCompletedOpdList();
      //  setUpNavigationDrawer();
    }



    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_GET_COMPLETED_OPD)) {

            if (customResponse != null) {
                CompletedOpdBaseModel mCompletedOpdBaseModel = (CompletedOpdBaseModel) customResponse;
                Bundle bundle = new Bundle();
                bundle.putParcelable(RescribeConstants.MYPATIENTS_DATA, mCompletedOpdBaseModel);
                mCompletedOpdfragment = CompletedOpdFragment.newInstance(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.viewContainer, mCompletedOpdfragment).commit();
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
            if (mCompletedOpdfragment != null)
                isLongPressed = mCompletedOpdfragment.callOnBackPressed();
            if (isLongPressed) {
                mCompletedOpdfragment.removeCheckBox();
            } else {
                super.onBackPressed();
            }

        }
    }




    public void callPatient(String patientPhone) {
        phoneNo = patientPhone;
        CompletedOpdActivityPermissionsDispatcher.doCallSupportWithCheck(this);
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
        CompletedOpdActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
