package com.consumers.fastwayadmin.NavFrags;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditVendorDetails extends AppCompatActivity {
    String name,email,phone,acNumber,acName,acIFSC;
    EditText nameEdit,emailEdit,phoneEdit,accountNumber,accountName,IFSCcode;
    String mauthId;
    FirebaseAuth mAuth;
    String acAddress;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String url = "https://intercellular-stabi.000webhostapp.com/editBankDetaills.php";
    Button proceed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_vendor_details);
        initiaalise();
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

                KAlertDialog kAlertDialog = new KAlertDialog(EditVendorDetails.this,KAlertDialog.WARNING_TYPE)
                        .setTitleText("Warning")
                        .setContentText("Do you sure wanna edit your bank credentials?")
                        .setConfirmText("Yes")
                        .setCancelText("No, Wait")
                        .setConfirmClickListener(kAlertDialog1 -> {
                            EditCredClass editCredClass = new EditCredClass(name,email,acNumber,acName,acIFSC,phone,auth.getUid() + "");
                            DatabaseReference changeCred = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("Changes Credentials");
                            changeCred.child(auth.getUid()).setValue(editCredClass);
//                            RequestQueue requestQueue = Volley.newRequestQueue(EditVendorDetails.this);
//                            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//                                @Override
//                                public void onResponse(String response) {
//                                    if(response != null){
//                                        Log.i("response",response);
//                                        Toast.makeText(EditVendorDetails.this, "Changes Saved Successfully", Toast.LENGTH_SHORT).show();
//                                        finish();
//                                    }
//                                }
//                            }, new Response.ErrorListener() {
//                                @Override
//                                public void onErrorResponse(VolleyError error) {
//                                    Log.i("error",error.getLocalizedMessage() + " ");
//                                }
//                            }){
//                                @NonNull
//                                @Override
//                                public Map<String, String> getParams() throws AuthFailureError {
//                                    Map<String,String> params = new HashMap<>();
//                                    params.put("name",name + "");
//                                    params.put("email",email + "");
//                                    params.put("phone",phone);
//                                    params.put("AccountNumber",acNumber);
//                                    params.put("AccountName",acName);
//                                    params.put("ifscCode",acIFSC);
//                                    params.put("vendorID",mauthId);
//                                    Log.i("details",name +  " " + mauthId + " ");
//                                    return params;
//                                }
//                            };
//                            requestQueue.add(stringRequest);
                            VendorBankClass vendorBankClass = new VendorBankClass(name,email,acNumber,acName,acIFSC,phone,mauthId,acAddress);
                            editor.putString("vendorDetails","yes");
                            editor.putString("accountNumber",acNumber);
                            editor.putString("accountName",acName);
                            editor.putString("ifscCode",acIFSC);
                            editor.apply();
                            reference.child("Bank Details").setValue(vendorBankClass);
                            setResult(100);
                            kAlertDialog1.dismissWithAnimation();
                            finish();

                        }).setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog kAlertDialog) {
                                kAlertDialog.dismissWithAnimation();
                            }
                        });

                kAlertDialog.setCanceledOnTouchOutside(true);
                kAlertDialog.create();
                kAlertDialog.show();
            }
        });
    }

    private void initiaalise() {
        nameEdit = findViewById(R.id.editVendorName);
        emailEdit = findViewById(R.id.editVendorEmail);
        phoneEdit = findViewById(R.id.editVendorNumber);
        proceed = findViewById(R.id.editVendorProceedButton);
        accountName = findViewById(R.id.editVendorACName);
        accountNumber = findViewById(R.id.editVendorACNumber);
        IFSCcode = findViewById(R.id.editVendorIFSCCode);
    }
}