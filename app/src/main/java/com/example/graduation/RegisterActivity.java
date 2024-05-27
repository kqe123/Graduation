package com.example.graduation;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth; // 데이터베이스 인증처리
    private DatabaseReference mDatabaseRef; // 실시간 DB
    private EditText mPwd, mNick, mEmail;
    private RadioGroup mGender;
    private View mBtnRegister;
    TextView sweet, bitter, dg,pi,cn;
    int sweetness = -1, bitterness = -1; // 사용자 취향정보(단맛, 쓴맛)
    String degree,price,recom1st,recom2nd,recom3rd;     // 사용자 취향정보(도수, 가격)
    Boolean carbonated;       // 사용자 취향정보(탄산)

    private ActivityResultLauncher<Intent> mPreContractStartActivityResult =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult a_result) {
                            if (a_result.getResultCode() == Activity.RESULT_OK) {
                                Intent data = a_result.getData(); // 인텐트 전달받음.
                                // 취향 설정한 정보 5가지를 가져온다.
                                sweetness = data.getIntExtra("sweetness",0);
                                bitterness = data.getIntExtra("bitterness",0);
                                degree = data.getStringExtra("degree");
                                price = data.getStringExtra("price");
                                carbonated = data.getBooleanExtra("carbonated", true);
                                recom1st = data.getStringExtra("recom1st");
                                recom2nd = data.getStringExtra("recom2nd");
                                recom3rd = data.getStringExtra("recom3rd");

                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Graduation");

        mEmail = findViewById(R.id.edtEmail);
        mPwd = findViewById(R.id.edtPassword);
        mNick = findViewById(R.id.edtNickname);
        mGender = findViewById(R.id.rgroup1);
        mBtnRegister = findViewById(R.id.btnRegister);

        //스피너 설정
        final String[] age = {"20대", "30대","40대","50대 이상"};
        Spinner spinner = (Spinner) findViewById(R.id.spinAge);

        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, age);
        spinner.setAdapter(adapter);

        //취향 설정을 위해 액티비티 이동
        Button goLikingBtn = findViewById(R.id.btn_go_liking);
        goLikingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, Register2Activity.class);
                mPreContractStartActivityResult.launch(intent);
            }
        });


        //회원가입 버튼 클릭시
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 회원가입 처리 시작
                String strEmail = mEmail.getText().toString();
                String strPwd = mPwd.getText().toString();
                String strNick = mNick.getText().toString();
                int selectedGenderId = mGender.getCheckedRadioButtonId();

                // 아이디, 비번, 닉네임, 성별, 취향정보중 하나라도 빠지면 회원가입 x하고 경고 메시지
                if (sweetness == -1 || strEmail == "" || strPwd == "" || strNick == "" || selectedGenderId == -1) {
                    Toast.makeText(getApplicationContext(), "모든 정보를 입력해주세요!", Toast.LENGTH_SHORT).show();
                } else {
                    //Firebase 인증 진행
                    mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // 유저 객체 만들고
                                FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                                UserAccount account = new UserAccount();
                                account.setIdToken(firebaseUser.getUid());
                                account.setEmailId(firebaseUser.getEmail());
                                account.setPassword(strPwd);
                                account.setNickname(strNick);
                                switch (mGender.getCheckedRadioButtonId()) {
                                    case R.id.rdoMale:
                                        account.setGender("남성");
                                        break;
                                    case R.id.rdoFemale:
                                        account.setGender("여성");
                                        break;
                                }
                                switch (spinner.getSelectedItem().toString()) {
                                    case "20대":
                                        account.setAge("20대");
                                        break;
                                    case "30대":
                                        account.setAge("30대");
                                        break;
                                    case "40대":
                                        account.setAge("40대");
                                        break;
                                    case "50대 이상":
                                        account.setAge("50대 이상");
                                        break;
                                }

                                account.setDegree(degree);
                                account.setPrice(price);
                                account.setCarbonated(carbonated);
                                account.setBitterness(bitterness);
                                account.setSweetness(sweetness);
                                account.setRecom1st(recom1st);
                                account.setRecom2nd(recom2nd);
                                account.setRecom3rd(recom3rd);
                                // setvalue() : db insert 기능임.
                                mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);
                                Toast.makeText(RegisterActivity.this, "회원가입에 성공하였습니다", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "이미 있는 이메일입니다. 다른 이메일을 입력해주세요!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
    }
}