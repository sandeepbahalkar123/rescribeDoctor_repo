package com.rescribe.doctor.model.add_opd;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.model.Common;

import java.util.ArrayList;

public class OpdTabHeadersBaseModel implements Parcelable, CustomResponse {

    @SerializedName("common")
    @Expose
    private Common common;
    @SerializedName("data")
    @Expose
    private ArrayList<OpdTabHeader> opdTabHeaderArrayList = null;
    public final static Parcelable.Creator<OpdTabHeadersBaseModel> CREATOR = new Creator<OpdTabHeadersBaseModel>() {


        @SuppressWarnings({
                "unchecked"
        })
        public OpdTabHeadersBaseModel createFromParcel(Parcel in) {
            return new OpdTabHeadersBaseModel(in);
        }

        public OpdTabHeadersBaseModel[] newArray(int size) {
            return (new OpdTabHeadersBaseModel[size]);
        }

    };

    protected OpdTabHeadersBaseModel(Parcel in) {
        this.common = ((Common) in.readValue((Common.class.getClassLoader())));
        in.readList(this.opdTabHeaderArrayList, (OpdTabHeader.class.getClassLoader()));
    }

    public OpdTabHeadersBaseModel() {
    }

    public Common getCommon() {
        return common;
    }

    public void setCommon(Common common) {
        this.common = common;
    }


    public ArrayList<OpdTabHeader> getOpdTabHeaderArrayList() {
        return opdTabHeaderArrayList;
    }

    public void setOpdTabHeaderArrayList(ArrayList<OpdTabHeader> opdTabHeaderArrayList) {
        this.opdTabHeaderArrayList = opdTabHeaderArrayList;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(common);
        dest.writeList(opdTabHeaderArrayList);
    }

    public int describeContents() {
        return 0;
    }

}