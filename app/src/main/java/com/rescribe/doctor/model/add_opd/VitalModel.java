package com.rescribe.doctor.model.add_opd;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VitalModel {

@SerializedName("colName")
@Expose
private String colName;
@SerializedName("value")
@Expose
private int value;

public String getColName() {
return colName;
}

public void setColName(String colName) {
this.colName = colName;
}

public int getValue() {
return value;
}

public void setValue(int value) {
this.value = value;
}

}