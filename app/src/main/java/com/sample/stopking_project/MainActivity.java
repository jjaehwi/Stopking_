package com.sample.stopking_project;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private int user_stop_days; // 금주 일수
    private int user_stop_bottles; // 몇 병 참았는지
    private Button settings;
    private String getName;
    private String getEmail;

    //test
    private Button btn_test;

    public static Date convertStringtoDate(String Date){ // 데이터베이스에서 가져온 날짜 변환
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date date = null;
        try{
            date = format.parse(Date);
        } catch(ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String caculateBank(int average_drink,int week_drink,int days){ // 절약 금액 계산
        int drink_price = 4500;
        int week = days / 7;
        int result = week * average_drink * week_drink * drink_price;
        DecimalFormat formatter = new DecimalFormat("###,###");  // 수에 콤마 넣기
        String result_str = formatter.format(result);
        return result_str;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        settings = findViewById(R.id.btn_settings);

        // 현재 로그인한 사용자 가져오기.
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser != null)
        {
            //로그인 한 사용자가 존재할 경우.
            getEmail = fbUser.getEmail();
        }

        btn_test = findViewById(R.id.button_test);

        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "버튼 눌렀노.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, DrinkFirebaseTest.class);
                startActivity(intent);
            }
        });

        // 금주 날짜 가져오기
        DocumentReference docRef = db.collection("users").document(getEmail);
        docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                TextView day_info = findViewById(R.id.day_info);
                String day_info_text = documentSnapshot.getString(("stop_drink"));
                Date date = convertStringtoDate(day_info_text);
                Date startDateValue = date;
                Date now = new Date();
                long diff = now.getTime() - startDateValue.getTime();
                long seconds = diff / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = (hours / 24) + 1;
                String d = String.valueOf(days);
                user_stop_days = Integer.parseInt(d);
                day_info.setText(d + "일째"); // 금주 날짜 계산 및 표시
            }
        });

        docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                getName = documentSnapshot.getString("name");
            }
        });

        docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() { // 절약 금액 계산을 위한 데이터 fetch
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                TextView bank_info = findViewById(R.id.bank_info);

                String average_drink_str = documentSnapshot.getString("average_drink");
                int average_drink_int = Integer.parseInt(average_drink_str);

                String week_drink_str = documentSnapshot.getString("week_drink");
                int week_drink_int = Integer.parseInt(week_drink_str);
                String bank_info_text = caculateBank(average_drink_int,week_drink_int,user_stop_days);
                user_stop_bottles = user_stop_days/7 * average_drink_int * week_drink_int;
                bank_info.setText(bank_info_text + "원");
            }
        });



        Button btn_ranking = findViewById(R.id.btn_ranking);
        btn_ranking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 랭킹 화면으로 이동
                Intent intent = new Intent(MainActivity.this, RankingActivity.class);
                intent.putExtra("email", getEmail); // email값 전달
                intent.putExtra("name",getName); // username 전달
                String user_stop_days_str =String.valueOf(user_stop_days);
                String user_stop_bottles_str = String.valueOf(user_stop_bottles);
                intent.putExtra("day",user_stop_days_str); // 금주 일수 전달
                intent.putExtra("bottle",user_stop_bottles_str); // 참은 병 전달
                startActivity(intent);
            }
        });



        Button btn_register = findViewById(R.id.btn_statistics);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 통계 화면으로 이동
                Intent intent = new Intent(MainActivity.this, Statistics.class);
                intent.putExtra("email", getEmail); // email값 전달
                startActivity(intent);
            }
        });

        //설정 버튼 클릭 시
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //설정 화면으로 이동
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                intent.putExtra("email", getEmail); // email값 전달
                startActivity(intent);
            }
        });
    }
}