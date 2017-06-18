package me.muapp.android.UI.Adapter.UserPhotos;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.muapp.android.Classes.Internal.MutualFriends;
import me.muapp.android.R;

/**
 * Created by fickz on 16/06/2017.
 */

public class UserMutualFriendsAdapter extends RecyclerView.Adapter<UserMutualFriendsAdapter.MutualFriendViewHolder> {
    private List<MutualFriends.MutualFriend> mutualFriends;
    private final LayoutInflater mInflater;
    private Context mContext;

    public UserMutualFriendsAdapter(Context context, List<MutualFriends.MutualFriend> mutualFriends) {
        this.mutualFriends = mutualFriends;
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public MutualFriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = mInflater.inflate(R.layout.crushes_item_layout, parent, false);
        return new MutualFriendViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MutualFriendViewHolder holder, int position) {
        holder.bind(mutualFriends.get(position));
    }

    @Override
    public int getItemCount() {
        return mutualFriends.size();
    }

    public class MutualFriendViewHolder extends RecyclerView.ViewHolder {
        ImageView img_crush_photo;
        ImageView img_crush_overlay;
        ImageView img_crush_notification;
        TextView txt_crush_name;

        public MutualFriendViewHolder(View itemView) {
            super(itemView);
            this.img_crush_photo = (ImageView) itemView.findViewById(R.id.img_crush_photo);
            this.img_crush_overlay = (ImageView) itemView.findViewById(R.id.img_crush_overlay);
            this.img_crush_notification = (ImageView) itemView.findViewById(R.id.img_crush_notification);
            this.txt_crush_name = (TextView) itemView.findViewById(R.id.txt_crush_name);
        }

        public void bind(MutualFriends.MutualFriend mf) {
            Glide.with(mContext).load(mf.getPhoto()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ic_placeholder).bitmapTransform(new CropCircleTransformation(mContext)).into(img_crush_photo);
            img_crush_overlay.setVisibility(View.GONE);
            img_crush_notification.setVisibility(View.GONE);
            txt_crush_name.setText(mf.getFirstName()

            );
        }
    }
}

