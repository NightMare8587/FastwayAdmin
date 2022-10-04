package com.consumers.fastwayadmin.MenuActivities.Combo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.consumers.fastwayadmin.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AddRemoveItemCombo extends AppCompatActivity {
    DatabaseReference reference;
    FirebaseAuth auth;
    ListView listView;
    SharedPreferences sharedPreferences;
    String[] arr;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FloatingActionButton floatingActionButton;
    String comboName;
    List<String> dishNames;
    HashMap<String,String> mainMap;
    ArrayList<String> dishQuan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remove_item_combo);
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        comboName = getIntent().getStringExtra("name");
//        dishNames = getIntent().getStringArrayListExtra("dishName");
//        dishQuan = getIntent().getStringArrayListExtra("dishQuan");

        mainMap = (HashMap<String, String>) getIntent().getSerializableExtra("comboDishInfo");
         arr =  mainMap.keySet().toArray(new String[0]);
         dishNames = new ArrayList<>(mainMap.keySet());
        Log.i("getInfo",dishNames.toString());
        initialise();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dishNames);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(AddRemoveItemCombo.this);
            alert.setTitle("Important");
            alert.setMessage("Do you sure wanna remove this item???");
            alert.setPositiveButton("Yes", (dialog, which) -> {
                String name = dishNames.get(position);


                if(dishNames.size() > 2)
                {
                    mainMap.remove(name);
//                    String[] arrs =  mainMap.keySet().toArray(new String[0]);
//                    dishNames = Arrays.asList(arrs);

//                    dishQuan.remove(position);
//                    String newArrList = dishNames.toString().replace("[", "").replace("]", "").trim();
//                    String newArrListQuan = dishQuan.toString().replace("[", "").replace("]", "").trim();
//                reference.child(name).removeValue();
                    firestore.collection(sharedPreferences.getString("state", "")).document("Restaurants").collection(sharedPreferences.getString("locality", "")).document(auth.getUid())
                            .collection("List of Dish").document(comboName).update("dishNamesPrice",mainMap);
                    dishNames.remove(position);
                    arrayAdapter.notifyDataSetChanged();
                }else
                    Toast.makeText(this, "Minimum 2 items two required in Combo", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }).setNegativeButton("No", (dialog, which) -> dialog.dismiss()).create().show();

        });

        floatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(AddRemoveItemCombo.this,AddDishToCurrentCombo.class);
            intent.putExtra("name",comboName);
            intent.putExtra("dishNamesPrice",mainMap);
            startActivity(intent);
        });

    }

    private void initialise() {
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(auth.getUid())).child("List of Dish").child("Combo").child(comboName);
        listView = findViewById(R.id.comboAddRemoveListView);
        floatingActionButton = findViewById(R.id.addDishComboButton);
    }
}