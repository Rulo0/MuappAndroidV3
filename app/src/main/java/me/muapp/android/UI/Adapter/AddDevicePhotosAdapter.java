package me.muapp.android.UI.Adapter;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import me.muapp.android.Classes.Internal.UserMedia;
import me.muapp.android.R;
import me.muapp.android.UI.Fragment.Interface.OnImageSelectedListener;

/**
 * Created by rulo on 28/03/17.
 */

public class AddDevicePhotosAdapter extends RecyclerView.Adapter<AddDevicePhotosAdapter.PhotoViewHolder> {
    private final LayoutInflater mInflater;
    private SortedList<UserMedia> photos;
    private Context mContext;
    OnImageSelectedListener onImageSelectedListener;

    public void setOnImageSelectedListener(OnImageSelectedListener onImageSelectedListener) {
        this.onImageSelectedListener = onImageSelectedListener;
    }

    public AddDevicePhotosAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);

        this.photos = new SortedList<>(UserMedia.class, new SortedList.Callback<UserMedia>() {
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
            public int compare(UserMedia o1, UserMedia o2) {
                return o2.getCreationDate().compareTo(o1.getCreationDate());
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(UserMedia oldItem, UserMedia newItem) {
                return oldItem.getUri().equals(newItem.getUri());
            }

            @Override
            public boolean areItemsTheSame(UserMedia item1, UserMedia item2) {
                return item1.getId() == item2.getId();
            }
        });
        this.mContext = context;
    }

    public void addPhoto(UserMedia image) {
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
        UserMedia currentPhoto;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.img_photo_fb_item = (ImageView) itemView.findViewById(R.id.img_photo_fb_item);
        }

        public void bind(final UserMedia image) {
            this.currentPhoto = image;
            Glide.with(mContext).load(image.getPath()).placeholder(R.drawable.ic_placeholder).centerCrop().error(R.drawable.ic_placeholder_error).diskCacheStrategy(DiskCacheStrategy.RESULT).into(img_photo_fb_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onImageSelectedListener != null) {
                onImageSelectedListener.onImageSelected(currentPhoto.getPath(), currentPhoto.getUri(), currentPhoto.getMediaType(),img_photo_fb_item);
            }
        }
    }
}
