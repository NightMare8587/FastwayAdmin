package com.consumers.fastwayadmin.ListViewActivity.StaffDetails;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import karpuzoglu.enes.com.fastdialog.Animations;
import karpuzoglu.enes.com.fastdialog.FastDialog;
import karpuzoglu.enes.com.fastdialog.FastDialogBuilder;
import karpuzoglu.enes.com.fastdialog.Type;

public class RestaurantStaff extends AppCompatActivity {
    DatabaseReference databaseReference;
    List<String> name = new ArrayList<>();
    List<String> image = new ArrayList<>();
    Bitmap bitmap;
    OutputStream outputStream;
    String currentUUID;
    File file;
    List<String> isBankAvailable = new ArrayList<>();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FastDialog loading;
    RecyclerView recyclerView;
    StorageReference storageReference;
    String currentString = "";
    Uri filePath;
    FirebaseStorage storage;
    Button addStaff;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_staff);
        recyclerView = findViewById(R.id.restaurantStaffDetailsRecyclerView);
        checkPermissions();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        addStaff = findViewById(R.id.addRestaurantStaffDetails);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid())).child("Restaurant Staff");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        name.add(String.valueOf(dataSnapshot.child("name").getValue()));
                        image.add(String.valueOf(dataSnapshot.child("image").getValue()));
                    }
                    recyclerView.setAdapter(new StaffAdapter(name,image));
                }else
                {
                    Toast.makeText(RestaurantStaff.this, "Add Staff Details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateChild();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateChild();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                updateChild();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(RestaurantStaff.this);
                alert.setTitle("Dialog");
                alert.setMessage("Add Name in below field");
                LinearLayout linearLayout = new LinearLayout(RestaurantStaff.this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                EditText editText = new EditText(RestaurantStaff.this);
                editText.setHint("Enter Name Here");
                editText.setMaxLines(100);
                alert.setPositiveButton("Add Name", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(!editText.getText().toString().equals("")) {
                            dialogInterface.dismiss();
                            currentString = editText.getText().toString();
                            currentUUID = String.valueOf(UUID.randomUUID());
                            DatabaseReference addStaff = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid()).child("Restaurant Staff");
                            addStaff.child(currentUUID).child("name").setValue(editText.getText().toString());
                            AlertDialog.Builder builder = new AlertDialog.Builder(RestaurantStaff.this);
                            builder.setTitle("Add Image").setMessage("Do you wanna image of your staff\n\nYou can also add image later").setPositiveButton("Choose From Gallery", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    Intent intent = new Intent();
                                    intent.setType("image/*");
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
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

                                }
                            }).create();
                            builder.show();
                        }else
                            Toast.makeText(RestaurantStaff.this, "Field can't be empty", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Toast.makeText(RestaurantStaff.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                linearLayout.addView(editText);
                alert.setView(linearLayout);
                alert.create();
                alert.show();
            }
        });
    }

    private void updateChild() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    image.clear();
                    name.clear();
                    isBankAvailable.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        name.add(String.valueOf(dataSnapshot.child("name").getValue()));
                        image.add(String.valueOf(dataSnapshot.child("image").getValue()));
                    }
                    recyclerView.setAdapter(new StaffAdapter(name,image));
                }else
                {
                    Toast.makeText(RestaurantStaff.this, "Add Staff Details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void askForBank(){
        AlertDialog.Builder alert = new AlertDialog.Builder(RestaurantStaff.this);
        alert.setTitle("Bank Details").setMessage("Do wanna add bank details of your staff\nIf customer wanna report something or give tip to your staff")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(RestaurantStaff.this);
                        builder.setTitle("Add Details").setMessage("Add Details Below");
                        LinearLayout linearLayout = new LinearLayout(RestaurantStaff.this);
                        linearLayout.setOrientation(LinearLayout.VERTICAL);
                        EditText holderAccountNumber = new EditText(RestaurantStaff.this);
                        holderAccountNumber.setHint("Enter Account number here");
                        holderAccountNumber.setMaxLines(100);
                        holderAccountNumber.setInputType(InputType.TYPE_CLASS_NUMBER);

                        EditText holderAccountIFSC = new EditText(RestaurantStaff.this);
                        holderAccountIFSC.setHint("Enter IFSC code here");
                        holderAccountIFSC.setMaxLines(100);

                        EditText holderName = new EditText(RestaurantStaff.this);
                        holderName.setHint("Enter Holder Name here");

                        linearLayout.addView(holderAccountNumber);
                        linearLayout.addView(holderAccountIFSC);
                        linearLayout.addView(holderName);
                        builder.setView(linearLayout);
                        builder.setPositiveButton("Add Details", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                if(holderAccountNumber.getText().toString().equals("")){
                                    holderAccountNumber.requestFocus();
                                    holderAccountNumber.setError("Field can't be empty");
                                    return;
                                }

                                if(holderAccountIFSC.getText().toString().equals("")){
                                    holderAccountIFSC.requestFocus();
                                    holderAccountIFSC.setError("Field can't be empty");
                                    return;
                                }

                                if(holderAccountIFSC.getText().toString().length() != 11){
                                    holderAccountIFSC.requestFocus();
                                    holderAccountIFSC.setError("Invalid IFSC code");
                                    return;
                                }

                                if(holderName.getText().toString().equals("")){
                                    holderName.requestFocus();
                                    holderName.setError("Field can't be empty");
                                    return;
                                }

                                DatabaseReference addBank = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid())).child("Restaurant Staff").child(currentUUID);
                                addBank.child("Bank Details").child("accountNumber").setValue(holderAccountNumber.getText().toString());
                                addBank.child("Bank Details").child("accountIFSC").setValue(holderAccountIFSC.getText().toString());
                                addBank.child("Bank Details").child("accountName").setValue(holderName.getText().toString());

                                Toast.makeText(RestaurantStaff.this, "Details Added Successfully", Toast.LENGTH_SHORT).show();

                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create();
                        builder.show();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create();
        alert.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions() {
        if(ContextCompat.checkSelfPermission(RestaurantStaff.this, Manifest.permission.ACCESS_FINE_LOCATION) + ContextCompat.checkSelfPermission(RestaurantStaff.this , Manifest.permission.CAMERA)
                + ContextCompat.checkSelfPermission(RestaurantStaff.this, Manifest.permission.WRITE_EXTERNAL_STORAGE )
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
                Toast.makeText(RestaurantStaff.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }else
                Toast.makeText(RestaurantStaff.this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 22){
//            loading = new FastDialogBuilder(RestaurantStaff.this, Type.PROGRESS)
//                    .setAnimation(Animations.SLIDE_BOTTOM)
//                    .progressText("Uploading Image.....")
//                    .create();

//            loading.show();
            bitmap = (Bitmap) data.getExtras().get("data");
            File filepath = Environment.getExternalStorageDirectory();
            File dir = new File(filepath.getAbsolutePath());
            dir.mkdir();
askForBank();
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
                reference.putFile(Uri.fromFile(file)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        StorageReference reference = storageReference.child(auth.getUid() + "/" + "Staff" + "/"  + currentString);
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(@NonNull Uri uri) {
                                Toast.makeText(RestaurantStaff.this, "Upload Complete and image saved in phone successfully", Toast.LENGTH_SHORT).show();
//                                loading.dismiss();

                                DatabaseReference dish = FirebaseDatabase.getInstance().getReference().getRoot();

                                dish.child("Admin").child(Objects.requireNonNull(auth.getUid())).child("Restaurant Staff").child(currentUUID).child("image").setValue(uri + "");

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RestaurantStaff.this,
                                "Something went wrong", Toast.LENGTH_SHORT).show();
//                        loading.dismiss();
                    }
                });
            }catch (Exception e){
                Toast.makeText(RestaurantStaff.this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                loading.dismiss();
            }
        }else if(requestCode == 4){
//            loading = new FastDialogBuilder(RestaurantStaff.this,Type.PROGRESS)
//                    .setAnimation(Animations.SLIDE_BOTTOM)
//                    .progressText("Uploading Image.....")
//                    .create();
//
//            loading.show();
            filePath = data.getData();
            askForBank();
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
                                Toast.makeText(RestaurantStaff.this, "Upload Complete and image saved in phone successfully", Toast.LENGTH_SHORT).show();
//                                loading.dismiss();
                                DatabaseReference dish = FirebaseDatabase.getInstance().getReference().getRoot();
                                dish.child("Admin").child(Objects.requireNonNull(auth.getUid())).child("Restaurant Staff").child(currentUUID).child("image").setValue(uri + "");

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        loading.dismiss();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
//                loading.dismiss();
            }
        }


    }
}