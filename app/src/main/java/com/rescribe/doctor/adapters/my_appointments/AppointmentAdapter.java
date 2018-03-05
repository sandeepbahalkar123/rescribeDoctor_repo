package com.rescribe.doctor.adapters.my_appointments;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
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
import com.rescribe.doctor.model.my_appointments.AppointmentList;
import com.rescribe.doctor.model.my_appointments.PatientList;
import com.rescribe.doctor.ui.customesViews.CircularImageView;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.ui.fragments.my_appointments.MyAppointmentsFragment;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jeetal on 31/1/18.
 */

public class AppointmentAdapter extends BaseExpandableListAdapter implements Filterable {

    private OnDownArrowClicked mOnDownArrowClicked;
    public static boolean isLongPressed;
    private ArrayList<AppointmentList> mAppointmentListTemp;
    private Context mContext;
    private ArrayList<AppointmentList> mDataList;

    public AppointmentAdapter(Context context, ArrayList<AppointmentList> mAppointmentList, OnDownArrowClicked mOnDownArrowClicked) {
        this.mContext = context;
        this.mDataList = new ArrayList<>(mAppointmentList);
        this.mAppointmentListTemp = new ArrayList<>(mAppointmentList);
        this.mOnDownArrowClicked = mOnDownArrowClicked;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.mAppointmentListTemp.get(groupPosition).getPatientList().get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final ChildViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.my_appointments_child_item, null);
            viewHolder = new ChildViewHolder();
            viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
            viewHolder.patientDetailsClickLinearLayout = (RelativeLayout)convertView.findViewById(R.id.patientDetailsClickLinearLayout);
            viewHolder.bluelineImageView = (ImageView) convertView.findViewById(R.id.bluelineImageView);
            viewHolder.appointmentTime = (CustomTextView) convertView.findViewById(R.id.appointmentTime);
            viewHolder.patientIdTextView = (CustomTextView) convertView.findViewById(R.id.patientIdTextView);
            viewHolder.patientImageView = (CircularImageView) convertView.findViewById(R.id.patientImageView);
            viewHolder.patientNameTextView = (CustomTextView) convertView.findViewById(R.id.patientNameTextView);
            viewHolder.patientAgeTextView = (CustomTextView) convertView.findViewById(R.id.patientAgeTextView);
            viewHolder.patientGenderTextView = (CustomTextView) convertView.findViewById(R.id.patientGenderTextView);
            viewHolder.patientDetailsLinearLayout = (LinearLayout) convertView.findViewById(R.id.patientDetailsLinearLayout);
            viewHolder.opdTypeTextView = (CustomTextView) convertView.findViewById(R.id.opdTypeTextView);
            viewHolder.patientPhoneNumber = (CustomTextView) convertView.findViewById(R.id.patientPhoneNumber);
            viewHolder.separatorView = (View) convertView.findViewById(R.id.separatorView);
            viewHolder.outstandingAmountTextView = (CustomTextView) convertView.findViewById(R.id.outstandingAmountTextView);
            viewHolder.payableAmountTextView = (CustomTextView) convertView.findViewById(R.id.payableAmountTextView);
            viewHolder.cardView = (LinearLayout) convertView.findViewById(R.id.cardView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ChildViewHolder) convertView.getTag();
        }
        final PatientList patientObject = mAppointmentListTemp.get(groupPosition).getPatientList().get(childPosition);

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
                viewHolder.patientNameTextView.setText(spannableString);
            } else {
                viewHolder.patientNameTextView.setText(patientName);
            }
            //Spannable condition for PatientPhoneNumber

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
                viewHolder.patientPhoneNumber.setText(spannablePhoneString);
            } else {
                viewHolder.patientPhoneNumber.setText(patientObject.getPatientPhone());
            }
            //Spannable condition for PatientId
            if (String.valueOf(patientObject.getHospitalPatId()).toLowerCase().contains(patientObject.getSpannableString().toLowerCase())) {

                SpannableString spannableIdString = new SpannableString(mContext.getString(R.string.id) + " " + String.valueOf(patientObject.getHospitalPatId()));
                Pattern pattern = Pattern.compile(patientObject.getSpannableString(), Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(mContext.getString(R.string.id) + " " + String.valueOf(patientObject.getHospitalPatId()));

                while (matcher.find()) {
                    spannableIdString.setSpan(new ForegroundColorSpan(
                                    ContextCompat.getColor(mContext, R.color.tagColor)),
                            matcher.start(), matcher.end(),//hightlight mSearchString
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                viewHolder.patientIdTextView.setText(spannableIdString);
            } else {

                viewHolder.patientIdTextView.setText(mContext.getString(R.string.id) + " " + String.valueOf(patientObject.getHospitalPatId()));
            }
        } else {
            viewHolder.patientNameTextView.setText(patientName);
            viewHolder.patientPhoneNumber.setText(patientObject.getPatientPhone());

            viewHolder.patientIdTextView.setText(mContext.getString(R.string.id) + " " + String.valueOf(patientObject.getHospitalPatId()));
        }

        if (patientObject.getAge().equals("") && !patientObject.getDateOfBirth().equals("")) {
            viewHolder.patientAgeTextView.setVisibility(View.VISIBLE);
            String getTodayDate = CommonMethods.getCurrentDate();
            String getBirthdayDate = patientObject.getDateOfBirth();
            DateTime todayDateTime = CommonMethods.convertToDateTime(getTodayDate);
            DateTime birthdayDateTime = CommonMethods.convertToDateTime(getBirthdayDate);
            viewHolder.patientAgeTextView.setText(CommonMethods.displayAgeAnalysis(todayDateTime, birthdayDateTime) + " " + mContext.getString(R.string.years));
        } else if (!patientObject.getAge().equals("")) {
            viewHolder.patientAgeTextView.setVisibility(View.VISIBLE);
            viewHolder.patientAgeTextView.setText(patientObject.getAge() + " " + mContext.getString(R.string.years));
        } else if (patientObject.getAge().equals("") && patientObject.getDateOfBirth().equals("")) {
            viewHolder.patientAgeTextView.setVisibility(View.GONE);

        }

        viewHolder.patientGenderTextView.setText(" " + patientObject.getGender());
        if (patientObject.getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.book))) {
            viewHolder.opdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.book_color));
            viewHolder.opdTypeTextView.setText(mContext.getString(R.string.opd_appointment) + " " + mContext.getString(R.string.booked));
        } else if (patientObject.getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.completed))) {
            viewHolder.opdTypeTextView.setText(mContext.getString(R.string.opd_appointment) + " " + mContext.getString(R.string.capitalcompleted));
            viewHolder.opdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.complete_color));
        } else {
            viewHolder.opdTypeTextView.setText(mContext.getString(R.string.opd_appointment) + " " + patientObject.getAppointmentStatus());
            viewHolder.opdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        }
        viewHolder.outstandingAmountTextView.setText(mContext.getString(R.string.outstanding_amount) + " ");
        if (patientObject.getOutStandingAmount() == 0) {
            viewHolder.payableAmountTextView.setText(" " + mContext.getString(R.string.nil));
            viewHolder.payableAmountTextView.setTextColor(ContextCompat.getColor(mContext, R.color.rating_color));

        } else {
            viewHolder.payableAmountTextView.setText(" Rs." + patientObject.getOutStandingAmount() + "/-");
            viewHolder.payableAmountTextView.setTextColor(ContextCompat.getColor(mContext, R.color.Red));

        }
        viewHolder.appointmentTime.setVisibility(View.VISIBLE);
        viewHolder.appointmentTime.setText(CommonMethods.formatDateTime(patientObject.getAppointmentTime(), RescribeConstants.DATE_PATTERN.hh_mm_a, RescribeConstants.DATE_PATTERN.HH_mm_ss, RescribeConstants.TIME).toLowerCase());
        TextDrawable textDrawable = CommonMethods.getTextDrawable(mContext, patientObject.getPatientName());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.dontAnimate();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(false);
        requestOptions.placeholder(textDrawable);
        requestOptions.error(textDrawable);

        Glide.with(mContext)
                .load(patientObject.getPatientImageUrl())
                .apply(requestOptions).thumbnail(0.5f)
                .into(viewHolder.patientImageView);
        viewHolder.checkbox.setChecked(patientObject.isSelected());

        viewHolder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                patientObject.setSelected(viewHolder.checkbox.isChecked());
                int selected = getSelectedCount(mAppointmentListTemp.get(groupPosition).getPatientList());
                mAppointmentListTemp.get(groupPosition).setSelectedGroupCheckbox(selected == mAppointmentListTemp.get(groupPosition).getPatientList().size() && mAppointmentListTemp.get(groupPosition).getPatientHeader().isSelected());
                mOnDownArrowClicked.onCheckUncheckRemoveSelectAllSelection(viewHolder.checkbox.isChecked());
                notifyDataSetChanged();
            }
        });

        if (isLongPressed)
            viewHolder.checkbox.setVisibility(View.VISIBLE);
        else viewHolder.checkbox.setVisibility(View.GONE);

        viewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isLongPressed = !isLongPressed;
                mOnDownArrowClicked.onLongPressOpenBottomMenu(isLongPressed, groupPosition);
                notifyDataSetChanged();
                return false;
            }
        });
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnDownArrowClicked.onClickOfPatientDetails(mAppointmentListTemp.get(groupPosition).getPatientList().get(childPosition), viewHolder.patientAgeTextView.getText().toString() + viewHolder.patientGenderTextView.getText().toString());
            }
        });
        return convertView;

    }

    private int getSelectedCount(List<PatientList> patientList) {
        int selectedCount = 0;
        for (PatientList patientL : patientList) {
            if (patientL.isSelected())
                selectedCount += 1;
        }
        return selectedCount;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mAppointmentListTemp.get(groupPosition).getPatientList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mAppointmentListTemp.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.mAppointmentListTemp.size();
    }

    public ArrayList<AppointmentList> getGroupList() {
        return mAppointmentListTemp;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded,
                             View convertView, final ViewGroup parent) {
        final GroupViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.my_appointment_patients_item_layout, null);
            viewHolder = new GroupViewHolder();

            viewHolder.mCheckbox = (CheckBox) convertView.findViewById(R.id.checkbox);
            viewHolder.patientDetailsClickLinearLayout = (RelativeLayout)convertView.findViewById(R.id.patientDetailsClickLinearLayout);
            viewHolder.mGroupCheckbox = (CheckBox) convertView.findViewById(R.id.groupCheckbox);
            viewHolder.downArrowClickLinearLayout = (LinearLayout) convertView.findViewById(R.id.downArrowClickLinearLayout);
            viewHolder.cardView = (LinearLayout) convertView.findViewById(R.id.cardView);
            viewHolder.mHospitalDetailsLinearLayout = (RelativeLayout) convertView.findViewById(R.id.hospitalDetailsLinearLayout);
            viewHolder.mBulletImageView = (CircularImageView) convertView.findViewById(R.id.bulletImageView);
            viewHolder.mClinicNameTextView = (CustomTextView) convertView.findViewById(R.id.clinicNameTextView);
            viewHolder.mClinicAddress = (CustomTextView) convertView.findViewById(R.id.clinicAddress);
            viewHolder.mClinicPatientCount = (CustomTextView) convertView.findViewById(R.id.clinicPatientCount);
            viewHolder.mDownArrow = (ImageView) convertView.findViewById(R.id.downArrow);
            viewHolder.upArrow = (ImageView) convertView.findViewById(R.id.upArrow);
            viewHolder.mBluelineImageView = (ImageView) convertView.findViewById(R.id.bluelineImageView);
            viewHolder.mPatientIdTextView = (CustomTextView) convertView.findViewById(R.id.patientIdTextView);
            viewHolder.mPatientImageView = (CircularImageView) convertView.findViewById(R.id.patientImageView);
            viewHolder.mPatientNameTextView = (CustomTextView) convertView.findViewById(R.id.patientNameTextView);
            viewHolder.mPatientAgeTextView = (CustomTextView) convertView.findViewById(R.id.patientAgeTextView);
            viewHolder.mPatientGenderTextView = (CustomTextView) convertView.findViewById(R.id.patientGenderTextView);
            viewHolder.mPatientDetailsLinearLayout = (LinearLayout) convertView.findViewById(R.id.patientDetailsLinearLayout);
            viewHolder.mOpdTypeTextView = (CustomTextView) convertView.findViewById(R.id.opdTypeTextView);
            viewHolder.mPatientPhoneNumber = (CustomTextView) convertView.findViewById(R.id.patientPhoneNumber);
            viewHolder.mSeparatorView = (View) convertView.findViewById(R.id.separatorView);
            viewHolder.mOutstandingAmountTextView = (CustomTextView) convertView.findViewById(R.id.outstandingAmountTextView);
            viewHolder.mPayableAmountTextView = (CustomTextView) convertView.findViewById(R.id.payableAmountTextView);
            viewHolder.mAppointmentTime = (CustomTextView) convertView.findViewById(R.id.appointmentTime);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (GroupViewHolder) convertView.getTag();
        }

        viewHolder.mClinicNameTextView.setText(mAppointmentListTemp.get(groupPosition).getClinicName() + " - ");
        viewHolder.mClinicAddress.setText(mAppointmentListTemp.get(groupPosition).getArea() + ", " + mAppointmentListTemp.get(groupPosition).getCity());
        viewHolder.mClinicPatientCount.setText(mAppointmentListTemp.get(groupPosition).getPatientList().size() + "");

        viewHolder.mPatientIdTextView.setText(mContext.getString(R.string.id) + " " + mAppointmentListTemp.get(groupPosition).getPatientHeader().getHospitalPatId() + "");

        if (mAppointmentListTemp.get(groupPosition).getPatientHeader().getSalutation() == 1) {
            viewHolder.mPatientNameTextView.setText(mContext.getString(R.string.mr) + " " + mAppointmentListTemp.get(groupPosition).getPatientHeader().getPatientName());
        } else if (mAppointmentListTemp.get(groupPosition).getPatientHeader().getSalutation() == 2) {
            viewHolder.mPatientNameTextView.setText(mContext.getString(R.string.mrs) + " " + mAppointmentListTemp.get(groupPosition).getPatientHeader().getPatientName());

        } else if (mAppointmentListTemp.get(groupPosition).getPatientHeader().getSalutation() == 3) {
            viewHolder.mPatientNameTextView.setText(mContext.getString(R.string.miss) + " " + mAppointmentListTemp.get(groupPosition).getPatientHeader().getPatientName());

        } else if (mAppointmentListTemp.get(groupPosition).getPatientHeader().getSalutation() == 4) {
            viewHolder.mPatientNameTextView.setText(mAppointmentListTemp.get(groupPosition).getPatientHeader().getPatientName());
        }
        if (mAppointmentListTemp.get(groupPosition).getPatientHeader().getAge().equals("") && !mAppointmentListTemp.get(groupPosition).getPatientHeader().getDateOfBirth().equals("")) {
            viewHolder.mPatientAgeTextView.setVisibility(View.VISIBLE);
            String getTodayDate = CommonMethods.getCurrentDate();
            String getBirthdayDate = mAppointmentListTemp.get(groupPosition).getPatientHeader().getDateOfBirth();
            DateTime todayDateTime = CommonMethods.convertToDateTime(getTodayDate);
            DateTime birthdayDateTime = CommonMethods.convertToDateTime(getBirthdayDate);
            viewHolder.mPatientAgeTextView.setText(CommonMethods.displayAgeAnalysis(todayDateTime, birthdayDateTime) + " " + mContext.getString(R.string.years));
        } else if (!mAppointmentListTemp.get(groupPosition).getPatientHeader().getAge().equals("")) {
            viewHolder.mPatientAgeTextView.setVisibility(View.VISIBLE);
            viewHolder.mPatientAgeTextView.setText(mAppointmentListTemp.get(groupPosition).getPatientHeader().getAge() + " " + mContext.getString(R.string.years));
        } else if (mAppointmentListTemp.get(groupPosition).getPatientHeader().getAge().equals("") && mAppointmentListTemp.get(groupPosition).getPatientHeader().getDateOfBirth().equals("")) {
            viewHolder.mPatientAgeTextView.setVisibility(View.GONE);

        }

        viewHolder.mPatientGenderTextView.setText(" " + mAppointmentListTemp.get(groupPosition).getPatientHeader().getGender());
        if (mAppointmentListTemp.get(groupPosition).getPatientHeader().getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.book))) {
            viewHolder.mOpdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.book_color));
            viewHolder.mOpdTypeTextView.setText(mContext.getString(R.string.opd_appointment) + " " + mContext.getString(R.string.booked));
        } else if (mAppointmentListTemp.get(groupPosition).getPatientHeader().getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.completed))) {
            viewHolder.mOpdTypeTextView.setText(mContext.getString(R.string.opd_appointment) + " " + mContext.getString(R.string.capitalcompleted));
            viewHolder.mOpdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.complete_color));
        } else {
            viewHolder.mOpdTypeTextView.setText(mContext.getString(R.string.opd_appointment) + " " + mAppointmentListTemp.get(groupPosition).getPatientHeader().getAppointmentStatus());
            viewHolder.mOpdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        }
        viewHolder.mPatientPhoneNumber.setText(mAppointmentListTemp.get(groupPosition).getPatientHeader().getPatientPhone());
        viewHolder.mOutstandingAmountTextView.setText(mContext.getString(R.string.outstanding_amount) + " ");
        if (mAppointmentListTemp.get(groupPosition).getPatientHeader().getOutStandingAmount() == 0) {
            viewHolder.mPayableAmountTextView.setText(" " + mContext.getString(R.string.nil));
            viewHolder.mPayableAmountTextView.setTextColor(ContextCompat.getColor(mContext, R.color.rating_color));

        } else {
            viewHolder.mPayableAmountTextView.setText(" Rs." + mAppointmentListTemp.get(groupPosition).getPatientHeader().getOutStandingAmount() + "/-");
            viewHolder.mPayableAmountTextView.setTextColor(ContextCompat.getColor(mContext, R.color.Red));

        }

        viewHolder.mAppointmentTime.setVisibility(View.VISIBLE);
        viewHolder.mAppointmentTime.setText(CommonMethods.formatDateTime(mAppointmentListTemp.get(groupPosition).getPatientHeader().getAppointmentTime(), RescribeConstants.DATE_PATTERN.hh_mm_a, RescribeConstants.DATE_PATTERN.HH_mm_ss, RescribeConstants.TIME).toLowerCase());
        TextDrawable textDrawable = CommonMethods.getTextDrawable(mContext, mAppointmentListTemp.get(groupPosition).getPatientHeader().getPatientName());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.dontAnimate();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(false);
        requestOptions.placeholder(textDrawable);
        requestOptions.error(textDrawable);

        Glide.with(mContext)
                .load(mAppointmentListTemp.get(groupPosition).getPatientHeader().getPatientImageUrl())
                .apply(requestOptions).thumbnail(0.5f)
                .into(viewHolder.mPatientImageView);

        viewHolder.mHospitalDetailsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded) {
                    viewHolder.mDownArrow.setVisibility(View.VISIBLE);
                    viewHolder.upArrow.setVisibility(View.GONE);
                } else {
                    viewHolder.mDownArrow.setVisibility(View.GONE);
                    viewHolder.upArrow.setVisibility(View.VISIBLE);
                }
                mOnDownArrowClicked.onDownArrowSetClick(groupPosition, isExpanded);
            }
        });
        viewHolder.mGroupCheckbox.setChecked(mAppointmentListTemp.get(groupPosition).isSelectedGroupCheckbox());

        viewHolder.mCheckbox.setChecked(mAppointmentListTemp.get(groupPosition).getPatientHeader().isSelected());

        viewHolder.mCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAppointmentListTemp.get(groupPosition).getPatientHeader().setSelected(viewHolder.mCheckbox.isChecked());
                int selected = getSelectedCount(mAppointmentListTemp.get(groupPosition).getPatientList());
                mAppointmentListTemp.get(groupPosition).setSelectedGroupCheckbox(selected == mAppointmentListTemp.get(groupPosition).getPatientList().size() && mAppointmentListTemp.get(groupPosition).getPatientHeader().isSelected());
                mOnDownArrowClicked.onCheckUncheckRemoveSelectAllSelection(viewHolder.mCheckbox.isChecked());

                notifyDataSetChanged();
            }
        });


        if (isLongPressed) {
            viewHolder.mCheckbox.setVisibility(View.VISIBLE);
            viewHolder.mGroupCheckbox.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mCheckbox.setVisibility(View.GONE);
            viewHolder.mGroupCheckbox.setVisibility(View.GONE);
        }

        viewHolder.mGroupCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAppointmentListTemp.get(groupPosition).setSelectedGroupCheckbox(viewHolder.mGroupCheckbox.isChecked());

                mAppointmentListTemp.get(groupPosition).getPatientHeader().setSelected(viewHolder.mGroupCheckbox.isChecked());

                for (PatientList patient : mAppointmentListTemp.get(groupPosition).getPatientList())
                    patient.setSelected(viewHolder.mGroupCheckbox.isChecked());
                mOnDownArrowClicked.onCheckUncheckRemoveSelectAllSelection(viewHolder.mGroupCheckbox.isChecked());

                notifyDataSetChanged();
            }
        });

        viewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isLongPressed = !isLongPressed;
                mOnDownArrowClicked.onLongPressOpenBottomMenu(isLongPressed, groupPosition);
                notifyDataSetChanged();
                return false;
            }
        });
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnDownArrowClicked.onClickOfPatientDetails(mAppointmentListTemp.get(groupPosition).getPatientHeader(), viewHolder.mPatientAgeTextView.getText().toString() + viewHolder.mPatientGenderTextView.getText().toString());
            }
        });

        if (isExpanded) {
            viewHolder.cardView.setVisibility(View.GONE);
        } else {
            viewHolder.cardView.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public interface OnDownArrowClicked {
        void onDownArrowSetClick(int groupPosition, boolean isExpanded);

        void onLongPressOpenBottomMenu(boolean isLongPressed, int groupPosition);

        void onRecordFound(boolean isListEmpty);

        void onCheckUncheckRemoveSelectAllSelection(boolean ischecked);

        void onClickOfPatientDetails(PatientList patientListObject, String text);
    }

    public boolean isLongPressed() {
        return isLongPressed;
    }

    public void setLongPressed(boolean longPressed) {
        isLongPressed = longPressed;
    }

    // Sorting clicniclist by patientName , patientId , patientPhoneNo
    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();

                ArrayList<AppointmentList> mListToShowAfterFilter;
                ArrayList<AppointmentList> mTempAppointmentListToIterate = new ArrayList<>(mDataList);

                if (charString.isEmpty()) {
                    mListToShowAfterFilter = new ArrayList<>();
                    for (AppointmentList AppointmentListObj : mTempAppointmentListToIterate) {

                        List<PatientList> patientLists = AppointmentListObj.getPatientList();
                        ArrayList<PatientList> sortedPatientLists = new ArrayList<>();
                        //--------------
                        AppointmentList tempAppointmentListObject = null;
                        try {
                            tempAppointmentListObject = (AppointmentList) AppointmentListObj.clone();
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }

                        //-----------------
                        for (PatientList patientListObject : patientLists) {
                            patientListObject.setSpannableString(null);
                            sortedPatientLists.add(patientListObject);
                        }
                        if (!sortedPatientLists.isEmpty()) {
                            tempAppointmentListObject.setPatientList(sortedPatientLists);
                            mListToShowAfterFilter.add(tempAppointmentListObject);
                        }
                    }

                } else {
                    mListToShowAfterFilter = new ArrayList<>();
                    for (AppointmentList AppointmentListObj : mTempAppointmentListToIterate) {

                        List<PatientList> patientLists = AppointmentListObj.getPatientList();
                        ArrayList<PatientList> sortedPatientLists = new ArrayList<>();
                        //--------------
                        AppointmentList tempAppointmentListObject = null;
                        try {
                            tempAppointmentListObject = (AppointmentList) AppointmentListObj.clone();
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }

                        //-----------------
                        for (PatientList patientListObject : patientLists) {
                            if (patientListObject.getPatientName().toLowerCase().contains(charString.toLowerCase())
                                    || patientListObject.getPatientPhone().contains(charString)
                                    || String.valueOf(patientListObject.getHospitalPatId()).contains(charString)) {
                                //--------
                                patientListObject.setSpannableString(charString);
                                sortedPatientLists.add(patientListObject);
                            }
                        }

                        if (!sortedPatientLists.isEmpty()) {
                            tempAppointmentListObject.setPatientList(sortedPatientLists);
                            mListToShowAfterFilter.add(tempAppointmentListObject);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mListToShowAfterFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mAppointmentListTemp.clear();
                mAppointmentListTemp.addAll((ArrayList<AppointmentList>) filterResults.values);

                if (mAppointmentListTemp.isEmpty()) {
                    notifyDataSetChanged();
                    mOnDownArrowClicked.onRecordFound(true);

                } else {
                    notifyDataSetChanged();
                    mOnDownArrowClicked.onRecordFound(false);
                    if (charSequence.toString().isEmpty()) {
                        MyAppointmentsFragment.collapseAll();
                    } else {
                        MyAppointmentsFragment.expandAll();
                    }
                }

            }
        };
    }

    /*  private ArrayList<AppointmentList> cloneList() {
          return mAppointmentListTemp = (ArrayList<AppointmentList>) mDataList.clone();
      }
  */
    static class ChildViewHolder {
        CheckBox checkbox;
        RelativeLayout patientDetailsClickLinearLayout;
        LinearLayout cardView;
        ImageView bluelineImageView;
        CustomTextView appointmentTime;
        CustomTextView patientIdTextView;
        CircularImageView patientImageView;
        CustomTextView patientNameTextView;
        CustomTextView patientAgeTextView;
        CustomTextView patientGenderTextView;
        LinearLayout patientDetailsLinearLayout;
        CustomTextView opdTypeTextView;
        CustomTextView patientPhoneNumber;
        View separatorView;
        CustomTextView outstandingAmountTextView;
        CustomTextView payableAmountTextView;


    }

    static class GroupViewHolder {
        LinearLayout cardView;
        CheckBox mCheckbox;
        RelativeLayout patientDetailsClickLinearLayout;
        CheckBox mGroupCheckbox;
        LinearLayout downArrowClickLinearLayout;
        RelativeLayout mHospitalDetailsLinearLayout;
        CircularImageView mBulletImageView;
        CustomTextView mClinicNameTextView;
        CustomTextView mClinicAddress;
        CustomTextView mClinicPatientCount;
        ImageView mDownArrow;
        ImageView upArrow;
        ImageView mBluelineImageView;
        CustomTextView mPatientIdTextView;
        CircularImageView mPatientImageView;
        CustomTextView mPatientNameTextView;
        CustomTextView mPatientAgeTextView;
        CustomTextView mPatientGenderTextView;
        LinearLayout mPatientDetailsLinearLayout;
        CustomTextView mOpdTypeTextView;
        CustomTextView mPatientPhoneNumber;
        View mSeparatorView;
        CustomTextView mOutstandingAmountTextView;
        CustomTextView mPayableAmountTextView;
        CustomTextView mAppointmentTime;

    }
}