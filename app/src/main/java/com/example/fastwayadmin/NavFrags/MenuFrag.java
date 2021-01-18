package com.example.fastwayadmin.NavFrags;

import android.content.Intent;
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

public class MenuFrag extends Fragment {
    Button mainCourse,breads,snacks,deserts,sweets,drinks;
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
                Intent intent = new Intent(getActivity(),AllMenuDish.class);
                intent.putExtra("Dish","Main Course");
                startActivity(intent);
            }
        });

        breads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),AllMenuDish.class);
                intent.putExtra("Dish","Breads");
                startActivity(intent);
            }
        });

        snacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),AllMenuDish.class);
                intent.putExtra("Dish","Snacks");
                startActivity(intent);
            }
        });

        deserts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),AllMenuDish.class);
                intent.putExtra("Dish","Deserts");
                startActivity(intent);
            }
        });

        sweets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),AllMenuDish.class);
                intent.putExtra("Dish","Sweets");
                startActivity(intent);
            }
        });

        drinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),AllMenuDish.class);
                intent.putExtra("Dish","Drinks");
                startActivity(intent);
            }
        });
    }
}
