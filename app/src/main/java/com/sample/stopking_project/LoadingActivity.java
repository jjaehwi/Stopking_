package com.sample.stopking_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.metrics.Event;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

public class LoadingActivity extends AppCompatActivity {
    private ImageView iv_drink;
    private ImageView iv_smoke;
    private TextView text;
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // 파이어스토어
    private String userEmail;
    private int flag; // 0이면 drink, 1이면 smoke 액티비티로 이동.
    private String drink = "drink";
    private String smoke = "smoke";
    private int random;
    private String ran_str;
    private String stop_drink;
    private String stop_smoke;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        iv_drink = findViewById(R.id.drink_gif);
        iv_smoke = findViewById(R.id.smoke_gif);
        text = findViewById(R.id.tv_text);
        Glide.with(this).load(R.raw.no_drink).into(iv_drink);
        Glide.with(this).load(R.raw.no_smoke1).into(iv_smoke);

        // 현재 로그인한 사용자 가져오기.
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser != null) {
            //로그인 한 사용자가 존재할 경우.
            userEmail = fbUser.getEmail();
        }

        //무작위 난수 추출 코드 삽입하여야 함.
        random = (int)(Math.random() * 23 + 1);
        Log.d("TEST", "random : "+random);
        ran_str = Integer.toString(random);

        DocumentReference docRef = db.collection("users").document(userEmail);
        docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                flag = Objects.equals(value.getString("flag"), drink) ? 0 : 1;
                if (flag==0)
                {
                    setStopText(drink);
                    loadingStart();
                }
                else if (flag==1)
                {
                    setStopText(smoke);
                    loadingStart();
                }
            }
        });
    }

    private void setStopText(String type)
    {
        DocumentReference doc = db.collection("stop").document(type);
        doc.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (type.equals(drink))
                {
                    stop_drink = value.getString(ran_str);
                    text.setText(stop_drink);
                }
                else if(type.equals(smoke))
                {
                    stop_smoke = value.getString(ran_str);
                    text.setText(stop_smoke);
                }
            }
        });
    }

    private void loadingStart() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (flag == 0)
                {
                    Intent intent = new Intent(LoadingActivity.this, Drink_MainActivity.class);
                    startActivity(intent);
                    finish(); // 현재 액티비티 파괴
                    Toast.makeText(LoadingActivity.this, "금주를 환영합니다!", Toast.LENGTH_SHORT).show();
                }
                else if (flag==1)
                {
                    Intent intent = new Intent(LoadingActivity.this, Smoke_MainActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(LoadingActivity.this, "금연을 환영합니다!", Toast.LENGTH_SHORT).show();
                }
            }
        },3000);
    }
}