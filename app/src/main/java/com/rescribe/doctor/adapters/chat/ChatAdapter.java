package com.rescribe.doctor.adapters.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.rescribe.doctor.R;
import com.rescribe.doctor.model.chat.MQTTMessage;
import com.rescribe.doctor.services.MQTTService;
import com.rescribe.doctor.ui.activities.ZoomImageViewActivity;
import com.rescribe.doctor.ui.customesViews.CustomTextView;
import com.rescribe.doctor.util.CommonMethods;
import com.rescribe.doctor.util.RescribeConstants;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.rescribe.doctor.util.RescribeConstants.COMPLETED;
import static com.rescribe.doctor.util.RescribeConstants.DOWNLOADING;
import static com.rescribe.doctor.util.RescribeConstants.FAILED;
import static com.rescribe.doctor.util.RescribeConstants.UPLOADING;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ListViewHolder> {

    private final Context context;
    private final ItemListener itemListener;
    private TextDrawable mReceiverTextDrawable;
    private TextDrawable mSelfTextDrawable;
    private ArrayList<MQTTMessage> mqttMessages;

    public ChatAdapter(ArrayList<MQTTMessage> mqttMessages, TextDrawable mSelfTextDrawable, TextDrawable mReceiverTextDrawable, Context context) {
        this.mqttMessages = mqttMessages;
        this.mSelfTextDrawable = mSelfTextDrawable;
        this.mReceiverTextDrawable = mReceiverTextDrawable;
        this.context = context;

        try {
            this.itemListener = ((ItemListener) context);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement ItemClickListener.");
        }
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_item, parent, false);

        return new ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListViewHolder holder, final int position) {
        final MQTTMessage message = mqttMessages.get(position);

        if (mqttMessages.get(position).getSender().equals(MQTTService.DOCTOR)) {
            holder.receiverLayout.setVisibility(View.GONE);
            holder.senderLayout.setVisibility(View.VISIBLE);

            if (!message.getImageUrl().equals("")) {
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.dontAnimate();
                requestOptions.override(100, 100);
                requestOptions.transform(new CircleCrop(holder.senderProfilePhoto.getContext()));
                requestOptions.placeholder(mSelfTextDrawable);
                Glide.with(holder.senderProfilePhoto.getContext())
                        .load(message.getImageUrl())
                        .apply(requestOptions).thumbnail(0.2f)
                        .into(holder.senderProfilePhoto);
            } else {
                holder.senderProfilePhoto.setImageDrawable(mSelfTextDrawable);
            }

            if (message.getFileUrl().isEmpty()) {
                holder.senderMessage.setText(message.getMsg());
                holder.senderPhotoLayout.setVisibility(View.GONE);
                holder.senderFileLayout.setVisibility(View.GONE);
                holder.senderMessage.setVisibility(View.VISIBLE);
            } else {

                holder.senderMessage.setVisibility(View.GONE);

                switch (message.getFileType()) {
                    case RescribeConstants.FILE.AUD:

                        holder.senderFileLayout.setVisibility(View.VISIBLE);

                        if (message.getUploadStatus() == RescribeConstants.UPLOADING) {
                            holder.senderFileProgressLayout.setVisibility(View.VISIBLE);
                            holder.senderFileUploadStopped.setVisibility(View.GONE);
                            holder.senderFileUploading.setVisibility(View.VISIBLE);
                        } else if (message.getUploadStatus() == FAILED) {
                            holder.senderFileProgressLayout.setVisibility(View.VISIBLE);
                            holder.senderFileUploadStopped.setVisibility(View.VISIBLE);
                            holder.senderFileUploading.setVisibility(View.GONE);
                        } else if (message.getUploadStatus() == RescribeConstants.COMPLETED) {
                            holder.senderFileProgressLayout.setVisibility(View.GONE);
                            holder.senderFileUploadStopped.setVisibility(View.GONE);
                            holder.senderFileUploading.setVisibility(View.GONE);
                        }

                        holder.senderPhotoLayout.setVisibility(View.GONE);
                        holder.senderFileExtension.setText(message.getMsg());
                        holder.senderFileIcon.setImageResource(R.drawable.ic_play_arrow_white_24dp);

                        holder.senderFileLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (message.getUploadStatus() == FAILED) {
                                    itemListener.uploadFile(message);
                                    message.setUploadStatus(UPLOADING);
                                    notifyItemChanged(position);
                                } else if (message.getUploadStatus() == COMPLETED) {
                                    // do file open stuff here
                                    try {
                                        itemListener.openFile(message, holder.senderFileIcon);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });

                        break;
                    case RescribeConstants.FILE.DOC:
                        holder.senderFileLayout.setVisibility(View.VISIBLE);

                        if (message.getUploadStatus() == RescribeConstants.UPLOADING) {
                            holder.senderFileProgressLayout.setVisibility(View.VISIBLE);
                            holder.senderFileUploadStopped.setVisibility(View.GONE);
                            holder.senderFileUploading.setVisibility(View.VISIBLE);
                        } else if (message.getUploadStatus() == FAILED) {
                            holder.senderFileProgressLayout.setVisibility(View.VISIBLE);
                            holder.senderFileUploadStopped.setVisibility(View.VISIBLE);
                            holder.senderFileUploading.setVisibility(View.GONE);
                        } else if (message.getUploadStatus() == RescribeConstants.COMPLETED) {
                            holder.senderFileProgressLayout.setVisibility(View.GONE);
                            holder.senderFileUploadStopped.setVisibility(View.GONE);
                            holder.senderFileUploading.setVisibility(View.GONE);
                        }

                        holder.senderPhotoLayout.setVisibility(View.GONE);
                        String extension = CommonMethods.getExtension(message.getFileUrl());

                        int fontSize = 26;
                        if (extension.length() > 3 && extension.length() < 5)
                            fontSize = 20;
                        else if (extension.length() > 4)
                            fontSize = 16;

                        holder.senderFileExtension.setText(message.getMsg());
                        TextDrawable fileTextDrawable = TextDrawable.builder()
                                .beginConfig()
                                .width(Math.round(holder.senderFileIcon.getResources().getDimension(R.dimen.dp34)))  // width in px
                                .height(Math.round(holder.senderFileIcon.getResources().getDimension(R.dimen.dp34))) // height in px
                                .bold()
                                .fontSize(fontSize)
                                .toUpperCase()
                                .endConfig()
                                .buildRoundRect(extension, holder.senderFileIcon.getResources().getColor(R.color.grey_500), CommonMethods.convertDpToPixel(2));
                        holder.senderFileIcon.setImageDrawable(fileTextDrawable);

                        holder.senderFileLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (message.getUploadStatus() == FAILED) {
                                    itemListener.uploadFile(message);
                                    message.setUploadStatus(UPLOADING);
                                    notifyItemChanged(position);
                                } else if (message.getUploadStatus() == COMPLETED) {
                                    // do file open stuff here
                                    try {
                                        itemListener.openFile(message, holder.senderFileIcon);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });

                        break;
                    default:

                        holder.senderPhotoLayout.setVisibility(View.VISIBLE);
                        holder.senderFileLayout.setVisibility(View.GONE);

                        if (message.getUploadStatus() == RescribeConstants.UPLOADING) {
                            holder.senderPhotoProgressLayout.setVisibility(View.VISIBLE);
                            holder.senderPhotoUploading.setVisibility(View.VISIBLE);
                            holder.senderPhotoUploadStopped.setVisibility(View.GONE);
                        } else if (message.getUploadStatus() == FAILED) {
                            holder.senderPhotoProgressLayout.setVisibility(View.VISIBLE);
                            holder.senderPhotoUploading.setVisibility(View.GONE);
                            holder.senderPhotoUploadStopped.setVisibility(View.VISIBLE);
                        } else if (message.getUploadStatus() == RescribeConstants.COMPLETED) {
                            holder.senderPhotoProgressLayout.setVisibility(View.GONE);
                            holder.senderPhotoUploading.setVisibility(View.GONE);
                            holder.senderPhotoUploadStopped.setVisibility(View.GONE);
                        }

                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.dontAnimate();
                        requestOptions.override(300, 300);
                        requestOptions.placeholder(R.drawable.image_placeholder);
                        requestOptions.error(R.drawable.image_placeholder);

                        String filePath = message.getFileUrl().substring(0, 4);

                        final boolean isUrl;
                        if (filePath.equals("http")) {

                            isUrl = true;

                            Glide.with(holder.senderPhotoThumb.getContext())
                                    .load(message.getFileUrl())
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            holder.senderPhotoProgressLayout.setVisibility(View.GONE);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            holder.senderPhotoProgressLayout.setVisibility(View.GONE);
                                            return false;
                                        }
                                    })
                                    .apply(requestOptions).thumbnail(0.2f)
                                    .into(holder.senderPhotoThumb);
                        } else {

                            isUrl = false;

                            Glide.with(holder.senderPhotoThumb.getContext())
                                    .load(new File(message.getFileUrl()))
                                    .apply(requestOptions).thumbnail(0.2f)
                                    .into(holder.senderPhotoThumb);
                        }

                        holder.senderPhotoLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (message.getUploadStatus() == FAILED) {
                                    itemListener.uploadFile(message);
                                    message.setUploadStatus(UPLOADING);
                                    notifyItemChanged(position);
                                } else if (message.getUploadStatus() == COMPLETED) {
                                    Intent intent = new Intent(context, ZoomImageViewActivity.class);
                                    intent.putExtra(RescribeConstants.DOCUMENTS, message.getFileUrl());
                                    intent.putExtra(RescribeConstants.IS_URL, isUrl);
                                    context.startActivity(intent);
                                }
                            }
                        });

                        if (message.getMsg().isEmpty())
                            holder.senderMessageWithImage.setVisibility(View.GONE);
                        else {
                            holder.senderMessageWithImage.setVisibility(View.VISIBLE);
                            holder.senderMessageWithImage.setText(message.getMsg());
                        }
                        break;
                }
            }

        } else {
            holder.receiverLayout.setVisibility(View.VISIBLE);
            holder.senderLayout.setVisibility(View.GONE);

            if (!message.getImageUrl().equals("")) {
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.dontAnimate();
                requestOptions.override(100, 100);
                requestOptions.transform(new CircleCrop(holder.receiverProfilePhoto.getContext()));
                requestOptions.placeholder(mReceiverTextDrawable);
                Glide.with(holder.receiverProfilePhoto.getContext())
                        .load(message.getImageUrl())
                        .apply(requestOptions).thumbnail(0.2f)
                        .into(holder.receiverProfilePhoto);
            } else {
                holder.receiverProfilePhoto.setImageDrawable(mReceiverTextDrawable);
            }

            if (message.getFileUrl().isEmpty()) {
                holder.receiverMessage.setText(message.getMsg());
                holder.receiverPhotoLayout.setVisibility(View.GONE);
                holder.receiverFileLayout.setVisibility(View.GONE);
                holder.receiverMessage.setVisibility(View.VISIBLE);
            } else {

                holder.receiverMessage.setVisibility(View.GONE);
                switch (message.getFileType()) {
                    case RescribeConstants.FILE.AUD: {

                        holder.receiverFileLayout.setVisibility(View.VISIBLE);
                        holder.receiverPhotoLayout.setVisibility(View.GONE);

                        if (message.getDownloadStatus() == FAILED) {
                            holder.receiverFileProgressLayout.setVisibility(View.VISIBLE);
                            holder.receiverFileDownloadStopped.setVisibility(View.VISIBLE);
                            holder.receiverFileDownloading.setVisibility(View.GONE);
                        } else if (message.getDownloadStatus() == COMPLETED) {
                            holder.receiverFileProgressLayout.setVisibility(View.GONE);
                            holder.receiverFileDownloadStopped.setVisibility(View.GONE);
                            holder.receiverFileDownloading.setVisibility(View.GONE);
                        } else if (message.getDownloadStatus() == UPLOADING) {
                            holder.receiverFileProgressLayout.setVisibility(View.VISIBLE);
                            holder.receiverFileDownloadStopped.setVisibility(View.GONE);
                            holder.receiverFileDownloading.setVisibility(View.VISIBLE);
                        }

                        holder.receiverFileExtension.setText(message.getMsg());
                        holder.receiverFileIcon.setImageResource(R.drawable.ic_play_arrow_white_24dp);

                        holder.receiverFileLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (message.getDownloadStatus() == FAILED) {
                                    itemListener.downloadFile(message);
                                    message.setDownloadStatus(DOWNLOADING);
                                    notifyItemChanged(position);
                                } else if (message.getDownloadStatus() == COMPLETED) {
                                    // open File in Viewer
                                    try {
                                        itemListener.openFile(message, holder.receiverFileIcon);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });

                        break;
                    }
                    case RescribeConstants.FILE.DOC: {

                        holder.receiverFileLayout.setVisibility(View.VISIBLE);
                        holder.receiverPhotoLayout.setVisibility(View.GONE);

                        if (message.getDownloadStatus() == FAILED) {
                            holder.receiverFileProgressLayout.setVisibility(View.VISIBLE);
                            holder.receiverFileDownloadStopped.setVisibility(View.VISIBLE);
                            holder.receiverFileDownloading.setVisibility(View.GONE);
                        } else if (message.getDownloadStatus() == COMPLETED) {
                            holder.receiverFileProgressLayout.setVisibility(View.GONE);
                            holder.receiverFileDownloadStopped.setVisibility(View.GONE);
                            holder.receiverFileDownloading.setVisibility(View.GONE);
                        } else if (message.getDownloadStatus() == UPLOADING) {
                            holder.receiverFileProgressLayout.setVisibility(View.VISIBLE);
                            holder.receiverFileDownloadStopped.setVisibility(View.GONE);
                            holder.receiverFileDownloading.setVisibility(View.VISIBLE);
                        }

                        String extension = CommonMethods.getExtension(message.getFileUrl());

                        int fontSize = 26;
                        if (extension.length() > 3 && extension.length() < 5)
                            fontSize = 20;
                        else if (extension.length() > 4)
                            fontSize = 16;

                        holder.receiverFileExtension.setText(message.getMsg());
                        TextDrawable fileTextDrawable = TextDrawable.builder()
                                .beginConfig()
                                .width(Math.round(holder.senderFileIcon.getResources().getDimension(R.dimen.dp34)))  // width in px
                                .height(Math.round(holder.senderFileIcon.getResources().getDimension(R.dimen.dp34))) // height in px
                                .bold()
                                .fontSize(fontSize)
                                .toUpperCase()
                                .endConfig()
                                .buildRoundRect(extension, holder.senderFileIcon.getResources().getColor(R.color.grey_500), CommonMethods.convertDpToPixel(3));

                        holder.receiverFileIcon.setImageDrawable(fileTextDrawable);

                        holder.receiverFileLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (message.getDownloadStatus() == FAILED) {
                                    itemListener.downloadFile(message);
                                    message.setDownloadStatus(DOWNLOADING);
                                    notifyItemChanged(position);
                                } else if (message.getDownloadStatus() == COMPLETED) {
                                    // open File in Viewer
                                    try {
                                        itemListener.openFile(message, holder.receiverFileIcon);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });

                        break;
                    }
                    default:

                        holder.receiverPhotoLayout.setVisibility(View.VISIBLE);
                        holder.receiverFileLayout.setVisibility(View.GONE);

                        holder.receiverPhotoProgressLayout.setVisibility(View.VISIBLE);
                        holder.receiverPhotoDownloading.setVisibility(View.VISIBLE);
                        holder.receiverPhotoDownloadStopped.setVisibility(View.GONE);

                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.dontAnimate();
                        requestOptions.override(300, 300);
                        requestOptions.placeholder(droidninja.filepicker.R.drawable.image_placeholder);
                        requestOptions.error(droidninja.filepicker.R.drawable.image_placeholder);
                        Glide.with(holder.receiverPhotoThumb.getContext())
                                .load(message.getFileUrl())
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        holder.receiverPhotoProgressLayout.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        holder.receiverPhotoProgressLayout.setVisibility(View.GONE);
                                        return false;
                                    }
                                })
                                .apply(requestOptions).thumbnail(0.2f)
                                .into(holder.receiverPhotoThumb);

                        holder.receiverPhotoLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, ZoomImageViewActivity.class);
                                intent.putExtra(RescribeConstants.DOCUMENTS, message.getFileUrl());
                                intent.putExtra(RescribeConstants.IS_URL, true);
                                context.startActivity(intent);
                            }
                        });

                        if (message.getMsg().isEmpty())
                            holder.receiverMessageWithImage.setVisibility(View.GONE);
                        else {
                            holder.receiverMessageWithImage.setVisibility(View.VISIBLE);
                            holder.receiverMessageWithImage.setText(message.getMsg());
                        }
                        break;
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return mqttMessages.size();
    }

    static class ListViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.senderMessage)
        TextView senderMessage;
        @BindView(R.id.senderProfilePhoto)
        ImageView senderProfilePhoto;
        @BindView(R.id.senderLayout)
        RelativeLayout senderLayout;
        @BindView(R.id.receiverProfilePhoto)
        ImageView receiverProfilePhoto;
        @BindView(R.id.receiverMessage)
        TextView receiverMessage;
        @BindView(R.id.receiverLayout)
        RelativeLayout receiverLayout;

        // Photo

        @BindView(R.id.senderPhotoThumb)
        ImageView senderPhotoThumb;
        @BindView(R.id.senderPhotoLayout)
        CardView senderPhotoLayout;
        @BindView(R.id.senderMessageWithImage)
        TextView senderMessageWithImage;

        @BindView(R.id.receiverPhotoThumb)
        ImageView receiverPhotoThumb;
        @BindView(R.id.receiverPhotoLayout)
        CardView receiverPhotoLayout;
        @BindView(R.id.receiverMessageWithImage)
        TextView receiverMessageWithImage;

        // File

        @BindView(R.id.senderFileIcon)
        ImageView senderFileIcon;
        @BindView(R.id.senderFileExtension)
        CustomTextView senderFileExtension;

        @BindView(R.id.senderFileLayout)
        RelativeLayout senderFileLayout;

        @BindView(R.id.receiverFileIcon)
        ImageView receiverFileIcon;
        @BindView(R.id.receiverFileExtension)
        CustomTextView receiverFileExtension;

        @BindView(R.id.receiverFileLayout)
        RelativeLayout receiverFileLayout;

        @BindView(R.id.receiverFileDownloading)
        RelativeLayout receiverFileDownloading;
        @BindView(R.id.receiverFileDownloadStopped)
        RelativeLayout receiverFileDownloadStopped;

        @BindView(R.id.senderFileUploading)
        RelativeLayout senderFileUploading;
        @BindView(R.id.senderFileUploadStopped)
        RelativeLayout senderFileUploadStopped;

        @BindView(R.id.senderFileProgressLayout)
        RelativeLayout senderFileProgressLayout;

        @BindView(R.id.senderPhotoProgressLayout)
        RelativeLayout senderPhotoProgressLayout;

        @BindView(R.id.receiverFileProgressLayout)
        RelativeLayout receiverFileProgressLayout;

        @BindView(R.id.receiverPhotoProgressLayout)
        RelativeLayout receiverPhotoProgressLayout;

        @BindView(R.id.receiverPhotoDownloading)
        RelativeLayout receiverPhotoDownloading;
        @BindView(R.id.receiverPhotoDownloadStopped)
        RelativeLayout receiverPhotoDownloadStopped;

        @BindView(R.id.senderPhotoUploading)
        RelativeLayout senderPhotoUploading;
        @BindView(R.id.senderPhotoUploadStopped)
        RelativeLayout senderPhotoUploadStopped;

        ListViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface ItemListener {
        void uploadFile(MQTTMessage mqttMessage);

        long downloadFile(MQTTMessage mqttMessage);

        void openFile(MQTTMessage message, ImageView senderFileIcon) throws IOException;
    }
}
