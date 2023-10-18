package au.edu.uq.deco3801.nullpointerexception.geospray.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

/**
 * Image uploading fragment. Allows the user to upload an image to be placed in the world in AR.
 * The user can set a title and description for the image to be hosted.
 */
public class CreateUploadFragment extends Fragment {
    private CloudAnchorFragment cloudAnchorFragment;
    private ImageView imageView;
    private EditText title;
    private EditText description;
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

        // Set button behaviour
        imageView = rootView.findViewById(R.id.create_upload_image);
        imageView.setOnClickListener(v -> onImagePressed());

        Button submitButton = rootView.findViewById(R.id.create_upload_submit);
        submitButton.setOnClickListener(v -> onSubmitButtonPressed());

        ImageButton backButton = rootView.findViewById(R.id.create_page_back);
        backButton.setOnClickListener(view -> getParentFragmentManager().popBackStack());

        imageText = rootView.findViewById(R.id.create_upload_image_text);
        title = rootView.findViewById(R.id.create_title);
        description = rootView.findViewById(R.id.create_description);

        // Pressing enter in the description box simulates a submit button press
        // https://stackoverflow.com/questions/2986387/multi-line-edittext-with-done-action-button/41022589#41022589
        // Accessed on October 18.
        description.setRawInputType(InputType.TYPE_CLASS_TEXT);
        description.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                onSubmitButtonPressed();
                return true;
            }

            return false;
        });

        // Get any arguments passed to this fragment from the paint fragment
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

    /**
     * Handles the behaviour when the image is pressed by allowing the user to select an image from
     * their device.
     */
    private void onImagePressed() {
        // Open file dialog
        Intent selectionDialog = new Intent(Intent.ACTION_GET_CONTENT);
        selectionDialog.setType("image/*");
        selectionDialog = Intent.createChooser(selectionDialog, "Select image");
        sActivityResultLauncher.launch(selectionDialog);
    }

    /**
     * Handles the file dialog behaviour.
     */
    // https://youtu.be/4EKlAvjY74U
    // Accessed on October 7.
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
                            showToast("Error getting image.");
                        }
                    }
                }
            }
    );

    /**
     * Handles the behaviour when the submit button is pressed by checking if all fields are
     * populated then sending the necessary information to the camera fragment.
     */
    private void onSubmitButtonPressed() {
        String titleText = title.getText().toString();
        String descriptionText = description.getText().toString();

        if (bitmap == null) {
            showToast("Please choose an image.");
            return;
        } else if (titleText.isEmpty()) {
            showToast("Please enter a title.");
            return;
        } else if (descriptionText.isEmpty()) {
            showToast("Please enter a description.");
            return;
        }

        // Close keyboard
        // https://stackoverflow.com/questions/72436314/how-to-close-the-android-soft-keyboard
        // Accessed on October 13.
        InputMethodManager inputMethodManager =
                (InputMethodManager) requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
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

        cloudAnchorFragment.setArguments(args);

        ((MainActivity) requireActivity()).replaceFrag(cloudAnchorFragment);
    }

    /**
     * Shows the given message in a toast pop-up, overwriting any previous pop-ups.
     *
     * @param message The message to display in a toast pop-up.
     */
    private void showToast(String message) {
        // Cancel any current toasts
        if (currentToast != null) {
            currentToast.cancel();
        }

        Activity activity = getActivity();

        if (activity != null) {
            currentToast = Toast.makeText(activity, message, Toast.LENGTH_LONG);
            currentToast.show();
        }
    }
}
