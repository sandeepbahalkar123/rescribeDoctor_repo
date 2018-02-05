package com.rescribe.doctor.ui.fragments.my_appointments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.my_appointments.AppointmentAdapter;
import com.rescribe.doctor.adapters.my_appointments.BottomMenuAppointmentAdapter;
import com.rescribe.doctor.model.my_appointments.ClinicList;
import com.rescribe.doctor.model.my_appointments.MyAppointmentsDataModel;
import com.rescribe.doctor.model.my_appointments.PatientList;
import com.rescribe.doctor.ui.customesViews.EditTextWithDeleteButton;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by jeetal on 31/1/18.
 */

public class MyAppointmentsFragment extends Fragment implements AppointmentAdapter.OnDownArrowClicked {
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
    FrameLayout appointmentLayoutContainer;
    private Unbinder unbinder;
    private AppointmentAdapter mAppointmentAdapter;
    private MyAppointmentsDataModel mMyAppointmentsDataModel;
    BottomSheetDialog dialog;
    private RecyclerView recyclerView;
    private BottomMenuAppointmentAdapter mBottomMenuAppointmentAdapter;
    HashMap<ClinicList, List<PatientList>> mChildClinicList = new HashMap<>();


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
            expandableListView.collapseGroup(groupPosition);
        } else {
            expandableListView.expandGroup(groupPosition);
        }

    }

    @Override
    public void onLongPressOpenBottomMenu(boolean isLongPressed) {

    }


}
