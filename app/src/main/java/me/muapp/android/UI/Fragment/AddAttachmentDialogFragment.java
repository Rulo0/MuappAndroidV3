package me.muapp.android.UI.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
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
    private ChatAttachmentListener mListener;

    public enum AttachmentType {
        TypePicture,
        TypeSticker,
        TypeGif,
        TypeMusic,
        TypeYoutube
    }

    public static AddAttachmentDialogFragment newInstance() {
        final AddAttachmentDialogFragment fragment = new AddAttachmentDialogFragment();
        final Bundle args = new Bundle();
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new ChatItemAdapter());
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
        void onChatAttachmentItemClicked(AttachmentType type);
    }


    private class ChatItemAdapter extends RecyclerView.Adapter<ChatItemAdapter.AddAttachmentHolder> {
        class ItemAttachment {
            String title;
            int resourceId;
            AttachmentType attachmentType;

            public ItemAttachment(int titleId, int resourceId, AttachmentType attachmentType) {
                this.title = getContext().getString(titleId);
                this.resourceId = resourceId;
                this.attachmentType = attachmentType;
            }

            public String getTitle() {
                return title;
            }

            public int getResourceId() {
                return resourceId;
            }

            public AttachmentType getAttachmentType() {
                return attachmentType;
            }
        }

        ArrayList<ItemAttachment> itemAttachments = new ArrayList<ItemAttachment>() {{
            add(new ItemAttachment(R.string.lbl_add_gallery, R.drawable.ic_add_photo, AttachmentType.TypePicture));
            add(new ItemAttachment(R.string.lbl_add_sticker, R.drawable.ic_add_sticker, AttachmentType.TypeSticker));
            add(new ItemAttachment(R.string.lbl_add_giphy, R.drawable.ic_add_giphy, AttachmentType.TypeGif));
            add(new ItemAttachment(R.string.lbl_add_music, R.drawable.ic_add_spotify, AttachmentType.TypeMusic));
            add(new ItemAttachment(R.string.lbl_add_video, R.drawable.ic_add_youtube, AttachmentType.TypeYoutube));
        }};


        ChatItemAdapter() {

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
            return itemAttachments.size();
        }

        class AddAttachmentHolder extends RecyclerView.ViewHolder {

            TextView txt_attachment_title;
            ImageButton btn_attachment_item;

            AddAttachmentHolder(LayoutInflater inflater, ViewGroup parent) {
                super(inflater.inflate(R.layout.fragment_chatitem_list_dialog_item, parent, false));
                txt_attachment_title = (TextView) itemView.findViewById(R.id.txt_attachment_title);
                btn_attachment_item = (ImageButton) itemView.findViewById(R.id.btn_attachment_item);
            }

            private void bind(final ItemAttachment itemAttachment) {
                btn_attachment_item.setImageResource(itemAttachment.getResourceId());
                txt_attachment_title.setText(String.valueOf(itemAttachment.getTitle()));
                btn_attachment_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onChatAttachmentItemClicked(itemAttachment.getAttachmentType());
                            dismiss();
                        }
                    }
                });
            }

        }
    }

}
