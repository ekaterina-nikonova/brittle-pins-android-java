package com.example.brittlepins.helpers;

import android.content.res.Resources;
import android.util.Log;
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
import com.example.brittlepins.ui.BoardsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import okhttp3.HttpUrl;

public class BoardsViewAdapter extends RecyclerView.Adapter<BoardsViewAdapter.ViewHolder> {
    private ArrayList<Board> mBoards;

    public BoardsViewAdapter(ArrayList<Board> boards) {
        mBoards = boards;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView card = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.board_card, parent, false);
        return new ViewHolder(card);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Board board = mBoards.get(position);

        ImageView boardImageView = holder.card.findViewById(R.id.boardImageView);
        TextView boardName = holder.card.findViewById(R.id.boardName);

        boardName.setText(board.getName());
        Picasso.get()
                .load(board.getImageURL())
                .placeholder(R.drawable.board_placeholder)
                .error(R.drawable.error)
                .into(boardImageView);
    }

    @Override
    public int getItemCount() {
        return mBoards.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView card;
        public ViewHolder(@NonNull CardView view) {
            super(view);
            card = view;
        }
    }
}
