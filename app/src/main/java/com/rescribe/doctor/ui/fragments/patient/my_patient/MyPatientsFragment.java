package com.rescribe.doctor.ui.fragments.patient.my_patient;

import android.content.Intent;
import android.net.Uri;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.my_appointments.BottomMenuAppointmentAdapter;
import com.rescribe.doctor.adapters.my_patients.MyPatientsAdapter;
import com.rescribe.doctor.bottom_menus.BottomMenu;
import com.rescribe.doctor.helpers.doctor_patients.MyPatientBaseModel;
import com.rescribe.doctor.helpers.doctor_patients.PatientList;
import com.rescribe.doctor.ui.activities.add_records.AddRecordsActivity;
import com.rescribe.doctor.ui.activities.my_patients.MyPatientsActivity;
import com.rescribe.doctor.ui.activities.my_patients.patient_history.PatientHistoryActivity;
import com.rescribe.doctor.ui.customesViews.EditTextWithDeleteButton;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by jeetal on 31/1/18.
 */

public class MyPatientsFragment extends Fragment implements MyPatientsAdapter.OnDownArrowClicked, BottomMenuAppointmentAdapter.OnMenuBottomItemClickListener {
    private static Bundle args;
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
    private MyPatientsAdapter mMyPatientsAdapter;
    private ArrayList<BottomMenu> mBottomMenuList;
    private String[] mMenuNames = {"Select All", "Send SMS", "Waiting List"};
    private BottomMenuAppointmentAdapter mBottomMenuAppointmentAdapter;

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
            mMyPatientsAdapter = new MyPatientsAdapter(getActivity(), myPatientBaseModel.getPatientDataModel().getPatientList(), this);
            LinearLayoutManager linearlayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(linearlayoutManager);
            // off recyclerView Animation
            RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
            recyclerView.setPadding(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.dp67));
            recyclerView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.divider));
            if (animator instanceof SimpleItemAnimator)
                ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
            recyclerView.setAdapter(mMyPatientsAdapter);
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
                mMyPatientsAdapter.getFilter().filter(s);
            }
        });
        mBottomMenuAppointmentAdapter = new BottomMenuAppointmentAdapter(getContext(), this, mBottomMenuList);
        recyclerViewBottom.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerViewBottom.setAdapter(mBottomMenuAppointmentAdapter);
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
    public void onClickOfPatientDetails(PatientList patientListObject, String text) {
        Bundle b = new Bundle();
        b.putString(RescribeConstants.PATIENT_NAME,patientListObject.getPatientName());
        b.putString(RescribeConstants.PATIENT_INFO,text);
        Intent intent = new Intent(getActivity(), PatientHistoryActivity.class);
        intent.putExtra(RescribeConstants.PATIENT_INFO,b);
        startActivity(intent);
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
                }
                mMyPatientsAdapter.notifyDataSetChanged();

            } else {
                for (int index = 0; index < mMyPatientsAdapter.getGroupList().size(); index++) {
                    mMyPatientsAdapter.getGroupList().get(index).setSelected(false);
                }
                mMyPatientsAdapter.notifyDataSetChanged();
            }
        } /*else if (bottomMenu.getMenuName().equalsIgnoreCase(getString(R.string.send_mail))) {

            if (bottomMenu.isSelected()) {
                ArrayList<String> mEmailPatinetsList = new ArrayList<>();
                for (int groupIndex = 0; groupIndex < mMyPatientsAdapter.getGroupList().size(); groupIndex++) {

                    PatientList patientList = mMyPatientsAdapter.getGroupList().get(groupIndex);
                    if (patientList.isSelected()) {
                        mEmailPatinetsList.add(patientList.getPatientEmail());
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

        }*/ else if (bottomMenu.getMenuName().equalsIgnoreCase(getString(R.string.send_sms))) {
            ArrayList<String> mSmsList = new ArrayList<>();

            for (int childIndex = 0; childIndex < mMyPatientsAdapter.getGroupList().size(); childIndex++) {
                PatientList patientList = mMyPatientsAdapter.getGroupList().get(childIndex);
                if (patientList.isSelected()) {
                    mSmsList.add(patientList.getPatientPhone());
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
        return mMyPatientsAdapter.isLongPressed;
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
}
