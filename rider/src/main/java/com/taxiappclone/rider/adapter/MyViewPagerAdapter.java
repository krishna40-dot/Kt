package com.taxiappclone.rider.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragmentList = new ArrayList<Fragment>();
        private int mCurrentPosition = -1;
        private final List<String> mFragmentTitleList = new ArrayList<>();
        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
            /*this.mFragmentList = new ArrayList<Fragment>();
            mFragmentList.add(new RechargeHistoryFragment());
            mFragmentTitleList.add("History");
            mFragmentList.add(new RechargeOffersFragment());
            mFragmentTitleList.add("Offers");*/
        }
        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }


        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }
