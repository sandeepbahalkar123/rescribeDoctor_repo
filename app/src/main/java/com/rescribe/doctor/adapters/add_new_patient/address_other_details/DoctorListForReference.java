package com.rescribe.doctor.adapters.add_new_patient.address_other_details;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
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
import com.rescribe.doctor.model.patient.add_new_patient.address_other_details.reference_details.DoctorData;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.util.CommonMethods;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DoctorListForReference extends RecyclerView.Adapter<DoctorListForReference.ListViewHolder> implements Filterable {

    private ArrayList<DoctorData> mFilteredList;
    private Context mContext;
    private ArrayList<DoctorData> list;

    private DoctorListForReference.OnItemClicked mOnItemClickedListener;

    public DoctorListForReference(Context mContext, ArrayList<DoctorData> list, DoctorListForReference.OnItemClicked listener) {
        this.mContext = mContext;
        this.list = new ArrayList<>(list);
        this.mFilteredList = new ArrayList<>(list);
        this.mOnItemClickedListener = listener;
    }

    @Override
    public DoctorListForReference.ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_doctor_list, parent, false);

        return new DoctorListForReference.ListViewHolder(itemView);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(final DoctorListForReference.ListViewHolder holder, final int position) {

        DoctorData idAndValueDataModel = mFilteredList.get(position);
        if (idAndValueDataModel.getSpannableSearchedText() != null) {
            if (idAndValueDataModel.getDocName().toLowerCase().contains(idAndValueDataModel.getSpannableSearchedText().toLowerCase())) {
                SpannableString spannablePhoneString = new SpannableString(idAndValueDataModel.getDocName());
                Pattern pattern = Pattern.compile(idAndValueDataModel.getSpannableSearchedText(), Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(idAndValueDataModel.getDocName());
                while (matcher.find()) {
                    spannablePhoneString.setSpan(new ForegroundColorSpan(
                                    ContextCompat.getColor(mContext, R.color.tagColor)),
                            matcher.start(), matcher.end(),//hightlight mSearchString
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                holder.docName.setText(spannablePhoneString);
            } else {
                holder.docName.setText(idAndValueDataModel.getDocName());
            }
        } else {
            holder.docName.setText(idAndValueDataModel.getDocName());
        }

        holder.docEmail.setText(idAndValueDataModel.getDocEmail());
        holder.docPhone.setText(idAndValueDataModel.getDocPhone());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoctorData idAndValueDataModel = mFilteredList.get(position);
                mOnItemClickedListener.onValueClicked(idAndValueDataModel.getId(), idAndValueDataModel);
            }
        });

        TextDrawable textDrawable = CommonMethods.getTextDrawable(mContext, idAndValueDataModel.getDocName());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.dontAnimate();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(true);
        requestOptions.placeholder(textDrawable);
        requestOptions.error(textDrawable);

        Glide.with(mContext)
                .load(idAndValueDataModel.getDocImagePath())
                .apply(requestOptions).thumbnail(0.5f)
                .into(holder.patientImageView);

    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString().toLowerCase();
                mFilteredList.clear();

                if (charString.isEmpty()) {
                    for (DoctorData patientListObject : list) {
                        patientListObject.setSpannableSearchedText("");
                        mFilteredList.add(patientListObject);
                    }
                } else {
                    for (DoctorData patientListObject : list) {
                        if (patientListObject.getDocName().toLowerCase().contains(charString)) {
                            patientListObject.setSpannableSearchedText(charString);
                            mFilteredList.add(patientListObject);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<DoctorData>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface OnItemClicked {
        public void onValueClicked(int id, DoctorData data);
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

}
