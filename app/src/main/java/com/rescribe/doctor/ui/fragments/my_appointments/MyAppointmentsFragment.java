package com.rescribe.doctor.ui.fragments.my_appointments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.my_appointments.AppointmentAdapter;
import com.rescribe.doctor.adapters.my_appointments.BottomMenuAppointmentAdapter;
import com.rescribe.doctor.adapters.waiting_list.ShowWaitingStatusAdapter;
import com.rescribe.doctor.bottom_menus.BottomMenu;
import com.rescribe.doctor.helpers.myappointments.AppointmentHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.my_appointments.AppointmentList;
import com.rescribe.doctor.model.my_appointments.MyAppointmentsDataModel;
import com.rescribe.doctor.model.my_appointments.PatientList;
import com.rescribe.doctor.model.my_appointments.RequestAppointmentDeleteModel;
import com.rescribe.doctor.model.my_appointments.cancel_appointment_bulk.CancelAppointmentBulkResponseBaseModel;
import com.rescribe.doctor.model.my_appointments.cancel_appointment_bulk.CancelAppointmentList;
import com.rescribe.doctor.model.my_appointments.cancel_appointment_bulk.CancelAppointmentPatientDetail;
import com.rescribe.doctor.model.my_appointments.request_cancel_or_complete_appointment.RequestAppointmentCancelModel;
import com.rescribe.doctor.model.patient.template_sms.TemplateBaseModel;
import com.rescribe.doctor.model.patient.template_sms.TemplateList;
import com.rescribe.doctor.model.patient.template_sms.request_send_sms.ClinicListForSms;
import com.rescribe.doctor.model.patient.template_sms.request_send_sms.PatientInfoList;
import com.rescribe.doctor.model.waiting_list.new_request_add_to_waiting_list.AddToList;
import com.rescribe.doctor.model.waiting_list.new_request_add_to_waiting_list.PatientAddToWaitingList;
import com.rescribe.doctor.model.waiting_list.new_request_add_to_waiting_list.RequestToAddWaitingList;
import com.rescribe.doctor.model.waiting_list.response_add_to_waiting_list.AddToWaitingListBaseModel;
import com.rescribe.doctor.model.waiting_list.response_add_to_waiting_list.AddToWaitingResponse;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.ui.activities.book_appointment.SelectSlotToBookAppointmentBaseActivity;
import com.rescribe.doctor.ui.activities.my_appointments.MyAppointmentsActivity;
import com.rescribe.doctor.ui.activities.my_patients.SendSmsActivity;
import com.rescribe.doctor.ui.activities.my_patients.ShowMyPatientsListActivity;
import com.rescribe.doctor.ui.activities.my_patients.TemplateListActivity;
import com.rescribe.doctor.ui.activities.my_patients.patient_history.PatientHistoryActivity;
import com.rescribe.doctor.ui.activities.waiting_list.WaitingMainListActivity;
import com.rescribe.doctor.ui.customesViews.EditTextWithDeleteButton;
import com.rescribe.doctor.ui.fragments.book_appointment.CoachFragment;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.rescribe.doctor.ui.activities.my_patients.SendSmsActivity.RESULT_SMS_SEND;
import static com.rescribe.doctor.ui.activities.waiting_list.WaitingMainListActivity.RESULT_CLOSE_ACTIVITY_WAITING_LIST;
import static com.rescribe.doctor.util.CommonMethods.toCamelCase;
import static com.rescribe.doctor.util.RescribeConstants.APPOINTMENT_DATA;
import static com.rescribe.doctor.util.RescribeConstants.APPOINTMENT_STATUS.BOOKED;
import static com.rescribe.doctor.util.RescribeConstants.APPOINTMENT_STATUS.CANCEL;
import static com.rescribe.doctor.util.RescribeConstants.APPOINTMENT_STATUS.COMPLETED;
import static com.rescribe.doctor.util.RescribeConstants.APPOINTMENT_STATUS.CONFIRM;


/**
 * Created by jeetal on 31/1/18.
 */

public class MyAppointmentsFragment extends Fragment implements AppointmentAdapter.OnDownArrowClicked, BottomMenuAppointmentAdapter.OnMenuBottomItemClickListener, HelperResponse {


    public static boolean isLongPressed;
    @BindView(R.id.searchEditText)
    EditTextWithDeleteButton searchEditText;
    @BindView(R.id.whiteUnderLine)
    ImageView whiteUnderLine;
    @BindView(R.id.historyExpandableListView)
    ExpandableListView expandableListView;
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
    @BindView(R.id.leftFabForAppointment)
    FloatingActionButton leftFabForAppointment;



    private AppointmentAdapter mAppointmentAdapter;
    private BottomMenuAppointmentAdapter mBottomMenuAppointmentAdapter;
    private String[] mMenuNames = {"Select All", "Send SMS", "Waiting List", "Bulk Cancel"};
    private int lastExpandedPosition = -1;
    private String charString = "";
    private AppointmentHelper mAppointmentHelper;
    private int childPos;
    private int groupPos;
    private boolean isFromGroup;
    private ArrayList<AppointmentList> mAppointmentLists;
    private ArrayList<AddToList> addToArrayList;
    private String mUserSelectedDate = "";
    private ArrayList<PatientList> tempSelectedList = new ArrayList<>();

