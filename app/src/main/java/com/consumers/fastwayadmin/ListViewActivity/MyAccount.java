package com.consumers.fastwayadmin.ListViewActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.consumers.fastwayadmin.Info.ChangeResLocation.NewLocationRestaurant;
import com.consumers.fastwayadmin.Info.FoodDeliveryIntegration;
import com.consumers.fastwayadmin.Info.Info;
import com.consumers.fastwayadmin.Info.RestaurantDocuments.ReUploadDocuments.ViewAndReuploadDocuments;
import com.consumers.fastwayadmin.ListViewActivity.LeaveFastwayPackage.LeaveFastway;
import com.consumers.fastwayadmin.ListViewActivity.StaffDetails.RestaurantStaff;
import com.consumers.fastwayadmin.NavFrags.EditVendorDetails;
import com.consumers.fastwayadmin.NavFrags.ReUploadResImages.ReUploadRestaurantImages;
import com.consumers.fastwayadmin.R;
import com.consumers.fastwayadmin.SplashAndIntro.SplashScreen;
import com.developer.kalert.KAlertDialog;
import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import nl.invissvenska.modalbottomsheetdialog.Item;
import nl.invissvenska.modalbottomsheetdialog.ModalBottomSheetDialog;

public class MyAccount extends AppCompatActivity implements ModalBottomSheetDialog.Listener {
    ListView listView;
    DatabaseReference reference;
    FirebaseAuth auth;
    GoogleSignInClient client;
    GoogleSignInOptions gso;
    SharedPreferences sharedPreferences;
    String UID;
    StorageReference storageReference;
    FirebaseStorage storage;
    TextView resNameText;
    ModalBottomSheetDialog modalBottomSheetDialog;
    SharedPreferences.Editor editor;
    TextView textView;
    String[] names = {"Change Credentials (Admin)","Change Credentials (Restaurants)","Restaurant Images","Delivery Integration","Change Bank Credentials","Restaurant Documents","Restaurant Staff Details","Leave Ordinalo","Transfer Ownership","Service Charge"};
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        initialise();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        SharedPreferences resInfoSharedPref = getSharedPreferences("RestaurantInfo",MODE_PRIVATE);
        editor = resInfoSharedPref.edit();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.list, names);
        listView.setAdapter(arrayAdapter);
        SharedPreferences.Editor loginEdit;
        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        loginEdit = sharedPreferences.edit();
        if(resInfoSharedPref.contains("hotelName")) {
            resNameText.setText(resInfoSharedPref.getString("hotelName", ""));
        }else{
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection(sharedPreferences.getString("state","")).document("Restaurants").collection(sharedPreferences.getString("locality","")).document(auth.getUid())
                    .get().addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            Map<String,Object> map = documentSnapshot.getData();
                            if(map.containsKey("name")){
                                SharedPreferences.Editor editor = resInfoSharedPref.edit();
                    editor.putString("hotelName", (String) map.get("name"));
                    editor.apply();
                    resNameText.setText(resInfoSharedPref.getString("hotelName", ""));
                            }
                        }
                    });
