package com.consumers.fastwayadmin.Info;

public class InfoRestaurant {
    public String name,address,pin,number,nearby,rating,totalRate,count;

    public InfoRestaurant(String name,String address,String pin,String number,String nearby,String rating,String totalRate,String count){
        this.address = address;
        this.name = name;
        this.nearby = nearby;
        this.number = number;
        this.pin = pin;
        this.totalRate = totalRate;
        this.count = count;
        this.rating = rating;
    }
}
