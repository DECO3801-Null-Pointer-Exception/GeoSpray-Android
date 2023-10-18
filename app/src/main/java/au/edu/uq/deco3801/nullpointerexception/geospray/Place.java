package au.edu.uq.deco3801.nullpointerexception.geospray;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.maps.android.clustering.ClusterItem;

/**
 * A class representing a place on Google maps. A Place has an associated image uploaded by a user.
 */
public class Place implements ClusterItem {
    private final int shortCode;
    private final String title;
    private final LatLng position;
    private Bitmap preview;

    /**
     * Constructor for a Place.
     *
     * @param shortCode This place's short code.
     * @param title This place's title.
     * @param position This place's position.
     */
    public Place(int shortCode, String title, LatLng position) {
        this.shortCode = shortCode;
        this.title = title;
        this.position = position;

        // Get the associated image corresponding to the short code from the database
        StorageReference imageReference = FirebaseStorage.getInstance().getReference()
                .child("previews/" + shortCode);
        imageReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(
                bytes -> preview = BitmapFactory.decodeByteArray(bytes, 0, bytes.length)
        );
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return null;
    }

    /**
     * Returns the short code associated with this Place.
     *
     * @return This Place's short code.
     */
    public int getShortCode() {
        return shortCode;
    }

    /**
     * Returns the preview image associated with this Place.
     *
     * @return This Place's preview image.
     */
    public Bitmap getPreview() {
        return preview;
    }
}
