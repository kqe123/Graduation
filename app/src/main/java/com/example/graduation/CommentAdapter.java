package com.example.graduation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder>{

    private ArrayList<Comment>arrayList;
    private Context context;

    public CommentAdapter(ArrayList<Comment> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_comment,parent, false);
        CommentViewHolder holder = new CommentViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.tv_userName.setText(arrayList.get(position).getUserName());
        holder.tv_content.setText(arrayList.get(position).getContent());
        holder.tv_age.setText(arrayList.get(position).getAge());
        holder.tv_gender.setText(arrayList.get(position).getGender());

    }

    @Override
    public int getItemCount() {
        //삼항 연산자 (if랑비슷. 참이면 ?이후가 실행, 거짓이면 :이후가 실행)
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tv_userName;
        TextView tv_content;
        TextView tv_age;
        TextView tv_gender;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_userName = itemView.findViewById(R.id.tv_userName);
            this.tv_content = itemView.findViewById(R.id.tv_content);
            this.tv_age = itemView.findViewById(R.id.tv_age);
            this.tv_gender = itemView.findViewById(R.id.tv_gender);
        }
    }
}
