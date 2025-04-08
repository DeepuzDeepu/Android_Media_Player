package com.example.deepakmediaplayer.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deepakmediaplayer.R;
import com.example.deepakmediaplayer.data.model.SongDataClass;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private List<SongDataClass> songs = new ArrayList<>();
    private static final String TAG = "searchAdapter";
    private  OnItemClickListener listener;

    public SearchAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int songId);
    }

    @NonNull
    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.media_all_songs_layout,parent,false);

        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.SearchViewHolder holder, int position) {

        SongDataClass song= songs.get(position);
        Log.d(TAG, "onBindViewHolder: "+song.getTitle());
        holder.favButton.setVisibility(View.GONE);
        holder.item_title.setText(song.getTitle());
        holder.itemView.setOnClickListener(view -> {
            listener.onItemClick(song.getId());
        });

    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public void setSongs(List<SongDataClass> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView item_title;
        Button favButton;

        SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            item_title = itemView.findViewById(R.id.item_title);
            favButton=itemView.findViewById(R.id.favouriteSongs);
        }
    }


}
