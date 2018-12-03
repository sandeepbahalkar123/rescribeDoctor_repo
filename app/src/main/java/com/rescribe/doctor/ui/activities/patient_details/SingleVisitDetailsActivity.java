package com.rescribe.doctor.ui.activities.patient_details;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.patient_detail.SingleVisitAdapter;
import com.rescribe.doctor.helpers.patient_detail.PatientDetailHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.CommonBaseModelContainer;
import com.rescribe.doctor.model.case_details.PatientHistory;
import com.rescribe.doctor.model.case_details.Range;
import com.rescribe.doctor.model.case_details.VisitCommonData;
import com.rescribe.doctor.model.case_details.VisitData;
import com.rescribe.doctor.model.case_details.Vital;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.singleton.RescribeApplication;
import com.rescribe.doctor.smartpen.PenInfoActivity;
import com.rescribe.doctor.ui.activities.add_records.SelectedRecordsActivity;
import com.rescribe.doctor.ui.activities.zoom_images.MultipleImageWithSwipeAndZoomActivity;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;
import com.smart.pen.core.common.Listeners;
import com.smart.pen.core.model.DeviceObject;
import com.smart.pen.core.services.PenService;
import com.smart.pen.core.symbol.ConnectState;
import com.smart.pen.core.symbol.Keys;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.rescribe.doctor.adapters.patient_detail.SingleVisitAdapter.CHILD_TYPE_ANNOTATION;
import static com.rescribe.doctor.adapters.patient_detail.SingleVisitAdapter.CHILD_TYPE_ATTACHMENTS;
import static com.rescribe.doctor.adapters.patient_detail.SingleVisitAdapter.CHILD_TYPE_NOTES;
import static com.rescribe.doctor.adapters.patient_detail.SingleVisitAdapter.CHILD_TYPE_VITALS;
import static com.rescribe.doctor.adapters.patient_detail.SingleVisitAdapter.TEXT_LIMIT;
import static com.rescribe.doctor.services.SyncOfflineRecords.ATTATCHMENT_DOC_UPLOAD;
import static com.rescribe.doctor.smartpen.PenInfoActivity.MY_PERMISSIONS_REQUEST_CODE;
import static com.rescribe.doctor.ui.fragments.patient.patient_history_fragment.PatientHistoryListFragmentContainer.SELECT_REQUEST_CODE;

/**
 * Created by jeetal on 14/6/17.
 */


public class SingleVisitDetailsActivity extends AppCompatActivity implements HelperResponse, SingleVisitAdapter.OnAttachmentsListener {

    private static final String PAIN_SCALE = "pain scale";
    @BindView(R.id.historyExpandableListView)
    ExpandableListView mHistoryExpandableListView;
    @BindView(R.id.emptyListView)
    RelativeLayout mNoRecordAvailable;
    @BindView(R.id.backImageView)
    ImageView backImageView;
    @BindView(R.id.titleTextView)
    CustomTextView titleTextView;
    @BindView(R.id.userInfoTextView)
    CustomTextView userInfoTextView;
    @BindView(R.id.dateTextview)
    CustomTextView dateTextview;
    @BindView(R.id.year)
    Spinner year;
    @BindView(R.id.addImageView)
    ImageView addImageView;

    @BindView(R.id.addRecordButton)
    Button addRecordButton;
    @BindView(R.id.addNoteButton)
    Button addNoteButton;

    @BindView(R.id.footer)
    LinearLayout footer;

    List<VisitCommonData> notesList = new ArrayList<>();

