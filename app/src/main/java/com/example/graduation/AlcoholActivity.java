package com.example.graduation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AlcoholActivity extends AppCompatActivity {
    TextView tv_name, tv_degree, tv_carbonated, tv_price, tv_category, tv_taste, tv_content, tv_review;
    ImageView iv_image, iv_backbtn;
    private FirebaseAuth mAuth;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Review> arrayList;

    private DatabaseReference databaseReference;
    String carbonated, category, content, taste, image; // db에서 찾은 값
    int price;    // db에서 찾은 값
    Float degree;  // db에서 찾은 값
    Boolean reviewVisible = false; // 댓글 목록 보여주지 말지 결정짓는 boolean 변수
    Float reviewSum = 0.0f; // 관련 술의 평점 총합
    int reviewCount = 0; // 관련 술의 리뷰수
    Button registerBtn; // 리뷰 등록버튼
    EditText et_reviewcontent; // 리뷰 내용
    RatingBar rb_score, rb_totalscore; // 별점
    String age, gender, nickname, userUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alcohol);

        tv_name = findViewById(R.id.name);
        tv_degree = findViewById(R.id.degree);
        tv_carbonated = findViewById(R.id.carbonated);
        tv_price = findViewById(R.id.price);
        tv_category = findViewById(R.id.category);
        tv_taste = findViewById(R.id.taste);
        tv_content = findViewById(R.id.content);
        iv_image = findViewById(R.id.image);
        tv_review = findViewById(R.id.reviewList);
        rb_totalscore = findViewById(R.id.totalScore);
        iv_backbtn = findViewById(R.id.backbtn);
        RecyclerView recyclerReview = findViewById(R.id.recyclerReview);
        registerBtn = findViewById(R.id.reviewbtn);
        et_reviewcontent = findViewById(R.id.reviewContent);
        rb_score = findViewById(R.id.score);

        // intent를 통해 클릭한 술의 이름을 가져온다.
        Intent intent = getIntent();
        String alcohol_name = intent.getStringExtra("name");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser(); // 현재 사용자 정보 가져옴
        userUid = user.getUid(); // 현재 사용자 uid 가져옴

        // 가져온 술의 이름을 기반으로 해당 DB안에 있는 술정보를 가져온다.
        databaseReference = FirebaseDatabase.getInstance().getReference("Graduation").child("Alcohol");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("name").getValue(String.class);
                    if (alcohol_name.equals(name)) {
                        image = snapshot.child("image").getValue(String.class);
                        category = snapshot.child("category").getValue(String.class);
                        degree = snapshot.child("degree").getValue(Float.class);
                        price = snapshot.child("price").getValue(int.class);
                        carbonated = snapshot.child("carbonated").getValue(String.class);
                        taste = snapshot.child("taste").getValue(String.class);
                        content = snapshot.child("content").getValue(String.class);
                        break;
                    }
                }
                Glide.with(AlcoholActivity.this).load(image).into(iv_image);
                tv_name.setText(alcohol_name);
                tv_category.setText(category);
                tv_degree.setText( Float.toString(degree) +"%");
                tv_price.setText("₩ " +Integer.toString(price));
                tv_carbonated.setText(carbonated);
                tv_taste.setText(taste);
                tv_content.setText(content);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // 평균점수와 댓글 개수 업데이트
        updateActivity(alcohol_name);


        // 뒤로가기 버튼
        iv_backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 리뷰 등록버튼
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference = FirebaseDatabase.getInstance().getReference("Graduation").child("UserAccount").child(userUid);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            age = snapshot.child("age").getValue(String.class);
                            gender = snapshot.child("gender").getValue(String.class);
                            nickname = snapshot.child("nickname").getValue(String.class);
                            if(et_reviewcontent.getText().toString().length() > 4) {
                                Review review = new Review();
                                review.setAlcohol_name(alcohol_name);
                                review.setScore(rb_score.getRating());
                                review.setAge(age);
                                review.setGender(gender);
                                review.setNickname(nickname);
                                review.setContent(et_reviewcontent.getText().toString());
                                DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Graduation").child("Review");
                                dRef.push().setValue(review).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        updateActivity(alcohol_name);
                                        Toast.makeText(getApplicationContext(), "리뷰가 성공적으로 등록되었습니다!", Toast.LENGTH_SHORT).show();
                                        et_reviewcontent.setText("");
                                    }
                                });
                            }
                            else {
                                Toast.makeText(getApplicationContext(),"오류! 5자이상 리뷰를 작성해주세요!", Toast.LENGTH_SHORT).show();
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        // 리뷰 목록 클릭시 술에 관한 리뷰 보여주기
        tv_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reviewVisible) {
                    // 목록이 이미 표시되어 있다면 숨김
                    recyclerReview.setVisibility(View.GONE);
                    reviewVisible = false;
                } else {
                    // 목록이 표시되어 있지 않다면 표시
                    recyclerReview.setVisibility(View.VISIBLE);
                    reviewVisible = true;
                    recyclerReview.setHasFixedSize(true); // 리사이클러뷰 기존성능 강화
                    layoutManager = new LinearLayoutManager(AlcoholActivity.this); // layoutManager는 하나의 RecyclerView하고만 연결될수 있으니 재사용하려면 매번 초기화해야됨.
                    recyclerReview.setLayoutManager(layoutManager);
                    arrayList = new ArrayList<>(); // Review 객체를 담을 어레이 리스트
                    databaseReference = FirebaseDatabase.getInstance().getReference("Graduation").child("Review");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            arrayList.clear(); // 기존 배열 초기화
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String get_alcohol = snapshot.child("alcohol_name").getValue(String.class);
                                if (alcohol_name.equals(get_alcohol)) {
                                    Review review = snapshot.getValue(Review.class); // 만들어뒀던 media 객체에 데이터를 담음.
                                    arrayList.add(review); // 담은 데이터들을 배열리스트에 넣고 리사이클뷰로 보낼 준비
                                }

                            }
                            adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    adapter = new ReviewAdapter(arrayList, AlcoholActivity.this);
                    recyclerReview.setAdapter(adapter); // 리사이클러뷰에 어댑터 연결
                }
            }
        });



    }
    // 리뷰가 등록되면 평균평점과 리뷰 개수를 업데이트하는 메서드
    public void updateActivity(String alcohol_name) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Graduation").child("Review");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reviewSum = 0.0f;
                reviewCount = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String get_alcohol = snapshot.child("alcohol_name").getValue(String.class);
                    if (alcohol_name.equals(get_alcohol)) {
                        Float get_score = snapshot.child("score").getValue(Float.class);
                        reviewCount += 1;
                        reviewSum += get_score;
                    }
                }
                if(reviewCount == 0) {
                    rb_totalscore.setRating(0.0f);
                }
                else {
                    rb_totalscore.setRating(reviewSum/reviewCount);
                }
                tv_review.setText(Integer.toString(reviewCount)+"개의 댓글 목록");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}