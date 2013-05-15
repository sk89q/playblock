package com.skcraft.playblock.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.sk89q.forge.Payload;

public class ProjectorUpdatePayload implements Payload {
    
    private String uri;
    private float height;
    private float width;
    private float triggerRange;
    private float fadeRange;

    public ProjectorUpdatePayload() {
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getTriggerRange() {
        return triggerRange;
    }

    public void setTriggerRange(float triggerRange) {
        this.triggerRange = triggerRange;
    }

    public float getFadeRange() {
        return fadeRange;
    }

    public void setFadeRange(float fadeRange) {
        this.fadeRange = fadeRange;
    }
    
    @Override
    public void read(DataInputStream in) throws IOException {
        setUri(in.readUTF());
        setWidth(in.readFloat());
        setHeight(in.readFloat());
        setTriggerRange(in.readFloat());
        setFadeRange(in.readFloat());
    }
    
    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(getUri());
        out.writeFloat(getWidth());
        out.writeFloat(getHeight());
        out.writeFloat(getTriggerRange());
        out.writeFloat(getFadeRange());
    }

}
