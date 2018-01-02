package com.calltracker.syncadapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class SyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static SyncAdapter mSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("","Sync Service created.");
        synchronized (sSyncAdapterLock){
            if(mSyncAdapter == null){
                mSyncAdapter = new SyncAdapter(getApplicationContext(),true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("","Sync Service binded.");
        return mSyncAdapter.getSyncAdapterBinder();
    }
}
