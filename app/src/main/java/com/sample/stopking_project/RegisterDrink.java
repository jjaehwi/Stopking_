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

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class RegisterDrink extends AppCompatActivity {

    private FirebaseAuth mAuth;                                      // 파이어베이스 인증
    private FirebaseFirestore db = FirebaseFirestore.getInstance();  // 파이어스토어
    private EditText mEtAvgDrink, mEtWeekDrink, mEtDrinkBank;        //금주 관련 회원 정보
    private Button mbtnDate;                                         // 금주 시작 날짜 버튼
    private Button mbtnComplete;                                     // 회원가입 버튼
    private Button mbtnCancel;                                       // 뒤로가기 버튼
    private String date;
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

        //취소 시 로그인&회원가입 화면으로 다시 이동.
        mbtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                //avgDrink 정보, weekDrink 정보, drink_bank 정보가 파이어베이스 DB에 저장된다.
                String avgDrink = mEtAvgDrink.getText().toString();
                String weekDrink = mEtWeekDrink.getText().toString();
                String drinkBank = mEtDrinkBank.getText().toString();

                //파이어베이스 인증 진행 및 신규 계정 등록하기.
                mAuth.createUserWithEmailAndPassword(getEmail, getPwd).addOnCompleteListener(RegisterDrink.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // task는 회원가입의 결과를 return
                        // 인증 처리가 완료되었을 때. 즉 가입 성공 시
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            String email = firebaseUser.getEmail();
                            String uid = firebaseUser.getUid();

                            HashMap<Object, String> user = new HashMap<>();
                            user.put("uid", uid);
                            user.put("email", email);
                            user.put("name", getName);
                            //금주 정보
                            user.put("average_drink", avgDrink);
                            user.put("week_drink", weekDrink);
                            user.put("drink_bank", drinkBank);
                            user.put("stop_drink", date);
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
                                    finish();
                                }
                            });
                        } // 회원가입 성공.
                        else { // 회원가입 실패한 경우
                            Toast.makeText(RegisterDrink.this, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}