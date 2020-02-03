package com.solaborate.healthtrack.presenters;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ec.easylibrary.utils.ToastUtils;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.ihealth.communication.control.Bp5Control;
import com.ihealth.communication.control.BpProfile;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.solaborate.healthtrack.views.Bp5View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Bp5Presenter extends MvpBasePresenter<Bp5View> {
    String mDeviceMac;
    String mDeviceName;

    private Context mContext;
    private static final String TAG = "BP5Activity";
    private Bp5Control mBp5Control;
    private int mClientCallbackId;

    public void init(Context context, String mDeviceMac, String mDeviceName) {
        mContext = context;
        this.mDeviceMac = mDeviceMac;
        this.mDeviceName = mDeviceName;
        /* register ihealthDevicesCallback id */
        mClientCallbackId = iHealthDevicesManager.getInstance().registerClientCallback(miHealthDevicesCallback);
        /* Limited wants to receive notification specified device */
        iHealthDevicesManager.getInstance().addCallbackFilterForDeviceType(mClientCallbackId, iHealthDevicesManager.TYPE_BP5);
        /* Get bp5 controller */
        mBp5Control = iHealthDevicesManager.getInstance().getBp5Control(this.mDeviceMac);

        mBp5Control.getBattery();
    }

    private iHealthDevicesCallback miHealthDevicesCallback = new iHealthDevicesCallback() {

        @Override
        public void onDeviceConnectionStateChange(String mac, String deviceType, int status, int errorID) {
            Log.i(TAG, "mac: " + mac);
            Log.i(TAG, "deviceType: " + deviceType);
            Log.i(TAG, "status: " + status);
            if (status == iHealthDevicesManager.DEVICE_STATE_DISCONNECTED) {

              //  ToastUtils.showToast(mContext, "The device has been disconnected.");
                ifViewAttached(view -> {
                view.finishActivity();
                });

            }
        }
        @Override
        public void onDeviceNotify(String mac, String deviceType, String action, String message) {
            Log.i(TAG, "mac: " + mac);
            Log.i(TAG, "deviceType: " + deviceType);
            Log.i(TAG, "action: " + action);
            Log.i(TAG, "message: " + message);

            if (BpProfile.ACTION_BATTERY_BP.equals(action)) {
                try {
                    JSONObject info = new JSONObject(message);
                    String battery = info.getString(BpProfile.BATTERY_BP);
                    Log.d(TAG,"battery :"+battery);
                    ifViewAttached(view -> {
                        view.showBattery(battery);
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (BpProfile.ACTION_ERROR_BP.equals(action)) {
                try {
                    JSONObject info = new JSONObject(message);
                    String num = info.getString(BpProfile.ERROR_NUM_BP);
                    Log.d(TAG,"ERROR num:"+num);
                    int errorNum = Integer.parseInt(num);
                    if(errorNum>=0 && errorNum<=2){
                        Toast.makeText(mContext, "Try again without moving.", Toast.LENGTH_SHORT).show();
                    }else if(errorNum==3 || errorNum==4){
                        Toast.makeText(mContext, "Attach the cuff correctly and try again.", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(mContext, "Wait 5 minutes and try again.", Toast.LENGTH_SHORT);
                    }

                    Toast.makeText(mContext, "Place your arm in the Device", Toast.LENGTH_SHORT).show();
                    mBp5Control.interruptMeasure();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }  else if (BpProfile.ACTION_ONLINE_PRESSURE_BP.equals(action)) {
                try {
                    JSONObject info = new JSONObject(message);
                    String pressure = info.getString(BpProfile.BLOOD_PRESSURE_BP);
                    Log.d("BP5Activity", "pressure =================================>>>>>>>>>>>>>>>>= " + pressure);
                    ifViewAttached(view -> {
                        view.showPressure(pressure);
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (BpProfile.ACTION_ONLINE_PULSEWAVE_BP.equals(action)) {
                try {
                    JSONObject info = new JSONObject(message);
                    String pressure = info.getString(BpProfile.BLOOD_PRESSURE_BP);
                    String wave = info.getString(BpProfile.PULSEWAVE_BP);
                    String heartbeat = info.getString(BpProfile.FLAG_HEARTBEAT_BP);
                    ifViewAttached(view -> {
                        view.showPressure(pressure);
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (BpProfile.ACTION_ONLINE_RESULT_BP.equals(action)) {
                try {
                    JSONObject info = new JSONObject(message);
                    String highPressure = info.getString(BpProfile.HIGH_BLOOD_PRESSURE_BP);
                    String lowPressure = info.getString(BpProfile.LOW_BLOOD_PRESSURE_BP);
                    String pulse = info.getString(BpProfile.PULSE_BP);
                 Log.d(TAG,"highPressure: " + highPressure
                            + " lowPressure: " + lowPressure

                            + " pulse: " + pulse);
                 ifViewAttached(view -> {
                     view.showHighPressure(highPressure);
                     view.showLowPressure(lowPressure);
                     view.showPulse(pulse);
                 });
                 mBp5Control.interruptMeasure();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    };

    public void disconnect() {
        if (mBp5Control != null) {
            mBp5Control.disconnect();
        }
        iHealthDevicesManager.getInstance().unRegisterClientCallback(mClientCallbackId);
    }

    public Bp5Control getmBp5Control(){
        return mBp5Control;
    }


}
