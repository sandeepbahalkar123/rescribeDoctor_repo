package com.rescribe.doctor.ui.fragments.my_appointments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.my_appointments.AppointmentAdapter;
import com.rescribe.doctor.adapters.my_appointments.BottomMenuAppointmentAdapter;
import com.rescribe.doctor.bottom_menus.BottomMenu;
import com.rescribe.doctor.model.my_appointments.AppointmentList;
import com.rescribe.doctor.model.my_appointments.MyAppointmentsDataModel;
import com.rescribe.doctor.model.my_appointments.PatientList;
import com.rescribe.doctor.ui.activities.my_appointments.MyAppointmentsActivity;
import com.rescribe.doctor.ui.activities.my_patients.patient_history.PatientHistoryActivity;
import com.rescribe.doctor.ui.customesViews.EditTextWithDeleteButton;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by jeetal on 31/1/18.
 */

public class MyAppointmentsFragment extends Fragment implements AppointmentAdapter.OnDownArrowClicked, BottomMenuAppointmentAdapter.OnMenuBottomItemClickListener {
    private static Bundle args;
    @BindView(R.id.searchEditText)
    EditTextWithDeleteButton searchEditText;
    @BindView(R.id.whiteUnderLine)
    ImageView whiteUnderLine;
    public static ExpandableListView expandableListView;
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
    private static AppointmentAdapter mAppointmentAdapter;
    private MyAppointmentsDataModel mMyAppointmentsDataModel;
    private BottomMenuAppointmentAdapter mBottomMenuAppointmentAdapter;
    private int mGroupPosition;
    private ArrayList<BottomMenu> mBottomMenuList;
    private String[] mMenuNames = {"Select All", "Send SMS", "Waiting List"};
    private int lastExpandedPosition = -1;
    private String charString = "";
    private List<PatientList> mPatientLists;

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
        mBottomMenuList = new ArrayList<>();
        for (String mMenuName : mMenuNames) {
            BottomMenu bottomMenu = new BottomMenu();
            bottomMenu.setMenuName(mMenuName);
            mBottomMenuList.add(bottomMenu);
        }
        mMyAppointmentsDataModel = args.getParcelable(RescribeConstants.APPOINTMENT_DATA);
        if (mMyAppointmentsDataModel.getAppointmentList().size() > 0||!mMyAppointmentsDataModel.getAppointmentList().isEmpty()) {
            expandableListView.setVisibility(View.VISIBLE);
            expandableListView.setPadding(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.dp67));
            expandableListView.setClipToPadding(false);
            emptyListView.setVisibility(View.GONE);

            for (int i = 0; i < mMyAppointmentsDataModel.getAppointmentList().size(); i++) {
                AppointmentList clinicList = mMyAppointmentsDataModel.getAppointmentList().get(i);
                mPatientLists = mMyAppointmentsDataModel.getAppointmentList().get(i).getPatientList();
                if(!mPatientLists.isEmpty()&&mPatientLists.size()>0) {
                    clinicList.setPatientHeader(mPatientLists.get(0));
                }
            }
            if(!mPatientLists.isEmpty()) {
                mAppointmentAdapter = new AppointmentAdapter(getActivity(), mMyAppointmentsDataModel.getAppointmentList(), this);
                expandableListView.setAdapter(mAppointmentAdapter);
            }else{
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

        mBottomMenuAppointmentAdapter = new BottomMenuAppointmentAdapter(getContext(), this, mBottomMenuList);
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

                charString = s.toString();

                mAppointmentAdapter.getFilter().filter(s);
                //expand view

                // doConfigureDataListViewVisibility(false, false);
                   /* else {
                    recentDoctorLayout.setVisibility(View.VISIBLE);
                    mFilterListLayout.setVisibility(View.VISIBLE);
                    if (isFilterApplied) {
                        mServiceCardDataViewBuilder.setReceivedDoctorDataList(mPreviousLoadedDocList);
                        setDoctorListAdapter(false);
                    }
                }*/
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

    public static void expandAll() {
        int count = mAppointmentAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            expandableListView.expandGroup(i);
        }
    }

