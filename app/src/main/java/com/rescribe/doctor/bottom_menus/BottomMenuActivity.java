package com.rescribe.doctor.bottom_menus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.rescribe.doctor.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


@SuppressWarnings("unused")
@SuppressLint("Registered")
public class BottomMenuActivity extends AppCompatActivity implements BottomMenuAdapter.OnBottomMenuClickListener {

    private static final long ANIMATION_DUR = 300;
    @Nullable
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private BottomMenuAdapter bottomMenuAdapter;
    public ArrayList<BottomMenu> bottomMenus = new ArrayList<>();
    private int mPosition;
    private ColorGenerator mColorGenerator;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.bottom_menu_activity);
        ButterKnife.bind(this);
        init();

    }

    private void init() {
        mContext = BottomMenuActivity.this;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        recyclerView.setLayoutParams(params);
        recyclerView.setClipToPadding(false);
        recyclerView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black));
        int widthPixels = Resources.getSystem().getDisplayMetrics().widthPixels;
        mColorGenerator = ColorGenerator.MATERIAL;
        createBottomMenu();
    }



    public void createBottomMenu() {
        for(int i = 0;i<4;i++){
            BottomMenu bottomMenu = new BottomMenu();
            if (i== 0){
                bottomMenu.setMenuName(getString(R.string.home));
            }else if(i ==1){
                bottomMenu.setMenuName(getString(R.string.profile));
            }else if(i ==2){
                bottomMenu.setMenuName(getString(R.string.app_logo));
            }else if(i ==3){
                bottomMenu.setMenuName(getString(R.string.settings));
            }else if(i ==4){
                bottomMenu.setMenuName(getString(R.string.support));
            }
            bottomMenus.add(bottomMenu);
        }
        bottomMenuAdapter = new BottomMenuAdapter(this, bottomMenus);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(bottomMenuAdapter);


    }

    public void addBottomMenu(BottomMenu bottomMenu) {
        bottomMenus.add(bottomMenu);
        bottomMenuAdapter.notifyItemInserted(bottomMenus.size() - 1);
    }

    /* public void addBottomSheetMenu(BottomSheetMenu bottomSheetMenu) {
         bottomSheetMenus.add(bottomSheetMenu);
     }
 */
    @Override
    public void onBackPressed() {

       /* if (isOpen)
            closeSheet();
        else*/
        super.onBackPressed();
    }

  /*  @Override
    public void onBottomSheetMenuClick(BottomSheetMenu bottomMenu) {
        if (isOpen)
            closeSheet();
    }*/

    @Override
    public void onBottomMenuClick(BottomMenu bottomMenu) {
      /*  if (bottomMenu.getMenuName().equalsIgnoreCase(getString(R.string.app_logo))) {
            if (isOpen)
                closeSheet();
            else
                openSheet();
        } else if (isOpen) {
            closeSheet();
        }*/
    }

    @Override
    public void onProfileImageClick() {

    }

    public void doNotifyDataSetChanged() {
        bottomMenuAdapter.notifyDataSetChanged();
    }
}