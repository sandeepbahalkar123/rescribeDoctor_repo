package com.rescribe.doctor.ui.activities.my_patients;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.rescribe.doctor.R;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.Config;
import com.rescribe.doctor.util.RescribeConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddNewPatientWebViewActivity extends AppCompatActivity {

    @BindView(R.id.webViewLayout)
    WebView mWebViewObject;
    @BindView(R.id.backButton)
    AppCompatImageView backButton;
    @BindView(R.id.webViewTitle)
    TextView mWebViewTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getBundleExtra(RescribeConstants.PATIENT_DETAILS);

        String urlData = Config.ADD_NEW_PATIENT_WEB_URL + Integer.valueOf(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, this)) + "/" +
                extras.getString(RescribeConstants.CLINIC_ID) + "/" + extras.getString(RescribeConstants.LOCATION_ID) + "/" + extras.getString(RescribeConstants.CITY_ID);

        mWebViewTitle.setText(getString(R.string.new_patients));
        loadWebViewData(urlData);
    }

    @OnClick(R.id.backButton)
    public void back() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (mWebViewObject.canGoBack()) {
            mWebViewObject.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private void loadWebViewData(String url) {
        if (url != null) {
            mWebViewObject.setVisibility(View.VISIBLE);

            WebSettings webSettings = mWebViewObject.getSettings();

            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setBuiltInZoomControls(true);
            webSettings.setSupportZoom(true);
            webSettings.setDefaultTextEncodingName("utf-8");

            mWebViewObject.setWebChromeClient(new WebChromeClient() {
                public void onProgressChanged(WebView view, int progress) {
                    // Activities and WebViews measure progress with different scales.
                    // The progress meter will automatically disappear when we reach 100%
                    setProgress(progress * 1000);
                }
            });

            renderWebPage(url);

            mWebViewObject.loadUrl(url);
        }
    }

    // Custom method to render a web page
    protected void renderWebPage(String urlToRender) {
        mWebViewObject.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // Do something on page loading started
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // Do something when page loading finished

            }

            @Override
            public void onLoadResource(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onLoadResource(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url.toLowerCase().contains(Config.ADD_NEW_PATIENT_WEB_URL_SUCCESS)) {
                    finish();
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }
}
