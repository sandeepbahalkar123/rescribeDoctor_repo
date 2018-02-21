package com.rescribe.doctor.ui.activities.add_records;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.philliphsu.bottomsheetpickers.date.DatePickerDialog;
import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.add_records.DoctorSpinnerAdapter;
import com.rescribe.doctor.helpers.myrecords.MyRecordsHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.my_records.AddDoctorModel;
import com.rescribe.doctor.model.my_records.MyRecordsDoctorListModel;
import com.rescribe.doctor.model.my_records.RequestAddDoctorModel;
import com.rescribe.doctor.model.my_records.VisitDate;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.ui.customesViews.CircularImageView;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by jeetal on 13/2/18.
 */
@RuntimePermissions
public class AddRecordsActivity extends AppCompatActivity implements DoctorSpinnerAdapter.TextEnterListener, DatePickerDialog.OnDateSetListener, GoogleApiClient.OnConnectionFailedListener, HelperResponse {

    @BindView(R.id.selectDoctorName)
    AutoCompleteTextView mSelectDoctorName;
    @BindView(R.id.clearButton)
    ImageView clearButton;
    @BindView(R.id.selectDateTextView)
    TextView selectDate;
    @BindView(R.id.uploadButton)
    Button uploadButton;
    @BindView(R.id.doctorImage)
    CircularImageView doctorImage;
    @BindView(R.id.doctorSpecialist)
    TextView doctorSpecialist;
    @BindView(R.id.doctorName)
    TextView doctorName;
    @BindView(R.id.doctorAddress)
    TextView doctorAddress;
    @BindView(R.id.dropdownLayout)
    RelativeLayout dropdownLayout;
    @BindView(R.id.searchButton)
    ImageView searchButton;
    @BindView(R.id.autocompleteLayout)
    RelativeLayout autocompleteLayout;
    @BindView(R.id.selectDateSpinner)
    Spinner selectDateSpinner;
    @BindView(R.id.dateSpinnerLayout)
    RelativeLayout dateSpinnerLayout;
    @BindView(R.id.dateIcon)
    ImageView dateIcon;
    @BindView(R.id.selectDateLayout)
    RelativeLayout selectDateLayout;
    @BindView(R.id.selectAddressLayout)
    RelativeLayout selectAddressLayout;
    @BindView(R.id.selectAddressText)
    EditText selectAddressText;
    @BindView(R.id.addressIcon)
    ImageView addressIcon;
    @BindView(R.id.backImageView)
    ImageView backImageView;
    @BindView(R.id.titleTextView)
    CustomTextView titleTextView;
    @BindView(R.id.userInfoTextView)
    CustomTextView userInfoTextView;
    @BindView(R.id.dateTextview)
    CustomTextView dateTextview;

    private int PLACE_PICKER_REQUEST = 1;

    private boolean isManual = true;

    private Context mContext;
    private DoctorSpinnerAdapter doctorSpinnerAdapter;
    private DatePickerDialog datePickerDialog;
    private String mSelectDoctorString = "";
    private String mSelectDateString = "Select Date";
    private boolean isDatesThere = false;
    private String visitDate;
    private int doctorId = -1;
    private int opdId;
    private int mSelectedId;
    private MyRecordsHelper myRecordsHelper;
    private ColorGenerator mColorGenerator;
    private int mImageSize;
    private String mSelectDatePicker = "";
    private String doctorNameString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_records);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ButterKnife.bind(this);
        initialize();
    }

    private void initialize() {
        mContext = AddRecordsActivity.this;
        mColorGenerator = ColorGenerator.MATERIAL;
        titleTextView.setText(getString(R.string.addrecords));
        setColumnNumber(mContext, 2);
        myRecordsHelper = new MyRecordsHelper(mContext, this);
        myRecordsHelper.getDoctorList(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.PATIENT_ID, mContext));
        // HardCoded
//        myRecordsHelper.getDoctorList("4092");

        Calendar now = Calendar.getInstance();
