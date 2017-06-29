package me.muapp.android.UI.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.muapp.android.Classes.Chat.ConversationItem;
import me.muapp.android.R;
import me.muapp.android.UI.Activity.ChatActivity;

import static me.muapp.android.UI.Activity.ChatActivity.CONVERSATION_EXTRA;

/**
 * Created by rulo on 4/04/17.
 */

public class CrushesAdapter extends RecyclerView.Adapter<CrushesAdapter.CrushViewHolder> implements Filterable {
    private List<ConversationItem> conversationItemList;
    private SortedList<ConversationItem> itemSortedList;
    private final LayoutInflater mInflater;
    private Context mContext;
    private ConversationFilter conversationFilter;

    public CrushesAdapter(Context context) {
        this.conversationItemList = new ArrayList<>();
        this.itemSortedList = new SortedList<>(ConversationItem.class, new SortedList.Callback<ConversationItem>() {
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
        for (ConversationItem item : conversationItemList) {
            if (item.getKey().equals(key) && !item.getProfilePicture().equals(newUrl)) {
                item.setProfilePicture(newUrl);
                break;
            }
        }
        for (int i = 0; i < itemSortedList.size(); i++) {
            if (itemSortedList.get(i).getKey().equals(key) && !itemSortedList.get(i).getProfilePicture().equals(newUrl)) {
                itemSortedList.get(i).setProfilePicture(newUrl);
                notifyDataSetChanged();
                break;
            }
        }
    }


    public void clearConversations() {
        conversationItemList.clear();
        itemSortedList.clear();
    }

    public void addConversation(ConversationItem c) {
        conversationItemList.add(c);
        itemSortedList.addAll(conversationItemList);
    }

    public void removeConversation(String conversationKey) {
        for (ConversationItem item : conversationItemList) {
            if (item.getKey().equals(conversationKey)) {
                conversationItemList.remove(item);
                break;
            }
        }

        for (int i = 0; i < itemSortedList.size(); i++) {
            if (itemSortedList.get(i).getKey().equals(conversationKey)) {
                itemSortedList.removeItemAt(i);
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
        holder.bind(itemSortedList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemSortedList.size();
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
            Glide.with(mContext).load(item.getProfilePicture()).error(R.drawable.ic_placeholder_error).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ic_placeholder).bitmapTransform(new CropCircleTransformation(mContext)).into(img_crush_photo);
            txt_crush_name.setText(item.getName());
            img_crush_overlay.setVisibility(View.GONE);
            if (item.getConversation().getSeen() != null) {
                img_crush_notification.setVisibility(item.getConversation().getSeen() ? View.GONE : View.VISIBLE);
            } else {
                img_crush_notification.setVisibility(View.VISIBLE);
            }
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

    @Override
    public Filter getFilter() {
        if (conversationFilter == null)
            conversationFilter = new ConversationFilter(this, conversationItemList);
        return conversationFilter;
    }

    public class ConversationFilter extends Filter {
        private final CrushesAdapter adapter;
        private final List<ConversationItem> originalList;
        private final List<ConversationItem> filteredList;

        public ConversationFilter(CrushesAdapter adapter, List<ConversationItem> originalList) {
            super();
            this.adapter = adapter;
            this.originalList = new LinkedList<>(originalList);
            this.filteredList = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredList.clear();
            final String filterPattern = constraint.toString().toLowerCase().trim();
            final FilterResults results = new FilterResults();
            if (TextUtils.isEmpty(constraint)) {
                filteredList.addAll(originalList);
            } else {
                for (final ConversationItem conversation : originalList) {
                    if (conversation.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(conversation);
                    }
                }
            }
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        public void publishResults(CharSequence constraint, FilterResults results) {
            itemSortedList.clear();
            itemSortedList.addAll((ArrayList<ConversationItem>) results.values);
        }

    }
}