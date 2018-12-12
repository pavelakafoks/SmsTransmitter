package info.ininfo.smstransmitter.service;

import android.content.Context;
import android.support.annotation.NonNull;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import info.ininfo.smstransmitter.App;

public class MyWorker extends Worker {

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        App app = (App) getApplicationContext();

        app.getAlarmSmsWorker().Process();

        return Result.SUCCESS;
    }
}
