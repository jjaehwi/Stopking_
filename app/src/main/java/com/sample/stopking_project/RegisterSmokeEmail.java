package com.sample.stopking_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private TextView emailCheck, completeEmail;
    private Button mbtnComplete, mbtnCheck;                                     // 회원가입 버튼
    private Button mbtnCancel;                                       // 뒤로가기 버튼
    private FirebaseUser fbUser;
    private String selectDate, getName, intAvgSmoke;
    private String avgSmoke, startSmoke, smokeBank;
    private String strEmail, strPwd;
    private int loginFlag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_smoke_email);

        mAuth = FirebaseAuth.getInstance();

        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);
        mbtnComplete = findViewById(R.id.btn_complete);
        mbtnCancel = findViewById(R.id.btn_cancel);
        mbtnCheck = findViewById(R.id.button_checkEmail);
        emailCheck = findViewById(R.id.tv_emailCheck);
        completeEmail = findViewById(R.id.tv_completeEmail);

        Intent intent = getIntent(); //전달할 데이터를 받을 intent
        getName = intent.getStringExtra("name");        // 이름
        selectDate = intent.getStringExtra("stopDate"); //  금주, 금연 날짜
        avgSmoke = intent.getStringExtra("avgSmoke");   // 한 번에 먹는 술 병
        startSmoke = intent.getStringExtra("startSmoke"); //일주일에 마시는 술 횟수
        smokeBank = intent.getStringExtra("smokeBank"); // 금주저금통

        mbtnCheck.setOnClickListener(new View.OnClickListener() {
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
                    strEmail = mEtEmail.getText().toString();
                    strPwd = mEtPwd.getText().toString();
                    // 일주일에 마시는 총 갑수
                    intAvgSmoke = avgSmoke.replaceAll("[^0-9]", "");

                    mAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(RegisterSmokeEmail.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                //회원가입 성공
                                fbUser = mAuth.getCurrentUser();

                                //사용자 인증 메일 보내기
                                fbUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                loginFlag=1;
                                                //인증 메일이 성공적으로 보내진 경우.
                                                Toast.makeText(RegisterSmokeEmail.this, "이메일을 성공적으로 전송하였습니다. 인증을 진행해주세요.", Toast.LENGTH_SHORT).show();
                                                mbtnCheck.setText("진행중..");
                                                emailCheck.setVisibility(View.VISIBLE);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(RegisterSmokeEmail.this, "해당 이메일은 존재하지 않습니다. 다시 시도하세요.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                            else { // 회원가입 실패한 경우
                                if (loginFlag < 1)
                                    Toast.makeText(RegisterSmokeEmail.this, "다른 아이디로 회원가입을 진행해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    if (loginFlag == 1)
                    {
                        mAuth.signInWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful())
                                {
                                    //인증 성공인지 check
                                    if (fbUser.isEmailVerified())
                                    {
                                        loginFlag =2;
                                        completeEmail.setVisibility(View.VISIBLE);
                                        //이메일 인증되었을 때
                                        mbtnCheck.setText("완료");
                                        Toast.makeText(RegisterSmokeEmail.this, "이메일 인증 성공!", Toast.LENGTH_SHORT).show();

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
                                                        Log.d("CHECKDB", "db 데이터 넣기"+ "성공");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        //사용자 정보 파이어베이스에 넣기 실패 시
                                                        Log.d("CHECKDB", "db 데이터 넣기"+ "실패");
                                                    }
                                                });
                                    }
                                    else
                                    {
                                        Toast.makeText(RegisterSmokeEmail.this, "이메일 인증을 해주세요.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });

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
                if (loginFlag == 2)
                {
                    mAuth.signOut();

                    //로그인부터 액티비티 초기화 후 다시 시작
                    Intent intent = new Intent(RegisterSmokeEmail.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    Toast.makeText(RegisterSmokeEmail.this, "회원가입에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(RegisterSmokeEmail.this, "이메일 인증을 진행해주세요!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}