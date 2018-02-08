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
import com.rescribe.doctor.model.my_appointments.ClinicList;
import com.rescribe.doctor.model.my_appointments.PatientList;
import com.rescribe.doctor.ui.customesViews.CircularImageView;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeetal on 31/1/18.
 */

public class AppointmentAdapter extends BaseExpandableListAdapter implements Filterable {

    private OnDownArrowClicked mOnDownArrowClicked;
    public static boolean isLongPressed;
    private ArrayList<ClinicList> mClinicListTemp;
    private Context mContext;
    private ArrayList<ClinicList> mDataList;

    public AppointmentAdapter(Context context, ArrayList<ClinicList> mClinicList, OnDownArrowClicked mOnDownArrowClicked) {
        this.mContext = context;
        this.mDataList = new ArrayList<>(mClinicList);
        this.mClinicListTemp = new ArrayList<>(mClinicList);

        this.mOnDownArrowClicked = mOnDownArrowClicked;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.mClinicListTemp.get(groupPosition).getPatientList().get(childPosititon);
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


        SpannableString patientID = new SpannableString(mContext.getString(R.string.id) + " " + mClinicListTemp.get(groupPosition).getPatientList().get(childPosition).getPatientId());
        patientID.setSpan(new UnderlineSpan(), 0, patientID.length(), 0);
        viewHolder.patientIdTextView.setText(patientID);
        if (mClinicListTemp.get(groupPosition).getPatientList().get(childPosition).getSalutation() == 1) {
            viewHolder.patientNameTextView.setText(mContext.getString(R.string.mr) + " " + mClinicListTemp.get(groupPosition).getPatientList().get(childPosition).getPatientName());
        } else if (mClinicListTemp.get(groupPosition).getPatientList().get(childPosition).getSalutation() == 2) {
            viewHolder.patientNameTextView.setText(mContext.getString(R.string.mrs) + " " + mClinicListTemp.get(groupPosition).getPatientList().get(childPosition).getPatientName());

        } else if (mClinicListTemp.get(groupPosition).getPatientList().get(childPosition).getSalutation() == 3) {
            viewHolder.patientNameTextView.setText(mContext.getString(R.string.miss) + " " + mClinicListTemp.get(groupPosition).getPatientList().get(childPosition).getPatientName());

        } else if (mClinicListTemp.get(groupPosition).getPatientList().get(childPosition).getSalutation() == 4) {
            viewHolder.patientNameTextView.setText(mClinicListTemp.get(groupPosition).getPatientList().get(childPosition).getPatientName());
        }

        if (mClinicListTemp.get(groupPosition).getPatientList().get(childPosition).getAge() == 0) {
            String getTodayDate = CommonMethods.getCurrentDate();
            String getBirthdayDate = mClinicListTemp.get(groupPosition).getPatientList().get(childPosition).getDateOfBirth();
            DateTime todayDateTime = CommonMethods.convertToDateTime(getTodayDate);
            DateTime birthdayDateTime = CommonMethods.convertToDateTime(getBirthdayDate);
            viewHolder.patientAgeTextView.setText(CommonMethods.displayAgeAnalysis(todayDateTime, birthdayDateTime) + " " + mContext.getString(R.string.years));
        } else {
            viewHolder.patientAgeTextView.setText(mClinicListTemp.get(groupPosition).getPatientList().get(childPosition).getAge() + " " + mContext.getString(R.string.years));

        }

        viewHolder.patientGenderTextView.setText(" " + mClinicListTemp.get(groupPosition).getPatientList().get(childPosition).getGender());
        if (mClinicListTemp.get(groupPosition).getPatientList().get(childPosition).getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.booked))) {
            viewHolder.opdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.book_color));
            viewHolder.opdTypeTextView.setText(mContext.getString(R.string.opd) + " " + mClinicListTemp.get(groupPosition).getPatientList().get(childPosition).getAppointmentStatus());
        } else if (mClinicListTemp.get(groupPosition).getPatientList().get(childPosition).getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.completed))) {
            viewHolder.opdTypeTextView.setText(mContext.getString(R.string.opd) + " " + mClinicListTemp.get(groupPosition).getPatientList().get(childPosition).getAppointmentStatus());
            viewHolder.opdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.complete_color));

        } else if (mClinicListTemp.get(groupPosition).getPatientList().get(childPosition).getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.follow))) {
            viewHolder.opdTypeTextView.setText(mContext.getString(R.string.opd) + " " + mClinicListTemp.get(groupPosition).getPatientList().get(childPosition).getAppointmentStatus());
            viewHolder.opdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.tagColor));

        }
        viewHolder.patientPhoneNumber.setText(mClinicListTemp.get(groupPosition).getPatientList().get(childPosition).getPatientPhone());
        viewHolder.outstandingAmountTextView.setText(mContext.getString(R.string.outstanding_amount) + " ");
        if (mClinicListTemp.get(groupPosition).getPatientList().get(childPosition).getOutStandingAmount() == 0) {
            viewHolder.payableAmountTextView.setText(" " + mContext.getString(R.string.nil));
            viewHolder.payableAmountTextView.setTextColor(ContextCompat.getColor(mContext, R.color.rating_color));

        } else {
            viewHolder.payableAmountTextView.setText(" Rs." + mClinicListTemp.get(groupPosition).getPatientList().get(childPosition).getOutStandingAmount() + "/-");
            viewHolder.payableAmountTextView.setTextColor(ContextCompat.getColor(mContext, R.color.Red));

        }
        viewHolder.appointmentTime.setVisibility(View.VISIBLE);
        viewHolder.appointmentTime.setText(CommonMethods.formatDateTime(mClinicListTemp.get(groupPosition).getPatientList().get(childPosition).getAppointmentTime(), RescribeConstants.DATE_PATTERN.hh_mm_a, RescribeConstants.DATE_PATTERN.HH_mm_ss, RescribeConstants.TIME).toLowerCase());
        TextDrawable textDrawable = CommonMethods.getTextDrawable(mContext, mClinicListTemp.get(groupPosition).getPatientList().get(childPosition).getPatientName());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.dontAnimate();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(false);
        requestOptions.placeholder(textDrawable);
        requestOptions.error(textDrawable);

        Glide.with(mContext)
                .load(mClinicListTemp.get(groupPosition).getPatientList().get(childPosition).getPatientImageUrl())
                .apply(requestOptions).thumbnail(0.5f)
                .into(viewHolder.patientImageView);
        viewHolder.checkbox.setChecked(mClinicListTemp.get(groupPosition).getPatientList().get(childPosition).isSelected());

        viewHolder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClinicListTemp.get(groupPosition).getPatientList().get(childPosition).setSelected(viewHolder.checkbox.isChecked());
                int selected = getSelectedCount(mClinicListTemp.get(groupPosition).getPatientList());
                mClinicListTemp.get(groupPosition).setSelectedGroupCheckbox(selected == mClinicListTemp.get(groupPosition).getPatientList().size() && mClinicListTemp.get(groupPosition).getPatientHeader().isSelected());
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
        return mClinicListTemp.get(groupPosition).getPatientList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mClinicListTemp.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.mClinicListTemp.size();
    }
    public ArrayList<ClinicList> getGroupList(){
        return mClinicListTemp;
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


        viewHolder.mClinicNameTextView.setText(mClinicListTemp.get(groupPosition).getClinicName() + " - ");
        viewHolder.mClinicAddress.setText(mClinicListTemp.get(groupPosition).getArea() + ", " + mClinicListTemp.get(groupPosition).getCity());
        viewHolder.mClinicPatientCount.setText(mClinicListTemp.get(groupPosition).getPatientList().size() + "");
        SpannableString patientID = new SpannableString(mContext.getString(R.string.id) + " " + mClinicListTemp.get(groupPosition).getPatientHeader().getPatientId() + "");
        patientID.setSpan(new UnderlineSpan(), 0, patientID.length(), 0);
        viewHolder.mPatientIdTextView.setText(patientID);

        if (mClinicListTemp.get(groupPosition).getPatientHeader().getSalutation() == 1) {
            viewHolder.mPatientNameTextView.setText(mContext.getString(R.string.mr) + " " + mClinicListTemp.get(groupPosition).getPatientHeader().getPatientName());
        } else if (mClinicListTemp.get(groupPosition).getPatientHeader().getSalutation() == 2) {
            viewHolder.mPatientNameTextView.setText(mContext.getString(R.string.mrs) + " " + mClinicListTemp.get(groupPosition).getPatientHeader().getPatientName());

        } else if (mClinicListTemp.get(groupPosition).getPatientHeader().getSalutation() == 3) {
            viewHolder.mPatientNameTextView.setText(mContext.getString(R.string.miss) + " " + mClinicListTemp.get(groupPosition).getPatientHeader().getPatientName());

        } else if (mClinicListTemp.get(groupPosition).getPatientHeader().getSalutation() == 4) {
            viewHolder.mPatientNameTextView.setText(mClinicListTemp.get(groupPosition).getPatientHeader().getPatientName());
        }
        if (mClinicListTemp.get(groupPosition).getPatientHeader().getAge() == 0) {
            String getTodayDate = CommonMethods.getCurrentDate();
            String getBirthdayDate = mClinicListTemp.get(groupPosition).getPatientHeader().getDateOfBirth();
            DateTime todayDateTime = CommonMethods.convertToDateTime(getTodayDate);
            DateTime birthdayDateTime = CommonMethods.convertToDateTime(getBirthdayDate);
            viewHolder.mPatientAgeTextView.setText(CommonMethods.displayAgeAnalysis(todayDateTime, birthdayDateTime) + " " + mContext.getString(R.string.years));
        } else {
            viewHolder.mPatientAgeTextView.setText(mClinicListTemp.get(groupPosition).getPatientHeader().getAge() + " " + mContext.getString(R.string.years));

        }
        viewHolder.mPatientGenderTextView.setText(" " + mClinicListTemp.get(groupPosition).getPatientHeader().getGender());
        if (mClinicListTemp.get(groupPosition).getPatientHeader().getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.booked))) {
            viewHolder.mOpdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.book_color));
            viewHolder.mOpdTypeTextView.setText(mContext.getString(R.string.opd) + " " + mClinicListTemp.get(groupPosition).getPatientHeader().getAppointmentStatus());
        } else if (mClinicListTemp.get(groupPosition).getPatientHeader().getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.completed))) {
            viewHolder.mOpdTypeTextView.setText(mContext.getString(R.string.opd) + " " + mClinicListTemp.get(groupPosition).getPatientHeader().getAppointmentStatus());
            viewHolder.mOpdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.complete_color));

        } else if (mClinicListTemp.get(groupPosition).getPatientHeader().getAppointmentStatus().toLowerCase().contains(mContext.getString(R.string.follow))) {
            viewHolder.mOpdTypeTextView.setText(mContext.getString(R.string.opd) + " " + mClinicListTemp.get(groupPosition).getPatientHeader().getAppointmentStatus());
            viewHolder.mOpdTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.tagColor));

        }
        viewHolder.mPatientPhoneNumber.setText(mClinicListTemp.get(groupPosition).getPatientHeader().getPatientPhone());
        viewHolder.mOutstandingAmountTextView.setText(mContext.getString(R.string.outstanding_amount) + " ");
        if (mClinicListTemp.get(groupPosition).getPatientHeader().getOutStandingAmount() == 0) {
            viewHolder.mPayableAmountTextView.setText(" " + mContext.getString(R.string.nil));
            viewHolder.mPayableAmountTextView.setTextColor(ContextCompat.getColor(mContext, R.color.rating_color));

        } else {
            viewHolder.mPayableAmountTextView.setText(" Rs." + mClinicListTemp.get(groupPosition).getPatientHeader().getOutStandingAmount() + "/-");
            viewHolder.mPayableAmountTextView.setTextColor(ContextCompat.getColor(mContext, R.color.Red));

        }

        viewHolder.mAppointmentTime.setVisibility(View.VISIBLE);
        viewHolder.mAppointmentTime.setText(CommonMethods.formatDateTime(mClinicListTemp.get(groupPosition).getPatientHeader().getAppointmentTime(), RescribeConstants.DATE_PATTERN.hh_mm_a, RescribeConstants.DATE_PATTERN.HH_mm_ss, RescribeConstants.TIME).toLowerCase());
        TextDrawable textDrawable = CommonMethods.getTextDrawable(mContext, mClinicListTemp.get(groupPosition).getPatientHeader().getPatientName());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.dontAnimate();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(false);
        requestOptions.placeholder(textDrawable);
        requestOptions.error(textDrawable);

        Glide.with(mContext)
                .load(mClinicListTemp.get(groupPosition).getPatientHeader().getPatientImageUrl())
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
        viewHolder.mGroupCheckbox.setChecked(mClinicListTemp.get(groupPosition).isSelectedGroupCheckbox());

        viewHolder.mCheckbox.setChecked(mClinicListTemp.get(groupPosition).getPatientHeader().isSelected());

        viewHolder.mCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClinicListTemp.get(groupPosition).getPatientHeader().setSelected(viewHolder.mCheckbox.isChecked());
                int selected = getSelectedCount(mClinicListTemp.get(groupPosition).getPatientList());
                mClinicListTemp.get(groupPosition).setSelectedGroupCheckbox(selected == mClinicListTemp.get(groupPosition).getPatientList().size() && mClinicListTemp.get(groupPosition).getPatientHeader().isSelected());
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
                mClinicListTemp.get(groupPosition).setSelectedGroupCheckbox(viewHolder.mGroupCheckbox.isChecked());

                mClinicListTemp.get(groupPosition).getPatientHeader().setSelected(viewHolder.mGroupCheckbox.isChecked());

                for (PatientList patient : mClinicListTemp.get(groupPosition).getPatientList())
                    patient.setSelected(viewHolder.mGroupCheckbox.isChecked());

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

                ArrayList<ClinicList> mListToShowAfterFilter;
                ArrayList<ClinicList> mTempClinicListToIterate = new ArrayList<>(mDataList);

                if (charString.isEmpty()) {
                    mListToShowAfterFilter = new ArrayList<>(mTempClinicListToIterate);
                } else {
                    mListToShowAfterFilter = new ArrayList<>();
                    for (ClinicList clinicListObj : mTempClinicListToIterate) {

                        List<PatientList> patientLists = clinicListObj.getPatientList();
                        ArrayList<PatientList> sortedPatientLists = new ArrayList<>();
                        //--------------
                        ClinicList tempClinicListObject = null;
                        try {
                            tempClinicListObject = (ClinicList) clinicListObj.clone();
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                        //-----------------
                        for (PatientList patientListObject : patientLists) {
                            if (patientListObject.getPatientName().toLowerCase().contains(charString.toLowerCase())
                                    || patientListObject.getPatientPhone().contains(charString)
                                    || String.valueOf(patientListObject.getPatientId()).contains(charString)) {
                                //--------
                                sortedPatientLists.add(patientListObject);
                            }
                        }

                        if (!sortedPatientLists.isEmpty()) {
                            tempClinicListObject.setPatientList(sortedPatientLists);
                            mListToShowAfterFilter.add(tempClinicListObject);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mListToShowAfterFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mClinicListTemp.clear();
                mClinicListTemp.addAll((ArrayList<ClinicList>) filterResults.values);

               if(mClinicListTemp.isEmpty()){
                   mOnDownArrowClicked.onRecordFound(true);
               }
               else mOnDownArrowClicked.onRecordFound(false);
                notifyDataSetChanged();
            }
        };
    }

  /*  private ArrayList<ClinicList> cloneList() {
        return mClinicListTemp = (ArrayList<ClinicList>) mDataList.clone();
    }
*/
    static class ChildViewHolder {
        CheckBox checkbox;
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