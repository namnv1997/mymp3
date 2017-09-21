package com.test.mymp3.play_screen;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.test.mymp3.ItemSong;
import com.test.mymp3.R;

import java.io.Serializable;
import java.util.List;

/**
 * Created by n on 30/08/2017.
 */

public class VPListSong extends Fragment {
    private RecyclerView rcSong;
    private List<ItemSong> listSongs;
    private VpListAdapter mAdapter;

    public VPListSong(List<ItemSong> listSongs) {
        this.listSongs = listSongs;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.vp_list_song, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inits();
    }

    private void inits() {
        rcSong = getView().findViewById(R.id.rc_song_no_image);
        mAdapter = new VpListAdapter();
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rcSong.setLayoutManager(manager);
        rcSong.setAdapter(mAdapter);
    }

    class VpListAdapter extends RecyclerView.Adapter<HolderVpList>{
        int start = 1;
        @Override
        public HolderVpList onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_no_image,parent, false);
            return new HolderVpList(view);
        }

        @Override
        public void onBindViewHolder(HolderVpList holder, int position) {

            holder.tvName.setText(listSongs.get(position).getSongName());
            holder.tvArtist.setText(listSongs.get(position).getSongArtist());
            holder.tvSongPosition.setText("" + start++);
        }

        @Override
        public int getItemCount() {
            return listSongs.size();
        }

    }

    public class HolderVpList extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvName, tvArtist, tvSongPosition;
        int position;
        public HolderVpList(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.item_song_name);
            tvArtist = itemView.findViewById(R.id.item_song_artist);
            tvSongPosition = itemView.findViewById(R.id.item_song_position);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            position = getLayoutPosition();
            Intent intent = new Intent();
            intent.putExtra("POSITION", position);
            intent.setAction("songChange");
            getContext().sendBroadcast(intent);
        }
    }

    interface IListMusic{
        ItemSong getSong(int position);
    }
}

