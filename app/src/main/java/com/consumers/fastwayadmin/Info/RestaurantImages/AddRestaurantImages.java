package com.consumers.fastwayadmin.Info.RestaurantImages;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.consumers.fastwayadmin.Dish.AddImageToDish;
import com.consumers.fastwayadmin.Info.MapsActivity;
import com.consumers.fastwayadmin.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import karpuzoglu.enes.com.fastdialog.Animations;
import karpuzoglu.enes.com.fastdialog.FastDialog;
import karpuzoglu.enes.com.fastdialog.FastDialogBuilder;
import karpuzoglu.enes.com.fastdialog.Type;

public class AddRestaurantImages extends AppCompatActivity {
    Button addImages,proceed;
    FirebaseStorage storage;
    int count = 1;
    int globalCount = 1;
    List<String> resImages = new ArrayList<>();
    DatabaseReference databaseReference;
    FastDialog fastDialog;
    String state;
    DatabaseReference reference;
    LinearLayoutManager linearLayoutManager;
    FirebaseAuth dishAuth = FirebaseAuth.getInstance();
    FirebaseAuth auth;
    RecyclerView recyclerView;
    StorageReference storageReference;
    String currentImageName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant_images);
        fastDialog = new FastDialogBuilder(AddRestaurantImages.this, Type.PROGRESS)
                .progressText("Uploading Image...")
                .setAnimation(Animations.FADE_IN)
                .cancelable(false)
                .create();
        addImages = findViewById(R.id.addImagesToDBButton);
        proceed = findViewById(R.id.proceedButtonAdmin);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        recyclerView = findViewById(R.id.showImagesAddedToResRecyclerView);
        state = getIntent().getStringExtra("state");
        auth = FirebaseAuth.getInstance();
        linearLayoutManager = new LinearLayoutManager(AddRestaurantImages.this,LinearLayoutManager.HORIZONTAL,false);
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(Objects.requireNonNull(auth.getUid())).child("RestaurantImages");
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
            currentImageName = "image" + count;
            intent.setType("image/*");
            intent.setAction("android.intent.action.PICK");
            startActivityForResult(Intent.createChooser(intent, "Select Picture Using"), 1);
        });
        proceed.setOnClickListener(click -> {
            startActivity(new Intent(AddRestaurantImages.this, MapsActivity.class));
            finish();
        });
    }

    private void updateChild() {

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    count = 1;
                    resImages.clear();
//                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
//                        resImages.add(String.valueOf(dataSnapshot.child("image" + count).getValue()));
//                        count++;
//                    }
                    for(int i=0;i<snapshot.getChildrenCount();i++) {
                        resImages.add(String.valueOf(snapshot.child("image" + count).getValue()));
                        count++;
                    }
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(new AddImagesAdapter(resImages));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri filepath = data.getData();
        fastDialog.show();
        Log.i("curr",currentImageName);
        StorageReference ref = storageReference.child(Objects.requireNonNull(dishAuth.getUid()) + "/" + "resImages" + "/"  + currentImageName);
        ref.putFile(filepath).addOnSuccessListener(taskSnapshot -> {
//                    Toast.makeText(AddImageToDish.this, "File Uploaded", Toast.LENGTH_SHORT).show();
            SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
            reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(Objects.requireNonNull(dishAuth.getUid()))
                    .child("RestaurantImages");

            StorageReference ref1 = storageReference.child(dishAuth.getUid() + "/" + "resImages" + "/"  + currentImageName);
            ref1.getDownloadUrl().addOnSuccessListener(uri -> {
                Toast.makeText(AddRestaurantImages.this, "New Image Uploaded", Toast.LENGTH_SHORT).show();
                reference.child(currentImageName).setValue(uri + "");
                fastDialog.dismiss();
            });
        }).addOnFailureListener(e -> fastDialog.dismiss());
    }
}