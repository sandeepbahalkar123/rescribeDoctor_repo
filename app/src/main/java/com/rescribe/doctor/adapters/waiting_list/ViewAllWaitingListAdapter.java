package com.rescribe.doctor.adapters.waiting_list;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.rescribe.doctor.R;
import com.rescribe.doctor.model.waiting_list.ViewAll;
import com.rescribe.doctor.ui.customesViews.CircularImageView;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.ui.customesViews.drag_drop_recyclerview_helper.ItemTouchHelperAdapter;
import com.rescribe.doctor.ui.customesViews.drag_drop_recyclerview_helper.OnStartDragListener;
import com.rescribe.doctor.ui.customesViews.swipeable_recyclerview.SwipeRevealLayout;
import com.rescribe.doctor.ui.customesViews.swipeable_recyclerview.ViewBinderHelper;
import com.rescribe.doctor.ui.fragments.waiting_list.ViewAllPatientListFragment;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jeetal on 23/2/18.
 */

public class ViewAllWaitingListAdapter extends RecyclerView.Adapter implements ItemTouchHelperAdapter {

    private ArrayList<ViewAll> mActiveArrayList = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context mContext;
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();
    private String appointmentScheduleTime = "";
    private String waitingTime = "";

    private final OnStartDragListener mDragStartListener;

    public ViewAllWaitingListAdapter(Context context, ArrayList<ViewAll> mActivesList, OnStartDragListener mDragStartListener) {
        mContext = context;
        mActiveArrayList = mActivesList;
        this.mDragStartListener = mDragStartListener;
        mInflater = LayoutInflater.from(context);

        // uncomment if you want to open only one row at a time
        // binderHelper.setOpenOnlyOne(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.waiting_list_row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
        final ViewHolder holder = (ViewHolder) h;

        final ViewAll mActiveObject = mActiveArrayList.get(position);

        // Use ViewBindHelper to restore and save the open/close state of the SwipeRevealView
        // put an unique string id as value, can be any string which uniquely define the data
        binderHelper.bindViewAll(holder.swipeLayout, mActiveObject);

        holder.view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
        // Add Move touch event.

        // Bind your data here
        holder.bind(mActiveObject);

    }

    @Override
    public void onItemDismiss(int position) {
        mActiveArrayList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mActiveArrayList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public int getItemCount() {
        if (mActiveArrayList == null)
            return 0;
        return mActiveArrayList.size();
    }

    /**
     * Only if you need to restore open/close state when the orientation is changed.
     * Call this method in {@link Activity#onSaveInstanceState(Bundle)}
     */
    public void saveStates(Bundle outState) {
        binderHelper.saveStates(outState);
    }

    /**
     * Only if you need to restore open/close state when the orientation is changed.
     * Call this method in {@link Activity#onRestoreInstanceState(Bundle)}
     */
    public void restoreStates(Bundle inState) {
        binderHelper.restoreStates(inState);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.delete_layout)
        FrameLayout deleteLayout;
        @BindView(R.id.bluelineImageView)
        ImageView bluelineImageView;
        @BindView(R.id.patientIdTextView)
        CustomTextView patientIdTextView;
        @BindView(R.id.appointmentTime)
        CustomTextView appointmentTime;
        @BindView(R.id.patientImageView)
        CircularImageView patientImageView;
        @BindView(R.id.patientNameTextView)
        CustomTextView patientNameTextView;
        @BindView(R.id.statusTextView)
        CustomTextView statusTextView;
        @BindView(R.id.typeStatus)
        CustomTextView typeStatus;
        @BindView(R.id.patientDetailsLinearLayout)
        LinearLayout patientDetailsLinearLayout;
        @BindView(R.id.appointmentLabelTextView)
        CustomTextView appointmentLabelTextView;
        @BindView(R.id.appointmentTimeTextView)
        CustomTextView appointmentTimeTextView;
        @BindView(R.id.appointmentDetailsLinearLayout)
        LinearLayout appointmentDetailsLinearLayout;
        @BindView(R.id.patientPhoneNumber)
        CustomTextView patientPhoneNumber;
        @BindView(R.id.separatorView)
        View separatorView;
        @BindView(R.id.tokenLabelTextView)
        CustomTextView tokenLabelTextView;
        @BindView(R.id.tokenNumber)
        CustomTextView tokenNumber;
        @BindView(R.id.cardView)
        LinearLayout cardView;
        @BindView(R.id.front_layout)
        FrameLayout frontLayout;
        @BindView(R.id.swipe_layout)
        SwipeRevealLayout swipeLayout;
        View view;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }

        public void bind(final ViewAll viewAll) {
            deleteLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActiveArrayList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                }
            });

            patientIdTextView.setText(mContext.getString(R.string.id) + " " + viewAll.getPatientId());
            if (!viewAll.getWaitingInTime().equals("")) {
                appointmentTime.setVisibility(View.VISIBLE);
                waitingTime = CommonMethods.formatDateTime(viewAll.getWaitingInTime(), RescribeConstants.DATE_PATTERN.hh_mm_a, RescribeConstants.DATE_PATTERN.HH_mm_ss, RescribeConstants.TIME).toLowerCase();
                appointmentTime.setText(mContext.getString(R.string.in_time) + " - " + waitingTime);

            } else {
                appointmentTime.setVisibility(View.INVISIBLE);

            }
            if (!viewAll.getAppointmentTime().equals("")) {
                appointmentTimeTextView.setVisibility(View.VISIBLE);
                appointmentLabelTextView.setVisibility(View.VISIBLE);
                appointmentScheduleTime = CommonMethods.formatDateTime(viewAll.getAppointmentTime(), RescribeConstants.DATE_PATTERN.hh_mm_a, RescribeConstants.DATE_PATTERN.HH_mm_ss, RescribeConstants.TIME).toLowerCase();
                appointmentTimeTextView.setText(appointmentScheduleTime);

            } else {
                appointmentTimeTextView.setVisibility(View.INVISIBLE);
                appointmentLabelTextView.setVisibility(View.INVISIBLE);

            }
            patientPhoneNumber.setText(viewAll.getPatientPhone());
            tokenNumber.setText(viewAll.getTokenNumber());
            patientNameTextView.setText(viewAll.getPatientName());
            typeStatus.setText(" " + viewAll.getWaitingStatus());
            TextDrawable textDrawable = CommonMethods.getTextDrawable(mContext, viewAll.getPatientName());
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.dontAnimate();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
            requestOptions.skipMemoryCache(true);
            requestOptions.placeholder(textDrawable);
            requestOptions.error(textDrawable);

            Glide.with(mContext)
                    .load(viewAll.getPatientImageUrl())
                    .apply(requestOptions).thumbnail(0.5f)
                    .into(patientImageView);

            frontLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String displayText = "" + viewAll.getPatientName() + " clicked";
                    Toast.makeText(mContext, displayText, Toast.LENGTH_SHORT).show();
                    Log.d("ViewAllWaitingList", displayText);
                }
            });
        }
    }
}
