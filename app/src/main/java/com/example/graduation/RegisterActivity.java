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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    private EditText mPwd, mNick, mEmail;
    private RadioGroup mGender;
    private View mBtnRegister;
    private LinearLayout mRemainingInputLayout;
    private TextView mTvEnterEmailPassword; // 새로 추가된 TextView
    int sweetness = -1, bitterness = -1;
    String degree, price;
    Boolean carbonated;

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
        mRemainingInputLayout = findViewById(R.id.remaining_input_layout);
        mTvEnterEmailPassword = findViewById(R.id.tvEnterEmailPassword); // 텍스트뷰 연결

        final String[] age = {"20대", "30대", "40대", "50대 이상"};
        Spinner spinner = findViewById(R.id.spinAge);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, age);
        spinner.setAdapter(adapter);

        Button goLikingBtn = findViewById(R.id.btn_go_liking);
        goLikingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, Register2Activity.class);
                mPreContractStartActivityResult.launch(intent);
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = mEmail.getText().toString().trim();
                String password = mPwd.getText().toString().trim();
                if (!email.isEmpty() && !password.isEmpty()) {
                    mRemainingInputLayout.setVisibility(View.VISIBLE);
                    mTvEnterEmailPassword.setVisibility(View.GONE); // 텍스트뷰 숨김
                } else {
                    mRemainingInputLayout.setVisibility(View.GONE);
                    mTvEnterEmailPassword.setVisibility(View.VISIBLE); // 텍스트뷰 표시
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        };

        mEmail.addTextChangedListener(textWatcher);
        mPwd.addTextChangedListener(textWatcher);

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strEmail = mEmail.getText().toString();
                String strPwd = mPwd.getText().toString();
                String strNick = mNick.getText().toString();
                int selectedGenderId = mGender.getCheckedRadioButtonId();

                if (sweetness == -1 || strEmail.isEmpty() || strPwd.isEmpty() || strNick.isEmpty() || selectedGenderId == -1) {
                    Toast.makeText(getApplicationContext(), "모든 정보를 입력해주세요!", Toast.LENGTH_SHORT).show();
                } else {
                    mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
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

        ImageView backBtn = findViewById(R.id.backbtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // 현재 액티비티를 종료하고 이전 액티비티로 돌아갑니다.
            }
        });
    }
}
