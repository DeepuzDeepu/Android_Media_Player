package com.example.deepakmediaplayer.ui.view.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deepakmediaplayer.R;
import com.example.deepakmediaplayer.data.model.FileItem;
import com.example.deepakmediaplayer.data.model.SongDataClass;
import com.example.deepakmediaplayer.ui.adapter.FileAdapter;
import com.example.deepakmediaplayer.ui.viewmodel.SongViewModel;
import com.example.deepakmediaplayer.utils.FileNode;
import com.example.deepakmediaplayer.utils.FileStructureBuilder;
import java.util.ArrayList;
import java.util.List;

public class FileFragment extends Fragment {

    private RecyclerView recyclerView;
    private FileAdapter fileAdapter;
    private FileNode currentNode;
    private FileNode rootNode;
    SongViewModel viewModel;
    private static final String TAG = "fileFragment";

    public FileFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SongViewModel.class);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_browser, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(((AppCompatActivity) requireActivity()).getSupportActionBar() !=null){
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Browse Files");
        }

        recyclerView = view.findViewById(R.id.file_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel.getSongs().observe(getViewLifecycleOwner(), songs -> {
            rootNode = FileStructureBuilder.buildFileStructure(getPathsFromSongData(songs));
            currentNode = rootNode;
            displayCurrentNode();
        });

        return view;
    }

    private List<String> getPathsFromSongData(List<SongDataClass> items) {
        List<String> paths = new ArrayList<>();
        for (SongDataClass item : items) {
            paths.add(item.getUrl());
        }
        return paths;
    }

    private void displayCurrentNode() {
        List<FileItem> displayList = new ArrayList<>();
        for (FileNode node : currentNode.children) {
            String fullPath = currentNode.getPath() + "/" + node.name;
            int songId = getSongIdForFile(fullPath);
            displayList.add(new FileItem(node.name, node.isDirectory, fullPath, songId));
        }

        fileAdapter = new FileAdapter(displayList, item -> {
            if (item.isDirectory()) {
                for (FileNode node : currentNode.children) {
                    if (node.name.equals(item.getName())) {
                        currentNode = node;
                        displayCurrentNode();
                        return;
                    }
                }
            } else {

                viewModel.playSong(item.getId());
                Log.d(TAG, "Playing song with ID: " + item.getId());
            }
        });
        recyclerView.setAdapter(fileAdapter);
    }

    private int getSongIdForFile(String filePath) {
        List<SongDataClass> songs = viewModel.getSongs().getValue();
        if (songs == null) {
            Log.e(TAG, "Song list is null");
            return -1;
        }


        String absolutePath = "/storage/emulated/0/" + filePath;

        for (SongDataClass song : songs) {
            Log.d(TAG, "Comparing stored: " + song.getUrl() + " with converted: " + absolutePath);
            if (song.getUrl().equals(absolutePath)) {
                return song.getId();
            }
        }

        Log.e(TAG, "No matching song found for path: " + filePath);
        return -1;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (currentNode != rootNode) {
                currentNode = currentNode.parent;
                displayCurrentNode();
            } else {
                Toast.makeText(getContext(), "You are in the Root Directory", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openAudioFile(FileItem file) {

    }
}