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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import au.edu.uq.deco3801.nullpointerexception.geospray.MainActivity;
import au.edu.uq.deco3801.nullpointerexception.geospray.R;
import au.edu.uq.deco3801.nullpointerexception.geospray.UserLogin;
import au.edu.uq.deco3801.nullpointerexception.geospray.helpers.FirebaseManager;
import au.edu.uq.deco3801.nullpointerexception.geospray.profile_recycler.ProfileAdapter;
import au.edu.uq.deco3801.nullpointerexception.geospray.rendering.GalleryImage;

/**
 * User profile fragment. Shows the user's profile and their details (followers, etc.). Also shows
 * artwork they have uploaded.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = ImageGalleryFragment.class.getName();

    private RecyclerView recyclerView;
    private ProfileAdapter yourAdapter, likedAdapter;
    private ArrayList<GalleryImage> yourImages, likedImages;
    private FirebaseManager firebaseManager;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private View rootView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        firebaseManager = new FirebaseManager(context);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        yourImages = new ArrayList<>();
        likedImages = new ArrayList<>();
        yourAdapter = new ProfileAdapter(context, yourImages);
        likedAdapter = new ProfileAdapter(context, likedImages);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.profile, container, false);

        // Get view elements
        ConstraintLayout your_area = rootView.findViewById(R.id.your_area);
        ImageView your_works = rootView.findViewById(R.id.your_works);
        View your_works_line = rootView.findViewById(R.id.your_works_line);

        ConstraintLayout liked_area = rootView.findViewById(R.id.liked_area);
        ImageView liked_works = rootView.findViewById(R.id.liked_works);
        View liked_works_line = rootView.findViewById(R.id.liked_line);

        Button signOutButton = rootView.findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(v -> {
            // Sign out and go back to home page.
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signInAnonymously();

            MainActivity mainActivity = (MainActivity) requireActivity();
            mainActivity.replaceFrag(mainActivity.getImageGalleryFragment());
        });

        // Switch tab selection on tap
        your_area.setOnClickListener(view -> {
            your_works.setColorFilter(Color.rgb(219, 214, 227));
            your_works_line.setBackgroundColor(Color.rgb(255, 255, 255));

            liked_works.setColorFilter(Color.rgb(152, 154, 157));
            liked_works_line.setBackgroundColor(Color.rgb(152, 154, 157));

            // Set the gallery images to user created ones
            recyclerView.setAdapter(yourAdapter);
        });

        liked_area.setOnClickListener(view -> {
            liked_works.setColorFilter(Color.rgb(219, 214, 227));
            liked_works_line.setBackgroundColor(Color.rgb(255, 255, 255));

            your_works.setColorFilter(Color.rgb(152, 154, 157));
            your_works_line.setBackgroundColor(Color.rgb(152, 154, 157));

            // Set the gallery images to user liked ones
            recyclerView.setAdapter(likedAdapter);
        });

        recyclerView = rootView.findViewById(R.id.profile_recycler);
        recyclerView.setAdapter(yourAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        // Get all images from the database uploaded by the user
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

                                    // If the image uid matches user uid then add it to user_created gallery
                                    if (String.valueOf(child.child("uid").getValue()).equals(getUID())) {
                                        if (!yourImages.contains(image)) {
                                            yourImages.add(image);
                                            yourAdapter.notifyDataSetChanged();
                                        }
                                    }

                                    // If the image is liked then add it to liked gallery
                                    if (String.valueOf(child.child("liked").getValue()).equals("true")) {
                                        if (!likedImages.contains(image)) {
                                            likedImages.add(image);
                                            likedAdapter.notifyDataSetChanged();
                                        }
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

    @Override
    public void onStart() {
        super.onStart();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null || user.isAnonymous()) {
            // user should not be here
            Intent login = new Intent(getContext(), UserLogin.class);
            startActivity(login); //send to login class
        } else {
            TextView username = rootView.findViewById(R.id.username);
            TextView handle = rootView.findViewById(R.id.user_handle);

            if (user.getDisplayName() != null) {
                username.setText(user.getDisplayName());
                Log.d("ProfileUID",user.getUid());
                String handleString = user.getDisplayName()
                        .replace(" ", "_");
                handle.setText(handleString);
            }
        }
    }

    /**
     * Returns the user's ID (UID).
     *
     * @return The user's ID.
     */
    private String getUID() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            return user.getUid();
        }

        // Set user identifier to 0 for anonymous
        return "0";
    }
}