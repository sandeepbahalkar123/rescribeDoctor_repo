package com.rescribe.doctor.model.select_slot_book_appointment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TimeSlotData {
    @SerializedName("slotId")
    @Expose
    private String slotId = "";
    @SerializedName("fromTime")
    @Expose
    private String fromTime = "";
    @SerializedName("toTime")
    @Expose
    private String toTime = "";
    @SerializedName("isAvailable")
    @Expose
    private boolean isAvailable;

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}