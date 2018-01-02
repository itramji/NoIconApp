package com.calltracker.observer;

import android.database.ContentObserver;
import android.os.Handler;

import com.calltracker.util.CallLogSyncUtils;


public class CallLogObserver extends ContentObserver {

    public CallLogObserver(Handler h) {
        super(h);
    }

    @Override
    public boolean deliverSelfNotifications() {
        return true;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        CallLogSyncUtils.TriggerRefresh();
    }
}