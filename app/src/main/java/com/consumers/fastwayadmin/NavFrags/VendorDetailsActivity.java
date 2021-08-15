package com.consumers.fastwayadmin.NavFrags;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.consumers.fastwayadmin.Login.MainActivity;
import com.consumers.fastwayadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import karpuzoglu.enes.com.fastdialog.Animations;
import karpuzoglu.enes.com.fastdialog.FastDialog;
import karpuzoglu.enes.com.fastdialog.FastDialogBuilder;
import karpuzoglu.enes.com.fastdialog.NegativeClick;
import karpuzoglu.enes.com.fastdialog.PositiveClick;
import karpuzoglu.enes.com.fastdialog.Type;

public class VendorDetailsActivity extends AppCompatActivity {
    String name,email,phone,acNumber,acName,acIFSC;
    FirebaseAuth mAuth;
    String authURL = "https://payout-api.cashfree.com/payout/v1/authorize";
    Button proceed;
    EditText nameEdit,emailEdit,phoneEdit,accountNumber,accountName,IFSCcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_details);
        initialise();
        mAuth = FirebaseAuth.getInstance();

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


                verifyAuthURL();
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