
package com.rescribe.doctor.model.patient.doctor_patients;

import com.rescribe.doctor.interfaces.CustomResponse;

public class PatientAddressDetails implements CustomResponse {

    private String patientAddress;
    private String patientArea;
    private String patientCity;
    private String patientState;


    public PatientAddressDetails() {
    }


    public String getPatientAddress() {
        return patientAddress;
    }

    public void setPatientAddress(String patientAddress) {
        this.patientAddress = patientAddress;
    }

    public String getPatientArea() {
        return patientArea;
    }

    public void setPatientArea(String patientArea) {
        this.patientArea = patientArea;
    }

    public String getPatientCity() {
        return patientCity;
    }

    public void setPatientCity(String patientCity) {
        this.patientCity = patientCity;
    }

    public String getPatientState() {
        return patientState;
    }

    public void setPatientState(String patientState) {
        this.patientState = patientState;
    }
}
