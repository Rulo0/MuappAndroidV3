package me.muapp.android.UI.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import me.muapp.android.R;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     AddAttachmentDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 * <p>You activity (or fragment) needs to implement {@link ChatAttachmentListener}.</p>
 */
public class AddAttachmentDialogFragment extends BottomSheetDialogFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_ITEM_COUNT = "item_count";
    private ChatAttachmentListener mListener;

    // TODO: Customize parameters
    public static AddAttachmentDialogFragment newInstance(int itemCount) {
        final AddAttachmentDialogFragment fragment = new AddAttachmentDialogFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_ITEM_COUNT, itemCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chatitem_list_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(new ChatItemAdapter(getArguments().getInt(ARG_ITEM_COUNT)));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final Fragment parent = getParentFragment();
        if (parent != null) {
            mListener = (ChatAttachmentListener) parent;
        } else {
            mListener = (ChatAttachmentListener) context;
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    public interface ChatAttachmentListener {
        void onChatAttachmentItemClicked(int position);
    }


    private class ChatItemAdapter extends RecyclerView.Adapter<ChatItemAdapter.AddAttachmentHolder> {
        private final int mItemCount;

        class ItemAttachment {
            String title;
            int resourceId;

            public ItemAttachment(int titleId, int resourceId) {
                this.title = getContext().getString(titleId);
                this.resourceId = resourceId;
            }

            public String getTitle() {
                return title;
            }

            public int getResourceId() {
                return resourceId;
            }
        }

        ArrayList<ItemAttachment> itemAttachments = new ArrayList<ItemAttachment>() {{
            add(new ItemAttachment(R.string.lbl_add_about_me, R.drawable.ic_add_quotes));
            add(new ItemAttachment(R.string.lbl_add_voice_note, R.drawable.ic_add_mic));
            add(new ItemAttachment(R.string.lbl_add_gallery, R.drawable.ic_add_photo));
            add(new ItemAttachment(R.string.lbl_add_giphy, R.drawable.ic_add_giphy));
            add(new ItemAttachment(R.string.lbl_add_music, R.drawable.ic_add_spotify));
            add(new ItemAttachment(R.string.lbl_add_video, R.drawable.ic_add_youtube));
        }};


        ChatItemAdapter(int itemCount) {
            mItemCount = itemCount;
        }

        @Override
        public AddAttachmentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new AddAttachmentHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(AddAttachmentHolder holder, int position) {
            holder.bind(itemAttachments.get(position));
        }

        @Override
        public int getItemCount() {
            return mItemCount;
        }

        class AddAttachmentHolder extends RecyclerView.ViewHolder {

            TextView txt_attachment_title;
            ImageButton btn_attachment_item;

            AddAttachmentHolder(LayoutInflater inflater, ViewGroup parent) {
                super(inflater.inflate(R.layout.fragment_chatitem_list_dialog_item, parent, false));
                txt_attachment_title = (TextView) itemView.findViewById(R.id.txt_attachment_title);
                btn_attachment_item = (ImageButton) itemView.findViewById(R.id.btn_attachment_item);
                btn_attachment_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onChatAttachmentItemClicked(getAdapterPosition());
                            dismiss();
                        }
                    }
                });
            }

            private void bind(ItemAttachment itemAttachment) {
                btn_attachment_item.setImageResource(itemAttachment.getResourceId());
                txt_attachment_title.setText(String.valueOf(itemAttachment.getTitle()));
            }

        }
    }

}
