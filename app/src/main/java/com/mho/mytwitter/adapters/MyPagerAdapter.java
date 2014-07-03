package com.mho.mytwitter.adapters;

import com.astuetz.PagerSlidingTabStrip.IconTabProvider;
import com.mho.mytwitter.R;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class MyPagerAdapter extends FragmentPagerAdapter implements IconTabProvider {

    private static final String[] CONTENT = { "Home", "Mentions"};
    private static final int[] ICONS = {
            R.drawable.ic_tab_home,
            R.drawable.ic_tab_mentions
    };

    private List<Fragment> mFragments;

    public MyPagerAdapter(FragmentManager fragmentManager, List<Fragment> fragments) {
        super(fragmentManager);
        this.mFragments = fragments;
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return this.mFragments.size();
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return CONTENT[position % CONTENT.length];
    }

    @Override
    public int getPageIconResId(int index) {
        return ICONS[index];
    }
}

