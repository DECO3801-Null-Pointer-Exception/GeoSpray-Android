package au.edu.uq.deco3801.nullpointerexception.geospray.rendering;

import android.graphics.Bitmap;

public class GalleryImage {
    int shortCode;
    Bitmap img;

    public GalleryImage(int shortCode, Bitmap img) {
        this.shortCode = shortCode;
        this.img = img;
    }

    public int getShortCode() {
        return shortCode;
    }

    public Bitmap getImg() {
        return img;
    }
}
