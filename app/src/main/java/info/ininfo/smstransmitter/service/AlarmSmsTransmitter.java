package info.ininfo.smstransmitter.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import info.ininfo.smstransmitter.helpers.DbHelper;
import info.ininfo.smstransmitter.engine.SmsWorker;
import info.ininfo.smstransmitter.models.Settings;

public class AlarmSmsTransmitter extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        PowerManager.WakeLock wl = null;
        try {
//            String key = new Settings(context).GetKey();
//            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//            // https://stackoverflow.com/questions/39954822/battery-optimizations-wakelocks-on-huawei-emui-4-0/47053479#47053479
//            wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LocationManagerService");
//            wl.acquire();
//
//            Log.d("smstransmitter", "AlarmSmsTransmitter.onReceive, key: " + key);
//
//            if (!SmsWorker.isWorking()) {
//                WorkerTask workerTask = new WorkerTask(context, null, true, true, key);
//                workerTask.execute();
//            }
        }catch (Exception exc){
            new DbHelper(context).LogInsert(exc);
        }
        finally {
            if (wl != null){
                wl.release();
            }
        }
    }

    public static void StartAlarm(Context context, int frequencyMinutes)
    {
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmSmsTransmitter.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.cancel(pendingIntent);
        //am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * frequencyMinutes, pi); // Millisec * Second * Minute

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP
                , 1000
                , 1000 * 60 * frequencyMinutes       // Millisec * Second * Minute
                , pendingIntent);
    }

    public static void StopAlarm(Context context)
    {
        Intent intent = new Intent(context, AlarmSmsTransmitter.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
