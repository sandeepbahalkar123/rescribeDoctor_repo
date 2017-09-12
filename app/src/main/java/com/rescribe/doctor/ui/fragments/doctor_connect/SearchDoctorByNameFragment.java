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
import com.rescribe.doctor.adapters.DoctorSearchByNameAdapter;
import com.rescribe.doctor.model.parceable_doctor_connect.ConnectList;
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

public class SearchDoctorByNameFragment extends Fragment implements DoctorConnectActivity.OnClickOfSearchBar {
    @BindView(R.id.listView)
    RecyclerView mRecyclerView;
    @BindView(R.id.emptyListView)
    RelativeLayout emptyListView;
    Unbinder unbinder;
    private View mRootView;
    private ArrayList<ConnectList> mReceivedList;
    private DoctorSearchByNameAdapter doctorSearchByNameAdapter;
    private String mClickedSpecialityOfDoctor;


    public SearchDoctorByNameFragment() {
    }

    public static SearchDoctorByNameFragment newInstance(ArrayList<ConnectList> connectLists, Bundle bundleData) {
        SearchDoctorByNameFragment fragment = new SearchDoctorByNameFragment();
        if (bundleData == null) {
            bundleData = new Bundle();
        }
        bundleData.putParcelableArrayList(RescribeConstants.CONNECT_REQUEST, connectLists);
        fragment.setArguments(bundleData);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.doctor_connect_recycle_view_layout, container, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mClickedSpecialityOfDoctor = arguments.getString(getString(R.string.clicked_item_data));
            mReceivedList = getArguments().getParcelableArrayList(RescribeConstants.CONNECT_REQUEST);
        }

        unbinder = ButterKnife.bind(this, mRootView);
        init();
        return mRootView;
    }

    private void init() {
        mRecyclerView.setVisibility(View.VISIBLE);
        doctorSearchByNameAdapter = new DoctorSearchByNameAdapter(getActivity(), filterDataOnDocSpeciality());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setAdapter(doctorSearchByNameAdapter);
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

    private ArrayList<ConnectList> filterDataOnDocSpeciality() {
        ArrayList<ConnectList> connectLists = this.mReceivedList;
        ArrayList<ConnectList> dataList = new ArrayList<>();
        if (mClickedSpecialityOfDoctor == null) {
            return connectLists;
        } else {
            for (ConnectList listObject :
                    connectLists) {
                if (mClickedSpecialityOfDoctor.equalsIgnoreCase(listObject.getSpecialization())) {
                    dataList.add(listObject);
                }
            }
        }

        return dataList;
    }
}
