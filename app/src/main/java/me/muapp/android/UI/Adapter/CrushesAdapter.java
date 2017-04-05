package me.muapp.android.UI.Adapter;

import android.content.Context;
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
import me.muapp.android.Classes.Quickblox.cache.DialogCacheObject;
import me.muapp.android.R;

/**
 * Created by rulo on 4/04/17.
 */

public class CrushesAdapter extends RecyclerView.Adapter<CrushesAdapter.MatchesViewHolder> {
    private final LayoutInflater mInflater;
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
            Glide.with(mContext).load(dialog.getOpponentPhoto()).placeholder(R.drawable.ic_logo_muapp_no_caption).centerCrop().bitmapTransform(new CropCircleTransformation(mContext)).into(img_crush_photo);
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
    }
}
