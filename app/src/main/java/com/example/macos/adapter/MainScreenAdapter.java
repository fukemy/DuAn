package com.example.macos.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.macos.utilities.CustomFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macos on 6/16/16.
 */
public class MainScreenAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mFragmentList;
    private List<String> mFragmentTitleList;
    private CustomFragment currentlyFragment = null;
    private String currentlyTitle = "";
    private boolean IS_PERFORM_MULTI_DATA = false;

    public List<Fragment> getmFragmentList(){
        return mFragmentList;
    }

    public List<String> getmTitleList(){
        return mFragmentTitleList;
    }

    public MainScreenAdapter(FragmentManager fm) {
        super(fm);
        mFragmentList = new ArrayList<Fragment>();
        mFragmentTitleList = new ArrayList<String>();
    }

    public void setMultiInput(boolean b){
        IS_PERFORM_MULTI_DATA = b;
    }

    public boolean isMultiInput(){
        return IS_PERFORM_MULTI_DATA;
    }

    public CustomFragment getCurentlyFragment() {
        return currentlyFragment;
    }

    public String getCurrentlyTitle() {
        return currentlyTitle;
    }

    @Override
    public Fragment getItem(int position) {
        currentlyFragment = (CustomFragment) mFragmentList.get(position);
        //currentlyFragment.setNextPage(position == (mFragmentList.size() - 1)? false : true);
        return currentlyFragment;
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }


    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        currentlyTitle = mFragmentTitleList.get(position);
        return currentlyTitle;
    }
}
