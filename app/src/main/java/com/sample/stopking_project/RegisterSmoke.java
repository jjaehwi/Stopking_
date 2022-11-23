package com.sample.stopking_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;

public class RegisterSmoke extends AppCompatActivity {

    private FirebaseAuth mAuth;                                      // 파이어베이스 인증
    private FirebaseFirestore db = FirebaseFirestore.getInstance();  // 파이어스토어
    private EditText mEtAvgSmoke, mEtStartSmoke, mEtSmokeBank;        // 금언 관련 회원 정보
    private Button mbtnDate;                                         // 금주 시작 날짜 버튼
    private Button mbtnComplete;                                     // 회원가입 버튼
    private Button mbtnCancel;                                       // 뒤로가기 버튼
    private String date;
    private String getEmail, getPwd, getName;
    DatePickerDialog dpd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_smoke);

        mAuth = FirebaseAuth.getInstance();

        mEtAvgSmoke = findViewById(R.id.et_avgSmoke);
        mEtStartSmoke = findViewById(R.id.et_startSmoke);
        mEtSmokeBank = findViewById(R.id.smoke_bank);

        mbtnDate = findViewById(R.id.btnDate);
        mbtnComplete = findViewById(R.id.btn_finish);
        mbtnCancel = findViewById(R.id.btn_cancel);

        Intent intent = getIntent(); //전달할 데이터를 받을 intent
        getEmail = intent.getStringExtra("email");
        getPwd = intent.getStringExtra("pwd");
        getName = intent.getStringExtra("name");

        //취소 시 로그인&회원가입 화면으로 다시 이동.
        mbtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 회원 탈퇴하기
                mAuth.getCurrentUser().delete();

                finish();
                Toast.makeText(RegisterSmoke.this, "회원가입이 취소되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        //금연 시작 날짜 버튼 클릭 처리
        mbtnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int nYear = c.get(Calendar.YEAR);
                int nMonth = c.get(Calendar.MONTH);
                int nDay = c.get(Calendar.DAY_OF_MONTH);
                dpd = new DatePickerDialog(RegisterSmoke.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        //1월은 0부터 시작하기 때문에 +1 해준다.
                        month = month + 1;
                        date = year + "/" + month + "/" + day;
                    }
                }, nYear, nMonth, nDay);
                dpd.show();
            }
        });

        //회원가입 완료 후 시작하기 버튼 클릭 처리
        mbtnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //예외 처리
                if (mEtAvgSmoke.getText().toString().equals("") || mEtAvgSmoke.getText().toString() == null) {
                    //평균 술 먹는 양을 입력 안 했을 경우.
                    Toast.makeText(RegisterSmoke.this, "1번 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(mEtAvgSmoke.getText().toString()) <= 0) {
                    //1번 항목에 0갑 이하를 입력했을 경우
                    Toast.makeText(RegisterSmoke.this, "1번 항목에서 최소 0.5갑 이상을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (mEtStartSmoke.getText().toString().equals("") || mEtStartSmoke.getText().toString() == null) {
                    //2번 항목 입력 안 했을 경우.
                    Toast.makeText(RegisterSmoke.this, "2번 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (mEtSmokeBank.getText().toString().equals("") || mEtSmokeBank.getText().toString() == null) {
                    //금주 저금통 입력을 안 했을 경우.
                    Toast.makeText(RegisterSmoke.this, "3번 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(mEtSmokeBank.getText().toString()) <= 4499) {
                    //금연 저금통 최소 금액이하로 목표 금액을 정했을 경우.
                    Toast.makeText(RegisterSmoke.this, "모으고 싶은 금액을 최소 4500원 이상으로 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (date == null) {
                    // 날짜 지정하지 않았을 경우.
                    Toast.makeText(RegisterSmoke.this, "금주 날짜를 선택해주세요.", Toast.LENGTH_SHORT).show();
                } else
                {
                    //avgDrink 정보, weekDrink 정보, drink_bank 정보가 파이어베이스 DB에 저장된다.
                    String avgSmoke = mEtAvgSmoke.getText().toString();
                    String startSmoke = mEtStartSmoke.getText().toString();
                    String smokeBank = mEtSmokeBank.getText().toString();

                    //파이어베이스 인증 진행 및 신규 계정 등록하기.
                    // task는 회원가입의 결과를 return
                    // 인증 처리가 완료되었을 때. 즉 가입 성공 시
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    String email = firebaseUser.getEmail();
                    String uid = firebaseUser.getUid();

                    HashMap<Object, String> user = new HashMap<>();
                    user.put("uid", uid);
                    user.put("email", email);
                    user.put("name", getName);
                    //금주 정보
                    user.put("average_drink", null);
                    user.put("week_drink", null);
                    user.put("drink_bank", null);
                    user.put("stop_drink", null);
                    //금연 정보
                    user.put("week_smoke", avgSmoke);
                    user.put("start_smoke", startSmoke);
                    user.put("smoke_bank", smokeBank);
                    user.put("stop_smoke", date);
                    user.put("flag","smoke");

                    //문서 추가
                    db.collection("users").document(email).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    // 회원가입 성공 및 사용자 정보 파이어베이스에 넣기 성공
                                    Toast.makeText(RegisterSmoke.this, "회원가입에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                                    mAuth.signOut();
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //사용자 정보 파이어베이스에 넣기 성공
                                    Toast.makeText(RegisterSmoke.this, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }
}