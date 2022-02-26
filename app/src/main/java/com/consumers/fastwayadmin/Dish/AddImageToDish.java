package com.consumers.fastwayadmin.Dish;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.consumers.fastwayadmin.MenuActivities.CustomDishImageSearch;
import com.consumers.fastwayadmin.R;
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

public class AddImageToDish extends AppCompatActivity {
    String dishName;
    OutputStream outputStream;
    DatabaseReference reference;
    FirebaseStorage storage;
    File outputFile;
    String type;
    FirebaseAuth dishAuth = FirebaseAuth.getInstance();
    StorageReference storageReference;
    FastDialog fastDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image_to_dish);
        type = getIntent().getStringExtra("type");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        StrictMode.VmPolicy.Builder builderaaa = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builderaaa.build());
        dishName = getIntent().getStringExtra("dishName");
        fastDialog = new FastDialogBuilder(AddImageToDish.this, Type.PROGRESS)
                .progressText("Uploading Image please wait")
                .setAnimation(Animations.FADE_IN)
                .create();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image").setMessage("Select Any One Option")
                .setPositiveButton("Upload from device", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        String nameDish = dishName;
//                        Intent intent = new Intent(getApplicationContext(), CustomDishImageSearch.class);
//                        intent.putExtra("name",nameDish);
//                        startActivity(intent);
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        }).create();
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                showDialogBox();
            }else{
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image").setMessage("Select Any One Option")
                .setPositiveButton("Upload From Device", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        }).create();
        builder.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 20 && resultCode == RESULT_OK && data != null){
            fastDialog.show();
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            File filepath = Environment.getExternalStorageDirectory();
            File dir = new File(filepath.getAbsolutePath());
            dir.mkdir();
            File file = new File(dir, dishName + ".jpg");
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
            }
            try {
                StorageReference reference = storageReference.child(dishAuth.getUid() + "/" + "image" + "/"  + dishName);
                reference.putFile(Uri.fromFile(file)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        Toast.makeText(AddImageToDish.this, "Upload Complete", Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
                        if(sharedPreferences.getString("storeInDevice","").equals("no")){
                            file.delete();
                        }
                        new UploadInBackground().execute();
                        fastDialog.dismiss();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        fastDialog.dismiss();
                    }
                });
            }catch (Exception e){
                Toast.makeText(this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                fastDialog.dismiss();
            }
        }else if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            fastDialog.show();
            Uri filepath = data.getData();
            StorageReference ref = storageReference.child(Objects.requireNonNull(dishAuth.getUid()) + "/" + "image" + "/"  + dishName);
            ref.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
//                    Toast.makeText(AddImageToDish.this, "File Uploaded", Toast.LENGTH_SHORT).show();
                    SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
                    reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(Objects.requireNonNull(dishAuth.getUid()))
                    .child("List of Dish").child(type).child(dishName);

            StorageReference ref = storageReference.child(dishAuth.getUid() + "/" + "image" + "/"  + dishName);
            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(@NonNull Uri uri) {
                    Toast.makeText(AddImageToDish.this, "New Image Uploaded", Toast.LENGTH_SHORT).show();
                    reference.child("image").setValue(uri + "");
                    fastDialog.dismiss();
                    finish();
                }
            });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
            fastDialog.dismiss();
                }
            });

//            SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
//            reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(Objects.requireNonNull(dishAuth.getUid()))
//                    .child("List of Dish").child(type).child(dishName);
//
//            StorageReference ref = storageReference.child(dishAuth.getUid() + "/" + "image" + "/"  + dishName);
//            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                @Override
//                public void onSuccess(@NonNull Uri uri) {
//                    reference.child("image").setValue(uri + "");
//                }
//            });
        }
    }

    public class UploadInBackground extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
            reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(Objects.requireNonNull(dishAuth.getUid()))
                    .child("List of Dish").child(type).child(dishName);

            StorageReference ref = storageReference.child(dishAuth.getUid() + "/" + "image" + "/"  + dishName);
            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(@NonNull Uri uri) {
                    reference.child("image").setValue(uri + "");
                }
            });
            return null;
        }
    }
}