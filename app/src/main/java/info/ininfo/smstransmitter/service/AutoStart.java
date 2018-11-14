package info.ininfo.smstransmitter.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import info.ininfo.smstransmitter.models.Settings;


public class AutoStart extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            Log.d("smstransmitter", "AutoStart.onReceive");
            if (new Settings(context).GetSwitchSendAutomatically()) {
                Log.d("smstransmitter", "AutoStart.onReceive, send automatically = true");
                context.startService(new Intent(context, ServiceSmsTransmitter.class));
            }
        }
    }
}