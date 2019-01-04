package com.rescribe.doctor.adapters.book_appointment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rescribe.doctor.R;

import java.util.List;

public class ClinicLocactionSpinnerAdapter extends ArrayAdapter<String> {

    LayoutInflater flater;

    public ClinicLocactionSpinnerAdapter(Activity context, int resouceId, int textviewId, List<String> list) {

        super(context, resouceId, textviewId, list);
// flater = context.getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return rowview(convertView, position);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return rowview(convertView, position);
    }

    private View rowview(View convertView, int position) {

        String rowItem = getItem(position);

        viewHolder holder;
        View rowview = convertView;
        if (rowview == null) {

            holder = new viewHolder();
            flater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowview = flater.inflate(R.layout.item_spinner, null, false);
            holder.txtTitle = (TextView) rowview.findViewById(R.id.text);
            rowview.setTag(holder);
        } else {
            holder = (viewHolder) rowview.getTag();
        }

        holder.txtTitle.setText(rowItem);

        return rowview;
    }

    private class viewHolder {
        TextView txtTitle;

    }
}

