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

public class Smoke_Help extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Smoke_Help_Item> list = new ArrayList();
    private View view;

    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smoke_help);
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
            progressDialog = new ProgressDialog(Smoke_Help.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("잠시 기다려 주세요.");
            progressDialog.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document doc = Jsoup.connect("https://www.nosmokeguide.go.kr/lay2/bbs/S1T33C111/H/24/list.do").get();
                Elements mElementDataSize = doc.select("div[class=thumb_style_3] li");
                int mElementSize = mElementDataSize.size();

                for (Element elem : mElementDataSize) {
                    String my_title = elem.select("li p[class=tit]").text();
                    String my_summary = elem.select("li p[class=txt]").text();
                    String my_imgUrl = elem.select("li img[class=slickHover]").attr("src");
                    String my_link = elem.select("div[class=content] a").attr("href");
                    list.add(new Smoke_Help_Item(my_title, my_imgUrl, my_link, my_summary));
                }
                // 데이터 확인
                Log.d("debug :", "List " + mElementDataSize);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            Smoke_CustomAdapter smoke_customAdapter = new Smoke_CustomAdapter(list);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(smoke_customAdapter);
            progressDialog.dismiss();
        }
    }
}