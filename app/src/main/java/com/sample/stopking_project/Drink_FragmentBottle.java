package com.sample.stopking_project;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class Drink_FragmentBottle extends Fragment {
    private String getEmail;
    private String getName;
    private String getBottle;
    private String getRank;
    private RecyclerView recyclerView;
    private DrinkBottleRankingAdapter adapter;
    private long documentCount;
    public int my_rank = 1;
    private int count = 0;


    public Drink_FragmentBottle() {
        // Required empty public constructor
    }


    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference dbReference;
    private ArrayList<DrinkBottleFirebaseData> list = new ArrayList<>();


    class ObjectSort implements Comparator<DrinkBottleFirebaseData> {
        @Override
        public int compare(DrinkBottleFirebaseData d1, DrinkBottleFirebaseData d2) {
            if (d1.getBottles()<d2.getBottles()) {
                return 1;
            } else if (d1.getBottles()>d2.getBottles()) {
                return -1;
            }
            return 0;
        }
    }



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
        View v = inflater.inflate(R.layout.fragment_drink_bottle, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.drink_bottle_recyclerView);
        TextView my_ranking_rank = v.findViewById(R.id.drink_bottle_my_ranking_rank);
        TextView my_ranking_position = v.findViewById(R.id.drink_bottle_my_ranking_position);
        TextView my_ranking_bottle = v.findViewById(R.id.drink_my_ranking_bottle);


        recyclerView.setHasFixedSize(true);
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser != null) {
            //로그인 한 사용자가 존재할 경우.
            getEmail = fbUser.getEmail();
        }

        dbReference = db.collection("users");

        TextView first_rank_name = v.findViewById(R.id.drink_bottle_first_ranking_name);
        TextView first_rank_bottle = v.findViewById(R.id.drink_first_ranking_bottle);
        TextView first_rank_average = v.findViewById(R.id.drink_first_average_drink);
        TextView second_rank_name = v.findViewById(R.id.drink_bottle_second_ranking_name);
        TextView second_rank_bottle = v.findViewById(R.id.drink_second_ranking_bottle);
        TextView second_rank_average = v.findViewById(R.id.drink_second_average_drink);
        TextView third_rank_name = v.findViewById(R.id.drink_bottle_third_ranking_name);
        TextView third_rank_bottle = v.findViewById(R.id.drink_third_ranking_bottle);
        TextView third_rank_average = v.findViewById(R.id.drink_third_average_drink);

        dbReference
                .whereNotEqualTo("stop_drink",null)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            list.clear(); // 기존 배열 리스트가 존재하지 않도록 초기화.
                            count = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                    DrinkBottleFirebaseData fbdata = document.toObject(DrinkBottleFirebaseData.class); //fbData 객체에 데이터를 담는다.
                                    list.add(fbdata); // 담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비를 한다.
                            }
                            Collections.sort(list, new ObjectSort()); // 배열을 순위에 맞춰 정렬
                            int size = list.size();
                            for(int i=0;i<size;i++){ // 내 정보 표시를 위한 탐색
                                if(list.get(i).getEmail().compareTo(getEmail)==0){
                                    int my_rank=i+1;
                                    my_ranking_rank.setText(my_rank+"위");
                                    my_ranking_position.setText("평균 주량 : "+list.get(i).getAverage_drink()+"병");
                                    my_ranking_bottle.setText(list.get(i).getDoubleBottles()+"병");
                                    break;
                                }
                            }


                            first_rank_name.setText(list.get(0).getName());
                            first_rank_bottle.setText(list.get(0).getDoubleBottles() + "병");
                            first_rank_average.setText("평균 주량 : "+list.get(0).getAverage_drink()+"병");
                            list.remove(0); // 리사이클러뷰에 넣을 데이터는 4위부터이므로 제거
                            second_rank_name.setText(list.get(0).getName());
                            second_rank_bottle.setText(list.get(0).getDoubleBottles() + "병");
                            second_rank_average.setText("평균 주량 : "+list.get(0).getAverage_drink()+"병");
                            list.remove(0);
                            third_rank_name.setText(list.get(0).getName());
                            third_rank_bottle.setText(list.get(0).getDoubleBottles() + "병");
                            third_rank_average.setText("평균 주량 : "+list.get(0).getAverage_drink()+"병");
                            list.remove(0);
                            adapter.notifyDataSetChanged(); // 리시트 저장 및 새로고침
                        } else {
                            Log.d("RANKING ACTIVITY", "에러문 출력: ", task.getException()); // 에러문 출력
                        }
                    }
                });

        // db에서 email, week_bottle, stop_drink 가져오기




        adapter = new DrinkBottleRankingAdapter(list, getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        Bundle extra = getArguments(); // 랭킹에서 번들 넘겨받기
        if (extra != null) {
            getEmail = extra.getString("email");
            getName = extra.getString("name");
            getBottle = extra.getString("bottle");
        }

        TextView my_ranking_name = v.findViewById(R.id.drink_bottle_my_ranking_name);

        my_ranking_name.setText(getName);

        // Inflate the layout for this fragment
        return v;
    }
}