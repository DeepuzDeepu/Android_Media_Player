package com.example.deepakmediaplayer.ui.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deepakmediaplayer.R;
import com.example.deepakmediaplayer.data.model.FileItem;

import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

    private List<FileItem> fileList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(FileItem item);
    }

    public FileAdapter(List<FileItem> fileList, OnItemClickListener listener) {
        this.fileList = fileList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FileItem item = fileList.get(position);
        holder.textView.setText(item.getName());

        if (item.isDirectory()) {
            holder.iconView.setImageResource(R.drawable.folder);
        } else {
            holder.iconView.setImageResource(R.drawable.file);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView iconView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.file_name);
            iconView = itemView.findViewById(R.id.file_icon);
        }
    }
}
