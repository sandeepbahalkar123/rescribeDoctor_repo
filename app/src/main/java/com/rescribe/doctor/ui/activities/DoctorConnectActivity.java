package com.rescribe.doctor.ui.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.rescribe.doctor.R;
import com.rescribe.doctor.helpers.doctor_connect.DoctorConnectChatHelper;
import com.rescribe.doctor.helpers.doctor_connect.DoctorConnectHelper;
import com.rescribe.doctor.helpers.doctor_connect.DoctorConnectSearchHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.doctor_connect_search.DoctorConnectSearchBaseModel;
import com.rescribe.doctor.model.doctor_connect_search.SearchDataModel;
import com.rescribe.doctor.model.parceable_doctor_connect.DoctorConnectBaseModel;
import com.rescribe.doctor.model.parceable_doctor_connect.DoctorConnectDataModel;
import com.rescribe.doctor.model.parceable_doctor_connect_chat.Data;
import com.rescribe.doctor.model.parceable_doctor_connect_chat.DoctorConnectChatBaseModel;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.ui.customesViews.EditTextWithDeleteButton;
import com.rescribe.doctor.ui.fragments.doctor_connect.DoctorConnectChatFragment;
import com.rescribe.doctor.ui.fragments.doctor_connect.DoctorConnectFragment;
import com.rescribe.doctor.ui.fragments.doctor_connect.DoctorConnectSearchContainerFragment;
import com.rescribe.doctor.ui.fragments.doctor_connect.SearchBySpecializationOfDoctorFragment;
import com.rescribe.doctor.ui.fragments.doctor_connect.SearchDoctorByNameFragment;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by jeetal on 5/9/17.
 */

