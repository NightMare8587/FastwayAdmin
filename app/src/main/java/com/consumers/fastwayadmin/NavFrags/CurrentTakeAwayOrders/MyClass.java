package com.consumers.fastwayadmin.NavFrags.CurrentTakeAwayOrders;

public class MyClass {
    public String name,price,image,type,time,timesOrdered,halfOr,state,totalAmountPaid,orderID,orderAndPayment,orderStatus;

    public MyClass(String name, String price, String image, String type, String time, String timesOrdered, String halfOr, String state, String totalAmountPaid, String orderID, String orderAndPayment, String orderStatus) {
        this.name = name;
        this.orderStatus = orderStatus;
        this.orderAndPayment = orderAndPayment;
        this.price = price;
        this.timesOrdered = timesOrdered;
        this.orderID = orderID;
        this.image = image;
        this.type = type;
        this.state = state;
        this.totalAmountPaid = totalAmountPaid;
        this.time = time;
        this.halfOr = halfOr;
    }
}
