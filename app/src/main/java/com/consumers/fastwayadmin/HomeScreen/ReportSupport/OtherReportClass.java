package com.consumers.fastwayadmin.HomeScreen.ReportSupport;

public class OtherReportClass {
    public String issueName,issueDetail,name,email,userId,restaurantName,state;

    public OtherReportClass(String issueName, String issueDetail, String name, String email,String userId,String restaurantName,String state) {
        this.issueName = issueName;
        this.issueDetail = issueDetail;
        this.state =state;
        this.name = name;
        this.email = email;
        this.userId = userId;
        this.restaurantName = restaurantName;
    }
}
