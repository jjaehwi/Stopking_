package com.sample.stopking_project;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;         // 파이어베이스 인증
    public EditText mEtEmail, mEtPwd;  //로그인 입력필드
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // 파이어스토어

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);

        Button btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 르그인 요청
                String strEmail = mEtEmail.getText().toString();
                String strPwd = mEtPwd.getText().toString();

                if (mEtEmail.getText().toString().equals("") || mEtEmail.getText().toString() == null) {
                    // 아이디 입력 안 했을 경우.
                    Toast.makeText(LoginActivity.this, "ID를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (mEtPwd.getText().toString().equals("") || mEtPwd.getText().toString() == null) {
                    // 비밀번호 입력 안 했을 경우.
                    Toast.makeText(LoginActivity.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.signInWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) { // 로그인이 성공적으로 이루어졌다면 ..
                                // TODO: 정보에 따라 처음 메인액티비티 띄우기
                                    Intent intent = new Intent(LoginActivity.this, LoadingActivity.class);
                                    startActivity(intent);
                                    finish(); // 현재 액티비티 파괴
                                    Toast.makeText(LoginActivity.this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                // 로그인 실패
                                Toast.makeText(LoginActivity.this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        Button btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 회원가입 화면으로 이동
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //로그인이 되어있다면 해당 액티비티로 바로 이동한다.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser !=null){
            Intent intent = new Intent(LoginActivity.this, LoadingActivity.class);
            startActivity(intent);
            finish(); // 현재 액티비티 파괴
//        String getEmail = currentUser.getEmail();
//        DocumentReference docRef = db.collection("users").document(getEmail);
//        docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
//                if (documentSnapshot.getString("flag").compareTo("drink") == 0) {
//                    //로그인 상태가 되어있다면 금주 메인 액티비티로 이동.
//                    Intent intent = new Intent(LoginActivity.this, Drink_MainActivity.class);
//                    startActivity(intent);
//                    finish(); // 현재 액티비티 파괴
//                    Toast.makeText(LoginActivity.this, "기존 계정으로 로그인되었습니다.", Toast.LENGTH_SHORT).show();
//                }
//                else if (documentSnapshot.getString("flag").compareTo("smoke") == 0){
//                    // 금연 액티비티로 이동
//                    Intent intent = new Intent(LoginActivity.this, Smoke_MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                    Toast.makeText(LoginActivity.this, "기존 계정으로 로그인되었습니다.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
        }
    }
}