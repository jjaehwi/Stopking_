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
        View v = inflater.inflate(R.layout.fragment_drink_bottle, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.drink_bottle_recyclerView);
        TextView my_ranking_rank = v.findViewById(R.id.drink_bottle_my_ranking_rank);
        TextView my_ranking_position = v.findViewById(R.id.drink_bottle_my_ranking_position);
        TextView my_ranking_bottle = v.findViewById(R.id.drink_my_ranking_bottle);


        recyclerView.setHasFixedSize(true);
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser != null) {
            //????????? ??? ???????????? ????????? ??????.
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
                            list.clear(); // ?????? ?????? ???????????? ???????????? ????????? ?????????.
                            count = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                    DrinkBottleFirebaseData fbdata = document.toObject(DrinkBottleFirebaseData.class); //fbData ????????? ???????????? ?????????.
                                    list.add(fbdata); // ?????? ??????????????? ?????????????????? ?????? ????????????????????? ?????? ????????? ??????.
                            }
                            Collections.sort(list, new ObjectSort()); // ????????? ????????? ?????? ??????
                            int size = list.size();
                            for(int i=0;i<size;i++){ // ??? ?????? ????????? ?????? ??????
                                if(list.get(i).getEmail().compareTo(getEmail)==0){
                                    int my_rank=i+1;
                                    my_ranking_rank.setText(my_rank+"???");
                                    my_ranking_position.setText("?????? ?????? : "+list.get(i).getAverage_drink()+"???");
                                    my_ranking_bottle.setText(list.get(i).getDoubleBottles()+"???");
                                    break;
                                }
                            }


                            first_rank_name.setText(list.get(0).getName());
                            first_rank_bottle.setText(list.get(0).getDoubleBottles() + "???");
                            first_rank_average.setText("?????? ?????? : "+list.get(0).getAverage_drink()+"???");
                            list.remove(0); // ????????????????????? ?????? ???????????? 4?????????????????? ??????
                            second_rank_name.setText(list.get(0).getName());
                            second_rank_bottle.setText(list.get(0).getDoubleBottles() + "???");
                            second_rank_average.setText("?????? ?????? : "+list.get(0).getAverage_drink()+"???");
                            list.remove(0);
                            third_rank_name.setText(list.get(0).getName());
                            third_rank_bottle.setText(list.get(0).getDoubleBottles() + "???");
                            third_rank_average.setText("?????? ?????? : "+list.get(0).getAverage_drink()+"???");
                            list.remove(0);
                            adapter.notifyDataSetChanged(); // ????????? ?????? ??? ????????????
                        } else {
                            Log.d("RANKING ACTIVITY", "????????? ??????: ", task.getException()); // ????????? ??????
                        }
                    }
                });

        // db?????? email, week_bottle, stop_drink ????????????




        adapter = new DrinkBottleRankingAdapter(list, getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        Bundle extra = getArguments(); // ???????????? ?????? ????????????
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