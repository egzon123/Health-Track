package com.solaborate.healthtrack.base;

import android.os.Bundle;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


public abstract class BaseFragmentActivity extends BaseActivity {
    protected FragmentManager mFragmentManager;
    protected Fragment mCurrFragment; // 前台的Fragment

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mFragmentManager = getSupportFragmentManager();
        super.onCreate(savedInstanceState);
    }

    public void addFragment(int framelayoutResId, Fragment fragment, String tag) {
        if (fragment != null && framelayoutResId != 0) {
            FragmentTransaction ft = mFragmentManager.beginTransaction();

            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            if (mCurrFragment != null) {
                ft.hide(mCurrFragment);
            }

            if (!fragment.isAdded()) {
                ft.add(framelayoutResId, fragment,
                        (tag == null)?fragment.getClass().getSimpleName():tag);
            } else {
                ft.show(fragment);
            }
            ft.commitAllowingStateLoss();

            mCurrFragment = fragment;
        }
    }

}