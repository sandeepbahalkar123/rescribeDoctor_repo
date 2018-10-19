package com.rescribe.doctor.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;

public class CommonBaseModelContainer implements Parcelable, CustomResponse {

    @SerializedName("common")
    @Expose
    private Common common;
    public final static Parcelable.Creator<CommonBaseModelContainer> CREATOR = new Creator<CommonBaseModelContainer>() {


        @SuppressWarnings({
                "unchecked"
        })
        public CommonBaseModelContainer createFromParcel(Parcel in) {
            return new CommonBaseModelContainer(in);
        }

        public CommonBaseModelContainer[] newArray(int size) {
            return (new CommonBaseModelContainer[size]);
        }

    };

    protected CommonBaseModelContainer(Parcel in) {
        this.common = ((Common) in.readValue((Common.class.getClassLoader())));
    }

    public CommonBaseModelContainer() {
    }

    public Common getCommonRespose() {
        return common;
    }

    public void setCommon(Common common) {
        this.common = common;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(common);
    }

    public int describeContents() {
        return 0;
    }

}