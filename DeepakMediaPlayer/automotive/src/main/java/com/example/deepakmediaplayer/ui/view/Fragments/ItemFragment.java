package com.example.deepakmediaplayer.ui.view.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.example.deepakmediaplayer.R;
import com.example.deepakmediaplayer.ui.adapter.ItemAdapter;
import com.example.deepakmediaplayer.ui.viewmodel.SongViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemFragment extends Fragment implements ItemAdapter.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG= ItemFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SongViewModel viewModel;
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private Toolbar toolbar;

    public ItemFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Media_Items_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ItemFragment newInstance(String param1, String param2) {
        ItemFragment fragment = new ItemFragment();
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

        adapter = new ItemAdapter();
        viewModel = new ViewModelProvider(requireActivity()).get(SongViewModel.class);


    }
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_media__items_, container, false);
        toolbar=view.findViewById(R.id.toolbar);
        recyclerView = view.findViewById(R.id.mediaitems_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        Log.d(TAG, "onCreateView: " + viewModel);
        viewModel.getItemList().observe(getViewLifecycleOwner(), songDataClasses -> {
            adapter.setSongs(songDataClasses);
            recyclerView.scrollToPosition(0);
            Log.d(TAG, "onCreateView: song data" + songDataClasses);
            adapter.setOnClickListener(this);
        });

        viewModel.getFavouriteSongs().observe(getViewLifecycleOwner(), favouriteSongs -> {

            adapter.setFavouriteSongs(favouriteSongs);
            Log.d(TAG, "onCreateView: favourite songs data" + favouriteSongs);
        });

        viewModel.songMetaData.observe(getViewLifecycleOwner(),songDataClass -> {
            adapter.setSelectedSongs(songDataClass);
        });

        return view;
    }

    @Override
    public void setFavSongId(int id, String name, String artist, String album, String url, long duration,boolean isLiked) {
        viewModel.addFavorite(id,name,artist,album,url,duration,isLiked);
    }

    @Override
    public void removeFavourites(int songId) {
        viewModel.removeFavourite(songId);
    }

    @Override
    public void onItemClick(int songId, String name, String artist, String album, String url, long duration) {
        viewModel.playSong(songId);
    }

    @Override
    public void removefromFavouriteList(int songId) {
        viewModel.removeFavourite(songId);
        viewModel.fetchList(3);
    }

    @Override
    public void favPlaySong(int songId) {
        viewModel.playFavSong(songId);
    }

    @Override
    public void onArtistClick(String artistName) {
        openNewFrag(artistName,null);
    }

    @Override
    public void onAlbumClick(String albumName) {
        openNewFrag(null,albumName);
    }

    @Override
    public void itemName(String name, String count) {
        toolbar.setTitle(name);

        toolbar.setSubtitle(count+" "+ name);

        Log.d(TAG, "itemName: "+name);
        Log.d(TAG, "itemName: "+count);

    }

    private void openNewFrag(String artist,String album) {
        Fragment artistFrag= DetailedSongFragment.newInstance(artist,album);
        FragmentTransaction transaction= getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.mediaItem,artistFrag);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}