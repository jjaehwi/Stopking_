package com.sample.stopking_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.protobuf.Any;
import com.tomergoldst.tooltips.ToolTip;
import com.tomergoldst.tooltips.ToolTipsManager;

import org.checkerframework.checker.regex.qual.Regex;
import org.checkerframework.framework.qual.Covariant;

import java.util.HashMap;
import java.util.Map;

public class Statistics extends AppCompatActivity implements ToolTipsManager.TipListener, View.OnClickListener {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    ImageButton tooltip;
    ToolTipsManager toolTipsManager;
    TextView tooltipTextView, saveMoneyText, goalMoneyText, progressRatio, cheerText;
    Button goalMoneyButton;
    TextView textView1, textView2, textView3, textView4, textView5, textView6;
    TextView textDays, currentDays, guideText1, guideText2, guideText3;
    RelativeLayout linearLayout;
    ProgressBar progressBar1, progressBar2;
    ImageView btnBack, btnBack2;
    private String getEmail;
    private String getSaveMoney;
    private String str_drinkBank;
    private String goalText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

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
        getSaveMoney = intent.getStringExtra("saveMoney"); // bank_info_text를 받아옴 String

        // 절약 금액 텍스트
        saveMoneyText = findViewById(R.id.saveMoneyText);
        System.out.println("절약금액: "+getSaveMoney);
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
                str_drinkBank = documentSnapshot.getString("drink_bank");
                    System.out.println(str_drinkBank);
                    goalMoneyText.setText(str_drinkBank);
                    goalMoneyText.setTypeface(null, Typeface.BOLD);
                    saveMoneyText.setText("총 " + getSaveMoney + "원");

                // 버튼 눌러서 목표 금액 받아오기
                goalMoneyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goalText = goalMoneyText.getText().toString();
                        alert_scaner alert = new alert_scaner(Statistics.this,goalText, getEmail);
                        alert.callFunction();
                        alert.setModifyReturnListener(new alert_scaner.ModifyReturnListener() {
                            @Override
                            public void afterModify(String text) {
                                goalMoneyText.setText(text);
                            }
                        });
                    }
                });

                docRef.update("drink_bank",goalMoneyText.getText().toString());
                // 진행률 계산
                getSaveMoney = getSaveMoney.replaceAll("[^0-9]","");
                int saveMoney = Integer.parseInt(getSaveMoney);
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
        guideText1 = findViewById(R.id.guidetext1);
        guideText2 = findViewById(R.id.guidetext2);
        guideText3 = findViewById(R.id.guidetext3);
        btnBack = findViewById(R.id.back_main);
        btnBack2 = findViewById(R.id.back_main2);

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

        String dayStr = textDays.getText().toString();
        dayStr = dayStr.replaceAll("[^0-9]","");
        int dayCount = Integer.parseInt(dayStr);
        System.out.println(dayCount);

        if(dayCount > 0) {
            textView1.setEnabled(true);
            textView2.setEnabled(true);
            textView3.setEnabled(true);
        }
        if(dayCount > 90) {
            textView4.setEnabled(true);
            guideText1.setVisibility(View.GONE);
        }
        if(dayCount > 180){
            textView5.setEnabled(true);
            guideText2.setVisibility(View.GONE);
        }
        if(dayCount > 365){
            textView6.setEnabled(true);
            guideText3.setVisibility(View.GONE);
        }

        // 원형 프로그래스바 설정
        progressBar2 = findViewById(R.id.progressbar2);
        currentDays = findViewById(R.id.currentDay);
        currentDays.setText("현재 "+dayCount+"일");
        progressBar2.setProgress(dayCount);

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