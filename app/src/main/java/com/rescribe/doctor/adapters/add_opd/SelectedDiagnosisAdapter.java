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
import com.rescribe.doctor.model.add_opd.DiagnosisModel;

import java.util.ArrayList;

public class SelectedDiagnosisAdapter extends RecyclerView.Adapter<SelectedDiagnosisAdapter.MyViewHolder> {

    private ArrayList<DiagnosisModel> diagnosisModels;
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

    public SelectedDiagnosisAdapter(ArrayList<DiagnosisModel> diagnosisModels, Context activity, OnClickedListener onCategoryClicked, String opdName) {
        this.diagnosisModels = diagnosisModels;
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

        final DiagnosisModel diagnosisModel = diagnosisModels.get(position);
        holder.textOpdHeaderName.setText(diagnosisModel.getName());
        holder.textOpdOtherInfo.setVisibility(View.GONE);


        holder.removeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onCategoryClicked.onDiagnosisRemoveClicked(diagnosisModel);
            }
        });

    }

    @Override
    public int getItemCount() {
        return diagnosisModels.size();
    }

    public interface OnClickedListener {
        public void onDiagnosisRemoveClicked(DiagnosisModel diagnosisModel);

    }

    public void add(DiagnosisModel diagnosisModel) {
        diagnosisModels.add(diagnosisModel);
        notifyDataSetChanged();
    }

    public void removeItem(DiagnosisModel diagnosisModel) {
        diagnosisModels.remove(diagnosisModel);
        notifyDataSetChanged();
    }
}
