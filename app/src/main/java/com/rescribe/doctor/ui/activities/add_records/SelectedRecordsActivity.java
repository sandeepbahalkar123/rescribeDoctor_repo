package com.rescribe.doctor.ui.activities.add_records;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.add_records.SelectedRecordsAdapter;
import com.rescribe.doctor.helpers.database.AppDBHelper;
import com.rescribe.doctor.model.investigation.Image;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.singleton.Device;
import com.rescribe.doctor.ui.activities.HomePageActivity;
import com.rescribe.doctor.ui.activities.my_patients.patient_history.PatientHistoryActivity;
import com.rescribe.doctor.ui.activities.patient_details.SingleVisitDetailsActivity;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.Config;
import com.rescribe.doctor.util.NetworkUtil;
import com.rescribe.doctor.util.RescribeConstants;
import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadService;
import net.gotev.uploadservice.UploadServiceBroadcastReceiver;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import io.github.shree.fabmenu.FabSpeedDial;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class SelectedRecordsActivity extends AppCompatActivity implements SelectedRecordsAdapter.OnClickOfComponentsOnSelectedPhoto {


    public static final int CLOSE_SINGLE_VISIT_CODE = 0707;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.uploadButton)
    Button uploadButton;
    @BindView(R.id.fab)
    FabSpeedDial fab;
    @BindView(R.id.coachmark)
    ImageView coachmark;
    private static final int MAX_ATTACHMENT_COUNT = 10;
    @BindView(R.id.backImageView)
    ImageView backImageView;
    @BindView(R.id.titleTextView)
    CustomTextView titleTextView;
    @BindView(R.id.userInfoTextView)
    CustomTextView userInfoTextView;
    @BindView(R.id.dateTextview)
    CustomTextView dateTextview;
    @BindView(R.id.addImageView)
    ImageView addImageView;
    private Context mContext;
    private ArrayList<Image> imagePaths = new ArrayList<>();
    ArrayList<Image> imageArrayList;
    private SelectedRecordsAdapter selectedRecordsAdapter;
    private Dialog dialog;
    private String visitDate;
    private int docId;
    private String patientId;
    private String opdId;
    private AppDBHelper appDBHelper;
    private Device device;
    private UploadNotificationConfig uploadNotificationConfig;
    private String authorizationString;
    private String month;
    private String year;
    private String patientName = "";
    private String patientInfo = "";
    public static final String OTHERS = "Others";
    private String Url;
    private String mHospitalPatId;
    private String mLocationId;
    private String mHospitalId;
    private String mOpdtime;
    private String currentOpdTime;

    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleted_records);
        ButterKnife.bind(this);

        init();


       /* String coachMarkStatus = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.COACHMARK, mContext);
        if (coachMarkStatus.equals(RescribeConstants.YES))
            coachmark.setVisibility(View.GONE);
*/

        // Show two options for user

        dialog = new Dialog(mContext);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.select_file_dialog);
        dialog.setCanceledOnTouchOutside(false);

        dialog.findViewById(R.id.gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                SelectedRecordsActivityPermissionsDispatcher.onPickPhotoWithCheck(SelectedRecordsActivity.this);
            }
        });

        dialog.findViewById(R.id.files).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                SelectedRecordsActivityPermissionsDispatcher.onPickDocWithCheck(SelectedRecordsActivity.this);
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (imagePaths.isEmpty())
                    onBackPressed();
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        // End
        // off recyclerView Animation

        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator)
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);

        selectedRecordsAdapter = new SelectedRecordsAdapter(mContext, imagePaths, this);
        recyclerView.setAdapter(selectedRecordsAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 2);
        recyclerView.setLayoutManager(layoutManager);


        fab.addOnStateChangeListener(new FabSpeedDial.OnStateChangeListener() {
            @Override
            public void onStateChange(boolean open) {
                if (open) {
                    fab.getMainFab().setImageResource(R.drawable.x);
                    fab.getMainFab().setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.statusbar));
                } else {
                    fab.getMainFab().setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.tagColor));
                    fab.getMainFab().setImageResource(R.drawable.fab_icon_records);
                }
            }
        });

        fab.addOnMenuItemClickListener(new FabSpeedDial.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(FloatingActionButton miniFab, @Nullable TextView label, int itemId) {
                for (Image image : imagePaths) {
                    if (image.isSelected()) {
                        if (label != null) {
                            //image.setParentCaption(label.getText().toString());
                            image.setSelected(false);
                        }
                    }
                }
                selectedRecordsAdapter.notifyDataSetChanged();
            }
        });
    }

    private void init() {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) dateTextview.getLayoutParams();

        lp.setMargins(0, 0, getResources().getDimensionPixelSize(R.dimen.dp30), 0);
        dateTextview.setLayoutParams(lp);
        mContext = SelectedRecordsActivity.this;
        mHospitalPatId = getIntent().getStringExtra(RescribeConstants.PATIENT_HOS_PAT_ID);
        mLocationId = getIntent().getStringExtra(RescribeConstants.LOCATION_ID);
        patientId = getIntent().getStringExtra(RescribeConstants.PATIENT_ID);
        mHospitalId = getIntent().getStringExtra(RescribeConstants.CLINIC_ID);
        addImageView.setVisibility(View.VISIBLE);
        patientName = getIntent().getStringExtra(RescribeConstants.PATIENT_NAME);
        patientInfo = getIntent().getStringExtra(RescribeConstants.PATIENT_INFO);
        imageArrayList = getIntent().getParcelableArrayListExtra(RescribeConstants.DOCUMENTS);
        mOpdtime = getIntent().getStringExtra(RescribeConstants.OPD_TIME);
        opdId = getIntent().getStringExtra(RescribeConstants.OPD_ID);

        visitDate = getIntent().getStringExtra(RescribeConstants.VISIT_DATE);

        docId = Integer.parseInt(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, mContext));


        boolean isUploading = getIntent().getBooleanExtra(RescribeConstants.UPLOADING_STATUS, false);
        if (isUploading)
            uploadButton.setEnabled(false);

        appDBHelper = new AppDBHelper(SelectedRecordsActivity.this);
        device = Device.getInstance(SelectedRecordsActivity.this);

        Url = Config.BASE_URL + Config.MY_RECORDS_UPLOAD;
