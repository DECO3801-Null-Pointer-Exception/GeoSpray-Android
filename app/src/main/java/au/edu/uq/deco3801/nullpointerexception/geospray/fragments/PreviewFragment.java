package au.edu.uq.deco3801.nullpointerexception.geospray.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.OutputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
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
    private boolean liked;
    private Toast currentToast;
    private Bitmap image;

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
                bytes -> {
                    image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    previewImage.setImageBitmap(image);
                }
        );

        firebaseManager.getImageTitle(shortCode, title -> ((TextView) rootView.findViewById(R.id.preview_title)).setText(title));
        firebaseManager.getImageDescription(shortCode, description -> ((TextView) rootView.findViewById(R.id.preview_description)).setText(description));
        firebaseManager.getImageLocation(shortCode, location -> ((TextView) rootView.findViewById(R.id.preview_location)).setText(location));

        LinearLayout navigationButton = rootView.findViewById(R.id.preview_navigate);
        navigationButton.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putInt("shortcode", shortCode);

            NavigationFragment navigationFragment = new NavigationFragment();
            navigationFragment.setArguments(args);

            ((MainActivity) requireActivity()).replaceFrag(navigationFragment);
        });

        ImageButton likeButton = rootView.findViewById(R.id.like_button);
        likeButton.setOnClickListener(view -> {
            liked = !liked;
            int colour;

            if (liked) {
                colour = ContextCompat.getColor(requireContext(), R.color.red);
            } else {
                colour = ContextCompat.getColor(requireContext(), R.color.white);
            }

            ImageViewCompat.setImageTintList(likeButton, ColorStateList.valueOf(colour));
        });

        ImageButton moreButton = rootView.findViewById(R.id.preview_page_kebab);
        moreButton.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), view);
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.share_button) {
                    onShareButtonPressed();
                    return true;
                } else if (menuItem.getItemId() == R.id.report_button) {
                    onReportButtonPressed();
                    return true;
                }

                return false;
            });

            // Show icons in menu
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                popupMenu.setForceShowIcon(true);
            }

            popupMenu.inflate(R.menu.preview_menu);
            popupMenu.show();
        });

        ImageButton backButton = rootView.findViewById(R.id.preview_page_back);
        backButton.setOnClickListener(view -> getParentFragmentManager().popBackStack());

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

    private void onShareButtonPressed() {
        if (image == null) {
            return;
        }

        // Save image
        OutputStream imageOutStream = null;
        ContentValues cv = new ContentValues();

        cv.put(MediaStore.Images.Media.DISPLAY_NAME, "share.jpg");
        cv.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        cv.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

        Uri uri = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        try {
            imageOutStream = requireContext().getContentResolver().openOutputStream(uri);
            image.compress(Bitmap.CompressFormat.JPEG, 50, imageOutStream);
            imageOutStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Share image
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpg");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(shareIntent, "Share image"));
    }

    private void onReportButtonPressed() {
        showToast("Report submitted.");
    }

    private void showToast(String message) {
        if (currentToast != null) {
            currentToast.cancel();
        }

        currentToast = Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG);
        currentToast.show();
    }
}
