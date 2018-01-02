package com.calltracker.syncadapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.calltracker.util.CallLogSyncUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private Context mContext;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        Log.e("SyncAdapter", "Sync Adapter created.");
        mContext = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("CallLog");

        myRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String lastUpdatedDate = "";
                if (dataSnapshot.hasChildren()) {
                    lastUpdatedDate = dataSnapshot.getChildren().iterator().next().getKey();
                }

                CallLogSyncUtils.getAllCallLogs(mContext, myRef, lastUpdatedDate);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                CallLogSyncUtils.getAllCallLogs(mContext, myRef, "");
            }
        });
    }

}
