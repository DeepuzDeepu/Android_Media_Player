package com.example.deepakmediaplayer.data.model;

public class Album {
    private int id;
    private String albumName;

    public Album(int id, String albumName) {
        this.id = id;
        this.albumName = albumName;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }
}
