package au.edu.uq.deco3801.nullpointerexception.geospray;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final Context context;

    public MarkerInfoWindowAdapter(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        Place place = (Place) marker.getTag();

        if (place == null) {
            return null;
        }

        View view = LayoutInflater.from(context).inflate(R.layout.marker_info_contents, null);
        ((ImageView) view.findViewById(R.id.image_view_preview)).setImageBitmap(place.getPreview());

        return view;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        return null;
    }
}
