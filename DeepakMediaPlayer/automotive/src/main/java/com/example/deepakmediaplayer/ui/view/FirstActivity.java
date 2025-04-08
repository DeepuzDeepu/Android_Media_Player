package com.example.deepakmediaplayer.ui.view;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deepakmediaplayer.R;
import com.example.deepakmediaplayer.data.model.SongDataClass;
import com.example.deepakmediaplayer.ui.view.Fragments.CategoryFragment;
import com.example.deepakmediaplayer.ui.view.Fragments.ItemFragment;
import com.example.deepakmediaplayer.ui.view.Fragments.PlaybackFragment;
import com.example.deepakmediaplayer.ui.viewmodel.ViewModelFactory;
import com.example.deepakmediaplayer.ui.viewmodel.SongViewModel;

import java.util.ArrayList;
import java.util.List;

public class FirstActivity extends AppCompatActivity {

    private static final String TAG = "FirstActivity";

    private RecyclerView recyclerView;
    private LiveData<List<SongDataClass>> songs;
    private Fragment mediaCategory = new CategoryFragment();
    private Fragment mediaItems = new ItemFragment();
    private Fragment mediaPlayback = new PlaybackFragment();
    private static final int REQUEST_CODE = 100;

    private final ActivityResultLauncher<String[]> requestPermissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                boolean allGranted = true;
                for (Boolean granted : result.values()) {
                    if (!granted) {
                        allGranted = false;
                        break;
                    }
                }
                if (allGranted) {
                    Log.d(TAG, "All permissions granted. Loading audio files.");
                    loadAudioFiles();
                } else {
                    Log.d(TAG, "Some permissions were denied. Cannot load audio files.");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        setContentView(R.layout.activity_first);

        checkPermissionsAndLoadFiles();
    }

    private void checkPermissionsAndLoadFiles() {
        List<String> permissionsNeeded = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.READ_MEDIA_AUDIO);
            }

        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
//                    != PackageManager.PERMISSION_GRANTED) {
//                permissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS);
//            }
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK)
//                    != PackageManager.PERMISSION_GRANTED) {
//                permissionsNeeded.add(Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK);
//            }
//        }

        if (!permissionsNeeded.isEmpty()) {
            requestPermissionsLauncher.launch(permissionsNeeded.toArray(new String[0]));
        } else {
            loadAudioFiles();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_CODE);
            }
        }
    }

    private void loadAudioFiles() {
        Log.d(TAG, "Loading audio files...");
        ViewModelFactory factory = new ViewModelFactory(this);
        SongViewModel viewModel = new ViewModelProvider(this, factory).get(SongViewModel.class);

        if (getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView3) == null) {
            Log.d(TAG, "Loading fragments...");
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView3, mediaCategory)
                    .replace(R.id.mediaItem, mediaItems)
                    .replace(R.id.fragmentContainerView5, mediaPlayback)
                    .commit();
        }
    }
}
