package com.rescribe.doctor.ui.fragments.doctor_connect;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.DoctorConnectSearchAdapter;
import com.rescribe.doctor.model.doctor_connect_search.DoctorSpeciality;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.ui.customesViews.GridSpacingItemDecoration;
import com.rescribe.doctor.ui.customesViews.SpacesItemDecoration;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by jeetal on 6/9/17.
 */

public class SearchBySpecializationOfDoctorFragment extends Fragment implements DoctorConnectSearchAdapter.OnDoctorSpecialityClickListener {
    @BindView(R.id.listView)
    RecyclerView mRecyclerView;
    @BindView(R.id.emptyListView)
    RelativeLayout emptyListView;
    Unbinder unbinder;
    @BindView(R.id.pickSpeciality)
    CustomTextView pickSpeciality;
    @BindView(R.id.displayNote)
    RelativeLayout displayNote;
    @BindView(R.id.fragmentContainer)
    RelativeLayout fragmentContainer;
    private View mRootView;
    private DoctorConnectSearchAdapter doctorConnectAdapter;
    private OnAddFragmentListener mListener;
    private ArrayList<DoctorSpeciality> searchDataModelList;


    public SearchBySpecializationOfDoctorFragment() {
    }

    public static SearchBySpecializationOfDoctorFragment newInstance(ArrayList<DoctorSpeciality> searchDataModels) {
        SearchBySpecializationOfDoctorFragment fragment = new SearchBySpecializationOfDoctorFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(RescribeConstants.SEARCH__REQUEST,searchDataModels);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.global_connect_recycle_view_layout, container, false);

        Bundle arguments = getArguments();
        if (arguments != null) {
            searchDataModelList = getArguments().getParcelableArrayList(RescribeConstants.SEARCH__REQUEST);
        }

        unbinder = ButterKnife.bind(this, mRootView);
        init();
        return mRootView;
    }

    private void init() {
        pickSpeciality.setVisibility(View.VISIBLE);
        displayNote.setVisibility(View.VISIBLE);
        doctorConnectAdapter = new DoctorConnectSearchAdapter(getActivity(), this, searchDataModelList);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(doctorConnectAdapter);
        int spanCount = 3; // 3 columns
        int spacing = 50; // 50px
        boolean includeEdge = true;
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

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
    public void setOnClickOfDoctorSpeciality(Bundle bundleData) {
        mListener.addSearchDoctorByNameFragment(bundleData);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAddFragmentListener) {
            mListener = (OnAddFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAddFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnAddFragmentListener {
        void addSearchDoctorByNameFragment(Bundle bundleData);
    }

}