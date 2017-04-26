package me.muapp.android.UI.Adapter;

import android.content.Context;
import android.provider.MediaStore;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import me.muapp.android.Classes.Internal.FacebookImage;
import me.muapp.android.Classes.Util.PreferenceHelper;
import me.muapp.android.R;
import me.muapp.android.UI.Fragment.Interface.OnImageSelectedListener;

import static me.muapp.android.UI.Activity.AddPhotosActivity.hasSelectedMedia;

/**
 * Created by rulo on 28/03/17.
 */

public class AddFBPhotosAdapter extends RecyclerView.Adapter<AddFBPhotosAdapter.PhotoViewHolder> {
    private static final String ALBUM_PHOTO_FORMAT = "https://graph.facebook.com/%s/picture?access_token=%s";
    private final LayoutInflater mInflater;
    private SortedList<FacebookImage> photos;
    private Context mContext;
    private String userFBToken;
    OnImageSelectedListener onImageSelectedListener;

    public void setOnImageSelectedListener(OnImageSelectedListener onImageSelectedListener) {
        this.onImageSelectedListener = onImageSelectedListener;
    }

    public AddFBPhotosAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);

        this.photos = new SortedList<>(FacebookImage.class, new SortedList.Callback<FacebookImage>() {
            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public int compare(FacebookImage o1, FacebookImage o2) {
                return o2.getCreatedTime().compareTo(o1.getCreatedTime());
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(FacebookImage oldItem, FacebookImage newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areItemsTheSame(FacebookImage item1, FacebookImage item2) {
                return item1.getId().equals(item2.getId()) && item1.getCreatedTime().equals(item2.getCreatedTime());
            }
        });

        this.mContext = context;
        this.userFBToken = new PreferenceHelper(context).getFacebookToken();
    }

    public void setPhotos(SortedList<FacebookImage> photos) {
        this.photos = photos;
        notifyDataSetChanged();
    }

    public void addPhoto(FacebookImage image) {
        photos.add(image);
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

        public void bind(final FacebookImage image) {
            photoUrl = String.format(ALBUM_PHOTO_FORMAT, image.getId(), userFBToken);
            Glide.with(mContext).load(photoUrl).placeholder(R.drawable.ic_logo_muapp_no_caption).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESULT).into(img_photo_fb_item);
            itemView.setOnClickListener(this);
            if (!hasSelectedMedia) {
                onImageSelectedListener.onImageSelected(photoUrl, null, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE, img_photo_fb_item);
                hasSelectedMedia = true;
            }
        }

        @Override
        public void onClick(View v) {
            if (onImageSelectedListener != null) {
                onImageSelectedListener.onImageSelected(photoUrl, null, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE, img_photo_fb_item);
            }
        }
    }
}
