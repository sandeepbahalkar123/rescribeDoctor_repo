package com.rescribe.doctor.smartpen;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.rescribe.doctor.R;
import com.rescribe.doctor.helpers.database.AppDBHelper;
import com.rescribe.doctor.model.UploadStatus;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.services.add_record_upload_Service.AddRecordService;
import com.rescribe.doctor.singleton.Device;
import com.rescribe.doctor.singleton.RescribeApplication;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.NetworkUtil;
import com.rescribe.doctor.util.RescribeConstants;
import com.smart.pen.core.common.Listeners;
import com.smart.pen.core.model.FrameSizeObject;
import com.smart.pen.core.model.PointObject;
import com.smart.pen.core.services.PenService;
import com.smart.pen.core.services.SmartPenService;
import com.smart.pen.core.symbol.BatteryState;
import com.smart.pen.core.symbol.ConnectState;
import com.smart.pen.core.symbol.Keys;
import com.smart.pen.core.symbol.SceneType;
import com.smart.pen.core.views.MultipleCanvasView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.rescribe.doctor.ui.activities.add_records.SelectedRecordsActivity.FILELIST;

/**
 * 笔信息显示
 *
 * @author Xiaoz
 * @date 2015年6月12日 下午3:34:58
 * <p>
 * Description
 */
public class PenInfoActivity extends AppCompatActivity {
    public static final String TAG = PenInfoActivity.class.getSimpleName();
    public static final int REQUEST_SETTING_SIZE = 1000;
    private static final String RESCRIBE_NOTES = "/DrRescribe/Notes/";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private Menu menu;
    /**
     * 笔服务广播处理
     **/

    private int bmpx = 0, bmpy = 0, bmpw = 0, bmph = 0;

    private PenServiceReceiver mPenServiceReceiver;
    private PenService mPenService;
    private ProgressDialog mProgressDialog;
    private int mShowType = 1;

    private RelativeLayout mLineFrame;
    private FrameLayout mLineWindow;

    /**
     * 笔画布
     **/
    private MultipleCanvasView mPenCanvasView;

    //笔视图
    private PenView mPenView;

    //当前设备屏幕宽度
    private int mDisplayWidth;
    //屏幕高度
    private int mDisplayHeight;
    private Listeners.OnConnectStateListener onConnectStateListener = new Listeners.OnConnectStateListener() {
        @Override
        public void stateChange(String address, ConnectState state) {
            switch (state) {
                case PEN_READY:

                    break;
                case PEN_INIT_COMPLETE:
                    dismissProgressDialog();
                    Toast.makeText(PenInfoActivity.this, R.string.initialized, Toast.LENGTH_SHORT).show();
                    initSceneType();
                    if (menu != null) {
                        MenuItem item = menu.findItem(R.id.action_disconnect);
                        if (item.getTitle().equals("Connect")) {
                            item.setTitle("Disconnect");
                        }
                    }
                    break;
                case CONNECTED:
                    /*if (menu != null) {
                        MenuItem item = menu.findItem(R.id.action_disconnect);
                        if (item.getTitle().equals("Connect")) {
                            item.setTitle("Disconnect");
                        }
                    }*/
                    break;
                case SERVICES_FAIL:
                    dismissProgressDialog();
                    alertError("The pen discovery failed, You can restart pen device bluetooth and connect again.", "Retry");
                    if (menu != null) {
                        MenuItem item = menu.findItem(R.id.action_disconnect);
                        if (!item.getTitle().equals("Connect")) {
                            item.setTitle("Connect");
                        }
                    }
                    break;
                case CONNECT_FAIL:
                    dismissProgressDialog();
                    if (menu != null) {
                        MenuItem item = menu.findItem(R.id.action_disconnect);
                        if (!item.getTitle().equals("Connect")) {
                            item.setTitle("Connect");
                        }
                    }
                    alertError("The pen connection failure, You can restart pen device bluetooth and connect again.", "Retry");
                    break;
                case DISCONNECTED:
                    dismissProgressDialog();
//                    Toast.makeText(PenInfoActivity.this, R.string.disconnected, Toast.LENGTH_SHORT).show();
                    if (menu != null) {
                        MenuItem item = menu.findItem(R.id.action_disconnect);
                        if (!item.getTitle().equals("Connect")) {
                            item.setTitle("Connect");
                        }
                    }

                    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (!mBluetoothAdapter.isEnabled())
                        alertError("Device Bluetooth is off, Please start Device Bluetooth!", "Connect");

                    break;
                default:

                    break;
            }
        }
    };
    private Listeners.OnPointChangeListener onPointChangeListener = new Listeners.OnPointChangeListener() {

        @Override
        public void change(PointObject point) {

            if (point.battery == BatteryState.LOW) {
                Toast.makeText(PenInfoActivity.this, R.string.battery_low, Toast.LENGTH_LONG).show();
            }

            //获取显示窗口比例缩放坐标
            int windowX = point.getSceneX(mPenCanvasView.getWindowWidth());
            int windowY = point.getSceneY(mPenCanvasView.getWindowHeight());

            if (mShowType != 1) return;

            //绘制笔
            mPenView.bitmapX = windowX;
            mPenView.bitmapY = windowY;
            mPenView.isRoute = point.isRoute;
            mPenView.invalidate();

            //绘制笔迹
            mPenCanvasView.drawLine(windowX, windowY, point.isRoute);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_info);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getText(R.string.draw_activity));

