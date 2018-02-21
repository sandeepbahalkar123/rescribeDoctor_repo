package com.rescribe.doctor.model.investigation;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Image implements Parcelable {

    public final static Creator<Image> CREATOR = new Creator<Image>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Image createFromParcel(Parcel in) {
            Image instance = new Image();
            instance.imageId = ((String) in.readValue((String.class.getClassLoader())));
            instance.imagePath = ((String) in.readValue((String.class.getClassLoader())));
            instance.selected = ((boolean) in.readValue((boolean.class.getClassLoader())));
            instance.parentCaption = ((String) in.readValue((String.class.getClassLoader())));
            instance.childCaption = ((String) in.readValue((String.class.getClassLoader())));
            instance.uploading = ((int) in.readValue((boolean.class.getClassLoader())));
            instance.type = ((int) in.readValue((boolean.class.getClassLoader())));

            return instance;
        }

        public Image[] newArray(int size) {
            return (new Image[size]);
        }

    };
    @SerializedName("image_id")
    @Expose
    private String imageId;
    @SerializedName("image_path")
    @Expose
    private String imagePath;
    @SerializedName("selected")
    @Expose
    private boolean selected = false;
    @SerializedName("parentCaption")
    @Expose
    private String parentCaption = "Add Caption";
    @SerializedName("childCaption")
    @Expose
    private String childCaption = "";
    @SerializedName("uploading")
    @Expose
    private int uploading = -1;

    @SerializedName("type")
    @Expose
    private int type;

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImagePath() {
        return imagePath;
}

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getParentCaption() {
        return parentCaption;
    }

    public void setParentCaption(String parentCaption) {
        this.parentCaption = parentCaption;
    }

    public String getChildCaption() {
        return childCaption;
    }

    public void setChildCaption(String childCaption) {
        this.childCaption = childCaption;
    }

    public int isUploading() {
        return uploading;
    }

    public void setUploading(int uploading) {
        this.uploading = uploading;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(imageId);
        dest.writeValue(imagePath);
        dest.writeValue(selected);
        dest.writeValue(parentCaption);
        dest.writeValue(childCaption);
        dest.writeValue(uploading);
        dest.writeValue(type);
    }

    public int describeContents() {
        return 0;
    }

}