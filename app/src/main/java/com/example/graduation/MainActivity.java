package com.example.graduation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Resources;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageView  mediaBtn, cameraBtn, mypageBtn, searchBtn, commuBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Alcohol> arrayList;

    String nickname; // 사용자닉네임, 1~3순위 추천요소
    float user_degree; // 사용자 선호 도수
    int user_sweetness, user_bitterness, user_price, user_carbonation; // 사용자 선호 단맛, 쓴맛, 가격
    Boolean user_carbonated; // 사용자 선호 탄산 여부
    TextView tv_nickname;
    String[][] user_preference = new String[1][5];
    int total = 0;
    HashMap<String, Integer> score = new HashMap<>();
    ArrayList<String> top3 = new ArrayList<>();

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

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        tv_nickname = findViewById(R.id.nickname);
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
                        user_sweetness = snapshot.child("sweetness").getValue(int.class);
                        user_bitterness = snapshot.child("bitterness").getValue(int.class);
                        user_carbonated = snapshot.child("carbonated").getValue(Boolean.class);
                        //도수(String) -> float
                        switch(temp_degree) {
                            case "10 이하" : user_preference[0][0] = "low"; break;
                            case "10~20" : user_preference[0][0] = "medium"; break;
                            case "20~30" : user_preference[0][0] = "little_high"; break;
                            case "30~40" : user_preference[0][0] = "high"; break;
                            case "40~50" : user_preference[0][0] = "very_high"; break;
                        }
                        //가격(String) -> int
                        switch(temp_price) {
                            case "1만원 이하" : user_preference[0][1] = "cheap"; break;
                            case "1~5만원"  : user_preference[0][1] = "average"; break;
                            case "5~10만원" : user_preference[0][1] = "expensive"; break;
                            case "10만원 이상" : user_preference[0][1] = "very_expensive" ; break;
                        }
                        user_preference[0][2] = returnSweetness(user_sweetness);
                        user_preference[0][3] = returnBitterness(user_bitterness);
                        user_preference[0][4] = returnCarbonation(user_carbonated);

                        tv_nickname.setText(nickname);

                        databaseReference = FirebaseDatabase.getInstance().getReference("Graduation").child("Alcohol");
                        Query query = databaseReference.orderByChild("name");
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    total = 0;
                                    String name = snapshot.child("name").getValue(String.class);
                                    String get_degree = returnDegree(snapshot.child("degree").getValue(Float.class));
                                    String get_price = returnPrice(snapshot.child("price").getValue(int.class));
                                    String get_sweet = returnSweetness(snapshot.child("sweetness").getValue(int.class));
                                    String get_bitter = returnBitterness(snapshot.child("bitterness").getValue(int.class));
                                    String get_carbon = snapshot.child("carbonated").getValue(String.class);
                                    total += distanceDegree(user_preference[0][0],get_degree);
                                    total += distancePrice(user_preference[0][1],get_price);
                                    total += distanceSweet(user_preference[0][2],get_sweet);
                                    total += distanceBitter(user_preference[0][3],get_bitter);
                                    total += distanceCarbon(user_preference[0][4],get_carbon);
                                    score.put(name,total);
                                }
                                // 점수를 기준으로 내림차순으로 정렬된 술 이름을 가져오기
                                List<String> keys = new ArrayList<>(score.keySet());
                                Collections.sort(keys, (v1, v2) -> (score.get(v1).compareTo(score.get(v2))));

                                //상위 3개 술이름 가져오기
                                int count = 0;
                                for (String key : keys) {
                                    top3.add(key);
                                    count += 1;
                                    if (count == 3) { break; }
                                }


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
        
        //커뮤니티 버튼
        commuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CommunityActivity.class);
                startActivity(intent);
            }
        });

    }

    String returnDegree(float degree) {
        String word = "";
        if(degree <= 10) {word = "low";}
        else if(degree > 10 && degree <= 20) {word = "medium"; }
        else if(degree > 20 && degree <= 30) {word = "little_high"; }
        else if(degree > 30 && degree <= 40) {word = "high"; }
        else {word = "very_high";}

        return word;
    }

    String returnPrice(int price) {
        String word = "";
        if(price <= 10000) {word = "cheap";}
        else if(price > 10000 && price <= 50000) {word = "average"; }
        else if(price > 50000 && price <= 100000) {word = "expensive"; }
        else {word = "very_expensive";}

        return word;
    }


    String returnSweetness(int sweetness) {
        String word = "";
        switch(sweetness) {
            case 0 : word = "no_sweet" ; break;
            case 1 : word = "no_sweet" ; break;
            case 2 : word = "average" ; break;
            case 3 : word = "little_sweet" ; break;
            case 4 : word = "sweet" ; break;
            case 5 : word = "very_sweet" ; break;
        }
        return word;
    }
    String returnBitterness(int bitterness) {
        String word = "";
        switch(bitterness) {
            case 0 : word = "no_bitter" ; break;
            case 1 : word = "no_bitter" ; break;
            case 2 : word = "average" ; break;
            case 3 : word = "little_bitter" ; break;
            case 4 : word = "bitter" ; break;
            case 5 : word = "very_sweet" ; break;
        }
        return word;
    }
    String returnCarbonation(Boolean carbonation) {
        String word = "";
        if (carbonation == true) {word = "O";}
        else {word = "X";}
        return word;
    }

    int distanceDegree(String user_degree,String alcohol_degree) {
        int user = 0, alcohol = 0;
        switch(user_degree) {
            case "low" : user = 1; break;
            case "medium" : user = 2; break;
            case "little_high" : user = 3; break;
            case "high" : user = 4; break;
            case "very_high" : user = 5; break;
        }
        switch(alcohol_degree) {
            case "low" : alcohol = 1; break;
            case "medium" : alcohol = 2; break;
            case "little_high" : alcohol = 3; break;
            case "high" : alcohol = 4; break;
            case "very_high" : alcohol = 5; break;
        }
        return Math.abs(user-alcohol);
    }

    int distancePrice(String user_price,String alcohol_price) {
        int user = 0, alcohol = 0;
        switch(user_price) {
            case "cheap" : user = 1; break;
            case "average" : user = 2; break;
            case "expensive" : user = 3; break;
            case "very_expensive" : user = 4; break;
        }
        switch(alcohol_price) {
            case "cheap" : alcohol = 1; break;
            case "average" : alcohol = 2; break;
            case "expensive" : alcohol = 3; break;
            case "very_expensive" : alcohol = 4; break;
        }
        return Math.abs(user-alcohol);
    }
    int distanceSweet(String user_sweet,String alcohol_sweet) {
        int user = 0, alcohol = 0;
        switch(user_sweet) {
            case "no_sweet" : user = 1 ; break;
            case "average" : user = 2 ; break;
            case "little_sweet" : user = 3; break;
            case "sweet" : user = 4; break;
            case "very_sweet" : user = 5; break;
        }
        switch(alcohol_sweet) {
            case "no_sweet" : alcohol = 1 ; break;
            case "average" : alcohol = 2 ; break;
            case "little_sweet" : alcohol = 3; break;
            case "sweet" : alcohol = 4; break;
            case "very_sweet" : alcohol = 5; break;
        }
        return Math.abs(user-alcohol);
    }
    int distanceBitter(String user_bitter,String alcohol_bitter) {
        int user = 0, alcohol = 0;
        switch(user_bitter) {
            case "no_bitter" : user = 1 ; break;
            case "average" : user = 2 ; break;
            case "little_bitter" : user = 3; break;
            case "bitter" : user = 4; break;
            case "very_sweet" : user = 5; break;
        }
        switch(alcohol_bitter) {
            case "no_bitter" : alcohol = 1 ; break;
            case "average" : alcohol = 2 ; break;
            case "little_bitter" : alcohol = 3; break;
            case "bitter" : alcohol = 4; break;
            case "very_sweet" : alcohol = 5; break;
        }
        return Math.abs(user-alcohol);
    }

    int distanceCarbon(String user_carbon,String alcohol_carbon) {
        int distance;
        if(user_carbon.equals(alcohol_carbon)) {distance = 0;}
        else {distance=1;}
        return distance;
    }
}