package com.sample.stopking_project;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Drink_MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference dbReference;
    private int user_stop_days; // 금주 일수
    private int user_stop_bottles; // 몇 병 참았는지
    private long documentCount;
    private Button settings;
    private String getName;
    private String getEmail,getAverageDrink,getWeekDrink,getStopDrink;
    private String getGoal;
    private String getRank;
    private String bank_info_text;
    public int my_rank = 1;


    public static Date convertStringtoDate(String Date) { // 데이터베이스에서 가져온 날짜 변환
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date date = null;
        try {
            date = format.parse(Date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String caculateBank(int average_drink, int week_drink, int days) { // 절약 금액 계산
        int drink_price = 4500;
        double week = Math.round((double)days / 7);
        double result = week * average_drink * week_drink * drink_price;
        DecimalFormat formatter = new DecimalFormat("###,###");  // 수에 콤마 넣기
        String result_str = formatter.format(result);
        return result_str;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_drink_main);
        mAuth = FirebaseAuth.getInstance();
        settings = findViewById(R.id.btn_settings);

        // 현재 로그인한 사용자 가져오기.
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser != null) {
            //로그인 한 사용자가 존재할 경우.
            getEmail = fbUser.getEmail();
        }

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
                getAverageDrink = documentSnapshot.getString("average_drink");
                getWeekDrink = documentSnapshot.getString("week_drink");
                getStopDrink = documentSnapshot.getString("stop_drink");
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
                bank_info_text = caculateBank(average_drink_int, week_drink_int, user_stop_days);
                user_stop_bottles = user_stop_days / 7 * average_drink_int * week_drink_int;
                bank_info.setText(bank_info_text + "원");
            }
        });

        TextView health_info_text = findViewById(R.id.health_info);
        // 건강 정보 노출
        docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (user_stop_days > 0) {
                    health_info_text.setText("1단계");
                }
                if (user_stop_days > 90) {
                    health_info_text.setText("2단계");
                }
                if (user_stop_days > 180) {
                    health_info_text.setText("3단계");
                }
                if (user_stop_days > 365) {
                    health_info_text.setText("4단계");
                }
            }
        });

        dbReference = db.collection("users");


        // 나의 랭킹 찾기, 데이터를 모두 가져옴
        TextView main_rank_position = findViewById(R.id.main_rank_position);

        AggregateQuery countQuery = db.collection("users")
                .whereNotEqualTo("stop_drink",null)
                .count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                AggregateQuerySnapshot snapshot = task.getResult();
                documentCount = snapshot.getCount();
                Log.d("ranktest",String.valueOf(documentCount));
                main_rank_position.setText("상위 " + String.valueOf((int)(((double)my_rank/(double)documentCount)*100)) + "%");
            } else {
                Log.d("superdroid", "Count failed: ", task.getException());
            }
        });

        dbReference.orderBy("stop_drink")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            my_rank = 1;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getString("email").compareTo(getEmail) == 0) {
                                    getRank = String.valueOf(my_rank);
                                    Log.d("ranktest",String.valueOf(getRank));
                                    break;
                                }
                                my_rank++;
                            }
                        } else {
                            Log.d("RANKING ACTIVITY", "에러문 출력: ", task.getException()); // 에러문 출력
                        }
                    }
                });



        TextView bank_goal = findViewById(R.id.bank_goal);
        TextView bank_goal_title = findViewById(R.id.bank_goal_title);
        Button btn_bank_goal_reset = findViewById(R.id.btn_bank_goal_reset);
        docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                getGoal = documentSnapshot.getString("drink_bank");
                double getGoal_double = Double.parseDouble(getGoal);
                String average_drink_str = documentSnapshot.getString("average_drink");
                double average_drink_double = Double.parseDouble(average_drink_str);
                String week_drink_str = documentSnapshot.getString("week_drink");
                double week_drink_double = Double.parseDouble(week_drink_str);
                double result = Math.round(getGoal_double / (average_drink_double * week_drink_double * 4500)) * 7;
                int result_int = (int) result - user_stop_days;
                if(result_int>0) {
                    String goal_text = String.valueOf(result_int);
                    bank_goal.setText("D - "+ goal_text);
                } else {
                    bank_goal_title.setText("목표 달성!");
                    bank_goal.setVisibility(View.GONE);
                    btn_bank_goal_reset.setVisibility(View.VISIBLE);
                } // 랭킹에서 목표 금액 변경 후 재설정 필요

            }
        });


        Button btn_ranking = findViewById(R.id.btn_ranking);
        btn_ranking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 랭킹 화면으로 이동
                Intent intent = new Intent(Drink_MainActivity.this, Drink_RankingActivity.class);
                intent.putExtra("email", getEmail); // email값 전달
                intent.putExtra("name", getName); // username 전달
                String user_stop_days_str = String.valueOf(user_stop_days);
                String user_stop_bottles_str = String.valueOf(user_stop_bottles);
                intent.putExtra("day", user_stop_days_str); // 금주 일수 전달
                intent.putExtra("bottle", user_stop_bottles_str); // 참은 병 전달
                startActivity(intent);
            }
        });

        btn_bank_goal_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 메인 액티비티 목표 금액 달성시 버튼 생기고 누르면 통계 화면으로 이동
                Intent intent = new Intent(Drink_MainActivity.this, Drink_Statistics.class);
                intent.putExtra("email", getEmail); // email값 전달
                intent.putExtra("saveMoney", bank_info_text); // 현재까지 저축한 금액
                String user_stop_days_str = String.valueOf(user_stop_days);
                intent.putExtra("stopDays",user_stop_days_str); // 끊은 일 수
                intent.putExtra("Bottles", getAverageDrink); // 술자리 평균 주량 (n병)
                intent.putExtra("weekDrink",getWeekDrink); // 일주일 평균 술자리 횟수
                intent.putExtra("userName",getName); // 유저 이름
                startActivity(intent);
            }
        });


        Button btn_register = findViewById(R.id.btn_statistics);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 메인 액티비티 통계 버튼 클릭시 통계 화면으로 이동
                Intent intent = new Intent(Drink_MainActivity.this, Drink_Statistics.class);
                intent.putExtra("email", getEmail); // email값 전달
                intent.putExtra("saveMoney", bank_info_text); // 현재까지 저축한 금액
                String user_stop_days_str = String.valueOf(user_stop_days);
                intent.putExtra("stopDays",user_stop_days_str); // 끊은 일 수
                intent.putExtra("Bottles", getAverageDrink); // 술자리 평균 주량 (n병)
                intent.putExtra("weekDrink",getWeekDrink); // 일주일 평균 술자리 횟수
                intent.putExtra("userName",getName); // 유저 이름
                startActivity(intent);
            }
        });

        //설정 버튼 클릭 시
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //설정 화면으로 이동
                Intent intent = new Intent(Drink_MainActivity.this, SettingActivity.class);
                intent.putExtra("email", getEmail); // email값 전달
                startActivity(intent);
            }
        });

        // 도움 버튼 클릭 시
        Button btn_help = findViewById(R.id.help_btn);
        btn_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Drink_MainActivity.this, Drink_Help.class);
                startActivity(intent);
            }
        });


    }


}