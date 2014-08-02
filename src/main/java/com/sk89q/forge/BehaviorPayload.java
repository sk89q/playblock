package com.sk89q.forge;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

/**
 * A payload designed for usage with {@link Behavior}s. A type field is provided
 * to differentiate different payload types for each {@link Behavior}.
 * 
 * <p>
 * However, to prevent payload ID collision, it is important for projects to
 * define a standard for assigning such IDs.
 * </p>
 */
public class BehaviorPayload implements Payload {

    private int type;
    private Payload payload;

    public BehaviorPayload() {
    }

    public BehaviorPayload(int type, Payload payload) {
        setType(type);
        setPayload(payload);
    }

    public BehaviorPayload(Enum<?> type, Payload payload) {
        setType(type);
        setPayload(payload);
    }

    public int getType() {
        return type;
    }

    public boolean isType(Enum<?> e) {
        return e.ordinal() == getType();
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setType(Enum<?> type) {
        this.type = type.ordinal();
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    @Override
    public void read(ByteBufInputStream in) throws IOException {
        setType(in.readByte() & 0xff);
        if (payload != null) {
            payload.read(in);
        }
    }

    @Override
    public void write(ByteBufOutputStream out) throws IOException {
        out.write(type);
        if (payload != null) {
            payload.write(out);
        }
    }

}
