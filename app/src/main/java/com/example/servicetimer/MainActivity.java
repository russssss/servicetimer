package com.example.servicetimer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements CustomResultReceiver.Receiver {

    private TimerService timerService;
    private boolean mServiceBound = false;
    private boolean isPause;
    private CustomResultReceiver customResultReceiver;
    private TextView textTimer;
    private Button buttonTimer;
    private final String RECEIVER = "RECEIVER";
    private String RECEIVER_NAME = "RECEIVER_NAME";

    ///////////////////////////////////////////////////////////////////////////
    // Activity
    ///////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initListener();

        if (savedInstanceState != null) {
            customResultReceiver = savedInstanceState.getParcelable(RECEIVER);
        } else {
            customResultReceiver = new CustomResultReceiver(new Handler(Looper.getMainLooper()));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        customResultReceiver.setReceiver(this);

        Intent intent = new Intent(this, TimerService.class);
        intent.putExtra(Consts.RECEIVER_INTENT, customResultReceiver);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }

        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mServiceBound) {
            unbindService(mServiceConnection);
            mServiceBound = false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putParcelable(RECEIVER, customResultReceiver);
        super.onSaveInstanceState(bundle);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Methods
    ///////////////////////////////////////////////////////////////////////////

    private void setTime(int time) {
        String romanTime = DataConverter.intToRoman(time);
        textTimer.setText(romanTime);
    }

    private void stopTimer() {
        buttonTimer.setText(R.string.start_timer);
        timerService.pause();
    }

    private void startTimer() {
        buttonTimer.setText(R.string.stop_timer);
        timerService.play();
    }

    private void initListener() {
        buttonTimer.setOnClickListener(v -> {
            if (!isPause) {
                stopTimer();
                isPause = true;
            } else {
                startTimer();
                isPause = false;
            }
        });
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimerService.MyBinder myBinder = (TimerService.MyBinder) service;
            timerService = myBinder.getService();
            mServiceBound = true;
            setTime(timerService.getTime());
            buttonTimer.setText(timerService.isPause() ? R.string.start_timer : R.string.stop_timer);
            isPause = timerService.isPause();
        }
    };

    private void initView() {
        textTimer = findViewById(R.id.text_timer);
        buttonTimer = findViewById(R.id.button_timer);
    }

    ///////////////////////////////////////////////////////////////////////////
    // CustomResultReceiver.Receiver
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        setTime(resultData.getInt(Consts.TIME_KEY));
    }

}