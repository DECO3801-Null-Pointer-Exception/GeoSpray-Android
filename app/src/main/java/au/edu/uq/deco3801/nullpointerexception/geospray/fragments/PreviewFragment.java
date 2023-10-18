package au.edu.uq.deco3801.nullpointerexception.geospray.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import au.edu.uq.deco3801.nullpointerexception.geospray.MainActivity;
import au.edu.uq.deco3801.nullpointerexception.geospray.R;
import au.edu.uq.deco3801.nullpointerexception.geospray.helpers.FirebaseManager;

/**
 * Image preview fragment. Shows a preview of the chosen artwork and its associated details
 * (title, description, location, comments, etc.).
 */
public class PreviewFragment extends Fragment {
    private int shortCode;
    private FirebaseManager firebaseManager;
    private boolean liked;
    private Toast currentToast;
    private Bitmap pImage;
    private Integer iconId;
    private String name;
    private int comments;
    private int likes;
    private ArrayList<ArrayList<Integer>> commentViewElements;
    private ArrayList<String> commentMessages;
    private ArrayList<Integer> icons;
    private ArrayList<String> usernames;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        firebaseManager = new FirebaseManager(context);

        // Get view elements of each comment
        commentViewElements = new ArrayList<>(Arrays.asList(
                new ArrayList<>(Arrays.asList(R.id.user_comment, R.id.comment_user_picture, R.id.comment_user_username, R.id.comment_user_handle, R.id.comment_user_message)),
                new ArrayList<>(Arrays.asList(R.id.comment_1, R.id.comment_1_picture, R.id.comment_1_username, R.id.comment_1_handle, R.id.comment_1_message)),
                new ArrayList<>(Arrays.asList(R.id.comment_2, R.id.comment_2_picture, R.id.comment_2_username, R.id.comment_2_handle, R.id.comment_2_message)),
                new ArrayList<>(Arrays.asList(R.id.comment_3, R.id.comment_3_picture, R.id.comment_3_username, R.id.comment_3_handle, R.id.comment_3_message)),
                new ArrayList<>(Arrays.asList(R.id.comment_4, R.id.comment_4_picture, R.id.comment_4_username, R.id.comment_4_handle, R.id.comment_4_message)),
                new ArrayList<>(Arrays.asList(R.id.comment_5, R.id.comment_5_picture, R.id.comment_5_username, R.id.comment_5_handle, R.id.comment_5_message)),
                new ArrayList<>(Arrays.asList(R.id.comment_6, R.id.comment_6_picture, R.id.comment_6_username, R.id.comment_6_handle, R.id.comment_6_message))
        ));

        // Initialise list of preset comments
        commentMessages = new ArrayList<>(Arrays.asList("This is stunning!", "I love the colors!",
                "The detail is incredible.", "It really evokes emotion.", "This speaks to me.",
                "The composition is perfect.", "Such a unique perspective.",
                "The use of light is amazing.", "I could stare at this all day.",
                "It's so thought-provoking.", "This is truly a masterpiece.",
                "The texture adds so much depth.", "I'm captivated by this.",
                "The artist's talent is evident.", "It's like a dream come to life.",
                "The symbolism is powerful.", "I'm drawn to the subject.",
                "This has a timeless quality.", "It's both bold and delicate.",
                "The contrast is striking."));

        // Initialise list of preset profile pictures
        icons = new ArrayList<>(Arrays.asList(R.drawable.profile_picture, R.drawable.i1,
                R.drawable.i2, R.drawable.i3, R.drawable.i4, R.drawable.i5, R.drawable.i6,
                R.drawable.i7, R.drawable.i8, R.drawable.i9, R.drawable.i10, R.drawable.i11,
                R.drawable.i12, R.drawable.i13, R.drawable.i14, R.drawable.i15, R.drawable.i16,
                R.drawable.i17, R.drawable.i18, R.drawable.i19, R.drawable.i20, R.drawable.i21,
                R.drawable.i22, R.drawable.i23, R.drawable.i24, R.drawable.i25, R.drawable.i26,
                R.drawable.i27, R.drawable.i28, R.drawable.i29, R.drawable.i30, R.drawable.i31,
                R.drawable.i32, R.drawable.i33, R.drawable.i34, R.drawable.i35));

