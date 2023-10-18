package au.edu.uq.deco3801.nullpointerexception.geospray.profile_recycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import au.edu.uq.deco3801.nullpointerexception.geospray.R;
import au.edu.uq.deco3801.nullpointerexception.geospray.rendering.GalleryImage;

/**
 * Class used to show images on the user's profile page in a scrollable fashion.
 */
public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private Context context;
    private ArrayList<GalleryImage> profileImages;

    /**
     * Constructor for a ProfileAdapter.
     *
     * @param context The Context of this Adapter.
     * @param galleryImages The list of images this Adapter is to display.
     */
    public ProfileAdapter(Context context, ArrayList<GalleryImage> galleryImages) {
        this.context = context;
        this.profileImages = galleryImages;
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

            ((MyViewHolder) holder).image.setImageBitmap(image.getImg());
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
     * Class representing a container for a View's images.
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.profile_image);
        }
    }
}
