package com.rescribe.doctor.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rescribe.doctor.service.MQTTService;

/**
 * Created by ganeshshirole on 27/6/17.
 */

public class StartUpBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // start mqtt Service
            // use this to start and trigger a service
            Intent serviceIntent = new Intent(context, MQTTService.class);
            // potentially add data to the serviceIntent
            serviceIntent.putExtra(MQTTService.IS_MESSAGE, false);
            context.startService(serviceIntent);
        }
    }
}