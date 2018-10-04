package com.rescribe.doctor.ui.fragments.book_appointment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rescribe.doctor.R;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.ui.activities.my_patients.MyPatientsActivity;
import com.rescribe.doctor.util.RescribeConstants;

public class CoachFragment extends Fragment {
    public CoachFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CoachFragment newInstance() {
        CoachFragment fragment = new CoachFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_coach_download_all_patient, container, false);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
                if (getActivity() instanceof MyPatientsActivity)
                    ((MyPatientsActivity) getActivity()).hideCoachmarkContainer();
            }
        });
        return rootView;
    }

}
