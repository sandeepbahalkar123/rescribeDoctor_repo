package com.rescribe.doctor.model.patient.add_new_patient.address_other_details.reference_details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.model.Common;
import com.rescribe.doctor.model.patient.add_new_patient.address_other_details.city_details.StateAndCityBaseModel;

import java.util.ArrayList;

public class DoctorListBaseModel implements CustomResponse {

    @SerializedName("common")
    @Expose
    private Common common;
    @SerializedName("data")
    @Expose
    private DoctorListDataModel doctorListDataModel;

    public Common getCommon() {
        return common;
    }

    public void setCommon(Common common) {
        this.common = common;
    }

    public DoctorListDataModel getDoctorListDataModel() {
        return doctorListDataModel;
    }

    public void setDoctorListDataModel(DoctorListDataModel doctorListDataModel) {
        this.doctorListDataModel = doctorListDataModel;
    }

    public class DoctorListDataModel implements CustomResponse {

        @SerializedName("doctorNames")
        @Expose
        private ArrayList<DoctorData> doctorDataList = new ArrayList<>();

        public ArrayList<DoctorData> getDoctorDataList() {
            return doctorDataList;
        }

        public void setDoctorDataList(ArrayList<DoctorData> doctorDataList) {
            this.doctorDataList = doctorDataList;
        }
    }
}
