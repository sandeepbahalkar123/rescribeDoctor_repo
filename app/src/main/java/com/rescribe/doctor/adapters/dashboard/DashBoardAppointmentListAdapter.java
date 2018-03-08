package com.rescribe.doctor.adapters.dashboard;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.rescribe.doctor.R;
import com.rescribe.doctor.model.dashboard.AppointmentClinicList;
import com.rescribe.doctor.ui.customesViews.CustomTypefaceSpan;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jeetal on 30/1/18.
 */

public class DashBoardAppointmentListAdapter extends RecyclerView.Adapter<DashBoardAppointmentListAdapter.ListViewHolder> {

    private String[] mDataListHospitalDetails = {"P.D.Hinduja National Hospital, Mumbai", "Pain Clinic, Pune"};
    private String[] timingList = {"From 02:30 pm", "From 08:00 am"};
    private String[] opdList = {"6 OPD, 4 OT", "2 OT"};
    private Context mContext;
   private  ArrayList<AppointmentClinicList> mAppointmentClinicLists = new ArrayList<>();
    public DashBoardAppointmentListAdapter(Context mContext, ArrayList<AppointmentClinicList> appointmentClinicList) {
        this.mContext = mContext;
        this.mAppointmentClinicLists = appointmentClinicList;
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dashboard_menu_item_layout, parent, false);

        return new ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListViewHolder holder, int position) {

        //TODO : NEED TO IMPLEMENT
        final AppointmentClinicList appointmentClinicList = mAppointmentClinicLists.get(position);
        String startTimeToShow = CommonMethods.formatDateTime(appointmentClinicList.getAppointmentStartTime(), RescribeConstants.DATE_PATTERN.hh_mm_a, RescribeConstants.DATE_PATTERN.HH_mm_ss,RescribeConstants.TIME);
        String otCount = appointmentClinicList.getAppointmentOTCount() +" OT";
        String opdCount = appointmentClinicList.getAppointmentOpdCount() +" OPD, ";
        String clinicInfo = appointmentClinicList.getClinicName()+", " +appointmentClinicList.getAreaName()+", "+appointmentClinicList.getCityName();

        Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/roboto_bold.ttf");
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(clinicInfo+ " From "+startTimeToShow.toLowerCase()+ " - " + opdCount +otCount);
        spannableStringBuilder.setSpan(new CustomTypefaceSpan("", font), clinicInfo.length()+6+startTimeToShow.length()+3,spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        holder.textViewName.setText(spannableStringBuilder);


    }

    @Override
    public int getItemCount() {
        return mAppointmentClinicLists.size();
    }

    static class ListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textViewName)
        TextView textViewName;
        @BindView(R.id.adapter_divider_bottom)
        View adapterDividerBottom;
        @BindView(R.id.expandVisitDetailsLayout)
        LinearLayout expandVisitDetailsLayout;
        View view;

        ListViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }
    }
}
