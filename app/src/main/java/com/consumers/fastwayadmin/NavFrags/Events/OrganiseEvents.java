package com.consumers.fastwayadmin.NavFrags.Events;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.consumers.fastwayadmin.HomeScreen.ReportSupport.RequestRefundClass;
import com.consumers.fastwayadmin.R;
import com.developer.kalert.KAlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import karpuzoglu.enes.com.fastdialog.Animations;
import karpuzoglu.enes.com.fastdialog.FastDialog;
import karpuzoglu.enes.com.fastdialog.FastDialogBuilder;
import karpuzoglu.enes.com.fastdialog.Type;

public class OrganiseEvents extends AppCompatActivity {
    String genratedToken;
    String customisation;
    double payoutAmt;
    String testPayoutToken = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/testToken.php";
    String prodPayoutToken = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/payoutIMPS.php";
    String testBearerToken = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/test/testBearerToken.php";
    String prodBearerToken = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/authBEarerToken.php";
    String testPaymentToVendor = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/test/testPayment.php";
    String prodPaymentToVendor = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/PaymentToVendor.php";
    DatabaseReference databaseReference;
    DatabaseReference currentEvent;
    String url = "https://intercellular-stabi.000webhostapp.com/refunds/initiateRefund.php";
    TextView currentEventName,seats,filled,price,artistName,dateAndTime;
    SharedPreferences sharedPreferences;
    Button cancelEvent,organiseEvent,showQR;
    RecyclerView recyclerView;
    List<String> eventNames = new ArrayList<>();
    List<String> ticketsSold = new ArrayList<>();
    long timeActualForEvent;
    List<String> dateAndTimeList = new ArrayList<>();
    List<String> artistNameList = new ArrayList<>();
    DatabaseReference previousEvents;
    boolean currentEventAvailable = false;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FastDialog fastDialog,fastDialog1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organise_events);
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("Registered Restaurants")
                .child(sharedPreferences.getString("state","")).child(Objects.requireNonNull(auth.getUid()));
        initialise();
        fastDialog = new FastDialogBuilder(OrganiseEvents.this, Type.PROGRESS)
                .progressText("Cancelling.... Dont Exit")
                        .cancelable(false)
                                .setAnimation(Animations.GROW_IN)
                                        .create();

        fastDialog1 = new FastDialogBuilder(OrganiseEvents.this, Type.PROGRESS)
                .progressText("Finishing Show.... Dont Exit")
                .cancelable(false)
                .setAnimation(Animations.GROW_IN)
                .create();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        previousEvents = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid()).child("Previous Events");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChild("allowEvents")){
                    KAlertDialog kAlertDialog = new KAlertDialog(OrganiseEvents.this,KAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("Your restaurant/cafe is not allowed to organise events. To be allowed contact Ordinalo")
                            .setConfirmText("Exit")
                            .setConfirmClickListener(clcik -> {
                                clcik.dismiss();
                                finish();
                            });
                    kAlertDialog.setCancelable(false);
                    kAlertDialog.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        cancelEvent.setOnClickListener(click -> {
            if(System.currentTimeMillis() < timeActualForEvent){
                AlertDialog.Builder builder = new AlertDialog.Builder(OrganiseEvents.this);
                builder.setTitle("Cancel").setMessage("Do you sure wanna cancel event ???")
                        .setPositiveButton("Cancel Now", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid()).child("Current Event").child("BookingIDs");
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            fastDialog.show();
                                            List<String> authIds = new ArrayList<>();
                                            List<String> orderIDs = new ArrayList<>();
                                            List<String> totalPrice = new ArrayList<>();
                                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                authIds.add(dataSnapshot.getKey());
                                                orderIDs.add(dataSnapshot.child("orderID").getValue(String.class));
                                                totalPrice.add(dataSnapshot.child("amountPaidForBook").getValue(String.class));
                                            }


                                            for(int i=0;i<authIds.size();i++){
                                                sendNotification(authIds.get(i));
                                                DatabaseReference requestRefundOrdinalo = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("RefundRequest").child(authIds.get(i));
                                                RequestRefundClass requestRefundClass = new RequestRefundClass(orderIDs.get(i),totalPrice.get(i),"","Event Cancelled by restaurant");
                                                requestRefundOrdinalo.setValue(requestRefundClass);

//                                                Toast.makeText(OrganiseEvents.this, "Refund Request Initiated", Toast.LENGTH_SHORT).show();
//                                                sendRefund(orderIDs.get(i),totalPrice.get(i));
                                            }

//                                            SharedPreferences userDetails = getSharedPreferences("AccountInfo",MODE_PRIVATE);



                                            DatabaseReference removeFromGeo = FirebaseDatabase.getInstance().getReference().getRoot().child("Offers").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality",""))
                                                    .child(auth.getUid());
                                            removeFromGeo.child("Current Event").removeValue();

                                            removeFromGeo = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid()).child("Current Event");
                                            removeFromGeo.removeValue();

                                            new Handler().postDelayed(() -> {
                                                fastDialog.dismiss();
                                                Toast.makeText(OrganiseEvents.this, "Cancelled Show", Toast.LENGTH_SHORT).show();
                                                finish();
                                            },7500);
                                        }else{
                                            DatabaseReference removeFromGeo = FirebaseDatabase.getInstance().getReference().getRoot().child("Offers").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality",""))
                                                    .child(auth.getUid());
                                            removeFromGeo.child("Current Event").removeValue();

                                            removeFromGeo = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid()).child("Current Event");
                                            removeFromGeo.removeValue();

                                            Toast.makeText(OrganiseEvents.this, "Cancelled Show", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).create();
                builder.show();
            }else{

                AlertDialog.Builder builder = new AlertDialog.Builder(OrganiseEvents.this);
                builder.setTitle("Finish Event").setMessage("Finish Event only after its finished. If finished then click Finish")
                        .setPositiveButton("Finish", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid()).child("Current Event");
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.hasChild("totalSalesPrice")){
                                            fastDialog1.show();
                                            double amt = Double.parseDouble(Objects.requireNonNull(snapshot.child("totalSalesPrice").getValue(String.class)));
                                            double cmmisn = (amt * 5) / 100;

                                            amt = amt - cmmisn;

                                            payoutAmt = amt;
                                            new MakePayout().execute();

                                            DatabaseReference removeFromGeo = FirebaseDatabase.getInstance().getReference().getRoot().child("Offers").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality",""))
                                                    .child(auth.getUid());
                                            removeFromGeo.child("Current Event").removeValue();

                                            removeFromGeo = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid()).child("Current Event");
                                            removeFromGeo.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    String name = String.valueOf(snapshot.child("eventName").getValue());
                                                    String date = String.valueOf(snapshot.child("dateAndTimeString").getValue());
                                                    String salesAmt = String.valueOf(snapshot.child("totalSalesPrice").getValue());
                                                    String seats = String.valueOf(snapshot.child("seats").getValue());
                                                    String filledSeats = String.valueOf(snapshot.child("filled").getValue());

                                                    DatabaseReference addToPrev = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid()).child("Previous Events");
                                                    addToPrev.child(name).child("eventName").setValue(name);
                                                    addToPrev.child(name).child("salesAmt").setValue(salesAmt);
                                                    addToPrev.child(name).child("filledSeats").setValue(filledSeats);
                                                    addToPrev.child(name).child("seats").setValue(seats);
                                                    addToPrev.child(name).child("date").setValue(date);

                                                    addToPrev = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid()).child("Current Event");
                                                    addToPrev.removeValue();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                        }else{
                                            DatabaseReference removeFromGeo = FirebaseDatabase.getInstance().getReference().getRoot().child("Offers").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality",""))
                                                    .child(auth.getUid());
                                            removeFromGeo.child("Current Event").removeValue();

                                            removeFromGeo = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid()).child("Current Event");
                                            removeFromGeo.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    String name = String.valueOf(snapshot.child("eventName").getValue());
                                                    String date = String.valueOf(snapshot.child("dateAndTimeString").getValue());
                                                    String salesAmt = String.valueOf(snapshot.child("totalSalesPrice").getValue());
                                                    String seats = String.valueOf(snapshot.child("seats").getValue());
                                                    String filledSeats = String.valueOf(snapshot.child("filled").getValue());

                                                    DatabaseReference addToPrev = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid()).child("Previous Events");
                                                    addToPrev.child(name).child("eventName").setValue(name);
                                                    addToPrev.child(name).child("salesAmt").setValue("0");
                                                    addToPrev.child(name).child("filledSeats").setValue("0");
                                                    addToPrev.child(name).child("seats").setValue(seats);
                                                    addToPrev.child(name).child("date").setValue(date);

                                                    addToPrev = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid()).child("Current Event");
                                                    addToPrev.removeValue();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                            Toast.makeText(OrganiseEvents.this, "Show finished", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).create();
                builder.show();
            }
        });

        AsyncTask.execute(() -> previousEvents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        eventNames.add(dataSnapshot.getKey());
                        dateAndTimeList.add(dataSnapshot.child("date").getValue(String.class));
                        artistNameList.add(dataSnapshot.child("salesAmt").getValue(String.class));
                        ticketsSold.add(dataSnapshot.child("filledSeats").getValue(String.class));
                    }
                    recyclerView.setAdapter(new PreviousEventADP(eventNames,ticketsSold,dateAndTimeList,artistNameList));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }));

        currentEvent = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid()).child("Current Event");
        currentEvent.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    currentEventName.setText("Event Name: " + snapshot.child("eventName").getValue(String.class));
                    price.setText("Price: \u20b9" + snapshot.child("price").getValue(String.class));
                    dateAndTime.setText("Date And Time: " + snapshot.child("dateAndTimeString").getValue(String.class));
                    filled.setText("Filled : " + snapshot.child("filled").getValue(String.class));
                    seats.setText("Seats: " + snapshot.child("seats").getValue(String.class));
                    artistName.setText("Artist Name: " + snapshot.child("artistNameComing").getValue(String.class));
                    long timeActual = Long.parseLong(snapshot.child("dateAndTime").getValue(String.class));
                    timeActualForEvent = timeActual;
                    if(timeActual < System.currentTimeMillis())
                        cancelEvent.setText("FINISH EVENT");
                    cancelEvent.setVisibility(View.VISIBLE);
                    showQR.setVisibility(View.VISIBLE);
                    currentEventAvailable = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        showQR.setOnClickListener(click -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(OrganiseEvents.this);
            builder.setTitle("QR Code").setMessage("Ask user to scan this QR");
            LinearLayout linearLayout = new LinearLayout(OrganiseEvents.this);
            ImageView imageView = new ImageView(OrganiseEvents.this);
            imageView.setMaxHeight(512);
            imageView.setMaxWidth(512);

            QRCodeWriter writer = new QRCodeWriter();
            try {
                BitMatrix bitMatrix = writer.encode(auth.getUid() + "," + sharedPreferences.getString("state", "") + "," + sharedPreferences.getString("locality", "") + "," + "OrdinaloQR", BarcodeFormat.QR_CODE, 512, 512);
                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                    }
                }
