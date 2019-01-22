package com.rescribe.doctor.ui.activities.waiting_list;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;

import com.rescribe.doctor.R;
import com.rescribe.doctor.helpers.myappointments.AppointmentHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.waiting_list.Active;
import com.rescribe.doctor.model.waiting_list.ViewAll;
import com.rescribe.doctor.model.waiting_list.WaitingListBaseModel;
import com.rescribe.doctor.model.waiting_list.WaitingPatientList;
import com.rescribe.doctor.model.waiting_list.WaitingclinicList;
import com.rescribe.doctor.ui.activities.my_patients.MyPatientsActivity;
import com.rescribe.doctor.ui.activities.my_patients.add_new_patient.AddNewPatientWebViewActivity;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.ui.fragments.waiting_list.ActivePatientListFragment;
import com.rescribe.doctor.ui.fragments.waiting_list.ViewAllPatientListFragment;
import com.rescribe.doctor.util.NetworkUtil;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.rescribe.doctor.util.RescribeConstants.LOCATION_ID;

/**
 * Created by jeetal on 22/2/18.
 */

public class WaitingMainListActivity extends AppCompatActivity implements HelperResponse {

    public static final int RESULT_CLOSE_ACTIVITY_WAITING_LIST = 404;
    @BindView(R.id.backImageView)
    ImageView backImageView;
    @BindView(R.id.titleTextView)
    CustomTextView titleTextView;
    @BindView(R.id.userInfoTextView)
    CustomTextView userInfoTextView;
    @BindView(R.id.dateTextview)
    CustomTextView dateTextview;
    @BindView(R.id.year)
    Spinner year;
    @BindView(R.id.addImageView)
    ImageView addImageView;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    String[] mFragmentTitleList = new String[2];
    @BindView(R.id.leftFab)
    FloatingActionButton leftFab;
    @BindView(R.id.addNewPatientFAB)
    FloatingActionButton mAddNewPatientFAB;
    public ArrayList<WaitingclinicList> mWaitingClinicList;
    public int appointmentFormat;
    private AppointmentHelper mAppointmentHelper;
    private ViewPagerAdapter mViewPagerAdapter;
    public int receivedLocationID = -1;
    private ViewAllPatientListFragment viewAllPatientListFragment;
    private ActivePatientListFragment activePatientListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting_base_layout);
        ButterKnife.bind(this);
        mFragmentTitleList[0] = getString(R.string.active);
        mFragmentTitleList[1] = getString(R.string.view_all);
        mAppointmentHelper = new AppointmentHelper(this, this);

        String tempLocID = getIntent().getStringExtra(LOCATION_ID);
        receivedLocationID = tempLocID != null ? Integer.parseInt(tempLocID) : -1;


        setupViewPager(viewpager);
        tabs.setupWithViewPager(viewpager);
    }

    private void setupViewPager(ViewPager viewPager) {
        titleTextView.setText(getString(R.string.waiting_list));
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        activePatientListFragment = ActivePatientListFragment.newInstance();
        viewAllPatientListFragment = ViewAllPatientListFragment.newInstance();

        mViewPagerAdapter.addFragment(activePatientListFragment, getString(R.string.active));
        mViewPagerAdapter.addFragment(viewAllPatientListFragment, getString(R.string.view_all));
        viewPager.setAdapter(mViewPagerAdapter);
        viewPager.setOffscreenPageLimit(2);

        /*viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                CommonMethods.Log("addOnPageChangeListener", "" + mViewPagerAdapter.getPageTitle(position));
            }

            @Override
            public void onPageSelected(int position) {
                if (isAnyItemDeleted) {
                    switch (position) {
                        case 0:
                            break;
                        case 1:
                            break;
                    }
                    isAnyItemDeleted = false;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/
    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        if (customResponse != null) {
            WaitingListBaseModel waitingListBaseModel = (WaitingListBaseModel) customResponse;
            mWaitingClinicList = waitingListBaseModel.getWaitingListDataModel().getWaitingclinicList();
            appointmentFormat=waitingListBaseModel.getWaitingListDataModel().getAppointmentFormat();
            // pass data to fragment
            activePatientListFragment.init();
            viewAllPatientListFragment.init();
        }
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

    @OnClick({R.id.backImageView, R.id.titleTextView, R.id.userInfoTextView, R.id.leftFab, R.id.addNewPatientFAB})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backImageView:
                onBackPressed();
                break;
            case R.id.titleTextView:
                break;
            case R.id.userInfoTextView:
                break;
            case R.id.leftFab:
                Intent intent = new Intent(this, MyPatientsActivity.class);
                intent.putExtra(RescribeConstants.ACTIVITY_LAUNCHED_FROM, RescribeConstants.WAITING_LIST);
                startActivityForResult(intent, RESULT_CLOSE_ACTIVITY_WAITING_LIST);
                break;
            case R.id.addNewPatientFAB: {

                if (mViewPagerAdapter != null) {
                    HashMap<String, String> selectedClinicDataMap = null;
                    switch (viewpager.getCurrentItem()) {
                        case 0:
                            selectedClinicDataMap = activePatientListFragment.getSelectedClinicDataMap();
                            break;
                        case 1:
                            selectedClinicDataMap = viewAllPatientListFragment.getSelectedClinicDataMap();
                            break;
                    }
                    if (selectedClinicDataMap != null) {
                        Bundle b = new Bundle();
                        b.putInt(RescribeConstants.CLINIC_ID, Integer.parseInt(selectedClinicDataMap.get(RescribeConstants.CLINIC_ID)));
                        b.putInt(RescribeConstants.CITY_ID, Integer.parseInt(selectedClinicDataMap.get(RescribeConstants.CITY_ID)));
                        b.putString(RescribeConstants.CITY_NAME, selectedClinicDataMap.get(RescribeConstants.CITY_NAME));
                        b.putString(RescribeConstants.LOCATION_ID, selectedClinicDataMap.get(RescribeConstants.LOCATION_ID));

                        Intent i = new Intent(WaitingMainListActivity.this, AddNewPatientWebViewActivity.class);
                        //  Intent i = new Intent(getActivity(), AddNewPatientActivity.class);
                        i.putExtra(RescribeConstants.PATIENT_DETAILS, b);
                        i.putExtra(RescribeConstants.START_FROM, RescribeConstants.WAITING_LIST);
                        startActivityForResult(i, 121);
                    }

                }
                //   showDialogToSelectLocation();
                break;
            }
        }
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_CLOSE_ACTIVITY_WAITING_LIST) {
            finish();
        } else if (requestCode == 121){
            // get location id
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        doCallGetWaitingListAPI();
    }

    public void doCallGetWaitingListAPI() {
        mAppointmentHelper.doGetWaitingList();
    }

    public void deletePatientActive(int clinicId, int waitingID) {

        for (WaitingclinicList cList : mWaitingClinicList) {
            if (cList.getClinicId() == clinicId) {
                WaitingPatientList waitingPatientList = cList.getWaitingPatientList();
                ArrayList<Active> activeList = waitingPatientList.getActive();
                for (int index = 0; index < activeList.size(); index++) {
                    Active activeObj = activeList.get(index);
                    if (activeObj.getWaitingId() == waitingID) {
                        activePatientListFragment.removeItem(index);
                        waitingPatientList.getActive().remove(activeObj);
                        break;
                    }
                }
            }
        }
    }

    public void deletePatientViewAll(int clinicId, int waitingID) {
        for (WaitingclinicList cList : mWaitingClinicList) {
            if (cList.getClinicId() == clinicId) {
                WaitingPatientList waitingPatientList = cList.getWaitingPatientList();
                ArrayList<ViewAll> viewAllList = waitingPatientList.getViewAll();
                for (int index = 0; index < viewAllList.size(); index++) {
                    ViewAll viewAllObj = viewAllList.get(index);
                    if (viewAllObj.getWaitingId() == waitingID) {
                        viewAllPatientListFragment.removeItem(index);
                        waitingPatientList.getViewAll().remove(viewAllObj);
                        break;
                    }
                }
            }
        }
    }

}