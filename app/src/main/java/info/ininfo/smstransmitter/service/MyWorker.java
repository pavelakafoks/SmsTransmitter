package info.ininfo.smstransmitter.service;

import android.content.Context;
import android.support.annotation.NonNull;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import info.ininfo.smstransmitter.App;
import info.ininfo.smstransmitter.engine.SmsWorker;

public class MyWorker extends Worker {

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        App app = (App) getApplicationContext();
        String key = app.getSettings().GetKey();
        if (key == null || key.isEmpty()) {
            return Result.SUCCESS;
        }

        app.testNotification();

        SmsWorker worker = new SmsWorker(getApplicationContext(),
                true,
                false,
                key
        );

        worker.Process();

        return Result.SUCCESS;
    }
}
