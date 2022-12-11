package com.consumers.fastwayadmin.ListViewActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    boolean cooldown = false;
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
//                        Toast.makeText(this, "Remaining: " + TimeUnit.MILLISECONDS.to(coolDownTime - System.currentTimeMillis()), Toast.LENGTH_SHORT).show();
                        return;
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
                            Toast.makeText(InitiatePayoutForAdminNEFT.this, "Amount Should be less than 30k", Toast.LENGTH_SHORT).show();
                        else{
                            enteredAmount = Double.parseDouble(editText.getText().toString());
                            if(enteredAmount > amount) {
                                Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(InitiatePayoutForAdminNEFT.this);
                            builder.setTitle("Choose one option").setMessage("Choose one payout option\nGet payout after 2 to 4 hours (NEFT) \nGet Instant Payout (IMPS \u20b95 charge)")
                                    .setPositiveButton("Choose NEFT", (dialog, which) -> {
                                        new MakePayout().execute();
                                        fastDialog.show();
                                        dialog.dismiss();
                                    }).setNegativeButton("Choose IMPS", (dialog, which) -> {
                                        new MakePayoutIMPS().execute();
                                        fastDialog.show();
                                        dialog.dismiss();
                                    }).setNeutralButton("Exit", (dialog, which) -> dialog.dismiss()).create().show();
                        }
                    }).setNegativeButton("Wait", (dialogInterface, i) -> {

                    }).create();
                    alert.setView(linearLayout);
                    alert.show();

                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(InitiatePayoutForAdminNEFT.this);
                builder.setTitle("Choose one option").setMessage("Choose one payout option\nGet payout after 2 to 4 hours (NEFT)\nGet Instant Payout (IMPS \u20b95 charge)")
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
                        }).setNeutralButton("Exit", (dialog, which) -> dialog.dismiss()).create().show();
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


                            long timeCool = System.currentTimeMillis() + 21600000L;
                            databaseReference.child("coolDownPeriod").setValue(timeCool + "");

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