        bmpx = 0;
        bmpy = getToolBarHeight() + getStatusBarHeight();

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mDisplayWidth = metric.widthPixels;  // 屏幕宽度（像素）
        mDisplayHeight = metric.heightPixels - bmpy;  // 屏幕高度（像素）

        bmpw = mDisplayWidth;
        bmph = mDisplayHeight;

        mLineFrame = (RelativeLayout) findViewById(R.id.lineFrame);
        mLineWindow = (FrameLayout) findViewById(R.id.lineWindow);
        mPenCanvasView = (MultipleCanvasView) findViewById(R.id.penCanvasView);
        //添加笔视图
        mPenView = new PenView(this);
        mLineWindow.addView(mPenView);

        mPenService = RescribeApplication.getInstance().getPenService();
        if (mPenService.checkDeviceConnect() == ConnectState.CONNECTED) {
            initSceneType();
        } else {
            String address = getIntent().getStringExtra(Keys.KEY_DEVICE_ADDRESS);
            if (address != null && !address.isEmpty()) {
                connectDevice(address);
            } else {
                String isUsbSvr = getIntent().getStringExtra(Keys.KEY_VALUE);
                if (isUsbSvr != null && !isUsbSvr.isEmpty() && isUsbSvr.equals(Keys.APP_USB_SERVICE_NAME)) {
                    initSceneType();
                } else {
                    alertError("IP address error.", "Retry");
                }
            }
        }
    }

    public int getToolBarHeight() {
        int[] attrs = new int[]{R.attr.actionBarSize};
        TypedArray ta = obtainStyledAttributes(attrs);
        int toolBarHeight = ta.getDimensionPixelSize(0, -1);
        ta.recycle();
        return toolBarHeight;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pen_info, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_settings:
                initSceneType(true);
                break;
            case R.id.action_save:
                mPenView.setVisibility(View.GONE);
                saveAndOpenBitmapToImage();
                mPenView.setVisibility(View.VISIBLE);
                break;
            case R.id.action_disconnect:
                if (!item.getTitle().equals("Connect")) {
                    item.setTitle("Connect");
                    PenService service = RescribeApplication.getInstance().getPenService();
                    if (service != null) {
                        service.disconnectDevice();
                    }
                } else {
                    item.setTitle("Disconnect");
                    String address = getIntent().getStringExtra(Keys.KEY_DEVICE_ADDRESS);
                    if (address != null && !address.isEmpty()) {
                        connectDevice(address);
                    } else {
                        String isUsbSvr = getIntent().getStringExtra(Keys.KEY_VALUE);
                        if (isUsbSvr != null && !isUsbSvr.isEmpty() && isUsbSvr.equals(Keys.APP_USB_SERVICE_NAME)) {
                            initSceneType();
                        } else {
                            alertError("IP address error.", "Retry");
                        }
                    }
                }
                break;
        }
        return true;
    }

    private void saveAndOpenBitmapToImage() {
        Date now = new Date();
        String time = String.valueOf(android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now));

        try {
            // image naming and path  to include sd card  appending name you choose for file

            String mPath = Environment.getExternalStorageDirectory().toString() + RESCRIBE_NOTES;
            File dirFilesFolder = new File(mPath);
            if (!dirFilesFolder.exists()) {
                if (dirFilesFolder.mkdirs()) {
                    Log.i(TAG, mPath + " Directory Created");
                }
            }
            mPath = mPath + time + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache(), bmpx, bmpy, bmpw, bmph);
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            //alertError("File saved to " + mPath);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);

            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".droidninja.filepicker.provider", new File(mPath));
            } else {
                uri = Uri.fromFile(new File(mPath));
            }

