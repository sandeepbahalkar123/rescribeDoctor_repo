package com.rescribe.doctor.adapters.add_new_patient.address_other_details;

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

import com.rescribe.doctor.R;
import com.rescribe.doctor.ui.activities.my_patients.add_new_patient.IdAndValueDataModel;
import com.rescribe.doctor.ui.customesViews.CustomTextView;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IdAndValueDataAdapter extends RecyclerView.Adapter<IdAndValueDataAdapter.ListViewHolder> implements Filterable {

    private ArrayList<IdAndValueDataModel> mFilteredList;
    private Context mContext;
    private ArrayList<IdAndValueDataModel> list;

    private IdAndValueDataAdapter.OnItemClicked mOnItemClickedListener;

    public IdAndValueDataAdapter(Context mContext, ArrayList<IdAndValueDataModel> list, IdAndValueDataAdapter.OnItemClicked listener) {
        this.mContext = mContext;
        this.list = new ArrayList<>(list);
        this.mFilteredList = new ArrayList<>(list);
        this.mOnItemClickedListener = listener;
    }

    @Override
    public IdAndValueDataAdapter.ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_textview, parent, false);

        return new IdAndValueDataAdapter.ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final IdAndValueDataAdapter.ListViewHolder holder, final int position) {

        IdAndValueDataModel idAndValueDataModel = mFilteredList.get(position);
        if (idAndValueDataModel.getSpannableSearchedText() != null) {
            if (idAndValueDataModel.getIdValue().toLowerCase().contains(idAndValueDataModel.getSpannableSearchedText().toLowerCase())) {
                SpannableString spannablePhoneString = new SpannableString(idAndValueDataModel.getIdValue());
                Pattern pattern = Pattern.compile(idAndValueDataModel.getSpannableSearchedText(), Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(idAndValueDataModel.getIdValue());
                while (matcher.find()) {
                    spannablePhoneString.setSpan(new ForegroundColorSpan(
                                    ContextCompat.getColor(mContext, R.color.tagColor)),
                            matcher.start(), matcher.end(),//hightlight mSearchString
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                holder.text.setText(spannablePhoneString);
            } else {
                holder.text.setText(idAndValueDataModel.getIdValue());
            }
        } else {
            holder.text.setText(idAndValueDataModel.getIdValue());
        }

        holder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IdAndValueDataModel idAndValueDataModel = mFilteredList.get(position);
                mOnItemClickedListener.onValueClicked(idAndValueDataModel.getId(), idAndValueDataModel.getIdValue());
            }
        });

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
                    mFilteredList.addAll(list);
                } else {
                    for (IdAndValueDataModel patientListObject : list) {
                        if (patientListObject.getIdValue().toLowerCase().contains(charString)) {
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
                mFilteredList = (ArrayList<IdAndValueDataModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    static class ListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text)
        CustomTextView text;
        View view;

        ListViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }
    }

    public interface OnItemClicked {
        public void onValueClicked(int id, String value);
    }

}
