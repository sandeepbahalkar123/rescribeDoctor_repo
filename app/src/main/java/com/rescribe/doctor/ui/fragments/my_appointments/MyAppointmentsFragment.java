package com.rescribe.doctor.ui.fragments.my_appointments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.my_appointments.AppointmentAdapter;
import com.rescribe.doctor.adapters.my_appointments.BottomMenuAppointmentAdapter;
import com.rescribe.doctor.bottom_menus.BottomMenu;
import com.rescribe.doctor.model.my_appointments.ClinicList;
import com.rescribe.doctor.model.my_appointments.MyAppointmentsDataModel;
import com.rescribe.doctor.model.my_appointments.PatientList;
import com.rescribe.doctor.ui.customesViews.EditTextWithDeleteButton;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by jeetal on 31/1/18.
 */

public class MyAppointmentsFragment extends Fragment implements AppointmentAdapter.OnDownArrowClicked, BottomMenuAppointmentAdapter.OnMenuBottomItemClickListener {
    private static final int EXPANDED_REQUEST_CODE = 010;
    private static final int COLLAPSED_REQUEST_CODE = 020;
    private static Bundle args;
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

    private AppointmentAdapter mAppointmentAdapter;
    private MyAppointmentsDataModel mMyAppointmentsDataModel;
    private BottomSheetDialog dialog;
    private BottomMenuAppointmentAdapter mBottomMenuAppointmentAdapter;
    private int mGroupPosition;
    private ArrayList<BottomMenu> mBottomMenuList;
    private String[] mMenuNames = {"Select All", "Send SMS", "Send Email", "Waiting List"};

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.my_appointments_layout, container, false);
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
        mMyAppointmentsDataModel = args.getParcelable(RescribeConstants.APPOINTMENT_DATA);
        if (mMyAppointmentsDataModel.getClinicList().size() > 0) {
            expandableListView.setVisibility(View.VISIBLE);
            expandableListView.setPadding(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.dp67));
            expandableListView.setClipToPadding(false);
            emptyListView.setVisibility(View.GONE);

            for (int i = 0; i < mMyAppointmentsDataModel.getClinicList().size(); i++) {
                ClinicList clinicList = mMyAppointmentsDataModel.getClinicList().get(i);
                List<PatientList> mPatientLists = mMyAppointmentsDataModel.getClinicList().get(i).getPatientList();
                clinicList.setPatientHeader(mPatientLists.get(0));
                mPatientLists.remove(0);
            }

            mAppointmentAdapter = new AppointmentAdapter(getActivity(), mMyAppointmentsDataModel.getClinicList(), this);
            expandableListView.setAdapter(mAppointmentAdapter);
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

        mBottomMenuAppointmentAdapter = new BottomMenuAppointmentAdapter(getContext(), this,mBottomMenuList);
        LinearLayoutManager linearlayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewBottom.setLayoutManager(linearlayoutManager);
        recyclerViewBottom.setAdapter(mBottomMenuAppointmentAdapter);

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


    @Override
    public void onDownArrowSetClick(int groupPosition, boolean isExpanded) {
        if (isExpanded) {
            // getActivity().setResult(EXPANDED_REQUEST_CODE);
            expandableListView.collapseGroup(groupPosition);
        } else {
            //getActivity().setResult(COLLAPSED_REQUEST_CODE);
            expandableListView.expandGroup(groupPosition);
        }

    }

    @Override
    public void onLongPressOpenBottomMenu(boolean isLongPressed, int groupPosition) {
        if (isLongPressed) {
            mGroupPosition = groupPosition;
            recyclerViewBottom.setVisibility(View.VISIBLE);


         /*   Animation slideUpAnimation = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.slide_up_animation);
            recyclerViewBottom.startAnimation(slideUpAnimation);*/

        } else {

            recyclerViewBottom.setVisibility(View.GONE);

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void setClickOnMenuItem(int position,  BottomMenu bottomMenu) {
        if (bottomMenu.getMenuName().equalsIgnoreCase(getString(R.string.select_all))) {
            if (bottomMenu.isSelected()) {
                for (int index = 0; index < mMyAppointmentsDataModel.getClinicList().size(); index++) {
                    for (ClinicList clinicList : mMyAppointmentsDataModel.getClinicList()) {
                        clinicList.setSelectedGroupCheckbox(true);
                        clinicList.getPatientHeader().setSelected(true);

                        for (int patientListIndex = 0; patientListIndex < mMyAppointmentsDataModel.getClinicList().get(index).getPatientList().size(); patientListIndex++) {
                            mMyAppointmentsDataModel.getClinicList().get(index).getPatientList().get(patientListIndex).setSelected(true);
                        }
                    }
                }
                mAppointmentAdapter.notifyDataSetChanged();

            } else {
                for (int index = 0; index < mMyAppointmentsDataModel.getClinicList().size(); index++) {
                    for (ClinicList clinicList : mMyAppointmentsDataModel.getClinicList()) {
                        clinicList.setSelectedGroupCheckbox(false);
                        clinicList.getPatientHeader().setSelected(false);
                        for (int patientListIndex = 0; patientListIndex < mMyAppointmentsDataModel.getClinicList().get(index).getPatientList().size(); patientListIndex++) {
                            mMyAppointmentsDataModel.getClinicList().get(index).getPatientList().get(patientListIndex).setSelected(false);
                        }
                    }
                }
                mAppointmentAdapter.notifyDataSetChanged();

            }

        }

    }
}
