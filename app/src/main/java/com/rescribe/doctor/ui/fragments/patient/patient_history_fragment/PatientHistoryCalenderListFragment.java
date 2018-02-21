package com.rescribe.doctor.ui.fragments.patient.patient_history_fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.patient_history.CalenderDayOfMonthGridAdapter;
import com.rescribe.doctor.helpers.patient_detail.PatientDetailHelper;
import com.rescribe.doctor.model.login.Year;
import com.rescribe.doctor.model.patient.patient_history.DatesData;
import com.rescribe.doctor.model.patient.patient_history.PatientHistoryInfo;
import com.rescribe.doctor.ui.activities.patient_details.SingleVisitDetailsActivity;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PatientHistoryCalenderListFragment extends Fragment implements CalenderDayOfMonthGridAdapter.OnDayClickListener {

    private static String patientName;
    private static String patientInfo;
    @BindView(R.id.calenderDays)
    RecyclerView mCalenderDays;
    private Context mContext;
    private String mMonthName;
    private String mYear;
    private ArrayList<PatientHistoryInfo> formattedDoctorList;
    private ArrayList<DatesData> mDateListForAdapter;
    private boolean mIsLongPressed;
    public CalenderDayOfMonthGridAdapter mCalenderDayOfMonthGridAdapter;
    private ArrayList<DatesData> mAdapterListToNotifyOnBackPress;

    public PatientHistoryCalenderListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mRootView = inflater.inflate(R.layout.patient_history_calender_view, container, false);
        ButterKnife.bind(this, mRootView);

        mContext = inflater.getContext();

        Bundle arguments = getArguments();
        if (arguments != null) {
            mMonthName = arguments.getString(RescribeConstants.MONTH);
            mYear = arguments.getString(RescribeConstants.YEAR);
        }

        setGridViewAdapter();
        return mRootView;
    }


    public static PatientHistoryCalenderListFragment createNewFragment(Year dataString,Bundle b) {
        PatientHistoryCalenderListFragment fragment = new PatientHistoryCalenderListFragment();
        Bundle args = new Bundle();
        args.putString(RescribeConstants.MONTH, dataString.getMonthName());
        args.putString(RescribeConstants.YEAR, dataString.getYear());
        patientName = b.getString(RescribeConstants.PATIENT_NAME);
        patientInfo = b.getString(RescribeConstants.PATIENT_INFO);
        fragment.setArguments(args);
        return fragment;
    }

    public boolean isLongPressed() {
        return mIsLongPressed;
    }


    private void setGridViewAdapter() {

        PatientHistoryListFragmentContainer parentFragment = (PatientHistoryListFragmentContainer) getParentFragment();

        PatientDetailHelper parentPatientDetailHelper = parentFragment.getParentPatientDetailHelper();
        if (parentPatientDetailHelper != null) {
            Map<String, Map<String, ArrayList<PatientHistoryInfo>>> yearWiseSortedPatientHistoryInfo = parentPatientDetailHelper.getYearWiseSortedPatientHistoryInfo();
            if (yearWiseSortedPatientHistoryInfo.size() != 0) {
                Map<String, ArrayList<PatientHistoryInfo>> monthArrayListHashMap = yearWiseSortedPatientHistoryInfo.get(mYear);
                if (monthArrayListHashMap != null) {

                    formattedDoctorList = monthArrayListHashMap.get(mMonthName);


                    mCalenderDayOfMonthGridAdapter = new CalenderDayOfMonthGridAdapter(this.getContext(), formattedDoctorList, this);
                    LinearLayoutManager linearlayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    mCalenderDays.setLayoutManager(linearlayoutManager);
                    mCalenderDays.setAdapter(mCalenderDayOfMonthGridAdapter);

                    setOPDStatusGridViewAdapter(parentFragment, formattedDoctorList);
                }
            }
        }
    }

    @Override
    public void onLongClicked(boolean longpressed) {
       // mAdapterListToNotifyOnBackPress = mCalenderDayOfMonthGridAdapter.getAdapterList();
        Intent intent = new Intent();

        Bundle bundle = new Bundle();
        bundle.putBoolean(RescribeConstants.LONGPRESSED,longpressed);
       // bundle.putParcelableArrayList(RescribeConstants.DATES_LIST,mCalenderDayOfMonthGridAdapter.getAdapterList());
        getTargetFragment().onActivityResult(
                getTargetRequestCode(),
                Activity.RESULT_OK,
                intent.putExtra(RescribeConstants.DATES_INFO, bundle)
        );
        mIsLongPressed = longpressed;
        if (longpressed) {
            PatientHistoryListFragmentContainer parentFragment = (PatientHistoryListFragmentContainer) getParentFragment();
            parentFragment.getAddRecordButton().setVisibility(View.VISIBLE);
        } else {
            PatientHistoryListFragmentContainer parentFragment = (PatientHistoryListFragmentContainer) getParentFragment();
            parentFragment.getAddRecordButton().setVisibility(View.GONE);
        }
    }

    @Override
    public void onClickOFLayout(String visitDate) {
        Intent intent = new Intent(getActivity(),SingleVisitDetailsActivity.class);
        intent.putExtra(RescribeConstants.PATIENT_NAME,patientName);
        intent.putExtra(RescribeConstants.PATIENT_INFO,patientInfo);
        intent.putExtra(RescribeConstants.DATE,visitDate);
        startActivity(intent);

    }

    // To find unique status from list, and set list in recycleview of parent fragment.
    private void setOPDStatusGridViewAdapter(PatientHistoryListFragmentContainer parent, ArrayList<PatientHistoryInfo> formattedDoctorList) {

        LinkedHashSet<String> linkedHashSet = new LinkedHashSet();
        for (PatientHistoryInfo obj :
                formattedDoctorList) {
            linkedHashSet.add(obj.getOpdStatus());
        }
        ArrayList<String> strings = new ArrayList<>(linkedHashSet);
        parent.setOPDStatusGridViewAdapter(strings);
    }

    private PatientHistoryInfo findWhichDate(int dateNumber) {
        for (PatientHistoryInfo obj :
                formattedDoctorList) {
            if (obj.getWhichDate() == dateNumber) {
                return obj;
            }
        }
        return null;
    }

    public void removeSelectedDate(ArrayList<DatesData> datesDataArrayList) {
        for (DatesData datesData : datesDataArrayList) {
            datesData.setLongPressed(false);
        }
       // mCalenderDayOfMonthGridAdapter.notifyDataSetChanged();
    }
}