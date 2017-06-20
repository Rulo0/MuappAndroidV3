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

import me.muapp.android.Classes.Instagram.Data.InstagramPhoto;
import me.muapp.android.R;
import me.muapp.android.UI.Fragment.Interface.OnImageSelectedListener;

/**
 * Created by rulo on 28/03/17.
 */

public class AddInstagramPhotosAdapter extends RecyclerView.Adapter<AddInstagramPhotosAdapter.PhotoViewHolder> {
    private static final String ALBUM_PHOTO_FORMAT = "https://graph.facebook.com/%s/picture?access_token=%s";
    private final LayoutInflater mInflater;
    private SortedList<InstagramPhoto> photos;
    private Context mContext;

    public void setOnImageSelectedListener(OnImageSelectedListener onImageSelectedListener) {
        this.onImageSelectedListener = onImageSelectedListener;
    }

    OnImageSelectedListener onImageSelectedListener;

    public AddInstagramPhotosAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);

        this.photos = new SortedList<>(InstagramPhoto.class, new SortedList.Callback<InstagramPhoto>() {
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
            public int compare(InstagramPhoto o1, InstagramPhoto o2) {
                Long created1 = 0L;
                Long created2 = 0L;
                try {
                    created1 = Long.parseLong(o1.getCreatedTime());
                } catch (Exception x) {
                }
                try {
                    created2 = Long.parseLong(o2.getCreatedTime());
                } catch (Exception x) {
                }
                return created1.compareTo(created2);
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(InstagramPhoto oldItem, InstagramPhoto newItem) {
                return oldItem.getId().equals(newItem.getId()) && oldItem.getLink().equals(newItem.getLink());
            }

            @Override
            public boolean areItemsTheSame(InstagramPhoto item1, InstagramPhoto item2) {
                return item1.getId().equals(item2.getId());
            }
        });
        this.mContext = context;
    }

    public void setPhotos(SortedList<InstagramPhoto> photos) {
        this.photos = photos;
        notifyDataSetChanged();
    }

    public void addPhotho(InstagramPhoto image) {
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

        public void bind(final InstagramPhoto image) {
            Glide.with(mContext).load(photoUrl = image.getImages().getStandardResolution().getUrl()).placeholder(R.drawable.ic_placeholder).centerCrop().error(R.drawable.ic_placeholder_error).diskCacheStrategy(DiskCacheStrategy.RESULT).into(img_photo_fb_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onImageSelectedListener != null) {
                onImageSelectedListener.onImageSelected(photoUrl, null, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE, img_photo_fb_item);
            }
        }
    }
}
