package com.rescribe.doctor.adapters.my_appointments;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.rescribe.doctor.R;
import com.rescribe.doctor.model.my_appointments.ClinicList;
import com.rescribe.doctor.ui.customesViews.CircularImageView;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * Created by jeetal on 31/1/18.
 */

public class AppointmentAdapter extends BaseExpandableListAdapter {
    ImageView bluelineImageView;
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

    private List<ClinicList> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;
    private Context mContext;
    private CustomTextView appointmentTime;

    public AppointmentAdapter(Context context, List<ClinicList> listDataHeader) {
        this.mContext = context;
        this._listDataHeader = listDataHeader;

    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {


        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.my_appointments_child_item, null);
        }
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

        SpannableString patientID = new SpannableString(mContext.getString(R.string.id) + " " + _listDataHeader.get(groupPosition).getPatientList().get(childPosition).getPatientId());
        patientID.setSpan(new UnderlineSpan(), 0, patientID.length(), 0);
        patientIdTextView.setText(patientID);
        if (_listDataHeader.get(groupPosition).getPatientList().get(childPosition).getSalutation() == 1) {
            patientNameTextView.setText(mContext.getString(R.string.mr) +" "+ _listDataHeader.get(groupPosition).getPatientList().get(childPosition).getPatientName());
        } else if (_listDataHeader.get(groupPosition).getPatientList().get(childPosition).getSalutation() == 2) {
            patientNameTextView.setText(mContext.getString(R.string.mrs) + " "+ _listDataHeader.get(groupPosition).getPatientList().get(childPosition).getPatientName());

        } else if (_listDataHeader.get(groupPosition).getPatientList().get(childPosition).getSalutation() == 3) {
            patientNameTextView.setText(mContext.getString(R.string.miss) +" "+  _listDataHeader.get(groupPosition).getPatientList().get(childPosition).getPatientName());

        } else if (_listDataHeader.get(groupPosition).getPatientList().get(childPosition).getSalutation() == 4) {
            patientNameTextView.setText(_listDataHeader.get(groupPosition).getPatientList().get(childPosition).getPatientName());
        }

        if(_listDataHeader.get(groupPosition).getPatientList().get(childPosition).getAge()==0){
            String getTodayDate = CommonMethods.getCurrentDate();
            String getBirthdayDate = _listDataHeader.get(groupPosition).getPatientList().get(childPosition).getDateOfBirth();
            DateTime todayDateTime = CommonMethods.convertToDateTime(getTodayDate);
            DateTime birthdayDateTime = CommonMethods.convertToDateTime(getBirthdayDate);
            patientAgeTextView.setText(CommonMethods.displayAgeAnalysis(todayDateTime, birthdayDateTime)+" "+ mContext.getString(R.string.years));
        }else{
            patientAgeTextView.setText(_listDataHeader.get(groupPosition).getPatientList().get(childPosition).getAge() +" "+ mContext.getString(R.string.years));

        }

