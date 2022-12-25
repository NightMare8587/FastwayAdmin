package com.consumers.fastwayadmin.ListViewActivity.StaffDetails.EmpHOME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeScreenEMP extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen_emp);
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("EmployeeDB").child(auth.getUid()).child("ResDetails");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    SharedPreferences loginInfo = getSharedPreferences("loginInfo",MODE_PRIVATE);
                    SharedPreferences.Editor editor = loginInfo.edit();
                    editor.remove("resDetails").apply();
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreenEMP.this);
                    builder.setTitle("Removed").setMessage("You have been removed from the restaurant staff. Contact restaurant for more info")
                            .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).create();
                    builder.show();
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}