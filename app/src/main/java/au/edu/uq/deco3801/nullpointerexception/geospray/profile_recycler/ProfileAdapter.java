package au.edu.uq.deco3801.nullpointerexception.geospray.profile_recycler;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import au.edu.uq.deco3801.nullpointerexception.geospray.MainActivity;
import au.edu.uq.deco3801.nullpointerexception.geospray.R;
import au.edu.uq.deco3801.nullpointerexception.geospray.fragments.PreviewFragment;
import au.edu.uq.deco3801.nullpointerexception.geospray.helpers.FirebaseManager;
import au.edu.uq.deco3801.nullpointerexception.geospray.rendering.GalleryImage;

public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private Context context;
    private ArrayList<GalleryImage> profileImages;
    private int currentIndex;
    private FirebaseManager firebaseManager;

    public ProfileAdapter(Context context, ArrayList<GalleryImage> galleryImages) {
        this.context = context;
        this.profileImages = galleryImages;
        currentIndex = 0;

        firebaseManager = new FirebaseManager(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;

        if (viewType == VIEW_TYPE_ITEM) {
            view = inflater.inflate(R.layout.profile_image, parent, false);
        } else {
            view = inflater.inflate(R.layout.gallery_loading, parent, false);
        }

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            GalleryImage image = profileImages.get(position);

            ((MyViewHolder) holder).image.setImageBitmap(image.getImg());
//            ((MyViewHolder) holder).image.setOnClickListener(view -> {
//                Bundle args = new Bundle();
//                args.putInt("shortcode", image.getShortCode());
//
//                PreviewFragment previewFragment = new PreviewFragment();
//                previewFragment.setArguments(args);
//
//                ((MainActivity) context).replaceFrag(previewFragment);
//            });
//
//            firebaseManager.getImageTitle(image.getShortCode(), title -> ((MyViewHolder) holder).name.setText(title));

        }
    }

    @Override
    public int getItemCount() {
        return profileImages.size();
    }

    public int getItemViewType(int position) {
        return profileImages.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        List<ImageView> images = new ArrayList<>();
        ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.profile_image);
        }
    }

}
