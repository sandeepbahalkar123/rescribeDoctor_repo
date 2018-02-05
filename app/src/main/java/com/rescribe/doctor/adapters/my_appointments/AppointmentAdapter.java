package com.rescribe.doctor.adapters.my_appointments;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.rescribe.doctor.R;
import com.rescribe.doctor.model.my_appointments.ClinicList;
import com.rescribe.doctor.model.my_appointments.PatientList;
import com.rescribe.doctor.ui.customesViews.CircularImageView;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by jeetal on 31/1/18.
 */

public class AppointmentAdapter extends BaseExpandableListAdapter {

    private OnDownArrowClicked mOnDownArrowClicked;
    public boolean isLongPressed;
    private List<ClinicList> mClinicLists;
    private Context mContext;

    public AppointmentAdapter(Context context, List<ClinicList> mClinicLists, OnDownArrowClicked mOnDownArrowClicked) {
        this.mContext = context;
        this.mClinicLists = mClinicLists;
        this.mOnDownArrowClicked = mOnDownArrowClicked;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.mClinicLists.get(groupPosition).getPatientList().get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {


        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.my_appointments_child_item, null);
        }


        final CheckBox checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
        checkbox.setChecked(mClinicLists.get(groupPosition).getPatientList().get(childPosition).isSelected());

        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClinicLists.get(groupPosition).getPatientList().get(childPosition).setSelected(checkbox.isChecked());
                int selected = getSelectedCount(mClinicLists.get(groupPosition).getPatientList());
                mClinicLists.get(groupPosition).setSelectedGroupCheckbox(selected == mClinicLists.get(groupPosition).getPatientList().size() && mClinicLists.get(groupPosition).getPatientHeader().isSelected());
                notifyDataSetChanged();
            }
        });

        if (isLongPressed)
            checkbox.setVisibility(View.VISIBLE);
        else checkbox.setVisibility(View.GONE);

        LinearLayout cardView = (LinearLayout) convertView.findViewById(R.id.cardView);
        cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mOnDownArrowClicked.onLongPressOpenBottomMenu(isLongPressed);
                isLongPressed = !isLongPressed;
                notifyDataSetChanged();
                return false;
            }
        });


        ImageView bluelineImageView = (ImageView) convertView.findViewById(R.id.bluelineImageView);
        CustomTextView appointmentTime = (CustomTextView) convertView.findViewById(R.id.appointmentTime);
        CustomTextView patientIdTextView = (CustomTextView) convertView.findViewById(R.id.patientIdTextView);
        CircularImageView patientImageView = (CircularImageView) convertView.findViewById(R.id.patientImageView);
        CustomTextView patientNameTextView = (CustomTextView) convertView.findViewById(R.id.patientNameTextView);
        CustomTextView patientAgeTextView = (CustomTextView) convertView.findViewById(R.id.patientAgeTextView);
        CustomTextView patientGenderTextView = (CustomTextView) convertView.findViewById(R.id.patientGenderTextView);
        LinearLayout patientDetailsLinearLayout = (LinearLayout) convertView.findViewById(R.id.patientDetailsLinearLayout);
        CustomTextView opdTypeTextView = (CustomTextView) convertView.findViewById(R.id.opdTypeTextView);
        CustomTextView patientPhoneNumber = (CustomTextView) convertView.findViewById(R.id.patientPhoneNumber);
        View separatorView = (View) convertView.findViewById(R.id.separatorView);
        CustomTextView outstandingAmountTextView = (CustomTextView) convertView.findViewById(R.id.outstandingAmountTextView);
        CustomTextView payableAmountTextView = (CustomTextView) convertView.findViewById(R.id.payableAmountTextView);

        SpannableString patientID = new SpannableString(mContext.getString(R.string.id) + " " + mClinicLists.get(groupPosition).getPatientList().get(childPosition).getPatientId());
        patientID.setSpan(new UnderlineSpan(), 0, patientID.length(), 0);
        patientIdTextView.setText(patientID);
        if (mClinicLists.get(groupPosition).getPatientList().get(childPosition).getSalutation() == 1) {
            patientNameTextView.setText(mContext.getString(R.string.mr) + " " + mClinicLists.get(groupPosition).getPatientList().get(childPosition).getPatientName());
        } else if (mClinicLists.get(groupPosition).getPatientList().get(childPosition).getSalutation() == 2) {
            patientNameTextView.setText(mContext.getString(R.string.mrs) + " " + mClinicLists.get(groupPosition).getPatientList().get(childPosition).getPatientName());

        } else if (mClinicLists.get(groupPosition).getPatientList().get(childPosition).getSalutation() == 3) {
            patientNameTextView.setText(mContext.getString(R.string.miss) + " " + mClinicLists.get(groupPosition).getPatientList().get(childPosition).getPatientName());

        } else if (mClinicLists.get(groupPosition).getPatientList().get(childPosition).getSalutation() == 4) {
            patientNameTextView.setText(mClinicLists.get(groupPosition).getPatientList().get(childPosition).getPatientName());
        }

        if (mClinicLists.get(groupPosition).getPatientList().get(childPosition).getAge() == 0) {
            String getTodayDate = CommonMethods.getCurrentDate();
            String getBirthdayDate = mClinicLists.get(groupPosition).getPatientList().get(childPosition).getDateOfBirth();
            DateTime todayDateTime = CommonMethods.convertToDateTime(getTodayDate);
            DateTime birthdayDateTime = CommonMethods.convertToDateTime(getBirthdayDate);
            patientAgeTextView.setText(CommonMethods.displayAgeAnalysis(todayDateTime, birthdayDateTime) + " " + mContext.getString(R.string.years));
        } else {
            patientAgeTextView.setText(mClinicLists.get(groupPosition).getPatientList().get(childPosition).getAge() + " " + mContext.getString(R.string.years));

        }

        patientGenderTextView.setText(" " + mClinicLists.get(groupPosition).getPatientList().get(childPosition).getGender());
        if (mClinicLists.get(groupPosition).getPatientList().get(childPosition).getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.booked))) {
            opdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.book_color));
            opdTypeTextView.setText(mContext.getString(R.string.opd) + " " + mClinicLists.get(groupPosition).getPatientList().get(childPosition).getAppointmentStatus());
        } else if (mClinicLists.get(groupPosition).getPatientList().get(childPosition).getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.completed))) {
            opdTypeTextView.setText(mContext.getString(R.string.opd) + " " + mClinicLists.get(groupPosition).getPatientList().get(childPosition).getAppointmentStatus());
            opdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.complete_color));

        } else if (mClinicLists.get(groupPosition).getPatientList().get(childPosition).getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.follow))) {
            opdTypeTextView.setText(mContext.getString(R.string.opd) + " " + mClinicLists.get(groupPosition).getPatientList().get(childPosition).getAppointmentStatus());
            opdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.tagColor));

        }
        patientPhoneNumber.setText(mClinicLists.get(groupPosition).getPatientList().get(childPosition).getPatientPhone());
        outstandingAmountTextView.setText(mContext.getString(R.string.outstanding_amount) + " ");
        if (mClinicLists.get(groupPosition).getPatientList().get(childPosition).getOutStandingAmount() == 0) {
            payableAmountTextView.setText(" " + mContext.getString(R.string.nil));
            payableAmountTextView.setTextColor(ContextCompat.getColor(mContext, R.color.rating_color));

        } else {
            payableAmountTextView.setText(" Rs." + mClinicLists.get(groupPosition).getPatientList().get(childPosition).getOutStandingAmount() + "/-");
            payableAmountTextView.setTextColor(ContextCompat.getColor(mContext, R.color.Red));

        }
        appointmentTime.setVisibility(View.VISIBLE);
        appointmentTime.setText(CommonMethods.formatDateTime(mClinicLists.get(groupPosition).getPatientList().get(childPosition).getAppointmentTime(), RescribeConstants.DATE_PATTERN.hh_mm_a, RescribeConstants.DATE_PATTERN.HH_mm_ss, RescribeConstants.TIME).toLowerCase());
        TextDrawable textDrawable = CommonMethods.getTextDrawable(mContext, mClinicLists.get(groupPosition).getPatientList().get(childPosition).getPatientName());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.dontAnimate();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(false);
        requestOptions.placeholder(textDrawable);
        requestOptions.error(textDrawable);

        Glide.with(mContext)
                .load(mClinicLists.get(groupPosition).getPatientList().get(childPosition).getPatientImageUrl())
                .apply(requestOptions).thumbnail(0.5f)
                .into(patientImageView);
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
        return mClinicLists.get(groupPosition).getPatientList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mClinicLists.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.mClinicLists.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded,
                             View convertView, final ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.my_appointment_patients_item_layout, null);
        }

        LinearLayout downArrowClickLinearLayout = (LinearLayout) convertView.findViewById(R.id.downArrowClickLinearLayout);
        final CheckBox mGroupCheckbox = (CheckBox) convertView.findViewById(R.id.groupCheckbox);
        mGroupCheckbox.setChecked(mClinicLists.get(groupPosition).isSelectedGroupCheckbox());

        final CheckBox mCheckbox = (CheckBox) convertView.findViewById(R.id.checkbox);
        mCheckbox.setChecked(mClinicLists.get(groupPosition).getPatientHeader().isSelected());

        mCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClinicLists.get(groupPosition).getPatientHeader().setSelected(mCheckbox.isChecked());
                int selected = getSelectedCount(mClinicLists.get(groupPosition).getPatientList());
                mClinicLists.get(groupPosition).setSelectedGroupCheckbox(selected == mClinicLists.get(groupPosition).getPatientList().size() && mClinicLists.get(groupPosition).getPatientHeader().isSelected());
                notifyDataSetChanged();
            }
        });


        if (isLongPressed) {
            mOnDownArrowClicked.onLongPressOpenBottomMenu(isLongPressed);
            mCheckbox.setVisibility(View.VISIBLE);
            mGroupCheckbox.setVisibility(View.VISIBLE);
        } else {
            mOnDownArrowClicked.onLongPressOpenBottomMenu(isLongPressed);
            mCheckbox.setVisibility(View.GONE);
            mGroupCheckbox.setVisibility(View.GONE);
        }

        mGroupCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClinicLists.get(groupPosition).setSelectedGroupCheckbox(mGroupCheckbox.isChecked());

                mClinicLists.get(groupPosition).getPatientHeader().setSelected(mGroupCheckbox.isChecked());

                for (PatientList patient : mClinicLists.get(groupPosition).getPatientList())
                    patient.setSelected(mGroupCheckbox.isChecked());

                notifyDataSetChanged();
            }
        });

        LinearLayout cardView = (LinearLayout) convertView.findViewById(R.id.cardView);
        cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mOnDownArrowClicked.onLongPressOpenBottomMenu(isLongPressed);
                isLongPressed = !isLongPressed;
                notifyDataSetChanged();
                return false;
            }
        });
        RelativeLayout mHospitalDetailsLinearLayout = (RelativeLayout) convertView.findViewById(R.id.hospitalDetailsLinearLayout);
        CircularImageView mBulletImageView = (CircularImageView) convertView.findViewById(R.id.bulletImageView);
        CustomTextView mClinicNameTextView = (CustomTextView) convertView.findViewById(R.id.clinicNameTextView);
        CustomTextView mClinicAddress = (CustomTextView) convertView.findViewById(R.id.clinicAddress);
        CustomTextView mClinicPatientCount = (CustomTextView) convertView.findViewById(R.id.clinicPatientCount);
        ImageView mDownArrow = (ImageView) convertView.findViewById(R.id.downArrow);
        ImageView mBluelineImageView = (ImageView) convertView.findViewById(R.id.bluelineImageView);
        CustomTextView mPatientIdTextView = (CustomTextView) convertView.findViewById(R.id.patientIdTextView);
        CircularImageView mPatientImageView = (CircularImageView) convertView.findViewById(R.id.patientImageView);
        CustomTextView mPatientNameTextView = (CustomTextView) convertView.findViewById(R.id.patientNameTextView);
        CustomTextView mPatientAgeTextView = (CustomTextView) convertView.findViewById(R.id.patientAgeTextView);
        CustomTextView mPatientGenderTextView = (CustomTextView) convertView.findViewById(R.id.patientGenderTextView);
        LinearLayout mPatientDetailsLinearLayout = (LinearLayout) convertView.findViewById(R.id.patientDetailsLinearLayout);
        CustomTextView mOpdTypeTextView = (CustomTextView) convertView.findViewById(R.id.opdTypeTextView);
        CustomTextView mPatientPhoneNumber = (CustomTextView) convertView.findViewById(R.id.patientPhoneNumber);
        View mSeparatorView = (View) convertView.findViewById(R.id.separatorView);
        CustomTextView mOutstandingAmountTextView = (CustomTextView) convertView.findViewById(R.id.outstandingAmountTextView);
        CustomTextView mPayableAmountTextView = (CustomTextView) convertView.findViewById(R.id.payableAmountTextView);
        CustomTextView mAppointmentTime = (CustomTextView) convertView.findViewById(R.id.appointmentTime);

        mClinicNameTextView.setText(mClinicLists.get(groupPosition).getClinicName() + " - ");
        mClinicAddress.setText(mClinicLists.get(groupPosition).getArea() + ", " + mClinicLists.get(groupPosition).getCity());
        mClinicPatientCount.setText(mClinicLists.get(groupPosition).getPatientList().size() + 1 + "");
        SpannableString patientID = new SpannableString(mContext.getString(R.string.id) + " " + mClinicLists.get(groupPosition).getPatientHeader().getPatientId() + "");
        patientID.setSpan(new UnderlineSpan(), 0, patientID.length(), 0);
        mPatientIdTextView.setText(patientID);

        if (mClinicLists.get(groupPosition).getPatientHeader().getSalutation() == 1) {
            mPatientNameTextView.setText(mContext.getString(R.string.mr) + " " + mClinicLists.get(groupPosition).getPatientHeader().getPatientName());
        } else if (mClinicLists.get(groupPosition).getPatientHeader().getSalutation() == 2) {
            mPatientNameTextView.setText(mContext.getString(R.string.mrs) + " " + mClinicLists.get(groupPosition).getPatientHeader().getPatientName());

        } else if (mClinicLists.get(groupPosition).getPatientHeader().getSalutation() == 3) {
            mPatientNameTextView.setText(mContext.getString(R.string.miss) + " " + mClinicLists.get(groupPosition).getPatientHeader().getPatientName());

        } else if (mClinicLists.get(groupPosition).getPatientHeader().getSalutation() == 4) {
            mPatientNameTextView.setText(mClinicLists.get(groupPosition).getPatientHeader().getPatientName());
        }
        if (mClinicLists.get(groupPosition).getPatientHeader().getAge() == 0) {
            String getTodayDate = CommonMethods.getCurrentDate();
            String getBirthdayDate = mClinicLists.get(groupPosition).getPatientHeader().getDateOfBirth();
            DateTime todayDateTime = CommonMethods.convertToDateTime(getTodayDate);
            DateTime birthdayDateTime = CommonMethods.convertToDateTime(getBirthdayDate);
            mPatientAgeTextView.setText(CommonMethods.displayAgeAnalysis(todayDateTime, birthdayDateTime) + " " + mContext.getString(R.string.years));
        } else {
            mPatientAgeTextView.setText(mClinicLists.get(groupPosition).getPatientHeader().getAge() + " " + mContext.getString(R.string.years));

        }
        mPatientGenderTextView.setText(" " + mClinicLists.get(groupPosition).getPatientHeader().getGender());
        if (mClinicLists.get(groupPosition).getPatientHeader().getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.booked))) {
            mOpdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.book_color));
            mOpdTypeTextView.setText(mContext.getString(R.string.opd) + " " + mClinicLists.get(groupPosition).getPatientHeader().getAppointmentStatus());
        } else if (mClinicLists.get(groupPosition).getPatientHeader().getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.completed))) {
            mOpdTypeTextView.setText(mContext.getString(R.string.opd) + " " + mClinicLists.get(groupPosition).getPatientHeader().getAppointmentStatus());
            mOpdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.complete_color));

        } else if (mClinicLists.get(groupPosition).getPatientHeader().getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.follow))) {
            mOpdTypeTextView.setText(mContext.getString(R.string.opd) + " " + mClinicLists.get(groupPosition).getPatientHeader().getAppointmentStatus());
            mOpdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.tagColor));

        }
        mPatientPhoneNumber.setText(mClinicLists.get(groupPosition).getPatientHeader().getPatientPhone());
        mOutstandingAmountTextView.setText(mContext.getString(R.string.outstanding_amount) + " ");
        if (mClinicLists.get(groupPosition).getPatientHeader().getOutStandingAmount() == 0) {
            mPayableAmountTextView.setText(" " + mContext.getString(R.string.nil));
            mPayableAmountTextView.setTextColor(ContextCompat.getColor(mContext, R.color.rating_color));

        } else {
            mPayableAmountTextView.setText(" Rs." + mClinicLists.get(groupPosition).getPatientHeader().getOutStandingAmount() + "/-");
            mPayableAmountTextView.setTextColor(ContextCompat.getColor(mContext, R.color.Red));

        }

        mAppointmentTime.setVisibility(View.VISIBLE);
        mAppointmentTime.setText(CommonMethods.formatDateTime(mClinicLists.get(groupPosition).getPatientHeader().getAppointmentTime(), RescribeConstants.DATE_PATTERN.hh_mm_a, RescribeConstants.DATE_PATTERN.HH_mm_ss, RescribeConstants.TIME).toLowerCase());
        TextDrawable textDrawable = CommonMethods.getTextDrawable(mContext, mClinicLists.get(groupPosition).getPatientHeader().getPatientName());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.dontAnimate();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(false);
        requestOptions.placeholder(textDrawable);
        requestOptions.error(textDrawable);

        Glide.with(mContext)
                .load(mClinicLists.get(groupPosition).getPatientHeader().getPatientImageUrl())
                .apply(requestOptions).thumbnail(0.5f)
                .into(mPatientImageView);

        mHospitalDetailsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnDownArrowClicked.onDownArrowSetClick(groupPosition, isExpanded);
            }
        });

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

        void onLongPressOpenBottomMenu(boolean isLongPressed);
    }
}