    public static MyAppointmentsFragment newInstance(MyAppointmentsDataModel myAppointmentsDataModel, String mDateSelectedByUser) {
        MyAppointmentsFragment myAppointmentsFragment = new MyAppointmentsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(RescribeConstants.DATE, mDateSelectedByUser);
        bundle.putParcelable(APPOINTMENT_DATA, myAppointmentsDataModel);
        myAppointmentsFragment.setArguments(bundle);
        return myAppointmentsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.my_appointments_layout, container, false);
        unbinder = ButterKnife.bind(this, mRootView);
        init();
        return mRootView;
    }

    private void init() {
        mAppointmentHelper = new AppointmentHelper(getActivity(), this);

        leftFabForAppointment.setVisibility(View.VISIBLE);

        ArrayList<BottomMenu> mBottomMenuList = new ArrayList<>();

        for (String mMenuName : mMenuNames) {
            BottomMenu bottomMenu = new BottomMenu();
            bottomMenu.setMenuName(mMenuName);
            mBottomMenuList.add(bottomMenu);
        }

        expandableListView.setPadding(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.dp67));
        expandableListView.setClipToPadding(false);
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

        mBottomMenuAppointmentAdapter = new BottomMenuAppointmentAdapter(getContext(), this, mBottomMenuList, true, RescribeConstants.NOT_FROM_COMPLETE_OPD);
        recyclerViewBottom.setLayoutManager(new GridLayoutManager(getActivity(), 4));
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
                if (mAppointmentAdapter != null && !charString.equals(s.toString())) {
                    charString = s.toString();
                    mAppointmentAdapter.getFilter().filter(s);
                    expandableListView.setVisibility(View.VISIBLE);
                }
            }
        });

        mUserSelectedDate = getArguments().getString(RescribeConstants.DATE);
        MyAppointmentsDataModel myAppointmentsDataModel = getArguments().getParcelable(APPOINTMENT_DATA);
        setFilteredData(myAppointmentsDataModel);
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
    public void onLongPressOpenBottomMenu(int groupPosition) {
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
        selectAllButton(!ischecked);
        int selectedCount = 0;
        for (AppointmentList appointmentList : mAppointmentAdapter.getGroupList()) {
            if (appointmentList.isSelectedGroupCheckbox())
                selectedCount += 1;
        }
        selectAllButton(mAppointmentAdapter.getGroupCount() == selectedCount);
    }

    private void selectAllButton(boolean isEnable) {
        for (int i = 0; i < mBottomMenuAppointmentAdapter.getList().size(); i++) {
            if (mBottomMenuAppointmentAdapter.getList().get(i).getMenuName().equalsIgnoreCase(getString(R.string.select_all))) {
                mBottomMenuAppointmentAdapter.getList().get(i).setSelected(isEnable);
            }
        }
        mBottomMenuAppointmentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClickOfPatientDetails(PatientList patientListObject, int clinicId, String patientDetails) {

        String patientName;
        if (patientListObject.getSalutation() != 0)
            patientName = RescribeConstants.SALUTATION[patientListObject.getSalutation() - 1] + toCamelCase(patientListObject.getPatientName());
        else patientName = toCamelCase(patientListObject.getPatientName());
        Bundle b = new Bundle();
        b.putString(RescribeConstants.PATIENT_NAME, patientName);
        b.putString(RescribeConstants.PATIENT_INFO, patientDetails);
        b.putInt(RescribeConstants.CLINIC_ID, clinicId);
        b.putInt(RescribeConstants.APPOINTMENT_ID, patientListObject.getAptId());
        b.putString(RescribeConstants.PATIENT_ID, String.valueOf(patientListObject.getPatientId()));
        b.putString(RescribeConstants.PATIENT_HOS_PAT_ID, String.valueOf(patientListObject.getHospitalPatId()));
        Intent intent = new Intent(getActivity(), PatientHistoryActivity.class);
        intent.putExtra(RescribeConstants.PATIENT_INFO, b);
        startActivity(intent);
    }

    @Override
    public void onAppointmentClicked(Integer aptId, Integer patientId, int status, String type, int childPosition, int groupPosition) {
        if (mUserSelectedDate.equals(CommonMethods.getCurrentDate(RescribeConstants.DATE_PATTERN.YYYY_MM_DD))) {
            childPos = childPosition;
            groupPos = groupPosition;
            isFromGroup = false;
            RequestAppointmentCancelModel mRequestAppointmentCancelModel = new RequestAppointmentCancelModel();
            mRequestAppointmentCancelModel.setAptId(aptId);
            mRequestAppointmentCancelModel.setPatientId(patientId);
            mRequestAppointmentCancelModel.setStatus(status);
            mRequestAppointmentCancelModel.setType(type);
            mAppointmentHelper.doAppointmentCancelOrComplete(mRequestAppointmentCancelModel);
        } else {
            Toast.makeText(getContext(), getString(R.string.cannot_completed_appointment), Toast.LENGTH_SHORT).show();
        }
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
        if (mUserSelectedDate.equals(CommonMethods.getCurrentDate(RescribeConstants.DATE_PATTERN.YYYY_MM_DD))) {
            childPos = 0;
            groupPos = groupPosition;
            isFromGroup = true;
            RequestAppointmentCancelModel mRequestAppointmentCancelModel = new RequestAppointmentCancelModel();
            mRequestAppointmentCancelModel.setAptId(aptId);
            mRequestAppointmentCancelModel.setPatientId(patientId);
            mRequestAppointmentCancelModel.setStatus(status);
            mRequestAppointmentCancelModel.setType(type);
            mAppointmentHelper.doAppointmentCancelOrComplete(mRequestAppointmentCancelModel);
        } else {
            Toast.makeText(getContext(), getString(R.string.cannot_completed_appointment), Toast.LENGTH_SHORT).show();
        }
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
    public void onGroupAppointmentDelete(Integer aptId, Integer patientId, int groupPosition) {
        childPos = 0;
        groupPos = groupPosition;
        RequestAppointmentDeleteModel requestAppointmentDeleteModel = new RequestAppointmentDeleteModel();
        requestAppointmentDeleteModel.setAptId(aptId);
        requestAppointmentDeleteModel.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, getActivity())));
        // mAppointmentHelper.doAppointmentDelete(requestAppointmentDeleteModel);
        deleteCancelAppointmentDialog(requestAppointmentDeleteModel,new CancelAppointmentList(),true);

    }

    @Override
    public void onAppointmentDelete(Integer aptId, Integer patientId, int childPosition, int groupPosition) {
        childPos = childPosition;
        groupPos = groupPosition;
        RequestAppointmentDeleteModel requestAppointmentDeleteModel = new RequestAppointmentDeleteModel();
        requestAppointmentDeleteModel.setAptId(aptId);
        requestAppointmentDeleteModel.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, getActivity())));

        deleteCancelAppointmentDialog(requestAppointmentDeleteModel, new CancelAppointmentList(), true);

    }

    private void deleteCancelAppointmentDialog(final RequestAppointmentDeleteModel requestAppointmentDeleteModel, final CancelAppointmentList cancelAppointmentList, final boolean isDelete) {

        final Dialog dialog = new Dialog(getActivity());

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_exit);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        ((TextView) dialog.findViewById(R.id.textview_sucess)).setText("Do you want to "+(isDelete ? "delete this appointment? ": "cancel selected appointments?"));
        ((Button) dialog.findViewById(R.id.button_ok)).setText("Yes");
        ((Button) dialog.findViewById(R.id.button_cancel)).setText("No");

        dialog.findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDelete)
                    mAppointmentHelper.doAppointmentDelete(requestAppointmentDeleteModel);
                else
                    mAppointmentHelper.cancelAppointmentBulk(cancelAppointmentList);
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        dialog.show();

    }

    @Override
    public void onAppointmentReshedule(PatientList patientList, String text, String cityName, String areaName) {
        Intent intent = new Intent(getActivity(), SelectSlotToBookAppointmentBaseActivity.class);
        com.rescribe.doctor.model.patient.doctor_patients.PatientList patientListforBookAppointment = new com.rescribe.doctor.model.patient.doctor_patients.PatientList();
        patientListforBookAppointment.setPatientName(patientList.getPatientName());
        patientListforBookAppointment.setPatientId(patientList.getPatientId());
        patientListforBookAppointment.setPatientCity(cityName);
        patientListforBookAppointment.setPatientArea(areaName);
        patientListforBookAppointment.setSalutation(patientList.getSalutation());
        patientListforBookAppointment.setPatientImageUrl(patientList.getPatientImageUrl());
        patientListforBookAppointment.setAptId(patientList.getAptId());
        intent.putExtra(RescribeConstants.PATIENT_INFO, patientListforBookAppointment);
        intent.putExtra(RescribeConstants.PATIENT_DETAILS, text);
        intent.putExtra(RescribeConstants.IS_APPOINTMENT_TYPE_RESHEDULE, true);
        startActivityForResult(intent, Activity.RESULT_OK);

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
            mAppointmentLists = new ArrayList<>();

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
                mAppointmentHelper.doGetDoctorTemplate();

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
            if (mUserSelectedDate.equals(CommonMethods.getCurrentDate(RescribeConstants.DATE_PATTERN.YYYY_MM_DD))) {
                addToArrayList = new ArrayList<>();
                ArrayList<AddToList> addToArrayListForSelectedCount = new ArrayList<>();
                for (int groupIndex = 0; groupIndex < mAppointmentAdapter.getGroupList().size(); groupIndex++) {
                    ArrayList<PatientAddToWaitingList> mPatientAddToWaitingList = new ArrayList<>();
                    ArrayList<PatientAddToWaitingList> mPatientListForCountOfPatientsSelected = new ArrayList<>();
                    for (int childIndex = 0; childIndex < mAppointmentAdapter.getGroupList().get(groupIndex).getPatientList().size(); childIndex++) {
                        PatientList patientList = mAppointmentAdapter.getGroupList().get(groupIndex).getPatientList().get(childIndex);
                        if (patientList.isSelected()) {
                            if (!patientList.getAppointmentStatusId().equals(COMPLETED) && !patientList.getAppointmentStatusId().equals(CANCEL)) {
                                PatientAddToWaitingList patientsAddToWaitingListObject = new PatientAddToWaitingList();
                                patientsAddToWaitingListObject.setHospitalPatId(String.valueOf(patientList.getHospitalPatId()));
                                patientsAddToWaitingListObject.setAppointmentId(patientList.getAptId());
                                patientsAddToWaitingListObject.setAppointmentStatusId(patientList.getAppointmentStatusId());
                                patientsAddToWaitingListObject.setPatientId(String.valueOf(patientList.getPatientId()));
                                patientsAddToWaitingListObject.setPatientName(patientList.getPatientName());
                                patientsAddToWaitingListObject.setCategoryId(patientList.getCategoryId());
                                patientsAddToWaitingListObject.setCalendarType(patientList.getCalendarType());
                                mPatientAddToWaitingList.add(patientsAddToWaitingListObject);
                            }
                            //two different list are created to manage selected patients and patients which cannot be added in waiting list
                            //Note : Patients of completed and cancelled cannot be added to waiting list
                            PatientAddToWaitingList patientsAddToWaitingListObject = new PatientAddToWaitingList();
                            patientsAddToWaitingListObject.setAppointmentId(patientList.getAptId());
                            patientsAddToWaitingListObject.setAppointmentStatusId(patientList.getAppointmentStatusId());
                            patientsAddToWaitingListObject.setHospitalPatId(String.valueOf(patientList.getHospitalPatId()));
                            patientsAddToWaitingListObject.setPatientId(String.valueOf(patientList.getPatientId()));
                            patientsAddToWaitingListObject.setPatientName(patientList.getPatientName());
                            patientsAddToWaitingListObject.setCategoryId(patientList.getCategoryId());
                            patientsAddToWaitingListObject.setCalendarType(patientList.getCalendarType());
                            mPatientListForCountOfPatientsSelected.add(patientsAddToWaitingListObject);
                        }
                    }
                    if (!mPatientListForCountOfPatientsSelected.isEmpty()) {
                        AddToList addToListObject = new AddToList();
                        addToListObject.setLocationDetails(mAppointmentAdapter.getGroupList().get(groupIndex).getClinicName() + ", " + mAppointmentAdapter.getGroupList().get(groupIndex).getArea() + ", " + mAppointmentAdapter.getGroupList().get(groupIndex).getCity());
                        addToListObject.setLocationId(mAppointmentAdapter.getGroupList().get(groupIndex).getLocationId());
                        addToListObject.setPatientAddToWaitingList(mPatientAddToWaitingList);
                        addToArrayListForSelectedCount.add(addToListObject);
                    }
                    if (!mPatientAddToWaitingList.isEmpty()) {
                        AddToList addToListObject = new AddToList();
                        addToListObject.setLocationDetails(mAppointmentAdapter.getGroupList().get(groupIndex).getClinicName() + ", " + mAppointmentAdapter.getGroupList().get(groupIndex).getArea() + ", " + mAppointmentAdapter.getGroupList().get(groupIndex).getCity());
                        addToListObject.setLocationId(mAppointmentAdapter.getGroupList().get(groupIndex).getLocationId());
                        addToListObject.setPatientAddToWaitingList(mPatientAddToWaitingList);
                        addToArrayList.add(addToListObject);
                    }
                }
                if (!addToArrayListForSelectedCount.isEmpty()) {
                    if (!addToArrayList.isEmpty()) {
                        callWaitingListApi();
                        for (int i = 0; i < mBottomMenuAppointmentAdapter.getList().size(); i++) {
                            if (mBottomMenuAppointmentAdapter.getList().get(i).getMenuName().equalsIgnoreCase(getString(R.string.waiting_list))) {
                                mBottomMenuAppointmentAdapter.getList().get(i).setSelected(false);
                            }
                        }
                        mBottomMenuAppointmentAdapter.notifyDataSetChanged();
                    } else {
                        CommonMethods.showToast(getActivity(), getString(R.string.complete_and_cancelled_status));
                        for (int i = 0; i < mBottomMenuAppointmentAdapter.getList().size(); i++) {
                            if (mBottomMenuAppointmentAdapter.getList().get(i).getMenuName().equalsIgnoreCase(getString(R.string.waiting_list))) {
                                mBottomMenuAppointmentAdapter.getList().get(i).setSelected(false);
                            }
                        }
                        mBottomMenuAppointmentAdapter.notifyDataSetChanged();
                    }
                } else {
                    CommonMethods.showToast(getActivity(), getString(R.string.please_select_patients));
                    for (int i = 0; i < mBottomMenuAppointmentAdapter.getList().size(); i++) {
                        if (mBottomMenuAppointmentAdapter.getList().get(i).getMenuName().equalsIgnoreCase(getString(R.string.waiting_list))) {
                            mBottomMenuAppointmentAdapter.getList().get(i).setSelected(false);
                        }
                    }
                    mBottomMenuAppointmentAdapter.notifyDataSetChanged();
                }
            } else {
                for (int i = 0; i < mBottomMenuAppointmentAdapter.getList().size(); i++) {
                    if (mBottomMenuAppointmentAdapter.getList().get(i).getMenuName().equalsIgnoreCase(getString(R.string.waiting_list))) {
                        mBottomMenuAppointmentAdapter.getList().get(i).setSelected(false);
                    }
                }
                mBottomMenuAppointmentAdapter.notifyDataSetChanged();
                Toast.makeText(getContext(), getString(R.string.you_cannot_add_waiting_list), Toast.LENGTH_SHORT).show();
            }
        } else if (bottomMenu.getMenuName().equalsIgnoreCase(getString(R.string.bulk_cancel))) {
            tempSelectedList.clear();
            CancelAppointmentList cancelAppointmentList = new CancelAppointmentList();
            cancelAppointmentList.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, getActivity())));
            ArrayList<CancelAppointmentPatientDetail> cancelAppointmentPatientDetailList = new ArrayList<>();
            for (int groupIndex = 0; groupIndex < mAppointmentAdapter.getGroupList().size(); groupIndex++) {
                for (int childIndex = 0; childIndex < mAppointmentAdapter.getGroupList().get(groupIndex).getPatientList().size(); childIndex++) {
                    PatientList patientList = mAppointmentAdapter.getGroupList().get(groupIndex).getPatientList().get(childIndex);
                    if (patientList.isSelected()) {
                        if (patientList.getAppointmentStatusId().equals(BOOKED) || patientList.getAppointmentStatusId().equals(CONFIRM)) {
                            tempSelectedList.add(patientList);

                            CancelAppointmentPatientDetail cancelAppointmentPatientDetail = new CancelAppointmentPatientDetail();
                            cancelAppointmentPatientDetail.setPatientId(patientList.getPatientId());
                            cancelAppointmentPatientDetail.setAptId(patientList.getAptId());
                            cancelAppointmentPatientDetail.setAppointmentDate(patientList.getAppointmentDate());
                            cancelAppointmentPatientDetail.setAppointmentTime(patientList.getAppointmentTime());
                            cancelAppointmentPatientDetail.setLocationId(mAppointmentAdapter.getGroupList().get(groupIndex).getLocationId());
                            cancelAppointmentPatientDetailList.add(cancelAppointmentPatientDetail);
                        }
                    }
                }
            }
            cancelAppointmentList.setPatientDetails(cancelAppointmentPatientDetailList);
            if (!cancelAppointmentList.getPatientDetails().isEmpty()) {

                deleteCancelAppointmentDialog(new RequestAppointmentDeleteModel(), cancelAppointmentList, false);
//                mAppointmentHelper.cancelAppointmentBulk(cancelAppointmentList);
            } else {
                CommonMethods.showToast(getActivity(), getString(R.string.please_select_book_confirm_patients));
            }
        }
    }


    private void callWaitingListApi() {
        RequestToAddWaitingList requestForWaitingListPatients = new RequestToAddWaitingList();
        requestForWaitingListPatients.setAddToList(addToArrayList);
        requestForWaitingListPatients.setTime(CommonMethods.getCurrentTimeStamp(RescribeConstants.DATE_PATTERN.HH_mm_ss));
        requestForWaitingListPatients.setDate(CommonMethods.getCurrentDate(RescribeConstants.DATE_PATTERN.YYYY_MM_DD));
        requestForWaitingListPatients.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, getActivity())));
        mAppointmentHelper.doAddToWaitingListFromMyPatients(requestForWaitingListPatients);
    }

    public void removeCheckBox() {
        recyclerViewBottom.setVisibility(View.GONE);
        isLongPressed = false;
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

    @OnClick({R.id.rightFab, R.id.leftFabForAppointment})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rightFab:
                MyAppointmentsActivity activity = (MyAppointmentsActivity) getActivity();
                activity.getActivityDrawerLayout().openDrawer(GravityCompat.END);
                break;
            case R.id.leftFabForAppointment:
                isLongPressed = false;
                Intent intent = new Intent(getActivity(), ShowMyPatientsListActivity.class);
                intent.putExtra(RescribeConstants.ACTIVITY_LAUNCHED_FROM, RescribeConstants.MY_APPOINTMENTS);
                startActivityForResult(intent, Activity.RESULT_OK);
                break;
        }
    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_ADD_TO_WAITING_LIST)) {
            AddToWaitingListBaseModel mAddToWaitingListBaseModel = (AddToWaitingListBaseModel) customResponse;
            if (mAddToWaitingListBaseModel.getCommon().isSuccess()) {
                showDialogForWaitingStatus(mAddToWaitingListBaseModel.getAddToWaitingModel().getAddToWaitingResponse());
            }
        } else if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_APPOINTMENT_CANCEL_OR_COMPLETE)) {
            TemplateBaseModel templateBaseModel = (TemplateBaseModel) customResponse;
            if (templateBaseModel.getCommon().isSuccess()) {
                Toast.makeText(getActivity(), templateBaseModel.getCommon().getStatusMessage() + "", Toast.LENGTH_SHORT).show();
                ArrayList<AppointmentList> mAppointmentListOfAdapter = mAppointmentAdapter.getGroupList();
                if (isFromGroup) {
                    if (templateBaseModel.getCommon().getStatusMessage().equalsIgnoreCase(getString(R.string.appointment_completed))) {
                        mAppointmentListOfAdapter.get(groupPos).getPatientHeader().setAppointmentStatus("Completed");
                        mAppointmentListOfAdapter.get(groupPos).getPatientList().get(0).setAppointmentStatus("Completed");
                        mAppointmentListOfAdapter.get(groupPos).getPatientList().get(0).setAddedToWaiting(0);
                        mAppointmentListOfAdapter.get(groupPos).getPatientList().get(0).setAppointmentStatusId(COMPLETED);
                    } else if (templateBaseModel.getCommon().getStatusMessage().equalsIgnoreCase(getString(R.string.appointment_cancelled))) {
                        mAppointmentListOfAdapter.get(groupPos).getPatientHeader().setAppointmentStatus("Cancelled");
                        mAppointmentListOfAdapter.get(groupPos).getPatientList().get(0).setAppointmentStatus("Cancelled");
                        mAppointmentListOfAdapter.get(groupPos).getPatientList().get(0).setAddedToWaiting(0);
                        mAppointmentListOfAdapter.get(groupPos).getPatientList().get(0).setAppointmentStatusId(CANCEL);
                    }

                } else {
                    if (templateBaseModel.getCommon().getStatusMessage().equalsIgnoreCase(getString(R.string.appointment_completed))) {
                        mAppointmentListOfAdapter.get(groupPos).getPatientList().get(childPos).setAppointmentStatus("Completed");
                        mAppointmentListOfAdapter.get(groupPos).getPatientList().get(childPos).setAddedToWaiting(0);
                        mAppointmentListOfAdapter.get(groupPos).getPatientList().get(childPos).setAppointmentStatusId(COMPLETED);
                        if (childPos == 0) {
                            mAppointmentListOfAdapter.get(groupPos).getPatientHeader().setAppointmentStatus("Completed");
                            mAppointmentListOfAdapter.get(groupPos).getPatientHeader().setAddedToWaiting(0);
                            mAppointmentListOfAdapter.get(groupPos).getPatientHeader().setAppointmentStatusId(COMPLETED);

                        }
                    } else if (templateBaseModel.getCommon().getStatusMessage().equalsIgnoreCase(getString(R.string.appointment_cancelled))) {
                        mAppointmentListOfAdapter.get(groupPos).getPatientList().get(childPos).setAppointmentStatus("Cancelled");
                        mAppointmentListOfAdapter.get(groupPos).getPatientList().get(childPos).setAddedToWaiting(0);
                        mAppointmentListOfAdapter.get(groupPos).getPatientList().get(childPos).setAppointmentStatusId(CANCEL);
                        if (childPos == 0) {
                            mAppointmentListOfAdapter.get(groupPos).getPatientHeader().setAppointmentStatus("Cancelled");
                            mAppointmentListOfAdapter.get(groupPos).getPatientHeader().setAddedToWaiting(0);
                            mAppointmentListOfAdapter.get(groupPos).getPatientHeader().setAppointmentStatusId(CANCEL);
                        }
                    }
                }

                mAppointmentAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), templateBaseModel.getCommon().getStatusMessage() + "", Toast.LENGTH_SHORT).show();
            }

        } else if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_GET_DOCTOR_SMS_TEMPLATE)) {
            removeCheckBox();
            TemplateBaseModel templateBaseModel = (TemplateBaseModel) customResponse;
            if (templateBaseModel.getTemplateDataModel() != null) {
                ArrayList<TemplateList> templateLists = templateBaseModel.getTemplateDataModel().getTemplateList();
                //   if (!templateLists.isEmpty()) {
                Intent intent = new Intent(getActivity(), TemplateListActivity.class);
                intent.putParcelableArrayListExtra(RescribeConstants.APPOINTMENT_LIST, mAppointmentLists);
                intent.putParcelableArrayListExtra(RescribeConstants.TEMPLATE_LIST, templateLists);
                startActivity(intent);
//                } else {
//                    //  showSendSmsDialog(clinicListForSms);
//
//                    ArrayList<PatientList> patientListsToShowOnSmsScreen = new ArrayList<>();
//                    for (AppointmentList appointmentList : mAppointmentLists) {
//                        patientListsToShowOnSmsScreen.addAll(appointmentList.getPatientList());
//                    }
//
//                    ArrayList<ClinicListForSms> clinicListForSms = new ArrayList<>();
//                    for (AppointmentList appointmentList : mAppointmentLists) {
//                        ClinicListForSms listForSms = new ClinicListForSms();
//                        ArrayList<PatientInfoList> patientInfoLists = new ArrayList<>();
//                        listForSms.setClinicId(appointmentList.getClinicId());
//                        listForSms.setClinicName(appointmentList.getClinicName());
//                        listForSms.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, getContext())));
//                        listForSms.setLocationId(appointmentList.getLocationId());
//                        listForSms.setTemplateContent("");
//                        for (PatientList patientList : appointmentList.getPatientList()) {
//                            PatientInfoList patientInfoList = new PatientInfoList();
//                            patientInfoList.setHospitalPatId(patientList.getHospitalPatId());
//                            patientInfoList.setPatientId(patientList.getPatientId());
//                            patientInfoList.setPatientPhone(patientList.getPatientPhone());
//                            patientInfoList.setPatientName(patientList.getPatientName());
//                            patientInfoLists.add(patientInfoList);
//                        }
//                        listForSms.setPatientInfoList(patientInfoLists);
//                        clinicListForSms.add(listForSms);
//                    }
//
//                    Intent intent = new Intent(getActivity(), SendSmsActivity.class);
//                    intent.putExtra(RescribeConstants.SMS_DETAIL_LIST, clinicListForSms);
//                    intent.putExtra(RescribeConstants.SMS_PATIENT_LIST_TO_SHOW, patientListsToShowOnSmsScreen);
//                    startActivityForResult(intent, RESULT_SMS_SEND);
//                }
            } else {
                ArrayList<PatientList> patientListsToShowOnSmsScreen = new ArrayList<>();
                ArrayList<ClinicListForSms> clinicListForSms = new ArrayList<>();
                for (AppointmentList appointmentList : mAppointmentLists) {
                    patientListsToShowOnSmsScreen.addAll(appointmentList.getPatientList());
                }

                for (AppointmentList appointmentList : mAppointmentLists) {
                    ClinicListForSms listForSms = new ClinicListForSms();
                    ArrayList<PatientInfoList> patientInfoLists = new ArrayList<>();
                    listForSms.setClinicId(appointmentList.getClinicId());
                    listForSms.setClinicName(appointmentList.getClinicName());
                    listForSms.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, getActivity())));
                    listForSms.setLocationId(appointmentList.getLocationId());
                    listForSms.setTemplateContent("");
                    for (PatientList patientList : appointmentList.getPatientList()) {
                        PatientInfoList patientInfoList = new PatientInfoList();
                        patientInfoList.setHospitalPatId(patientList.getHospitalPatId());
                        patientInfoList.setPatientId(patientList.getPatientId());
                        patientInfoList.setPatientPhone(patientList.getPatientPhone());
                        patientInfoList.setPatientName(patientList.getPatientName());
                        patientInfoList.setPatBloodGroup(patientList.getBloodGroup());
                        patientInfoList.setPatientGender(patientList.getGender());
                        patientInfoList.setOutstandingAmt(patientList.getOutStandingAmount());
                        patientInfoLists.add(patientInfoList);
                    }
                    listForSms.setPatientInfoList(patientInfoLists);
                    clinicListForSms.add(listForSms);

                }
                //  showSendSmsDialog(clinicListForSms);
                Intent intent = new Intent(getActivity(), SendSmsActivity.class);
                intent.putExtra(RescribeConstants.SMS_DETAIL_LIST, clinicListForSms);
                intent.putExtra(RescribeConstants.SMS_PATIENT_LIST_TO_SHOW, patientListsToShowOnSmsScreen);
                startActivityForResult(intent, RESULT_SMS_SEND);
            }

        } else if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_CANCEL_APPOINTMENT_BULK)) {
            CancelAppointmentBulkResponseBaseModel cancelAppointmentBulkResponseBaseModel = (CancelAppointmentBulkResponseBaseModel) customResponse;
            if (cancelAppointmentBulkResponseBaseModel.getCommon().isSuccess()) {
                Toast.makeText(getActivity(), cancelAppointmentBulkResponseBaseModel.getCommon().getStatusMessage(), Toast.LENGTH_SHORT).show();

                ArrayList<AppointmentList> emptyIndexList = new ArrayList<>();
                for (int groupIndex = 0; groupIndex < mAppointmentAdapter.getGroupList().size(); groupIndex++) {

                    mAppointmentAdapter.getGroupList().get(groupIndex).getPatientList().removeAll(tempSelectedList);
                    if (mAppointmentAdapter.getGroupList().get(groupIndex).getPatientList().isEmpty())
                        emptyIndexList.add(mAppointmentAdapter.getGroupList().get(groupIndex));
                }
                mAppointmentAdapter.getGroupList().removeAll(emptyIndexList);

                mAppointmentAdapter.notifyDataSetChanged();
                if (mAppointmentAdapter.getGroupList().size()==1){
                    expandableListView.expandGroup(0);
                }
                if (mAppointmentAdapter.getGroupList().isEmpty()) {
                    expandableListView.setVisibility(View.GONE);
                    emptyListView.setVisibility(View.VISIBLE);
                }
                isLongPressed = false;

            } else {
                Toast.makeText(getActivity(), cancelAppointmentBulkResponseBaseModel.getCommon().getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_APPOINTMENT_DELETE)) {
            CancelAppointmentBulkResponseBaseModel deleteAppointmentResponseBaseModel = (CancelAppointmentBulkResponseBaseModel) customResponse;
            if (deleteAppointmentResponseBaseModel.getCommon().isSuccess()) {
                Toast.makeText(getActivity(), deleteAppointmentResponseBaseModel.getCommon().getStatusMessage(), Toast.LENGTH_SHORT).show();

                mAppointmentAdapter.getGroupList().get(groupPos).getPatientList().remove(childPos);
                if (mAppointmentAdapter.getGroupList().get(groupPos).getPatientList().isEmpty())
                    mAppointmentAdapter.getGroupList().remove(groupPos);
                else
                    mAppointmentAdapter.getGroupList().get(groupPos).setPatientHeader(mAppointmentAdapter.getGroupList().get(groupPos).getPatientList().get(0));

                mAppointmentAdapter.notifyDataSetChanged();
                if (mAppointmentAdapter.getGroupList().isEmpty()) {
                    expandableListView.setVisibility(View.GONE);
                    emptyListView.setVisibility(View.VISIBLE);
                }
                isLongPressed = false;

            } else {
                Toast.makeText(getActivity(), deleteAppointmentResponseBaseModel.getCommon().getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void showDialogForWaitingStatus(ArrayList<AddToWaitingResponse> addToWaitingResponse) {
        final Dialog dialog = new Dialog(getActivity());

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.waiting_status_list_dialog);
        dialog.setCancelable(true);

        RecyclerView recyclerViewBottom = (RecyclerView) dialog.findViewById(R.id.recyclerViewBottom);
        LinearLayoutManager linearlayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewBottom.setLayoutManager(linearlayoutManager);
        ShowWaitingStatusAdapter mShowWaitingStatusAdapter = new ShowWaitingStatusAdapter(getActivity(), addToWaitingResponse);
        recyclerViewBottom.setAdapter(mShowWaitingStatusAdapter);
        TextView okButton = (TextView) dialog.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(getActivity(), WaitingMainListActivity.class);
                startActivity(intent);
                //  getActivity().finish();
                getActivity().setResult(RESULT_CLOSE_ACTIVITY_WAITING_LIST);
                removeCheckBox();
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

    public void setFilteredData(MyAppointmentsDataModel myAppointmentsDataModel) {

        final ArrayList<AppointmentList> mAppointmentList = new ArrayList<>();
        if (!myAppointmentsDataModel.getAppointmentList().isEmpty()) {
            expandableListView.setVisibility(View.VISIBLE);
            emptyListView.setVisibility(View.GONE);

            List<PatientList> mPatientLists;
            for (int i = 0; i < myAppointmentsDataModel.getAppointmentList().size(); i++) {
                AppointmentList clinicList = myAppointmentsDataModel.getAppointmentList().get(i);
                mPatientLists = myAppointmentsDataModel.getAppointmentList().get(i).getPatientList();
                if (!mPatientLists.isEmpty()) {
                    clinicList.setPatientHeader(mPatientLists.get(0));
                    mAppointmentList.add(clinicList);
                }
            }

            if (!mAppointmentList.isEmpty()) {
                //list is sorted for Booked and Confirmed Status appointments
                mAppointmentAdapter = new AppointmentAdapter(getActivity(), mAppointmentList, this,myAppointmentsDataModel.getAppointmentFormat());
                expandableListView.setAdapter(mAppointmentAdapter);

                Log.e("mAppointmentAdaptersize", String.valueOf(mAppointmentAdapter.getGroupList().size()));

                if (mAppointmentAdapter.getGroupList().size()==1){
                    expandableListView.expandGroup(0);
                }


            } else {
                expandableListView.setVisibility(View.GONE);
                emptyListView.setVisibility(View.VISIBLE);
            }
        } else {
            expandableListView.setVisibility(View.GONE);
            emptyListView.setVisibility(View.VISIBLE);
        }

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (charString.equals("")) {
                    if (mAppointmentList.get(groupPosition).getPatientList().size() == 1) {
                        expandableListView.collapseGroup(groupPosition);
                    }
                    if (lastExpandedPosition != -1
                            && groupPosition != lastExpandedPosition) {
                        expandableListView.collapseGroup(lastExpandedPosition);
                    }
                    lastExpandedPosition = groupPosition;
                }  //CommonMethods.showToast(getActivity(), "all expand");

            }
        });
    }

}