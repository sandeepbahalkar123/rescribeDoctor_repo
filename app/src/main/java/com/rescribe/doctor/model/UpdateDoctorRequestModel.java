package com.rescribe.doctor.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;

public class UpdateDoctorRequestModel implements CustomResponse {

@SerializedName("docId")
@Expose
private int docId;
@SerializedName("doctorName")
@Expose
private String doctorName;
@SerializedName("doctorGender")
@Expose
private String doctorGender;
@SerializedName("doctorPhone")
@Expose
private String doctorPhone;
@SerializedName("doctorWebsite")
@Expose
private String doctorWebsite;
@SerializedName("doctorSpeciality")
@Expose
private String doctorSpeciality;
@SerializedName("doctorDegree")
@Expose
private String doctorDegree;
@SerializedName("doctorExperience")
@Expose
private int doctorExperience;
@SerializedName("aboutMe")
@Expose
private String aboutMe;

public int getDocId() {
return docId;
}

public void setDocId(int docId) {
this.docId = docId;
}

public String getDoctorName() {
return doctorName;
}

public void setDoctorName(String doctorName) {
this.doctorName = doctorName;
}

public String getDoctorGender() {
return doctorGender;
}

public void setDoctorGender(String doctorGender) {
this.doctorGender = doctorGender;
}

public String getDoctorPhone() {
return doctorPhone;
}

public void setDoctorPhone(String doctorPhone) {
this.doctorPhone = doctorPhone;
}

public String getDoctorWebsite() {
return doctorWebsite;
}

public void setDoctorWebsite(String doctorWebsite) {
this.doctorWebsite = doctorWebsite;
}

public String getDoctorSpeciality() {
return doctorSpeciality;
}

public void setDoctorSpeciality(String doctorSpeciality) {
this.doctorSpeciality = doctorSpeciality;
}

public String getDoctorDegree() {
return doctorDegree;
}

public void setDoctorDegree(String doctorDegree) {
this.doctorDegree = doctorDegree;
}

public int getDoctorExperience() {
return doctorExperience;
}

public void setDoctorExperience(int doctorExperience) {
this.doctorExperience = doctorExperience;
}

public String getAboutMe() {
return aboutMe;
}

public void setAboutMe(String aboutMe) {
this.aboutMe = aboutMe;
}

}