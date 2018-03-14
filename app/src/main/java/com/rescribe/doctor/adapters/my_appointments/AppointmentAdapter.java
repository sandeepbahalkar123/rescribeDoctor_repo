package com.rescribe.doctor.adapters.my_appointments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
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
import com.rescribe.doctor.ui.customesViews.swipeable_recyclerview.SwipeRevealLayout;
import com.rescribe.doctor.ui.customesViews.swipeable_recyclerview.ViewBinderHelper;
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
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();
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
            convertView = infalInflater.inflate(R.layout.trial2_layout, parent, false);
            viewHolder = new ChildViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ChildViewHolder) convertView.getTag();
        }
        final PatientList patientObject = mAppointmentListTemp.get(groupPosition).getPatientList().get(childPosition);
        binderHelper.setOpenOnlyOne(true);
        binderHelper.bindAppointmentChildList(viewHolder.swipe_layout, patientObject);
        bind(patientObject, groupPosition, childPosition, viewHolder);
        if (patientObject.getAppointmentStatus().equals("Booked") || patientObject.getAppointmentStatus().equals("Confirmed")) {
            binderHelper.unlockSwipe(patientObject.getPatientName());
        } else {
            binderHelper.lockSwipe(patientObject.getPatientName());

        }
        return convertView;

    }

    @SuppressLint("CheckResult")
    private void bind(final PatientList patientList, final int groupPosition, final int childPosition, final ChildViewHolder viewHolder) {
        String patientName = "";
        if (patientList.getSalutation() == 1) {
            patientName = mContext.getString(R.string.mr) + " " + CommonMethods.toCamelCase(patientList.getPatientName());
        } else if (patientList.getSalutation() == 2) {
            patientName = mContext.getString(R.string.mrs) + " " + CommonMethods.toCamelCase(patientList.getPatientName());

        } else if (patientList.getSalutation() == 3) {
            patientName = mContext.getString(R.string.miss) + " " + CommonMethods.toCamelCase(patientList.getPatientName());

        } else if (patientList.getSalutation() == 4) {
            patientName = CommonMethods.toCamelCase(patientList.getPatientName());
        }

        if (patientList.getSpannableString() != null) {
            //Spannable condition for PatientName
            if (patientList.getPatientName().toLowerCase().contains(patientList.getSpannableString().toLowerCase())) {
                SpannableString spannableString = new SpannableString(patientName);
                Pattern pattern = Pattern.compile(patientList.getSpannableString(), Pattern.CASE_INSENSITIVE);
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

            if (patientList.getPatientPhone().toLowerCase().contains(patientList.getSpannableString().toLowerCase())) {
                SpannableString spannablePhoneString = new SpannableString(patientList.getPatientPhone());
                Pattern pattern = Pattern.compile(patientList.getSpannableString(), Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(patientList.getPatientPhone());
                while (matcher.find()) {
                    spannablePhoneString.setSpan(new ForegroundColorSpan(
                                    ContextCompat.getColor(mContext, R.color.tagColor)),
                            matcher.start(), matcher.end(),//hightlight mSearchString
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                viewHolder.patientPhoneNumber.setText(spannablePhoneString);
            } else {
                viewHolder.patientPhoneNumber.setText(patientList.getPatientPhone());
            }
            //Spannable condition for PatientId
            if (String.valueOf(patientList.getHospitalPatId()).toLowerCase().contains(patientList.getSpannableString().toLowerCase())) {

                SpannableString spannableIdString = new SpannableString(mContext.getString(R.string.id) + " " + String.valueOf(patientList.getHospitalPatId()));
                Pattern pattern = Pattern.compile(patientList.getSpannableString(), Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(mContext.getString(R.string.id) + " " + String.valueOf(patientList.getHospitalPatId()));

                while (matcher.find()) {
                    spannableIdString.setSpan(new ForegroundColorSpan(
                                    ContextCompat.getColor(mContext, R.color.tagColor)),
                            matcher.start(), matcher.end(),//hightlight mSearchString
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                viewHolder.patientIdTextView.setText(spannableIdString);
            } else {

                viewHolder.patientIdTextView.setText(mContext.getString(R.string.id) + " " + String.valueOf(patientList.getHospitalPatId()));
            }
        } else {
            viewHolder.patientNameTextView.setText(patientName);
            viewHolder.patientPhoneNumber.setText(patientList.getPatientPhone());
            viewHolder.patientIdTextView.setText(mContext.getString(R.string.id) + " " + String.valueOf(patientList.getHospitalPatId()));
        }

        if (patientList.getAge().equals("") && !patientList.getDateOfBirth().equals("")) {
            viewHolder.patientAgeTextView.setVisibility(View.VISIBLE);
            String getTodayDate = CommonMethods.getCurrentDate();
            String getBirthdayDate = patientList.getDateOfBirth();
            DateTime todayDateTime = CommonMethods.convertToDateTime(getTodayDate);
            DateTime birthdayDateTime = CommonMethods.convertToDateTime(getBirthdayDate);
            viewHolder.patientAgeTextView.setText(CommonMethods.displayAgeAnalysis(todayDateTime, birthdayDateTime) + " " + mContext.getString(R.string.years));
        } else if (!patientList.getAge().equals("")) {
            viewHolder.patientAgeTextView.setVisibility(View.VISIBLE);
            viewHolder.patientAgeTextView.setText(patientList.getAge() + " " + mContext.getString(R.string.years));
        } else if (patientList.getAge().equals("") && patientList.getDateOfBirth().equals("")) {
            viewHolder.patientAgeTextView.setVisibility(View.GONE);

        }

        viewHolder.patientGenderTextView.setText(" " + CommonMethods.toCamelCase(patientList.getGender()));
        if (patientList.getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.book))) {
            viewHolder.opdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.book_color));
            viewHolder.opdTypeTextView.setText(mContext.getString(R.string.opd_appointment) + " " + mContext.getString(R.string.booked));
        } else if (patientList.getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.completed))) {
            viewHolder.opdTypeTextView.setText(mContext.getString(R.string.opd_appointment) + " " + mContext.getString(R.string.capitalcompleted));
            viewHolder.opdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.complete_color));
        } else if (patientList.getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.confirmed))) {
            viewHolder.opdTypeTextView.setText(mContext.getString(R.string.opd_appointment) + " " + patientList.getAppointmentStatus());
            viewHolder.opdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.confirm_color));
        } else if (patientList.getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.cancelled))) {
            viewHolder.opdTypeTextView.setText(mContext.getString(R.string.opd_appointment) + " " + patientList.getAppointmentStatus());
            viewHolder.opdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.cancel_color));
        } else if (patientList.getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.no_show))) {
            viewHolder.opdTypeTextView.setText(mContext.getString(R.string.opd_appointment) + " " + patientList.getAppointmentStatus());
            viewHolder.opdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.no_show_color));
        } else if (patientList.getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.other))) {
            viewHolder.opdTypeTextView.setText(mContext.getString(R.string.opd_appointment) + " " + patientList.getAppointmentStatus());
            viewHolder.opdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.other_color));
        }
        viewHolder.outstandingAmountTextView.setText(mContext.getString(R.string.outstanding_amount) + " ");
        if (patientList.getOutStandingAmount() == 0) {
            viewHolder.payableAmountTextView.setText(" " + mContext.getString(R.string.nil));
            viewHolder.payableAmountTextView.setTextColor(ContextCompat.getColor(mContext, R.color.rating_color));

        } else {
            viewHolder.payableAmountTextView.setText(" Rs." + patientList.getOutStandingAmount() + "/-");
            viewHolder.payableAmountTextView.setTextColor(ContextCompat.getColor(mContext, R.color.Red));

        }
        viewHolder.appointmentTime.setVisibility(View.VISIBLE);
        viewHolder.appointmentTime.setText(CommonMethods.formatDateTime(patientList.getAppointmentTime(), RescribeConstants.DATE_PATTERN.hh_mm_a, RescribeConstants.DATE_PATTERN.HH_mm_ss, RescribeConstants.TIME).toLowerCase());
        TextDrawable textDrawable = CommonMethods.getTextDrawable(mContext, patientList.getPatientName());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.dontAnimate();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(false);
        requestOptions.placeholder(textDrawable);
        requestOptions.error(textDrawable);

        Glide.with(mContext)
                .load(patientList.getPatientImageUrl())
                .apply(requestOptions).thumbnail(0.5f)
                .into(viewHolder.patientImageView);
        viewHolder.checkbox.setChecked(patientList.isSelected());

        viewHolder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                patientList.setSelected(viewHolder.checkbox.isChecked());
                int selected = getSelectedCount(mAppointmentListTemp.get(groupPosition).getPatientList());
                mAppointmentListTemp.get(groupPosition).setSelectedGroupCheckbox(selected == mAppointmentListTemp.get(groupPosition).getPatientList().size() && mAppointmentListTemp.get(groupPosition).getPatientHeader().isSelected());
                mOnDownArrowClicked.onCheckUncheckRemoveSelectAllSelection(viewHolder.checkbox.isChecked());
                notifyDataSetChanged();
            }
        });

        if (isLongPressed)
            viewHolder.checkbox.setVisibility(View.VISIBLE);
        else viewHolder.checkbox.setVisibility(View.GONE);

        viewHolder.patientDetailsClickLinearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isLongPressed = !isLongPressed;
                mOnDownArrowClicked.onLongPressOpenBottomMenu(isLongPressed, groupPosition);
                notifyDataSetChanged();
                return false;
            }
        });
        viewHolder.patientDetailsClickLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnDownArrowClicked.onClickOfPatientDetails(mAppointmentListTemp.get(groupPosition).getPatientList().get(childPosition), viewHolder.patientAgeTextView.getText().toString() + viewHolder.patientGenderTextView.getText().toString());
            }
        });
        viewHolder.appointmentComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnDownArrowClicked.onAppointmentClicked(patientList.getAptId(), patientList.getPatientId(), 3, "complete", childPosition, groupPosition);
                binderHelper.closeLayout(patientList.getPatientName());
            }
        });
        viewHolder.appointmentCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnDownArrowClicked.onAppointmentCancelled(patientList.getAptId(), patientList.getPatientId(), 4, "cancel", childPosition, groupPosition);
                binderHelper.closeLayout(patientList.getPatientName());
            }
        });
        viewHolder.patientPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnDownArrowClicked.onPhoneNoClick(patientList.getPatientPhone());
            }
        });
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

        GroupViewHolder groupViewHolder;

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.my_appointment_patients_item_layout, parent, false);
            groupViewHolder = new GroupViewHolder(convertView);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        final AppointmentList appointmentListObject = mAppointmentListTemp.get(groupPosition);
        binderHelper.setOpenOnlyOne(true);
        binderHelper.bindGroup(groupViewHolder.swipe_layout, appointmentListObject, groupPosition);
        bindGroupItem(appointmentListObject, groupPosition, isExpanded, groupViewHolder);

        if (appointmentListObject.getPatientHeader().getAppointmentStatus().equals("Booked") || appointmentListObject.getPatientHeader().getAppointmentStatus().equals("Confirmed")) {
            binderHelper.unlockSwipe(groupPosition + "");
        } else {
            binderHelper.lockSwipe(groupPosition + "");
        }
        return convertView;
    }

    @SuppressLint("CheckResult")
    private void bindGroupItem(final AppointmentList appointmentListObject, final int groupPosition, final boolean isExpanded, final GroupViewHolder groupViewHolder) {
        groupViewHolder.mClinicNameTextView.setText(appointmentListObject.getClinicName() + " - ");
        groupViewHolder.mClinicAddress.setText(appointmentListObject.getArea() + ", " + appointmentListObject.getCity());
        int count = appointmentListObject.getPatientList().size();
        if (count < 10) {
            groupViewHolder.mClinicPatientCount.setText("0" + count);
        } else {
            groupViewHolder.mClinicPatientCount.setText("" + count);
        }
        groupViewHolder.mPatientIdTextView.setText(mContext.getString(R.string.id) + " " + appointmentListObject.getPatientHeader().getHospitalPatId() + "");

        if (appointmentListObject.getPatientHeader().getSalutation() == 1) {
            groupViewHolder.mPatientNameTextView.setText(mContext.getString(R.string.mr) + " " + CommonMethods.toCamelCase(appointmentListObject.getPatientHeader().getPatientName()));
        } else if (appointmentListObject.getPatientHeader().getSalutation() == 2) {
            groupViewHolder.mPatientNameTextView.setText(mContext.getString(R.string.mrs) + " " + CommonMethods.toCamelCase(appointmentListObject.getPatientHeader().getPatientName()));

        } else if (appointmentListObject.getPatientHeader().getSalutation() == 3) {
            groupViewHolder.mPatientNameTextView.setText(mContext.getString(R.string.miss) + " " + CommonMethods.toCamelCase(appointmentListObject.getPatientHeader().getPatientName()));

        } else if (appointmentListObject.getPatientHeader().getSalutation() == 4) {
            groupViewHolder.mPatientNameTextView.setText(CommonMethods.toCamelCase(appointmentListObject.getPatientHeader().getPatientName()));
        }
        if (appointmentListObject.getPatientHeader().getAge().equals("") && !appointmentListObject.getPatientHeader().getDateOfBirth().equals("")) {
            groupViewHolder.mPatientAgeTextView.setVisibility(View.VISIBLE);
            String getTodayDate = CommonMethods.getCurrentDate();
            String getBirthdayDate = appointmentListObject.getPatientHeader().getDateOfBirth();
            DateTime todayDateTime = CommonMethods.convertToDateTime(getTodayDate);
            DateTime birthdayDateTime = CommonMethods.convertToDateTime(getBirthdayDate);
            groupViewHolder.mPatientAgeTextView.setText(CommonMethods.displayAgeAnalysis(todayDateTime, birthdayDateTime) + " " + mContext.getString(R.string.years));
        } else if (!appointmentListObject.getPatientHeader().getAge().equals("")) {
            groupViewHolder.mPatientAgeTextView.setVisibility(View.VISIBLE);
            groupViewHolder.mPatientAgeTextView.setText(appointmentListObject.getPatientHeader().getAge() + " " + mContext.getString(R.string.years));
        } else if (appointmentListObject.getPatientHeader().getAge().equals("") && appointmentListObject.getPatientHeader().getDateOfBirth().equals("")) {
            groupViewHolder.mPatientAgeTextView.setVisibility(View.GONE);

        }

        groupViewHolder.mPatientGenderTextView.setText(" " + CommonMethods.toCamelCase(appointmentListObject.getPatientHeader().getGender()));
        if (appointmentListObject.getPatientHeader().getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.book))) {
            groupViewHolder.mOpdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.book_color));
            groupViewHolder.mOpdTypeTextView.setText(mContext.getString(R.string.opd_appointment) + " " + mContext.getString(R.string.booked));
        } else if (appointmentListObject.getPatientHeader().getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.completed))) {
            groupViewHolder.mOpdTypeTextView.setText(mContext.getString(R.string.opd_appointment) + " " + mContext.getString(R.string.capitalcompleted));
            groupViewHolder.mOpdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.complete_color));
        } else if (appointmentListObject.getPatientHeader().getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.confirmed))) {
            groupViewHolder.mOpdTypeTextView.setText(mContext.getString(R.string.opd_appointment) + " " + appointmentListObject.getPatientHeader().getAppointmentStatus());
            groupViewHolder.mOpdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.confirm_color));
        } else if (appointmentListObject.getPatientHeader().getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.cancelled))) {
            groupViewHolder.mOpdTypeTextView.setText(mContext.getString(R.string.opd_appointment) + " " + appointmentListObject.getPatientHeader().getAppointmentStatus());
            groupViewHolder.mOpdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.cancel_color));
        } else if (appointmentListObject.getPatientHeader().getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.no_show))) {
            groupViewHolder.mOpdTypeTextView.setText(mContext.getString(R.string.opd_appointment) + " " + appointmentListObject.getPatientHeader().getAppointmentStatus());
            groupViewHolder.mOpdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.no_show_color));
        } else if (appointmentListObject.getPatientHeader().getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.other))) {
            groupViewHolder.mOpdTypeTextView.setText(mContext.getString(R.string.opd_appointment) + " " + appointmentListObject.getPatientHeader().getAppointmentStatus());
            groupViewHolder.mOpdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.other_color));
        }
        groupViewHolder.mPatientPhoneNumber.setText(appointmentListObject.getPatientHeader().getPatientPhone());
        groupViewHolder.mOutstandingAmountTextView.setText(mContext.getString(R.string.outstanding_amount) + " ");
        if (appointmentListObject.getPatientHeader().getOutStandingAmount() == 0) {
            groupViewHolder.mPayableAmountTextView.setText(" " + mContext.getString(R.string.nil));
            groupViewHolder.mPayableAmountTextView.setTextColor(ContextCompat.getColor(mContext, R.color.rating_color));

        } else {
            groupViewHolder.mPayableAmountTextView.setText(" Rs." + appointmentListObject.getPatientHeader().getOutStandingAmount() + "/-");
            groupViewHolder.mPayableAmountTextView.setTextColor(ContextCompat.getColor(mContext, R.color.Red));

        }

        groupViewHolder.mAppointmentTime.setVisibility(View.VISIBLE);
        groupViewHolder.mAppointmentTime.setText(CommonMethods.formatDateTime(appointmentListObject.getPatientHeader().getAppointmentTime(), RescribeConstants.DATE_PATTERN.hh_mm_a, RescribeConstants.DATE_PATTERN.HH_mm_ss, RescribeConstants.TIME).toLowerCase());
        TextDrawable textDrawable = CommonMethods.getTextDrawable(mContext, appointmentListObject.getPatientHeader().getPatientName());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.dontAnimate();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(false);
        requestOptions.placeholder(textDrawable);
        requestOptions.error(textDrawable);

        Glide.with(mContext)
                .load(appointmentListObject.getPatientHeader().getPatientImageUrl())
                .apply(requestOptions).thumbnail(0.5f)
                .into(groupViewHolder.mPatientImageView);

        groupViewHolder.mHospitalDetailsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(appointmentListObject.getPatientList().size()==1){
                    if (isExpanded) {
                        groupViewHolder.mDownArrow.setVisibility(View.VISIBLE);
                        groupViewHolder.upArrow.setVisibility(View.GONE);
                    } else {
                        groupViewHolder.mDownArrow.setVisibility(View.VISIBLE);
                        groupViewHolder.upArrow.setVisibility(View.GONE);
                    }
                    mOnDownArrowClicked.onDownArrowSetClick(groupPosition, isExpanded);

                }else {
                    if (isExpanded) {
                        groupViewHolder.mDownArrow.setVisibility(View.VISIBLE);
                        groupViewHolder.upArrow.setVisibility(View.GONE);
                    } else {
                        groupViewHolder.mDownArrow.setVisibility(View.GONE);
                        groupViewHolder.upArrow.setVisibility(View.VISIBLE);
                    }
                    mOnDownArrowClicked.onDownArrowSetClick(groupPosition, isExpanded);
                }
            }
        });
        groupViewHolder.mGroupCheckbox.setChecked(appointmentListObject.isSelectedGroupCheckbox());

        groupViewHolder.mCheckbox.setChecked(appointmentListObject.getPatientHeader().isSelected());

        groupViewHolder.mCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appointmentListObject.getPatientHeader().setSelected(groupViewHolder.mCheckbox.isChecked());
                int selected = getSelectedCount(appointmentListObject.getPatientList());
                appointmentListObject.setSelectedGroupCheckbox(selected == appointmentListObject.getPatientList().size() && appointmentListObject.getPatientHeader().isSelected());
                mOnDownArrowClicked.onCheckUncheckRemoveSelectAllSelection(groupViewHolder.mCheckbox.isChecked());

                notifyDataSetChanged();
            }
        });


        if (isLongPressed) {
            groupViewHolder.mCheckbox.setVisibility(View.VISIBLE);
            groupViewHolder.mGroupCheckbox.setVisibility(View.VISIBLE);
        } else {
            groupViewHolder.mCheckbox.setVisibility(View.GONE);
            groupViewHolder.mGroupCheckbox.setVisibility(View.GONE);
        }

        groupViewHolder.mGroupCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appointmentListObject.setSelectedGroupCheckbox(groupViewHolder.mGroupCheckbox.isChecked());

                appointmentListObject.getPatientHeader().setSelected(groupViewHolder.mGroupCheckbox.isChecked());

                for (PatientList patient : appointmentListObject.getPatientList())
                    patient.setSelected(groupViewHolder.mGroupCheckbox.isChecked());
                mOnDownArrowClicked.onCheckUncheckRemoveSelectAllSelection(groupViewHolder.mGroupCheckbox.isChecked());

                notifyDataSetChanged();
            }
        });

        groupViewHolder.mPatientDetailsClickLinearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isLongPressed = !isLongPressed;
                mOnDownArrowClicked.onLongPressOpenBottomMenu(isLongPressed, groupPosition);
                notifyDataSetChanged();
                return false;
            }
        });
        groupViewHolder.mPatientDetailsClickLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnDownArrowClicked.onClickOfPatientDetails(appointmentListObject.getPatientHeader(), groupViewHolder.mPatientAgeTextView.getText().toString() + groupViewHolder.mPatientGenderTextView.getText().toString());
            }
        });

        if (isExpanded) {
            groupViewHolder.swipe_layout.setVisibility(View.GONE);
            groupViewHolder.cardView.setVisibility(View.GONE);
        } else {
            groupViewHolder.cardView.setVisibility(View.VISIBLE);
            groupViewHolder.swipe_layout.setVisibility(View.VISIBLE);
        }

        groupViewHolder.mAppointmentComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnDownArrowClicked.onGroupAppointmentCancelled(appointmentListObject.getPatientHeader().getAptId(), appointmentListObject.getPatientHeader().getPatientId(), 3, "complete", groupPosition);
                binderHelper.closeLayout(groupPosition + "");
            }
        });
        groupViewHolder.mAppointmentCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnDownArrowClicked.onGroupAppointmentCancelled(appointmentListObject.getPatientHeader().getAptId(), appointmentListObject.getPatientHeader().getPatientId(), 4, "cancel", groupPosition);
                binderHelper.closeLayout(groupPosition + "");
            }
        });
        groupViewHolder.mPatientPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnDownArrowClicked.onPhoneNoClick(appointmentListObject.getPatientHeader().getPatientPhone());
            }
        });
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
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
                        mOnDownArrowClicked.collapseAll();
                    } else {
                        mOnDownArrowClicked.expandAll();
                    }
                }
            }
        };
    }

    /*  private ArrayList<AppointmentList> cloneList() {
          return mAppointmentListTemp = (ArrayList<AppointmentList>) mDataList.clone();
      }
  */
    public static class ChildViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkbox;
        private SwipeRevealLayout swipe_layout;
        private RelativeLayout patientDetailsClickLinearLayout;
        private LinearLayout cardView;
        private ImageView bluelineImageView;
        private CustomTextView appointmentTime;
        private CustomTextView patientIdTextView;
        private CircularImageView patientImageView;
        private CustomTextView patientNameTextView;
        private CustomTextView patientAgeTextView;
        private CustomTextView patientGenderTextView;
        private LinearLayout patientDetailsLinearLayout;
        private CustomTextView opdTypeTextView;
        private CustomTextView patientPhoneNumber;
        private View separatorView;
        private CustomTextView outstandingAmountTextView;
        private CustomTextView payableAmountTextView;
        private LinearLayout appointmentCancel;
        private LinearLayout appointmentComplete;

        ChildViewHolder(View convertView) {
            super(convertView);

            appointmentCancel = (LinearLayout) convertView.findViewById(R.id.appointmentCancel);
            appointmentComplete = (LinearLayout) convertView.findViewById(R.id.appointmentComplete);
            swipe_layout = (SwipeRevealLayout) convertView.findViewById(R.id.swipe_layout);
            checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
            patientDetailsClickLinearLayout = (RelativeLayout) convertView.findViewById(R.id.patientDetailsClickLinearLayout);
            bluelineImageView = (ImageView) convertView.findViewById(R.id.bluelineImageView);
            appointmentTime = (CustomTextView) convertView.findViewById(R.id.appointmentTime);
            patientIdTextView = (CustomTextView) convertView.findViewById(R.id.patientIdTextView);
            patientImageView = (CircularImageView) convertView.findViewById(R.id.patientImageView);
            patientNameTextView = (CustomTextView) convertView.findViewById(R.id.patientNameTextView);
            patientAgeTextView = (CustomTextView) convertView.findViewById(R.id.patientAgeTextView);
            patientGenderTextView = (CustomTextView) convertView.findViewById(R.id.patientGenderTextView);
            patientDetailsLinearLayout = (LinearLayout) convertView.findViewById(R.id.patientDetailsLinearLayout);
            opdTypeTextView = (CustomTextView) convertView.findViewById(R.id.opdTypeTextView);
            patientPhoneNumber = (CustomTextView) convertView.findViewById(R.id.patientPhoneNumber);
            separatorView = (View) convertView.findViewById(R.id.separatorView);
            outstandingAmountTextView = (CustomTextView) convertView.findViewById(R.id.outstandingAmountTextView);
            payableAmountTextView = (CustomTextView) convertView.findViewById(R.id.payableAmountTextView);
            cardView = (LinearLayout) convertView.findViewById(R.id.cardView);
        }
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        private SwipeRevealLayout swipe_layout;
        private LinearLayout cardView;
        private CheckBox mCheckbox;
        private RelativeLayout mPatientDetailsClickLinearLayout;
        private CheckBox mGroupCheckbox;
        private LinearLayout downArrowClickLinearLayout;
        private RelativeLayout mHospitalDetailsLinearLayout;
        private CircularImageView mBulletImageView;
        private CustomTextView mClinicNameTextView;
        private CustomTextView mClinicAddress;
        private CustomTextView mClinicPatientCount;
        private ImageView mDownArrow;
        private ImageView upArrow;
        private ImageView mBluelineImageView;
        private CustomTextView mPatientIdTextView;
        private CircularImageView mPatientImageView;
        private CustomTextView mPatientNameTextView;
        private CustomTextView mPatientAgeTextView;
        private CustomTextView mPatientGenderTextView;
        private LinearLayout mPatientDetailsLinearLayout;
        private CustomTextView mOpdTypeTextView;
        private CustomTextView mPatientPhoneNumber;
        private View mSeparatorView;
        private CustomTextView mOutstandingAmountTextView;
        private CustomTextView mPayableAmountTextView;
        private CustomTextView mAppointmentTime;
        private LinearLayout mAppointmentCancel;
        private LinearLayout mAppointmentComplete;

        GroupViewHolder(View convertView) {
            super(convertView);
            mAppointmentCancel = (LinearLayout) convertView.findViewById(R.id.appointmentCancel);
            mAppointmentComplete = (LinearLayout) convertView.findViewById(R.id.appointmentComplete);
            swipe_layout = (SwipeRevealLayout) convertView.findViewById(R.id.swipe_layout);
            mCheckbox = (CheckBox) convertView.findViewById(R.id.checkbox);
            mPatientDetailsClickLinearLayout = (RelativeLayout) convertView.findViewById(R.id.patientDetailsClickLinearLayout);
            mGroupCheckbox = (CheckBox) convertView.findViewById(R.id.groupCheckbox);
            downArrowClickLinearLayout = (LinearLayout) convertView.findViewById(R.id.downArrowClickLinearLayout);
            cardView = (LinearLayout) convertView.findViewById(R.id.cardView);
            mHospitalDetailsLinearLayout = (RelativeLayout) convertView.findViewById(R.id.hospitalDetailsLinearLayout);
            mBulletImageView = (CircularImageView) convertView.findViewById(R.id.bulletImageView);
            mClinicNameTextView = (CustomTextView) convertView.findViewById(R.id.clinicNameTextView);
            mClinicAddress = (CustomTextView) convertView.findViewById(R.id.clinicAddress);
            mClinicPatientCount = (CustomTextView) convertView.findViewById(R.id.clinicPatientCount);
            mDownArrow = (ImageView) convertView.findViewById(R.id.downArrow);
            upArrow = (ImageView) convertView.findViewById(R.id.upArrow);
            mBluelineImageView = (ImageView) convertView.findViewById(R.id.bluelineImageView);
            mPatientIdTextView = (CustomTextView) convertView.findViewById(R.id.patientIdTextView);
            mPatientImageView = (CircularImageView) convertView.findViewById(R.id.patientImageView);
            mPatientNameTextView = (CustomTextView) convertView.findViewById(R.id.patientNameTextView);
            mPatientAgeTextView = (CustomTextView) convertView.findViewById(R.id.patientAgeTextView);
            mPatientGenderTextView = (CustomTextView) convertView.findViewById(R.id.patientGenderTextView);
            mPatientDetailsLinearLayout = (LinearLayout) convertView.findViewById(R.id.patientDetailsLinearLayout);
            mOpdTypeTextView = (CustomTextView) convertView.findViewById(R.id.opdTypeTextView);
            mPatientPhoneNumber = (CustomTextView) convertView.findViewById(R.id.patientPhoneNumber);
            mSeparatorView = (View) convertView.findViewById(R.id.separatorView);
            mOutstandingAmountTextView = (CustomTextView) convertView.findViewById(R.id.outstandingAmountTextView);
            mPayableAmountTextView = (CustomTextView) convertView.findViewById(R.id.payableAmountTextView);
            mAppointmentTime = (CustomTextView) convertView.findViewById(R.id.appointmentTime);
        }
    }

    public interface OnDownArrowClicked {
        void onDownArrowSetClick(int groupPosition, boolean isExpanded);

        void onLongPressOpenBottomMenu(boolean isLongPressed, int groupPosition);

        void onRecordFound(boolean isListEmpty);

        void onCheckUncheckRemoveSelectAllSelection(boolean ischecked);

        void onClickOfPatientDetails(PatientList patientListObject, String text);

        void onAppointmentClicked(Integer aptId, Integer patientId, int status, String type, int childPosition, int groupPosition);

        void onAppointmentCancelled(Integer aptId, Integer patientId, int status, String type, int childPosition, int groupPosition);

        void onGroupAppointmentClicked(Integer aptId, Integer patientId, int status, String type, int groupPosition);

        void onGroupAppointmentCancelled(Integer aptId, Integer patientId, int status, String type, int groupPosition);

        void expandAll();

        void collapseAll();

        void onPhoneNoClick(String patientPhone);
    }

}