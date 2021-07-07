package com.solaborate.healthtrack.business;

import android.app.Application;

import com.ec.easylibrary.utils.ToastUtils;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.solaborate.healthtrack.BaseApplication;

import java.io.IOException;
import java.io.InputStream;

public class Certification {

    public boolean isPassCertification(){
        boolean isPass = false;
        try {
            InputStream is = BaseApplication.instance().getAssets().open("com_solaborate_healthtrack_android.pem");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
             isPass = iHealthDevicesManager.getInstance().sdkAuthWithLicense(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
       return isPass;
    }

    public boolean checkIfPass(){
        if(isPassCertification() == false){
            ToastUtils.showToast(BaseApplication.instance(),"Certification not passed");
            return false;
        }else {
            System.out.println("--->> Certification passed <<---");
            return true;
        }
    }
}
