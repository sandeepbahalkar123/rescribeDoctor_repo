package com.rescribe.doctor.adapters.add_opd;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rescribe.doctor.R;
import com.rescribe.doctor.model.add_opd.LabDetailModel;

import java.util.ArrayList;


public class SelectedLaboratoryAdapter extends RecyclerView.Adapter<SelectedLaboratoryAdapter.MyViewHolder> {

    private ArrayList<LabDetailModel> labDetailModels;
    Context activity;
    OnCategoryClicked onCategoryClicked;
    String opdName;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textOpdHeaderName, textOpdOtherInfo;
        ImageButton removeItem;

        public MyViewHolder(View view) {
            super(view);
            textOpdHeaderName = view.findViewById(R.id.textOpdHeaderName);
            textOpdOtherInfo = view.findViewById(R.id.textOpdOtherInfo);
            removeItem = view.findViewById(R.id.removeItem);
        }
    }

    public SelectedLaboratoryAdapter(ArrayList<LabDetailModel> labDetailModels, Context activity, OnCategoryClicked onCategoryClicked, String opdName) {
        this.labDetailModels = labDetailModels;
        this.activity = activity;
        this.onCategoryClicked = onCategoryClicked;
        this.opdName = opdName;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selected_opd_header, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final LabDetailModel labDetailModel = labDetailModels.get(position);
        holder.textOpdHeaderName.setText(labDetailModel.getName());


        holder.removeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               onCategoryClicked.onLaboratoryItemRemoveClicked(labDetailModel);
            }
        });

    }

    @Override
    public int getItemCount() {
        return labDetailModels.size();
    }

    public interface OnCategoryClicked {
        public void onLaboratoryItemRemoveClicked(LabDetailModel labDetailModel);
    }

    public void add(LabDetailModel labDetailModel) {
        labDetailModels.add(labDetailModel);
        notifyDataSetChanged();
    }

    public void removeItem(LabDetailModel labDetailModel) {
        labDetailModels.remove(labDetailModel);
        notifyDataSetChanged();
    }
}
