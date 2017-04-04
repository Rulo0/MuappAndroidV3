package me.muapp.android.UI.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.muapp.android.Classes.Quickblox.QuickbloxHelper;
import me.muapp.android.Classes.Quickblox.cache.DialogCacheObject;
import me.muapp.android.R;
import me.muapp.android.UI.Activity.ChatActivity;

/**
 * Created by rulo on 4/04/17.
 */

public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.MatchesViewHolder> {
    private final LayoutInflater mInflater;
    private SortedList<DialogCacheObject> dialogs;
    private Context mContext;

    public MatchesAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.dialogs = null;
        this.mContext = context;
    }

    public void setDialogs(SortedList<DialogCacheObject> dialogs) {
        this.dialogs = dialogs;
        notifyDataSetChanged();
    }

    @Override
    public MatchesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = mInflater.inflate(R.layout.matches_item_layout, parent, false);
        return new MatchesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MatchesViewHolder holder, int position) {
        holder.bind(dialogs.get(position));
    }

    @Override
    public int getItemCount() {
        return dialogs.size();
    }

    public class MatchesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView matchImage;
        TextView matchLine1;
        TextView matchLine2;
        ImageView matchIndicator;
        RelativeLayout match_item_container;
        DialogCacheObject thisDialog;

        public MatchesViewHolder(View itemView) {
            super(itemView);
            this.match_item_container = (RelativeLayout) itemView.findViewById(R.id.match_item_container);
            this.matchImage = (ImageView) itemView.findViewById(R.id.match_user_image);
            this.matchLine1 = (TextView) itemView.findViewById(R.id.match_item_line_1);
            this.matchLine2 = (TextView) itemView.findViewById(R.id.match_item_line_2);
            this.matchIndicator = (ImageView) itemView.findViewById(R.id.match_notification);
        }

        public void bind(DialogCacheObject dialog) {
            thisDialog = dialog;
            match_item_container.setOnClickListener(this);
            Glide.with(mContext).load(dialog.getOpponentPhoto()).placeholder(R.drawable.ic_logo_muapp_no_caption).centerCrop().bitmapTransform(new CropCircleTransformation(mContext)).into(matchImage);
            matchLine1.setText(dialog.getOpponentName());
            if (dialog.getLastMessageUserId() == null ||
                    dialog.getDeletedAt() != null && dialog.getLastMessageDateSent() < dialog.getDeletedAt().getTime() / 1000) {
                //   matchLine2.setText(DateUtils.getMatchCrushTimeAgo(dialog.getCreatedAt().getTime(), mContext, false));
                matchLine2.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            } else { //with messages
                if (dialog.getLastMessage() != null && dialog.getLastMessage().length() > 0) {
                    if (dialog.getLastMessage().equalsIgnoreCase(QuickbloxHelper.POKE_IMAGE)) {
                        matchLine2.setText(mContext.getString(R.string.matches_last_message_photo));
                    } else if (dialog.getLastMessage().equalsIgnoreCase(QuickbloxHelper.POKE_STICKER)) {
                        matchLine2.setText(mContext.getString(R.string.matches_last_message_sticker));
                    } else if (dialog.getLastMessage().equalsIgnoreCase(QuickbloxHelper.POKE_VOICE)) {
                        matchLine2.setText(R.string.matches_last_message_voice_message);
                    } else {
                        matchLine2.setText(dialog.getLastMessage());
                    }
                } else {
                    matchLine2.setText("");
                }
                matchLine2.setTextColor(ContextCompat.getColor(mContext, R.color.color_muapp_dark));
            }
        }

        @Override
        public void onClick(View v) {
            Intent chatIntent = new Intent(mContext, ChatActivity.class);
            chatIntent.putExtra("DIALOG", thisDialog);
            mContext.startActivity(chatIntent);
        }
    }
}
