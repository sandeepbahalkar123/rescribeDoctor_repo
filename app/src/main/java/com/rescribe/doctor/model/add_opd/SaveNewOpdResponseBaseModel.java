package com.rescribe.doctor.model.add_opd;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.model.Common;

public class SaveNewOpdResponseBaseModel implements Parcelable, CustomResponse {

    @SerializedName("common")
    @Expose
    private Common common;
    @SerializedName("data")
    @Expose
    private SaveNewOpdResponse saveNewOpdResponse;
    public final static Parcelable.Creator<SaveNewOpdResponseBaseModel> CREATOR = new Creator<SaveNewOpdResponseBaseModel>() {


        @SuppressWarnings({
                "unchecked"
        })
        public SaveNewOpdResponseBaseModel createFromParcel(Parcel in) {
            return new SaveNewOpdResponseBaseModel(in);
        }

        public SaveNewOpdResponseBaseModel[] newArray(int size) {
            return (new SaveNewOpdResponseBaseModel[size]);
        }

    };

    protected SaveNewOpdResponseBaseModel(Parcel in) {
        this.common = ((Common) in.readValue((Common.class.getClassLoader())));
        this.saveNewOpdResponse = ((SaveNewOpdResponse) in.readValue((SaveNewOpdResponse.class.getClassLoader())));
    }

    public SaveNewOpdResponseBaseModel() {
    }

    public Common getCommon() {
        return common;
    }

    public void setCommon(Common common) {
        this.common = common;
    }

    public SaveNewOpdResponse getSaveNewOpdResponse() {
        return saveNewOpdResponse;
    }

    public void setSaveNewOpdResponse(SaveNewOpdResponse saveNewOpdResponse) {
        this.saveNewOpdResponse = saveNewOpdResponse;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(common);
        dest.writeValue(saveNewOpdResponse);
    }

    public int describeContents() {
        return 0;
    }

}