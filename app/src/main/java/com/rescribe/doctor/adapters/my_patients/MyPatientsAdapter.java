package com.rescribe.doctor.adapters.my_patients;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.rescribe.doctor.R;
import com.rescribe.doctor.helpers.doctor_patients.PatientList;
import com.rescribe.doctor.ui.customesViews.CircularImageView;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jeetal on 31/1/18.
 */

public class MyPatientsAdapter extends RecyclerView.Adapter<MyPatientsAdapter.ListViewHolder> {

    private Context mContext;
    private List<PatientList> mDataList;

    public MyPatientsAdapter(Context mContext, List<PatientList> dataList) {
        this.mDataList = dataList;
        this.mContext = mContext;
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_appointments_child_item, parent, false);

        return new ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListViewHolder holder, int position) {
        PatientList doctorObject = mDataList.get(position);
        //TODO : NEED TO IMPLEMENT
        //  holder.timeSlot.setText(doctorObject.ge);
        SpannableString patientID = new SpannableString(mContext.getString(R.string.id) + " " + doctorObject.getPatientId());
        patientID.setSpan(new UnderlineSpan(), 0, patientID.length(), 0);
        holder.patientIdTextView.setText(patientID);
        if (doctorObject.getSalutation() == 1) {
            holder.patientNameTextView.setText(mContext.getString(R.string.mr) +" "+ doctorObject.getPatientName());
        } else if (doctorObject.getSalutation() == 2) {
            holder.patientNameTextView.setText(mContext.getString(R.string.mrs) + " "+ doctorObject.getPatientName());

        } else if (doctorObject.getSalutation() == 3) {
            holder.patientNameTextView.setText(mContext.getString(R.string.miss) +" "+  doctorObject.getPatientName());

        } else if (doctorObject.getSalutation() == 4) {
            holder.patientNameTextView.setText(doctorObject.getPatientName());
        }

        if(doctorObject.getAge()==0){
            String getTodayDate = CommonMethods.getCurrentDate();
            String getBirthdayDate = doctorObject.getDateOfBirth();
            DateTime todayDateTime = CommonMethods.convertToDateTime(getTodayDate);
            DateTime birthdayDateTime = CommonMethods.convertToDateTime(getBirthdayDate);
            holder.patientAgeTextView.setText(CommonMethods.displayAgeAnalysis(todayDateTime, birthdayDateTime)+" "+ mContext.getString(R.string.years));
        }else{
            holder.patientAgeTextView.setText(doctorObject.getAge() +" "+ mContext.getString(R.string.years));

        }

        holder. patientGenderTextView.setText(" " + doctorObject.getGender());
        if (doctorObject.getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.booked))) {
            holder.opdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.book_color));
            holder.opdTypeTextView.setText(mContext.getString(R.string.opd) + " " + doctorObject.getAppointmentStatus());
        } else if (doctorObject.getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.completed))) {
            holder.opdTypeTextView.setText(mContext.getString(R.string.opd) + " " + doctorObject.getAppointmentStatus());
            holder.opdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.complete_color));

        } else if (doctorObject.getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.follow))) {
            holder.opdTypeTextView.setText(mContext.getString(R.string.opd) + " " + doctorObject.getAppointmentStatus());
            holder.opdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.tagColor));

        }
        holder.patientPhoneNumber.setText(doctorObject.getPatientPhone());
        holder.outstandingAmountTextView.setText(mContext.getString(R.string.outstanding_amount) + " ");
        if (doctorObject.getOutStandingAmount() == 0) {
            holder.payableAmountTextView.setText(" " + mContext.getString(R.string.nil));
            holder.payableAmountTextView.setTextColor(ContextCompat.getColor(mContext, R.color.rating_color));

        } else {
            holder.payableAmountTextView.setText(" Rs." + doctorObject.getOutStandingAmount() + "/-");
            holder.payableAmountTextView.setTextColor(ContextCompat.getColor(mContext, R.color.Red));

        }
        holder.chatImageView.setVisibility(View.VISIBLE);
        TextDrawable textDrawable = CommonMethods.getTextDrawable(mContext, doctorObject.getPatientName());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.dontAnimate();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(true);
        requestOptions.placeholder(textDrawable);
        requestOptions.error(textDrawable);

        Glide.with(mContext)
                .load(doctorObject.getPatientImageUrl())
                .apply(requestOptions).thumbnail(0.5f)
                .into( holder.patientImageView);



    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    static class ListViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.bluelineImageView)
        ImageView bluelineImageView;
        @BindView(R.id.patientIdTextView)
        CustomTextView patientIdTextView;
        @BindView(R.id.appointmentTime)
        CustomTextView appointmentTime;
        @BindView(R.id.chatImageView)
        ImageView chatImageView;
        @BindView(R.id.patientImageView)
        CircularImageView patientImageView;
        @BindView(R.id.patientNameTextView)
        CustomTextView patientNameTextView;
        @BindView(R.id.patientAgeTextView)
        CustomTextView patientAgeTextView;
        @BindView(R.id.patientGenderTextView)
        CustomTextView patientGenderTextView;
        @BindView(R.id.patientDetailsLinearLayout)
        LinearLayout patientDetailsLinearLayout;
        @BindView(R.id.opdTypeTextView)
        CustomTextView opdTypeTextView;
        @BindView(R.id.patientPhoneNumber)
        CustomTextView patientPhoneNumber;
        @BindView(R.id.separatorView)
        View separatorView;
        @BindView(R.id.outstandingAmountTextView)
        CustomTextView outstandingAmountTextView;
        @BindView(R.id.payableAmountTextView)
        CustomTextView payableAmountTextView;

        View view;

        ListViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }
    }
}