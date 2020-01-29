package com.solaborate.healthtrack;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {
    protected View mRootView;
    private Context mContext;

    // 导入布局文件
    public abstract int contentViewID();

    // 初始化控件
    public abstract void initView();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (contentViewID() != 0) {
            mRootView = LayoutInflater.from(mContext).inflate(contentViewID(), null);
            init();
        }
        return mRootView;
    }

    // 初始化UI
    private void init() {
        // ButterKnife bind
        ButterKnife.bind(this, mRootView);
        initView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
