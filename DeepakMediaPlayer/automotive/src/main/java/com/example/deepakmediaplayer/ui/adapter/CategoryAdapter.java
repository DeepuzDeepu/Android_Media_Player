package com.example.deepakmediaplayer.ui.adapter;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deepakmediaplayer.R;
import com.example.deepakmediaplayer.ui.view.viewholders.mediaCategoryViewHolder;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<mediaCategoryViewHolder> {


    Context context;
    List<ClipData.Item> items;
    OnItemClickListener listener;
    private int selectedPosition=0;
    private static final String TAG = "MediaCategoryAdapter";

    public CategoryAdapter(Context context, List<ClipData.Item> items, OnItemClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener=listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onSearch(int position);
        void files(int position);
    }

    @NonNull
    @Override
    public mediaCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new mediaCategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.media_category_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull mediaCategoryViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tv1.setText(items.get(position).getText());

//        holder.tv1.setTextColor(Color.RED);
        if(position==selectedPosition){
            holder.tv1.setTextColor(Color.RED);
        }
        else{
            holder.tv1.setTextColor(Color.BLACK);
        }

        holder.itemView.setOnClickListener(view -> {
            int previousPosition=selectedPosition;
            selectedPosition=holder.getAdapterPosition();

            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);

            listener.onItemClick(position);
            if(position==4){
                listener.onSearch(position);
            }

            if(position==5){
                listener.files(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
