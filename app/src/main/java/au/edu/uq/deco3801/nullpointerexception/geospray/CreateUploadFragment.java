package au.edu.uq.deco3801.nullpointerexception.geospray;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CreateUploadFragment extends Fragment {
    private CloudAnchorFragment cloudAnchorFragment;
    private ImageView imageView;
    private EditText title;
    private EditText description;
    private EditText location;
    private Bitmap bitmap;
    private View rootView;
    private TextView imageText;

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

        return rootView;
    }

    private void onImagePressed() {
        // Open file dialog
        // TODO: Ensure "cancel" (back button) works
        //  No upload, grid stays, etc.
        Intent selectionDialog = new Intent(Intent.ACTION_GET_CONTENT);
        selectionDialog.setType("image/*");
        selectionDialog = Intent.createChooser(selectionDialog, "Select image");
        sActivityResultLauncher.launch(selectionDialog);
    }

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
                            // getBitmap error
                        }
                    }
                }
            }
    );

    private void onSubmitButtonPressed() {
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
        args.putString("title", title.getText().toString());
        args.putString("description", description.getText().toString());
        args.putString("location", location.getText().toString());

        cloudAnchorFragment.setArguments(args);

        ((MainActivity) requireActivity()).replaceFrag(cloudAnchorFragment);
    }
}
