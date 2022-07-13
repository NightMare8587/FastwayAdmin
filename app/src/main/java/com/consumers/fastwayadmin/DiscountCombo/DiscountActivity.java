package com.consumers.fastwayadmin.DiscountCombo;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.consumers.fastwayadmin.R;
import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class DiscountActivity extends AppCompatActivity {
    DatabaseReference reference;
    DatabaseReference dis;
    DatabaseReference addToDB;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    EditText dishName;
    Button search;
    SharedPreferences sharedPreferences;
    SharedPreferences resInfoPred;
    String resName;
    String URL = "https://fcm.googleapis.com/fcm/send";
    List<String> name = new ArrayList<>();
    List<String> price = new ArrayList<>();
    List<String> alreadyHasDiscount = new ArrayList<>();
    List<String> image = new ArrayList<>();
    HashMap<String,String> map = new HashMap<>();
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount);
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        initialise();
        resInfoPred = getSharedPreferences("RestaurantInfo",MODE_PRIVATE);
        resName = resInfoPred.getString("hotelName","");
        name.clear();

        dishName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 0){
                    reference.child("List of Dish").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                name.clear();
                                map.clear();
                                alreadyHasDiscount.clear();
                                price.clear();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                        if (dataSnapshot1.exists() && String.valueOf(dataSnapshot1.child("mrp").getValue()).equals("no")) {
                                            map.put(dataSnapshot1.child("name").getValue(String.class), dataSnapshot.getKey());
                                            name.add(dataSnapshot1.child("name").getValue(String.class));
                                            if(dataSnapshot1.hasChild("Discount")){
                                                alreadyHasDiscount.add("yes");
                                            }else
                                                alreadyHasDiscount.add("no");
                                            price.add(dataSnapshot1.child("full").getValue(String.class));
                                            if(dataSnapshot1.hasChild("image"))
                                                image.add(String.valueOf(dataSnapshot1.child("image").getValue()));
                                            else
                                                image.add("");
                                        }
                                    }
                                }
                                recyclerView.setLayoutManager(new LinearLayoutManager(DiscountActivity.this));
                                recyclerView.setAdapter(new DiscountRecycler(map,DiscountActivity.this,name,image,price,alreadyHasDiscount));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    return;
                }

                String searchText = charSequence.toString();

                reference.child("List of Dish").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            name.clear();
                            map.clear();
                            image.clear();
                            price.clear();
                            alreadyHasDiscount.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    if (dataSnapshot1.exists() && String.valueOf(dataSnapshot1.child("mrp").getValue()).equals("no") && Objects.requireNonNull(dataSnapshot1.child("name").getValue(String.class).toLowerCase(Locale.ROOT)).contains(searchText.toLowerCase(Locale.ROOT))) {
                                        map.put(dataSnapshot1.child("name").getValue(String.class), dataSnapshot.getKey());
                                        name.add(dataSnapshot1.child("name").getValue(String.class));
                                        if(dataSnapshot1.hasChild("Discount")){
                                            alreadyHasDiscount.add("yes");
                                        }else
                                            alreadyHasDiscount.add("no");
                                        price.add(dataSnapshot1.child("full").getValue(String.class));
                                        if(dataSnapshot1.hasChild("image"))
                                            image.add(String.valueOf(dataSnapshot1.child("image").getValue()));
                                        else
                                            image.add("");
                                    }
                                }
                            }
                            recyclerView.setLayoutManager(new LinearLayoutManager(DiscountActivity.this));
                            recyclerView.setAdapter(new DiscountRecycler(map,DiscountActivity.this,name,image,price,alreadyHasDiscount));
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
        reference.child("List of Dish").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    name.clear();
                    map.clear();
                    price.clear();
                    alreadyHasDiscount.clear();
                    image.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            if (dataSnapshot1.exists() && String.valueOf(dataSnapshot1.child("mrp").getValue()).equals("no")) {
                                map.put(dataSnapshot1.child("name").getValue(String.class), dataSnapshot.getKey());
                                name.add(dataSnapshot1.child("name").getValue(String.class));
//                                Log.i("where",dataSnapshot1.child("full").getValue(String.class) + "\n" + dataSnapshot1.toString());
                                price.add(dataSnapshot1.child("full").getValue(String.class));
                                if(dataSnapshot1.hasChild("Discount")){
                                    alreadyHasDiscount.add("yes");
                                }else
                                    alreadyHasDiscount.add("no");
                                if(dataSnapshot1.hasChild("image"))
                                    image.add(String.valueOf(dataSnapshot1.child("image").getValue()));
                                else
                                    image.add("");

                                Log.i("ingi",dataSnapshot1.toString());
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FlatDialog flatDialog = new FlatDialog(DiscountActivity.this);
        flatDialog.setCanceledOnTouchOutside(true);
        flatDialog.setOnCancelListener(dialog -> {
            dialog.dismiss();
            Toast.makeText(DiscountActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            finish();
        });
        flatDialog.setTitle("Choose One Option")
                .setTitleColor(Color.BLACK)
                .setBackgroundColor(Color.parseColor("#f9fce1"))
                .setFirstButtonColor(Color.parseColor("#d3f6f3"))
                .setFirstButtonTextColor(Color.parseColor("#000000"))
                .setFirstButtonText("DISCOUNT ON ALL DISH")
                .setSecondButtonColor(Color.parseColor("#fee9b2"))
                .setSecondButtonTextColor(Color.parseColor("#000000"))
                .setSecondButtonText("LET ME CHOOSE")
                .withFirstButtonListner(view -> {
                    FlatDialog flatDialog1 = new FlatDialog(DiscountActivity.this);
                    flatDialog1.setCanceledOnTouchOutside(true);
                    flatDialog1.setOnCancelListener(dialogInterface -> {
                        finish();
                        Toast.makeText(DiscountActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    });
                    flatDialog1.setTitle("Choose One Option")
                            .setSubtitle("Only Applicable for items above 149")
                            .setSubtitleColor(Color.BLACK)
                            .setTitleColor(Color.BLACK)
                            .setBackgroundColor(Color.parseColor("#f9fce1"))
                            .setFirstButtonColor(Color.parseColor("#d3f6f3"))
                            .setFirstButtonTextColor(Color.parseColor("#000000"))
                            .setFirstButtonText("50% OFF ON ALL")
                            .setSecondButtonColor(Color.parseColor("#fee9b2"))
                            .setSecondButtonTextColor(Color.parseColor("#000000"))
                            .setSecondButtonText("40% OFF ON ALL")
                            .setThirdButtonText("ADD YOUR OWN")
                            .setThirdButtonColor(Color.parseColor("#fbd1b7"))
                            .setThirdButtonTextColor(Color.parseColor("#000000"))
                            .setFirstTextFieldHint("Enter How much discount!!")
                            .setFirstTextFieldBorderColor(Color.BLACK)
                            .setFirstTextFieldHintColor(Color.BLACK)
                            .setFirstTextFieldTextColor(Color.BLACK)
                            .withFirstButtonListner(view1 -> {
                                fiftyDiscount();
                                flatDialog1.dismiss();
                            })
                            .withSecondButtonListner(view12 -> {
                                fourtyDiscount();
                                flatDialog1.dismiss();
                            })
                            .withThirdButtonListner(view13 -> {
                                if (flatDialog1.getFirstTextField().equals("")) {

                                    Toast.makeText(DiscountActivity.this, "Field Can't be Empty", Toast.LENGTH_SHORT).show();
                                } else {
                                    customDiscount(flatDialog1.getFirstTextField());
                                    flatDialog1.dismiss();
                                }
                            }).show();
                    flatDialog.dismiss();
                })
                .withSecondButtonListner(view -> {
                    recyclerView.setLayoutManager(new LinearLayoutManager(DiscountActivity.this));
                    recyclerView.setAdapter(new DiscountRecycler(map,DiscountActivity.this,name,image,price,alreadyHasDiscount));
                    flatDialog.dismiss();
                }).show();
    }

    private void customDiscount(String firstTextField) {
        RequestQueue requestQueue = Volley.newRequestQueue(DiscountActivity.this);
        JSONObject main = new JSONObject();
        try{
            main.put("to","/topics/"+"Restaurant"+ auth.getUid());
            JSONObject notification = new JSONObject();
            notification.put("title",resName + "");
            notification.put("body","Get upto " + firstTextField + "% off on all dish above \u20B9" + "149");
            main.put("notification",notification);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, response -> {

            }, error -> Toast.makeText(DiscountActivity.this, error.getLocalizedMessage()+"null", Toast.LENGTH_SHORT).show()){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String,String> header = new HashMap<>();
                    header.put("content-type","application/json");
                    header.put("authorization","key=AAAAsigSEMs:APA91bEUF9ZFwIu84Jctci56DQd0TQOepztGOIKIBhoqf7N3ueQrkClw0xBTlWZEWyvwprXZmZgW2MNywF1pNBFpq1jFBr0CmlrJ0wygbZIBOnoZ0jP1zZC6nPxqF2MAP6iF3wuBHD2R");
                    return header;
                }
            };

            requestQueue.add(jsonObjectRequest);

        }
        catch (Exception e){
            Toast.makeText(DiscountActivity.this, e.getLocalizedMessage()+"null", Toast.LENGTH_SHORT).show();
        }
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(auth.getUid()));
        reference.child("List of Dish").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (String.valueOf(dataSnapshot1.child("mrp").getValue()).equals("no")) {
                            String type = String.valueOf(dataSnapshot.getKey());
                            String dishName = String.valueOf(dataSnapshot1.child("name").getValue());
                            if (Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("full").getValue()))) >= 149) {
                                if(!String.valueOf(dataSnapshot1.child("half").getValue()).equals("")) {
                                    int price = Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("full").getValue())));
                                    int halfPrice = Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("half").getValue())));
                                    int discount = Integer.parseInt(firstTextField);
                                    int afterDis = price - (price * discount / 100);
                                    int afterDisHalf = halfPrice - (halfPrice * discount / 100);
                                    beforeDiscount(price, afterDis, discount, type, dishName, halfPrice);
                                    addToDiscountDatabase(discount + "");
                                    auth = FirebaseAuth.getInstance();
                                    Log.i("type", type);
                                    Log.i("name", dishName);
