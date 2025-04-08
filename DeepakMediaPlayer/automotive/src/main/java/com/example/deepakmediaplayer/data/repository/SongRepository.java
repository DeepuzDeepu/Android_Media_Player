package com.example.deepakmediaplayer.data.repository;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.deepakmediaplayer.data.database.DBHelper;
import com.example.deepakmediaplayer.data.model.Album;
import com.example.deepakmediaplayer.data.model.Artist;
import com.example.deepakmediaplayer.data.model.SongDataClass;
import com.example.deepakmediaplayer.service.MusicService;
import com.example.deepakmediaplayer.utils.CategoryEnum;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SongRepository {
    private static final String TAG = SongRepository.class.getSimpleName();

    private final Context context;
    @SuppressLint("StaticFieldLeak")
    private static SongRepository instance;
    MutableLiveData<SongDataClass> songMetaData=new MutableLiveData<>();
    private static MutableLiveData<Integer> currentPosition = new MutableLiveData<>();
    private static MutableLiveData<Integer> categoryPos=new MutableLiveData<>();
    public DBHelper db;
    private final MutableLiveData<Boolean> isPlayingLiveData=new MutableLiveData<>();
    public void setPlayingState(boolean isPlaying){
        isPlayingLiveData.postValue(isPlaying);
        Log.d(TAG, "setPlayingState: "+isPlayingLiveData.getValue());
    }
    public LiveData<Boolean>getPlayingState(){
        return isPlayingLiveData;
    }
    public LiveData<SongDataClass> getSongMetaData() {
        return songMetaData;
    }
    public static synchronized SongRepository getInstance(Context context){
        if(instance == null){
            instance = new SongRepository(context);
        }
        return instance;
    }
    public SongRepository(Context context){

        this.context=context;
        db = new DBHelper(context);
        isPlayingLiveData.setValue(false);

        initializeMediaBrowser();
        Log.d(TAG, "SongRepository: one");

    }
    public List<SongDataClass> allSongsList =new ArrayList<>();
    public List<SongDataClass> FavouriteSongslist=new ArrayList<>();
    public List<SongDataClass> filteredSongList=new ArrayList<>();
    public List<SongDataClass> getdata(){
        getSongs();

        return allSongsList;
    }
    private MediaBrowserCompat mediaBrowser;
    private MediaControllerCompat mediaController;

    public LiveData<List<SongDataClass>> getSongs(){
        Log.d(TAG, "getSongs: checkingggg");
        List<SongDataClass> songs=new ArrayList<>();
        Log.d(TAG, "SongRepository: two");
        String [] proj={MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media.ALBUM,MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.ALBUM_ID,MediaStore.Audio.Media.DURATION};
        Cursor audiocursor=context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,proj,null,null,null);
        Log.d(TAG, "getSongs: "+audiocursor.getCount());
        Log.d(TAG, "cursor "+ audiocursor.getCount());
        if(audiocursor!=null){
            while(audiocursor.moveToNext()){
                int id = audiocursor.getInt(audiocursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                String title=audiocursor.getString(audiocursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String artist=audiocursor.getString(audiocursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String album=audiocursor.getString(audiocursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                String url=audiocursor.getString(audiocursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                long duration=audiocursor.getLong(audiocursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                songs.add(new SongDataClass(id,title,artist,album,url,duration,false));
            }
            audiocursor.close();
        }
        MutableLiveData<List<SongDataClass>> liveData=new MutableLiveData<>();
        for(SongDataClass song : songs){
            for(SongDataClass favList : FavouriteSongslist){
                if(favList.getId()==song.getId()){
                    song.setLiked(true);
                }
            }
        }
        allSongsList =songs;
        liveData.setValue(songs);
        return liveData;
    }


    public List<Artist>getArtist(){
        List<Artist> artist=new ArrayList<>();
        Set<String>artistNames=new HashSet<>();
        String [] proj={MediaStore.Audio.Media.ARTIST_ID,MediaStore.Audio.Media.ARTIST};
        Cursor artistcursor=context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,proj,null,null);
        if(artistcursor!=null){
            while (artistcursor.moveToNext()){
                int id=artistcursor.getInt(artistcursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID));
                String artistname=artistcursor.getString(artistcursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                if(!artistNames.contains(artistname)){
                    artistNames.add(artistname);
                    Log.d(TAG, "getArtist: ");
                    artist.add(new Artist(id,artistname));
                }
            }
            artistcursor.close();
        }
        return artist;
    }

    public List<Album> getAlbum(){
        List<Album> album=new ArrayList<>();
        Set<String>albumNames=new HashSet<>();
        String [] proj={MediaStore.Audio.Media.ALBUM_ID,MediaStore.Audio.Media.ALBUM};
        Cursor albumcursor=context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,proj,null,null);
        if(albumcursor!=null){
            while (albumcursor.moveToNext()){
                int id=albumcursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
                String albumname=albumcursor.getString(albumcursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                if(!albumNames.contains(albumname)){
                    albumNames.add(albumname);
                    album.add(new Album(id,albumname));
                }
            }
            albumcursor.close();
        }
        return album;
    }

    public LiveData<List<SongDataClass>> getFavouriteSongs(){
        List<SongDataClass> favouriteSongs=new ArrayList<>();
        SQLiteDatabase db=this.db.getReadableDatabase();
        Cursor cursor= db.rawQuery("SELECT * FROM favouriteSongs",null);
        if(cursor!=null){
            Log.d(TAG, "getFavouriteSongs: "+cursor);
            while(cursor.moveToNext()){
                int id=cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String title=cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String artist=cursor.getString(cursor.getColumnIndexOrThrow("artist"));
                String album=cursor.getString(cursor.getColumnIndexOrThrow("album"));
                String url=cursor.getString(cursor.getColumnIndexOrThrow("url"));
                long duration=cursor.getLong(cursor.getColumnIndexOrThrow("duration"));
                boolean isLiked=cursor.getInt(cursor.getColumnIndexOrThrow("isLiked"))>0;
                favouriteSongs.add(new SongDataClass(id,title,artist,album,url,duration,isLiked));
                Log.d(TAG, "getFavouriteSongs: "+title);
            }
            cursor.close();
        }
        MutableLiveData<List<SongDataClass>> favouriteLiveData=new MutableLiveData<>();
        FavouriteSongslist=favouriteSongs;
        favouriteLiveData.setValue(FavouriteSongslist);
        Log.d(TAG, "getFavouriteSongs: "+favouriteSongs);
        return favouriteLiveData;
    }

    public List<SongDataClass> getFilterSongs(String query){
        List<SongDataClass>filterSongs=new ArrayList<>();
        Uri uri=MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String [] proj={MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media.ALBUM,MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.ALBUM_ID,MediaStore.Audio.Media.DURATION};
        String selection=MediaStore.Audio.Media.IS_MUSIC + "!= 0 AND " + MediaStore.Audio.Media.TITLE + " LIKE ?";
        String[] selectionArgs=new String[]{"%"+query+"%"};
        Cursor cursor=context.getContentResolver().query(uri,proj,selection,selectionArgs,null);
        if(cursor!=null){
            while (cursor.moveToNext()){
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                String title=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String artist=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String album=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                String url=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                long duration=cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                filterSongs.add(new SongDataClass(id,title,artist,album,url,duration,false));
            }
            cursor.close();
        }
        MutableLiveData<List<SongDataClass>> filterSongLiveData=new MutableLiveData<>();
        filteredSongList=filterSongs;
        filterSongLiveData.setValue(filteredSongList);
        Log.d(TAG, "getFilterSongs: "+filteredSongList);
        return filterSongs;
    }

    //Database connectivity for Favourite Songs
    public void addFavourite(int id,String name,String artist,String album,String url,long duration,boolean isLiked){
        Log.d(TAG, "addFavourite: ");
        db.insertFavouriteSongs(id,name,artist,album,url,duration,isLiked);
    }

    public void removeFavourite(int songId){
        db.deleteFavouriteSongs(songId);
    }

    //Service connection
    private void initializeMediaBrowser(){
        Log.d(TAG, "initializeMediaBrowser: media browswe");
        mediaBrowser = new MediaBrowserCompat(context, new ComponentName(context, MusicService.class), connectionCallback, null);
        mediaBrowser.connect();
    }
    public void mediaBrowserDisconnect(){
        if(mediaBrowser!=null && mediaBrowser.isConnected()){
            mediaBrowser.disconnect();
            Log.d(TAG, "mediaBrowserDisconnect: success");
        }
        mediaBrowser=null;
    }


    private final MediaBrowserCompat.ConnectionCallback connectionCallback=new MediaBrowserCompat.ConnectionCallback() {

        @Override
        public void onConnected() {
            Log.d(TAG, "onConnected: success");
            CheckmediaConnected();
            super.onConnected();
        }
        public void onConnectionSuspended() {
            mediaController = null;
        }
        public void onConnectionFailed() {
            mediaBrowser = null;
        }
    };





    private void CheckmediaConnected(){
        Log.d(TAG, "CheckmediaConnected: check0 ok");
        if(mediaBrowser!=null && mediaBrowser.isConnected()){
            Log.d(TAG, "CheckmediaConnected: browser"+mediaBrowser);
            mediaController=new MediaControllerCompat(context,mediaBrowser.getSessionToken());
            Log.d(TAG, "CheckmediaConnected: check1 ok");
            mediaController.registerCallback(new MediaControllerCompat.Callback() {
                public void onPlaybackStateChanged(PlaybackStateCompat playbackState){
                    int currentPos= (int) playbackState.getPosition();
                    currentPosition.setValue(currentPos);
                }
                public void onMetadataChanged(MediaMetadataCompat metadata){
                    SongDataClass newdata =convertedmetadata(metadata);
                    songMetaData.setValue(newdata);
                    Log.d(TAG, "onMetadataChanged: "+newdata);
                }
            });
        }
        else{
        }
    }

    private SongDataClass convertedmetadata(MediaMetadataCompat data){
        int id=Integer.parseInt(data.getDescription().getMediaId().toString());
        String name=data.getDescription().getTitle().toString();
        String artist=data.getDescription().getSubtitle().toString();
        String album=data.getDescription().getDescription().toString();
        String path=data.getDescription().getMediaUri().toString();
        long duration=data.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
        return new SongDataClass(id,name,artist,album,path,duration,false);
    }
    public void playSong(int song){
        Log.d(TAG, "playSong:hello "+mediaController);
        if(mediaController!=null ){
            Log.d(TAG, "playSong: playing");
            Bundle bundle=new Bundle();
            bundle.putString("CATEGORY", CategoryEnum.SONGS.name());
            mediaController.getTransportControls().playFromMediaId(String.valueOf(song),bundle);
            Log.d(TAG, "playSong: ");
        }
        else{
            Log.d(TAG, "playSong: MediaControlleris not initialized ");
        }
    }
    public void continuePlay(int song){
        if(mediaController!=null){
            mediaController.getTransportControls().play();
        }
    }

    public void pauseSong(int song){
        if(mediaController!=null){
            mediaController.getTransportControls().pause();
        }
    }
    public void nextSong(int song){
        if(mediaController!=null){
            mediaController.getTransportControls().skipToNext();
        }
    }
    public void previousSong(int song){
        if(mediaController!=null){
            mediaController.getTransportControls().skipToPrevious();
            Log.d(TAG, "previousSong: "+song);
        }
    }
    public void manualSeek(int pos){
        if(mediaController!=null){
            mediaController.getTransportControls().seekTo((long)pos);
        }
    }
    public void playFilter(int song){
        if(mediaController!=null ){
            Bundle bundle=new Bundle();
            bundle.putString("CATEGORY", CategoryEnum.SEARCH.name());
            mediaController.getTransportControls().playFromMediaId(String.valueOf(song),bundle);

        }
    }
    public void favPlay(int song){
        Bundle bundle=new Bundle();
        bundle.putString("CATEGORY", CategoryEnum.FAVOURITES.name());
        if(mediaController!=null){
            mediaController.getTransportControls().playFromMediaId(String.valueOf(song),bundle);
        }
    }
    public void playArtistSong(int songId) {
        Bundle bundle=new Bundle();
        bundle.putString("CATEGORY",CategoryEnum.ARTIST.name());
        if(mediaController!=null){
            mediaController.getTransportControls().playFromMediaId(String.valueOf(songId),bundle);
            Log.d(TAG, "playArtistSong: ");
        }
    }

    public LiveData<Integer>getCurrentPosition(){
        return currentPosition;
    }


    public void updateCurrentPosition(int position){
        currentPosition.setValue(position);
    }


    public void updateCategoryPos(int pos){
        categoryPos.setValue(pos);
        Log.d(TAG, "updateCategoryPos: "+categoryPos.getValue());
    }


    public LiveData<Integer>getCategoryPos(){
        Log.d(TAG, "getCategoryPos: "+categoryPos);
        return categoryPos;
    }



    public List<SongDataClass> artistAlbumSongs;
    public void updateFilteredSongs(List<SongDataClass> songs) {
        this.artistAlbumSongs=songs;
        Log.d(TAG, "updateFilteredSongs: "+songs);
    }


    public List<SongDataClass> getFilteredSongs() {
        return artistAlbumSongs;
    }
}
