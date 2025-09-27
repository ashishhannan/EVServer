package com.ev07b.model;

import java.util.Arrays;

public final class EV07BMessage {
    public final byte properties;
    public final int length;
    public final int checksum;
    public final int sequenceId;
    public final byte[] body;

    public EV07BMessage(byte properties, int length, int checksum, int sequenceId, byte[] body) {
        this.properties = properties;
        this.length = length;
        this.checksum = checksum & 0xffff;
        this.sequenceId = sequenceId & 0xffff;
        this.body = body == null ? new byte[0] : body.clone();
    }

    public boolean ackRequested() {
        return ((properties >> 4) & 0x1) == 1;
    }

    @Override
    public String toString() {
        return "EV07BMessage{" +
                "properties=0x" + Integer.toHexString(properties & 0xFF) +
                ", length=" + length +
                ", checksum=0x" + Integer.toHexString(checksum) +
                ", sequenceId=0x" + Integer.toHexString(sequenceId) +
                ", body=" + Arrays.toString(body) +
                '}';
    }
}
