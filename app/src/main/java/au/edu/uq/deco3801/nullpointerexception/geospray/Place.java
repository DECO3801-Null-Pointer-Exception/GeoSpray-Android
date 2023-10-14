package au.edu.uq.deco3801.nullpointerexception.geospray;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.maps.android.clustering.ClusterItem;

public class Place implements ClusterItem {
    private final int shortCode;
    private final String title;
    private final String location;
    private final LatLng position;
    private Bitmap preview;

    public Place(int shortCode, String title, String location, LatLng position) {
        this.shortCode = shortCode;
        this.title = title;
        this.location = location;
        this.position = position;

        StorageReference imageReference = FirebaseStorage.getInstance().getReference().child("previews/" + shortCode);
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

    public int getShortCode() {
        return shortCode;
    }

    public Bitmap getPreview() {
        return preview;
    }

    public String getLocation() {
        return location;
    }
}
