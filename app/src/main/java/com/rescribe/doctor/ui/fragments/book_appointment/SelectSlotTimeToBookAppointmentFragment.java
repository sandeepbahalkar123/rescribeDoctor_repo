package com.rescribe.doctor.ui.fragments.book_appointment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.philliphsu.bottomsheetpickers.date.DatePickerDialog;
import com.philliphsu.bottomsheetpickers.time.BottomSheetTimePickerDialog;
import com.rescribe.doctor.R;
import com.rescribe.doctor.adapters.book_appointment.SelectSlotToBookAppointmentAdapter;
import com.rescribe.doctor.helpers.myappointments.AppointmentHelper;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.interfaces.HelperResponse;
import com.rescribe.doctor.model.doctor_location.DoctorLocationModel;
import com.rescribe.doctor.model.patient.doctor_patients.PatientList;
import com.rescribe.doctor.model.request_appointment_confirmation.Reschedule;
import com.rescribe.doctor.model.request_appointment_confirmation.ResponseAppointmentConfirmationModel;
import com.rescribe.doctor.model.select_slot_book_appointment.TimeSlotData;
import com.rescribe.doctor.model.select_slot_book_appointment.TimeSlotListBaseModel;
import com.rescribe.doctor.model.select_slot_book_appointment.TimeSlotListDataModel;
import com.rescribe.doctor.model.select_slot_book_appointment.TimeSlotsInfoList;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.ui.activities.my_appointments.MyAppointmentsActivity;
import com.rescribe.doctor.ui.customesViews.CircularImageView;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.ui.customesViews.NonScrollExpandableListView;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.rescribe.doctor.singleton.RescribeApplication.getDoctorLocationModels;
import static com.rescribe.doctor.util.CommonMethods.toCamelCase;

public class SelectSlotTimeToBookAppointmentFragment extends Fragment implements HelperResponse, DatePickerDialog.OnDateSetListener, BottomSheetTimePickerDialog.OnTimeSetListener {

    public static final int CONFIRM_REQUESTCODE = 212;
    private static final String TAG = "TimeSlotFragment";

    public static TimeSlotData mSelectedTimeSlot = new TimeSlotData();
    //-------------

    Unbinder unbinder;
    @BindView(R.id.premiumType)
    CustomTextView premiumType;
    @BindView(R.id.doctorFees)
    CustomTextView doctorFees;
    @BindView(R.id.ruppeeShadow)
    ImageView ruppeeShadow;
    @BindView(R.id.rupeesLayout)
    LinearLayout rupeesLayout;
    @BindView(R.id.profileImage)
    CircularImageView profileImage;
    @BindView(R.id.patientNameTextView)
    CustomTextView patientNameTextView;
    @BindView(R.id.patientInfo)
    CustomTextView patientInfo;
    @BindView(R.id.patientAddress)
    CustomTextView patientAddress;
    @BindView(R.id.docRating)
    CustomTextView docRating;
    @BindView(R.id.docRatingBar)
    RatingBar docRatingBar;
    @BindView(R.id.docRatingBarLayout)
    LinearLayout docRatingBarLayout;
    @BindView(R.id.doChat)
    ImageView doChat;
    @BindView(R.id.favorite)
    ImageView favorite;
    @BindView(R.id.docPracticesLocationCount)
    CustomTextView docPracticesLocationCount;
    @BindView(R.id.viewAllClinicsOnMap)
    ImageView viewAllClinicsOnMap;
    @BindView(R.id.allClinicPracticeLocationMainLayout)
    LinearLayout allClinicPracticeLocationMainLayout;
    @BindView(R.id.clinicNameSpinner)
    Spinner clinicNameSpinner;
    @BindView(R.id.clinicNameSpinnerParentLayout)
    LinearLayout clinicNameSpinnerParentLayout;
    @BindView(R.id.yearsExperienceLine)
    View yearsExperienceLine;
    @BindView(R.id.leftArrow)
    ImageView leftArrow;
    @BindView(R.id.selectDateTime)
    CustomTextView selectDateTime;
    @BindView(R.id.rightArrow)
    ImageView rightArrow;
    @BindView(R.id.appointmentMessageTextView)
    TextView appointmentMessageTextView;
    @BindView(R.id.noTimeSlotMessageTextView)
    TextView noTimeSlotMessageTextView;
    @BindView(R.id.selectTimeDateExpandableView)
    NonScrollExpandableListView selectTimeDateExpandableView;
    @BindView(R.id.no_data_found)
    LinearLayout noDataFound;
    @BindView(R.id.timeSlotListViewLayout)
    LinearLayout timeSlotListViewLayout;
    @BindView(R.id.scheduledAppointmentsTimeStamp)
    CustomTextView scheduledAppointmentsTimeStamp;
    @BindView(R.id.tokenNewTimeStamp)
    ImageView tokenNewTimeStamp;
    @BindView(R.id.waitingTime)
    CustomTextView waitingTime;
    @BindView(R.id.doctorExperienceLayout)
    LinearLayout doctorExperienceLayout;
    @BindView(R.id.confirmedTokenMainLayout)
    LinearLayout confirmedTokenMainLayout;
    @BindView(R.id.tokenMessageTextView)
    TextView tokenMessageTextView;
    @BindView(R.id.appointmentTypeIsBookButton)
    AppCompatButton appointmentTypeIsBookButton;
    @BindView(R.id.appointmentTypeFooterButtonBarLayout)
    LinearLayout appointmentTypeFooterButtonBarLayout;

