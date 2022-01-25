package com.consumers.fastwayadmin.Info.RestaurantDocuments.ReUploadDocuments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ViewAndReuploadDocuments extends AppCompatActivity {
    DatabaseReference databaseReference;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String pan,adhaar,fssai,gst;
    Button panButton,gstButton,adhaarButton,fssaiButton;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_and_reupload_documents);
        initialise();
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid())).child("Restaurant Documents");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adhaar = String.valueOf(dataSnapshot.child("adhaar").getValue());
                gst = String.valueOf(dataSnapshot.child("gst").getValue());
                pan = String.valueOf(dataSnapshot.child("pan").getValue());
                fssai = String.valueOf(dataSnapshot.child("fssai").getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        gstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!gst.isEmpty())
                showAlertDialog(gst);
            }
        });
        fssaiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!fssai.isEmpty())
                showAlertDialog(fssai);
            }
        });
        panButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!pan.isEmpty())
                showAlertDialog(pan);
            }
        });
        adhaarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!adhaar.isEmpty())
                showAlertDialog(adhaar);
            }
        });
    }

    private void showAlertDialog(String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewAndReuploadDocuments.this);
        builder.setTitle("Dialog").setMessage("Choose one option from below!").setPositiveButton("Reupload Document", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setNegativeButton("View Document", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Picasso.get().load(str).into(imageView);
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
        gstButton = findViewById(R.id.gstGridViewButton);
        fssaiButton = findViewById(R.id.fssaiGridViewButton);
        adhaarButton = findViewById(R.id.adhaarGridViewButton);
        panButton = findViewById(R.id.panGridViewButton);
        imageView = findViewById(R.id.imageViewViewAndReuploadDocuments);
    }
}