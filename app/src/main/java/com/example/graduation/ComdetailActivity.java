package com.example.graduation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ComdetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Comment> arrayList;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private EditText comment;
    private View btnComment;
    private String nickname, age, gender;
    TextView tv_comment, tv_title, tv_content, tv_create_date;
    Boolean comment_visible = false;
    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comdetail);

        recyclerView = findViewById(R.id.answer);
        recyclerView.setNestedScrollingEnabled(false);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();
        comment = findViewById(R.id.commentEditText);
        btnComment = findViewById(R.id.button_post_comment);
        tv_comment = findViewById(R.id.tv_comment);
        tv_title = findViewById(R.id.title);
        tv_content = findViewById(R.id.content);
        tv_create_date = findViewById(R.id.create_date);
        backBtn = findViewById(R.id.backbtn);

        // 인텐트에서 게시물의 ID를 가져옴
        String title = getIntent().getStringExtra("title");

        // Firebase 데이터베이스 참조
        databaseReference = FirebaseDatabase.getInstance().getReference("Graduation").child("Comment");

        // 게시물 내용 가져와서 표시
        fetchPostContent(title);

        // 댓글 데이터 가져와서 리스트에 추가
        fetchComments(title);

        // 댓글 추가 버튼 클릭 이벤트 처리
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment(title);
            }
        });

        tv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comment_visible) {
                    recyclerView.setVisibility(View.GONE);
                    comment_visible = false;
                }
                else {
                    recyclerView.setVisibility(View.VISIBLE);
                    comment_visible = true;
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ComdetailActivity.this, CommunityActivity.class);
                startActivity(intent);
                finish(); // 현재 액티비티 삭제
            }
        });

    }

    // 게시물 내용을 가져와서 표시하는 메서드
    private void fetchPostContent(String title) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Graduation").child("Community");
        postRef.orderByChild("title").equalTo(title).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String content = snapshot.child("content").getValue(String.class);
                        String title = snapshot.child("title").getValue(String.class);
                        String create_date = snapshot.child("create_date").getValue(String.class);
                        tv_content.setText(content);
                        tv_title.setText(title);
                        tv_create_date.setText(create_date);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ComdetailActivity.this, "게시물 내용을 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }



    // 댓글 데이터를 가져와서 리스트에 추가하는 메서드
    private void fetchComments(String title) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int commentCount = 0;
                arrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String get_title = snapshot.child("title").getValue(String.class);
                    if (title.equals(get_title)) {
                        Comment comment = snapshot.getValue(Comment.class);
                        commentCount += 1;
                        arrayList.add(comment);
                    }
                }
                adapter.notifyDataSetChanged();
                tv_comment.setText( "> " +Integer.toString(commentCount)+"개의 댓글 목록" );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ComdetailActivity.this, "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 리사이클러뷰 어댑터 설정
        adapter = new CommentAdapter(arrayList, ComdetailActivity.this);
        recyclerView.setAdapter(adapter);
    }

    // 댓글 추가 메서드
    private void addComment(String title) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser(); // 현재 사용자 정보 가져옴.
        if (user != null) {
            String userUid = user.getUid();
            DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Graduation").child("UserAccount").child(userUid);
            dRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        nickname = snapshot.child("nickname").getValue(String.class);
                        age = snapshot.child("age").getValue(String.class);
                        gender = snapshot.child("gender").getValue(String.class);

                        // nickname을 가져온 후에 댓글 추가 코드 실행
                        String strComment = comment.getText().toString();
                        Comment upload_comment = new Comment();
                        upload_comment.setTitle(title);
                        upload_comment.setContent(strComment);
                        upload_comment.setUserName(nickname);
                        upload_comment.setAge(age);
                        upload_comment.setGender(gender);

                        
                        // Firebase 데이터베이스에 댓글 추가
                        databaseReference.push().setValue(upload_comment)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ComdetailActivity.this, "댓글이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                                        comment.setText(""); // 댓글 입력창 비우기
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ComdetailActivity.this, "다시 입력해주세요", Toast.LENGTH_SHORT).show();
                                    }
                                });

                        // 댓글 추가 후에 리스트를 업데이트
                        fetchComments(title);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else {
            // 사용자가 로그인되어 있지 않으면 로그인 화면으로 이동하도록 할 수 있습니다.
            // 예시: startActivity(new Intent(ComdetailActivity.this, LoginActivity.class));
            Toast.makeText(ComdetailActivity.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
