package au.edu.uq.deco3801.nullpointerexception.geospray;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import au.edu.uq.deco3801.nullpointerexception.geospray.helpers.SnackbarHelper;
import au.edu.uq.deco3801.nullpointerexception.geospray.rendering.gallery_adapter;
import au.edu.uq.deco3801.nullpointerexception.geospray.rendering.gallery_image;

public class ImageGalleryFragment extends Fragment {
    public static final String TAG = ImageGalleryFragment.class.getName();
    private final SnackbarHelper messageSnackbarHelper = new SnackbarHelper();
    RecyclerView recyclerView;
    gallery_adapter adapter;

    Bitmap image;
    ArrayList<ArrayList<gallery_image>> gallery_images = new ArrayList<>();

    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        gallery_images.add(addData());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.image_gallery, container, false);

        SwipeRefreshLayout refreshLayout = rootView.findViewById(R.id.refresh_gallery2);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
            }
        });

        recyclerView = rootView.findViewById(R.id.gallery_recycler);
        adapter = new gallery_adapter(requireContext(), gallery_images);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    gallery_images.add(addData());
                    adapter.notifyDataSetChanged();
                }
            }
        });
        return rootView;
    }

    public ArrayList<gallery_image> addData() {
        ArrayList<gallery_image> images = new ArrayList<>();

        for (int i = 0; i < 16; i++) {
            StorageReference imageReference = storageReference.child("images/3");
            imageReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(
                    bytes -> {
                        image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    }
            ).addOnFailureListener(
                    e -> messageSnackbarHelper.showMessage(getActivity(), "Error retrieving image")
            );
            images.add(new gallery_image(3, image));
        }
        return images;
    }
}
