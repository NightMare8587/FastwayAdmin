package com.consumers.fastwayadmin.MenuActivities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.consumers.fastwayadmin.Dish.DishInfo;
import com.consumers.fastwayadmin.Dish.SearchDishFastway.SearchFastwayDatabase;
import com.consumers.fastwayadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class CreateDish extends AppCompatActivity {
    EditText nameOfDish,halfPlate,fullPlate;
    FirebaseAuth dishAuth;
    CheckBox checkBox;
    Bitmap bitmap;
    File file;
    DatabaseReference dish;
    boolean imageAddedOr = false;
    OutputStream outputStream;
    String mrp;
    FirebaseStorage storage;
    String state;
    StorageReference storageReference;
    Button createDish,chooseImage;
    FloatingActionButton floatingActionButton;
    String menuType;
    String name,half,full,image;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_dish);
        initialise();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            floatingActionButton.setTooltipText("Search our database");
        }

        floatingActionButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SearchFastwayDatabase.class);
            intent.putExtra("dish",menuType);
            startActivity(intent);
        });

        chooseImage.setOnClickListener(view -> {
            if(nameOfDish.getText().toString().equals(""))
                Toast.makeText(CreateDish.this, "Please enter name of dish", Toast.LENGTH_SHORT).show();
            else
            CheckPermission();
//               showDialogBox();
        });

        createDish.setOnClickListener(view -> {
            if(nameOfDish.length() == 0){
                nameOfDish.setError("Invalid Name");
                nameOfDish.requestFocus();
                return;
            }else if(fullPlate.length() == 0){
                fullPlate.setError("Invalid input");
                fullPlate.requestFocus();
                return;
            }

            name = nameOfDish.getText().toString();
            half = halfPlate.getText().toString();
            full = fullPlate.getText().toString();
            image = "";
            if(checkBox.isChecked())
                mrp = "yes";
                        else
                            mrp = "no";


                        if(imageAddedOr)
                        new Upload().execute();
                        else{
                            DishInfo info = new DishInfo(name,half,full,image,mrp,"0","0","0","yes");
                            dish.child("Restaurants").child(state).child(Objects.requireNonNull(dishAuth.getUid())).child("List of Dish").child(menuType).child(name).setValue(info);
                            Toast.makeText(CreateDish.this, "Dish Added Successfully", Toast.LENGTH_SHORT).show();
                            imageAddedOr = false;
                        }


            finish();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void CheckPermission() {
        if(ContextCompat.checkSelfPermission(CreateDish.this, Manifest.permission.CAMERA) + ContextCompat.checkSelfPermission(CreateDish.this
        ,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }else
            showDialogBox();
    }

    private void showDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image").setMessage("Select Any One Option")
                .setPositiveButton("Search Online", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
//                        String nameDish = nameOfDish.getText().toString();
//                        Intent intent = new Intent(getApplicationContext(),CustomDishImageSearch.class);
//                        intent.putExtra("name",nameDish);
//                        startActivity(intent);
                    }
                }).setNegativeButton("Take Photo", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //IMAGE CAPTURE CODE
                    startActivityForResult(intent, 20);
                }).create();
        builder.show();
    }

    private void addToDatabase(String name, String half, String full,String image,String mrp) {
        DishInfo info = new DishInfo(name,half,full,image,mrp,"0","0","0","yes");
        try {
            dish.child("Restaurants").child(state).child(Objects.requireNonNull(dishAuth.getUid())).child("List of Dish").child(menuType).child(name).setValue(info);
            StorageReference reference = storageReference.child(dishAuth.getUid() + "/" + "image" + "/"  + nameOfDish.getText().toString());
            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(@NonNull Uri uri) {
                    DatabaseReference dish = FirebaseDatabase.getInstance().getReference().getRoot();
                    dish.child("Restaurants").child(state).child(Objects.requireNonNull(dishAuth.getUid())).child("List of Dish").child(menuType).child(name).child("image").setValue(uri + "");
                }
            });
            Toast.makeText(this, "Dish Added Successfully", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this, "Something went Wrong!!!!!", Toast.LENGTH_SHORT).show();
        }finally {
            finish();
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 20 && resultCode == RESULT_OK && data != null){
            imageAddedOr = true;
             bitmap = (Bitmap) data.getExtras().get("data");
            File filepath = Environment.getExternalStorageDirectory();
            File dir = new File(filepath.getAbsolutePath());
            dir.mkdir();
            file = new File(dir, nameOfDish.getText().toString() + ".jpg");
            try {
                Log.i("file stored","yes");
                outputStream = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    private void initialise() {
        nameOfDish = findViewById(R.id.dishName);
        halfPlate = findViewById(R.id.halfPlatePrice);
        floatingActionButton = findViewById(R.id.searchOurDatabase);
        state = getIntent().getStringExtra("state");
        fullPlate = findViewById(R.id.fullPlatePrice);
        createDish = findViewById(R.id.saveDishInfo);
        dishAuth = FirebaseAuth.getInstance();
        dish = FirebaseDatabase.getInstance().getReference().getRoot();
        checkBox = findViewById(R.id.sellOnMRPprice);
        menuType = getIntent().getStringExtra("Dish");
        chooseImage = findViewById(R.id.chooseImageForFood);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    public class Upload extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            try {
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                StorageReference reference = storageReference.child(dishAuth.getUid() + "/" + "image" + "/"  + nameOfDish.getText().toString());
                reference.putFile(Uri.fromFile(file)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
                        if(sharedPreferences.getString("storeInDevice","").equals("no"))
                            file.delete();

                        Toast.makeText(CreateDish.this, "Upload Complete", Toast.LENGTH_SHORT).show();
                        addToDatabase(name,half,full,image,mrp);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }catch (Exception e){
                Toast.makeText(CreateDish.this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

            return null;
        }
    }



}