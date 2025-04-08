package com.example.deepakmediaplayer.ui.view.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deepakmediaplayer.R;

public class mediaCategoryViewHolder extends RecyclerView.ViewHolder {

    public TextView tv1;
    public mediaCategoryViewHolder(@NonNull View itemView) {
        super(itemView);

        tv1=itemView.findViewById(R.id.tv1);

    }


}
