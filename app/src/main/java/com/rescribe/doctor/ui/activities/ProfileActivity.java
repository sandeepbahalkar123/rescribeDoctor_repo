package com.rescribe.doctor.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatImageView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.rescribe.doctor.R;
import com.rescribe.doctor.bottom_menus.BottomMenu;
import com.rescribe.doctor.bottom_menus.BottomMenuActivity;
import com.rescribe.doctor.bottom_menus.BottomMenuAdapter;
import com.rescribe.doctor.model.doctor_location.DoctorLocationModel;
import com.rescribe.doctor.preference.RescribePreferencesManager;
import com.rescribe.doctor.singleton.RescribeApplication;
import com.rescribe.doctor.ui.activities.dashboard.SettingsActivity;
import com.rescribe.doctor.ui.activities.dashboard.SupportActivity;
import com.rescribe.doctor.ui.customesViews.BottomSheetDialog;
import com.rescribe.doctor.ui.customesViews.CircularImageView;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.util.CommonMethods;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jeetal on 16/2/18.
 */

public class ProfileActivity extends BottomMenuActivity implements BottomMenuAdapter.OnBottomMenuClickListener {
    @BindView(R.id.backImageView)
    ImageView backImageView;
    @BindView(R.id.titleTextView)
    CustomTextView titleTextView;
    @BindView(R.id.userInfoTextView)
    CustomTextView userInfoTextView;
    @BindView(R.id.dateTextview)
    CustomTextView dateTextview;
    @BindView(R.id.year)
    Spinner year;
    @BindView(R.id.addImageView)
    ImageView addImageView;
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
    @BindView(R.id.clinicName)
    CustomTextView clinicName;
    @BindView(R.id.doctorName)
    CustomTextView doctorName;
    @BindView(R.id.doctorSpecialization)
    CustomTextView doctorSpecialization;
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
    @BindView(R.id.selectClinicLine)
    View selectClinicLine;
    @BindView(R.id.countDoctorExperience)
    CustomTextView countDoctorExperience;
    @BindView(R.id.doctorExperience)
    CustomTextView doctorExperience;
    @BindView(R.id.doctorExperienceLayout)
    LinearLayout doctorExperienceLayout;
    @BindView(R.id.yearsExperienceLine)
    View yearsExperienceLine;
    @BindView(R.id.servicesHeaderView)
    CustomTextView servicesHeaderView;
    @BindView(R.id.servicesListView)
    ListView servicesListView;
    @BindView(R.id.readMoreDocServices)
    CustomTextView readMoreDocServices;
    @BindView(R.id.servicesLayout)
    LinearLayout servicesLayout;
    @BindView(R.id.servicesLine)
    View servicesLine;
    @BindView(R.id.aboutDoctor)
    CustomTextView aboutDoctor;
    @BindView(R.id.aboutDoctorDescription)
    CustomTextView aboutDoctorDescription;
    @BindView(R.id.aboutLayout)
    LinearLayout aboutLayout;
    private Context mContext;
    private BottomSheetDialog mBottomSheetDialog;
    private String doctorNameToDisplay;
    private ArrayList<String> mServiceslist = new ArrayList<>();
    private ArrayList<DoctorLocationModel> mArrayListDoctorLocationModel = new ArrayList<>();
    private ColorGenerator mColorGenerator;
    private String mDoctorName;
    private DoctorLocationModel doctorLocationModel;
    private int mSelectedClinicDataPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_base_layout);
        ButterKnife.bind(this);
        setCurrentActivtyTab(getString(R.string.profile));
        initialize();
    }

    private void initialize() {
        mContext = ProfileActivity.this;
        mColorGenerator = ColorGenerator.MATERIAL;
        mArrayListDoctorLocationModel = RescribeApplication.getDoctorLocationModels();
        int size = mArrayListDoctorLocationModel.size();
        titleTextView.setText(getString(R.string.profile));
        aboutDoctorDescription.setText(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_INFO, mContext));
        if (RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.USER_NAME, mContext).toLowerCase().contains("Dr.")) {
            doctorNameToDisplay = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.USER_NAME, mContext);
        } else {

            doctorNameToDisplay = "Dr. " + RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.USER_NAME, mContext);
        }
        doctorName.setText(doctorNameToDisplay);
        SpannableString content = new SpannableString("Services");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        servicesHeaderView.setText(content);
        countDoctorExperience.setText(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_EXPERIENCE, mContext));
        doctorExperience.setText(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_EXPERIENCE, mContext) + " years of experience");
        doctorSpecialization.setText(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.DOC_DEGREE, mContext));
        mServiceslist = RescribePreferencesManager.getListString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.D0C_SERVICES);
        setServicesInView(mServiceslist);

        if (size > 0) {
            allClinicPracticeLocationMainLayout.setVisibility(View.VISIBLE);

            String mainString = getString(R.string.practices_at_locations);
            if (size == 1) {
                mainString = mainString.substring(0, mainString.length() - 1);
            }
            String updatedString = mainString.replace("$$", "" + size);
            SpannableString contentExp = new SpannableString(updatedString);
            contentExp.setSpan(new ForegroundColorSpan(
                            ContextCompat.getColor(mContext, R.color.tagColor)),
                    13, 13 + String.valueOf(size).length(),//hightlight mSearchString
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            docPracticesLocationCount.setText(contentExp);
        } else {
            allClinicPracticeLocationMainLayout.setVisibility(View.GONE);

        }
        if (RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.PROFILE_PHOTO, mContext) != null) {

            mDoctorName = RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.USER_NAME, mContext);
            if (mDoctorName.contains("Dr. ")) {
                mDoctorName = mDoctorName.replace("Dr. ", "");
            }
            int color2 = mColorGenerator.getColor(mDoctorName);
            TextDrawable drawable = TextDrawable.builder()
                    .beginConfig()
                    .width(Math.round(getResources().getDimension(R.dimen.dp40))) // width in px
                    .height(Math.round(getResources().getDimension(R.dimen.dp40))) // height in px
                    .endConfig()
                    .buildRound(("" + mDoctorName.charAt(0)).toUpperCase(), color2);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.dontAnimate();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
            requestOptions.skipMemoryCache(true);
            requestOptions.placeholder(drawable);
            requestOptions.error(drawable);

            Glide.with(mContext)
                    .load(RescribePreferencesManager.getString(RescribePreferencesManager.RESCRIBE_PREFERENCES_KEY.PROFILE_PHOTO, mContext))
                    .apply(requestOptions).thumbnail(0.5f)
                    .into(profileImage);

        }
        if (mArrayListDoctorLocationModel.size() > 0) {
            ArrayList<String> mClinicname = new ArrayList<>();
            for (int i = 0; i < mArrayListDoctorLocationModel.size(); i++) {
                mClinicname.add(mArrayListDoctorLocationModel.get(i).getClinicName() + ", " + mArrayListDoctorLocationModel.get(i).getArea() + ", " + mArrayListDoctorLocationModel.get(i).getCity());
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mContext, R.layout.clinic_spinner_layout, mClinicname);
            clinicNameSpinner.setAdapter(arrayAdapter);


            clinicNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    doctorLocationModel = mArrayListDoctorLocationModel.get(position);
                    if (doctorLocationModel.getClinicName().equals("")) {
                        clinicName.setVisibility(View.GONE);
                    } else {
                        clinicName.setVisibility(View.VISIBLE);
                        clinicName.setText("" + doctorLocationModel.getClinicName());

                    }

                    mSelectedClinicDataPosition = position;

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            if (mArrayListDoctorLocationModel.size() == 1) {
                clinicNameSpinner.setEnabled(false);
                clinicNameSpinner.setClickable(false);
                mSelectedClinicDataPosition = 0;
                clinicNameSpinner.setBackgroundColor(ContextCompat.getColor(mContext, R.color.transparent));
            } else {
                clinicNameSpinner.setEnabled(true);
                clinicNameSpinner.setClickable(true);
                clinicNameSpinner.setBackground(ContextCompat.getDrawable(mContext, R.drawable.spinner_bg_profile));
            }
        } else {
            clinicNameSpinnerParentLayout.setVisibility(View.GONE);
        }


    }


    private void setServicesInView(ArrayList<String> receivedDocService) {
        //---------

        int receivedDocServiceSize = receivedDocService.size();
        if (receivedDocServiceSize > 0) {
            servicesLine.setVisibility(View.VISIBLE);
            servicesLayout.setVisibility(View.VISIBLE);
            ArrayList<String> docListToSend = new ArrayList<>();
            if (receivedDocServiceSize > 4) {
                docListToSend.addAll(receivedDocService.subList(0, 4));
                readMoreDocServices.setVisibility(View.VISIBLE);
            } else {
                docListToSend.addAll(receivedDocService);
                readMoreDocServices.setVisibility(View.GONE);
            }
            DocServicesListAdapter mServicesAdapter = new DocServicesListAdapter(mContext, docListToSend);
            servicesListView.setAdapter(mServicesAdapter);
            CommonMethods.setListViewHeightBasedOnChildren(servicesListView);
        } else {
            servicesLine.setVisibility(View.GONE);
            servicesLayout.setVisibility(View.GONE);
        }

        //---------
    }

    @Override
    public void onBottomMenuClick(BottomMenu bottomMenu) {

        if (bottomMenu.getMenuName().equalsIgnoreCase(getString(R.string.support))) {
            Intent intent = new Intent(this, SupportActivity.class);
            startActivity(intent);
            finish();
        } else if (bottomMenu.getMenuName().equalsIgnoreCase(getString(R.string.home))) {
            finish();
        } else if (bottomMenu.getMenuName().equalsIgnoreCase(getString(R.string.settings))) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            finish();
        }
        super.onBottomMenuClick(bottomMenu);
    }

    @OnClick({R.id.backImageView, R.id.titleTextView, R.id.userInfoTextView, R.id.readMoreDocServices,R.id.viewAllClinicsOnMap})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backImageView:
                finish();
                break;
            case R.id.titleTextView:
                break;
            case R.id.userInfoTextView:
                break;
            case R.id.readMoreDocServices:

                showServiceDialog();

                break;

            case R.id.viewAllClinicsOnMap: // on view-all location clicked
                //-----Show all doc clinic on map, copied from BookAppointFilteredDoctorListFragment.java----
                //this list is sorted for plotting map for each clinic location, the values of clinicName and doctorAddress are set in string here, which are coming from arraylist.

                Intent intentObjectMap = new Intent(this, MapActivityShowDoctorLocation.class);
                intentObjectMap.putExtra(getString(R.string.address),doctorLocationModel.getArea()+", "+doctorLocationModel.getCity());
                startActivity(intentObjectMap);
                break;
        }
    }

    public void showServiceDialog() {
        mBottomSheetDialog = new BottomSheetDialog(mContext, R.style.Material_App_BottomSheetDialog);
        View v = getLayoutInflater().inflate(R.layout.services_dialog_modal, null);
        ///  CommonMethods.setBackground(v, new ThemeDrawable(R.array.bg_window));
        mBottomSheetDialog.setTitle("Services");
        mBottomSheetDialog.heightParam(ViewGroup.LayoutParams.MATCH_PARENT);
        ListView mServicesListView = (ListView) v.findViewById(R.id.servicesListView);
        DialogServicesListAdapter mServicesAdapter = new DialogServicesListAdapter(mContext, mServiceslist);
        mServicesListView.setAdapter(mServicesAdapter);
        AppCompatImageView closeButton = (AppCompatImageView) v.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
            }
        });

        mBottomSheetDialog.contentView(v)
                .show();

    }

    class DialogServicesListAdapter extends BaseAdapter {
        Context mContext;
        private ArrayList<String> mDocServiceList;

        DialogServicesListAdapter(Context context, ArrayList<String> items) {
            this.mContext = context;
            this.mDocServiceList = items;
        }

        @Override
        public int getCount() {
            return mDocServiceList.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View view = convertView;
            String data = mDocServiceList.get(position);

            if (convertView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                view = layoutInflater.inflate(R.layout.services_item_textview, null);
            }

            CustomTextView dataView = (CustomTextView) view.findViewById(R.id.text);
            dataView.setText(data);
            return view;
        }
    }


    public class DocServicesListAdapter extends BaseAdapter {
        Context mContext;
        private ArrayList<String> mDocServiceList;


        DocServicesListAdapter(Context context, ArrayList<String> items) {
            this.mContext = context;
            mDocServiceList = items;
        }

        @Override
        public int getCount() {
            return mDocServiceList.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View view = convertView;


            if (convertView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                view = layoutInflater.inflate(R.layout.services_item_textview, null);
            }
            CustomTextView dataView = (CustomTextView) view.findViewById(R.id.text);
            dataView.setText("" + mDocServiceList.get(position));
            return view;
        }
    }


}
