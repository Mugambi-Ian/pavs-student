package com.nenecorp.pavsstudent.Utility.Tools.PresenceManager;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class PresenceRecord {
    private final String sender;
    private final String presence;
    private final String timestamp;

    public PresenceRecord(@JsonProperty("sender") String sender, @JsonProperty("presence") String presence, @JsonProperty("timestamp") String timestamp) {
        this.sender = sender;
        this.presence = presence;
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public String getPresence() {
        return presence;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final PresenceRecord other = (PresenceRecord) obj;

        return Objects.equal(this.sender, other.sender)
                && Objects.equal(this.presence, other.presence)
                && Objects.equal(this.timestamp, other.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sender, presence, timestamp);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(PresenceRecord.class)
                .add("sender", sender)
                .add("presence", presence)
                .add("timestamp", timestamp)
                .toString();
    }
}
