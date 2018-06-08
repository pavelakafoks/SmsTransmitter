package info.ininfo.smstransmitter.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import info.ininfo.smstransmitter.models.Settings;


public class AutoStart extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            if (new Settings(context).GetSwitchSendAutomatically()) {
                context.startService(new Intent(context, ServiceSmsTransmitter.class));
            }
        }
    }
}