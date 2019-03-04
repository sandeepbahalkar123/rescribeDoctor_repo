package com.rescribe.doctor.adapters.prescription;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rescribe.doctor.R;
import com.rescribe.doctor.model.new_opd.Prescription;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PrescriptionAdapter extends RecyclerView.Adapter<PrescriptionAdapter.MyViewHolder> {

    private List<Prescription> prescriptionsList;

    public PrescriptionAdapter(List<Prescription> prescriptionsList) {
        this.prescriptionsList = prescriptionsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.prescription_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Prescription prescription = prescriptionsList.get(position);
        holder.medicineName.setText(prescription.getName());
    }

    @Override
    public int getItemCount() {
        return prescriptionsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.medicineName)
        TextView medicineName;
        @BindView(R.id.medicineTypeIcon)
        ImageView medicineTypeIcon;
        @BindView(R.id.dosageText)
        TextView dosageText;
        @BindView(R.id.daysText)
        TextView daysText;
        @BindView(R.id.freqScheduleText)
        TextView freqScheduleText;
        @BindView(R.id.remarksText)
        TextView remarksText;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}