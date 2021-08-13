package com.consumers.fastwayadmin.NavFrags;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.widget.EditText;

import com.consumers.fastwayadmin.R;
import com.google.android.material.tabs.TabLayout;

public class VendorDetailsActivity extends AppCompatActivity {
    String name,email;
    TabLayout tabLayout;
    ViewPager viewPager2;
    EditText nameEdit,emailEdit,phoneEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_details);
        initialise();
    }

    private void initialise() {
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        nameEdit = findViewById(R.id.nameVendorEditText);
        emailEdit = findViewById(R.id.emailVendorEditText);
        phoneEdit = findViewById(R.id.numberVendorEditText);

        nameEdit.setText(name);
        emailEdit.setText(email);
        tabLayout = findViewById(R.id.vendorTabLayout);
        viewPager2 = findViewById(R.id.vendorViewPager);
        tabLayout.addTab(tabLayout.newTab().setText("Bank"));
        tabLayout.addTab(tabLayout.newTab().setText("UPI"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final MyAdapter adapter = new MyAdapter(this,getSupportFragmentManager(),
                tabLayout.getTabCount());

        viewPager2.setAdapter(adapter);
        viewPager2.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }
}