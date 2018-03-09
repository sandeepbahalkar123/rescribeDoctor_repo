package com.rescribe.doctor.ui.fragments.waiting_list;

import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.waiting_list.DraggableSwipeableWaitingListAdapter;
import com.rescribe.doctor.adapters.waiting_list.WaitingListSpinnerAdapter;
import com.rescribe.doctor.helpers.myappointments.AppointmentHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.patient.template_sms.TemplateBaseModel;
import com.rescribe.doctor.model.waiting_list.AbstractDataProvider;
import com.rescribe.doctor.model.waiting_list.Active;
import com.rescribe.doctor.model.waiting_list.PatientDataProvider;
import com.rescribe.doctor.model.waiting_list.ViewAll;
import com.rescribe.doctor.model.waiting_list.WaitingPatientList;
import com.rescribe.doctor.model.waiting_list.WaitingclinicList;
import com.rescribe.doctor.model.waiting_list.request_delete_waiting_list.RequestDeleteBaseModel;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.ui.customesViews.CircularImageView;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.ui.customesViews.drag_drop_recyclerview_helper.OnStartDragListener;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by jeetal on 22/2/18.
 */

public class ViewAllPatientListFragment extends Fragment implements OnStartDragListener, HelperResponse {

    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewDragDropManager recyclerViewDragDropManager;
    private RecyclerViewSwipeManager recyclerViewSwipeManager;
    private RecyclerViewTouchActionGuardManager recyclerViewTouchActionGuardManager;

    @BindView(R.id.clinicListSpinner)
    Spinner clinicListSpinner;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.bulletImageView)
    CircularImageView bulletImageView;
    @BindView(R.id.clinicNameTextView)
    CustomTextView clinicNameTextView;
    @BindView(R.id.clinicAddress)
    CustomTextView clinicAddress;
    @BindView(R.id.hospitalDetailsLinearLayout)
    RelativeLayout hospitalDetailsLinearLayout;
    private Unbinder unbinder;
    private static Bundle args;
    private ArrayList<WaitingclinicList> waitingclinicLists = new ArrayList<>();
    private WaitingListSpinnerAdapter mWaitingListSpinnerAdapter;
