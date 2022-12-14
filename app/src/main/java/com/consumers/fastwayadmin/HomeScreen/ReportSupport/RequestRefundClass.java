package com.consumers.fastwayadmin.HomeScreen.ReportSupport;

public class RequestRefundClass {
    public String payID,refundAmount,time,message;

    public RequestRefundClass(String payID, String refundAmount, String time, String message) {
        this.payID = payID;
        this.refundAmount = refundAmount;
        this.time = time;
        this.message = message;
    }
}
