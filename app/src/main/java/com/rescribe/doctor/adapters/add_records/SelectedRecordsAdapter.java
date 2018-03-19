package com.rescribe.doctor.adapters.add_records;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.rescribe.doctor.R;
import com.rescribe.doctor.model.investigation.Image;
import com.rescribe.doctor.ui.customesViews.EditTextWithDeleteButton;
import com.rescribe.doctor.util.RescribeConstants;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectedRecordsAdapter extends RecyclerView.Adapter<SelectedRecordsAdapter.FileViewHolder> {

    private final ArrayList<Image> mPaths;
    private final Context mContext;
    private int mImageSize;
    private OnClickOfComponentsOnSelectedPhoto mOnClickOfComponentsOnSelectedPhoto;

    public SelectedRecordsAdapter(Context context, ArrayList<Image> paths, OnClickOfComponentsOnSelectedPhoto mOnClickOfComponentsOnSelectedPhoto) {
        this.mContext = context;
        this.mPaths = paths;
        this.mOnClickOfComponentsOnSelectedPhoto = mOnClickOfComponentsOnSelectedPhoto;
        setColumnNumber(context, 2);
    }

    private void setColumnNumber(Context context, int columnNum) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        mImageSize = (widthPixels / columnNum) - context.getResources().getDimensionPixelSize(R.dimen.dp30);
    }

    @Override
    public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.selected_records_item_layout, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FileViewHolder holder, final int position) {
        final Image image = mPaths.get(position);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.dontAnimate();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(true);
        requestOptions.override(mImageSize, mImageSize);
        requestOptions.error(R.drawable.ic_file);
        requestOptions.placeholder(R.drawable.ic_file);

        Glide.with(mContext)
                .load(new File(image.getImagePath()))
                .apply(requestOptions).thumbnail(0.5f)
                .into(holder.ivPhoto);
        if (image.isUploading() == RescribeConstants.FILE_STATUS.UPLOADING) {
            holder.progressBarLay.setVisibility(View.VISIBLE);
            holder.crossImageView.setVisibility(View.GONE);
            holder.retryButton.setVisibility(View.GONE);
        } else if (image.isUploading() == RescribeConstants.FILE_STATUS.FAILED) {
            holder.progressBarLay.setVisibility(View.GONE);
            holder.retryButton.setVisibility(View.VISIBLE);
            holder.crossImageView.setVisibility(View.GONE);
        } else if (image.isUploading() == RescribeConstants.FILE_STATUS.COMPLETED) {
            holder.progressBarLay.setVisibility(View.GONE);
            holder.crossImageView.setVisibility(View.GONE);
            holder.retryButton.setVisibility(View.GONE);
        } else {
            holder.progressBarLay.setVisibility(View.GONE);
            holder.crossImageView.setVisibility(View.VISIBLE);
            holder.retryButton.setVisibility(View.GONE);
        }
        holder.retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add retry code
            //    mOnClickOfComponentsOnSelectedPhoto.uploadImage(mainPosition + "_" + position, image);
            }
        });

        //holder.removeCheckbox.setChecked(image.isSelected());
        holder.addCaptionText.addTextChangedListener(new EditTextWithDeleteButton.TextChangedListener() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
               /* if(holder.addCaptionText.getText().toString().equals("Add caption")) {
                    image.setParentCaption("");
                }else{
                    image.setParentCaption(holder.addCaptionText.getText().toString());
                }*/
                image.setParentCaption(s.toString());
                notifyItemChanged(position);
            }
        });
        holder.crossImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickOfComponentsOnSelectedPhoto.onClickOfCrossImage(position);
            }
        });

        /*holder.removeCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (image.isSelected())
                    image.setSelected(false);
                else
                    image.setSelected(true);

                notifyItemChanged(position);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return mPaths.size();
    }

    static class FileViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_photo)
        ImageView ivPhoto;
        @BindView(R.id.retryButton)
        Button retryButton;
        @BindView(R.id.progress_bar)
        ProgressBar progressBar;
        @BindView(R.id.progress_bar_lay)
        RelativeLayout progressBarLay;
        @BindView(R.id.addCaptionText)
        EditText addCaptionText;
        @BindView(R.id.crossImageView)
        ImageView crossImageView;
        @BindView(R.id.item_layout)
        RelativeLayout itemLayout;

        FileViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public ArrayList<Image> getAdapterList() {
        return mPaths;
    }

    public interface OnClickOfComponentsOnSelectedPhoto {
        public void onClickOfCaptionEditext();

        public void onClickOfCrossImage(int position);

        void uploadImage(String uploadId, Image image);

    }
}
