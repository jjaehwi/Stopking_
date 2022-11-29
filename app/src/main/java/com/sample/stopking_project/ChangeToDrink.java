package com.sample.stopking_project;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ChangeToDrink extends AppCompatActivity {

    private FirebaseAuth mAuth;                                      // 파이어베이스 인증
    private FirebaseFirestore db = FirebaseFirestore.getInstance();  // 파이어스토어
    private EditText mEtWeekDrink, mEtDrinkBank;        //금주 관련 회원 정보
    private Button mEtAvgDrink;
    private Button mbtnDate;                                         // 금주 시작 날짜 버튼
    private Button mbtnComplete;                                     // 회원가입 버튼
    private Button mbtnCancel;                                       // 뒤로가기 버튼
    private String selectDate;
    private String userEmail;
    private String selectAvgDrink;
    DatePickerDialog dpd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_to_drink);

        mAuth = FirebaseAuth.getInstance();

        mEtAvgDrink = findViewById(R.id.change_to_drink_et_avgDrink);
        mEtWeekDrink = findViewById(R.id.change_to_drink_et_weekDrink);
        mEtDrinkBank = findViewById(R.id.change_to_drink_drink_bank);
        mbtnDate = findViewById(R.id.change_to_drink_btn_date);
        mbtnComplete = findViewById(R.id.change_to_drink_btn_complete);
        mbtnCancel = findViewById(R.id.change_to_drink_btn_cancel);

        // 현재 로그인한 사용자 가져오기.
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser != null) {
            //로그인 한 사용자가 존재할 경우.
            userEmail = fbUser.getEmail();
        }

        //현재 날짜를 가져온다.
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String getDate = dateFormat.format(date);
        int numCurrentTime=Integer.parseInt(getDate);

        //취소 시 설정 화면으로 다시 이동.
        mbtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 몇 병인지 버튼 클릭 후 선택
        mEtAvgDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(ChangeToDrink.this);
                dlg.setTitle("한번 마실 때 평균적으로 마시는 술병 개수"); //제목
                final String[] versionArray = new String[] {"1병","2병","3병","4병","5병","6병"};
                dlg.setIcon(R.drawable.beer); // 아이콘 설정

                dlg.setSingleChoiceItems(versionArray, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mEtAvgDrink.setText(versionArray[which]);
                        selectAvgDrink = versionArray[which];
                    }
                });

                dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        Log.d("MYTAG", "selectAvgDrink : " + selectAvgDrink);
                    }
                });
                dlg.show();
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

                dpd = new DatePickerDialog(ChangeToDrink.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        //1월은 0부터 시작하기 때문에 +1 해준다.
                        String getMonth, getDay;
                        month = month + 1;
                        if(0<month && month<10){
                            getMonth = "0" + month;
                        } else getMonth = String.valueOf(month);
                        if(0<day && month<10){
                            getDay = "0" + day;
                        } else getDay = String.valueOf(day);
                        selectDate = year + "/" + getMonth + "/" + getDay;
                    }
                }, nYear, nMonth, nDay);
                dpd.show();
            }
        });

        //설정 변경 완료 후 시작하기 버튼 클릭 처리
        mbtnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectAvgDrink == null)
                {
                    //평균 술 먹는 양을 입력 안 했을 경우.
                    Toast.makeText(ChangeToDrink.this, "1번 항목을 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if (mEtWeekDrink.getText().toString().equals("") || mEtWeekDrink.getText().toString() == null) {
                    //일주일 당 술 횟수 입력을 안 했을 경우.
                    Toast.makeText(ChangeToDrink.this, "2번 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if ((Integer.parseInt(mEtWeekDrink.getText().toString())<=0) || (Integer.parseInt(mEtWeekDrink.getText().toString())>=8)){
                    //2번 항목에서 1~7사이의 값을 입력하지 않았을 경우
                    Toast.makeText(ChangeToDrink.this, "2번 항목에서 1~7 사이의 값을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else if (mEtDrinkBank.getText().toString().equals("") || mEtDrinkBank.getText().toString() == null) {
                    //금주 저금통 입력을 안 했을 경우.
                    Toast.makeText(ChangeToDrink.this, "3번 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else if (Integer.parseInt(mEtDrinkBank.getText().toString()) <= 4999) {
                    //금주 저금통 최소 금액이하로 목표 금액을 정했을 경우.
                    Toast.makeText(ChangeToDrink.this, "모으고 싶은 금액을 최소 5000원 이상으로 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else if (selectDate == null)
                {
                    // 날짜 지정하지 않았을 경우.
                    Toast.makeText(ChangeToDrink.this, "금주 날짜를 선택해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    //모든 정보가 올바르게 입력된 경우
                    //avgDrink 정보, weekDrink 정보, drink_bank 정보가 파이어베이스 DB에 저장된다.
                    String weekDrink = mEtWeekDrink.getText().toString();
                    String drinkBank = mEtDrinkBank.getText().toString();
                    String intAvgDrink = selectAvgDrink.replaceAll("[^0-9]", "");
                    int weekBottle = Integer.parseInt(weekDrink) * Integer.parseInt(intAvgDrink);

                    //파이어베이스 DB에 정보 저장
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    String email = firebaseUser.getEmail();
                    String uid = firebaseUser.getUid();

                    DocumentReference docRef = db.collection("users").document(userEmail);
                    docRef.update("average_drink",intAvgDrink);
                    docRef.update("week_drink",weekDrink);
                    docRef.update("drink_bank",drinkBank);
                    docRef.update("week_bottle",weekBottle);
                    docRef.update("stop_drink",selectDate);
                    docRef.update("flag","drink");

                    Intent intent = new Intent(ChangeToDrink.this,LoadingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    }
}