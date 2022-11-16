package com.sample.stopking_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DrinkFirebaseTest extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<DrinkFirebaseData> arrayList;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // 파이어스토어
    private CollectionReference dbReference;
    private String getEmail;

    //뒤로가기
    private ImageView backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_firebase_test);

        mAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.recyclerView); // 아아디 연결
        recyclerView.setHasFixedSize(true); // 리사이클러뷰 기존 성능 강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); // 사용자 정보 객체를 담을 arrayList (이후에 어댑터 쪽으로 전송함)

        backBtn = findViewById(R.id.btn_back);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 현재 로그인한 사용자 가져오기.
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser != null)
        {
            //로그인 한 사용자가 존재할 경우.
            getEmail = fbUser.getEmail();
        }

        dbReference = db.collection("users"); //DB 테이블 연결
        dbReference.orderBy("average_drink")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //파이어베이스 데이터베이스의 데이터를 받아오는 곳
                            arrayList.clear(); // 기존 배열 리스트가 존재하지 않도록 초기화.
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("RANKING ACTIVITY", document.getId() + " => " + document.getData());
                                DrinkFirebaseData fbdata = document.toObject(DrinkFirebaseData.class); //fbData 객체에 데이터를 담는다.
                                arrayList.add(fbdata); // 담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비를 한다.
                            }
                            adapter.notifyDataSetChanged(); // 리시트 저장 및 새로고침
                        } else {
                            Log.d("RANKING ACTIVITY", "에러문 출력: ", task.getException()); // 에러문 출력
                        }
                    }
                });

        adapter = new DrinkRankingAdapter(arrayList, this);
        recyclerView.setAdapter(adapter); // 리사이클러뷰에 어댑터 연결
    }
}