package me.muapp.android.UI.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
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


public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder> {
    private static final int VIEW_TYPE_CRUSHES = 0;
    private static final int VIEW_TYPE_MATCHES = 1;

    private SortedList<ConversationItem> conversations;
    private final LayoutInflater mInflater;
    private Context mContext;
    private CrushesAdapter crushesAdapter;

    public void addConversationCrush(ConversationItem conversationItem) {
        crushesAdapter.addConversation(conversationItem);
    }

    public ChatsAdapter(Context context, CrushesAdapter crushesAdapter) {
        this.crushesAdapter = crushesAdapter;
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
                return new Date(o1.getConversation().getLastMessage().getTimeStamp()).compareTo(new Date(o2.getConversation().getLastMessage().getTimeStamp()));
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

    @Override
    public int getItemViewType(int position) {

        switch (position) {
            case 0:
                return VIEW_TYPE_CRUSHES;
            default:
                return VIEW_TYPE_MATCHES;
        }
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

    public void addConversation(ConversationItem c) {
        conversations.add(c);
    }

    interface ChatInterface {
        void bind(ConversationItem conversation);

    }

    @Override
    public ChatsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ChatsViewHolder holder;
        switch (viewType) {
            case VIEW_TYPE_CRUSHES:
                View crushesView = mInflater.inflate(R.layout.crushes_container_layout, parent, false);
                CrushesViewHolder crushesViewHolder = new CrushesViewHolder(crushesView);
                crushesViewHolder.setCrushesAdapter(crushesAdapter);
                holder = crushesViewHolder;
                break;
            default:
                View matchesView = mInflater.inflate(R.layout.matches_item_layout, parent, false);
                holder = new MatchesViewHolder(matchesView);
                break;
        }
        return holder;


    }

    @Override
    public void onBindViewHolder(ChatsViewHolder holder, int position) {
        if (position == 0)
            holder.bind(null);
        else
            holder.bind(conversations.get(position - 1));
    }

    @Override
    public int getItemCount() {
        return conversations.size() + 1;
    }

    public class ChatsViewHolder extends RecyclerView.ViewHolder implements ChatInterface {
        public ChatsViewHolder(View itemView) {
            super(itemView);
        }


        @Override
        public void bind(ConversationItem conversation) {

        }
    }

    public class CrushesViewHolder extends ChatsViewHolder {
        RecyclerView recycler_crushes;

        public CrushesViewHolder(View itemView) {
            super(itemView);
            recycler_crushes = (RecyclerView) itemView.findViewById(R.id.recycler_crushes);
        }

        public void setCrushesAdapter(CrushesAdapter crushesAdapter) {
            LinearLayoutManager linearLayoutManagerHorizontal = new LinearLayoutManager(mContext);
            linearLayoutManagerHorizontal.setOrientation(LinearLayoutManager.HORIZONTAL);
            linearLayoutManagerHorizontal.setStackFromEnd(true);
            linearLayoutManagerHorizontal.setReverseLayout(true);
            recycler_crushes.setLayoutManager(linearLayoutManagerHorizontal);
            recycler_crushes.setAdapter(crushesAdapter);
        }

    }

    public class MatchesViewHolder extends ChatsViewHolder implements View.OnClickListener {
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

        @Override
        public void bind(ConversationItem conversation) {
            super.bind(conversation);
            thisConversation = conversation;
            match_item_container.setOnClickListener(this);
            Glide.with(mContext).load(conversation.getProfilePicture()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ic_logo_muapp_no_caption).bitmapTransform(new CropCircleTransformation(mContext)).into(matchImage);
            matchLine1.setText(conversation.getFullName());
            matchLine2.setText(conversation.getConversation().getLastMessage().getContent());
        }

        @Override
        public void onClick(View v) {
            Intent chatIntent = new Intent(mContext, ChatActivity.class);
            chatIntent.putExtra(CONVERSATION_EXTRA, thisConversation);
            mContext.startActivity(chatIntent);
        }
    }
}
