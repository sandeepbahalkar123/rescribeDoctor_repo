package com.rescribe.doctor.ui.fragments.patient_connect;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.DoctorSearchByNameAdapter;
import com.rescribe.doctor.ui.activities.DoctorConnectActivity;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by jeetal on 8/9/17.
 */

public class PatientSearchFragment extends Fragment implements DoctorConnectActivity.OnClickOfSearchBar {
    @BindView(R.id.listView)
    RecyclerView mRecyclerView;
    @BindView(R.id.emptyListView)
    RelativeLayout emptyListView;
    Unbinder unbinder;
    private View mRootView;
    private DoctorSearchByNameAdapter doctorSearchByNameAdapter;
    private String mClickedSpecialityOfDoctor;


    public PatientSearchFragment() {
    }

    public static PatientSearchFragment newInstance() {
        PatientSearchFragment fragment = new PatientSearchFragment();

        fragment.setArguments(new Bundle());

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.global_connect_recycle_view_layout, container, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mClickedSpecialityOfDoctor = arguments.getString(getString(R.string.clicked_item_data));
        }

        unbinder = ButterKnife.bind(this, mRootView);
        //   init();
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void setOnClickOfSearchBar(String searchText) {
        doctorSearchByNameAdapter.getFilter().filter(searchText);
    }

}