//            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(UID));
//            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    if(snapshot.exists()){
//                        SharedPreferences.Editor editor = resInfoSharedPref.edit();
//                        editor.putString("hotelName",snapshot.child("name").getValue(String.class));
//                        editor.apply();
//                        resNameText.setText(resInfoSharedPref.getString("hotelName", ""));
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
        }
        textView.setText("Hi, " + sharedPreferences.getString("name",""));
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            switch (i){
                case 0:
                    modalBottomSheetDialog = new ModalBottomSheetDialog.Builder()
                            .setRoundedModal(true)
                            .setHeader("Choose One Option")
                            .add(R.menu.bottom_sheet)
                            .build();
                    modalBottomSheetDialog.show(getSupportFragmentManager(),"admin");

                    break;
                case 1:
                    modalBottomSheetDialog = new ModalBottomSheetDialog.Builder()
                            .setRoundedModal(true)
                            .setHeader("Choose One Option")
                            .add(R.menu.bottom_sheet_restaurant)
                            .build();
                    modalBottomSheetDialog.show(getSupportFragmentManager(),"restaurant");
                    break;

                case 2:
                    Intent intents = new Intent(MyAccount.this, ReUploadRestaurantImages.class);
                    SharedPreferences sharedPreferences1 = getSharedPreferences("loginInfo",MODE_PRIVATE);
                    intents.putExtra("state",sharedPreferences1.getString("state",""));
                    intents.putExtra("locality",sharedPreferences1.getString("locality",""));
                    startActivity(intents);
                    break;

                case 3:
                    startActivity(new Intent(MyAccount.this, FoodDeliveryIntegration.class));
//                    new KAlertDialog(MyAccount.this,KAlertDialog.WARNING_TYPE)
//                            .setTitleText("Delete Account")
//                            .setContentText("Do you sure wanna delete your account!!! All of your data will be removed\n"+"This action can't be revert")
//                            .setConfirmText("Yes, Delete")
//                            .setConfirmClickListener(kAlertDialog -> {
//
//                                try {
//                                    new removeAll().execute();
//                                }
//                                catch (Exception e){
//                                    Log.i("logs",e.getLocalizedMessage());
//                                }
//                            }).setCancelText("No, Wait")
//                            .setCancelClickListener(KAlertDialog::dismissWithAnimation).show();
                    break;
                case 4:
                    Intent intent = new Intent(MyAccount.this, EditVendorDetails.class);
                    startActivityForResult(intent,2);
                    break;
                case 5:
                    startActivity(new Intent(MyAccount.this, ViewAndReuploadDocuments.class));
                    break;
                case 6:
                    startActivity(new Intent(MyAccount.this, RestaurantStaff.class));
                    break;
                case 7:
                    startActivity(new Intent(MyAccount.this, LeaveFastway.class));
                    break;
                case 8:
                    AlertDialog.Builder builder = new AlertDialog.Builder(MyAccount.this);
                    builder.setTitle("Transfer Ownership")
                            .setMessage("Transfer Ownerships means giving other person all admin rights ( in simple words transferring account access to another person)\nDo you sure wanna proceed?\nAn executive from ordinalo will visit and will start the transfer process")
                            .setPositiveButton("Transfer Initiate", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),"RestaurantEarningTracker.xlsx");
                                    storageReference.child(Objects.requireNonNull(auth.getUid()) + "/" + "RestaurantEarningTracker.xlsx").putFile(Uri.fromFile(file))
                                            .addOnCompleteListener(task -> {
                                                if(task.isSuccessful())
                                                    Toast.makeText(MyAccount.this, "Completed", Toast.LENGTH_SHORT).show();
                                            });
                                    DatabaseReference transferRequest = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("TransferRequest").child(UID);
                                    Map<String,String> map = new HashMap();
                                    map.put("id",UID);
                                    transferRequest.setValue(map);
                                }
                            }).setNegativeButton("No, Wait", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).create();
                    builder.setCancelable(false);
                    builder.show();
                    break;

                case 9:
                    if(sharedPreferences.contains("serviceCharge")){
                        AlertDialog.Builder builderS = new AlertDialog.Builder(MyAccount.this);
                        builderS.setTitle("Service Charge").setMessage("Choose one option from below buttons")
                                .setPositiveButton("Remove Service Charge", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                        DocumentReference documentReference = firestore.collection(sharedPreferences.getString("state","")).document("Restaurants").collection(sharedPreferences.getString("state","")).document(UID);
                                        Map<String,Object> updates = new HashMap<>();
                                        updates.put("serviceCharge", FieldValue.delete());
                                        documentReference.update(updates);

                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("state",""))
                                                .child(UID);
                                        databaseReference.child("serviceCharge").removeValue();
                                        loginEdit.remove("serviceCharge").apply();
                                    }
                                }).setNegativeButton("Change % charge", (dialogInterface, i1) -> {
                                    AlertDialog.Builder builderN = new AlertDialog.Builder(MyAccount.this);
                                    builderN.setTitle("Service Charge").setMessage("Service charge is only applicable on dining not on takeaway");
                                    LinearLayout linearLayout = new LinearLayout(MyAccount.this);
                                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                                    EditText editText = new EditText(MyAccount.this);
                                    editText.setHint("Enter % to be applied");
                                    linearLayout.addView(editText);
                                    builderN.setView(linearLayout);
                                    builderN.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i1) {
                                            String s = editText.getText().toString();
                                            if(s.equals("") || s.equals("0")) {
                                                Toast.makeText(MyAccount.this, "Invalid Input", Toast.LENGTH_SHORT).show();
                                            }else{
                                                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                                firestore.collection(sharedPreferences.getString("state","")).document("Restaurants").collection(sharedPreferences.getString("state","")).document(UID)
                                                        .update("serviceCharge",s);
                                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("state",""))
                                                        .child(UID);
                                                databaseReference.child("serviceCharge").setValue(s);
                                                loginEdit.putString("serviceCharge",s);
                                                loginEdit.apply();
                                            }
                                        }
                                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i1) {

                                        }
                                    });
                                    builderN.create().show();
                                }).setNeutralButton("Exit", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                        builderS.create().show();
                    }else{
                        AlertDialog.Builder builderN = new AlertDialog.Builder(MyAccount.this);
                        builderN.setTitle("Service Charge").setMessage("Service charge is only applicable on dining not on takeaway");
                        LinearLayout linearLayout = new LinearLayout(MyAccount.this);
                        linearLayout.setOrientation(LinearLayout.VERTICAL);
                        EditText editText = new EditText(MyAccount.this);
                        editText.setHint("Enter % to be applied");
                        linearLayout.addView(editText);

                        builderN.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i1) {
                                String s = editText.getText().toString();
                                if(s.equals("") || s.equals("0")) {
                                    Toast.makeText(MyAccount.this, "Invalid Input", Toast.LENGTH_SHORT).show();
                                }else{
                                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                    firestore.collection(sharedPreferences.getString("state","")).document("Restaurants").collection(sharedPreferences.getString("state","")).document(UID)
                                            .update("serviceCharge",s);
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("state",""))
                                            .child(UID);
                                    databaseReference.child("serviceCharge").setValue(s);
                                    loginEdit.putString("serviceCharge",s);
                                    loginEdit.apply();
                                }
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i1) {

                            }
                        });
                        builderN.setView(linearLayout);
                        builderN.create().show();
                    }
                    break;

            }
        });
    }
    private void initialise() {
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        textView = findViewById(R.id.account_activity_text);
        resNameText = findViewById(R.id.resNameAccountFrag);
        listView = findViewById(R.id.accountActivityListView);
        auth = FirebaseAuth.getInstance();
        UID = auth.getUid() + "";
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(UID));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onItemSelected(String tag, Item item) {
        if (tag.equals("admin")) {
            reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(UID));
            int id = item.getId();
            switch (id) {
                case R.id.changeNameBottomSheet:
                    FlatDialog flatDialog = new FlatDialog(MyAccount.this);
                    flatDialog.setTitle("Enter New Name")
                            .setBackgroundColor(Color.WHITE)
                            .setTitleColor(Color.BLACK)
                            .setFirstTextFieldHint("Enter New Name")
                            .setFirstTextFieldBorderColor(Color.BLACK)
                            .setFirstTextFieldHintColor(Color.BLACK)
                            .setFirstTextFieldTextColor(Color.BLACK)
                            .setFirstTextFieldTextColor(Color.BLACK)
                            .setFirstButtonText("Make Changes")
                            .setFirstButtonColor(Color.LTGRAY)
                            .setFirstButtonTextColor(Color.BLACK)
                            .setSecondButtonText("Cancel")
                            .setSecondButtonColor(Color.CYAN)
                            .setSecondButtonTextColor(Color.BLACK)
                            .withFirstButtonListner(view -> {
                                Map<String, Object> map = new HashMap<>();
                                Log.i("auth", UID);
                                map.put("name", String.valueOf(flatDialog.getFirstTextField()));
                                reference.child("name").setValue(flatDialog.getFirstTextField());
                                Toast.makeText(MyAccount.this, "Name Changed Successfully", Toast.LENGTH_SHORT).show();
                                flatDialog.dismiss();
                            })
                            .withSecondButtonListner(view -> flatDialog.dismiss());

                    flatDialog.create();
                    flatDialog.show();
                    modalBottomSheetDialog.dismiss();
                    break;
                case R.id.changeEmailBottomSheet:
                    FlatDialog flatDialog1 = new FlatDialog(MyAccount.this);
                    flatDialog1.setTitle("Enter New Name")
                            .setBackgroundColor(Color.WHITE)
                            .setTitleColor(Color.BLACK)
                            .setFirstTextFieldBorderColor(Color.BLACK)
                            .setFirstTextFieldHintColor(Color.BLACK)
                            .setFirstTextFieldTextColor(Color.BLACK)
                            .setFirstTextFieldHint("Enter New Name")
                            .setFirstButtonText("Make Changes")
                            .setFirstButtonColor(Color.LTGRAY)
                            .setFirstButtonTextColor(Color.BLACK)
                            .setSecondButtonText("Cancel")
                            .setSecondButtonColor(Color.CYAN)
                            .setSecondButtonTextColor(Color.BLACK)
                            .withFirstButtonListner(view -> {
                                Map<String, Object> map = new HashMap<>();
                                Log.i("auth", UID);
                                map.put("name", String.valueOf(flatDialog1.getFirstTextField()));
                                reference.child("email").setValue(flatDialog1.getFirstTextField());
                                Toast.makeText(MyAccount.this, "Email Changed Successfully", Toast.LENGTH_SHORT).show();
                                flatDialog1.dismiss();
                            })
                            .withSecondButtonListner(view -> flatDialog1.dismiss());

                    flatDialog1.create();
                    flatDialog1.show();
                    modalBottomSheetDialog.dismiss();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + id);
            }
        }else if(tag.equals("restaurant")){
            reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(UID));
            int id = item.getId();
            switch (id){
                case R.id.changeNameBottomSheetRestaurant:
//                    reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(UID));
                    FlatDialog flatDialog = new FlatDialog(MyAccount.this);
                    flatDialog.setTitle("Enter New Name")
                            .setBackgroundColor(Color.WHITE)
                            .setTitleColor(Color.BLACK)
                            .setFirstTextFieldHint("Enter New Name")
                            .setFirstTextFieldBorderColor(Color.BLACK)
                            .setFirstTextFieldHintColor(Color.BLACK)
                            .setFirstTextFieldTextColor(Color.BLACK)
                            .setFirstTextFieldTextColor(Color.BLACK)
                            .setFirstButtonText("Make Changes")
                            .setFirstButtonColor(Color.LTGRAY)
                            .setFirstButtonTextColor(Color.BLACK)
                            .setSecondButtonText("Cancel")
                            .setSecondButtonColor(Color.CYAN)
                            .setSecondButtonTextColor(Color.BLACK)
                            .withFirstButtonListner(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(flatDialog.getFirstTextField().length() == 0){
                                        Toast.makeText(MyAccount.this, "Field can't be empty", Toast.LENGTH_SHORT).show();
                                        return;
                                    }else {
                                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                        firestore.collection(sharedPreferences.getString("state","")).document("Restaurants").collection(sharedPreferences.getString("locality","")).document(auth.getUid())
                                                .update("name",flatDialog.getFirstTextField().toString());
                                        reference.child("name").setValue(flatDialog.getFirstTextField().toString());
                                        resNameText.setText(flatDialog.getFirstTextField());
                                        Toast.makeText(MyAccount.this, "Name Changed Successfully", Toast.LENGTH_SHORT).show();
                                        editor.putString("hotelName",flatDialog.getFirstTextField());
                                        editor.apply();
                                        flatDialog.dismiss();
                                    }
                                }
                            })
                            .withSecondButtonListner(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(MyAccount.this, "Cancelled", Toast.LENGTH_SHORT).show();
                                    flatDialog.dismiss();
                                }
                            });

                    flatDialog.create();
                    flatDialog.show();
                    modalBottomSheetDialog.dismiss();
                    break;
                case R.id.changeEmailBottomSheetRestaurant:
                    FlatDialog flatDialog1 = new FlatDialog(MyAccount.this);
                    flatDialog1.setTitle("Enter New Name")
                            .setBackgroundColor(Color.WHITE)
                            .setTitleColor(Color.BLACK)
                            .setFirstTextFieldBorderColor(Color.BLACK)
                            .setFirstTextFieldHintColor(Color.BLACK)
                            .setFirstTextFieldTextColor(Color.BLACK)
                            .setFirstTextFieldHint("Enter New Email")
                            .setFirstButtonText("Make Changes")
                            .setFirstButtonColor(Color.LTGRAY)
                            .setFirstButtonTextColor(Color.BLACK)
                            .setSecondButtonText("Cancel")
                            .setSecondButtonColor(Color.CYAN)
                            .setSecondButtonTextColor(Color.BLACK)
                            .withFirstButtonListner(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(flatDialog1.getFirstTextField().length() == 0){
                                        Toast.makeText(MyAccount.this, "Field can't be empty", Toast.LENGTH_SHORT).show();
                                        return;
                                    }else {
                                        reference.child("email").setValue(flatDialog1.getFirstTextField().toString());
                                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                        firestore.collection(sharedPreferences.getString("state","")).document("Restaurants").collection(sharedPreferences.getString("locality","")).document(auth.getUid())
                                                .update("email",flatDialog1.getFirstTextField().toString());
                                        Toast.makeText(MyAccount.this, "Email Changed Successfully", Toast.LENGTH_SHORT).show();
                                        flatDialog1.dismiss();
                                    }
                                }
                            })
                            .withSecondButtonListner(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    flatDialog1.dismiss();
                                }
                            });

                    flatDialog1.create();
                    flatDialog1.show();
                    modalBottomSheetDialog.dismiss();
                    break;
                case R.id.changePhoneNumberBottomSheetRestaurant:
                    FlatDialog flatDialog2 = new FlatDialog(MyAccount.this);
                    flatDialog2.setTitle("Enter New Number")
                            .setBackgroundColor(Color.WHITE)
                            .setTitleColor(Color.BLACK)
                            .setFirstTextFieldBorderColor(Color.BLACK)
                            .setFirstTextFieldHintColor(Color.BLACK)
                            .setFirstTextFieldTextColor(Color.BLACK)
                            .setFirstTextFieldHint("Enter New Number")
                            .setFirstButtonText("Make Changes")
                            .setFirstButtonColor(Color.LTGRAY)
                            .setFirstButtonTextColor(Color.BLACK)
                            .setSecondButtonText("Cancel")
                            .setSecondButtonColor(Color.CYAN)
                            .setSecondButtonTextColor(Color.BLACK)
                            .withFirstButtonListner(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(flatDialog2.getFirstTextField().length() == 0){
                                        Toast.makeText(MyAccount.this, "Field can't be empty", Toast.LENGTH_SHORT).show();
                                        return;
                                    }else {
                                        reference.child("number").setValue(flatDialog2.getFirstTextField().toString());
                                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                        firestore.collection(sharedPreferences.getString("state","")).document("Restaurants").collection(sharedPreferences.getString("locality","")).document(auth.getUid())
                                                .update("number",flatDialog2.getFirstTextField().toString());
                                        Toast.makeText(MyAccount.this, "Number Changed Successfully", Toast.LENGTH_SHORT).show();
                                        flatDialog2.dismiss();
                                    }
                                }
                            })
                            .withSecondButtonListner(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    flatDialog2.dismiss();
                                }
                            });

                    flatDialog2.create();
                    flatDialog2.show();
                    modalBottomSheetDialog.dismiss();
                    break;
                case R.id.changeAddressBottomSheetRestaurant:
                    startActivity(new Intent(MyAccount.this, NewLocationRestaurant.class));
