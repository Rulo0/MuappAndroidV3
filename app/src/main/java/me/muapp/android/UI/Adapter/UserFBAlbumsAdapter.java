package me.muapp.android.UI.Adapter;

import android.content.Intent;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import me.muapp.android.Classes.Internal.FacebookAlbum;
import me.muapp.android.Classes.Util.PreferenceHelper;
import me.muapp.android.R;
import me.muapp.android.UI.Activity.FacebookAlbumsActivity;
import me.muapp.android.UI.Activity.FacebookPhotosActivity;

import static me.muapp.android.UI.Activity.FacebookAlbumsActivity.FACEBOOK_PHOTOS_REQUEST_CODE;
import static me.muapp.android.UI.Activity.FacebookPhotosActivity.ALBUM_EXTRA;

/**
 * Created by rulo on 28/03/17.
 */

public class UserFBAlbumsAdapter extends RecyclerView.Adapter<UserFBAlbumsAdapter.AlbumViewHolder> {
    private final LayoutInflater mInflater;
    private SortedList<FacebookAlbum> albums;
    private FacebookAlbumsActivity mContext;
    private String userFBToken;
    private static final String ALBUM_PHOTO_FORMAT = "https://graph.facebook.com/%s/picture?access_token=%s";

    public UserFBAlbumsAdapter(FacebookAlbumsActivity context) {
        this.mInflater = LayoutInflater.from(context);
        this.albums = new SortedList<>(FacebookAlbum.class, new SortedList.Callback<FacebookAlbum>() {
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
            public int compare(FacebookAlbum o1, FacebookAlbum o2) {
                return o1.getCreated_time().compareTo(o2.getCreated_time());
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(FacebookAlbum oldItem, FacebookAlbum newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areItemsTheSame(FacebookAlbum item1, FacebookAlbum item2) {
                return false;
            }
        });
        this.mContext = context;
        this.userFBToken = new PreferenceHelper(context).getFacebookToken();
    }


    public void addAlbum(FacebookAlbum album) {
        albums.add(album);
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = mInflater.inflate(R.layout.user_album_item_layout, parent, false);
        return new AlbumViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder holder, int position) {
        holder.bind(albums.get(position));
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }


    public class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img_fb_album;
        TextView txt_fb_album_name;
        LinearLayout album_container;
        FacebookAlbum album;

        public AlbumViewHolder(View itemView) {
            super(itemView);
            this.album_container = (LinearLayout) itemView.findViewById(R.id.album_container);
            this.img_fb_album = (ImageView) itemView.findViewById(R.id.img_fb_album);
            this.txt_fb_album_name = (TextView) itemView.findViewById(R.id.txt_fb_album_name);
        }

        public void bind(final FacebookAlbum album) {
            this.album = album;
            Glide.with(mContext).load(String.format(ALBUM_PHOTO_FORMAT, album.getFirstPhotoId(), userFBToken)).placeholder(R.drawable.ic_placeholder).centerCrop().into(img_fb_album);
            txt_fb_album_name.setText(album.getName());
            album_container.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent photoIntent = new Intent(mContext, FacebookPhotosActivity.class);
            photoIntent.putExtra(ALBUM_EXTRA, album);
            mContext.startActivityForResult(photoIntent, FACEBOOK_PHOTOS_REQUEST_CODE);
        }
    }
}
