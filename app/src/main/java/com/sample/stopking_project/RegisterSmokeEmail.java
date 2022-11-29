package com.sample.stopking_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterSmokeEmail extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // 파이어스토어
    private FirebaseAuth mAuth;                                      // 파이어베이스 인증
    private EditText mEtEmail, mEtPwd;
    private Button mbtnComplete;                                     // 회원가입 버튼
    private Button mbtnCancel;                                       // 뒤로가기 버튼
    private FirebaseUser firebaseUser;
    private String selectDate, getName;
    private String avgSmoke, startSmoke, smokeBank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_smoke_email);

        mAuth = FirebaseAuth.getInstance();

        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);
        mbtnComplete = findViewById(R.id.btn_complete);
        mbtnCancel = findViewById(R.id.btn_cancel);

        Intent intent = getIntent(); //전달할 데이터를 받을 intent
        getName = intent.getStringExtra("name");        // 이름
        selectDate = intent.getStringExtra("stopDate"); //  금주, 금연 날짜
        avgSmoke = intent.getStringExtra("avgSmoke");   // 한 번에 먹는 술 병
        startSmoke = intent.getStringExtra("startSmoke"); //일주일에 마시는 술 횟수
        smokeBank = intent.getStringExtra("smokeBank"); // 금주저금통

        //취소 시 이전 회원가입 화면으로 다시 이동.
        mbtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //회원가입 완료 후 시작
        mbtnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mEtEmail.getText().toString().equals("") || mEtEmail.getText().toString() == null)
                {
                    //email 입력을 안 했을 경우.
                    Toast.makeText(RegisterSmokeEmail.this, "이메일 주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(mEtPwd.getText().toString().equals("") || mEtPwd.getText().toString() == null)
                {
                    //비밀번호 입력을 안 했을 경우.
                    Toast.makeText(RegisterSmokeEmail.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if (mEtPwd.getText().toString().length() < 6)
                {
                    //비밀번호가 6자리 이하일 경우
                    Toast.makeText(RegisterSmokeEmail.this, "비밀번호를 최소 6글자 이상 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String strEmail = mEtEmail.getText().toString();
                    String strPwd = mEtPwd.getText().toString();
                    // 일주일에 마시는 총 갑수
                    String intAvgSmoke = avgSmoke.replaceAll("[^0-9]", "");

                    mAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(RegisterSmokeEmail.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                //회원가입 성공
                                firebaseUser = mAuth.getCurrentUser();
                                mAuth.signOut();

                                //로그인부터 액티비티 초기화 후 다시 시작
                                Intent intent = new Intent(RegisterSmokeEmail.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            else { // 회원가입 실패한 경우
                                Toast.makeText(RegisterSmokeEmail.this, "다른 아이디로 회원가입을 진행해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    HashMap<Object, Object> user = new HashMap<>();
                    user.put("email", strEmail);
                    user.put("name", getName);
                    //금주 정보
                    user.put("average_drink", null);
                    user.put("week_drink", null);
                    user.put("drink_bank", null);
                    user.put("stop_drink", null);
                    user.put("week_bottle",null);

                    //금연 정보
                    user.put("week_smoke", intAvgSmoke);
                    user.put("start_smoke", startSmoke);
                    user.put("smoke_bank", smokeBank);
                    user.put("stop_smoke", selectDate);
                    user.put("flag","smoke");

                    //문서 추가
                    db.collection("users").document(strEmail).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //사용자 정보 파이어베이스에 넣기 성공
                                    Toast.makeText(RegisterSmokeEmail.this, "회원가입에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //사용자 정보 파이어베이스에 넣기 실패 시
                                    Toast.makeText(RegisterSmokeEmail.this, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

    }
}