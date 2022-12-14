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
        View v = inflater.inflate(R.layout.fragment_smoke_pack, container, false);
        // TODO
        recyclerView = (RecyclerView) v.findViewById(R.id.smoke_pack_recyclerView);
        TextView my_ranking_rank = v.findViewById(R.id.smoke_pack_my_ranking_rank);
        TextView my_ranking_position = v.findViewById(R.id.smoke_pack_my_ranking_position);
        TextView my_ranking_pack = v.findViewById(R.id.smoke_my_ranking_pack);


        recyclerView.setHasFixedSize(true);
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser != null) {
            //????????? ??? ???????????? ????????? ??????.
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
                            list.clear(); // ?????? ?????? ???????????? ???????????? ????????? ?????????.
                            count = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                    SmokePackFirebaseData fbdata = document.toObject(SmokePackFirebaseData.class); //fbData ????????? ???????????? ?????????.
                                    list.add(fbdata); // ?????? ??????????????? ?????????????????? ?????? ????????????????????? ?????? ????????? ??????.
                            }
                            Collections.sort(list, new ObjectSort()); // ????????? ????????? ?????? ??????
                            int size = list.size();
                            for(int i=0;i<size;i++){ // ??? ?????? ????????? ?????? ??????
                                if(list.get(i).getEmail().compareTo(getEmail)==0){
                                    int my_rank=i+1;
                                    my_ranking_rank.setText(my_rank+"???");
                                    my_ranking_position.setText("????????? "+list.get(i).getWeek_smoke()+"??? ??????");
                                    my_ranking_pack.setText(list.get(i).getDoublePacks()+"???");
                                    break;
                                }
                            }


                            first_rank_name.setText(list.get(0).getName());
                            first_rank_pack.setText(list.get(0).getDoublePacks() + "???");
                            first_rank_average.setText("????????? "+list.get(0).getWeek_smoke()+"??? ??????");
                            list.remove(0); // ????????????????????? ?????? ???????????? 4?????????????????? ??????
                            second_rank_name.setText(list.get(0).getName());
                            second_rank_pack.setText(list.get(0).getDoublePacks() + "???");
                            second_rank_average.setText("????????? "+list.get(0).getWeek_smoke()+"??? ??????");
                            list.remove(0);
                            third_rank_name.setText(list.get(0).getName());
                            third_rank_pack.setText(list.get(0).getDoublePacks() + "???");
                            third_rank_average.setText("????????? "+list.get(0).getWeek_smoke()+"??? ??????");
                            list.remove(0);
                            adapter.notifyDataSetChanged(); // ????????? ?????? ??? ????????????
                        } else {
                            Log.d("RANKING ACTIVITY", "????????? ??????: ", task.getException()); // ????????? ??????
                        }
                    }
                });

        // db?????? email, week_smoke, stop_drink ????????????




        adapter = new SmokePackRankingAdapter(list, getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        Bundle extra = getArguments(); // ???????????? ?????? ????????????
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