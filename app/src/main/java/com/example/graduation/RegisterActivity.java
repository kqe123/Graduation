package com.example.graduation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
        final String[] age = {"10대","20대", "30대","40대","50대 이상"};
        Spinner spinner = (Spinner) findViewById(R.id.spinAge);

        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, age);
        spinner.setAdapter(adapter);



        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 회원가입 처리 시작
                String strEmail = mEmail.getText().toString();
                String strPwd = mPwd.getText().toString();
                String strNick = mNick.getText().toString();

                //Firebase 인증 진행
                mFirebaseAuth.createUserWithEmailAndPassword(strEmail,strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
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
                            switch(mGender.getCheckedRadioButtonId()) {
                                case R.id.rdoMale:
                                    account.setGender("남성");
                                    break;
                                case R.id.rdoFemale:
                                    account.setGender("여성");
                                    break;
                            }
                            switch(spinner.getSelectedItem().toString()) {
                                case "10대" : account.setAge("10대"); break;
                                case "20대" : account.setAge("20대"); break;
                                case "30대" : account.setAge("30대"); break;
                                case "40대" : account.setAge("40대"); break;
                                case "50대 이상" : account.setAge("50대 이상"); break;
                            }


                            // setvalue() : db insert 기능임.
                            mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);
                            Toast.makeText(RegisterActivity.this, "회원가입에 성공하였습니다", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "회원가입에 실패하였습니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }
}