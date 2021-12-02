package com.consumers.fastwayadmin.NavFrags;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.consumers.fastwayadmin.MenuActivities.AllMenuDish;
import com.consumers.fastwayadmin.MenuActivities.Combo.ComboMenuDish;
import com.consumers.fastwayadmin.R;
import com.developer.kalert.KAlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import karpuzoglu.enes.com.fastdialog.Animations;
import karpuzoglu.enes.com.fastdialog.FastDialog;
import karpuzoglu.enes.com.fastdialog.FastDialogBuilder;
import karpuzoglu.enes.com.fastdialog.Type;
import mehdi.sakout.fancybuttons.FancyButton;

public class MenuFrag extends Fragment {
    FancyButton mainCourse,breads,snacks,deserts,combo,drinks;
    Toolbar menuBar;
    SharedPreferences sharedPreferences;
    int count = 0;
    Button appliedOffers;
    boolean pressed = false;
    int total = 0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.menu_frag,container,false);
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
        menuBar = view.findViewById(R.id.menuFragBar);
        mainCourse = view.findViewById(R.id.MainCourse);
        breads = view.findViewById(R.id.Breads);
        appliedOffers = view.findViewById(R.id.removeAppliedOffers);
        sharedPreferences = view.getContext().getSharedPreferences("loginInfo",Context.MODE_PRIVATE);
        snacks = view.findViewById(R.id.Snacks);
        deserts = view.findViewById(R.id.Deserts);
        combo = view.findViewById(R.id.Combo);
        drinks = view.findViewById(R.id.Drinks);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(Objects.requireNonNull(auth.getUid()));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("Discount")){
                    appliedOffers.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        appliedOffers.setOnClickListener(click -> {
            KAlertDialog kAlertDialog = new KAlertDialog(requireContext(),KAlertDialog.ERROR_TYPE)
                    .setTitleText("Warning")
                    .setContentText("Do you sure wanna remove all offers?")
                    .setConfirmText("Yes").setConfirmClickListener(remove -> {
                        remove.dismissWithAnimation();
                        FastDialog fastDialog = new FastDialogBuilder(requireContext(), Type.PROGRESS)
                                .progressText("Removing..... please wait")
                                .setAnimation(Animations.FADE_IN)
                                .create();

                        fastDialog.show();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(auth.getUid()).child("List of Dish");
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                        if(dataSnapshot1.hasChild("Discount")){
                                            String beforeFull = String.valueOf(dataSnapshot1.child("Discount").child(Objects.requireNonNull(dataSnapshot1.getKey())).child("before").getValue());
                                            reference.child(Objects.requireNonNull(dataSnapshot.getKey())).child(dataSnapshot1.getKey()).child("full").setValue(beforeFull);

                                            if(dataSnapshot1.child("Discount").child(Objects.requireNonNull(dataSnapshot1.getKey())).hasChild("beforeHalf")){
                                                String beforeHalf = String.valueOf(dataSnapshot1.child("Discount").child(Objects.requireNonNull(dataSnapshot1.getKey())).child("beforeHalf").getValue());
                                                reference.child(dataSnapshot.getKey()).child(dataSnapshot1.getKey()).child("half").setValue(beforeHalf);
                                            }

                                            reference.child(dataSnapshot.getKey()).child(dataSnapshot1.getKey()).child("Discount").removeValue();
                                        }
                                    }
                                }
                                databaseReference.child("Discount").removeValue();
                                fastDialog.dismiss();
                                appliedOffers.setVisibility(View.INVISIBLE);
                                Toast.makeText(requireContext(), "Offers removed successfully", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }).setCancelText("No, Wait").setCancelClickListener(KAlertDialog::dismissWithAnimation);

            kAlertDialog.setCancelable(false);
            kAlertDialog.show();
        });
        mainCourse.setOnClickListener(view1 -> {
            SharedPreferences preferences = view1.getContext().getSharedPreferences("DishType", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("Type","Main Course");
            editor.putString("state",sharedPreferences.getString("state",""));
            editor.apply();
            Intent intent = new Intent(getActivity(), AllMenuDish.class);
            intent.putExtra("Dish","Main Course");
            startActivity(intent);
        });

        breads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = view.getContext().getSharedPreferences("DishType", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Type","Breads");
                editor.putString("state",sharedPreferences.getString("state",""));
                editor.apply();
                Intent intent = new Intent(getActivity(),AllMenuDish.class);
                intent.putExtra("Dish","Breads");
                startActivity(intent);
            }
        });

        snacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = view.getContext().getSharedPreferences("DishType", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Type","Snacks");
                editor.putString("state",sharedPreferences.getString("state",""));
                editor.apply();
                Intent intent = new Intent(getActivity(),AllMenuDish.class);
                intent.putExtra("Dish","Snacks");
                startActivity(intent);
            }
        });

        deserts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = view.getContext().getSharedPreferences("DishType", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Type","Deserts");
                editor.putString("state",sharedPreferences.getString("state",""));
                editor.apply();
                Intent intent = new Intent(getActivity(),AllMenuDish.class);
                intent.putExtra("Dish","Deserts");
                startActivity(intent);
            }
        });

        combo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = view.getContext().getSharedPreferences("DishType", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Type","Combo");
                editor.putString("state",sharedPreferences.getString("state",""));
                editor.apply();
                Intent intent = new Intent(getActivity(), ComboMenuDish.class);
                intent.putExtra("Dish","Combo");
                intent.putExtra("state",sharedPreferences.getString("state",""));
                startActivity(intent);
            }
        });

        drinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = view.getContext().getSharedPreferences("DishType", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Type","Drinks");
                editor.putString("state",sharedPreferences.getString("state",""));
                editor.apply();
                Intent intent = new Intent(getActivity(),AllMenuDish.class);
                intent.putExtra("Dish","Drinks");
                startActivity(intent);
            }
        });
    }
}
