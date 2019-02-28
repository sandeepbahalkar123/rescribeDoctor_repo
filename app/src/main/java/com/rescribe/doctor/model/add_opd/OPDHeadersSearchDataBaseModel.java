package com.rescribe.doctor.model.add_opd;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.model.Common;

import java.util.ArrayList;

public class OPDHeadersSearchDataBaseModel implements Parcelable, CustomResponse {

    @SerializedName("common")
    @Expose
    private Common common;
    @SerializedName("data")
    @Expose
    private ArrayList<OpdSearch> opdSearchArrayList = null;
    public final static Parcelable.Creator<OPDHeadersSearchDataBaseModel> CREATOR = new Creator<OPDHeadersSearchDataBaseModel>() {


        @SuppressWarnings({
                "unchecked"
        })
        public OPDHeadersSearchDataBaseModel createFromParcel(Parcel in) {
            return new OPDHeadersSearchDataBaseModel(in);
        }

        public OPDHeadersSearchDataBaseModel[] newArray(int size) {
            return (new OPDHeadersSearchDataBaseModel[size]);
        }

    };

    protected OPDHeadersSearchDataBaseModel(Parcel in) {
        this.common = ((Common) in.readValue((Common.class.getClassLoader())));
        in.readList(this.opdSearchArrayList, (OpdSearch.class.getClassLoader()));
    }

    public OPDHeadersSearchDataBaseModel() {
    }

    public Common getCommon() {
        return common;
    }

    public void setCommon(Common common) {
        this.common = common;
    }

    public ArrayList<OpdSearch> getOpdSearchArrayList() {
        return opdSearchArrayList;
    }

    public void setOpdSearchArrayList(ArrayList<OpdSearch> opdSearchArrayList) {
        this.opdSearchArrayList = opdSearchArrayList;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(common);
        dest.writeList(opdSearchArrayList);
    }

    public int describeContents() {
        return 0;
    }

}