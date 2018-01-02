package com.calltracker.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ramji on 20/12/17.
 */

public class CallLog {

    public String callNumber;
    public String callName;
    public String callTime;
    public String dateString;
    public String callType;
    public String duration;

    private String getCallType(String callType) {
        String dir = "";
        int callTypeCode = Integer.parseInt(callType);
        switch (callTypeCode) {
            case android.provider.CallLog.Calls.OUTGOING_TYPE:
                dir = "Outgoing";
                break;

            case android.provider.CallLog.Calls.INCOMING_TYPE:
                dir = "Incoming";
                break;

            case android.provider.CallLog.Calls.MISSED_TYPE:
                dir = "Missed";
                break;
        }
        return dir;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("callNumber", callNumber);
        result.put("callName", callName);
        result.put("dateString", dateString);
        result.put("callType", getCallType(callType));
        result.put("duration", duration);

        return result;
    }

}
