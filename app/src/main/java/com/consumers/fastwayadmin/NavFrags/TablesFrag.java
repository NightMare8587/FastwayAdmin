package com.consumers.fastwayadmin.NavFrags;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.consumers.fastwayadmin.R;
import com.consumers.fastwayadmin.Tables.AddTables;
import com.consumers.fastwayadmin.Tables.TableView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TablesFrag extends Fragment {
    Toolbar tableBar;
    FloatingActionButton addTable;
    TableView tableView;
    FirebaseAuth tableAuth;
    SharedPreferences sharedPreferences;
    SwipeRefreshLayout layout;
    DatabaseReference tableRef;
    List<String> tableNumber = new ArrayList<>();
    List<String> status  = new ArrayList<>();
    List<String> timeInMillis  = new ArrayList<>();
    List<String> timeOfBooking  = new ArrayList<>();
    List<String> timeOfUnavailability  = new ArrayList<>();
    RecyclerView table;
    int count = 0;
    boolean pressed = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tables,container,false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                count++;
                pressed = true;
                Toast.makeText(getContext(), "Press again to exit", Toast.LENGTH_SHORT).show();

                if(count == 2 && pressed)
                    requireActivity().finish();

                new Handler().postDelayed(() -> {
                    pressed = false;
                    count = 0;
                },2000);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this,callback);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tableBar = view.findViewById(R.id.tableBar);
        sharedPreferences = view.getContext().getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        addTable = view.findViewById(R.id.addTables);
        layout = view.findViewById(R.id.tableRefreshLayout);
        table = view.findViewById(R.id.tableRecyclerView);
        table.setLayoutManager(new LinearLayoutManager(view.getContext()));
//        Toast.makeText(getActivity(), "Swipe down to refresh", Toast.LENGTH_SHORT).show();
        tableAuth = FirebaseAuth.getInstance();
        tableRef = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(Objects.requireNonNull(tableAuth.getUid())).child("Tables");
        tableNumber.clear();
        status.clear();
