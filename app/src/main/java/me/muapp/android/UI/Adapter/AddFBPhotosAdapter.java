package me.muapp.android.UI.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import me.muapp.android.Classes.Util.PreferenceHelper;
import me.muapp.android.R;

/**
 * Created by rulo on 28/03/17.
 */

public class AddFBPhotosAdapter extends RecyclerView.Adapter<AddFBPhotosAdapter.PhotoViewHolder> {
    private static final String ALBUM_PHOTO_FORMAT = "https://graph.facebook.com/%s/picture?access_token=%s";
    private final LayoutInflater mInflater;
    private List<String> photos;
    private Context mContext;
    private String userFBToken;

    public AddFBPhotosAdapter(Context context, List<String> photos) {
        this.mInflater = LayoutInflater.from(context);
        if (photos == null)
            this.photos = new ArrayList<>();
        else
            this.photos = photos;
        this.mContext = context;
        this.userFBToken = new PreferenceHelper(context).getFacebookToken();
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
        notifyDataSetChanged();
    }

    public void addPhotho(String photo) {
        photos.add(photos.size(), photo);
        notifyItemInserted(photos.size());
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = mInflater.inflate(R.layout.user_picture_fb_item_layout, parent, false);
        return new PhotoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        holder.bind(photos.get(position));
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }


    public class PhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img_photo_fb_item;
        View itemView;
        String photoUrl;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.img_photo_fb_item = (ImageView) itemView.findViewById(R.id.img_photo_fb_item);
        }

        public void bind(final String photoId) {
            photoUrl = String.format(ALBUM_PHOTO_FORMAT, photoId, userFBToken);
            Glide.with(mContext).load(photoUrl).placeholder(R.drawable.ic_logo_muapp_no_caption).centerCrop().into(img_photo_fb_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
