package au.edu.uq.deco3801.nullpointerexception.geospray;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.maps.android.clustering.ClusterItem;

public class Place implements ClusterItem {
    private final String name;
    private final LatLng latLng;
    private Bitmap preview;

    public Place(String name, LatLng latLng) {
        this.name = name;
        this.latLng = latLng;

        StorageReference imageReference = FirebaseStorage.getInstance().getReference().child("previews/" + name);
        imageReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(
                bytes -> preview = BitmapFactory.decodeByteArray(bytes, 0, bytes.length)
        );
    }

    @Override
    public LatLng getPosition() {
        return latLng;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getSnippet() {
        return null;
    }

    public Bitmap getPreview() {
        return preview;
    }
}