//                                reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(auth.getUid()).child("List of Dish");
                                    reference.child("List of Dish").child(type).child(dishName).child("full").setValue(afterDis + "");
                                    reference.child("List of Dish").child(type).child(dishName).child("half").setValue(afterDisHalf + "");
                                }else{
                                    int price = Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("full").getValue())));
                                    int discount = Integer.parseInt(firstTextField);
                                    int afterDis = price - (price * discount / 100);
                                    beforeDiscount(price,afterDis,discount,type,dishName,0);
                                    addToDiscountDatabase(discount + "");
                                    auth = FirebaseAuth.getInstance();
                                    Log.i("type",type);
                                    Log.i("name",dishName);
//                                reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(auth.getUid()).child("List of Dish");
                                    reference.child("List of Dish").child(type).child(dishName).child("full").setValue(afterDis + "");
                                }
                            }
                        }
                    }
                }
                AestheticDialog.Builder builder = new AestheticDialog.Builder(DiscountActivity.this, DialogStyle.FLAT, DialogType.SUCCESS);
                builder.setTitle("Applying Discount")
                        .setMessage("Wait while we are applying discount :)")
                        .setCancelable(false)
                        .setDuration(3000)
                        .setAnimation(DialogAnimation.SHRINK)
                        .setDarkMode(true);

                builder.show();

                new Handler().postDelayed(() -> {
                    builder.dismiss();
                    finish();
                },3000);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void fourtyDiscount() {

        RequestQueue requestQueue = Volley.newRequestQueue(DiscountActivity.this);
        JSONObject main = new JSONObject();
        try{
            main.put("to","/topics/"+"Restaurant"+ auth.getUid());
            JSONObject notification = new JSONObject();
            notification.put("title",resName + "");
            notification.put("body","Get upto 40% off on all dish above \u20B9" + "149");
            main.put("notification",notification);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, response -> {

            }, error -> Toast.makeText(DiscountActivity.this, error.getLocalizedMessage()+"null", Toast.LENGTH_SHORT).show()){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String,String> header = new HashMap<>();
                    header.put("content-type","application/json");
                    header.put("authorization","key=AAAAsigSEMs:APA91bEUF9ZFwIu84Jctci56DQd0TQOepztGOIKIBhoqf7N3ueQrkClw0xBTlWZEWyvwprXZmZgW2MNywF1pNBFpq1jFBr0CmlrJ0wygbZIBOnoZ0jP1zZC6nPxqF2MAP6iF3wuBHD2R");
                    return header;
                }
            };

            requestQueue.add(jsonObjectRequest);

        }
        catch (Exception e){
            Toast.makeText(DiscountActivity.this, e.getLocalizedMessage()+"null", Toast.LENGTH_SHORT).show();
        }
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(auth.getUid()));

        reference.child("List of Dish").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (String.valueOf(dataSnapshot1.child("mrp").getValue()).equals("no")) {
                            String type = String.valueOf(dataSnapshot.getKey());
                            String dishName = String.valueOf(dataSnapshot1.child("name").getValue());
                            if (Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("full").getValue()))) >= 149) {
                                if(!String.valueOf(dataSnapshot1.child("half").getValue()).equals("")) {
                                    int price = Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("full").getValue())));
                                    int halfPrice = Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("half").getValue())));
                                    int discount = 40;
                                    int afterDis = price - (price * discount / 100);
                                    int afterDisHalf = halfPrice - (halfPrice * discount / 100);
                                    beforeDiscount(price, afterDis, discount, type, dishName, halfPrice);
                                    addToDiscountDatabase("yes");
                                    auth = FirebaseAuth.getInstance();
                                    Log.i("type", type);
                                    Log.i("name", dishName);
