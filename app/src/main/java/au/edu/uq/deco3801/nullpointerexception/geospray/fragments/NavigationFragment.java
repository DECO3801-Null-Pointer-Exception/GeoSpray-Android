package au.edu.uq.deco3801.nullpointerexception.geospray.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import au.edu.uq.deco3801.nullpointerexception.geospray.MarkerInfoWindowAdapter;
import au.edu.uq.deco3801.nullpointerexception.geospray.Place;
import au.edu.uq.deco3801.nullpointerexception.geospray.R;
import au.edu.uq.deco3801.nullpointerexception.geospray.helpers.FirebaseManager;
import au.edu.uq.deco3801.nullpointerexception.geospray.rendering.PlaceRenderer;

public class NavigationFragment extends Fragment {
    public static final String TAG = NavigationFragment.class.getName();
    private List<Place> places = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        FirebaseManager firebaseManager = new FirebaseManager(context);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        firebaseManager.getRootRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    // Don't try and retrieve lat/long from short code index
                    if (Objects.equals(child.getKey(), "next_short_code")) {
                        continue;
                    }

                    Place place = new Place(child.getKey(), new LatLng(
                            child.child("lat").getValue(Double.class),
                            child.child("long").getValue(Double.class)));

                    if (!places.contains(place)) {
                        places.add(place);
                    }
                }

                SupportMapFragment map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

                if (map != null) {
                    map.getMapAsync(googleMap -> {
                        addClusteredMarkers(googleMap);

                        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }

                        fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener(
                                requireActivity(), location -> {
                                    if (location != null) {
                                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
                                    }
                                }
                        );
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(
                        TAG,
                        "The Firebase operation for addMarkers was cancelled.",
                        error.toException());
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.navigation_fragment, container, false);
    }

    private void addClusteredMarkers(GoogleMap googleMap) {
        ClusterManager<Place> clusterManager = new ClusterManager<>(requireContext(), googleMap);
        clusterManager.setRenderer(new PlaceRenderer(getContext(), googleMap, clusterManager));

        clusterManager.getMarkerCollection().setInfoWindowAdapter(new MarkerInfoWindowAdapter(getContext()));

        clusterManager.addItems(places);
        clusterManager.cluster();

        googleMap.setOnCameraIdleListener(clusterManager);
    }
}
