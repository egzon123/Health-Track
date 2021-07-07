package com.solaborate.healthtrack.activities;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ec.easylibrary.dialog.loadingdialog.LoadingDialog;
import com.hannesdorfmann.mosby3.mvp.MvpActivity;
import com.ihealth.communication.manager.iHealthDevicesManager;

import com.solaborate.healthtrack.R;
import com.solaborate.healthtrack.adapter.ListScanDeviceAdapter;
import com.solaborate.healthtrack.presenters.ScanPresenter;
import com.solaborate.healthtrack.views.ScanView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScanActivity extends MvpActivity<ScanView, ScanPresenter> implements ScanView {
    private static final String TAG = "ScanActivity";

    @BindView(R.id.tvListTitle)
    TextView mTvListTitle;
    @BindView(R.id.NbList)
    ListView mNbListView;
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
                System.out.println("INSIDE onClick = position:"+position+"---------------->>>>>>>>>>>>>>>");
                Log.d(TAG,"INSIDE onClick = position:"+position);
                presenter.connectDevice(position);

            }
        });
    }

    @Override
    public void loadingDialogDissmis() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unregisterCallBack();
    }

    @OnClick(R.id.btnDiscovery)
    public void onViewClicked() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
        } else if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Turn bluetooth on", Toast.LENGTH_SHORT).show();
        } else {
            mLoadingDialog.show();
            mTvListTitle.setVisibility(View.VISIBLE);
            presenter.startDiscovery();
        }
    }
    @Override
    public void loadingDialogShow() {
        mLoadingDialog.show();
    }

    @Override
    public void showFunctionActivity(String mac, String type) {
        Intent intent = new Intent(ScanActivity.this, BP5Activity.class);
        intent.putExtra("mac", mac);
        intent.putExtra("type", type);
        startActivity(intent);
    }


}
