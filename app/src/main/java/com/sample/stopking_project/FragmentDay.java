package com.sample.stopking_project;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;



public  class FragmentDay extends Fragment {
    private String getEmail;
    private String getName;
    private String getDay;
    private String getRank;
    private RecyclerView recyclerView;
    private DrinkDayRankingAdapter adapter;
    private long documentCount;
    public int my_rank = 1;
    private int count = 0;


    public FragmentDay() {
        // Required empty public constructor
    }




    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference dbReference;
    private ArrayList<DrinkDayFirebaseData> list = new ArrayList<>();

    public Date convertStringtoDate(String Date) { // 데이터베이스에서 가져온 날짜 변환
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date date = null;
        try {
            date = format.parse(Date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public int caculateDay(Date stop_date) {
        Date startDateValue = stop_date;
        Date now = new Date();
        long diff = now.getTime() - startDateValue.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = (hours / 24) + 1;
        String d = String.valueOf(days);
        return Integer.parseInt(d);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fragment_day, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.drink_day_recyclerView);
        TextView my_ranking_rank = v.findViewById(R.id.drink_my_day_ranking_rank);
        TextView my_ranking_position = v.findViewById(R.id.drink_my_day_ranking_position);


        recyclerView.setHasFixedSize(true);
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser != null) {
            //로그인 한 사용자가 존재할 경우.
            getEmail = fbUser.getEmail();
        }


        dbReference = db.collection("users");


        // 나의 랭킹 찾기, 데이터를 모두 가져옴
        dbReference.orderBy("stop_drink")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            my_rank=1;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getString("email").compareTo(getEmail) == 0) {
                                    getRank = String.valueOf(my_rank);
                                    my_ranking_rank.setText(getRank + "위");
                                    break;
                                }
                                my_rank++;
                            }
                        } else {
                            Log.d("RANKING ACTIVITY", "에러문 출력: ", task.getException()); // 에러문 출력
                        }
                    }
                });

        AggregateQuery countQuery = db.collection("users").count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                AggregateQuerySnapshot snapshot = task.getResult();
                documentCount = snapshot.getCount();
                my_ranking_position.setText("상위 " + String.valueOf((int)(((double)my_rank/(double)documentCount)*100)) + "%");
            } else {
                Log.d("superdroid", "Count failed: ", task.getException());
            }
        });

        TextView first_rank_name = v.findViewById(R.id.drink_day_first_ranking_name);
        TextView first_rank_day = v.findViewById(R.id.drink_first_ranking_day);
        TextView second_rank_name = v.findViewById(R.id.drink_day_second_ranking_name);
        TextView second_rank_day = v.findViewById(R.id.drink_second_ranking_day);
        TextView third_rank_name = v.findViewById(R.id.drink_third_ranking_name);
        TextView third_rank_day = v.findViewById(R.id.drink_third_ranking_day);

        // 1,2,3등 데이터 설정
        dbReference.orderBy("stop_drink")
                .limit(100)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            count = 0;
                            //파이어베이스 데이터베이스의 데이터를 받아오는 곳
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (count == 0) {
                                    first_rank_name.setText(document.getString("name"));
                                    first_rank_day.setText(
                                            String.valueOf(
                                                    caculateDay(
                                                            convertStringtoDate(
                                                                    document.getString("stop_drink")))) + "일");
                                } else if (count == 1) {
                                    second_rank_name.setText(document.getString("name"));
                                    second_rank_day.setText(
                                            String.valueOf(
                                                    caculateDay(
                                                            convertStringtoDate(
                                                                    document.getString("stop_drink")))) + "일");

                                } else if (count == 2) {
                                    third_rank_name.setText(document.getString("name"));
                                    third_rank_day.setText(
                                            String.valueOf(
                                                    caculateDay(
                                                            convertStringtoDate(
                                                                    document.getString("stop_drink")))) + "일");
                                }
                                count++;
                            }
                        } else {
                            Log.d("RANKING ACTIVITY", "에러문 출력: ", task.getException()); // 에러문 출력
                        }
                    }
                });

        // recyclerView에 넣을 나머지 데이터 fetch
        dbReference.orderBy("stop_drink")
                .limit(100)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //파이어베이스 데이터베이스의 데이터를 받아오는 곳
                            list.clear(); // 기존 배열 리스트가 존재하지 않도록 초기화.
                            count = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (count >= 3) {
//                                    if (document.getString("email").compareTo(getEmail) == 0) {
//                                        getRank = String.valueOf(my_rank);
//                                        my_ranking_rank.setText(getRank + "위");
//                                        Log.d("compare", "True");
//                                    }
//                                    my_rank++;
                                    DrinkDayFirebaseData fbdata = document.toObject(DrinkDayFirebaseData.class); //fbData 객체에 데이터를 담는다.
                                    list.add(fbdata); // 담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비를 한다.
                                }
                                count++;
                            }
                            adapter.notifyDataSetChanged(); // 리시트 저장 및 새로고침
                        } else {
                            Log.d("RANKING ACTIVITY", "에러문 출력: ", task.getException()); // 에러문 출력
                        }
                    }
                });
        adapter = new DrinkDayRankingAdapter(list, getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        Bundle extra = getArguments(); // 랭킹에서 번들 넘겨받기
        if (extra != null) {
            getEmail = extra.getString("email");
            getName = extra.getString("name");
            getDay = extra.getString("day");
        }

        TextView my_ranking_name = v.findViewById(R.id.drink_my_day_ranking_name);
        TextView my_ranking_day = v.findViewById(R.id.drink_my_ranking_day);
        my_ranking_name.setText(getName);
        my_ranking_day.setText(getDay + "일");

        // Inflate the layout for this fragment
        return v;
    }
}




