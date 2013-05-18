package com.skcraft.playblock.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.sk89q.forge.Payload;
import com.sk89q.forge.RequestResponse;

/**
 * A client -> server request to add a media clip to a queue.
 */
public class Enqueue implements Payload, RequestResponse<EnqueueResponse> {
    
    private short callId;
    private String uri;

    @Override
    public short getCallId() {
        return callId;
    }

    @Override
    public void setCallId(short id) {
        this.callId = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        setCallId(in.readShort());
        setUri(in.readUTF());
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeShort(getCallId());
        out.writeUTF(getUri());
    }

}
