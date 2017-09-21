package com.test.mymp3.offline;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.test.mymp3.R;

/**
 * Created by n on 09/08/2017.
 */

public class ListSongOffline extends AppCompatActivity {
    private TabLayout tabMusicOffline;
    private ViewPager vpMusicOffline;
    private AdapterViewpager mAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_song);
        initComponents();
    }

    private void initComponents() {
        tabMusicOffline = (TabLayout) findViewById(R.id.tab_music_offline);
        vpMusicOffline = (ViewPager) findViewById(R.id.vp_music_offline);
        mAdapter = new AdapterViewpager(getSupportFragmentManager());
        vpMusicOffline.setAdapter(mAdapter);

        tabMusicOffline.setupWithViewPager(vpMusicOffline);
        tabMusicOffline.getTabAt(0).setText("Bài Hát");
        tabMusicOffline.getTabAt(1).setText("Playlist");
        tabMusicOffline.getTabAt(2).setText("Ca sĩ");
        tabMusicOffline.getTabAt(3).setText("Album");
    }

    public static class AdapterViewpager extends FragmentPagerAdapter {

        public AdapterViewpager(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new TabBaiHat();
                case 1:
                    return new TabPlaylist();
                case 2:
                    return new TabArtist();
                default:
                    return new TabAlbum();
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }



}