        patientGenderTextView.setText(" " + _listDataHeader.get(groupPosition).getPatientList().get(childPosition).getGender());
        if (_listDataHeader.get(groupPosition).getPatientList().get(childPosition).getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.booked))) {
            opdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.book_color));
            opdTypeTextView.setText(mContext.getString(R.string.opd) + " " + _listDataHeader.get(groupPosition).getPatientList().get(childPosition).getAppointmentStatus());
        } else if (_listDataHeader.get(groupPosition).getPatientList().get(childPosition).getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.completed))) {
            opdTypeTextView.setText(mContext.getString(R.string.opd) + " " + _listDataHeader.get(groupPosition).getPatientList().get(childPosition).getAppointmentStatus());
            opdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.complete_color));

        } else if (_listDataHeader.get(groupPosition).getPatientList().get(childPosition).getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.follow))) {
            opdTypeTextView.setText(mContext.getString(R.string.opd) + " " + _listDataHeader.get(groupPosition).getPatientList().get(childPosition).getAppointmentStatus());
            opdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.tagColor));

        }
        patientPhoneNumber.setText(_listDataHeader.get(groupPosition).getPatientList().get(childPosition).getPatientPhone());
        outstandingAmountTextView.setText(mContext.getString(R.string.outstanding_amount) + " ");
        if (_listDataHeader.get(groupPosition).getPatientList().get(childPosition).getOutStandingAmount() == 0) {
            payableAmountTextView.setText(" " + mContext.getString(R.string.nil));
            payableAmountTextView.setTextColor(ContextCompat.getColor(mContext, R.color.rating_color));

        } else {
            payableAmountTextView.setText(" Rs." + _listDataHeader.get(groupPosition).getPatientList().get(childPosition).getOutStandingAmount() + "/-");
            payableAmountTextView.setTextColor(ContextCompat.getColor(mContext, R.color.Red));

        }
        appointmentTime.setVisibility(View.VISIBLE);
        appointmentTime.setText(CommonMethods.formatDateTime(_listDataHeader.get(groupPosition).getPatientList().get(childPosition).getAppointmentTime(), RescribeConstants.DATE_PATTERN.hh_mm_a, RescribeConstants.DATE_PATTERN.HH_mm_ss, RescribeConstants.TIME).toLowerCase());
        TextDrawable textDrawable = CommonMethods.getTextDrawable(mContext, _listDataHeader.get(groupPosition).getPatientList().get(childPosition).getPatientName());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.dontAnimate();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(true);
        requestOptions.placeholder(textDrawable);
        requestOptions.error(textDrawable);

        Glide.with(mContext)
                .load(_listDataHeader.get(groupPosition).getPatientList().get(childPosition).getPatientImageUrl())
                .apply(requestOptions).thumbnail(0.5f)
                .into(patientImageView);
        return convertView;

    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataHeader.get(groupPosition).getPatientList().size();
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
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.my_appointment_patients_item_layout, null);
        }

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
        mClinicAddress.setText(_listDataHeader.get(groupPosition).getAddress());
        mClinicPatientCount.setText(_listDataHeader.get(groupPosition).getPatientList().size() + "");
        SpannableString patientID = new SpannableString(mContext.getString(R.string.id) + " " + _listDataHeader.get(groupPosition).getPatientList().get(0).getPatientId() + "");
        patientID.setSpan(new UnderlineSpan(), 0, patientID.length(), 0);
        mPatientIdTextView.setText(patientID);

        if (_listDataHeader.get(groupPosition).getPatientList().get(0).getSalutation() == 1) {
            mPatientNameTextView.setText(mContext.getString(R.string.mr) +" "+ _listDataHeader.get(groupPosition).getPatientList().get(0).getPatientName());
        } else if (_listDataHeader.get(groupPosition).getPatientList().get(0).getSalutation() == 2) {
            mPatientNameTextView.setText(mContext.getString(R.string.mrs) + " "+ _listDataHeader.get(groupPosition).getPatientList().get(0).getPatientName());

        } else if (_listDataHeader.get(groupPosition).getPatientList().get(0).getSalutation() == 3) {
            mPatientNameTextView.setText(mContext.getString(R.string.miss) +" "+  _listDataHeader.get(groupPosition).getPatientList().get(0).getPatientName());

        } else if (_listDataHeader.get(groupPosition).getPatientList().get(0).getSalutation() == 4) {
            mPatientNameTextView.setText(_listDataHeader.get(groupPosition).getPatientList().get(0).getPatientName());
        }
         if(_listDataHeader.get(groupPosition).getPatientList().get(0).getAge()==0){
             String getTodayDate = CommonMethods.getCurrentDate();
             String getBirthdayDate = _listDataHeader.get(groupPosition).getPatientList().get(0).getDateOfBirth();
             DateTime todayDateTime = CommonMethods.convertToDateTime(getTodayDate);
             DateTime birthdayDateTime = CommonMethods.convertToDateTime(getBirthdayDate);
             mPatientAgeTextView.setText(CommonMethods.displayAgeAnalysis(todayDateTime, birthdayDateTime)+" "+ mContext.getString(R.string.years));
         }else{
             mPatientAgeTextView.setText(_listDataHeader.get(groupPosition).getPatientList().get(0).getAge() +" "+ mContext.getString(R.string.years));

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
}