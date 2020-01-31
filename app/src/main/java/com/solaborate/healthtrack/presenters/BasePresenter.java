package com.solaborate.healthtrack.presenters;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.ec.easylibrary.AppManager;
import com.ec.easylibrary.utils.DateUtils;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.solaborate.healthtrack.BaseApplication;
import com.solaborate.healthtrack.R;
import com.solaborate.healthtrack.views.BaseView;

import butterknife.ButterKnife;

public class BasePresenter extends MvpBasePresenter<BaseView> {
    public Context mContext;
    /** Animation */
    private TranslateAnimation mShowAction;
    private TranslateAnimation mHiddenAction;

    /** Global Log Information */
    private String mLogInformation = "";



    public void initAnim() {
        mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -2.0f, Animation.RELATIVE_TO_SELF, -1.0f);
        mShowAction.setDuration(500);

        mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF,
                -2.0f);
        mHiddenAction.setDuration(500);
    }

    public void addLogInfo(String infomation) {

        if (infomation != null && !infomation.isEmpty()) {
           String  infor = DateUtils.getNow("yyyy-MM-dd HH:mm:ss.SSS") + ": " + infomation + " \n";
            mLogInformation += infor;
            ifViewAttached(view ->{
                    view.showLogInfo(infor);
            });
        }

    }

}
