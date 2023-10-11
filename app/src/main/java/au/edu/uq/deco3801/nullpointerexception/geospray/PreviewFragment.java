package au.edu.uq.deco3801.nullpointerexception.geospray;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

        // Retrieve image, title, location, description from shortCode
        StorageReference imageReference = storageReference.child("images/" + shortCode);
        imageReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(
                bytes -> ((ImageView) rootView.findViewById(R.id.preview_image)).setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length))
        );

        firebaseManager.getImageTitle(shortCode, title -> ((TextView) rootView.findViewById(R.id.preview_title)).setText(title));
        firebaseManager.getImageDescription(shortCode, description -> ((TextView) rootView.findViewById(R.id.preview_description)).setText(description));
        firebaseManager.getImageLocation(shortCode, location -> ((TextView) rootView.findViewById(R.id.preview_location)).setText(location));

        // Initialise bottom sheet
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(rootView.findViewById(R.id.bottom_sheet));
        // Convert 100 dp to px
        behavior.setPeekHeight((int) (100 * getResources().getDisplayMetrics().density));
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        return rootView;
    }
}
