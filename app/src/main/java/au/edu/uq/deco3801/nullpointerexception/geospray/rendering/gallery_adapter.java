package au.edu.uq.deco3801.nullpointerexception.geospray.rendering;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import au.edu.uq.deco3801.nullpointerexception.geospray.R;
import au.edu.uq.deco3801.nullpointerexception.geospray.helpers.SnackbarHelper;

public class gallery_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    Context context;
    ArrayList<ArrayList<gallery_image>> gallery_images;
    ArrayList<gallery_image> temp = new ArrayList<>();

    public gallery_adapter(Context context, ArrayList<ArrayList<gallery_image>> gallery_images) {
        this.context = context;
        this.gallery_images = gallery_images;

        for (int i = 0; i < 16; i++) {
            temp.add(new gallery_image(i, BitmapFactory.decodeResource(context.getResources(), R.drawable.logo)));
        }
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
            ((MyViewHolder) holder).image1.setImageBitmap(gallery_images.get(position).get(0).getImg());
        } else if (holder instanceof LoadingviewHolder) {
            showLoadingView((LoadingviewHolder) holder, position);
        }
//        holder.image2.setImageBitmap(gallery_images.get(position).get(1).getImg());
//        holder.image3.setImageBitmap(gallery_images.get(position).get(2).getImg());
//        holder.image4.setImageBitmap(gallery_images.get(position).get(3).getImg());
//        holder.image5.setImageBitmap(gallery_images.get(position).get(4).getImg());
//        holder.image6.setImageBitmap(gallery_images.get(position).get(5).getImg());
//        holder.image7.setImageBitmap(gallery_images.get(position).get(6).getImg());
//        holder.image8.setImageBitmap(gallery_images.get(position).get(7).getImg());
//        holder.image9.setImageBitmap(gallery_images.get(position).get(8).getImg());
//        holder.image10.setImageBitmap(gallery_images.get(position).get(9).getImg());
//        holder.image11.setImageBitmap(gallery_images.get(position).get(10).getImg());
//        holder.image12.setImageBitmap(gallery_images.get(position).get(11).getImg());
//        holder.image13.setImageBitmap(gallery_images.get(position).get(12).getImg());
//        holder.image14.setImageBitmap(gallery_images.get(position).get(13).getImg());
//        holder.image15.setImageBitmap(gallery_images.get(position).get(14).getImg());
//        holder.image16.setImageBitmap(gallery_images.get(position).get(15).getImg());
//        Log.i("POSITION", String.valueOf(position));
//        if (position >= getItemCount() - 1) {
//
//            gallery_images.add(temp);
//        }
    }

    @Override
    public int getItemCount() {
        return gallery_images.size();
    }

    public int getItemViewType(int position) {
        return gallery_images.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView image1, image2, image3, image4, image5, image6, image7, image8, image9, image10,
            image11, image12, image13, image14, image15, image16;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image1 = itemView.findViewById(R.id.g1);
            image2 = itemView.findViewById(R.id.g2);
            image3 = itemView.findViewById(R.id.g3);
            image4 = itemView.findViewById(R.id.g4);
            image5 = itemView.findViewById(R.id.g5);
            image6= itemView.findViewById(R.id.g6);
            image7 = itemView.findViewById(R.id.g7);
            image8 = itemView.findViewById(R.id.g8);
            image9 = itemView.findViewById(R.id.g9);
            image10 = itemView.findViewById(R.id.g10);
            image11 = itemView.findViewById(R.id.g11);
            image12 = itemView.findViewById(R.id.g12);
            image13 = itemView.findViewById(R.id.g13);
            image14 = itemView.findViewById(R.id.g14);
            image15 = itemView.findViewById(R.id.g15);
            image16 = itemView.findViewById(R.id.g16);
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
