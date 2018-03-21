package com.rescribe.doctor.ui.fragments.patient.my_patient;

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
import com.rescribe.doctor.adapters.my_patients.MyPatientsAdapter;
import com.rescribe.doctor.bottom_menus.BottomMenu;
import com.rescribe.doctor.helpers.doctor_patients.MyPatientBaseModel;
import com.rescribe.doctor.helpers.doctor_patients.PatientList;
import com.rescribe.doctor.helpers.myappointments.AppointmentHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.doctor_location.DoctorLocationModel;
import com.rescribe.doctor.model.patient.template_sms.TemplateBaseModel;
import com.rescribe.doctor.model.patient.template_sms.TemplateList;
import com.rescribe.doctor.model.patient.template_sms.request_send_sms.PatientInfoList;
import com.rescribe.doctor.model.request_patients.RequestSearchPatients;
import com.rescribe.doctor.model.waiting_list.new_request_add_to_waiting_list.AddToList;
import com.rescribe.doctor.model.waiting_list.new_request_add_to_waiting_list.PatientAddToWaitingList;
import com.rescribe.doctor.model.waiting_list.new_request_add_to_waiting_list.RequestToAddWaitingList;
import com.rescribe.doctor.model.waiting_list.response_add_to_waiting_list.AddToWaitingListBaseModel;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.singleton.RescribeApplication;
import com.rescribe.doctor.ui.activities.my_patients.MyPatientsActivity;
import com.rescribe.doctor.ui.activities.my_patients.patient_history.PatientHistoryActivity;
import com.rescribe.doctor.ui.activities.waiting_list.WaitingMainListActivity;
import com.rescribe.doctor.ui.customesViews.EditTextWithDeleteButton;
import com.rescribe.doctor.ui.customesViews.drag_drop_recyclerview_helper.EndlessRecyclerViewScrollListener;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.rescribe.doctor.ui.activities.waiting_list.WaitingMainListActivity.RESULT_CLOSE_ACTIVITY_WAITING_LIST;
import static com.rescribe.doctor.ui.fragments.patient.my_patient.SendSmsPatientActivity.RESULT_SEND_SMS;
import static com.rescribe.doctor.util.CommonMethods.toCamelCase;
import static com.rescribe.doctor.util.RescribeConstants.LOCATION_ID;


/**
 * Created by jeetal on 31/1/18.
 */

public class MyPatientsFragment extends Fragment implements MyPatientsAdapter.OnDownArrowClicked, BottomMenuAppointmentAdapter.OnMenuBottomItemClickListener, HelperResponse {
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
    private MyPatientsAdapter mMyPatientsAdapter;
    private String[] mMenuNames = {"Select All", "Send SMS", "Waiting List"};
    private BottomMenuAppointmentAdapter mBottomMenuAppointmentAdapter;
    public static final int PAGE_START = 0;
    private String searchText = "";
    private ArrayList<DoctorLocationModel> mDoctorLocationModel = new ArrayList<>();
    private ArrayList<PatientList> mPatientListsOriginal;
    private int mLocationId;
    private ArrayList<PatientAddToWaitingList> patientsListAddToWaitingLists;
    private ArrayList<PatientInfoList> patientInfoLists;
    private int mClinicId;
    private String mClinicName = "";
    private boolean isFiltered = false;
    private boolean isFromDrawer;
    private RequestSearchPatients mRequestSearchPatientsForDrawer = new RequestSearchPatients();
    private ArrayList<PatientList> patientLists;
    private ArrayList<AddToList> addToWaitingArrayList;
    private String mClinicCity;
    private String mClinicArea;

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
        ArrayList<BottomMenu> mBottomMenuList = new ArrayList<>();
        isFromDrawer = args.getBoolean(RescribeConstants.IS_FROM_DRAWER);
        if (isFromDrawer) {
            mRequestSearchPatientsForDrawer = args.getParcelable(RescribeConstants.DRAWER_REQUEST);
        }
        mDoctorLocationModel = RescribeApplication.getDoctorLocationModels();
        mAppointmentHelper = new AppointmentHelper(getActivity(), this);
        for (String mMenuName : mMenuNames) {
            BottomMenu bottomMenu = new BottomMenu();
            bottomMenu.setMenuName(mMenuName);
            mBottomMenuList.add(bottomMenu);
        }
        MyPatientBaseModel patientBaseModel = args.getParcelable(RescribeConstants.MYPATIENTS_DATA);
        patientLists = patientBaseModel.getPatientDataModel().getPatientList();

