package com.rescribe.doctor.adapters.my_patients;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
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
import com.rescribe.doctor.ui.customesViews.CircularImageView;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.util.CommonMethods;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public MyPatientsAdapter(Context mContext, ArrayList<PatientList> dataList, OnDownArrowClicked mOnDownArrowClicked) {
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
        holder.opdTypeTextView.setVisibility(View.GONE);
        holder.patientClinicAddress.setVisibility(View.VISIBLE);
        holder.patientClinicAddress.setText(patientObject.getClinicName()+ " - " + patientObject.getPatientCity());
        String patientName = "";
        if (patientObject.getSalutation() == 1) {
            patientName = mContext.getString(R.string.mr) + " " + patientObject.getPatientName();
        } else if (patientObject.getSalutation() == 2) {
            patientName = mContext.getString(R.string.mrs) + " " + patientObject.getPatientName();

        } else if (patientObject.getSalutation() == 3) {
            patientName = mContext.getString(R.string.miss) + " " + patientObject.getPatientName();

        } else if (patientObject.getSalutation() == 4) {
            patientName = patientObject.getPatientName();
        }


        if (patientObject.getSpannableString() != null) {
            //Spannable condition for PatientName
            if (patientObject.getPatientName().toLowerCase().contains(patientObject.getSpannableString().toLowerCase())) {
                SpannableString spannableString = new SpannableString(patientName);
                Pattern pattern = Pattern.compile(patientObject.getSpannableString(), Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(patientName);
                while (matcher.find()) {
                    spannableString.setSpan(new ForegroundColorSpan(
                                    ContextCompat.getColor(mContext, R.color.tagColor)),
                            matcher.start(), matcher.end(),//hightlight mSearchString
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                holder.patientNameTextView.setText(spannableString);
            } else {
                holder.patientNameTextView.setText(patientName);
            }
            //Spannable condition for PatientPhoneNomber

            if (patientObject.getPatientPhone().toLowerCase().contains(patientObject.getSpannableString().toLowerCase())) {
                SpannableString spannablePhoneString = new SpannableString(patientObject.getPatientPhone());
                Pattern pattern = Pattern.compile(patientObject.getSpannableString(), Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(patientObject.getPatientPhone());
                while (matcher.find()) {
                    spannablePhoneString.setSpan(new ForegroundColorSpan(
                                    ContextCompat.getColor(mContext, R.color.tagColor)),
                            matcher.start(), matcher.end(),//hightlight mSearchString
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                holder.patientPhoneNumber.setText(spannablePhoneString);
            } else {
                holder.patientPhoneNumber.setText(patientObject.getPatientPhone());
            }
            //TODO:
            //Spannable condition for PatientId
            if (String.valueOf(patientObject.getHospitalPatId()).contains(patientObject.getSpannableString())) {
                SpannableString patientID = new SpannableString(mContext.getString(R.string.id) + " " + String.valueOf(patientObject.getHospitalPatId()));
                patientID.setSpan(new UnderlineSpan(), 0, patientID.length(), 0);
                SpannableString spannableIdString = new SpannableString(patientID);
                Pattern pattern = Pattern.compile(patientObject.getSpannableString(), Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(patientID);

                while (matcher.find()) {
                    spannableIdString.setSpan(new ForegroundColorSpan(
                                    ContextCompat.getColor(mContext, R.color.tagColor)),
                            matcher.start(), matcher.end(),//hightlight mSearchString
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                holder.patientIdTextView.setText(spannableIdString);
            } else {
                SpannableString patientID = new SpannableString(mContext.getString(R.string.id) + " " + patientObject.getHospitalPatId());
                patientID.setSpan(new UnderlineSpan(), 0, patientID.length(), 0);
                holder.patientIdTextView.setText(patientID);
            }
        } else {
            holder.patientNameTextView.setText(patientName);
            holder.patientPhoneNumber.setText(patientObject.getPatientPhone());
            SpannableString patientID = new SpannableString(mContext.getString(R.string.id) + " " + patientObject.getHospitalPatId());
            patientID.setSpan(new UnderlineSpan(), 0, patientID.length(), 0);
            holder.patientIdTextView.setText(patientID);
        }

        if (patientObject.getAge().equals("") && !patientObject.getDateOfBirth().equals("")) {
            holder.patientAgeTextView.setVisibility(View.VISIBLE);
            String getTodayDate = CommonMethods.getCurrentDate();
            String getBirthdayDate = patientObject.getDateOfBirth();
            DateTime todayDateTime = CommonMethods.convertToDateTime(getTodayDate);
            DateTime birthdayDateTime = CommonMethods.convertToDateTime(getBirthdayDate);
            holder.patientAgeTextView.setText(CommonMethods.displayAgeAnalysis(todayDateTime, birthdayDateTime) + " " + mContext.getString(R.string.years));
        } else if (!patientObject.getAge().equals("")) {
            holder.patientAgeTextView.setVisibility(View.VISIBLE);
            holder.patientAgeTextView.setText(patientObject.getAge() + " " + mContext.getString(R.string.years));
        } else {
            holder.patientAgeTextView.setVisibility(View.GONE);
        }

        holder.patientGenderTextView.setText(" " + patientObject.getGender());
        /*if (patientObject.getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.book))) {
            holder.opdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.book_color));
            holder.opdTypeTextView.setText(mContext.getString(R.string.opd_appointment) + " " + patientObject.getAppointmentStatus());
        } else if (patientObject.getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.completed))) {
            holder.opdTypeTextView.setText(mContext.getString(R.string.opd_appointment) + " " + patientObject.getAppointmentStatus());
            holder.opdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.complete_color));

        } else if (patientObject.getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.follow))) {
            holder.opdTypeTextView.setText(mContext.getString(R.string.opd_appointment) + " " + patientObject.getAppointmentStatus());
            holder.opdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.tagColor));

        }*/

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
                mOnDownArrowClicked.onCheckUncheckRemoveSelectAllSelection(holder.checkbox.isChecked());
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
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnDownArrowClicked.onClickOfPatientDetails(patientObject, holder.patientAgeTextView.getText().toString() + holder.patientGenderTextView.getText().toString());
            }
        });


    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public ArrayList<PatientList> getGroupList() {
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
        @BindView(R.id.patientClinicAddress)
        CustomTextView patientClinicAddress;

        View view;

        ListViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }
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
                    mListToShowAfterFilter = new ArrayList<>();
                    for (PatientList patientListObject : mTempPatientListToIterate) {
                        //--------
                        patientListObject.setSpannableString(null);
                        mListToShowAfterFilter.add(patientListObject);

                    }
                } else {
                    mListToShowAfterFilter = new ArrayList<>();
                    for (PatientList patientListObject : mTempPatientListToIterate) {

                        if (patientListObject.getPatientName().toLowerCase().contains(charString.toLowerCase())
                                || patientListObject.getPatientPhone().contains(charString)
                                || String.valueOf(patientListObject.getPatientId()).contains(charString)) {
                            //--------
                            patientListObject.setSpannableString(charString);
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

                if (mDataList.isEmpty()) {
                    mOnDownArrowClicked.onRecordFound(true);
                } else mOnDownArrowClicked.onRecordFound(false);
                notifyDataSetChanged();
            }
        };
    }

    public interface OnDownArrowClicked {

        void onLongPressOpenBottomMenu(boolean isLongPressed, int groupPosition);

        void onRecordFound(boolean isListEmpty);

        void onCheckUncheckRemoveSelectAllSelection(boolean ischecked);

        void onClickOfPatientDetails(PatientList patientListObject, String text);

    }


    /* public void addLoadingFooter() {
         isLoadingAdded = true;
         add(new PatientList());
     }

     public void removeLoadingFooter() {
         isLoadingAdded = false;

         int position = mDataList.size() - 1;
         PatientList item = getItem(position);

         if (item != null) {
             mDataList.remove(position);
             notifyItemRemoved(position);
         }
     }

     public void addAll(ArrayList<PatientList> mcList) {
         for (PatientList mc : mcList) {
             add(mc);
         }
     }
     public void add(PatientList mc) {
         mDataList.add(mc);
         notifyItemInserted(mDataList.size() - 1);
     }
     public PatientList getItem(int position) {
         return mDataList.get(position);
     }*/
    public void add(PatientList mc) {
        mDataList.add(mc);
        notifyItemInserted(mDataList.size() - 1);
    }

    public void addAll(ArrayList<PatientList> mcList) {

        for (PatientList mc : mcList) {
            add(mc);
        }
    }
    public void clear() {
        final int size = mDataList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                mDataList.remove(0);
            }

            notifyItemRangeRemoved(0, size);
        }
    }
}