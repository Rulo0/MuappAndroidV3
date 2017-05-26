package me.muapp.android.UI.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.Date;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.muapp.android.Classes.Chat.ConversationItem;
import me.muapp.android.R;
import me.muapp.android.UI.Activity.ChatActivity;

import static me.muapp.android.UI.Activity.ChatActivity.CONVERSATION_EXTRA;


/**
 * Created by rulo on 4/04/17.
 */

public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.MatchesViewHolder> {
    private SortedList<ConversationItem> conversations;
    private final LayoutInflater mInflater;
    private Context mContext;

    public MatchesAdapter(Context context) {
        this.conversations = new SortedList<>(ConversationItem.class, new SortedList.Callback<ConversationItem>() {
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
            public int compare(ConversationItem o1, ConversationItem o2) {
                Long date1 = o1.getConversation().getLastMessage() != null ? o1.getConversation().getLastMessage().getTimeStamp() : o1.getConversation().getCreationDate();
                Long date2 = o2.getConversation().getLastMessage() != null ? o2.getConversation().getLastMessage().getTimeStamp() : o2.getConversation().getCreationDate();
                return new Date(date2).compareTo(new Date(date1));
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(ConversationItem oldItem, ConversationItem newItem) {
                return oldItem.toString().equals(newItem.toString());
            }

            @Override
            public boolean areItemsTheSame(ConversationItem item1, ConversationItem item2) {
                return item1.getConversation().getKey().equals(item2.getConversation().getKey());
            }
        });
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }

    public void updateConversationUser(String key, String newUrl) {
        for (int i = 0; i < conversations.size(); i++) {
            if (conversations.get(i).getKey().equals(key) && !conversations.get(i).getProfilePicture().equals(newUrl)) {
                conversations.get(i).setProfilePicture(newUrl);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void clearConversations() {
        conversations.clear();
    }

    public void addConversation(ConversationItem c) {
        conversations.add(c);
    }

    public void removeConversation(String conversationKey) {
        for (int i = 0; i < conversations.size(); i++) {
            if (conversations.get(i).getKey().equals(conversationKey)) {
                conversations.removeItemAt(i);
                break;
            }
        }
    }

    @Override
    public MatchesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = mInflater.inflate(R.layout.matches_item_layout, parent, false);
        return new MatchesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MatchesViewHolder holder, int position) {
        holder.bind(conversations.get(position));
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public class MatchesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView matchImage;
        TextView matchLine1;
        TextView matchLine2;
        ImageView matchIndicator;
        RelativeLayout match_item_container;
        ConversationItem thisConversation;

        public MatchesViewHolder(View itemView) {
            super(itemView);
            this.match_item_container = (RelativeLayout) itemView.findViewById(R.id.match_item_container);
            this.matchImage = (ImageView) itemView.findViewById(R.id.match_user_image);
            this.matchLine1 = (TextView) itemView.findViewById(R.id.match_item_line_1);
            this.matchLine2 = (TextView) itemView.findViewById(R.id.match_item_line_2);
            this.matchIndicator = (ImageView) itemView.findViewById(R.id.match_notification);
        }

        public void bind(ConversationItem conversation) {
            thisConversation = conversation;
            match_item_container.setOnClickListener(this);
            Glide.with(mContext).load(conversation.getProfilePicture()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ic_logo_muapp_no_caption).bitmapTransform(new CropCircleTransformation(mContext)).into(matchImage);
            matchLine1.setText(conversation.getFullName());
            if (conversation.getConversation().getLastMessage() != null)
                matchLine2.setText(conversation.getConversation().getLastMessage().getContent());
            else {
                matchLine2.setText("match hace");
            }
        }

        @Override
        public void onClick(View v) {
            Intent chatIntent = new Intent(mContext, ChatActivity.class);
            chatIntent.putExtra(CONVERSATION_EXTRA, thisConversation);
            mContext.startActivity(chatIntent);
        }
    }
}
