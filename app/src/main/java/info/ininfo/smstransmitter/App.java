package info.ininfo.smstransmitter;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.work.Configuration;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import info.ininfo.smstransmitter.di.AppModule;
import info.ininfo.smstransmitter.models.Settings;
import info.ininfo.smstransmitter.service.MyWorker;

public class App extends Application {

    public static final String WORKER_NAME = "sender";
    private static final String TEST_CHANNEL = "test_channel";

    private AppModule appModule;
    private Settings settings;

    @Override
    public void onCreate() {
        super.onCreate();
        appModule = new AppModule(this);
        settings = new Settings(this);
    }

    public void checkWorker(boolean replaceWorker) {

        if (!settings.GetSwitchSendAutomatically()) {
            WorkManager.getInstance().cancelUniqueWork(WORKER_NAME);
            return;
        }

        ExistingPeriodicWorkPolicy policy;
        if (replaceWorker) {
            policy = ExistingPeriodicWorkPolicy.REPLACE;
        } else {
            policy = ExistingPeriodicWorkPolicy.KEEP;
        }

        int repeatInterval = Math.min(15, settings.GetFrequency());
        int flexInterval = Math.min(10, repeatInterval * 2 / 3);

        PeriodicWorkRequest myWorkRequest = new PeriodicWorkRequest.Builder(MyWorker.class,
                repeatInterval, TimeUnit.MINUTES, flexInterval, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance().enqueueUniquePeriodicWork(WORKER_NAME, policy, myWorkRequest);
    }

    public void testNotification() {
        Handler handler = new Handler(getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (shouldCreateNowRunningChannel()) {
                    createNowRunningChannel();
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                Notification notification = new NotificationCompat.Builder(getApplicationContext(), TEST_CHANNEL)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentText(sdf.format(new Date()))
                        .setAutoCancel(false)  // last change
                        .build();

                ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(1122, notification);
            }
        });
    }

    private boolean shouldCreateNowRunningChannel() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !nowRunningChannelExists();
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private boolean nowRunningChannelExists() {
        NotificationManager platformNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        return platformNotificationManager != null &&
                platformNotificationManager.getNotificationChannel(TEST_CHANNEL) != null;
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private void createNowRunningChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(TEST_CHANNEL,
                "Test Notification",
                NotificationManager.IMPORTANCE_LOW);
        notificationChannel.setDescription("Test Notification Description");

        NotificationManager platformNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (platformNotificationManager != null) {
            platformNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public AppModule getAppModule() {
        return appModule;
    }

    public Settings getSettings() {
        return settings;
    }
}
