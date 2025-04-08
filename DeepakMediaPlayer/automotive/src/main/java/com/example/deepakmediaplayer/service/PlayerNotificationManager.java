//// Save this file as: automotive/src/main/java/com/example/deepakmediaplayer/service/PlayerNotificationManager.java
//
//package com.example.deepakmediaplayer.service;
//
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.media.MediaMetadataRetriever;
//import android.os.Build;
//import android.support.v4.media.session.MediaSessionCompat;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.RequiresApi;
//import androidx.core.app.NotificationCompat;
//
//import com.example.deepakmediaplayer.R;
//
//public class PlayerNotificationManager {
//    private static final String CHANNEL_ID = "MUSIC_CHANNEL";
//    private final NotificationManager notificationManager;
//    private final MusicService musicService;
//    private NotificationCompat.Builder notificationBuilder;
//    private final androidx.media.app.NotificationCompat.MediaStyle notificationStyle;
//    private MediaMetadataRetriever retriever;
//
//    PlayerNotificationManager(@NonNull final MusicService musicService) {
//        this.musicService = musicService;
//        this.retriever = new MediaMetadataRetriever();
//        notificationStyle = new androidx.media.app.NotificationCompat.MediaStyle();
//        notificationManager = (NotificationManager) musicService.getSystemService(Context.NOTIFICATION_SERVICE);
//    }
//
//    public NotificationManager getNotificationManager() {
//        return notificationManager;
//    }
//
//    private PendingIntent createActionIntent(@NonNull final String action) {
//        Intent intent = new Intent(musicService, MusicService.class);
//        intent.setAction(action);
//        return PendingIntent.getService(musicService, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//    }
//
//    public Notification createNotification(String title, String artist, boolean isPlaying, String uri, MediaSessionCompat mediaSession) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            createNotificationChannel();
//        }
//
//        // Get album art
//        Bitmap albumArt = null;
//        try {
//            retriever.setDataSource(uri);
//            byte[] art = retriever.getEmbeddedPicture();
//            if (art != null) {
//                albumArt = BitmapFactory.decodeByteArray(art, 0, art.length);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        // Create actions
//        NotificationCompat.Action prevAction = new NotificationCompat.Action(
//                R.drawable.previousbutton,
//                "Previous",
//                createActionIntent(MusicService.ACTION_PREV)
//        );
//
//        NotificationCompat.Action playPauseAction = new NotificationCompat.Action(
//                isPlaying ? R.drawable.pausebutton : R.drawable.playbutton,
//                isPlaying ? "Pause" : "Play",
//                createActionIntent(isPlaying ? MusicService.ACTION_PAUSE : MusicService.ACTION_PLAY)
//        );
//
//        NotificationCompat.Action nextAction = new NotificationCompat.Action(
//                R.drawable.nextbutton,
//                "Next",
//                createActionIntent(MusicService.ACTION_NEXT)
//        );
//
//        // Build notification
//        notificationBuilder = new NotificationCompat.Builder(musicService, CHANNEL_ID)
//                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                .setSmallIcon(R.drawable.music_symbol)
//                .setContentTitle(title)
//                .setContentText(artist)
//                .setLargeIcon(albumArt)
//                .addAction(prevAction)
//                .addAction(playPauseAction)
//                .addAction(nextAction)
//                .setStyle(notificationStyle
//                        .setMediaSession(mediaSession.getSessionToken())
//                        .setShowActionsInCompactView(0, 1, 2)
//                        .setShowCancelButton(true))
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setOngoing(true);
//
//        return notificationBuilder.build();
//    }
//
//    public void updateNotification(String title, String artist, boolean isPlaying, String uri, MediaSessionCompat mediaSession) {
//        Notification notification = createNotification(title, artist, isPlaying, uri, mediaSession);
//        notificationManager.notify(1, notification);
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private void createNotificationChannel() {
//        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
//            NotificationChannel channel = new NotificationChannel(
//                    CHANNEL_ID,
//                    "Music Playback",
//                    NotificationManager.IMPORTANCE_LOW
//            );
//            channel.setDescription("Music Player Controls");
//            channel.enableLights(false);
//            channel.enableVibration(false);
//            channel.setShowBadge(true);
//            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
//
//            notificationManager.createNotificationChannel(channel);
//        }
//    }
//}