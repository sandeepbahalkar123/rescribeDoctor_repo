package com.rescribe.doctor.smartpen;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.rescribe.doctor.R;
import com.rescribe.doctor.singleton.RescribeApplication;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.smart.pen.core.common.Listeners;
import com.smart.pen.core.model.PointObject;
import com.smart.pen.core.services.PenService;
import com.smart.pen.core.services.SmartPenService;
import com.smart.pen.core.symbol.LocationState;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Xiaoz
 * @date 2015年8月10日 上午9:59:41
 * <p>
 * Description
 */
public class SettingSizeActivity extends AppCompatActivity {
    public static final String TAG = SettingSizeActivity.class.getSimpleName();

    private PenService mPenService;
    private PointObject currPointObject;

    private ImageView mTitleImg;
    private TextView mTitleMsg;
    private TextView mSizeWidth;
    private TextView mSizeHeight;
    private Button mBackBut;
    private Button mResetBut;

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

    private Listeners.OnFixedPointListener onFixedPointListener = new Listeners.OnFixedPointListener() {
        @Override
        public void location(PointObject first, PointObject second,
                             LocationState state) {
            Log.v(TAG, "location state:" + state);
            switch (state) {
                case FirstComp:
                    mTitleMsg.setText(R.string.setting_size_step2);
                    mTitleImg.setImageResource(R.drawable.arrow_right);

                    int width = currPointObject.originalX - first.originalX;
                    int height = currPointObject.originalY - first.originalY;
                    //第二个点只能在右下角出现
                    if (width < SmartPenService.SETTING_SIZE_MIN
                            || height < SmartPenService.SETTING_SIZE_MIN) {
                        mSizeWidth.setText("-");
                        mSizeHeight.setText("-");
                    } else {
                        mSizeWidth.setText(String.valueOf(width));
                        mSizeHeight.setText(String.valueOf(height));
                    }
                    mResetBut.setEnabled(true);
                    break;
                case SecondComp:
                    mTitleMsg.setText(R.string.setting_size_end);
                    mTitleImg.setImageResource(R.drawable.complete);

                    mBackBut.setEnabled(true);
                    break;
                case LocationSmall:
                    mTitleMsg.setText(R.string.setting_size_small);
                    mTitleImg.setImageResource(R.drawable.error_size);
                    mSizeWidth.setText("-");
                    mSizeHeight.setText("-");
                    break;
                default:
                    mTitleMsg.setText(R.string.setting_size_step1);
                    mTitleImg.setImageResource(R.drawable.arrow_left);
                    mSizeWidth.setText("-");
                    mSizeHeight.setText("-");
                    mResetBut.setEnabled(false);
                    mBackBut.setEnabled(false);
                    break;
            }
        }
    };
    private Listeners.OnPointChangeListener onPointChangeListener = new Listeners.OnPointChangeListener() {
        @Override
        public void change(PointObject point) {
            //Log.v(TAG, "out point:"+point.toString());
            currPointObject = point;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_setting_size);
        ButterKnife.bind(this);
        titleTextView.setText(getResources().getText(R.string.settings));

        mTitleImg = (ImageView) findViewById(R.id.titleImg);
        mTitleMsg = (TextView) findViewById(R.id.titleMsg);
        mSizeWidth = (TextView) findViewById(R.id.sizeWidth);
        mSizeHeight = (TextView) findViewById(R.id.sizeHeight);
        mBackBut = (Button) findViewById(R.id.backBut);
        mResetBut = (Button) findViewById(R.id.resetBut);


        mBackBut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                backHandler();
            }
        });

        mResetBut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mPenService.againFixedPoint();
            }
        });

        //设置笔坐标监听
        mPenService = RescribeApplication.getInstance().getPenService();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                backHandler();
                break;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPenService.setOnPointChangeListener(onPointChangeListener);
        mPenService.setOnFixedPointListener(onFixedPointListener);
    }

    @Override
    public void onPause() {
        mPenService.setOnPointChangeListener(null);
        mPenService.setOnFixedPointListener(null);

        super.onPause();
    }

    /**
     * 返回处理
     */
    private void backHandler() {
        mPenService.applyFixedPoint();

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        SettingSizeActivity.this.finish();
    }
}
