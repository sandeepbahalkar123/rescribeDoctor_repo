package com.rescribe.doctor.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.rescribe.doctor.R;
import com.rescribe.doctor.model.doctor_connect_search.DoctorConnectSearchModel;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.ui.fragments.LoginFragment;
import com.rescribe.doctor.ui.fragments.doctor_connect.DoctorConnectFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by jeetal on 6/9/17.
 */

public class DoctorConnectSearchAdapter extends RecyclerView.Adapter<DoctorConnectSearchAdapter.ListViewHolder> {

    private Context mContext;
    private List<DoctorConnectSearchModel> doctorConnectSearchModels;
    private OnDoctorSpecialityClickListener mOnDoctorSpecialityClickListener;


    static class ListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.specialityImage)
        ImageView specialityImage;
        @BindView(R.id.specialityName)
        CustomTextView specialityName;
        @BindView(R.id.specialityOfDoctor)
        LinearLayout specialityOfDoctor;

        View view;

        ListViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }
    }


    public DoctorConnectSearchAdapter(Context mContext, OnDoctorSpecialityClickListener mOnDoctorSpecialityClickListener, List<DoctorConnectSearchModel> appointmentsList) {
        this.doctorConnectSearchModels = appointmentsList;
        this.mContext = mContext;
        this.mOnDoctorSpecialityClickListener = mOnDoctorSpecialityClickListener;
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.doctor_connect_grid_search, parent, false);

        return new ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        final DoctorConnectSearchModel doctorConnectSearchModel = doctorConnectSearchModels.get(position);
        holder.specialityName.setText(doctorConnectSearchModel.getSpeciality());
        holder.specialityOfDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString(mContext.getString(R.string.clicked_item_data), doctorConnectSearchModel.getSpeciality());
                mOnDoctorSpecialityClickListener.setOnClickOfDoctorSpeciality(b);
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctorConnectSearchModels.size();
    }

    public interface OnDoctorSpecialityClickListener {
        void setOnClickOfDoctorSpeciality(Bundle bundleData);
    }


}
