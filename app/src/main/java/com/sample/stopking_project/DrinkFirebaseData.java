package com.sample.stopking_project;

public class DrinkFirebaseData {
    // 어떤 data를 가져올지 여기서 선택함.
    private String average_drink;
    private String drink_bank;
    private String email;
    private String name;
    private String week_drink;
    private String stop_drink;

    public DrinkFirebaseData() {}

    public String getStop_drink() {
        return stop_drink;
    }

    public void setStop_drink(String stop_drink) {
        this.stop_drink = stop_drink;
    }

    public String getAverage_drink() {
        return average_drink;
    }

    public void setAverage_drink(String average_drink) {
        this.average_drink = average_drink;
    }

    public String getDrink_bank() {
        return drink_bank;
    }

    public void setDrink_bank(String drink_bank) {
        this.drink_bank = drink_bank;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public void setWeek_drink(String week_drink) {
        this.week_drink = week_drink;
    }
}
