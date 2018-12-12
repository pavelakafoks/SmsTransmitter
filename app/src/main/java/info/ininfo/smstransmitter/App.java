package info.ininfo.smstransmitter;

import android.app.Application;

import java.util.concurrent.TimeUnit;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import info.ininfo.smstransmitter.engine.SmsWorker;
import info.ininfo.smstransmitter.models.Settings;
import info.ininfo.smstransmitter.service.MyWorker;

public class App extends Application {

    public static final String WORKER_NAME = "sender";

    private Settings settings;
    private SmsWorker alarmSmsWorker;

    @Override
    public void onCreate() {
        super.onCreate();
        settings = new Settings(this);
        alarmSmsWorker = new SmsWorker(this, true);
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

        long repeatInterval = Math.max(15, settings.GetFrequency());
        long flexInterval = PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS;

        PeriodicWorkRequest myWorkRequest = new PeriodicWorkRequest.Builder(MyWorker.class,
                repeatInterval, TimeUnit.MINUTES, flexInterval, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance().enqueueUniquePeriodicWork(WORKER_NAME, policy, myWorkRequest);
    }

    public SmsWorker getAlarmSmsWorker() {
        return alarmSmsWorker;
    }
}
