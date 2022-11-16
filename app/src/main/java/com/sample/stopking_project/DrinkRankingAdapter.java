package com.sample.stopking_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DrinkRankingAdapter extends RecyclerView.Adapter<DrinkRankingAdapter.RankingViewHolder> {

    private ArrayList<DrinkFirebaseData> arrayList;
    private Context context;

    public DrinkRankingAdapter(ArrayList<DrinkFirebaseData> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //실제 리스트뷰가 연결된 후 뷰 홀더를 최초로 만들어 냄.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_data, parent, false);
        RankingViewHolder holder = new RankingViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
        //그림 필요 시 필요.
        holder.tv_Name.setText(arrayList.get(position).getName());
        holder.tv_avg_drink.setText(arrayList.get(position).getAverage_drink());
        holder.email.setText(arrayList.get(position).getEmail());
        holder.stop_drink.setText(arrayList.get(position).getStop_drink());
    }

    @Override
    public int getItemCount() {
        //삼항 연산자
        //null이 아니면 arrayList의 크기를 출력, 아니면 0
        return (arrayList != null ? arrayList.size() : 0);
    }

    //list.data에 data들을 여기로 가져온다.
    public class RankingViewHolder extends RecyclerView.ViewHolder {
        TextView tv_Name;
        TextView tv_avg_drink;
        TextView email;
        TextView stop_drink;

        public RankingViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_Name = itemView.findViewById(R.id.tv_name);
            this.tv_avg_drink = itemView.findViewById(R.id.tv_agvDrink);
            this.email = itemView.findViewById(R.id.tv_email);
            this.stop_drink = itemView.findViewById(R.id.tv_stop_drink);
        }
    }
}
