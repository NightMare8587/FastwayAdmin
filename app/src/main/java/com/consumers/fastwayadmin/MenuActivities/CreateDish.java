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
import android.os.StrictMode;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.consumers.fastwayadmin.Dish.AddImageToDish;
import com.consumers.fastwayadmin.Dish.DishInfo;
import com.consumers.fastwayadmin.Dish.SearchDishFastway.SearchFastwayDatabase;
import com.consumers.fastwayadmin.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class CreateDish extends AppCompatActivity {
    EditText nameOfDish,halfPlate,fullPlate;
    FirebaseAuth dishAuth;
    String descriptionToSubmit;
    CheckBox checkBox;
    Uri filePath;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    DatabaseReference dish;
    String dishType = "Veg";
    String locality;
    RadioGroup radioGroup;
    boolean imageAddedOr = false;

    String mrp;
    FirebaseStorage storage;
    File outputFile;
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
        CheckPermission();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            floatingActionButton.setTooltipText("Search our database");
        }

        radioGroup = findViewById(R.id.disgTypeRadioGroup);
        floatingActionButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SearchFastwayDatabase.class);
            intent.putExtra("dish",menuType);
            startActivity(intent);
        });

        radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if(i == R.id.nonVegRadioButton)
                dishType = "NonVeg";
            else if(i == R.id.vegRadioButton)
                dishType = "Veg";
            else
                dishType = "Vegan";
        });

        chooseImage.setOnClickListener(view -> {
            if(nameOfDish.getText().toString().equals(""))
                Toast.makeText(CreateDish.this, "Please enter name of dish", Toast.LENGTH_SHORT).show();
            else {

                showDialogBox();
            }
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

                        if(imageAddedOr) {
                            AlertDialog.Builder myAlert = new AlertDialog.Builder(CreateDish.this);
                            LinearLayout linearLayout = new LinearLayout(CreateDish.this);
                            EditText editText = new EditText(CreateDish.this);
                            editText.setHint("Enter Description Here");
                            linearLayout.setOrientation(LinearLayout.VERTICAL);
                            myAlert.setTitle("Description").setMessage("Do you wanna add some description to your dish like how much quantity will be provided and all!!\n")
                                    .setPositiveButton("Submit Description", (dialogInterface, i) -> {
                                        if(editText.getText().toString().equals("")){
                                            Toast.makeText(CreateDish.this, "Field can't be empty", Toast.LENGTH_SHORT).show();
                                        }else {
                                            descriptionToSubmit = editText.getText().toString();
                                            new Upload().execute();
                                            finish();
                                        }
                                    }).setNegativeButton("Skip", (dialogInterface, i) -> {
//                                        DishInfo info = new DishInfo(name,half,full,image,mrp,"0","0","0","yes","",dishType,menuType);
//                                        dish.child("Restaurants").child(state).child(locality).child(Objects.requireNonNull(dishAuth.getUid())).child("List of Dish").child(menuType).child(name).setValue(info);
//                                        firestore.collection(state).document("Restaurants").collection(locality).document(Objects.requireNonNull(dishAuth.getUid())).collection("List of Dish").document(name).set(info);
//                                        Toast.makeText(CreateDish.this, "Dish Added Successfully", Toast.LENGTH_SHORT).show();
//                                        imageAddedOr = false;
                                descriptionToSubmit = "";
                                        new Upload().execute();
                                        finish();
                                    }).create();
                            myAlert.setCancelable(false);
                            linearLayout.addView(editText);
                            myAlert.setView(linearLayout);
                            myAlert.show();

                        }
                        else{
                            AlertDialog.Builder myAlert = new AlertDialog.Builder(CreateDish.this);
                            LinearLayout linearLayout = new LinearLayout(CreateDish.this);
                            EditText editText = new EditText(CreateDish.this);
                            editText.setHint("Enter Description Here");
                            linearLayout.setOrientation(LinearLayout.VERTICAL);
                            myAlert.setTitle("Description").setMessage("Do you wanna add some description to your dish like how much quantity will be provided and all!!\n")
                                    .setPositiveButton("Submit Description", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if(editText.getText().toString().equals("")){
                                                Toast.makeText(CreateDish.this, "Field can't be empty", Toast.LENGTH_SHORT).show();
                                            }else{
                                                DishInfo info = new DishInfo(name,half,full,image,mrp,"0","0","0","yes",editText.getText().toString(),dishType,menuType);

                                                dish.child("Restaurants").child(state).child(locality).child(Objects.requireNonNull(dishAuth.getUid())).child("List of Dish").child(menuType).child(name).setValue(info);
                                                firestore.collection(state).document("Restaurants").collection(locality).document(dishAuth.getUid()).collection("List of Dish").document(name).set(info);
                                                Toast.makeText(CreateDish.this, "Dish Added Successfully", Toast.LENGTH_SHORT).show();
                                                imageAddedOr = false;
                                                finish();
                                            }
                                        }
                                    }).setNegativeButton("Skip", (dialogInterface, i) -> {
                                        DishInfo info = new DishInfo(name,half,full,image,mrp,"0","0","0","yes","",dishType,menuType);
                                        dish.child("Restaurants").child(state).child(locality).child(Objects.requireNonNull(dishAuth.getUid())).child("List of Dish").child(menuType).child(name).setValue(info);
                                        firestore.collection(state).document("Restaurants").collection(locality).document(dishAuth.getUid()).collection("List of Dish").document(name).set(info);
                                        Toast.makeText(CreateDish.this, "Dish Added Successfully", Toast.LENGTH_SHORT).show();
                                        imageAddedOr = false;
                                        finish();
                                    }).create();
                            myAlert.setCancelable(false);
                            linearLayout.addView(editText);
                            myAlert.setView(linearLayout);
                            myAlert.show();

                        }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void CheckPermission() {
        if(ContextCompat.checkSelfPermission(CreateDish.this, Manifest.permission.CAMERA) + ContextCompat.checkSelfPermission(CreateDish.this
        ,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        // Create the File where the photo should go
        File photoFile = Environment.getExternalStorageDirectory();
        imageAddedOr = true;
        outputFile = new File(photoFile,nameOfDish.getText().toString() + ".jpg");
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputFile));
        startActivityForResult(takePictureIntent, 20);
    }

    private void showDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image").setMessage("Select Any One Option")
                .setPositiveButton("Use Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction("android.intent.action.PICK");
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                    }
                }).create();
        builder.show();
    }

    private void addToDatabase(String name, String half, String full,String image,String mrp) {

        try {
            StorageReference reference = storageReference.child(dishAuth.getUid() + "/" + "image" + "/"  + name);
            reference.getDownloadUrl().addOnSuccessListener(uri -> {
                DatabaseReference dish = FirebaseDatabase.getInstance().getReference().getRoot();
                SharedPreferences storeImages = getSharedPreferences("storeImages",MODE_PRIVATE);
                SharedPreferences.Editor imageEdit = storeImages.edit();
                imageEdit.putString(name,uri + "");
                imageEdit.apply();
                DishInfo info = new DishInfo(name,half,full,uri + "",mrp,"0","0","0","yes",descriptionToSubmit,dishType,menuType);
                dish.child("Restaurants").child(state).child(locality).child(Objects.requireNonNull(dishAuth.getUid())).child("List of Dish").child(menuType).child(name).setValue(info);
                firestore.collection(state).document("Restaurants").collection(locality).document(dishAuth.getUid()).collection("List of Dish").document(name).set(info);
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
        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
             filePath = data.getData();
            Uri selectedImageUri = data.getData();

            imageAddedOr = true;

        }
    }

    private void initialise() {
        nameOfDish = findViewById(R.id.dishName);
        halfPlate = findViewById(R.id.halfPlatePrice);
        floatingActionButton = findViewById(R.id.searchOurDatabase);
        state = getIntent().getStringExtra("state");
        locality = getIntent().getStringExtra("locality");
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

            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            //here you can choose quality factor in third parameter(ex. i choosen 25)
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] fileInBytes = baos.toByteArray();
            StorageReference ref = storageReference.child(Objects.requireNonNull(dishAuth.getUid()) + "/" + "image" + "/"  + name);
            UploadTask uploadTask = ref.putBytes(fileInBytes);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(CreateDish.this, "File Uploaded", Toast.LENGTH_SHORT).show();
                    SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
                    DatabaseReference reference;
                    reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(dishAuth.getUid()))
                            .child("List of Dish").child(menuType).child(name);

                    StorageReference ref1 = storageReference.child(dishAuth.getUid() + "/" + "image" + "/"  + name);
                    ref1.getDownloadUrl().addOnSuccessListener(uri -> {
//                        Toast.makeText(CreateDish.this, "New Image Uploaded", Toast.LENGTH_SHORT).show();
//                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//                        firestore.collection(sharedPreferences.getString("state","")).document("Restaurants").collection(sharedPreferences.getString("locality","")).document(dishAuth.getUid())
//                                .collection("List of Dish").document(name).update("image",uri + "");
//                        SharedPreferences storeImages = getSharedPreferences("storeImages",MODE_PRIVATE);
//                        SharedPreferences.Editor imageEdit = storeImages.edit();
//                        imageEdit.putString(name,uri + "");
//                        reference.child("image").setValue(uri + "");
                        addToDatabase(name,half,full,uri + "",mrp);
//                        imageEdit.apply();
////                fastDialog.dismiss();
//                        finish();
                    });
                }
            });

            return null;
        }
    }



}