package com.consumers.fastwayadmin.Tables;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.consumers.fastwayadmin.Chat.chat;
import com.consumers.fastwayadmin.Chat.chatAdapter;
import com.consumers.fastwayadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.itextpdf.text.pdf.parser.Line;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChatWithCustomer extends AppCompatActivity {
    DatabaseReference reference;
    FirebaseAuth auth;
    String id;
    Uri photoURI;
    FirebaseStorage storage;
    StorageReference storageReference;
    boolean containsBad = false;
    Uri filepath;
    String restaurantName;
    Button imageSend;
    String URL = "https://fcm.googleapis.com/fcm/send";
    RecyclerView recyclerView;
    List<String> message = new ArrayList<>();
    List<String> typeOfMessage = new ArrayList<>();
    List<String> time = new ArrayList<>();
    SharedPreferences sharedPreferences;
    List<String> badWords = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    DatabaseReference resName;
    List<String> leftOrRight = new ArrayList<>();
    EditText editText;
    Button sendME,endConversation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_with_customer);
        auth = FirebaseAuth.getInstance();
        addBadWords();
        imageSend = findViewById(R.id.sendImageChatWithCustomer);
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        editText = findViewById(R.id.sendMessageEditText);
        sendME = findViewById(R.id.sendMessageButton);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        endConversation = findViewById(R.id.endConversationWithUSer);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
        recyclerView = findViewById(R.id.messageRecyclerView);
         id = getIntent().getStringExtra("id");
        resName = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(auth.getUid());
        resName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                if(snapshot.exists())
                    restaurantName = String.valueOf(snapshot.child("name").getValue());
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        endConversation.setOnClickListener(click -> {
            reference.child("messages").child(id).removeValue();
            finish();
        });
        reference.child("messages").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    message.clear();
                    time.clear();
                    typeOfMessage.clear();
                    leftOrRight.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        message.add(String.valueOf(dataSnapshot.child("message").getValue()));
                        time.add(String.valueOf(dataSnapshot.child("time").getValue()));
                        typeOfMessage.add(String.valueOf(dataSnapshot.child("typeOfMessage").getValue()));
                        leftOrRight.add(String.valueOf(dataSnapshot.child("id").getValue()));
                    }
                    if(message.size() != 0)
                        endConversation.setVisibility(View.VISIBLE);
                    else
                        endConversation.setVisibility(View.INVISIBLE);
                    linearLayoutManager.setStackFromEnd(true);
                    recyclerView.setLayoutManager(linearLayoutManager);
