package com.rescribe.doctor.adapters.add_opd;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rescribe.doctor.R;
import com.rescribe.doctor.model.add_opd.ComplaintModel;

import java.util.ArrayList;

public class SelectedComplaintsAdapter extends RecyclerView.Adapter<SelectedComplaintsAdapter.MyViewHolder> {

    private ArrayList<ComplaintModel> complaintModels;
    Context activity;
    OnClickedListener onCategoryClicked;
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

    public SelectedComplaintsAdapter(ArrayList<ComplaintModel> complaintModels, Context activity, OnClickedListener onCategoryClicked, String opdName) {
        this.complaintModels = complaintModels;
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

        final ComplaintModel complaintModel = complaintModels.get(position);
        holder.textOpdHeaderName.setText(complaintModel.getName());
        holder.textOpdOtherInfo.setVisibility(View.VISIBLE);
        String days = "";
        if (complaintModel.getCompDays() != 0) {
            days = complaintModel.getCompDays() + " Days";
        }else if (complaintModel.getCompMonth() != 0) {
            days = complaintModel.getCompMonth() + " Months";
        }
        else if (complaintModel.getCompYear()!=0) {
            days = complaintModel.getCompYear() + " Years";
        }
        else {
            holder.textOpdOtherInfo.setVisibility(View.GONE);
        }

        holder.textOpdOtherInfo.setText("From " + days);

        holder.removeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //removeItem(complaintModel);
                onCategoryClicked.onComplaintRemoveClicked(complaintModel);
            }
        });

    }

    @Override
    public int getItemCount() {
        return complaintModels.size();
    }

    public interface OnClickedListener {
        public void onComplaintRemoveClicked(ComplaintModel complaintModel);

        //public void onComplaintAddClicked(ComplaintModel complaintModel);
    }

    public void add(ComplaintModel complaintModel) {
        complaintModels.add(complaintModel);
        notifyDataSetChanged();
    }

    public void removeItem(ComplaintModel complaintModel) {
        complaintModels.remove(complaintModel);
        notifyDataSetChanged();
    }
}
