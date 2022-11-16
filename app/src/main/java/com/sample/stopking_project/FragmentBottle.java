package com.sample.stopking_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class FragmentBottle extends Fragment {
    private String getEmail;
    private String getName;
    private String getBottle;

    public FragmentBottle() {
        // Required empty public constructor
    }


    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fragment_bottle, container, false);

        Bundle extra = getArguments();
        if (extra != null) {
            getEmail = extra.getString("email");
            getName = extra.getString("name");
            getBottle = extra.getString("bottle");
        }
        TextView my_ranking_name = v.findViewById(R.id.my_ranking_name);
        my_ranking_name.setText(getName);
        TextView my_ranking_bottle = v.findViewById(R.id.my_ranking_bottle);
        my_ranking_bottle.setText(getBottle + "병");


        // Inflate the layout for this fragment
        return v;


    }
}