//                                    reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(auth.getUid()));
                                    reference.child("List of Dish").child(type).child(dishName).child("full").setValue(afterDis + "");
                                    reference.child("List of Dish").child(type).child(dishName).child("half").setValue(afterDisHalf + "");
                                }else{
                                    int price = Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("full").getValue())));
                                    int discount = 40;
                                    int afterDis = price - (price * discount / 100);
                                    beforeDiscount(price,afterDis,discount,type,dishName,0);
                                    addToDiscountDatabase(discount + "");
                                    auth = FirebaseAuth.getInstance();
                                    Log.i("type",type);
                                    Log.i("name",dishName);
//                                    reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(auth.getUid()));
                                    reference.child("List of Dish").child(type).child(dishName).child("full").setValue(afterDis + "");
                                }
                             }
                          }
                       }
                    }
                AestheticDialog.Builder builder = new AestheticDialog.Builder(DiscountActivity.this, DialogStyle.FLAT, DialogType.SUCCESS);
                builder.setTitle("Applying Discount")
                        .setMessage("Wait while we are applying discount :)")
                        .setCancelable(false)
                        .setDuration(2000)
                        .setAnimation(DialogAnimation.SHRINK)
                        .setDarkMode(true);

                builder.show();

                new Handler().postDelayed(() -> {
                   builder.dismiss();
                    finish();
                },3000);
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addToDiscountDatabase(String discount) {
        auth = FirebaseAuth.getInstance();
        addToDB = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(auth.getUid()));
        addToDB.child("Discount").child("available").setValue("yes");
        addToDB.child("Discount").child("dis").setValue(discount);
    }

    private void beforeDiscount(int price,int after, int discount,String type,String name,int halfPrice) {
        DisInfo disInfo;
        if(halfPrice == 0)
         disInfo = new DisInfo(String.valueOf(price),String.valueOf(after),String.valueOf(discount),"");
        else
            disInfo = new DisInfo(String.valueOf(price),String.valueOf(after),String.valueOf(discount),""+ halfPrice);

        dis = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(auth.getUid()));
        dis.child("List of Dish").child(type).child(name).child("Discount").child(name).setValue(disInfo);
    }


    private void fiftyDiscount() {
        RequestQueue requestQueue = Volley.newRequestQueue(DiscountActivity.this);
        JSONObject main = new JSONObject();
        try{
            main.put("to","/topics/"+"Restaurant"+ auth.getUid());
            JSONObject notification = new JSONObject();
            notification.put("title",resName + "");
            notification.put("body","Get upto 50% off on all dish above \u20B9" + "149");
            main.put("notification",notification);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, response -> {

            }, error -> Toast.makeText(DiscountActivity.this, error.getLocalizedMessage()+"null", Toast.LENGTH_SHORT).show()){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String,String> header = new HashMap<>();
                    header.put("content-type","application/json");
                    header.put("authorization","key=AAAAsigSEMs:APA91bEUF9ZFwIu84Jctci56DQd0TQOepztGOIKIBhoqf7N3ueQrkClw0xBTlWZEWyvwprXZmZgW2MNywF1pNBFpq1jFBr0CmlrJ0wygbZIBOnoZ0jP1zZC6nPxqF2MAP6iF3wuBHD2R");
                    return header;
                }
            };

            requestQueue.add(jsonObjectRequest);

        }
        catch (Exception e){
            Toast.makeText(DiscountActivity.this, e.getLocalizedMessage()+"null", Toast.LENGTH_SHORT).show();
        }
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(auth.getUid()));
        reference.child("List of Dish").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (String.valueOf(dataSnapshot1.child("mrp").getValue()).equals("no")) {
                            String type = String.valueOf(dataSnapshot.getKey());
                            String dishName = String.valueOf(dataSnapshot1.child("name").getValue());
                            if (Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("full").getValue()))) >= 149) {
                                if(!String.valueOf(dataSnapshot1.child("half").getValue()).equals("")) {
                                    int price = Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("full").getValue())));
                                    int halfPrice = Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("half").getValue())));

                                    int discount = 50;
                                    int afterDis = price - (price * discount / 100);
                                    int afterDisHalf = halfPrice - (halfPrice * discount / 100);
                                    beforeDiscount(price, afterDis, discount, type, dishName, halfPrice);
                                    addToDiscountDatabase(discount + "");
                                    auth = FirebaseAuth.getInstance();
                                    Log.i("type", type);
                                    Log.i("name", dishName);
