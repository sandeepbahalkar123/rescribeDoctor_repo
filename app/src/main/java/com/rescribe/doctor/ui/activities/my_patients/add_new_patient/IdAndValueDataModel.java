package com.rescribe.doctor.ui.activities.my_patients.add_new_patient;

import android.os.Parcel;
import android.os.Parcelable;

public class IdAndValueDataModel implements Parcelable {

    public final static Creator<IdAndValueDataModel> CREATOR = new Creator<IdAndValueDataModel>() {


        @SuppressWarnings({"unchecked"})
        public IdAndValueDataModel createFromParcel(Parcel in) {
            return new IdAndValueDataModel(in);
        }

        public IdAndValueDataModel[] newArray(int size) {
            return (new IdAndValueDataModel[size]);
        }

    };

    public IdAndValueDataModel() {

    }

    protected IdAndValueDataModel(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.idValue = ((String) in.readValue((String.class.getClassLoader())));
    }

    private int id;

    private String idValue;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String spannableSearchedText;

    public String getIdValue() {
        return idValue;
    }

    public void setIdValue(String idValue) {
        this.idValue = idValue;
    }

    public String getSpannableSearchedText() {
        return spannableSearchedText;
    }

    public void setSpannableSearchedText(String spannableSearchedText) {
        this.spannableSearchedText = spannableSearchedText;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(idValue);
    }
}