    private AppointmentHelper mAppointmentHelper;
    private ArrayList<DoctorLocationModel> mDoctorLocationModel = new ArrayList<>();
    private Context mContext;
    private int mLastExpandedPosition = -1;
    private SelectSlotToBookAppointmentAdapter mSelectSlotToBookAppointmentAdapter;
    private String mSelectedTimeSlotDate;
    private int mSelectedClinicDataPosition = -1;
    private Date mMaxDateRange;
    private DatePickerDialog mDatePickerDialog;
    private ColorGenerator mColorGenerator;
    private DoctorLocationModel mDoctorLocationModelObject;
    private PatientList mPatientinfoObject = new PatientList();
    private boolean isAppointmentTypeReschedule;

    public SelectSlotTimeToBookAppointmentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mRootView = inflater.inflate(R.layout.book_appoint_doc_desc_select_time_slot, container, false);
        unbinder = ButterKnife.bind(this, mRootView);
        init();
        return mRootView;
    }

    public static SelectSlotTimeToBookAppointmentFragment newInstance(Bundle b) {
        SelectSlotTimeToBookAppointmentFragment fragment = new SelectSlotTimeToBookAppointmentFragment();
        fragment.setArguments(b);
        return fragment;
    }

    private void init() {

        String mPatientDetail = getArguments().getString(RescribeConstants.PATIENT_DETAILS);
        mPatientinfoObject = getArguments().getParcelable(RescribeConstants.PATIENT_INFO);
        isAppointmentTypeReschedule = getArguments().getBoolean(RescribeConstants.IS_APPOINTMENT_TYPE_RESHEDULE, false);

        mContext = getContext();
        mDoctorLocationModel = getDoctorLocationModels();
        yearsExperienceLine.setVisibility(View.GONE);
        mColorGenerator = ColorGenerator.MATERIAL;
        String mPatientNameToShow = "";
        if (mPatientinfoObject.getSalutation() != 0)
            mPatientNameToShow = RescribeConstants.SALUTATION[mPatientinfoObject.getSalutation() - 1] + toCamelCase(mPatientinfoObject.getPatientName());
        else mPatientNameToShow = toCamelCase(mPatientinfoObject.getPatientName());
        patientNameTextView.setText(mPatientNameToShow);
        patientInfo.setText(mPatientDetail);
        if (mPatientinfoObject.getPatientArea().equals("") && mPatientinfoObject.getPatientCity().equals("")) {
            patientAddress.setText("");
        } else if (!mPatientinfoObject.getPatientArea().equals("") && mPatientinfoObject.getPatientCity().equals("")) {
            patientAddress.setText(mPatientinfoObject.getPatientArea());
        } else if (mPatientinfoObject.getPatientArea().equals("") && !mPatientinfoObject.getPatientCity().equals("")) {
            patientAddress.setText(mPatientinfoObject.getPatientCity());
        } else if (!mPatientinfoObject.getPatientArea().equals("") && !mPatientinfoObject.getPatientCity().equals("")) {
            patientAddress.setText(mPatientinfoObject.getPatientArea() + ", " + mPatientinfoObject.getPatientCity());
        }

        Calendar now = Calendar.getInstance();
        //---------
        // As of version 2.3.0, `BottomSheetDatePickerDialog` is deprecated.
        mDatePickerDialog = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        //------------

        //----******************------
        String monthInt = "" + (now.get(Calendar.MONTH) + 1);
        if (monthInt.length() == 1) {
            monthInt = "0" + monthInt;
        }
        //----------
        String dayInt = "" + (now.get(Calendar.DAY_OF_MONTH));
        if (dayInt.length() == 1) {
            dayInt = "0" + dayInt;
        }
        //----------
        if (RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.SELECTED_APP_DATE, mContext).equals(""))
            mSelectedTimeSlotDate = now.get(Calendar.YEAR) + "-" + monthInt + "-" + dayInt;
        else {
            // store currentDate if date is less than current date
            mSelectedTimeSlotDate = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.SELECTED_APP_DATE, mContext);
            Date date = CommonMethods.convertStringToDate(mSelectedTimeSlotDate, RescribeConstants.DATE_PATTERN.YYYY_MM_DD);
            Date currentDate = new Date();
            if (date.getTime() < currentDate.getTime()) {
                mSelectedTimeSlotDate = CommonMethods.getCurrentDate(RescribeConstants.DATE_PATTERN.YYYY_MM_DD);
                RescribePreferencesManager.putString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.SELECTED_APP_DATE, mSelectedTimeSlotDate, mContext);
            }
        }

        //----******************------

        String dayFromDate = CommonMethods.getDayFromDate(RescribeConstants.YYYY_MM_DD, mSelectedTimeSlotDate);
        selectDateTime.setText(dayFromDate + ", " + CommonMethods.getFormattedDate(mSelectedTimeSlotDate, RescribeConstants.DATE_PATTERN.YYYY_MM_DD, RescribeConstants.DATE_PATTERN.DD_MMM));

        selectTimeDateExpandableView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                if (mLastExpandedPosition != -1
                        && groupPosition != mLastExpandedPosition) {
                    selectTimeDateExpandableView.collapseGroup(mLastExpandedPosition);
                }
                mLastExpandedPosition = groupPosition;
            }
        });
        selectTimeDateExpandableView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                selectTimeDateExpandableView.collapseGroup(groupPosition);
                return false;
            }
        });
        //--------------

    }

    @SuppressLint("CheckResult")
    private void setDataInViews() {
        if (mDoctorLocationModel.size() > 0) {
            appointmentTypeFooterButtonBarLayout.setVisibility(View.VISIBLE);
            noDataFound.setVisibility(View.GONE);
        } else {
            appointmentTypeFooterButtonBarLayout.setVisibility(View.GONE);
            noDataFound.setVisibility(View.VISIBLE);
        }

        if (mPatientinfoObject.getPatientName() != null) {

            int color2 = mColorGenerator.getColor(mPatientinfoObject.getPatientName());
            TextDrawable drawable = TextDrawable.builder()
                    .beginConfig()
                    .width(Math.round(getActivity().getResources().getDimension(R.dimen.dp40))) // width in px
                    .height(Math.round(getActivity().getResources().getDimension(R.dimen.dp40))) // height in px
                    .endConfig()
                    .buildRound(("" + mPatientinfoObject.getPatientName().charAt(0)).toUpperCase(), color2);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.dontAnimate();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
            requestOptions.skipMemoryCache(true);
            requestOptions.placeholder(drawable);
            requestOptions.error(drawable);

            Glide.with(getActivity())
                    .load(mPatientinfoObject.getPatientImageUrl())
                    .apply(requestOptions).thumbnail(0.5f)
                    .into(profileImage);
        }

        if (mDoctorLocationModel.size() > 0) {
            clinicNameSpinnerParentLayout.setVisibility(View.VISIBLE);

            ArrayAdapter<DoctorLocationModel> arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.global_item_simple_spinner, mDoctorLocationModel);
            clinicNameSpinner.setAdapter(arrayAdapter);

            int selectedPosition = getClinicSelectedPosition();
            clinicNameSpinner.setSelection(selectedPosition, false);
            mDoctorLocationModelObject = mDoctorLocationModel.get(selectedPosition);
            changeViewBasedOnAppointmentType();

            clinicNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mDoctorLocationModelObject = mDoctorLocationModel.get(position);
                    changeViewBasedOnAppointmentType();
                    RescribePreferencesManager.putInt(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.SELECTED_APP_CLINIC, mDoctorLocationModel.get(position).getLocationId(), mContext);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            if (mDoctorLocationModel.size() == 1) {
                clinicNameSpinner.setEnabled(false);
                clinicNameSpinner.setClickable(false);
                clinicNameSpinner.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.transparent));
            } else {
                clinicNameSpinner.setEnabled(true);
                clinicNameSpinner.setClickable(true);
                clinicNameSpinner.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.spinner_bg_profile));
            }
        } else {
            clinicNameSpinnerParentLayout.setVisibility(View.GONE);
        }

        //---------
    }

    private int getClinicSelectedPosition() {
        int result = 0;
        for (int index = 0; index < mDoctorLocationModel.size(); index++) {
            if (mDoctorLocationModel.get(index).getLocationId() == RescribePreferencesManager.getInt(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.SELECTED_APP_CLINIC, mContext)) {
                result = index;
                break;
            }
        }
        return result;
    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {

        switch (mOldDataTag) {

            //first & second case are alomst same, only change in doc_data param.
            case RescribeConstants.TASK_GET_TIME_SLOTS_TO_BOOK_APPOINTMENT:
                TimeSlotListBaseModel slotListBaseModel = (TimeSlotListBaseModel) customResponse;
                if (slotListBaseModel != null) {
                    tokenMessageTextView.setVisibility(View.GONE);

                    TimeSlotListDataModel selectSlotList = slotListBaseModel.getTimeSlotListDataModel();
                    if (selectSlotList != null) {
                        if (!isAppointmentTypeReschedule) {
                            if (selectSlotList.isAppointmentTaken() == 0) {
                                appointmentMessageTextView.setVisibility(View.GONE);
                                noTimeSlotMessageTextView.setVisibility(View.GONE);
                                if (!selectSlotList.getTimeSlotsInfoList().isEmpty()) {
                                    noTimeSlotMessageTextView.setVisibility(View.GONE);
                                    selectTimeDateExpandableView.setVisibility(View.VISIBLE);
                                    appointmentTypeIsBookButton.setVisibility(View.VISIBLE);
                                    mSelectSlotToBookAppointmentAdapter = new SelectSlotToBookAppointmentAdapter(getActivity(), selectSlotList.getTimeSlotsInfoList(), mSelectedTimeSlotDate,selectSlotList.getAppointmentFormat());
                                    selectTimeDateExpandableView.setAdapter(mSelectSlotToBookAppointmentAdapter);
                                    int selectedSlotPosition = getSelectedSlotPosition(selectSlotList.getTimeSlotsInfoList());
                                    if (selectedSlotPosition != -1)
                                        selectTimeDateExpandableView.expandGroup(selectedSlotPosition, true);
                                } else {
                                    noTimeSlotMessageTextView.setVisibility(View.VISIBLE);
                                    noTimeSlotMessageTextView.setText(slotListBaseModel.getCommon().getStatusMessage());
                                    selectTimeDateExpandableView.setVisibility(View.GONE);
                                    appointmentTypeIsBookButton.setVisibility(View.GONE);
                                }
                            } else {
                                selectTimeDateExpandableView.setVisibility(View.GONE);
                                appointmentTypeIsBookButton.setVisibility(View.GONE);
                                appointmentMessageTextView.setVisibility(View.VISIBLE);
                            }
                        } else {
                            appointmentMessageTextView.setVisibility(View.GONE);
                            noTimeSlotMessageTextView.setVisibility(View.GONE);
                            if (!selectSlotList.getTimeSlotsInfoList().isEmpty()) {
                                noTimeSlotMessageTextView.setVisibility(View.GONE);
                                selectTimeDateExpandableView.setVisibility(View.VISIBLE);
                                appointmentTypeIsBookButton.setVisibility(View.VISIBLE);
                                mSelectSlotToBookAppointmentAdapter = new SelectSlotToBookAppointmentAdapter(getActivity(), selectSlotList.getTimeSlotsInfoList(), mSelectedTimeSlotDate,selectSlotList.getAppointmentFormat());
                                selectTimeDateExpandableView.setAdapter(mSelectSlotToBookAppointmentAdapter);
                                int selectedSlotPosition = getSelectedSlotPosition(selectSlotList.getTimeSlotsInfoList());
                                if (selectedSlotPosition != -1)
                                    selectTimeDateExpandableView.expandGroup(selectedSlotPosition, true);
                            } else {
                                noTimeSlotMessageTextView.setVisibility(View.VISIBLE);
                                noTimeSlotMessageTextView.setText(slotListBaseModel.getCommon().getStatusMessage());
                                selectTimeDateExpandableView.setVisibility(View.GONE);
                                appointmentTypeIsBookButton.setVisibility(View.GONE);
                            }

                        }
                    }
                }
                break;

            case RescribeConstants.TASK_CONFIRM_APPOINTMENT:
                if (customResponse != null) {
                    ResponseAppointmentConfirmationModel mResponseAppointmentConfirmationModel = (ResponseAppointmentConfirmationModel) customResponse;
                    if (mResponseAppointmentConfirmationModel.getCommon().isSuccess()) {
                        Toast.makeText(mContext, mResponseAppointmentConfirmationModel.getCommon().getStatusMessage(), Toast.LENGTH_SHORT).show();
                        Intent intentObject = new Intent(getContext(), MyAppointmentsActivity.class);
                        intentObject.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentObject);
                        getActivity().setResult(MyAppointmentsActivity.CLOSE_APPOINTMENT_ACTIVITY_AFTER_BOOK_APPOINTMENT);
                        getActivity().finish();
                    } else {
                        Toast.makeText(mContext, mResponseAppointmentConfirmationModel.getCommon().getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private int getSelectedSlotPosition(ArrayList<TimeSlotsInfoList> timeSlotsInfoList) {
        int index = -1;
        for (int pos = 0; pos < timeSlotsInfoList.size(); pos++) {
            TimeSlotsInfoList timeSlotsInfo = timeSlotsInfoList.get(pos);
            for (TimeSlotData timeSlotData : timeSlotsInfo.getTimeSlotList()) {
                if (timeSlotData.getFromTime().equalsIgnoreCase(mSelectedTimeSlot.getFromTime()) && timeSlotData.getSlotId().equals(mSelectedTimeSlot.getSlotId())) {
                    index = pos;
                    break;
                }
            }
        }
        return index;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CONFIRM_REQUESTCODE) {
                init();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mAppointmentHelper == null) {
            mAppointmentHelper = new AppointmentHelper(getActivity(), this);

            //--------------
            if (mSelectedClinicDataPosition != -1) {
                if (mDoctorLocationModel.size() > 0)
                    mDoctorLocationModelObject = mDoctorLocationModel.get(mSelectedClinicDataPosition);
            } //--------------

            setDataInViews();
            changeViewBasedOnAppointmentType();


        }

    }

    @OnClick({R.id.selectDateTime, R.id.leftArrow, R.id.rightArrow, R.id.appointmentTypeIsBookButton})
    public void onClickOfView(View view) {
        Calendar now = Calendar.getInstance();
        switch (view.getId()) {
            case R.id.selectDateTime:

                if (mDoctorLocationModelObject != null) {
                    //---------
                    Calendar selectedTimeSlotDateCal = Calendar.getInstance();
                    Date date1 = CommonMethods.convertStringToDate(mSelectedTimeSlotDate, RescribeConstants.DATE_PATTERN.YYYY_MM_DD);
                    selectedTimeSlotDateCal.setTime(date1);
                    mDatePickerDialog = DatePickerDialog.newInstance(
                            this,
                            selectedTimeSlotDateCal.get(Calendar.YEAR),
                            selectedTimeSlotDateCal.get(Calendar.MONTH),
                            selectedTimeSlotDateCal.get(Calendar.DAY_OF_MONTH));
                    //---------

                    mDatePickerDialog.setAccentColor(getResources().getColor(R.color.tagColor));
                    mDatePickerDialog.setMinDate(now);
                    mDatePickerDialog.show(getFragmentManager(), getResources().getString(R.string.select_date_text));
                    mDatePickerDialog.setOutOfRageInvisible();

                    //-------------
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DATE, mDoctorLocationModelObject.getApptScheduleLmtDays());
                    mMaxDateRange = calendar.getTime();
                    mDatePickerDialog.setMaxDate(calendar);
                    //-------------

                }
                break;

            case R.id.appointmentTypeIsBookButton:
                if (mSelectSlotToBookAppointmentAdapter != null) {
                    if (RescribeConstants.BLANK.equalsIgnoreCase(mSelectSlotToBookAppointmentAdapter.getSelectedTimeSlot().getFromTime()) || mSelectSlotToBookAppointmentAdapter.getSelectedTimeSlot().getFromTime() == null) {
                        CommonMethods.showToast(getContext(), getString(R.string.time_select_err));
                    } else {
                        if (isAppointmentTypeReschedule) {
                            Reschedule reschedule = new Reschedule();
                            reschedule.setAptId(String.valueOf(mPatientinfoObject.getAptId()));
                            reschedule.setStatus("4");
                            mAppointmentHelper.doConfirmAppointmentRequest(Integer.parseInt(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, getActivity())), mDoctorLocationModelObject.getLocationId(), mSelectedTimeSlotDate, mSelectSlotToBookAppointmentAdapter.getSelectedTimeSlot(), reschedule, mPatientinfoObject.getPatientId());
                        } else
                            mAppointmentHelper.doConfirmAppointmentRequest(Integer.parseInt(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, getActivity())), mDoctorLocationModelObject.getLocationId(), mSelectedTimeSlotDate, mSelectSlotToBookAppointmentAdapter.getSelectedTimeSlot(), null, mPatientinfoObject.getPatientId());
                    }
                }


                break;

            case R.id.leftArrow:
                if (mDoctorLocationModelObject != null) {
                    isShowPreviousDayLeftArrow(true);
                }
                break;

            case R.id.rightArrow:
                if (mDoctorLocationModelObject != null) {
                    Calendar calendarNow = Calendar.getInstance();
                    calendarNow.add(Calendar.DATE, mDoctorLocationModelObject.getApptScheduleLmtDays());
                    mMaxDateRange = calendarNow.getTime();
                    //---------
                    Date date = CommonMethods.convertStringToDate(this.mSelectedTimeSlotDate, RescribeConstants.DATE_PATTERN.YYYY_MM_DD);
                    Calendar c = Calendar.getInstance();
                    c.setTime(date); // Now use today date.
                    c.add(Calendar.DATE, 1); // Adding 1 days
                    date = c.getTime();
                    //---------
                    if (mMaxDateRange.getTime() >= date.getTime()) {
                        onDateSet(mDatePickerDialog, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                    }
                }
                break;
        }
    }

    private void isShowPreviousDayLeftArrow(boolean isCallOnDateSet) {

        leftArrow.setVisibility(View.VISIBLE);
        //------------
        Date receivedDate = CommonMethods.convertStringToDate(this.mSelectedTimeSlotDate, RescribeConstants.DATE_PATTERN.YYYY_MM_DD);
        Calendar cc = Calendar.getInstance();
        cc.setTime(receivedDate); // Now use today date.
        cc.add(Calendar.DATE, -1); // subtracting 1 days
        receivedDate = cc.getTime();
        //-----------
        String formattedCurrentDateString = CommonMethods.formatDateTime(CommonMethods.getCurrentDate(RescribeConstants.DD_MM_YYYY), RescribeConstants.DATE_PATTERN.YYYY_MM_DD, RescribeConstants.DD_MM_YYYY, RescribeConstants.DATE);
        SimpleDateFormat dateFormat = new SimpleDateFormat(RescribeConstants.DATE_PATTERN.YYYY_MM_DD, Locale.US);
        String receivedDateString = dateFormat.format(receivedDate);
        //------------
        Date currentDate = new Date();
        //------------
        if ((currentDate.getTime() < receivedDate.getTime()) || (formattedCurrentDateString.equalsIgnoreCase(receivedDateString))) {
            if (isCallOnDateSet)
                onDateSet(mDatePickerDialog, cc.get(Calendar.YEAR), cc.get(Calendar.MONTH), cc.get(Calendar.DAY_OF_MONTH));
        } else {
            leftArrow.setVisibility(View.INVISIBLE);
        }
    }

    private void changeViewBasedOnAppointmentType() {
        if (mDoctorLocationModelObject != null) {
            //----------
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, mDoctorLocationModelObject.getApptScheduleLmtDays());
            mMaxDateRange = calendar.getTime();

            confirmedTokenMainLayout.setVisibility(View.GONE);
            timeSlotListViewLayout.setVisibility(View.VISIBLE);
            //--------
            //----
            appointmentTypeFooterButtonBarLayout.setVisibility(View.VISIBLE);
            appointmentTypeIsBookButton.setVisibility(View.VISIBLE);
            //----
            //--------
            if (CommonMethods.getCurrentDate(RescribeConstants.YYYY_MM_DD).equals(mSelectedTimeSlotDate))
                leftArrow.setVisibility(View.INVISIBLE);
            else
                leftArrow.setVisibility(View.VISIBLE);

            rightArrow.setVisibility(View.VISIBLE);
            //--------

            selectDateTime.setEnabled(true);
            mAppointmentHelper.getTimeSlotToBookAppointmentWithDoctor("" + RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, getActivity()), mDoctorLocationModelObject.getLocationId(), mSelectedTimeSlotDate, false, mPatientinfoObject.getPatientId());

        }
    }

    @Override
    public void onTimeSet(ViewGroup viewGroup, int hourOfDay, int minute) {
        //-----------------
        String currentTimeStamp = CommonMethods.getCurrentTimeStamp(RescribeConstants.DATE_PATTERN.YYYY_MM_DD);
        Date maxCurrentDateTimeForDay = CommonMethods.convertStringToDate(currentTimeStamp + " " + "23:59:59", RescribeConstants.DATE_PATTERN.YYYY_MM_DD_HH_mm_ss);
        //---
        Date currentDateTimeForDay = CommonMethods.convertStringToDate(CommonMethods.getCurrentTimeStamp(RescribeConstants.DATE_PATTERN.YYYY_MM_DD_HH_mm_ss), RescribeConstants.DATE_PATTERN.YYYY_MM_DD_HH_mm_ss);
        Date selectedDateTimeForDay = CommonMethods.convertStringToDate(mSelectedTimeSlotDate + " " + hourOfDay + ":" + minute + ":00", RescribeConstants.DATE_PATTERN.YYYY_MM_DD_HH_mm_ss);

        if (currentDateTimeForDay.getTime() > selectedDateTimeForDay.getTime()) {
            CommonMethods.showToast(getContext(), getString(R.string.token_select_time_err_msg));
        } else if (maxCurrentDateTimeForDay.getTime() < selectedDateTimeForDay.getTime()) {
            CommonMethods.showToast(getContext(), getString(R.string.token_select_time_err_msg));
        }
    }


    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        String dateConverted = "" + dayOfMonth;
        if (dayOfMonth < 10) {
            dateConverted = "0" + dayOfMonth;
        }

        String monthOfYearData = "" + (monthOfYear + 1);
        if (monthOfYearData.length() == 1) {
            monthOfYearData = "0" + monthOfYearData;
        }

        String dayFromDate = CommonMethods.getDayFromDate(RescribeConstants.DATE_PATTERN.DD_MM_YYYY, dateConverted + "-" + monthOfYearData + "-" + year);

        mSelectedTimeSlotDate = year + "-" + monthOfYearData + "-" + dateConverted;
        selectDateTime.setText(dayFromDate + ", " + CommonMethods.getFormattedDate(dateConverted + "-" + monthOfYearData + "-" + year, RescribeConstants.DATE_PATTERN.DD_MM_YYYY, RescribeConstants.DATE_PATTERN.DD_MMM));
        isShowPreviousDayLeftArrow(false);
        mAppointmentHelper.getTimeSlotToBookAppointmentWithDoctor("" + RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_ID, getActivity()), mDoctorLocationModelObject.getLocationId(), mSelectedTimeSlotDate, false, mPatientinfoObject.getPatientId());
    }
}