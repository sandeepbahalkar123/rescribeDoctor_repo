package com.rescribe.doctor.model.new_patient;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BloodGroup {

@SerializedName("id")
@Expose
private int id;
@SerializedName("blood_group")
@Expose
private String bloodGroup;

public int getId() {
return id;
}

public void setId(int id) {
this.id = id;
}

public String getBloodGroup() {
return bloodGroup;
}

public void setBloodGroup(String bloodGroup) {
this.bloodGroup = bloodGroup;
}

}