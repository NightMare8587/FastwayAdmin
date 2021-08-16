package com.consumers.fastwayadmin.NavFrags;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.consumers.fastwayadmin.Login.MainActivity;
import com.consumers.fastwayadmin.R;
import com.developer.kalert.KAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import karpuzoglu.enes.com.fastdialog.Animations;
import karpuzoglu.enes.com.fastdialog.FastDialog;
import karpuzoglu.enes.com.fastdialog.FastDialogBuilder;
import karpuzoglu.enes.com.fastdialog.NegativeClick;
import karpuzoglu.enes.com.fastdialog.PositiveClick;
import karpuzoglu.enes.com.fastdialog.Type;

public class VendorDetailsActivity extends AppCompatActivity {
    String name,email,phone,acNumber,acName,acIFSC;
    String url = "https://intercellular-stabi.000webhostapp.com/addBankDetails.php";
    FirebaseAuth mAuth;
    String mauthId;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String authURL = "https://payout-api.cashfree.com/payout/v1/authorize";
    Button proceed;
    EditText nameEdit,emailEdit,phoneEdit,accountNumber,accountName,IFSCcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_details);
        initialise();
        mAuth = FirebaseAuth.getInstance();
        mauthId = String.valueOf(mAuth.getUid());
        sharedPreferences = getSharedPreferences("VendorID",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        proceed.setOnClickListener(v -> {
            if(nameEdit.getText().toString().equals("")){
                nameEdit.setError("Field can't be empty");
                nameEdit.requestFocus();
                return;
            }else if(emailEdit.getText().toString().equals("")){
                emailEdit.setError("Field can't be empty");
                emailEdit.requestFocus();
                return;
            }else if(phoneEdit.getText().toString().equals("")){
                phoneEdit.setError("Field can't be empty");
                phoneEdit.requestFocus();
                return;
            }else if(accountNumber.getText().toString().equals("")){
                accountNumber.requestFocus();
                accountNumber.setError("Field can't be empty");
                return;
            }else if(accountName.getText().toString().equals("")){
                accountName.requestFocus();
                accountName.setError("Field can't be empty");
                return;
            }else if(IFSCcode.getText().toString().equals("")){
                IFSCcode.requestFocus();
                IFSCcode.setError("Field can't be empty");
                return;
            }else{
                name = nameEdit.getText().toString();
                email = emailEdit.getText().toString();
                phone = phoneEdit.getText().toString();
                acName = accountName.getText().toString();
                acNumber = accountNumber.getText().toString();
                acIFSC = IFSCcode.getText().toString();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));

                KAlertDialog kAlertDialog = new KAlertDialog(VendorDetailsActivity.this,KAlertDialog.WARNING_TYPE)
                        .setTitleText("Warning")
                        .setContentText("Do you sure wanna continue with above credentials?")
                        .setConfirmText("Yes")
                        .setCancelText("No, Wait")
                        .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog kAlertDialog) {
                                RequestQueue requestQueue = Volley.newRequestQueue(VendorDetailsActivity.this);
                                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        if(response != null){
                                            Log.i("response",response);
                                            finish();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.i("error",error.getLocalizedMessage() + " ");
                                    }
                                }){
                                    @NonNull
                                    @Override
                                    public Map<String, String> getParams() throws AuthFailureError {
                                        Map<String,String> params = new HashMap<>();
                                        params.put("name",name + "");
                                        params.put("email",email + "");
                                        params.put("phone",phone);
                                        params.put("AccountNumber",acNumber);
                                        params.put("AccountName",acName);
                                        params.put("ifscCode",acIFSC);
                                        params.put("vendorID",mauthId);
                                        Log.i("details",name +  " " + mauthId + " ");
                                        return params;
                                    }
                                };
                                requestQueue.add(stringRequest);
                                VendorBankClass vendorBankClass = new VendorBankClass(name,email,acNumber,acName,acIFSC,phone,mauthId);
                                editor.putString("vendorDetails","yes");
                                editor.putString("accountNumber",acNumber);
                                editor.putString("accountName",acName);
                                editor.putString("ifscCode",acIFSC);
                                editor.apply();
                                reference.child("Bank Details").setValue(vendorBankClass);
                                kAlertDialog.dismissWithAnimation();
                            }
                        }).setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog kAlertDialog) {
                                kAlertDialog.dismissWithAnimation();
                            }
                        });

                kAlertDialog.setCanceledOnTouchOutside(true);
                kAlertDialog.create();
                kAlertDialog.show();
//                verifyAuthURL();
            }
        });
    }

    private void verifyAuthURL() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, authURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }
        };
    }


    private void initialise() {
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        nameEdit = findViewById(R.id.nameVendorEditText);
        emailEdit = findViewById(R.id.emailVendorEditText);
        phoneEdit = findViewById(R.id.numberVendorEditText);
        proceed = findViewById(R.id.proceedVendorDetails);
        accountName = findViewById(R.id.AccountHolderNameEditText);
        accountNumber = findViewById(R.id.AccountnumberVendorEditText);
        IFSCcode = findViewById(R.id.AccountIFSCcodeVendor);
        nameEdit.setText(name);
        emailEdit.setText(email);

    }

}