package com.consumers.fastwayadmin.NavFrags.BankVerification;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.consumers.fastwayadmin.R;
import com.developer.kalert.KAlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import karpuzoglu.enes.com.fastdialog.Animations;
import karpuzoglu.enes.com.fastdialog.FastDialog;
import karpuzoglu.enes.com.fastdialog.FastDialogBuilder;
import karpuzoglu.enes.com.fastdialog.Type;

public class VendorDetailsActivity extends AppCompatActivity {
    boolean verifyIFSC;
    boolean ifscVerified = false;
    String testPayoutToken = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/testToken.php";
    String testBearerToken = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/test/testBearerToken.php";
    String testPaymentToVendor = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/test/addTEstBen.php";
    String name, email, phone, acNumber, acName, acIFSC,acAddress;
    String url = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/addBenificiary.php";
    FirebaseAuth mAuth;

    
    String ifscURL = "https://intercellular-stabi.000webhostapp.com/verifyIFSCBank/verifyIFSC.php";
    String mauthId;
    FastDialog fastDialog;
    String prodVerifyBankAccount = "https://intercellular-stabi.000webhostapp.com/VerifyBankAccount/verifyAcc.php";
    String genratedToken = "";
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference;
    SharedPreferences sharedPreferences;
    String neftUrl = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/payoutNeft.php";
    SharedPreferences.Editor editor;
    String authToken = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/authBEarerToken.php";
    String authURL = "https://payout-api.cashfree.com/payout/v1/authorize";
    Button proceed;
    EditText nameEdit, emailEdit, phoneEdit, accountNumber, accountName, IFSCcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_details);
        initialise();
        AlertDialog.Builder builder = new AlertDialog.Builder(VendorDetailsActivity.this);
        builder.setTitle("IFSC Code").setMessage("Enter you IFSC Code");
        EditText editText = new EditText(VendorDetailsActivity.this);
        editText.setHint("Enter Code Here");
        editText.setMaxLines(11);
        LinearLayout linearLayout = new LinearLayout(VendorDetailsActivity.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(editText);
        builder.setView(linearLayout);
        builder.setCancelable(false);
        fastDialog = new FastDialogBuilder(VendorDetailsActivity.this,Type.PROGRESS)
                .progressText("Verifying Please Wait.....")
                .setAnimation(Animations.SLIDE_TOP)
                .create();
        builder.setPositiveButton("Verify", (dialogInterface, i) -> {
            if(editText.length() == 11){
                acIFSC = editText.getText().toString().toUpperCase(Locale.ROOT);
                fastDialog.show();
                new MakePayout().execute();
                verifyIFSC = true;
            }else
                Toast.makeText(VendorDetailsActivity.this, "Check your IFSC code again", Toast.LENGTH_SHORT).show();
        }).setNegativeButton("Cancel", (dialogInterface, i) -> {
            finish();
        });
        builder.create().show();
        mAuth = FirebaseAuth.getInstance();
        mauthId = String.valueOf(mAuth.getUid());

        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
        sharedPreferences = getSharedPreferences("VendorID", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        proceed.setOnClickListener(v -> {
            if(ifscVerified) {
                if (phoneEdit.getText().toString().equals("")) {
                    phoneEdit.setError("Field can't be empty");
                    phoneEdit.requestFocus();
                }else if(emailEdit.getText().toString().equals("")){
                    emailEdit.requestFocus();
                    emailEdit.setError("Field can't be empty");
                }
                else if (accountNumber.getText().toString().equals("")) {
                    accountNumber.requestFocus();
                    accountNumber.setError("Field can't be empty");
                } else if (accountName.getText().toString().equals("")) {
                    accountName.requestFocus();
                    accountName.setError("Field can't be empty");
                } else {
                    name = nameEdit.getText().toString();
                    email = emailEdit.getText().toString();
                    phone = phoneEdit.getText().toString();
                    acName = accountName.getText().toString();
                    acNumber = accountNumber.getText().toString();
                    acIFSC = IFSCcode.getText().toString();


                    KAlertDialog kAlertDialog = new KAlertDialog(VendorDetailsActivity.this, KAlertDialog.WARNING_TYPE)
                            .setTitleText("Warning")
                            .setContentText("Do you sure wanna continue with above credentials?")
                            .setConfirmText("Yes")
                            .setCancelText("No, Wait")
                            .setConfirmClickListener(kAlertDialog1 -> {

                                new MakePayout().execute();
                                fastDialog.show();
                                kAlertDialog1.dismissWithAnimation();
                            }).setCancelClickListener(KAlertDialog::dismissWithAnimation);

                    kAlertDialog.setCanceledOnTouchOutside(true);
                    kAlertDialog.create();
                    kAlertDialog.show();
//                verifyAuthURL();
                }
            }else
                Toast.makeText(this, "IFSC not verified", Toast.LENGTH_SHORT).show();
        });

    }


    private void initialise() {
        email = getIntent().getStringExtra("email");
        nameEdit = findViewById(R.id.nameVendorEditText);
        emailEdit = findViewById(R.id.emailVendorEditText);
        phoneEdit = findViewById(R.id.numberVendorEditText);
        proceed = findViewById(R.id.proceedVendorDetails);
        accountName = findViewById(R.id.AccountHolderAddressEditText);
        accountNumber = findViewById(R.id.AccountnumberVendorEditText);
        IFSCcode = findViewById(R.id.AccountIFSCcodeVendor);
        emailEdit.setText(email);

    }
    public class MakePayout extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue requestQueue = Volley.newRequestQueue(VendorDetailsActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, neftUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("response",response);
                    genratedToken = response.trim();
                    new AuthorizeToken().execute();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            requestQueue.add(stringRequest);
            return null;
        }
    }

    public class AuthorizeToken extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue requestQueue = Volley.newRequestQueue(VendorDetailsActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, authToken, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("response",response);

                    if(response.trim().equals("Token is valid")){
                        if(verifyIFSC)
                        new verifyBankIFSC().execute();
                        else
                            new verifyBankAcc().execute();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Nullable
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

    public class verifyBankAcc extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue requestQueue = Volley.newRequestQueue(VendorDetailsActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, prodVerifyBankAccount, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    List<String> lst = Arrays.asList(response.split("/"));
                    if(lst.get(0).equals("SUCCESS") && lst.get(0).equals("VALID") && lst.get(1).equals("ACCOUNT_IS_VALID")){
                        new AddBenificiary().execute();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                public Map<String, String> getParams(){
                    Map<String,String> params = new HashMap<>();
                    params.put("accNum",acNumber);
                    params.put("ifscCode",acIFSC);
                    params.put("name",name);
                    params.put("token",genratedToken);
                    params.put("number",phone);
                    return params;
                }
            };
            requestQueue.add(stringRequest);
            return null;
        }
    }

    public class verifyBankIFSC extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue requestQueue = Volley.newRequestQueue(VendorDetailsActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, ifscURL, response -> {
                verifyIFSC = false;
                fastDialog.dismiss();
                ifscVerified = true;
                List<String> lst = Arrays.asList(response.split("/"));
                if(lst.get(0).equals("SUCCESS") && lst.get(1).equals("Ifsc verification successful")){
                    nameEdit.setText(lst.get(3));
                    acAddress = lst.get(2);
                    nameEdit.setEnabled(false);
                    IFSCcode.setText(acIFSC);
                    IFSCcode.setEnabled(false);
                }
            }, error -> {

            }){
                @Nullable
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> params = new HashMap<>();
                    params.put("token",genratedToken);
                    params.put("ifsc",acIFSC);
                    return params;
                }
            };
            requestQueue.add(stringRequest);
            return null;
        }
    }

    public class AddBenificiary extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue requestQueue = Volley.newRequestQueue(VendorDetailsActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
                Log.i("response",response.trim());
//                Log.i("response",response.trim());
                Toast.makeText(VendorDetailsActivity.this, "" + response.trim(), Toast.LENGTH_SHORT).show();
                fastDialog.dismiss();
                if(response.trim().equals("SUCCESS"))
                    Toast.makeText(VendorDetailsActivity.this, "Beneficiary added successfully", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(VendorDetailsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(VendorDetailsActivity.this,SelectPayoutMethodType.class));
                    }
                });
            }, error -> {

            }){
                @Nullable
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> params = new HashMap<>();
                    VendorBankClass vendorBankClass = new VendorBankClass(nameEdit.getText().toString(), email, acNumber, acName, acIFSC, phone, mauthId,acAddress);
                    editor.putString("vendorDetails", "yes");
                    editor.putString("accountNumber", acNumber);
                    editor.putString("address", acAddress);
                    editor.putString("ifscCode", acIFSC);
                    editor.apply();
                    reference.child("Bank Details").setValue(vendorBankClass);
                    params.put("token",genratedToken);
                    params.put("benID",mauthId);
                    params.put("name",accountName.getText().toString());
                    params.put("email",email);
                    params.put("phone",phone);
                    params.put("bankAc",acNumber);
                    params.put("ifsc",acIFSC);
                    params.put("address",acAddress);
                    return params;
                }
            };
            requestQueue.add(stringRequest);
            return null;
        }
    }
}