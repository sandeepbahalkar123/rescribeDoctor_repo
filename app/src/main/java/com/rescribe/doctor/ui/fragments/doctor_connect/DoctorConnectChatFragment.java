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
import com.rescribe.doctor.adapters.DoctorConnectChatAdapter;
import com.rescribe.doctor.model.doctor_connect_chat.ChatList;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by jeetal on 6/9/17.
 */

public class DoctorConnectChatFragment extends Fragment {
    @BindView(R.id.listView)
    RecyclerView mRecyclerView;
    @BindView(R.id.emptyListView)
    RelativeLayout emptyListView;
    Unbinder unbinder;
    DoctorConnectChatAdapter mDoctorConnectChatAdapter;
    private View mRootView;
    private ArrayList<ChatList> chatLists;


    public static DoctorConnectChatFragment newInstance(ArrayList<ChatList> chatLists) {
        DoctorConnectChatFragment fragment = new DoctorConnectChatFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(RescribeConstants.CHAT_REQUEST, chatLists);
        fragment.setArguments(args);
        return fragment;
    }

    public DoctorConnectChatFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.global_connect_recycle_view_layout, container, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            chatLists = getArguments().getParcelableArrayList(RescribeConstants.CHAT_REQUEST);
        }

        unbinder = ButterKnife.bind(this, mRootView);
        init();
        return mRootView;
    }

    private void init() {
        for (int i = 0; i < chatLists.size(); i++) {
            String doctorName = chatLists.get(i).getDoctorName();
            if (doctorName.startsWith(getString(R.string.dr))) {
                chatLists.get(i).setDoctorName(doctorName);
            } else {
                String drName = getString(R.string.dr) + doctorName;
                chatLists.get(i).setDoctorName(drName);
            }
        }
        mDoctorConnectChatAdapter = new DoctorConnectChatAdapter(getActivity(), chatLists);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setAdapter(mDoctorConnectChatAdapter);
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

}

