package com.consumers.fastwayadmin.NavFrags.ResEarningTracker.NotifyUserPackage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UserAdap extends RecyclerView.Adapter<UserAdap.Holder> {
    List<String> adapterTime = new ArrayList<>();
    List<String> adapterContact = new ArrayList<>();
    List<String> adapterId = new ArrayList<>();

    public UserAdap(List<String> adapterTime, List<String> adapterContact, List<String> adapterId) {
        this.adapterTime = adapterTime;
        this.adapterContact = adapterContact;
        this.adapterId = adapterId;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notify_user_card,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.contact.setText("Contact Num: " + adapterContact.get(position));
        holder.time.setText("Last Visit " + TimeUnit.MILLISECONDS.toDays(Long.parseLong(adapterTime.get(position))) + " ago");

        holder.cardView.setOnClickListener(click -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(click.getContext());
            LinearLayout linearLayout = new LinearLayout(click.getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            EditText editText = new EditText(click.getContext());
            editText.setHint("Enter Message Here");
            linearLayout.addView(editText);
            builder.setView(linearLayout);
            builder.setTitle("Notify User")
                    .setMessage("Do you wanna notify this user via whatsapp !!")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        if(editText.length() == 0) {
                            Toast.makeText(click.getContext(), "Enter some message", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            String text = editText.getText().toString();// Replace with your message.

                            String toNumber = "91" + adapterContact.get(position); // Replace with mobile phone number without +Sign or leading zeros, but with country code
                            //Suppose your country is India and your phone number is “xxxxxxxxxx”, then you need to send “91xxxxxxxxxx”.


                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+toNumber +"&text="+text));
                            click.getContext().startActivity(intent);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }).setNegativeButton("Later", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).create().show();
        });
    }

    @Override
    public int getItemCount() {
        return adapterId.size();
    }
    public class Holder extends RecyclerView.ViewHolder{
        TextView time,contact;
        CardView cardView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.timeStampUserNotify);
            contact = itemView.findViewById(R.id.ContactNumUserNotify);
            cardView = itemView.findViewById(R.id.notifyUserCardId);
        }
    }
}
