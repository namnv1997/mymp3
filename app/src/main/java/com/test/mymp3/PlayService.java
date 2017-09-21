package com.test.mymp3;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;

import com.test.mymp3.play_screen.PlayActivity;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Random;

/**
 * Created by n on 15/09/2017.
 */

public class PlayService extends Service implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener, View.OnClickListener, MediaPlayer.OnCompletionListener {

    private static final String TAG = PlayService.class.getSimpleName();
    private MediaPlayer mPlayer;
    private int mPosition;
    private WeakReference<SeekBar> seekBar;
    private WeakReference<TextView> tvCu, tvSongName, tvArtist, tvTotalDuration;
    private WeakReference<ImageView> ivBack, ivTimeOff, ivShuffle, ivPrevious, ivPlay, ivNext, ivLoop;
    private Handler mHandler;
    private Utilities utils;
    private ImageBroadCast imageBroadCast;
    private SongBroadCast songBroadCast;
    private WeakReference<List<ItemSong>> listSongs;
    private boolean isShuffle;
    private boolean isLoop;
    private boolean isUserNext;

    @Override
    public void onCreate() {
        super.onCreate();
        utils = new Utilities();
        mPlayer = new MediaPlayer();
        mPlayer.setOnErrorListener(this);
        registerBroadCast();
    }

