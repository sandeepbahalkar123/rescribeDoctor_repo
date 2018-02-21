package com.rescribe.doctor.adapters.drawer_adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.rescribe.doctor.R;
import com.rescribe.doctor.model.my_appointments.StatusList;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jeetal on 12/2/18.
 */

public class DrawerAppointmentSelectStatusAdapter extends RecyclerView.Adapter<DrawerAppointmentSelectStatusAdapter.ListViewHolder> {

    private Context mContext;
    private ArrayList<StatusList> mStatusLists;
    public DrawerAppointmentSelectStatusAdapter(Context mContext, ArrayList<StatusList> statusList) {
        this.mContext = mContext;
        this.mStatusLists = statusList;
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_item_chechbox_layout, parent, false);

        return new ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListViewHolder holder, int position) {

        holder.menuName.setText(mStatusLists.get(position).getStatusName());

    }

    @Override
    public int getItemCount() {
        return mStatusLists.size();
    }

    static class ListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.menuName)
        CheckBox menuName;
        View view;

        ListViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }
    }
}
