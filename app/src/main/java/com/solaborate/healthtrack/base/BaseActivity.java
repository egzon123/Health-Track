package com.solaborate.healthtrack.base;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ec.easylibrary.AppManager;
import com.ec.easylibrary.dialog.confirm.ConfirmDialog;
import com.ec.easylibrary.utils.DateUtils;

import com.hannesdorfmann.mosby3.mvp.MvpActivity;
import com.solaborate.healthtrack.BaseApplication;
import com.solaborate.healthtrack.R;
import com.solaborate.healthtrack.presenters.BasePresenter;
import com.solaborate.healthtrack.views.BaseView;
import com.solaborate.healthtrack.views.MainView;

import butterknife.ButterKnife;

/**
 * <li>BaseActivity</li>
 * <li>All Activity Basic</li>
 * <p>
 * Created by wj on 2018/11/20
 */
public abstract class BaseActivity extends MvpActivity<BaseView, BasePresenter> implements BaseView {
    private static final String TAG = "BaseActivity";
    public Context mContext;
    private RelativeLayout mRlMain;
    /**
     * Log
     */
    public TextView mTvLogMessage;
    public LinearLayout mLogLayout;
    public ScrollView mScrollViewLog;


    /**
     * Device Name
     */
    public String mDeviceName = "";
    /**
     * Device Mac
     */
    public String mDeviceMac = "";
    /**
     * Global Screen Width Default 1080 px
     */
    public int mScreenWidth = 1080;
    /**
     * Global Screen Height Default 1920 px
     */
    public int mScreenHeight = 1920;
    /**
     * Handle Message What Code
     */
    public static final int HANDLER_MESSAGE = 101;

    /**
     * Importing Layout  Abstraction Method
     */
    public abstract int contentViewID();

    /**
     * Init  Abstraction Method
     */
    public abstract void initView();

    @NonNull
    @Override
    public BasePresenter createPresenter() {
        return new BasePresenter();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
        setBaseActivityLayout();
        initBaseActivity();

    }


    public void setBaseActivityLayout() {
        setContentView(R.layout.activity_base);
    }


    public void initBaseActivity() {
        mContext = BaseApplication.instance().getApplicationContext();
        mRlMain = findViewById(R.id.rlMain);
        if (contentViewID() != 0) {
            View layout = LayoutInflater.from(mContext).inflate(contentViewID(), null);
            layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mRlMain.addView(layout);
        }

        ButterKnife.bind(this);
        AppManager.instance().addActivity(this);

        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;

        presenter.initAnim();
        initView();

    }


    public void addLogInfo(String infomation) {
        presenter.addLogInfo(infomation);

    }

    @Override
    public void showLogInfo(String message) {
        if (mTvLogMessage != null) {
            mTvLogMessage.append(message);
            mScrollViewLog.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.instance().finishActivity(this);
    }


}
