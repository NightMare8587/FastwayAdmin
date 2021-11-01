package com.consumers.fastwayadmin.ListViewActivity.BlockedUsersList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class BlockedRecyclerView extends RecyclerView.Adapter<BlockedRecyclerView.Holder> {
    List<String> userID;
    List<String> timeReported;
    String state;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    Context context;

    public BlockedRecyclerView(List<String> userID, List<String> timeReported, String state, Context context) {
        this.userID = userID;
        this.timeReported = timeReported;
        this.state = state;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.blocked_users_cardview,parent,false);
        return new Holder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, @SuppressLint("RecyclerView") int position) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(userID.get(position));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.userName.setText("Name: " + String.valueOf(snapshot.child("name").getValue()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        long milis = Long.parseLong(timeReported.get(position));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milis);
        holder.timeBLocked.setText("Date Reported: " + dateFormat.format(calendar.getTime()));

        holder.cardView.setOnClickListener(click -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle("Important");
            alert.setMessage("Do you wanna remove user from blocked list!");
            alert.setPositiveButton("Remove User", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(Objects.requireNonNull(auth.getUid())).child("Blocked List");
                    databaseReference1.child(userID.get(position)).removeValue();
                    Toast.makeText(context, "User Removed from Blocked List", Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).create();

            alert.show();
        });
    }

    @Override
    public int getItemCount() {
        return userID.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        TextView userName,timeBLocked;
        CardView cardView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.nameOfUserBlocked);
            timeBLocked = itemView.findViewById(R.id.dateOnUserBlocked);
            cardView = itemView.findViewById(R.id.blockedUserCardView);
        }
    }
}
