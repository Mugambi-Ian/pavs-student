package com.nenecorp.pavsstudent.Utility.Tools.PresenceManager;

import com.nenecorp.pavsstudent.Utility.Resources.NCTimeDate;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.util.ArrayList;

public class PresencePnCallback extends SubscribeCallback {
    public ArrayList<PresenceRecord> getRecords() {
        return records;
    }

    private ArrayList<PresenceRecord> records;


    public PresencePnCallback() {
        this.records = new ArrayList<>();
    }

    @Override
    public void status(PubNub pubnub, PNStatus status) {
    }

    @Override
    public void message(PubNub pubnub, PNMessageResult message) {
    }

    @Override
    public void presence(PubNub pubnub, PNPresenceEventResult presence) {
        String sender = presence.getUuid();
        String presenceString = presence.getEvent();
        String timestamp = NCTimeDate.getTimeStampUtc();

        PresenceRecord pm = new PresenceRecord(sender, presenceString, timestamp);
        ArrayList<PresenceRecord> temp = records;
        for (PresenceRecord record : records) {
            if (record.getSender().equals(pm.getSender())) {
                temp.remove(record);
            }
        }
        records=temp;
        records.add(pm);
    }
}
