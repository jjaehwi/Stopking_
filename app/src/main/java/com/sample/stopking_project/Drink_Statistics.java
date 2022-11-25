package com.sample.stopking_project;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.tomergoldst.tooltips.ToolTip;
import com.tomergoldst.tooltips.ToolTipsManager;

public class Drink_Statistics extends AppCompatActivity implements ToolTipsManager.TipListener, View.OnClickListener {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    ImageButton tooltip;
    ToolTipsManager toolTipsManager;
    TextView dayTitle, drinkFrequencyTitle, countBottlesTitle, saveTimeTitle, saveMoneyTitle;
    TextView tooltipTextView, saveMoneyText, goalMoneyText, progressRatio, cheerText;
    Button goalMoneyButton;
    TextView textView1, textView2, textView3, textView4, textView5, textView6, userNameTitle;
    TextView textDays, currentDays, guideText1, guideText2, guideText3;
    RelativeLayout linearLayout;
    ProgressBar progressBar1, progressBar2;
    ImageView btnBack, btnBack2;
    private String getEmail; // 유저 로그인할때의 이메일
    private String str_getSaveMoney; // 현재까지 모인 금액
    private String str_drinkBank; // 목표 금액
    private String str_goalText; // 목표 금액 텍스트
    private String str_WeekDrink; // 일주일에 술자리 몇 번 있는지
    private String str_Bottles; // 술자리 한번 당 마시는 평균 주량
    private String str_StopDays; // 금주 시작 일자로 부터 몇일 지났는지
    private String getName;
    private double drinkFrequecny,bottleTotal, saveTime, saveKcal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_statistics);

        TabHost tabHost = findViewById(R.id.host); tabHost.setup();
        TabHost.TabSpec spec = tabHost.newTabSpec("tab1");
        spec.setIndicator("절 약");
        spec.setContent(R.id.tab_content1);
        tabHost.addTab(spec);
        spec = tabHost.newTabSpec("tab2");
        spec.setIndicator("건 강");
        spec.setContent(R.id.tab_content2);
        tabHost.addTab(spec);

        tooltip = findViewById(R.id.tooltip);
        tooltipTextView = findViewById(R.id.tooltipTextView);
        linearLayout = findViewById(R.id.linearlayout);

        Intent intent = getIntent(); // 전달한 데이터를 받을 intent
        getEmail = intent.getStringExtra("email");
        str_getSaveMoney = intent.getStringExtra("saveMoney"); // bank_info_text를 받아옴 String
        str_WeekDrink = intent.getStringExtra("weekDrink"); // 일주일에 마시는 술 자리가 몇 번 있는지
        str_Bottles = intent.getStringExtra("Bottles"); // 평균 마시는 병 수
        str_StopDays = intent.getStringExtra("stopDays"); // 끊은 일 수
        double stopDays = Double.parseDouble(str_StopDays);
        int weekDrink = Integer.parseInt(str_WeekDrink);
        int bottles = Integer.parseInt(str_Bottles);
        drinkFrequecny = ((stopDays / 7) * weekDrink);
        bottleTotal = drinkFrequecny*bottles;
        saveTime = drinkFrequecny * 2.5;
        saveKcal = bottleTotal * 408;

        // 총 금주/ 금연 일수 텍스트
        dayTitle = findViewById(R.id.dayTitle);
        // 금주 횟수 텍스트
        drinkFrequencyTitle = findViewById(R.id.drinkFrequencyTitle);
        // 참은 병 수 텍스트
        countBottlesTitle = findViewById(R.id.countBottlesTitle);
        // 아낀 시간 텍스트
        saveTimeTitle = findViewById(R.id.saveTimeTitle);
        // 아낀 돈 텍스트
        saveMoneyTitle = findViewById(R.id.saveMoneyTitle);




        // 절약 금액 텍스트
        saveMoneyText = findViewById(R.id.saveMoneyText);
        // 목표 금액 텍스트
        goalMoneyText = findViewById(R.id.goalMoneyText);
        // 진행률 텍스트
        progressRatio = findViewById(R.id.progressRatio);
        // 응원 문구 텍스트
        cheerText = findViewById(R.id.cheerText);
        // 목표 금액 재설정 버튼
        goalMoneyButton = findViewById(R.id.goalMoneyButton);

        // db에서 데이터 받아오기
        DocumentReference docRef = db.collection("users").document(getEmail);
        docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                dayTitle.setText(str_StopDays + "일 동안,");
                drinkFrequencyTitle.setText(String.format("%.1f", drinkFrequecny) + " 번의 음주를 쉬었습니다.");
                countBottlesTitle.setText(String.format("%.1f", bottleTotal) + " 병을 마시지 않았습니다.");
                saveTimeTitle.setText(String.format("%.1f", saveTime) + " 시간을 아꼈습니다.");
                saveMoneyTitle.setText(str_getSaveMoney + " 원을 아꼈습니다.");
                tooltipTextView.setText(String.format("%.1f",saveKcal) + " 칼로리를 참았습니다.");

                str_drinkBank = documentSnapshot.getString("drink_bank");
                    System.out.println(str_drinkBank);
                    goalMoneyText.setText(str_drinkBank);
                    goalMoneyText.setTypeface(null, Typeface.BOLD);
                    saveMoneyText.setText("총 " + str_getSaveMoney + "원");

                // 버튼 눌러서 목표 금액 받아오기
                goalMoneyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        str_goalText = goalMoneyText.getText().toString();
                        Drink_Alert_Scanner alert = new Drink_Alert_Scanner(Drink_Statistics.this, str_goalText, getEmail);
                        alert.callFunction();
                        alert.setModifyReturnListener(new Drink_Alert_Scanner.ModifyReturnListener() {
                            @Override
                            public void afterModify(String text) {
                                goalMoneyText.setText(text);
                            }
                        });
                    }
                });
                // 진행률 계산
                str_getSaveMoney = str_getSaveMoney.replaceAll("[^0-9]","");
                int saveMoney = Integer.parseInt(str_getSaveMoney);
                System.out.println(str_drinkBank);
                int goalMoney = Integer.parseInt(str_drinkBank);
                int progress =(int)(((double)saveMoney/goalMoney) * 100);

                progressRatio.setText("진행률 : "+progress+"%");
                // 프로그래스 바 1
                progressBar1 = findViewById(R.id.progressbar1);
                progressBar1.setProgress(progress);

                if(progress >= 100)
                    cheerText.setText("축하합니다! \n목표금액을 달성하셨습니다!");
                else
                    cheerText.setText("티끌 모아 태산! \n목표까지 달려봐요!");

            }
        });

        // 금주 / 금연 기간 별 효과 표시
        textDays = findViewById(R.id.Days);
        textView1 = findViewById(R.id.text1);
        textView2 = findViewById(R.id.text2);
        textView3 = findViewById(R.id.text3);
        textView4 = findViewById(R.id.text4);
        textView5 = findViewById(R.id.text5);
        textView6 = findViewById(R.id.text6);
        userNameTitle = findViewById(R.id.userNameTitle);
        guideText1 = findViewById(R.id.guidetext1);
        guideText2 = findViewById(R.id.guidetext2);
        guideText3 = findViewById(R.id.guidetext3);
        btnBack = findViewById(R.id.back_main);
        btnBack2 = findViewById(R.id.back_main2);

        getName = intent.getStringExtra("userName");

        userNameTitle.setText(getName + " 님은");

        textDays.setText(str_StopDays + " 일 동안 금주하셨네요");

        // 뒤로 가기 버튼 눌렀을 때 (첫 번째 화면)
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 뒤로 가기 버튼 눌렀을 때 (두 번째 화면)
        btnBack2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if(stopDays > 0) {
            textView1.setEnabled(true);
            textView2.setEnabled(true);
            textView3.setEnabled(true);
        }
        if(stopDays > 90) {
            textView4.setEnabled(true);
            guideText1.setVisibility(View.GONE);
        }
        if(stopDays > 180){
            textView5.setEnabled(true);
            guideText2.setVisibility(View.GONE);
        }
        if(stopDays > 365){
            textView6.setEnabled(true);
            guideText3.setVisibility(View.GONE);
        }

        // 원형 프로그래스바 설정
        progressBar2 = findViewById(R.id.progressbar2);
        currentDays = findViewById(R.id.currentDay);
        currentDays.setText("현재 "+str_StopDays+"일");
        progressBar2.setProgress(Integer.parseInt(str_StopDays));

        // initialize tooltip manager
        toolTipsManager = new ToolTipsManager(this);
        tooltip.setOnClickListener(this);
        tooltipTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolTipsManager.dismissAll();
            }
        });
    }
    @Override
    public void onTipDismissed(View view, int anchorViewId, boolean byUser) {
        // Check condition
        if(byUser) {
            // When user dismiss the tooltip
            // Display toast
            Toast.makeText(getApplicationContext(),"Dismissed",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onClick(View view){
        // Check condition
                int position = ToolTip.POSITION_ABOVE;
                int align = ToolTip.ALIGN_LEFT;
                displayTooltip(position,align);
    }
    private void displayTooltip(int position, int align) {
        String message = "소주 한 병의 칼로리는 408kcal입니다.";
        toolTipsManager.findAndDismiss(tooltipTextView);
        if(!message.isEmpty()) {
            ToolTip.Builder builder = new ToolTip.Builder(this, tooltip, linearLayout, message, position);
            builder.setAlign(align);
            builder.setBackgroundColor(Color.BLUE);
            toolTipsManager.show(builder.build());
        }else{
            Toast.makeText(getApplicationContext(),"입력해주세요",Toast.LENGTH_SHORT).show();
        }
    }
}