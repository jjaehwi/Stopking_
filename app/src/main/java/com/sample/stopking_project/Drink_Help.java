package com.sample.stopking_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Drink_Help extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Drink_Help_Item> list = new ArrayList();
    private View view;
    private ImageView backButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
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

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        //AsyncTask 작동시킴(파싱)
        new Description().execute();
    }

    private class Description extends AsyncTask<Void, Void, Void> {
        // 진행바 표시
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //진행다일로그 시작
            progressDialog = new ProgressDialog(Drink_Help.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("잠시 기다려 주세요.");
            progressDialog.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document doc = Jsoup.connect("https://www.nosmokeguide.go.kr/lay2/bbs/S1T33C110/H/23/list.do").get();
                Elements mElementDataSize = doc.select("div[class=thumb_style_3] li");
                int mElementSize = mElementDataSize.size();

                for (Element elem : mElementDataSize) {
                    String my_title = elem.select("li p[class=tit]").text();
                    String my_summary = elem.select("li p[class=txt]").text();
                    String my_imgUrl = elem.select("div[class=thumb_style_3] li").attr("img src");
                    String my_link = elem.select("li a").attr("href");
                    list.add(new Drink_Help_Item(my_title, my_imgUrl, my_link, my_summary));
                }
                Log.d("debug :", "List " + mElementDataSize);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            Drink_CustomAdapter drink_customAdapter = new Drink_CustomAdapter(list);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(drink_customAdapter);
            progressDialog.dismiss();
        }
    }
}
//        // 테스트를 위한 더미 데이터 생성
//        ArrayList<String> testDataSet = new ArrayList<>();
//        for(int i = 0; i<20; i++){
//            testDataSet.add("TEST DATA"+i);
//        }
//
//        RecyclerView recyclerView = findViewById(R.id.recyclerView);
//
//        // LayoutManager 는 아래 3가지 중 하나를 선택하여 사용
//        // 1) LinearLayoutManager()
//        // 2) GridLayoutManager()
//        // 3) StaggeredGridLayoutManager()
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager((Context) this);
//        recyclerView.setLayoutManager(linearLayoutManager); // LayoutManager 설정
//
//        Drink_CustomAdapter drink_customAdapter = new Drink_CustomAdapter(testDataSet);
//        recyclerView.setAdapter(drink_customAdapter);
