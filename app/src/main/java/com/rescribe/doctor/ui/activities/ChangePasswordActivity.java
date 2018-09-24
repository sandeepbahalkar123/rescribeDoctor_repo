package com.rescribe.doctor.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.rescribe.doctor.R;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.util.CommonMethods;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangePasswordActivity extends AppCompatActivity {

    @BindView(R.id.oldPasswordEditText)
    EditText oldPasswordEditText;
    @BindView(R.id.newPasswordEditText)
    EditText newPasswordEditText;
    @BindView(R.id.reEnterNewPassword)
    EditText reEnterNewPassword;
    @BindView(R.id.okButton)
    Button okButton;
    @BindView(R.id.backImageView)
    ImageView backImageView;
    @BindView(R.id.titleTextView)
    CustomTextView titleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        backImageView.setVisibility(View.GONE);
        titleTextView.setText(getString(R.string.change_password));
    }

    @OnClick(R.id.okButton)
    public void onViewClicked() {
        validate();
    }

    private void validate() {
        if (oldPasswordEditText.getText().toString().isEmpty()) {
            CommonMethods.showToast(this, "Please enter old password.");
            return;
        } else if (oldPasswordEditText.getText().toString().length() < 6) {
            CommonMethods.showToast(this, getString(R.string.error_too_small_password));
            return;
        }

        if (newPasswordEditText.getText().toString().isEmpty()) {
            CommonMethods.showToast(this, "Please enter new password.");
            return;
        } else if (newPasswordEditText.getText().toString().length() < 6) {
            CommonMethods.showToast(this, getString(R.string.error_too_small_password));
            return;
        }

        if (reEnterNewPassword.getText().toString().isEmpty()) {
            CommonMethods.showToast(this, "Please re-enter new password.");
            return;
        } else if (reEnterNewPassword.getText().toString().length() < 6) {
            CommonMethods.showToast(this, getString(R.string.error_too_small_password));
            return;
        }

        if (!newPasswordEditText.getText().toString().equals(reEnterNewPassword.getText().toString())) {
            CommonMethods.showToast(this, "Password not match.");
            return;
        }

        callChangePasswordApi();
    }

    private void callChangePasswordApi() {

    }
}