        recyclerView.setClipToPadding(false);
        // off recyclerView Animation
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        recyclerView.setPadding(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.dp67));
        recyclerView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.divider));
        if (animator instanceof SimpleItemAnimator)
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);

        initAdapter();

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
                    initAdapter();
                }
            }
        });
        if (args.getString(RescribeConstants.ACTIVITY_LAUNCHED_FROM).equals(RescribeConstants.HOME_PAGE)) {
            mBottomMenuAppointmentAdapter = new BottomMenuAppointmentAdapter(getContext(), this, mBottomMenuList, true, RescribeConstants.NOT_FROM_COMPLETE_OPD);
            recyclerViewBottom.setLayoutManager(new GridLayoutManager(getActivity(), 3));
            recyclerViewBottom.setAdapter(mBottomMenuAppointmentAdapter);
        } else {
            mBottomMenuAppointmentAdapter = new BottomMenuAppointmentAdapter(getContext(), this, mBottomMenuList, false, RescribeConstants.NOT_FROM_COMPLETE_OPD);
            recyclerViewBottom.setLayoutManager(new GridLayoutManager(getActivity(), 3));
            recyclerViewBottom.setAdapter(mBottomMenuAppointmentAdapter);
        }
    }

    private void initAdapter() {
        if (patientLists.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            emptyListView.setVisibility(View.GONE);

            for (PatientList patientList : patientLists) {
                patientList.setSelected(((MyPatientsActivity) getActivity()).selectedDoctorId.contains(patientList.getHospitalPatId()));
            }

            boolean isLongPress = mMyPatientsAdapter != null && mMyPatientsAdapter.isLongPressed;

            LinearLayoutManager linearlayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(linearlayoutManager);
            mMyPatientsAdapter = new MyPatientsAdapter(getActivity(), patientLists, this, args.getString(RescribeConstants.ACTIVITY_LAUNCHED_FROM).equals(RescribeConstants.HOME_PAGE));
            recyclerView.setAdapter(mMyPatientsAdapter);

            recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearlayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    loadNextPage(page);
                }
            });

            mMyPatientsAdapter.isLongPressed = isLongPress;

        } else {
            recyclerView.setVisibility(View.GONE);
            emptyListView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLongPressOpenBottomMenu(boolean isLongPressed, int groupPosition) {
        if (isLongPressed) {
            recyclerViewBottom.setVisibility(View.VISIBLE);
        } else {
            for (int index = 0; index < mMyPatientsAdapter.getGroupList().size(); index++) {
                mMyPatientsAdapter.getGroupList().get(index).setSelected(false);
            }
            mMyPatientsAdapter.notifyDataSetChanged();
            for (int i = 0; i < mBottomMenuAppointmentAdapter.getList().size(); i++) {
                mBottomMenuAppointmentAdapter.getList().get(i).setSelected(false);
            }
            mBottomMenuAppointmentAdapter.notifyDataSetChanged();
            recyclerViewBottom.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRecordFound(boolean isListEmpty) {
        emptyListView.setVisibility(isListEmpty ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onCheckUncheckRemoveSelectAllSelection(boolean ischecked, PatientList patientObject) {
        if (!ischecked) {
            ((MyPatientsActivity) getActivity()).selectedDoctorId.remove(patientObject.getHospitalPatId());

            for (int i = 0; i < mBottomMenuAppointmentAdapter.getList().size(); i++) {
                if (mBottomMenuAppointmentAdapter.getList().get(i).getMenuName().equalsIgnoreCase(getString(R.string.select_all))) {
                    mBottomMenuAppointmentAdapter.getList().get(i).setSelected(false);
                }
            }
            mBottomMenuAppointmentAdapter.notifyDataSetChanged();
        } else
            ((MyPatientsActivity) getActivity()).selectedDoctorId.add(patientObject.getHospitalPatId());
    }

    @Override
    public void onClickOfPatientDetails(PatientList patientListObject, String text, boolean isClickOnPatientDetailsRequired) {
        if (isClickOnPatientDetailsRequired) {

            String patientName;
            if (patientListObject.getSalutation() != 0)
                patientName = RescribeConstants.SALUTATION[patientListObject.getSalutation() - 1] + toCamelCase(patientListObject.getPatientName());
            else patientName = toCamelCase(patientListObject.getPatientName());

            Bundle b = new Bundle();
            b.putString(RescribeConstants.PATIENT_NAME, patientName);
            b.putString(RescribeConstants.PATIENT_INFO, text);
            b.putString(RescribeConstants.PATIENT_ID, String.valueOf(patientListObject.getPatientId()));
            b.putString(RescribeConstants.PATIENT_HOS_PAT_ID, String.valueOf(patientListObject.getHospitalPatId()));
            Intent intent = new Intent(getActivity(), PatientHistoryActivity.class);
            intent.putExtra(RescribeConstants.PATIENT_INFO, b);
            startActivity(intent);
        } else {
            patientsListAddToWaitingLists = new ArrayList<>();
            for (int childIndex = 0; childIndex < mMyPatientsAdapter.getGroupList().size(); childIndex++) {
                PatientList patientList = mMyPatientsAdapter.getGroupList().get(childIndex);
                if (patientList.getHospitalPatId().equals(patientListObject.getHospitalPatId())) {
                    PatientAddToWaitingList patientInfoListObject = new PatientAddToWaitingList();
                    patientInfoListObject.setPatientName(patientList.getPatientName());
                    patientInfoListObject.setPatientId(String.valueOf(patientList.getPatientId()));
                    patientInfoListObject.setHospitalPatId(String.valueOf(patientList.getHospitalPatId()));
                    patientsListAddToWaitingLists.add(patientInfoListObject);

                }
            }

            if (!patientsListAddToWaitingLists.isEmpty()) {

                showDialogToSelectLocation(mDoctorLocationModel);

            } else {
                //   CommonMethods.showToast(getActivity(), getString(R.string.please_select_patients));

            }

        }
    }

    @Override
    public void onPhoneNoClick(String patientPhone) {
        MyPatientsActivity activity = (MyPatientsActivity) getActivity();
        activity.callPatient(patientPhone);
    }


    public static MyPatientsFragment newInstance(Bundle b) {
        MyPatientsFragment fragment = new MyPatientsFragment();
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
                    ((MyPatientsActivity) getActivity()).selectedDoctorId.add(mMyPatientsAdapter.getGroupList().get(index).getHospitalPatId());
                }
                mMyPatientsAdapter.notifyDataSetChanged();

            } else {
                for (int index = 0; index < mMyPatientsAdapter.getGroupList().size(); index++) {
                    mMyPatientsAdapter.getGroupList().get(index).setSelected(false);
                    ((MyPatientsActivity) getActivity()).selectedDoctorId.remove(mMyPatientsAdapter.getGroupList().get(index).getHospitalPatId());
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
                    PatientAddToWaitingList patientInfoListObject = new PatientAddToWaitingList();
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

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        if (!RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.SELECTED_LOCATION_ID, getActivity()).equals(""))
            mLocationId = Integer.parseInt(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.SELECTED_LOCATION_ID, getActivity()));
        RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroup);
        for (int index = 0; index < mDoctorLocationModel.size(); index++) {
            final DoctorLocationModel clinicList = mDoctorLocationModel.get(index);

            final RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.dialog_location_radio_item, null, false);
            if (mLocationId == clinicList.getLocationId()) {
                radioButton.setChecked(true);
            } else {
                radioButton.setChecked(false);
            }
            radioButton.setText(clinicList.getClinicName() + ", " + clinicList.getAddress());
            radioButton.setId(CommonMethods.generateViewId());
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
                    RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.SELECTED_LOCATION_ID, String.valueOf(mLocationId), getActivity());
                    mAppointmentHelper.doGetDoctorTemplate();
                    dialog.cancel();
                } else {
                    Toast.makeText(getActivity(), "Please select clinic location.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

    }

    private void showDialogToSelectLocation(ArrayList<DoctorLocationModel> mPatientListsOriginal) {
        final Dialog dialog = new Dialog(getActivity());

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_select_location_waiting_list_layout);

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        if (!RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.SELECTED_LOCATION_ID, getActivity()).equals(""))
            mLocationId = Integer.parseInt(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.SELECTED_LOCATION_ID, getActivity()));
        RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroup);
        for (int index = 0; index < mPatientListsOriginal.size(); index++) {
            final DoctorLocationModel clinicList = mPatientListsOriginal.get(index);

            final RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.dialog_location_radio_item, null, false);
            if (mLocationId == clinicList.getLocationId()) {
                radioButton.setChecked(true);
            } else {
                radioButton.setChecked(false);
            }
            radioButton.setText(clinicList.getClinicName() + ", " + clinicList.getAddress());
            radioButton.setId(CommonMethods.generateViewId());
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mLocationId = clinicList.getLocationId();
                    mClinicId = clinicList.getClinicId();
                    mClinicName = clinicList.getClinicName();
                    mClinicCity = clinicList.getCity();
                    mClinicArea = clinicList.getArea();
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
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);


    }


    private void callWaitingListApi() {
        RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.SELECTED_LOCATION_ID, String.valueOf(mLocationId), getActivity());
        for(int i = 0;i<mDoctorLocationModel.size();i++){
            if(mLocationId==mDoctorLocationModel.get(i).getLocationId()){
                mClinicId = mDoctorLocationModel.get(i).getClinicId();
                mClinicName = mDoctorLocationModel.get(i).getClinicName();
                mClinicCity = mDoctorLocationModel.get(i).getCity();
                mClinicArea = mDoctorLocationModel.get(i).getArea();
            }
        }
        addToWaitingArrayList = new ArrayList<>();
        AddToList addToList = new AddToList();
        addToList.setPatientAddToWaitingList(patientsListAddToWaitingLists);
        addToList.setLocationId(mLocationId);
        addToList.setLocationDetails(mClinicName + ", " + mClinicArea + ", " + mClinicCity);
        addToWaitingArrayList.add(addToList);
        RequestToAddWaitingList requestForWaitingListPatients = new RequestToAddWaitingList();
        requestForWaitingListPatients.setAddToList(addToWaitingArrayList);
        requestForWaitingListPatients.setDate(CommonMethods.getFormattedDate(CommonMethods.getCurrentDate(RescribeConstants.DATE_PATTERN.YYYY_MM_DD), RescribeConstants.DATE_PATTERN.DD_MM_YYYY, RescribeConstants.DATE_PATTERN.YYYY_MM_DD));
        requestForWaitingListPatients.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, getActivity())));
        mAppointmentHelper.doAddToWaitingListFromMyPatients(requestForWaitingListPatients);
    }


    public boolean callOnBackPressed() {
        if (mMyPatientsAdapter != null) {
            return mMyPatientsAdapter.isLongPressed;
        } else {
            return false;
        }
    }

    public void removeCheckBox() {
        recyclerViewBottom.setVisibility(View.GONE);
        mMyPatientsAdapter.setLongPressed(false);
        for (int index = 0; index < mMyPatientsAdapter.getGroupList().size(); index++) {
            mMyPatientsAdapter.getGroupList().get(index).setSelected(false);
        }
        mMyPatientsAdapter.notifyDataSetChanged();
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
        if (!isFromDrawer) {
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
        } else {
            if (searchText.length() == 0) {
                mRequestSearchPatientsForDrawer.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, getActivity())));
                mRequestSearchPatientsForDrawer.setPageNo(currentPage);
                mAppointmentHelper.doGetMyPatients(mRequestSearchPatientsForDrawer);
            } else {
                mRequestSearchPatientsForDrawer.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, getActivity())));
                mRequestSearchPatientsForDrawer.setPageNo(currentPage);
                mRequestSearchPatientsForDrawer.setSearchText(searchText);
                mAppointmentHelper.doGetMyPatients(mRequestSearchPatientsForDrawer);
            }

        }
    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_GET_PATIENT_DATA)) {
            if (customResponse != null) {
                //Paginated items added here
                MyPatientBaseModel myAppointmentsBaseModel = (MyPatientBaseModel) customResponse;
                ArrayList<PatientList> mLoadedPatientList = myAppointmentsBaseModel.getPatientDataModel().getPatientList();
                mMyPatientsAdapter.addAll(mLoadedPatientList, ((MyPatientsActivity) getActivity()).selectedDoctorId);

            }
        } else if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_GET_SEARCH_RESULT_MY_PATIENT)) {

            MyPatientBaseModel myAppointmentsBaseModel = (MyPatientBaseModel) customResponse;
            ArrayList<PatientList> mLoadedPatientList = myAppointmentsBaseModel.getPatientDataModel().getPatientList();
            if (mLoadedPatientList.size() > 0) {
                emptyListView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                mMyPatientsAdapter.clear();
                for (PatientList patientList : mLoadedPatientList) {
                    patientList.setSpannableString(searchText);
                }
                mMyPatientsAdapter.addAll(mLoadedPatientList, ((MyPatientsActivity) getActivity()).selectedDoctorId);
            } else {
                recyclerView.setVisibility(View.GONE);
                emptyListView.setVisibility(View.VISIBLE);
            }

        } else if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_ADD_TO_WAITING_LIST)) {
            AddToWaitingListBaseModel addToWaitingListBaseModel = (AddToWaitingListBaseModel) customResponse;
            if (addToWaitingListBaseModel.getCommon().isSuccess()) {
                if (addToWaitingListBaseModel.getAddToWaitingModel().getAddToWaitingResponse().get(0).getStatusMessage().toLowerCase().contains(getString(R.string.patients_added_to_waiting_list).toLowerCase())) {
                    Intent intent = new Intent(getActivity(), WaitingMainListActivity.class);
                    intent.putExtra(LOCATION_ID, mLocationId);
                    startActivity(intent);
                    Toast.makeText(getActivity(), addToWaitingListBaseModel.getAddToWaitingModel().getAddToWaitingResponse().get(0).getStatusMessage(), Toast.LENGTH_LONG).show();
                    getActivity().finish();
                    getActivity().setResult(RESULT_CLOSE_ACTIVITY_WAITING_LIST);
                } else if (addToWaitingListBaseModel.getAddToWaitingModel().getAddToWaitingResponse().get(0).getStatusMessage().toLowerCase().contains(getString(R.string.patient_limit_exceeded).toLowerCase())) {
                    Toast.makeText(getActivity(), addToWaitingListBaseModel.getAddToWaitingModel().getAddToWaitingResponse().get(0).getStatusMessage(), Toast.LENGTH_LONG).show();

                } else if (addToWaitingListBaseModel.getAddToWaitingModel().getAddToWaitingResponse().get(0).getStatusMessage().toLowerCase().contains(getString(R.string.already_exists_in_waiting_list).toLowerCase())) {
                    Toast.makeText(getActivity(), addToWaitingListBaseModel.getAddToWaitingModel().getAddToWaitingResponse().get(0).getStatusMessage(), Toast.LENGTH_LONG).show();

                } else if (addToWaitingListBaseModel.getAddToWaitingModel().getAddToWaitingResponse().get(0).getStatusMessage().toLowerCase().contains(getString(R.string.added_to_waiting_list).toLowerCase())) {
                    Intent intent = new Intent(getActivity(), WaitingMainListActivity.class);
                    intent.putExtra(LOCATION_ID, mLocationId);
                    startActivity(intent);
                    Toast.makeText(getActivity(), addToWaitingListBaseModel.getAddToWaitingModel().getAddToWaitingResponse().get(0).getStatusMessage(), Toast.LENGTH_LONG).show();
                    getActivity().finish();
                    getActivity().setResult(RESULT_CLOSE_ACTIVITY_WAITING_LIST);
                }
            }
        }
        if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_GET_DOCTOR_SMS_TEMPLATE)) {
            TemplateBaseModel templateBaseModel = (TemplateBaseModel) customResponse;
            ArrayList<TemplateList> templateLists = templateBaseModel.getTemplateDataModel().getTemplateList();
            if (!templateLists.isEmpty()) {
                Intent intent = new Intent(getActivity(), TemplateListForMyPatients.class);
                intent.putExtra(LOCATION_ID, mLocationId);
                intent.putExtra(RescribeConstants.CLINIC_ID, mClinicId);
                intent.putExtra(RescribeConstants.CLINIC_NAME, mClinicName);
                intent.putParcelableArrayListExtra(RescribeConstants.PATIENT_LIST, patientInfoLists);
                intent.putParcelableArrayListExtra(RescribeConstants.TEMPLATE_LIST, templateLists);
                startActivity(intent);
            } else {

                Intent intent = new Intent(getActivity(), SendSmsPatientActivity.class);
                intent.putExtra(LOCATION_ID, mLocationId);
                intent.putExtra(RescribeConstants.CLINIC_ID, mClinicId);
//                intent.putExtra(RescribeConstants.TEMPLATE_OBJECT, templateList);
                intent.putExtra(RescribeConstants.CLINIC_NAME, mClinicName);
                intent.putParcelableArrayListExtra(RescribeConstants.PATIENT_LIST, patientInfoLists);
                startActivityForResult(intent, RESULT_SEND_SMS);
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
