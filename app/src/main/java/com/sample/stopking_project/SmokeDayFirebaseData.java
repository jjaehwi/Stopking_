package com.sample.stopking_project;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SmokeDayFirebaseData {
    // 어떤 data를 가져올지 여기서 선택함.
    private String smoke_bank;
    private String email;
    private String name;
    private String stop_smoke;
    private String stop_smoke_days;
    private String week_pack;

    public SmokeDayFirebaseData() {}

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

    public String caculate_day(String day) {
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
        return String.valueOf(user_stop_days);
    }

    public String getStop_smoke() {
        return stop_smoke;
    }

    public String setStop_smoke() {
        this.stop_smoke_days = caculate_day(stop_smoke); // TODO : stop smoke set stopsmoke 데이터형 다시 보기
        return stop_smoke_days;
    }

    public String getSmoke_bank() {
        return smoke_bank;
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