package com.sample.stopking_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText mEtEmail, mEtPwd, mEtName;  //회원가입 입력필드
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // 파이어스토어
    private FirebaseAuth mAuth;                                      // 파이어베이스 인증
    private Button mbtnRegister;        //회원가입 버튼
    private Button mbtnBack;            // 뒤로가기 버튼
    private CheckBox cDrink, cSmoke;    //금연, 금주 선택

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);
        mbtnRegister = findViewById(R.id.btn_register);
        mEtName = findViewById(R.id.et_name);
        mbtnBack = findViewById(R.id.btn_back);

        cDrink = findViewById(R.id.check_drink);
        cSmoke = findViewById(R.id.check_smoke);

        mbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //뒤로 가기 버튼이 눌렸을 때
                Toast.makeText(RegisterActivity.this, "회원가입이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        mbtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mEtEmail.getText().toString().equals("") || mEtEmail.getText().toString() == null)
                {
                    //email 입력을 안 했을 경우.
                    Toast.makeText(RegisterActivity.this, "이메일 주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(mEtPwd.getText().toString().equals("") || mEtPwd.getText().toString() == null)
                {
                    //비밀번호 입력을 안 했을 경우.
                    Toast.makeText(RegisterActivity.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if (mEtPwd.getText().toString().length() < 6)
                {
                    //비밀번호가 6자리 이하일 경우
                    Toast.makeText(RegisterActivity.this, "비밀번호를 최소 6글자 이상 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(mEtName.getText().toString().equals("") || mEtName.getText().toString() == null)
                {
                    //이름 입력을 안 했을 경우.
                    Toast.makeText(RegisterActivity.this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if (cDrink.isChecked() && cSmoke.isChecked()) {
                    // 금연, 금주 모두 체크한 경우
                    Toast.makeText(RegisterActivity.this, "금연/금주 중 하나만 선택해주세요.", Toast.LENGTH_SHORT).show();
                } else if ((!cDrink.isChecked()) && !(cSmoke.isChecked())) {
                    // 금연, 금주 모두 체크하지 않은 경우
                    Toast.makeText(RegisterActivity.this, "금연/금주 중 하나를 선택해주세요.", Toast.LENGTH_SHORT).show();
                } else
                {
                    //이미 이메일 아이디가 존재하는지 확인.
                    //파이어베이스 인증 진행 및 신규 계정 등록하기.
                    // 금연, 금주 중 하나만 체크한 경우
                    //회원가입 처리 시작(기입한 회원 정보를 가져온다.)

                    String strEmail = mEtEmail.getText().toString();
                    String strPwd = mEtPwd.getText().toString();
                    String strName = mEtName.getText().toString();

                    mAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // 금연, 금주 중 하나만 체크한 경우
                            //회원가입 처리 시작(기입한 회원 정보를 가져온다.)
                            // task는 회원가입의 결과를 return
                            // 인증 처리가 완료되었을 때. 즉 가입 성공 시
                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                String email = firebaseUser.getEmail();
                                String uid = firebaseUser.getUid();

                                //문서 저장한 후 금연/금주 파트에 맞게 액티비티 이동.
                                if (cDrink.isChecked() && (!cSmoke.isChecked())) {
                                    // 금주 파트를 선택했을 때
                                    Intent intent = new Intent(RegisterActivity.this, RegisterDrink.class);
                                    intent.putExtra("email", strEmail); // email값 전달
                                    intent.putExtra("pwd", strPwd);     // password값 전달
                                    intent.putExtra("name", strName);   // name값 전달
                                    startActivity(intent);
                                    finish();
                                } else if ((!cDrink.isChecked()) && cSmoke.isChecked()) {
                                    // 금연 파트를 선택했을 때
                                    Intent intent = new Intent(RegisterActivity.this, RegisterSmoke.class);
                                    intent.putExtra("email", strEmail); // email값 전달
                                    intent.putExtra("pwd", strPwd);     // password값 전달
                                    intent.putExtra("name", strName);   // name값 전달
                                    startActivity(intent);
                                    finish();
                                }
                            } // 회원가입 성공.
                            else { // 회원가입 실패한 경우
                                Toast.makeText(RegisterActivity.this, "다른 아이디로 회원가입을 진행해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}