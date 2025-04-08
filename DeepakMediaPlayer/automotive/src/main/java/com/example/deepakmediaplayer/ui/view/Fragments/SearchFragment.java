package com.example.deepakmediaplayer.ui.view.Fragments;

import static java.util.Collections.emptyList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.widget.SearchView;

import com.example.deepakmediaplayer.R;
import com.example.deepakmediaplayer.data.model.SongDataClass;
import com.example.deepakmediaplayer.ui.adapter.SearchAdapter;
import com.example.deepakmediaplayer.ui.viewmodel.SongViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements SearchAdapter.OnItemClickListener{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "searchFragment";

    private String mParam1;
    private String mParam2;
    private SearchView searchView;
    private SongViewModel viewModel;
    private RecyclerView recyclerView;
    private SearchAdapter adapter;
    List<SongDataClass> originalList=new ArrayList<>();

    public SearchFragment() {

    }

    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
        adapter = new SearchAdapter(this);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.filterListView);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        viewModel.getSongsLiveData().observe((LifecycleOwner) getContext(), new Observer<List<SongDataClass>>() {
            @Override
            public void onChanged(List<SongDataClass> songs) {
                Log.d(TAG, "onChanged: " + songs);

                adapter.setSongs(songs);
            }
        });

        searchView = view.findViewById(R.id.search_box);
        setUpSearchView(searchView);

        return view;
    }

    private void setUpSearchView(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                hideKeyboard(searchView);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return false;
            }
        });
    }

    private void performSearch(String query) {
        if (query != null && !query.isEmpty()) {
            viewModel.searchSongs(query);
        }
        else{
            adapter.setSongs(emptyList());
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onItemClick(int songId) {
        viewModel.playFilter(songId);

    }
}