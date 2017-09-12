package com.rescribe.doctor.ui.fragments.doctor_connect;

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
import com.rescribe.doctor.adapters.DoctorConnectAdapter;
import com.rescribe.doctor.model.doctor_connect.ConnectList;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by jeetal on 5/9/17.
 */
public class DoctorConnectFragment extends Fragment {
    private static final String DATA = "DATA";
    @BindView(R.id.listView)
    RecyclerView mRecyclerView;
    @BindView(R.id.emptyListView)
    RelativeLayout emptyListView;
    Unbinder unbinder;
    private View mRootView;
    private DoctorConnectAdapter doctorConnectAdapter;
    private ArrayList<ConnectList> connectLists;


    public DoctorConnectFragment() {
    }

    public static DoctorConnectFragment newInstance(ArrayList<ConnectList> connectLists) {
        DoctorConnectFragment fragment = new DoctorConnectFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(RescribeConstants.CONNECT_REQUEST, connectLists);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.doctor_connect_recycle_view_layout, container, false);


        Bundle arguments = getArguments();
        if (arguments != null) {
            connectLists = getArguments().getParcelableArrayList(RescribeConstants.CONNECT_REQUEST);
        }
        unbinder = ButterKnife.bind(this, mRootView);
        init();
        return mRootView;
    }

    private void init() {
        doctorConnectAdapter = new DoctorConnectAdapter(getActivity(), connectLists);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setAdapter(doctorConnectAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        //setDoctorListAdapter();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
