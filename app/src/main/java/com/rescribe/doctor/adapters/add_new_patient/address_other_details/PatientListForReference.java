package com.rescribe.doctor.adapters.add_new_patient.address_other_details;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.rescribe.doctor.R;
import com.rescribe.doctor.model.patient.doctor_patients.PatientList;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.util.CommonMethods;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PatientListForReference extends RecyclerView.Adapter<PatientListForReference.ListViewHolder> implements Filterable {

    private ArrayList<PatientList> mListToShowAfterFilter;
    private Context mContext;
    private ArrayList<PatientList> mDataList;

    private PatientListForReference.OnItemClicked mOnItemClickedListener;

    public PatientListForReference(Context mContext, ArrayList<PatientList> mDataList, PatientListForReference.OnItemClicked listener) {
        this.mContext = mContext;
        this.mDataList = new ArrayList<>(mDataList);
        this.mListToShowAfterFilter = new ArrayList<>(mDataList);
        removeDuplicateElements();
        this.mOnItemClickedListener = listener;

    }

    @Override
    public PatientListForReference.ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_doctor_list, parent, false);

        return new PatientListForReference.ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PatientListForReference.ListViewHolder holder, final int position) {

        PatientList idAndValueDataModel = mListToShowAfterFilter.get(position);
        if (idAndValueDataModel.getSpannableString() != null) {
            if (idAndValueDataModel.getPatientName().toLowerCase().contains(idAndValueDataModel.getSpannableString().toLowerCase())) {
                SpannableString spannablePhoneString = new SpannableString(idAndValueDataModel.getPatientName());
                Pattern pattern = Pattern.compile(idAndValueDataModel.getSpannableString(), Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(idAndValueDataModel.getPatientName());
                while (matcher.find()) {
                    spannablePhoneString.setSpan(new ForegroundColorSpan(
                                    ContextCompat.getColor(mContext, R.color.tagColor)),
                            matcher.start(), matcher.end(),//hightlight mSearchString
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                holder.docName.setText(spannablePhoneString);
            } else {
                holder.docName.setText(idAndValueDataModel.getPatientName());
            }
        } else {
            holder.docName.setText(idAndValueDataModel.getPatientName());
        }

        holder.docEmail.setText(idAndValueDataModel.getPatientEmail());
        holder.docPhone.setText(idAndValueDataModel.getPatientPhone());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PatientList idAndValueDataModel = mListToShowAfterFilter.get(position);
                mOnItemClickedListener.onValueClicked(idAndValueDataModel.getPatientId(), idAndValueDataModel);
            }
        });

        TextDrawable textDrawable = CommonMethods.getTextDrawable(mContext, idAndValueDataModel.getPatientName());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.dontAnimate();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(true);
        requestOptions.placeholder(textDrawable);
        requestOptions.error(textDrawable);

        Glide.with(mContext)
                .load(idAndValueDataModel.getPatientImageUrl())
                .apply(requestOptions).thumbnail(0.5f)
                .into(holder.patientImageView);

    }

    @Override
    public int getItemCount() {
        return mListToShowAfterFilter.size();
    }


    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString().toLowerCase();
                mDataList.clear();

                if (charString.isEmpty()) {
                    for (PatientList patList : mListToShowAfterFilter) {
                        patList.setSpannableString("");
                        mDataList.add(patList);
                    }
                } else {
                    for (PatientList patientListObject : mListToShowAfterFilter) {
                        if (patientListObject.getPatientName().toLowerCase().contains(charString)
                                || patientListObject.getPatientPhone().contains(charString)
                                || String.valueOf(patientListObject.getPatientId()).contains(charString)) {

                            patientListObject.setSpannableString(charString);
                            mDataList.add(patientListObject);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mDataList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mDataList = (ArrayList<PatientList>) filterResults.values;

                notifyDataSetChanged();
            }
        };
    }

    static class ListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.docName)
        CustomTextView docName;
        @BindView(R.id.docEmail)
        CustomTextView docEmail;
        @BindView(R.id.docPhone)
        CustomTextView docPhone;
        @BindView(R.id.patientImageView)
        ImageView patientImageView;
        @BindView(R.id.cardView)
        LinearLayout cardView;
        View view;

        ListViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }
    }

    public interface OnItemClicked {
        public void onValueClicked(int id, PatientList data);
    }


    public void add(PatientList mc) {
        mDataList.add(mc);
        mListToShowAfterFilter.add(mc);
        //removeDuplicateElements();
        notifyItemInserted(mDataList.size() - 1);
    }


    // this is added to remove duplicate patients from mDataList based on patientID.
    private void removeDuplicateElements() {
        Map<Integer, PatientList> map = new LinkedHashMap<>();
        for (PatientList ays : mDataList) {
            map.put(ays.getHospitalPatId(), ays);
        }

        mDataList.clear();
        mDataList.addAll(map.values());
        mListToShowAfterFilter.clear();
        mListToShowAfterFilter.addAll(map.values());
    }

    public ArrayList<PatientList> getGroupList() {
        return mDataList;
    }


    public void addAll(ArrayList<PatientList> mcList, String searchText) {

        for (PatientList mc : mcList) {
            mc.setSpannableString(searchText);

            add(mc);
            // add patient in sqlite while pagination.

            if (mc.isOfflinePatientSynced()) {
                mc.setOfflinePatientSynced(true);
            } else {
                mc.setOfflinePatientSynced(false);
            }

        }

        notifyDataSetChanged();
    }

    public void clear() {
        mDataList.clear();
    }

}
