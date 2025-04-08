package com.example.deepakmediaplayer.data.model;

public class Artist {
    private int id;
    private String ArtistName;

    public Artist(int id, String artistName) {
        this.id = id;
        ArtistName = artistName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArtistName() {
        return ArtistName;
    }

    public void setArtistName(String artistName) {
        ArtistName = artistName;
    }
}



