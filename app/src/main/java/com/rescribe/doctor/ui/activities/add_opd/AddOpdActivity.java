package com.rescribe.doctor.ui.activities.add_opd;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.rescribe.doctor.R;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.ui.fragments.add_opd.PrescriptionFragment;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddOpdActivity extends AppCompatActivity {


    @BindView(R.id.backImageView)
    ImageView mBackArrow;
    @BindView(R.id.tabFragment)
    TabLayout mTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewpager;
    @BindView(R.id.titleTextView)
    CustomTextView titleTextView;
    @BindView(R.id.userInfoTextView)
    CustomTextView userInfoTextView;


    private Context mContext;
    private ViewPagerAdapter mViewPagerAdapter;

    ArrayList<String> arrayListTabTitle = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_opd);
        ButterKnife.bind(this);
        mContext = this;
        getIntentParams();
        addTabNames();
        setupViewPager();


    }

    private void addTabNames() {
        arrayListTabTitle.add("Complaints");
        arrayListTabTitle.add("Vitals");
        arrayListTabTitle.add("Diagnosis");
        arrayListTabTitle.add("Prescription");
        arrayListTabTitle.add("Radiology");
        arrayListTabTitle.add("Laboratory");
    }

    private void setupViewPager() {
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mTabLayout.setupWithViewPager(mViewpager);
        mViewPagerAdapter.addFragment(AddOpdContainerFragment.getInstance("Complaints"), "Complaints");
        mViewPagerAdapter.addFragment(new Fragment(), "Vitals");
        mViewPagerAdapter.addFragment(AddOpdContainerFragment.getInstance("Diagnosis"), "Diagnosis");
        mViewPagerAdapter.addFragment(PrescriptionFragment.newInstance(), "Prescription");
        mViewPagerAdapter.addFragment(AddOpdContainerFragment.getInstance("Radiology"), "Radiology");
        mViewPagerAdapter.addFragment(AddOpdContainerFragment.getInstance("Laboratory"), "Laboratory"); // pass title here
        mViewpager.setAdapter(mViewPagerAdapter);

    }

    private void getIntentParams() {
        String patientName = getIntent().getStringExtra(RescribeConstants.PATIENT_NAME);
        String patientInfo = getIntent().getStringExtra(RescribeConstants.PATIENT_INFO);
        titleTextView.setText(patientName);
        userInfoTextView.setVisibility(View.VISIBLE);
        userInfoTextView.setText(patientInfo);
    }


    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
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
            return "" + mFragmentTitleList.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

}
