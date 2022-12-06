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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterSmoke extends AppCompatActivity {

    private FirebaseAuth mAuth;                                      // 파이어베이스 인증
    private FirebaseFirestore db = FirebaseFirestore.getInstance();  // 파이어스토어
    private EditText mEtStartSmoke, mEtSmokeBank;        // 금언 관련 회원 정보
    private Button mbtnAvgSmoke;
    private Button mbtnComplete;                                     // 회원가입 버튼
    private Button mbtnCancel;                                       // 뒤로가기 버튼
    private String getName;
    private String selectAvgSmoke, selectDate;
    private String getYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_smoke);

        mAuth = FirebaseAuth.getInstance();

        mbtnAvgSmoke = findViewById(R.id.et_avgSmoke);
        mEtStartSmoke = findViewById(R.id.et_startSmoke);
        mEtSmokeBank = findViewById(R.id.smoke_bank);
        mbtnComplete = findViewById(R.id.btn_finish);
        mbtnCancel = findViewById(R.id.btn_cancel);

        Intent intent = getIntent(); //전달할 데이터를 받을 intent
        getName = intent.getStringExtra("name");
        selectDate = intent.getStringExtra("stopDate");

        //취소 시 로그인&회원가입 화면으로 다시 이동.
        mbtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 현재 년도보다 큰 값을 입력했는지 확인하기 위한 절차
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        getYear = dateFormat.format(date);

        // 몇 갑인지 버튼 클릭 후 선택
        mbtnAvgSmoke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(RegisterSmoke.this);
                dlg.setTitle("일주일에 몇 갑을 피우시나요?"); //제목
                final String[] versionArray = new String[] {"1갑","2갑","3갑","4갑","5갑","6갑", "7갑"};
                dlg.setIcon(R.drawable.ciga); // 아이콘 설정

                dlg.setSingleChoiceItems(versionArray, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mbtnAvgSmoke.setText(versionArray[which]);
                        selectAvgSmoke = versionArray[which];
                        Log.d("MYTAG", "selectAvgSmoke : " + selectAvgSmoke);
                    }
                });

                dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        Log.d("MYTAG", "selectAvgSmoke : " + selectAvgSmoke);
                    }
                });
                dlg.show();
            }
        });

        //회원가입 완료 후 시작하기 버튼 클릭 처리
        mbtnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //예외 처리
                if (selectAvgSmoke == null)
                {
                    //평균 담배 갑을 입력 안 했을 경우.
                    Toast.makeText(RegisterSmoke.this, "1번 항목을 선택해주세요.", Toast.LENGTH_SHORT).show();
                }else if (mEtStartSmoke.getText().toString().equals("") || mEtStartSmoke.getText().toString() == null) {
                    //2번 항목 입력 안 했을 경우.
                    Toast.makeText(RegisterSmoke.this, "2번 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if (mEtStartSmoke.getText().toString().compareTo(getYear) > 0)
                {
                    //현재 년도보다 크게 입력하였을 경우.
                    Toast.makeText(RegisterSmoke.this, "시작 년도를 올바르게 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else if (mEtSmokeBank.getText().toString().equals("") || mEtSmokeBank.getText().toString() == null) {
                    //금연 저금통 입력을 안 했을 경우.
                    Toast.makeText(RegisterSmoke.this, "3번 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(mEtSmokeBank.getText().toString()) <= 4499) {
                    //금연 저금통 최소 금액이하로 목표 금액을 정했을 경우.
                    Toast.makeText(RegisterSmoke.this, "모으고 싶은 금액을 최소 4500원 이상으로 입력해주세요.", Toast.LENGTH_SHORT).show();
                }  else
                {
                    String startSmoke = mEtStartSmoke.getText().toString();
                    String smokeBank = mEtSmokeBank.getText().toString();

                    Intent intent = new Intent(RegisterSmoke.this, RegisterSmokeEmail.class);
                    intent.putExtra("name", getName);   // name값 전달
                    intent.putExtra("stopDate", selectDate); // 날짜 전달
                    intent.putExtra("avgSmoke", selectAvgSmoke); // 한 번에 먹는 술 병 전달
                    intent.putExtra("startSmoke", startSmoke); // 일주일에 마시는 술 횟수 전달
                    intent.putExtra("smokeBank", smokeBank); // 금주저금통 전달
                    startActivity(intent);
                }
            }
        });
    }
}