package com.example.brittlepins.helpers;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.brittlepins.R;
import com.example.brittlepins.api.model.Board;

import java.util.ArrayList;

public class BoardsViewAdapter extends RecyclerView.Adapter<BoardsViewAdapter.ViewHolder> {
    private ArrayList<Board> mBoards;

    public BoardsViewAdapter(ArrayList<Board> boards) {
        mBoards = boards;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView view = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.support_simple_spinner_dropdown_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(mBoards.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mBoards.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ViewHolder(@NonNull TextView view) {
            super(view);
            textView = view;
        }
    }
}
