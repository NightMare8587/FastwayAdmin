package com.example.fastwayadmin.NavFrags;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.fastwayadmin.Login.MainActivity;
import com.example.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class AccountFrag extends Fragment {
    Toolbar accountBar;
    EditText fullName,emailAddress;
    FirebaseUser mUser;
    Button changeInfo,saveChanges,logout,deleteAccount;
    FirebaseAuth accountAuth;
    DatabaseReference accountRef;
    ProgressBar progressBar;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.account_frag,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        accountBar = view.findViewById(R.id.accountFragBar);
        progressBar = view.findViewById(R.id.pBar);
        fullName = view.findViewById(R.id.AccountName);
        deleteAccount = view.findViewById(R.id.deleteAccount);
        changeInfo = view.findViewById(R.id.changeInfo);
        saveChanges = view.findViewById(R.id.saveChanges);
        logout = view.findViewById(R.id.Logout);
        emailAddress = view.findViewById(R.id.AccountEmail);
        fullName.setEnabled(false);
        emailAddress.setEnabled(false);
        accountAuth = FirebaseAuth.getInstance();
        accountRef = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(accountAuth.getUid()));
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        accountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    progressBar.setVisibility(View.INVISIBLE);
                    fullName.setText(snapshot.child("name").getValue().toString());
                    emailAddress.setText(snapshot.child("email").getValue().toString());
                }else{
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(), "Something Went Wrong!!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        changeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges.setVisibility(View.VISIBLE);
                fullName.setEnabled(true);
                emailAddress.setEnabled(true);
                changeInfo.setEnabled(false);
            }
        });
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accountRef.getRoot().child("Admin").child(accountAuth.getUid());
                accountRef.child("name").setValue(fullName.getText().toString()+"");
                accountRef.child("email").setValue(emailAddress.getText().toString()+"");
                saveChanges.setVisibility(View.INVISIBLE);
                changeInfo.setEnabled(true);
            }
        });

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
                builder.setTitle("Delete Account")
                        .setMessage("Are you sure wanna Delete Account!!!")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                accountRef = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants");
                                accountRef.child(accountAuth.getUid()).removeValue();
                                accountRef = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin");
                                accountRef.child(accountAuth.getUid()).removeValue();
                                accountAuth.signOut();
                                mUser.delete();
                                startActivity(new Intent(getActivity(), MainActivity.class));
                                Objects.requireNonNull(getActivity()).finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create();
                builder.show();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
                builder.setTitle("Sign Out")
                        .setMessage("Are you sure wanna sign out")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                accountAuth.signOut();
                                startActivity(new Intent(getActivity(), MainActivity.class));
                                Objects.requireNonNull(getActivity()).finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create();
               builder.show();
            }
        });
    }
}
