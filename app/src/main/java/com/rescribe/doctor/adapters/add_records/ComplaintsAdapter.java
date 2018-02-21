package com.rescribe.doctor.adapters.add_records;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.rescribe.doctor.R;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.ui.customesViews.SwitchButton;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jeetal on 19/2/18.
 */

public class ComplaintsAdapter extends RecyclerView.Adapter<ComplaintsAdapter.ListViewHolder> {


    private Context mContext;
    private String[] mDataListHospitalDetails = {"P.D.Hinduja National Hospital, Mumbai", "Pain Clinic, Pune"};
    private String[] timingList = {"From 02:30 pm", "From 08:00 am"};
    private String[] opdList = {"Complaints", "Vitals", "Prescription", "Diagnosis", "Investigations", "Advice", "Pain Score"};
    private Integer[] mIntegers = {R.drawable.complaints, R.drawable.vitals, R.drawable.prescription, R.drawable.diagnosis, R.drawable.investigations, R.drawable.advice, R.drawable.painscore};

    public ComplaintsAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dashboard_menu_common_layout
                        , parent, false);

        return new ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListViewHolder holder, int position) {

        //TODO : NEED TO IMPLEMENT
        holder.menuNameTextView2.setVisibility(View.VISIBLE);
        holder.menuNameTextView.setVisibility(View.GONE);
        holder.menuNameTextView2.setText(opdList[position]);
        holder.menuImageView.setImageResource(mIntegers[position]);
        holder.menuNameTextView.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        holder.menuOptionLinearLayout.setBackgroundColor(ContextCompat.getColor(mContext,R.color.toolbar_divider_color));
    }

    @Override
    public int getItemCount() {
        return opdList.length;
    }

    static class ListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.menuImageView)
        ImageView menuImageView;
        @BindView(R.id.menuNameTextView)
        CustomTextView menuNameTextView;
        @BindView(R.id.dashboardArrowImageView)
        ImageView dashboardArrowImageView;
        @BindView(R.id.radioSwitch)
        SwitchButton radioSwitch;
        @BindView(R.id.menuOptionLinearLayout)
        LinearLayout menuOptionLinearLayout;
        @BindView(R.id.menuNameTextView2)
        CustomTextView menuNameTextView2;
        View view;

        ListViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }
    }
}
