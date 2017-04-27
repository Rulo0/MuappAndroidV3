package me.muapp.android.UI.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rd.PageIndicatorView;
import com.rd.animation.AnimationType;

import java.util.Arrays;
import java.util.Comparator;

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
    private LayoutInflater mLayoutInflater;
    private RecyclerView.Adapter mBaseAdapter;
    private SparseArray<Section> mSections = new SparseArray<>();


    public SectionedProfileAdapter(Context context, int sectionResourceId,
                                   RecyclerView.Adapter baseAdapter) {

        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSectionResourceId = sectionResourceId;
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

        public SectionViewHolder(View view) {
            super(view);
            pager_profile_pictures = (ViewPager) view.findViewById(R.id.pager_profile_pictures);
            indicator_profile_pictures = (PageIndicatorView) view.findViewById(R.id.indicator_profile_pictures);
            title = (TextView) view.findViewById(R.id.pillbox_section_text);
            indicator_profile_pictures.setViewPager(pager_profile_pictures);
        }

        public void bindHeader(Section section) {
            title.setText("");
            profilePicturesAdapter = new ProfilePicturesAdapter(mContext, section.user.getAlbum());
            pager_profile_pictures.setAdapter(profilePicturesAdapter);
            indicator_profile_pictures.setAnimationType(AnimationType.SWAP);
            indicator_profile_pictures.setCount(section.user.getAlbum().size());
            indicator_profile_pictures.setRadius(5);
            createHeader(section.user, title);
        }
    }

    private void createHeader(User user, TextView nameView) {
        String userAge = String.format(mContext.getString(R.string.format_user_years), user.getAge());
        SpannableString ssAge = new SpannableString(userAge);
        ssAge.setSpan(new StyleSpan(Typeface.BOLD), 0, ssAge.length(), 0);
        nameView.append(ssAge);
        user.setHometown("Chicken Town");
        user.setEducation("Some Place University (SPU)");
        user.setWork("MUAPP");
        if (!TextUtils.isEmpty(user.getHometown())) {
            nameView.append(mContext.getString(R.string.format_user_hometown));
            nameView.append(" ");
            String userHomeTown = user.getHometown();
            SpannableString ssHomeTown = new SpannableString(userHomeTown);
            ssHomeTown.setSpan(new StyleSpan(Typeface.BOLD), 0, ssHomeTown.length(), 0);
            nameView.append(ssHomeTown);
        }
        if (user.getVisibleEducation() && !TextUtils.isEmpty(user.getEducation())) {
            nameView.append(mContext.getString(R.string.format_user_studies));
            nameView.append(" ");
            String userStudies = user.getEducation();
            SpannableString ssStudies = new SpannableString(userStudies);
            ssStudies.setSpan(new StyleSpan(Typeface.BOLD), 0, ssStudies.length(), 0);
            nameView.append(ssStudies);
        }
        if (user.getVisibleWork() && !TextUtils.isEmpty(user.getWork())) {
            nameView.append(mContext.getString(R.string.format_user_work));
            nameView.append(" ");
            String userWork = user.getWork();
            SpannableString ssWork = new SpannableString(userWork);
            ssWork.setSpan(new StyleSpan(Typeface.BOLD), 0, userWork.length(), 0);
            nameView.append(ssWork);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int typeView) {
        if (typeView == SECTION_TYPE) {
            final View view = LayoutInflater.from(mContext).inflate(mSectionResourceId, parent, false);
            return new SectionViewHolder(view);
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