package com.example.deepakmediaplayer.ui.view.Fragments;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.deepakmediaplayer.R;
import com.example.deepakmediaplayer.data.model.SongDataClass;
import com.example.deepakmediaplayer.service.MusicService;
import com.example.deepakmediaplayer.ui.viewmodel.SongViewModel;

import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlaybackFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaybackFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    Button play,previous,next;
    TextView songTextView,artistName,currentDuration,totalDuration;
    ImageView imageView;
    SeekBar seekBar;
    MusicService service;
    int songId=0;
    SongViewModel songViewModel;
    private static final String TAG = "Media_Playback_Fragment";
    private MediaMetadataRetriever retriever=new MediaMetadataRetriever();
    private Runnable updateSeekBar;
    private int currentSeekbarPosition=0;
    private Handler handler=new Handler();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PlaybackFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Media_Playback_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlaybackFragment newInstance(String param1, String param2) {
        PlaybackFragment fragment = new PlaybackFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        songViewModel=new ViewModelProvider(requireActivity()).get(SongViewModel.class);

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_media__playback_, container, false);
        play= view.findViewById(R.id.playButton);
        previous=view.findViewById(R.id.toPreviousButton);
        next=view.findViewById(R.id.toNextButton);
        imageView=view.findViewById(R.id.songImageView);
        seekBar=view.findViewById(R.id.seekbar);
        songTextView=view.findViewById(R.id.songName);
        songTextView.setSelected(true);
        artistName=view.findViewById(R.id.artistNameDisplay);
        currentDuration=view.findViewById(R.id.currentDuration);
        totalDuration=view.findViewById(R.id.totalDuration);

        songViewModel.songMetaData.observe(getViewLifecycleOwner(), new Observer<SongDataClass>()
        {
            @Override
            public void onChanged(SongDataClass songDataClass) {

                songId=songDataClass.getId();
                songTextView.setText(songDataClass.getTitle());
                artistName.setText(songDataClass.getArtist());
                totalDuration.setText(formatDuration(songDataClass.getDuration()));
                play.setBackgroundResource(R.drawable.pausebutton);
                final boolean[] isPlayingState = {true};
                songViewModel.getPlayingState().observe(getViewLifecycleOwner(),isPlaying->{
                    Log.d(TAG, "onChanged: play state"+isPlaying);
                    isPlayingState[0]=isPlaying;
                    if(isPlayingState[0]==true){
                        play.setBackgroundResource(R.drawable.pausebutton);
                    } else if (isPlayingState[0]==false) {
                        play.setBackgroundResource(R.drawable.playbutton);
                    }

                });

                play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(isPlayingState[0] == true){
                            songViewModel.pauseSong(songId);
                            isPlayingState[0] =false;
                            play.setBackgroundResource(R.drawable.playbutton);
                        }
                        else {
                            songViewModel.continuePlay(songId);
                            isPlayingState[0]=true;
                            play.setBackgroundResource(R.drawable.pausebutton);

                        }
                    }
                });

                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        songViewModel.nextSong(songId);
                    }
                });
                previous.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        songViewModel.previousSong(songId);

                    }
                });

                songViewModel.getCurrentPosition().observe(getViewLifecycleOwner(),position->{
                    if(seekBar!=null){
                        seekBar.setMax((int) songDataClass.getDuration());
                    }
                    if(position!=null){
                        seekBar.setProgress(position);
                        currentDuration.setText(formatDuration(position));

                    }
                });

                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromuser) {
                        if(fromuser){
                            songViewModel.seekTo(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

                retriever.setDataSource(songDataClass.getUrl());
                byte[] art=retriever.getEmbeddedPicture();
                if(art!=null){
                    Log.d(TAG, "onChanged: picture "+art);
                    imageView.setImageBitmap(BitmapFactory.decodeByteArray(art,0, art.length));
                }
                else{
                    int[] images={R.drawable.songimg,R.drawable.song2,R.drawable.song3,R.drawable.song4,R.drawable.song5};
                    Random random=new Random();
                    int randomIndex= random.nextInt(images.length);

                    imageView.setImageResource(images[randomIndex]);
                }
            }
            private String formatDuration(long duration) {
                int minutes = (int) ((duration / 1000) / 60);
                int seconds = (int) ((duration / 1000) % 60);
                return String.format("%02d:%02d", minutes, seconds);
            }
        });

        return view;
    }

}