//    private ViewAllWaitingListAdapter mViewAllWaitingListAdapter;
    private ArrayList<ViewAll> viewAllArrayList;
    private int adapterPos;
    private AppointmentHelper mAppointmentHelper;
    private int mLocationId;

    public ViewAllPatientListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.waiting_content_layout, container, false);
        unbinder = ButterKnife.bind(this, mRootView);
        init();
        return mRootView;
    }

    private void init() {
        mAppointmentHelper = new AppointmentHelper(getActivity(), this);
        waitingclinicLists = args.getParcelableArrayList(RescribeConstants.WAITING_LIST_INFO);
        if (waitingclinicLists.size() > 1) {
            clinicListSpinner.setVisibility(View.VISIBLE);
            hospitalDetailsLinearLayout.setVisibility(View.GONE);
            mWaitingListSpinnerAdapter = new WaitingListSpinnerAdapter(getActivity(), waitingclinicLists);
            clinicListSpinner.setAdapter(mWaitingListSpinnerAdapter);

        } else {
            mLocationId = waitingclinicLists.get(0).getLocationId();
            clinicListSpinner.setVisibility(View.GONE);
            hospitalDetailsLinearLayout.setVisibility(View.VISIBLE);
            clinicNameTextView.setText(waitingclinicLists.get(0).getClinicName() + " - ");
            clinicAddress.setText(waitingclinicLists.get(0).getArea() + ", " + waitingclinicLists.get(0).getCity());
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setClipToPadding(false);
            /*WaitingPatientList waitingPatientSingleList = waitingclinicLists.get(0).getWaitingPatientList();
            mViewAllWaitingListAdapter = new ViewAllWaitingListAdapter(getActivity(), waitingPatientSingleList.getViewAll(),this);
            LinearLayoutManager linearlayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(linearlayoutManager);
            // off recyclerView Animation
            RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
            if (animator instanceof SimpleItemAnimator)
                ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);

            recyclerView.setAdapter(mViewAllWaitingListAdapter);*/

            setAdapter();

        }
        clinicListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()

    {
        @Override
        public void onItemSelected (AdapterView < ? > adapterView, View view,int i, long l){
        mLocationId = waitingclinicLists.get(i).getLocationId();
        WaitingPatientList waitingPatientTempList = new WaitingPatientList();
        for (WaitingclinicList waitingclinicList : waitingclinicLists) {
            if (mLocationId == waitingclinicList.getLocationId()) {
                waitingPatientTempList = waitingclinicList.getWaitingPatientList();
            }
        }
        if (waitingPatientTempList != null) {

            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setClipToPadding(false);
           /* mViewAllWaitingListAdapter = new ViewAllWaitingListAdapter(getActivity(), waitingPatientTempList.getViewAll(), ViewAllPatientListFragment.this);
            LinearLayoutManager linearlayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(linearlayoutManager);
            // off recyclerView Animation
            RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
            if (animator instanceof SimpleItemAnimator)
                ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
            recyclerView.setAdapter(mViewAllWaitingListAdapter);*/

           setAdapter();
        }


    }

        @Override
        public void onNothingSelected (AdapterView < ? > adapterView){

    }
    });
}


    private void setAdapter() {
        // New

        mLayoutManager = new LinearLayoutManager(getContext());

        // touch guard manager  (this class is required to suppress scrolling while swipe-dismiss animation is running)
        recyclerViewTouchActionGuardManager = new RecyclerViewTouchActionGuardManager();
        recyclerViewTouchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true);
        recyclerViewTouchActionGuardManager.setEnabled(true);

        // drag & drop manager
        recyclerViewDragDropManager = new RecyclerViewDragDropManager();
        recyclerViewDragDropManager.setDraggingItemShadowDrawable(
                (NinePatchDrawable) ContextCompat.getDrawable(getContext(), R.drawable.material_shadow_z3));

        // swipe manager
        recyclerViewSwipeManager = new RecyclerViewSwipeManager();

        //adapter
        final DraggableSwipeableWaitingListAdapter myItemAdapter = new DraggableSwipeableWaitingListAdapter(getDataProvider());
        myItemAdapter.setEventListener(new DraggableSwipeableWaitingListAdapter.EventListener() {

            @Override
            public void onDeleteClick(int position) {
                CommonMethods.showToast(getContext(), "Position " + position);
            }

            @Override
            public void onItemPinned(int position) {
                CommonMethods.showToast(getContext(), "Pinned " + position);
            }

            @Override
            public void onItemViewClicked(View v, boolean pinned) {
                onItemViewClick(v, pinned);
            }

            @Override
            public void onItemMoved(int fromPosition, int toPosition) {

            }
        });

        mAdapter = myItemAdapter;

        mWrappedAdapter = recyclerViewDragDropManager.createWrappedAdapter(myItemAdapter);      // wrap for dragging
        mWrappedAdapter = recyclerViewSwipeManager.createWrappedAdapter(mWrappedAdapter);      // wrap for swiping

        final GeneralItemAnimator animator = new DraggableItemAnimator();

        // Change animations are enabled by default since support-v7-recyclerview v22.
        // Disable the change animation in order to make turning back animation of swiped item works properly.
        animator.setSupportsChangeAnimations(false);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter
        recyclerView.setItemAnimator(animator);

        // additional decorations
        //noinspection StatementWithEmptyBody
        if (supportsViewElevation()) {
            // Lollipop or later has native drop shadow feature. ItemShadowDecorator is not required.
        } else {
            recyclerView.addItemDecoration(new ItemShadowDecorator((NinePatchDrawable) ContextCompat.getDrawable(getContext(), R.drawable.material_shadow_z3)));
        }
        recyclerView.addItemDecoration(new SimpleListDividerDecorator(ContextCompat.getDrawable(getContext(), R.drawable.list_divider_h), true));

        // NOTE:
        // The initialization order is very important! This order determines the priority of touch event handling.
        //
        // priority: TouchActionGuard > Swipe > DragAndDrop
        recyclerViewTouchActionGuardManager.attachRecyclerView(recyclerView);
        recyclerViewSwipeManager.attachRecyclerView(recyclerView);
        recyclerViewDragDropManager.attachRecyclerView(recyclerView);

        // for debugging
