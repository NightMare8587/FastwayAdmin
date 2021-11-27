package com.consumers.fastwayadmin.NavFrags.CurrentTakeAwayOrders;

public class CashTransactionClass {
    public String orderId,orderAmount,time,userID;

    public CashTransactionClass(String orderId, String orderAmount, String time, String userID) {
        this.orderId = orderId;
        this.orderAmount = orderAmount;
        this.time = time;
        this.userID = userID;
    }
}
