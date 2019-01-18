package com.rescribe.doctor.adapters.book_appointment;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.rescribe.doctor.R;
import com.rescribe.doctor.model.select_slot_book_appointment.TimeSlotData;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.rescribe.doctor.ui.fragments.book_appointment.SelectSlotTimeToBookAppointmentFragment.mSelectedTimeSlot;

public class BookAppointmentShowTimingsAdapter extends RecyclerView.Adapter<BookAppointmentShowTimingsAdapter.ListViewHolder> {

    private String mFormattedCurrentDateString;
    private String mSelectedDate;
    private Context mContext;
    private ArrayList<TimeSlotData> mDataList;
    private int appointmentFormat;

    public BookAppointmentShowTimingsAdapter(Context mContext, ArrayList<TimeSlotData> dataList, String mSelectedDate, int appointmentFormat) {
        this.mDataList = dataList;
        this.mContext = mContext;
        this.mSelectedDate = mSelectedDate;
        mFormattedCurrentDateString = CommonMethods.formatDateTime(CommonMethods.getCurrentDate(RescribeConstants.DD_MM_YYYY), RescribeConstants.DATE_PATTERN.YYYY_MM_DD, RescribeConstants.DD_MM_YYYY, RescribeConstants.DATE);
        this.appointmentFormat=appointmentFormat;
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_appointment_select_slot_childitem, parent, false);

        return new ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListViewHolder holder, int position) {
        final TimeSlotData timeSlotData = mDataList.get(position);
        String fromTime = timeSlotData.getFromTime();

        //-----------
        if (appointmentFormat==24){
            holder.showTime.setText(fromTime);
        }else {
            String s = CommonMethods.formatDateTime(fromTime, RescribeConstants.DATE_PATTERN.hh_mm_a, RescribeConstants.DATE_PATTERN.HH_mm, RescribeConstants.TIME);
            holder.showTime.setText(s);
        }
        //-----------

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mFormattedCurrentTimeString = CommonMethods.getCurrentTimeStamp(RescribeConstants.DATE_PATTERN.HH_mm);
                String s = "" + v.getTag();
                TimeSlotData timeSlot = mDataList.get(Integer.parseInt(s));
                String fromTime = timeSlot.getFromTime();
                String toTime = timeSlot.getToTime();
                String slotId = timeSlot.getSlotId();

                Date fromTimeDate = CommonMethods.convertStringToDate(mSelectedDate + " " + fromTime, RescribeConstants.DATE_PATTERN.YYYY_MM_DD_HH_mm);
                Date currentDate = CommonMethods.convertStringToDate(mFormattedCurrentDateString + " " + mFormattedCurrentTimeString, RescribeConstants.DATE_PATTERN.YYYY_MM_DD_HH_mm);
                if ((currentDate.getTime() > fromTimeDate.getTime()) && (mSelectedDate.equalsIgnoreCase(mFormattedCurrentDateString))) {
                    CommonMethods.showToast(mContext, mContext.getString(R.string.book_time_slot_err));
                    holder.showTime.setPaintFlags(holder.showTime.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    if (timeSlot.isAvailable()) {
                        fromTime = timeSlot.getFromTime();
                        if (fromTime.equalsIgnoreCase(mSelectedTimeSlot.getFromTime()) && slotId.equals(mSelectedTimeSlot.getSlotId())) {
                            mSelectedTimeSlot.setFromTime("");
                            mSelectedTimeSlot.setToTime("");
                            mSelectedTimeSlot.setSlotId("");
                        } else {
                            mSelectedTimeSlot.setFromTime(fromTime);
                            mSelectedTimeSlot.setToTime(toTime);
                            mSelectedTimeSlot.setSlotId(slotId);
                        }

                        notifyDataSetChanged();
                    }
                }
            }
        });

        Date fromTimeDate = CommonMethods.convertStringToDate(mSelectedDate + " " + fromTime, RescribeConstants.DATE_PATTERN.YYYY_MM_DD_HH_mm);
        Date currentDate = Calendar.getInstance().getTime();

        if (timeSlotData.getFromTime().equalsIgnoreCase(mSelectedTimeSlot.getFromTime()) && timeSlotData.getSlotId().equals(mSelectedTimeSlot.getSlotId())) {
            if (currentDate.getTime() < fromTimeDate.getTime()){
                holder.mainLayout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.green_round_rectangle));
                holder.showTime.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            } else {

                mSelectedTimeSlot.setSlotId("");
                mSelectedTimeSlot.setToTime("");
                mSelectedTimeSlot.setFromTime("");

                holder.mainLayout.setBackground(ContextCompat.getDrawable(mContext, R.color.white));
                holder.showTime.setTextColor(ContextCompat.getColor(mContext, R.color.tagColor));
                holder.showTime.setPaintFlags(holder.showTime.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }

        } else {
            holder.mainLayout.setBackground(ContextCompat.getDrawable(mContext, R.color.white));
            holder.showTime.setTextColor(ContextCompat.getColor(mContext, R.color.tagColor));
            if (!timeSlotData.isAvailable() || currentDate.getTime() > fromTimeDate.getTime())
                holder.showTime.setPaintFlags(holder.showTime.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        holder.view.setTag("" + position);

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    static class ListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.showTime)
        CustomTextView showTime;
        @BindView(R.id.mainLayout)
        LinearLayout mainLayout;
        View view;

        ListViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }
    }


    public static TimeSlotData getmSelectedToTimeSlot() {
        return mSelectedTimeSlot;
    }
}
