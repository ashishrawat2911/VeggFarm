package com.veggfarm.android.adaptor;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.veggfarm.android.R;
import com.veggfarm.android.activities.MainCategoryActivity;
import com.veggfarm.android.fragments.FruitsCategoryFragment;
import com.veggfarm.android.fragments.VegetableCategoryFragment;

/**
 * created by Ashish Rawat
 */

public class ViewPagerCategoryAdaptor extends FragmentPagerAdapter {

    private MainCategoryActivity mContext;

    public ViewPagerCategoryAdaptor(FragmentManager fm, MainCategoryActivity context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new VegetableCategoryFragment();
            case 1:
                return new FruitsCategoryFragment();

        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {

            case 0:
                return mContext.getResources().getString(R.string.vegetable);
            case 1:
                return mContext.getResources().getString(R.string.fruits);


        }
        return null;
    }
}
