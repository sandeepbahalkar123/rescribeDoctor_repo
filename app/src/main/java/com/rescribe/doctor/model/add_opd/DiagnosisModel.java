package com.rescribe.doctor.model.add_opd;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DiagnosisModel {

@SerializedName("id")
@Expose
private int id;
@SerializedName("name")
@Expose
private String name;
@SerializedName("type")
@Expose
private String type;
@SerializedName("isPrimary")
@Expose
private int isPrimary;

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

public int getIsPrimary() {
return isPrimary;
}

public void setIsPrimary(int isPrimary) {
this.isPrimary = isPrimary;
}

}