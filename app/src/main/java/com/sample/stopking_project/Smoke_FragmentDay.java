package com.sample.stopking_project;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public  class Smoke_FragmentDay extends Fragment {
    private String getEmail;
    private String getName;
    private String getDay;
    private String getRank;
    private RecyclerView recyclerView;
    private SmokeDayRankingAdapter adapter;
    private long documentCount;
    public int my_rank = 1;
    private int count = 0;


    public Smoke_FragmentDay() {
        // Required empty public constructor
    }




    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference dbReference;
    private ArrayList<SmokeDayFirebaseData> list = new ArrayList<>();

    public Date convertStringtoDate(String Date) { // ???????????????????????? ????????? ?????? ??????
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
        View v = inflater.inflate(R.layout.fragment_smoke_day, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.smoke_day_recyclerView);
        TextView my_ranking_rank = v.findViewById(R.id.smoke_my_day_ranking_rank);
        TextView my_ranking_position = v.findViewById(R.id.smoke_my_day_ranking_position);


        recyclerView.setHasFixedSize(true);
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser != null) {
            //????????? ??? ???????????? ????????? ??????.
            getEmail = fbUser.getEmail();
        }


        dbReference = db.collection("users");


        // ?????? ?????? ??????, ???????????? ?????? ?????????
        dbReference.orderBy("stop_smoke")
                .whereNotEqualTo("stop_smoke",null)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            my_rank=1;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getString("email").compareTo(getEmail) == 0) {
                                    getRank = String.valueOf(my_rank);
                                    my_ranking_rank.setText(getRank + "???");
                                    getName = document.getString("name");
                                    break;
                                }
                                my_rank++;
                            }
                        } else {
                            Log.d("RANKING ACTIVITY", "????????? ??????: ", task.getException()); // ????????? ??????
                        }
                    }
                });

        AggregateQuery countQuery = db.collection("users")
                .whereNotEqualTo("stop_smoke",null)
                .count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                AggregateQuerySnapshot snapshot = task.getResult();
                documentCount = snapshot.getCount();
                my_ranking_position.setText("?????? " + String.valueOf((int)(((double)my_rank/(double)documentCount)*100)) + "%");
            } else {
                Log.d("superdroid", "Count failed: ", task.getException());
            }
        });

        TextView first_rank_name = v.findViewById(R.id.smoke_day_first_ranking_name);
        TextView first_rank_day = v.findViewById(R.id.smoke_first_ranking_day);
        TextView second_rank_name = v.findViewById(R.id.smoke_day_second_ranking_name);
        TextView second_rank_day = v.findViewById(R.id.smoke_second_ranking_day);
        TextView third_rank_name = v.findViewById(R.id.smoke_third_ranking_name);
        TextView third_rank_day = v.findViewById(R.id.smoke_third_ranking_day);

        // 1,2,3??? ????????? ??????
        dbReference.orderBy("stop_smoke")
                .limit(100)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            count = 0;
                            //?????????????????? ????????????????????? ???????????? ???????????? ???
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getString("stop_smoke")==null) continue;
                                else if (count == 0) {
                                    Log.d("ranking",document.getString("stop_smoke")+document.getString("name")  + "???");

                                    first_rank_name.setText(document.getString("name"));
                                    first_rank_day.setText(
                                            String.valueOf(
                                                    caculateDay(
                                                            convertStringtoDate(
                                                                    document.getString("stop_smoke")))) + "???");
                                } else if (count == 1) {
                                    second_rank_name.setText(document.getString("name"));
                                    second_rank_day.setText(
                                            String.valueOf(
                                                    caculateDay(
                                                            convertStringtoDate(
                                                                    document.getString("stop_smoke")))) + "???");

                                } else if (count == 2) {
                                    third_rank_name.setText(document.getString("name"));
                                    third_rank_day.setText(
                                            String.valueOf(
                                                    caculateDay(
                                                            convertStringtoDate(
                                                                    document.getString("stop_smoke")))) + "???");
                                }
                                count++;
                            }
                        } else {
                            Log.d("RANKING ACTIVITY", "????????? ??????: ", task.getException()); // ????????? ??????
                        }
                    }
                });

        // recyclerView??? ?????? ????????? ????????? fetch
        dbReference.orderBy("stop_smoke")
                .limit(100)
                .whereNotEqualTo("stop_smoke",null)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //?????????????????? ????????????????????? ???????????? ???????????? ???
                            list.clear(); // ?????? ?????? ???????????? ???????????? ????????? ?????????.
                            count = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                if (count >= 3) {
//                                    if (document.getString("email").compareTo(getEmail) == 0) {
//                                        getRank = String.valueOf(my_rank);
//                                        my_ranking_rank.setText(getRank + "???");
//                                        Log.d("compare", "True");
//                                    }
//                                    my_rank++;
                                    SmokeDayFirebaseData fbdata = document.toObject(SmokeDayFirebaseData.class); //fbData ????????? ???????????? ?????????.
                                    list.add(fbdata); // ?????? ??????????????? ?????????????????? ?????? ????????????????????? ?????? ????????? ??????.
                                }
                                count++;
                            }
                            adapter.notifyDataSetChanged(); // ????????? ?????? ??? ????????????
                        } else {
                            Log.d("RANKING ACTIVITY", "????????? ??????: ", task.getException()); // ????????? ??????
                        }
                    }
                });
        adapter = new SmokeDayRankingAdapter(list, getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        Bundle extra = getArguments(); // ???????????? ?????? ????????????
        if (extra != null) {
            getEmail = extra.getString("email");
            getName = extra.getString("name");
            getDay = extra.getString("day");
        }

        TextView my_ranking_name = v.findViewById(R.id.smoke_my_day_ranking_name);
        TextView my_ranking_day = v.findViewById(R.id.smoke_my_ranking_day);
        my_ranking_name.setText(getName);
        my_ranking_day.setText(getDay + "???");

        // Inflate the layout for this fragment
        return v;
    }
}




