
package com.rescribe.doctor.model.request_patients;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FilterParams implements Parcelable
{

    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("age")
    @Expose
    private String age;
    public final static Creator<FilterParams> CREATOR = new Creator<FilterParams>() {


        @SuppressWarnings({
            "unchecked"
        })
        public FilterParams createFromParcel(Parcel in) {
            return new FilterParams(in);
        }

        public FilterParams[] newArray(int size) {
            return (new FilterParams[size]);
        }

    }
    ;

    protected FilterParams(Parcel in) {
        this.gender = ((String) in.readValue((String.class.getClassLoader())));
        this.age = ((String) in.readValue((String.class.getClassLoader())));
    }

    public FilterParams() {
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(gender);
        dest.writeValue(age);
    }

    public int describeContents() {
        return  0;
    }

}
