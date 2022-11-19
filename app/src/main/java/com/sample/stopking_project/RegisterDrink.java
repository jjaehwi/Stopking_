package com.sample.stopking_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class RegisterDrink extends AppCompatActivity {

    private FirebaseAuth mAuth;                                      // 파이어베이스 인증
    private FirebaseFirestore db = FirebaseFirestore.getInstance();  // 파이어스토어
    private EditText mEtAvgDrink, mEtWeekDrink, mEtDrinkBank;        //금주 관련 회원 정보
    private Button mbtnDate;                                         // 금주 시작 날짜 버튼
    private Button mbtnComplete;                                     // 회원가입 버튼
    private Button mbtnCancel;                                       // 뒤로가기 버튼
    private String selectDate;
    private String getEmail, getPwd, getName;
    DatePickerDialog dpd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_drink);

        mAuth = FirebaseAuth.getInstance();

        mEtAvgDrink = findViewById(R.id.et_avgDrink);
        mEtWeekDrink = findViewById(R.id.et_weekDrink);
        mEtDrinkBank = findViewById(R.id.drink_bank);
        mbtnDate = findViewById(R.id.btn_date);
        mbtnComplete = findViewById(R.id.btn_complete);
        mbtnCancel = findViewById(R.id.btn_cancel);

        Intent intent = getIntent(); //전달할 데이터를 받을 intent
        getEmail = intent.getStringExtra("email");
        getPwd = intent.getStringExtra("pwd");
        getName = intent.getStringExtra("name");

        //현재 날짜를 가져온다.
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String getDate = dateFormat.format(date);
        int numCurrentTime=Integer.parseInt(getDate);

        //취소 시 로그인&회원가입 화면으로 다시 이동.
        mbtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 회원 탈퇴하기
                mAuth.getCurrentUser().delete();

                finish();
                Toast.makeText(RegisterDrink.this, "회원가입이 취소되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        //금주 시작 날짜 버튼 클릭 처리
        mbtnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int nYear = c.get(Calendar.YEAR);
                int nMonth = c.get(Calendar.MONTH);
                int nDay = c.get(Calendar.DAY_OF_MONTH);

                dpd = new DatePickerDialog(RegisterDrink.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        //1월은 0부터 시작하기 때문에 +1 해준다.
                        month = month + 1;
                        selectDate = year + "/" + month + "/" + day;
                    }
                }, nYear, nMonth, nDay);
                dpd.show();
            }
        });

        //회원가입 완료 후 시작하기 버튼 클릭 처리
        mbtnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEtAvgDrink.getText().toString().equals("") || mEtAvgDrink.getText().toString() == null) {
                    //평균 술 먹는 양을 입력 안 했을 경우.
                    Toast.makeText(RegisterDrink.this, "1번 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (mEtAvgDrink.getText().toString().equals("0")){
                    //1번 항목에 0병을 입력했을 경우
                    Toast.makeText(RegisterDrink.this, "1번 항목에서 최소 0.5병 이상을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if (mEtWeekDrink.getText().toString().equals("") || mEtWeekDrink.getText().toString() == null) {
                    //일주일 당 술 횟수 입력을 안 했을 경우.
                    Toast.makeText(RegisterDrink.this, "2번 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if ((Integer.parseInt(mEtWeekDrink.getText().toString())<=0) || (Integer.parseInt(mEtWeekDrink.getText().toString())>=8)){
                    //2번 항목에서 1~7사이의 값을 입력하지 않았을 경우
                    Toast.makeText(RegisterDrink.this, "2번 항목에서 1~7 사이의 값을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else if (mEtDrinkBank.getText().toString().equals("") || mEtDrinkBank.getText().toString() == null) {
                    //금주 저금통 입력을 안 했을 경우.
                    Toast.makeText(RegisterDrink.this, "3번 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else if (Integer.parseInt(mEtDrinkBank.getText().toString()) <= 4999) {
                    //금주 저금통 최소 금액이하로 목표 금액을 정했을 경우.
                    Toast.makeText(RegisterDrink.this, "모으고 싶은 금액을 최소 5000원 이상으로 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else if (selectDate == null)
                {
                    // 날짜 지정하지 않았을 경우.
                    Toast.makeText(RegisterDrink.this, "금주 날짜를 선택해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    //모든 정보가 올바르게 입력된 경우
                    //avgDrink 정보, weekDrink 정보, drink_bank 정보가 파이어베이스 DB에 저장된다.
                    String avgDrink = mEtAvgDrink.getText().toString();
                    String weekDrink = mEtWeekDrink.getText().toString();
                    String drinkBank = mEtDrinkBank.getText().toString();
                    // 일주일에 마시는 총 병수
                    int weekBottle = Integer.parseInt(weekDrink) * Integer.parseInt(avgDrink);

                    //파이어베이스 DB에 정보 저장
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    String email = firebaseUser.getEmail();
                    String uid = firebaseUser.getUid();

                    HashMap<Object, Object> user = new HashMap<>();
                    user.put("uid", uid);
                    user.put("email", email);
                    user.put("name", getName);
                    //금주 정보
                    user.put("average_drink", avgDrink);
                    user.put("week_drink", weekDrink);
                    user.put("drink_bank", drinkBank);
                    user.put("stop_drink", selectDate);
                    user.put("week_bottle",(Number)weekBottle);
                    //금연 정보
                    user.put("week_smoke", null);
                    user.put("start_smoke", null);
                    user.put("smoke_bank", null);
                    user.put("stop_smoke", null);



                    //문서 추가
                    db.collection("users").document(email).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //사용자 정보 파이어베이스에 넣기 성공
                            Toast.makeText(RegisterDrink.this, "회원가입에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                            finish();
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //사용자 정보 파이어베이스에 넣기 실패 시
                                    Toast.makeText(RegisterDrink.this, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                }
                            });
                } // 회원가입 성공.
            }
    });
}
}