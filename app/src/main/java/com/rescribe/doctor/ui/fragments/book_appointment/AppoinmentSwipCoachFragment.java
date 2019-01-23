package com.rescribe.doctor.ui.fragments.book_appointment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rescribe.doctor.R;
import com.rescribe.doctor.ui.activities.my_appointments.MyAppointmentsActivity;
import com.rescribe.doctor.ui.activities.my_patients.MyPatientsActivity;

public class AppoinmentSwipCoachFragment extends Fragment {
    public AppoinmentSwipCoachFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AppoinmentSwipCoachFragment newInstance() {
        AppoinmentSwipCoachFragment fragment = new AppoinmentSwipCoachFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_coach_mark_delete_the_appointment, container, false);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
                if (getActivity() instanceof MyAppointmentsActivity)
                    ((MyAppointmentsActivity) getActivity()).hideCoachmarkContainer();
            }
        });
        return rootView;
    }

}
