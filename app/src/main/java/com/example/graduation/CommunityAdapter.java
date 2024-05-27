package com.example.graduation;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.CommunityViewHolder>{

    private ArrayList<Community> arrayList;
    private Context context;

    public CommunityAdapter(ArrayList<Community> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CommunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_community,parent, false);
        return new CommunityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityViewHolder holder, int position) {
        holder.tv_title.setText(arrayList.get(position).getTitle());

        // 게시물 클릭 이벤트 처리
        holder.tv_title.setOnClickListener(view -> {
            // 선택한 게시물의 ID를 가져와서 ComdetailActivity로 전달
            Intent intent = new Intent(context, ComdetailActivity.class);
            intent.putExtra("title", arrayList.get(holder.getAdapterPosition()).getTitle());
            context.startActivity(intent);

            Log.d("CommunityAdapter", "클릭됨" + arrayList.get(position).getTitle());
        });
        holder.tv_create_date.setText(arrayList.get(position).getCreate_date());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class CommunityViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_create_date;

        public CommunityViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_title = itemView.findViewById(R.id.tv_postTitle);
            this.tv_create_date = itemView.findViewById(R.id.tv_create_date);
        }
    }
}