public class DoctorConnectActivity extends AppCompatActivity implements DoctorConnectSearchContainerFragment.OnAddFragmentListener, SearchBySpecializationOfDoctorFragment.OnAddFragmentListener, HelperResponse {
    @BindView(R.id.backButton)
    ImageView mBackButton;
    @BindView(R.id.tabsDoctorConnect)
    TabLayout mTabsDoctorConnect;
    @BindView(R.id.doctorConnectViewpager)
    ViewPager mDoctorConnectViewpager;
    String[] mFragmentTitleList = new String[3];
    @BindView(R.id.title)
    CustomTextView title;
    @BindView(R.id.searchView)
    EditTextWithDeleteButton mSearchView;
    private DoctorConnectSearchContainerFragment doctorConnectSearchContainerFragment;
    private SearchBySpecializationOfDoctorFragment searchBySpecializationOfDoctorFragment;
    private SearchDoctorByNameFragment searchDoctorByNameFragment;
    private DoctorConnectChatHelper mDoctorConnectChatHelper;
    private DoctorConnectChatBaseModel mDoctorConnectChatBaseModel = new DoctorConnectChatBaseModel();
    private Data mData = new Data();
    private DoctorConnectHelper mDoctorConnectHelper;
    private DoctorConnectBaseModel doctorConnectBaseModel = new DoctorConnectBaseModel();
    private DoctorConnectDataModel mDoctorConnectDataModel = new DoctorConnectDataModel();
    private DoctorConnectSearchHelper doctorConnectSearchHelper;
    private DoctorConnectSearchBaseModel doctorConnectSearchBaseModel;
    private ArrayList<SearchDataModel> doctorConnectSearchBaseModelList;
    private SearchDataModel searchDataModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_connect);
        ButterKnife.bind(this);
        mFragmentTitleList[0] = getString(R.string.chats);
        mFragmentTitleList[1] = getString(R.string.connect);
        mFragmentTitleList[2] = getString(R.string.search);
        setupViewPager();
        mTabsDoctorConnect.setupWithViewPager(mDoctorConnectViewpager);
        initialize();
    }

    private void initialize() {
        mTabsDoctorConnect.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabPosition = mTabsDoctorConnect.getSelectedTabPosition();
                if (tabPosition == 2) {
                    mSearchView.setVisibility(View.VISIBLE);
                    title.setVisibility(View.GONE);
                } else {
                    mSearchView.setVisibility(View.GONE);
                    title.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabPosition = mTabsDoctorConnect.getSelectedTabPosition();
                if (tabPosition == 2) {
                    title.setVisibility(View.GONE);
                    mSearchView.setVisibility(View.VISIBLE);
                } else {
                    mSearchView.setVisibility(View.GONE);
                    title.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int tabPosition = mTabsDoctorConnect.getSelectedTabPosition();
                if (tabPosition == 2) {
                    mSearchView.setVisibility(View.VISIBLE);
                    title.setVisibility(View.GONE);
                } else {
                    mSearchView.setVisibility(View.GONE);
                    title.setVisibility(View.VISIBLE);
                }
            }
        });

        mSearchView.addTextChangedListener(editTextChanged());
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
                //search Bar Code
                if (searchDoctorByNameFragment != null)
                    searchDoctorByNameFragment.setOnClickOfSearchBar(mSearchView.getText().toString());
            }
        };
    }

    private void setupViewPager() {
        //Api call to get doctorChatList
        mDoctorConnectChatHelper = new DoctorConnectChatHelper(this, this);
        mDoctorConnectChatHelper.doDoctorConnectChat();
        //Api call to get doctorConnectList
        mDoctorConnectHelper = new DoctorConnectHelper(this, this);
        mDoctorConnectHelper.doDoctorConnecList();
        //Api call to get getDoctorSpeciality
        doctorConnectSearchHelper = new DoctorConnectSearchHelper(this, this);
        doctorConnectSearchHelper.getDoctorSpecialityList();
        //Doctor connect , chat and search fragment loaded here
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        DoctorConnectChatFragment doctorConnectChatFragment = DoctorConnectChatFragment.newInstance(mData.getChatList());
        DoctorConnectFragment doctorConnectFragment = DoctorConnectFragment.newInstance(mDoctorConnectDataModel.getConnectList());
        doctorConnectSearchContainerFragment = new DoctorConnectSearchContainerFragment();
        adapter.addFragment(doctorConnectChatFragment, getString(R.string.chats));
        adapter.addFragment(doctorConnectFragment, getString(R.string.connect));
        adapter.addFragment(doctorConnectSearchContainerFragment, getString(R.string.search));
        mDoctorConnectViewpager.setAdapter(adapter);
    }

    @OnClick(R.id.backButton)
    public void onViewClicked() {
        mSearchView.setText("");
        onBackPressed();
    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_DOCTOR_CONNECT_CHAT)) {
            mDoctorConnectChatBaseModel = (DoctorConnectChatBaseModel) customResponse;
            mData = mDoctorConnectChatBaseModel.getData();

        } else if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_DOCTOR_CONNECT)) {
            doctorConnectBaseModel = (DoctorConnectBaseModel) customResponse;
            mDoctorConnectDataModel = doctorConnectBaseModel.getDoctorConnectDataModel();
        } else if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_DOCTOR_FILTER_DOCTOR_SPECIALITY_LIST)) {
            doctorConnectSearchBaseModel = (DoctorConnectSearchBaseModel) customResponse;
            searchDataModel = doctorConnectSearchBaseModel.getSearchDataModel();
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

    //TODO: parceable has to be used to getSpecialityOFDoctorList
    @Override

    public void addSpecializationOfDoctorFragment(Bundle bundleData) {
        // Show speciality of Doctor fragment loaded

        searchBySpecializationOfDoctorFragment = SearchBySpecializationOfDoctorFragment.newInstance(searchDataModel.getDoctorSpecialities());
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, searchBySpecializationOfDoctorFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void addSearchDoctorByNameFragment(Bundle bundleData) {

        if (bundleData != null) {
            mSearchView.setText("" + bundleData.getString(getString(R.string.clicked_item_data)));
        }
        // Filter doctor list by name fragment loaded
        searchDoctorByNameFragment = SearchDoctorByNameFragment.newInstance(mDoctorConnectDataModel.getConnectList(), bundleData);
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack("SearchDoctorByNameFragment");
        searchDoctorByNameFragment.setArguments(bundleData);
        fragmentTransaction.replace(R.id.container, searchDoctorByNameFragment);
        fragmentTransaction.commit();


    }
}
