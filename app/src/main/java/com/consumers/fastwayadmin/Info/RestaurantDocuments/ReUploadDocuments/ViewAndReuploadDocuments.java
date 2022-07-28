package com.consumers.fastwayadmin.Info.RestaurantDocuments.ReUploadDocuments;

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
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.consumers.fastwayadmin.Info.RestaurantDocuments.UploadRemainingDocs;
import com.consumers.fastwayadmin.R;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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

public class ViewAndReuploadDocuments extends AppCompatActivity {
    DatabaseReference databaseReference;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String currentString = "";
    boolean fssaiNum = false;
    ProgressBar progressBar;
    String pan,adhaar,fssai,gst,resProof;
    FastDialog loading;
    String fssaiDigitsNums;
    Button panButton,gstButton,adhaarButton,fssaiButton,resProofButton;
    File file;
    OutputStream outputStream;
    StorageReference storageReference;
    Uri filePath;
    FirebaseStorage storage;
    Bitmap bitmap;
    ImageView imageView;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_and_reupload_documents);
        initialise();
        checkPermissions();
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid())).child("Restaurant Documents");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adhaar = String.valueOf(dataSnapshot.child("adhaar").getValue());
//                gst = String.valueOf(dataSnapshot.child("gst").getValue());
//                pan = String.valueOf(dataSnapshot.child("pan").getValue());
                fssai = String.valueOf(dataSnapshot.child("fssai").getValue());
                fssaiDigitsNums = String.valueOf(dataSnapshot.child("fssaiDigits").getValue());
                resProof = String.valueOf(dataSnapshot.child("resProof").getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        gstButton.setOnClickListener(view -> {
//            if(!gst.isEmpty() && !gst.equals("null"))
//            showAlertDialog(gst,"gst");
//            else{
//                reuploadImageOrNotUploaded("gst");
//            }
//        });
        fssaiButton.setOnClickListener(view -> {
            if(!fssai.isEmpty() && !fssai.equals("null"))
            showAlertDialog(fssai,"fssai");
            else{
                reuploadImageOrNotUploaded("fssai");
            }
        });
//        panButton.setOnClickListener(view -> {
//            if(!pan.isEmpty() && !pan.equals("null"))
//            showAlertDialog(pan,"pan");
//            else{
//                reuploadImageOrNotUploaded("pan");
//            }
//        });
        adhaarButton.setOnClickListener(view -> {
            if(!adhaar.isEmpty() && !adhaar.equals("null"))
            showAlertDialog(adhaar,"adhaar");
            else{
                reuploadImageOrNotUploaded("adhaar");
            }
        });
        resProofButton.setOnClickListener(click -> {
            if(!resProof.isEmpty() && !resProof.equals("null"))
                showAlertDialog(resProof,"resProof");
            else{
                reuploadImageOrNotUploaded("resProof");
            }
        });
    }

    private void reuploadImageOrNotUploaded(String str) {
        if(str.equals("fssai")){
            AlertDialog.Builder fssaiBuild = new AlertDialog.Builder(ViewAndReuploadDocuments.this);
            fssaiBuild.setTitle("Fssai Number").setMessage("Enter your 14 digit FSSAI number below");
            LinearLayout linearLayout = new LinearLayout(ViewAndReuploadDocuments.this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            EditText editText = new EditText(ViewAndReuploadDocuments.this);
            editText.setHint("Enter FSSAI Number here");
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setMaxLines(14);
            linearLayout.addView(editText);
            fssaiBuild.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewAndReuploadDocuments.this);
                    builder.setTitle("Choose one option").setPositiveButton("Choose from Gallery", (dialogInterface, i) -> {
                        if(editText.length() == 14 && TextUtils.isDigitsOnly(editText.getText().toString())) {
                            dialogInterface.dismiss();
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            fssaiDigitsNums = editText.getText().toString();
                            SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("fssaiNumDigit",fssaiDigitsNums);
                            editor.apply();
                            fssaiNum = true;
                            intent.setAction("android.intent.action.PICK");
                            currentString = str;
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 5);
                        }
                    }).setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
                }
            }).setNegativeButton("Exit", (dialog, which) -> dialog.dismiss()).create();
            fssaiBuild.setView(linearLayout);
            fssaiBuild.show();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(ViewAndReuploadDocuments.this);
            builder.setTitle("Choose one option").setPositiveButton("Choose from Gallery", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    Intent intent = new Intent();
                    intent.setType("image/*");
//                        fssaiDigitsNums = editText.getText().toString();
//                        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putString("fssaiNumDigit",fssaiDigitsNums);
//                        editor.apply();
                    fssaiNum = false;
                    intent.setAction("android.intent.action.PICK");
                    currentString = str;
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 5);
            }).setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
        }


    }

    private void showAlertDialog(String str,String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewAndReuploadDocuments.this);
        builder.setTitle("Dialog").setMessage("Choose one option from below!").setPositiveButton("Re-upload Document", (dialogInterface, i) -> {
            dialogInterface.dismiss();
            AlertDialog.Builder alert = new AlertDialog.Builder(ViewAndReuploadDocuments.this);
            alert.setTitle("Dialog").setMessage("Choose one option from below")
                    .setPositiveButton("Take Photo", (dialogInterface1, i1) -> {
                        dialogInterface1.dismiss();
                        currentString = name;
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //IMAGE CAPTURE CODE
                        startActivityForResult(intent, 22);
                    }).setNegativeButton("Choose from gallery", (dialogInterface12, i12) -> {

                        dialogInterface12.dismiss();
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction("android.intent.action.PICK");
                        currentString = name;
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 4);
                    }).setNeutralButton("Cancel", (dialogInterface13, i13) -> dialogInterface13.dismiss()).create();
            alert.show();
        }).setNegativeButton("View Document", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                progressBar.setVisibility(View.VISIBLE);
                Picasso.get().load(str).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }).setNeutralButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create();
        builder.show();
    }

    private void initialise() {
//        gstButton = findViewById(R.id.gstGridViewButton);
        fssaiButton = findViewById(R.id.fssaiGridViewButton);
        adhaarButton = findViewById(R.id.adhaarGridViewButton);
//        panButton = findViewById(R.id.panGridViewButton);
        resProofButton = findViewById(R.id.resProofGridViewButton);
        imageView = findViewById(R.id.imageViewViewAndReuploadDocuments);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        progressBar = findViewById(R.id.viewAndReuploadProgressBar);
        progressBar.setVisibility(View.INVISIBLE);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions() {
        if(ContextCompat.checkSelfPermission(ViewAndReuploadDocuments.this, Manifest.permission.ACCESS_FINE_LOCATION) + ContextCompat.checkSelfPermission(ViewAndReuploadDocuments.this , Manifest.permission.CAMERA)
                + ContextCompat.checkSelfPermission(ViewAndReuploadDocuments.this, Manifest.permission.WRITE_EXTERNAL_STORAGE )
                != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION},88);
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 88){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(ViewAndReuploadDocuments.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }else
                Toast.makeText(ViewAndReuploadDocuments.this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 22){
            loading = new FastDialogBuilder(ViewAndReuploadDocuments.this, Type.PROGRESS)
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

            }
            try {
                StorageReference reference = storageReference.child(auth.getUid() + "/" + "Documents" + "/"  + currentString);
                reference.putFile(Uri.fromFile(file)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        StorageReference reference = storageReference.child(auth.getUid() + "/" + "Documents" + "/"  + currentString);
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(@NonNull Uri uri) {
                                Toast.makeText(ViewAndReuploadDocuments.this, "Upload Complete and image saved in phone successfully", Toast.LENGTH_SHORT).show();
                                loading.dismiss();

                                DatabaseReference dish = FirebaseDatabase.getInstance().getReference().getRoot();

                                dish.child("Admin").child(Objects.requireNonNull(auth.getUid())).child("Restaurant Documents").child(currentString).setValue(uri + "");

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ViewAndReuploadDocuments.this,
                                "Something went wrong", Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                    }
                });
            }catch (Exception e){
                Toast.makeText(ViewAndReuploadDocuments.this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }
        }else if(requestCode == 4){
            loading = new FastDialogBuilder(ViewAndReuploadDocuments.this,Type.PROGRESS)
                    .setAnimation(Animations.SLIDE_BOTTOM)
                    .progressText("Uploading Image.....")
                    .create();

            loading.show();
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                StorageReference reference = storageReference.child(auth.getUid() + "/" + "Documents" + "/"  + currentString);
                reference.putFile(filePath).addOnSuccessListener(taskSnapshot -> {
                    StorageReference reference1 = storageReference.child(auth.getUid() + "/" + "Documents" + "/"  + currentString);
                    reference1.getDownloadUrl().addOnSuccessListener(uri -> {
                        Toast.makeText(ViewAndReuploadDocuments.this, "Upload Complete and image saved in phone successfully", Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                        DatabaseReference dish = FirebaseDatabase.getInstance().getReference().getRoot();
                        dish.child("Admin").child(Objects.requireNonNull(auth.getUid())).child("Restaurant Documents").child(currentString).setValue(uri + "");

                    });
                }).addOnFailureListener(e -> loading.dismiss());
            } catch (IOException e) {
                e.printStackTrace();
                loading.dismiss();
            }
        }else if(requestCode == 5 && resultCode == RESULT_OK && data != null){
            loading = new FastDialogBuilder(ViewAndReuploadDocuments.this,Type.PROGRESS)
                    .setAnimation(Animations.SLIDE_BOTTOM)
                    .progressText("Uploading Image.....")
                    .create();

            loading.show();
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                StorageReference reference = storageReference.child(auth.getUid() + "/" + "Documents" + "/"  + currentString);
                reference.putFile(filePath).addOnSuccessListener(taskSnapshot -> {
                    StorageReference reference1 = storageReference.child(auth.getUid() + "/" + "Documents" + "/"  + currentString);
                    reference1.getDownloadUrl().addOnSuccessListener(uri -> {
                        Toast.makeText(ViewAndReuploadDocuments.this, "Upload Complete and image saved in phone successfully", Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                        DatabaseReference dish = FirebaseDatabase.getInstance().getReference().getRoot();
                        dish.child("Admin").child(Objects.requireNonNull(auth.getUid())).child("Restaurant Documents").child(currentString).setValue(uri + "");
                        if(fssaiNum)
                            dish.child("Admin").child(Objects.requireNonNull(auth.getUid())).child("Restaurant Documents").child("fssaiDigits").setValue(fssaiDigitsNums + "");
                    });
                }).addOnFailureListener(e -> loading.dismiss());
            } catch (IOException e) {
                e.printStackTrace();
                loading.dismiss();
            }
        }
    }
}