    private void registerBroadCast() {
        imageBroadCast = new ImageBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("select");
        registerReceiver(imageBroadCast, filter);

        songBroadCast = new SongBroadCast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("songChange");
        registerReceiver(songBroadCast, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initUI();
        mPosition = intent.getIntExtra("LINK", 0);
        if (!mPlayer.isPlaying()) {
            Uri uri = Uri.parse(listSongs.get().get(mPosition).getSongPath());
            mPlayer = MediaPlayer.create(getBaseContext(), uri);
            mPlayer.setOnCompletionListener(this);
            Log.d(TAG, "ok");
            play();
        }
        return START_STICKY;
    }

    private void initUI() {
        seekBar = new WeakReference<SeekBar>(PlayActivity.seekBar);
        mHandler = new Handler();
        tvCu = new WeakReference<TextView>(PlayActivity.tvTimeCurrent);
        tvTotalDuration = new WeakReference<TextView>(PlayActivity.tvTimeSong);
        tvSongName = new WeakReference<TextView>(PlayActivity.tvSongName);
        tvArtist = new WeakReference<TextView>(PlayActivity.tvArtist);
        ivBack = new WeakReference<ImageView>(PlayActivity.ivBack);
        ivTimeOff = new WeakReference<ImageView>(PlayActivity.ivTimeOff);
        ivPrevious = new WeakReference<ImageView>(PlayActivity.ivPrevious);
        ivPlay = new WeakReference<ImageView>(PlayActivity.ivPlay);
        ivNext = new WeakReference<ImageView>(PlayActivity.ivNext);
        ivShuffle = new WeakReference<ImageView>(PlayActivity.ivShuffle);
        ivLoop = new WeakReference<ImageView>(PlayActivity.ivLoop);
        listSongs = new WeakReference<List<ItemSong>>(PlayActivity.listSongs);

        ivBack.get().setOnClickListener(this);
        ivShuffle.get().setOnClickListener(this);
        ivLoop.get().setOnClickListener(this);
        ivNext.get().setOnClickListener(this);
        ivPlay.get().setOnClickListener(this);
        ivPrevious.get().setOnClickListener(this);
        ivTimeOff.get().setOnClickListener(this);

        seekBar.get().setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    mHandler.removeCallbacks(mUpdateTimeTask);
                    mPlayer.seekTo(i * mPlayer.getDuration() / 100);
                    updateProgressBar();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    //Cập nhật thời gian trên seekbar
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {

        @Override
        public void run() {
            long totalDuration = 0;
            try {
                totalDuration = mPlayer.getDuration();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            long currentDuration = 0;
            try {
                currentDuration = mPlayer.getCurrentPosition();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }


            //Hiển thị thời gian hoàn thành
            tvCu.get().setText("" + utils.milliSecondsToTimer(currentDuration));
            tvTotalDuration.get().setText("" + utils.milliSecondsToTimer(totalDuration));
            //Thực hiện việc cập nhật progress bar
            int progress = (int) (utils.getProgressPercentage(currentDuration,
                    totalDuration));
            // Log.d("Progress", ""+progress);
            seekBar.get().setProgress(progress);
            //Chạy lại sau 0,1s
            mHandler.postDelayed(this, 100);
        }

    };


    public synchronized void play() {
        //Thiết lập giá trị thanh Progress bar
        updateUI();
        ivPlay.get().setImageResource(R.drawable.ic_pause_white_48dp);
        seekBar.get().setProgress(0);
        seekBar.get().setMax(100);
        mPlayer.start();
        updateProgressBar();
        createNotification(mPosition, true);
    }

    private void updateUI() {
        tvSongName.get().setText(listSongs.get().get(mPosition).getSongName());
        tvArtist.get().setText(listSongs.get().get(mPosition).getSongArtist());

    }

    private void createNotification(int position, boolean isPlay) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//        builder.setContentText(mSongs.get(position).getName());
//        builder.setContentTitle("Media Pro");
//        builder.setLargeIcon(BitmapFactory.decodeResource(
//                getResources(), R.mipmap.ic_launcher));
//        builder.setAutoCancel(false);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.layout_nofifiction);
        remoteViews.setTextViewText(R.id.tv_name, listSongs.get().get(position).getSongName() +" - " +listSongs.get().get(position).getSongArtist() );
        if (isPlay) {
            //play
            remoteViews.setImageViewResource(R.id.btn_play, android.R.drawable.ic_media_pause);
        } else {
            //set lai icon pause
            remoteViews.setImageViewResource(R.id.btn_play, android.R.drawable.ic_media_play);
        }

        Intent intent = new Intent();
        intent.setAction("PLAY");
        PendingIntent pendingPlay = PendingIntent.getBroadcast(this, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.btn_play, pendingPlay);


        builder.setCustomContentView(remoteViews);
        builder.setCustomBigContentView(remoteViews);


        if (isPlay) {
            stopForeground(false);
            builder.setAutoCancel(false);
            startForeground(101, builder.build());
        } else {

            builder.setAutoCancel(true);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(101);
            manager.notify(101, builder.build());
        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pl_screen_back:
                goBack();
                break;
            case R.id.pl_screen_previous:
                previousSong();
                break;
            case R.id.pl_screen_play:
                playSong();
                break;
            case R.id.pl_screen_next:
                isUserNext = true;
                nextSong();
                break;
            case R.id.pl_screen_shuffle:
                shuffleSong();
                break;
            case R.id.pl_screen_loop:
                loopSong();
                break;
            case R.id.pl_screen_time_off:
                timeOff();
                break;
            default:
                break;
        }
    }

    private void timeOff() {

    }

    private void goBack() {
        Intent intent = new Intent();
        intent.setAction("back");
        sendBroadcast(intent);
    }

    private void playSong() {
        if (mPlayer.isPlaying()) {
            if (mPlayer != null) {
                mPlayer.pause();
                //Thay đổi image button
                ivPlay.get().setImageResource(R.drawable.ic_play_arrow_white_48dp);
                Log.d("Player Service", "Pause");
            }
        } else {
            if (mPlayer != null) {
                updateProgressBar();
                mPlayer.start();
                //hay đổi image button
                ivPlay.get().setImageResource(R.drawable.ic_pause_white_48dp);
                Log.d("Player Service", "Play");
            }
        }
    }

    private void shuffleSong() {
        isShuffle = !isShuffle;
        if (isShuffle) {
            ivShuffle.get().setImageResource(R.drawable.ic_shuffle_pink_400_36dp);
        } else {
            ivShuffle.get().setImageResource(R.drawable.ic_shuffle_white_36dp);
        }
        Log.d(TAG, String.valueOf(isShuffle));
    }

    private void loopSong() {
        isLoop = !isLoop;
        if (isLoop) {
            ivLoop.get().setImageResource(R.drawable.ic_loop_pink_400_36dp);
        } else {
            ivLoop.get().setImageResource(R.drawable.ic_loop_white_36dp);
        }
    }


    private void previousSong() {
        Random rd = new Random();
        int posRandom = rd.nextInt(listSongs.get().size());
        if (mPosition == 0) {
            mPosition = listSongs.get().size() - 1;
        } else if (isShuffle) {
            mPosition = posRandom;
        } else {
            mPosition = mPosition - 1;
        }
        sendBroadPlaySong();
    }

    private void nextSong() {
        Random rd = new Random();
        int posRandom = rd.nextInt(listSongs.get().size());
        int temp = mPosition;
        if (isLoop && !isUserNext) {
            mPosition = temp;
        } else if (isShuffle) {
            mPosition = posRandom;
        } else if (mPosition == listSongs.get().size() - 1) {
            mPosition = 0;
        } else {
            mPosition += 1;
            isUserNext = false;
        }
        Log.d(TAG, "isShuffe" + isShuffle);
        Log.d(TAG, "isLoop" + isLoop);
        Log.d(TAG, "pos" + mPosition);
        sendBroadPlaySong();

    }

    private void sendBroadPlaySong() {
        Intent intent = new Intent();
        intent.putExtra("LINK", listSongs.get().get(mPosition).getSongPath());
        intent.setAction("imageSong");
        sendBroadcast(intent);

        mPlayer.release();
        Uri uriNext = Uri.parse(listSongs.get().get(mPosition).getSongPath());
        mPlayer = MediaPlayer.create(getBaseContext(), uriNext);
        play();
        mPlayer.setOnCompletionListener(this);
    }

    private class ImageBroadCast extends BroadcastReceiver implements MediaPlayer.OnCompletionListener {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("nnv", action);
            switch (action) {
                case "select":
                    mPosition = intent.getIntExtra("LINK", 0);
                    if (mPlayer.isPlaying()) {
                        mPlayer.release();
                    }
                    Uri uri = Uri.parse(listSongs.get().get(mPosition).getSongPath());
                    mPlayer = MediaPlayer.create(context, uri);
                    mPlayer.setOnCompletionListener(this);
                    play();

                    break;

            }


        }


        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            nextSong();
        }
    }

    private class SongBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case "songChange":
                    mPosition = intent.getIntExtra("POSITION", 0 );
                    sendBroadPlaySong();
                    break;
            }
        }
    }
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        nextSong();
        Log.d("nnv", "proxx");
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        unregisterReceiver(imageBroadCast);
        unregisterReceiver(songBroadCast);
        super.onDestroy();
    }


}
