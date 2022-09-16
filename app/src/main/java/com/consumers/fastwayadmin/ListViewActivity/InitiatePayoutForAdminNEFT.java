package com.consumers.fastwayadmin.ListViewActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.consumers.fastwayadmin.NavFrags.BankVerification.SelectPayoutMethodType;
import com.consumers.fastwayadmin.NavFrags.homeFrag.ApproveCurrentOrder;
import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thecode.aestheticdialogs.DialogType;

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
  MakePaymentToVendor makePaymentToVendor = new MakePaymentToVendor();
    String genratedToken;
    FastDialog fastDialog;
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


        button = findViewById(R.id.initiatePaymentForPayoutAdmin);
        changeMethod = findViewById(R.id.changePayoutMethodNeftorAdmin);
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("totalPayoutAmount")){
                    availableForPayout = true;
                     amount = Double.parseDouble(String.valueOf(snapshot.child("totalPayoutAmount").getValue()));
                     textView.setText(decimalFormat.format(amount));
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

        changeMethod.setOnClickListener(click -> {
            startActivity(new Intent(InitiatePayoutForAdminNEFT.this, SelectPayoutMethodType.class));
        });

        button.setOnClickListener(click -> {
            if(availableForPayout && amount != 0){
                AlertDialog.Builder builder = new AlertDialog.Builder(InitiatePayoutForAdminNEFT.this);
                builder.setTitle("Choose one option").setMessage("Choose one payout option\nGet payout after 4 to 5 hours (NEFT)\nGet Instant Payout (IMPS)")
                        .setPositiveButton("Choose NEFT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new MakePayout().execute();
                                fastDialog.show();
                                dialog.dismiss();
                            }
                        }).setNegativeButton("Choose IMPS", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new MakePayoutIMPS().execute();
                                fastDialog.show();
                                dialog.dismiss();
                            }
                        }).setNeutralButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });
    }

    public class MakePayoutIMPS extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue requestQueue = Volley.newRequestQueue(InitiatePayoutForAdminNEFT.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, testPayoutToken, response -> {
                Log.i("response",response);
                genratedToken = response.trim();
                amount = amount - 7;
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
                amount = amount - 5;
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
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid());
                databaseReference.child("totalPayoutAmount").setValue("0");
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