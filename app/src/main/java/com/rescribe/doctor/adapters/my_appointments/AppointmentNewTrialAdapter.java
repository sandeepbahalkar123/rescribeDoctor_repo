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
import android.widget.CompoundButton;
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

import java.util.HashMap;
import java.util.List;

/**
 * Created by jeetal on 31/1/18.
 */

public class AppointmentNewTrialAdapter extends BaseExpandableListAdapter {
    ImageView bluelineImageView;
    CustomTextView patientIdTextView;
    CircularImageView patientImageView;
    CustomTextView patientNameTextView;
    CustomTextView patientAgeTextView;
    CustomTextView patientGenderTextView;
    LinearLayout patientDetailsLinearLayout;
    CustomTextView opdTypeTextView;
    CustomTextView patientPhoneNumber;
    CheckBox checkbox;
    View separatorView;
    CustomTextView outstandingAmountTextView;
    CustomTextView payableAmountTextView;
    private List<ClinicList> _listDataHeader;
    private Context mContext;
    HashMap<ClinicList, List<PatientList>> mChildClinicLists;
    private CustomTextView appointmentTime;
    private OnDownArrowClicked mOnDownArrowClicked;
    public boolean isLongPressed;
    private LinearLayout cardView;
    private HashMap<Integer, boolean[]> mChildCheckStates;
    private int mChildindex;


    public AppointmentNewTrialAdapter(Context context, List<ClinicList> listDataHeader, HashMap<ClinicList, List<PatientList>> mClinicLists, OnDownArrowClicked mOnDownArrowClicked) {
        this.mContext = context;
        this._listDataHeader = listDataHeader;
        this.mChildClinicLists = mClinicLists;
        this.mOnDownArrowClicked = mOnDownArrowClicked;
        mChildCheckStates = new HashMap<Integer, boolean[]>();

    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.mChildClinicLists.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
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
        mChildindex = childPosition;
        final PatientList mPatientList = (PatientList) getChild(groupPosition, childPosition);

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
        checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);


        checkbox.setChecked(_listDataHeader.get(groupPosition).isSelectedGroupCheckbox());

        if (isLongPressed) {
            checkbox.setVisibility(View.VISIBLE);
            mOnDownArrowClicked.onLongPressOpenBottomMenu(isLongPressed);
        } else {
            checkbox.setVisibility(View.GONE);
            mOnDownArrowClicked.onLongPressOpenBottomMenu(isLongPressed);
        }

