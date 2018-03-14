package com.rescribe.doctor.ui.fragments.my_appointments;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.rescribe.doctor.adapters.my_appointments.AppointmentAdapter;
import com.rescribe.doctor.adapters.my_appointments.BottomMenuAppointmentAdapter;
import com.rescribe.doctor.bottom_menus.BottomMenu;
import com.rescribe.doctor.helpers.myappointments.AppointmentHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.doctor_location.DoctorLocationModel;
import com.rescribe.doctor.model.my_appointments.AppointmentList;
import com.rescribe.doctor.model.my_appointments.MyAppointmentsDataModel;
import com.rescribe.doctor.model.my_appointments.PatientList;
import com.rescribe.doctor.model.my_appointments.request_cancel_or_complete_appointment.RequestAppointmentCancelModel;
import com.rescribe.doctor.model.patient.template_sms.TemplateBaseModel;
import com.rescribe.doctor.model.waiting_list.request_add_waiting_list.PatientsListAddToWaitingList;
import com.rescribe.doctor.model.waiting_list.request_add_waiting_list.RequestForWaitingListPatients;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.singleton.RescribeApplication;
import com.rescribe.doctor.ui.activities.my_appointments.MyAppointmentsActivity;
import com.rescribe.doctor.ui.activities.my_patients.TemplateListActivity;
import com.rescribe.doctor.ui.activities.my_patients.patient_history.PatientHistoryActivity;
import com.rescribe.doctor.ui.activities.waiting_list.WaitingMainListActivity;
import com.rescribe.doctor.ui.customesViews.EditTextWithDeleteButton;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.rescribe.doctor.ui.activities.waiting_list.WaitingMainListActivity.RESULT_CLOSE_ACTIVITY_WAITING_LIST;
import static com.rescribe.doctor.util.CommonMethods.toCamelCase;

/**
 * Created by jeetal on 31/1/18.
 */

