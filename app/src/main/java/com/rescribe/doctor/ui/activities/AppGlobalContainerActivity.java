package com.rescribe.doctor.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.rescribe.doctor.R;
import com.rescribe.doctor.ui.fragments.login.ForgotPassword;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jeetal on 19/5/17.
 */

public class AppGlobalContainerActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getName();
    @BindView(R.id.blankContainer)
    FrameLayout mBlankContainer;
    Context mContext;
    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank_layout);
        ButterKnife.bind(this);
        mContext = AppGlobalContainerActivity.this;
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        String header = getIntent().getStringExtra(getString(R.string.title));
        loadFragment(getIntent().getSerializableExtra(getString(R.string.details)), header);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    public void loadFragment(Serializable serializableExtra, String header) {
        //When ever this activity will be called respective function fragement will be loaded for eg .Forgotpassword according to type set through intent
        mActionBar.setTitle(header);

        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        Bundle b = new Bundle();
        if (serializableExtra != null) {
            b.putSerializable(getString(R.string.details), serializableExtra);
        }

        ForgotPassword forgotPassword = new ForgotPassword();
        forgotPassword.setArguments(b);
        fragmentTransaction.replace(R.id.blankContainer, forgotPassword);

        fragmentTransaction.commit();
    }
}
