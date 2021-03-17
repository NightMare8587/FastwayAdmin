package com.example.fastwayadmin.DiscountCombo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.example.fastwayadmin.R;
import com.example.flatdialoglibrary.dialog.FlatDialog;

public class DiscountActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount);
        recyclerView = findViewById(R.id.discountActivityRecyclerView);

        FlatDialog flatDialog = new FlatDialog(DiscountActivity.this);
        flatDialog.setTitle("Choose One Option")
                .setTitleColor(Color.BLACK)
                .setBackgroundColor(Color.parseColor("#f9fce1"))
                .setFirstButtonColor(Color.parseColor("#d3f6f3"))
                .setFirstButtonTextColor(Color.parseColor("#000000"))
                .setFirstButtonText("DISCOUNT ON ALL DISH")
                .setSecondButtonColor(Color.parseColor("#fee9b2"))
                .setSecondButtonTextColor(Color.parseColor("#000000"))
                .setSecondButtonText("LET ME CHOOSE")
                .withFirstButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FlatDialog flatDialog1 = new FlatDialog(DiscountActivity.this);
                        flatDialog1.setTitle("Choose One Option")
                                .setTitleColor(Color.BLACK)
                                .setBackgroundColor(Color.parseColor("#f9fce1"))
                                .setFirstButtonColor(Color.parseColor("#d3f6f3"))
                                .setFirstButtonTextColor(Color.parseColor("#000000"))
                                .setFirstButtonText("50% OFF ON ALL")
                                .setSecondButtonColor(Color.parseColor("#fee9b2"))
                                .setSecondButtonTextColor(Color.parseColor("#000000"))
                                .setSecondButtonText("40% OFF ON ALL")
                                .setThirdButtonText("30% OFF ON ALL")
                                .setThirdButtonColor(Color.parseColor("#fbd1b7"))
                                .setThirdButtonTextColor(Color.parseColor("#000000"))
                                .setFirstTextFieldHint("Enter How much discount!!")
                                .setFirstTextFieldBorderColor(Color.BLACK)
                                .setFirstTextFieldHintColor(Color.BLACK)
                                .withFirstButtonListner(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        flatDialog1.dismiss();
                                    }
                                })
                                .withSecondButtonListner(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        flatDialog1.dismiss();
                                    }
                                }).show();
                        flatDialog.dismiss();
                    }
                })
                .withSecondButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        flatDialog.dismiss();
                    }
                }).show();
    }
}