//        Url = "http://192.168.0.115:8000/" + Config.MY_RECORDS_UPLOAD;

        authorizationString = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.AUTHTOKEN, SelectedRecordsActivity.this);

        uploadNotificationConfig = new UploadNotificationConfig();
        uploadNotificationConfig.setTitleForAllStatuses("Document Uploading");
        uploadNotificationConfig.setIconColorForAllStatuses(Color.parseColor("#04abdf"));
        uploadNotificationConfig.setClearOnActionForAllStatuses(true);

        UploadService.UPLOAD_POOL_SIZE = 10;
        setDateInToolbar();
        userInfoTextView.setVisibility(View.VISIBLE);
        titleTextView.setText(patientName);
        userInfoTextView.setText(patientInfo);
    }

    private void setDateInToolbar() {
        //Set Date in Required Format i.e 13thJuly'18
        dateTextview.setVisibility(View.VISIBLE);
        String timeToShow = CommonMethods.formatDateTime(visitDate, RescribeConstants.DATE_PATTERN.MMM_YY,
                RescribeConstants.DATE_PATTERN.DD_MM_YYYY, RescribeConstants.DATE).toLowerCase();
        String[] timeToShowSpilt = timeToShow.split(",");
        month = timeToShowSpilt[0].substring(0, 1).toUpperCase() + timeToShowSpilt[0].substring(1);
        year = timeToShowSpilt.length == 2 ? timeToShowSpilt[1] : "";
        Date date = CommonMethods.convertStringToDate(visitDate, RescribeConstants.DATE_PATTERN.DD_MM_YYYY);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        timeToShow = timeToShow.substring(0, 1).toUpperCase() + timeToShow.substring(1);
        String toDisplay = cal.get(Calendar.DAY_OF_MONTH) + "<sup>" + CommonMethods.getSuffixForNumber(cal.get(Calendar.DAY_OF_MONTH)) + "</sup> " + month + "'" + year;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            dateTextview.setText(Html.fromHtml(toDisplay, Html.FROM_HTML_MODE_LEGACY));
        } else {
            dateTextview.setText(Html.fromHtml(toDisplay));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.select_docs_menu, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_docs:
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void onPickPhoto() {
        if (imagePaths.size() == MAX_ATTACHMENT_COUNT)
            Toast.makeText(this, "Cannot select more than " + MAX_ATTACHMENT_COUNT + " documents", Toast.LENGTH_SHORT).show();
        else {

            ArrayList photos = new ArrayList();
            for (Image photo : imagePaths) {
                if (photo.getType() == FilePickerConst.REQUEST_CODE_PHOTO)
                    photos.add(photo.getImagePath());
            }

            FilePickerBuilder.getInstance().setMaxCount(MAX_ATTACHMENT_COUNT)
                    .setSelectedFiles(photos)
                    .setActivityTheme(R.style.AppTheme)
                    .enableVideoPicker(false)
                    .enableCameraSupport(true)
                    .showGifs(false)
                    .showFolderView(true)
                    .enableOrientation(true)
                    .pickPhoto(this);
        }
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void onPickDoc() {
        String[] documents = {".doc", ".docx", ".odt", ".pdf", ".xls", ".xlsx", ".ods", ".ppt", ".pptx"};
        if (imagePaths.size() == MAX_ATTACHMENT_COUNT)
            Toast.makeText(this, "Cannot select more than " + MAX_ATTACHMENT_COUNT + " documents", Toast.LENGTH_SHORT).show();
        else {
            ArrayList photos = new ArrayList();
            for (Image photo : imagePaths) {
                if (photo.getType() == FilePickerConst.REQUEST_CODE_DOC)
                    photos.add(photo.getImagePath());
            }

            FilePickerBuilder.getInstance().setMaxCount(MAX_ATTACHMENT_COUNT)
                    .setSelectedFiles(photos)
                    .setActivityTheme(R.style.AppTheme)
                    .addFileSupport(documents)
                    .enableDocSupport(false)
                    .enableOrientation(true)
                    .pickFile(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SelectedRecordsActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FilePickerConst.REQUEST_CODE_PHOTO) {
                if (data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA).size() == 0) {
                    if (imagePaths.isEmpty())
                        finish();
                } else
                    addFiles(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA), FilePickerConst.REQUEST_CODE_PHOTO);
            } else if (requestCode == FilePickerConst.REQUEST_CODE_DOC) {
                if (data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS).size() == 0) {
                    if (imagePaths.isEmpty())
                        finish();
                } else
                    addFiles(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS), FilePickerConst.REQUEST_CODE_DOC);
            }
        } else if (imagePaths.isEmpty())
            finish();
    }

    private void addFiles(ArrayList<String> data, int type) {
        for (String imagePath : data) {
            boolean isExist = false;
            for (Image imagePre : imagePaths) {
                if (imagePre.getImagePath().equals(imagePath))
                    isExist = true;
            }

            if (!isExist) {
                Image image = new Image();
                image.setImageId(patientId + "_" + UUID.randomUUID().toString());
                image.setImagePath(imagePath);
                image.setType(type);
                image.setSelected(false);
                imagePaths.add(image);
            }
        }
        selectedRecordsAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.coachmark, R.id.uploadButton, R.id.addImageView, R.id.backImageView})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.coachmark:
              /*  coachmark.setVisibility(View.GONE);
                RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.COACHMARK, RescribeConstants.YES, mContext);*/
                break;
            case R.id.uploadButton:
                if (imagePaths.isEmpty()) {
                    CommonMethods.showToast(SelectedRecordsActivity.this, getResources().getString(R.string.select_report));
                } else {
                    if (NetworkUtil.isInternetAvailable(SelectedRecordsActivity.this)) {
                        uploadButton.setEnabled(false);

                        count = 0;

                        for (int parentIndex = 0; parentIndex < imagePaths.size(); parentIndex++) {
                            uploadImage(parentIndex + "", imagePaths.get(parentIndex));
                        }
                    } else
                        CommonMethods.showToast(SelectedRecordsActivity.this, getResources().getString(R.string.internet));
                }
                break;
            case R.id.addImageView:
                dialog.show();
                break;
            case R.id.backImageView:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onClickOfCaptionEditext() {

    }

    @Override
    public void onClickOfCrossImage(int position) {

        imagePaths.remove(position);
        selectedRecordsAdapter.notifyDataSetChanged();
    }

    @Override
    public void uploadImage(String uploadId, Image image) {
        try {

            if (image.getParentCaption() == null || image.getChildCaption().equals(""))
                image.setChildCaption(OTHERS);
            if (mOpdtime.equals("")) {
                currentOpdTime = CommonMethods.getCurrentTimeStamp(RescribeConstants.DATE_PATTERN.HH_mm_ss);
            } else {
                currentOpdTime = mOpdtime;
            }
            String visitDateToPass = CommonMethods.getFormattedDate(visitDate, RescribeConstants.DATE_PATTERN.DD_MM_YYYY, RescribeConstants.DATE_PATTERN.YYYY_MM_DD);
            if (!image.getParentCaption().equals("")) {
                MultipartUploadRequest uploadRequest = new MultipartUploadRequest(SelectedRecordsActivity.this, uploadId, Url)
                        .setNotificationConfig(uploadNotificationConfig)
                        .setMaxRetries(RescribeConstants.MAX_RETRIES)

                        .addHeader(RescribeConstants.AUTHORIZATION_TOKEN, authorizationString)
                        .addHeader(RescribeConstants.DEVICEID, device.getDeviceId())
                        .addHeader(RescribeConstants.OS, device.getOS())
                        .addHeader(RescribeConstants.OSVERSION, device.getOSVersion())
                        .addHeader(RescribeConstants.DEVICE_TYPE, device.getDeviceType())

                        .addHeader("patientid", patientId)
                        .addHeader("docid", String.valueOf(docId))
                        .addHeader("opddate", visitDateToPass)
                        .addHeader("opdtime", currentOpdTime)
                        .addHeader("opdid", opdId)
                        .addHeader("hospitalid", mHospitalId)
                        .addHeader("hospitalpatid", mHospitalPatId)
                        .addHeader("locationid", mLocationId)
                        .addHeader("captionname", image.getParentCaption())
                   /* .addHeader("imageId", image.getImageId())*/
                   /* .addHeader("parentCaptionName", image.getParentCaption())*/

                        .addFileToUpload(image.getImagePath(), "attachment");
                uploadRequest.startUpload();
            } else {
                MultipartUploadRequest uploadRequest = new MultipartUploadRequest(SelectedRecordsActivity.this, uploadId, Url)
                        .setNotificationConfig(uploadNotificationConfig)
                        .setMaxRetries(RescribeConstants.MAX_RETRIES)

                        .addHeader(RescribeConstants.AUTHORIZATION_TOKEN, authorizationString)
                        .addHeader(RescribeConstants.DEVICEID, device.getDeviceId())
                        .addHeader(RescribeConstants.OS, device.getOS())
                        .addHeader(RescribeConstants.OSVERSION, device.getOSVersion())
                        .addHeader(RescribeConstants.DEVICE_TYPE, device.getDeviceType())
                        .addHeader("patientid", patientId)
                        .addHeader("docid", String.valueOf(docId))
                        .addHeader("opddate", visitDateToPass)
                        .addHeader("opdtime", currentOpdTime)
                        .addHeader("opdid", opdId)
                        .addHeader("hospitalid", mHospitalId)
                        .addHeader("hospitalpatid", mHospitalPatId)
                        .addHeader("locationid", mLocationId)

                   /* .addHeader("imageId", image.getImageId())*/
                   /* .addHeader("parentCaptionName", image.getParentCaption())*/

                        .addFileToUpload(image.getImagePath(), "attachment");
                uploadRequest.startUpload();
            }


        } catch (FileNotFoundException | MalformedURLException e) {
            e.printStackTrace();
        }

        //  appDBHelper.insertMyRecordsData(uploadId, RescribeConstants.UPLOADING, new Gson().toJson(image), docId, opdId, visitDate);
    }

    @Override
    public void onBackPressed() {
        /*if (!uploadButton.isEnabled()) {
            Intent intent = new Intent(SelectedRecordsActivity.this, HomePageActivity.class);
            intent.putExtra(RescribeConstants.ALERT, false);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }*/
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        broadcastReceiver.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        broadcastReceiver.unregister(this);
    }

    private UploadServiceBroadcastReceiver broadcastReceiver = new UploadServiceBroadcastReceiver() {
        @Override
        public void onProgress(Context context, UploadInfo uploadInfo) {
          /* String pos[] = uploadInfo.getUploadId().split("_");
            int finalI = Integer.parseInt(pos[0]);
            int finalJ = Integer.parseInt(pos[1]);

            if (groups.get(finalI).getImages().get(finalJ).isUploading() != RescribeConstants.UPLOADING) {
                groups.get(finalI).getImages().get(finalJ).setUploading(RescribeConstants.UPLOADING);
                uploadButton.setEnabled(false);
                mAdapter.notifyDataSetChanged();
            }

            CommonMethods.Log(IMAGEDUPLOADID, uploadInfo.getUploadId() + " onProgress " + uploadInfo.getProgressPercent());*/
        }

        @Override
        public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {

          /*  appDBHelper.updateMyRecordsData(uploadInfo.getUploadId(), RescribeConstants.FAILED);

       String pos[] = uploadInfo.getUploadId().split("_");
            int finalI = Integer.parseInt(pos[0]);
            int finalJ = Integer.parseInt(pos[1]);

            groups.get(finalI).getImages().get(finalJ).setUploading(RescribeConstants.FAILED);
            mAdapter.notifyItemChanged(finalI);
            CommonMethods.Log(IMAGEDUPLOADID, uploadInfo.getUploadId() + " onError");

            navigate();*/

          count+=1;

            if (imagePaths.size() == count){
                allDone();
            }
        }

        @Override
        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {

            count+=1;

            if (imagePaths.size() == count){
                allDone();
            }


          /*  appDBHelper.updateMyRecordsData(uploadInfo.getUploadId(), RescribeConstants.COMPLETED);

            String pos[] = uploadInfo.getUploadId().split("_");
            int finalI = Integer.parseInt(pos[0]);
            int finalJ = Integer.parseInt(pos[1]);

            groups.get(finalI).getImages().get(finalJ).setUploading(RescribeConstants.COMPLETED);
            mAdapter.notifyItemChanged(finalI);

            CommonMethods.Log(IMAGEDUPLOADID, uploadInfo.getUploadId() + " onCompleted " + serverResponse.getBodyAsString());

            navigate();*/
        }

        @Override
        public void onCancelled(Context context, UploadInfo uploadInfo) {
            //   CommonMethods.Log(IMAGEDUPLOADID, uploadInfo.getUploadId() + " onCancelled");
        }
    };

    private void allDone() {
        Bundle b = new Bundle();
        b.putString(RescribeConstants.PATIENT_NAME,patientName);
        b.putString(RescribeConstants.PATIENT_INFO, patientInfo);
        b.putString(RescribeConstants.PATIENT_ID, patientId);
        b.putString(RescribeConstants.PATIENT_HOS_PAT_ID, mHospitalPatId);
        Intent intent = new Intent(mContext,PatientHistoryActivity.class);
        intent.putExtra(RescribeConstants.PATIENT_INFO, b);
        startActivity(intent);
        finish();
    }

    private void navigate() {
        // Navigate
      /* MyRecordsData myRecordsData = appDBHelper.getMyRecordsData();
        int completeCount = 0;
        for (Image image : myRecordsData.getImageArrayList()) {
            if (image.isUploading() == RescribeConstants.COMPLETED)
                completeCount++;
        }
        if (completeCount == myRecordsData.getImageArrayList().size()) {
            Intent intent = new Intent(SelectedRecordsGroupActivity.this, MyRecordsActivity.class);
            intent.putExtra(RescribeConstants.ALERT, false);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }*/
        // End Navigate
    }


}
