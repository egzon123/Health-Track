package com.solaborate.healthtrack.presenters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;

import com.ec.easylibrary.dialog.loadingdialog.LoadingDialog;
import com.ec.easylibrary.utils.ToastUtils;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.ihealth.communication.control.BtmControl;
import com.ihealth.communication.control.HsProfile;
import com.ihealth.communication.manager.DiscoveryTypeEnum;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.solaborate.healthtrack.BaseApplication;
import com.solaborate.healthtrack.adapter.ListScanDeviceAdapter;
import com.solaborate.healthtrack.business.Certification;
import com.solaborate.healthtrack.business.device.BP5;
import com.solaborate.healthtrack.model.DeviceCharacteristic;
import com.solaborate.healthtrack.views.ScanView;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;

public class ScanPresenter extends MvpBasePresenter<ScanView> {
    private Context mContext;
    private RxPermissions permissions;
    private static final String TAG = "ScanPresenter";

    /** Animation */
    private TranslateAnimation mShowAction;
    private TranslateAnimation mHiddenAction;
    //handler 中处理的四种状态
    public static final int HANDLER_SCAN = 101;
    public static final int HANDLER_CONNECTED = 102;
    public static final int HANDLER_DISCONNECT = 103;
    public static final int HANDLER_CONNECT_FAIL = 104;
    public static final int HANDLER_RECONNECT = 105;
    public static final int HANDLER_USER_STATUE = 106;

    //Support device list
    public static ArrayList<DeviceCharacteristic> deviceStructList = new ArrayList<>();

    //退出事件的超时时间
    //Setting this time can change the response time when you exit the application.
    private final static long TIMEOUT_EXIT = 2000;


    // TODO: Rename and change types of parameters
    private String mDeviceName = "BP5";
    private String mParam2;
    private int callbackId;
    private List<DeviceCharacteristic> list_ScanDevices = new ArrayList<>();
    private ListScanDeviceAdapter mAdapter;


    /**
     *
     */
    public void init(Context context) {
        mContext = BaseApplication.instance().getApplicationContext();
        checkPermission(context);
        initDeviceInfo();
        if(checkCertificaton()){
            registerCallBackId();
        }
    }

    private boolean checkCertificaton() {
        Certification certification = new Certification();
      return certification.checkIfPass();
    }


    /**
     *
     * Initialize all support device information
     */
    private void initDeviceInfo() {
        Field[] fields = iHealthDevicesManager.class.getFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (fieldName.contains("DISCOVERY_")) {
                DeviceCharacteristic struct = new DeviceCharacteristic();
                struct.setDeviceName(fieldName.substring(10));
                try {
                    struct.setDeviceType(field.getLong(null));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                deviceStructList.add(struct);
            }
        }
    }