//            intent.setDataAndType(uri, "image/*");
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            startActivity(intent);


            uploadNote(mPath);

        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    private void uploadNote(String path) {

        // get Params

        String mHospitalPatId = getIntent().getStringExtra(RescribeConstants.PATIENT_HOS_PAT_ID);
        String mLocationId = getIntent().getStringExtra(RescribeConstants.LOCATION_ID);
        String patientId = getIntent().getStringExtra(RescribeConstants.PATIENT_ID);
        int mAptId = getIntent().getIntExtra(RescribeConstants.APPOINTMENT_ID, 0);
        int mHospitalId = getIntent().getIntExtra(RescribeConstants.CLINIC_ID, 0);

//        String patientName = getIntent().getStringExtra(RescribeConstants.PATIENT_NAME);
//        String patientInfo = getIntent().getStringExtra(RescribeConstants.PATIENT_INFO);
        String mOpdtime = getIntent().getStringExtra(RescribeConstants.OPD_TIME);
        String opdId = getIntent().getStringExtra(RescribeConstants.OPD_ID);

        String visitDate = getIntent().getStringExtra(RescribeConstants.VISIT_DATE);

        int docId = Integer.parseInt(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, this));

        AppDBHelper appDBHelper = new AppDBHelper(this);
        Device device = Device.getInstance(this);
        String authorizationString = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.AUTHTOKEN, this);

        ////////////

        ArrayList<UploadStatus> uploadDataList = new ArrayList<>();

        String uploadId = System.currentTimeMillis() + "_" + 0 + "_" + patientId;
        appDBHelper.insertRecordUploads(uploadId, patientId, docId, visitDate, mOpdtime, opdId, String.valueOf(mHospitalId), mHospitalPatId, mLocationId, "", path, mAptId, RescribeConstants.NOTES, "", "");

        String currentOpdTime;
        if (mOpdtime.equals(""))
            currentOpdTime = CommonMethods.getCurrentTimeStamp(RescribeConstants.DATE_PATTERN.HH_mm_ss);
        else
            currentOpdTime = mOpdtime;

        String visitDateToPass = CommonMethods.getFormattedDate(visitDate, RescribeConstants.DATE_PATTERN.DD_MM_YYYY, RescribeConstants.DATE_PATTERN.YYYY_MM_DD);

        HashMap<String, String> headers = new HashMap<>();
        headers.put(RescribeConstants.AUTHORIZATION_TOKEN, authorizationString);
        headers.put(RescribeConstants.DEVICEID, device.getDeviceId());
        headers.put(RescribeConstants.OS, device.getOS());
        headers.put(RescribeConstants.OSVERSION, device.getOSVersion());
        headers.put(RescribeConstants.DEVICE_TYPE, device.getDeviceType());
        headers.put("patientid", patientId);
        headers.put("docid", String.valueOf(docId));
        headers.put("opddate", visitDateToPass);
        headers.put("opdtime", currentOpdTime);
        headers.put("opdid", opdId);
        headers.put("hospitalid", String.valueOf(mHospitalId));
        headers.put("hospitalpatid", mHospitalPatId);
        headers.put("locationid", mLocationId);
        headers.put("aptid", String.valueOf(mAptId));

        // Added in 5 to 6 update
        headers.put("fileid", "0");
        headers.put("orderid", "1");

        UploadStatus uploadStatus = new UploadStatus(uploadId, visitDate, mOpdtime, "", path, RescribeConstants.NOTES, headers);
        uploadDataList.add(uploadStatus);

        if (NetworkUtil.isInternetAvailable(this))
            uploadImage(uploadDataList);
        else
            CommonMethods.showToast(this, getString(R.string.records_will_upload_when_internet_available));

        onBackPressed();
    }

    public void uploadImage(ArrayList<UploadStatus> images) {
        Intent intent = new Intent(this, AddRecordService.class);
        intent.putParcelableArrayListExtra(FILELIST, images);
        ContextCompat.startForegroundService(this, intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_SETTING_SIZE) {
                Log.v(TAG, "onActivityResult:" + REQUEST_SETTING_SIZE);

                initSceneType();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mPenService != null) {
            //设置笔坐标监听
            mPenService.setOnPointChangeListener(onPointChangeListener);
        } else {
            //注册笔服务通过广播方式发送的笔迹坐标信息
            mPenServiceReceiver = new PenServiceReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Keys.ACTION_SERVICE_SEND_POINT);
            registerReceiver(mPenServiceReceiver, intentFilter);
        }
    }

    @Override
    public void onPause() {
        if (mPenService != null) {
            mPenService.setOnPointChangeListener(null);
        } else {
            unregisterReceiver(mPenServiceReceiver);
        }
        super.onPause();
    }

    /*@Override
    protected void onDestroy() {
        //断开设备
        PenService service = RescribeApplication.getInstance().getPenService();
        if (service != null) {
            service.disconnectDevice();
        }

        super.onDestroy();
    }*/

    private void initPage() {
        PenService service = RescribeApplication.getInstance().getPenService();

        //设置画布尺寸信息
        FrameSizeObject sizeObj = new FrameSizeObject();

        sizeObj.frameWidth = mDisplayWidth;
        sizeObj.frameHeight = mDisplayHeight;

        sizeObj.sceneWidth = service.getSceneWidth();
        sizeObj.sceneHeight = service.getSceneHeight();

        sizeObj.initWindowSize();

        Log.v(TAG, "sceneWidth:" + sizeObj.sceneWidth + ",sceneHeight:" + sizeObj.sceneHeight);
        Log.v(TAG, "DisplayWidth:" + mDisplayWidth + ",DisplayHeight:" + mDisplayHeight);
        Log.v(TAG, "windowWidth:" + sizeObj.windowWidth + ",windowHeight:" + sizeObj.windowHeight);
        Log.v(TAG, "windowLeft:" + sizeObj.windowLeft + ",windowTop:" + sizeObj.windowTop);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(sizeObj.windowWidth, sizeObj.windowHeight);
        params.setMargins(sizeObj.windowLeft, sizeObj.windowTop, 0, 0);
        mLineWindow.setLayoutParams(params);
        mPenCanvasView.setPenModel(MultipleCanvasView.PenModel.Pen);
        mPenCanvasView.setSize(sizeObj.windowWidth, sizeObj.windowHeight);
    }

    /**
     * 初始化纸张尺寸
     */
    private void initSceneType() {
        initSceneType(false);
    }

    /**
     * 初始化纸张尺寸
     *
     * @param isShow 是否强制显示
     */
    private void initSceneType(boolean isShow) {
        SceneType sceneType = mPenService.getSceneType();
        if (sceneType == SceneType.NOTHING || isShow) {
            final String[] menus = new String[]{"A4 (portrait)", "A4 (horizontal)", "A5 (portrait)", "A5 (horizontal)"/*, "Customize"*/};

            Builder alert = new Builder(this, R.style.AlertDialogTheme);
            alert.setTitle(R.string.select_please);
            alert.setItems(menus, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    switch (which) {
                        case 0:
                            mPenService.setSceneType(SceneType.A4);
                            break;
                        case 1:
                            mPenService.setSceneType(SceneType.A4_horizontal);
                            break;
                        case 2:
                            mPenService.setSceneType(SceneType.A5);
                            break;
                        case 3:
                            mPenService.setSceneType(SceneType.A5_horizontal);
                            break;
                        /*case 4:
                            //mSmartPenService.setSceneType(SceneType.CUSTOM);
                            Intent intent = new Intent();
                            intent.setClass(PenInfoActivity.this, SettingSizeActivity.class);
                            PenInfoActivity.this.startActivityForResult(intent, REQUEST_SETTING_SIZE);
                            return;*/
                    }
                    initPage();
                }
            });

            alert.setNegativeButton(R.string.canceled, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    initSceneType();
                }
            });
            alert.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    initSceneType();
                }
            });
            alert.show();
        } else {
            initPage();
        }
    }

    private void alertError(String msg, String buttonName) {
        Builder alert = new Builder(this, R.style.MyDialogTheme);
        alert.setTitle("Warning");
        alert.setMessage(msg);
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PenInfoActivity.this.finish();
            }
        });
        alert.setPositiveButton(buttonName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String address = getIntent().getStringExtra(Keys.KEY_DEVICE_ADDRESS);
                if (address != null && !address.isEmpty()) {
                    connectDevice(address);
                } else {
                    String isUsbSvr = getIntent().getStringExtra(Keys.KEY_VALUE);
                    if (isUsbSvr != null && !isUsbSvr.isEmpty() && isUsbSvr.equals(Keys.APP_USB_SERVICE_NAME)) {
                        initSceneType();
                    } else {
                        alertError("IP address error.", "Retry");
                    }
                }
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

    private void connectDevice(final String address) {
        final PenService service = RescribeApplication.getInstance().getPenService();

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.enable())
                Toast.makeText(this, "Please Turn on bluetooth.", Toast.LENGTH_SHORT).show();
            else {
                mProgressDialog = ProgressDialog.show(PenInfoActivity.this, "", getString(R.string.initializing), true);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("PEN_ADDRESS", address);
                        if (service != null) {
                            ConnectState state = ((SmartPenService) service).connectDevice(onConnectStateListener, address);
                            if (state != ConnectState.CONNECTING) {
                                dismissProgressDialog();
                                alertError("The pen connection failure, You can restart pen device bluetooth and connect again.", "Retry");
                            }
                        }
                    }
                }, 500);
            }
        } else {
            Log.i("PEN_ADDRESS", address);
            if (service != null) {
                ConnectState state = ((SmartPenService) service).connectDevice(onConnectStateListener, address);
                if (state != ConnectState.CONNECTING) {
                    alertError("The pen connection failure, You can restart pen device bluetooth and connect again.", "Retry");
                } else {
                    mProgressDialog = ProgressDialog.show(PenInfoActivity.this, "", getString(R.string.initializing), true);
                }
            }
        }
    }


    // Handle the pen coordinate information sent by the pen service by broadcast
    //The example is only used as a demo to have this feature, there is no special requirement to delete the following code
    private class PenServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Keys.ACTION_SERVICE_SEND_POINT)) {
                //广播的形式接收笔迹信息
                String pointJson = intent.getStringExtra(Keys.KEY_PEN_POINT);
                if (pointJson != null && !pointJson.isEmpty()) {

                    Toast.makeText(PenInfoActivity.this, pointJson, Toast.LENGTH_SHORT).show();
                    //Log.v(TAG, "pointJson:"+pointJson);

                    //更新笔坐标信息
                    //如果注册了service.setOnPointChangeListener监听，那么请注释掉下面的代码，否则信息会冲突
                    //反之如果需要使用Receiver，那么就不要使用setOnPointChangeListener
                    //PointObject item = new PointObject(pointJson);
                    //onPointChangeListener.change(item);
                }
                return;
            }
        }
    }
}
	 
