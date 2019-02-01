package com.rescribe.doctor.adapters.waiting_list;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.rescribe.doctor.R;
import com.rescribe.doctor.model.waiting_list.WaitingclinicList;
import com.rescribe.doctor.ui.customesViews.CustomTextView;

import java.util.ArrayList;

/**
 * Created by jeetal on 23/2/18.
 */

public class WaitingListSpinnerAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<WaitingclinicList> mWaitingclinicLists;

    public WaitingListSpinnerAdapter(Context context, ArrayList<WaitingclinicList> mWaitingclinicLists) {
        this.mContext = context;
        this.mWaitingclinicLists = mWaitingclinicLists;
    }

    @Override
    public int getCount() {
        return mWaitingclinicLists.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.spinner_row_item_layout, parent, false);
        }

        final WaitingclinicList waitingClinicListObject = mWaitingclinicLists.get(position);
        if (waitingClinicListObject != null) {
            CustomTextView clinicNameTextView = (CustomTextView) view.findViewById(R.id.clinicNameTextView);

            String clinicName = waitingClinicListObject.getClinicName() + " - ";
            String address = waitingClinicListObject.getArea() + ", " + waitingClinicListObject.getCity();
            SpannableString textClinicName = new SpannableString(clinicName);
            textClinicName.setSpan(new RelativeSizeSpan(1.2f), 0, textClinicName.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            SpannableString textClinicAddress = new SpannableString(address);
            textClinicAddress.setSpan(new ForegroundColorSpan(Color.GRAY), 0, textClinicAddress.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            clinicNameTextView.setText(TextUtils.concat(textClinicName, "", textClinicAddress));
        }
        return view;
    }

}
