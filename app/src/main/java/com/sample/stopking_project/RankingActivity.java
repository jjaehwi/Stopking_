package com.sample.stopking_project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;


public class RankingActivity extends AppCompatActivity implements View.OnClickListener {


    private final int FRAGMENT_DAY = 1;
    private final int FRAGMENT_BOTTLE = 2;
    private ImageView backButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // 파이어스토어
    private String getEmail;
    private String my_ranking_name;
    private String getName;
    private String getDay;
    private String getBottle;
    private Button btn_stop_drink_day, btn_stop_drink_bottle;
    boolean btn_day_active = true;
    boolean btn_bottle_active = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);


        mAuth = FirebaseAuth.getInstance();
        backButton = findViewById(R.id.btn_back);
        String[][] string_array = new String[110][3];


        //현석 - 정렬 가능했던 코드 (name 는 내가 정렬하고자 하는 대상 필드를 의미)
        //정렬해서 가져와보기
//        CollectionReference citiesRef = db.collection("users");
//        citiesRef.orderBy("name").limit(3)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d("RANKING ACTIVITY", document.getId() + " => " + document.getData());
//                                text.setText(document.getData());
//                            }
//                        } else {
//                            Log.d("RANKING ACTIVITY", "Error getting documents: ", task.getException());
//                        }
//                    }
//                });

        // 파이어베이스 자체 기능 사용해서 로그 찍어 본 부분입니다.
        // 지우셔도 무방합니다
        CollectionReference colRef = db.collection("users");
        colRef
                .whereGreaterThan("stop_drink", new Date().toString())
                .orderBy("stop_drink", Query.Direction.DESCENDING)
                .limit(100)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int i = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getString("stop_drink") != null) {
                                    string_array[i][0] = document.getString("email");
                                    string_array[i][1] = document.getString("name");
                                    string_array[i][2] = document.getString("stop_drink");
                                    Log.d("superdroid", string_array[i][0] + " " + string_array[i][1] + " " + string_array[i][2]);
                                }
                                i++;
                            }
                        } else {
                            Log.d("superdroid", "Error getting documents: ", task.getException());
                        }
                    }
                });
        // 로그 확인해보시면 정렬이 이상하게 됩니다..


        Intent intent = getIntent(); //전달할 데이터를 받을 intent
        getEmail = intent.getStringExtra("email");
        getName = intent.getStringExtra("name");
        getDay = intent.getStringExtra("day");
        getBottle = intent.getStringExtra("bottle");


        Bundle bundle = new Bundle(); // 프라그먼트에 넘겨줄 정보들
        bundle.putString("email", getEmail);
        bundle.putString("name", getName);
        bundle.putString("day", getDay);
        bundle.putString("bottle", getBottle);

        btn_stop_drink_day = (Button) findViewById(R.id.btn_stop_drink_day);
        btn_stop_drink_bottle = (Button) findViewById(R.id.btn_stop_drink_bottle);

        btn_stop_drink_day.setOnClickListener(this);
        btn_stop_drink_bottle.setOnClickListener(this);
        callFragment(FRAGMENT_DAY, bundle);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 해당 액티비티 종료 후 전 화면으로 이동.
                finish();
            }
        });


    }


    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putString("email", getEmail);
        bundle.putString("name", getName);
        bundle.putString("day", getDay);
        bundle.putString("bottle", getBottle);

        switch (v.getId()) {
            case R.id.btn_stop_drink_day:
                // '버튼DAY' 클릭 시 '프래그먼트DAY' 호출 및 버튼 색 변경


                callFragment(FRAGMENT_DAY, bundle);
                if (btn_bottle_active) {
                    btn_stop_drink_day.setBackgroundResource(R.drawable.remove_btn_padding_active);
                    btn_stop_drink_bottle.setBackgroundResource(R.drawable.remove_btn_padding);
                    btn_bottle_active = false;
                    btn_day_active = true;
                }
                break;

            case R.id.btn_stop_drink_bottle:
                // '버튼BOTTLE' 클릭 시 '프래그먼트BOTTLE' 호출 및 버튼 색 변경
                callFragment(FRAGMENT_BOTTLE, bundle);
                if (btn_day_active) {
                    btn_stop_drink_day.setBackgroundResource(R.drawable.remove_btn_padding);
                    btn_stop_drink_bottle.setBackgroundResource(R.drawable.remove_btn_padding_active);
                    btn_day_active = false;
                    btn_bottle_active = true;
                }
                break;
        }
    }


    private void callFragment(int fragment_no, Bundle bundle) {

        // 프래그먼트 사용을 위해
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        switch (fragment_no) {
            case 1:
                // '프래그먼트1' 호출

                FragmentDay fragmentDay = new FragmentDay();
                fragmentDay.setArguments(bundle);
                fragmentTransaction.replace(R.id.fragment_container, fragmentDay);
                fragmentTransaction.commit();
                break;

            case 2:
                // '프래그먼트2' 호출
                FragmentBottle fragmentBottle = new FragmentBottle();
                fragmentBottle.setArguments(bundle);
                fragmentTransaction.replace(R.id.fragment_container, fragmentBottle);
                fragmentTransaction.commit();
                break;
        }

    }


}