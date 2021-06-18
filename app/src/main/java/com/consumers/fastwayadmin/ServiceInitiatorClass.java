package com.consumers.fastwayadmin;

import android.app.Application;
import android.content.Intent;

public class ServiceInitiatorClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        startService(new Intent(this,MyService.class));
    }
}
