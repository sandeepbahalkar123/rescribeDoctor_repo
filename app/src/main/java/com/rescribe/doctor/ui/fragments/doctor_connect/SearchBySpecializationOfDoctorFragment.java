package com.rescribe.doctor.ui.fragments.doctor_connect;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.DoctorConnectSearchAdapter;
import com.rescribe.doctor.helpers.doctor_connect.DoctorConnectSearchHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.doctor_connect_search.DoctorConnectSearchBaseModel;
import com.rescribe.doctor.model.parceable_doctor_connect_chat.ChatList;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.ui.activities.DoctorConnectActivity;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by jeetal on 6/9/17.
 */

public class SearchBySpecializationOfDoctorFragment extends Fragment implements HelperResponse, DoctorConnectSearchAdapter.OnDoctorSpecialityClickListener {
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
    private DoctorConnectSearchHelper doctorConnectSearchHelper;
    private DoctorConnectSearchAdapter doctorConnectAdapter;
    private OnAddFragmentListener mListener;


    public SearchBySpecializationOfDoctorFragment() {
    }

    public static SearchBySpecializationOfDoctorFragment newInstance(ArrayList<ChatList> chatLists) {
        SearchBySpecializationOfDoctorFragment fragment = new SearchBySpecializationOfDoctorFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(RescribeConstants.CHAT_REQUEST,chatLists);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.doctor_connect_recycle_view_layout, container, false);

        Bundle arguments = getArguments();
        if (arguments != null) {
        }

        unbinder = ButterKnife.bind(this, mRootView);
        init();
        return mRootView;
    }

    private void init() {
        pickSpeciality.setVisibility(View.VISIBLE);
        displayNote.setVisibility(View.VISIBLE);
        doctorConnectSearchHelper = new DoctorConnectSearchHelper(getActivity(), this);
        doctorConnectSearchHelper.getDoctorSpecialityList();


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
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        DoctorConnectSearchBaseModel doctorConnectSearchBaseModel = (DoctorConnectSearchBaseModel) customResponse;

        doctorConnectAdapter = new DoctorConnectSearchAdapter(getActivity(), this, doctorConnectSearchBaseModel.getData());
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        mRecyclerView.setLayoutManager(layoutManager);
      /*  ViewGroup.MarginLayoutParams marginLayoutParams =
                (ViewGroup.MarginLayoutParams) mRecyclerView.getLayoutParams();
        marginLayoutParams.setMargins(0, CommonMethods.convertDpToPixel(30), 0, CommonMethods.convertDpToPixel(16));*/

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(doctorConnectAdapter);
    }

    @Override
    public void onParseError(String mOldDataTag, String errorMessage) {

    }

    @Override
    public void onServerError(String mOldDataTag, String serverErrorMessage) {

    }

    @Override
    public void onNoConnectionError(String mOldDataTag, String serverErrorMessage) {

    }

    @Override
    public void setOnClickOfDoctorSpeciality() {
        mListener.addSearchDoctorByNameFragment();
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
        void addSearchDoctorByNameFragment();
    }

}
