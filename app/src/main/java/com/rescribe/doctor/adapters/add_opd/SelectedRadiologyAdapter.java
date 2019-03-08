package com.rescribe.doctor.adapters.add_opd;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rescribe.doctor.R;
import com.rescribe.doctor.model.add_opd.DiagnosisModel;
import com.rescribe.doctor.model.add_opd.RadioDetailModel;

import java.util.ArrayList;

public class SelectedRadiologyAdapter extends RecyclerView.Adapter<SelectedRadiologyAdapter.MyViewHolder> {

    private ArrayList<RadioDetailModel> radioDetailModels;
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

    public SelectedRadiologyAdapter(ArrayList<RadioDetailModel> radioDetailModels, Context activity, OnClickedListener onCategoryClicked, String opdName) {
        this.radioDetailModels = radioDetailModels;
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

        final RadioDetailModel radioDetailModel = radioDetailModels.get(position);
        holder.textOpdHeaderName.setText(radioDetailModel.getName());
        holder.textOpdOtherInfo.setVisibility(View.GONE);


        holder.removeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onCategoryClicked.onRadiologyRemoveClicked(radioDetailModel);
            }
        });

    }

    @Override
    public int getItemCount() {
        return radioDetailModels.size();
    }

    public interface OnClickedListener {
        public void onRadiologyRemoveClicked(RadioDetailModel radioDetailModel);

    }

    public void add(RadioDetailModel radioDetailModel) {
        radioDetailModels.add(radioDetailModel);
        notifyDataSetChanged();
    }

    public void removeItem(RadioDetailModel radioDetailModel) {
        radioDetailModels.remove(radioDetailModel);
        notifyDataSetChanged();
    }
}