//                    AlertDialog.Builder alert = new AlertDialog.Builder(MyAccount.this);
//                    alert.setTitle("Change Info");
//                    LinearLayout layout = new LinearLayout(MyAccount.this);
//                    layout.setOrientation(LinearLayout.VERTICAL);
//                    EditText resAddress = new EditText(MyAccount.this);
//                    EditText resNearby = new EditText(MyAccount.this);
//                    EditText resPIN = new EditText(MyAccount.this);
//                    resPIN.setInputType(InputType.TYPE_CLASS_NUMBER);
//                    resAddress.setHint("Enter new address");
//                    resNearby.setHint("Enter new nearby");
//                    resPIN.setHint("Enter new pin");
//                    layout.addView(resAddress);
//                    layout.addView(resNearby);
//                    layout.addView(resPIN);
//                    alert.setPositiveButton("Make Changes", (dialogInterface, i) -> {
//                        if(!resPIN.getText().toString().equals("") && !resAddress.getText().toString().equals("") && !resNearby.getText().toString().equals("")) {
//                            reference.child("address").setValue(resAddress.getText().toString());
//                            reference.child("nearby").setValue(resNearby.getText().toString());
//                            reference.child("pin").setValue(resPIN.getText().toString());
//                            dialogInterface.dismiss();
//                            Toast.makeText(MyAccount.this, "Address And Nearby Changed Successfully", Toast.LENGTH_SHORT).show();
//                                    startActivityForResult(new Intent(MyAccount.this, MapsActivity2.class),69);
//
//                        }else
//                            Toast.makeText(MyAccount.this, "Field can't be empty", Toast.LENGTH_SHORT).show();
//
//                    }).setNegativeButton("No, Wait", (dialogInterface, i) -> dialogInterface.dismiss());
//                    alert.setView(layout);
//                    alert.create().show();
//                    modalBottomSheetDialog.dismiss();
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 69){
           new KAlertDialog(MyAccount.this,KAlertDialog.SUCCESS_TYPE)
                   .setTitleText("Success")
                   .setContentText("Location Changes Successfully")
                   .setConfirmText("Great")
                   .setConfirmClickListener(KAlertDialog::dismissWithAnimation).show();
        }else if(resultCode == 100){
            new KAlertDialog(MyAccount.this,KAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Success")
                    .setContentText("If Approved credentials will be changes in next 24hrs")
                    .setConfirmText("Great")
                    .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                        @Override
                        public void onClick(KAlertDialog kAlertDialog) {
                            kAlertDialog.dismissWithAnimation();
                        }
                    }).show();
        }
    }

    public class removeAll extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            auth = FirebaseAuth.getInstance();
            reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(UID);
            reference.removeValue();
            reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(UID);
            reference.removeValue();
            SharedPreferences settings = getSharedPreferences("loginInfo", MODE_PRIVATE);
            settings.edit().clear().commit();

            SharedPreferences res = getSharedPreferences("RestaurantInfo", MODE_PRIVATE);
            res.edit().clear().apply();

            SharedPreferences intro = getSharedPreferences("IntroAct", MODE_PRIVATE);
            intro.edit().clear().apply();

            SharedPreferences current = getSharedPreferences("locations current", MODE_PRIVATE);
            current.edit().clear().apply();

            SharedPreferences AdminPremiumDetails = getSharedPreferences("AdminPremiumDetails", MODE_PRIVATE);
            AdminPremiumDetails.edit().clear().apply();

            SharedPreferences CalenderForExcel = getSharedPreferences("CalenderForExcel", MODE_PRIVATE);
            CalenderForExcel.edit().clear().apply();

            SharedPreferences resDailyStore = getSharedPreferences("RestaurantDailyStoreForAnalysis", MODE_PRIVATE);
            resDailyStore.edit().clear().apply();


            SharedPreferences last7days = getSharedPreferences("last7daysReport", MODE_PRIVATE);
            last7days.edit().clear().apply();

            SharedPreferences storeOrders = getSharedPreferences("StoreOrders", MODE_PRIVATE);
            storeOrders.edit().clear().apply();

            SharedPreferences location = getSharedPreferences("LocationMaps", MODE_PRIVATE);
            location.edit().clear().apply();

            SharedPreferences storeImages = getSharedPreferences("storeImages", MODE_PRIVATE);
            storeImages.edit().clear().apply();

            SharedPreferences StoreDataForPayInEnd = getSharedPreferences("StoreDataForPayInEnd", MODE_PRIVATE);
            StoreDataForPayInEnd.edit().clear().apply();

            SharedPreferences cashCommission = getSharedPreferences("CashCommission", MODE_PRIVATE);
            cashCommission.edit().clear().apply();

            SharedPreferences DailyUserTrackingFor7days = getSharedPreferences("DailyUserTrackingFor7days", MODE_PRIVATE);
            DailyUserTrackingFor7days.edit().clear().apply();

            SharedPreferences DailyAverageOrderMonthly = getSharedPreferences("DailyAverageOrderMonthly", MODE_PRIVATE);
            DailyAverageOrderMonthly.edit().clear().apply();

            SharedPreferences RestaurantTrackingDaily = getSharedPreferences("RestaurantTrackingDaily", MODE_PRIVATE);
            RestaurantTrackingDaily.edit().clear().apply();

            SharedPreferences RestaurantTrackRecords = getSharedPreferences("RestaurantTrackRecords", MODE_PRIVATE);
            RestaurantTrackRecords.edit().clear().apply();

            SharedPreferences UserFrequencyPerMonth = getSharedPreferences("UserFrequencyPerMonth", MODE_PRIVATE);
            UserFrequencyPerMonth.edit().clear().apply();

            SharedPreferences DishOrderedWithOthers = getSharedPreferences("DishOrderedWithOthers", MODE_PRIVATE);
            DishOrderedWithOthers.edit().clear().apply();

            SharedPreferences DishAnalysis = getSharedPreferences("DishAnalysis", MODE_PRIVATE);
            DishAnalysis.edit().clear().apply();
            SharedPreferences trackTake = getSharedPreferences("TrackingOfTakeAway", MODE_PRIVATE);
            trackTake.edit().clear().apply();
            SharedPreferences trackFood = getSharedPreferences("TrackingOfFoodDining", MODE_PRIVATE);
            trackFood.edit().clear().apply();
            auth.signOut();
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("765176451275-u1ati379eiinc9b21472ml968chmlsqh.apps.googleusercontent.com")
                    .requestEmail()
                    .build();
            client = GoogleSignIn.getClient(MyAccount.this, gso);
            try {

                client.revokeAccess().addOnCompleteListener(task -> {

                });
                client.signOut();
            } catch (Exception e) {
                Log.i("exception", e.getLocalizedMessage());
            }

            startActivity(new Intent(MyAccount.this, SplashScreen.class));
            finish();

            return null;
        }
    }
}