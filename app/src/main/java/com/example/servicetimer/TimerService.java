package com.example.servicetimer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.widget.Chronometer;

import java.util.Timer;
import java.util.TimerTask;

public class TimerService extends Service {

    private IBinder mBinder = new MyBinder();
    private Chronometer mChronometer;
    private static final int TIMER_CODE = 99;
    private ResultReceiver receiver;
    private Timer timer;
    private boolean isPause = true;
    private final String RECEIVER_VAL = "receiver";
    private final int FOREGROUND_ID = 2;
    private int seconds = 1;
    public static final String NOTIFICATION_CHANNEL_ID_SERVICE = "com.package.TimerService";

    ///////////////////////////////////////////////////////////////////////////
    // Events
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Notification.Builder builder = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID_SERVICE)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.app_name))
                    .setAutoCancel(true);

            Notification notification = builder.build();

            startForeground(FOREGROUND_ID, notification);

        } else {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID_SERVICE)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.app_name))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            Notification notification = builder.build();

            startForeground(FOREGROUND_ID, notification);
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mChronometer = new Chronometer(this);
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        receiver = intent.getParcelableExtra(RECEIVER_VAL);
        initCounter();
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        receiver = intent.getParcelableExtra(RECEIVER_VAL);
        initCounter();
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mChronometer.stop();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Methods
    ///////////////////////////////////////////////////////////////////////////

    public int getTime() {
        return seconds;
    }

    public boolean isPause() {
        return isPause;
    }

    public class MyBinder extends Binder {
        TimerService getService() {
            return TimerService.this;
        }
    }

    public void pause() {
        isPause = true;
    }

    public void play() {
        isPause = false;
    }

    public void initCounter() {
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new UpdateTimeTask(), 0, 1000);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Class
    ///////////////////////////////////////////////////////////////////////////

    private class UpdateTimeTask extends TimerTask {

        public void run() {

            if (isPause)
                return;

            Bundle b = new Bundle();
            b.putInt(Consts.TIME_KEY, seconds);

            if (receiver != null) {
                receiver.send(TIMER_CODE, b);
            }

            seconds++;

            if (seconds > 100) {
                seconds = 1;
            }
        }
    }
}

