package com.consumers.fastwayadmin.NavFrags.ReUploadResImages;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.consumers.fastwayadmin.Info.RestaurantDocuments.UploadRequiredDocuments;
import com.consumers.fastwayadmin.Info.RestaurantImages.AddImagesAdapter;
import com.consumers.fastwayadmin.Info.RestaurantImages.AddRestaurantImages;
import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import karpuzoglu.enes.com.fastdialog.Animations;
import karpuzoglu.enes.com.fastdialog.FastDialog;
import karpuzoglu.enes.com.fastdialog.FastDialogBuilder;
import karpuzoglu.enes.com.fastdialog.Type;

public class ReUploadRestaurantImages extends AppCompatActivity {
    Button addImages,proceed;
    FirebaseStorage storage;
    List<String> resImages = new ArrayList<>();
    DatabaseReference databaseReference;
    FastDialog fastDialog;
    List<String> timeAdded = new ArrayList<>();
    String state;
    String locality;
    DatabaseReference reference;
    LinearLayoutManager linearLayoutManager;
    FirebaseAuth dishAuth = FirebaseAuth.getInstance();
    FirebaseAuth auth;
    RecyclerView recyclerView;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_upload_restaurant_images);
        fastDialog = new FastDialogBuilder(ReUploadRestaurantImages.this, Type.PROGRESS)
                .progressText("Uploading Image...")
                .setAnimation(Animations.FADE_IN)
                .cancelable(false)
                .create();
        addImages = findViewById(R.id.addImagesToDBButtonReUpload);
        proceed = findViewById(R.id.proceedButtonAdminReUpload);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        recyclerView = findViewById(R.id.showImagesAddedToResRecyclerViewReUpload);
        recyclerView.setLayoutManager(new GridLayoutManager(ReUploadRestaurantImages.this,3));
        state = getIntent().getStringExtra("state");
        locality = getIntent().getStringExtra("locality");
        Toast.makeText(this, "Click on particular image to delete that image", Toast.LENGTH_SHORT).show();
        auth = FirebaseAuth.getInstance();
        linearLayoutManager = new LinearLayoutManager(ReUploadRestaurantImages.this,LinearLayoutManager.HORIZONTAL,false);
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(locality).child(Objects.requireNonNull(auth.getUid())).child("RestaurantImages");
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
        addImages.setOnClickListener(click -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction("android.intent.action.PICK");
            startActivityForResult(Intent.createChooser(intent, "Select Picture Using"), 1);
        });
        proceed.setOnClickListener(click -> {
            finish();
        });
    }
    private void updateChild() {

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    timeAdded.clear();
                    resImages.clear();
                    Log.i("snap",snapshot.toString());
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        timeAdded.add(dataSnapshot.getKey());
                        resImages.add(String.valueOf(dataSnapshot.child("imageUri").getValue()));
                    }
                    Log.i("info",resImages.toString());
                }else{
                    resImages.clear();
                    timeAdded.clear();
                }
                recyclerView.setAdapter(new AddImagesAdapter(resImages,state,locality,timeAdded));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Uri filepath = data.getData();
            fastDialog.show();
            String time = String.valueOf(System.currentTimeMillis());
            StorageReference ref = storageReference.child(Objects.requireNonNull(dishAuth.getUid()) + "/" + "resImages" + "/" + time);
            ref.putFile(filepath).addOnSuccessListener(taskSnapshot -> {
                SharedPreferences sharedPreferences = getSharedPreferences("loginInfo", MODE_PRIVATE);
                reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state", "")).child(locality).child(Objects.requireNonNull(dishAuth.getUid()))
                        .child("RestaurantImages");

                StorageReference ref1 = storageReference.child(dishAuth.getUid() + "/" + "resImages" + "/" + time);
                ref1.getDownloadUrl().addOnSuccessListener(uri -> {
                    Toast.makeText(ReUploadRestaurantImages.this, "New Image Uploaded", Toast.LENGTH_SHORT).show();

                    reference.child(time).child("imageUri").setValue(uri + "");
                    updateChild();
                    fastDialog.dismiss();

                });
            }).addOnFailureListener(e -> fastDialog.dismiss());
        }else
            Toast.makeText(this, "Error. Try again", Toast.LENGTH_SHORT).show();
    }
}