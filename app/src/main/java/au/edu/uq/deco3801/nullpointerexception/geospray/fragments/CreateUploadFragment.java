package au.edu.uq.deco3801.nullpointerexception.geospray.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import au.edu.uq.deco3801.nullpointerexception.geospray.MainActivity;
import au.edu.uq.deco3801.nullpointerexception.geospray.R;

public class CreateUploadFragment extends Fragment {
    private CloudAnchorFragment cloudAnchorFragment;
    private ImageView imageView;
    private EditText title;
    private EditText description;
    private EditText location;
    private Bitmap bitmap;
    private View rootView;
    private TextView imageText;
    private Toast currentToast;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        cloudAnchorFragment = new CloudAnchorFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.create_page_upload_fragment, container, false);

        imageView = rootView.findViewById(R.id.create_upload_image);
        imageView.setOnClickListener(v -> onImagePressed());

        imageText = rootView.findViewById(R.id.create_upload_image_text);

        Button submitButton = rootView.findViewById(R.id.create_upload_submit);
        submitButton.setOnClickListener(v -> onSubmitButtonPressed());

        title = rootView.findViewById(R.id.create_title);
        description = rootView.findViewById(R.id.create_description);
        location = rootView.findViewById(R.id.create_location);

        Bundle args = this.getArguments();

        if (args != null) {
            byte[] imageBytes = args.getByteArray("image");

            if (imageBytes != null) {
                bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                imageView.setImageBitmap(bitmap);
                imageText.setVisibility(View.GONE);
            }
        }

        return rootView;
    }

    private void onImagePressed() {
        // Open file dialog
        Intent selectionDialog = new Intent(Intent.ACTION_GET_CONTENT);
        selectionDialog.setType("image/*");
        selectionDialog = Intent.createChooser(selectionDialog, "Select image");
        sActivityResultLauncher.launch(selectionDialog);
    }

    // File dialog handler
    ActivityResultLauncher<Intent> sActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    if (data != null) {
                        Uri imageUri = data.getData();

                        try {
                            // Set preview image
                            bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
                            imageView.setImageBitmap(bitmap);
                            imageText.setVisibility(View.GONE);
                        } catch (IOException e) {
                            showToast("Error getting image: " + e + ".");
                        }
                    }
                }
            }
    );

    private void onSubmitButtonPressed() {
        String titleText = title.getText().toString();
        String descriptionText = description.getText().toString();
        String locationText = location.getText().toString();

        if (bitmap == null) {
            showToast("Please choose an image.");
            return;
        } else if (titleText.isEmpty()) {
            showToast("Please enter a title.");
            return;
        } else if (descriptionText.isEmpty()) {
            showToast("Please enter a description.");
            return;
        } else if (locationText.isEmpty()) {
            showToast("Please enter a location.");
            return;
        }

        // Close keyboard
        InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(rootView.getWindowToken(), 0);

        // Pass arguments to CloudAnchorFragment
        Bundle args = new Bundle();

        // Convert to byte array to pass to CloudAnchorFragment
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        bitmap.recycle();

        args.putByteArray("image", stream.toByteArray());
        args.putString("title", titleText);
        args.putString("description", descriptionText);
        args.putString("location", locationText);

        cloudAnchorFragment.setArguments(args);

        ((MainActivity) requireActivity()).replaceFrag(cloudAnchorFragment);
    }

    private void showToast(String message) {
        if (currentToast != null) {
            currentToast.cancel();
        }

        currentToast = Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT);
        currentToast.show();
    }
}
