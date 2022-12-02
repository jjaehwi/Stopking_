package com.sample.stopking_project;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SmokePackFirebaseData {
    // 어떤 data를 가져올지 여기서 선택함.
    private String email;
    private String name;
    private String week_smoke;
    private String stop_smoke;
    private int packs;
    private int packs_double;


    public static Date convertStringtoDate(String Date){ // 데이터베이스에서 가져온 날짜 변환
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date date = null;
        try{
            date = format.parse(Date);
        } catch(ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public int caculate_day(String day) {
        Date date = convertStringtoDate(day);
        Date startDateValue = date;
        Date now = new Date();
        long diff = now.getTime() - startDateValue.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = (hours / 24) + 1;
        String d = String.valueOf(days);
        int user_stop_days = Integer.parseInt(d);
        return user_stop_days;
    }

    public SmokePackFirebaseData() {}

    public String getStop_smoke() {
        return stop_smoke;
    }

    public String getWeek_smoke(){ return week_smoke;}

    public int getPacks(){
        this.packs = (Integer.parseInt(week_smoke)*(caculate_day(stop_smoke)/7));
        return packs;
        }

    public int getDoublePacks(){
        this.packs_double = (int)Math.round((Math.round(
                Math.round((double)(caculate_day(stop_smoke))/7)
                        * Integer.parseInt(getWeek_smoke()))
                ));
        return packs_double;
    }


    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}