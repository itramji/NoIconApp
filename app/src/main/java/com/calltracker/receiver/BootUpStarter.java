package com.calltracker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.calltracker.util.CallLogSyncUtils;

public class BootUpStarter extends BroadcastReceiver {

    public void onReceive(Context context, Intent arg1) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(arg1.getAction())) {
            CallLogSyncUtils.CreateSyncAccount(context);
        }
    }
}