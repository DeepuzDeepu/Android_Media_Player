package com.example.deepakmediaplayer.ui.view.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.deepakmediaplayer.R;
import com.example.deepakmediaplayer.data.model.SongDataClass;
import com.example.deepakmediaplayer.ui.adapter.ArtistSongAdapter;
import com.example.deepakmediaplayer.ui.viewmodel.SongViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailedSongFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailedSongFragment extends Fragment implements ArtistSongAdapter.OnItemClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "artistFrag";
    private SongViewModel viewModel;
    private ArtistSongAdapter adapter;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextView name;

    public DetailedSongFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment artistFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailedSongFragment newInstance(String param1, String param2) {
        DetailedSongFragment fragment = new DetailedSongFragment();
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
        viewModel = new ViewModelProvider(requireActivity()).get(SongViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_artist, container, false);
        Button backButton=view.findViewById(R.id.backButton);
        name=view.findViewById(R.id.artistName);
        RecyclerView recyclerView=view.findViewById(R.id.artistList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new ArtistSongAdapter(this,new ArrayList<>(),mParam1,mParam2);
        recyclerView.setAdapter(adapter);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });
       if(mParam1!=null){
           name.setText(mParam1);
       }
       else if (mParam2!=null){
           name.setText(mParam2);
       }
        viewModel.getSongs().observe(requireActivity(), new Observer<List<SongDataClass>>() {
            @Override
            public void onChanged(List<SongDataClass> songDataClasses) {
                adapter.updateSongs(songDataClasses);
                List<SongDataClass> filteredSongs = adapter.getFilteredSongList();
                viewModel.setFilteredSongs(filteredSongs);

            }
        });

        return view;
    }

    @Override
    public void onItemClick(int songId) {
        viewModel.playArtistSong(songId);
    }
}