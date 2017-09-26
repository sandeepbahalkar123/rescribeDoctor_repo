package com.rescribe.doctor.adapters.patient_connect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.rescribe.doctor.R;
import com.rescribe.doctor.helpers.database.AppDBHelper;
import com.rescribe.doctor.model.patient_connect.PatientData;
import com.rescribe.doctor.ui.activities.ChatActivity;
import com.rescribe.doctor.ui.activities.PatientConnectActivity;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.ui.customesViews.EditTextWithDeleteButton;
import com.rescribe.doctor.ui.fragments.patient_connect.PatientSearchFragment;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by jeetal on 6/9/17.
 */

public class PatientConnectAdapter extends RecyclerView.Adapter<PatientConnectAdapter.ListViewHolder> implements Filterable {

    private final ArrayList<PatientData> mArrayList;
    private final ColorGenerator mColorGenerator;
    private Fragment mCalledParentFragment;
    private String mIdle, mOnline, mOffline;
    private Context mContext;
    private ArrayList<PatientData> dataList;
    String searchString = "";

    private final AppDBHelper appDBHelper;

    static class ListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.doctorName)
        CustomTextView doctorName;
        @BindView(R.id.doctorType)
        TextView doctorType;
        @BindView(R.id.onlineStatusTextView)
        TextView onlineStatusTextView;
        @BindView(R.id.paidStatusTextView)
        TextView paidStatusTextView;
        @BindView(R.id.imageOfDoctor)
        ImageView imageOfDoctor;
        @BindView(R.id.badgeView)
        TextView badgeView;

        View view;

        ListViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }
    }

    public PatientConnectAdapter(Context mContext, ArrayList<PatientData> appointmentsList, Fragment parentFragment) {

        appDBHelper = new AppDBHelper(mContext);

        this.dataList = appointmentsList;
        this.mCalledParentFragment = parentFragment;
        mArrayList = appointmentsList;

        mOnline = mContext.getString(R.string.online);
        mOffline = mContext.getString(R.string.offline);
        mIdle = mContext.getString(R.string.idle);
        this.mContext = mContext;
        //--------------
        mColorGenerator = ColorGenerator.MATERIAL;
        //-----------------
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.global_connect_chats_row_item, parent, false);

        return new ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListViewHolder holder, int position) {
        final PatientData doctorConnectChatModel = dataList.get(position);

        //-----------
        if (doctorConnectChatModel.getOnlineStatus().equalsIgnoreCase(mOnline)) {
            holder.onlineStatusTextView.setTextColor(ContextCompat.getColor(mContext, R.color.green_light));
        } else if (doctorConnectChatModel.getOnlineStatus().equalsIgnoreCase(mIdle)) {
            holder.onlineStatusTextView.setTextColor(ContextCompat.getColor(mContext, R.color.range_yellow));
        } else if (doctorConnectChatModel.getOnlineStatus().equalsIgnoreCase(mOffline)) {
            holder.onlineStatusTextView.setTextColor(ContextCompat.getColor(mContext, R.color.grey_500));
        } else {
            holder.onlineStatusTextView.setTextColor(ContextCompat.getColor(mContext, R.color.tagColor));
        }
        //-----------

        holder.onlineStatusTextView.setText(doctorConnectChatModel.getOnlineStatus());
        holder.paidStatusTextView.setVisibility(View.GONE);
        holder.doctorType.setVisibility(View.GONE);

        //---------
        String patientName = doctorConnectChatModel.getPatientName();
        patientName = patientName.replace("Dr. ", "");
        if (patientName != null && patientName.length() > 0) {
            int color2 = mColorGenerator.getColor(patientName);
            TextDrawable drawable = TextDrawable.builder()
                    .beginConfig()
                    .width(Math.round(mContext.getResources().getDimension(R.dimen.dp40)))  // width in px
                    .height(Math.round(mContext.getResources().getDimension(R.dimen.dp40))) // height in px
                    .endConfig()
                    .buildRound(("" + patientName.charAt(0)).toUpperCase(), color2);
            holder.imageOfDoctor.setImageDrawable(drawable);
        }
        //---------
        SpannableString spannableStringSearch = null;

        if ((searchString != null) && (!searchString.isEmpty())) {
            spannableStringSearch = new SpannableString(doctorConnectChatModel.getPatientName());
            spannableStringSearch.setSpan(new ForegroundColorSpan(
                            ContextCompat.getColor(mContext, R.color.tagColor)),
                    0, searchString.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        if (spannableStringSearch != null) {
            holder.doctorName.setText(spannableStringSearch);
        } else {
            holder.doctorName.setText(doctorConnectChatModel.getPatientName());
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra(RescribeConstants.PATIENT_INFO, doctorConnectChatModel);
                intent.putExtra(RescribeConstants.STATUS_COLOR, holder.onlineStatusTextView.getCurrentTextColor());
                mContext.startActivity(intent);
            }
        });

        holder.paidStatusTextView.setVisibility(View.GONE);

        int count = appDBHelper.unreadMessageCountById(doctorConnectChatModel.getId());
        doctorConnectChatModel.setUnreadMessages(count);

        if (doctorConnectChatModel.getUnreadMessages() > 0) {
            holder.badgeView.setVisibility(View.VISIBLE);
            holder.badgeView.setText(String.valueOf(doctorConnectChatModel.getUnreadMessages()));
        } else {
            holder.badgeView.setVisibility(View.GONE);
        }

        //---------

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();
                searchString = charString;
                if (charString.isEmpty()) {

                    dataList = mArrayList;
                } else {

                    ArrayList<PatientData> filteredList = new ArrayList<>();

                    for (PatientData doctorConnectModel : mArrayList) {

                        if (doctorConnectModel.getPatientName().toLowerCase().startsWith(charString.toLowerCase())) {

                            filteredList.add(doctorConnectModel);
                        }
                    }

                    dataList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = dataList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                dataList = (ArrayList<PatientData>) filterResults.values;
                if (dataList.size() == 0) {
                    PatientSearchFragment temp = (PatientSearchFragment) mCalledParentFragment;
                    temp.isDataListViewVisible(false);
                } else {
                    PatientSearchFragment temp = (PatientSearchFragment) mCalledParentFragment;
                    temp.isDataListViewVisible(true);
                }
                notifyDataSetChanged();
            }
        };
    }

}

