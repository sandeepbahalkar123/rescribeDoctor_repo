package com.rescribe.doctor.ui.activities.my_appointments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.Html;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.my_appointments.BottomMenuAppointmentAdapter;
import com.rescribe.doctor.helpers.myappointments.AppointmentHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.my_appointments.MyAppointmentsBaseModel;
import com.rescribe.doctor.model.my_appointments.MyAppointmentsDataModel;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.ui.fragments.my_appointments.MyAppointmentsFragment;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jeetal on 31/1/18.
 */

public class MyAppointmentsActivity extends AppCompatActivity implements HelperResponse {
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
    private MyAppointmentsFragment mMyAppointmentsFragment;
    private AppointmentHelper mAppointmentHelper;
    private Bundle bundle;
    private String month;
    private String year;
    private BottomSheetDialog dialog;
    private RecyclerView recyclerView;
    private BottomMenuAppointmentAdapter mBottomMenuAppointmentAdapter;

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
        mAppointmentHelper = new AppointmentHelper(this, this);
        mAppointmentHelper.doGetAppointmentData();

    }

    private void setDateInToolbar() {
        //Set Date in Required Format i.e 13thJuly'18
        dateTextview.setVisibility(View.VISIBLE);
        String timeToShow = CommonMethods.formatDateTime(CommonMethods.getCurrentDate(), RescribeConstants.DATE_PATTERN.MMM_YY,
                RescribeConstants.DATE_PATTERN.DD_MM_YYYY, RescribeConstants.DATE).toLowerCase();
        String[] timeToShowSpilt = timeToShow.split(",");
        month = timeToShowSpilt[0].substring(0, 1).toUpperCase() + timeToShowSpilt[0].substring(1);
        year = timeToShowSpilt.length == 2 ? timeToShowSpilt[1] : "";
        Date date = CommonMethods.convertStringToDate(CommonMethods.getCurrentDate(), RescribeConstants.DATE_PATTERN.DD_MM_YYYY);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        timeToShow = timeToShow.substring(0, 1).toUpperCase() + timeToShow.substring(1);
        String toDisplay = cal.get(Calendar.DAY_OF_MONTH) + "<sup>" + CommonMethods.getSuffixForNumber(cal.get(Calendar.DAY_OF_MONTH)) + "</sup> " + month + "'" + year;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            dateTextview.setText(Html.fromHtml(toDisplay, Html.FROM_HTML_MODE_LEGACY));
        } else {
            dateTextview.setText(Html.fromHtml(toDisplay));
        }

    }


    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_GET_APPOINTMENT_DATA)) {

            if (customResponse != null) {
                MyAppointmentsBaseModel myAppointmentsBaseModel = (MyAppointmentsBaseModel) customResponse;
                bundle = new Bundle();
                bundle.putParcelable(RescribeConstants.APPOINTMENT_DATA, myAppointmentsBaseModel.getMyAppointmentsDataModel());
                mMyAppointmentsFragment = MyAppointmentsFragment.newInstance(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.viewContainer, mMyAppointmentsFragment).commit();
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

    public void openBottomMenusDialog() {
        View modalbottomsheet = getLayoutInflater().inflate(R.layout.bottom_menu_appointments, null);

        dialog = new BottomSheetDialog(mContext);
        dialog.setContentView(modalbottomsheet);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        recyclerView = (RecyclerView)modalbottomsheet.findViewById(R.id.recyclerView);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setBackgroundColor(ContextCompat.getColor(mContext,R.color.black));
        mBottomMenuAppointmentAdapter = new BottomMenuAppointmentAdapter(mContext);
        LinearLayoutManager linearlayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearlayoutManager);
        // off recyclerView Animation
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator)
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(mBottomMenuAppointmentAdapter);
    }
}
