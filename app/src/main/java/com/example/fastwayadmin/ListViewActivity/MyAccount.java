package com.example.fastwayadmin.ListViewActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.fastwayadmin.R;
import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import nl.invissvenska.modalbottomsheetdialog.Item;
import nl.invissvenska.modalbottomsheetdialog.ModalBottomSheetDialog;

public class MyAccount extends AppCompatActivity implements ModalBottomSheetDialog.Listener {
    ListView listView;
    DatabaseReference reference;
    FirebaseAuth auth;
    ModalBottomSheetDialog modalBottomSheetDialog;
    TextView textView;
    String[] names = {"Change Credentials (Admin)","Change Credentials (Restaurants)","Change Mobile Number","Delete Account"};
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        initialise();
        modalBottomSheetDialog = new ModalBottomSheetDialog.Builder()
                .setRoundedModal(true)
                .setHeader("Choose One Option")
                .add(R.menu.bottom_sheet)
                .build();
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, R.layout.list,names);
        listView.setAdapter(arrayAdapter);
        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        textView.setText("Hi, " + sharedPreferences.getString("name",""));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
//                        FlatDialog flatDialog = new FlatDialog(MyAccount.this)
//                                .setTitle("Choose One Option")
//                                .setTitleColor(Color.BLACK)
//                                .setFirstButtonText("Change Name")
//                                .setFirstButtonTextColor(Color.BLACK)
//                                .setFirstButtonColor(Color.LTGRAY)
//                                .setSecondButtonColor(Color.LTGRAY)
//                                .setSecondButtonText("Change Email")
//                                .setSecondButtonTextColor()

                        modalBottomSheetDialog.show(getSupportFragmentManager(),"xd");

                        break;
                    case 1:
                        break;
                }
            }
        });
    }
    private void initialise() {
        textView = findViewById(R.id.account_activity_text);
        listView = findViewById(R.id.accountActivityListView);
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onItemSelected(String tag, Item item) {
        int id = item.getId();
        switch (id){
            case R.id.changeNameBottomSheet:
                FlatDialog flatDialog = new FlatDialog(MyAccount.this);
                               flatDialog.setTitle("Enter New Name")
                                       .setBackgroundColor(Color.WHITE)
                                .setTitleColor(Color.BLACK)
                                .setFirstTextFieldHint("Enter New Name")
                                       .setFirstTextFieldBorderColor(Color.BLACK)
                                       .setFirstTextFieldHintColor(Color.BLACK)
                                       .setFirstTextFieldTextColor(Color.BLACK)
                                       .setFirstTextFieldTextColor(Color.BLACK)
                                .setFirstButtonText("Make Changes")
                        .setFirstButtonColor(Color.LTGRAY)
                        .setFirstButtonTextColor(Color.BLACK)
                                       .setSecondButtonText("Cancel")
                                       .setSecondButtonColor(Color.CYAN)
                                       .setSecondButtonTextColor(Color.BLACK)
                        .withFirstButtonListner(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Map<String,Object> map = new HashMap<>();
                                Log.i("auth",auth.getUid());
                                map.put("name",String.valueOf(flatDialog.getFirstTextField().toString()));
                                reference.child("name").setValue(flatDialog.getFirstTextField().toString());
                                Toast.makeText(MyAccount.this, "Name Changed Successfully", Toast.LENGTH_SHORT).show();
                                flatDialog.dismiss();
                            }
                        })
                                       .withSecondButtonListner(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               flatDialog.dismiss();
                                           }
                                       });

                flatDialog.create();
                flatDialog.show();
                modalBottomSheetDialog.dismiss();
                break;
            case R.id.changeEmailBottomSheet:
                FlatDialog flatDialog1 = new FlatDialog(MyAccount.this);
                flatDialog1.setTitle("Enter New Name")
                        .setBackgroundColor(Color.WHITE)
                        .setTitleColor(Color.BLACK)
                        .setFirstTextFieldBorderColor(Color.BLACK)
                        .setFirstTextFieldHintColor(Color.BLACK)
                        .setFirstTextFieldTextColor(Color.BLACK)
                        .setFirstTextFieldHint("Enter New Name")
                        .setFirstButtonText("Make Changes")
                        .setFirstButtonColor(Color.LTGRAY)
                        .setFirstButtonTextColor(Color.BLACK)
                        .setSecondButtonText("Cancel")
                        .setSecondButtonColor(Color.CYAN)
                        .setSecondButtonTextColor(Color.BLACK)
                        .withFirstButtonListner(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Map<String,Object> map = new HashMap<>();
                                Log.i("auth",auth.getUid());
                                map.put("name",String.valueOf(flatDialog1.getFirstTextField().toString()));
                                reference.child("email").setValue(flatDialog1.getFirstTextField().toString());
                                Toast.makeText(MyAccount.this, "Email Changed Successfully", Toast.LENGTH_SHORT).show();
                                flatDialog1.dismiss();
                            }
                        })
                        .withSecondButtonListner(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                flatDialog1.dismiss();
                            }
                        });

                flatDialog1.create();
                flatDialog1.show();
                modalBottomSheetDialog.dismiss();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + id);
        }
    }
}