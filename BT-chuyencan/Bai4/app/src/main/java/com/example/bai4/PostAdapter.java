package com.example.bai4;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> postList;

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.userId.setText("User ID: " + post.getUserId());
        holder.id.setText("Post ID: " + post.getId());
        holder.title.setText(post.getTitle());
        holder.body.setText(post.getBody());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView userId, id, title, body;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            userId = itemView.findViewById(R.id.txtUserId);
            id = itemView.findViewById(R.id.txtId);
            title = itemView.findViewById(R.id.txtTitle);
            body = itemView.findViewById(R.id.txtBody);
        }
    }
}
