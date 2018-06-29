
package com.rescribe.doctor.model.select_slot_book_appointment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TimeSlotsInfoList {

    @SerializedName("time")
    @Expose
    private String slotName;
    @SerializedName("description")
    @Expose
    private String slotDescription;
    @SerializedName("timeslot")
    @Expose
    private ArrayList<TimeSlotData> timeSlotList = null;

    public String getSlotName() {
        return slotName;
    }

    public void setSlotName(String slotName) {
        this.slotName = slotName;
    }

    public String getSlotDescription() {
        return slotDescription;
    }

    public void setSlotDescription(String slotDescription) {
        this.slotDescription = slotDescription;
    }

    public ArrayList<TimeSlotData> getTimeSlotList() {
        return timeSlotList;
    }

    public void setTimeSlotList(ArrayList<TimeSlotData> timeSlotList) {
        this.timeSlotList = timeSlotList;
    }
}
