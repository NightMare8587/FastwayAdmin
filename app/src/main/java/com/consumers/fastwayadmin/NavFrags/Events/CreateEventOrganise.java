package com.consumers.fastwayadmin.NavFrags.Events;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.consumers.fastwayadmin.NavFrags.CurrentTakeAwayOrders.ApproveCurrentTakeAway;
import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateEventOrganise extends AppCompatActivity {
    TextView pickDate;
    EditText eventName,artistName,eventInfo,price,seats;
    DatabaseReference databaseReference;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String dateAndTimeInString;
    String URL = "https://fcm.googleapis.com/fcm/send";
    SharedPreferences sharedPreferences;
    boolean dateAndTimePicked = false;
    long timeChoosen;
    Button createEvent;
    Calendar calendar = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event_organise);
        pickDate = findViewById(R.id.pickDateAndTimeForEventOrganise);
        eventName = findViewById(R.id.eventNameEditTextOrganise);
        artistName = findViewById(R.id.artistnameComingEditTextOrganise);
        eventInfo = findViewById(R.id.InfoOfUpComingEditTextOrganise);
        price = findViewById(R.id.priceOfPassEditTextEvent);
        seats = findViewById(R.id.totalSeatsForUpcomingEventEdit);
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid())).child("Current Event");
        createEvent = findViewById(R.id.createEventOrganiseButtonNow);


        pickDate.setOnClickListener(click -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(CreateEventOrganise.this,
                    (datePicker, year, month, day) -> {
                        TimePickerDialog timePickerDialog = new TimePickerDialog(CreateEventOrganise.this
                                , (timePicker, i, i1) -> {
                                        calendar.set(year,month,day,i,i1,0);
                                    Log.i("infoTime",calendar.getTimeInMillis() + "");
                                    timeChoosen = calendar.getTimeInMillis();
                                    @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss");
                                    Date date = new Date(timeChoosen);
                                    dateAndTimeInString = dateFormat.format(date);
                                    dateAndTimePicked = true;
                            Toast.makeText(this, "Date And Time Set Successfully", Toast.LENGTH_SHORT).show();
                                },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false);
                        timePickerDialog.show();
                    },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));

            datePickerDialog.show();
        });

        createEvent.setOnClickListener(click -> {
            if(eventName.length() == 0){
                Toast.makeText(this, "Field can't be empty", Toast.LENGTH_SHORT).show();
                eventName.requestFocus();
                return;
            }

            if(artistName.length() == 0){
                Toast.makeText(this, "Field can't be empty", Toast.LENGTH_SHORT).show();
                artistName.requestFocus();
                return;
            }

            if(eventInfo.length() == 0){
                Toast.makeText(this, "Field can't be empty", Toast.LENGTH_SHORT).show();
                eventInfo.requestFocus();
                return;
            }

            if(price.length() == 0){
                Toast.makeText(this, "Field can't be empty", Toast.LENGTH_SHORT).show();
                price.requestFocus();
                return;
            }

            if(seats.length() == 0){
                Toast.makeText(this, "Field can't be empty", Toast.LENGTH_SHORT).show();
                seats.requestFocus();
                return;
            }

            if(!dateAndTimePicked){
                Toast.makeText(this, "Please Pick Date And Time", Toast.LENGTH_SHORT).show();
                return;
            }

            databaseReference.child("eventName").setValue(eventName.getText().toString());
            databaseReference.child("eventInfo").setValue(eventInfo.getText().toString());
            databaseReference.child("artistNameComing").setValue(artistName.getText().toString());
            databaseReference.child("filled").setValue("0");
            databaseReference.child("seats").setValue(seats.getText().toString());
            databaseReference.child("price").setValue(price.getText().toString());
            databaseReference.child("dateAndTime").setValue(timeChoosen + "");
            databaseReference.child("dateAndTimeString").setValue(dateAndTimeInString);

            RequestQueue requestQueue = Volley.newRequestQueue(CreateEventOrganise.this);
            JSONObject main = new JSONObject();
            SharedPreferences resName = getSharedPreferences("RestaurantInfo",MODE_PRIVATE);
            try {
                main.put("to", "/topics/" + sharedPreferences.getString("state","").replaceAll("\\s+","") + "");
                JSONObject notification = new JSONObject();
                notification.put("title", "New Event at " + resName.getString("hotelName",""));
                notification.put("click_action", "Table Frag");
                notification.put("body", "New Upcoming Event at " + resName.getString("hotelName","") + "\n" + "Artist Coming: " +
                        artistName.getText().toString() + "\nDate-Time: " + dateAndTimeInString + "\nPrice: \u20b9" + price.getText().toString());
                main.put("notification", notification);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, response -> {

                }, error -> Toast.makeText(CreateEventOrganise.this, error.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show()) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> header = new HashMap<>();
                        header.put("content-type", "application/json");
                        header.put("authorization", "key=AAAAjq_WsHs:APA91bGZV-uH-NJddxIniy8h1tDGDHqxhgvFdyNRDaV_raxjvSM_FkKu7JSwtSp4Q_iSmPuTKGGIB2M_07c9rKgPXUH43-RzpK6zkaSaIaNgmeiwUO40rYxYUZAkKoLAQQVeVJ7mXboD");
                        return header;
                    }
                };
                requestQueue.add(jsonObjectRequest);
            } catch (Exception e) {
                Toast.makeText(CreateEventOrganise.this, e.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
            }

            DatabaseReference addToRTDB = FirebaseDatabase.getInstance().getReference().getRoot().child("Offers").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(auth.getUid());
            HashMap<String,String> map = new HashMap<>();
            map.put("eventName",eventName.getText().toString());
            map.put("eventInfo",eventInfo.getText().toString());
            map.put("price",price.getText().toString());
            map.put("seats",seats.getText().toString());
            map.put("dateAndTime",timeChoosen + "");
            map.put("dateAndTimeString",dateAndTimeInString + "");
            map.put("artistNameComing",artistName.getText().toString() + "");

            addToRTDB.child("Current Event").setValue(map);

            Toast.makeText(this, "Event Created Successfully", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> {
                setResult(22);
                finish();
            },300);
        });

    }
}