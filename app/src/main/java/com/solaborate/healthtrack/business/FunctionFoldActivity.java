package com.solaborate.healthtrack.business;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;



import com.solaborate.healthtrack.R;
import com.solaborate.healthtrack.base.BaseActivity;

public abstract class FunctionFoldActivity extends BaseActivity {
    private RelativeLayout mRlMain;
    private CollapsingToolbarLayoutState state;
//    public CollapsingToolbarLayout mToolbarLayout;
    private AppBarLayout mAppBar;

    private ImageView mImgStatus;
    private TextView mTvDeviceInfo;
    private TextView mTvTitle;

    private enum CollapsingToolbarLayoutState {
        EXPANDED,
        COLLAPSED,
        INTERNEDIATE
    }

    @Override
    public void setBaseActivityLayout() {
        setContentView(R.layout.activity_fold);
    }
    
    @Override
    public void initBaseActivity() {
        super.initBaseActivity();

//        mToolbarLayout = findViewById(R.id.toolbarLayout);
        mAppBar = findViewById(R.id.appBar);
//        mImgStatus = findViewById(R.id.imgStatus);
//        mTvDeviceInfo = findViewById(R.id.tvDeviceInfo);
        mTvTitle = findViewById(R.id.tvTitle);


        mImgStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable.activity_main_icon_status_2_ok));
        mTvTitle.setText(mContext.getString(R.string.main_title_functions));

        mTvDeviceInfo.setPadding(0, 0, 0, 80);
//        mToolbarLayout.setTitle(mDeviceName + " " + mDeviceMac);

        //TODO if you want to get status and doing something of folding effect.you can use them
//        mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//
//                if (verticalOffset == 0) {
//                    if (state != CollapsingToolbarLayoutState.EXPANDED) {
//                        mToolbarLayout.setTitle(mDeviceName+" "+mDeviceMac);
//                        state = CollapsingToolbarLayoutState.EXPANDED;//修改状态标记为展开
//                    }
//                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
//                    if (state != CollapsingToolbarLayoutState.COLLAPSED) {
//                        mToolbarLayout.setTitle(mDeviceName+" "+mDeviceMac);
//                        state = CollapsingToolbarLayoutState.COLLAPSED;//修改状态标记为折叠
//                    }
//                } else {
//                    if (state != CollapsingToolbarLayoutState.INTERNEDIATE) {
//                        mToolbarLayout.setTitle(mDeviceName+" "+mDeviceMac);
//                        state = CollapsingToolbarLayoutState.INTERNEDIATE;//修改状态标记为中间
//                    }
//                }
//            }
//        });

    }


}
