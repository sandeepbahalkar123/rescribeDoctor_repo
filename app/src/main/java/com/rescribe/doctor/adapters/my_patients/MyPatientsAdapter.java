package com.rescribe.doctor.adapters.my_patients;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.rescribe.doctor.R;
import com.rescribe.doctor.helpers.doctor_patients.PatientList;
import com.rescribe.doctor.model.my_appointments.ClinicList;
import com.rescribe.doctor.ui.customesViews.CircularImageView;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.util.CommonMethods;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jeetal on 31/1/18.
 */

public class MyPatientsAdapter extends RecyclerView.Adapter<MyPatientsAdapter.ListViewHolder> implements Filterable {

    private Context mContext;
    private ArrayList<PatientList> mDataList;
    private ArrayList<PatientList> mOriginalPatientList;
    public boolean isLongPressed;
    private OnDownArrowClicked mOnDownArrowClicked;

    public MyPatientsAdapter(Context mContext, ArrayList<PatientList> dataList,OnDownArrowClicked mOnDownArrowClicked) {
        this.mDataList = new ArrayList<>(dataList);
        this.mOriginalPatientList = new ArrayList<>(dataList);
        this.mContext = mContext;
        this.mOnDownArrowClicked = mOnDownArrowClicked;
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_appointments_child_item, parent, false);

        return new ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListViewHolder holder, final int position) {
        final PatientList patientObject = mDataList.get(position);
        //TODO : NEED TO IMPLEMENT
        //  holder.timeSlot.setText(doctorObject.ge);
        SpannableString patientID = new SpannableString(mContext.getString(R.string.id) + " " + patientObject.getPatientId());
        patientID.setSpan(new UnderlineSpan(), 0, patientID.length(), 0);
        holder.patientIdTextView.setText(patientID);
        if (patientObject.getSalutation() == 1) {
            holder.patientNameTextView.setText(mContext.getString(R.string.mr) + " " + patientObject.getPatientName());
        } else if (patientObject.getSalutation() == 2) {
            holder.patientNameTextView.setText(mContext.getString(R.string.mrs) + " " + patientObject.getPatientName());

        } else if (patientObject.getSalutation() == 3) {
            holder.patientNameTextView.setText(mContext.getString(R.string.miss) + " " + patientObject.getPatientName());

        } else if (patientObject.getSalutation() == 4) {
            holder.patientNameTextView.setText(patientObject.getPatientName());
        }

        if (patientObject.getAge() == 0) {
            String getTodayDate = CommonMethods.getCurrentDate();
            String getBirthdayDate = patientObject.getDateOfBirth();
            DateTime todayDateTime = CommonMethods.convertToDateTime(getTodayDate);
            DateTime birthdayDateTime = CommonMethods.convertToDateTime(getBirthdayDate);
            holder.patientAgeTextView.setText(CommonMethods.displayAgeAnalysis(todayDateTime, birthdayDateTime) + " " + mContext.getString(R.string.years));
        } else {
            holder.patientAgeTextView.setText(patientObject.getAge() + " " + mContext.getString(R.string.years));

        }

        holder.patientGenderTextView.setText(" " + patientObject.getGender());
        if (patientObject.getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.booked))) {
            holder.opdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.book_color));
            holder.opdTypeTextView.setText(mContext.getString(R.string.opd) + " " + patientObject.getAppointmentStatus());
        } else if (patientObject.getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.completed))) {
            holder.opdTypeTextView.setText(mContext.getString(R.string.opd) + " " + patientObject.getAppointmentStatus());
            holder.opdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.complete_color));

        } else if (patientObject.getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.follow))) {
            holder.opdTypeTextView.setText(mContext.getString(R.string.opd) + " " + patientObject.getAppointmentStatus());
            holder.opdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.tagColor));

        }
        holder.patientPhoneNumber.setText(patientObject.getPatientPhone());
        holder.outstandingAmountTextView.setText(mContext.getString(R.string.outstanding_amount) + " ");
        if (patientObject.getOutStandingAmount() == 0) {
            holder.payableAmountTextView.setText(" " + mContext.getString(R.string.nil));
            holder.payableAmountTextView.setTextColor(ContextCompat.getColor(mContext, R.color.rating_color));

        } else {
            holder.payableAmountTextView.setText(" Rs." + patientObject.getOutStandingAmount() + "/-");
            holder.payableAmountTextView.setTextColor(ContextCompat.getColor(mContext, R.color.Red));

        }
        holder.chatImageView.setVisibility(View.VISIBLE);
        TextDrawable textDrawable = CommonMethods.getTextDrawable(mContext, patientObject.getPatientName());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.dontAnimate();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(true);
        requestOptions.placeholder(textDrawable);
        requestOptions.error(textDrawable);

        Glide.with(mContext)
                .load(patientObject.getPatientImageUrl())
                .apply(requestOptions).thumbnail(0.5f)
                .into(holder.patientImageView);
        holder.checkbox.setChecked(patientObject.isSelected());

        holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               patientObject.setSelected(holder.checkbox.isChecked());
                notifyDataSetChanged();
            }
        });
        if (isLongPressed)
            holder.checkbox.setVisibility(View.VISIBLE);
        else holder.checkbox.setVisibility(View.GONE);

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isLongPressed = !isLongPressed;
                mOnDownArrowClicked.onLongPressOpenBottomMenu(isLongPressed, position);
                notifyDataSetChanged();
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }
    public ArrayList<PatientList> getGroupList(){
        return mDataList;
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
        @BindView(R.id.checkbox)
        CheckBox checkbox;
        @BindView(R.id.patientPhoneNumber)
        CustomTextView patientPhoneNumber;
        @BindView(R.id.separatorView)
        View separatorView;
        @BindView(R.id.outstandingAmountTextView)
        CustomTextView outstandingAmountTextView;
        @BindView(R.id.payableAmountTextView)
        CustomTextView payableAmountTextView;
        @BindView(R.id.cardView)
        LinearLayout cardView;


        View view;

        ListViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }
    }
    public interface OnDownArrowClicked {

        void onLongPressOpenBottomMenu(boolean isLongPressed, int groupPosition);
        void onRecordFound(boolean isListEmpty);
    }
    public boolean isLongPressed() {
        return isLongPressed;
    }

    public void setLongPressed(boolean longPressed) {
        isLongPressed = longPressed;
    }
    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();

                ArrayList<PatientList> mListToShowAfterFilter;
                ArrayList<PatientList> mTempPatientListToIterate = new ArrayList<>(mOriginalPatientList);

                if (charString.isEmpty()) {
                    mListToShowAfterFilter = new ArrayList<>(mTempPatientListToIterate);
                } else {
                    mListToShowAfterFilter = new ArrayList<>();
                    for (PatientList patientListObject : mTempPatientListToIterate) {

                        if (patientListObject.getPatientName().toLowerCase().contains(charString.toLowerCase())
                                    || patientListObject.getPatientPhone().contains(charString)
                                    || String.valueOf(patientListObject.getPatientId()).contains(charString)) {
                                //--------
                               mListToShowAfterFilter.add(patientListObject);
                            }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mListToShowAfterFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mDataList.clear();
                mDataList.addAll((ArrayList<PatientList>) filterResults.values);

                if(mDataList.isEmpty()){
                    mOnDownArrowClicked.onRecordFound(true);
                }
                else mOnDownArrowClicked.onRecordFound(false);
                notifyDataSetChanged();
            }
        };
    }
}