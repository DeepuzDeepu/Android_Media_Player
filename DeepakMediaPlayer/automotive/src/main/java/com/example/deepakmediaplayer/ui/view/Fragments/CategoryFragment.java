package com.example.deepakmediaplayer.ui.view.Fragments;

import android.annotation.SuppressLint;
import android.content.ClipData;
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

import com.example.deepakmediaplayer.R;
import com.example.deepakmediaplayer.data.model.SongDataClass;
import com.example.deepakmediaplayer.ui.adapter.CategoryAdapter;
import com.example.deepakmediaplayer.ui.adapter.ItemAdapter;
import com.example.deepakmediaplayer.ui.viewmodel.SongViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryFragment extends Fragment implements CategoryAdapter.OnItemClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = CategoryFragment.class.getSimpleName();
    List<ClipData.Item>items=new ArrayList<ClipData.Item>();
    private ItemFragment mediaItemsFragment;
    private ItemAdapter itemadapter;
    private SongDataClass songDataClass;
    private SongViewModel viewModel;
    private Fragment searchFrag= SearchFragment.newInstance(null,null);
    private Fragment mediaItems = new ItemFragment();
    private Fragment FileBrowser=new FileFragment();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RecyclerView recyclerView;
    public CategoryFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MediaCategoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoryFragment newInstance(String param1, String param2) {
        CategoryFragment fragment = new CategoryFragment();
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
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view= inflater.inflate(R.layout.fragment_media_category, container, false);
        recyclerView=view.findViewById(R.id.mediacategory_recyclerview);
        viewModel = new ViewModelProvider(requireActivity()).get(SongViewModel.class);
        items.add(new ClipData.Item("All Songs"));
        items.add(new ClipData.Item("Artists"));
        items.add(new ClipData.Item("Albums"));
        items.add(new ClipData.Item("Liked Songs"));
        items.add(new ClipData.Item("Browse Songs"));
        items.add(new ClipData.Item("Files"));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new CategoryAdapter(getContext(),items,this));
        viewModel.fetchList(0);

        return view;
    }

    @Override
    public void onItemClick(int position) {
        Log.d(TAG, "onItemClick: "+position);
        FragmentTransaction transaction= getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.mediaItem,mediaItems);
        transaction.addToBackStack(null);
        transaction.commit();
        viewModel.setPosition(position);
    }

    @Override
    public void onSearch(int position) {

        FragmentTransaction transaction= getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.mediaItem,searchFrag);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    @Override
    public void files(int position) {
        FragmentTransaction transaction= getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.mediaItem,FileBrowser);
        transaction.addToBackStack(null);
        transaction.commit();

    }
}
