package com.example.deepakmediaplayer.ui.adapter;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deepakmediaplayer.R;
import com.example.deepakmediaplayer.data.model.Album;
import com.example.deepakmediaplayer.data.model.Artist;
import com.example.deepakmediaplayer.data.model.SongDataClass;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.SongViewHolder> {
    private static final String TAG = ItemAdapter.class.getSimpleName();
    private List<Item> itemList = new ArrayList<>();
    private int nowPlayingSongId = -1;
    private int previousPlayingSongId = -1;
    private OnItemClickListener listener;
    private List<SongDataClass> favouriteSongs;

    public void setOnClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setSongs(List<Item> songs) {
        this.itemList = songs != null ? songs : new ArrayList<>();
        Log.d(TAG, "setSongs: itemList size = " + itemList.size());
        for (Item item : itemList) {
            if (item instanceof Item.LikedSongsItem) {
                Log.d(TAG, "Found a liked song: " + ((Item.LikedSongsItem) item).getSong().getTitle());
            }
        }
        notifyDataSetChanged();
        if (listener != null) {
            listener.itemName("Favourite Songs", String.valueOf(getItemCount()));
        } else {
            Log.e(TAG, "Listener is null when setting songs");
        }
    }

    public void setSelectedSongs(SongDataClass song) {
        if (song == null) return;
        previousPlayingSongId = nowPlayingSongId;
        nowPlayingSongId = song.getId();

        notifyItemChanged(getItemPositionById(previousPlayingSongId));
        notifyItemChanged(getItemPositionById(nowPlayingSongId));

        Log.d(TAG, "setSelectedSongs: Now playing song ID = " + nowPlayingSongId);
    }

    private int getItemPositionById(int songId) {
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i) instanceof Item.SongItem) {
                SongDataClass song = ((Item.SongItem) itemList.get(i)).getSong();
                if (song.getId() == songId) return i;
            }
        }
        return RecyclerView.NO_POSITION;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_all_songs_layout, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.favSongs.setVisibility(View.GONE);



        if (item instanceof Item.SongItem) {
            holder.favSongs.setVisibility(View.VISIBLE);
            listener.itemName("Songs", String.valueOf(getItemCount()));
            Log.d(TAG, "onBindViewHolder: ");
            SongDataClass song = ((Item.SongItem) item).getSong();
            holder.title.setText(song.getTitle());
            Log.d(TAG, "onBin    "+nowPlayingSongId);

            holder.title.setTextColor(song.getId() == nowPlayingSongId ? Color.RED : Color.BLACK);


            holder.favSongs.setBackgroundResource(song.isLiked() ? R.drawable.favourite : R.drawable.favourite_white_outline);

            holder.itemView.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onItemClick(song.getId(), song.getTitle(), song.getArtist(), song.getAlbum(), song.getUrl(), song.getDuration());
                }
                setSelectedSongs(song);
            });

            holder.favSongs.setOnClickListener(view -> handleFavoriteClick(holder, song));

        } else if (item instanceof Item.ArtistItem) {
            listener.itemName("Artists",String.valueOf(getItemCount()));
            holder.title.setTextColor(Color.BLACK);
            Artist artist = ((Item.ArtistItem) item).getArtist();
            holder.title.setText(artist.getArtistName());
            holder.itemView.setOnClickListener(view -> {
                if (listener != null) listener.onArtistClick(artist.getArtistName());
            });

        } else if (item instanceof Item.AlbumItem) {
            listener.itemName("Album",String.valueOf(getItemCount()));
            Album album = ((Item.AlbumItem) item).getAlbum();
            holder.title.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.Orange));
            holder.title.setText(album.getAlbumName());
            holder.itemView.setOnClickListener(view -> {
                if (listener != null) listener.onAlbumClick(album.getAlbumName());
            });

        } else if (item instanceof Item.LikedSongsItem) {


            holder.favSongs.setVisibility(View.VISIBLE);
            SongDataClass song = ((Item.LikedSongsItem) item).getSong();

            holder.title.setTextColor(song.getId() == nowPlayingSongId ? Color.RED : Color.BLACK);
            holder.title.setText(song.getTitle());
            holder.favSongs.setBackgroundResource(R.drawable.favourite);

//            listener.itemName("Favourite Songs",String.valueOf(getItemCount()));
            holder.itemView.setOnClickListener(view -> {
                if (listener != null) listener.favPlaySong(song.getId());
            });
            holder.favSongs.setOnClickListener(view -> {
                if (listener != null) {
                    notifyItemRemoved(holder.getAdapterPosition());
                    listener.removefromFavouriteList(song.getId());
                }
            });
            if (itemList.isEmpty()) {
                listener.itemName("Favourite Songs", "0");
            } else {
                listener.itemName("Favourite Songs", String.valueOf(getItemCount()));
            }
        }
    }

    private void handleFavoriteClick(SongViewHolder holder, SongDataClass song) {
        if (listener == null) return;
        boolean isLiked = song.isLiked();
        song.setLiked(!isLiked);

        if (!isLiked) {
            holder.favSongs.setBackgroundResource(R.drawable.fav_animation);
            AnimationDrawable animationDrawable = (AnimationDrawable) holder.favSongs.getBackground();
            animationDrawable.start();

            listener.setFavSongId(song.getId(), song.getTitle(), song.getArtist(), song.getAlbum(), song.getUrl(), song.getDuration(), true);

            new Handler().postDelayed(() -> {
                animationDrawable.stop();
                holder.favSongs.setBackgroundResource(R.drawable.favourite);
            }, 500);
        } else {
            holder.favSongs.setBackgroundResource(R.drawable.favourite_white_outline);
            listener.removeFavourites(song.getId());
        }
        notifyItemChanged(holder.getAdapterPosition());
    }

    public void setFavouriteSongs(List<SongDataClass> favouriteSongs) {
        this.favouriteSongs = favouriteSongs != null ? favouriteSongs : new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: "+itemList.size());
        return itemList.size();

    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public Button favSongs;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_title);
            favSongs = itemView.findViewById(R.id.favouriteSongs);
        }
    }

    public interface OnItemClickListener {
        void setFavSongId(int id, String name, String artist, String album, String url, long duration, boolean isLiked);

        void removeFavourites(int songId);

        void onItemClick(int songId, String name, String artist, String album, String url, long duration);

        void removefromFavouriteList(int songId);

        void favPlaySong(int songId);

        void onArtistClick(String name);

        void onAlbumClick(String name);

        void itemName(String name, String count);
    }

    public static abstract class Item {
        public static class ArtistItem extends Item {
            private final Artist artist;

            public ArtistItem(Artist artist) {
                this.artist = artist;
            }

            public Artist getArtist() {
                return artist;
            }
        }

        public static class AlbumItem extends Item {
            private final Album album;

            public AlbumItem(Album album) {
                this.album = album;
            }

            public Album getAlbum() {
                return album;
            }
        }

        public static class SongItem extends Item {
            private final SongDataClass song;

            public SongItem(SongDataClass song) {
                this.song = song;
            }

            public SongDataClass getSong() {
                return song;
            }
        }

        public static class LikedSongsItem extends Item {
            private final SongDataClass song;

            public LikedSongsItem(SongDataClass song) {
                this.song = song;
            }

            public SongDataClass getSong() {
                return song;
            }
        }
    }
}