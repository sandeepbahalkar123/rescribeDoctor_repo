package com.rescribe.doctor.ui.activities.zoom_images;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.rescribe.doctor.R;
import com.rescribe.doctor.model.case_details.VisitCommonData;
import com.rescribe.doctor.singleton.RescribeApplication;
import com.rescribe.doctor.smartpen.PenInfoActivity;
import com.rescribe.doctor.smartpen.ScanActivity;
import com.rescribe.doctor.ui.customesViews.zoomview.ZoomageView;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;
import com.smart.pen.core.services.PenService;
import com.smart.pen.core.symbol.ConnectState;
import com.smart.pen.core.symbol.Keys;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MultipleImageWithSwipeAndZoomActivity extends AppCompatActivity {

    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.titleText)
    TextView titleText;

    @BindView(R.id.backButton)
    AppCompatImageView backButton;
    @BindView(R.id.editButton)
    Button editButton;

    private boolean isFromNotes;
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        setContentView(R.layout.multi_image_with_swipe_and_zoom);
        ButterKnife.bind(this);
        mContext = this;
        mHandler = new Handler();

        final ArrayList<VisitCommonData> parcelableArrayListExtra = getIntent().getParcelableArrayListExtra(RescribeConstants.ATTACHMENTS_LIST);

        String url = getIntent().getStringExtra(RescribeConstants.DOCUMENTS);
        isFromNotes = getIntent().getBooleanExtra(RescribeConstants.START_FROM_NOTE, false);
        if (isFromNotes)
            editButton.setVisibility(View.VISIBLE);
        else editButton.setVisibility(View.GONE);

        //------------
        int clickedImage = 0;
        for (int i = 0; i < parcelableArrayListExtra.size(); i++) {
            if (parcelableArrayListExtra.get(i).getUrl().equalsIgnoreCase(url)) {
                clickedImage = i;
                break;
            }
        }
        //------------

        CustomPagerAdapter mCustomPagerAdapter = new CustomPagerAdapter(this, parcelableArrayListExtra);
        pager.setAdapter(mCustomPagerAdapter);
        pager.setCurrentItem(clickedImage);

        titleText.setText(parcelableArrayListExtra.get(clickedImage).getName());
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                titleText.setText(parcelableArrayListExtra.get(position).getName());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @OnClick({R.id.backButton, R.id.editButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backButton:
                onBackPressed();
                break;
            case R.id.editButton:
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (!mBluetoothAdapter.isEnabled()) {
                    if (!mBluetoothAdapter.enable())
                        Toast.makeText(this, "Please Turn on bluetooth.", Toast.LENGTH_SHORT).show();
                }
                openSmartPen();
                break;
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

                Intent intent = getIntent();
                intent.setClass(mContext, PenInfoActivity.class);
                intent.putExtra(RescribeConstants.SELECTED_INDEX, pager.getCurrentItem());
                startActivityForResult(intent, 323);

            } else {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismissProgressDialog();
                        if (Keys.APP_PEN_SERVICE_NAME.equals(svrName)) {
                            Intent intent = getIntent();
                            intent.setClass(mContext, ScanActivity.class);
                            intent.putExtra(RescribeConstants.SELECTED_INDEX, pager.getCurrentItem());
                            startActivityForResult(intent, 323);
                        } /*else if (Keys.APP_USB_SERVICE_NAME.equals(svrName)) {
                        Intent intent = new Intent(mContext, PenInfoActivity.class);
                        intent.putExtra(Keys.KEY_VALUE, svrName);
                        startActivity(intent);
                    }*/
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

    private class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;
        RequestOptions requestOptions;
        private ArrayList<VisitCommonData> visitCommonData;

        public CustomPagerAdapter(Context context, ArrayList<VisitCommonData> visitCommonData) {
            mContext = context;
            this.visitCommonData = visitCommonData;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            requestOptions = new RequestOptions();
            requestOptions.placeholder(droidninja.filepicker.R.drawable.image_placeholder);
            requestOptions.error(droidninja.filepicker.R.drawable.image_placeholder);
        }

        @Override
        public int getCount() {
            return visitCommonData.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((ZoomageView) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.item_zoom_image_view, container, false);

            VisitCommonData visitCommonData = this.visitCommonData.get(position);

            ZoomageView zoomView = (ZoomageView) itemView.findViewById(R.id.zoomView);

            String tag = visitCommonData.getUrl();
            String fileExtension = tag.substring(tag.lastIndexOf("."));

            Glide.with(mContext).load(tag)
                    .apply(requestOptions)
                    .into(zoomView);

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ZoomageView) object);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == 323){
                finish();
            }
        }
    }
}