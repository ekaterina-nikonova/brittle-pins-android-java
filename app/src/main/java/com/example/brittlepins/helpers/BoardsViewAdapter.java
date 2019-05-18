package com.example.brittlepins.helpers;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
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
        LinearLayout view = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.board_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageView boardImageView = holder.layout.findViewById(R.id.boardImageView);
        TextView boardName = holder.layout.findViewById(R.id.boardName);

        boardName.setText(mBoards.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mBoards.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout layout;
        public ViewHolder(@NonNull LinearLayout view) {
            super(view);
            layout = view;
        }
    }
}
