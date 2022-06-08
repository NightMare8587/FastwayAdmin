package com.consumers.fastwayadmin.Info.RestaurantImages;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AddImagesAdapter extends RecyclerView.Adapter<AddImagesAdapter.Holder> {
    List<String> resImages,timeAdded;
    String state,locality;
    StorageReference storageReference;
    FirebaseStorage storage;

    public AddImagesAdapter(List<String> resImages, String state, String locality, List<String> timeAdded) {
        this.resImages = resImages;
        this.timeAdded = timeAdded;
        this.state = state;
        this.locality = locality;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.images_res_card,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Picasso.get().load(resImages.get(position)).into(holder.imageView, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(Exception e) {
                holder.progressBar.setVisibility(View.INVISIBLE);
            }
        });
        holder.cardView.setOnClickListener(click -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(click.getContext());
            builder.setTitle("Delete").setMessage("Do you wanna delete this image?").setPositiveButton("Yes", (dialogInterface, i) -> {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(locality).child(auth.getUid()).child("RestaurantImages");
                databaseReference.child(timeAdded.get(position)).removeValue();
                storage = FirebaseStorage.getInstance();
                storageReference = storage.getReference();
                StorageReference photoRef = storage.getReferenceFromUrl(resImages.get(position));
                photoRef.delete();
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).create().show();
        });
    }

    @Override
    public int getItemCount() {
        return resImages.size();
    }
    public class Holder extends RecyclerView.ViewHolder{
        ImageView imageView;
        ProgressBar progressBar;
        CardView cardView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewCardViewRes);
            progressBar = itemView.findViewById(R.id.progressBarResImages);
            cardView = itemView.findViewById(R.id.resImagesCardViewAdmin);
        }
    }
}
