package com.test.mymp3.offline;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.mymp3.ItemSong;
import com.test.mymp3.play_screen.PlayActivity;
import com.test.mymp3.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by n on 27/08/2017.
 */

public class TabBaiHat extends Fragment {
    private RecyclerView rcMusicOffline;
    private AdapterRecycleView mAdapter;
    private List<ItemSong> listSongs;
    private static final String TAG = TabBaiHat.class.getSimpleName();
    private List<String> listPath;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_song, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inits();
        getMusic();
    }

    public void getMusic() {
        String info[] = new String[]{
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM
        };

        Uri uriMusic = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = getView().getContext().getContentResolver().query(uriMusic, info, null, null, null);

        if (null == cursor){
            return;
        }

        cursor.moveToFirst();
        int indexData = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        int indexTitle = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int indexArtist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int indexDuration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

        while (!cursor.isAfterLast()){
            String path = cursor.getString(indexData);
            String title = cursor.getString(indexTitle);
            String artist = cursor.getString(indexArtist);
            int duration = cursor.getInt(indexDuration);

            listSongs.add( new ItemSong(path, title, artist, duration));
            listPath.add(path);
            cursor.moveToNext();
        }
        cursor.close();

    }

    public Bitmap getAlbumart(Uri uri, Context context) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        byte[] rawArt;
        Bitmap art=null;
        BitmapFactory.Options bfo=new BitmapFactory.Options();

        mmr.setDataSource(context, uri);
        rawArt = mmr.getEmbeddedPicture();
        if (null != rawArt) {
            art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
        }
        return art;
    }

    private void inits() {
        listSongs = new ArrayList<>();
        listPath = new ArrayList<>();
        rcMusicOffline = getView().findViewById(R.id.rc_music_offline);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rcMusicOffline.setLayoutManager(manager);
        mAdapter = new AdapterRecycleView();
        rcMusicOffline.setAdapter(mAdapter);

    }

    class AdapterRecycleView extends RecyclerView.Adapter<HolderMusic> {

        @Override
        public HolderMusic onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
            return new HolderMusic(view);
        }

        @Override
        public void onBindViewHolder(HolderMusic holder, int position) {
            holder.songName.setText(listSongs.get(position).getSongName());
            holder.artist.setText(listSongs.get(position).getSongArtist());
            holder.imgSong.setImageBitmap(listSongs.get(position).getImgSong());

            Uri u = Uri.parse(listSongs.get(position).getSongPath());
            Bitmap b = getAlbumart(u, getContext());
            holder.imgSong.setImageBitmap(b);
        }

        @Override
        public int getItemCount() {
            return listSongs.size();
        }

    }

    public class HolderMusic extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView songName;
        private TextView artist;
        private ImageView imgSong;

        public HolderMusic(View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.item_song_name);
            artist = itemView.findViewById(R.id.item_song_artist);
            imgSong = itemView.findViewById(R.id.item_song_img);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int position = getLayoutPosition();
            Intent intent = new Intent();
            intent.putExtra("LISTPATH", (Serializable) listPath);
            intent.putExtra("POSITION", position);
            intent.setClass(getContext(), PlayActivity.class);
            startActivity(intent);


        }
    }
}
