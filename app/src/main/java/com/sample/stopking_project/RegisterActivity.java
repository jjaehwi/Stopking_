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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText mEtEmail, mEtPwd, mEtName;  //회원가입 입력필드
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // 파이어스토어
    private Button mbtnRegister;        //회원가입 버튼
    private Button mbtnBack;            // 뒤로가기 버튼

    private CheckBox cDrink, cSmoke;    //금연, 금주 선택
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
                if (cDrink.isChecked() && cSmoke.isChecked()) {
                    // 금연, 금주 모두 체크한 경우
                    Toast.makeText(RegisterActivity.this, "금연/금주 중 하나만 선택해주세요.", Toast.LENGTH_SHORT).show();
                } else if ((!cDrink.isChecked()) && !(cSmoke.isChecked())) {
                    // 금연, 금주 모두 체크하지 않은 경우
                    Toast.makeText(RegisterActivity.this, "금연/금주 중 하나를 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    // 금연, 금주 중 하나만 체크한 경우
                    //회원가입 처리 시작(기입한 회원 정보를 가져온다.)

                    String strEmail = mEtEmail.getText().toString();
                    String strPwd = mEtPwd.getText().toString();
                    String strName = mEtName.getText().toString();

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
                }
        }
        });
    }
}