//                    Toast.makeText(AddTables.this, "Click on Image to download it..", Toast.LENGTH_SHORT).show();
                imageView.setImageBitmap(bmp);

            }catch (Exception e){
                Toast.makeText(this, "Try again... :)", Toast.LENGTH_SHORT).show();
            }
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.addView(imageView);
            builder.setView(linearLayout);
            builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.create().show();
        });

        organiseEvent.setOnClickListener(click -> {
            if(currentEventAvailable){
                KAlertDialog kAlertDialog = new KAlertDialog(OrganiseEvents.this,KAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("Your restaurant/cafe has already planned an event. You can organise only one at a time")
                        .setConfirmText("Exit")
                        .setConfirmClickListener(AppCompatDialog::dismiss);
                kAlertDialog.setCancelable(false);
                kAlertDialog.show();
            }else{
                startActivityForResult(new Intent(OrganiseEvents.this,CreateEventOrganise.class),20);
            }
        });
    }

    public class MakePayout extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue requestQueue = Volley.newRequestQueue(OrganiseEvents.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, testPayoutToken, response -> {
                Log.i("response",response);
                genratedToken = response.trim();
                new AuthorizeToken().execute();
            }, error -> {

            });
            requestQueue.add(stringRequest);
            return null;
        }
    }

    public class AuthorizeToken extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue requestQueue = Volley.newRequestQueue(OrganiseEvents.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, testBearerToken, response -> {
                Log.i("response",response);
                if(response.trim().equals("Token is valid")){
                        initiatePayout(payoutAmt);

                }
            }, error -> {

            }){
                @NonNull
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> params = new HashMap<>();
                    params.put("token",genratedToken);
                    return params;
                }
            };
            requestQueue.add(stringRequest);
            return null;
        }
    }

    private void initiatePayout(double amt) {
        AsyncTask.execute(() -> {
            RequestQueue requestQueue = Volley.newRequestQueue(OrganiseEvents.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, testPaymentToVendor, response -> {
                Log.i("resp",response);
            }, error -> {
                runOnUiThread(() -> {
                    fastDialog1.dismiss();
                    Toast.makeText(OrganiseEvents.this, "Show Finished", Toast.LENGTH_SHORT).show();
                    finish();
                });

            }){
                @NonNull
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> params = new HashMap<>();
                    params.put("benID",auth.getUid());
                    String genratedID = "ORDER_" + System.currentTimeMillis();

                    params.put("transID",genratedID);
                    params.put("token",genratedToken);
                    params.put("amount", new DecimalFormat("0.00").format(amt) + "");
                    return params;
                }
            };
            requestQueue.add(stringRequest);
        });
    }

    private void sendRefund(String s, String s1) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                RequestQueue requestQueue = Volley.newRequestQueue(OrganiseEvents.this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("resp",response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
                    @NonNull
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String,String> params = new HashMap<>();
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        String referid = String.valueOf(System.currentTimeMillis());
                        String referIDSS = referid;
                        Random random = new Random();

                        referid = referid + (random.nextInt(1000 - 1) + 1);
                        String finalReferIDForInfo = "refund_" + referid + "s";
                        String time = String.valueOf(System.currentTimeMillis());

                        params.put("referID",referid + "");
                        params.put("refundAmount", s1 + "");
                        params.put("orderID",s);
                        return params;
                    }
                };
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(stringRequest);
            }
        });
    }

    private void sendNotification(String s) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 22){
            currentEvent.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        currentEventName.setText("Event Name: " + snapshot.child("eventName").getValue(String.class));
                        price.setText("Price: \u20b9" + snapshot.child("price").getValue(String.class));
                        dateAndTime.setText("Date And Time: " + snapshot.child("dateAndTimeString").getValue(String.class));
                        filled.setText("Filled : " + snapshot.child("filled").getValue(String.class));
                        seats.setText("Seats: " + snapshot.child("seats").getValue(String.class));
                        artistName.setText("Artist Name: " + snapshot.child("artistNameComing").getValue(String.class));
                        cancelEvent.setVisibility(View.VISIBLE);
                        long timeActual = Long.parseLong(snapshot.child("dateAndTime").getValue(String.class));
                        if(timeActual < System.currentTimeMillis())
                            cancelEvent.setText("FINISH EVENT");
                        showQR.setVisibility(View.VISIBLE);
                        currentEventAvailable = true;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void initialise() {
        currentEventName = findViewById(R.id.nameOfEventOrganise);
        price = findViewById(R.id.priceOfPassORganiseEvent);
        dateAndTime = findViewById(R.id.dateAndTimeEventOrganise);
        filled = findViewById(R.id.filledSeatsOrganiseEvents);
        seats = findViewById(R.id.totalSeatsOrganiseEvent);
        recyclerView = findViewById(R.id.recyclerViewPreviousEventsList);
        showQR = findViewById(R.id.showQRcodeToScanButton);
        artistName = findViewById(R.id.nameOfArtistComingOrganise);
        cancelEvent = findViewById(R.id.cancelCurrentEventButtonOrganised);
        organiseEvent = findViewById(R.id.organiseEventButton);
    }


}