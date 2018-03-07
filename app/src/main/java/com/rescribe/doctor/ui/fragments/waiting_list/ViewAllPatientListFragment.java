package com.rescribe.doctor.ui.fragments.waiting_list;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.waiting_list.ViewAllWaitingListAdapter;
import com.rescribe.doctor.adapters.waiting_list.WaitingListSpinnerAdapter;
import com.rescribe.doctor.helpers.myappointments.AppointmentHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.patient.template_sms.TemplateBaseModel;
import com.rescribe.doctor.model.waiting_list.Active;
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

public class ViewAllPatientListFragment extends Fragment implements OnStartDragListener ,HelperResponse{
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
    private ViewAllWaitingListAdapter mViewAllWaitingListAdapter;
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
        mAppointmentHelper = new AppointmentHelper(getActivity(),this);
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
            clinicNameTextView.setText(waitingclinicLists.get(0).getClinicName()+" - ");
            clinicAddress.setText(waitingclinicLists.get(0).getArea() + ", " + waitingclinicLists.get(0).getCity());
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setClipToPadding(false);
            WaitingPatientList waitingPatientSingleList = waitingclinicLists.get(0).getWaitingPatientList();
            mViewAllWaitingListAdapter = new ViewAllWaitingListAdapter(getActivity(), waitingPatientSingleList.getViewAll(),this);
            LinearLayoutManager linearlayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(linearlayoutManager);
            // off recyclerView Animation
            RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
            if (animator instanceof SimpleItemAnimator)
                ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);

            recyclerView.setAdapter(mViewAllWaitingListAdapter);
        }


        clinicListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
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
                        mViewAllWaitingListAdapter = new ViewAllWaitingListAdapter(getActivity(), waitingPatientTempList.getViewAll(),ViewAllPatientListFragment.this);
                        LinearLayoutManager linearlayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                        recyclerView.setLayoutManager(linearlayoutManager);
                        // off recyclerView Animation
                        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
                        if (animator instanceof SimpleItemAnimator)
                            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
                        recyclerView.setAdapter(mViewAllWaitingListAdapter);
                    }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
        requestDeleteBaseModel.setDocId(Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID,getActivity())));
        requestDeleteBaseModel.setLocationId(mLocationId);
        requestDeleteBaseModel.setWaitingDate(CommonMethods.getFormattedDate(CommonMethods.getCurrentDate(), RescribeConstants.DATE_PATTERN.DD_MM_YYYY,RescribeConstants.DATE_PATTERN.YYYY_MM_DD));
        requestDeleteBaseModel.setWaitingId(viewAll.getWaitingId());
        requestDeleteBaseModel.setWaitingSequence(viewAll.getWaitingSequence());
        mAppointmentHelper.doDeleteWaitingList(requestDeleteBaseModel);

    }

    @Override
    public void onDeleteActiveLayoutClicked(int adapterPosition, Active active) {

    }


    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        if(mOldDataTag.equals(RescribeConstants.TASK_DELETE_WAITING_LIST)){
            TemplateBaseModel templateBaseModel = (TemplateBaseModel)customResponse;
          if(templateBaseModel.getCommon().isSuccess()){
              Toast.makeText(getActivity(), templateBaseModel.getCommon().getStatusMessage()+"", Toast.LENGTH_SHORT).show();
              viewAllArrayList = mViewAllWaitingListAdapter.getAdapterList();
              viewAllArrayList.remove(adapterPos);
              mViewAllWaitingListAdapter.notifyItemRemoved(adapterPos);
          }else{
              Toast.makeText(getActivity(), templateBaseModel.getCommon().getStatusMessage()+"", Toast.LENGTH_SHORT).show();

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
}
