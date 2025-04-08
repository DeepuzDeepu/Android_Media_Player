package com.example.deepakmediaplayer.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.core.app.NotificationCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media3.common.util.UnstableApi;

import android.os.Handler;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.deepakmediaplayer.R;
import com.example.deepakmediaplayer.data.model.FavouriteSong;
import com.example.deepakmediaplayer.data.model.SongDataClass;
import com.example.deepakmediaplayer.data.repository.SongRepository;
import com.example.deepakmediaplayer.ui.viewmodel.SongViewModel;
import com.example.deepakmediaplayer.utils.CategoryEnum;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends MediaBrowserServiceCompat {

    public static final String ACTION_PLAY = "com.example.deepakmediaplayer.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.example.deepakmediaplayer.ACTION_PAUSE";
    public static final String ACTION_PREV = "com.example.deepakmediaplayer.ACTION_PREV";
    public static final String ACTION_NEXT = "com.example.deepakmediaplayer.ACTION_NEXT";
    public static final String ACTION_SEEK_FORWARD = "com.deepakmediaplayer.ACTION_SEEK_FORWARD";
    public static final String ACTION_SEEK_BACKWARD = "com.deepakmediaplayer.ACTION_SEEK_BACKWARD";
    private NotificationCompat.Action mPlayAction;
    private NotificationCompat.Action mPauseAction;
    private NotificationCompat.Action mNextAction;
    private NotificationCompat.Action mPrevAction;
//    private PlayerNotificationManager notificationManager;

    private NotificationManager notificationManager;
    private MediaSessionCompat mSession;
    private PlaybackStateCompat.Builder playbackStateBuilder;
    Context context;
    private List<SongDataClass> list;
    private List<FavouriteSong> favSongList;
    private MediaPlayer mediaPlayer;
    private int currentIndex = -1;
    private SongViewModel songViewModel;
    private final String TAG = MusicService.class.getSimpleName();
    private SongRepository songrepository;
    private MediaMetadataCompat metadata;
    private Runnable updateSeekBar;
    private int currentPosition = 0;
    private Handler handler = new Handler();
    private boolean isPlaying = false;
    private MediaMetadataRetriever retriever = new MediaMetadataRetriever();
    private TelephonyManager telephonyManager;
    private PhoneStateListener phoneStateListener;
    private boolean wasPlayingBeforeCall=false;
    private AudioManager audioManager;
    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener;


    @Override
    public void onCreate() {
        super.onCreate();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mSession = new MediaSessionCompat(this, "MyMusicService");
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mSession.setActive(true);


        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_STOP |
                        PlaybackStateCompat.ACTION_SEEK_TO);

        mSession.setPlaybackState(stateBuilder.build());
        mediaPlayer = new MediaPlayer();
        songrepository = SongRepository.getInstance(context);
        list = songrepository.getdata();
        int categoryPos = songrepository.getCategoryPos().getValue();
        setSessionToken(mSession.getSessionToken());
        mSession.setCallback(new MediaSessionCallback());
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createNotificationChannel();
        setupAudioFocusChangeListener();



    }


    private void setupAudioFocusChangeListener() {
        audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                Log.d(TAG, "onAudioFocusChange: " + focusChange);
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_LOSS:
                        pauseMusic();
                        Log.d(TAG, "Audio focus lost permanently.");
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        pauseMusic();
                        wasPlayingBeforeCall = true;
                        Log.d(TAG, "Audio focus lost transiently.");
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        if (mediaPlayer.isPlaying()) {
                            Log.d(TAG, "Audio focus lost transiently, ducking volume.");
                            mediaPlayer.setVolume(0.3f, 0.3f);
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_GAIN:
                        Log.d(TAG, "Audio focus gained.");
                        if (!mediaPlayer.isPlaying() && wasPlayingBeforeCall) {
                            Log.d(TAG, "Resuming playback.");
                            mediaPlayer.start();
                            mediaPlayer.setVolume(1.0f, 1.0f);
                            isPlaying = true;
                            wasPlayingBeforeCall = false;
                            songrepository.setPlayingState(true);
                        }
                        break;
                }
            }
        };
    }
    private void pauseMusic() {
        if (mediaPlayer.isPlaying()) {
            wasPlayingBeforeCall = true;
            mediaPlayer.pause();
            isPlaying = false;
            songrepository.setPlayingState(false);
//            abandonAudioFocus();
        }
    }



    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "MUSIC_CHANNEL",
                    "Music Playback",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Music Player Controls");
            channel.enableVibration(false);
            channel.setShowBadge(false);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.setSound(null,null);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_PLAY:
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                        showNotification(metadata.getDescription().getTitle().toString(),
                                metadata.getDescription().getSubtitle().toString(),
                                true,
                                metadata.getDescription().getMediaUri().toString());
                        isPlaying = true;
                        songrepository.setPlayingState(true);
                    }
                    break;
                case ACTION_PAUSE:
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        showNotification(metadata.getDescription().getTitle().toString(),
                                metadata.getDescription().getSubtitle().toString(),
                                false,
                                metadata.getDescription().getMediaUri().toString());
                        isPlaying = false;
                        songrepository.setPlayingState(false);
                    }
                    break;
                case ACTION_PREV:
                    if (currentIndex > 0) {
                        play(currentIndex - 1);
                        songrepository.setPlayingState(true);
                    }
                    break;
                case ACTION_NEXT:
                    if (currentIndex < list.size() - 1) {
                        play(currentIndex + 1);
                        songrepository.setPlayingState(true);
                    }
                    break;
            }
            songrepository.setPlayingState(isPlaying);
        }
        return START_STICKY;
    }

    @OptIn(markerClass = UnstableApi.class)
    private void showNotification(String title, String artist, boolean isPlaying, String uri) {
        Bitmap albumArtBitmap = null;
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        if (art != null) {
            albumArtBitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
        }

        // Create an intent to launch your app's main activity
        Intent openAppIntent = getPackageManager()
                .getLaunchIntentForPackage(getPackageName());
        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Media control intents
        Intent playIntent = new Intent(this, MusicService.class);
        Intent prevIntent = new Intent(this, MusicService.class);
        Intent nextIntent = new Intent(this, MusicService.class);
        playIntent.setAction(isPlaying ? ACTION_PAUSE : ACTION_PLAY);
        prevIntent.setAction(ACTION_PREV);
        nextIntent.setAction(ACTION_NEXT);

        PendingIntent playPendingIntent = PendingIntent.getService(this, 0,
                playIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        PendingIntent prevPendingIntent = PendingIntent.getService(this, 0,
                prevIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        PendingIntent nextPendingIntent = PendingIntent.getService(this, 0,
                nextIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Action prevAction = new NotificationCompat.Action(
                R.drawable.previousbutton, "Previous", prevPendingIntent);
        NotificationCompat.Action playPauseAction = new NotificationCompat.Action(
                isPlaying ? R.drawable.pausebutton : R.drawable.playbutton,
                isPlaying ? "Pause" : "Play",
                playPendingIntent);
        NotificationCompat.Action nextAction = new NotificationCompat.Action(
                R.drawable.nextbutton, "Next", nextPendingIntent);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "MUSIC_CHANNEL")
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.music_symbol)
                .setContentTitle(title)
                .setContentText(artist)
                .setLargeIcon(albumArtBitmap)
                .addAction(prevAction)
                .addAction(playPauseAction)
                .addAction(nextAction)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mSession.getSessionToken())
                        .setShowActionsInCompactView(0, 1, 2))
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true)  // Always set to true to make it persistent
                .setAutoCancel(false)  // Prevent auto-cancellation
                .setAutoCancel(false)
                .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE);


        Notification notification = notificationBuilder.build();

        if (isPlaying) {
            startForeground(1, notification);
        } else {
            stopForeground(false);
            notificationManager.notify(1, notification);
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        // Restart the service when the app is removed from the recent apps
        Intent restartServiceIntent = new Intent(getApplicationContext(), MusicService.class);
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }

            mediaPlayer.release();
            Log.d(TAG, "onDestroy: mediaPlayer release");
            mediaPlayer = null;
        }
        if (mSession != null) {
            mSession.setActive(false);
            mSession.release();
            Log.d(TAG, "onDestroy: msession release");
            mSession = null;
        }
        stopForeground(true);
        if (notificationManager != null) {
            notificationManager.cancelAll();
            Log.d(TAG, "onDestroy: noti cancell");
            notificationManager = null;
        }
        abandonAudioFocus();
        Log.d(TAG, "onDestroy: Media Service Stopped");
    }

    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, Bundle rootHints) {
        return new BrowserRoot("root", null);
    }

    @Override
    public void onLoadChildren(@NonNull final String parentMediaId, @NonNull final Result<List<MediaBrowserCompat.MediaItem>> result) {
        Log.d(TAG, "onLoadChildren: ");
        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();
        for (SongDataClass song : list) {
            mediaItems.add(createmediaitems(song));
        }
        result.sendResult(mediaItems);
    }

    private MediaBrowserCompat.MediaItem createmediaitems(SongDataClass song) {
        MediaDescriptionCompat descriptionCompat = new MediaDescriptionCompat.Builder()
                .setMediaId(String.valueOf(song.getId()))
                .setTitle(song.getTitle())
                .setSubtitle(song.getArtist())
                .setDescription(song.getAlbum())
                .setMediaUri(Uri.parse(song.getUrl()))
                .build();
        return new MediaBrowserCompat.MediaItem(descriptionCompat, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
    }

    private void startUpdateSeekBar() {
        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    currentPosition = mediaPlayer.getCurrentPosition();
                    songrepository.updateCurrentPosition(currentPosition);
                    mSession.setPlaybackState(new PlaybackStateCompat.Builder()
                            .setState(mediaPlayer.isPlaying() ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED,
                                    mediaPlayer.getCurrentPosition(), 1.0f)
                            .build());
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updateSeekBar);
    }

    private void play(int index) {
        if (list != null && index >= 0 && index < list.size()) {
            try {
                int result = audioManager.requestAudioFocus(audioFocusChangeListener,
                        AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN);

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    currentIndex = index;
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(list.get(index).getUrl());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    startUpdateSeekBar();

                    setmetadata(list.get(index));
                    mSession.setPlaybackState(new PlaybackStateCompat.Builder()
                            .setState(PlaybackStateCompat.STATE_PLAYING,
                                    mediaPlayer.getCurrentPosition(), 1.0f)
                            .build());

                    showNotification(list.get(index).getTitle(),
                            list.get(index).getArtist(),
                            true,
                            list.get(index).getUrl());
                    songrepository.setPlayingState(true);

                    // Set completion listener
                    mediaPlayer.setOnCompletionListener(mp -> {
                        if (currentIndex + 1 < list.size()) {
                            play(currentIndex + 1);
                        } else {
                            // Stop service if no more songs
                            stopForeground(true);
                            stopSelf();
                            abandonAudioFocus();
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
                stopForeground(true);
                stopSelf();
            }
        }
    }
    private void abandonAudioFocus() {
        if (audioManager != null) {
            audioManager.abandonAudioFocus(audioFocusChangeListener);
        }
    }

    private void setmetadata(SongDataClass song) {
        metadata = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, String.valueOf(song.getId()))
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.getTitle())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.getAlbum())
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, song.getUrl())
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.getDuration())
                .build();
        mSession.setMetadata(metadata);
    }

    @Override
    public void onSearch(@NonNull String query, Bundle extras, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        super.onSearch(query, extras, result);
    }

    private final class MediaSessionCallback extends MediaSessionCompat.Callback {

        @Override
        public void onPlay() {
            super.onPlay();
            if (currentIndex == -1) {
                play(0);
            } else {
                mediaPlayer.start();
                mSession.setPlaybackState(new PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_PLAYING,
                                mediaPlayer.getCurrentPosition(), 1.0f)
                        .build());
                isPlaying = true;
                songrepository.setPlayingState(true);

                // Show non-dismissible notification when playing
                showNotification(metadata.getDescription().getTitle().toString(),
                        metadata.getDescription().getSubtitle().toString(),
                        true,
                        metadata.getDescription().getMediaUri().toString());
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                mSession.setPlaybackState(new PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_PAUSED,
                                mediaPlayer.getCurrentPosition(), 1.0f)
                        .build());
                songrepository.setPlayingState(false);
                isPlaying = false;

                // Show a dismissible notification when paused
                showNotification(metadata.getDescription().getTitle().toString(),
                        metadata.getDescription().getSubtitle().toString(),
                        false,
                        metadata.getDescription().getMediaUri().toString());
            }
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            if (list != null && currentIndex < list.size() - 1) {
                play(currentIndex + 1);
                songrepository.setPlayingState(true);
            } else {
                play(0);
            }
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            if (list != null && currentIndex <= list.size() - 1) {
                Log.d(TAG, "onSkipToPrevious: " + currentIndex);
                play(currentIndex - 1);
                songrepository.setPlayingState(true);
            }
        }

        @Override
        public void onStop() {
            super.onStop();
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.reset();
                mSession.setPlaybackState(new PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_STOPPED, 0, 1.0f)
                        .build());
                songrepository.setPlayingState(false);
                isPlaying = false;
                stopForeground(true);
                stopSelf();
            }
        }



        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
            if (mediaPlayer != null) {
                mediaPlayer.seekTo((int) pos);
                currentPosition = (int) pos;
            }
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle bundle) {
            super.onPlayFromMediaId(mediaId, bundle);
            Log.d(TAG, "onPlayFromMediaId: hha");
            String CategoryString = bundle.getString("CATEGORY");
            CategoryEnum category = CategoryEnum.valueOf(CategoryString);
            Log.d(TAG, "onPlayFromMediaId: " + category);
            switch (category) {
                case FAVOURITES:
                    list = songrepository.FavouriteSongslist;
                    break;
                case SONGS:
                    list = songrepository.allSongsList;
                    break;
                case SEARCH:
                    list = songrepository.filteredSongList;
                    Log.d(TAG, "onPlayFromMediaId: " + list);
                    break;
                case ARTIST:
                    list = songrepository.artistAlbumSongs;
                    Log.d(TAG, "onPlayFromMediaId: " + songrepository.artistAlbumSongs);
                    break;
            }
            for (int i = 0; i < list.size(); i++) {
                if (String.valueOf(list.get(i).getId()).equals(mediaId)) {
                    mediaPlayer.reset();
                    play(i);
                    songrepository.setPlayingState(true);
                    break;
                }
            }
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {
            super.onCustomAction(action, extras);
        }

    }
}