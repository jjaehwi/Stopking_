package com.sample.stopking_project;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class ChangeToSmoke extends AppCompatActivity {

    private FirebaseAuth mAuth;                                      // 파이어베이스 인증
    private FirebaseFirestore db = FirebaseFirestore.getInstance();  // 파이어스토어
    private EditText mEtAvgSmoke, mEtStartSmoke, mEtSmokeBank;        // 금언 관련 회원 정보
    private Button mbtnDate;                                         // 금주 시작 날짜 버튼
    private Button mbtnComplete;                                     // 회원가입 버튼
    private Button mbtnCancel;                                       // 뒤로가기 버튼
    private String date;
    private String getEmail;
    DatePickerDialog dpd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_to_smoke);

        mAuth = FirebaseAuth.getInstance();

        mEtAvgSmoke = findViewById(R.id.change_to_smoke_et_avgSmoke);
        mEtStartSmoke = findViewById(R.id.change_to_smoke_et_startSmoke);
        mEtSmokeBank = findViewById(R.id.change_to_smoke_smoke_bank);
        mbtnDate = findViewById(R.id.change_to_smoke_btn_date);
        mbtnComplete = findViewById(R.id.change_to_smoke_btn_finish);
        mbtnCancel = findViewById(R.id.change_to_smoke_btn_cancel);

        Intent intent = getIntent(); //전달할 데이터를 받을 intent
        getEmail = intent.getStringExtra("email");

        //취소 시 설정 화면으로 다시 이동.
        mbtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
                dpd = new DatePickerDialog(ChangeToSmoke.this, new DatePickerDialog.OnDateSetListener() {
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
                    Toast.makeText(ChangeToSmoke.this, "1번 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(mEtAvgSmoke.getText().toString()) <= 0) {
                    //1번 항목에 0갑 이하를 입력했을 경우
                    Toast.makeText(ChangeToSmoke.this, "1번 항목에서 최소 0.5갑 이상을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (mEtStartSmoke.getText().toString().equals("") || mEtStartSmoke.getText().toString() == null) {
                    //2번 항목 입력 안 했을 경우.
                    Toast.makeText(ChangeToSmoke.this, "2번 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (mEtSmokeBank.getText().toString().equals("") || mEtSmokeBank.getText().toString() == null) {
                    //금주 저금통 입력을 안 했을 경우.
                    Toast.makeText(ChangeToSmoke.this, "3번 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(mEtSmokeBank.getText().toString()) <= 4499) {
                    //금연 저금통 최소 금액이하로 목표 금액을 정했을 경우.
                    Toast.makeText(ChangeToSmoke.this, "모으고 싶은 금액을 최소 4500원 이상으로 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (date == null) {
                    // 날짜 지정하지 않았을 경우.
                    Toast.makeText(ChangeToSmoke.this, "금주 날짜를 선택해주세요.", Toast.LENGTH_SHORT).show();
                } else
                {
                    //week_smoke 정보, smoke_bank, start_smoke 정보가 파이어베이스 DB에 저장된다.
                    String avgSmoke = mEtAvgSmoke.getText().toString();
                    String startSmoke = mEtStartSmoke.getText().toString();
                    String smokeBank = mEtSmokeBank.getText().toString();

                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    String email = firebaseUser.getEmail();
                    String uid = firebaseUser.getUid();
                    //문서 추가
                    DocumentReference docRef = db.collection("users").document(getEmail);
                    docRef.update("week_smoke",avgSmoke);
                    docRef.update("smoke_bank",smokeBank);
                    docRef.update("start_smoke",startSmoke);
                    docRef.update("stop_smoke",date);
                    docRef.update("flag","smoke");
                }
                Intent intent = new Intent(ChangeToSmoke.this,Smoke_MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}