// As of version 2.3.0, `BottomSheetDatePickerDialog` is deprecated.
        datePickerDialog = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setAccentColor(getResources().getColor(R.color.tagColor));
        datePickerDialog.setMaxDate(Calendar.getInstance());

        // Places

        new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

    }

    @OnClick({R.id.backImageView,R.id.clearButton, R.id.selectDateTextView, R.id.dateIcon, R.id.uploadButton, R.id.searchButton, R.id.addressIcon})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.clearButton:
                mSelectDoctorName.setText("");
                selectDate.setText("");
                dropdownLayout.setVisibility(View.GONE);
                autocompleteLayout.setVisibility(View.VISIBLE);
                dateSpinnerLayout.setVisibility(View.GONE);
                selectDateLayout.setVisibility(View.VISIBLE);
                selectAddressLayout.setVisibility(View.GONE);
                isManual = true;
                mSelectedId = -1;
                doctorId = -1;
                opdId = 0;
                mSelectDoctorString = "";
                mSelectDateString = getResources().getString(R.string.select_date_text);
                break;
            case R.id.selectDateTextView:
                datePickerDialog.show(getSupportFragmentManager(), getResources().getString(R.string.select_date_text));
                break;
            case R.id.dateIcon:
                datePickerDialog.show(getSupportFragmentManager(), getResources().getString(R.string.select_date_text));
                break;
            case R.id.uploadButton:
                RequestAddDoctorModel requestAddDoctorModel = new RequestAddDoctorModel();
                requestAddDoctorModel.setAddress(selectAddressText.getText().toString());
                requestAddDoctorModel.setName(mSelectDoctorName.getText().toString());
                myRecordsHelper.addDoctor(requestAddDoctorModel);
               /* if (isManual) {
                    if (mSelectDoctorName.getText().length() == 0) {
                        CommonMethods.showToast(mContext, getResources().getString(R.string.please_select_doctor_name));
                        return;
                    }
                    if (selectDate.getText().length() == 0) {
                        CommonMethods.showToast(mContext, getResources().getString(R.string.please_enter_date));
                        return;
                    }
                    if (selectAddressText.getText().length() == 0) {
                        CommonMethods.showToast(mContext, getResources().getString(R.string.please_enter_doctor_address));
                        return;
                    }
                    visitDate = mSelectDatePicker;
                } else {
                    doctorId = mSelectedId;
                    if (isDatesThere) {
                        if (mSelectDoctorString.length() == 0) {
                            CommonMethods.showToast(mContext, getResources().getString(R.string.please_select_doctor_name));
                            return;
                        }
                        if (mSelectDateString.equals(getResources().getString(R.string.select_date_text))) {
                            CommonMethods.showToast(mContext, getResources().getString(R.string.please_enter_date));
                            return;
                        }
                        visitDate = mSelectDateString;
                    } else {
                        if (mSelectDoctorString.length() == 0) {
                            CommonMethods.showToast(mContext, getResources().getString(R.string.please_select_doctor_name));
                            return;
                        }
                        if (selectDate.getText().length() == 0) {
                            CommonMethods.showToast(mContext, getResources().getString(R.string.please_enter_date));
                            return;
                        }
                        visitDate = mSelectDatePicker;
                    }
                }

                if (doctorId != -1) {
                    callRecordsActivity();
                } else {
                    RequestAddDoctorModel requestAddDoctorModel = new RequestAddDoctorModel();
                    requestAddDoctorModel.setAddress(selectAddressText.getText().toString());
                    requestAddDoctorModel.setName(mSelectDoctorName.getText().toString());
                    myRecordsHelper.addDoctor(requestAddDoctorModel);
                }*/
                callRecordsActivity();
                break;
            case R.id.searchButton:
                mSelectDoctorName.setText("");
                break;
            case R.id.addressIcon:
                AddRecordsActivityPermissionsDispatcher.callPickPlaceWithCheck(this);
                break;
            case R.id.backImageView:
                onBackPressed();
                break;
        }
    }

    private void callRecordsActivity() {
        Intent intent = new Intent(mContext, SelectedRecordsActivity.class);
        intent.putExtra(RescribeConstants.DOCTORS_ID, doctorId);
        intent.putExtra(RescribeConstants.OPD_ID, opdId);
        intent.putExtra(RescribeConstants.VISIT_DATE, visitDate);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AddRecordsActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION})
    public void callPickPlace() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            Intent intentPlace = builder.build(AddRecordsActivity.this);
            startActivityForResult(intentPlace, PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                StringBuilder stBuilder = new StringBuilder();
                String placename = String.format("%s", place.getName());
                String latitude = String.valueOf(place.getLatLng().latitude);
                String longitude = String.valueOf(place.getLatLng().longitude);
                String address = String.format("%s", place.getAddress());
                stBuilder.append("Name: ");
                stBuilder.append(placename);
                stBuilder.append("\n");
                stBuilder.append("Latitude: ");
                stBuilder.append(latitude);
                stBuilder.append("\n");
                stBuilder.append("Logitude: ");
                stBuilder.append(longitude);
                stBuilder.append("\n");
                stBuilder.append("Address: ");
                stBuilder.append(address);

                CommonMethods.Log("Address: ", stBuilder.toString());

                selectAddressText.setText(address);
            }
        }
    }

    @Override
    public void onTextEnter(boolean isEntered) {
        if (isEntered)
            searchButton.setImageResource(R.drawable.del);
        else searchButton.setImageResource(R.drawable.magnifying_glass);
    }

    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        selectDate.setText(CommonMethods.ordinal(String.valueOf(dayOfMonth)) + " " + CommonMethods.getFormattedDate(String.valueOf(monthOfYear + 1), RescribeConstants.DATE_PATTERN.MM, RescribeConstants.DATE_PATTERN.MMM) + " " + year);
        mSelectDatePicker = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {

        if (customResponse instanceof MyRecordsDoctorListModel) {
            MyRecordsDoctorListModel myRecordsDoctorListModel = (MyRecordsDoctorListModel) customResponse;
            if (myRecordsDoctorListModel.getCommon().getStatusCode().equals(RescribeConstants.SUCCESS)) {
                mSelectDoctorName.setThreshold(1);
                doctorSpinnerAdapter = new DoctorSpinnerAdapter(AddRecordsActivity.this, R.layout.activity_add_records, R.id.doctorName, myRecordsDoctorListModel.getDoctorListModel().getDocList());
                mSelectDoctorName.setAdapter(doctorSpinnerAdapter);

                mSelectDoctorName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (String.valueOf(s).length() > 0)
                            selectAddressLayout.setVisibility(View.VISIBLE);
                        else selectAddressLayout.setVisibility(View.GONE);
                    }
                });

                mSelectDoctorName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        CommonMethods.hideKeyboard(AddRecordsActivity.this);

                        final ArrayList<VisitDate> spinnerList = new ArrayList<VisitDate>();
                        mSelectDateString = getResources().getString(R.string.select_date_text);
                        if (doctorSpinnerAdapter.getDoctor(position).getDoctorName().contains("Dr.")) {
                            doctorNameString = doctorSpinnerAdapter.getDoctor(position).getDoctorName();
                        } else {
                            doctorNameString = "Dr. " + doctorSpinnerAdapter.getDoctor(position).getDoctorName();
                        }
                        mSelectDoctorString = doctorSpinnerAdapter.getDoctor(position).getDoctorName();

                        mSelectedId = doctorSpinnerAdapter.getDoctor(position).getId();
                        if (!doctorSpinnerAdapter.getDoctor(position).getDates().isEmpty()) {
                            isDatesThere = true;

                            dropdownLayout.setVisibility(View.VISIBLE);
                            autocompleteLayout.setVisibility(View.GONE);
                            doctorName.setText(doctorNameString);
                            doctorSpecialist.setText(doctorSpinnerAdapter.getDoctor(position).getSpecialization());
                            doctorAddress.setText(doctorSpinnerAdapter.getDoctor(position).getAddress());
                            int color2 = mColorGenerator.getColor(doctorSpinnerAdapter.getDoctor(position).getDoctorName());
                            TextDrawable drawable = TextDrawable.builder()
                                    .beginConfig()
                                    .width(Math.round(mContext.getResources().getDimension(R.dimen.dp40))) // width in px
                                    .height(Math.round(mContext.getResources().getDimension(R.dimen.dp40))) // height in px
                                    .endConfig()
                                    .buildRound(("" + doctorSpinnerAdapter.getDoctor(position).getDoctorName().charAt(0)).toUpperCase(), color2);


                            RequestOptions requestOptions = new RequestOptions();
                            requestOptions.dontAnimate();
                            requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
                            requestOptions.skipMemoryCache(true);
                            requestOptions.override(mImageSize, mImageSize);
                            requestOptions.error(drawable);
                            requestOptions.placeholder(drawable);

                            Glide.with(mContext)
                                    .load(doctorSpinnerAdapter.getDoctor(position).getDocImg())
                                    .apply(requestOptions).thumbnail(0.5f)
                                    .into(doctorImage);

                            dateSpinnerLayout.setVisibility(View.VISIBLE);
                            selectDateLayout.setVisibility(View.GONE);

                            selectAddressLayout.setVisibility(View.GONE);
                        } else {
                            isDatesThere = false;

                            mSelectDoctorName.setText("");
                            dropdownLayout.setVisibility(View.VISIBLE);
                            autocompleteLayout.setVisibility(View.GONE);
                            doctorName.setText(doctorNameString);
                            doctorSpecialist.setText(doctorSpinnerAdapter.getDoctor(position).getSpecialization());
                            doctorAddress.setText(doctorSpinnerAdapter.getDoctor(position).getAddress());
                            int color2 = mColorGenerator.getColor(doctorSpinnerAdapter.getDoctor(position).getDoctorName());
                            TextDrawable drawable = TextDrawable.builder()
                                    .beginConfig()
                                    .width(Math.round(mContext.getResources().getDimension(R.dimen.dp40))) // width in px
                                    .height(Math.round(mContext.getResources().getDimension(R.dimen.dp40))) // height in px
                                    .endConfig()
                                    .buildRound(("" + doctorSpinnerAdapter.getDoctor(position).getDoctorName().charAt(0)).toUpperCase(), color2);

                            RequestOptions requestOptions = new RequestOptions();
                            requestOptions.dontAnimate();
                            requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
                            requestOptions.skipMemoryCache(true);
                            requestOptions.override(mImageSize, mImageSize);
                            requestOptions.error(drawable);
                            requestOptions.placeholder(drawable);

                            Glide.with(mContext)
                                    .load(doctorSpinnerAdapter.getDoctor(position).getDocImg())
                                    .apply(requestOptions).thumbnail(0.5f)
                                    .into(doctorImage);
                            dateSpinnerLayout.setVisibility(View.GONE);
                            selectDateLayout.setVisibility(View.VISIBLE);

                            selectAddressLayout.setVisibility(View.GONE);
                        }

                        isManual = false;

                        for (VisitDate date : doctorSpinnerAdapter.getDoctor(position).getDates()) {
                            String formatedDate = CommonMethods.getFormattedDate(date.getOpdDate(), RescribeConstants.DATE_PATTERN.UTC_PATTERN, RescribeConstants.DD_MM_YYYY);

                            VisitDate visitD = new VisitDate();
                            visitD.setOpdDate(formatedDate);
                            visitD.setOpdId(date.getOpdId());
                            spinnerList.add(visitD);
                        }

                        Comparator<VisitDate> comparator = new Comparator<VisitDate>() {
                            @Override
                            public int compare(VisitDate o1, VisitDate o2) {
                                Date m1Date = CommonMethods.convertStringToDate(o1.getOpdDate(), RescribeConstants.DATE_PATTERN.DD_MM_YYYY);
                                Date m2Date = CommonMethods.convertStringToDate(o2.getOpdDate(), RescribeConstants.DATE_PATTERN.DD_MM_YYYY);
                                return m1Date.compareTo(m2Date);
                            }
                        };

                        Collections.sort(spinnerList, Collections.reverseOrder(comparator));

                        VisitDate visitDate = new VisitDate();
                        visitDate.setOpdDate(getResources().getString(R.string.select_date_text));
                        visitDate.setOpdId(0);
                        spinnerList.add(0, visitDate);

                        ArrayAdapter<VisitDate> arrayAdapter = new ArrayAdapter<>(AddRecordsActivity.this, R.layout.global_item_simple_spinner, spinnerList);
                        selectDateSpinner.setAdapter(arrayAdapter);
                        selectDateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                mSelectDateString = spinnerList.get(position).getOpdDate();
                                opdId = spinnerList.get(position).getOpdId();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                });
            }
        } else if (customResponse instanceof AddDoctorModel) {
            AddDoctorModel addDoctorModel = (AddDoctorModel) customResponse;
            if (addDoctorModel.getCommon().getStatusCode().equals(RescribeConstants.SUCCESS)) {
                doctorId = addDoctorModel.getData().getDocId();
                callRecordsActivity();
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

    private void setColumnNumber(Context context, int columnNum) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        mImageSize = (widthPixels / columnNum) - mContext.getResources().getDimensionPixelSize(R.dimen.dp30);
    }

}
