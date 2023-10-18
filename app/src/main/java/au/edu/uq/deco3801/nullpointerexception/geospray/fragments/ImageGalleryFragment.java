package au.edu.uq.deco3801.nullpointerexception.geospray.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import au.edu.uq.deco3801.nullpointerexception.geospray.R;
import au.edu.uq.deco3801.nullpointerexception.geospray.helpers.FirebaseManager;
import au.edu.uq.deco3801.nullpointerexception.geospray.rendering.GalleryAdapter;
import au.edu.uq.deco3801.nullpointerexception.geospray.rendering.GalleryImage;

/**
 * Scrolling image gallery fragment. Displays all artworks in the database in a vertically scrolling
 * view.
 */
public class ImageGalleryFragment extends Fragment {
    private static final String TAG = ImageGalleryFragment.class.getName();

    private RecyclerView recyclerView;
    private GalleryAdapter adapter;
    private ArrayList<GalleryImage> galleryImages;
    private FirebaseManager firebaseManager;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        firebaseManager = new FirebaseManager(context);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        galleryImages = new ArrayList<>();
        adapter = new GalleryAdapter(context, galleryImages);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.image_gallery, container, false);

        SwipeRefreshLayout refreshLayout = rootView.findViewById(R.id.refresh_gallery2);
        refreshLayout.setOnRefreshListener(() -> refreshLayout.setRefreshing(false));

        recyclerView = rootView.findViewById(R.id.gallery_recycler);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    // Retrieve all images from the database and add them to the list of images to display
                    firebaseManager.getRootRef().addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                StorageReference imageReference = storageReference.child("previews/" +
                                        child.getKey());
                                imageReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(
                                        bytes -> {
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0,
                                                    bytes.length);

                                            if (child.getKey() != null) {
                                                GalleryImage image = new GalleryImage(Integer.parseInt(
                                                        child.getKey()), bitmap);

                                                if (!galleryImages.contains(image)) {
                                                    galleryImages.add(image);
                                                    adapter.notifyDataSetChanged();
                                                }
                                            }
                                        }
                                );
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
            }
        });

        // Retrieve all images from the database and add them to the list of images to display
        firebaseManager.getRootRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    StorageReference imageReference = storageReference.child("previews/" +
                            child.getKey());
                    imageReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(
                            bytes -> {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0,
                                        bytes.length);

                                if (child.getKey() != null) {
                                    GalleryImage image = new GalleryImage(Integer.parseInt(
                                            child.getKey()), bitmap);

                                    if (!galleryImages.contains(image)) {
                                        galleryImages.add(image);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                    );
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

        return rootView;
    }
}
