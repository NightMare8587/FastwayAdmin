package com.consumers.fastwayadmin.Login.EmpLogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.consumers.fastwayadmin.HomeScreen.HomeScreen;
import com.consumers.fastwayadmin.ListViewActivity.StaffDetails.EmpHOME.HomeScreenEMP;
import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ApplyForRestaurantAndDocs extends AppCompatActivity {
    EditText editText;
    List<String> name = new ArrayList<>();
    List<String> address = new ArrayList<>();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    List<String> resID = new ArrayList<>();
    List<String> resImage = new ArrayList<>();
    SharedPreferences loginInfo;
    DatabaseReference checkIfApproved;
    SharedPreferences.Editor editor;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_for_restaurant_and_docs);
        editText = findViewById(R.id.editTextTextPersonNameEmpSearch);
        recyclerView = findViewById(R.id.recyclerViewRestaurantEmp);
        loginInfo = getSharedPreferences("loginInfo",MODE_PRIVATE);
        editor = loginInfo.edit();

        if(loginInfo.contains("resDetails")){
            startActivity(new Intent(ApplyForRestaurantAndDocs.this,HomeScreenEMP.class));
            finish();
            return;
        }
        checkIfApproved = FirebaseDatabase.getInstance().getReference().getRoot().child("EmployeeDB").child(auth.getUid()).child("ResDetails");
        checkIfApproved.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    editor.putString("resDetails","yes");
                    editor.putString("resName",snapshot.child("resName").getValue(String.class));
                    editor.putString("resAddress",snapshot.child("resAddress").getValue(String.class));
                    editor.putString("resID",snapshot.child("resID").getValue(String.class));
                    editor.apply();

                    startActivity(new Intent(ApplyForRestaurantAndDocs.this, HomeScreenEMP.class));
                    finish();
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 0){
                    resID.clear();
                    name.clear();
                    resImage.clear();
                    address.clear();


                    recyclerView.setAdapter(new ShowResEmp(name,address,resID,resImage));
                    return;
                }

                String custom = charSequence.toString().trim().toLowerCase(Locale.ROOT);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(loginInfo.getString("state","")).child(loginInfo.getString("state",""));
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            resID.clear();
                            name.clear();
                            resImage.clear();
                            address.clear();
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                String nameRes = dataSnapshot.child("name").getValue(String.class);

                                if(nameRes.contains(custom)){
                                    resID.add(dataSnapshot.getKey());
                                    address.add(dataSnapshot.child("address").getValue(String.class));
                                    name.add(dataSnapshot.child("name").getValue(String.class));
                                    resImage.add(dataSnapshot.child("DisplayImage").getValue(String.class));
                                }
                            }

                            recyclerView.setAdapter(new ShowResEmp(name,address,resID,resImage));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}