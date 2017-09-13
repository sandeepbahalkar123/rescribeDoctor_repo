package com.rescribe.doctor.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.rescribe.doctor.R;
import com.rescribe.doctor.model.parceable_doctor_connect.ConnectList;
import com.rescribe.doctor.ui.customesViews.CustomTextView;

import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by jeetal on 6/9/17.
 */

public class DoctorConnectAdapter extends RecyclerView.Adapter<DoctorConnectAdapter.ListViewHolder> {

    private Context mContext;
    private ArrayList<ConnectList> appointmentsList;
    private ArrayList<ConnectList> mArrayList;
    String searchString = "";

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

        View view;

        ListViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }
    }


    public DoctorConnectAdapter(Context mContext, ArrayList<ConnectList> appointmentsList) {
        this.appointmentsList = appointmentsList;
        mArrayList = appointmentsList;
        this.mContext = mContext;

    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.global_connect_chats_row_item, parent, false);

        return new ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        ConnectList doctorConnectChatModel = appointmentsList.get(position);

        // holder.doctorName.setText(doctorConnectChatModel.getDoctorName());
        holder.doctorType.setText(doctorConnectChatModel.getSpecialization());
        if (doctorConnectChatModel.getOnlineStatus().equalsIgnoreCase("Online")) {
            holder.onlineStatusTextView.setTextColor(ContextCompat.getColor(mContext, R.color.green_light));
        } else {
            holder.onlineStatusTextView.setTextColor(ContextCompat.getColor(mContext, R.color.tagColor));
        }
        holder.onlineStatusTextView.setText(doctorConnectChatModel.getOnlineStatus());
        holder.paidStatusTextView.setText(doctorConnectChatModel.getPaidStatus());
        String s = doctorConnectChatModel.getDoctorName();
        s = s.replace("Dr. ", "");
        char first = s.charAt(0);

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(String.valueOf(first), getRandomColor());
        holder.imageOfDoctor.setImageDrawable(drawable);

        SpannableString spannableStringSearch = null;

        if ((searchString != null) && (!searchString.isEmpty())) {
            spannableStringSearch = new SpannableString(doctorConnectChatModel.getDoctorName());
            Pattern pattern = Pattern.compile(searchString, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(doctorConnectChatModel.getDoctorName());
            while (matcher.find()) {
                spannableStringSearch.setSpan(new ForegroundColorSpan(
                                ContextCompat.getColor(mContext, R.color.tagColor)),
                        matcher.start(), matcher.end(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        if (spannableStringSearch != null) {
            holder.doctorName.setText(spannableStringSearch);
        } else {
            holder.doctorName.setText(doctorConnectChatModel.getDoctorName());
        }

    }

    @Override
    public int getItemCount() {
        return appointmentsList.size();
    }


    public int getRandomColor() {
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        return color;
    }

}

