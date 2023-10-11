package au.edu.uq.deco3801.nullpointerexception.geospray;

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

import au.edu.uq.deco3801.nullpointerexception.geospray.helpers.FirebaseManager;
import au.edu.uq.deco3801.nullpointerexception.geospray.helpers.SnackbarHelper;
import au.edu.uq.deco3801.nullpointerexception.geospray.rendering.gallery_adapter;
import au.edu.uq.deco3801.nullpointerexception.geospray.rendering.gallery_image;

public class ImageGalleryFragment extends Fragment {
    private static final String TAG = ImageGalleryFragment.class.getName();

    private SnackbarHelper messageSnackbarHelper;
    private RecyclerView recyclerView;
    private gallery_adapter adapter;
    private ArrayList<gallery_image> gallery_images;
    private FirebaseManager firebaseManager;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    // Args
    private Bundle args;
    private int shortCode;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        messageSnackbarHelper = new SnackbarHelper();
        firebaseManager = new FirebaseManager(context);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        gallery_images = new ArrayList<>();

        adapter = new gallery_adapter(context, gallery_images);

        args = this.getArguments();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.image_gallery, container, false);

        SwipeRefreshLayout refreshLayout = rootView.findViewById(R.id.refresh_gallery2);
        refreshLayout.setOnRefreshListener(() -> refreshLayout.setRefreshing(false));

        recyclerView = rootView.findViewById(R.id.gallery_recycler);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        firebaseManager.getRootRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    StorageReference imageReference = storageReference.child("images/" + child.getKey());
                    imageReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(
                            bytes -> {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                                if (child.getKey() != null) {
                                    gallery_image image = new gallery_image(Integer.parseInt(child.getKey()), bitmap);

                                    if (!gallery_images.contains(image)) {
                                        gallery_images.add(image);
                                    }
                                }
                            }
                    );
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(
                        TAG,
                        "The Firebase operation for addMarkers was cancelled.",
                        error.toException());
            }
        });

        if (args != null) {
            shortCode = args.getInt("shortcode");
        }

        return rootView;
    }
}
