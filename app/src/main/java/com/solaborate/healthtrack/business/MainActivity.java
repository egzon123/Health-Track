package com.solaborate.healthtrack.business;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;



import com.ihealth.communication.manager.iHealthDevicesManager;
import com.solaborate.healthtrack.BaseApplication;
import com.solaborate.healthtrack.R;
import com.solaborate.healthtrack.base.BaseFragmentActivity;
import com.solaborate.healthtrack.business.device.BP5;
import com.solaborate.healthtrack.model.DeviceCharacteristic;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.ec.easylibrary.utils.ToastUtils;
import java.lang.reflect.Field;
import java.util.ArrayList;

import butterknife.BindView;
import io.reactivex.functions.Consumer;

public class
MainActivity extends BaseFragmentActivity {
    @BindView(R.id.flContent)
    FrameLayout mFlContent;
    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.tvDeviceInfo)
    TextView mTvDeviceInfo;
    @BindView(R.id.imgStatus)
    ImageView mImgStatus;

    private Context mContext;
    private RxPermissions permissions;


    //handler 中处理的四种状态
    public static final int HANDLER_SCAN = 101;
    public static final int HANDLER_CONNECTED = 102;
    public static final int HANDLER_DISCONNECT = 103;
    public static final int HANDLER_CONNECT_FAIL = 104;
    public static final int HANDLER_RECONNECT = 105;
    public static final int HANDLER_USER_STATUE = 106;

    public static final int FRAGMENT_CERTIFICATION = 0;
    public static final int FRAGMENT_CERTIFICATION_ERROR = 1;
    public static final int FRAGMENT_DEVICE_MAIN = 2;
    public static final int FRAGMENT_SCAN = 3;

    private int mCurrentFragment;

    private long mTimeKeyBackPressed = 0; // Back键按下时的系统时间

    //退出事件的超时时间
    //Setting this time can change the response time when you exit the application.
    private final static long TIMEOUT_EXIT = 2000;

    //Support device list
    public static ArrayList<DeviceCharacteristic> deviceStructList = new ArrayList<>();

    @Override
    public int contentViewID() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        init();
    }


    /**
     * 初始化
     */
    private void init() {
        mContext = this;
//        clearFragments();
        checkPermission();
        initDeviceInfo();
//        showCertificationFragment("", "");
        checkCertificaton();
        showScanFragment("BP5",null);
    }

    private void checkCertificaton() {
        Certification certification = new Certification();
        certification.checkIfPass();
    }


    /**
     * 初始化所有支持设备信息
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
    private void checkPermission() {
        permissions = new RxPermissions(this);
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
                            ToastUtils.showToast(MainActivity.this, "请打开相关权限，否则会影响功能的使用");
                        } else {
                            ToastUtils.showToast(MainActivity.this, "请打开相关权限，否则会影响功能的使用");
                        }
                    }
                });
        System.out.println("===>>> Inside checkPermisson");
    }

    /**
     * 切换到认证页面
     * Switch to Authentication Fragment
     *
     * @param param1
     * @param param2
     */
//    public void showCertificationFragment(String param1, String param2) {
//        mCurrentFragment = FRAGMENT_CERTIFICATION;
//        Fragment fragment = CertificationFragment.newInstance(param1, param2);
//        addFragment(R.id.flContent, fragment, CertificationFragment.class.getSimpleName());
//        setTitle(mContext.getString(R.string.main_title_authorization));
//        mTvDeviceInfo.setVisibility(View.INVISIBLE);
//        mImgStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable.activity_main_icon_status_normal));
//    }

    /**
     * 切换到认证失败log展示页面
     * Switch to LogFragment
     *
     * @param param1
     * @param param2
     */
//    public void showCertificationLogFragment(String param1, String param2) {
//        mCurrentFragment = FRAGMENT_CERTIFICATION_ERROR;
//        Fragment fragment = LogFragment.newInstance(param1, param2);
//        addFragment(R.id.flContent, fragment, LogFragment.class.getSimpleName());
//        setTitle(mContext.getString(R.string.main_title_authorization));
//        setDeviceInfo(mContext.getString(R.string.main_tip_author_fail));
//        mTvDeviceInfo.setVisibility(View.VISIBLE);
//        mImgStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable.activity_main_icon_status_1_error));
//    }

    /**
     * 切换到显示所有设备到主页面
     * Switch to DevicesFragment
     *
     * @param param1
     * @param param2
     */
//    public void showDevicesFragment(String param1, String param2) {
//        mCurrentFragment = FRAGMENT_DEVICE_MAIN;
//        Fragment fragment = DevicesFragment.newInstance(param1, param2);
//        addFragment(R.id.flContent, fragment, DevicesFragment.class.getSimpleName());
//        setTitle(mContext.getString(R.string.main_title_connect));
//        mTvDeviceInfo.setVisibility(View.VISIBLE);
//        mTvDeviceInfo.setText(mContext.getString(R.string.main_tip_select_device, ""));
//        mImgStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable.activity_main_icon_status_1_ok));
//    }

    /**
     * 切换到搜索页面
     * Switch to showScanFragment
     *
     * @param param1
     * @param param2
     */
    public void showScanFragment(String param1, String param2) {
        mCurrentFragment = FRAGMENT_SCAN;
        Fragment fragment = ScanFragment.newInstance(param1, param2);
        addFragment(R.id.flContent, fragment, ScanFragment.class.getSimpleName());
        setTitle("Connected Device");
        mTvDeviceInfo.setVisibility(View.VISIBLE);
        mTvDeviceInfo.setText("Selected Device "+ param1);
    }

    /**
     * 跳转到设备功能页
     * Switch to showFunctionActivity
     *
     * @param mac
     * @param type
     */
    public void showFunctionActivity(String mac, String type) {

        Intent intent = new Intent();
        intent.putExtra("mac", mac);
        intent.putExtra("type", type);
        switch (type) {
            case iHealthDevicesManager.TYPE_BP5:
                intent.setClass(MainActivity.this, BP5.class);
                break;


        }


        startActivity(intent);
    }

    /**
     * 设置主页面到标题
     *
     * @param title
     */
    public void setTitle(String title) {
        mTvTitle.setText(title);
    }

    /**
     * 设置设备连接信息
     *
     * @param deviceInfo
     */
    public void setDeviceInfo(String deviceInfo) {
        mTvDeviceInfo.setText(deviceInfo);
    }


//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        if (KeyEvent.KEYCODE_BACK == keyCode) {
//            //如果当前在认证错误的页面 则直接返回 最开始的页面重新取认证
//            if (mCurrentFragment == FRAGMENT_CERTIFICATION_ERROR) {
//                showCertificationFragment("", "");
//            } else if (mCurrentFragment == FRAGMENT_SCAN) {//如果当前在搜索页面 则直接返回 设备主页面
//                showDevicesFragment("", "");
//            } else {//点击两次返回退出
//                long currTime = System.currentTimeMillis();
//                if (currTime - mTimeKeyBackPressed > TIMEOUT_EXIT) {
//                    ToastUtils.showToast(this, R.string.exit_warning);
//                    mTimeKeyBackPressed = currTime;
//                } else {
//                    ToastUtils.stopToast();
//                    finish();
//                    System.exit(0);
//                }
//            }
//
//
//            return true;
//        }
//        return super.onKeyUp(keyCode, event);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseApplication.instance().logOut();
    }
}
