package com.sample.stopking_project;

import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Drink_CustomAdapter extends RecyclerView.Adapter<Drink_CustomAdapter.ViewHolder> {
    private ArrayList<Drink_Help_Item> mList;
    // 뷰홀더 클래스
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private TextView textView_title, textView_summary, textView_link;
        public ViewHolder(View itemView){
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            textView_title = (TextView) itemView.findViewById(R.id.textView_title);
            textView_summary = (TextView) itemView.findViewById(R.id.textView_summary);
            textView_link = (TextView) itemView.findViewById(R.id.textView_link);
        }
    }

    // 생성자
    // 생성자를 통해 데이터를 전달 받음
    public Drink_CustomAdapter (ArrayList<Drink_Help_Item> list){
        this.mList = list;
    }
    @NonNull
    @Override // ViewHolder 객체를 생성하여 리턴한다.
    public Drink_CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.drink_recyclerview_item, parent, false);
        Drink_CustomAdapter.ViewHolder viewHolder = new Drink_CustomAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override   // ViewHolder 안의 내용을 position에 해당되는 데이터로 교체
    public void onBindViewHolder(@NonNull Drink_CustomAdapter.ViewHolder holder, int position){
        holder.textView_title.setText(String.valueOf(mList.get(position).getTitle()));
        holder.textView_summary.setText(String.valueOf(mList.get(position).getSummary()));
        GlideApp.with(holder.itemView).load(mList.get(position).getImg_url())
                .override(300,400)
                .into(holder.imageView);

        // 링크 연결
        Pattern pattern1 = Pattern.compile("전체 기사 보기");
        Linkify.TransformFilter transformFilter = new Linkify.TransformFilter() {
            @Override
            public String transformUrl(Matcher matcher, String s) {
                return "";
            }
        };
        Linkify.addLinks(holder.textView_link, pattern1, "https://www.hidoc.co.kr/" + mList.get(position).getDetail_link(),null,transformFilter);
    }
    @Override   // 전체 데이터의 갯수를 리턴
    public int getItemCount(){
        return mList.size();
    }
}
