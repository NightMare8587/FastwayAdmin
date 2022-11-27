package com.consumers.fastwayadmin.HomeScreen;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReportsClassRecycler extends RecyclerView.Adapter<ReportsClassRecycler.Holder> {
    List<String> keyList = new ArrayList<>();
    String key;

    public ReportsClassRecycler(List<String> keyList,String key) {
        this.keyList = keyList;
        this.key = key;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_adaptercard_alert,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.textView.setText(keyList.get(position));
        holder.textView.setOnClickListener(v -> {
            FirebaseStorage storage;
            StorageReference storageReference;
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();
            FirebaseAuth auth = FirebaseAuth.getInstance();

            StorageReference innerRef = storageReference.child(auth.getUid() + "/" + "InsightsReports" + "/"  + key + "/" + keyList.get(position));
            final File rootPaths = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)));
            final File localFile = new File(rootPaths, keyList.get(position) + ".pdf");

            File file = new File(rootPaths,keyList.get(position) + ".pdf");

            innerRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                Log.e("firebase ", ";local tem file created  created " + localFile);
                Toast.makeText(v.getContext(), "File Downloaded", Toast.LENGTH_SHORT).show();
                Intent target = new Intent(Intent.ACTION_VIEW);
                target.setDataAndType(FileProvider.getUriForFile(v.getContext(), v.getContext().getApplicationContext().getPackageName() + ".provider",file), "application/pdf"); // here we set correct type for PDF
                target.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Intent intent = Intent.createChooser(target, "Open File");
                try {
                    v.getContext().startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    // Instruct the user to install a PDF reader here, or something
                }

                //  updateDb(timestamp,localFile.toString(),position);
            }).addOnFailureListener(exception -> {
                Log.e("firebase ", ";local tem file not created  created " + exception);


                if (ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.ACCESS_FINE_LOCATION + ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.CAMERA) + ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(v.getContext(), "Grant permission to download files :)", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions((Activity) v.getContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    Toast.makeText(v.getContext(), "Something went wrong :(", Toast.LENGTH_SHORT).show();
                }


            });

        });
    }

    @Override
    public int getItemCount() {
        return keyList.size();
    }
    public class Holder extends RecyclerView.ViewHolder{
        TextView textView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.recyclerDataTextViewAdap);
        }
    }
}
