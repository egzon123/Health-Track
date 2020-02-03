package com.solaborate.healthtrack.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.hannesdorfmann.mosby3.mvp.MvpActivity;
import com.solaborate.healthtrack.R;
import com.solaborate.healthtrack.presenters.Bp5Presenter;
import com.solaborate.healthtrack.views.Bp5View;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
public class BP5Activity extends MvpActivity<Bp5View, Bp5Presenter> implements Bp5View {
    @BindView(R.id.nrHigh)
    TextView highPressureTxt;
    @BindView(R.id.nrLow)
    TextView lowPressureTxt;
    @BindView(R.id.nrHartRate)
    TextView pulseTxt;
    @BindView(R.id.number_pressure)
    TextView number_pressure;
    @BindView(R.id.battery_nr)
    TextView battery_nr;
    @BindView(R.id.battery_linear)
    LinearLayout battery_linear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bp5);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        String mDeviceMac = intent.getStringExtra("mac");
        String mDeviceName = intent.getStringExtra("type");
        presenter.init(this, mDeviceMac, mDeviceName);
    }

    @Override
    protected void onDestroy() {
        presenter.disconnect();
        super.onDestroy();
    }

    @NonNull
    @Override
    public Bp5Presenter createPresenter() {
        return new Bp5Presenter();
    }


    @OnClick({R.id.btnDisconnect, R.id.btnMeasurement, R.id.btnStopMeasurement})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnDisconnect:
                presenter.getmBp5Control().disconnect();
                Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnMeasurement:
                clearResult();
                presenter.getmBp5Control().startMeasure();
                break;
            case R.id.btnStopMeasurement:
                presenter.getmBp5Control().interruptMeasure();
                break;
        }
    }

    @OnClick(R.id.btnMeasurement)
    public void start(){

        presenter.getmBp5Control().startMeasure();

    }


    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void showBattery(String battery) {
        battery_linear.setVisibility(View.VISIBLE);
        battery_nr.setText(battery + "%");
    }

    @Override
    public void showPressure(String pressure) {
        number_pressure.setText(pressure);
    }

    @Override
    public void showPulse(String pulse) {
        pulseTxt.setText(pulse);
    }

    @Override
    public void showHighPressure(String highPressure) {
        highPressureTxt.setText(highPressure);
    }

    @Override
    public void showLowPressure(String lowPressure) {
        lowPressureTxt.setText(lowPressure);
    }

    @Override
    public void clearResult() {
        highPressureTxt.setText("0");
        lowPressureTxt.setText("0");
        pulseTxt.setText("0");
    }
}
