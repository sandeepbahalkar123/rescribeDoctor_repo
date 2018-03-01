package com.rescribe.doctor.ui.fragments.patient.patient_history_fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.philliphsu.bottomsheetpickers.date.DatePickerDialog;
import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.patient_history.OPDStatusShowAdapter;
import com.rescribe.doctor.adapters.patient_history.YearSpinnerAdapter;
import com.rescribe.doctor.helpers.patient_detail.PatientDetailHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.login.Year;
import com.rescribe.doctor.model.patient.patient_history.DatesData;
import com.rescribe.doctor.model.patient.patient_history.PatientHistoryBaseModel;
import com.rescribe.doctor.model.patient.patient_history.PatientHistoryDataModel;
import com.rescribe.doctor.model.patient.patient_history.PatientHistoryInfo;
import com.rescribe.doctor.ui.activities.add_records.SelectedRecordsActivity;
import com.rescribe.doctor.ui.activities.my_patients.patient_history.PatientHistoryActivity;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PatientHistoryListFragmentContainer extends Fragment implements HelperResponse, DatePickerDialog.OnDateSetListener {

    private static final int REQUEST_CODE = 111;
    private static Bundle args;
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
    private YearSpinnerAdapter mYearSpinnerAdapter;
    @BindView(R.id.year)
    Spinner mYearSpinnerView;
    @BindView(R.id.dateTextview)
    CustomTextView mYearSpinnerSingleItem;
    @BindView(R.id.noRecords)
    ImageView noRecords;
    @BindView(R.id.addRecordButton)
    Button mAddRecordButton;
    //----------
    private ArrayList<String> mYearList = new ArrayList<>();
    private ArrayList<Year> mTimePeriodList = new ArrayList<>();
    private Year mCurrentSelectedTimePeriodTab;
    private PatientDetailHelper mPatientDetailHelper;
    private ViewPagerAdapter mViewPagerAdapter = null;
    private HashSet<String> mGeneratedRequestForYearList = new HashSet<>();
    private Context mContext;

    private PatientHistoryActivity mParentActivity;
    private Fragment fragment;
    private PatientHistoryCalenderListFragment mPatientHistoryCalenderListFragment;
    private boolean longpressed;
    private Bundle bundle = new Bundle();
    ArrayList<DatesData> mDatesDataArrayList = new ArrayList<>();
    private DatePickerDialog datePickerDialog;

    public PatientHistoryListFragmentContainer() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mRootView = inflater.inflate(R.layout.patient_history_list_fragment_container, container, false);
        ButterKnife.bind(this, mRootView);

        mParentActivity = (PatientHistoryActivity) getActivity();
        mContext = inflater.getContext();
        initialize();
        return mRootView;
    }

    public static PatientHistoryListFragmentContainer newInstance(Bundle b) {
        PatientHistoryListFragmentContainer fragment = new PatientHistoryListFragmentContainer();
        args = new Bundle();
        args = b;
        fragment.setArguments(args);
        return fragment;
    }


    private void initialize() {

        titleTextView.setText(args.getString(RescribeConstants.PATIENT_NAME));
        userInfoTextView.setVisibility(View.VISIBLE);
        userInfoTextView.setText(args.getString(RescribeConstants.PATIENT_INFO));
        YearSpinnerInteractionListener listener = new YearSpinnerInteractionListener();
        mYearSpinnerView.setOnTouchListener(listener);
        mYearSpinnerView.setOnItemSelectedListener(listener);
        mYearSpinnerView.setVisibility(View.GONE);
        mYearSpinnerSingleItem.setVisibility(View.GONE);
        //-------
        mPatientDetailHelper = new PatientDetailHelper(mContext, this);
        //-------
        mCurrentSelectedTimePeriodTab = new Year();
        mCurrentSelectedTimePeriodTab.setMonthName(new SimpleDateFormat("MMM", Locale.US).format(new Date()));
        mCurrentSelectedTimePeriodTab.setYear(new SimpleDateFormat("yyyy", Locale.US).format(new Date()));
        //---------

//        mOpdStatusGridView.addItemDecoration(new GridSpacingItemDecoration(2,
//                getResources().getDimensionPixelSize(R.dimen.dp5), true));
        //---------

    }

    @OnClick({R.id.backImageView, R.id.addRecordButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backImageView:
                mParentActivity.finish();
                break;
            case R.id.addRecordButton:
                Calendar now = Calendar.getInstance();
// As of version 2.3.0, `BottomSheetDatePickerDialog` is deprecated.
                datePickerDialog = DatePickerDialog.newInstance(
                        this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.setAccentColor(getResources().getColor(R.color.tagColor));
                datePickerDialog.setMaxDate(Calendar.getInstance());
                datePickerDialog.show(getActivity().getSupportFragmentManager(), getResources().getString(R.string.select_date_text));

                break;

        }
    }

    private void setupViewPager() {
        mViewPagerAdapter.mFragmentList.clear();
        mViewPagerAdapter.mFragmentTitleList.clear();
        for (Year data :
                mTimePeriodList) {
            mPatientHistoryCalenderListFragment = new PatientHistoryCalenderListFragment();
            fragment = mPatientHistoryCalenderListFragment.createNewFragment(data, args); // pass data here
            fragment.setTargetFragment(PatientHistoryListFragmentContainer.this, REQUEST_CODE);
            mViewPagerAdapter.addFragment(fragment, data); // pass title here
        }
        mViewpager.setOffscreenPageLimit(0);
        mViewpager.setAdapter(mViewPagerAdapter);

        //------------
        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //    datePickerDialog.show(getActivity().getSupportFragmentManager(), getResources().getString(R.string.select_date_text));

            }

            @Override
            public void onPageSelected(int position) {

                PatientHistoryCalenderListFragment item = (PatientHistoryCalenderListFragment) mViewPagerAdapter.getItem(position);
                Bundle arguments = item.getArguments();
                String month = arguments.getString(RescribeConstants.MONTH);
                String year = arguments.getString(RescribeConstants.YEAR);
                CommonMethods.Log("onPageSelected", month + " " + year);
                mCurrentSelectedTimePeriodTab.setMonthName(month);
                mCurrentSelectedTimePeriodTab.setYear(year);

                for (int i = 0; i < mYearList.size(); i++) {
                    if (mYearList.get(i).equalsIgnoreCase(year)) {
                        mYearSpinnerView.setSelection(i);
                        break;
                    }
                }
                //-------
                if (mYearList.size() == 1) {
                    mYearSpinnerSingleItem.setVisibility(View.VISIBLE);
                    mYearSpinnerView.setVisibility(View.GONE);
                    mYearSpinnerSingleItem.setText(mYearList.get(0).toString());
                } else {
                    mYearSpinnerSingleItem.setVisibility(View.GONE);
                    mYearSpinnerView.setVisibility(View.VISIBLE);
                }
                //-------

                //-----THis condition calls API only once for that specific year.----
                if (!mGeneratedRequestForYearList.contains(year)) {
                    Map<String, Map<String, ArrayList<PatientHistoryInfo>>> yearWiseSortedPatientHistoryInfo = mPatientDetailHelper.getYearWiseSortedPatientHistoryInfo();
                    if (yearWiseSortedPatientHistoryInfo.get(year) == null) {
                        mGeneratedRequestForYearList.add(year);
                        mPatientDetailHelper.doGetPatientHistory(args.getString(RescribeConstants.PATIENT_ID), year);
                    }
                }
                //---------
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //------------
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < mTimePeriodList.size(); i++) {
                    Year temp = mTimePeriodList.get(i);
                    if (temp.getYear().equalsIgnoreCase(mCurrentSelectedTimePeriodTab.getYear()) &&
                            temp.getMonthName().equalsIgnoreCase(mCurrentSelectedTimePeriodTab.getMonthName())) {
                        mViewpager.setCurrentItem(i);
                        break;
                    }

                    //    mViewpager.setCurrentItem(mTimePeriodList.size());
                }
            }
        }, 0);
        //---------
    }

    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {

        Intent intent = new Intent(getActivity(), SelectedRecordsActivity.class);
        startActivity(intent);

    }

    //---------------
    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<Year> mFragmentTitleList = new ArrayList<>();

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

        public void addFragment(Fragment fragment, Year title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position).getMonthName();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    private class YearSpinnerInteractionListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {

        boolean mYearSpinnerConfigChange = false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mYearSpinnerConfigChange = true;
            return false;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if (mYearSpinnerConfigChange) {
                // Your selection handling code here
                mYearSpinnerConfigChange = false;
                if (parent.getId() == R.id.year && !mYearSpinnerConfigChange) {
                    String selectedYear = mYearList.get(parent.getSelectedItemPosition());
                    for (int i = 0; i < mTimePeriodList.size(); i++) {
                        if (mTimePeriodList.get(i).getYear().equalsIgnoreCase("" + selectedYear)) {
                            mCurrentSelectedTimePeriodTab = mTimePeriodList.get(i);
                            mViewpager.setCurrentItem(i);
                            break;
                        }
                    }
                } else {
                    mYearSpinnerConfigChange = false;
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    //---------------
    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        PatientHistoryBaseModel newBaseModel = (PatientHistoryBaseModel) customResponse;
        PatientHistoryDataModel dataModel = newBaseModel.getPatientHistoryDataModel();

        mTimePeriodList = dataModel.getFormattedYearList();
        if (mViewPagerAdapter == null) {
            mViewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
            mTabLayout.setupWithViewPager(mViewpager);
            mYearList = dataModel.getUniqueYears();
            mYearSpinnerAdapter = new YearSpinnerAdapter(mParentActivity, mYearList, ContextCompat.getColor(getActivity(), R.color.white));
            mYearSpinnerView.setAdapter(mYearSpinnerAdapter);
        }

        if (dataModel.getYearsMonthsData().isEmpty()) {
            noRecords.setVisibility(View.VISIBLE);
            mYearSpinnerView.setVisibility(View.GONE);
            mTabLayout.setVisibility(View.GONE);
        } else {
            noRecords.setVisibility(View.GONE);
            mYearSpinnerView.setVisibility(View.VISIBLE);
            mTabLayout.setVisibility(View.VISIBLE);
            if (mYearList.size() == 1) {
                mYearSpinnerView.setVisibility(View.GONE);
                mYearSpinnerSingleItem.setVisibility(View.VISIBLE);
                mYearSpinnerSingleItem.setText(mYearList.get(0).toString());
            } else {
                mYearSpinnerView.setVisibility(View.VISIBLE);
                mYearSpinnerSingleItem.setVisibility(View.GONE);

            }
        }

        if (mTabLayout != null) {
            if (mTabLayout.getTabCount() > 5) {
                mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            } else {
                mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                mTabLayout.setTabMode(TabLayout.MODE_FIXED);
            }
        }

        setupViewPager();
    }

    @Override
    public void onParseError(String mOldDataTag, String errorMessage) {

    }

    @Override
    public void onServerError(String mOldDataTag, String serverErrorMessage) {

    }

    @Override
    public void onNoConnectionError(String mOldDataTag, String serverErrorMessage) {

        setupViewPager();

    }
    //---------------


    @Override
    public void onResume() {
        super.onResume();
        if (!mGeneratedRequestForYearList.contains(mCurrentSelectedTimePeriodTab.getYear())) {
            Map<String, Map<String, ArrayList<PatientHistoryInfo>>> yearWiseSortedMyRecordInfoAndReports = mPatientDetailHelper.getYearWiseSortedPatientHistoryInfo();
            if (yearWiseSortedMyRecordInfoAndReports.get(mCurrentSelectedTimePeriodTab.getYear()) == null) {
                mPatientDetailHelper.doGetPatientHistory(args.getString(RescribeConstants.PATIENT_ID), mCurrentSelectedTimePeriodTab.getYear());
                mGeneratedRequestForYearList.add(mCurrentSelectedTimePeriodTab.getYear());
            }
        }
    }

    public PatientDetailHelper getParentPatientDetailHelper() {
        return mPatientDetailHelper;
    }

    public Button getAddRecordButton() {
        return mAddRecordButton;
    }


    public void setOPDStatusGridViewAdapter(ArrayList<String> list) {
        OPDStatusShowAdapter baseAdapter = new OPDStatusShowAdapter(getContext(), list);
        // mOpdStatusGridView.setAdapter(baseAdapter);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            bundle = data.getBundleExtra(RescribeConstants.DATES_INFO);
            if (bundle != null) {
                longpressed = bundle.getBoolean(RescribeConstants.LONGPRESSED, false);
                mDatesDataArrayList = bundle.getParcelableArrayList(RescribeConstants.DATES_LIST);
            }
        }
    }
}