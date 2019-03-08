package com.rescribe.doctor.model.add_opd;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;

public class OpdTabHeader implements Parcelable, CustomResponse {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("header_label")
    @Expose
    private String headerLabel;
    @SerializedName("permission")
    @Expose
    private String permission;
    @SerializedName("visible")
    @Expose
    private boolean visible;
    @SerializedName("mandatory")
    @Expose
    private boolean mandatory;
    public final static Parcelable.Creator<OpdTabHeader> CREATOR = new Creator<OpdTabHeader>() {


        @SuppressWarnings({
                "unchecked"
        })
        public OpdTabHeader createFromParcel(Parcel in) {
            return new OpdTabHeader(in);
        }

        public OpdTabHeader[] newArray(int size) {
            return (new OpdTabHeader[size]);
        }

    };

    protected OpdTabHeader(Parcel in) {
        this.id = ((int) in.readValue((int.class.getClassLoader())));
        this.headerLabel = ((String) in.readValue((String.class.getClassLoader())));
        this.permission = ((String) in.readValue((String.class.getClassLoader())));
        this.visible = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.mandatory = ((boolean) in.readValue((boolean.class.getClassLoader())));
    }

    public OpdTabHeader() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHeaderLabel() {
        return headerLabel;
    }

    public void setHeaderLabel(String headerLabel) {
        this.headerLabel = headerLabel;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(headerLabel);
        dest.writeValue(permission);
        dest.writeValue(visible);
        dest.writeValue(mandatory);
    }

    public int describeContents() {
        return 0;
    }

}