    /**
     * 检查权限
     * check Permission
     */
    @SuppressLint("CheckResult")
    private void checkPermission(Context context) {
        permissions = new RxPermissions((Activity) context);
        permissions.requestEach(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        )
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) {
                        if (permission.granted) {

                        } else if (permission.shouldShowRequestPermissionRationale) {
//                            ToastUtils.showToast(MainActivity.this, "请打开相关权限，否则会影响功能的使用");
                        } else {
//                            ToastUtils.showToast(MainActivity.this, "请打开相关权限，否则会影响功能的使用");
                        }
                    }
                });
        System.out.println("===>>> Inside checkPermisson");
    }

    public void registerCallBackId() {

        /*
         * Register callback to the manager. This method will return a callback Id.
         */

        callbackId = iHealthDevicesManager.getInstance().registerClientCallback(miHealthDevicesCallback);

        mAdapter = new ListScanDeviceAdapter(mContext, list_ScanDevices);

        ifViewAttached(view -> {
            view.initScanListView(mAdapter);
        });

    }

    private iHealthDevicesCallback miHealthDevicesCallback = new iHealthDevicesCallback() {

        @Override
        public void onScanDevice(String mac, String deviceType, int rssi, Map manufactorData) {
            Log.i(TAG, "onScanDevice - mac:" + mac + " - deviceType:" + deviceType + " - rssi:" + rssi + " - manufactorData:" + manufactorData);
            Bundle bundle = new Bundle();
            bundle.putString("mac", mac);
            bundle.putString("type", deviceType);
            bundle.putInt("rssi", rssi);

            Message msg = new Message();
            msg.what = HANDLER_SCAN;
            msg.setData(bundle);
            myHandler.sendMessage(msg);

            //Device additional information wireless MAC suffix table
            if (manufactorData != null) {
                Log.d(TAG, "onScanDevice mac suffix = " + manufactorData.get(HsProfile.SCALE_WIFI_MAC_SUFFIX));
            }

        }

        @Override
        public void onDeviceConnectionStateChange(String mac, String deviceType, int status, int errorID, Map manufactorData) {
            Log.e(TAG, "mac:" + mac + " deviceType:" + deviceType + " status:" + status + " errorid:" + errorID + " -manufactorData:" + manufactorData);
            Bundle bundle = new Bundle();
            bundle.putString("mac", mac);
            bundle.putString("type", deviceType);
            Message msg = new Message();

            if (status == iHealthDevicesManager.DEVICE_STATE_CONNECTED) {
                msg.what = HANDLER_CONNECTED;
            } else if (status == iHealthDevicesManager.DEVICE_STATE_DISCONNECTED) {
                msg.what = HANDLER_DISCONNECT;
            } else if (status == iHealthDevicesManager.DEVICE_STATE_CONNECTIONFAIL) {
                msg.what = HANDLER_CONNECT_FAIL;
            } else if (status == iHealthDevicesManager.DEVICE_STATE_RECONNECTING) {
                msg.what = HANDLER_RECONNECT;
            }
            msg.setData(bundle);
            myHandler.sendMessage(msg);
        }

        /**
         * Callback indicating an error happened during discovery.
         *
         * @param reason A string for the reason why discovery failed.
         */
        @Override
        public void onScanError(String reason, long latency) {
            Log.e(TAG, reason);
            Log.e(TAG, "please wait for " + latency + " ms");
            ifViewAttached(view -> {
                view.loadingDialogDismis();
            });

        }

        @Override
        public void onScanFinish() {
            super.onScanFinish();
            ifViewAttached(view -> {
                view.loadingDialogDismis();
            });
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_SCAN:
                    Bundle bundleScan = msg.getData();
                    String macScan = bundleScan.getString("mac");
                    String typeScan = bundleScan.getString("type");
                    int rssi = bundleScan.getInt("rssi");

                    DeviceCharacteristic device = new DeviceCharacteristic();

                    device.setDeviceName(typeScan);
                    device.setRssi(rssi);
                    device.setDeviceMac(macScan);
                    //ECG devices should deal with multiple USB connection duplicate display
                    if (mDeviceName.equals("ECGUSB")||mDeviceName.equals("ECG3USB")) {
                        for (int x = 0; x < list_ScanDevices.size(); x++) {
                            if (list_ScanDevices.get(x).getDeviceMac().equals(device.getDeviceMac())) {
                                list_ScanDevices.remove(x);
                                mAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }

                    list_ScanDevices.add(device);
                    mAdapter.setList(list_ScanDevices);
                    mAdapter.notifyDataSetChanged();
                    ifViewAttached(view -> {
                        view.loadingDialogDismis();
                    });

                    Log.d(TAG,"scan device : type:" + typeScan + " mac:" + macScan + " rssi:" + rssi);
                    break;

                case HANDLER_CONNECTED:
                    Bundle bundleConnect = msg.getData();
                    String macConnect = bundleConnect.getString("mac");
                    String typeConnect = bundleConnect.getString("type");
                    iHealthDevicesManager.getInstance().stopDiscovery();
                    ifViewAttached(view -> {
                        view.showFunctionActivity(macConnect,typeConnect);
                    });
                    ifViewAttached(view -> {
                        view.loadingDialogDismis();
                    });

                    ToastUtils.showToast(mContext,"The device is connected");
                    Log.d(TAG,"connected device : type:" + typeConnect + " mac:" + macConnect);
                    break;
                case HANDLER_DISCONNECT:
                    ifViewAttached(view -> {
                        view.loadingDialogDismis();
                    });

                    ToastUtils.showToast(mContext,"The device has been disconnected");
//                    showLog(mContext.getString(R.string.connect_main_tip_disconnect));
                    break;
                case HANDLER_CONNECT_FAIL:
//                    ToastUtils.showToast(mContext, mContext.getString(R.string.connect_main_tip_connect_fail));
                    break;
                case HANDLER_RECONNECT:
                    ifViewAttached(view -> {
                        view.loadingDialogShow();
                    });

//                    ToastUtils.showToast(mContext, mContext.getString(R.string.connect_main_tip_reconnect));
                    break;
                case HANDLER_USER_STATUE:
                    break;

                default:
                    break;
            }
        }
    };

    private void ConnectDevice(String type, String mac, String userName) {
        boolean req;
        if (type.equals(iHealthDevicesManager.TYPE_FDIR_V3)) {
            req = iHealthDevicesManager.getInstance().connectTherm(userName, mac, type,
                    BtmControl.TEMPERATURE_UNIT_C, BtmControl.MEASURING_TARGET_BODY,
                    BtmControl.FUNCTION_TARGET_OFFLINE, 0, 1, 0);
        } else {
            req = iHealthDevicesManager.getInstance().connectDevice(userName, mac, type);
        }

        if (!req) {
            ToastUtils.showToast(mContext,"Haven't premissoin to connect this device or the mac is not valid");
            Log.d(TAG,"Haven't premissoin to connect this device or the mac is not valid");
        }
    }

    private DiscoveryTypeEnum getDiscoveryTypeEnum(String deviceName) {
        for (DiscoveryTypeEnum type : DiscoveryTypeEnum.values()) {
            if (deviceName.equals(type.name())) {
                return type;
            }
        }
        return null;
    }


    public void startDiscovery() {
        list_ScanDevices.clear();
        mAdapter.notifyDataSetChanged();
        ifViewAttached(view -> {
            view.loadingDialogShow();
        });
        iHealthDevicesManager.getInstance().startDiscovery(getDiscoveryTypeEnum(mDeviceName));
        Log.d(TAG,"startDiscovery() ---current device type:" + mDeviceName);
    }

    public void connectDevice(int position){
        DeviceCharacteristic deviceCharacteristic = list_ScanDevices.get(position);
        ConnectDevice(deviceCharacteristic.getDeviceName(), deviceCharacteristic.getDeviceMac(), "");
    }
}
