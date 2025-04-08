package com.example.deepakmediaplayer.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.deepakmediaplayer.data.model.Album;
import com.example.deepakmediaplayer.data.model.Artist;
import com.example.deepakmediaplayer.data.model.SongDataClass;
import com.example.deepakmediaplayer.data.repository.SongRepository;
import com.example.deepakmediaplayer.ui.adapter.ItemAdapter;

import java.util.List;
import java.util.stream.Collectors;

public class SongViewModel extends ViewModel {
    private static final String TAG = SongViewModel.class.getSimpleName();
    private SongRepository repository;
    private MutableLiveData<List<ItemAdapter.Item>> _itemList = new MutableLiveData<>();
    public LiveData<List<ItemAdapter.Item>> getItemList() {
        return _itemList;
    }
    private LiveData<List<SongDataClass>> songs;
    private LiveData<List<SongDataClass>> allSongs;
    private MutableLiveData<List<SongDataClass>> filterSongsLiveData;
    private final MutableLiveData<List<SongDataClass>> artistAlbumSongsLiveData = new MutableLiveData<>();
    private LiveData<Integer> currentPosition;
    private final LiveData<List<SongDataClass>> favouriteSongs;
    public LiveData<SongDataClass> songMetaData;
    private final LiveData<Boolean> isPlayingLiveData;
    private LiveData<Long> songDuration;


    public void setPosition(int pos) {
        position.setValue(pos);
        fetchList(pos);
    }

    private final MutableLiveData<Integer> position = new MutableLiveData<>();

    public LiveData<List<SongDataClass>> getSongs() {
        return songs;
    }

    public void fetchList(int position) {
        repository.updateCategoryPos(position);
        switch (position) {
            case 0:
                Log.d(TAG, "fetchList: " + position);
                List<SongDataClass> songs = repository.getSongs().getValue();
                assert songs != null;
                _itemList.setValue(songs.stream()
                        .map(ItemAdapter.Item.SongItem::new)
                        .collect(Collectors.toList()));

                break;
            case 1:
                List<Artist> artists = repository.getArtist();
                assert artists != null;
                _itemList.setValue(artists.stream()
                        .map(ItemAdapter.Item.ArtistItem::new)
                        .collect(Collectors.toList()));
                break;
            case 2:
                List<Album> albums = repository.getAlbum();
                Log.d(TAG, "fetchList: " + albums);
                assert albums != null;
                _itemList.setValue(albums.stream()
                        .map(ItemAdapter.Item.AlbumItem::new)
                        .collect(Collectors.toList()));
                break;
            case 3:
                repository.getFavouriteSongs().observeForever(FavouriteSongs -> {
                    Log.d(TAG, "fetchList: observe");
                    if (FavouriteSongs != null) {
                        _itemList.setValue(FavouriteSongs.stream()
                                .map(ItemAdapter.Item.LikedSongsItem::new)
                                .collect(Collectors.toList()));
                    }
                });
                Log.d(TAG, "fetchList: updated");
                break;
            case 4:
                break;
            default:
                Log.d(TAG, "Updateitems: Invalid Option");
                break;
        }
    }

    public LiveData<List<SongDataClass>> getSongsLiveData() {
        return filterSongsLiveData;
    }

    public void setFilteredSongs(List<SongDataClass> filteredSongs) {
        artistAlbumSongsLiveData.setValue(filteredSongs);
        repository.updateFilteredSongs(filteredSongs);
    }

    public void searchSongs(String query) {
        List<SongDataClass> songs = repository.getFilterSongs(query);
        filterSongsLiveData.setValue(songs);
    }

    public SongViewModel(SongRepository repository) {
        this.repository = repository;
        songMetaData = repository.getSongMetaData();
        this.songs = repository.getSongs();
        filterSongsLiveData = new MutableLiveData<>();
        isPlayingLiveData = repository.getPlayingState();
        currentPosition = repository.getCurrentPosition();
        favouriteSongs = repository.getFavouriteSongs();
//        songDuration= Transformations.map(songMetaData,metadata->{
//            if(metadata!=null){
//                return metadata.getDuration();
//            }
//            else{
//                return null;
//            }
//        });
    }



    public LiveData<List<SongDataClass>> getFavouriteSongs() {
        return favouriteSongs;
    }

    public void addFavorite(int id, String name, String artist, String album, String url, long duration, boolean isLiked) {
        repository.addFavourite(id, name, artist, album, url, duration, isLiked);
    }

    public void removeFavourite(int songId) {
        repository.removeFavourite(songId);
    }

    public void playSong(int songId) {
        repository.playSong(songId);
    }

    public void playFilter(int songId) {
        repository.playFilter(songId);
    }

    public void playFavSong(int songId) {
        repository.favPlay(songId);
    }

    public void playArtistSong(int songId) {
        repository.playArtistSong(songId);
    }

    public void pauseSong(int songId) {
        repository.pauseSong(songId);
    }

    public void nextSong(int songId) {
        repository.nextSong(songId);
    }

    public void previousSong(int songId) {
        repository.previousSong(songId);
    }

    public void continuePlay(int songId) {
        repository.continuePlay(songId);
    }

    public LiveData<Boolean> getPlayingState() {
        return isPlayingLiveData;
    }

    public LiveData<Integer> getCurrentPosition() {
        return currentPosition;
    }

    public void seekTo(int position) {
        repository.manualSeek(position);
    }

    public LiveData<Long>getSongDuration(){
        return songDuration;
    }

}