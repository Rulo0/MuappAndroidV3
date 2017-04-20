package me.muapp.android.UI.Adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rd.PageIndicatorView;
import com.rd.animation.AnimationType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import me.muapp.android.Classes.Internal.User;
import me.muapp.android.R;

/**
 * Created by rulo on 20/04/17.
 */

public class SectionedProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private static final int SECTION_TYPE = 0;

    private boolean mValid = true;
    private int mSectionResourceId;
    private int mTextResourceId;
    private int mImageResourceId;
    private LayoutInflater mLayoutInflater;
    private RecyclerView.Adapter mBaseAdapter;
    private SparseArray<Section> mSections = new SparseArray<>();


    public SectionedProfileAdapter(Context context, int sectionResourceId, int textResourceId, int imageResourceId,
                                   RecyclerView.Adapter baseAdapter) {

        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSectionResourceId = sectionResourceId;
        mTextResourceId = textResourceId;
        mImageResourceId = imageResourceId;
        mBaseAdapter = baseAdapter;
        mContext = context;

        mBaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                mValid = mBaseAdapter.getItemCount() > 0;
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                mValid = mBaseAdapter.getItemCount() > 0;
                notifyItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mValid = mBaseAdapter.getItemCount() > 0;
                notifyItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                mValid = mBaseAdapter.getItemCount() > 0;
                notifyItemRangeRemoved(positionStart, itemCount);
            }
        });
    }

    class SectionViewHolder extends RecyclerView.ViewHolder {
        ProfilePicturesAdapter profilePicturesAdapter;
        PageIndicatorView indicator_profile_pictures;
        ViewPager pager_profile_pictures;
        public TextView title;
        public ImageView icon;

        public SectionViewHolder(View view, int mTextResourceid, int mImageResourceId) {
            super(view);
            pager_profile_pictures = (ViewPager) view.findViewById(R.id.pager_profile_pictures);
            indicator_profile_pictures = (PageIndicatorView) view.findViewById(R.id.indicator_profile_pictures);
            title = (TextView) view.findViewById(mTextResourceid);
            icon = (ImageView) view.findViewById(mImageResourceId);
            indicator_profile_pictures.setViewPager(pager_profile_pictures);
        }

        public void bindHeader(Section section) {
            title.setText(section.getTitle());
            List<String> testing = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                testing.add(section.user.getAlbum().get(0));
            }
            profilePicturesAdapter = new ProfilePicturesAdapter(mContext, testing);
            pager_profile_pictures.setAdapter(profilePicturesAdapter);
            indicator_profile_pictures.setAnimationType(AnimationType.SWAP);
            indicator_profile_pictures.setCount(testing.size());
            indicator_profile_pictures.setRadius(5);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int typeView) {
        if (typeView == SECTION_TYPE) {
            final View view = LayoutInflater.from(mContext).inflate(mSectionResourceId, parent, false);
            return new SectionViewHolder(view, mTextResourceId, mImageResourceId);
        } else {
            return mBaseAdapter.onCreateViewHolder(parent, typeView - 1);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder sectionViewHolder, int position) {
        if (isSectionHeaderPosition(position)) {
            ((SectionViewHolder) sectionViewHolder).bindHeader(mSections.get(position));

        } else {
            mBaseAdapter.onBindViewHolder(sectionViewHolder, sectionedPositionToPosition(position));
        }

    }

    @Override
    public int getItemViewType(int position) {
        return isSectionHeaderPosition(position)
                ? SECTION_TYPE
                : mBaseAdapter.getItemViewType(sectionedPositionToPosition(position)) + 1;
    }


    public static class Section {
        int firstPosition;
        int sectionedPosition;
        User user;

        public Section(int firstPosition, User user) {
            this.firstPosition = firstPosition;
            this.user = user;
        }

        public CharSequence getTitle() {
            return user.getFirstName();
        }
    }


    public void setSections(Section[] sections) {
        mSections.clear();

        Arrays.sort(sections, new Comparator<Section>() {
            @Override
            public int compare(Section o, Section o1) {
                return (o.firstPosition == o1.firstPosition)
                        ? 0
                        : ((o.firstPosition < o1.firstPosition) ? -1 : 1);
            }
        });

        int offset = 0; // offset positions for the headers we're adding
        for (Section section : sections) {
            section.sectionedPosition = section.firstPosition + offset;
            mSections.append(section.sectionedPosition, section);
            ++offset;
        }

        notifyDataSetChanged();
    }

    public int positionToSectionedPosition(int position) {
        int offset = 0;
        for (int i = 0; i < mSections.size(); i++) {
            if (mSections.valueAt(i).firstPosition > position) {
                break;
            }
            ++offset;
        }
        return position + offset;
    }

    public int sectionedPositionToPosition(int sectionedPosition) {
        if (isSectionHeaderPosition(sectionedPosition)) {
            return RecyclerView.NO_POSITION;
        }

        int offset = 0;
        for (int i = 0; i < mSections.size(); i++) {
            if (mSections.valueAt(i).sectionedPosition > sectionedPosition) {
                break;
            }
            --offset;
        }
        return sectionedPosition + offset;
    }

    public boolean isSectionHeaderPosition(int position) {
        return mSections.get(position) != null;
    }


    @Override
    public long getItemId(int position) {
        return isSectionHeaderPosition(position)
                ? Integer.MAX_VALUE - mSections.indexOfKey(position)
                : mBaseAdapter.getItemId(sectionedPositionToPosition(position));
    }

    @Override
    public int getItemCount() {
        return (mValid ? mBaseAdapter.getItemCount() + mSections.size() : 0);
    }
}