package com.rescribe.doctor.model.add_opd;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SaveNewOpdResponse implements Parcelable {

    @SerializedName("opdId")
    @Expose
    private int opdId;
    public final static Parcelable.Creator<SaveNewOpdResponse> CREATOR = new Creator<SaveNewOpdResponse>() {


        @SuppressWarnings({
                "unchecked"
        })
        public SaveNewOpdResponse createFromParcel(Parcel in) {
            return new SaveNewOpdResponse(in);
        }

        public SaveNewOpdResponse[] newArray(int size) {
            return (new SaveNewOpdResponse[size]);
        }

    };

    protected SaveNewOpdResponse(Parcel in) {
        this.opdId = ((int) in.readValue((int.class.getClassLoader())));
    }

    public SaveNewOpdResponse() {
    }

    public int getOpdId() {
        return opdId;
    }

    public void setOpdId(int opdId) {
        this.opdId = opdId;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(opdId);
    }

    public int describeContents() {
        return 0;
    }

}
