package au.edu.uq.deco3801.nullpointerexception.geospray.rendering;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import au.edu.uq.deco3801.nullpointerexception.geospray.MainActivity;
import au.edu.uq.deco3801.nullpointerexception.geospray.fragments.PreviewFragment;
import au.edu.uq.deco3801.nullpointerexception.geospray.R;

public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private Context context;
    private ArrayList<GalleryImage> GalleryImages;
    private int currentIndex;

    public GalleryAdapter(Context context, ArrayList<GalleryImage> GalleryImages) {
        this.context = context;
        this.GalleryImages = GalleryImages;
        currentIndex = 0;
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
            GalleryImage image = GalleryImages.get(position);

            ((MyViewHolder) holder).image.setImageBitmap(image.getImg());
            ((MyViewHolder) holder).image.setOnClickListener(view -> {
                Bundle args = new Bundle();
                args.putInt("shortcode", image.getShortCode());

                PreviewFragment previewFragment = new PreviewFragment();
                previewFragment.setArguments(args);

                ((MainActivity) context).replaceFrag(previewFragment);
            });

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
        return GalleryImages.size();
    }

    public int getItemViewType(int position) {
        return GalleryImages.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        List<ImageView> images = new ArrayList<>();
        ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.view_image);
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
