package com.solaborate.healthtrack.views;

import com.hannesdorfmann.mosby3.mvp.MvpView;

public interface Bp5View extends MvpView {
    void finishActivity();

    void showBattery(String battery);

    void showPressure(String pressure);

    void showPulse(String heartbeat);

    void showHighPressure(String highPressure);

    void showLowPressure(String lowPressure);
}
