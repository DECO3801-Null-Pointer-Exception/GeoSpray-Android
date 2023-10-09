package au.edu.uq.deco3801.nullpointerexception.geospray.rendering;

import android.graphics.Bitmap;

public class gallery_image {
    int short_code;
    Bitmap img;

    public gallery_image(int short_code, Bitmap img) {
        this.short_code = short_code;
        this.img = img;
    }

    public int getShort_code() {
        return short_code;
    }

    public Bitmap getImg() {
        return img;
    }
}
