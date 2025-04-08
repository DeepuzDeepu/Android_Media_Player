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

    public class ArtistSongAdapter extends RecyclerView.Adapter<ArtistSongAdapter.ArtistViewHolder> {
        private List<SongDataClass> originalSongList;
        private List<SongDataClass> filteredSongList;
        private static final String TAG = "artistSongAdapter";
        private String mParam1;
        private String mParam2;
        private OnItemClickListener listener;

        public ArtistSongAdapter(OnItemClickListener listener, List<SongDataClass> songList, String mParam1, String mParam2) {
            this.listener = listener;
            this.originalSongList = songList;
            this.filteredSongList = new ArrayList<>(songList);
            this.mParam1 = mParam1;
            this.mParam2 = mParam2;
            filterSongs();
        }
        public List<SongDataClass> getFilteredSongList() {
            return filteredSongList;
        }

        public interface OnItemClickListener {
            void onItemClick(int songId);
        }

        public void filterSongs() {
            filteredSongList.clear();
            for (SongDataClass song : originalSongList) {
                String artistNames = song.getArtist();
                String albumNames = song.getAlbum();
                if ((mParam1 != null && mParam1.equals(artistNames)) ||
                        (mParam2 != null && mParam2.equals(albumNames))) {
                    filteredSongList.add(song);
                }
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ArtistSongAdapter.ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_all_songs_layout, parent, false);
            return new ArtistViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ArtistSongAdapter.ArtistViewHolder holder, int position) {
            SongDataClass song = filteredSongList.get(position);
            holder.favSongs.setVisibility(View.GONE);
            holder.songName.setText(song.getTitle());
            Log.d(TAG, "onBindViewHolder: "+song.getTitle());
            holder.itemView.setOnClickListener(view -> {
                Log.d(TAG, "onBindViewHolder: entered");
                listener.onItemClick(song.getId());
            });
        }

        @Override
        public int getItemCount() {
            return filteredSongList.size();
        }
        public void updateSongs(List<SongDataClass> songs) {
            this.originalSongList = songs;
            filterSongs();
        }

        public static class ArtistViewHolder extends RecyclerView.ViewHolder {
            TextView artistName, songName;
            Button favSongs;

            public ArtistViewHolder(@NonNull View itemView) {
                super(itemView);
                artistName = itemView.findViewById(R.id.artistName);
                songName = itemView.findViewById(R.id.item_title);
                favSongs = itemView.findViewById(R.id.favouriteSongs);
            }
        }
    }