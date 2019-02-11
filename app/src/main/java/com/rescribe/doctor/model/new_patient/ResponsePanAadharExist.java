package com.rescribe.doctor.model.new_patient;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rescribe.doctor.interfaces.CustomResponse;
import com.rescribe.doctor.model.Common;

public class ResponsePanAadharExist implements CustomResponse {

    @SerializedName("common")
    @Expose
    private Common common;
    @SerializedName("data")
    @Expose
    private ResponseIsExist responseIsExist;

    public Common getCommon() {
        return common;
    }

    public void setCommon(Common common) {
        this.common = common;
    }


    public ResponseIsExist getResponseIsExist() {
        return responseIsExist;
    }

    public void setResponseIsExist(ResponseIsExist responseIsExist) {
        this.responseIsExist = responseIsExist;
    }

    public class ResponseIsExist {

        @SerializedName("isExists")
        @Expose
        private boolean isExists;

        public boolean isIsExists() {
            return isExists;
        }

        public void setIsExists(boolean isExists) {
            this.isExists = isExists;
        }

    }

}