    private boolean mIsDocUploaded = false;
    private int mLastExpandedPosition = -1;
    private SingleVisitAdapter mSingleVisitAdapter;
    private boolean isBpMin = false;
    private boolean isBpMax = false;
    private String month;
    private String mYear;
    private String mDateSelected;
    private String patientID;
    private String opdID;
    private String mHospitalPatId;
    private String mOpdTime;
    private boolean isDead = false;
    private PatientDetailHelper mSingleVisitDetailHelper;
    private boolean isAttachmentDeleted = false;
    private int mAptId;
    private ProgressDialog mProgressDialog;
    private Context mContext;
    private Handler mHandler;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                if (intent.getAction().equals(ATTATCHMENT_DOC_UPLOAD)) {
                    initialize();
                    mIsDocUploaded = true;

                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_detail_activity);
        ButterKnife.bind(this);
        mContext = this;
        mHandler = new Handler();
        getBundleData();
        initialize();
    }

    private void getBundleData() {

        Intent intent = getIntent();
        userInfoTextView.setVisibility(View.VISIBLE);
        dateTextview.setVisibility(View.VISIBLE);
        if (intent.getExtras() != null) {
            patientID = intent.getStringExtra(RescribeConstants.PATIENT_ID);
            mAptId = intent.getIntExtra(RescribeConstants.APPOINTMENT_ID, 0);
            opdID = intent.getStringExtra(RescribeConstants.PATIENT_OPDID);
            mOpdTime = intent.getStringExtra(RescribeConstants.OPD_TIME);
            mHospitalPatId = intent.getStringExtra(RescribeConstants.PATIENT_HOS_PAT_ID);
            titleTextView.setText(intent.getStringExtra(RescribeConstants.PATIENT_NAME));
            userInfoTextView.setText(intent.getStringExtra(RescribeConstants.PATIENT_INFO));
            mDateSelected = intent.getStringExtra(RescribeConstants.DATE);
            isDead = intent.getBooleanExtra(RescribeConstants.PATIENT_IS_DEAD, false);
            String timeToShow = CommonMethods.formatDateTime(mDateSelected, RescribeConstants.DATE_PATTERN.MMM_YY,
                    RescribeConstants.DATE_PATTERN.UTC_PATTERN, RescribeConstants.DATE).toLowerCase();
            String[] timeToShowSpilt = timeToShow.split(",");
            month = timeToShowSpilt[0].substring(0, 1).toUpperCase() + timeToShowSpilt[0].substring(1);
            mYear = timeToShowSpilt.length == 2 ? timeToShowSpilt[1] : "";
            Date date = CommonMethods.convertStringToDate(mDateSelected, RescribeConstants.DATE_PATTERN.UTC_PATTERN);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            String toDisplay = cal.get(Calendar.DAY_OF_MONTH) + "<sup>" + CommonMethods.getSuffixForNumber(cal.get(Calendar.DAY_OF_MONTH)) + "</sup> " + month + "'" + mYear;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dateTextview.setText(Html.fromHtml(toDisplay, Html.FROM_HTML_MODE_LEGACY));
            } else {
                dateTextview.setText(Html.fromHtml(toDisplay));
            }
        }


    }

    private void initialize() {


        mSingleVisitDetailHelper = new PatientDetailHelper(this, this);
        mSingleVisitDetailHelper.doGetOneDayVisit(opdID, patientID);


        // title.setText(getString(R.string.visit_details));

        mHistoryExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

                // this is done because if single element in child list , groupPosition will not expand, it will expand on advice even if it has only one element ,vitals will also expand
                List<PatientHistory> listDataList = mSingleVisitAdapter.getListDataList();
                List<VisitCommonData> childObject = listDataList.get(groupPosition).getCommonData();

                if (childObject.size() == 1) {

                    boolean flag = true;
                    if (listDataList.get(groupPosition).getCaseDetailName().toLowerCase().contains(CHILD_TYPE_ATTACHMENTS) || listDataList.get(groupPosition).getCaseDetailName().toLowerCase().contains(CHILD_TYPE_ANNOTATION) || listDataList.get(groupPosition).getCaseDetailName().toLowerCase().contains(CHILD_TYPE_VITALS) || listDataList.get(groupPosition).getCaseDetailName().toLowerCase().contains(CHILD_TYPE_NOTES))
                        flag = false;

                    if (flag) {
                        if (childObject.get(0).getName().length() <= TEXT_LIMIT)
                            mHistoryExpandableListView.collapseGroup(groupPosition);
                    }
                }

                collapseOther(groupPosition);
            }

            private void collapseOther(int groupPosition) {
                if (mLastExpandedPosition != -1 && mLastExpandedPosition != groupPosition)
                    mHistoryExpandableListView.collapseGroup(mLastExpandedPosition);
                mLastExpandedPosition = groupPosition;
            }
        });

        mHistoryExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener()

        {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                        int childPosition, long id) {

                mHistoryExpandableListView.collapseGroup(groupPosition);

                return false;
            }
        });


    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        Log.e("Pat Single visit rsp", customResponse.toString());
        Log.e("mOldDataTag", mOldDataTag);

        if (mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_DELETE_PATIENT_OPD_ATTCHMENTS) || mOldDataTag.equalsIgnoreCase(RescribeConstants.TASK_DELETE_PATIENT_OPD_NOTES)) {
            CommonBaseModelContainer common = (CommonBaseModelContainer) customResponse;
            if (common.getCommonRespose().isSuccess()) {
                isAttachmentDeleted = mSingleVisitAdapter.removeSelectedAttachmentFromList();
                if (mSingleVisitAdapter.getListDataList().size() == 1) {
                    if (mSingleVisitAdapter.getListDataList().get(0).getCaseDetailName().equals(PAIN_SCALE)) {
                        mSingleVisitAdapter.getListDataList().remove(0);
                        mSingleVisitAdapter.notifyDataSetChanged();
                        mNoRecordAvailable.setVisibility(View.VISIBLE);
                        mHistoryExpandableListView.setVisibility(View.INVISIBLE);
                    }
                } else {
                    mSingleVisitAdapter.notifyDataSetChanged();
                    if (mSingleVisitAdapter.getListDataList().size() == 0) {
                        mNoRecordAvailable.setVisibility(View.VISIBLE);
                        mHistoryExpandableListView.setVisibility(View.INVISIBLE);
                    }
                }
            }
            CommonMethods.showToast(this, common.getCommonRespose().getStatusMessage());
        } else {
            VisitData visitData = (VisitData) customResponse;
            mHistoryExpandableListView.setVisibility(View.VISIBLE);
            mNoRecordAvailable.setVisibility(View.GONE);
            List<PatientHistory> patientHistoryList = visitData.getPatientHistory();
            List<Vital> vitalSortedList = new ArrayList<>();
            // Bpmin and Bpmax is clubed together as Bp in vitals
            for (int i = 0; i < patientHistoryList.size(); i++) {
                if (patientHistoryList.get(i).getVitals() != null) {
                    String pos = null;

                    List<Vital> vitalList = patientHistoryList.get(i).getVitals();
                    for (int j = 0; j < vitalList.size(); j++) {

                        Vital dataObject = vitalList.get(j);
                        if (dataObject.getUnitName().contains(getString(R.string.bp_max))) {
                            setBpMax(true);
                        }
                        if (dataObject.getUnitName().contains(getString(R.string.bp_min))) {
                            setBpMin(true);
                        }
                    }

                    for (int j = 0; j < vitalList.size(); j++) {
                        Vital dataObject = vitalList.get(j);
                        if (isBpMax() && isBpMin()) {
                            if (dataObject.getUnitName().contains(getString(R.string.bp_max)) || dataObject.getUnitName().contains(getString(R.string.bp_min))) {
                                Vital vital = new Vital();
                                if (pos == null) {
                                    vital.setUnitName(getString(R.string.bp) + " " + dataObject.getUnitValue());
                                    vital.setUnitValue(dataObject.getUnitValue());
//                                    vital.setCategory(dataObject.getCategory());
                                    if (!dataObject.getCategory().isEmpty())
                                        vital.setCategory(dataObject.getCategory());
                                    else
                                        vital.setCategory(dataObject.getUnitName());
                                    vital.setIcon(dataObject.getIcon());
                                    for (int k = 0; k < dataObject.getRanges().size(); k++) {
                                        dataObject.getRanges().get(k).setNameOfVital(getString(R.string.bp_max));
                                    }
                                    vital.setRanges(dataObject.getRanges());
                                    vital.setDisplayName(dataObject.getDisplayName());
                                    vitalSortedList.add(vital);
                                    pos = String.valueOf(j);
                                } else {
                                    Vital previousData = vitalSortedList.get(Integer.parseInt(pos));
                                    String unitValue = previousData.getUnitValue();
                                    String unitCategory = "";
                                    if (!previousData.getCategory().isEmpty()) {
                                        unitCategory = previousData.getCategory();
                                        unitCategory = unitCategory + getString(R.string.colon_sign) + dataObject.getCategory();
                                    } else {
                                        unitCategory = previousData.getUnitName();
                                        unitCategory = unitCategory + getString(R.string.colon_sign) + dataObject.getUnitName();

                                    }
                                    for (int k = 0; k < dataObject.getRanges().size(); k++) {
                                        dataObject.getRanges().get(k).setNameOfVital(getString(R.string.bp_min));
                                    }
                                    unitValue = unitValue + "/" + dataObject.getUnitValue();
                                    previousData.setUnitName(getString(R.string.bp));
                                    previousData.setUnitValue(unitValue);
                                    previousData.setCategory(unitCategory);
                                    List<Range> ranges = previousData.getRanges();
                                    ranges.addAll(dataObject.getRanges());
                                    vitalSortedList.set(Integer.parseInt(pos), previousData);
                                }
                            } else {
                                Vital vital = new Vital();
                                vital.setUnitName(vitalList.get(j).getDisplayName());
                                vital.setUnitValue(vitalList.get(j).getUnitValue());
                                if (!vitalList.get(j).getCategory().isEmpty())
                                    vital.setCategory(vitalList.get(j).getCategory());
                                else
                                    vital.setCategory(vitalList.get(j).getUnitName());
                                vital.setRanges(vitalList.get(j).getRanges());
                                vital.setIcon(vitalList.get(j).getIcon());
                                vital.setDisplayName(vitalList.get(j).getDisplayName());
                                vitalSortedList.add(vital);
                            }

                        } else {
                            Vital vital;
                            if (dataObject.getUnitName().contains(getString(R.string.bp_max))) {
                                vital = vitalList.get(j);
                                vital.setUnitName("Systolic BP" + " " + vital.getUnitValue());
                                vital.setDisplayName("Systolic BP");
                                vitalSortedList.add(vital);
                            } else if (dataObject.getUnitName().contains(getString(R.string.bp_min))) {
                                vital = vitalList.get(j);
                                vital.setUnitName("Diastolic BP" + " " + vital.getUnitValue());
                                vital.setDisplayName("Diastolic BP");
                                vitalSortedList.add(vital);
                            } else {
                                vital = vitalList.get(j);
                                vital.setUnitName(vitalList.get(j).getDisplayName());
                                vitalSortedList.add(vital);
                            }
                        }
                    }
                    patientHistoryList.get(i).setVitals(vitalSortedList);
                }

                if (patientHistoryList.get(i).getCaseDetailName().toLowerCase().contains(CHILD_TYPE_NOTES))
                    notesList = patientHistoryList.get(i).getCommonData();
            }


            if (!isAllCaseDetailsDataIsEmpty(patientHistoryList)) {
                mSingleVisitAdapter = new SingleVisitAdapter(this, patientHistoryList, this);
                mHistoryExpandableListView.setAdapter(mSingleVisitAdapter);
                mHistoryExpandableListView.setVisibility(View.VISIBLE);
                mNoRecordAvailable.setVisibility(View.GONE);
            } else {
                mHistoryExpandableListView.setVisibility(View.INVISIBLE);
                mNoRecordAvailable.setVisibility(View.VISIBLE);
            }

            if (!isDead) {
                addRecordButton.setVisibility(View.VISIBLE);

                if (RescribePreferencesManager.getBoolean(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.PREMIUM, mContext))
                    addNoteButton.setVisibility(View.VISIBLE);
                else addNoteButton.setVisibility(View.GONE);
            } else footer.setVisibility(View.GONE);

        }

    }

    private boolean isAllCaseDetailsDataIsEmpty(List<PatientHistory> patientHistoryList) {

        int allEmptyCount = 0;
        int isVitalEmpty = 0;
        //---- to check vital is empty or not.
        for (PatientHistory obj :
                patientHistoryList) {
            if (obj.getVitals() != null) {
                if (obj.getVitals().isEmpty())
                    isVitalEmpty = isVitalEmpty + 1;
            }
        }
        //--------
        for (PatientHistory obj :
                patientHistoryList) {
            if (obj.getCommonData() != null) {
                if (obj.getCommonData().isEmpty()) {
                    allEmptyCount = allEmptyCount + 1;
                }
            }
        }

        if ((isVitalEmpty + allEmptyCount) == patientHistoryList.size()) {
            return true;
        }
        return false;

        //---------

    }

    @Override
    public void onParseError(String mOldDataTag, String errorMessage) {

    }

    @Override
    public void onServerError(String mOldDataTag, String serverErrorMessage) {

    }

    @Override
    public void onNoConnectionError(String mOldDataTag, String serverErrorMessage) {
        mHistoryExpandableListView.setVisibility(View.GONE);
        mNoRecordAvailable.setVisibility(View.VISIBLE);
    }

    public boolean isBpMin() {
        return isBpMin;
    }

    public void setBpMin(boolean bpMin) {
        isBpMin = bpMin;
    }

    public boolean isBpMax() {
        return isBpMax;
    }

    public void setBpMax(boolean bpMax) {
        isBpMax = bpMax;
    }

    @OnClick({R.id.backImageView, R.id.addNoteButton, R.id.addRecordButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backImageView:
                onBackPressed();
                break;
            case R.id.addNoteButton:

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        MY_PERMISSIONS_REQUEST_CODE
                );

                break;
            case R.id.addRecordButton:
                CommonMethods.getFormattedDate(mDateSelected, RescribeConstants.DATE_PATTERN.UTC_PATTERN, RescribeConstants.DATE_PATTERN.DD_MM_YYYY);
                Intent intent = new Intent(this, SelectedRecordsActivity.class);
                intent.putExtra(RescribeConstants.OPD_ID, opdID);
                intent.putExtra(RescribeConstants.PATIENT_HOS_PAT_ID, mHospitalPatId);
                intent.putExtra(RescribeConstants.LOCATION_ID, "0");
                intent.putExtra(RescribeConstants.PATIENT_ID, patientID);
                intent.putExtra(RescribeConstants.CLINIC_ID, "0");
                intent.putExtra(RescribeConstants.APPOINTMENT_ID, mAptId);
                intent.putExtra(RescribeConstants.PATIENT_NAME, titleTextView.getText().toString());
                intent.putExtra(RescribeConstants.PATIENT_INFO, userInfoTextView.getText().toString());
                intent.putExtra(RescribeConstants.VISIT_DATE, CommonMethods.getFormattedDate(mDateSelected, RescribeConstants.DATE_PATTERN.UTC_PATTERN, RescribeConstants.DATE_PATTERN.DD_MM_YYYY));
                intent.putExtra(RescribeConstants.OPD_TIME, mOpdTime);

                startActivity(intent);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CODE: {
                // When request is cancelled, the results array are empty
                if ((grantResults.length <= 0) ||
                        (grantResults[0]
                                + grantResults[1]
                                + grantResults[2] != PackageManager.PERMISSION_GRANTED)) {
                    // Permissions are denied
                    Toast.makeText(this, "Permissions denied.", Toast.LENGTH_SHORT).show();
                } else {

                    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (!mBluetoothAdapter.isEnabled()) {
                        if (!mBluetoothAdapter.enable())
                            Toast.makeText(this, "Please Turn on bluetooth.", Toast.LENGTH_SHORT).show();
                    }
                    openSmartPen();
                    // Permissions are granted
//                    Toast.makeText(this, "Permissions granted.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void openSmartPen() {
        mProgressDialog = ProgressDialog.show(mContext, "", getString(R.string.service_ble_start), true);
        // Binding Bluetooth pen service
        RescribeApplication.getInstance().bindPenService(Keys.APP_PEN_SERVICE_NAME);
        isPenServiceReady(Keys.APP_PEN_SERVICE_NAME);
    }

    private void isPenServiceReady(final String svrName) {
        PenService service = RescribeApplication.getInstance().getPenService();
        if (service != null) {
            if (service.checkDeviceConnect() == ConnectState.CONNECTED) {
                dismissProgressDialog();

                CommonMethods.getFormattedDate(mDateSelected, RescribeConstants.DATE_PATTERN.UTC_PATTERN, RescribeConstants.DATE_PATTERN.DD_MM_YYYY);
                Intent intent = new Intent(mContext, PenInfoActivity.class);
                intent.putExtra(RescribeConstants.OPD_ID, opdID);
                intent.putExtra(RescribeConstants.PATIENT_HOS_PAT_ID, mHospitalPatId);
                intent.putExtra(RescribeConstants.LOCATION_ID, "0");
                intent.putExtra(RescribeConstants.PATIENT_ID, patientID);
                intent.putExtra(RescribeConstants.CLINIC_ID, "0");
                intent.putExtra(RescribeConstants.APPOINTMENT_ID, mAptId);
                intent.putExtra(RescribeConstants.PATIENT_NAME, titleTextView.getText().toString());
                intent.putExtra(RescribeConstants.PATIENT_INFO, userInfoTextView.getText().toString());
                intent.putExtra(RescribeConstants.VISIT_DATE, CommonMethods.getFormattedDate(mDateSelected, RescribeConstants.DATE_PATTERN.UTC_PATTERN, RescribeConstants.DATE_PATTERN.DD_MM_YYYY));
                intent.putExtra(RescribeConstants.OPD_TIME, mOpdTime);
                if (!notesList.isEmpty()) {
                    intent.putExtra(RescribeConstants.START_FROM_NOTE, true);
                    intent.putParcelableArrayListExtra(RescribeConstants.ATTACHMENTS_LIST, new ArrayList<VisitCommonData>(notesList));
                }
                startActivity(intent);

            } else {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Scan Bluetooth and connect service
                        scanBluetoothAndConnect();
                    }
                }, 500);
            }
        } else {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isPenServiceReady(svrName);
                }
            }, 1000);
        }
    }

    private void scanBluetoothAndConnect() {
        PenService service = RescribeApplication.getInstance().getPenService();
        if (service != null) {
            service.scanDevice(new Listeners.OnScanDeviceListener() {
                @Override
                public void find(DeviceObject device) {

                    CommonMethods.getFormattedDate(mDateSelected, RescribeConstants.DATE_PATTERN.UTC_PATTERN, RescribeConstants.DATE_PATTERN.DD_MM_YYYY);
                    Intent intent = new Intent(mContext, PenInfoActivity.class);
                    intent.putExtra(RescribeConstants.OPD_ID, opdID);
                    intent.putExtra(RescribeConstants.PATIENT_HOS_PAT_ID, mHospitalPatId);
                    intent.putExtra(RescribeConstants.LOCATION_ID, "0");
                    intent.putExtra(RescribeConstants.PATIENT_ID, patientID);
                    intent.putExtra(RescribeConstants.CLINIC_ID, "0");
                    intent.putExtra(RescribeConstants.APPOINTMENT_ID, mAptId);
                    intent.putExtra(RescribeConstants.PATIENT_NAME, titleTextView.getText().toString());
                    intent.putExtra(RescribeConstants.PATIENT_INFO, userInfoTextView.getText().toString());
                    intent.putExtra(RescribeConstants.VISIT_DATE, CommonMethods.getFormattedDate(mDateSelected, RescribeConstants.DATE_PATTERN.UTC_PATTERN, RescribeConstants.DATE_PATTERN.DD_MM_YYYY));
                    intent.putExtra(RescribeConstants.OPD_TIME, mOpdTime);
                    intent.putExtra(Keys.KEY_DEVICE_ADDRESS, device.address);
                    if (!notesList.isEmpty()) {
                        intent.putExtra(RescribeConstants.START_FROM_NOTE, true);
                        intent.putParcelableArrayListExtra(RescribeConstants.ATTACHMENTS_LIST, new ArrayList<VisitCommonData>(notesList));
                    }
                    startActivity(intent);

                    // Stop searching
                    PenService service = RescribeApplication.getInstance().getPenService();
                    if (service != null) {
                        service.stopScanDevice();
                    }

                    dismissProgressDialog();
                }

                @Override
                public void complete(HashMap<String, DeviceObject> list) {
                    Log.i("DEVICES", list.toString());
                    dismissProgressDialog();
                    if (list.isEmpty())
                        showRetryDialog();
                }
            });
        }
    }

    private void showRetryDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext, R.style.MyDialogTheme);
        alert.setTitle("Info");
        alert.setMessage("Device not found");
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.setPositiveButton("Scan Again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mProgressDialog = ProgressDialog.show(mContext, "", getString(R.string.service_ble_start), true);
                scanBluetoothAndConnect();
            }
        });
        alert.show();
    }

    /**
     * 释放progressDialog
     **/
    protected void dismissProgressDialog() {
        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing())
                mProgressDialog.dismiss();

            mProgressDialog = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (isAttachmentDeleted || mIsDocUploaded) {
            Intent output = new Intent();
            output.putExtra("SINGLE_PAGE_ADAPTER", isAttachmentDeleted);
            setResult(RESULT_OK, output);
        }
        super.onBackPressed();
    }

    @Override
    public void deleteAttachments(HashSet<VisitCommonData> list, String caseName) {
        if (caseName.contains(CHILD_TYPE_ATTACHMENTS))
            mSingleVisitDetailHelper.deleteSelectedAttachments(list, patientID);
        else if (caseName.contains(CHILD_TYPE_NOTES))
            mSingleVisitDetailHelper.deleteSelectedNotes(list);
    }

    @Override
    public void onClickAttachment(VisitCommonData visitCommonData, List<VisitCommonData> visitCommonDatas, boolean isNote) {
        // Show Image or WebView.
        String tag = visitCommonData.getUrl();
        String fileExtension = tag.substring(tag.lastIndexOf("."));

        if (fileExtension.contains(".doc") || fileExtension.contains(".odt") || fileExtension.contains(".ppt") || fileExtension.contains(".odp") || fileExtension.contains(".xls") || fileExtension.contains(".ods") || fileExtension.contains(".pdf")) {
            Intent intent = new Intent(mContext, WebViewActivity.class);
            intent.putExtra(mContext.getString(R.string.title_activity_selected_docs), tag);
            intent.putExtra(mContext.getString(R.string.file_extension), fileExtension);
            startActivity(intent);
        } else {
            // do stuff here
            //  Intent intent = new Intent(mContext, ZoomImageViewActivity.class);
            Intent intent = new Intent(mContext, MultipleImageWithSwipeAndZoomActivity.class);
            intent.putExtra(RescribeConstants.DOCUMENTS, tag);
            intent.putExtra(RescribeConstants.IS_URL, true);

            intent.putExtra(RescribeConstants.START_FROM_NOTE, isNote);
            intent.putExtra(RescribeConstants.OPD_ID, opdID);
            intent.putExtra(RescribeConstants.PATIENT_HOS_PAT_ID, mHospitalPatId);
            intent.putExtra(RescribeConstants.LOCATION_ID, "0");
            intent.putExtra(RescribeConstants.PATIENT_ID, patientID);
            intent.putExtra(RescribeConstants.CLINIC_ID, "0");
            intent.putExtra(RescribeConstants.APPOINTMENT_ID, mAptId);
            intent.putExtra(RescribeConstants.PATIENT_NAME, titleTextView.getText().toString());
            intent.putExtra(RescribeConstants.PATIENT_INFO, userInfoTextView.getText().toString());
            intent.putExtra(RescribeConstants.VISIT_DATE, CommonMethods.getFormattedDate(mDateSelected, RescribeConstants.DATE_PATTERN.UTC_PATTERN, RescribeConstants.DATE_PATTERN.DD_MM_YYYY));
            intent.putExtra(RescribeConstants.OPD_TIME, mOpdTime);

            intent.putParcelableArrayListExtra(RescribeConstants.ATTACHMENTS_LIST, new ArrayList<VisitCommonData>(visitCommonDatas));
            startActivity(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ATTATCHMENT_DOC_UPLOAD);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_REQUEST_CODE)
                initialize();
        }
    }

}

