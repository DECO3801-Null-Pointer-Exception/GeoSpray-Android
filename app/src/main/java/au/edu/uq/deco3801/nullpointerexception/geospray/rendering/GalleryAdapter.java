package au.edu.uq.deco3801.nullpointerexception.geospray.rendering;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import au.edu.uq.deco3801.nullpointerexception.geospray.MainActivity;
import au.edu.uq.deco3801.nullpointerexception.geospray.fragments.PreviewFragment;
import au.edu.uq.deco3801.nullpointerexception.geospray.R;
import au.edu.uq.deco3801.nullpointerexception.geospray.helpers.FirebaseManager;

public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private Context context;
    private ArrayList<GalleryImage> galleryImages;
    private int currentIndex;
    private FirebaseManager firebaseManager;
    private ArrayList<Integer> icons;
    private ArrayList<String> usernames;

    public GalleryAdapter(Context context, ArrayList<GalleryImage> galleryImages) {
        this.context = context;
        this.galleryImages = galleryImages;
        currentIndex = 0;

        firebaseManager = new FirebaseManager(context);

        icons = new ArrayList<>(Arrays.asList(R.drawable.profile_picture, R.drawable.i1,
                R.drawable.i2, R.drawable.i3, R.drawable.i4, R.drawable.i5, R.drawable.i6,
                R.drawable.i7, R.drawable.i8, R.drawable.i9, R.drawable.i10, R.drawable.i11,
                R.drawable.i12, R.drawable.i13, R.drawable.i14, R.drawable.i15, R.drawable.i16,
                R.drawable.i17, R.drawable.i18, R.drawable.i19, R.drawable.i20, R.drawable.i21,
                R.drawable.i22, R.drawable.i23, R.drawable.i24, R.drawable.i25, R.drawable.i26,
                R.drawable.i27, R.drawable.i28, R.drawable.i29, R.drawable.i30, R.drawable.i31,
                R.drawable.i32, R.drawable.i33, R.drawable.i34, R.drawable.i35));

        usernames = new ArrayList<>(Arrays.asList("guest", "SpaceCadet", "CaptainSporty", "FarmHick",
                "HoodUnmasked", "billdates", "CouchCactus", "Ruddy", "Thunderbeast",
                "Faulty Devils" , "DarkLord" , "NoTolerance" , "unfriend_now", "im_watching_you",
                "ur_buddha" , "Funkysticks" , "Warrior666" , "RapidRacket" , "GunSly Lee" ,
                "DEADPOOL" , "Gun Guru GG" , "Odin" , "LegoLord" , "lonely boy" , "wizard harry" ,
                "Psychedelics", "AbraKadaBra" , "Mazafacker" , "JediReturn" , "HotAsAshes" ,
                "realOnline" , "tranquility_tom" , "ACuteAssasin" , "iBookScore" ,
                "oprah_wind_fury" , "Godistime"));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;

        if (viewType == VIEW_TYPE_ITEM) {
            view = inflater.inflate(R.layout.gallery_view, parent, false);
        } else {
            view = inflater.inflate(R.layout.gallery_loading, parent, false);
        }

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            GalleryImage image = galleryImages.get(position);

            ((MyViewHolder) holder).icon.setImageDrawable(context.getResources().getDrawable(icons.get(image.getShortCode()%35)));
            ((MyViewHolder) holder).username.setText(usernames.get(image.getShortCode()%35));
            ((MyViewHolder) holder).userhandle.setText("@" + usernames.get(image.getShortCode()%35));

            ((MyViewHolder) holder).image.setImageBitmap(image.getImg());
            ((MyViewHolder) holder).image.setOnClickListener(view -> {
                Bundle args = new Bundle();
                args.putInt("shortcode", image.getShortCode());
                args.putParcelable("BitmapImage", image.getImg());
                args.putInt("iconid", icons.get(image.getShortCode()%35));
                args.putString("username", usernames.get(image.getShortCode()%35));
                PreviewFragment previewFragment = new PreviewFragment();
                previewFragment.setArguments(args);

                ((MainActivity) context).replaceFrag(previewFragment);
            });

            firebaseManager.getImageTitle(image.getShortCode(), title -> ((MyViewHolder) holder).name.setText(title));



//            firebaseManager.getImageUid(image.getShortCode(), new FirebaseManager.ImageRotationListener() {
//                @Override
//                public void onImageRotationAvailable(Integer rotation) {
//                    if (rotation != null) {
//                        ((MyViewHolder) holder).icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), icons.get(rotation)));
//                        ((MyViewHolder) holder).username.setText(usernames.get(rotation));
//                        String handle = "@" + usernames.get(rotation);
//                        ((MyViewHolder) holder).userhandle.setText(handle);
//                    }
//                }
//            });

//            for (int i = 0; i < 16; i++) {
//                // Never exceed gallery_images max index
//                int index = (currentIndex++) % GalleryImages.size();
//
//                GalleryImage image = GalleryImages.get(index);
//
//                if (image != null) {
//                    ((MyViewHolder) holder).images.get(i).setImageBitmap(image.getImg());
//                    ((MyViewHolder) holder).images.get(i).setOnClickListener(view -> {
//                        Bundle args = new Bundle();
//                        args.putInt("shortcode", image.getShort_code());
//
//                        PreviewFragment previewFragment = new PreviewFragment();
//                        previewFragment.setArguments(args);
//
//                        ((MainActivity) context).replaceFrag(previewFragment);
//                    });
//                }
//            }
//
//            // Make sure images aren't skipped
//            if (currentIndex > GalleryImages.size()) {
//                currentIndex = GalleryImages.size();
//            }
        } else if (holder instanceof LoadingviewHolder) {
            showLoadingView((LoadingviewHolder) holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return galleryImages.size();
    }

    public int getItemViewType(int position) {
        return galleryImages.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        List<ImageView> images = new ArrayList<>();
        ImageView image;
        TextView name;
        ImageView icon;
        TextView username;
        TextView userhandle;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.view_image);
            name = itemView.findViewById(R.id.image_name);
            icon = itemView.findViewById(R.id.preview_profile_picture);
            username = itemView.findViewById(R.id.image_username);
            userhandle = itemView.findViewById(R.id.user_handle);
//            images.add(itemView.findViewById(R.id.g1));
//            images.add(itemView.findViewById(R.id.g2));
//            images.add(itemView.findViewById(R.id.g3));
//            images.add(itemView.findViewById(R.id.g4));
//            images.add(itemView.findViewById(R.id.g5));
//            images.add(itemView.findViewById(R.id.g6));
//            images.add(itemView.findViewById(R.id.g7));
//            images.add(itemView.findViewById(R.id.g8));
//            images.add(itemView.findViewById(R.id.g9));
//            images.add(itemView.findViewById(R.id.g10));
//            images.add(itemView.findViewById(R.id.g11));
//            images.add(itemView.findViewById(R.id.g12));
//            images.add(itemView.findViewById(R.id.g13));
//            images.add(itemView.findViewById(R.id.g14));
//            images.add(itemView.findViewById(R.id.g15));
//            images.add(itemView.findViewById(R.id.g16));
        }
    }

    private class LoadingviewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingviewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    private void showLoadingView(LoadingviewHolder viewHolder, int position) {
        // Progressbar would be displayed
    }
}