        // Initialise list of preset usernames
        usernames = new ArrayList<>(Arrays.asList("guest", "SpaceCadet", "CaptainSporty", "FarmHick",
                "HoodUnmasked", "billdates", "CouchCactus", "Ruddy", "Thunderbeast",
                "Faulty Devils" , "DarkLord" , "NoTolerance" , "unfriend_now", "im_watching_you",
                "ur_buddha" , "Funkysticks" , "Warrior666" , "RapidRacket" , "GunSly Lee" ,
                "DEADPOOL" , "Gun Guru GG" , "Odin" , "LegoLord" , "lonely boy" , "wizard harry" ,
                "Psychedelics", "AbraKadaBra" , "Mazafacker" , "JediReturn" , "HotAsAshes" ,
                "realOnline" , "tranquility_tom" , "ACuteAssasin" , "iBookScore" ,
                "oprah_wind_fury" , "Godistime"));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.preview_fragment, container, false);

        Bundle args = this.getArguments();

        if (args != null) {
            shortCode = args.getInt("shortcode");
            pImage = args.getParcelable("BitmapImage");
            iconId = args.getInt("iconid");
            name = args.getString("username");
            comments = args.getInt("comments");
            likes = args.getInt("likes");
        }

        // Retrieve view components
        ImageView previewImage = rootView.findViewById(R.id.preview_image);
        ImageView icon = rootView.findViewById(R.id.preview_profile_picture);
        TextView username = rootView.findViewById(R.id.username);
        TextView userHandle = rootView.findViewById(R.id.user_handle);
        TextView commentTextView = rootView.findViewById(R.id.preview_comments);
        TextView likeTextView = rootView.findViewById(R.id.preview_likes);

        // Set view components
        icon.setImageDrawable(getResources().getDrawable(iconId));
        username.setText(name);
        userHandle.setText("@" + name);
        commentTextView.setText(String.valueOf(comments));
        likeTextView.setText(String.valueOf(likes));

        // Retrieve image, title, location, description from shortCode
        previewImage.setImageBitmap(pImage);

        firebaseManager.getImageTitle(shortCode, title ->
                ((TextView) rootView.findViewById(R.id.preview_title)).setText(title));
        firebaseManager.getImageDescription(shortCode, description ->
                ((TextView) rootView.findViewById(R.id.preview_description)).setText(description));
        firebaseManager.getImageLat(shortCode, lat ->
                firebaseManager.getImageLong(shortCode, longitude -> {
            // https://stackoverflow.com/questions/31248257/androidgoogle-maps-get-the-address-of-location-on-touch
            // Accessed on October 15.
            try {
                Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                StringBuilder location = new StringBuilder();
                List<Address> addresses = geocoder.getFromLocation(lat, longitude, 1);

                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);

                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                        location.append(address.getAddressLine(i));

                        if (i != address.getMaxAddressLineIndex()) {
                            location.append("\n");
                        }
                    }
                } else {
                    location.append("Unknown");
                }

                ((TextView) rootView.findViewById(R.id.preview_location)).setText(location);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        // Set button behaviour
        LinearLayout navigationButton = rootView.findViewById(R.id.preview_navigate);
        navigationButton.setOnClickListener(view -> {
            Bundle navArgs = new Bundle();
            navArgs.putInt("shortcode", shortCode);

            NavigationFragment navigationFragment = new NavigationFragment();
            navigationFragment.setArguments(navArgs);

            ((MainActivity) requireActivity()).replaceFrag(navigationFragment);
        });

        ImageButton likeButton = rootView.findViewById(R.id.like_button);
        likeButton.setOnClickListener(view -> {
            liked = !liked;
            int colour;

            if (liked) {
                colour = ContextCompat.getColor(requireContext(), R.color.red);
                likeTextView.setText(String.valueOf(likes + 1));
            } else {
                colour = ContextCompat.getColor(requireContext(), R.color.white);
                likeTextView.setText(String.valueOf(likes));
            }

            firebaseManager.getRootRef().child("" + shortCode).child("liked")
                    .setValue(liked);

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

        firebaseManager.getImageDate(shortCode, date ->
                ((TextView) rootView.findViewById(R.id.preview_date)).setText(date));

        // Initialise bottom sheet
        BottomSheetBehavior<View> behavior =
                BottomSheetBehavior.from(rootView.findViewById(R.id.bottom_sheet));
        // Convert 100 dp to px
        // https://stackoverflow.com/questions/29664993/how-to-convert-dp-px-sp-among-each-other-especially-dp-and-sp
        // Accessed on October 10.
        behavior.setPeekHeight((int) (100 * getResources().getDisplayMetrics().density));
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        previewImage.setOnClickListener(view -> behavior.setState(BottomSheetBehavior.STATE_COLLAPSED));

        // Prevent tapping bottom sheet closing itself
        rootView.findViewById(R.id.bottom_sheet).setOnClickListener(null);

        // Comment behaviour
        // Randomise comment messages
        Collections.shuffle(commentMessages);

        // Hide user comment until a comment is written
        rootView.findViewById(commentViewElements.get(0).get(0)).setVisibility(View.GONE);
        ((ShapeableImageView) rootView.findViewById(commentViewElements.get(0).get(1)))
                .setImageDrawable(requireContext().getResources().getDrawable(icons.get(0)));
        ((TextView) rootView.findViewById(commentViewElements.get(0).get(2)))
                .setText(usernames.get(0));
        ((TextView) rootView.findViewById(commentViewElements.get(0).get(3)))
                .setText("@" + usernames.get(0));

        // Set randomised comments
        for (int i = 1; i < commentViewElements.size(); i++) {
            if (i <= comments) {
                int index = new Random().nextInt(icons.size());

                ((ShapeableImageView) rootView.findViewById(commentViewElements.get(i).get(1)))
                        .setImageDrawable(requireContext().getResources().getDrawable(icons.get(index)));
                ((TextView) rootView.findViewById(commentViewElements.get(i).get(2)))
                        .setText(usernames.get(index));
                ((TextView) rootView.findViewById(commentViewElements.get(i).get(3)))
                        .setText("@" + usernames.get(index));
                ((TextView) rootView.findViewById(commentViewElements.get(i).get(4)))
                        .setText(commentMessages.get(i));
            } else {
                rootView.findViewById(commentViewElements.get(i).get(0)).setVisibility(View.GONE);
            }
        }

        // Show user comment
        EditText commentField = rootView.findViewById(R.id.comment_field);
        commentField.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                rootView.findViewById(commentViewElements.get(0).get(0)).setVisibility(View.VISIBLE);
                ((TextView) rootView.findViewById(commentViewElements.get(0).get(4)))
                        .setText(commentField.getText());
                commentField.setText("");
                commentTextView.setText(String.valueOf(comments + 1));

                return true;
            }

            return false;
        });

        return rootView;
    }

    /**
     * Handle behaviour when the share button is pressed. Open a share dialog with the image preview
     * selected as the item to share.
     */
    private void onShareButtonPressed() {
        if (pImage == null) {
            return;
        }

        // Save image (code adapted from PaintFragment)
        OutputStream imageOutStream = null;
        ContentValues cv = new ContentValues();

        cv.put(MediaStore.Images.Media.DISPLAY_NAME, "share.jpg");
        cv.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        cv.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

        Uri uri = requireContext().getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        try {
            imageOutStream = requireContext().getContentResolver().openOutputStream(uri);
            pImage.compress(Bitmap.CompressFormat.JPEG, 50, imageOutStream);
            imageOutStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Open share dialog
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpg");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(shareIntent, "Share image"));
    }

    /**
     * Handle behaviour when the report button is pressed by simulating a report submission.
     */
    private void onReportButtonPressed() {
        showToast("Report submitted.");
    }

    /**
     * Shows a toast pop-up that replaces the previous pop-up.
     *
     * @param message The message to show in a pop-up.
     */
    private void showToast(String message) {
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
