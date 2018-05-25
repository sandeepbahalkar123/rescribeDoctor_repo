package com.rescribe.doctor.model.patient.add_new_patient.address_other_details.reference_details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;

public class DoctorData implements CustomResponse {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("doctor_name")
    @Expose
    private String docName;
    @SerializedName("doc_email")
    @Expose
    private String docEmail;
    @SerializedName("doc_phone")
    @Expose
    private String docPhone;
    @SerializedName("doctor_image_path")
    @Expose
    private String docImagePath;
    private String spannableSearchedText;

    public String getSpannableSearchedText() {
        return spannableSearchedText;
    }

    public void setSpannableSearchedText(String spannableSearchedText) {
        this.spannableSearchedText = spannableSearchedText;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getDocEmail() {
        return docEmail;
    }

    public void setDocEmail(String docEmail) {
        this.docEmail = docEmail;
    }

    public String getDocPhone() {
        return docPhone;
    }

    public void setDocPhone(String docPhone) {
        this.docPhone = docPhone;
    }

    public String getDocImagePath() {
        return docImagePath;
    }

    public void setDocImagePath(String docImagePath) {
        this.docImagePath = docImagePath;
    }
}
