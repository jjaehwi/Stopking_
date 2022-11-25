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

public class Smoke_MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference dbReference;
    private int user_stop_days; // 금연 일수
    private int user_stop_packs; // 몇 갑 참았는지
    private long documentCount;
    private Button settings;
    private String getName;
    private String getEmail;
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

    public static String caculateBank(int week_smoke, int days) { // 절약 금액 계산
        int smoke_price = 4500;
        int week = days / 7;
        int result = week * week_smoke * smoke_price;
        DecimalFormat formatter = new DecimalFormat("###,###");  // 수에 콤마 넣기
        String result_str = formatter.format(result);
        return result_str;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_smoke_main);
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
                String day_info_text = documentSnapshot.getString(("stop_smoke"));
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
                day_info.setText(d + "일째"); // 금연 날짜 계산 및 표시
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

                String week_smoke_str = documentSnapshot.getString("week_smoke");
                int week_smoke_int = Integer.parseInt(week_smoke_str);

                bank_info_text = caculateBank(week_smoke_int, user_stop_days);
                user_stop_packs = user_stop_days / 7 * week_smoke_int;
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

        // 나의 랭킹 찾기, 데이터를 모두 가져옴
        dbReference = db.collection("users");
        TextView main_rank_position = findViewById(R.id.main_rank_position);
        dbReference.orderBy("stop_smoke")
                .whereNotEqualTo("stop_smoke",null)
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

        AggregateQuery countQuery = db.collection("users")
                .whereNotEqualTo("stop_smoke",null)
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




        TextView bank_goal = findViewById(R.id.bank_goal);
        TextView bank_goal_title = findViewById(R.id.bank_goal_title);
        Button btn_bank_goal_reset = findViewById(R.id.btn_bank_goal_reset);
        docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                getGoal = documentSnapshot.getString("smoke_bank");
                getName = documentSnapshot.getString("name");
                double getGoal_double = Double.parseDouble(getGoal);
                String week_smoke_str = documentSnapshot.getString("week_smoke");
                double week_smoke_double = Double.parseDouble(week_smoke_str);
                double result = Math.round(getGoal_double / (week_smoke_double * 4500)) * 7;
                int result_int = (int) result - user_stop_days;
                if(result_int>0) {
                    String goal_text = String.valueOf(result_int);
                    bank_goal.setText("D - " + goal_text);
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
                Intent intent = new Intent(Smoke_MainActivity.this, Smoke_RankingActivity.class);
                intent.putExtra("email", getEmail); // email값 전달
                intent.putExtra("name", getName); // username 전달
                String user_stop_days_str = String.valueOf(user_stop_days);
                String user_stop_packs_str = String.valueOf(user_stop_packs);
                intent.putExtra("day", user_stop_days_str); // 금연 일수 전달
                intent.putExtra("pack", user_stop_packs_str); // 참은 갑 전달
                startActivity(intent);
            }
        });

        btn_bank_goal_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 통계 화면으로 이동
                Intent intent = new Intent(Smoke_MainActivity.this, Smoke_Statistics.class);
                intent.putExtra("email", getEmail); // email값 전달
                intent.putExtra("saveMoney", bank_info_text); // 현재까지 저축한 금액
                String user_stop_days_str = String.valueOf(user_stop_days);
                String user_stop_packs_str = String.valueOf(user_stop_packs);
                intent.putExtra("day", user_stop_days_str); // 금연 일수 전달
                intent.putExtra("pack", user_stop_packs_str); // 참은 갑 전달
                intent.putExtra("userName",getName); // 유저 이름
                startActivity(intent);
            }
        });




        Button btn_register = findViewById(R.id.btn_statistics);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 통계 화면으로 이동
                Intent intent = new Intent(Smoke_MainActivity.this, Smoke_Statistics.class);
                intent.putExtra("email", getEmail); // email값 전달
                intent.putExtra("saveMoney", bank_info_text); // 현재까지 저축한 금액
                String user_stop_days_str = String.valueOf(user_stop_days);
                String user_stop_packs_str = String.valueOf(user_stop_packs);
                intent.putExtra("day", user_stop_days_str); // 금연 일수 전달
                intent.putExtra("pack", user_stop_packs_str); // 참은 갑 전달
                intent.putExtra("userName",getName); // 유저 이름
                startActivity(intent);
            }
        });

        //설정 버튼 클릭 시
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //설정 화면으로 이동
                Intent intent = new Intent(Smoke_MainActivity.this, SettingActivity.class);
                intent.putExtra("email", getEmail); // email값 전달
                startActivity(intent);
            }
        });
    }


}