package com.sample.stopking_project;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    private EditText mEtName;  //회원가입 입력필드
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // 파이어스토어
    private FirebaseAuth mAuth;                                      // 파이어베이스 인증
    private Button mbtnRegister;        //회원가입 버튼
    private Button mbtnBack;            // 뒤로가기 버튼
    private Button mbtnDate;                                         // 금주 시작 날짜 버튼
    private CheckBox cDrink, cSmoke;    //금연, 금주 선택
    DatePickerDialog dpd;
    private String selectDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mbtnRegister = findViewById(R.id.btn_register);
        mEtName = findViewById(R.id.et_name);
        mbtnBack = findViewById(R.id.btn_back);
        mbtnDate = findViewById(R.id.btn_date);
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

        //금주 시작 날짜 버튼 클릭 처리
        mbtnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int nYear = c.get(Calendar.YEAR);
                int nMonth = c.get(Calendar.MONTH);
                int nDay = c.get(Calendar.DAY_OF_MONTH);

                dpd = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
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

        mbtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(mEtName.getText().toString().equals("") || mEtName.getText().toString() == null)
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
                } else if (selectDate == null)
                {
                    // 날짜 지정하지 않았을 경우.
                    Toast.makeText(RegisterActivity.this, "금주 날짜를 선택해주세요.", Toast.LENGTH_SHORT).show();
                } else
                {
                    String strName = mEtName.getText().toString();
                    //문서 저장한 후 금연/금주 파트에 맞게 액티비티 이동.
                    if (cDrink.isChecked() && (!cSmoke.isChecked())) {
                        // 금주 파트를 선택했을 때
                        Intent intent = new Intent(RegisterActivity.this, RegisterDrink.class);
                        intent.putExtra("name", strName);   // name값 전달
                        intent.putExtra("stopDate", selectDate); // 날짜 전달
                        startActivity(intent);
                    } else if ((!cDrink.isChecked()) && cSmoke.isChecked()) {
                        // 금연 파트를 선택했을 때
                        Intent intent = new Intent(RegisterActivity.this, RegisterSmoke.class);
                        intent.putExtra("name", strName);   // name값 전달
                        intent.putExtra("stopDate", selectDate); // 날짜 전달
                        startActivity(intent);
                    }
                }
            }
        });
    }
}