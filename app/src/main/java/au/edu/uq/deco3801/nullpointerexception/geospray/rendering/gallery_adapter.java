package au.edu.uq.deco3801.nullpointerexception.geospray.rendering;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import au.edu.uq.deco3801.nullpointerexception.geospray.CreateUploadFragment;
import au.edu.uq.deco3801.nullpointerexception.geospray.MainActivity;
import au.edu.uq.deco3801.nullpointerexception.geospray.PreviewFragment;
import au.edu.uq.deco3801.nullpointerexception.geospray.R;

public class gallery_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private Context context;
    private ArrayList<gallery_image> gallery_images;
    private int currentIndex;

    public gallery_adapter(Context context, ArrayList<gallery_image> gallery_images) {
        this.context = context;
        this.gallery_images = gallery_images;
        currentIndex = 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;

        if (viewType == VIEW_TYPE_ITEM) {
            view = inflater.inflate(R.layout.grid_gallery, parent, false);
        } else {
            view = inflater.inflate(R.layout.gallery_loading, parent, false);
        }

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            for (int i = 0; i < 16; i++) {
                // Never exceed gallery_images max index
                int index = (currentIndex++) % gallery_images.size();

                gallery_image image = gallery_images.get(index);

                if (image != null) {
                    ((MyViewHolder) holder).images.get(i).setImageBitmap(image.getImg());
                    ((MyViewHolder) holder).images.get(i).setOnClickListener(view -> {
                        Bundle args = new Bundle();
                        args.putInt("shortcode", image.getShort_code());

                        PreviewFragment previewFragment = new PreviewFragment();
                        previewFragment.setArguments(args);

                        ((MainActivity) context).replaceFrag(previewFragment);
                    });
                }
            }

            // Make sure images aren't skipped
            if (currentIndex > gallery_images.size()) {
                currentIndex = gallery_images.size();
            }
        } else if (holder instanceof LoadingviewHolder) {
            showLoadingView((LoadingviewHolder) holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return gallery_images.size();
    }

    public int getItemViewType(int position) {
        return gallery_images.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        List<ImageView> images = new ArrayList<>();

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            images.add(itemView.findViewById(R.id.g1));
            images.add(itemView.findViewById(R.id.g2));
            images.add(itemView.findViewById(R.id.g3));
            images.add(itemView.findViewById(R.id.g4));
            images.add(itemView.findViewById(R.id.g5));
            images.add(itemView.findViewById(R.id.g6));
            images.add(itemView.findViewById(R.id.g7));
            images.add(itemView.findViewById(R.id.g8));
            images.add(itemView.findViewById(R.id.g9));
            images.add(itemView.findViewById(R.id.g10));
            images.add(itemView.findViewById(R.id.g11));
            images.add(itemView.findViewById(R.id.g12));
            images.add(itemView.findViewById(R.id.g13));
            images.add(itemView.findViewById(R.id.g14));
            images.add(itemView.findViewById(R.id.g15));
            images.add(itemView.findViewById(R.id.g16));
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
