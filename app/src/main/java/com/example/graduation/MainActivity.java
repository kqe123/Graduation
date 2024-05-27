package com.example.graduation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    ImageView  mediaBtn, cameraBtn, mypageBtn, searchBtn, commuBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Alcohol> arrayList;

    String nickname, recom1st, recom2nd, recom3rd; // 사용자닉네임, 1~3순위 추천요소
    float degree; // 사용자 선호 도수
    int sweetness, bitterness, price; // 사용자 선호 단맛, 쓴맛, 가격
    Boolean carbonated; // 사용자 선호 탄산 여부
    TextView tv_nickname;
    float degreeValue=0.0f, priceValue=0.0f, sweetValue=0.0f, bitterValue=0.0f, carbonValue=0.0f;
    float total;
    HashMap<String, Float> score = new HashMap<>();
    ArrayList<String> top3 = new ArrayList<>();

    float returnDegree(float value, float alcohol_degree, float user_degree) {
        float alcohol_pos = 0.0f, user_pos = 0.0f;
        float result;
        if (alcohol_degree <= 10) {alcohol_pos=0.2f;}
        else if(alcohol_degree >10 && alcohol_degree <= 20) {alcohol_pos=0.4f;}
        else if(alcohol_degree >20 && alcohol_degree <= 30) {alcohol_pos=0.6f;}
        else if(alcohol_degree >30 && alcohol_degree <= 40) {alcohol_pos=0.8f;}
        else if(alcohol_degree >40 && alcohol_degree <= 50) {alcohol_pos=1.0f;}
        if (user_degree <= 10) {user_pos=0.2f;}
        else if(user_degree >10 && user_degree <= 20) {user_pos=0.4f;}
        else if(user_degree >20 && user_degree <= 30) {user_pos=0.6f;}
        else if(user_degree >30 && user_degree <= 40) {user_pos=0.8f;}
        else if(user_degree >40 && user_degree <= 50) {user_pos=1.0f;}
        result = value * (1-Math.abs(alcohol_pos-user_pos));
        return result;
    }

    float returnPrice(float value, int alcohol_price, int user_price) {
        float alcohol_pos = 0.0f, user_pos = 0.0f;
        float result;
        if (alcohol_price <= 10000) {alcohol_pos=0.25f;}
        else if(alcohol_price > 10000 && alcohol_price <= 50000) {alcohol_pos=0.5f;}
        else if(alcohol_price >50000 && alcohol_price <= 100000) {alcohol_pos=0.75f;}
        else if(alcohol_price > 100000) {alcohol_pos=1.0f;}
        if (user_price <= 10000) {user_pos=0.25f;}
        else if(user_price > 10000 && user_price <= 50000) {user_pos=0.5f;}
        else if(user_price >50000 && user_price <= 100000) {user_pos=0.75f;}
        else if(user_price > 100000) {user_pos=1.0f;}
        result = value * (1-Math.abs(alcohol_pos-user_pos));
        return result;
    }

    float returnSweet(float value, int alcohol_sweet, int user_sweet) {
        float alcohol_pos = 0.0f, user_pos = 0.0f;
        float result;
        switch(alcohol_sweet) {
            case 1 : alcohol_pos = 0.2f; break;
            case 2 : alcohol_pos = 0.4f; break;
            case 3 : alcohol_pos = 0.6f; break;
            case 4 : alcohol_pos = 0.8f; break;
            case 5 : alcohol_pos = 1.0f; break;
        }
        switch(user_sweet) {
            case 1 : user_pos = 0.2f; break;
            case 2 : user_pos = 0.4f; break;
            case 3 : user_pos = 0.6f; break;
            case 4 : user_pos = 0.8f; break;
            case 5 : user_pos = 1.0f; break;
        }
        result = value * (1-Math.abs(alcohol_pos-user_pos));
        return result;
    }

    float returnBitter(float value, int alcohol_bitter, int user_bitter) {
        float alcohol_pos = 0.0f, user_pos = 0.0f;
        float result;
        switch(alcohol_bitter) {
            case 1 : alcohol_pos = 0.2f; break;
            case 2 : alcohol_pos = 0.4f; break;
            case 3 : alcohol_pos = 0.6f; break;
            case 4 : alcohol_pos = 0.8f; break;
            case 5 : alcohol_pos = 1.0f; break;
        }
        switch(user_bitter) {
            case 1 : user_pos = 0.2f; break;
            case 2 : user_pos = 0.4f; break;
            case 3 : user_pos = 0.6f; break;
            case 4 : user_pos = 0.8f; break;
            case 5 : user_pos = 1.0f; break;
        }
        result = value * (1-Math.abs(alcohol_pos-user_pos));
        return result;
    }

    float returnCarbon(float value, Boolean alcohol_carbon, Boolean user_carbon) {
        float pos = 1.0f;
        float result;
        if(alcohol_carbon != user_carbon) { pos = 0.5f; }
        result = value * pos;
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaBtn = findViewById(R.id.mediabtn);
        cameraBtn = findViewById(R.id.camerabtn);
        mypageBtn = findViewById(R.id.mypagebtn);
        searchBtn = findViewById(R.id.searchbtn);
        commuBtn = findViewById(R.id.commubtn);

        tv_nickname = findViewById(R.id.nickname);
        TextView text1 = findViewById(R.id.text1);
        TextView text2 = findViewById(R.id.text2);
        TextView text3 = findViewById(R.id.text3);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser(); // 현재 사용자 정보 가져옴

        //동작1 : 현재 로그인된 계정의 정보를 가져온다!
        if (user != null) {
            String userUid = user.getUid(); // 현재 유저의 UID가져옴.
            databaseReference = FirebaseDatabase.getInstance().getReference("Graduation").child("UserAccount").child(userUid);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        nickname = snapshot.child("nickname").getValue(String.class);
                        String temp_degree = snapshot.child("degree").getValue(String.class);
                        String temp_price = snapshot.child("price").getValue(String.class);
                        sweetness = snapshot.child("sweetness").getValue(int.class);
                        bitterness = snapshot.child("bitterness").getValue(int.class);
                        carbonated = snapshot.child("carbonated").getValue(Boolean.class);
                        recom1st = snapshot.child("recom1st").getValue(String.class);
                        recom2nd = snapshot.child("recom2nd").getValue(String.class);
                        recom3rd = snapshot.child("recom3rd").getValue(String.class);
                        tv_nickname.setText(nickname);

                        //도수(String) -> float
                        switch(temp_degree) {
                            case "10 이하" : degree = 5; break;
                            case "10~20" : degree = 15; break;
                            case "20~30" : degree = 25; break;
                            case "30~40" : degree = 35; break;
                            case "40~50" : degree = 45; break;
                        }
                        //가격(String) -> int
                        switch(temp_price) {
                            case "1만원 이하" : price = 5000; break;
                            case "1~5만원"  : price = 25000; break;
                            case "5~10만원" : price = 75000; break;
                            case "10만원 이상" : price = 125000; break;
                        }
                        //가중치 설정 1~3순위
                        switch(recom1st) {
                            case "도수" : degreeValue = 27f; break;
                            case "가격" : priceValue = 27f; break;
                            case "단맛" : sweetValue = 27f; break;
                            case "쓴맛" : bitterValue = 27f; break;
                            case "탄산 여부" : carbonValue = 27f; break;
                        }
                        switch(recom2nd) {
                            case "도수" : degreeValue = 22.5f; break;
                            case "가격" : priceValue = 22.5f; break;
                            case "단맛" : sweetValue = 22.5f; break;
                            case "쓴맛" : bitterValue = 22.5f; break;
                            case "탄산 여부" : carbonValue = 22.5f; break;
                        }
                        switch(recom3rd) {
                            case "도수" : degreeValue = 18f; break;
                            case "가격" : priceValue = 18f; break;
                            case "단맛" : sweetValue = 18f; break;
                            case "쓴맛" : bitterValue = 18f; break;
                            case "탄산 여부" : carbonValue = 18f; break;
                        }
                        //4~5순위 가중치 설정
                        if(degreeValue == 0) {degreeValue = 13.5f;}
                        if(priceValue == 0) {priceValue = 13.5f;}
                        if(sweetValue == 0) {sweetValue = 13.5f;}
                        if(bitterValue == 0) {bitterValue = 13.5f;}
                        if(carbonValue == 0) {carbonValue = 13.5f;}

                        databaseReference = FirebaseDatabase.getInstance().getReference("Graduation").child("Alcohol");
                        Query query = databaseReference.orderByChild("name");
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    total = 0;
                                    Float get_degree = snapshot.child("degree").getValue(Float.class);
                                    int get_price = snapshot.child("price").getValue(int.class);
                                    int get_sweet = snapshot.child("sweetness").getValue(int.class);
                                    int get_bitter = snapshot.child("bitterness").getValue(int.class);
                                    String temp_carbon = snapshot.child("carbonated").getValue(String.class);
                                    Boolean get_carbon;
                                    if(temp_carbon.equals("O")) {get_carbon=true;}
                                    else {get_carbon=false;}

                                    //점수 계산
                                    total += returnDegree(degreeValue,get_degree,degree);
                                    total += returnPrice(priceValue,get_price,price);
                                    total += returnSweet(sweetValue, get_sweet, sweetness);
                                    total += returnBitter(bitterValue, get_bitter, bitterness);
                                    total += returnCarbon(carbonValue, get_carbon, carbonated);
                                    score.put(snapshot.child("name").getValue(String.class),total);

                                }
                                // 점수를 기준으로 내림차순으로 정렬된 술 이름을 가져오기
                                List<String> keys = new ArrayList<>(score.keySet());

                                Collections.sort(keys, (v1, v2) -> (score.get(v2).compareTo(score.get(v1))));

                                //상위 3개 술이름 가져오기
                                int count = 0;
                                for (String key : keys) {
                                    top3.add(key);
                                    count += 1;
                                    if (count == 3) { break; }
                                }
                                text1.setText(top3.get(0) + " : " + score.get(top3.get(0)));
                                text2.setText(top3.get(1) + " : " + score.get(top3.get(1)));
                                text3.setText(top3.get(2) + " : " + score.get(top3.get(2)));

                                //리사이클러뷰에 가장 높은 점수를 받은 술 3개 연결
                                recyclerView.setHasFixedSize(true); // 리사이클러뷰 기존성능 강화
                                layoutManager = new LinearLayoutManager(MainActivity.this); // layoutManager는 하나의 RecyclerView하고만 연결될수 있으니 재사용하려면 매번 초기화해야됨.
                                recyclerView.setLayoutManager(layoutManager);
                                arrayList = new ArrayList<>(); // Media 객체를 담을 어레이 리스트
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        arrayList.clear(); // 기존 배열 초기화
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            String name = snapshot.child("name").getValue(String.class);
                                            if (top3.contains(name)) {
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
                                adapter = new AlcoholAdapter(arrayList, MainActivity.this);
                                recyclerView.setAdapter(adapter);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(),"오류! 해당 계정이 없습니다!!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        // 미디어 버튼
        mediaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MediaActivity.class);
                startActivity(intent);
            }
        });

        //카메라 버튼
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });

        //마이페이지 버튼
        mypageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MypageActivity.class);
                startActivity(intent);
            }
        });

        //검색 버튼
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        commuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CommunityActivity.class);
                startActivity(intent);
            }
        });

    }
}