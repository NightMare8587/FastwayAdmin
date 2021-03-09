package com.example.fastwayadmin.MenuActivities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fastwayadmin.Dish.DishInfo;
import com.example.fastwayadmin.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class CreateDish extends AppCompatActivity {
    EditText nameOfDish,halfPlate,fullPlate;
    FirebaseAuth dishAuth;
    DatabaseReference dish;
    StorageReference storageReference;
    Button createDish,chooseImage;
    FloatingActionButton floatingActionButton;
    String menuType;
    String name,half,full;
    Uri imageUri = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_dish);
        initialise();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            floatingActionButton.setTooltipText("Search our database");
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),SearchYourDish.class));
            }
        });

        chooseImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                CheckPermission();
//               showDialogBox();
            }
        });

        createDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                addToDatabase(name,half,full);
            }
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
                        String nameDish = nameOfDish.getText().toString();
                        Intent intent = new Intent(getApplicationContext(),CustomDishImageSearch.class);
                        intent.putExtra("name",nameDish);
                        startActivity(intent);
                    }
                }).setNegativeButton("Take Photo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //IMAGE CAPTURE CODE
                startActivityForResult(intent, 0);
            }
        }).create();
        builder.show();
    }

    private void addToDatabase(String name, String half, String full) {
        DishInfo info = new DishInfo(name,half,full);
        try {
            dish.child("Restaurants").child(Objects.requireNonNull(dishAuth.getUid())).child("List of Dish").child(menuType).child(name).setValue(info);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK) {
            Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
            imageUri = data.getData();
            startPostingImage(imageUri);
        }
    }

    private void startPostingImage(Uri imageUri) {
        storageReference.child(dishAuth.getUid());
        storageReference.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(CreateDish.this, "Uploaded", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void initialise() {
        nameOfDish = findViewById(R.id.dishName);
        halfPlate = findViewById(R.id.halfPlatePrice);
        floatingActionButton = findViewById(R.id.searchOurDatabase);
        fullPlate = findViewById(R.id.fullPlatePrice);
        createDish = findViewById(R.id.saveDishInfo);
        dishAuth = FirebaseAuth.getInstance();
        dish = FirebaseDatabase.getInstance().getReference().getRoot();
        menuType = getIntent().getStringExtra("Dish");
        chooseImage = findViewById(R.id.chooseImageForFood);
        storageReference = FirebaseStorage.getInstance().getReference();
    }
}