//        tableRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()){
//                    HashMap<String,List<String>> map = new HashMap<>();
//                    timeInMillis.clear();
//                    timeOfBooking.clear();
//                    timeOfUnavailability.clear();
//                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
//                        tableNumber.add(Objects.requireNonNull(dataSnapshot.child("tableNum").getValue()).toString());
//                        status.add(dataSnapshot.child("status").getValue().toString());
//                        if(dataSnapshot.hasChild("customerId")) {
////                            map.put(String.valueOf(dataSnapshot.child("tableNum").getValue(String.class)),String.valueOf(dataSnapshot.child("customerId").getValue()));
//                            List<String> list = new ArrayList<>();
//                            list.add(String.valueOf(dataSnapshot.child("customerId").getValue()));
//                            list.add(String.valueOf(dataSnapshot.child("time").getValue()));
//                            map.put(String.valueOf(dataSnapshot.child("tableNum").getValue(String.class)), list);
//                            if(dataSnapshot.hasChild("timeInMillis")) {
//                                timeInMillis.add(String.valueOf(dataSnapshot.child("timeInMillis").getValue()));
//                                timeOfBooking.add(String.valueOf(dataSnapshot.child("timeOfBooking").getValue()));
//                            }
//                            else {
//                                timeInMillis.add("");
//                                timeOfBooking.add("");
//                            }
//
//                            if(dataSnapshot.hasChild("timeOfUnavailability")){
//                                timeOfUnavailability.add(String.valueOf(dataSnapshot.child("timeOfUnavailability").getValue()));
//                            }else
//                                timeOfUnavailability.add("");
//                        }
//                    }
//                    tableView = new TableView(tableNumber,status,map,getContext(),timeInMillis,timeOfBooking,timeOfUnavailability);
//                    table.setAdapter(tableView);
//                    tableView.notifyDataSetChanged();
//
//                }else{
//                    Toast.makeText(view.getContext(), "Add Some Tables!!!", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        tableRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateChild();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateChild();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                updateChild();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addTable.setOnClickListener(view1 -> startActivity(new Intent(getActivity(), AddTables.class)));

        layout.setOnRefreshListener(() -> {

            tableNumber.clear();
            status.clear();
            tableRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        tableNumber.clear();
                        status.clear();
                        timeOfBooking.clear();
                        timeInMillis.clear();
                        timeOfUnavailability.clear();
                        HashMap<String,List<String>> map = new HashMap<>();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            tableNumber.add(Objects.requireNonNull(dataSnapshot.child("tableNum").getValue()).toString());
                            status.add(dataSnapshot.child("status").getValue().toString());
                            if (dataSnapshot.hasChild("customerId")) {
                                List<String> list = new ArrayList<>();
                                list.add(String.valueOf(dataSnapshot.child("customerId").getValue()));
                                list.add(String.valueOf(dataSnapshot.child("time").getValue()));
                                map.put(String.valueOf(dataSnapshot.child("tableNum").getValue(String.class)), list);
                                if (dataSnapshot.hasChild("timeInMillis")) {
                                    timeInMillis.add(String.valueOf(dataSnapshot.child("timeInMillis").getValue()));
                                    timeOfBooking.add(String.valueOf(dataSnapshot.child("timeOfBooking").getValue()));
                                } else {
                                    timeInMillis.add("");
                                    timeOfBooking.add("");
                                }

                                if(dataSnapshot.hasChild("timeOfUnavailability")){
                                    timeOfUnavailability.add(String.valueOf(dataSnapshot.child("timeOfUnavailability").getValue()));
                                }else
                                    timeOfUnavailability.add("");
                            } else {
                                timeOfBooking.add("");
                                timeInMillis.add("");
                                timeOfUnavailability.add("");
                            }
                        }
                        tableView = new TableView(tableNumber,status,map,getContext(),timeInMillis,timeOfBooking,timeOfUnavailability);
                        table.setAdapter(tableView);
                        tableView.notifyDataSetChanged();

                    }else{
                        Toast.makeText(view.getContext(), "Add Some Tables!!!", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            layout.setRefreshing(false);

        });
    }

    private void updateChild() {
        tableRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    tableNumber.clear();
                    timeOfUnavailability.clear();
                    status.clear();
                    timeOfBooking.clear();
                    timeInMillis.clear();
                    HashMap<String,List<String>> map = new HashMap<>();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {

                        tableNumber.add(Objects.requireNonNull(dataSnapshot.child("tableNum").getValue()).toString());
                        status.add(dataSnapshot.child("status").getValue().toString());
                        if (dataSnapshot.hasChild("customerId")) {
                            List<String> list = new ArrayList<>();
                            list.add(String.valueOf(dataSnapshot.child("customerId").getValue()));
                            list.add(String.valueOf(dataSnapshot.child("time").getValue()));
                            map.put(String.valueOf(dataSnapshot.child("tableNum").getValue(String.class)), list);
                            if(dataSnapshot.hasChild("timeInMillis")) {
                                timeInMillis.add(String.valueOf(dataSnapshot.child("timeInMillis").getValue()));
                                timeOfBooking.add(String.valueOf(dataSnapshot.child("timeOfBooking").getValue()));
                            }
                            else {
                                timeInMillis.add("");
                                timeOfBooking.add("");
                            }

                            if(dataSnapshot.hasChild("timeOfUnavailability")){
                                timeOfUnavailability.add(dataSnapshot.child("timeOfUnavailability").getValue(String.class));
                            }else
                                timeOfUnavailability.add("");
                        }else {
                            timeInMillis.add("");
                            timeOfBooking.add("");
                            timeOfUnavailability.add("");
                        }
                    }
                    tableView = new TableView(tableNumber,status,map,getContext(),timeInMillis,timeOfBooking,timeOfUnavailability);
                    table.setAdapter(tableView);
                    Log.i("info",timeOfBooking.toString());
                    tableView.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private class MyTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            tableRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        timeInMillis.clear();
                        HashMap<String,List<String>> map = new HashMap<>();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            tableNumber.add(Objects.requireNonNull(dataSnapshot.child("tableNum").getValue()).toString());
                            status.add(dataSnapshot.child("status").getValue().toString());
                            if(dataSnapshot.hasChild("customerId")) {
//                            map.put(String.valueOf(dataSnapshot.child("tableNum").getValue(String.class)),String.valueOf(dataSnapshot.child("customerId").getValue()));
                                List<String> list = new ArrayList<>();
                                list.add(String.valueOf(dataSnapshot.child("customerId").getValue()));
                                list.add(String.valueOf(dataSnapshot.child("time").getValue()));
                                map.put(String.valueOf(dataSnapshot.child("tableNum").getValue(String.class)), list);
                                if(dataSnapshot.hasChild("timeInMillis")) {
                                    timeInMillis.add(String.valueOf(dataSnapshot.child("timeInMillis").getValue()));
                                    timeOfBooking.add(String.valueOf(dataSnapshot.child("timeOfBooking").getValue()));
                                }
                                else {
                                    timeInMillis.add("");
                                    timeOfBooking.add("");
                                }
                            }
                        }
                        tableView = new TableView(tableNumber,status,map,getContext(),timeInMillis,timeOfBooking,timeOfUnavailability);
                        table.setAdapter(tableView);
                        tableView.notifyDataSetChanged();

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            return null;
        }
    }


}
