package com.rescribe.doctor.adapters.drawer_adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.rescribe.doctor.R;
import com.rescribe.doctor.model.my_appointments.ClinicList;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jeetal on 12/2/18.
 */

public class DrawerAppointmetClinicNameAdapter extends RecyclerView.Adapter<DrawerAppointmetClinicNameAdapter.ListViewHolder> {

    private Context mContext;
    private ArrayList<ClinicList> mClinicList ;
    public DrawerAppointmetClinicNameAdapter(Context mContext, ArrayList<ClinicList> clinicList) {
        this.mContext = mContext;
        this.mClinicList = clinicList;
    }

    @Override
    public DrawerAppointmetClinicNameAdapter.ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_item_chechbox_layout, parent, false);

        return new DrawerAppointmetClinicNameAdapter.ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DrawerAppointmetClinicNameAdapter.ListViewHolder holder, int position) {

        holder.menuName.setText(mClinicList.get(position).getClinicName()+", "+mClinicList.get(position).getCity());

    }

    @Override
    public int getItemCount() {
        return mClinicList.size();
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
