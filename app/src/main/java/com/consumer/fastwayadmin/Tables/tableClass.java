package com.consumer.fastwayadmin.Tables;

public class tableClass {
    public String numSeats;
    public String status;
    public String tableNum;
    public tableClass(String seats,String num){
        numSeats = seats;
        status = "available";
        tableNum = num;
    }
}
