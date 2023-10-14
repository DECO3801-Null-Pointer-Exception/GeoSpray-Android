package au.edu.uq.deco3801.nullpointerexception.geospray.fragments;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import au.edu.uq.deco3801.nullpointerexception.geospray.MainActivity;
import au.edu.uq.deco3801.nullpointerexception.geospray.R;
import au.edu.uq.deco3801.nullpointerexception.geospray.helpers.FirebaseManager;

public class PreviewFragment extends Fragment {
    private Bundle args;
    private int shortCode;
    private FirebaseManager firebaseManager;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        firebaseManager = new FirebaseManager(context);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        args = this.getArguments();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.preview_fragment, container, false);

        if (args != null) {
            shortCode = args.getInt("shortcode");
        }

        ImageView previewImage = rootView.findViewById(R.id.preview_image);

        // Retrieve image, title, location, description from shortCode
        StorageReference imageReference = storageReference.child("previews/" + shortCode);
        imageReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(
                bytes -> previewImage.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length))
        );

        firebaseManager.getImageTitle(shortCode, title -> ((TextView) rootView.findViewById(R.id.preview_title)).setText(title));
        firebaseManager.getImageDescription(shortCode, description -> ((TextView) rootView.findViewById(R.id.preview_description)).setText(description));
        firebaseManager.getImageLocation(shortCode, location -> ((TextView) rootView.findViewById(R.id.preview_location)).setText(location));

        // TODO: Send to navigation page
        LinearLayout navigationButton = rootView.findViewById(R.id.preview_navigate);
        navigationButton.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putInt("shortcode", shortCode);

            NavigationFragment navigationFragment = new NavigationFragment();
            navigationFragment.setArguments(args);

            ((MainActivity) requireActivity()).replaceFrag(navigationFragment);
        });

        firebaseManager.getImageDate(shortCode, date -> ((TextView) rootView.findViewById(R.id.preview_date)).setText(date));

        // Initialise bottom sheet
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(rootView.findViewById(R.id.bottom_sheet));
        // Convert 100 dp to px
        behavior.setPeekHeight((int) (100 * getResources().getDisplayMetrics().density));
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        previewImage.setOnClickListener(view -> behavior.setState(BottomSheetBehavior.STATE_COLLAPSED));

        // Prevent tapping bottom sheet closing itself
        rootView.findViewById(R.id.bottom_sheet).setOnClickListener(null);

        return rootView;
    }
}
