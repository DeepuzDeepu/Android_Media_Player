package com.example.deepakmediaplayer.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deepakmediaplayer.data.model.SongDataClass;

import java.util.List;

public class AlbumSongAdapter extends RecyclerView.Adapter<AlbumSongAdapter.AlbumViewHolder> {

    private List<SongDataClass> songList;
    private String mParam;
    public AlbumSongAdapter(List<SongDataClass> songList, String mParam) {
        this.songList = songList;
        this.mParam = mParam;
    }
    public static class AlbumViewHolder extends RecyclerView.ViewHolder{
        TextView albumName,songName;
        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    @NonNull
    @Override
    public AlbumSongAdapter.AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }
    @Override
    public void onBindViewHolder(@NonNull AlbumSongAdapter.AlbumViewHolder holder, int position) {
    }
    @Override
    public int getItemCount() {
        return 0;
    }
}
