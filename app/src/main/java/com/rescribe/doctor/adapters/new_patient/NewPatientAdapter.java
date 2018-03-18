package com.rescribe.doctor.adapters.new_patient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.rescribe.doctor.R;
import com.rescribe.doctor.model.new_patient.NewPatientsDetail;
import com.rescribe.doctor.model.patient.patient_connect.PatientData;
import com.rescribe.doctor.ui.activities.ChatActivity;
import com.rescribe.doctor.ui.customesViews.CircularImageView;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.rescribe.doctor.util.CommonMethods.toCamelCase;

/**
 * Created by jeetal on 17/3/18.
 */

public class NewPatientAdapter extends RecyclerView.Adapter<NewPatientAdapter.ListViewHolder> implements Filterable {

    private Context mContext;
    private ArrayList<NewPatientsDetail> mDataList;
    private ArrayList<NewPatientsDetail> mOriginalPatientList;
    public boolean isLongPressed;
    private NewPatientAdapter.OnDownArrowClicked mOnDownArrowClicked;


    public NewPatientAdapter(Context mContext, ArrayList<NewPatientsDetail> dataList, NewPatientAdapter.OnDownArrowClicked mOnDownArrowClicked) {
        this.mDataList = new ArrayList<>(dataList);
        this.mOriginalPatientList = new ArrayList<>(dataList);
        this.mContext = mContext;
        this.mOnDownArrowClicked = mOnDownArrowClicked;
    }

