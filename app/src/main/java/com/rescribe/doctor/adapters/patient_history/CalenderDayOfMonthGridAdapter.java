package com.rescribe.doctor.adapters.patient_history;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rescribe.doctor.R;
import com.rescribe.doctor.model.patient.patient_history.PatientHistoryInfo;
import com.rescribe.doctor.ui.customesViews.CircularImageView;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by riteshpandhurkar on 6/2/18.
 */

public class CalenderDayOfMonthGridAdapter extends RecyclerView.Adapter<CalenderDayOfMonthGridAdapter.ListViewHolder> {

    private final OnDayClickListener mListener;
    private Context mContext;
    private ArrayList<PatientHistoryInfo> mDays;
    public  boolean longPressed;
    private SimpleDateFormat mDateFormat;

    public CalenderDayOfMonthGridAdapter(Context mContext, ArrayList<PatientHistoryInfo> days, OnDayClickListener listener) {
        this.mDays = days;
        this.mContext = mContext;
        mDateFormat = new SimpleDateFormat(RescribeConstants.DATE_PATTERN.DD_MM_YYYY, Locale.US);
        this.mListener = listener;
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_doctor_list_layout, parent, false);

        return new ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListViewHolder holder, int position) {
        final PatientHistoryInfo patientHistoryInfoObject = mDays.get(position);

        Date date = CommonMethods.convertStringToDate(patientHistoryInfoObject.getVisitDate(), RescribeConstants.DATE_PATTERN.YYYY_MM_DD);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //----
        int day = cal.get(Calendar.DAY_OF_MONTH);

        String toDisplay = day + "<sup>" + CommonMethods.getSuffixForNumber(day) + "</sup>";
        //------
        if (patientHistoryInfoObject.getVisitDate().equalsIgnoreCase(mDateFormat.format(new Date()))) {
            toDisplay = toDisplay + "\n" + mContext.getString(R.string.just_now);
        }
        //------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.date.setText(Html.fromHtml(toDisplay, Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.date.setText(Html.fromHtml(toDisplay));
        }

        if (position % 2 == 0) {
            holder.parentDataContainer.setBackgroundColor(Color.WHITE);
            holder.sideBarView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.tagColor));
        } else {
            holder.parentDataContainer.setBackgroundColor(Color.LTGRAY);
            holder.sideBarView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.statusbar));

        }

        holder.patientOpdInfoLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                for (PatientHistoryInfo datesData : mDays) {
                    datesData.setLongpressed(false);
                }
                if (patientHistoryInfoObject.isLongpressed()) {

                    patientHistoryInfoObject.setLongpressed(false);

                } else {

                    patientHistoryInfoObject.setLongpressed(true);

                }
                notifyDataSetChanged();
                mListener.onLongClicked(patientHistoryInfoObject.isLongpressed());
                return false;
            }
        });
       /* if (day.getDate() != 0) {
            holder.day.setText("" + day.getDate());
            holder.day.setTag(day);
        } else {
            holder.day.setText("");

        }
*/
       /* if (day != null) {
            switch (day.getOpdStatus().toLowerCase()) {
                case RescribeConstants.PATIENT_OPDS_STATUS.OPD_COMPLETED:
                    holder.day.setBackgroundResource(R.drawable.opd_completed_patch);
                    holder.day.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                    holder.otherStatusView.setVisibility(View.GONE);
                    break;
                case RescribeConstants.PATIENT_OPDS_STATUS.OPD_SAVED:
                    holder.otherStatusView.setBackgroundResource(R.drawable.saved_circle);
                    holder.otherStatusView.setVisibility(View.VISIBLE);
                    break;
                case RescribeConstants.PATIENT_OPDS_STATUS.ONLY_ATTACHMENTS:
                    holder.otherStatusView.setBackgroundResource(R.drawable.attachments_circle);
                    holder.otherStatusView.setVisibility(View.VISIBLE);
                    break;
                case RescribeConstants.PATIENT_OPDS_STATUS.NO_SHOW:
                    holder.otherStatusView.setBackgroundResource(R.drawable.no_show);
                    holder.otherStatusView.setVisibility(View.VISIBLE);
                    break;
            }
        }
*/
        /*holder.day.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                for (PatientHistoryInfo datesData : mDays) {
                    datesData.setLongPressed(false);
                }
                if (day.isLongPressed()) {

                    day.setLongPressed(false);

                } else {

                    day.setLongPressed(true);

                }
                notifyDataSetChanged();
                mListener.onLongClicked(day.isLongPressed());

                return false;
            }
        });
        if (day.getOpdStatus().toLowerCase().equalsIgnoreCase(RescribeConstants.PATIENT_OPDS_STATUS.OPD_COMPLETED)) {
            if (day.isLongPressed()) {
                holder.day.setTextColor(ContextCompat.getColor(mContext, R.color.orange));

            } else {
                holder.day.setTextColor(ContextCompat.getColor(mContext, R.color.white));

            }

        }*/


        //--------

    }

    @Override
    public int getItemCount() {
        return mDays.size();
    }

    public ArrayList<PatientHistoryInfo> getAdapterList() {
        return mDays;
    }

    static class ListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.sideBarView)
        TextView sideBarView;
        @BindView(R.id.date)
        CustomTextView date;
        @BindView(R.id.upperLine)
        TextView upperLine;
        @BindView(R.id.circularBulletMainElement)
        ImageView circularBulletMainElement;
        @BindView(R.id.circularBulletChildElement)
        ImageView circularBulletChildElement;
        @BindView(R.id.lowerLine)
        TextView lowerLine;
        @BindView(R.id.docProfileImage)
        CircularImageView docProfileImage;
        @BindView(R.id.thumbnail)
        LinearLayout thumbnail;
        @BindView(R.id.doctorType)
        CustomTextView doctorType;
        @BindView(R.id.doctorName)
        CustomTextView doctorName;
        @BindView(R.id.doctorAddress)
        CustomTextView doctorAddress;
        @BindView(R.id.patientOpdInfoLayout)
        LinearLayout patientOpdInfoLayout;
        @BindView(R.id.parentDataContainer)
        LinearLayout parentDataContainer;
        @BindView(R.id.clickOnDoctorVisitLinearLayout)
        LinearLayout clickOnDoctorVisitLinearLayout;

        View view;

        ListViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }
    }

    public interface OnDayClickListener {
        public void onLongClicked(boolean longpressed);

    }
}