package au.edu.uq.deco3801.nullpointerexception.geospray.rendering;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import au.edu.uq.deco3801.nullpointerexception.geospray.Place;

/**
 * Class used to set cluster properties.
 */
public class PlaceRenderer extends DefaultClusterRenderer<Place> {
    public PlaceRenderer(Context context, GoogleMap map, ClusterManager<Place> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(Place item, MarkerOptions markerOptions) {
        markerOptions.title(item.getTitle())
                .position(item.getPosition());
    }

    @Override
    protected void onClusterItemRendered(Place clusterItem, Marker marker) {
        marker.setTag(clusterItem);
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<Place> cluster) {
        // Cluster if there are 20 or more nearby points
        return cluster.getSize() >= 20;
    }
}
