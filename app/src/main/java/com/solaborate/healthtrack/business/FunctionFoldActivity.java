package com.solaborate.healthtrack.business;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;



import com.solaborate.healthtrack.R;
import com.solaborate.healthtrack.base.BaseActivity;

public abstract class FunctionFoldActivity extends BaseActivity {

//    public CollapsingToolbarLayout mToolbarLayout;
    private AppBarLayout mAppBar;
    private TextView mTvTitle;
    @Override
    public void setBaseActivityLayout() {
        setContentView(R.layout.activity_fold);
    }
    
    @Override
    public void initBaseActivity() {
        super.initBaseActivity();
        mAppBar = findViewById(R.id.appBar);
        mTvTitle = findViewById(R.id.tvTitle);

    }


}
