package com.rescribe.doctor.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;

import com.rescribe.doctor.R;
import com.rescribe.doctor.bottom_menus.BottomMenu;
import com.rescribe.doctor.bottom_menus.BottomMenuActivity;
import com.rescribe.doctor.bottom_menus.BottomMenuAdapter;
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
        titleTextView.setText(getString(R.string.profile));
        DocServicesListAdapter mServicesAdapter = new DocServicesListAdapter(mContext, null);
        servicesListView.setAdapter(mServicesAdapter);
       /* ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mContext, R.layout.global_item_simple_spinner, mClickedDoctorObject.getClinicDataList());

        mClinicNameSpinner.setAdapter(arrayAdapter);*/
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

    @OnClick({R.id.backImageView, R.id.titleTextView, R.id.userInfoTextView,R.id.readMoreDocServices})
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
        }
    }

    public void showServiceDialog() {
        mBottomSheetDialog = new BottomSheetDialog(mContext, R.style.Material_App_BottomSheetDialog);
        View v = getLayoutInflater().inflate(R.layout.services_dialog_modal, null);
        ///  CommonMethods.setBackground(v, new ThemeDrawable(R.array.bg_window));
        mBottomSheetDialog.setTitle("Services");
        mBottomSheetDialog.heightParam(ViewGroup.LayoutParams.MATCH_PARENT);
        ListView mServicesListView = (ListView) v.findViewById(R.id.servicesListView);
       /* DialogServicesListAdapter mServicesAdapter = new DialogServicesListAdapter(mContext, clinicData.getDocServices());
        mServicesListView.setAdapter(mServicesAdapter);*/
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
            Drawable leftDrawable = AppCompatResources.getDrawable(mContext, R.drawable.services_dot);
            dataView.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);

            dataView.setText("" + data);
            return view;
        }
    }

    public class DocServicesListAdapter extends BaseAdapter {
        Context mContext;
        private ArrayList<String> mDocServiceList;
        private String[] mStringList = {"Memory Loss","Parkinson","Alzheimer"};


        DocServicesListAdapter(Context context, ArrayList<String> items) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mStringList.length;
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
                view = layoutInflater.inflate(R.layout.global_item_simple_spinner, null);
            }
            CustomTextView dataView = (CustomTextView) view.findViewById(R.id.servicestextView);
            dataView.setText("" + mStringList[position]);
            return view;
        }
    }


}
