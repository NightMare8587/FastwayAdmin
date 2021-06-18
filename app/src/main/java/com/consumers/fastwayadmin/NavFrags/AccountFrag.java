package com.consumers.fastwayadmin.NavFrags;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.consumers.fastwayadmin.ListViewActivity.MyAccount;
import com.consumers.fastwayadmin.Login.MainActivity;
import com.consumers.fastwayadmin.MyService;
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
    String[] names = {"My Account","Logout","Terms And Conditions","Privacy policy"};
    GoogleSignInClient googleSignInClient;
    SharedPreferences sharedPreferences;
    FirebaseAuth auth;
    int count = 0;
    boolean pressed = false;
    int total = 0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.account_frag,container,false);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                count++;
                pressed = true;
                Toast.makeText(getContext(), "Press again to exit", Toast.LENGTH_SHORT).show();

                if(count == 2 && pressed)
                    requireActivity().finish();

                new Handler().postDelayed(() -> {
                    pressed = false;
                    count = 0;
                },2000);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this,callback);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = view.findViewById(R.id.listView);
        auth = FirebaseAuth.getInstance();
        sharedPreferences = requireActivity().getSharedPreferences("After Logout",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(view.getContext(),R.layout.list,names);
        listView.setAdapter(arrayAdapter);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(requireContext(),gso);
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
                                        requireContext().stopService(new Intent(requireContext(), MyService.class));
                                        editor.putString("logout","yes");
                                        editor.apply();
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

                    case 2:
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.websitepolicies.com/policies/view/CpwDZziF"));
                        startActivity(browserIntent);
                        break;
                    case 3:
                        Intent privacyIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://fastway.flycricket.io/privacy.html"));
                        startActivity(privacyIntent);
                        break;
                }
            }
        });
    }
}
