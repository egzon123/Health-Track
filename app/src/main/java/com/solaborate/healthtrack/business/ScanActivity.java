package com.solaborate.healthtrack.business;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ec.easylibrary.dialog.loadingdialog.LoadingDialog;
import com.ec.easylibrary.utils.ToastUtils;
import com.hannesdorfmann.mosby3.mvp.MvpActivity;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.ihealth.communication.control.BtmControl;
import com.ihealth.communication.control.HsProfile;
import com.ihealth.communication.manager.DiscoveryTypeEnum;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesManager;

import com.solaborate.healthtrack.BaseApplication;
import com.solaborate.healthtrack.adapter.ListScanDeviceAdapter;
import com.solaborate.healthtrack.business.device.BP5;
import com.solaborate.healthtrack.model.DeviceCharacteristic;
import com.solaborate.healthtrack.R;
import com.solaborate.healthtrack.presenters.ScanPresenter;
import com.solaborate.healthtrack.views.ScanView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScanActivity extends MvpActivity<ScanView, ScanPresenter> implements ScanView {
    private static final String TAG = "ScanActivity";

    @BindView(R.id.tvListTitle)
    TextView mTvListTitle;
    @BindView(R.id.NbList)
    ListView mNbListView;
    private int callbackId;
    private LoadingDialog mLoadingDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        ButterKnife.bind(this);
        initLoadingDialog();
        presenter.init(this);
    }

    @NonNull
    @Override
    public ScanPresenter createPresenter() {
        return new ScanPresenter();
    }

    public void initLoadingDialog() {

        mLoadingDialog = new LoadingDialog(this);
        mLoadingDialog.setCancellable(true);
    }

    @Override
    public void initScanListView(ListScanDeviceAdapter adapter) {
        mNbListView.setAdapter(adapter);
        mNbListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mLoadingDialog.show();
                presenter.connectDevice(position);

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iHealthDevicesManager.getInstance().unRegisterClientCallback(callbackId);
        BaseApplication.instance().logOut();

    }

    @OnClick(R.id.btnDiscovery)
    public void onViewClicked() {
        System.out.println("Inside btnDiscovery ------------------");
        mTvListTitle.setVisibility(View.VISIBLE);
        presenter.startDiscovery();

    }



    @Override
    public void loadingDialogDismis() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public void loadingDialogShow() {
        Log.d(TAG,"Loading dialog show ---------???????");
        mLoadingDialog.show();
    }

    @Override
    public void showFunctionActivity(String mac,String type) {
        Intent intent = new Intent();
        intent.putExtra("mac", mac);
        intent.putExtra("type", type);
        switch (type) {
            case iHealthDevicesManager.TYPE_BP5:
                intent.setClass(ScanActivity.this, BP5.class);
                break;
        }
        startActivity(intent);
    }


}
