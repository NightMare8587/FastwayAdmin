package com.consumers.fastwayadmin.Info.RestaurantDocuments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.consumers.fastwayadmin.Info.MapsActivity;
import com.consumers.fastwayadmin.R;
import com.developer.kalert.KAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.collections4.map.ReferenceMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import karpuzoglu.enes.com.fastdialog.Animations;
import karpuzoglu.enes.com.fastdialog.FastDialog;
import karpuzoglu.enes.com.fastdialog.FastDialogBuilder;
import karpuzoglu.enes.com.fastdialog.Type;

public class UploadRemainingDocs extends AppCompatActivity {
    DatabaseReference databaseReference;
    String panUrl,adhaarUrl,gstUrl,fssaiUrl,residentialProofSubmitUrl;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    SharedPreferences sharedPreferences;
    Button panCard,adhaarCard,fssaiCard,gstCard,residentialProof;
    StorageReference storageReference;
    Uri filePath;
    FirebaseStorage storage;
    String fssaiDigitNums = "";
    File file;
    Bitmap bitmap;
    TextView panText,gstText,adhaarText,FssaiText,resProofText;
    OutputStream outputStream;
    FastDialog loading;
    boolean pan = false;
    FastDialog fastDialog;
    boolean gst = false;
    boolean adhaar = false;
    boolean fssai = false;
    Button proceedFurther;
    boolean resProof = false;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_remaining_docs);
        initialise();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        fastDialog = new FastDialogBuilder(UploadRemainingDocs.this, Type.PROGRESS)
                .progressText("Checking Database....")
                .setAnimation(Animations.FADE_IN)
                .create();

