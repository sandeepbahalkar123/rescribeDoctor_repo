package com.rescribe.doctor.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.rescribe.doctor.R;
import com.rescribe.doctor.helpers.login.LoginHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.CommonBaseModelContainer;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.util.CommonMethods;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.rescribe.doctor.util.RescribeConstants.CHANGE_PASSWORD;
import static com.rescribe.doctor.util.RescribeConstants.SUCCESS;

public class ChangePasswordActivity extends AppCompatActivity implements HelperResponse {

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
    private LoginHelper loginHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        backImageView.setVisibility(View.VISIBLE);
        titleTextView.setText(getString(R.string.change_password));
        loginHelper = new LoginHelper(this, this);

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
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
            CommonMethods.showToast(this, "Password do not match.");
            return;
        }

        loginHelper.changePassword(oldPasswordEditText.getText().toString(), newPasswordEditText.getText().toString());
    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        if (CHANGE_PASSWORD.equals(mOldDataTag)){
            if (customResponse instanceof CommonBaseModelContainer){
                CommonBaseModelContainer commonBaseModelContainer = (CommonBaseModelContainer) customResponse;
                if (commonBaseModelContainer.getCommonRespose().getStatusCode().equals(SUCCESS))
                    onBackPressed();

                CommonMethods.showToast(this, commonBaseModelContainer.getCommonRespose().getStatusMessage());
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
