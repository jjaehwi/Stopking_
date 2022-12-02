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

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyInfoActivity extends AppCompatActivity {
    private ImageView backButton; // 뒤로 가기 버튼
    private TextView userName; // 사용자 이름
    private TextView userEmail; // 사용자 이메일
    private TextView avgDrink; // 한 번 마실 때 평균 마시는 술의 양
    private TextView stopDrinkDate; // 금주 시작 날짜
    private TextView weekDrink; // 일주일 당 술자리 횟수
    private TextView drinkBank; // 금주 저금통
    private TextView dayStopDrink; // 나의 금주 일수
    private TextView countBottle; // 내가 참은 병의 개수
    private TextView startSmokeYear; // 흡연 시작년도
    private TextView stopSmokeDate; // 금연 시작 날짜
    private TextView weekCountCiga; // 일주일에 태우는 담배 갑 개수
    private TextView smokeBank; // 금연 저금통
    private TextView dayStopSmoke; // 나의 금연 일수
    private TextView countStopCiga; // 내가 참은 담배 갑 개수

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // 파이어스토어

    private String getName;
    private String getEmail;
    private String str_avg_drink;
    private String str_startToStopDrink;
    private String str_weekDrink;
    private String str_drinkBank;
    private String str_day;

    //금주 일수와 참은 병의 개수 따로 추가해야 함.

    //금연 관련 문자열
    private String str_startSmokeYear;
    private String str_stopSmokeDate;
    private String str_weekCountCiga;
    private String str_smokeBank;
    private String str_stop_pack;

    //참은 날짜, 참은 병/갑 개수를 위한 변수
    private int stop_drinkDay; //금주 참은 날짜
    private int stop_smokeDay; //금연 참은 날짜
    private int bottles;
    private int average_drink_int;
    private int week_drink_int;
    private int weeklySmoke;

    private double drinkFrequecny,bottleTotal;
    private double pack;


    // 데이터베이스에서 가져온 날짜 변환을 위한 함수
    public static Date convertStringtoDate(String Date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date date = null;
        try {
            date = format.parse(Date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    // 금주 절약 금액 계산을 위한 함수
    public static String caculateBank(int average_drink, int week_drink, int days) {
        int drink_price = 4500;
        int week = days / 7;
        int result = week * average_drink * week_drink * drink_price;
        DecimalFormat formatter = new DecimalFormat("###,###");  // 수에 콤마 넣기
        String result_str = formatter.format(result);
        return result_str;
    }

    // 금연 절약 금액 계산을 위한 함수
    public static String caculateSmokeBank(int week_smoke, int days) {
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
                    //내가 참은 병 개수
                    bottles = Integer.parseInt(str_avg_drink);
                    // 일주일 간 마시는 병 출력
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

                //금주 일수, 참은 병의 개수는 Intent로 값 받아오기.
                String drink_day_info_text = documentSnapshot.getString(("stop_drink"));
                if (drink_day_info_text != null) {
                    Date date = convertStringtoDate(drink_day_info_text);
                    Date startDateValue = date;
                    Date now = new Date();
                    long diff = now.getTime() - startDateValue.getTime();
                    long seconds = diff / 1000;
                    long minutes = seconds / 60;
                    long hours = minutes / 60;
                    long days = (hours / 24) + 1;
                    str_day = String.valueOf(days);
                    stop_drinkDay = Integer.parseInt(str_day);
                    dayStopDrink.setText(str_day + "일");
                    dayStopDrink.setTypeface(null, Typeface.BOLD);
                    dayStopDrink.setTextColor(Color.BLACK);
                }

                // 금주 저금통 목표액
                str_drinkBank = documentSnapshot.getString("drink_bank");
                if (str_drinkBank != null)
                {
                    average_drink_int = Integer.parseInt(str_avg_drink);
                    week_drink_int = Integer.parseInt(str_weekDrink);
                    str_drinkBank = caculateBank(average_drink_int, week_drink_int, stop_drinkDay);
                    drinkBank.setText(str_drinkBank + "원");
                    drinkBank.setTypeface(null, Typeface.BOLD);
                    drinkBank.setTextColor(Color.BLACK);
                }


                if ((bottles != 0) && (week_drink_int != 0))
                {
                    double stopDays = Double.parseDouble(str_day);
                    drinkFrequecny = Math.round(((stopDays / 7) * week_drink_int));
                    bottleTotal = drinkFrequecny*bottles;
                    countBottle.setText(Math.round(bottleTotal) +" 병");
                    countBottle.setTypeface(null, Typeface.BOLD);
                    countBottle.setTextColor(Color.BLACK);
                }

                //금연 정보 받아옴.

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
                    weeklySmoke = Integer.parseInt(str_weekCountCiga); // 참은 담배 갑 개수를 위한 변수 설정
                    weekCountCiga.setText(str_weekCountCiga + " 갑");
                    weekCountCiga.setTypeface(null, Typeface.BOLD);
                    weekCountCiga.setTextColor(Color.BLACK);
                }

                // 금연 일수
                String smoke_day_info_text = documentSnapshot.getString(("stop_smoke"));
                if (smoke_day_info_text != null)
                {
                    Date smoke_date = convertStringtoDate(smoke_day_info_text);
                    Date smokeStartDateValue = smoke_date;
                    Date smoke_now = new Date();
                    long differ = smoke_now.getTime() - smokeStartDateValue.getTime();
                    long second = differ / 1000;
                    long minute = second / 60;
                    long hour = minute / 60;
                    long day = (hour / 24) + 1;
                    String smoke_day = String.valueOf(day);
                    stop_smokeDay = Integer.parseInt(smoke_day);
                    dayStopSmoke.setText(smoke_day+"일");
                    dayStopSmoke.setTypeface(null, Typeface.BOLD);
                    dayStopSmoke.setTextColor(Color.BLACK);
                }


                //금연 저금통
                String week_smoke_str = documentSnapshot.getString("week_smoke");
                if (week_smoke_str != null)
                {
                    int week_smoke_int = Integer.parseInt(week_smoke_str);

                    str_smokeBank = caculateSmokeBank(week_smoke_int, stop_smokeDay);
                    smokeBank.setText(str_smokeBank + "원");
                    smokeBank.setTypeface(null, Typeface.BOLD);
                    smokeBank.setTextColor(Color.BLACK);
                }


                //내가 참은 갑 개수는 구현 예정.
                if (weeklySmoke != 0)
                {
                    pack = Math.round(((Math.round((double)stop_smokeDay / 7)) * weeklySmoke));
                    str_stop_pack = String.valueOf(Math.round(pack));
                    countStopCiga.setText(str_stop_pack + "갑");
                    countStopCiga.setTypeface(null, Typeface.BOLD);
                    countStopCiga.setTextColor(Color.BLACK);
                }
            }
        });
    }
}