//        gstText = findViewById(R.id.uploadGstTextRemaining);
//        panText = findViewById(R.id.panTextUploadedRemaining);
        adhaarText = findViewById(R.id.uploadAdhaarCardTextRemaining);
        FssaiText = findViewById(R.id.uploadFssaiTextRemaining);
        resProofText = findViewById(R.id.uploadResidentialTextRemaining);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("Restaurant Documents")){
                    fastDialog.show();
                    if(snapshot.child("Restaurant Documents").hasChild("fssai")){
                        fssai = true;
                        fssaiUrl = snapshot.child("Restaurant Documents").child("fssai").getValue(String.class);
                        fssaiDigitNums = snapshot.child("Restaurant Documents").child("fssaiDigits").getValue(String.class);
                        FssaiText.setVisibility(View.VISIBLE);
                        fssaiCard.setClickable(false);
                    }
                    if(snapshot.child("Restaurant Documents").hasChild("adhaar")){
                        adhaar = true;
                        adhaarText.setVisibility(View.VISIBLE);
                        adhaarUrl = snapshot.child("Restaurant Documents").child("adhaar").getValue(String.class);
                        adhaarCard.setClickable(false);
                    }
                    if(snapshot.child("Restaurant Documents").hasChild("resProof")){
                        resProof = true;
                        resProofText.setVisibility(View.VISIBLE);
                        residentialProofSubmitUrl = snapshot.child("Restaurant Documents").child("resProof").getValue(String.class);
                        residentialProof.setClickable(false);
                    }

                    checkIfAllUploaded();
                    fastDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

//        panCard.setOnClickListener(click -> {
//            if(ContextCompat.checkSelfPermission(UploadRemainingDocs.this, Manifest.permission.ACCESS_FINE_LOCATION) + ContextCompat.checkSelfPermission(UploadRemainingDocs.this , Manifest.permission.CAMERA)
//                    + ContextCompat.checkSelfPermission(UploadRemainingDocs.this, Manifest.permission.WRITE_EXTERNAL_STORAGE )
//                    != PackageManager.PERMISSION_GRANTED){
////            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
//                requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION},88);
//            }else {
//                AlertDialog.Builder alert = new AlertDialog.Builder(UploadRemainingDocs.this);
//                alert.setTitle("Choose one option")
//                        .setPositiveButton("Upload from gallery", (dialogInterface, i) -> {
//                            dialogInterface.dismiss();
//                            Intent intent = new Intent();
//                            intent.setType("image/*");
//                            intent.setAction("android.intent.action.PICK");
//                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
//                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                            }
//                        }).create();
//
//                alert.show();
//            }
//        });

        fssaiCard.setOnClickListener(click -> {
            if(ContextCompat.checkSelfPermission(UploadRemainingDocs.this, Manifest.permission.ACCESS_FINE_LOCATION) + ContextCompat.checkSelfPermission(UploadRemainingDocs.this , Manifest.permission.CAMERA)
                    + ContextCompat.checkSelfPermission(UploadRemainingDocs.this, Manifest.permission.WRITE_EXTERNAL_STORAGE )
                    != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION},88);
            }else {
                AlertDialog.Builder fssaiBuild = new AlertDialog.Builder(UploadRemainingDocs.this);
                fssaiBuild.setTitle("Fssai Number").setMessage("Enter your 14 digit FSSAI number below");
                LinearLayout linearLayout = new LinearLayout(UploadRemainingDocs.this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                EditText editText = new EditText(UploadRemainingDocs.this);
                editText.setHint("Enter FSSAI Number here");
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setMaxLines(14);
                linearLayout.addView(editText);
                fssaiBuild.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(editText.length() == 14 && TextUtils.isDigitsOnly(editText.getText().toString())) {
                            fssaiDigitNums = editText.getText().toString();
                            AlertDialog.Builder alert = new AlertDialog.Builder(UploadRemainingDocs.this);
                            alert.setTitle("Choose one option")
                                    .setPositiveButton("Upload from gallery", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            Intent intent = new Intent();
                                            intent.setType("image/*");
                                            intent.setAction("android.intent.action.PICK");
                                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2);
                                        }
                                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    }).create();

                            alert.show();
                        }else
                            Toast.makeText(UploadRemainingDocs.this, "Check your input", Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
                fssaiBuild.setView(linearLayout);
                fssaiBuild.show();

            }
        });

        adhaarCard.setOnClickListener(click -> {
            if(ContextCompat.checkSelfPermission(UploadRemainingDocs.this, Manifest.permission.ACCESS_FINE_LOCATION) + ContextCompat.checkSelfPermission(UploadRemainingDocs.this , Manifest.permission.CAMERA)
                    + ContextCompat.checkSelfPermission(UploadRemainingDocs.this, Manifest.permission.WRITE_EXTERNAL_STORAGE )
                    != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION},88);
            }else {
                AlertDialog.Builder alert = new AlertDialog.Builder(UploadRemainingDocs.this);
                alert.setTitle("Choose one option")
                        .setPositiveButton("Upload from gallery", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction("android.intent.action.PICK");
                                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 3);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create();

                alert.show();
            }
        });

        residentialProof.setOnClickListener(click -> {
            if(ContextCompat.checkSelfPermission(UploadRemainingDocs.this, Manifest.permission.ACCESS_FINE_LOCATION) + ContextCompat.checkSelfPermission(UploadRemainingDocs.this , Manifest.permission.CAMERA)
                    + ContextCompat.checkSelfPermission(UploadRemainingDocs.this, Manifest.permission.WRITE_EXTERNAL_STORAGE )
                    != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION},88);
            }else {
                AlertDialog.Builder alert = new AlertDialog.Builder(UploadRemainingDocs.this);
                alert.setTitle("Choose one option").setMessage("Residential Proof includes: Electricity Bill, Water Bill, Telephone Bill or Any other bill which proves the restaurant residential address")
                        .setPositiveButton("Upload from gallery", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction("android.intent.action.PICK");
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 5);
                        }).setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss()).create();

                alert.show();
            }
        });

