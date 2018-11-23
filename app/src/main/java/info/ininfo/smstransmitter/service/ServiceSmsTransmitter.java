package info.ininfo.smstransmitter.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import info.ininfo.smstransmitter.R;
import info.ininfo.smstransmitter.activity.MainActivity;
import info.ininfo.smstransmitter.helpers.DbHelper;
import info.ininfo.smstransmitter.models.EnumLogType;
import info.ininfo.smstransmitter.models.Settings;

public class ServiceSmsTransmitter extends Service {

    Handler _handler = new Handler();
    Context _context;
    PowerManager.WakeLock _wakeLock;
    WifiManager.WifiLock _wifiLock;
    int _frequency;
    boolean _batterySaveMode;
    String _key;

    private final String NOW_RUNNING_CHANNEL = "info.ininfo.smstransmitter.NOW_RUNNING";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static boolean IsRunning(Context context) {
        Class<?> serviceClass = ServiceSmsTransmitter.class;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean startService(Context context) {
        Settings settings = new Settings(context);
        boolean goodRun = true;
        Intent intent = new Intent(context, ServiceSmsTransmitter.class);
        if (settings.GetSwitchSendAutomatically()) {
            if (settings.GetKey().isEmpty()) {
                Toast.makeText(context, context.getString(R.string.settings_error_empty_key), Toast.LENGTH_LONG).show();
                new DbHelper(context).LogInsert(R.string.settings_error_empty_key, EnumLogType.Error);
                goodRun = false;
            } else {
                if (ServiceSmsTransmitter.IsRunning(context) == false) {
                    intent.putExtra("batterySaveMode", settings.GetSwitchBatterySaveMode());
                    intent.putExtra("frequency", settings.GetFrequency());
                    intent.putExtra("key", settings.GetKey());
                    ContextCompat.startForegroundService(context, intent);
                }
            }
        } else {
            try {
                context.stopService(intent);
            } catch (Exception exc) {
            }
        }
        return goodRun;
    }

    public static void stopService(Context context) {
        Intent intent = new Intent(context, ServiceSmsTransmitter.class);
        try {
            context.stopService(intent);
        } catch (Exception exc) {
        }
    }

    @Override
    public void onDestroy() {

        if (_batterySaveMode) {
            try {
                AlarmSmsTransmitter.StopAlarm(this);
            } catch (Exception exc) {
            }
        } else {
            try {
                if (_handler != null) {
                    _handler.removeCallbacksAndMessages(null);
                }
            } catch (Exception exc) {
            }

            try {
                if (_wakeLock != null) {
                    _wakeLock.release();
                }
            } catch (Exception exc) {
            }

            try {
                if (_wifiLock != null) {
                    _wifiLock.release();
                }
            } catch (Exception exc) {
            }
        }

        try {
            stopForeground(true);
        } catch (Exception exc) {
        }

        super.onDestroy();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        _context = this;

        // NULL REFERENCE ERROR
        //Bundle extras = intent.getExtras();

        _batterySaveMode = intent.getBooleanExtra("batterySaveMode", true);
        _frequency = intent.getIntExtra("frequency", 15);
        _key = intent.getStringExtra("key");

        //_frequency = 1; // TEST FOR

        Log.d("smstransmitter", "ServiceSmsTransmitter.onStartCommand, key: " + _key);

        if (_batterySaveMode) {
            Log.d("smstransmitter", "branch: _batterySaveMode = true");
//            AlarmSmsTransmitter.StartAlarm(_context, _frequency);
        } else {
            Log.d("smstransmitter", "branch: _batterySaveMode = false");
//            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            // https://stackoverflow.com/questions/39954822/battery-optimizations-wakelocks-on-huawei-emui-4-0/47053479#47053479
//            _wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LocationManagerService");
//            _wakeLock.acquire();

            WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wm != null) {
                _wifiLock = wm.createWifiLock(WifiManager.WIFI_MODE_FULL, "SMS Transmitter wifiLock");
                //_wifiLock.setReferenceCounted(true);
                _wifiLock.acquire();
            }
            _handler.post(runnableCode);
        }

        runAsForeground();
        return Service.START_STICKY;
    }

    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            try {
//                WorkerTask workerTask = new WorkerTask(_context, null, true, false, _key);
//                workerTask.execute();
            } catch (Exception exc) {
            }
            _handler.postDelayed(runnableCode, 1000 * 60 * _frequency);
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void runAsForeground() {
        if (shouldCreateNowRunningChannel()) {
            createNowRunningChannel();
        }

        Notification notification = new NotificationCompat.Builder(this, NOW_RUNNING_CHANNEL)
                .setSmallIcon(getNotificationIcon())
                .setContentText(this.getString(R.string.notification_text))
                .setContentIntent(getOnNoticeClickAction())
                .setAutoCancel(false)  // last change
                .setPriority(Notification.PRIORITY_HIGH)  // last change
                .build();

        notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;  // last change

        startForeground(17367, notification);
    }

    private boolean shouldCreateNowRunningChannel() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !nowRunningChannelExists();
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private boolean nowRunningChannelExists() {
        NotificationManager platformNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        return platformNotificationManager != null &&
                platformNotificationManager.getNotificationChannel(NOW_RUNNING_CHANNEL) != null;
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private void createNowRunningChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(NOW_RUNNING_CHANNEL,
                "Now Running",
                NotificationManager.IMPORTANCE_LOW);
        notificationChannel.setDescription("Shows is app running");

        NotificationManager platformNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (platformNotificationManager != null) {
            platformNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ?
                R.drawable.ic_notice_silhouette3
                : R.drawable.ic_notice;
    }

    private PendingIntent getOnNoticeClickAction() {
        Intent intent = new Intent(this, MainActivity.class);
        return PendingIntent.getActivity(this, 0, intent, 0);
    }

}
