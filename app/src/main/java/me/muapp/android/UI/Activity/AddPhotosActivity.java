package me.muapp.android.UI.Activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.fenchtose.nocropper.CropperView;

import me.muapp.android.R;
import me.muapp.android.UI.Fragment.FacebookPhotosFragment;
import me.muapp.android.UI.Fragment.GalleryPhotosFragment;
import me.muapp.android.UI.Fragment.InstagramPhotosFragment;
import me.muapp.android.UI.Fragment.Interface.OnImageSelectedListener;

public class AddPhotosActivity extends BaseActivity implements OnImageSelectedListener {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    TabLayout tabLayout;
    int[] activeIcons = new int[]{R.drawable.ic_tab_fb, R.drawable.ic_tab_gal, R.drawable.ic_tab_inst};
    int[] inactiveIcons = new int[]{R.drawable.ic_tab_fb_inactive, R.drawable.ic_tab_gal_inactive, R.drawable.ic_tab_inst_inactive};
    CropperView cropper_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        cropper_view = (CropperView) findViewById(R.id.cropper_view);
        cropper_view.setGestureEnabled(true);
        cropper_view.setDebug(true);
        cropper_view.setPreScaling(true);
        Glide.with(this)
                .load(R.mipmap.bg_login)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(300, 300) {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                        Log.wtf("onResourceReady", bitmap.getByteCount() + "");
                        cropper_view.setImageBitmap(bitmap);
                    }
                });
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setupTabIcons(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        setupTabIcons(0);
    }


    private void setupTabIcons(int pos) {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setIcon(inactiveIcons[i]);
        }

        tabLayout.getTabAt(pos).setIcon(activeIcons[pos]);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onImageSelected(String name, Bitmap img) {
        Log.wtf("onImageSelected", img.getByteCount() + "");
        cropper_view.setImageBitmap(img);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        Fragment[] fragments = new Fragment[]{FacebookPhotosFragment.newInstance(loggedUser), GalleryPhotosFragment.newInstance(loggedUser), InstagramPhotosFragment.newInstance(loggedUser)};

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }
    }
}
