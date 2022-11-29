package com.sample.stopking_project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterDrink extends AppCompatActivity {

    private EditText mEtWeekDrink, mEtDrinkBank;        //금주 관련 회원 정보
    private Button mEtAvgDrink;
    private Button mbtnComplete;                                     // 회원가입 버튼
    private Button mbtnCancel;                                       // 뒤로가기 버튼
    private String selectDate, selectAvgDrink, getName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_drink);

        //Button 까지 바꿔줌
        mEtAvgDrink = findViewById(R.id.et_avgDrink);
        mEtWeekDrink = findViewById(R.id.et_weekDrink);
        mEtDrinkBank = findViewById(R.id.drink_bank);
        mbtnComplete = findViewById(R.id.btn_complete);
        mbtnCancel = findViewById(R.id.btn_cancel);

        Intent intent = getIntent(); //전달할 데이터를 받을 intent
        getName = intent.getStringExtra("name");
        selectDate = intent.getStringExtra("stopDate");

        //취소 시 이전 회원가입 화면으로 다시 이동.
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
                AlertDialog.Builder dlg = new AlertDialog.Builder(RegisterDrink.this);
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

        //회원가입 완료 후 시작하기 버튼 클릭 처리
        mbtnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectAvgDrink == null)
                {
                    //평균 술 먹는 양을 입력 안 했을 경우.
                    Toast.makeText(RegisterDrink.this, "1번 항목을 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if (mEtWeekDrink.getText().toString().equals("") || mEtWeekDrink.getText().toString() == null)
                {
                    //일주일 당 술 횟수 입력을 안 했을 경우.
                    Toast.makeText(RegisterDrink.this, "2번 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if ((Integer.parseInt(mEtWeekDrink.getText().toString())<1) || (Integer.parseInt(mEtWeekDrink.getText().toString())>7))
                {
                    //2번 항목에서 1~7사이의 값을 입력하지 않았을 경우
                    Toast.makeText(RegisterDrink.this, "2번 항목에서 1~7 사이의 값을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if (mEtDrinkBank.getText().toString().equals("") || mEtDrinkBank.getText().toString() == null)
                {
                    //금주 저금통 입력을 안 했을 경우.
                    Toast.makeText(RegisterDrink.this, "3번 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if (Integer.parseInt(mEtDrinkBank.getText().toString()) <= 4999)
                {
                    //금주 저금통 최소 금액이하로 목표 금액을 정했을 경우.
                    Toast.makeText(RegisterDrink.this, "모으고 싶은 금액을 최소 5000원 이상으로 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    //모든 정보가 올바르게 입력된 경우
                    //avgDrink 정보, weekDrink 정보, drink_bank 정보가 파이어베이스 DB에 저장된다.
                    String weekDrink = mEtWeekDrink.getText().toString();
                    String drinkBank = mEtDrinkBank.getText().toString();

                    Intent intent = new Intent(RegisterDrink.this, RegisterDrinkEmail.class);
                    intent.putExtra("name", getName);   // name값 전달
                    intent.putExtra("stopDate", selectDate); // 날짜 전달
                    intent.putExtra("avgDrink", selectAvgDrink); // 한 번에 먹는 술 병 전달
                    intent.putExtra("weekDrink", weekDrink); // 일주일에 마시는 술 횟수 전달
                    intent.putExtra("drinkBank", drinkBank); // 금주저금통 전달
                    startActivity(intent);
                } // 회원가입 성공.
            }
        });
    }
}