    public static void collapseAll() {
        int count = mAppointmentAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            expandableListView.collapseGroup(i);
        }
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
        if (!ischecked) {
            for (int i = 0; i < mBottomMenuAppointmentAdapter.getList().size(); i++) {
                if (mBottomMenuAppointmentAdapter.getList().get(i).getMenuName().equalsIgnoreCase(getString(R.string.select_all))) {
                    mBottomMenuAppointmentAdapter.getList().get(i).setSelected(false);
                }
            }
            mBottomMenuAppointmentAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClickOfPatientDetails(PatientList patientListObject,String patientDetails) {
        Bundle b = new Bundle();
        b.putString(RescribeConstants.PATIENT_NAME,patientListObject.getPatientName());
        b.putString(RescribeConstants.PATIENT_INFO,patientDetails);
        Intent intent = new Intent(getActivity(), PatientHistoryActivity.class);
        intent.putExtra(RescribeConstants.PATIENT_INFO,b);
        startActivity(intent);
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
        } /*else if (bottomMenu.getMenuName().equalsIgnoreCase(getString(R.string.send_mail))) {
            if (bottomMenu.isSelected()) {
                ArrayList<String> mEmailPatinetsList = new ArrayList<>();
                for (int groupIndex = 0; groupIndex < mAppointmentAdapter.getGroupList().size(); groupIndex++) {
                    if (mAppointmentAdapter.getGroupList().get(groupIndex).getPatientHeader().isSelected()) {
                        mEmailPatinetsList.add(mAppointmentAdapter.getGroupList().get(groupIndex).getPatientHeader().getPatientEmail());
                    }
                    for (int childIndex = 0; childIndex < mAppointmentAdapter.getGroupList().get(groupIndex).getPatientList().size(); childIndex++) {
                        PatientList patientList = mAppointmentAdapter.getGroupList().get(groupIndex).getPatientList().get(childIndex);
                        if (patientList.isSelected()) {
                            mEmailPatinetsList.add(patientList.getPatientEmail());
                        }
                    }
                }
                int count = mEmailPatinetsList.size();
                String[] emailList = new String[count];
                for (int emailIndex = 0; emailIndex < mEmailPatinetsList.size(); emailIndex++) {
                    emailList[emailIndex] = mEmailPatinetsList.get(emailIndex);
                }
                if (!mEmailPatinetsList.isEmpty()) {
                    Intent intent = null;
                    intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("plain/text");
                    intent.putExtra(Intent.EXTRA_EMAIL, emailList);
                    startActivity(intent);
                    for (int i = 0; i < mBottomMenuAppointmentAdapter.getList().size(); i++) {
                        if (mBottomMenuAppointmentAdapter.getList().get(i).getMenuName().equalsIgnoreCase(getString(R.string.send_mail))) {
                            mBottomMenuAppointmentAdapter.getList().get(i).setSelected(false);
                        }
                    }
                    mBottomMenuAppointmentAdapter.notifyDataSetChanged();

                } else {
                    CommonMethods.showToast(getActivity(), getString(R.string.please_select_patients));
                }
            }

        } */ else if (bottomMenu.getMenuName().equalsIgnoreCase(getString(R.string.send_sms))) {
            ArrayList<String> mSmsList = new ArrayList<>();
            for (int groupIndex = 0; groupIndex < mAppointmentAdapter.getGroupList().size(); groupIndex++) {
                if (mAppointmentAdapter.getGroupList().get(groupIndex).getPatientHeader().isSelected()) {
                    mSmsList.add(mAppointmentAdapter.getGroupList().get(groupIndex).getPatientHeader().getPatientPhone());
                }
                for (int childIndex = 0; childIndex < mAppointmentAdapter.getGroupList().get(groupIndex).getPatientList().size(); childIndex++) {
                    PatientList patientList = mAppointmentAdapter.getGroupList().get(groupIndex).getPatientList().get(childIndex);
                    if (patientList.isSelected()) {
                        mSmsList.add(patientList.getPatientPhone());
                    }
                }
            }
            int count = mSmsList.size();
            String emailList = "";
            for (int emailIndex = 0; emailIndex < mSmsList.size(); emailIndex++) {
                emailList += mSmsList.get(emailIndex) + ";";
            }
            if (!mSmsList.isEmpty()) {
                Uri smsToUri = Uri.parse("smsto:" + emailList);
                Intent intent = new Intent(
                        Intent.ACTION_SENDTO, smsToUri);
                String message = "hello";
                // message = message.replace("%s", StoresMessage.m_storeName);
                intent.putExtra("sms_body", message);
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

        }
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
}