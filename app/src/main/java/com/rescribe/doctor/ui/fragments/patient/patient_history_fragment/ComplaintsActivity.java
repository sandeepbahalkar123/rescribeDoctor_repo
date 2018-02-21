package com.rescribe.doctor.ui.fragments.patient.patient_history_fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;

import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.add_records.ComplaintsAdapter;
import com.rescribe.doctor.adapters.dashboard.WaitingOrAppointmentListAdapter;
import com.rescribe.doctor.ui.activities.HomePageActivity;
import com.rescribe.doctor.ui.activities.my_appointments.MyAppointmentsActivity;
import com.rescribe.doctor.ui.customesViews.CustomTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jeetal on 19/2/18.
 */

public class ComplaintsActivity extends AppCompatActivity {

    @BindView(R.id.backImageView)
    ImageView backImageView;
    @BindView(R.id.titleTextView)
    CustomTextView titleTextView;
    @BindView(R.id.userInfoTextView)
    CustomTextView userInfoTextView;
    @BindView(R.id.dateTextview)
    CustomTextView dateTextview;
    @BindView(R.id.year)
    Spinner year;
    @BindView(R.id.addImageView)
    ImageView addImageView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private ComplaintsAdapter mComplaintsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complaint_activity);
        ButterKnife.bind(this);
        mComplaintsAdapter = new ComplaintsAdapter(this);
        LinearLayoutManager linearlayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearlayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        // off recyclerView Animation
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator)
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(mComplaintsAdapter);
        titleTextView.setText("Mr. Rohit Patil");
        userInfoTextView.setVisibility(View.VISIBLE);
        userInfoTextView.setText("33 yrs - MALE");
        dateTextview.setVisibility(View.VISIBLE);
        dateTextview.setText("12th Jan'18");
    }

    @OnClick({R.id.backImageView, R.id.titleTextView, R.id.userInfoTextView})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backImageView:
                onBackPressed();
                break;
            case R.id.titleTextView:
                break;
            case R.id.userInfoTextView:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
