package com.consumers.fastwayadmin.HomeScreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.consumers.fastwayadmin.R;
import com.consumers.fastwayadmin.demoFrag;

import java.util.ArrayList;
import java.util.List;

public class SupportActivity extends AppCompatActivity{
    ListView listView;
    ListView faqList;
    FragmentManager fragmentManager;
    List<String> genarlQuery = new ArrayList<String>();
    List<String> faQQuery = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        genarlQuery.add("Live Chat With Us");
        genarlQuery.add("Email Us");
        fragmentManager = getSupportFragmentManager();
        initialise();
        listView = findViewById(R.id.genralQueryListView);
        faqList = findViewById(R.id.faqListView);
        demoFrag demoFrag = new demoFrag();
        ArrayAdapter<String> faqAdapter = new ArrayAdapter<String>(this,R.layout.list,faQQuery);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.list,genarlQuery);
        faqList.setAdapter(faqAdapter);
        listView.setAdapter(arrayAdapter);


        faqList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_top,R.anim.fade_in).replace(R.id.supportactivity, demoFrag).commit();
//                        startActivity(new Intent(SupportActivity.this, PaymentAndRefunds.class));
//                        overridePendingTransition(R.anim.slide_in_top,R.anim.slide_out_bottom);
                        break;
                    case 1:

                        break;
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Toast.makeText(SupportActivity.this, "Work in Progress :)", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.setType("plain/text");
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "fastway8587@gmail.com" });
                        startActivity(emailIntent);
                        break;
                }
            }
        });
    }

    private void initialise() {
        faQQuery.add("Payments And Refunds");
        faQQuery.add("Cashbacks And Offers");
    }
}