//        animator.setDebug(true);
//        animator.setMoveDuration(2000);
//        animator.setRemoveDuration(2000);
//        recyclerViewSwipeManager.setMoveToOutsideWindowAnimationDuration(2000);
//        recyclerViewSwipeManager.setReturnToDefaultPositionAnimationDuration(2000);
    }

    public static ViewAllPatientListFragment newInstance(Bundle bundle) {
        ViewAllPatientListFragment fragment = new ViewAllPatientListFragment();
        args = new Bundle();
        args = bundle;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {

    }

    @Override
    public void onDeleteViewAllLayoutClicked(int adapterPosition, ViewAll viewAll) {
        adapterPos = adapterPosition;
        RequestDeleteBaseModel requestDeleteBaseModel = new RequestDeleteBaseModel();
        requestDeleteBaseModel.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, getActivity())));
        requestDeleteBaseModel.setLocationId(mLocationId);
        requestDeleteBaseModel.setWaitingDate(CommonMethods.getFormattedDate(CommonMethods.getCurrentDate(), RescribeConstants.DATE_PATTERN.DD_MM_YYYY, RescribeConstants.DATE_PATTERN.YYYY_MM_DD));
        requestDeleteBaseModel.setWaitingId(viewAll.getWaitingId());
        requestDeleteBaseModel.setWaitingSequence(viewAll.getWaitingSequence());
        mAppointmentHelper.doDeleteWaitingList(requestDeleteBaseModel);

    }

    @Override
    public void onDeleteActiveLayoutClicked(int adapterPosition, Active active) {

    }


    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        if (mOldDataTag.equals(RescribeConstants.TASK_DELETE_WAITING_LIST)) {
            TemplateBaseModel templateBaseModel = (TemplateBaseModel) customResponse;
            /*if (templateBaseModel.getCommon().isSuccess()) {
                Toast.makeText(getActivity(), templateBaseModel.getCommon().getStatusMessage() + "", Toast.LENGTH_SHORT).show();
                viewAllArrayList = mViewAllWaitingListAdapter.getAdapterList();
                viewAllArrayList.remove(adapterPos);
                mViewAllWaitingListAdapter.notifyItemRemoved(adapterPos);
            } else {
                Toast.makeText(getActivity(), templateBaseModel.getCommon().getStatusMessage() + "", Toast.LENGTH_SHORT).show();

            }*/
        }
    }

    @Override
    public void onParseError(String mOldDataTag, String errorMessage) {

    }

    @Override
    public void onServerError(String mOldDataTag, String serverErrorMessage) {

    }

    @Override
    public void onNoConnectionError(String mOldDataTag, String serverErrorMessage) {

    }

    @Override
    public void onPause() {
        recyclerViewDragDropManager.cancelDrag();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (recyclerViewDragDropManager != null) {
            recyclerViewDragDropManager.release();
            recyclerViewDragDropManager = null;
        }

        if (recyclerViewSwipeManager != null) {
            recyclerViewSwipeManager.release();
            recyclerViewSwipeManager = null;
        }

        if (recyclerViewTouchActionGuardManager != null) {
            recyclerViewTouchActionGuardManager.release();
            recyclerViewTouchActionGuardManager = null;
        }

        if (recyclerView != null) {
            recyclerView.setItemAnimator(null);
            recyclerView.setAdapter(null);
            recyclerView = null;
        }

        if (mWrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(mWrappedAdapter);
            mWrappedAdapter = null;
        }
        mAdapter = null;
        mLayoutManager = null;

        super.onDestroyView();
    }

    private void onItemViewClick(View v, boolean pinned) {
        int position = recyclerView.getChildAdapterPosition(v);
        if (position != RecyclerView.NO_POSITION) {

        }
    }

    private boolean supportsViewElevation() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }

    public AbstractDataProvider getDataProvider() {
        return new PatientDataProvider(waitingclinicLists.get(0).getWaitingPatientList().getViewAll());
    }

    public void notifyItemChanged(int position) {
        mAdapter.notifyItemChanged(position);
    }

    public void notifyItemInserted(int position) {
        mAdapter.notifyItemInserted(position);
        recyclerView.scrollToPosition(position);
    }
}