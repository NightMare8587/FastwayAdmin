package com.consumers.fastwayadmin;

import java.util.Map;

public class notificationClass {
    public String title,message;
    public Map<String,String> timestamp;
    public notificationClass(String title, String message, Map<String, String> timestamp){
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
    }
}
