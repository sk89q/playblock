package com.skcraft.playblock.network;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

import com.sk89q.forge.Payload;
import com.sk89q.forge.RequestResponse;
import com.skcraft.playblock.media.Media;
import com.skcraft.playblock.util.IOUtils;

/**
 * A server -> client response to an {@link Enqueue}.
 */
public class EnqueueResponse implements Payload, RequestResponse<EnqueueResponse> {

    public enum Response {
        OK, ERROR_NO_QUEUE, ERROR_UNKNOWN_LENGTH, ERROR_TOO_LONG, ERROR_TOO_SHORT, ERROR_INTERNAL
    }

    private short callId;
    private Response response;
    private String uri;

    public EnqueueResponse() {
    }

    public EnqueueResponse(Response response, Media media) {
        setResponse(response);
        setMedia(media);
    }

    @Override
    public short getCallId() {
        return callId;
    }

    @Override
    public void setCallId(short id) {
        this.callId = id;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setMedia(Media media) {
        setUri(media.getUri());
    }

    @Override
    public void read(ByteBufInputStream in) throws IOException {
        setCallId(in.readByte());
        setResponse(IOUtils.resolveOrdinal(Response.class, in.readByte()));
        setUri(in.readUTF());
    }

    @Override
    public void write(ByteBufOutputStream out) throws IOException {
        out.writeByte(getCallId());
        out.writeByte(getResponse().ordinal());
        out.writeUTF(getUri());
    }

}
