package com.rescribe.doctor.adapters.my_appointments;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.rescribe.doctor.R;
import com.rescribe.doctor.model.doctor_location.DoctorLocationModel;
import com.rescribe.doctor.model.login.ClinicList;
import com.rescribe.doctor.model.patient.template_sms.TemplateList;
import com.rescribe.doctor.ui.customesViews.CustomTextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jeetal on 27/2/18.
 */

public class DialogClinicListAdapter extends RecyclerView.Adapter<DialogClinicListAdapter.ListViewHolder> {

    private Context mContext;
    private ArrayList<DoctorLocationModel> mTemplateLists;
    private OnClickOfRadioButton mOnClickOfRadioButton;

    public DialogClinicListAdapter(Context mContext, ArrayList<DoctorLocationModel> mTemplateLists, OnClickOfRadioButton mOnClickOfRadioButton) {
        this.mTemplateLists = mTemplateLists;
        this.mContext = mContext;
        this.mOnClickOfRadioButton = mOnClickOfRadioButton;
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dialog_location_item_layout, parent, false);

        return new ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListViewHolder holder, int position) {
        final DoctorLocationModel doctorLocationModelObject = mTemplateLists.get(position);

        holder.radioButtonClinicName.setText(doctorLocationModelObject.getClinicName() + ", " + doctorLocationModelObject.getAddress());
        holder.radioButtonClinicName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickOfRadioButton.onRadioButtonClick(doctorLocationModelObject);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTemplateLists.size();
    }

    static class ListViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.radioButtonClinicName)
        RadioButton radioButtonClinicName;
        @BindView(R.id.radioGroup)
        RadioGroup radioGroup;
        View view;

        ListViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }
    }

    public interface OnClickOfRadioButton {
        void onRadioButtonClick(DoctorLocationModel clinicList);
    }

}
