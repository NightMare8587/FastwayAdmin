package com.consumers.fastwayadmin.NavFrags;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.consumers.fastwayadmin.NavFrags.VendorTabs.BankVendor;
import com.consumers.fastwayadmin.NavFrags.VendorTabs.UPIvendor;

class MyAdapter extends FragmentPagerAdapter {
    Context context;
    int totalTabs;
    public MyAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                BankVendor bankVendor = new BankVendor();
                return bankVendor;
            case 1:
                UPIvendor upIvendor = new UPIvendor();
                return upIvendor;
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return totalTabs;
    }
}
