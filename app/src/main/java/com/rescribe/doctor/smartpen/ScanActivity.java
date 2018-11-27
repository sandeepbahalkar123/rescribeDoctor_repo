package com.rescribe.doctor.smartpen;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rescribe.doctor.R;
import com.rescribe.doctor.singleton.RescribeApplication;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.smart.pen.core.common.Listeners;
import com.smart.pen.core.model.DeviceObject;
import com.smart.pen.core.services.PenService;
import com.smart.pen.core.symbol.Keys;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScanActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_CODE = 1212;
    private Button mScanBut;
    private ListView mDeviceList;
    private TextView mEmptytext;

    @BindView(R.id.backImageView)
    ImageView backImageView;
    @BindView(R.id.titleTextView)
    CustomTextView titleTextView;

    @OnClick({R.id.backImageView})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backImageView:
                onBackPressed();
                break;
        }
    }

    private PenAdapter mPenAdapter;
    private Listeners.OnScanDeviceListener onScanDeviceListener = new Listeners.OnScanDeviceListener() {
        @Override
        public void find(DeviceObject device) {
            mPenAdapter.addItem(device);
            mPenAdapter.notifyDataSetChanged();
        }

        @Override
        public void complete(HashMap<String, DeviceObject> list) {
            mScanBut.setText("Start Scan");
            mScanBut.setEnabled(true);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        ButterKnife.bind(this);
        titleTextView.setText(getResources().getText(R.string.scan_activity));
        mPenAdapter = new PenAdapter(this);

        mEmptytext = (TextView) findViewById(R.id.emptytext);
        mDeviceList = (ListView) findViewById(R.id.listview);
        mDeviceList.setEmptyView(mEmptytext);
        mDeviceList.setAdapter(mPenAdapter);
        mDeviceList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                //停止搜索
                PenService service = RescribeApplication.getInstance().getPenService();
                if (service != null) {
                    service.stopScanDevice();
                }

                DeviceObject item = mPenAdapter.getItem(arg2);

                Intent intent = getIntent();
                intent.setClass(ScanActivity.this, PenInfoActivity.class);
                intent.putExtra(Keys.KEY_DEVICE_ADDRESS, item.address);
                intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(intent);
                finish();
            }
        });

        mScanBut = (Button) findViewById(R.id.scanBut);
        mScanBut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        + ContextCompat.checkSelfPermission(
                        ScanActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        + ContextCompat.checkSelfPermission(
                        ScanActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                } else {
                    scanButton();
                }
            }
        });
    }

    private void scanButton() {
        mScanBut.setText("Scanning...");
        mScanBut.setEnabled(false);
        mPenAdapter.clearItems();
        mPenAdapter.notifyDataSetChanged();

        PenService service = RescribeApplication.getInstance().getPenService();
        if (service != null) {
            service.scanDevice(onScanDeviceListener);
        }
    }

    protected void checkPermission() {

        // Do something, when permissions not granted
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.ACCESS_COARSE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // If we should give explanation of requested permissions

            ActivityCompat.requestPermissions(
                    ScanActivity.this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    MY_PERMISSIONS_REQUEST_CODE
            );

        } else {
            // Directly request for required permissions, without explanation
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    MY_PERMISSIONS_REQUEST_CODE
            );
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
                    scanButton();
                    // Permissions are granted
//                    Toast.makeText(this, "Permissions granted.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
