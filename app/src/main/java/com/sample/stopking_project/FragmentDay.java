package com.sample.stopking_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class FragmentDay extends Fragment {
    private String getEmail;
    private String getName;
    private String getDay;


    public FragmentDay() {
        // Required empty public constructor
    }

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fragment_day, container, false);

        Bundle extra = getArguments();
        if (extra != null) {
            getEmail = extra.getString("email");
            getName = extra.getString("name");
            getDay = extra.getString("day");
        }
        TextView my_ranking_name = v.findViewById(R.id.my_ranking_name);
        TextView my_ranking_day = v.findViewById(R.id.my_ranking_day);
        my_ranking_name.setText(getName);
        my_ranking_day.setText(getDay + "일");


        // Inflate the layout for this fragment
        return v;


    }
}