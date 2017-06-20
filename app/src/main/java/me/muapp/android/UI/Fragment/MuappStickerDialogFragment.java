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
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import me.muapp.android.Classes.Chat.MuappSticker;
import me.muapp.android.Classes.Util.PreferenceHelper;
import me.muapp.android.R;


public class MuappStickerDialogFragment extends BottomSheetDialogFragment {
    private MuappStickerListener mMuappStickerListener;
    RecyclerView recyclerView;

    public static MuappStickerDialogFragment newInstance() {
        final MuappStickerDialogFragment fragment = new MuappStickerDialogFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_muappsticker_list_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        recyclerView = (RecyclerView) view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            //Reading from file
          /*  InputStream is = getResources().openRawResource(R.raw.stickers);
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            Type listType = new TypeToken<List<MuappSticker>>() {
            }.getType();
            List<MuappSticker> stickers = new Gson().fromJson(reader, listType);
            for (MuappSticker sticker : stickers) {
                Log.wtf("Stickers", sticker.toString());
            }*/

            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            recyclerView.setAdapter(new MuappStickerAdapter(new PreferenceHelper(getContext()).getStickers()));
        } catch (Exception x) {
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final Fragment parent = getParentFragment();
        if (parent != null) {
            mMuappStickerListener = (MuappStickerListener) parent;
        } else {
            mMuappStickerListener = (MuappStickerListener) context;
        }
    }

    @Override
    public void onDetach() {
        mMuappStickerListener = null;
        super.onDetach();
    }

    public interface MuappStickerListener {
        void onMuappStickerClicked(MuappSticker sticker);
    }


    private class MuappStickerAdapter extends RecyclerView.Adapter<MuappStickerAdapter.StickerViewHolder> {
        List<MuappSticker> stickers;

        MuappStickerAdapter(List<MuappSticker> stickers) {
            this.stickers = stickers;

        }

        @Override
        public StickerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new StickerViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(StickerViewHolder holder, int position) {
            holder.bind(stickers.get(position));
        }

        @Override
        public int getItemCount() {
            return stickers.size();
        }


        class StickerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            final ImageView img_sticker;
            MuappSticker currentSticker;

            StickerViewHolder(LayoutInflater inflater, ViewGroup parent) {
                super(inflater.inflate(R.layout.fragment_muappsticker_list_dialog_item, parent, false));
                img_sticker = (ImageView) itemView.findViewById(R.id.img_sticker);

            }

            public void bind(MuappSticker sticker) {
                currentSticker = sticker;
                Glide.with(MuappStickerDialogFragment.this).load(sticker.getImage()).placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder_error).diskCacheStrategy(DiskCacheStrategy.ALL).into(img_sticker);
                img_sticker.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (mMuappStickerListener != null)
                    mMuappStickerListener.onMuappStickerClicked(currentSticker);
                dismiss();
            }
        }
    }

}
