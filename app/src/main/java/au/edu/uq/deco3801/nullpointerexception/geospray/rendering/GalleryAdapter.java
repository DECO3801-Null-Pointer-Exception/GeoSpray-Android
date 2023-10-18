package au.edu.uq.deco3801.nullpointerexception.geospray.rendering;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import au.edu.uq.deco3801.nullpointerexception.geospray.MainActivity;
import au.edu.uq.deco3801.nullpointerexception.geospray.R;
import au.edu.uq.deco3801.nullpointerexception.geospray.fragments.PreviewFragment;
import au.edu.uq.deco3801.nullpointerexception.geospray.helpers.FirebaseManager;

public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private Context context;
    private ArrayList<GalleryImage> galleryImages;
    private FirebaseManager firebaseManager;
    private ArrayList<Integer> icons;
    private ArrayList<String> usernames;
    private int[] comments;
    private int[] likes;
    private Toast currentToast;

    public GalleryAdapter(Context context, ArrayList<GalleryImage> galleryImages) {
        this.context = context;
        this.galleryImages = galleryImages;

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

        // 35 random integers between 0 and 7, and 0 and 1001 respectively
        // TODO: https://stackoverflow.com/questions/22584244/how-to-generate-6-different-random-numbers-in-java
        comments = new Random().ints(0, 7).limit(35).toArray();
        likes = new Random().ints(0, 1001).limit(35).toArray();
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

            ((MyViewHolder) holder).comments.setText(String.valueOf(comments[image.getShortCode() % 35]));
            ((MyViewHolder) holder).likes.setText(String.valueOf(likes[image.getShortCode() % 35]));

            firebaseManager.getImageUid(image.getShortCode(), uid -> {
                if (uid != null && uid.equals("0")) {
                    ((MyViewHolder) holder).icon.setImageDrawable(context.getResources().getDrawable(icons.get(0)));
                    ((MyViewHolder) holder).username.setText(usernames.get(0));
                    ((MyViewHolder) holder).userhandle.setText("@" + usernames.get(0));

                    ((MyViewHolder) holder).image.setOnClickListener(view -> {
                        Bundle args = new Bundle();

                        args.putInt("shortcode", image.getShortCode());
                        args.putParcelable("BitmapImage", image.getImg());
                        args.putInt("iconid", icons.get(0));
                        args.putString("username", usernames.get(0));
                        args.putInt("comments", comments[image.getShortCode() % 35]);
                        args.putInt("likes", likes[image.getShortCode() % 35]);

                        PreviewFragment previewFragment = new PreviewFragment();
                        previewFragment.setArguments(args);

                        ((MainActivity) context).replaceFrag(previewFragment);
                    });
                } else {
                    ((MyViewHolder) holder).icon.setImageDrawable(context.getResources().getDrawable(icons.get(image.getShortCode()%35)));
                    ((MyViewHolder) holder).username.setText(usernames.get(image.getShortCode()%35));
                    ((MyViewHolder) holder).userhandle.setText("@" + usernames.get(image.getShortCode()%35));

                    ((MyViewHolder) holder).image.setOnClickListener(view -> {
                        Bundle args = new Bundle();

                        args.putInt("shortcode", image.getShortCode());
                        args.putParcelable("BitmapImage", image.getImg());
                        args.putInt("iconid", icons.get(image.getShortCode() % 35));
                        args.putString("username", usernames.get(image.getShortCode() % 35));
                        args.putInt("comments", comments[image.getShortCode() % 35]);
                        args.putInt("likes", likes[image.getShortCode() % 35]);

                        PreviewFragment previewFragment = new PreviewFragment();
                        previewFragment.setArguments(args);

                        ((MainActivity) context).replaceFrag(previewFragment);
                    });
                }
            });

            ((MyViewHolder) holder).image.setImageBitmap(image.getImg());

            ((MyViewHolder) holder).more.setOnClickListener(view1 -> {
                PopupMenu popupMenu = new PopupMenu(context, view1);
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    if (menuItem.getItemId() == R.id.share_button) {
                        onShareButtonPressed(image.getImg());
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

            firebaseManager.getImageTitle(image.getShortCode(), title -> ((MyViewHolder) holder).name.setText(title));
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
        ImageView image;
        TextView name;
        ImageView icon;
        TextView username;
        TextView userhandle;
        TextView comments;
        TextView likes;
        LinearLayout more;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.view_image);
            name = itemView.findViewById(R.id.image_name);
            icon = itemView.findViewById(R.id.preview_profile_picture);
            username = itemView.findViewById(R.id.image_username);
            userhandle = itemView.findViewById(R.id.user_handle);
            comments = itemView.findViewById(R.id.image_comments);
            likes = itemView.findViewById(R.id.image_likes);
            more = itemView.findViewById(R.id.image_options);
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

    private void onShareButtonPressed(Bitmap image) {
        if (image == null) {
            return;
        }

        // Save image
        OutputStream imageOutStream = null;
        ContentValues cv = new ContentValues();

        cv.put(MediaStore.Images.Media.DISPLAY_NAME, "share.jpg");
        cv.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        cv.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        try {
            imageOutStream = context.getContentResolver().openOutputStream(uri);
            image.compress(Bitmap.CompressFormat.JPEG, 50, imageOutStream);
            imageOutStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Share image
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpg");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(shareIntent, "Share image"));
    }

    private void onReportButtonPressed() {
        showToast("Report submitted.");
    }

    private void showToast(String message) {
        if (currentToast != null) {
            currentToast.cancel();
        }

        if (context != null) {
            currentToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            currentToast.show();
        }
    }
}
