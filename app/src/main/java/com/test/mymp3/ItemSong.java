package com.test.mymp3;

import android.graphics.Bitmap;
import android.media.Image;

/**
 * Created by n on 27/08/2017.
 */

public class ItemSong {
    private String songName;

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    private String songArtist;
    private int songDuration;
    private Bitmap imgSong;
    private String songPath;

    public String getSongName() {
        return songName;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public ItemSong() {
    }

    public Bitmap getImgSong() {
        return imgSong;
    }

    public ItemSong(String songPath, String songName, String songArtist, int songDuration) {
        this.songPath = songPath;
        this.songName = songName;
        this.songArtist = songArtist;
        this.songDuration = songDuration;
    }

    public String getSongPath() {
        return songPath;
    }

    public void setImgSong(Bitmap imgSong) {
        this.imgSong = imgSong;
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }

    public int getSongDuration() {
        return songDuration;
    }
}
