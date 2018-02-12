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

public class DrawerPatientsCityNameAdapter extends RecyclerView.Adapter<DrawerPatientsCityNameAdapter.ListViewHolder> {

    private Context mContext;
    private String[] mClinicList = {"Mumbai", "Pune", "Banglore", "Chennai"};

    public DrawerPatientsCityNameAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public DrawerPatientsCityNameAdapter.ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_item_chechbox_layout, parent, false);

        return new DrawerPatientsCityNameAdapter.ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DrawerPatientsCityNameAdapter.ListViewHolder holder, int position) {

        holder.menuName.setText(mClinicList[position]);

    }

    @Override
    public int getItemCount() {
        return mClinicList.length;
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
