package com.example.adapter;

import com.example.oldbooksshop.CatalogueFragment;
import com.example.oldbooksshop.MyBooksFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
 
public class TabsPagerAdapter extends FragmentPagerAdapter {
 
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public Fragment getItem(int index) {
 
        switch (index) {
        case 0:
            // Catalogue fragment activity
            return new CatalogueFragment();
        case 1:
            // My books fragment activity
            return new MyBooksFragment();
        }
        
        return null;
    }
 
    @Override
    public int getCount() {
        
        return 2; //change this to match the number of tabs
    }
 
}