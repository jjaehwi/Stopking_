package com.sample.stopking_project;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
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

import java.text.DecimalFormat;

public class Drink_Statistics extends AppCompatActivity implements ToolTipsManager.TipListener, View.OnClickListener {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    ImageButton tooltip;
    ToolTipsManager toolTipsManager;
    TextView userNameTitle1, dayTitle, drinkFrequencyTitle, countBottlesTitle, saveTimeTitle, saveMoneyTitle;
    TextView tooltipTextView, saveMoneyText, goalMoneyText, progressRatio, cheerText;
    Button goalMoneyButton;
    TextView textView1, textView2, textView3, textView4, textView5, textView6, userNameTitle2;
    TextView textDays, currentDays, guideText1, guideText2, guideText3;
    RelativeLayout linearLayout;
    ProgressBar progressBar1, progressBar2;
    ImageView btnBack, btnBack2;
    Bitmap bitmap;
    private String getEmail; // 유저 로그인할때의 이메일
    private String str_getSaveMoney; // 현재까지 모인 금액
    private String str_drinkBank; // 목표 금액
    private String str_goalText; // 목표 금액 텍스트
    private String str_WeekDrink; // 일주일에 술자리 몇 번 있는지
    private String str_Bottles; // 술자리 한번 당 마시는 평균 주량
    private String str_StopDays; // 금주 시작 일자로 부터 몇일 지났는지
    private String getName;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel"; // 채널 id
    private NotificationManager mNotificationManager;
    private static final int NOTIFICATION_ID = 0;
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
        drinkFrequecny = Math.round(((stopDays / 7) * weekDrink));
        bottleTotal = drinkFrequecny*bottles;
        saveTime = drinkFrequecny * 3;
        saveKcal = bottleTotal * 408;

        // 이름 텍스트
        userNameTitle1 = findViewById(R.id.userNameTitle1);
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

                userNameTitle1.setText(Html.fromHtml("<b>"+getName+"</b>"+" 님은"));
                dayTitle.setText(Html.fromHtml("<b>"+str_StopDays+"일</b>"+ " 동안,"));
                drinkFrequencyTitle.setText(Html.fromHtml("<b>"+(Math.round(drinkFrequecny)+" 번</b>"+" 의 음주를 쉬었습니다.")));
                countBottlesTitle.setText(Html.fromHtml("<b>"+(Math.round(bottleTotal)+" 병</b>"+" 을 마시지 않았습니다.")));
                saveTimeTitle.setText(Html.fromHtml("<b>"+(Math.round(saveTime)+" 시간</b>" + " 을 아꼈습니다.")));
                saveMoneyTitle.setText(Html.fromHtml("<b>"+(str_getSaveMoney +" 원</b>" +" 을 아꼈습니다.")));
                tooltipTextView.setText(Html.fromHtml("<b>"+(Math.round(saveKcal)+" kcal</b>" + " 를 참았습니다.")));

                str_drinkBank = documentSnapshot.getString("drink_bank");
                    DecimalFormat formatter = new DecimalFormat("###,###,###");// 수에 콤마 넣기
                    int drinkBank_Money = Integer.parseInt(str_drinkBank);
                    goalMoneyText.setText(formatter.format(drinkBank_Money));
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
                double progress =(((double)saveMoney/goalMoney) * 100);

                progressRatio.setText("진행률 : "+String.format("%.1f",progress)+"%");
                // 프로그래스 바 1
                progressBar1 = findViewById(R.id.progressbar1);
                progressBar1.setProgress((int)progress);
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.attach_money);

                if(progress >= 100) {
                    cheerText.setText("축하합니다! \n목표금액을 달성하셨습니다!");
                    createNotificationChannel();
                    sendNotification();

                }
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
        userNameTitle2 = findViewById(R.id.userNameTitle2);
        guideText1 = findViewById(R.id.guidetext1);
        guideText2 = findViewById(R.id.guidetext2);
        guideText3 = findViewById(R.id.guidetext3);
        btnBack = findViewById(R.id.back_main);
        btnBack2 = findViewById(R.id.back_main2);

        getName = intent.getStringExtra("userName");

        userNameTitle2.setText(Html.fromHtml("<b>"+getName+"</b>"+" 님은"));

        textDays.setText(Html.fromHtml("<b>"+str_StopDays + "</b>" +" 일 동안 금주하셨네요"));

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
            textView1.setTextColor(Color.BLACK);
            textView2.setTextColor(Color.BLACK);
            textView3.setTextColor(Color.BLACK);
        }
        if(stopDays > 90) {
            textView4.setEnabled(true);
            textView4.setTextColor(Color.BLACK);
            guideText1.setVisibility(View.GONE);
        }
        if(stopDays > 180){
            textView5.setEnabled(true);
            textView5.setTextColor(Color.BLACK);
            guideText2.setVisibility(View.GONE);
        }
        if(stopDays > 365){
            textView6.setEnabled(true);
            textView6.setTextColor(Color.BLACK);
            guideText3.setVisibility(View.GONE);
        }

        // 원형 프로그래스바 설정
        progressBar2 = findViewById(R.id.progressbar2);
        currentDays = findViewById(R.id.currentDay);
        currentDays.setText("현재 "+str_StopDays+"일");
        double progressCalculate = ((double)(Integer.parseInt(str_StopDays) / (double)365)*100);
        progressBar2.setProgress((int)progressCalculate);

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


    public void createNotificationChannel()
    {
        //notification manager 생성
        mNotificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        // 기기의 SDK 버전 확인 ( SDK 26 버전 이상인지)
        if(android.os.Build.VERSION.SDK_INT
                >= android.os.Build.VERSION_CODES.O){
            //Channel 정의 생성자( construct 이용 )
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID
                    ,"Goal Notification",mNotificationManager.IMPORTANCE_HIGH);
            //Channel에 대한 기본 설정
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification");
            // Manager을 이용하여 Channel 생성
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

    }



    // Notification Builder를 만드는 메소드
    private NotificationCompat.Builder getNotificationBuilder() {
        PendingIntent mPendingIntent = PendingIntent.getActivity(
                Drink_Statistics.this,
                0, // 보통 default값 0을 삽입
                new Intent(getApplicationContext(),Drink_Statistics.class)
                        .putExtra("email",getEmail)
                        .putExtra("saveMoney",str_getSaveMoney)
                        .putExtra("weekDrink",str_WeekDrink)
                        .putExtra("Bottles",str_Bottles)
                        .putExtra("stopDays",str_StopDays)
                ,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.attach_money)
                .setLargeIcon(bitmap)
                .setContentTitle("목표 금액을 달성했어요!")
                .setContentText("목표 금액을 다시 설정해볼까요?")
                .setContentIntent(mPendingIntent);

        return notifyBuilder;
    }

    // Notification을 보내는 메소드
    public void sendNotification(){
        // Builder 생성
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        // Manager를 통해 notification 디바이스로 전달
        mNotificationManager.notify(NOTIFICATION_ID,notifyBuilder.build());
    }



}