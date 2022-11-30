package com.sample.stopking_project;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DrinkBottleFirebaseData {
    // 어떤 data를 가져올지 여기서 선택함.
    private String average_drink;
    private String email;
    private String name;
    private String week_drink;
    private String stop_drink;
    private int week_bottle;
    private String bottles;

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

    public DrinkBottleFirebaseData() {}

    public String getStop_drink() {
        return stop_drink;
    }


    public String getAverage_drink() {
        return average_drink;
    }
    public Number getWeek_bottle(){ return week_bottle;}

    public int getBottles() {
        this.bottles = String.valueOf((week_bottle) * (caculate_day(stop_drink)/7));
        return Integer.parseInt(bottles);
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

    public String getWeek_drink() {
        return week_drink;
    }
}