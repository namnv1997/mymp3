package com.test.mymp3.play_screen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.test.mymp3.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by n on 30/08/2017.
 */

public class VPPlayMusic extends Fragment implements MediaPlayer.OnErrorListener {
    private static final String TAG = VPPlayMusic.class.getSimpleName();
    private ISetPicture mInterf;
    private CircleImageView mCircle;
    private boolean isPrepare;
    private BroadCastImg broadCastImg;

    public VPPlayMusic(ISetPicture mInterf) {
        this.mInterf = mInterf;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.vp_play_music, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initComponents(view);
        registerBroad();
    }

    private void registerBroad() {
        broadCastImg = new BroadCastImg();
        IntentFilter filter = new IntentFilter();
        filter.addAction("imageSong");
        getContext().registerReceiver(broadCastImg, filter);
    }

    private void initComponents(View view) {
        mCircle = view.findViewById(R.id.circle_image);
        mCircle.setImageBitmap(mInterf.getBitMap());

    }



    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        Log.d(TAG, "OnError");
        return false;
    }

    interface ISetPicture {

        Bitmap getBitMap();

        String getLink();
    }

    private class BroadCastImg extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case "imageSong":
                    String path = intent.getStringExtra("LINK");
                    Uri uri = Uri.parse(path);
                    Bitmap b = PlayActivity.getAlbumart(uri, getContext());
                    mCircle.setImageBitmap(b);
            }
        }
    }

    @Override
    public void onDestroy() {
        getContext().unregisterReceiver(broadCastImg);
        super.onDestroy();
    }
}
