package com.consumers.fastwayadmin.ListViewActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.consumers.fastwayadmin.NavFrags.BankVerification.VendorDetailsActivity;
import com.consumers.fastwayadmin.R;
import com.developer.kalert.KAlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import karpuzoglu.enes.com.fastdialog.FastDialog;
import karpuzoglu.enes.com.fastdialog.FastDialogBuilder;
import karpuzoglu.enes.com.fastdialog.Type;

public class InitiatePayoutForAdminNEFT extends AppCompatActivity {
    DatabaseReference databaseReference;
    double amount;
    double enteredAmount;
  MakePaymentToVendor makePaymentToVendor = new MakePaymentToVendor();
    String genratedToken;
    long coolDownTime;
    String URL = "https://fcm.googleapis.com/fcm/send";
    boolean cooldown = false;
    boolean notHave = false;
    boolean initiateCooldown = false;
    FastDialog fastDialog;
    boolean moreThan20 = false;
    boolean availableForPayout = false;
    String testPayoutToken = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/testToken.php";
    String prodPayoutToken = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/payoutIMPS.php";
    String prodPayoutTokenNeft = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/payoutNeft.php";
    String testBearerToken = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/test/testBearerToken.php";
    String prodBearerToken = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/authBEarerToken.php";
    String testPaymentToVendor = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/test/testPayment.php";
    String prodPaymentToVendor = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/PaymentToVendor.php";
    FirebaseAuth auth = FirebaseAuth.getInstance();
    TextView textView,changeMethod;
    DecimalFormat decimalFormat = new DecimalFormat("0.00");
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initiate_payout_for_admin_neft);
        textView = findViewById(R.id.totalPayoutAmountAvailableToBeInitiated);
        fastDialog = new FastDialogBuilder(InitiatePayoutForAdminNEFT.this, Type.PROGRESS)
                .progressText("Creating Payout.....")
                .cancelable(false)
                .create();

        SharedPreferences sharedPreferences = getSharedPreferences("VendorID", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

       DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid())).child("Bank Details");
       databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.exists()){
                   editor.putString("vendorDetails", "yes");
                   editor.putString("accountNumber", snapshot.child("acNum").getValue(String.class));
                   editor.putString("ifscCode", snapshot.child("acIfsc").getValue(String.class));
                   if(snapshot.hasChild("contactID")) {

                       editor.putString("contactID", snapshot.child("contactID").getValue(String.class).trim());
                   }else
                       notHave = true;
                   if(snapshot.hasChild("fundId")) {

                       editor.putString("fundId", snapshot.child("fundId").getValue(String.class).trim());
                   }else
                       notHave = true;
                   editor.apply();


                   if(notHave) {
                       AlertDialog.Builder builder = new AlertDialog.Builder(InitiatePayoutForAdminNEFT.this);
                       builder.setTitle("Bank Not Verified").setMessage("Bank account is not yet verified by Ordinalo\nPayout can't be made right now")
                               .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialogInterface, int i) {
                                       dialogInterface.dismiss();
                                       finish();
                                   }
                               }).create();
                       builder.setCancelable(true);
                       builder.show();
                   }
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });


        button = findViewById(R.id.initiatePaymentForPayoutAdmin);
