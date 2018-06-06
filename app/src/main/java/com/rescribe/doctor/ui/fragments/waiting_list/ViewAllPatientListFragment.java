package com.rescribe.doctor.ui.fragments.waiting_list;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.waiting_list.DraggableSwipeableViewAllWaitingListAdapter;
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
import com.rescribe.doctor.model.waiting_list.request_delete_waiting_list.RequestWaitingListStatusChangeBaseModel;
import com.rescribe.doctor.model.waiting_list.request_drag_drop.RequestForDragAndDropBaseModel;
import com.rescribe.doctor.model.waiting_list.request_drag_drop.WaitingListSequence;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.ui.activities.my_patients.patient_history.PatientHistoryActivity;
import com.rescribe.doctor.ui.activities.waiting_list.WaitingMainListActivity;
import com.rescribe.doctor.ui.customesViews.CircularImageView;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.ui.customesViews.drag_drop_recyclerview_helper.OnStartDragListener;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.rescribe.doctor.util.CommonMethods.toCamelCase;
import static com.rescribe.doctor.util.RescribeConstants.LOCATION_ID;

/**
 * Created by jeetal on 22/2/18.
 */
@RuntimePermissions
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

    @BindView(R.id.noRecords)
    LinearLayout noRecords;

    private Unbinder unbinder;
    private ArrayList<WaitingclinicList> waitingclinicLists = new ArrayList<>();
    private HashMap<String, String> mSelectedClinicDataMap = new HashMap<>();

    private int adapterPos;
    private AppointmentHelper mAppointmentHelper;
    private int mLocationId;
    private DraggableSwipeableViewAllWaitingListAdapter myItemAdapter;
    private WaitingPatientList waitingPatientTempList;
    private String phoneNo;
    private Integer mClinicID;
    private Integer mWaitingIdToBeDeleted;

    private WaitingMainListActivity mParentActivity;

    public ViewAllPatientListFragment() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // unbind
        unbinder.unbind();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.waiting_content_layout, container, false);
        unbinder = ButterKnife.bind(this, mRootView);
        init();
        setClinicListSpinner();
        return mRootView;
    }

    public void init() {
        mAppointmentHelper = new AppointmentHelper(getActivity(), this);
        mParentActivity = (WaitingMainListActivity) getActivity();
        waitingclinicLists = mParentActivity.getReceivedWaitingClinicList();

        if (waitingclinicLists != null) {
            if (waitingclinicLists.size() > 1) {
                clinicListSpinner.setVisibility(View.VISIBLE);
                hospitalDetailsLinearLayout.setVisibility(View.GONE);
                WaitingListSpinnerAdapter mWaitingListSpinnerAdapter = new WaitingListSpinnerAdapter(getActivity(), waitingclinicLists);
                clinicListSpinner.setAdapter(mWaitingListSpinnerAdapter);
            } else {

                if (waitingclinicLists.isEmpty()) {
                    noRecords.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    mLocationId = waitingclinicLists.get(0).getLocationId();
                    waitingPatientTempList = waitingclinicLists.get(0).getWaitingPatientList();
                    clinicListSpinner.setVisibility(View.GONE);
                    hospitalDetailsLinearLayout.setVisibility(View.VISIBLE);
                    clinicNameTextView.setText(waitingclinicLists.get(0).getClinicName() + " - ");
                    clinicAddress.setText(waitingclinicLists.get(0).getArea() + ", " + waitingclinicLists.get(0).getCity());
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setClipToPadding(false);
                    setAdapter();
                }
            }
        }

    }

    private void setClinicListSpinner() {
        clinicListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                WaitingclinicList waitingclinicList = waitingclinicLists.get(i);
                mLocationId = waitingclinicList.getLocationId();
                mClinicID = waitingclinicList.getClinicId();
                waitingPatientTempList = waitingclinicList.getWaitingPatientList();

                if (waitingPatientTempList != null) {
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setClipToPadding(false);
                    setAdapter();
                }

                //-------
                mSelectedClinicDataMap.put(RescribeConstants.CLINIC_ID, "" + mClinicID);
                mSelectedClinicDataMap.put(RescribeConstants.CITY_ID, "" + waitingclinicList.getCityId());
                mSelectedClinicDataMap.put(RescribeConstants.CITY_NAME, "" + waitingclinicList.getCity());
                mSelectedClinicDataMap.put(RescribeConstants.LOCATION_ID, "" + mLocationId);
                //-------

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        for (int index = 0; index < waitingclinicLists.size(); index++) {
            WaitingclinicList waitingclinicL = waitingclinicLists.get(index);
            if (waitingclinicL.getLocationId() == getArguments().getInt(LOCATION_ID)) {
                clinicListSpinner.setSelection(index);
                break;
            }
        }

    }

    public static ViewAllPatientListFragment newInstance(Bundle bundle) {
        ViewAllPatientListFragment fragment = new ViewAllPatientListFragment();
        fragment.setArguments(bundle);
        return fragment;
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
        myItemAdapter = new DraggableSwipeableViewAllWaitingListAdapter(getDataProvider());
        myItemAdapter.setEventListener(new DraggableSwipeableViewAllWaitingListAdapter.EventListener() {

            @Override
            public void onInConsultation(int position, ViewAll viewAll) {
                if (viewAll.getWaitingStatusId().equals(RescribeConstants.WAITING_LIST_STATUS.IN_CONSULTATION)) {
                    CommonMethods.showToast(getActivity(), getString(R.string.err_inconsultation_already_msg));
                } else {
                    adapterPos = position;
                    mWaitingIdToBeDeleted = viewAll.getWaitingId();
                    createInstanceToChangeWaitingListStatus(RescribeConstants.WAITING_LIST_STATUS.IN_CONSULTATION, viewAll);
                }
            }

            @Override
            public void onCompletedAction(int position, ViewAll viewAll) {
                adapterPos = position;
                mWaitingIdToBeDeleted = viewAll.getWaitingId();
                createInstanceToChangeWaitingListStatus(RescribeConstants.WAITING_LIST_STATUS.COMPLETED, viewAll);
            }

            @Override
            public void onDeleteClick(int position, ViewAll viewAll) {
                if (viewAll.getWaitingStatusId().equals(RescribeConstants.WAITING_LIST_STATUS.IN_CONSULTATION)) {
                    CommonMethods.showToast(getActivity(), getString(R.string.err_inconsultation_delete_msg));
                } else {
                    adapterPos = position;
                    mWaitingIdToBeDeleted = viewAll.getWaitingId();
                    RequestWaitingListStatusChangeBaseModel requestDeleteBaseModel = new RequestWaitingListStatusChangeBaseModel();
                    requestDeleteBaseModel.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, getActivity())));
                    requestDeleteBaseModel.setLocationId(mLocationId);
                    requestDeleteBaseModel.setWaitingDate(CommonMethods.getCurrentDate(RescribeConstants.DATE_PATTERN.YYYY_MM_DD));
                    requestDeleteBaseModel.setWaitingId(viewAll.getWaitingId());
                    requestDeleteBaseModel.setWaitingSequence(viewAll.getWaitingSequence());
                    mAppointmentHelper.doDeleteWaitingList(requestDeleteBaseModel);
                }
            }


            @Override
            public void onItemPinned(int position) {
//                CommonMethods.showToast(getContext(), "Pinned " + position);
            }

            @Override
            public void onItemViewClicked(View v, boolean pinned, AbstractDataProvider.Data clickedDataObject) {
                onItemViewClick(v, pinned, clickedDataObject);
            }

            @Override
            public void onItemMoved(int fromPosition, int toPosition) {

                ViewAll removed = waitingPatientTempList.getViewAll().remove(fromPosition);
                waitingPatientTempList.getViewAll().add(toPosition, removed);

                RequestForDragAndDropBaseModel requestForDragAndDropBaseModel = new RequestForDragAndDropBaseModel();
                ArrayList<WaitingListSequence> waitingListSequences = new ArrayList<>();

                for (int i = 0; i < myItemAdapter.getAllItems().size(); i++) {
                    WaitingListSequence waitingListSequence = new WaitingListSequence();
                    waitingListSequence.setWaitingSequence(i + 1);
                    waitingListSequence.setWaitingId(String.valueOf(myItemAdapter.getAllItems().get(i).getWaitingId()));

                    waitingListSequences.add(waitingListSequence);
                }
                requestForDragAndDropBaseModel.setWaitingListSequence(waitingListSequences);
                mAppointmentHelper.doDargAndDropApi(requestForDragAndDropBaseModel);
            }

            @Override
            public void onPhoneClick(String patientPhone) {
                phoneNo = patientPhone;
                ViewAllPatientListFragmentPermissionsDispatcher.doCallSupportWithCheck(ViewAllPatientListFragment.this);

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
       /* if (supportsViewElevation()) {
            // Lollipop or later has native drop shadow feature. ItemShadowDecorator is not required.
        } else {
            recyclerView.addItemDecoration(new ItemShadowDecorator((NinePatchDrawable) ContextCompat.getDrawable(getContext(), R.drawable.material_shadow_z3)));
        }
        recyclerView.addItemDecoration(new SimpleListDividerDecorator(ContextCompat.getDrawable(getContext(), R.drawable.list_divider_h), true));
*/
        // NOTE:
        // The initialization order is very important! This order determines the priority of touch event handling.
        //
        // priority: TouchActionGuard > Swipe > DragAndDrop
        recyclerViewTouchActionGuardManager.attachRecyclerView(recyclerView);
        recyclerViewSwipeManager.attachRecyclerView(recyclerView);
        recyclerViewDragDropManager.attachRecyclerView(recyclerView);

        if (myItemAdapter.getItemCount() == 0)
            noRecords.setVisibility(View.VISIBLE);
        else noRecords.setVisibility(View.GONE);

    }


    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {

    }

    @Override
    public void onDeleteViewAllLayoutClicked(int adapterPosition, ViewAll viewAll) {
        adapterPos = adapterPosition;
        RequestWaitingListStatusChangeBaseModel requestDeleteBaseModel = new RequestWaitingListStatusChangeBaseModel();
        requestDeleteBaseModel.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, getActivity())));
        requestDeleteBaseModel.setLocationId(mLocationId);
        requestDeleteBaseModel.setWaitingDate(CommonMethods.getCurrentDate(RescribeConstants.DATE_PATTERN.YYYY_MM_DD));
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
            if (templateBaseModel.getCommon().isSuccess()) {
                Toast.makeText(getActivity(), templateBaseModel.getCommon().getStatusMessage() + "", Toast.LENGTH_SHORT).show();
                myItemAdapter.removeItem(adapterPos);
                waitingPatientTempList.getViewAll().remove(adapterPos);


                //------------
                mParentActivity.deletePatientFromWaitingClinicList(mClinicID, mWaitingIdToBeDeleted);
                //------------

                // remove from original

                if (myItemAdapter.getItemCount() == 0)
                    noRecords.setVisibility(View.VISIBLE);
                else noRecords.setVisibility(View.GONE);

            } else {
                Toast.makeText(getActivity(), templateBaseModel.getCommon().getStatusMessage() + "", Toast.LENGTH_SHORT).show();
            }
        } else if (mOldDataTag.equals(RescribeConstants.TASK_DARG_DROP)) {
            TemplateBaseModel templateBaseModel = (TemplateBaseModel) customResponse;
            if (templateBaseModel.getCommon().isSuccess()) {
                Toast.makeText(getActivity(), templateBaseModel.getCommon().getStatusMessage() + "", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), templateBaseModel.getCommon().getStatusMessage() + "", Toast.LENGTH_SHORT).show();
            }
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
        if (recyclerViewDragDropManager != null)
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

    private void onItemViewClick(View v, boolean pinned, AbstractDataProvider.Data clickedDataObject) {

        Integer salutation = clickedDataObject.getViewAll().getSalutation();
        String pName = clickedDataObject.getViewAll().getPatientName();
        String pID = String.valueOf(clickedDataObject.getViewAll().getPatientId());
        Integer hostPatID = clickedDataObject.getViewAll().getHospitalPatId();

        String patientName;
        if (salutation != 0)
            patientName = RescribeConstants.SALUTATION[salutation - 1] + toCamelCase(pName);
        else patientName = toCamelCase(pName);
        Bundle b = new Bundle();
        b.putString(RescribeConstants.PATIENT_NAME, patientName);
        b.putString(RescribeConstants.PATIENT_INFO, ""); // TODO: Age and gender is not getting from API
        b.putInt(RescribeConstants.CLINIC_ID, mClinicID);
        b.putString(RescribeConstants.PATIENT_ID, pID);
        b.putString(RescribeConstants.PATIENT_HOS_PAT_ID, String.valueOf(hostPatID));
        Intent intent = new Intent(getActivity(), PatientHistoryActivity.class);
        intent.putExtra(RescribeConstants.PATIENT_INFO, b);
        startActivity(intent);
    }

    private boolean supportsViewElevation() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }

    public AbstractDataProvider getDataProvider() {
        return new PatientDataProvider(waitingPatientTempList.getViewAll());
    }

    public void notifyItemChanged(int position) {
        mAdapter.notifyItemChanged(position);
    }

    public void notifyItemInserted(int position) {
        mAdapter.notifyItemInserted(position);
        recyclerView.scrollToPosition(position);
    }

    @NeedsPermission(Manifest.permission.CALL_PHONE)
    void doCallSupport() {
        callSupport(phoneNo);
    }

    private void callSupport(String phoneNo) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNo));
        startActivity(callIntent);
    }


    public void onRequestPermssionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ViewAllPatientListFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    private void createInstanceToChangeWaitingListStatus(int status, ViewAll viewAll) {
        RequestWaitingListStatusChangeBaseModel requestDeleteBaseModel = new RequestWaitingListStatusChangeBaseModel();
        requestDeleteBaseModel.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, getActivity())));
        requestDeleteBaseModel.setLocationId(mLocationId);
        requestDeleteBaseModel.setWaitingDate(CommonMethods.getCurrentDate(RescribeConstants.DATE_PATTERN.YYYY_MM_DD));
        requestDeleteBaseModel.setTime(viewAll.getWaitingInTime());
        requestDeleteBaseModel.setWaitingId(viewAll.getWaitingId());
        requestDeleteBaseModel.setPatientId("" + viewAll.getPatientId());
        requestDeleteBaseModel.setHospitalPatId("" + viewAll.getHospitalPatId());
        requestDeleteBaseModel.setHospitalId("" + mClinicID);
        requestDeleteBaseModel.setStatus("" + status);
        mAppointmentHelper.doUpdateWaitingListStatus(requestDeleteBaseModel);
    }

    public HashMap<String, String> getSelectedClinicDataMap() {
        return mSelectedClinicDataMap;
    }
}