//                    recyclerView.smoothScrollToPosition(message.size()-1);
                    recyclerView.setAdapter(new chatAdapter(message,time,leftOrRight,typeOfMessage));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        imageSend.setOnClickListener(click -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ChatWithCustomer.this);
            builder.setTitle("Choose one option").setMessage("Choose one option from below").setPositiveButton("Open Gallery", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent();
                    dialog.dismiss();
                    intent.setType("image/*");
                    intent.setAction("android.intent.action.PICK");
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                }
            }).setNegativeButton("Open Camera", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    String file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/" + Calendar.getInstance().getTimeInMillis() + ".jpg";
                    File file1 = new File(file);
                    photoURI = FileProvider.getUriForFile(ChatWithCustomer.this, getApplicationContext().getPackageName() + ".provider", file1);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(intent, 11);
                }
            }).setNeutralButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        });

        reference.child("messages").child(id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateChat();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateChat();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                updateChat();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendME.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText.getText().toString().length() == 0){
                    Toast.makeText(ChatWithCustomer.this, "Enter Some Text", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    containsBad = false;
                    String text = editText.getText().toString().trim().toLowerCase();
                    for (int i = 0; i < badWords.size(); i++) {
                        if (text.contains(badWords.get(i).toLowerCase()))
                            containsBad = true;
                    }


                    if (!containsBad) {
                        RequestQueue requestQueue = Volley.newRequestQueue(ChatWithCustomer.this);
                        JSONObject main = new JSONObject();
                        try {
                            main.put("to", "/topics/" + id + "");
                            JSONObject notification = new JSONObject();
                            notification.put("title", "Restaurant Owner" + "(" + restaurantName + ")");
                            notification.put("body", "" + editText.getText().toString().trim());
                            main.put("notification", notification);

                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getApplicationContext(), error.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                                }
                            }) {
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String> header = new HashMap<>();
                                    header.put("content-type", "application/json");
                                    header.put("authorization", "key=AAAAjq_WsHs:APA91bGZV-uH-NJddxIniy8h1tDGDHqxhgvFdyNRDaV_raxjvSM_FkKu7JSwtSp4Q_iSmPuTKGGIB2M_07c9rKgPXUH43-RzpK6zkaSaIaNgmeiwUO40rYxYUZAkKoLAQQVeVJ7mXboD");
                                    return header;
                                }
                            };

                            requestQueue.add(jsonObjectRequest);
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                        }
                        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo", MODE_PRIVATE);
                        chat chat = new chat(editText.getText().toString(), auth.getUid() + "", System.currentTimeMillis() + "", "1", sharedPreferences.getString("name", ""),"message");
                        reference.child("messages").child(id).child(System.currentTimeMillis() + "").setValue(chat);
                        editText.setText("");
                        updateChat();
                    }else
                        Toast.makeText(ChatWithCustomer.this, "We don't allow to use bad words in our app", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addBadWords() {
        badWords.add("chutiya");
        badWords.add("gandu");
        badWords.add("lodu");
        badWords.add("kutta");
        badWords.add("kutti");
        badWords.add("saale");
        badWords.add("madarchod");
        badWords.add("bc");
        badWords.add("mc");
        badWords.add("bkl");
        badWords.add("randi");
        badWords.add("fuck");
        badWords.add("bitch");
        badWords.add("asshole");
        badWords.add("choda");
    }

    private void updateChat() {
        reference.child("messages").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    message.clear();
                    time.clear();
                    typeOfMessage.clear();
                    leftOrRight.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        message.add(String.valueOf(dataSnapshot.child("message").getValue()));
                        time.add(String.valueOf(dataSnapshot.child("time").getValue()));
                        leftOrRight.add(String.valueOf(dataSnapshot.child("id").getValue()));
                        typeOfMessage.add(String.valueOf(dataSnapshot.child("typeOfMessage").getValue()));
                    }
                    if(message.size() != 0)
                        endConversation.setVisibility(View.VISIBLE);
                    else
                        endConversation.setVisibility(View.INVISIBLE);
                    linearLayoutManager.setStackFromEnd(true);
                    recyclerView.setLayoutManager(linearLayoutManager);
//                    recyclerView.smoothScrollToPosition(message.size()-1);
                    recyclerView.setAdapter(new chatAdapter(message,time,leftOrRight,typeOfMessage));
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
        if(requestCode == 20 && resultCode == RESULT_OK && data != null){
            Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
        }else if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            filepath = data.getData();
            String times = System.currentTimeMillis() + "";
            try {
                FirebaseAuth dishAuth = FirebaseAuth.getInstance();
                StorageReference reference = storageReference.child(dishAuth.getUid() + "/" + "Chats" + "/" + times);
                reference.putFile(filepath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                        Toast.makeText(ChatWithCustomer.this, "Upload Complete", Toast.LENGTH_SHORT).show();
                        Toast.makeText(ChatWithCustomer.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                        StorageReference reference = storageReference.child(dishAuth.getUid() + "/" + "Chats" + "/"  + times);
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(@NonNull Uri uri) {
//                    TempClass tempClass = new TempClass(restaurantName,resId,name,email,issueName,details,uri+"",state,orderID,locality);
                                SharedPreferences sharedPreferences = getSharedPreferences("loginInfo", MODE_PRIVATE);
                                chat chat = new chat(uri + "", auth.getUid() + "", System.currentTimeMillis() + "", "1", sharedPreferences.getString("name", ""),"image");
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid());;
                                reference.child("messages").child(Objects.requireNonNull(id)).child(System.currentTimeMillis() + "").setValue(chat);
                                updateChat();
                            }
                        });
                    }
                }).addOnFailureListener(e -> Toast.makeText(ChatWithCustomer.this, "Failed. Try again later", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                Toast.makeText(ChatWithCustomer.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == 11 && resultCode == RESULT_OK){
            String times = System.currentTimeMillis() + "";
            Bitmap bmp = null;
            StorageReference childRef2 = storageReference.child(auth.getUid() + "/" + "Chats" + "/" + times);
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), photoURI);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] dataa = baos.toByteArray();

            UploadTask uploadTask2 = childRef2.putBytes(dataa);
            uploadTask2.addOnSuccessListener(taskSnapshot -> {

                Toast.makeText(ChatWithCustomer.this, "Upload successful", Toast.LENGTH_LONG).show();
                StorageReference reference = storageReference.child(auth.getUid() + "/" + "Chats" + "/"  + times);
                reference.getDownloadUrl().addOnSuccessListener(uri -> {
                    SharedPreferences preferences = getSharedPreferences("loginInfo", MODE_PRIVATE);
                    chat chat = new chat(uri + "", auth.getUid() + "", System.currentTimeMillis() + "", "1", preferences.getString("name", ""),"image");
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));;
                    reference1.child("messages").child(Objects.requireNonNull(id)).child(System.currentTimeMillis() + "").setValue(chat);
                    updateChat();
                });

            }).addOnFailureListener(e -> Toast.makeText(ChatWithCustomer.this, "Upload Failed -> " + e, Toast.LENGTH_LONG).show());
//            try {
//                FirebaseAuth dishAuth = FirebaseAuth.getInstance();
//                StorageReference reference = storageReference.child(dishAuth.getUid() + "/" + "Chats" + "/" + times);
//                reference.putFile(photoURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                        Toast.makeText(ChatWithOwner.this, "Upload Complete", Toast.LENGTH_SHORT).show();
//                        Toast.makeText(ChatWithOwner.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
//                        StorageReference reference = storageReference.child(dishAuth.getUid() + "/" + "Chats" + "/"  + times);
//                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                            @Override
//                            public void onSuccess(@NonNull Uri uri) {
////                    TempClass tempClass = new TempClass(restaurantName,resId,name,email,issueName,details,uri+"",state,orderID,locality);
//                                SharedPreferences preferences = getSharedPreferences("AccountInfo", MODE_PRIVATE);
//                                chat chat = new chat(uri + "", auth.getUid() + "", System.currentTimeMillis() + "", "0", preferences.getString("name", ""),"image");
//                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(id);;
//                                reference.child("messages").child(Objects.requireNonNull(auth.getUid())).child(System.currentTimeMillis() + "").setValue(chat);
//                                updateChat();
//                            }
//                        });
//                    }
//                }).addOnFailureListener(e -> Toast.makeText(ChatWithOwner.this, "Failed. Try again later", Toast.LENGTH_SHORT).show());
//            } catch (Exception e) {
//                Toast.makeText(ChatWithOwner.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//            }
        }
    }

}