package com.calltracker.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.calltracker.model.CallLog;
import com.calltracker.syncadapter.GenericAccountService;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ramji on 21/12/17.
 */

public class CallLogSyncUtils {

    public static final String AUTHORITY = "com.calltracker.authority";
    public static final String ACCOUNT_TYPE = "com.calltracker";
    public static final String ACCOUNT = "default_account";

    private static final long SYNC_FREQUENCY = 30 * 60;  // 30 hour (in seconds)
    private static final String PREF_SETUP_COMPLETE = "setup_complete";

    /**
     * Create an entry for this application in the system account list, if it isn't already there.
     *
     * @param context Context
     */
    public static void CreateSyncAccount(Context context) {
        boolean newAccount = false;
        boolean setupComplete = PreferenceManager
                .getDefaultSharedPreferences(context).getBoolean(PREF_SETUP_COMPLETE, false);

        // Create account, if it's missing. (Either first run, or user has deleted account.)
        Account account = GenericAccountService.GetAccount();
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        if (accountManager != null && accountManager.addAccountExplicitly(account, null, null)) {
            // Inform the system that this account supports sync
            ContentResolver.setIsSyncable(account, AUTHORITY, 1);
            // Inform the system that this account is eligible for auto sync when the network is up
            ContentResolver.setSyncAutomatically(account, AUTHORITY, true);
            // Recommend a schedule for automatic synchronization. The system may modify this based
            // on other scheduled syncs and network utilization.
            ContentResolver.addPeriodicSync(
                    account, AUTHORITY, new Bundle(), SYNC_FREQUENCY);
            newAccount = true;
        }

        // Schedule an initial sync if we detect problems with either our account or our local
        // data has been deleted. (Note that it's possible to clear app data WITHOUT affecting
        // the account list, so wee need to check both.)
        if (newAccount || !setupComplete) {
            TriggerRefresh();
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putBoolean(PREF_SETUP_COMPLETE, true).apply();
        }
    }

    /**
     * Helper method to trigger an immediate sync ("refresh").
     * <p>
     * <p>This should only be used when we need to preempt the normal sync schedule. Typically, this
     * means the user has pressed the "refresh" button.
     * <p>
     * Note that SYNC_EXTRAS_MANUAL will cause an immediate sync, without any optimization to
     * preserve battery life. If you know new data is available (perhaps via a GCM notification),
     * but the user is not actively waiting for that data, you should omit this flag; this will give
     * the OS additional freedom in scheduling your sync request.
     */
    public static void TriggerRefresh() {
        Bundle b = new Bundle();
        // Disable sync backoff and ignore sync preferences. In other words...perform sync NOW!
        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(
                GenericAccountService.GetAccount(),      // Sync account
                AUTHORITY, // Content authority
                b);                                      // Extras
    }


    public static void getAllCallLogs(Context context, DatabaseReference myRef, String lastValue) {

        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
        String mSelectionClause = android.provider.CallLog.Calls.DATE + " > ?";
        String[] selectionArgs = {lastValue};
        Cursor cur = context.getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, TextUtils.isEmpty(lastValue) ? null : mSelectionClause, TextUtils.isEmpty(lastValue) ? null : selectionArgs, strOrder);
        if (cur != null) {
            try {
                // loop through cursor
                while (cur.moveToNext()) {
                    CallLog callLog = new CallLog();
                    callLog.callNumber = cur.getString(cur
                            .getColumnIndex(android.provider.CallLog.Calls.NUMBER));
                    callLog.callName = cur.getString(cur
                            .getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME));

                    callLog.callTime = cur.getString(cur
                            .getColumnIndex(android.provider.CallLog.Calls.DATE));
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
                    callLog.dateString = formatter.format(new Date(Long
                            .parseLong(callLog.callTime)));
                    Log.e("find the date", "" + callLog.callTime + ">>>" + callLog.dateString);

                    callLog.callType = cur.getString(cur
                            .getColumnIndex(android.provider.CallLog.Calls.TYPE));
                    callLog.duration = cur.getString(cur
                            .getColumnIndex(android.provider.CallLog.Calls.DURATION));

                    if (callLog.callNumber != null) {
                        writeNewLog(myRef, callLog);
                    }

                }
            } finally {
                cur.close();
            }
        } else {
            Log.e("SyncAdapter", "cursor is null");
        }

    }

    private static void writeNewLog(DatabaseReference mDatabase, CallLog callLog) {
        Map<String, Object> postValues = callLog.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(callLog.callTime, postValues);

        mDatabase.updateChildren(childUpdates);
    }
}
