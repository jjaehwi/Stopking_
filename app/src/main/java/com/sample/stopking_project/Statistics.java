package com.sample.stopking_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Color;
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

import com.tomergoldst.tooltips.ToolTip;
import com.tomergoldst.tooltips.ToolTipsManager;

import org.checkerframework.checker.regex.qual.Regex;
import org.checkerframework.framework.qual.Covariant;

public class Statistics extends AppCompatActivity implements ToolTipsManager.TipListener, View.OnClickListener {

    ImageButton tooltip;
    ToolTipsManager toolTipsManager;
    TextView tooltipTextView, textView1, textView2, textView3, textView4, textView5, textView6;
    TextView textDays, currentDays, guideText1, guideText2, guideText3;
    RelativeLayout linearLayout;
    ProgressBar progressBar;
    ImageView btnBack, btnBack2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        TabHost tabHost = findViewById(R.id.host); tabHost.setup();
        TabHost.TabSpec spec = tabHost.newTabSpec("tab1");
        spec.setIndicator(null, ResourcesCompat.getDrawable(getResources(), R.drawable.tab_icon1, null));
        spec.setContent(R.id.tab_content1);
        tabHost.addTab(spec);
        spec = tabHost.newTabSpec("tab2");
        spec.setIndicator(null, ResourcesCompat.getDrawable(getResources(), R.drawable.tab_icon2, null));
        spec.setContent(R.id.tab_content2);
        tabHost.addTab(spec);

        tooltip = findViewById(R.id.tooltip);
        tooltipTextView = findViewById(R.id.tooltipTextView);
        linearLayout = findViewById(R.id.linearlayout);

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
        progressBar = findViewById(R.id.progressbar);
        currentDays = findViewById(R.id.currentDay);
        currentDays.setText("현재 "+dayCount+"일");
        progressBar.setProgress(dayCount);

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