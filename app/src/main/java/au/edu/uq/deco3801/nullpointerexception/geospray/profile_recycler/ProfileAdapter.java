package au.edu.uq.deco3801.nullpointerexception.geospray.profile_recycler;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import au.edu.uq.deco3801.nullpointerexception.geospray.MainActivity;
import au.edu.uq.deco3801.nullpointerexception.geospray.R;
import au.edu.uq.deco3801.nullpointerexception.geospray.fragments.PreviewFragment;
import au.edu.uq.deco3801.nullpointerexception.geospray.helpers.FirebaseManager;
import au.edu.uq.deco3801.nullpointerexception.geospray.rendering.GalleryAdapter;
import au.edu.uq.deco3801.nullpointerexception.geospray.rendering.GalleryImage;

/**
 * Class used to show images on the user's profile page in a scrollable fashion.
 */
public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private Context context;
    private ArrayList<GalleryImage> profileImages;
    private ArrayList<Integer> icons;
    private ArrayList<String> usernames;
    private int[] comments;
    private int[] likes;

    /**
     * Constructor for a ProfileAdapter.
     *
     * @param context The Context of this Adapter.
     * @param galleryImages The list of images this Adapter is to display.
     */
    public ProfileAdapter(Context context, ArrayList<GalleryImage> galleryImages) {
        this.context = context;
        this.profileImages = galleryImages;

        // Initialise a preset list of profile pictures
        icons = new ArrayList<>(Arrays.asList(R.drawable.profile_picture, R.drawable.i1,
                R.drawable.i2, R.drawable.i3, R.drawable.i4, R.drawable.i5, R.drawable.i6,
                R.drawable.i7, R.drawable.i8, R.drawable.i9, R.drawable.i10, R.drawable.i11,
                R.drawable.i12, R.drawable.i13, R.drawable.i14, R.drawable.i15, R.drawable.i16,
                R.drawable.i17, R.drawable.i18, R.drawable.i19, R.drawable.i20, R.drawable.i21,
                R.drawable.i22, R.drawable.i23, R.drawable.i24, R.drawable.i25, R.drawable.i26,
                R.drawable.i27, R.drawable.i28, R.drawable.i29, R.drawable.i30, R.drawable.i31,
                R.drawable.i32, R.drawable.i33, R.drawable.i34, R.drawable.i35));

        // Initialise a preset list of usernames
        usernames = new ArrayList<>(Arrays.asList("Jane Q. User", "SpaceCadet", "CaptainSporty", "FarmHick",
                "HoodUnmasked", "billdates", "CouchCactus", "Ruddy", "Thunderbeast",
                "Faulty Devils" , "DarkLord" , "NoTolerance" , "unfriend_now", "im_watching_you",
                "ur_buddha" , "Funkysticks" , "Warrior666" , "RapidRacket" , "GunSly Lee" ,
                "DEADPOOL" , "Gun Guru GG" , "Odin" , "LegoLord" , "lonely boy" , "wizard harry" ,
                "Psychedelics", "AbraKadaBra" , "Mazafacker" , "JediReturn" , "HotAsAshes" ,
                "realOnline" , "tranquility_tom" , "ACuteAssasin" , "iBookScore" ,
                "oprah_wind_fury" , "Godistime"));

        // Generate 35 random integers between 0 and 7, and 0 and 1001, respectively
        // https://stackoverflow.com/questions/22584244/how-to-generate-6-different-random-numbers-in-java
        // Accessed on October 16.
        comments = new Random().ints(0, 7).limit(35).toArray();
        likes = new Random().ints(0, 1001).limit(35).toArray();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.profile_image, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            GalleryImage image = profileImages.get(position);
            FirebaseManager firebaseManager = new FirebaseManager(context);

            ((MyViewHolder) holder).image.setImageBitmap(image.getImg());

            Bundle args = new Bundle();

            ((MyViewHolder) holder).image.setOnClickListener(view -> {
                firebaseManager.getImageUid(image.getShortCode(), uid -> {
                    if (uid != null && uid.equals("0")) {
                        // Random user
                        args.putInt("iconid", icons.get(image.getShortCode() % 35));
                        args.putString("username", usernames.get(image.getShortCode() % 35));
                    } else {
                        // User
                        args.putInt("iconid", icons.get(0));
                        args.putString("username", usernames.get(0));
                    }
                });

                args.putInt("shortcode", image.getShortCode());
                args.putParcelable("BitmapImage", image.getImg());
                args.putBoolean("fromProfile", true);
                args.putInt("comments", comments[image.getShortCode() % 35]);
                args.putInt("likes", likes[image.getShortCode() % 35]);

                PreviewFragment previewFragment = new PreviewFragment();
                previewFragment.setArguments(args);

                ((MainActivity) context).replaceFrag(previewFragment);
            });
        }
    }

    @Override
    public int getItemCount() {
        return profileImages.size();
    }

    /**
     * Returns the type of the image at a given position.
     *
     * @param position Position to query.
     * @return The image's type.
     */
    public int getItemViewType(int position) {
        return profileImages.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    /**
     * Class representing a container for an Adapter's images.
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.profile_image);
        }
    }
}
