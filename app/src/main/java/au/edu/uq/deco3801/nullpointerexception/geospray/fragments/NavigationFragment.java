package au.edu.uq.deco3801.nullpointerexception.geospray.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

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

import au.edu.uq.deco3801.nullpointerexception.geospray.MainActivity;
import au.edu.uq.deco3801.nullpointerexception.geospray.MarkerInfoWindowAdapter;
import au.edu.uq.deco3801.nullpointerexception.geospray.Place;
import au.edu.uq.deco3801.nullpointerexception.geospray.R;
import au.edu.uq.deco3801.nullpointerexception.geospray.helpers.FirebaseManager;
import au.edu.uq.deco3801.nullpointerexception.geospray.rendering.PlaceRenderer;

/**
 * Google maps navigation fragment. Shows the user the location of all images on Google maps.
 * The user is able to tap on an image to either navigate to it or resolve the image in AR.
 * <br/>
 * Code adapted from Google Maps Platform 101 Codelab, available at:
 * <a href="https://github.com/googlecodelabs/maps-platform-101-android">
 *     https://github.com/googlecodelabs/maps-platform-101-android</a>
 */
public class NavigationFragment extends Fragment {
    public static final String TAG = NavigationFragment.class.getName();

    private List<Place> places = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationProviderClient;
    private int shortCode;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        FirebaseManager firebaseManager = new FirebaseManager(context);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        // Get any arguments from preview page redirect
        Bundle args = this.getArguments();

        if (args != null) {
            shortCode = args.getInt("shortcode");
        }

        // Retrieve the location of all images
        firebaseManager.getRootRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    // Don't try and retrieve lat/long from short code index
                    if (Objects.equals(child.getKey(), "next_short_code")) {
                        continue;
                    }

                    Place place = new Place(Integer.parseInt(child.getKey()),
                            child.child("title").getValue(String.class),
                            new LatLng(child.child("lat").getValue(Double.class),
                                    child.child("long").getValue(Double.class)));

                    if (!places.contains(place)) {
                        places.add(place);
                    }
                }

                SupportMapFragment map = (SupportMapFragment) getChildFragmentManager()
                        .findFragmentById(R.id.map);

                if (map != null) {
                    map.getMapAsync(googleMap -> {
                        // Default camera to Australia
                        LatLng defaultLatLng = new LatLng(-25, 135);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(defaultLatLng));

                        addClusteredMarkers(googleMap);

                        // Redirect the camera to the user's location if not redirected from the
                        // preview page.
                        if (shortCode != 0) {
                            return;
                        }

                        if (ActivityCompat.checkSelfPermission(requireContext(),
                                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(requireContext(),
                                android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                        PackageManager.PERMISSION_GRANTED) {
                            return;
                        }

                        fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY,
                                null).addOnSuccessListener(
                                requireActivity(), location -> {
                                    if (location != null) {
                                        LatLng latLng = new LatLng(location.getLatitude(),
                                                location.getLongitude());
                                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,
                                                15.0f));
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.navigation_fragment, container, false);

        // Set back button behaviour
        rootView.findViewById(R.id.navigation_page_back).setOnClickListener(view ->
                getParentFragmentManager().popBackStack());

        return rootView;
    }

    /**
     * Adds the location of all images in the database to a Google map view, then clusters them
     * (combine them all into one) if there are too many in one location.
     *
     * @param googleMap The Google map to add markers to.
     */
    private void addClusteredMarkers(GoogleMap googleMap) {
        ClusterManager<Place> clusterManager = new ClusterManager<>(requireContext(), googleMap);
        clusterManager.setRenderer(new PlaceRenderer(getContext(), googleMap, clusterManager));

        clusterManager.getMarkerCollection().setInfoWindowAdapter(new MarkerInfoWindowAdapter(
                getContext()));

        clusterManager.setOnClusterItemInfoWindowClickListener(item -> {
            // Send to camera page
            Bundle args = new Bundle();
            args.putInt("shortcode", item.getShortCode());

            CloudAnchorFragment cloudAnchorFragment = new CloudAnchorFragment();
            cloudAnchorFragment.setArguments(args);

            ((MainActivity) requireActivity()).replaceFrag(cloudAnchorFragment);
        });

        clusterManager.addItems(places);
        clusterManager.cluster();

        // Zoom view to image from preview page (if applicable)
        for (Place place : places) {
            if (place.getShortCode() == shortCode) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getPosition(),
                        20.0f));
            }
        }

        googleMap.setOnCameraIdleListener(clusterManager);

        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Show user's current location on the map
        googleMap.setMyLocationEnabled(true);
    }
}
