package com.test.mymp3.play_screen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.test.mymp3.ItemSong;
import com.test.mymp3.PlayService;
import com.test.mymp3.R;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by n on 30/08/2017.
 */

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {
    private List<String> listPath;
    public static List<ItemSong> listSongs;
    private int currentPosition;
    public static TextView tvSongName, tvArtist, tvTimeCurrent, tvTimeSong;
    private ViewPager vpPlayScreen;
    private CircleIndicator indicator;
    private AdapterVPPlayScreen mAdapter;
    public static ImageView ivBack, ivTimeOff, ivShuffle, ivPrevious, ivPlay, ivNext, ivLoop;
    public static SeekBar seekBar;
    private Handler mHandler;
    private boolean mIsPlay;
    private Intent intentPl;
    private BroadBack broadBack;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_play_screen);
        inits();
        getListSongs();
        reBroad();
    }

    private void reBroad() {
        broadBack = new BroadBack();
        IntentFilter filter = new IntentFilter();
        filter.addAction("back");
        registerReceiver(broadBack, filter);
    }

    private void inits() {
        listSongs = new ArrayList<>();
        mAdapter = new AdapterVPPlayScreen(getSupportFragmentManager());
        vpPlayScreen = (ViewPager) findViewById(R.id.vp_play_sreen);
        tvSongName = (TextView) findViewById(R.id.pl_screen_tv_song_name);
        tvArtist = (TextView) findViewById(R.id.pl_screen_tv_artist);
        tvTimeCurrent = (TextView) findViewById(R.id.pl_screen_tv_time_current);
        tvTimeSong = (TextView) findViewById(R.id.pl_screen_tv_time_song);
        ivBack = (ImageView) findViewById(R.id.pl_screen_back);
        ivTimeOff = (ImageView) findViewById(R.id.pl_screen_time_off);
        ivPrevious = (ImageView) findViewById(R.id.pl_screen_previous);
        ivPlay = (ImageView) findViewById(R.id.pl_screen_play);
        ivShuffle = (ImageView) findViewById(R.id.pl_screen_shuffle);
        ivNext = (ImageView) findViewById(R.id.pl_screen_next);
        ivLoop = (ImageView) findViewById(R.id.pl_screen_loop);
        seekBar = (SeekBar) findViewById(R.id.pl_screen_seek_bar);
        mHandler = new Handler();


        ivBack.setOnClickListener(this);
        ivLoop.setOnClickListener(this);
        ivTimeOff.setOnClickListener(this);
        ivPrevious.setOnClickListener(this);
        ivShuffle.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        seekBar.setOnClickListener(this);

        Intent intent = getIntent();
        listPath = (List<String>) intent.getSerializableExtra("LISTPATH");
        currentPosition = intent.getIntExtra("POSITION", 0);
        System.out.println("cuu"+currentPosition);

        vpPlayScreen.setAdapter(mAdapter);
        vpPlayScreen.setCurrentItem(1);
        indicator = (CircleIndicator) findViewById(R.id.circle_indicator);
        indicator.setViewPager(vpPlayScreen);


        intentPl = new Intent(this, PlayService.class);
        Intent intentx = new Intent();
        intentx.putExtra("LINK", currentPosition);
        intentPl.putExtra("LINK", currentPosition);
        intentx.setAction("select");
        sendBroadcast(intentx);
        startService(intentPl);

//        runOnUiThread(new Runnable() {
//            int time = 0;
//            int t = 0;
//
//            @Override
//            public void run() {{
//                    if (t > seekBar.getMax()) {
//                        return;
//                    }
//                    seekBar.setProgress(t++);
//                    tvTimeCurrent.setText(getTime(1000 * time++) + "");
//                }
//                mHandler.postDelayed(this, 1000);
//            }
//        });


//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                    if(mPlayer != null && mPlayer.isPrepare()){
//                        seekBar.setProgress(i/seekBar.getMax());
//                        tvTimeCurrent.setText(getTime(i)+"");
//                    }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
    }


    public static String getTime(int duration) {
        String result;
        int timetempt = duration / 1000;
        int minutes = timetempt / 60;
        int seconds = timetempt % 60;
        if (minutes < 10) {
            if (seconds < 10) {
                result = "0" + minutes + ":" + "0" + seconds;
            } else {
                result = "0" + minutes + ":" + seconds;
            }
        } else {
            if (seconds < 10) {
                result = minutes + ":" + "0" + seconds;
            } else {
                result = minutes + ":" + seconds;
            }
        }
        return result;
    }

    public static Bitmap getAlbumart(Uri uri, Context context) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        byte[] rawArt;
        Bitmap art = null;
        BitmapFactory.Options bfo = new BitmapFactory.Options();

        mmr.setDataSource(context, uri);
        rawArt = mmr.getEmbeddedPicture();
        if (null != rawArt) {
            art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
        }else {
            art = BitmapFactory.decodeResource(context.getResources(), R.drawable.bgr);
        }
        return art;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pl_screen_back:
                super.onBackPressed();
                break;
            case R.id.pl_screen_shuffle:
                break;
            case R.id.pl_screen_time_off:
                break;
            case R.id.pl_screen_previous:
                break;
            case R.id.pl_screen_play:
                break;
            case R.id.pl_screen_next:
                break;
            case R.id.pl_screen_loop:
                break;
            default:
                break;

        }
    }


    public class AdapterVPPlayScreen extends FragmentPagerAdapter {
        public AdapterVPPlayScreen(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new VPListSong(listSongs);
                case 1:
                    return new VPPlayMusic(new VPPlayMusic.ISetPicture() {
                        @Override
                        public Bitmap getBitMap() {
                            Uri uri = Uri.parse(listPath.get(currentPosition));
                            return getAlbumart(uri, getBaseContext());
                        }

                        @Override
                        public String getLink() {
                            return listPath.get(currentPosition);
                        }
                    });
                default:
                    return new VPLyric();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    public void getListSongs() {
        MediaMetadataRetriever mediaMetadataRetriever = (MediaMetadataRetriever) new MediaMetadataRetriever();
        ItemSong i;
        for (String s : listPath) {
            Uri uri = Uri.parse(s);
            mediaMetadataRetriever.setDataSource(this, uri);
            String title = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            i = new ItemSong();
            i.setSongPath(s);
            i.setSongName(title);
            i.setSongArtist(artist);
            listSongs.add(i);

        }
    }

    private void onBack(){
        super.onBackPressed();
    }

    private class BroadBack extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case "back":
                    onBack();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadBack);
        super.onDestroy();
    }
}
