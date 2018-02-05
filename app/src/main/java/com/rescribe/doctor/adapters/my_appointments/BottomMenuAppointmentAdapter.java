package com.rescribe.doctor.adapters.my_appointments;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rescribe.doctor.R;
import com.rescribe.doctor.bottom_menus.BottomMenu;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jeetal on 2/2/18.
 */

public class BottomMenuAppointmentAdapter extends RecyclerView.Adapter<BottomMenuAppointmentAdapter.ListViewHolder> {

    private Context mContext;
    private List<BottomMenu> mBottomMenuList;
    private String[] mMenuNames = {"Select All", "Send SMS", "Send Email", "Waiting List"};

    public BottomMenuAppointmentAdapter(Context mContext) {
        this.mContext = mContext;
        mBottomMenuList = new ArrayList<>();
        for (int index = 0; index < mMenuNames.length; index++) {
            BottomMenu bottomMenu = new BottomMenu();
            bottomMenu.setMenuName(mMenuNames[index]);
            mBottomMenuList.add(bottomMenu);
        }

    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bottom_menu_appointment_item_layout, parent, false);

        return new ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListViewHolder holder, int position) {
        BottomMenu mBottomMenu = mBottomMenuList.get(position);
        //TODO : NEED TO IMPLEMENT
        holder.bottomMenuName.setText(mBottomMenu.getMenuName());
        if(mBottomMenu.getMenuName().equalsIgnoreCase(mContext.getString(R.string.select_all))){
          holder.menuBottomIcon.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.select_all));
        }else if(mBottomMenu.getMenuName().equalsIgnoreCase(mContext.getString(R.string.send_sms))){
            holder.menuBottomIcon.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.send_sms));

        }else if(mBottomMenu.getMenuName().equalsIgnoreCase(mContext.getString(R.string.send_mail))){
            holder.menuBottomIcon.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.send_email));

        }else if(mBottomMenu.getMenuName().equalsIgnoreCase(mContext.getString(R.string.waiting_list))){

            holder.menuBottomIcon.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.add_waiting_list));

        }
        //  holder.timeSlot.setText(doctorObject.ge);


    }

    @Override
    public int getItemCount() {
        return mBottomMenuList.size();
    }

    static class ListViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.spaceView)
        View spaceView;
        @BindView(R.id.menuBottomIcon)
        AppCompatImageView menuBottomIcon;
        @BindView(R.id.showCountTextView)
        TextView showCountTextView;
        @BindView(R.id.bottomMenuName)
        TextView bottomMenuName;
        @BindView(R.id.bottomMenuTab)
        TextView bottomMenuTab;
        @BindView(R.id.menuBottomLayout)
        LinearLayout menuBottomLayout;
        View view;

        ListViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }
    }
}
