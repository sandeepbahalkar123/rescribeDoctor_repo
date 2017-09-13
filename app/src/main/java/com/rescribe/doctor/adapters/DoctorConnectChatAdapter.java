package com.rescribe.doctor.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.amulyakhare.textdrawable.TextDrawable;
import com.rescribe.doctor.R;
import com.rescribe.doctor.model.doctor_connect_chat.ChatList;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import java.util.ArrayList;
import java.util.Random;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by jeetal on 5/9/17.
 */
public class DoctorConnectChatAdapter extends RecyclerView.Adapter<DoctorConnectChatAdapter.ListViewHolder> {

    private Context mContext;
    private ArrayList<ChatList> chatLists;


    static class ListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.doctorName)
        CustomTextView doctorName;
        @BindView(R.id.doctorType)
        TextView doctorType;
        @BindView(R.id.onlineStatusTextView)
        TextView onlineStatusTextView;
        @BindView(R.id.imageOfDoctor)
        ImageView imageOfDoctor;
        View view;

        ListViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }
    }


    public DoctorConnectChatAdapter(Context mContext, ArrayList<ChatList> chatList) {
        this.chatLists = chatList;
        this.mContext = mContext;

    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.doctor_connect_chats_row_item, parent, false);

        return new ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        ChatList doctorConnectChatModel = chatLists.get(position);

        holder.doctorName.setText(doctorConnectChatModel.getDoctorName());
        holder.doctorType.setText(doctorConnectChatModel.getSpecialization());
        holder.onlineStatusTextView.setText(doctorConnectChatModel.getOnlineStatus());
        if (doctorConnectChatModel.getOnlineStatus().equalsIgnoreCase("Online")) {
            holder.onlineStatusTextView.setTextColor(ContextCompat.getColor(mContext, R.color.green_light));
        } else {
            holder.onlineStatusTextView.setTextColor(ContextCompat.getColor(mContext, R.color.tagColor));
        }


        String s = doctorConnectChatModel.getDoctorName();
        s = s.replace("Dr. ", "");
        char first = s.charAt(0);

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(String.valueOf(first), getRandomColor());
        holder.imageOfDoctor.setImageDrawable(drawable);

    }
    public int getRandomColor() {
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        return color;
    }
    @Override
    public int getItemCount() {
        return chatLists.size();
    }


}