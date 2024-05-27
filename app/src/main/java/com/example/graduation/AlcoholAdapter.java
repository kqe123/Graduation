package com.example.graduation;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class AlcoholAdapter extends RecyclerView.Adapter<AlcoholAdapter.AlcoholViewHolder> {

    private ArrayList<Alcohol> arrayList;
    private Context context;

    public AlcoholAdapter(ArrayList<Alcohol> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public AlcoholViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 실제 리스트뷰가 어댑터에 연결된 후 뷰홀더를 최초 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_alcohol, parent, false);
        AlcoholViewHolder holder = new AlcoholViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AlcoholViewHolder holder, int position) {
        // 이미지뷰안에 서버로부터 이미지를 받아와서 삽입됨.
        // arrayList.get(position).getProfile()
        Glide.with(holder.itemView).load(arrayList.get(position).getImage()).into(holder.alcohol_image);
        holder.alcohol_name.setText(arrayList.get(position).getName());

        holder.alcohol_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AlcoholActivity.class);
                intent.putExtra("name", holder.alcohol_name.getText().toString());
                context.startActivity(intent);
            }
        });
        // 아래는 서버로부터 텍스트를 받아와서 삽입.
        holder.alcohol_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AlcoholActivity.class);
                intent.putExtra("name", holder.alcohol_name.getText().toString());
                context.startActivity(intent);
            }
        });
        // 아래는 서버로부터 텍스트를 받아와서 삽입.
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class AlcoholViewHolder extends RecyclerView.ViewHolder {
        ImageView alcohol_image;
        TextView alcohol_name;

        public AlcoholViewHolder(@NonNull View itemView) {
            super(itemView);
            this.alcohol_image = itemView.findViewById(R.id.alcohol_image);
            this.alcohol_name = itemView.findViewById(R.id.alcohol_name);
        }
    }


}
