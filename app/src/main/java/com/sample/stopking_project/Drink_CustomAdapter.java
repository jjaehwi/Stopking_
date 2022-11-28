package com.sample.stopking_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Drink_CustomAdapter extends RecyclerView.Adapter<Drink_CustomAdapter.ViewHolder> {
    private ArrayList<String> localDataSet;

    // 뷰홀더 클래스
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textView;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            textView=itemView.findViewById(R.id.textView);
        }
        public TextView getTextView(){
            return textView;
        }
    }

    // 생성자
    // 생성자를 통해 데이터를 전달 받음
    public Drink_CustomAdapter (ArrayList<String> dataSet){
        localDataSet = dataSet;
    }

    @NonNull
    @Override // ViewHolder 객체를 생성하여 리턴한다.
    public Drink_CustomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.drink_recyclerview_item, parent, false);
        Drink_CustomAdapter.ViewHolder viewHolder = new Drink_CustomAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override   // ViewHolder 안의 내용을 position에 해당되는 데이터로 교체
    public void onBindViewHolder(@NonNull Drink_CustomAdapter.ViewHolder holder, int position){
        String text = localDataSet.get(position);
        holder.textView.setText(text);
    }

    @Override   // 전체 데이터의 갯수를 리턴
    public int getItemCount(){
        return localDataSet.size();
    }
}
