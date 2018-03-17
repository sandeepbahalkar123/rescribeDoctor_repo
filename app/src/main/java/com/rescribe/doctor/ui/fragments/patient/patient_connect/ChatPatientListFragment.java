package com.rescribe.doctor.ui.fragments.patient.patient_connect;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.Editable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.my_appointments.BottomMenuAppointmentAdapter;
import com.rescribe.doctor.adapters.patient_connect.ChatPatientListAdapter;
import com.rescribe.doctor.bottom_menus.BottomMenu;
import com.rescribe.doctor.helpers.doctor_patients.MyPatientBaseModel;
import com.rescribe.doctor.helpers.doctor_patients.PatientList;
import com.rescribe.doctor.helpers.myappointments.AppointmentHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.doctor_connect_chat.DoctorConnectChatBaseModel;
import com.rescribe.doctor.model.doctor_location.DoctorLocationModel;
import com.rescribe.doctor.model.patient.patient_connect.PatientData;
import com.rescribe.doctor.model.patient.template_sms.TemplateBaseModel;
import com.rescribe.doctor.model.patient.template_sms.request_send_sms.PatientInfoList;
import com.rescribe.doctor.model.request_patients.RequestSearchPatients;
import com.rescribe.doctor.model.waiting_list.request_add_waiting_list.PatientsListAddToWaitingList;
import com.rescribe.doctor.model.waiting_list.request_add_waiting_list.RequestForWaitingListPatients;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.singleton.RescribeApplication;
import com.rescribe.doctor.ui.activities.ChatActivity;
import com.rescribe.doctor.ui.activities.my_patients.MyPatientsActivity;
import com.rescribe.doctor.ui.activities.my_patients.ShowMyPatientsListActivity;
import com.rescribe.doctor.ui.activities.my_patients.patient_history.PatientHistoryActivity;
import com.rescribe.doctor.ui.activities.waiting_list.WaitingMainListActivity;
import com.rescribe.doctor.ui.customesViews.EditTextWithDeleteButton;
import com.rescribe.doctor.ui.customesViews.drag_drop_recyclerview_helper.EndlessRecyclerViewScrollListener;
import com.rescribe.doctor.ui.fragments.patient.my_patient.TemplateListForMyPatients;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.rescribe.doctor.ui.activities.waiting_list.WaitingMainListActivity.RESULT_CLOSE_ACTIVITY_WAITING_LIST;

/**
 * Created by jeetal on 5/3/18.
 */

public class ChatPatientListFragment extends Fragment implements ChatPatientListAdapter.OnDownArrowClicked, HelperResponse {

