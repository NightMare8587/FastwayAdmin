package com.consumers.fastwayadmin.ListViewActivity;

public class TransactionReportClass {
    public String startDate,endDate,resId,email,startMilli,endMilli;

    public TransactionReportClass(String startDate, String endDate, String resId, String email,String startMilli,String endMilli) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.resId = resId;
        this.email = email;
        this.startMilli = startMilli;
        this.endMilli = endMilli;
    }
}
