package com.rescribe.doctor.model.my_appointments;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StatusList implements Parcelable
{

@SerializedName("statusId")
@Expose
private Integer statusId;
@SerializedName("statusName")
@Expose
private String statusName;

public final static Parcelable.Creator<StatusList> CREATOR = new Creator<StatusList>() {


@SuppressWarnings({
"unchecked"
})
public StatusList createFromParcel(Parcel in) {
return new StatusList(in);
}

public StatusList[] newArray(int size) {
return (new StatusList[size]);
}

}
;

protected StatusList(Parcel in) {
this.statusId = ((Integer) in.readValue((Integer.class.getClassLoader())));
this.statusName = ((String) in.readValue((String.class.getClassLoader())));
}

public StatusList() {
}

public Integer getStatusId() {
return statusId;
}

public void setStatusId(Integer statusId) {
this.statusId = statusId;
}

public String getStatusName() {
return statusName;
}

public void setStatusName(String statusName) {
this.statusName = statusName;
}

public void writeToParcel(Parcel dest, int flags) {
dest.writeValue(statusId);
dest.writeValue(statusName);
}

public int describeContents() {
return 0;
}

}