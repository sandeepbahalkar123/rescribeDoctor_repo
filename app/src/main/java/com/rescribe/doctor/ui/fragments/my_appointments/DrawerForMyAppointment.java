package com.rescribe.doctor.ui.fragments.my_appointments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.drawer_adapters.DrawerAppointmentSelectStatusAdapter;
import com.rescribe.doctor.adapters.drawer_adapters.DrawerAppointmetClinicNameAdapter;
import com.rescribe.doctor.adapters.drawer_adapters.SortByPriceFilterAdapter;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.my_appointments.AppointmentList;
import com.rescribe.doctor.model.my_appointments.FilterSortByHighLowList;
import com.rescribe.doctor.model.my_appointments.MyAppointmentsDataModel;
import com.rescribe.doctor.model.my_appointments.PatientList;
import com.rescribe.doctor.model.my_appointments.StatusList;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;
import java.util.concurrent.atomic.DoubleAdder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by jeetal on 12/2/18.
 */

public class DrawerForMyAppointment extends Fragment implements HelperResponse, SortByPriceFilterAdapter.onSortByAmountMenuClicked , DrawerAppointmentSelectStatusAdapter.OnClickOfFilterComponents {


    private static Bundle bundle;
    @BindView(R.id.applyButton)
    Button applyButton;
    @BindView(R.id.titleTextView)
    CustomTextView titleTextView;
    @BindView(R.id.resetButton)
    Button resetButton;
    @BindView(R.id.sortingHeaderView)
    LinearLayout sortingHeaderView;
    @BindView(R.id.chooseOptionForSort)
    CustomTextView chooseOptionForSort;
    @BindView(R.id.chooseOptionToSort)
    LinearLayout chooseOptionToSort;
    @BindView(R.id.genderHeaderView)
    LinearLayout genderHeaderView;
    @BindView(R.id.statusNameRecyclerView)
    RecyclerView statusNameRecyclerView;
    @BindView(R.id.clinicFeesHeaderView)
    LinearLayout clinicFeesHeaderView;
    @BindView(R.id.clinicNameRecyclerView)
    RecyclerView clinicNameRecyclerView;
    @BindView(R.id.clinicFeesContentView)
    LinearLayout clinicFeesContentView;
    @BindView(R.id.clinicFeesView)
    LinearLayout clinicFeesView;
    @BindView(R.id.nestedScroll)
    NestedScrollView nestedScroll;
    @BindView(R.id.hideMainLayout)
    LinearLayout hideMainLayout;
    @BindView(R.id.doneButton)
    Button doneButton;
    @BindView(R.id.sortingTitleTextView)
    CustomTextView sortingTitleTextView;
    @BindView(R.id.resetSortingButton)
    Button resetSortingButton;
    @BindView(R.id.sortingView)
    LinearLayout sortingView;
    @BindView(R.id.sortRecyclerView)
    RecyclerView sortRecyclerView;
    @BindView(R.id.showSortLayout)
    LinearLayout showSortLayout;
    @BindView(R.id.mainParentLayout)
    LinearLayout mainParentLayout;
    @BindView(R.id.selectStatus)
    CustomTextView selectStatus;
    @BindView(R.id.selectClinic)
    CustomTextView selectClinic;
    private Unbinder unbinder;
    private DrawerAppointmentSelectStatusAdapter mDrawerAppointmentSelectStatusAdapter;
    private DrawerAppointmetClinicNameAdapter mDrawerAppointmetClinicNameAdapter;
    private OnDrawerInteractionListener mListener;
    private SortByPriceFilterAdapter mSortByPriceFilterAdapter;
    private String lowToHigh = "(low to high)";
    private String highToLow = "(high to low)";
    private ArrayList<FilterSortByHighLowList> filterSortByHighLowLists = new ArrayList<>();
    private String[] sortOptions = new String[]{"Outstanding Amt" + lowToHigh,
            "Outstanding Amt" + highToLow};
    private int mSortByAmountAdapterPosition;
    private MyAppointmentsDataModel mMyAppointmentsDataModel;
    private ArrayList<StatusList> statusLists = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.appointment_sorting_filter, container, false);
        unbinder = ButterKnife.bind(this, view);
        initialize();
        return view;
    }

    private void initialize() {
        for (String amtString : sortOptions) {
            FilterSortByHighLowList filterSortByHighLowListObject = new FilterSortByHighLowList();
            filterSortByHighLowListObject.setAmountHighOrLow(amtString);
            filterSortByHighLowLists.add(filterSortByHighLowListObject);
        }
        // mSelectedDays = new HashMap<>();
        configureDrawerFieldsData();
    }

    private void configureDrawerFieldsData() {
        mMyAppointmentsDataModel = bundle.getParcelable(RescribeConstants.APPOINTMENT_DATA);
        chooseOptionForSort.setText(getString(R.string.choose_one_option));
        SpannableString selectStatusString = new SpannableString("Select Status");
        selectStatusString.setSpan(new UnderlineSpan(), 0, selectStatusString.length(), 0);
        selectStatus.setText(selectStatusString);
        SpannableString selectClinicString = new SpannableString("Select Clinic");
        selectClinicString.setSpan(new UnderlineSpan(), 0, selectClinicString.length(), 0);
        selectClinic.setText(selectClinicString);
        //select status recyelerview
        mDrawerAppointmentSelectStatusAdapter = new DrawerAppointmentSelectStatusAdapter(getActivity(),mMyAppointmentsDataModel,this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        statusNameRecyclerView.setLayoutManager(layoutManager);
        statusNameRecyclerView.setHasFixedSize(true);
        statusNameRecyclerView.setNestedScrollingEnabled(false);
        statusNameRecyclerView.setAdapter(mDrawerAppointmentSelectStatusAdapter);

        // clinic names recyclerview
        mDrawerAppointmetClinicNameAdapter = new DrawerAppointmetClinicNameAdapter(getActivity(),mMyAppointmentsDataModel.getClinicList());
        LinearLayoutManager layoutManagerClinicList = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        clinicNameRecyclerView.setLayoutManager(layoutManagerClinicList);
        clinicNameRecyclerView.setHasFixedSize(true);
        clinicNameRecyclerView.setNestedScrollingEnabled(false);
        clinicNameRecyclerView.setAdapter(mDrawerAppointmetClinicNameAdapter);

    }

    public static DrawerForMyAppointment newInstance(Bundle b) {
        DrawerForMyAppointment fragment = new DrawerForMyAppointment();
        bundle = new Bundle();
        bundle = b;
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {

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

    @OnClick({R.id.applyButton, R.id.titleTextView, R.id.resetButton, R.id.sortingHeaderView, R.id.genderHeaderView, R.id.selectStatus, R.id.statusNameRecyclerView, R.id.clinicFeesHeaderView, R.id.selectClinic, R.id.clinicNameRecyclerView, R.id.clinicFeesContentView, R.id.clinicFeesView, R.id.nestedScroll, R.id.hideMainLayout, R.id.doneButton, R.id.sortingTitleTextView, R.id.resetSortingButton, R.id.sortingView, R.id.sortRecyclerView, R.id.showSortLayout, R.id.mainParentLayout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.applyButton:
                Bundle b = new Bundle();
                b.putParcelable(RescribeConstants.APPOINTMENT_DATA,mMyAppointmentsDataModel);
                b.putParcelableArrayList(RescribeConstants.FILTER_STATUS_LIST,statusLists);
                mListener.onApply(b, true);
                break;
            case R.id.titleTextView:
                break;
            case R.id.resetButton:
                configureDrawerFieldsData();
                break;
            case R.id.sortingHeaderView:
                break;
            case R.id.genderHeaderView:
                break;
            case R.id.selectStatus:
                break;
            case R.id.statusNameRecyclerView:
                break;
            case R.id.clinicFeesHeaderView:
                break;
            case R.id.selectClinic:
                break;
            case R.id.clinicNameRecyclerView:
                break;
            case R.id.clinicFeesContentView:
                break;
            case R.id.clinicFeesView:
                break;
            case R.id.nestedScroll:
                break;
            case R.id.hideMainLayout:
                break;
            case R.id.doneButton:
                hideMainLayout.setVisibility(View.VISIBLE);
                showSortLayout.setVisibility(View.GONE);
                break;
            case R.id.sortingTitleTextView:
                break;
            case R.id.resetSortingButton:
                break;
            case R.id.sortingView:
                break;
            case R.id.sortRecyclerView:
                break;
            case R.id.showSortLayout:
                break;
            case R.id.mainParentLayout:
                break;
        }
    }

    @Override
    public void onClickOfSortMenu(FilterSortByHighLowList filterSortByHighLowObject, int groupPosition) {
        mSortByAmountAdapterPosition = groupPosition;
    }

    @Override
    public void onClickofSelectStatus(ArrayList<StatusList> mStatusLists) {
         statusLists = mStatusLists;
/*
        for(int statusIndex = 0;statusIndex<mStatusLists.size();statusIndex++){
            ArrayList<AppointmentList> mAppointmentLists = new ArrayList<>();
            for(int parentlistIndex = 0;parentlistIndex<mMyAppointmentsDataModel.getAppointmentList().size();parentlistIndex++){
              AppointmentList appointmentListObject = mMyAppointmentsDataModel.getAppointmentList().get(parentlistIndex);
                ArrayList<PatientList> mPatientList = new ArrayList<>();
                for(int childPatientListIndex = 0;childPatientListIndex<mMyAppointmentsDataModel.getAppointmentList().get(parentlistIndex).getPatientList().size();childPatientListIndex++){
                    PatientList patientListOriginalObject = mMyAppointmentsDataModel.getAppointmentList().get(parentlistIndex).getPatientList().get(childPatientListIndex);
                   if(mStatusLists.get(statusIndex).isSelected()){
                       if(mStatusLists.get(statusIndex).getStatusName().equalsIgnoreCase(patientListOriginalObject.getAppointmentStatus())){
                           mPatientList.add(patientListOriginalObject);
                       }
                   }

                }
                appointmentListObject.setPatientList(mPatientList);
                mAppointmentLists.add(appointmentListObject);
            }

        }*/

    }

    public interface OnDrawerInteractionListener {
        void onApply(Bundle b, boolean drawerRequired);

        void onReset(boolean drawerRequired);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDrawerInteractionListener) {
            mListener = (OnDrawerInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDrawerInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
