package com.rescribe.doctor.model.add_opd;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;

public class OpdSearch implements Parcelable, CustomResponse {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("type")
    @Expose
    private String type;
    public final static Parcelable.Creator<OpdSearch> CREATOR = new Creator<OpdSearch>() {


        @SuppressWarnings({
                "unchecked"
        })
        public OpdSearch createFromParcel(Parcel in) {
            return new OpdSearch(in);
        }

        public OpdSearch[] newArray(int size) {
            return (new OpdSearch[size]);
        }

    };

    protected OpdSearch(Parcel in) {
        this.id = ((int) in.readValue((int.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.type = ((String) in.readValue((String.class.getClassLoader())));
    }

    public OpdSearch() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeValue(type);
    }

    public int describeContents() {
        return 0;
    }

}