    @Override
    public NewPatientAdapter.ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_appointments_child_item, parent, false);

        return new NewPatientAdapter.ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final NewPatientAdapter.ListViewHolder holder, final int position) {
        final NewPatientsDetail patientObject = mDataList.get(position);
        holder.opdTypeTextView.setVisibility(View.VISIBLE);
        holder.patientClinicAddress.setVisibility(View.VISIBLE);
        String patientName = toCamelCase(patientObject.getPatientName());
        holder.chatImageView.setVisibility(View.GONE);
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

            if (patientObject.getPatientPhon().toLowerCase().contains(patientObject.getSpannableString().toLowerCase())) {
                SpannableString spannablePhoneString = new SpannableString(patientObject.getPatientPhon());
                Pattern pattern = Pattern.compile(patientObject.getSpannableString(), Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(patientObject.getPatientPhon());
                while (matcher.find()) {
                    spannablePhoneString.setSpan(new ForegroundColorSpan(
                                    ContextCompat.getColor(mContext, R.color.tagColor)),
                            matcher.start(), matcher.end(),//hightlight mSearchString
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                holder.patientPhoneNumber.setText(spannablePhoneString);
            } else {
                holder.patientPhoneNumber.setText(patientObject.getPatientPhon());
            }
            //TODO:
            //Spannable condition for PatientId
            if (String.valueOf(patientObject.getHospitalPatId()).contains(patientObject.getSpannableString())) {

                SpannableString spannableIdString = new SpannableString(mContext.getString(R.string.id) + " " + String.valueOf(patientObject.getHospitalPatId()));
                Pattern pattern = Pattern.compile(patientObject.getSpannableString(), Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(mContext.getString(R.string.id) + " " + String.valueOf(patientObject.getHospitalPatId()));

                while (matcher.find()) {
                    spannableIdString.setSpan(new ForegroundColorSpan(
                                    ContextCompat.getColor(mContext, R.color.tagColor)),
                            matcher.start(), matcher.end(),//hightlight mSearchString
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                holder.patientIdTextView.setText(spannableIdString);
            } else {

                holder.patientIdTextView.setText(mContext.getString(R.string.id) + " " + String.valueOf(patientObject.getHospitalPatId()));
            }
        } else {
            holder.patientNameTextView.setText(patientName);
            holder.patientPhoneNumber.setText(patientObject.getPatientPhon());
            holder.patientIdTextView.setText(mContext.getString(R.string.id) + " " + String.valueOf(patientObject.getHospitalPatId()));
        }

        if (patientObject.getAge().equals("") && !patientObject.getPatientDob().equals("")) {
            holder.patientAgeTextView.setVisibility(View.VISIBLE);
            String getTodayDate = CommonMethods.getCurrentDate();
            String getBirthdayDate = patientObject.getPatientDob();
            DateTime todayDateTime = CommonMethods.convertToDateTime(getTodayDate);
            DateTime birthdayDateTime = CommonMethods.convertToDateTime(getBirthdayDate);
            holder.patientAgeTextView.setText(CommonMethods.displayAgeAnalysis(todayDateTime, birthdayDateTime) + " " + mContext.getString(R.string.years));
        } else if (!patientObject.getAge().equals("")) {
            holder.patientAgeTextView.setVisibility(View.VISIBLE);
            holder.patientAgeTextView.setText(patientObject.getAge() + " " + mContext.getString(R.string.years));
        } else {
            holder.patientAgeTextView.setVisibility(View.GONE);
        }

        holder.patientGenderTextView.setText(" " + CommonMethods.toCamelCase(patientObject.getPatientGender()));
        holder.outstandingAmountTextView.setText(mContext.getString(R.string.outstanding_amount) + " ");
        if (patientObject.getOutstandingAmount().equals(0)) {
            holder.payableAmountTextView.setText(" " + mContext.getString(R.string.nil));
            holder.payableAmountTextView.setTextColor(ContextCompat.getColor(mContext, R.color.rating_color));

        } else {
            holder.payableAmountTextView.setText(" Rs." + patientObject.getOutstandingAmount() + "/-");
            holder.payableAmountTextView.setTextColor(ContextCompat.getColor(mContext, R.color.Red));

        }

        TextDrawable textDrawable = CommonMethods.getTextDrawable(mContext, patientObject.getPatientName());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.dontAnimate();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(true);
        requestOptions.placeholder(textDrawable);
        requestOptions.error(textDrawable);

        Glide.with(mContext)
                .load(patientObject.getProfilePhoto())
                .apply(requestOptions).thumbnail(0.5f)
                .into(holder.patientImageView);
        holder.checkbox.setChecked(patientObject.isSelected());

        holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                patientObject.setSelected(holder.checkbox.isChecked());
                mOnDownArrowClicked.onCheckUncheckRemoveSelectAllSelection(holder.checkbox.isChecked(), patientObject);
            }
        });
        if (isLongPressed)
            holder.checkbox.setVisibility(View.VISIBLE);
        else holder.checkbox.setVisibility(View.GONE);

        holder.patientDetailsClickLinearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isLongPressed = !isLongPressed;
                mOnDownArrowClicked.onLongPressOpenBottomMenu(isLongPressed, position);
                notifyDataSetChanged();
                return false;
            }
        });
        holder.patientDetailsClickLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnDownArrowClicked.onClickOfPatientDetails(patientObject, holder.patientAgeTextView.getText().toString() + holder.patientGenderTextView.getText().toString());
            }
        });

        holder.chatImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                PatientData doctorConnectChatModel = new PatientData();
                doctorConnectChatModel.setId(patientObject.getPatientID());
                doctorConnectChatModel.setImageUrl(patientObject.getProfilePhoto());
                doctorConnectChatModel.setPatientName(patientObject.getPatientName());
                intent.putExtra(RescribeConstants.PATIENT_INFO, doctorConnectChatModel);
                ((Activity) mContext).startActivityForResult(intent, Activity.RESULT_OK);

            }
        });
        holder.patientPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnDownArrowClicked.onPhoneNoClick(patientObject.getPatientPhon());
            }
        });


    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public ArrayList<NewPatientsDetail> getGroupList() {
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
        @BindView(R.id.patientDetailsClickLinearLayout)
        RelativeLayout patientDetailsClickLinearLayout;

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

                ArrayList<NewPatientsDetail> mListToShowAfterFilter;
                ArrayList<NewPatientsDetail> mTempPatientListToIterate = new ArrayList<>(mOriginalPatientList);

                if (charString.isEmpty()) {
                    mListToShowAfterFilter = new ArrayList<>();
                    for (NewPatientsDetail patientListObject : mTempPatientListToIterate) {
                        //--------
                        patientListObject.setSpannableString(null);
                        mListToShowAfterFilter.add(patientListObject);

                    }
                } else {
                    mListToShowAfterFilter = new ArrayList<>();
                    for (NewPatientsDetail patientListObject : mTempPatientListToIterate) {

                        if (patientListObject.getPatientName().toLowerCase().contains(charString.toLowerCase())
                                || patientListObject.getPatientPhon().contains(charString)
                                || String.valueOf(patientListObject.getHospitalPatId()).contains(charString)) {
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
                mDataList.addAll((ArrayList<NewPatientsDetail>) filterResults.values);

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

        void onCheckUncheckRemoveSelectAllSelection(boolean ischecked, NewPatientsDetail patientObject);

        void onClickOfPatientDetails(NewPatientsDetail patientListObject, String text);
        void onPhoneNoClick(String patientPhone);

    }



}