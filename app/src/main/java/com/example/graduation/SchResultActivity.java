package com.example.graduation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SchResultActivity extends AppCompatActivity {
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Alcohol> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sch_result);
        Intent intent = getIntent();
        String keyword = intent.getStringExtra("keyword");

        TextView searchContent = findViewById(R.id.searchContent);
        searchContent.setText(keyword);
        RecyclerView recyclerSearch = findViewById(R.id.recyclerSearch);
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Graduation").child("Alcohol");

        recyclerSearch.setHasFixedSize(true); // 리사이클러뷰 기존성능 강화
        layoutManager = new LinearLayoutManager(SchResultActivity.this); // layoutManager는 하나의 RecyclerView하고만 연결될수 있으니 재사용하려면 매번 초기화해야됨.
        recyclerSearch.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); // Media 객체를 담을 어레이 리스트
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear(); // 기존 배열 초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String alcohol_name = snapshot.child("name").getValue(String.class);
                    if (alcohol_name.contains(keyword)) {
                        Alcohol alcohol = snapshot.getValue(Alcohol.class); // 만들어뒀던 media 객체에 데이터를 담음.
                        arrayList.add(alcohol); // 담은 데이터들을 배열리스트에 넣고 리사이클뷰로 보낼 준비
                    }

                }
                adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter = new AlcoholAdapter(arrayList, SchResultActivity.this);
        recyclerSearch.setAdapter(adapter); // 리사이클러뷰에 어댑터 연결


    }
}