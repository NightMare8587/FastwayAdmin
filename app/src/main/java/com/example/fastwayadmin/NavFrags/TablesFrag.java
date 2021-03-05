package com.example.fastwayadmin.NavFrags;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.fastwayadmin.R;
import com.example.fastwayadmin.Tables.AddTables;
import com.example.fastwayadmin.Tables.TableView;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TablesFrag extends Fragment {
    Toolbar tableBar;
    FloatingActionButton addTable;
    FirebaseAuth tableAuth;
    SwipeRefreshLayout layout;
    DatabaseReference tableRef;
    SpinKitView spinKitView;
    List<String> tableNumber = new ArrayList<>();
    List<String> status  = new ArrayList<>();
    RecyclerView table;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tables,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tableBar = view.findViewById(R.id.tableBar);

        addTable = view.findViewById(R.id.addTables);
        layout = view.findViewById(R.id.tableRefreshLayout);
        table = view.findViewById(R.id.tableRecyclerView);
        table.setLayoutManager(new LinearLayoutManager(view.getContext()));
        Toast.makeText(getActivity(), "Swipe down to refresh", Toast.LENGTH_SHORT).show();
        tableAuth = FirebaseAuth.getInstance();
        tableRef = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(tableAuth.getUid())).child("Tables");
        tableNumber.clear();
        status.clear();
        tableRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        tableNumber.add(Objects.requireNonNull(dataSnapshot.child("tableNum").getValue()).toString());
                        status.add(dataSnapshot.child("status").getValue().toString());
                    }

                    table.setAdapter(new TableView(tableNumber,status));
                }else{
                    Toast.makeText(view.getContext(), "Add Some Tables!!!", Toast.LENGTH_SHORT).show();
                }

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
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            tableNumber.add(Objects.requireNonNull(dataSnapshot.child("tableNum").getValue()).toString());
                            status.add(dataSnapshot.child("status").getValue().toString());
                        }
                        table.setAdapter(new TableView(tableNumber,status));
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


}
