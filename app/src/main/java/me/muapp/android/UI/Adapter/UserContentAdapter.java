package me.muapp.android.UI.Adapter;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;
import java.util.HashMap;

import me.muapp.android.Classes.Internal.UserContent;
import me.muapp.android.R;

/**
 * Created by rulo on 18/04/17.
 */

public class UserContentAdapter extends RecyclerView.Adapter<UserContentAdapter.UserContentHolder> {
    SortedList<UserContent> userContentList;
    HashMap<String, Integer> viewTypeMap = new HashMap<String, Integer>() {{
        put("contentAud", 1);
        put("contentCmt", 2);
        put("contentGif", 3);
        put("contentPic", 4);
        put("contentQte", 5);
        put("contentSpt", 6);
        put("contentVid", 7);
        put("contentYtv", 8);
    }};
    Context context;
    LayoutInflater mInflater;

    public UserContentAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.userContentList = new SortedList<>(UserContent.class, new SortedList.Callback<UserContent>() {
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
            public int compare(UserContent o1, UserContent o2) {
                return new Date(o2.getCreatedAt()).compareTo(new Date(o1.getCreatedAt()));
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(UserContent oldItem, UserContent newItem) {
                return newItem.toString().equals(oldItem.toString());
            }

            @Override
            public boolean areItemsTheSame(UserContent item1, UserContent item2) {
                return item1.getKey().equals(item2.getKey());
            }
        });
        this.context = context;
    }


    @Override
    public UserContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = mInflater.inflate(R.layout.user_content_picture_item_layout, parent, false);
        return new PictureHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UserContentHolder holder, int position) {
        holder.bind(userContentList.get(position));
    }

    public void addContent(UserContent content) {
        userContentList.add(content);
    }

    @Override
    public int getItemCount() {
        return viewTypeMap.size();
    }

    interface userContentBinderInterface {
        void bind(UserContent content);
    }

    public class UserContentHolder extends RecyclerView.ViewHolder implements userContentBinderInterface {
        public UserContentHolder(View v) {
            super(v);
        }

        @Override
        public void bind(UserContent content) {

        }
    }

    public class PictureHolder extends UserContentHolder {
        TextView txt_test;

        public PictureHolder(View v) {
            super(v);
            this.txt_test = (TextView) v.findViewById(R.id.txt_test);
        }

        @Override
        public void bind(UserContent content) {
            super.bind(content);
            txt_test.setText(content.getKey());
        }
    }


}
