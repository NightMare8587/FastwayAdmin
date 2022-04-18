package com.consumers.fastwayadmin.Info.RestaurantDocuments;

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
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.consumers.fastwayadmin.HomeScreen.HomeScreen;
import com.consumers.fastwayadmin.R;
import com.developer.kalert.KAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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

public class ReUploadDocumentsAgain extends AppCompatActivity {
    String panUrl,adhaarUrl,gstUrl,fssaiUrl,resUrl;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    SharedPreferences sharedPreferences;
    Button panCard,adhaarCard,fssaiCard,gstCard,resProofCard;
    StorageReference storageReference;
    Uri filePath;
    FirebaseStorage storage;
    File file;
    Bitmap bitmap;
    TextView panText,gstText,adhaarText,FssaiText,resText;
    OutputStream outputStream;
    FastDialog loading;
    boolean pan = false;
    FastDialog fastDialog;
    boolean gst = false;
    boolean adhaar = false;
    boolean fssai = false;
    boolean resProof = false;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_upload_documents_again);
        initialise();
        fastDialog = new FastDialogBuilder(ReUploadDocumentsAgain.this, Type.PROGRESS)
                .progressText("Checking Database....")
                .setAnimation(Animations.FADE_IN)
                .create();

        gstText = findViewById(R.id.ReuploadGstText);
        panText = findViewById(R.id.RepanTextUploaded);
        adhaarText = findViewById(R.id.ReuploadAdhaarCardText);
        FssaiText = findViewById(R.id.ReuploadFssaiText);
        resText = findViewById(R.id.ReuploadResProofCardText);
        checkPermissions();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);


        panCard.setOnClickListener(click -> {
            if(ContextCompat.checkSelfPermission(ReUploadDocumentsAgain.this, Manifest.permission.ACCESS_FINE_LOCATION) + ContextCompat.checkSelfPermission(ReUploadDocumentsAgain.this , Manifest.permission.CAMERA)
                    + ContextCompat.checkSelfPermission(ReUploadDocumentsAgain.this, Manifest.permission.WRITE_EXTERNAL_STORAGE )
                    != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION},88);
            }else {
                AlertDialog.Builder alert = new AlertDialog.Builder(ReUploadDocumentsAgain.this);
                alert.setTitle("Choose one option")
                        .setPositiveButton("Upload from gallery", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction("android.intent.action.PICK");
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                        }).setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss()).create();

                alert.show();
            }
        });

        fssaiCard.setOnClickListener(click -> {
            if(ContextCompat.checkSelfPermission(ReUploadDocumentsAgain.this, Manifest.permission.ACCESS_FINE_LOCATION) + ContextCompat.checkSelfPermission(ReUploadDocumentsAgain.this , Manifest.permission.CAMERA)
                    + ContextCompat.checkSelfPermission(ReUploadDocumentsAgain.this, Manifest.permission.WRITE_EXTERNAL_STORAGE )
                    != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION},88);
            }else {
                AlertDialog.Builder alert = new AlertDialog.Builder(ReUploadDocumentsAgain.this);
                alert.setTitle("Choose one option")
                        .setPositiveButton("Upload from gallery", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction("android.intent.action.PICK");
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2);
                        }).setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss()).create();

                alert.show();
            }
        });

        adhaarCard.setOnClickListener(click -> {
            if(ContextCompat.checkSelfPermission(ReUploadDocumentsAgain.this, Manifest.permission.ACCESS_FINE_LOCATION) + ContextCompat.checkSelfPermission(ReUploadDocumentsAgain.this , Manifest.permission.CAMERA)
                    + ContextCompat.checkSelfPermission(ReUploadDocumentsAgain.this, Manifest.permission.WRITE_EXTERNAL_STORAGE )
                    != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION},88);
            }else {
                AlertDialog.Builder alert = new AlertDialog.Builder(ReUploadDocumentsAgain.this);
                alert.setTitle("Choose one option")
                        .setPositiveButton("Upload from gallery", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction("android.intent.action.PICK");
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 3);
                        }).setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss()).create();

                alert.show();
            }
        });

        gstCard.setOnClickListener(click -> {
            if(ContextCompat.checkSelfPermission(ReUploadDocumentsAgain.this, Manifest.permission.ACCESS_FINE_LOCATION) + ContextCompat.checkSelfPermission(ReUploadDocumentsAgain.this , Manifest.permission.CAMERA)
                    + ContextCompat.checkSelfPermission(ReUploadDocumentsAgain.this, Manifest.permission.WRITE_EXTERNAL_STORAGE )
                    != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION},88);
            }else {
                AlertDialog.Builder alert = new AlertDialog.Builder(ReUploadDocumentsAgain.this);
                alert.setTitle("Choose one option")
                        .setPositiveButton("Upload from gallery", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 4);
                        }).setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss()).create();

                alert.show();
            }
        });

        resProofCard.setOnClickListener(click -> {
            if(ContextCompat.checkSelfPermission(ReUploadDocumentsAgain.this, Manifest.permission.ACCESS_FINE_LOCATION) + ContextCompat.checkSelfPermission(ReUploadDocumentsAgain.this , Manifest.permission.CAMERA)
                    + ContextCompat.checkSelfPermission(ReUploadDocumentsAgain.this, Manifest.permission.WRITE_EXTERNAL_STORAGE )
                    != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION},88);
            }else {
                AlertDialog.Builder alert = new AlertDialog.Builder(ReUploadDocumentsAgain.this);
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 88){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(ReUploadDocumentsAgain.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }else
                Toast.makeText(ReUploadDocumentsAgain.this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions() {
        if(ContextCompat.checkSelfPermission(ReUploadDocumentsAgain.this, Manifest.permission.ACCESS_FINE_LOCATION) + ContextCompat.checkSelfPermission(ReUploadDocumentsAgain.this , Manifest.permission.CAMERA)
                + ContextCompat.checkSelfPermission(ReUploadDocumentsAgain.this, Manifest.permission.WRITE_EXTERNAL_STORAGE )
                != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION},88);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 20){
            loading = new FastDialogBuilder(ReUploadDocumentsAgain.this,Type.PROGRESS)
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
                reference.putFile(Uri.fromFile(file)).addOnCompleteListener(task -> {
                    StorageReference reference1 = storageReference.child(auth.getUid() + "/" + "Documents" + "/"  + "pan");
                    reference1.getDownloadUrl().addOnSuccessListener(uri -> {
                        pan = true;
                        Toast.makeText(ReUploadDocumentsAgain.this, "Upload Complete and image saved in phone successfully", Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                        panText.setVisibility(View.VISIBLE);
                        DatabaseReference dish = FirebaseDatabase.getInstance().getReference().getRoot();
                        panUrl = uri + "";
                        dish.child("Admin").child(Objects.requireNonNull(auth.getUid())).child("Restaurant Documents").child("pan").setValue(uri + "");
                        checkIfAllUploaded();
                    });

                }).addOnFailureListener(e -> {
                    Toast.makeText(ReUploadDocumentsAgain.this,
                            "Something went wrong", Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                });
            }catch (Exception e){
                Toast.makeText(ReUploadDocumentsAgain.this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }

        }else if(requestCode == 21){
            loading = new FastDialogBuilder(ReUploadDocumentsAgain.this,Type.PROGRESS)
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
                        reference.getDownloadUrl().addOnSuccessListener(uri -> {
                            Toast.makeText(ReUploadDocumentsAgain.this, "Upload Complete and image saved in phone successfully", Toast.LENGTH_SHORT).show();
                            loading.dismiss();
                            fssai = true;
                            FssaiText.setVisibility(View.VISIBLE);
                            fssaiUrl = uri + "";
                            DatabaseReference dish = FirebaseDatabase.getInstance().getReference().getRoot();
                            dish.child("Admin").child(Objects.requireNonNull(auth.getUid())).child("Restaurant Documents").child("fssai").setValue(uri + "");
                            checkIfAllUploaded();
                        });

                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(ReUploadDocumentsAgain.this,
                            "Something went wrong", Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                });
            }catch (Exception e){
                Toast.makeText(ReUploadDocumentsAgain.this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }

        }else if(requestCode == 22){
            loading = new FastDialogBuilder(ReUploadDocumentsAgain.this,Type.PROGRESS)
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
                                Toast.makeText(ReUploadDocumentsAgain.this, "Upload Complete and image saved in phone successfully", Toast.LENGTH_SHORT).show();
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
                }).addOnFailureListener(e -> {
                    Toast.makeText(ReUploadDocumentsAgain.this,
                            "Something went wrong", Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                });
            }catch (Exception e){
                Toast.makeText(ReUploadDocumentsAgain.this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }

        }else if(requestCode == 23){
            loading = new FastDialogBuilder(ReUploadDocumentsAgain.this,Type.PROGRESS)
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
                reference.putFile(Uri.fromFile(file)).addOnCompleteListener(task -> {
                    StorageReference reference12 = storageReference.child(auth.getUid() + "/" + "Documents" + "/"  + "gst");
                    reference12.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(@NonNull Uri uri) {
                            Toast.makeText(ReUploadDocumentsAgain.this, "Upload Complete and image saved in phone successfully", Toast.LENGTH_SHORT).show();
                            loading.dismiss();
                            gst = true;
                            gstText.setVisibility(View.VISIBLE);
                            gstUrl = uri + "";
                            DatabaseReference dish = FirebaseDatabase.getInstance().getReference().getRoot();
                            dish.child("Admin").child(Objects.requireNonNull(auth.getUid())).child("Restaurant Documents").child("gst").setValue(uri + "");
                            checkIfAllUploaded();
                        }
                    });

                }).addOnFailureListener(e -> {
                    Toast.makeText(ReUploadDocumentsAgain.this,
                            "Something went wrong", Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                });
            }catch (Exception e){
                Toast.makeText(ReUploadDocumentsAgain.this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }

        }else if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            loading = new FastDialogBuilder(ReUploadDocumentsAgain.this,Type.PROGRESS)
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
            loading = new FastDialogBuilder(ReUploadDocumentsAgain.this,Type.PROGRESS)
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
            loading = new FastDialogBuilder(ReUploadDocumentsAgain.this,Type.PROGRESS)
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
            loading = new FastDialogBuilder(ReUploadDocumentsAgain.this,Type.PROGRESS)
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
            loading = new FastDialogBuilder(ReUploadDocumentsAgain.this,Type.PROGRESS)
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
                    if(value.equals("pan")) {
                        panUrl = uri + "";
                        pan = true;
                        panText.setVisibility(View.VISIBLE);
                    }
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
                    if(value.equals("gst")) {
                        gst = true;
                        gstUrl = uri + "";
                        gstText.setVisibility(View.VISIBLE);
                    }
                    if(value.equals("resProof")){
                        resProof = true;
                        resUrl = uri + "";
                        resText.setVisibility(View.VISIBLE);
                    }
                    Toast.makeText(ReUploadDocumentsAgain.this, "Upload Complete and image saved in phone successfully", Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                    DatabaseReference dish = FirebaseDatabase.getInstance().getReference().getRoot();
                    dish.child("Admin").child(Objects.requireNonNull(auth.getUid())).child("Restaurant Documents").child(value).setValue(uri + "");
                    checkIfAllUploaded();
                });
            }).addOnFailureListener(e -> loading.dismiss());
        }else
            loading.dismiss();
    }

    private void checkIfAllUploaded() {
        if(adhaar && pan && gst && fssai && resProof){
            ResDocuments resDocuments = new ResDocuments(panUrl,adhaarUrl,fssaiUrl,gstUrl,resUrl);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("Restaurant Registration");
            databaseReference.child(Objects.requireNonNull(auth.getUid())).setValue(resDocuments);
            SharedPreferences sharedPreferences = getSharedPreferences("RestaurantInfo",MODE_PRIVATE);
            databaseReference.child(auth.getUid()).child("ResName").setValue(sharedPreferences.getString("hotelName",""));
            databaseReference.child(auth.getUid()).child("ResAddress").setValue(sharedPreferences.getString("hotelAddress",""));
            databaseReference.child(auth.getUid()).child("ResNumber").setValue(sharedPreferences.getString("hotelNumber",""));
            databaseReference.child(auth.getUid()).child("state").setValue(this.sharedPreferences.getString("state",""));

            databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid()).child("Restaurant Documents");
            databaseReference.child("verified").setValue("no");
            databaseReference.child("bankVerified").setValue("no");
            databaseReference.child("reasonForCancel").removeValue();
            new KAlertDialog(ReUploadDocumentsAgain.this,KAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Documents Re-Uploaded")
                    .setContentText("Documents Re-Uploaded and will be verified by our fastway staff with an on-site verification")
                    .setConfirmText("Exit")
                    .setConfirmClickListener(click -> {
                        click.dismissWithAnimation();
                        startActivity(new Intent(getApplicationContext(), HomeScreen.class));
                        finish();
                    }).show();
        }
    }

    private void initialise() {
        panCard = findViewById(R.id.ReuploadPANcard);
        adhaarCard = findViewById(R.id.ReuploadAdhaarCard);
        fssaiCard = findViewById(R.id.ReuploadFSSAIcard);
        gstCard= findViewById(R.id.ReuploadGSTcard);
        resProofCard= findViewById(R.id.ReuploadResProofCard);
    }
}