package com.sample.stopking_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class Drink_Help extends AppCompatActivity {

    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_help);

        backButton = findViewById(R.id.back_main);

        // 뒤로 가기 버튼
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 해당 액티비티 종료 후 전 화면으로 이동.
                finish();
            }
        });

        // 테스트를 위한 더미 데이터 생성
        ArrayList<String> testDataSet = new ArrayList<>();
        for(int i = 0; i<20; i++){
            testDataSet.add("TEST DATA"+i);
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        // LayoutManager 는 아래 3가지 중 하나를 선택하여 사용
        // 1) LinearLayoutManager()
        // 2) GridLayoutManager()
        // 3) StaggeredGridLayoutManager()
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager((Context) this);
        recyclerView.setLayoutManager(linearLayoutManager); // LayoutManager 설정

        Drink_CustomAdapter drink_customAdapter = new Drink_CustomAdapter(testDataSet);
        recyclerView.setAdapter(drink_customAdapter);
    }
}