package com.example.deepakmediaplayer.ui.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.deepakmediaplayer.data.repository.SongRepository;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private SongRepository repository;
    private Context context;

    public ViewModelFactory(Context context) {
        this.repository = SongRepository.getInstance(context);
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {


        if(modelClass.isAssignableFrom(SongViewModel.class)){
            return (T) new SongViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown viewmodel class");
    }
}
