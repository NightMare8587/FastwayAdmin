package com.consumer.fastwayadmin.Info;

public class InfoRestaurant {
    public String name,address,pin,number,nearby;

    public InfoRestaurant(String name,String address,String pin,String number,String nearby){
        this.address = address;
        this.name = name;
        this.nearby = nearby;
        this.number = number;
        this.pin = pin;
    }
}
