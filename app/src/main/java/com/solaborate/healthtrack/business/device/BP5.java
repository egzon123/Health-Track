package com.solaborate.healthtrack.business.device;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ec.easylibrary.utils.ToastUtils;
import com.hannesdorfmann.mosby3.mvp.MvpActivity;
import com.ihealth.communication.control.Bp5Control;
import com.ihealth.communication.control.BpProfile;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.solaborate.healthtrack.R;
import com.solaborate.healthtrack.presenters.Bp5Presenter;
import com.solaborate.healthtrack.views.Bp5View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;


public class BP5 extends MvpActivity<Bp5View, Bp5Presenter> implements Bp5View{
    @BindView(R.id.nrHigh)
    TextView highPressureTxt;
    @BindView(R.id.nrLow)
    TextView lowPressureTxt;
    @BindView(R.id.nrHartRate)
    TextView hartRateTxt;
    @BindView(R.id.number_pressure)
    TextView number_pressure;
    @BindView(R.id.battery_nr)
    TextView battery_nr;
    @BindView(R.id.battery_linear)
    LinearLayout battery_linear;
    public static String battery_get;
    public static final int HANDLER_MESSAGE = 101;
    String mDeviceMac;
    String mDeviceName;

    //    Button mBtnOfflineMeasureEnable;
//    @BindView(R.id.btnOfflineMeasureDisable)
    Button mBtnOfflineMeasureDisable;
    private Context mContext;
    private static final String TAG = "BP5";
    private Bp5Control mBp5Control;
    private int mClientCallbackId;





    public void initView() {
        mContext = this;
        Intent intent = getIntent();
        mDeviceMac = intent.getStringExtra("mac");
        mDeviceName = intent.getStringExtra("type");
        /* register ihealthDevicesCallback id */
        mClientCallbackId = iHealthDevicesManager.getInstance().registerClientCallback(miHealthDevicesCallback);
        /* Limited wants to receive notification specified device */
        iHealthDevicesManager.getInstance().addCallbackFilterForDeviceType(mClientCallbackId, iHealthDevicesManager.TYPE_BP5);
        /* Get bp5 controller */
        mBp5Control = iHealthDevicesManager.getInstance().getBp5Control(mDeviceMac);

        mBp5Control.getBattery();
    }

