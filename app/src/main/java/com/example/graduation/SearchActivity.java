package com.example.graduation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    TextView tv_beer, tv_soju, tv_wine, tv_whiskey, tv_rum, tv_jin, tv_vodka, tv_portwine, tv_champagne, tv_makgeolli;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Alcohol> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    Boolean beerVisible = false, sojuVisible = false, wineVisible = false, whiskeyVisible = false, rumVisible = false;
    Boolean jinVisible = false, vodkaVisible = false, portwineVisible = false, champagneVisible = false, makgeolliVisible = false;
    SearchView searchView;
    ImageView iv_backbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        tv_beer = findViewById(R.id.tv_beer);
        tv_soju = findViewById(R.id.tv_soju);
        tv_wine = findViewById(R.id.tv_wine);
        tv_whiskey = findViewById(R.id.tv_whiskey);
        tv_rum = findViewById(R.id.tv_rum);
        tv_jin = findViewById(R.id.tv_jin);
        tv_vodka = findViewById(R.id.tv_vodka);
        tv_portwine = findViewById(R.id.tv_portwine);
        tv_champagne = findViewById(R.id.tv_champagne);
        tv_makgeolli = findViewById(R.id.tv_makgeolli);
        searchView = findViewById(R.id.searchView);
        iv_backbtn = findViewById(R.id.backbtn);
        RecyclerView recyclerBeer = findViewById(R.id.recyclerBeer);
        RecyclerView recyclerSoju = findViewById(R.id.recyclerSoju);
        RecyclerView recyclerWine = findViewById(R.id.recyclerWine);
        RecyclerView recyclerWhiskey = findViewById(R.id.recyclerWhiskey);
        RecyclerView recyclerRum = findViewById(R.id.recyclerRum);
        RecyclerView recyclerJin = findViewById(R.id.recyclerJin);
        RecyclerView recyclerVodka = findViewById(R.id.recyclerVodka);
        RecyclerView recyclerPortWine = findViewById(R.id.recyclerPortWine);
        RecyclerView recyclerChampagne = findViewById(R.id.recyclerChampagne);
        RecyclerView recyclerMakgeolli = findViewById(R.id.recyclerMakgeolli);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String searchContent = searchView.getQuery().toString();
                if(searchContent.length() > 1) {
                    Intent intent = new Intent(SearchActivity.this, SchResultActivity.class);
                    intent.putExtra("keyword", searchContent);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(),"오류! 2자이상 입력해주세요!", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });


        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Graduation").child("Alcohol");

        tv_beer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (beerVisible) {
                    // 목록이 이미 표시되어 있다면 숨김
                    recyclerBeer.setVisibility(View.GONE);
                    beerVisible = false;
                } else {
                    // 목록이 표시되어 있지 않다면 표시
                    recyclerBeer.setVisibility(View.VISIBLE);
                    beerVisible = true;
                    recyclerBeer.setHasFixedSize(true); // 리사이클러뷰 기존성능 강화
                    layoutManager = new LinearLayoutManager(SearchActivity.this); // layoutManager는 하나의 RecyclerView하고만 연결될수 있으니 재사용하려면 매번 초기화해야됨.
                    recyclerBeer.setLayoutManager(layoutManager);
                    arrayList = new ArrayList<>(); // Media 객체를 담을 어레이 리스트
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            arrayList.clear(); // 기존 배열 초기화
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String category = snapshot.child("category").getValue(String.class);
                                if (category.equals("맥주")) {
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
                    adapter = new AlcoholAdapter(arrayList, SearchActivity.this);
                    recyclerBeer.setAdapter(adapter); // 리사이클러뷰에 어댑터 연결
                }

            }
        });

        tv_soju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sojuVisible) {
                    // 목록이 이미 표시되어 있다면 숨김
                    recyclerSoju.setVisibility(View.GONE);
                    sojuVisible = false;
                } else {
                    // 목록이 표시되어 있지 않다면 표시
                    recyclerSoju.setVisibility(View.VISIBLE);
                    sojuVisible = true;
                    recyclerSoju.setHasFixedSize(true); // 리사이클러뷰 기존성능 강화
                    layoutManager = new LinearLayoutManager(SearchActivity.this);
                    recyclerSoju.setLayoutManager(layoutManager);
                    arrayList = new ArrayList<>(); // Media 객체를 담을 어레이 리스트
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            arrayList.clear(); // 기존 배열 초기화
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String category = snapshot.child("category").getValue(String.class);
                                if (category.equals("소주")) {
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
                    adapter = new AlcoholAdapter(arrayList, SearchActivity.this);
                    recyclerSoju.setAdapter(adapter); // 리사이클러뷰에 어댑터 연결

                }
            }
        });

        tv_wine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wineVisible) {
                    // 목록이 이미 표시되어 있다면 숨김
                    recyclerWine.setVisibility(View.GONE);
                    wineVisible = false;
                } else {
                    // 목록이 표시되어 있지 않다면 표시
                    recyclerWine.setVisibility(View.VISIBLE);
                    wineVisible = true;
                    recyclerWine.setHasFixedSize(true); // 리사이클러뷰 기존성능 강화
                    layoutManager = new LinearLayoutManager(SearchActivity.this);
                    recyclerWine.setLayoutManager(layoutManager);
                    arrayList = new ArrayList<>(); // Media 객체를 담을 어레이 리스트
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            arrayList.clear(); // 기존 배열 초기화
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String category = snapshot.child("category").getValue(String.class);
                                if (category.equals("와인")) {
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
                    adapter = new AlcoholAdapter(arrayList, SearchActivity.this);
                    recyclerWine.setAdapter(adapter); // 리사이클러뷰에 어댑터 연결

                }
            }
        });

        tv_whiskey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (whiskeyVisible) {
                    // 목록이 이미 표시되어 있다면 숨김
                    recyclerWhiskey.setVisibility(View.GONE);
                    whiskeyVisible = false;
                } else {
                    // 목록이 표시되어 있지 않다면 표시
                    recyclerWhiskey.setVisibility(View.VISIBLE);
                    whiskeyVisible = true;
                    recyclerWhiskey.setHasFixedSize(true); // 리사이클러뷰 기존성능 강화
                    layoutManager = new LinearLayoutManager(SearchActivity.this);
                    recyclerWhiskey.setLayoutManager(layoutManager);
                    arrayList = new ArrayList<>(); // Media 객체를 담을 어레이 리스트
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            arrayList.clear(); // 기존 배열 초기화
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String category = snapshot.child("category").getValue(String.class);
                                if (category.equals("위스키")) {
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
                    adapter = new AlcoholAdapter(arrayList, SearchActivity.this);
                    recyclerWhiskey.setAdapter(adapter); // 리사이클러뷰에 어댑터 연결

                }
            }
        });

        tv_rum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rumVisible) {
                    // 목록이 이미 표시되어 있다면 숨김
                    recyclerRum.setVisibility(View.GONE);
                    rumVisible = false;
                } else {

                    // 목록이 표시되어 있지 않다면 표시
                    recyclerRum.setVisibility(View.VISIBLE);
                    rumVisible = true;
                    recyclerRum.setHasFixedSize(true); // 리사이클러뷰 기존성능 강화
                    layoutManager = new LinearLayoutManager(SearchActivity.this);
                    recyclerRum.setLayoutManager(layoutManager);
                    arrayList = new ArrayList<>(); // Media 객체를 담을 어레이 리스트
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            arrayList.clear(); // 기존 배열 초기화
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String category = snapshot.child("category").getValue(String.class);
                                if (category.equals("럼")) {
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
                    adapter = new AlcoholAdapter(arrayList, SearchActivity.this);
                    recyclerRum.setAdapter(adapter); // 리사이클러뷰에 어댑터 연결

                }
            }
        });

        tv_vodka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vodkaVisible) {
                    // 목록이 이미 표시되어 있다면 숨김
                    recyclerVodka.setVisibility(View.GONE);
                    vodkaVisible = false;
                } else {
                    // 목록이 표시되어 있지 않다면 표시
                    recyclerVodka.setVisibility(View.VISIBLE);
                    vodkaVisible = true;
                    recyclerVodka.setHasFixedSize(true); // 리사이클러뷰 기존성능 강화
                    layoutManager = new LinearLayoutManager(SearchActivity.this);
                    recyclerVodka.setLayoutManager(layoutManager);
                    arrayList = new ArrayList<>(); // Media 객체를 담을 어레이 리스트
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            arrayList.clear(); // 기존 배열 초기화
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String category = snapshot.child("category").getValue(String.class);
                                if (category.equals("보드카")) {
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
                    adapter = new AlcoholAdapter(arrayList, SearchActivity.this);
                    recyclerVodka.setAdapter(adapter); // 리사이클러뷰에 어댑터 연결

                }
            }
        });

        tv_jin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jinVisible) {
                    // 목록이 이미 표시되어 있다면 숨김
                    recyclerJin.setVisibility(View.GONE);
                    jinVisible = false;
                } else {
                    // 목록이 표시되어 있지 않다면 표시
                    recyclerJin.setVisibility(View.VISIBLE);
                    jinVisible = true;
                    recyclerJin.setHasFixedSize(true); // 리사이클러뷰 기존성능 강화
                    layoutManager = new LinearLayoutManager(SearchActivity.this);
                    recyclerJin.setLayoutManager(layoutManager);
                    arrayList = new ArrayList<>(); // Media 객체를 담을 어레이 리스트
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            arrayList.clear(); // 기존 배열 초기화
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String category = snapshot.child("category").getValue(String.class);
                                if (category.equals("진")) {
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
                    adapter = new AlcoholAdapter(arrayList, SearchActivity.this);
                    recyclerJin.setAdapter(adapter); // 리사이클러뷰에 어댑터 연결

                }
            }
        });

        tv_portwine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (portwineVisible) {
                    // 목록이 이미 표시되어 있다면 숨김
                    recyclerPortWine.setVisibility(View.GONE);
                    portwineVisible = false;
                } else {
                    // 목록이 표시되어 있지 않다면 표시
                    recyclerPortWine.setVisibility(View.VISIBLE);
                    portwineVisible = true;
                    recyclerPortWine.setHasFixedSize(true); // 리사이클러뷰 기존성능 강화
                    layoutManager = new LinearLayoutManager(SearchActivity.this);
                    recyclerPortWine.setLayoutManager(layoutManager);
                    arrayList = new ArrayList<>(); // Media 객체를 담을 어레이 리스트
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            arrayList.clear(); // 기존 배열 초기화
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String category = snapshot.child("category").getValue(String.class);
                                if (category.equals("포트 와인")) {
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
                    adapter = new AlcoholAdapter(arrayList, SearchActivity.this);
                        recyclerPortWine.setAdapter(adapter); // 리사이클러뷰에 어댑터 연결

                }
            }
        });

        tv_champagne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (champagneVisible) {
                    // 목록이 이미 표시되어 있다면 숨김
                    recyclerChampagne.setVisibility(View.GONE);
                    champagneVisible = false;
                } else {
                    // 목록이 표시되어 있지 않다면 표시
                    recyclerChampagne.setVisibility(View.VISIBLE);
                    champagneVisible = true;
                    recyclerChampagne.setHasFixedSize(true); // 리사이클러뷰 기존성능 강화
                    layoutManager = new LinearLayoutManager(SearchActivity.this);
                    recyclerChampagne.setLayoutManager(layoutManager);
                    arrayList = new ArrayList<>(); // Media 객체를 담을 어레이 리스트
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            arrayList.clear(); // 기존 배열 초기화
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String category = snapshot.child("category").getValue(String.class);
                                if (category.equals("샴페인")) {
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
                    adapter = new AlcoholAdapter(arrayList, SearchActivity.this);
                    recyclerChampagne.setAdapter(adapter); // 리사이클러뷰에 어댑터 연결

                }
            }
        });

        tv_makgeolli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (makgeolliVisible) {
                    // 목록이 이미 표시되어 있다면 숨김
                    recyclerMakgeolli.setVisibility(View.GONE);
                    makgeolliVisible = false;
                } else {
                    // 목록이 표시되어 있지 않다면 표시
                    recyclerMakgeolli.setVisibility(View.VISIBLE);
                    makgeolliVisible = true;
                    recyclerMakgeolli.setHasFixedSize(true); // 리사이클러뷰 기존성능 강화
                    layoutManager = new LinearLayoutManager(SearchActivity.this);
                    recyclerMakgeolli.setLayoutManager(layoutManager);
                    arrayList = new ArrayList<>(); // Media 객체를 담을 어레이 리스트
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            arrayList.clear(); // 기존 배열 초기화
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String category = snapshot.child("category").getValue(String.class);
                                if (category.equals("막걸리")) {
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
                    adapter = new AlcoholAdapter(arrayList, SearchActivity.this);
                    recyclerMakgeolli.setAdapter(adapter); // 리사이클러뷰에 어댑터 연결

                }
            }
        });

        iv_backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}