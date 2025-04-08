package com.example.deepakmediaplayer.data.model;

public class FavouriteSong {
    private int id;
    private String title;
    private String Artist;
    private String Album;
    private String url;
    private long duration;
    private boolean isLiked;




    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public FavouriteSong(int id, String title, String artist, String album, String url, long duration,boolean isLiked) {
        this.id = id;
        this.title = title;
        Artist = artist;
        Album = album;
        this.url=url;
        this.duration=duration;
        this.isLiked=isLiked;



    }

    public FavouriteSong() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return Artist;
    }

    public void setArtist(String artist) {
        Artist = artist;
    }

    public String getAlbum() {
        return Album;
    }

    public void setAlbum(String album) {
        Album = album;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