public class MyAppointmentsFragment extends Fragment implements AppointmentAdapter.OnDownArrowClicked, BottomMenuAppointmentAdapter.OnMenuBottomItemClickListener, HelperResponse {
    private static Bundle args;
    @BindView(R.id.searchEditText)
    EditTextWithDeleteButton searchEditText;
    @BindView(R.id.whiteUnderLine)
    ImageView whiteUnderLine;
    public ExpandableListView expandableListView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.appointmentLayoutContainer)
    LinearLayout appointmentLayoutContainer;
    @BindView(R.id.emptyListView)
    RelativeLayout emptyListView;
    @BindView(R.id.rightFab)
    FloatingActionButton rightFab;
    @BindView(R.id.leftFab)
    FloatingActionButton leftFab;
    @BindView(R.id.recyclerViewBottom)
    RecyclerView recyclerViewBottom;
    Unbinder unbinder;
    private AppointmentAdapter mAppointmentAdapter;
    private MyAppointmentsDataModel mMyAppointmentsDataModel;
    private BottomMenuAppointmentAdapter mBottomMenuAppointmentAdapter;
    private int mGroupPosition;
    private ArrayList<BottomMenu> mBottomMenuList;
    private String[] mMenuNames = {"Select All", "Send SMS", "Waiting List"};
    private int lastExpandedPosition = -1;
    private String charString = "";
    private List<PatientList> mPatientLists;
    private ArrayList<DoctorLocationModel> mDoctorLocationModel = new ArrayList<>();
    private AppointmentHelper mAppointmentHelper;
    private ArrayList<PatientsListAddToWaitingList> patientsListAddToWaitingLists;
    private int mLocationId;
    private ArrayList<AppointmentList> mAppointmentListOfAdapter = new ArrayList<>();
    private int childPos;
    private int groupPos;
    private int headerPos;
    private boolean isFromGroup;
    private String patientName;
    private String phoneNo;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.my_appointments_layout, container, false);
        unbinder = ButterKnife.bind(this, mRootView);
        expandableListView = (ExpandableListView) mRootView.findViewById(R.id.historyExpandableListView);
        init();
        return mRootView;
    }

    private void init() {
        mAppointmentHelper = new AppointmentHelper(getActivity(), this);
        mBottomMenuList = new ArrayList<>();
        for (String mMenuName : mMenuNames) {
            BottomMenu bottomMenu = new BottomMenu();
            bottomMenu.setMenuName(mMenuName);
            mBottomMenuList.add(bottomMenu);
        }
        mMyAppointmentsDataModel = args.getParcelable(RescribeConstants.APPOINTMENT_DATA);
        final ArrayList<AppointmentList> mAppointmentList = new ArrayList<>();
        if (mMyAppointmentsDataModel.getAppointmentList().size() > 0 || !mMyAppointmentsDataModel.getAppointmentList().isEmpty()) {
            expandableListView.setVisibility(View.VISIBLE);
            expandableListView.setPadding(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.dp67));
            expandableListView.setClipToPadding(false);
            emptyListView.setVisibility(View.GONE);

            for (int i = 0; i < mMyAppointmentsDataModel.getAppointmentList().size(); i++) {
                AppointmentList clinicList = mMyAppointmentsDataModel.getAppointmentList().get(i);
                mPatientLists = mMyAppointmentsDataModel.getAppointmentList().get(i).getPatientList();
                if (!mPatientLists.isEmpty() && mPatientLists.size() > 0) {
                    clinicList.setPatientHeader(mPatientLists.get(0));
                    mAppointmentList.add(clinicList);
                }
            }
            if (!mAppointmentList.isEmpty()) {
                mAppointmentAdapter = new AppointmentAdapter(getActivity(), mAppointmentList, this);
                expandableListView.setAdapter(mAppointmentAdapter);
            } else {
                expandableListView.setVisibility(View.GONE);
                emptyListView.setVisibility(View.VISIBLE);
            }
        } else {
            expandableListView.setVisibility(View.GONE);
            emptyListView.setVisibility(View.VISIBLE);
        }

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                return true;
            }
        });

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (charString.equals("")) {
                    if(mAppointmentList.get(groupPosition).getPatientList().size()==1){
                        expandableListView.collapseGroup(groupPosition);
                    }
                    if (lastExpandedPosition != -1
                            && groupPosition != lastExpandedPosition) {
                        expandableListView.collapseGroup(lastExpandedPosition);
                    }
                    lastExpandedPosition = groupPosition;
                } else {
                    //CommonMethods.showToast(getActivity(), "all expand");
                }
            }
        });

        mBottomMenuAppointmentAdapter = new BottomMenuAppointmentAdapter(getContext(), this, mBottomMenuList, true);
        recyclerViewBottom.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerViewBottom.setAdapter(mBottomMenuAppointmentAdapter);
        searchEditText.addTextChangedListener(new EditTextWithDeleteButton.TextChangedListener() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (mAppointmentAdapter != null) {
                    charString = s.toString();
                    mAppointmentAdapter.getFilter().filter(s);
                }

            }
        });

    }

    public static MyAppointmentsFragment newInstance(Bundle b) {
        MyAppointmentsFragment fragment = new MyAppointmentsFragment();
        args = b;
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    public void expandAll() {
        int count = mAppointmentAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            expandableListView.expandGroup(i);
        }
    }

    public void collapseAll() {
        int count = mAppointmentAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            expandableListView.collapseGroup(i);
        }
    }

    @Override
    public void onPhoneNoClick(String patientPhone) {
        MyAppointmentsActivity activity = (MyAppointmentsActivity) getActivity();
        activity.callPatient(patientPhone);
    }


    @Override
    public void onDownArrowSetClick(int groupPosition, boolean isExpanded) {
        if (isExpanded) {
            expandableListView.collapseGroup(groupPosition);
        } else {
            //getActivity().setResult(COLLAPSED_REQUEST_CODE);
            expandableListView.expandGroup(groupPosition);
        }

    }

    @Override
    public void onLongPressOpenBottomMenu(boolean isLongPressed, int groupPosition) {
        if (isLongPressed) {
            recyclerViewBottom.setVisibility(View.VISIBLE);
        } else {
            for (int index = 0; index < mAppointmentAdapter.getGroupList().size(); index++) {
                for (AppointmentList clinicList : mAppointmentAdapter.getGroupList()) {
                    clinicList.setSelectedGroupCheckbox(false);
                    clinicList.getPatientHeader().setSelected(false);
                    for (int patientListIndex = 0; patientListIndex < mAppointmentAdapter.getGroupList().get(index).getPatientList().size(); patientListIndex++) {
                        mAppointmentAdapter.getGroupList().get(index).getPatientList().get(patientListIndex).setSelected(false);
                    }
                }
            }

            mAppointmentAdapter.notifyDataSetChanged();
            for (int i = 0; i < mBottomMenuAppointmentAdapter.getList().size(); i++) {
                mBottomMenuAppointmentAdapter.getList().get(i).setSelected(false);
            }
            mBottomMenuAppointmentAdapter.notifyDataSetChanged();

            recyclerViewBottom.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRecordFound(boolean isListEmpty) {
        if (isListEmpty) {
            emptyListView.setVisibility(View.VISIBLE);
        } else {
            emptyListView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCheckUncheckRemoveSelectAllSelection(boolean ischecked) {
        if (ischecked) {
            for (int i = 0; i < mBottomMenuAppointmentAdapter.getList().size(); i++) {
                if (mBottomMenuAppointmentAdapter.getList().get(i).getMenuName().equalsIgnoreCase(getString(R.string.select_all))) {
                    mBottomMenuAppointmentAdapter.getList().get(i).setSelected(false);
                }
            }
            mBottomMenuAppointmentAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClickOfPatientDetails(PatientList patientListObject, String patientDetails) {
        if (patientListObject.getSalutation() == 1) {
            patientName = getString(R.string.mr) + " " + toCamelCase(patientListObject.getPatientName());
        } else if (patientListObject.getSalutation() == 2) {
            patientName = getString(R.string.mrs) + " " + toCamelCase(patientListObject.getPatientName());
        } else if (patientListObject.getSalutation() == 3) {
            patientName = getString(R.string.miss) + " " + toCamelCase(patientListObject.getPatientName());
        } else if (patientListObject.getSalutation() == 4) {
            patientName = toCamelCase(patientListObject.getPatientName());
        }
        Bundle b = new Bundle();
        b.putString(RescribeConstants.PATIENT_NAME, patientName);
        b.putString(RescribeConstants.PATIENT_INFO, patientDetails);
        b.putString(RescribeConstants.PATIENT_ID, String.valueOf(patientListObject.getPatientId()));
        b.putString(RescribeConstants.PATIENT_HOS_PAT_ID, String.valueOf(patientListObject.getHospitalPatId()));
        Intent intent = new Intent(getActivity(), PatientHistoryActivity.class);
        intent.putExtra(RescribeConstants.PATIENT_INFO, b);
        startActivity(intent);
    }

    @Override
    public void onAppointmentClicked(Integer aptId, Integer patientId, int status, String type, int childPosition, int groupPosition) {
        childPos = childPosition;
        groupPos = groupPosition;
        isFromGroup = false;
        RequestAppointmentCancelModel mRequestAppointmentCancelModel = new RequestAppointmentCancelModel();
        mRequestAppointmentCancelModel.setAptId(aptId);
        mRequestAppointmentCancelModel.setPatientId(patientId);
        mRequestAppointmentCancelModel.setStatus(status);
        mRequestAppointmentCancelModel.setType(type);
        mAppointmentHelper.doAppointmentCancelOrComplete(mRequestAppointmentCancelModel);
    }

    @Override
    public void onAppointmentCancelled(Integer aptId, Integer patientId, int status, String type, int childPosition, int groupPosition) {
        childPos = childPosition;
        groupPos = groupPosition;
        isFromGroup = false;
        RequestAppointmentCancelModel mRequestAppointmentCancelModel = new RequestAppointmentCancelModel();
        mRequestAppointmentCancelModel.setAptId(aptId);
        mRequestAppointmentCancelModel.setPatientId(patientId);
        mRequestAppointmentCancelModel.setStatus(status);
        mRequestAppointmentCancelModel.setType(type);
        mAppointmentHelper.doAppointmentCancelOrComplete(mRequestAppointmentCancelModel);
    }

    @Override
    public void onGroupAppointmentClicked(Integer aptId, Integer patientId, int status, String type, int groupPosition) {
        childPos = 0;
        groupPos = groupPosition;
        isFromGroup = true;
        RequestAppointmentCancelModel mRequestAppointmentCancelModel = new RequestAppointmentCancelModel();
        mRequestAppointmentCancelModel.setAptId(aptId);
        mRequestAppointmentCancelModel.setPatientId(patientId);
        mRequestAppointmentCancelModel.setStatus(status);
        mRequestAppointmentCancelModel.setType(type);
        mAppointmentHelper.doAppointmentCancelOrComplete(mRequestAppointmentCancelModel);
    }

    @Override
    public void onGroupAppointmentCancelled(Integer aptId, Integer patientId, int status, String type, int groupPosition) {
        childPos = 0;
        groupPos = groupPosition;
        isFromGroup = true;
        RequestAppointmentCancelModel mRequestAppointmentCancelModel = new RequestAppointmentCancelModel();
        mRequestAppointmentCancelModel.setAptId(aptId);
        mRequestAppointmentCancelModel.setPatientId(patientId);
        mRequestAppointmentCancelModel.setStatus(status);
        mRequestAppointmentCancelModel.setType(type);
        mAppointmentHelper.doAppointmentCancelOrComplete(mRequestAppointmentCancelModel);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void setClickOnMenuItem(int position, BottomMenu bottomMenu) {
        if (bottomMenu.getMenuName().equalsIgnoreCase(getString(R.string.select_all))) {
            if (bottomMenu.isSelected()) {

                for (int index = 0; index < mAppointmentAdapter.getGroupList().size(); index++) {
                    for (AppointmentList clinicList : mAppointmentAdapter.getGroupList()) {
                        clinicList.setSelectedGroupCheckbox(true);
                        clinicList.getPatientHeader().setSelected(true);

                        for (int patientListIndex = 0; patientListIndex < mAppointmentAdapter.getGroupList().get(index).getPatientList().size(); patientListIndex++) {
                            mAppointmentAdapter.getGroupList().get(index).getPatientList().get(patientListIndex).setSelected(true);
                        }
                    }
                }
                mAppointmentAdapter.notifyDataSetChanged();

            } else {
                for (int index = 0; index < mAppointmentAdapter.getGroupList().size(); index++) {
                    for (AppointmentList clinicList : mAppointmentAdapter.getGroupList()) {
                        clinicList.setSelectedGroupCheckbox(false);
                        clinicList.getPatientHeader().setSelected(false);
                        for (int patientListIndex = 0; patientListIndex < mAppointmentAdapter.getGroupList().get(index).getPatientList().size(); patientListIndex++) {
                            mAppointmentAdapter.getGroupList().get(index).getPatientList().get(patientListIndex).setSelected(false);
                        }
                    }
                }
                mAppointmentAdapter.notifyDataSetChanged();
            }
            //Send Sms
        } else if (bottomMenu.getMenuName().equalsIgnoreCase(getString(R.string.send_sms))) {
            ArrayList<AppointmentList> mAppointmentLists = new ArrayList<>();

            for (int groupIndex = 0; groupIndex < mAppointmentAdapter.getGroupList().size(); groupIndex++) {
                ArrayList<PatientList> patientLists = new ArrayList<>();
                AppointmentList tempAppointmentListObject = null;
                try {
                    tempAppointmentListObject = (AppointmentList) mAppointmentAdapter.getGroupList().get(groupIndex).clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                for (int childIndex = 0; childIndex < mAppointmentAdapter.getGroupList().get(groupIndex).getPatientList().size(); childIndex++) {
                    PatientList patientList = mAppointmentAdapter.getGroupList().get(groupIndex).getPatientList().get(childIndex);
                    if (patientList.isSelected()) {
                        patientLists.add(patientList);
                    }
                }
                if (!patientLists.isEmpty()) {
                    tempAppointmentListObject.setPatientList(patientLists);
                    mAppointmentLists.add(tempAppointmentListObject);

                }
            }

            if (!mAppointmentLists.isEmpty()) {
                Intent intent = new Intent(getActivity(), TemplateListActivity.class);
                intent.putParcelableArrayListExtra(RescribeConstants.APPOINTMENT_LIST, mAppointmentLists);
                startActivity(intent);
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

            //Add to WaitingList
        } else if (bottomMenu.getMenuName().equalsIgnoreCase(getString(R.string.waiting_list))) {

            patientsListAddToWaitingLists = new ArrayList<>();
            for (int groupIndex = 0; groupIndex < mAppointmentAdapter.getGroupList().size(); groupIndex++) {
                ArrayList<PatientList> patientLists = new ArrayList<>();

                for (int childIndex = 0; childIndex < mAppointmentAdapter.getGroupList().get(groupIndex).getPatientList().size(); childIndex++) {
                    PatientList patientList = mAppointmentAdapter.getGroupList().get(groupIndex).getPatientList().get(childIndex);
                    if (patientList.isSelected()) {
                        patientLists.add(patientList);
                        PatientsListAddToWaitingList patientsAddToWaitingListObject = new PatientsListAddToWaitingList();
                        patientsAddToWaitingListObject.setHospitalPatId(String.valueOf(patientList.getHospitalPatId()));
                        patientsAddToWaitingListObject.setPatientId(String.valueOf(patientList.getPatientId()));
                        patientsAddToWaitingListObject.setPatientName(patientList.getPatientName());
                        patientsAddToWaitingListObject.setAppointmentStatusId(patientList.getAppointmentStatusId());
                        patientsAddToWaitingListObject.setAppointmentId(patientList.getAptId());
                        patientsListAddToWaitingLists.add(patientsAddToWaitingListObject);
                    }
                }
            }

            if (!patientsListAddToWaitingLists.isEmpty()) {
                mDoctorLocationModel = RescribeApplication.getDoctorLocationModels();
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

    private void showDialogToSelectLocation(ArrayList<DoctorLocationModel> mPatientListsOriginal) {
        final Dialog dialog = new Dialog(getActivity());

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_select_location_waiting_list_layout);
        dialog.setCancelable(true);

        LayoutInflater inflater = LayoutInflater.from(getContext());

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


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);
        dialog.setCanceledOnTouchOutside(true);
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


    public boolean callOnBackPressed() {
        return mAppointmentAdapter.isLongPressed;
    }

    public void removeCheckBox() {
        recyclerViewBottom.setVisibility(View.GONE);
        mAppointmentAdapter.setLongPressed(false);
        for (int index = 0; index < mAppointmentAdapter.getGroupList().size(); index++) {
            for (AppointmentList clinicList : mAppointmentAdapter.getGroupList()) {
                clinicList.setSelectedGroupCheckbox(false);
                clinicList.getPatientHeader().setSelected(false);
                for (int patientListIndex = 0; patientListIndex < mAppointmentAdapter.getGroupList().get(index).getPatientList().size(); patientListIndex++) {
                    mAppointmentAdapter.getGroupList().get(index).getPatientList().get(patientListIndex).setSelected(false);
                }
            }
        }
        mAppointmentAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.rightFab, R.id.leftFab})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rightFab:
                MyAppointmentsActivity activity = (MyAppointmentsActivity) getActivity();
                activity.getActivityDrawerLayout().openDrawer(GravityCompat.END);
                break;
            case R.id.leftFab:
                break;
        }
    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_ADD_TO_WAITING_LIST)) {
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
        } else if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_APPOINTMENT_CANCEL_OR_COMPLETE)) {
            TemplateBaseModel templateBaseModel = (TemplateBaseModel) customResponse;
            if (templateBaseModel.getCommon().isSuccess()) {
                Toast.makeText(getActivity(), templateBaseModel.getCommon().getStatusMessage() + "", Toast.LENGTH_SHORT).show();
                mAppointmentListOfAdapter = mAppointmentAdapter.getGroupList();
                if (isFromGroup) {
                    if (templateBaseModel.getCommon().getStatusMessage().equalsIgnoreCase(getString(R.string.appointment_completed))) {
                        mAppointmentListOfAdapter.get(groupPos).getPatientHeader().setAppointmentStatus("Completed");
                        mAppointmentListOfAdapter.get(groupPos).getPatientList().get(0).setAppointmentStatus("Completed");
                    } else if (templateBaseModel.getCommon().getStatusMessage().equalsIgnoreCase(getString(R.string.appointment_cancelled))) {
                        mAppointmentListOfAdapter.get(groupPos).getPatientHeader().setAppointmentStatus("Cancelled");
                        mAppointmentListOfAdapter.get(groupPos).getPatientList().get(0).setAppointmentStatus("Cancelled");
                    }

                } else {
                    if (templateBaseModel.getCommon().getStatusMessage().equalsIgnoreCase(getString(R.string.appointment_completed))) {
                        mAppointmentListOfAdapter.get(groupPos).getPatientList().get(childPos).setAppointmentStatus("Completed");
                        if (childPos == 0) {
                            mAppointmentListOfAdapter.get(groupPos).getPatientHeader().setAppointmentStatus("Completed");

                        }
                    } else if (templateBaseModel.getCommon().getStatusMessage().equalsIgnoreCase(getString(R.string.appointment_cancelled))) {
                        mAppointmentListOfAdapter.get(groupPos).getPatientList().get(childPos).setAppointmentStatus("Cancelled");
                        if (childPos == 0) {
                            mAppointmentListOfAdapter.get(groupPos).getPatientHeader().setAppointmentStatus("Cancelled");

                        }
                    }
                }

                mAppointmentAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), templateBaseModel.getCommon().getStatusMessage() + "", Toast.LENGTH_SHORT).show();

            }

        }
    }

    @Override
    public void onParseError(String mOldDataTag, String errorMessage) {
        CommonMethods.showToast(getActivity(), errorMessage);

    }

    @Override
    public void onServerError(String mOldDataTag, String serverErrorMessage) {
        CommonMethods.showToast(getActivity(), serverErrorMessage);
    }

    @Override
    public void onNoConnectionError(String mOldDataTag, String serverErrorMessage) {
        CommonMethods.showToast(getActivity(), serverErrorMessage);
    }
}
