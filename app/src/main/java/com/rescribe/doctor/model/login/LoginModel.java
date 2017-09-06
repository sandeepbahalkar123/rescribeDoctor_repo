package com.rescribe.doctor.model.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.model.Common;

public class LoginModel implements CustomResponse {

    @SerializedName("common")
    @Expose
    private Common common;
    @SerializedName("authToken")
    @Expose
    private String authToken;
    @SerializedName("patientId")
    @Expose
    private String patientId;

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public Common getCommon() {
        return common;
    }

    public void setCommon(Common common) {
        this.common = common;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
/*
    public ArrayList<YearsMonthsDataList> getYearsMonthsDataList() {
        return yearsMonthsDataList;
    }

    public ArrayList<Year> getYearList() {
        ArrayList<YearsMonthsDataList> yearsMonthsDataList = getYearsMonthsDataList();
        ArrayList<Year> yearList = new ArrayList<>();
        for (YearsMonthsDataList yearObject :
                yearsMonthsDataList) {
            String[] months = yearObject.getMonths();
            if (months.length > 0) {
                for (int i = 0; i < months.length; i++) {
                    Year year = new Year();
                    year.setYear(yearObject.getYear());
                    year.setMonthName(months[i]);
                    yearList.add(year);
                }
            }
        }
        return yearList;
    }*/

}