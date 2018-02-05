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
import com.rescribe.doctor.ui.customesViews.CustomTypefaceSpan;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jeetal on 30/1/18.
 */

public class WaitingOrAppointmentListAdapter extends RecyclerView.Adapter<WaitingOrAppointmentListAdapter.ListViewHolder> {


    private Context mContext;
    private String[] mDataListHospitalDetails = {"P.D.Hinduja National Hospital, Mumbai", "Pain Clinic, Pune"};
    private String[] timingList = {"From 02:30 pm", "From 08:00 am"};
    private String[] opdList = {"6 OPD, 4 OT", "2 OT"};

    public WaitingOrAppointmentListAdapter(Context mContext) {
        this.mContext = mContext;
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

        Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/roboto_bold.ttf");
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(mDataListHospitalDetails[position] + " - " + timingList[position] + " - " + opdList[position]);
        spannableStringBuilder.setSpan(new CustomTypefaceSpan("", font), mDataListHospitalDetails[position].length()+3+timingList[position].length()+3,spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        holder.textViewName.setText(spannableStringBuilder);


    }

    @Override
    public int getItemCount() {
        return mDataListHospitalDetails.length;
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
