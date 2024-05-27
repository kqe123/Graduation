package com.example.graduation;

import android.content.Context;
import android.content.Intent;
import android.media.Rating;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private ArrayList<Review> arrayList;
    private Context context;

    public ReviewAdapter(ArrayList<Review> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 실제 리스트뷰가 어댑터에 연결된 후 뷰홀더를 최초 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_review, parent, false);
        ReviewViewHolder holder = new ReviewViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        holder.tv_content.setText(arrayList.get(position).getContent());
        holder.tv_nickname.setText(arrayList.get(position).getNickname());
        holder.score.setRating(arrayList.get(position).getScore());
        holder.tv_age.setText(arrayList.get(position).getAge());
        holder.tv_gender.setText(arrayList.get(position).getGender());
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        RatingBar score;
        TextView tv_content;
        TextView tv_nickname;
        TextView tv_age;
        TextView tv_gender;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            this.score = itemView.findViewById(R.id.score);
            this.tv_content = itemView.findViewById(R.id.content);
            this.tv_nickname = itemView.findViewById(R.id.nickname);
            this.tv_age = itemView.findViewById(R.id.age);
            this.tv_gender = itemView.findViewById(R.id.gender);
        }
    }


}