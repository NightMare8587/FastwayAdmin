package com.consumers.fastwayadmin.Chat;

public class chat {
    public String message,senderId,time,id,name,typeOfMessage;

    public chat(String message, String senderId, String time,String id,String name,String typeOfMessage) {
        this.message = message;
        this.senderId = senderId;
        this.typeOfMessage = typeOfMessage;
        this.time = time;
        this.id = id;
        this.name = name;
    }
}
