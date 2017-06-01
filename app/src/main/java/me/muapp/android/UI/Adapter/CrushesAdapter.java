package me.muapp.android.UI.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Calendar;
import java.util.Date;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.muapp.android.Classes.Chat.ConversationItem;
import me.muapp.android.R;
import me.muapp.android.UI.Activity.ChatActivity;

import static me.muapp.android.UI.Activity.ChatActivity.CONVERSATION_EXTRA;

/**
 * Created by rulo on 4/04/17.
 */

public class CrushesAdapter extends RecyclerView.Adapter<CrushesAdapter.CrushViewHolder> {
    private SortedList<ConversationItem> conversations;
    private final LayoutInflater mInflater;
    private Context mContext;

    public CrushesAdapter(Context context) {
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
                return new Date(date1).compareTo(new Date(date2));
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
    public CrushViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = mInflater.inflate(R.layout.crushes_item_layout, parent, false);
        return new CrushViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CrushViewHolder holder, int position) {
        holder.bind(conversations.get(position));
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public class CrushViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img_crush_photo;
        ImageView img_crush_overlay;
        ImageView img_crush_notification;
        TextView txt_crush_name;
        ConversationItem thisConversation;

        public CrushViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.img_crush_photo = (ImageView) itemView.findViewById(R.id.img_crush_photo);
            this.img_crush_overlay = (ImageView) itemView.findViewById(R.id.img_crush_overlay);
            this.img_crush_notification = (ImageView) itemView.findViewById(R.id.img_crush_notification);
            this.txt_crush_name = (TextView) itemView.findViewById(R.id.txt_crush_name);
        }

        public void bind(ConversationItem item) {
            thisConversation = item;
            Glide.with(mContext).load(item.getProfilePicture()).placeholder(R.drawable.ic_placeholder).bitmapTransform(new CropCircleTransformation(mContext)).into(img_crush_photo);
            txt_crush_name.setText(item.getName());
            img_crush_overlay.setVisibility(View.GONE);
            img_crush_notification.setVisibility(View.GONE);
            try {
                final Calendar expirationDate = Calendar.getInstance();
                expirationDate.setTime(new Date(item.getConversation().getCreationDate()));
                expirationDate.add(Calendar.DATE, 1);
                long difference = expirationDate.getTime().getTime() - Calendar.getInstance().getTime().getTime();
                if (difference <= 0) {
                    img_crush_overlay.setVisibility(View.VISIBLE);
                    if (!item.getConversation().getLikeByMe())
                        img_crush_notification.setVisibility(View.VISIBLE);
                }
            } catch (Exception x) {

            }
        }

        @Override
        public void onClick(View v) {
            Intent chatIntent = new Intent(mContext, ChatActivity.class);
            chatIntent.putExtra(CONVERSATION_EXTRA, thisConversation);
            mContext.startActivity(chatIntent);
        }
    }
    /*private final LayoutInflater mInflater;
    private SortedList<DialogCacheObject> dialogs;
    private Context mContext;

    public CrushesAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.dialogs = new SortedList<>(DialogCacheObject.class, new SortedList.Callback<DialogCacheObject>() {
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
            public int compare(DialogCacheObject o1, DialogCacheObject o2) {
                return new Date(o2.getLastMessageDateSent()).compareTo(new Date(o1.getLastMessageDateSent()));
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(DialogCacheObject oldItem, DialogCacheObject newItem) {
                return oldItem.toString().equals(newItem.toString());
            }

            @Override
            public boolean areItemsTheSame(DialogCacheObject item1, DialogCacheObject item2) {
                return item1.equals(item2);
            }
        });
        this.mContext = context;
    }

    public void addDialog(DialogCacheObject dco) {
        dialogs.add(dco);
    }


    public void setDialogs(SortedList<DialogCacheObject> dialogs) {
        this.dialogs = dialogs;
        notifyDataSetChanged();
    }

    @Override
    public MatchesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = mInflater.inflate(R.layout.crushes_item_layout, parent, false);
        return new MatchesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MatchesViewHolder holder, int position) {
        holder.bind(dialogs.get(position));
    }

    @Override
    public int getItemCount() {

        return dialogs != null ? dialogs.size() : 0;
    }

    public class MatchesViewHolder extends RecyclerView.ViewHolder {
        ImageView img_crush_photo;
        ImageView img_crush_overlay;
        ImageView img_crush_notification;
        TextView txt_crush_name;

        public MatchesViewHolder(View itemView) {
            super(itemView);
            this.img_crush_photo = (ImageView) itemView.findViewById(R.id.img_crush_photo);
            this.img_crush_overlay = (ImageView) itemView.findViewById(R.id.img_crush_overlay);
            this.img_crush_notification = (ImageView) itemView.findViewById(R.id.img_crush_notification);
            this.txt_crush_name = (TextView) itemView.findViewById(R.id.txt_crush_name);
        }

        private String getFirstWord(String s) {
            try {
                return s.substring(0, s.indexOf(' '));
            } catch (Exception x) {
                return s;
            }
        }

        public void bind(DialogCacheObject dialog) {
            Glide.with(mContext).load(dialog.getOpponentPhoto()).placeholder(R.drawable.ic_placeholder).centerCrop().bitmapTransform(new CropCircleTransformation(mContext)).into(img_crush_photo);
            txt_crush_name.setText(getFirstWord(dialog.getOpponentName()));
            img_crush_overlay.setVisibility(View.GONE);
            if ((dialog.getUnreadMessageCount() != null && dialog.getUnreadMessageCount() > 0)) {
                img_crush_notification.setVisibility(View.VISIBLE);
            } else if (dialog.getLastMessageUserId() == null && !dialog.isSeen()) {
                img_crush_notification.setVisibility(View.VISIBLE);
            } else {
                img_crush_notification.setVisibility(View.GONE);
            }
            try {
                final Calendar expirationDate = Calendar.getInstance();
                expirationDate.setTime(dialog.getCreatedAt());
                expirationDate.add(Calendar.DATE, 1);
                long difference = expirationDate.getTime().getTime() - Calendar.getInstance().getTime().getTime();
                if (difference <= 0) {
                    img_crush_overlay.setVisibility(View.VISIBLE);
                    if (!dialog.isMyLike())
                        img_crush_notification.setVisibility(View.VISIBLE);
                }
            } catch (Exception x) {

            }
        }
    }*/
}