//        changeMethod = findViewById(R.id.changePayoutMethodNeftorAdmin);
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("totalPayoutAmount")){
                    if(snapshot.hasChild("coolDownPeriod")) {
                        coolDownTime = Long.parseLong(Objects.requireNonNull(snapshot.child("coolDownPeriod").getValue(String.class)));
                        cooldown = true;
                    }
                    availableForPayout = true;
                     amount = Double.parseDouble(String.valueOf(snapshot.child("totalPayoutAmount").getValue()));
                     textView.setText("\u20b9" + decimalFormat.format(amount));
                }else
                {
                    button.setVisibility(View.INVISIBLE);
                    Toast.makeText(InitiatePayoutForAdminNEFT.this, "No Amount For Payout Available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

//        changeMethod.setOnClickListener(click -> {
//            startActivity(new Intent(InitiatePayoutForAdminNEFT.this, SelectPayoutMethodType.class));
//        });

        button.setOnClickListener(click -> {

            if(availableForPayout && amount != 0){

                if(cooldown) {
                    if(System.currentTimeMillis() < coolDownTime){
                        Toast.makeText(this, "Cooldown period is active", Toast.LENGTH_SHORT).show();
                        return;
//                        AlertDialog.Builder builder = new AlertDialog.Builder(InitiatePayoutForAdminNEFT.this);
//                        builder.setTitle("Cooldown").setMessage("Cooldown period is active. You can initiate payout after cooldown period has ended")
//                                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                                    }
//                                }).setNegativeButton("Initiate (Extra charges)", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        dialogInterface.dismiss();
//                                        AlertDialog.Builder alert = new AlertDialog.Builder(InitiatePayoutForAdminNEFT.this);
//                                        alert.setMessage("You can initiate payout but extra charges will be applied (\u20b925)")
//                                                .setPositiveButton("Initiate Payout", new DialogInterface.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                                        dialogInterface.dismiss();
//                                                        initiateCooldown = true;
//                                                        if(amount > 30000D){
//                                                            moreThan20 = true;
//                                                            AlertDialog.Builder alert = new AlertDialog.Builder(InitiatePayoutForAdminNEFT.this);
//                                                            alert.setTitle("Message").setMessage("You can initiate payout of amount less than \u20b930000 at a time. You can initiate again after a cooldown period of 6 hours");
//                                                            LinearLayout linearLayout = new LinearLayout(InitiatePayoutForAdminNEFT.this);
//                                                            linearLayout.setOrientation(LinearLayout.VERTICAL);
//                                                            EditText editText = new EditText(InitiatePayoutForAdminNEFT.this);
//                                                            editText.setHint("Enter Amount Here");
//                                                            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
//                                                            linearLayout.addView(editText);
//                                                            alert.setPositiveButton("Initiate Payout", (dialogInterfaces, ii) -> {
//                                                                dialogInterface.dismiss();
//                                                                if(Double.parseDouble(editText.getText().toString()) > 30000D)
//                                                                    Toast.makeText(InitiatePayoutForAdminNEFT.this, "Amount Should be less than 30k", Toast.LENGTH_SHORT).show();
//                                                                else{
//                                                                    enteredAmount = Double.parseDouble(editText.getText().toString());
//                                                                    if(enteredAmount > amount) {
//                                                                        Toast.makeText(InitiatePayoutForAdminNEFT.this, "Invalid Input", Toast.LENGTH_SHORT).show();
//                                                                        return;
//                                                                    }
//                                                                    AlertDialog.Builder builder = new AlertDialog.Builder(InitiatePayoutForAdminNEFT.this);
//                                                                    builder.setTitle("Choose one option").setMessage("Choose one payout option\nGet payout after 2 to 4 hours (NEFT) \nGet Instant Payout (IMPS \u20b95 charge)")
//                                                                            .setPositiveButton("Choose NEFT", (dialog, which) -> {
//                                                                                initiateCooldown = true;
//                                                                                new MakePayout().execute();
//
//                                                                                fastDialog.show();
//                                                                                dialog.dismiss();
//                                                                            }).setNegativeButton("Choose IMPS", (dialog, which) -> {
//                                                                                initiateCooldown = true;
//                                                                                new MakePayoutIMPS().execute();
//                                                                                fastDialog.show();
//                                                                                dialog.dismiss();
//                                                                            }).setNeutralButton("Exit", (dialog, which) -> dialog.dismiss()).create().show();
//                                                                }
//                                                            }).setNegativeButton("Wait", (dialogInterfaces, ii) -> {
//
//                                                            }).create();
//                                                            alert.setView(linearLayout);
//                                                            alert.show();
//                                                        }else{
//                                                            AlertDialog.Builder builder = new AlertDialog.Builder(InitiatePayoutForAdminNEFT.this);
//                                                            builder.setTitle("Choose one option").setMessage("Choose one payout option\nGet payout after 2 to 4 hours (NEFT)\nGet Instant Payout (IMPS \u20b95 charge)")
//                                                                    .setPositiveButton("Choose NEFT", new DialogInterface.OnClickListener() {
//                                                                        @Override
//                                                                        public void onClick(DialogInterface dialog, int which) {
//                                                                            initiateCooldown = true;
//                                                                            new MakePayout().execute();
//                                                                            fastDialog.show();
//                                                                            dialog.dismiss();
//                                                                        }
//                                                                    }).setNegativeButton("Choose IMPS", new DialogInterface.OnClickListener() {
//                                                                        @Override
//                                                                        public void onClick(DialogInterface dialog, int which) {
//                                                                            initiateCooldown = true;
//                                                                            new MakePayoutIMPS().execute();
//                                                                            fastDialog.show();
//                                                                            dialog.dismiss();
//                                                                        }
//                                                                    }).setNeutralButton("Exit", (dialog, which) -> dialog.dismiss()).create().show();
//                                                        }
//
//
//                                                    }
//                                                }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                                                    }
//                                                }).create();
//                                        alert.show();
//                                    }
//                                }).create();
//                        builder.show();
////                        Toast.makeText(this, "Remaining: " + TimeUnit.MILLISECONDS.to(coolDownTime - System.currentTimeMillis()), Toast.LENGTH_SHORT).show();
//                        return;
                    }
                }

                if(amount > 30000D) {
                    moreThan20 = true;
                    AlertDialog.Builder alert = new AlertDialog.Builder(InitiatePayoutForAdminNEFT.this);
                    alert.setTitle("Message").setMessage("You can initiate payout of amount less than \u20b930000 at a time. You can initiate again after a cooldown period of 6 hours");
                    LinearLayout linearLayout = new LinearLayout(InitiatePayoutForAdminNEFT.this);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    EditText editText = new EditText(InitiatePayoutForAdminNEFT.this);
                    editText.setHint("Enter Amount Here");
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    linearLayout.addView(editText);
                    alert.setPositiveButton("Initiate Payout", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        if(Double.parseDouble(editText.getText().toString()) > 30000D)
                            Toast.makeText(InitiatePayoutForAdminNEFT.this, "Amount Should be less than \u20b930000", Toast.LENGTH_SHORT).show();
                        else{
                            enteredAmount = Double.parseDouble(editText.getText().toString());
                            if(enteredAmount > amount) {
                                Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            RequestQueue requestQueue = Volley.newRequestQueue(InitiatePayoutForAdminNEFT.this);
                            JSONObject main = new JSONObject();
                            try{
                                main.put("to","/topics/"+"RequestPayout");
                                JSONObject notification = new JSONObject();
                                notification.put("title","New Payout Request");
                                notification.put("body","You have a new request to create payout. Check now");
                                main.put("notification",notification);

                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, response -> {

                                }, error -> Toast.makeText(InitiatePayoutForAdminNEFT.this, error.getLocalizedMessage()+"null", Toast.LENGTH_SHORT).show()){
                                    @Override
                                    public Map<String, String> getHeaders() {
                                        Map<String,String> header = new HashMap<>();
                                        header.put("content-type","application/json");
                                        header.put("authorization","key=AAAAjq_WsHs:APA91bGZV-uH-NJddxIniy8h1tDGDHqxhgvFdyNRDaV_raxjvSM_FkKu7JSwtSp4Q_iSmPuTKGGIB2M_07c9rKgPXUH43-RzpK6zkaSaIaNgmeiwUO40rYxYUZAkKoLAQQVeVJ7mXboD");
                                        return header;
                                    }
                                };

                                requestQueue.add(jsonObjectRequest);

                            }
                            catch (Exception e){
                                Toast.makeText(InitiatePayoutForAdminNEFT.this, e.getLocalizedMessage()+"null", Toast.LENGTH_SHORT).show();
                            }

                            DatabaseReference requestPayout = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("Payouts").child(auth.getUid());
                            HashMap<String,String> map = new HashMap<>();
                            SharedPreferences resInfo = getSharedPreferences("RestaurantInfo",MODE_PRIVATE);
                            map.put("amount",enteredAmount + "");
                            map.put("contactId",sharedPreferences.getString("contactID",""));
                            map.put("fundId",sharedPreferences.getString("fundId",""));
                            map.put("name",resInfo.getString("hotelName",""));
                            map.put("number",resInfo.getString("hotelNumber",""));
                            requestPayout.setValue(map);
                            new KAlertDialog(InitiatePayoutForAdminNEFT.this,KAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Payout Request")
                                    .setContentText("Payout will be initiated after request is verified by ordinalo")
                                    .setConfirmText("Exit")
                                    .setConfirmClickListener(clicks -> {
                                        clicks.dismissWithAnimation();
                                        finish();
                                    }).show();
                            Toast.makeText(this, "Payout Request Generated", Toast.LENGTH_SHORT).show();

//                            AlertDialog.Builder builder = new AlertDialog.Builder(InitiatePayoutForAdminNEFT.this);
//                            builder.setTitle("Choose one option").setMessage("Choose one payout option\nGet payout after 2 to 4 hours (NEFT) \nGet Instant Payout (IMPS \u20b95 charge)")
//                                    .setPositiveButton("Choose NEFT", (dialog, which) -> {
//                                        new MakePayout().execute();
//                                        fastDialog.show();
//                                        dialog.dismiss();
//                                    }).setNegativeButton("Choose IMPS", (dialog, which) -> {
//                                        new MakePayoutIMPS().execute();
//                                        fastDialog.show();
//                                        dialog.dismiss();
//                                    }).setNeutralButton("Exit", (dialog, which) -> dialog.dismiss()).create().show();
                        }
                    }).setNegativeButton("Wait", (dialogInterface, i) -> {

                    }).create();
                    alert.setView(linearLayout);
                    alert.show();

                    return;
                }

                RequestQueue requestQueue = Volley.newRequestQueue(InitiatePayoutForAdminNEFT.this);
                JSONObject main = new JSONObject();
                try{
                    main.put("to","/topics/"+"RequestPayout");
                    JSONObject notification = new JSONObject();
                    notification.put("title","New Payout Request");
                    notification.put("body","You have a new request to create payout. Check now");
                    main.put("notification",notification);

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, response -> {

                    }, error -> Toast.makeText(InitiatePayoutForAdminNEFT.this, error.getLocalizedMessage()+"null", Toast.LENGTH_SHORT).show()){
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String,String> header = new HashMap<>();
                            header.put("content-type","application/json");
                            header.put("authorization","key=AAAAjq_WsHs:APA91bGZV-uH-NJddxIniy8h1tDGDHqxhgvFdyNRDaV_raxjvSM_FkKu7JSwtSp4Q_iSmPuTKGGIB2M_07c9rKgPXUH43-RzpK6zkaSaIaNgmeiwUO40rYxYUZAkKoLAQQVeVJ7mXboD");
                            return header;
                        }
                    };

                    requestQueue.add(jsonObjectRequest);

                }
                catch (Exception e){
                    Toast.makeText(InitiatePayoutForAdminNEFT.this, e.getLocalizedMessage()+"null", Toast.LENGTH_SHORT).show();
                }

                Toast.makeText(this, "Payout Request Generated", Toast.LENGTH_SHORT).show();
                DatabaseReference requestPayout = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("Payouts").child(auth.getUid());
                HashMap<String,String> map = new HashMap<>();
                SharedPreferences resInfo = getSharedPreferences("RestaurantInfo",MODE_PRIVATE);
                map.put("amount",amount + "");
                map.put("contactId",sharedPreferences.getString("contactID",""));
                map.put("fundId",sharedPreferences.getString("fundId",""));
                map.put("name",resInfo.getString("hotelName",""));
                map.put("number",resInfo.getString("hotelNumber",""));
                requestPayout.setValue(map);

                new KAlertDialog(InitiatePayoutForAdminNEFT.this,KAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Payout Request")
                        .setContentText("Payout will be initiated after request is verified by ordinalo")
                        .setConfirmText("Exit")
                        .setConfirmClickListener(clicks -> {
                            clicks.dismissWithAnimation();
                            finish();
                        }).show();

//                AlertDialog.Builder builder = new AlertDialog.Builder(InitiatePayoutForAdminNEFT.this);
//                builder.setTitle("Choose one option").setMessage("Choose one payout option\nGet payout after 2 to 4 hours (NEFT)\nGet Instant Payout (IMPS \u20b95 charge)")
//                        .setPositiveButton("Choose NEFT", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                new MakePayout().execute();
//                                fastDialog.show();
//                                dialog.dismiss();
//                            }
//                        }).setNegativeButton("Choose IMPS", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                new MakePayoutIMPS().execute();
//                                fastDialog.show();
//                                dialog.dismiss();
//                            }
//                        }).setNeutralButton("Exit", (dialog, which) -> dialog.dismiss()).create().show();
            }else
                Toast.makeText(this, "Amount should not be \u20b90", Toast.LENGTH_SHORT).show();
        });
    }

    public class MakePayoutIMPS extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue requestQueue = Volley.newRequestQueue(InitiatePayoutForAdminNEFT.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, testPayoutToken, response -> {
                Log.i("response",response);
                genratedToken = response.trim();
                amount = amount - 5;

                if(initiateCooldown)
                    amount = amount - 20;
                new AuthorizeToken().execute();
            }, error -> {

            });
            requestQueue.add(stringRequest);
            return null;
        }
    }

    public class MakePayout extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue requestQueue = Volley.newRequestQueue(InitiatePayoutForAdminNEFT.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, testPayoutToken, response -> {
                Log.i("response",response);
                genratedToken = response.trim();
                if(initiateCooldown)
                    amount = amount - 25;
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
            RequestQueue requestQueue = Volley.newRequestQueue(InitiatePayoutForAdminNEFT.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, testBearerToken, response -> {
                Log.i("response",response);
                if(response.trim().equals("Token is valid")){

                    makePaymentToVendor.execute();
                }
            }, error -> {

            }){
                @NonNull
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("token",genratedToken);
                    return params;
                }
            };
            requestQueue.add(stringRequest);
            return null;
        }
    }
    public class MakePaymentToVendor extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue requestQueue = Volley.newRequestQueue(InitiatePayoutForAdminNEFT.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, testPaymentToVendor, response -> {
                fastDialog.dismiss();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
                if(!moreThan20) {
                    databaseReference.child("totalPayoutAmount").setValue("0");
                    long timeCool = System.currentTimeMillis() + 21600000L;
                    databaseReference.child("coolDownPeriod").setValue(timeCool + "");
                    textView.setText("0");
                    Toast.makeText(InitiatePayoutForAdminNEFT.this, "Payout Initiated", Toast.LENGTH_SHORT).show();

                    finish();
                }else{
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            double current = Double.parseDouble(Objects.requireNonNull(snapshot.child("totalPayoutAmount").getValue(String.class)));
                            current = current - enteredAmount;
                            if(current == 0)
                                databaseReference.child("totalPayoutAmount").setValue("0");
                            else
                                databaseReference.child("totalPayoutAmount").setValue("" + current);

                            textView.setText("\u20b9" + current + "");

                            Toast.makeText(InitiatePayoutForAdminNEFT.this, "Payout Initiated", Toast.LENGTH_SHORT).show();
                            long timeCool = System.currentTimeMillis() + 21600000L;
                            databaseReference.child("coolDownPeriod").setValue(timeCool + "");
                            finish();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            },error -> {

            }){
                @Nullable
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> params = new HashMap<>();
                    params.put("benID",auth.getUid());
                    String genratedID = RandomString
                            .getAlphaNumericString(8);
                    genratedID = genratedID + System.currentTimeMillis();

                    params.put("transID",genratedID);
                    params.put("token",genratedToken);
                    params.put("amount", decimalFormat.format(amount));
                    return params;
                }
            };
            requestQueue.add(stringRequest);
            return null;
        }
    }
    public static class RandomString {

        // function to generate a random string of length n
        static String getAlphaNumericString(int n) {

            // chose a Character random from this String
            String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                    + "0123456789"
                    + "abcdefghijklmnopqrstuvxyz";

            // create StringBuffer size of AlphaNumericString
            StringBuilder sb = new StringBuilder(n);

            for (int i = 0; i < n; i++) {

                // generate a random number between
                // 0 to AlphaNumericString variable length
                int index
                        = (int) (AlphaNumericString.length()
                        * Math.random());

                // add Character one by one in end of sb
                sb.append(AlphaNumericString
                        .charAt(index));
            }

            return sb.toString();
        }
    }
}