//                                reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(auth.getUid()).child("List of Dish");
                                    reference.child("List of Dish").child(type).child(dishName).child("full").setValue(afterDis + "");
                                    reference.child("List of Dish").child(type).child(dishName).child("half").setValue(afterDisHalf + "");
                                }else{
                                    int price = Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("full").getValue())));
                                    int discount = 50;
                                    int afterDis = price - (price * discount / 100);
                                    beforeDiscount(price,afterDis,discount,type,dishName,0);
                                    addToDiscountDatabase("yes");
                                    auth = FirebaseAuth.getInstance();
                                    Log.i("type",type);
                                    Log.i("name",dishName);
//                                reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(auth.getUid()).child("List of Dish");
                                    reference.child("List of Dish").child(type).child(dishName).child("full").setValue(afterDis + "");
                                }
                            }
                        }
                    }
                }
                AestheticDialog.Builder builder = new AestheticDialog.Builder(DiscountActivity.this, DialogStyle.FLAT, DialogType.SUCCESS);
                builder.setTitle("Applying Discount")
                        .setMessage("Wait while we are applying discount :)")
                        .setCancelable(false)
                        .setDuration(3000)
                        .setAnimation(DialogAnimation.SHRINK)
                        .setDarkMode(true);

                builder.show();

                new Handler().postDelayed(() -> {
                    builder.dismiss();
                    finish();
                },3000);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initialise() {
        recyclerView = findViewById(R.id.discountActivityRecyclerView);
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(auth.getUid()));
        dis = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
        dishName = findViewById(R.id.searchDishNameForSingleDiscount);
        search = findViewById(R.id.searchEnteredDishNameInDatabase);
    }
}