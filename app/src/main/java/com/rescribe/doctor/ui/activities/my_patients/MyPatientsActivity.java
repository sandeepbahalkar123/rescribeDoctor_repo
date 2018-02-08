package com.rescribe.doctor.ui.activities.my_patients;

import android.content.Context;
import android.os.Bundle;
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
import com.rescribe.doctor.model.my_appointments.MyAppointmentsBaseModel;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.ui.fragments.my_appointments.MyAppointmentsFragment;
import com.rescribe.doctor.ui.fragments.my_patients.MyPatientsFragment;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jeetal on 31/1/18.
 */

public class MyPatientsActivity extends AppCompatActivity implements HelperResponse {
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
    private Bundle bundle;
    private MyPatientsFragment mMyPatientsFragment;
    private boolean isLongPressed;

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
        mAppointmentHelper.doGetMyPatients();
    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_GET_PATIENT_DATA)) {

            if (customResponse != null) {
                MyPatientBaseModel myAppointmentsBaseModel = (MyPatientBaseModel) customResponse;
                bundle = new Bundle();
                bundle.putParcelable(RescribeConstants.MYPATIENTS_DATA, myAppointmentsBaseModel);
                mMyPatientsFragment = MyPatientsFragment.newInstance(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.viewContainer, mMyPatientsFragment).commit();
            }

        }
    }

    @Override
    public void onParseError(String mOldDataTag, String errorMessage) {
        CommonMethods.showToast(mContext,errorMessage);

    }

    @Override
    public void onServerError(String mOldDataTag, String serverErrorMessage) {
        CommonMethods.showToast(mContext,serverErrorMessage);
    }

    @Override
    public void onNoConnectionError(String mOldDataTag, String serverErrorMessage) {
        CommonMethods.showToast(mContext,serverErrorMessage);
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
        isLongPressed = mMyPatientsFragment.callOnBackPressed();
        if(isLongPressed){
            mMyPatientsFragment.removeCheckBox();
        }else{
            super.onBackPressed();
        }

    }
}