        cardView = (LinearLayout) convertView.findViewById(R.id.cardView);
        cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isLongPressed = !isLongPressed;
                notifyDataSetChanged();
                return false;
            }
        });



        SpannableString patientID = new SpannableString(mContext.getString(R.string.id) + " " + mPatientList.getPatientId());
        patientID.setSpan(new UnderlineSpan(), 0, patientID.length(), 0);
        patientIdTextView.setText(patientID);
        if (mPatientList.getSalutation() == 1) {
            patientNameTextView.setText(mContext.getString(R.string.mr) + " " + mPatientList.getPatientName());
        } else if (mPatientList.getSalutation() == 2) {
            patientNameTextView.setText(mContext.getString(R.string.mrs) + " " + mPatientList.getPatientName());

        } else if (mPatientList.getSalutation() == 3) {
            patientNameTextView.setText(mContext.getString(R.string.miss) + " " + mPatientList.getPatientName());

        } else if (mPatientList.getSalutation() == 4) {
            patientNameTextView.setText(mPatientList.getPatientName());
        }

        if (mPatientList.getAge() == 0) {
            String getTodayDate = CommonMethods.getCurrentDate();
            String getBirthdayDate = mPatientList.getDateOfBirth();
            DateTime todayDateTime = CommonMethods.convertToDateTime(getTodayDate);
            DateTime birthdayDateTime = CommonMethods.convertToDateTime(getBirthdayDate);
            patientAgeTextView.setText(CommonMethods.displayAgeAnalysis(todayDateTime, birthdayDateTime) + " " + mContext.getString(R.string.years));
        } else {
            patientAgeTextView.setText(mPatientList.getAge() + " " + mContext.getString(R.string.years));

        }

        patientGenderTextView.setText(" " + mPatientList.getGender());
        if (mPatientList.getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.booked))) {
            opdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.book_color));
            opdTypeTextView.setText(mContext.getString(R.string.opd) + " " + mPatientList.getAppointmentStatus());
        } else if (mPatientList.getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.completed))) {
            opdTypeTextView.setText(mContext.getString(R.string.opd) + " " + mPatientList.getAppointmentStatus());
            opdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.complete_color));

        } else if (mPatientList.getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.follow))) {
            opdTypeTextView.setText(mContext.getString(R.string.opd) + " " + mPatientList.getAppointmentStatus());
            opdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.tagColor));

        }
        patientPhoneNumber.setText(mPatientList.getPatientPhone());
        outstandingAmountTextView.setText(mContext.getString(R.string.outstanding_amount) + " ");
        if (mPatientList.getOutStandingAmount() == 0) {
            payableAmountTextView.setText(" " + mContext.getString(R.string.nil));
            payableAmountTextView.setTextColor(ContextCompat.getColor(mContext, R.color.rating_color));

        } else {
            payableAmountTextView.setText(" Rs." + mPatientList.getOutStandingAmount() + "/-");
            payableAmountTextView.setTextColor(ContextCompat.getColor(mContext, R.color.Red));

        }
        appointmentTime.setVisibility(View.VISIBLE);
        appointmentTime.setText(CommonMethods.formatDateTime(mPatientList.getAppointmentTime(), RescribeConstants.DATE_PATTERN.hh_mm_a, RescribeConstants.DATE_PATTERN.HH_mm_ss, RescribeConstants.TIME).toLowerCase());
        TextDrawable textDrawable = CommonMethods.getTextDrawable(mContext, mPatientList.getPatientName());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.dontAnimate();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(true);
        requestOptions.placeholder(textDrawable);
        requestOptions.error(textDrawable);

        Glide.with(mContext)
                .load(mPatientList.getPatientImageUrl())
                .apply(requestOptions).thumbnail(0.5f)
                .into(patientImageView);
        return convertView;

    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChildClinicLists.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.my_appointment_patients_item_layout, null);
        }
        RelativeLayout mHospitalDetailsLinearLayout = (RelativeLayout) convertView.findViewById(R.id.hospitalDetailsLinearLayout);
        LinearLayout downArrowClickLinearLayout = (LinearLayout) convertView.findViewById(R.id.downArrowClickLinearLayout);
        final CheckBox mGroupCheckbox = (CheckBox) convertView.findViewById(R.id.groupCheckbox);

        final CheckBox mCheckbox = (CheckBox) convertView.findViewById(R.id.checkbox);
        mCheckbox.setChecked(_listDataHeader.get(groupPosition).isSelectedGroupCheckbox());
        mGroupCheckbox.setOnCheckedChangeListener(null);
        mCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // mClinicLists.get(groupPosition).setSelectedGroupCheckbox(mCheckbox.isChecked());
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

        mGroupCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                _listDataHeader.get(groupPosition).setSelectedGroupCheckbox(isChecked);
                _listDataHeader.get(groupPosition).getPatientList().get(0).setSelected(isChecked);

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

        mClinicNameTextView.setText(_listDataHeader.get(groupPosition).getClinicName() + " - ");
        mClinicAddress.setText(_listDataHeader.get(groupPosition).getArea() + ", " + _listDataHeader.get(groupPosition).getCity());
        mClinicPatientCount.setText(_listDataHeader.get(groupPosition).getPatientList().size() + "");
        SpannableString patientID = new SpannableString(mContext.getString(R.string.id) + " " + _listDataHeader.get(groupPosition).getPatientList().get(0).getPatientId() + "");
        patientID.setSpan(new UnderlineSpan(), 0, patientID.length(), 0);
        mPatientIdTextView.setText(patientID);

        if (_listDataHeader.get(groupPosition).getPatientList().get(0).getSalutation() == 1) {
            mPatientNameTextView.setText(mContext.getString(R.string.mr) + " " + _listDataHeader.get(groupPosition).getPatientList().get(0).getPatientName());
        } else if (_listDataHeader.get(groupPosition).getPatientList().get(0).getSalutation() == 2) {
            mPatientNameTextView.setText(mContext.getString(R.string.mrs) + " " + _listDataHeader.get(groupPosition).getPatientList().get(0).getPatientName());

        } else if (_listDataHeader.get(groupPosition).getPatientList().get(0).getSalutation() == 3) {
            mPatientNameTextView.setText(mContext.getString(R.string.miss) + " " + _listDataHeader.get(groupPosition).getPatientList().get(0).getPatientName());

        } else if (_listDataHeader.get(groupPosition).getPatientList().get(0).getSalutation() == 4) {
            mPatientNameTextView.setText(_listDataHeader.get(groupPosition).getPatientList().get(0).getPatientName());
        }
        if (_listDataHeader.get(groupPosition).getPatientList().get(0).getAge() == 0) {
            String getTodayDate = CommonMethods.getCurrentDate();
            String getBirthdayDate = _listDataHeader.get(groupPosition).getPatientList().get(0).getDateOfBirth();
            DateTime todayDateTime = CommonMethods.convertToDateTime(getTodayDate);
            DateTime birthdayDateTime = CommonMethods.convertToDateTime(getBirthdayDate);
            mPatientAgeTextView.setText(CommonMethods.displayAgeAnalysis(todayDateTime, birthdayDateTime) + " " + mContext.getString(R.string.years));
        } else {
            mPatientAgeTextView.setText(_listDataHeader.get(groupPosition).getPatientList().get(0).getAge() + " " + mContext.getString(R.string.years));

        }
        mPatientGenderTextView.setText(" " + _listDataHeader.get(groupPosition).getPatientList().get(0).getGender());
        if (_listDataHeader.get(groupPosition).getPatientList().get(0).getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.booked))) {
            mOpdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.book_color));
            mOpdTypeTextView.setText(mContext.getString(R.string.opd) + " " + _listDataHeader.get(groupPosition).getPatientList().get(0).getAppointmentStatus());
        } else if (_listDataHeader.get(groupPosition).getPatientList().get(0).getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.completed))) {
            mOpdTypeTextView.setText(mContext.getString(R.string.opd) + " " + _listDataHeader.get(groupPosition).getPatientList().get(0).getAppointmentStatus());
            mOpdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.complete_color));

        } else if (_listDataHeader.get(groupPosition).getPatientList().get(0).getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.follow))) {
            mOpdTypeTextView.setText(mContext.getString(R.string.opd) + " " + _listDataHeader.get(groupPosition).getPatientList().get(0).getAppointmentStatus());
            mOpdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.tagColor));

        }
        mPatientPhoneNumber.setText(_listDataHeader.get(groupPosition).getPatientList().get(0).getPatientPhone());
        mOutstandingAmountTextView.setText(mContext.getString(R.string.outstanding_amount) + " ");
        if (_listDataHeader.get(groupPosition).getPatientList().get(0).getOutStandingAmount() == 0) {
            mPayableAmountTextView.setText(" " + mContext.getString(R.string.nil));
            mPayableAmountTextView.setTextColor(ContextCompat.getColor(mContext, R.color.rating_color));

        } else {
            mPayableAmountTextView.setText(" Rs." + _listDataHeader.get(groupPosition).getPatientList().get(0).getOutStandingAmount() + "/-");
            mPayableAmountTextView.setTextColor(ContextCompat.getColor(mContext, R.color.Red));

        }

        mAppointmentTime.setVisibility(View.VISIBLE);
        mAppointmentTime.setText(CommonMethods.formatDateTime(_listDataHeader.get(groupPosition).getPatientList().get(0).getAppointmentTime(), RescribeConstants.DATE_PATTERN.hh_mm_a, RescribeConstants.DATE_PATTERN.HH_mm_ss, RescribeConstants.TIME).toLowerCase());
        TextDrawable textDrawable = CommonMethods.getTextDrawable(mContext, _listDataHeader.get(groupPosition).getPatientList().get(0).getPatientName());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.dontAnimate();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(true);
        requestOptions.placeholder(textDrawable);
        requestOptions.error(textDrawable);

        Glide.with(mContext)
                .load(_listDataHeader.get(groupPosition).getPatientList().get(0).getPatientImageUrl())
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