package com.rescribe.doctor.ui.fragments.patient.patient_connect;

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

public class ChatPatientListFragment extends Fragment implements ChatPatientListAdapter.OnDownArrowClicked, BottomMenuAppointmentAdapter.OnMenuBottomItemClickListener, HelperResponse {
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
    private BottomMenuAppointmentAdapter mBottomMenuAppointmentAdapter;
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
        if(args.getString(RescribeConstants.ACTIVITY_LAUNCHED_FROM).equals(RescribeConstants.HOME_PAGE)) {
            mBottomMenuAppointmentAdapter = new BottomMenuAppointmentAdapter(getContext(), this, mBottomMenuList,true);
            recyclerViewBottom.setLayoutManager(new GridLayoutManager(getActivity(), 3));
            recyclerViewBottom.setAdapter(mBottomMenuAppointmentAdapter);
        }else{
            mBottomMenuAppointmentAdapter = new BottomMenuAppointmentAdapter(getContext(), this, mBottomMenuList, false);
            recyclerViewBottom.setLayoutManager(new GridLayoutManager(getActivity(), 3));
            recyclerViewBottom.setAdapter(mBottomMenuAppointmentAdapter);
        }
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
        intent.putExtra(RescribeConstants.PATIENT_INFO, doctorConnectChatModel);
        startActivity(intent);

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

    @Override
    public void setClickOnMenuItem(int position, BottomMenu bottomMenu) {
        if (bottomMenu.getMenuName().equalsIgnoreCase(getString(R.string.select_all))) {
            if (bottomMenu.isSelected()) {

                for (int index = 0; index < mMyPatientsAdapter.getGroupList().size(); index++) {
                    mMyPatientsAdapter.getGroupList().get(index).setSelected(true);
                }
                mMyPatientsAdapter.notifyDataSetChanged();

            } else {
                for (int index = 0; index < mMyPatientsAdapter.getGroupList().size(); index++) {
                    mMyPatientsAdapter.getGroupList().get(index).setSelected(false);
                }
                mMyPatientsAdapter.notifyDataSetChanged();
            }

            //Send SMS
        } else if (bottomMenu.getMenuName().equalsIgnoreCase(getString(R.string.send_sms))) {
            patientInfoLists = new ArrayList<>();
            ArrayList<PatientList> mPatientListsOriginal = new ArrayList<>();
            for (int childIndex = 0; childIndex < mMyPatientsAdapter.getGroupList().size(); childIndex++) {
                PatientList patientList = mMyPatientsAdapter.getGroupList().get(childIndex);
                if (patientList.isSelected()) {
                    PatientInfoList patientInfoListObject = new PatientInfoList();
                    patientInfoListObject.setPatientName(patientList.getPatientName());
                    patientInfoListObject.setPatientId(patientList.getPatientId());
                    patientInfoListObject.setPatientPhone(patientList.getPatientPhone());
                    patientInfoListObject.setHospitalPatId(patientList.getHospitalPatId());
                    patientInfoLists.add(patientInfoListObject);
                    mPatientListsOriginal.add(patientList);
                }
            }


            if (!patientInfoLists.isEmpty()) {
                showDialogForSmsLocationSelection(mDoctorLocationModel);

                for (int i = 0; i < mBottomMenuAppointmentAdapter.getList().size(); i++) {
                    if (mBottomMenuAppointmentAdapter.getList().get(i).getMenuName().equalsIgnoreCase(getString(R.string.send_sms))) {
                        mBottomMenuAppointmentAdapter.getList().get(i).setSelected(false);
                    }
                }
                mBottomMenuAppointmentAdapter.notifyDataSetChanged();
            } else {
                CommonMethods.showToast(getActivity(), getString(R.string.please_select_patients));
                for (int i = 0; i < mBottomMenuAppointmentAdapter.getList().size(); i++) {
                    if (mBottomMenuAppointmentAdapter.getList().get(i).getMenuName().equalsIgnoreCase(getString(R.string.send_sms))) {
                        mBottomMenuAppointmentAdapter.getList().get(i).setSelected(false);
                    }
                }
                mBottomMenuAppointmentAdapter.notifyDataSetChanged();
            }

        } else if (bottomMenu.getMenuName().equalsIgnoreCase(getString(R.string.waiting_list))) {

            patientsListAddToWaitingLists = new ArrayList<>();
            for (int childIndex = 0; childIndex < mMyPatientsAdapter.getGroupList().size(); childIndex++) {
                PatientList patientList = mMyPatientsAdapter.getGroupList().get(childIndex);
                if (patientList.isSelected()) {
                    PatientsListAddToWaitingList patientInfoListObject = new PatientsListAddToWaitingList();
                    patientInfoListObject.setPatientName(patientList.getPatientName());
                    patientInfoListObject.setPatientId(String.valueOf(patientList.getPatientId()));
                    patientInfoListObject.setHospitalPatId(String.valueOf(patientList.getHospitalPatId()));
                    patientsListAddToWaitingLists.add(patientInfoListObject);

                }
            }


            if (!patientsListAddToWaitingLists.isEmpty()) {

                showDialogToSelectLocation(mDoctorLocationModel);

                for (int i = 0; i < mBottomMenuAppointmentAdapter.getList().size(); i++) {
                    if (mBottomMenuAppointmentAdapter.getList().get(i).getMenuName().equalsIgnoreCase(getString(R.string.waiting_list))) {
                        mBottomMenuAppointmentAdapter.getList().get(i).setSelected(false);
                    }
                }
                mBottomMenuAppointmentAdapter.notifyDataSetChanged();
            } else {
                CommonMethods.showToast(getActivity(), getString(R.string.please_select_patients));
                for (int i = 0; i < mBottomMenuAppointmentAdapter.getList().size(); i++) {
                    if (mBottomMenuAppointmentAdapter.getList().get(i).getMenuName().equalsIgnoreCase(getString(R.string.waiting_list))) {
                        mBottomMenuAppointmentAdapter.getList().get(i).setSelected(false);
                    }
                }
                mBottomMenuAppointmentAdapter.notifyDataSetChanged();
            }
        }

    }

