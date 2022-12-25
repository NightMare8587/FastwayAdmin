package com.consumers.fastwayadmin.ListViewActivity.StaffDetails;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

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

public class UpdateImageRestaurantStaff extends AppCompatActivity {
    Bitmap bitmap;
    OutputStream outputStream;
    String currentUUID;
    File file;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    StorageReference storageReference;
    String currentString = "";
    FastDialog loading;
    Uri filePath;
    FirebaseStorage storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_image_restaurant_staff);
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateImageRestaurantStaff.this);
        currentUUID = getIntent().getStringExtra("uuid");
        currentString = getIntent().getStringExtra("name");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        builder.setTitle("Add Image").setMessage("Do you wanna image of your staff\n\nYou can also add image later").setPositiveButton("Choose From Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction("android.intent.action.PICK");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 4);

            }
        }).setNegativeButton("Take Photo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //IMAGE CAPTURE CODE
                startActivityForResult(intent, 22);
            }
        }).setNeutralButton("Not Now", new DialogInterface.OnClickListener() {
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
        if(requestCode == 22){
            loading = new FastDialogBuilder(UpdateImageRestaurantStaff.this, Type.PROGRESS)
                    .setAnimation(Animations.SLIDE_BOTTOM)
                    .progressText("Uploading Image.....")
                    .create();

            loading.show();
            bitmap = (Bitmap) data.getExtras().get("data");
            File filepath = Environment.getExternalStorageDirectory();
            File dir = new File(filepath.getAbsolutePath());
            dir.mkdir();
            file = new File(dir, currentString + ".jpg");
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
                StorageReference reference = storageReference.child(auth.getUid() + "/" + "Staff" + "/"  + currentString);
                reference.putFile(Uri.fromFile(file)).addOnCompleteListener(task -> {
                    StorageReference reference1 = storageReference.child(auth.getUid() + "/" + "Staff" + "/"  + currentString);
                    reference1.getDownloadUrl().addOnSuccessListener(uri -> {
                        Toast.makeText(UpdateImageRestaurantStaff.this, "Upload Complete and image saved in phone successfully", Toast.LENGTH_SHORT).show();
                        loading.dismiss();

                        DatabaseReference dish = FirebaseDatabase.getInstance().getReference().getRoot();

                        dish.child("Admin").child(Objects.requireNonNull(auth.getUid())).child("Registered Staff").child(currentUUID).child("image").setValue(uri + "");
                        finish();
                    });

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateImageRestaurantStaff.this,
                                "Something went wrong", Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                    }
                });
            }catch (Exception e){
                Toast.makeText(UpdateImageRestaurantStaff.this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }
        }else if(requestCode == 4){
            loading = new FastDialogBuilder(UpdateImageRestaurantStaff.this,Type.PROGRESS)
                    .setAnimation(Animations.SLIDE_BOTTOM)
                    .progressText("Uploading Image.....")
                    .create();

            loading.show();
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                StorageReference reference = storageReference.child(auth.getUid() + "/" + "Staff" + "/"  + currentString);
                reference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        StorageReference reference = storageReference.child(auth.getUid() + "/" + "Staff" + "/"  + currentString);
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(@NonNull Uri uri) {
                                Toast.makeText(UpdateImageRestaurantStaff.this, "Upload Complete and image saved in phone successfully", Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                                DatabaseReference dish = FirebaseDatabase.getInstance().getReference().getRoot();
                                dish.child("Admin").child(Objects.requireNonNull(auth.getUid())).child("Registered Staff").child(currentUUID).child("image").setValue(uri + "");
                                finish();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loading.dismiss();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                loading.dismiss();
            }
        }


    }
}