    public static final int CALL_FROM_MY_PATIENT_LIST = 0600;
    private static Bundle args;
    private AppointmentHelper mAppointmentHelper;
    @BindView(R.id.whiteUnderLine)
    ImageView whiteUnderLine;
    @BindView(R.id.historyExpandableListView)
    ExpandableListView expandableListView;
    @BindView(R.id.emptyListView)
    RelativeLayout emptyListView;
    @BindView(R.id.rightFab)
    FloatingActionButton rightFab;
    @BindView(R.id.leftFab)
    FloatingActionButton leftFab;
    @BindView(R.id.appointmentLayoutContainer)
    LinearLayout appointmentLayoutContainer;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.recyclerViewBottom)
    RecyclerView recyclerViewBottom;
    @BindView(R.id.searchEditText)
    EditTextWithDeleteButton searchEditText;
    private Unbinder unbinder;
    private MyPatientBaseModel myPatientBaseModel;
    private ChatPatientListAdapter mMyPatientsAdapter;
    private ArrayList<BottomMenu> mBottomMenuList;
    private String[] mMenuNames = {"Select All", "Send SMS", "Waiting List"};
    public int TOTAL_PAGE_COUNT = 1;
    public boolean isLoading = false;
    public static final int PAGE_START = 0;
    public int currentPage = PAGE_START;
    private String searchText = "";
    private ArrayList<DoctorLocationModel> mDoctorLocationModel = new ArrayList<>();
    private ArrayList<PatientList> mPatientListsOriginal;
    private int mLocationId;
    private ArrayList<PatientsListAddToWaitingList> patientsListAddToWaitingLists;
    private ArrayList<PatientInfoList> patientInfoLists;
    private int mClinicId;
    private String mClinicName = "";
    private boolean isFiltered = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.my_appointments_layout, container, false);
        //  hideSoftKeyboard();
        unbinder = ButterKnife.bind(this, mRootView);
        init();
        return mRootView;

    }

    private void init() {
        mBottomMenuList = new ArrayList<>();
        mDoctorLocationModel = RescribeApplication.getDoctorLocationModels();
        mAppointmentHelper = new AppointmentHelper(getActivity(), this);
        for (String mMenuName : mMenuNames) {
            BottomMenu bottomMenu = new BottomMenu();
            bottomMenu.setMenuName(mMenuName);
            mBottomMenuList.add(bottomMenu);
        }
        myPatientBaseModel = args.getParcelable(RescribeConstants.MYPATIENTS_DATA);

        if (myPatientBaseModel.getPatientDataModel().getPatientList().size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setClipToPadding(false);
            emptyListView.setVisibility(View.GONE);
            LinearLayoutManager linearlayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(linearlayoutManager);
            // off recyclerView Animation
            RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
            recyclerView.setPadding(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.dp67));
            recyclerView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.divider));
            if (animator instanceof SimpleItemAnimator)
                ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);

            mMyPatientsAdapter = new ChatPatientListAdapter(getActivity(), myPatientBaseModel.getPatientDataModel().getPatientList(), this);
            recyclerView.setAdapter(mMyPatientsAdapter);
            recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearlayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    loadNextPage(page);
                }
            });

        } else {
            recyclerView.setVisibility(View.GONE);
            emptyListView.setVisibility(View.VISIBLE);
        }

        searchEditText.addTextChangedListener(new EditTextWithDeleteButton.TextChangedListener() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchText = s.toString();
                if (searchText.length() >= 3) {
                    serachPatientsUsingSearchBar(getContext(), searchText);
                    isFiltered = true;
                } else if (searchText.length() < 3 && isFiltered) {
                    isFiltered = false;
                    resetFilter();
                }
            }
        });

    }

    private void resetFilter() {
        init();
    }


    @Override
    public void onRecordFound(boolean isListEmpty) {
        if (isListEmpty)
            emptyListView.setVisibility(View.VISIBLE);
        else
            emptyListView.setVisibility(View.GONE);
    }


    @Override
    public void onClickOfPatientDetails(PatientList patientListObject, String text) {

        Intent intent = new Intent(getActivity(), ChatActivity.class);
        PatientData doctorConnectChatModel = new PatientData();
        doctorConnectChatModel.setId(patientListObject.getPatientId());
        doctorConnectChatModel.setImageUrl(patientListObject.getPatientImageUrl());
        doctorConnectChatModel.setPatientName(patientListObject.getPatientName());
        doctorConnectChatModel.setSalutation(patientListObject.getSalutation());
        intent.putExtra(RescribeConstants.PATIENT_INFO, doctorConnectChatModel);
        intent.putExtra(RescribeConstants.IS_CALL_FROM_MY_PATEINTS, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        getActivity().startActivityForResult(intent, Activity.RESULT_OK);
        getActivity().finish();

    }


    public static ChatPatientListFragment newInstance(Bundle b) {
        ChatPatientListFragment fragment = new ChatPatientListFragment();
        args = b;
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }


    @OnClick({R.id.rightFab, R.id.leftFab})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rightFab:
                ShowMyPatientsListActivity activity = (ShowMyPatientsListActivity) getActivity();
                activity.getActivityDrawerLayout().openDrawer(GravityCompat.END);
                break;
            case R.id.leftFab:

                break;
        }
    }

    public void loadNextPage(int currentPage) {
        if (searchText.length() == 0) {
            RequestSearchPatients mRequestSearchPatients = new RequestSearchPatients();
            mRequestSearchPatients.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, getActivity())));
            mRequestSearchPatients.setPageNo(currentPage);
            mAppointmentHelper.doGetMyPatients(mRequestSearchPatients);
        } else {
            RequestSearchPatients mRequestSearchPatients = new RequestSearchPatients();
            mRequestSearchPatients.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, getActivity())));
            mRequestSearchPatients.setPageNo(currentPage);
            mRequestSearchPatients.setSearchText(searchText);
            mAppointmentHelper.doGetMyPatients(mRequestSearchPatients);
        }
    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_GET_PATIENT_DATA)) {
            if (customResponse != null) {
                //Paginated items added here
                MyPatientBaseModel myAppointmentsBaseModel = (MyPatientBaseModel) customResponse;
                ArrayList<PatientList> mLoadedPatientList = myAppointmentsBaseModel.getPatientDataModel().getPatientList();
                mMyPatientsAdapter.addAll(mLoadedPatientList);

            }
        } else if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_GET_SEARCH_RESULT_MY_PATIENT)) {

            MyPatientBaseModel myAppointmentsBaseModel = (MyPatientBaseModel) customResponse;
            ArrayList<PatientList> mLoadedPatientList = myAppointmentsBaseModel.getPatientDataModel().getPatientList();
            mMyPatientsAdapter.clear();
            for (PatientList patientList : mLoadedPatientList) {
                patientList.setSpannableString(searchText);
            }
            mMyPatientsAdapter.addAll(mLoadedPatientList);

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

    public void serachPatientsUsingSearchBar(Context mContext, String searchText) {
        mAppointmentHelper = new AppointmentHelper(mContext, this);
        RequestSearchPatients mRequestSearchPatients = new RequestSearchPatients();
        mRequestSearchPatients.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, mContext)));
        mRequestSearchPatients.setSearchText(searchText);
        mAppointmentHelper.doGetSearchResult(mRequestSearchPatients);

    }

}
