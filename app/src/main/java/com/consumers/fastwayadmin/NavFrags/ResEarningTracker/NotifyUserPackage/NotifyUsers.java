package com.consumers.fastwayadmin.NavFrags.ResEarningTracker.NotifyUserPackage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.consumers.fastwayadmin.R;

import java.util.ArrayList;
import java.util.List;

public class NotifyUsers extends AppCompatActivity {
    RecyclerView recyclerView;
    SharedPreferences timeStampOfUser;
    TextView notifyIse;
    SharedPreferences contactDetails;
    List<String> userList;
    List<String> adapterTime = new ArrayList<>();
    List<String> adapterContact = new ArrayList<>();
    List<String> adapterId = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_users);
        recyclerView = findViewById(R.id.recyclerViewNotifyUsers);
        userList = new ArrayList<>(getIntent().getStringArrayListExtra("list"));
        notifyIse = findViewById(R.id.noUserToNotify);
        contactDetails = getSharedPreferences("DatabaseForContactNum",MODE_PRIVATE);

        timeStampOfUser = getSharedPreferences("lastVisitOfCustomer",MODE_PRIVATE);

        for(String id : userList){
            if(contactDetails.contains(id)){
                Long time = Long.parseLong(timeStampOfUser.getString(id,""));
                if(System.currentTimeMillis() - time > 1209600000L){
                    adapterContact.add(contactDetails.getString(id,""));
                    adapterTime.add(String.valueOf(System.currentTimeMillis() - time));
                    adapterId.add(id);
                }
            }
        }

        if(adapterContact.isEmpty()){
            notifyIse.setVisibility(View.VISIBLE);
        }else{
            notifyIse.setVisibility(View.INVISIBLE);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new UserAdap(adapterTime,adapterContact,adapterId));

        }

    }
}