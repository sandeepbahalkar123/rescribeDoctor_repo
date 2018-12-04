package com.rescribe.doctor.smartpen;

import android.graphics.Bitmap;

public class BitmapProps {
    private Bitmap bitmap;
    private boolean edited;

    public BitmapProps(Bitmap bitmap, boolean edited) {
        this.bitmap = bitmap;
        this.edited = edited;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }
}
