package com.calltracker;

import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;

import com.calltracker.observer.CallLogObserver;
import com.calltracker.util.CallLogSyncUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create account, if needed
        CallLogSyncUtils.CreateSyncAccount(this);

        getContentResolver().
                registerContentObserver(
                        android.provider.CallLog.Calls.CONTENT_URI,
                        true,
                        new CallLogObserver(new Handler()));

        finish();


       /* FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("CallLog");


        myRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String lastUpdatedDate = "";
                if (dataSnapshot.hasChildren()) {
                    lastUpdatedDate = dataSnapshot.getChildren().iterator().next().getKey();
                }

                CallLogSyncUtils.getAllCallLogs(MainActivity.this, myRef, lastUpdatedDate);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                CallLogSyncUtils.getAllCallLogs(MainActivity.this, myRef, "");
            }
        });*/
    }

}
