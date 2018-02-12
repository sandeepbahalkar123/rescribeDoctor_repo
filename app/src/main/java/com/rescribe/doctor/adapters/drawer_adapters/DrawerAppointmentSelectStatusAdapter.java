package com.rescribe.doctor.adapters.drawer_adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.rescribe.doctor.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jeetal on 12/2/18.
 */

public class DrawerAppointmentSelectStatusAdapter extends RecyclerView.Adapter<DrawerAppointmentSelectStatusAdapter.ListViewHolder> {

    private Context mContext;
    private String[] mSelectStatusList = {"Follow Up", "Booked", "Confirmed", "Completed", "Cancelled", "Other", "No Show"};

    public DrawerAppointmentSelectStatusAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_item_chechbox_layout, parent, false);

        return new ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListViewHolder holder, int position) {

        holder.menuName.setText(mSelectStatusList[position]);

    }

    @Override
    public int getItemCount() {
        return mSelectStatusList.length;
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
