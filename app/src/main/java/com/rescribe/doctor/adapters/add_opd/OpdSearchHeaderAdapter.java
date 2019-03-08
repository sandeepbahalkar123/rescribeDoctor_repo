package com.rescribe.doctor.adapters.add_opd;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rescribe.doctor.R;
import com.rescribe.doctor.model.add_opd.OpdSearch;


import java.util.List;


public class OpdSearchHeaderAdapter extends ArrayAdapter<OpdSearch> {
    private List<OpdSearch> opdSearches;
    Context activity;
    OnSearchHeaderClicked onSearchHeaderClicked;

    public class MyViewHolder  {
         TextView textHeaderName;
         LinearLayout layoutItemSearchHeader;

    }
    public OpdSearchHeaderAdapter(List<OpdSearch> opdSearches, Context activity, OnSearchHeaderClicked onSearchHeaderClicked) {
        super(activity, R.layout.item_opd_search_header, opdSearches);
        this.opdSearches = opdSearches;
        this.activity = activity;
        this.onSearchHeaderClicked=onSearchHeaderClicked;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final OpdSearch opdSearch = opdSearches.get(position);
        MyViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new MyViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_opd_search_header, parent, false);
            viewHolder.textHeaderName =  convertView.findViewById(R.id.textHeaderName);
            viewHolder.layoutItemSearchHeader = convertView.findViewById(R.id.layoutItemSearchHeader);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MyViewHolder) convertView.getTag();
        }
        viewHolder.textHeaderName.setText(opdSearch.getName());

        viewHolder.layoutItemSearchHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchHeaderClicked.onHeaderItemClicked(opdSearch);
            }
        });

        return convertView;
    }




   public interface OnSearchHeaderClicked{
        public void onHeaderItemClicked(OpdSearch opdSearch);
     //   public void onHeaderItemRemoveClicked(OpdSearch opdSearch);
    }

   public void add(List<OpdSearch> categories){
       opdSearches.addAll(categories);
       notifyDataSetChanged();
   }
}