    private void showDialogForSmsLocationSelection(ArrayList<DoctorLocationModel> mDoctorLocationModel) {
        final Dialog dialog = new Dialog(getActivity());

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_select_location_waiting_list_layout);
        dialog.setCancelable(true);

        LayoutInflater inflater = LayoutInflater.from(getActivity());

        RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroup);
        for (int index = 0; index < mDoctorLocationModel.size(); index++) {
            final DoctorLocationModel clinicList = mDoctorLocationModel.get(index);

            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.dialog_location_radio_item, null, false);
            radioButton.setText(clinicList.getClinicName() + ", " + clinicList.getAddress());
            radioButton.setId(CommonMethods.generateViewId());
            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mLocationId = clinicList.getLocationId();
                    mClinicId = clinicList.getClinicId();
                    mClinicName = clinicList.getClinicName();
                }
            });
            radioGroup.addView(radioButton);
        }

        TextView okButton = (TextView) dialog.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mLocationId != 0) {
                    Intent intent = new Intent(getActivity(),TemplateListForMyPatients.class);
                    intent.putExtra(RescribeConstants.LOCATION_ID,mLocationId);
                    intent.putExtra(RescribeConstants.CLINIC_ID,mClinicId);
                    intent.putExtra(RescribeConstants.CLINIC_NAME,mClinicName);
                    intent.putParcelableArrayListExtra(RescribeConstants.PATIENT_LIST,patientInfoLists);
                    startActivity(intent);
                    dialog.cancel();
                } else {
                    Toast.makeText(getActivity(), "Please select clinic location.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    private void showDialogToSelectLocation(ArrayList<DoctorLocationModel> mPatientListsOriginal) {
        final Dialog dialog = new Dialog(getActivity());

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_select_location_waiting_list_layout);
        dialog.setCancelable(true);

        LayoutInflater inflater = LayoutInflater.from(getActivity());

        RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroup);
        for (int index = 0; index < mPatientListsOriginal.size(); index++) {
            final DoctorLocationModel clinicList = mPatientListsOriginal.get(index);

            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.dialog_location_radio_item, null, false);
            radioButton.setText(clinicList.getClinicName() + ", " + clinicList.getAddress());
            radioButton.setId(CommonMethods.generateViewId());
            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mLocationId = clinicList.getLocationId();
                    mClinicId = clinicList.getClinicId();
                    mClinicName = clinicList.getClinicName();
                }
            });
            radioGroup.addView(radioButton);
        }

        TextView okButton = (TextView) dialog.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mLocationId != 0) {
                    callWaitingListApi();
                    dialog.cancel();
                } else
                    Toast.makeText(getActivity(), "Please select clinic location.", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }


    private void callWaitingListApi() {
        RequestForWaitingListPatients requestForWaitingListPatients = new RequestForWaitingListPatients();
        requestForWaitingListPatients.setPatientsListAddToWaitingList(patientsListAddToWaitingLists);
        requestForWaitingListPatients.setDate(CommonMethods.getFormattedDate(CommonMethods.getCurrentDate(), RescribeConstants.DATE_PATTERN.DD_MM_YYYY, RescribeConstants.DATE_PATTERN.YYYY_MM_DD));
        requestForWaitingListPatients.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, getActivity())));
        requestForWaitingListPatients.setLocationId(mLocationId);
        mAppointmentHelper.doAddToWaitingList(requestForWaitingListPatients);
    }


    @OnClick({R.id.rightFab, R.id.leftFab})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rightFab:
                MyPatientsActivity activity = (MyPatientsActivity) getActivity();
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

        } else if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_ADD_TO_WAITING_LIST)) {
            TemplateBaseModel templateBaseModel = (TemplateBaseModel) customResponse;
            if (templateBaseModel.getCommon().isSuccess()) {
                if (templateBaseModel.getCommon().getStatusMessage().toLowerCase().contains(getString(R.string.all_patients_exists).toLowerCase())) {
                    Toast.makeText(getActivity(), templateBaseModel.getCommon().getStatusMessage(), Toast.LENGTH_LONG).show();

                } else if (templateBaseModel.getCommon().getStatusMessage().toLowerCase().contains(getString(R.string.patients_added_to_waiting_list).toLowerCase())) {
                    Intent intent = new Intent(getActivity(), WaitingMainListActivity.class);
                    startActivity(intent);
                    Toast.makeText(getActivity(), templateBaseModel.getCommon().getStatusMessage(), Toast.LENGTH_LONG).show();
                    getActivity().finish();
                    getActivity().setResult(RESULT_CLOSE_ACTIVITY_WAITING_LIST);
                } else if (templateBaseModel.getCommon().getStatusMessage().toLowerCase().contains(getString(R.string.patient_limit_exceeded).toLowerCase())) {
                    Toast.makeText(getActivity(), templateBaseModel.getCommon().getStatusMessage(), Toast.LENGTH_LONG).show();

                } else if (templateBaseModel.getCommon().getStatusMessage().toLowerCase().contains(getString(R.string.already_exists_in_waiting_list).toLowerCase())) {
                    Toast.makeText(getActivity(), templateBaseModel.getCommon().getStatusMessage(), Toast.LENGTH_LONG).show();

                } else if (templateBaseModel.getCommon().getStatusMessage().toLowerCase().contains(getString(R.string.added_to_waiting_list).toLowerCase())) {
                    Intent intent = new Intent(getActivity(), WaitingMainListActivity.class);
                    startActivity(intent);
                    Toast.makeText(getActivity(), templateBaseModel.getCommon().getStatusMessage(), Toast.LENGTH_LONG).show();
                    getActivity().finish();
                    getActivity().setResult(RESULT_CLOSE_ACTIVITY_WAITING_LIST);
                }
            }
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