//        gstCard.setOnClickListener(click -> {
//            if(ContextCompat.checkSelfPermission(UploadRemainingDocs.this, Manifest.permission.ACCESS_FINE_LOCATION) + ContextCompat.checkSelfPermission(UploadRemainingDocs.this , Manifest.permission.CAMERA)
//                    + ContextCompat.checkSelfPermission(UploadRemainingDocs.this, Manifest.permission.WRITE_EXTERNAL_STORAGE )
//                    != PackageManager.PERMISSION_GRANTED){
////            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
//                requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION},88);
//            }else {
//                AlertDialog.Builder alert = new AlertDialog.Builder(UploadRemainingDocs.this);
//                alert.setTitle("Choose one option")
//                        .setPositiveButton("Upload from gallery", (dialogInterface, i) -> {
//                            dialogInterface.dismiss();
//                            Intent intent = new Intent();
//                            intent.setType("image/*");
//                            intent.setAction("android.intent.action.PICK");
//                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 4);
//                        }).setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss()).create();
//
//                alert.show();
//            }
//        });
    }
    private void initialise() {
//        panCard = findViewById(R.id.uploadPANcardRemaining);
        adhaarCard = findViewById(R.id.uploadAdhaarCardRemaining);
        fssaiCard = findViewById(R.id.uploadFSSAIcardRemaining);
//        gstCard= findViewById(R.id.uploadGSTcardRemaining);
        residentialProof = findViewById(R.id.uploadResidentialProofRemaining);
        panUrl = "";
        adhaarUrl = "";
        gstUrl = "";
        residentialProofSubmitUrl = "";
        fssaiUrl = "";
    }
    private void checkIfAllUploaded() {
        if(adhaar && fssai && resProof){
            DatabaseReference databaseReferenceCheck = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("Registered Restaurants").child(sharedPreferences.getString("state",""));
            databaseReferenceCheck.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild(Objects.requireNonNull(auth.getUid()))){
                        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid()).child("Restaurant Documents");
                        databaseReference.child("verified").setValue("yes");
                        databaseReference.child("bankVerified").setValue("yes");
                        databaseReference.child("timeToUploadDocs").removeValue();
                        Toast.makeText(UploadRemainingDocs.this, "All Docs Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        },600);
                    }else{
                        ResDocuments resDocuments = new ResDocuments(panUrl,adhaarUrl,fssaiUrl,gstUrl,residentialProofSubmitUrl);
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("Restaurant Registration");
                        databaseReference.child(Objects.requireNonNull(auth.getUid())).setValue(resDocuments);
                        SharedPreferences sharedPreferences = getSharedPreferences("RestaurantInfo",MODE_PRIVATE);
                        databaseReference.child(auth.getUid()).child("ResName").setValue(sharedPreferences.getString("hotelName",""));
                        databaseReference.child(auth.getUid()).child("ResAddress").setValue(sharedPreferences.getString("hotelAddress",""));
                        databaseReference.child(auth.getUid()).child("ResNumber").setValue(sharedPreferences.getString("hotelNumber",""));
                        SharedPreferences loginShared = getSharedPreferences("loginInfo",MODE_PRIVATE);
                        databaseReference.child(auth.getUid()).child("state").setValue(loginShared.getString("state",""));
                        databaseReference.child(auth.getUid()).child("locality").setValue(loginShared.getString("locality",""));

                        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid()).child("Restaurant Documents");
                        databaseReference.child("verified").setValue("no");
                        databaseReference.child("bankVerified").setValue("no");
                        new KAlertDialog(UploadRemainingDocs.this,KAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Documents Uploaded")
                                .setContentText("Documents uploaded and will be verified by our fastway staff with an on-site verification")
                                .setConfirmText("Exit")
                                .setConfirmClickListener(click -> {
                                    click.dismissWithAnimation();
                                    startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                                }).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("documentsUploadedAll","yes");
            editor.apply();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 20){
            loading = new FastDialogBuilder(UploadRemainingDocs.this,Type.PROGRESS)
                    .setAnimation(Animations.SLIDE_BOTTOM)
                    .progressText("Uploading Image.....")
                    .create();

            loading.show();

            bitmap = (Bitmap) data.getExtras().get("data");
            File filepath = Environment.getExternalStorageDirectory();
            File dir = new File(filepath.getAbsolutePath());
            dir.mkdir();

            file = new File(dir, "pan" + ".jpg");
            try {
                Log.i("file stored","yes");
                outputStream = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            try {
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
                loading.dismiss();
            }
            try {
                StorageReference reference = storageReference.child(auth.getUid() + "/" + "Documents" + "/"  + "pan");
                reference.putFile(Uri.fromFile(file)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        StorageReference reference = storageReference.child(auth.getUid() + "/" + "Documents" + "/"  + "pan");
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(@NonNull Uri uri) {
                                pan = true;
                                Toast.makeText(UploadRemainingDocs.this, "Upload Complete and image saved in phone successfully", Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                                panText.setVisibility(View.VISIBLE);
                                DatabaseReference dish = FirebaseDatabase.getInstance().getReference().getRoot();
                                panUrl = uri + "";
                                dish.child("Admin").child(Objects.requireNonNull(auth.getUid())).child("Restaurant Documents").child("pan").setValue(uri + "");
                                checkIfAllUploaded();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UploadRemainingDocs.this,
                                "Something went wrong", Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                    }
                });
            }catch (Exception e){
                Toast.makeText(UploadRemainingDocs.this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }

        }else if(requestCode == 21){
            loading = new FastDialogBuilder(UploadRemainingDocs.this,Type.PROGRESS)
                    .setAnimation(Animations.SLIDE_BOTTOM)
                    .progressText("Uploading Image.....")
                    .create();

            loading.show();

            bitmap = (Bitmap) data.getExtras().get("data");
            File filepath = Environment.getExternalStorageDirectory();
            File dir = new File(filepath.getAbsolutePath());
            dir.mkdir();

            file = new File(dir, "fssai" + ".jpg");
            try {
                Log.i("file stored","yes");
                outputStream = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            try {
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
                loading.dismiss();
            }
            try {
                StorageReference reference = storageReference.child(auth.getUid() + "/" + "Documents" + "/"  + "fssai");
                reference.putFile(Uri.fromFile(file)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        StorageReference reference = storageReference.child(auth.getUid() + "/" + "Documents" + "/"  + "fssai");
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(@NonNull Uri uri) {
                                Toast.makeText(UploadRemainingDocs.this, "Upload Complete and image saved in phone successfully", Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                                fssai = true;
                                FssaiText.setVisibility(View.VISIBLE);
                                fssaiUrl = uri + "";
                                DatabaseReference dish = FirebaseDatabase.getInstance().getReference().getRoot();
                                dish.child("Admin").child(Objects.requireNonNull(auth.getUid())).child("Restaurant Documents").child("fssai").setValue(uri + "");
                                dish.child("Admin").child(Objects.requireNonNull(auth.getUid())).child("Restaurant Documents").child("fssaiDigits").setValue(fssaiDigitNums + "");
                                checkIfAllUploaded();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UploadRemainingDocs.this,
                                "Something went wrong", Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                    }
                });
            }catch (Exception e){
                Toast.makeText(UploadRemainingDocs.this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }

        }else if(requestCode == 22){
            loading = new FastDialogBuilder(UploadRemainingDocs.this,Type.PROGRESS)
                    .setAnimation(Animations.SLIDE_BOTTOM)
                    .progressText("Uploading Image.....")
                    .create();

            loading.show();

            bitmap = (Bitmap) data.getExtras().get("data");
            File filepath = Environment.getExternalStorageDirectory();
            File dir = new File(filepath.getAbsolutePath());
            dir.mkdir();

            file = new File(dir, "adhaar" + ".jpg");
            try {
                Log.i("file stored","yes");
                outputStream = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            try {
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
                loading.dismiss();
            }
            try {
                StorageReference reference = storageReference.child(auth.getUid() + "/" + "Documents" + "/"  + "adhaar");
                reference.putFile(Uri.fromFile(file)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        StorageReference reference = storageReference.child(auth.getUid() + "/" + "Documents" + "/"  + "adhaar");
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(@NonNull Uri uri) {
                                Toast.makeText(UploadRemainingDocs.this, "Upload Complete and image saved in phone successfully", Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                                adhaar = true;
                                adhaarUrl = uri + "";
                                adhaarText.setVisibility(View.VISIBLE);
                                DatabaseReference dish = FirebaseDatabase.getInstance().getReference().getRoot();
                                dish.child("Admin").child(Objects.requireNonNull(auth.getUid())).child("Restaurant Documents").child("adhaar").setValue(uri + "");
                                checkIfAllUploaded();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UploadRemainingDocs.this,
                                "Something went wrong", Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                    }
                });
            }catch (Exception e){
                Toast.makeText(UploadRemainingDocs.this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }

        }else if(requestCode == 23){
            loading = new FastDialogBuilder(UploadRemainingDocs.this,Type.PROGRESS)
                    .setAnimation(Animations.SLIDE_BOTTOM)
                    .progressText("Uploading Image.....")
                    .create();

            loading.show();

            bitmap = (Bitmap) data.getExtras().get("data");
            File filepath = Environment.getExternalStorageDirectory();
            File dir = new File(filepath.getAbsolutePath());
            dir.mkdir();

            file = new File(dir, "gst" + ".jpg");
            try {
                Log.i("file stored","yes");
                outputStream = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            try {
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
                loading.dismiss();
            }
            try {
                StorageReference reference = storageReference.child(auth.getUid() + "/" + "Documents" + "/"  + "gst");
                reference.putFile(Uri.fromFile(file)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        StorageReference reference = storageReference.child(auth.getUid() + "/" + "Documents" + "/"  + "gst");
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(@NonNull Uri uri) {
                                Toast.makeText(UploadRemainingDocs.this, "Upload Complete and image saved in phone successfully", Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                                gst = true;
                                gstText.setVisibility(View.VISIBLE);
                                gstUrl = uri + "";
                                DatabaseReference dish = FirebaseDatabase.getInstance().getReference().getRoot();
                                dish.child("Admin").child(Objects.requireNonNull(auth.getUid())).child("Restaurant Documents").child("gst").setValue(uri + "");
                                checkIfAllUploaded();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UploadRemainingDocs.this,
                                "Something went wrong", Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                    }
                });
            }catch (Exception e){
                Toast.makeText(UploadRemainingDocs.this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }

        }else if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            loading = new FastDialogBuilder(UploadRemainingDocs.this,Type.PROGRESS)
                    .setAnimation(Animations.SLIDE_BOTTOM)
                    .progressText("Uploading Image.....")
                    .create();

            loading.show();
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                uploadImage("pan");
            } catch (IOException e) {
                e.printStackTrace();
                loading.dismiss();
            }
        }else if(requestCode == 2 && resultCode == RESULT_OK && data != null){
            loading = new FastDialogBuilder(UploadRemainingDocs.this,Type.PROGRESS)
                    .setAnimation(Animations.SLIDE_BOTTOM)
                    .progressText("Uploading Image.....")
                    .create();

            loading.show();
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                uploadImage("fssai");
            } catch (IOException e) {
                e.printStackTrace();
                loading.dismiss();
            }
        }else if(requestCode == 3 && resultCode == RESULT_OK && data != null){
            loading = new FastDialogBuilder(UploadRemainingDocs.this,Type.PROGRESS)
                    .setAnimation(Animations.SLIDE_BOTTOM)
                    .progressText("Uploading Image.....")
                    .create();

            loading.show();
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                uploadImage("adhaar");
            } catch (IOException e) {
                e.printStackTrace();
                loading.dismiss();
            }
        }else if(requestCode == 4 && resultCode == RESULT_OK && data != null){
            loading = new FastDialogBuilder(UploadRemainingDocs.this,Type.PROGRESS)
                    .setAnimation(Animations.SLIDE_BOTTOM)
                    .progressText("Uploading Image.....")
                    .create();

            loading.show();
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                uploadImage("gst");
            } catch (IOException e) {
                e.printStackTrace();
                loading.dismiss();
            }
        }else if(requestCode == 5 && resultCode == RESULT_OK && data != null){
            loading = new FastDialogBuilder(UploadRemainingDocs.this,Type.PROGRESS)
                    .setAnimation(Animations.SLIDE_BOTTOM)
                    .progressText("Uploading Image.....")
                    .create();

            loading.show();
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                uploadImage("resProof");
            } catch (IOException e) {
                e.printStackTrace();
                loading.dismiss();
            }
        }
    }
    private void uploadImage(String value) {
        if(filePath != null){
            StorageReference reference = storageReference.child(auth.getUid() + "/" + "Documents" + "/"  + value);
            reference.putFile(filePath).addOnSuccessListener(taskSnapshot -> {
                StorageReference reference1 = storageReference.child(auth.getUid() + "/" + "Documents" + "/"  + value);
                reference1.getDownloadUrl().addOnSuccessListener(uri -> {

                    if(value.equals("fssai")) {
                        fssai = true;
                        fssaiUrl = uri + "";
                        FssaiText.setVisibility(View.VISIBLE);
                    }
                    if(value.equals("adhaar")) {
                        adhaar = true;
                        adhaarUrl = uri + "";
                        adhaarText.setVisibility(View.VISIBLE);
                    }

                    if(value.equals("resProof")){
                        resProof = true;
                        residentialProofSubmitUrl = uri + "";
                        resProofText.setVisibility(View.VISIBLE);
                    }
                    Toast.makeText(UploadRemainingDocs.this, "Upload Complete and image saved in phone successfully", Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                    DatabaseReference dish = FirebaseDatabase.getInstance().getReference().getRoot();
                    dish.child("Admin").child(Objects.requireNonNull(auth.getUid())).child("Restaurant Documents").child(value).setValue(uri + "");
                    checkIfAllUploaded();
                });
            }).addOnFailureListener(e -> loading.dismiss());
        }else
            loading.dismiss();
    }
}