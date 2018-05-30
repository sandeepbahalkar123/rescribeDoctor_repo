package com.rescribe.doctor.model.patient.add_new_patient.address_other_details.city_details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;

public class CityData  implements CustomResponse {
    @SerializedName("cityId")
    @Expose
    private int cityId;
    @SerializedName("cityName")
    @Expose
    private String cityName;

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
