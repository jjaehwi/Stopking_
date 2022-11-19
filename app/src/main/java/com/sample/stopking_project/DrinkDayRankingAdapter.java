package com.sample.stopking_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DrinkDayRankingAdapter extends RecyclerView.Adapter<DrinkDayRankingAdapter.RankingViewHolder> {

    private static ArrayList<DrinkDayFirebaseData> arrayList;
    private Context context;
    private int rank = 4;

    public DrinkDayRankingAdapter(ArrayList<DrinkDayFirebaseData> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //실제 리스트뷰가 연결된 후 뷰 홀더를 최초로 만들어 냄.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drinking_day_user_list_data, parent, false);
        RankingViewHolder holder = new RankingViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
        //그림 필요 시 필요.
        holder.user_name.setText(arrayList.get(position).getName());
        holder.user_days.setText(arrayList.get(position).setStop_drink()+"일");
        holder.user_rank.setText(rank +"위");
        rank+=1;
    }

    @Override
    public int getItemCount() {
        //삼항 연산자
        //null이 아니면 arrayList의 크기를 출력, 아니면 0
        return (arrayList != null ? arrayList.size() : 0);
    }

    //list.data에 data들을 여기로 가져온다.
    public class RankingViewHolder extends RecyclerView.ViewHolder {
        TextView user_name;
        TextView user_days;
        TextView user_rank;

        public RankingViewHolder(@NonNull View itemView) {
            super(itemView);
            this.user_name = itemView.findViewById(R.id.drink_day_ranking_user_name);
            this.user_days = itemView.findViewById(R.id.drink_ranking_user_days);
            this.user_rank = itemView.findViewById(R.id.drink_day_ranking_rank);
        }
    }
}