package au.edu.uq.deco3801.nullpointerexception.geospray.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import au.edu.uq.deco3801.nullpointerexception.geospray.R;
import au.edu.uq.deco3801.nullpointerexception.geospray.helpers.FirebaseManager;
import au.edu.uq.deco3801.nullpointerexception.geospray.rendering.GalleryAdapter;
import au.edu.uq.deco3801.nullpointerexception.geospray.rendering.GalleryImage;

public class ProfileFragment extends Fragment {
    private FirebaseManager firebaseManager;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        firebaseManager = new FirebaseManager(context);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile, container, false);


        ConstraintLayout your_area = rootView.findViewById(R.id.your_area);
        ImageView your_works = rootView.findViewById(R.id.your_works);
        View your_works_line = rootView.findViewById(R.id.your_works_line);

        ConstraintLayout liked_area = rootView.findViewById(R.id.liked_area);
        ImageView liked_works = rootView.findViewById(R.id.liked_works);
        View liked_works_line = rootView.findViewById(R.id.liked_line);

        your_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                your_works.setColorFilter(Color.rgb(219, 214, 227));
                your_works_line.setBackgroundColor(Color.rgb(255, 255, 255));

                liked_works.setColorFilter(Color.rgb(152, 154, 157));
                liked_works_line.setBackgroundColor(Color.rgb(152, 154, 157));
            }
        });

        liked_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                liked_works.setColorFilter(Color.rgb(219, 214, 227));
                liked_works_line.setBackgroundColor(Color.rgb(255, 255, 255));

                your_works.setColorFilter(Color.rgb(152, 154, 157));
                your_works_line.setBackgroundColor(Color.rgb(152, 154, 157));
            }
        });

        return rootView;
    }
}