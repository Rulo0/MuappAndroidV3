package me.muapp.android.UI.Adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.util.List;

import me.muapp.android.R;

/**
 * Created by rulo on 18/04/17.
 */

public class ProfilePicturesAdapter extends PagerAdapter {
    private Context context;
    private List<String> userAlbum;
    private final LayoutInflater mInflater;

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v = mInflater.inflate(R.layout.profile_picture_item_layout, container, false);
        ImageView img_profile_picture_big = (ImageView) v.findViewById(R.id.img_profile_picture_big);
        Glide.with(context).load(userAlbum.get(position)).centerCrop().placeholder(R.drawable.ic_placeholder).into(img_profile_picture_big);
        container.addView(v);
        return v;
    }

    public ProfilePicturesAdapter(Context context, List<String> userAlbum) {
        this.context = context;
        this.userAlbum = userAlbum;
        this.mInflater = LayoutInflater.from(context);
    }

    public void setUserAlbum(List<String> newAlbum) {
        this.userAlbum = newAlbum;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return userAlbum.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }
    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

}
