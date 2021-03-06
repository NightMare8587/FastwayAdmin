package com.example.fastwayadmin.NavFrags;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.fastwayadmin.MenuActivities.AllMenuDish;
import com.example.fastwayadmin.R;

import mehdi.sakout.fancybuttons.FancyButton;

public class MenuFrag extends Fragment {
    FancyButton mainCourse,breads,snacks,deserts,sweets,drinks;
    Toolbar menuBar;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.menu_frag,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        menuBar = view.findViewById(R.id.menuFragBar);
        mainCourse = view.findViewById(R.id.MainCourse);
        breads = view.findViewById(R.id.Breads);
        snacks = view.findViewById(R.id.Snacks);
        deserts = view.findViewById(R.id.Deserts);
        sweets = view.findViewById(R.id.Sweets);
        drinks = view.findViewById(R.id.Drinks);

        mainCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = view.getContext().getSharedPreferences("DishType", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Type","Main Course");
                editor.apply();
                Intent intent = new Intent(getActivity(),AllMenuDish.class);
                intent.putExtra("Dish","Main Course");
                startActivity(intent);
            }
        });

        breads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = view.getContext().getSharedPreferences("DishType", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Type","Breads");
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
                editor.apply();
                Intent intent = new Intent(getActivity(),AllMenuDish.class);
                intent.putExtra("Dish","Deserts");
                startActivity(intent);
            }
        });

        sweets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = view.getContext().getSharedPreferences("DishType", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Type","Sweets");
                editor.apply();
                Intent intent = new Intent(getActivity(),AllMenuDish.class);
                intent.putExtra("Dish","Sweets");
                startActivity(intent);
            }
        });

        drinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = view.getContext().getSharedPreferences("DishType", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Type","Drinks");
                editor.apply();
                Intent intent = new Intent(getActivity(),AllMenuDish.class);
                intent.putExtra("Dish","Drinks");
                startActivity(intent);
            }
        });
    }
}
