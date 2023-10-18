package au.edu.uq.deco3801.nullpointerexception.geospray.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import au.edu.uq.deco3801.nullpointerexception.geospray.R;
import au.edu.uq.deco3801.nullpointerexception.geospray.UserLogin;
import au.edu.uq.deco3801.nullpointerexception.geospray.helpers.FirebaseManager;
import au.edu.uq.deco3801.nullpointerexception.geospray.helpers.FirebaseManagerUsers;
import au.edu.uq.deco3801.nullpointerexception.geospray.profile_recycler.ProfileAdapter;
import au.edu.uq.deco3801.nullpointerexception.geospray.rendering.GalleryAdapter;
import au.edu.uq.deco3801.nullpointerexception.geospray.rendering.GalleryImage;

public class ProfileFragment extends Fragment {
    private static final String TAG = ImageGalleryFragment.class.getName();
    private RecyclerView recyclerView;
    private ProfileAdapter adapter;
    private ArrayList<GalleryImage> yourImages;
    private FirebaseManager firebaseManager;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;

    private View rootView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        firebaseManager = new FirebaseManager(context);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        yourImages = new ArrayList<>();
        adapter = new ProfileAdapter(context, yourImages);


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.profile, container, false);


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

        recyclerView = rootView.findViewById(R.id.profile_recycler);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        firebaseManager.getRootRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    StorageReference imageReference = storageReference.child("previews/" + child.getKey());
                    imageReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(
                            bytes -> {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                                if (child.getKey() != null) {
                                    GalleryImage image = new GalleryImage(Integer.parseInt(child.getKey()), bitmap);

                                    if (!yourImages.contains(image)) {
                                        yourImages.add(image);
                                    }

                                    adapter.notifyDataSetChanged();
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

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null || user.isAnonymous()) {
            // user should not be here
            Intent login = new Intent(getContext(), UserLogin.class);
            startActivity(login); //send to login class
        } else {
            TextView username = rootView.findViewById(R.id.username);
            if (user.getDisplayName() != null) {
                username.setText(user.getDisplayName());
                Log.d("ProfileUID",user.getUid());
                // todo
                FirebaseManagerUsers userdatabase = new FirebaseManagerUsers(getContext());
                userdatabase.storeUser(user.getUid());
            } else {
                username.setText("Display Name");
            }

        }
    }
}