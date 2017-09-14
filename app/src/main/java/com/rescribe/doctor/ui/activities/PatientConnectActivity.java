package com.rescribe.doctor.ui.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rescribe.doctor.R;
import com.rescribe.doctor.model.patient_connect.PatientData;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.ui.customesViews.EditTextWithDeleteButton;
import com.rescribe.doctor.ui.fragments.patient_connect.PatientConnectChatFragment;
import com.rescribe.doctor.ui.fragments.patient_connect.PatientConnectFragment;
import com.rescribe.doctor.ui.fragments.patient_connect.PatientSearchFragment;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by jeetal on 5/9/17.
 */

public class PatientConnectActivity extends AppCompatActivity {
    @BindView(R.id.backButton)
    ImageView mBackButton;
    @BindView(R.id.tabsDoctorConnect)
    TabLayout mTabsPatientConnect;
    @BindView(R.id.doctorConnectViewpager)
    ViewPager mPatientConnectViewpager;
    String[] mFragmentTitleList = new String[3];
    @BindView(R.id.title)
    CustomTextView title;
    @BindView(R.id.searchView)
    EditTextWithDeleteButton mSearchView;
    @BindView(R.id.whiteUnderLine)
    TextView whiteUnderLine;

    private ViewPagerAdapter mAdapter;
    //-----
    private PatientConnectChatFragment mPatientConnectChatFragment;
    private PatientConnectFragment mPatientConnectFragment;
    private PatientSearchFragment mPatientSearchFragment;
    private ArrayList<PatientData> mReceivedConnectedPatientDataList;
    //-----

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_connect);
        ButterKnife.bind(this);
        title.setText("" + getString(R.string.patient_connect));
        mFragmentTitleList[0] = getString(R.string.chats);
        mFragmentTitleList[1] = getString(R.string.connect);
        mFragmentTitleList[2] = getString(R.string.search);
        setupViewPager();
        mTabsPatientConnect.setupWithViewPager(mPatientConnectViewpager);
        initialize();
    }

    private void initialize() {
        mTabsPatientConnect.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabPosition = mTabsPatientConnect.getSelectedTabPosition();
                if (tabPosition == 2) {
                    mSearchView.setVisibility(View.VISIBLE);
                    whiteUnderLine.setVisibility(View.VISIBLE);
                    title.setVisibility(View.GONE);
                } else {
                    mSearchView.setVisibility(View.GONE);
                    title.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabPosition = mTabsPatientConnect.getSelectedTabPosition();
                if (tabPosition == 2) {
                    whiteUnderLine.setVisibility(View.VISIBLE);

                    mSearchView.setVisibility(View.VISIBLE);
                    title.setVisibility(View.GONE);
                } else {
                    mSearchView.setVisibility(View.GONE);
                    title.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int tabPosition = mTabsPatientConnect.getSelectedTabPosition();
                if (tabPosition == 2) {
                    whiteUnderLine.setVisibility(View.VISIBLE);

                    mSearchView.setVisibility(View.VISIBLE);
                    title.setVisibility(View.GONE);
                } else {
                    mSearchView.setVisibility(View.GONE);
                    title.setVisibility(View.VISIBLE);
                }
            }
        });

        mSearchView.addTextChangedListener(editTextChanged());
        mSearchView.addClearTextButtonListener(new EditTextWithDeleteButton.OnClearButtonClickedInEditTextListener() {
            @Override
            public void onClearButtonClicked() {

            }
        });
    }


    private EditTextWithDeleteButton.TextChangedListener editTextChanged() {
        return new EditTextWithDeleteButton.TextChangedListener() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mPatientSearchFragment != null) {
                    mPatientSearchFragment.setOnClickOfSearchBar(mSearchView.getText().toString());
                }
            }
        };
    }

    private void setupViewPager() {
        //Doctor connect , chat and search fragment loaded here
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mPatientConnectChatFragment = PatientConnectChatFragment.newInstance();
        mPatientConnectFragment = PatientConnectFragment.newInstance();
        mPatientSearchFragment = PatientSearchFragment.newInstance();
        mAdapter.addFragment(mPatientConnectChatFragment, getString(R.string.chats));
        mAdapter.addFragment(mPatientConnectFragment, getString(R.string.connect));
        mAdapter.addFragment(mPatientSearchFragment, getString(R.string.search));
        mPatientConnectViewpager.setAdapter(mAdapter);
    }

    @OnClick(R.id.backButton)
    public void onViewClicked() {
        mSearchView.setText("");
        onBackPressed();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
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

    public interface OnClickOfSearchBar {
        void setOnClickOfSearchBar(String searchText);
    }

    public ArrayList<PatientData> getReceivedConnectedPatientDataList() {
        return mReceivedConnectedPatientDataList;
    }

    public void setReceivedConnectedPatientDataList(ArrayList<PatientData> mReceivedConnectedPatientDataList) {
        this.mReceivedConnectedPatientDataList = mReceivedConnectedPatientDataList;
    }
}