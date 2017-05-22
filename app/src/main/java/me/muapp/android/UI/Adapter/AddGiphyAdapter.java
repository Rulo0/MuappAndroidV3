package me.muapp.android.UI.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import me.muapp.android.Classes.Chat.ChatReferences;
import me.muapp.android.Classes.Giphy.Data.GiphyEntry;
import me.muapp.android.R;
import me.muapp.android.UI.Activity.AddGiphyDetailActivity;

import static me.muapp.android.UI.Activity.AddGiphyDetailActivity.CURRENT_GIPHY;
import static me.muapp.android.UI.Activity.AddGiphyDetailActivity.GIPHY_CODE;
import static me.muapp.android.UI.Activity.ChatActivity.CONTENT_FROM_CHAT;

/**
 * Created by rulo on 28/03/17.
 */

public class AddGiphyAdapter extends RecyclerView.Adapter<AddGiphyAdapter.PhotoViewHolder> {
    private final LayoutInflater mInflater;
    private List<GiphyEntry> entries;
    private Context mContext;
    private Activity activity;
    ChatReferences chatReferences;

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setChatReferences(ChatReferences chatReferences) {
        this.chatReferences = chatReferences;
    }

    public AddGiphyAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.entries = new ArrayList<>();
        this.mContext = context;
    }

    public void addPhoto(GiphyEntry image) {
        try {
            int currentIndex = entries.size();
            entries.add(currentIndex, image);
            notifyItemInserted(currentIndex);
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public void addPhotos(List<GiphyEntry> entries) {
        try {
            this.entries = entries;
            notifyDataSetChanged();
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = mInflater.inflate(R.layout.giphy_entry_item_layout, parent, false);
        return new PhotoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        holder.bind(entries.get(position));
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }


    public class PhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img_giphy_add;
        View itemView;
        GiphyEntry currentGiphy;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.img_giphy_add = (ImageView) itemView.findViewById(R.id.img_giphy_add);
        }

        public void bind(final GiphyEntry entry) {
            try {
                this.currentGiphy = entry;
                Glide.with(mContext).load(entry.getImages().getPreviewGif().getUrl()).asGif().priority(Priority.IMMEDIATE).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).into(img_giphy_add);
                itemView.setOnClickListener(this);
            } catch (Exception x) {
                x.printStackTrace();
            }
        }

        @Override
        public void onClick(View v) {
            if (activity != null) {
                Intent giphyIntent = new Intent(activity, AddGiphyDetailActivity.class);
                giphyIntent.putExtra(CURRENT_GIPHY, currentGiphy);
                if (chatReferences != null)
                    giphyIntent.putExtra(CONTENT_FROM_CHAT, chatReferences);
                activity.startActivityForResult(giphyIntent, GIPHY_CODE);
            }
        }
    }
}
