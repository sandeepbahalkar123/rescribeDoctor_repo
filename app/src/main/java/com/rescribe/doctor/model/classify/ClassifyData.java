package com.rescribe.doctor.model.classify;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;

public class ClassifyData implements Parcelable, CustomResponse {

    public final static Parcelable.Creator<ClassifyData> CREATOR = new Creator<ClassifyData>() {


        @SuppressWarnings({
                "unchecked"
        })
        public ClassifyData createFromParcel(Parcel in) {
            return new ClassifyData(in);
        }

        public ClassifyData[] newArray(int size) {
            return (new ClassifyData[size]);
        }

    };
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("doc_id")
    @Expose
    private String docId;
    @SerializedName("hospital_name")
    @Expose
    private String hospitalName;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("months")
    @Expose
    private String months;
    @SerializedName("patient_name")
    @Expose
    private String patientName;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("trigger")
    @Expose
    private String trigger;
    @SerializedName("capacity")
    @Expose
    private String capacity;
    @SerializedName("consult trigger")
    @Expose
    private String consultTrigger;
    @SerializedName("dose")
    @Expose
    private String dose;
    @SerializedName("drugname")
    @Expose
    private String drugname;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("medtype")
    @Expose
    private String medtype;
    @SerializedName("whentotake")
    @Expose
    private String whentotake;
    @SerializedName("timeslot")
    @Expose
    private String timeslot;

    protected ClassifyData(Parcel in) {
        this.date = ((String) in.readValue((String.class.getClassLoader())));
        this.docId = ((String) in.readValue((String.class.getClassLoader())));
        this.hospitalName = ((String) in.readValue((String.class.getClassLoader())));
        this.location = ((String) in.readValue((String.class.getClassLoader())));
        this.months = ((String) in.readValue((String.class.getClassLoader())));
        this.patientName = ((String) in.readValue((String.class.getClassLoader())));
        this.time = ((String) in.readValue((String.class.getClassLoader())));
        this.trigger = ((String) in.readValue((String.class.getClassLoader())));
        this.capacity = ((String) in.readValue((String.class.getClassLoader())));
        this.consultTrigger = ((String) in.readValue((String.class.getClassLoader())));
        this.dose = ((String) in.readValue((String.class.getClassLoader())));
        this.drugname = ((String) in.readValue((String.class.getClassLoader())));
        this.duration = ((String) in.readValue((String.class.getClassLoader())));
        this.medtype = ((String) in.readValue((String.class.getClassLoader())));
        this.whentotake = ((String) in.readValue((String.class.getClassLoader())));
        this.timeslot = ((String) in.readValue((String.class.getClassLoader())));
    }

    public ClassifyData() {
    }

    public String getDate() {
        return date == null ? "" : date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDocId() {
        return docId == null ? "" : docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getHospitalName() {
        return hospitalName == null ? "" : hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getLocation() {
        return location == null ? "" : location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMonths() {
        return months == null ? "" : months;
    }

    public void setMonths(String months) {
        this.months = months;
    }

    public String getPatientName() {
        return patientName == null ? "" : patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getTime() {
        return time == null ? "" : time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTrigger() {
        return trigger == null ? "" : trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public String getCapacity() {
        return capacity == null ? "" : capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getConsultTrigger() {
        return consultTrigger == null ? "" : consultTrigger;
    }

    public void setConsultTrigger(String consultTrigger) {
        this.consultTrigger = consultTrigger;
    }

    public String getDose() {
        return dose == null ? "" : dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public String getDrugname() {
        return drugname == null ? "" : drugname;
    }

    public void setDrugname(String drugname) {
        this.drugname = drugname;
    }

    public String getDuration() {
        return duration == null ? "" : duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getMedtype() {
        return medtype == null ? "" : medtype;
    }

    public void setMedtype(String medtype) {
        this.medtype = medtype;
    }

    public String getWhentotake() {
        return whentotake == null ? "" : whentotake;
    }

    public void setWhentotake(String whentotake) {
        this.whentotake = whentotake;
    }

    public String getTimeslot() {
        return timeslot == null ? "" : timeslot;
    }

    public void setTimeslot(String timeslot) {
        this.timeslot = timeslot;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(date);
        dest.writeValue(docId);
        dest.writeValue(hospitalName);
        dest.writeValue(location);
        dest.writeValue(months);
        dest.writeValue(patientName);
        dest.writeValue(time);
        dest.writeValue(trigger);
        dest.writeValue(capacity);
        dest.writeValue(consultTrigger);
        dest.writeValue(dose);
        dest.writeValue(drugname);
        dest.writeValue(duration);
        dest.writeValue(medtype);
        dest.writeValue(whentotake);
        dest.writeValue(timeslot);
    }

    public int describeContents() {
        return 0;
    }

}