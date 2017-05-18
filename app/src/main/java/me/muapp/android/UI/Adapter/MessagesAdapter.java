package me.muapp.android.UI.Adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.curioustechizen.ago.RelativeTimeTextView;

import java.util.Date;

import me.muapp.android.Classes.Chat.Message;
import me.muapp.android.R;

/**
 * Created by rulo on 17/05/17.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageContentHolder> {
    SortedList<Message> messageList;
    Context context;
    LayoutInflater mInflater;
    MediaPlayer mediaPlayer;

    Integer loggedUserId;

    public void setLoggedUserId(Integer loggedUserId) {
        this.loggedUserId = loggedUserId;
    }

    private static final int TYPE_SENDER = 0;
    private static final int TYPE_RECEIVER = 1;

    public void addMessage(Message m) {
        messageList.add(m);
    }

    public MessagesAdapter(Context context) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.messageList = new SortedList<>(Message.class, new SortedList.Callback<Message>() {
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
            public int compare(Message o1, Message o2) {
                return new Date(o1.getTimeStamp()).compareTo(new Date(o2.getTimeStamp()));
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(Message oldItem, Message newItem) {
                return false;
            }

            @Override
            public boolean areItemsTheSame(Message item1, Message item2) {
                return false;
            }
        });
    }

    @Override
    public MessageContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MessageContentHolder holder;
        if (viewType == TYPE_SENDER) {
            View senderView = mInflater.inflate(R.layout.message_item_layout_sender, parent, false);
            holder = new MyMessageContentHolder(senderView);
        } else {
            View receiverView = mInflater.inflate(R.layout.message_item_layout_receiver, parent, false);
            holder = new YourMessageContentHolder(receiverView);
        }
        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).getSenderId() == loggedUserId ? TYPE_SENDER : TYPE_RECEIVER;
    }

    @Override
    public void onBindViewHolder(MessageContentHolder holder, int position) {
        holder.bind(messageList.get(position));
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    interface UserMessagesInterface {
        void bind(Message message);
    }

    public class MessageContentHolder extends RecyclerView.ViewHolder implements UserMessagesInterface {
        public MessageContentHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bind(Message message) {

        }
    }

    public class MyMessageContentHolder extends MessageContentHolder {
        RelativeTimeTextView txt_time_sender;
        TextView txt_content_sender;

        public MyMessageContentHolder(View itemView) {
            super(itemView);
            txt_time_sender = (RelativeTimeTextView) itemView.findViewById(R.id.txt_time_sender);
            txt_content_sender = (TextView) itemView.findViewById(R.id.txt_content_sender);
        }

        @Override
        public void bind(Message message) {
            txt_time_sender.setReferenceTime(message.getTimeStamp());
            txt_content_sender.setText(message.getContent());
        }
    }

    public class YourMessageContentHolder extends MessageContentHolder {
        RelativeTimeTextView txt_time_receiver;
        TextView txt_content_receiver;

        public YourMessageContentHolder(View itemView) {
            super(itemView);
            txt_time_receiver = (RelativeTimeTextView) itemView.findViewById(R.id.txt_time_receiver);
            txt_content_receiver = (TextView) itemView.findViewById(R.id.txt_content_receiver);
        }

        @Override
        public void bind(Message message) {
            txt_time_receiver.setReferenceTime(message.getTimeStamp());
            txt_content_receiver.setText(message.getContent());
        }
    }
}
