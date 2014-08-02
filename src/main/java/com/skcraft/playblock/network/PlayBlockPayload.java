package com.skcraft.playblock.network;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

import com.sk89q.forge.Payload;
import com.sk89q.forge.TileEntityPayload;

public class PlayBlockPayload implements Payload {

    public enum Type {
        TILE_ENTITY, TILE_ENTITY_NBT
    }

    private Type type;
    private Payload payload;

    public PlayBlockPayload() {
    }

    public PlayBlockPayload(TileEntityPayload payload) {
        type = Type.TILE_ENTITY;
        this.payload = payload;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    @Override
    public void read(ByteBufInputStream in) throws IOException {
        Type[] types = Type.values();
        int typeCode = in.readByte() & 0xFF;
        if (typeCode >= 0 && typeCode < types.length) {
            type = types[typeCode];
        } else {
            throw new IOException("Invalid packet type!");
        }
        if (payload != null) {
            payload.read(in);
        }

    }

    @Override
    public void write(ByteBufOutputStream out) throws IOException {
        out.writeByte(type.ordinal());
        if (payload != null) {
            payload.write(out);
        }
    }

}
