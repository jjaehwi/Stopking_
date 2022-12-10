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

public class Smoke_FragmentPack extends Fragment {
    private String getEmail;
    private String getName;
    private String getPack;
    private String getRank;
    private RecyclerView recyclerView;
    private SmokePackRankingAdapter adapter;
    private long documentCount;
    public int my_rank = 1;
    private int count = 0;


    public Smoke_FragmentPack() {
        // Required empty public constructor
    }


    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference dbReference;
    private ArrayList<SmokePackFirebaseData> list = new ArrayList<>();


    class ObjectSort implements Comparator<SmokePackFirebaseData> {
        @Override
        public int compare(SmokePackFirebaseData d1, SmokePackFirebaseData d2) {
            if (d1.getPacks()<d2.getPacks()) {
                return 1;
            } else if (d1.getPacks()>d2.getPacks()) {
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
        View v = inflater.inflate(R.layout.fragment_smoke_pack, container, false);
        // TODO
        recyclerView = (RecyclerView) v.findViewById(R.id.smoke_pack_recyclerView);
        TextView my_ranking_rank = v.findViewById(R.id.smoke_pack_my_ranking_rank);
        TextView my_ranking_position = v.findViewById(R.id.smoke_pack_my_ranking_position);
        TextView my_ranking_pack = v.findViewById(R.id.smoke_my_ranking_pack);


        recyclerView.setHasFixedSize(true);
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser != null) {
            //로그인 한 사용자가 존재할 경우.
            getEmail = fbUser.getEmail();
        }

        dbReference = db.collection("users");

        TextView first_rank_name = v.findViewById(R.id.smoke_pack_first_ranking_name);
        TextView first_rank_pack = v.findViewById(R.id.smoke_first_ranking_pack);
        TextView first_rank_average = v.findViewById(R.id.smoke_first_week_smoke);
        TextView second_rank_name = v.findViewById(R.id.smoke_pack_second_ranking_name);
        TextView second_rank_pack = v.findViewById(R.id.smoke_second_ranking_pack);
        TextView second_rank_average = v.findViewById(R.id.smoke_second_week_smoke);
        TextView third_rank_name = v.findViewById(R.id.smoke_pack_third_ranking_name);
        TextView third_rank_pack = v.findViewById(R.id.smoke_third_ranking_pack);
        TextView third_rank_average = v.findViewById(R.id.smoke_third_week_smoke);

        dbReference
                .whereNotEqualTo("stop_smoke",null)
                .limit(100)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            list.clear(); // 기존 배열 리스트가 존재하지 않도록 초기화.
                            count = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                    SmokePackFirebaseData fbdata = document.toObject(SmokePackFirebaseData.class); //fbData 객체에 데이터를 담는다.
                                    list.add(fbdata); // 담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비를 한다.
                            }
                            Collections.sort(list, new ObjectSort()); // 배열을 순위에 맞춰 정렬
                            int size = list.size();
                            for(int i=0;i<size;i++){ // 내 정보 표시를 위한 탐색
                                if(list.get(i).getEmail().compareTo(getEmail)==0){
                                    int my_rank=i+1;
                                    my_ranking_rank.setText(my_rank+"위");
                                    my_ranking_position.setText("일주일 "+list.get(i).getWeek_smoke()+"갑 기준");
                                    my_ranking_pack.setText(list.get(i).getDoublePacks()+"갑");
                                    break;
                                }
                            }


                            first_rank_name.setText(list.get(0).getName());
                            first_rank_pack.setText(list.get(0).getDoublePacks() + "갑");
                            first_rank_average.setText("일주일 "+list.get(0).getWeek_smoke()+"갑 기준");
                            list.remove(0); // 리사이클러뷰에 넣을 데이터는 4위부터이므로 제거
                            second_rank_name.setText(list.get(0).getName());
                            second_rank_pack.setText(list.get(0).getDoublePacks() + "갑");
                            second_rank_average.setText("일주일 "+list.get(0).getWeek_smoke()+"갑 기준");
                            list.remove(0);
                            third_rank_name.setText(list.get(0).getName());
                            third_rank_pack.setText(list.get(0).getDoublePacks() + "갑");
                            third_rank_average.setText("일주일 "+list.get(0).getWeek_smoke()+"갑 기준");
                            list.remove(0);
                            adapter.notifyDataSetChanged(); // 리시트 저장 및 새로고침
                        } else {
                            Log.d("RANKING ACTIVITY", "에러문 출력: ", task.getException()); // 에러문 출력
                        }
                    }
                });

        // db에서 email, week_smoke, stop_drink 가져오기




        adapter = new SmokePackRankingAdapter(list, getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        Bundle extra = getArguments(); // 랭킹에서 번들 넘겨받기
        if (extra != null) {
            getEmail = extra.getString("email");
            getName = extra.getString("name");
            getPack = extra.getString("pack");
        }

        TextView my_ranking_name = v.findViewById(R.id.smoke_pack_my_ranking_name);

        my_ranking_name.setText(getName);

        // Inflate the layout for this fragment
        return v;
    }
}