package com.example.pitchdetection;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    static private List<String> items;

    public RecyclerAdapter(List<String> items) {
        RecyclerAdapter.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tuning_textview, parent, false);
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main, parent, false);
        return new ViewHolder(view,view2);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String item = items.get(position);
        holder.textView.setText(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    static private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        RecyclerAdapter.listener = listener;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;


        public ViewHolder(View itemView, View itemView2) {
            super(itemView);
            textView = itemView.findViewById(R.id.tuningOption);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        String clickedItem = items.get(position);
                        String notesString = clickedItem.substring(clickedItem.indexOf("(") + 1, clickedItem.indexOf(")"));

                        String[] notes = notesString.split(" ");
                        listener.onItemClick(notes);
                    }
                }
            });

        }





    }

    public interface OnItemClickListener {
        void onItemClick(String[] notes);
    }
}