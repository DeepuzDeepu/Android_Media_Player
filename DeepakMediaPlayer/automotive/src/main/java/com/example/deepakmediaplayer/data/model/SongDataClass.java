package com.example.deepakmediaplayer.data.model;

public class SongDataClass {
    private int id;
    private String title;
    private String Artist;
    private String Album;
    private String url;
    private long duration;
    private boolean isLiked;

    public SongDataClass(String name, boolean isDirectory, String s) {
        this.title=name;
    }


    public int getDuration() {
        return (int) duration;
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

    public SongDataClass(int id, String title, String artist, String album, String url, long duration,boolean isLiked) {
        this.id = id;
        this.title = title;
        Artist = artist;
        Album = album;
        this.url=url;
        this.duration=duration;
        this.isLiked=isLiked;



    }

    public SongDataClass() {

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
