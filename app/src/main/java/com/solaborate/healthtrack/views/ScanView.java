package com.solaborate.healthtrack.views;

import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.solaborate.healthtrack.adapter.ListScanDeviceAdapter;

public interface ScanView extends MvpView {

    void initScanListView(ListScanDeviceAdapter adapter);

    void loadingDialogDissmis();

    void loadingDialogShow();

    void showFunctionActivity(String mac,String type);

}
