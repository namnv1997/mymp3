package com.test.mymp3.offline;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.test.mymp3.R;

/**
 * Created by n on 06/08/2017.
 */

public class FragmentOffline extends Fragment implements View.OnClickListener {
    private TextView mSongStorage, mSongFavorite, mListSong, mSongAgo;
    private int sumMusic;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_offline, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initEvents();
        getMusic();
    }


    private void initViews() {
        mSongAgo = getView().findViewById(R.id.tv_number_song_ago);
        mListSong = getView().findViewById(R.id.tv_number_list);
        mSongStorage = getView().findViewById(R.id.tv_number_song_storage);
        mSongFavorite = getView().findViewById(R.id.tv_number_song_favorite);
    }

    private void initEvents() {
        getView().findViewById(R.id.tap_list).setOnClickListener(this);
        getView().findViewById(R.id.tap_ago).setOnClickListener(this);
        getView().findViewById(R.id.tap_song).setOnClickListener(this);
        getView().findViewById(R.id.tap_favorite).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tap_song:
                songStorage();
                break;
            case R.id.tap_favorite:
                favoriteSong();
                break;
            case R.id.tap_list:
                listUser();
                break;
            case R.id.tap_ago:
                songAgo();
                break;
            default:
                break;
        }
    }

    private void songStorage() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), ListSongOffline.class);
        startActivity(intent);

    }

    private void favoriteSong() {
        Toast.makeText(getContext(), "favorite", Toast.LENGTH_SHORT).show();
    }

    private void listUser() {
        Toast.makeText(getContext(), "list", Toast.LENGTH_SHORT).show();
    }

    private void songAgo() {
        Toast.makeText(getContext(), "songAgo", Toast.LENGTH_SHORT).show();
    }

    public void getMusic() {
        sumMusic = 0;
        String info[] = new String[]{
                MediaStore.Audio.Media.DATA,
        };

        Uri uriMusic = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = getContext().getContentResolver().query(uriMusic, info, null, null, null);

        if (null == cursor){
            return;
        }

        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            sumMusic +=1;
            cursor.moveToNext();
        }
        mSongStorage.setText(sumMusic+" bài hát");
        cursor.close();

    }
}


