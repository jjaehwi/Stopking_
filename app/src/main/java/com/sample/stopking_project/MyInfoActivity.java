package com.sample.stopking_project;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class MyInfoActivity extends AppCompatActivity {
    private ImageView backButton;
    private TextView userName;
    private TextView userEmail;
    private TextView avgDrink;
    private TextView stopDrinkDate;
    private TextView weekDrink;
    private TextView drinkBank;
    private TextView dayStopDrink;
    private TextView countBottle;
    private TextView startSmokeYear;
    private TextView stopSmokeDate;
    private TextView weekCountCiga;
    private TextView smokeBank;
    private TextView dayStopSmoke;
    private TextView countStopCiga;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // 파이어스토어

    private String getName;
    private String getEmail;
    private String str_avg_drink;
    private String str_startToStopDrink;
    private String str_weekDrink;
    private String str_drinkBank;
    //금주 일수와 참은 병의 개수 따로 추가해야 함.

    //금연 관련 문자열
    private String str_startSmokeYear;
    private String str_stopSmokeDate;
    private String str_weekCountCiga;
    private String str_smokeBank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);

        mAuth = FirebaseAuth.getInstance();

        backButton = findViewById(R.id.btn_back);
        userName = findViewById(R.id.tv_myName);
        userEmail = findViewById(R.id.tv_myEmail);
        avgDrink = findViewById(R.id.tv_averageDrink);
        stopDrinkDate = findViewById(R.id.tv_stop_drink_date);
        weekDrink = findViewById(R.id.tv_countDrink);
        drinkBank = findViewById(R.id.tv_drinkBank);
        dayStopDrink = findViewById(R.id.tv_dayStopDrink);
        countBottle = findViewById(R.id.tv_countBottle);

        startSmokeYear = findViewById(R.id.tv_startYear);
        stopSmokeDate = findViewById(R.id.tv_dayStopSmoke);
        weekCountCiga = findViewById(R.id.tv_weekCiga);
        smokeBank = findViewById(R.id.tv_smokeBank);
        dayStopSmoke = findViewById(R.id.tv_stop_smoke_date);
        countStopCiga = findViewById(R.id.tv_countCiga);

        // 뒤로 가기 버튼
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 해당 액티비티 종료 후 전 화면으로 이동.
                finish();
            }
        });

        // 현재 로그인한 사용자 가져오기.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
        {
            //로그인 한 사용자가 존재할 경우.
            getEmail = user.getEmail();
        }

        //정보 받아오기
        DocumentReference docRef = db.collection("users").document(getEmail);
        docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                //이름, 이메일 받아옴
                getName  = documentSnapshot.getString("name");
                userName.setText(getName);
                userEmail.setText(getEmail);

                //금주 관련 정보 받아옴.

                //한 번 마실 때 먹는 술의 양
                str_avg_drink = documentSnapshot.getString("average_drink");
                if (str_avg_drink != null) {
                    avgDrink.setText(str_avg_drink + " 병");
                    avgDrink.setTypeface(null, Typeface.BOLD);
                    avgDrink.setTextColor(Color.BLACK);
                }

                // 금주 시작 날짜
                str_startToStopDrink = documentSnapshot.getString("stop_drink");
                if (str_startToStopDrink != null) {
                    stopDrinkDate.setText(str_startToStopDrink);
                    stopDrinkDate.setTypeface(null, Typeface.BOLD);
                    stopDrinkDate.setTextColor(Color.BLACK);
                }

                // 일주일 당 술자리 횟수
                str_weekDrink = documentSnapshot.getString("week_drink");
                if (str_weekDrink != null) {
                    weekDrink.setText(str_weekDrink + " 번");
                    weekDrink.setTypeface(null, Typeface.BOLD);
                    weekDrink.setTextColor(Color.BLACK);
                }

                // 금주 저금통 목표액
                str_drinkBank = documentSnapshot.getString("drink_bank");
                if (str_drinkBank != null) {
                    drinkBank.setText(str_drinkBank + " 원");
                    drinkBank.setTypeface(null, Typeface.BOLD);
                    drinkBank.setTextColor(Color.BLACK);
                }

                //금주 일수, 참은 병의 개수는 Intent로 값 받아오기.




                //금연 정보

                // 흡연 시작 년도
                str_startSmokeYear = documentSnapshot.getString("start_smoke");
                if (str_startSmokeYear != null) {
                    startSmokeYear.setText(str_startSmokeYear + " 년");
                    startSmokeYear.setTypeface(null, Typeface.BOLD);
                    startSmokeYear.setTextColor(Color.BLACK);
                }

                // 금연 시작 날짜
                str_stopSmokeDate = documentSnapshot.getString("stop_smoke");
                if (str_stopSmokeDate != null) {
                    stopSmokeDate.setText(str_stopSmokeDate);
                    stopSmokeDate.setTypeface(null, Typeface.BOLD);
                    stopSmokeDate.setTextColor(Color.BLACK);
                }

                // 일주일에 태우는 담배 갑 개수
                str_weekCountCiga = documentSnapshot.getString("week_smoke");
                if (str_weekCountCiga != null) {
                    weekCountCiga.setText(str_weekCountCiga + " 갑");
                    weekCountCiga.setTypeface(null, Typeface.BOLD);
                    weekCountCiga.setTextColor(Color.BLACK);
                }

                // 금연 저금통 목표액
                str_smokeBank = documentSnapshot.getString("week_smoke");
                if (str_smokeBank != null) {
                    smokeBank.setText(str_smokeBank + " 원");
                    smokeBank.setTypeface(null, Typeface.BOLD);
                    smokeBank.setTextColor(Color.BLACK);
                }
            }
        });
    }
}