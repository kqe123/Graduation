package com.example.graduation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MypageActivity extends AppCompatActivity {
    ImageView homeBtn, cameraBtn, mediaBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    TextView tv_emailId,tv_nickname,tv_gender,tv_age;   // 사용자 취향정보
    TextView tv_degree,tv_price,tv_sweetness,tv_bitterness,tv_carbonated; // 사용자 취향정보
    Button changePwBtn, changeLikingBtn, removeUserBtn; // 3개 버튼
    EditText edtRemove, edtnewPw, edtnewPwConfirm; // 삭제 텍스트, 새 비번, 새 비번확인
    RadioGroup rgroupDegree, rgroupPrice, rgroupCarbonated;
    RatingBar sweet, bitter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        homeBtn = findViewById(R.id.homebtn);
        cameraBtn = findViewById(R.id.camerabtn);
        mediaBtn = findViewById(R.id.mediabtn);
        tv_emailId = findViewById(R.id.emailID);
        tv_nickname = findViewById(R.id.nickname);
        tv_gender = findViewById(R.id.gender);
        tv_age = findViewById(R.id.age);
        tv_degree = findViewById(R.id.degree);
        tv_price = findViewById(R.id.price);
        tv_sweetness = findViewById(R.id.sweetness);
        tv_bitterness = findViewById(R.id.bitterness);
        tv_carbonated = findViewById(R.id.carbonated);
        changePwBtn = findViewById(R.id.changePassword);
        changeLikingBtn = findViewById(R.id.changeLiking);
        removeUserBtn = findViewById(R.id.removeUser);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Graduation");
        FirebaseUser user = mAuth.getCurrentUser(); // 현재 사용자 정보 가져옴

        //동작1 : 현재 로그인된 계정의 정보를 가져온다!
        if (user != null) {
            String userUid = user.getUid(); // 현재 유저의 UID를 가져옴
            //Graduation->UserAccount->해당 UID를 가리켜서(참조) 해당 UID가 존재한다면 그 유저의 정보를 가져오고, 없다면 에러 처리
            DatabaseReference userRef = databaseReference.child("UserAccount").child(userUid);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String emailID = snapshot.child("emailId").getValue(String.class);
                        String nickname = snapshot.child("nickname").getValue(String.class);
                        String gender = snapshot.child("gender").getValue(String.class);
                        String age = snapshot.child("age").getValue(String.class);
                        String degree = snapshot.child("degree").getValue(String.class);
                        String price = snapshot.child("price").getValue(String.class);
                        int sweetness = snapshot.child("sweetness").getValue(int.class);
                        int bitterness = snapshot.child("bitterness").getValue(int.class);
                        Boolean carbonated = snapshot.child("carbonated").getValue(Boolean.class);

                        tv_emailId.setText(emailID);
                        tv_nickname.setText(nickname);
                        tv_gender.setText(gender);
                        tv_age.setText(age);
                        tv_degree.setText(degree);
                        tv_price.setText(price);
                        tv_sweetness.setText(Integer.toString(sweetness));
                        tv_bitterness.setText(Integer.toString(bitterness));
                        if(carbonated) { tv_carbonated.setText("O");}
                            else { tv_carbonated.setText("X"); }

                    } else {
                        Toast.makeText(getApplicationContext(),"현재 로그인이 안되어있습니다!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        //비밀번호 변경 버튼
        changePwBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialogView = (View) View.inflate(MypageActivity.this, R.layout.user_changepw, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(MypageActivity.this);
                dlg.setTitle("비밀번호 변경");
                dlg.setIcon(R.drawable.a_mybtn);
                dlg.setView(dialogView);
                dlg.setPositiveButton("수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        edtnewPw = dialogView.findViewById(R.id.newPw);
                        edtnewPwConfirm = dialogView.findViewById(R.id.newPwConfirm);

                        // 새 비밀번호, 새 비밀번호 확인이 모두 공백인 경우 -> 아무동작 x
                        if (edtnewPw.getText().toString().isEmpty() || edtnewPwConfirm.getText().toString().isEmpty()) {
                            Toast.makeText(getApplicationContext(),"오류! 두 입력창 모두 입력해주세요!", Toast.LENGTH_LONG).show();
                        }
                        // 새 비밀번호, 새 비밀번호 확인이 같은 경우
                        else if(Objects.equals(edtnewPw.getText().toString(),edtnewPwConfirm.getText().toString())) {
                            String newPassword = edtnewPw.getText().toString();
                            user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        String userUid = user.getUid(); // 현재 유저의 UID를 가져옴
                                        DatabaseReference userRef = databaseReference.child("UserAccount").child(userUid).child("password");
                                        userRef.setValue(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(getApplicationContext(), "비밀번호가 변경되었습니다!", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "비밀번호가 변경되었습니다!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(),"오류! 비밀번호 6자 이상으로 설정해주세요!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                        // 새 비밀번호, 새 비밀번호 확인이 다른 경우
                        else {
                            Toast.makeText(getApplicationContext(),"오류! 비밀번호와 비밀번호 확인이 일치하지 않습니다!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                dlg.setNegativeButton("취소",null);
                dlg.show();
            }
        });


        //계정삭제 버튼
        removeUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialogView = (View) View.inflate(MypageActivity.this, R.layout.user_remove, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(MypageActivity.this);
                dlg.setTitle("회원 탈퇴");
                dlg.setIcon(R.drawable.a_mybtn);
                dlg.setView(dialogView);
                dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        edtRemove = dialogView.findViewById(R.id.edtRemove);
                        if ("remove".equals(edtRemove.getText().toString())) {
                            //1. user.delete() : authentication에서 사용자 삭제
                            //addOnCompleteListener() : 해당 작업이 성공적으로 완수되었을시 작동하는 리스너
                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        String userUid = user.getUid(); // 현재 유저의 UID를 가져옴
                                        DatabaseReference userRef = databaseReference.child("UserAccount").child(userUid);
                                        userRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(getApplicationContext(),"회원 탈퇴가 성공적으로 완수되었습니다.",Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(MypageActivity.this, StartActivity.class);
                                                startActivity(intent);
                                                finishAffinity();
                                                }
                                            })
                                        .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getApplicationContext(),"회원 탈퇴가 성공적으로 완수되었습니다.",Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(MypageActivity.this, StartActivity.class);
                                                    startActivity(intent);
                                                    finishAffinity();
                                                }
                                            });

                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(),"오류! authentication에서 유저가 삭제되지 않음!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            });
                        }
                        else {

                        }
                    }
                });
                dlg.setNegativeButton("아니오", null);
                dlg.show();
            }
        });

        // 취향변경 버튼
        changeLikingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialogView = (View) View.inflate(MypageActivity.this, R.layout.user_changeliking, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(MypageActivity.this);
                dlg.setTitle("취향정보 변경");
                dlg.setIcon(R.drawable.a_mybtn);
                dlg.setView(dialogView);
                dlg.setPositiveButton("설정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        rgroupDegree = dialogView.findViewById(R.id.rgroupDegree);
                        rgroupPrice = dialogView.findViewById(R.id.rgroupPrice);
                        rgroupCarbonated = dialogView.findViewById(R.id.rgroupCarbonated);
                        sweet = dialogView.findViewById(R.id.sweetness);
                        bitter = dialogView.findViewById(R.id.bitterness);

                        int selectedDegreeId = rgroupDegree.getCheckedRadioButtonId();
                        int selectedPriceId = rgroupPrice.getCheckedRadioButtonId();
                        int selectedCarbonId = rgroupCarbonated.getCheckedRadioButtonId();
                        int sweetness, bitterness;
                        String degree = "", price = ""; Boolean carbonated = true;

                        if (selectedDegreeId == -1 || selectedPriceId == -1 || selectedCarbonId == -1) {
                            // 도수, 가격, 탄산여부 radiobutton이 아무것도 클릭되지 않았을때 동작
                            Toast.makeText(getApplicationContext(), "모든 질문에 답해주세요!", Toast.LENGTH_SHORT).show();
                        } else {
                            sweetness = (int) sweet.getRating(); // 현재 표시된 별값
                            bitterness = (int) bitter.getRating(); // (이하동문)

                            switch (selectedDegreeId) {
                                case R.id.rdoDegree10:
                                    degree = "10 이하";
                                    break;
                                case R.id.rdoDegree20:
                                    degree = "10~20";
                                    break;
                                case R.id.rdoDegree30:
                                    degree = "20~30";
                                    break;
                                case R.id.rdoDegree40:
                                    degree = "30~40";
                                    break;
                                case R.id.rdoDegree50:
                                    degree = "40~50";
                                    break;
                            }

                            switch (selectedPriceId) {
                                case R.id.rdoPrice5k:
                                    price = "1만원 이하";
                                    break;
                                case R.id.rdoPrice25k:
                                    price = "1~5만원";
                                    break;
                                case R.id.rdoPrice75k:
                                    price = "5~10만원";
                                    break;
                                case R.id.rdoPrice100k:
                                    price = "10만원 이상";
                                    break;
                            }

                            switch (selectedCarbonId) {
                                case R.id.rdoCarbon:
                                    carbonated = true;
                                    break;
                                case R.id.rdoNocarbon:
                                    carbonated = false;
                                    break;

                            }

                            String userUid = user.getUid(); // 현재 유저의 UID를 가져옴
                            DatabaseReference userRef = databaseReference.child("UserAccount").child(userUid).child("degree");
                            userRef.setValue(degree);
                            userRef = databaseReference.child("UserAccount").child(userUid).child("price");
                            userRef.setValue(price);
                            userRef = databaseReference.child("UserAccount").child(userUid).child("sweetness");
                            userRef.setValue(sweetness);
                            userRef = databaseReference.child("UserAccount").child(userUid).child("bitterness");
                            userRef.setValue(bitterness);
                            userRef = databaseReference.child("UserAccount").child(userUid).child("carbonated");
                            userRef.setValue(carbonated);
                            Toast.makeText(getApplicationContext(), "취향 정보가 변경되었습니다!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MypageActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        }
                    }
                });
                dlg.setNegativeButton("취소",null);
                dlg.show();
            }
        });



        //홈버튼
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MypageActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // 현재 액티비티 종료

            }
        });

        //카메라 버튼
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MypageActivity.this, CameraActivity.class);
                startActivity(intent);
                finish(); // 현재 액티비티 종료

            }
        });

        //미디어 버튼
        mediaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MypageActivity.this, MediaActivity.class);
                startActivity(intent);
                finish(); // 현재 액티비티 종료

            }
        });




    }
}