    private iHealthDevicesCallback miHealthDevicesCallback = new iHealthDevicesCallback() {

        @Override
        public void onDeviceConnectionStateChange(String mac, String deviceType, int status, int errorID) {
            Log.i(TAG, "mac: " + mac);
            Log.i(TAG, "deviceType: " + deviceType);
            Log.i(TAG, "status: " + status);
            if (status == iHealthDevicesManager.DEVICE_STATE_DISCONNECTED) {
//                addLogInfo(mContext.getString(R.string.connect_main_tip_disconnect));
                ToastUtils.showToast(mContext, "The device has been disconnected.");
                finish();
            }
        }

        @Override
        public void onUserStatus(String username, int userStatus) {
            Log.i(TAG, "username: " + username);
            Log.i(TAG, "userState: " + userStatus);
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
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "battery: " + battery;
                    battery_linear.setVisibility(View.VISIBLE);
                    battery_nr.setText(battery+"%");
                    battery_get = battery;
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else if (BpProfile.ACTION_DISENABLE_OFFLINE_BP.equals(action)) {
                Log.i(TAG, "disable operation is success");
//                addLogInfo("disable operation is success");

            } else if (BpProfile.ACTION_ENABLE_OFFLINE_BP.equals(action)) {
                Log.i(TAG, "enable operation is success");
//                addLogInfo("enable operation is success");
            } else if (BpProfile.ACTION_ERROR_BP.equals(action)) {
                try {
                    JSONObject info = new JSONObject(message);
                    String num = info.getString(BpProfile.ERROR_NUM_BP);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "error num: " + num;
                    int errorNum = Integer.parseInt(num);
                    if(errorNum>=0 && errorNum<=2){
                        Toast.makeText(mContext, "Try again without moving.", Toast.LENGTH_SHORT).show();
                    }else if(errorNum==3 || errorNum==4){
                        Toast.makeText(mContext, "Attach the cuff correctly and try again.", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(mContext, "Wait 5 minutes and try again.", Toast.LENGTH_SHORT);
                    }

                    Toast.makeText(mContext, "Place your arme in the Device", Toast.LENGTH_SHORT).show();
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (BpProfile.ACTION_HISTORICAL_DATA_BP.equals(action)) {
                String str = "{}";
                try {
                    JSONObject info = new JSONObject(message);
                    if (info.has(BpProfile.HISTORICAL_DATA_BP)) {
                        JSONArray array = info.getJSONArray(BpProfile.HISTORICAL_DATA_BP);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String date = obj.getString(BpProfile.MEASUREMENT_DATE_BP);
                            String hightPressure = obj.getString(BpProfile.HIGH_BLOOD_PRESSURE_BP);
                            String lowPressure = obj.getString(BpProfile.LOW_BLOOD_PRESSURE_BP);
                            String pulseWave = obj.getString(BpProfile.PULSE_BP);
                            String ahr = obj.getString(BpProfile.MEASUREMENT_AHR_BP);
                            String hsd = obj.getString(BpProfile.MEASUREMENT_HSD_BP);
                            str = "date:" + date
                                    + "hightPressure:" + hightPressure + "\n"
                                    + "lowPressure:" + lowPressure + "\n"
                                    + "pulseWave" + pulseWave + "\n"
                                    + "ahr:" + ahr + "\n"
                                    + "hsd:" + hsd + "\n";

                            Message msg = new Message();
                            msg.what = HANDLER_MESSAGE;
                            msg.obj = str;
                            myHandler.sendMessage(msg);
                        }
                    } else {
                        Message msg = new Message();
                        msg.what = HANDLER_MESSAGE;
                        msg.obj = str;
                        myHandler.sendMessage(msg);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (BpProfile.ACTION_HISTORICAL_NUM_BP.equals(action)) {
                try {
                    JSONObject info = new JSONObject(message);
                    String num = info.getString(BpProfile.HISTORICAL_NUM_BP);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "num: " + num;
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (BpProfile.ACTION_IS_ENABLE_OFFLINE.equals(action)) {
                try {
                    JSONObject info = new JSONObject(message);
                    boolean isEnableoffline = info.getBoolean(BpProfile.IS_ENABLE_OFFLINE);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "isEnableoffline: " + isEnableoffline;
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (BpProfile.ACTION_ONLINE_PRESSURE_BP.equals(action)) {
                try {
                    JSONObject info = new JSONObject(message);
                    String pressure = info.getString(BpProfile.BLOOD_PRESSURE_BP);
//                    Message msg = new Message();
//                    msg.what = HANDLER_MESSAGE;
//                    msg.obj = "pressure: " + pressure;
                    Log.d("BP5", "pressure = " + pressure);
                    number_pressure.setText(pressure);
                    //   myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (BpProfile.ACTION_ONLINE_PULSEWAVE_BP.equals(action)) {
                try {
                    JSONObject info = new JSONObject(message);
                    String pressure = info.getString(BpProfile.BLOOD_PRESSURE_BP);
                    String wave = info.getString(BpProfile.PULSEWAVE_BP);
                    String heartbeat = info.getString(BpProfile.FLAG_HEARTBEAT_BP);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "pressure:" + pressure + "\n"
                            + "wave: " + wave + "\n"
                            + " - heartbeat:" + heartbeat;
                    number_pressure.setText(pressure);
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (BpProfile.ACTION_ONLINE_RESULT_BP.equals(action)) {
                try {
                    JSONObject info = new JSONObject(message);
                    String highPressure = info.getString(BpProfile.HIGH_BLOOD_PRESSURE_BP);
                    String lowPressure = info.getString(BpProfile.LOW_BLOOD_PRESSURE_BP);
                    String ahr = info.getString(BpProfile.MEASUREMENT_AHR_BP);
                    String pulse = info.getString(BpProfile.PULSE_BP);
                    Log.d("ErzenTest", " highPressure = " + highPressure);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "highPressure: " + highPressure
                            + " lowPressure: " + lowPressure
                            + " ahr: " + ahr
                            + " pulse: " + pulse;
                    highPressureTxt.setText(highPressure);
                    lowPressureTxt.setText(lowPressure);
                    hartRateTxt.setText(pulse);
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (BpProfile.ACTION_ZOREING_BP.equals(action)) {
                Message msg = new Message();
                msg.what = HANDLER_MESSAGE;
                msg.obj = "zoreing";
                myHandler.sendMessage(msg);

            } else if (BpProfile.ACTION_ZOREOVER_BP.equals(action)) {
                Message msg = new Message();
                msg.what = HANDLER_MESSAGE;
                msg.obj = "zoreover";
                myHandler.sendMessage(msg);

            }
        }
    };


    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_MESSAGE:

                    String output = (String) msg.obj;
                    System.out.println("Inside handler message ===>>> " + output);
                    break;
            }
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onDestroy() {
        if (mBp5Control != null) {
            mBp5Control.disconnect();
        }
        iHealthDevicesManager.getInstance().unRegisterClientCallback(mClientCallbackId);
        super.onDestroy();

    }

    @NonNull
    @Override
    public Bp5Presenter createPresenter() {
        return new Bp5Presenter();
    }


    @OnClick({R.id.btnDisconnect, R.id.btnMeasurement, R.id.btnStopMeasurement})
    public void onViewClicked(View view) {
        if (mBp5Control == null) {
//            addLogInfo("mBp5Control == null");
            return;
        }
//        showLogLayout();
        switch (view.getId()) {
            case R.id.btnDisconnect:
                mBp5Control.disconnect();

                Toast.makeText(mContext, "Disconnected", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnMeasurement:
                mBp5Control.startMeasure();
//                highPressure.setText();
//               addLogInfo("startMeasure()");
                break;
            case R.id.btnStopMeasurement:
                mBp5Control.interruptMeasure();
//                addLogInfo("interruptMeasure()");
                break;

        }
    }


}
