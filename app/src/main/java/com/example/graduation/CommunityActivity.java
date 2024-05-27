package com.example.graduation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CommunityActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Community> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    ImageView mediaBtn, cameraBtn, mypageBtn, searchBtn, homeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        //버튼 위젯 연결
        mediaBtn = findViewById(R.id.mediabtn);
        cameraBtn = findViewById(R.id.camerabtn);
        mypageBtn = findViewById(R.id.mypagebtn);
        searchBtn = findViewById(R.id.searchbtn);
        homeBtn = findViewById(R.id.homebtn);

        recyclerView = findViewById(R.id.postlist); //xml아이디연결
        recyclerView.setHasFixedSize(true); //기존성능강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); //comment객첼 담을 어레이 리스트(어뎁터쪽으로)

        database = FirebaseDatabase.getInstance(); //파이어베이스 데이터베이스 연동
        databaseReference = database.getReference("Graduation").child("Community"); //DB테이블 연결
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //파이어베이스 데이터베이스의 데이터 받아오는 곳
                arrayList.clear(); //기존배열리스트 존재하지않게 초기화(방지차원)
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) { //반복문으로 데이터리스트 추출
                    Community community = snapshot.getValue(Community.class); //만들어뒀던 Community객체에 데이터 담기
                    arrayList.add(community); //담을 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비
                }
                adapter.notifyDataSetChanged();//리스트 저장및 새로고침
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){
                //디비 가져오던중 에러 발생시
            }
        });
        adapter = new CommunityAdapter(arrayList, this);
        recyclerView.setAdapter(adapter);

        //마이페이지 버튼
        mypageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CommunityActivity.this, MypageActivity.class);
                startActivity(intent);
                finish(); // 현재 액티비티 종료
            }
        });

        //검색 버튼
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CommunityActivity.this, SearchActivity.class);
                startActivity(intent);
                finish(); // 현재 액티비티 종료
            }
        });

        //홈버튼
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CommunityActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // 현재 액티비티 종료
            }
        });

        //미디어버튼
        mediaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CommunityActivity.this, MediaActivity.class);
                startActivity(intent);
                finish(); // 현재 액티비티 종료
            }
        });

        //카메라버튼
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CommunityActivity.this, CameraActivity.class);
                startActivity(intent);
                finish(); // 현재 액티비티 종료
            }
        });


    }

}