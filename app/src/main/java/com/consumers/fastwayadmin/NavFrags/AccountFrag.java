package com.consumers.fastwayadmin.NavFrags;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.consumers.fastwayadmin.ListViewActivity.MyAccount;
import com.consumers.fastwayadmin.Login.MainActivity;
import com.consumers.fastwayadmin.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class AccountFrag extends Fragment {
    ListView listView;
    String[] names = {"My Account","Logout"};
    GoogleSignInClient googleSignInClient;
    FirebaseAuth auth;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.account_frag,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = view.findViewById(R.id.listView);
        auth = FirebaseAuth.getInstance();
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(view.getContext(),R.layout.list,names);
        listView.setAdapter(arrayAdapter);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(Objects.requireNonNull(getContext()),gso);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int id = (int) adapterView.getItemIdAtPosition(i);
                switch (id){
                    case 0:
                        startActivity(new Intent(getActivity(), MyAccount.class));
                        break;
                    case 1:
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Logout")
                                .setMessage("Do you wanna logout?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        auth.signOut();
                                        googleSignInClient.signOut().addOnCompleteListener((Activity) getContext(), new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                            }
                                        });
                                        startActivity(new Intent(getActivity(), MainActivity.class));
                                        getActivity().finish();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).create();

                        builder.show();
                        break;
                }
            }
        });
    }
}
