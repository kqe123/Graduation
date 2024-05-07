package com.example.graduation;

import android.content.Context;
import android.net.Uri;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.ArrayList;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder>{
    private ArrayList<Media> arrayList;
    private Context context;


    public MediaAdapter(ArrayList<Media> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MediaAdapter.MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_media, parent, false);
        MediaViewHolder holder =  new MediaViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MediaAdapter.MediaViewHolder holder, int position) {
        holder.bindVideo(arrayList.get(position).getUrl());
        holder.tv_title.setText(arrayList.get(position).getTitle());
        holder.tv_views.setText(arrayList.get(position).getViews());
        holder.tv_post_date.setText("    " + arrayList.get(position).getPost_date());
        holder.tv_source.setText(arrayList.get(position).getSource());
    }

    @Override
    public int getItemCount() { return (arrayList != null ? arrayList.size() : 0); }

    public class MediaViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title, tv_post_date, tv_views, tv_source;
        SimpleExoPlayer exoplayer;
        PlayerView playerView;

        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            this.playerView = itemView.findViewById(R.id.playerView);
            this.tv_views = itemView.findViewById(R.id.tv_views);
            this.tv_post_date = itemView.findViewById(R.id.tv_post_date);
            this.tv_source = itemView.findViewById(R.id.tv_source);
            this.tv_title = itemView.findViewById(R.id.tv_title);
            initializePlayer();
        }

        private void initializePlayer() {
            exoplayer = new SimpleExoPlayer.Builder(playerView.getContext()).build();
            playerView.setPlayer(exoplayer);
        }

        public void bindVideo(String videoUrl) {
            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
            exoplayer.setMediaItem(mediaItem);
            exoplayer.prepare();
            exoplayer.setPlayWhenReady(false);
        }

    }
}
