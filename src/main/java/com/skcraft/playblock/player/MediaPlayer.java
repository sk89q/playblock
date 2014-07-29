package com.skcraft.playblock.player;

import net.minecraft.nbt.NBTTagCompound;

import com.sk89q.forge.AbstractBehavior;
import com.skcraft.playblock.media.MediaResolver;
import com.skcraft.playblock.util.MathUtils;
import com.skcraft.playblock.util.Validate;

/**
 * This class (and its subclasses) manage a media player that is appropriately
 * synchronized between a server and clients.
 */
public abstract class MediaPlayer extends AbstractBehavior {

    private static final int MAX_SCREEN_SIZE = 64;

    private String uri = "";
    private float width = 1;
    private float height = 1;
    private boolean queueMode = false;

    /**
     * Get the width of the screen.
     * 
     * @return the width
     */
    public float getWidth() {
        return width;
    }

    /**
     * Get the URI of the stream.
     * 
     * @return the URI
     */
    public String getUri() {
        return uri;
    }

    /**
     * Get the URI of the stream.
     * 
     * @param uri
     *            the URI
     */
    public void setUri(String uri) {
        Validate.notNull(uri);
        this.uri = MediaResolver.cleanUri(uri);
    }

    /**
     * Set the width of the screen.
     * 
     * @param width
     *            the width
     */
    public void setWidth(float width) {
        this.width = MathUtils.clamp(width, 1, MAX_SCREEN_SIZE);
    }

    /**
     * Get the width of the screen.
     * 
     * @return the height
     */
    public float getHeight() {
        return height;
    }

    /**
     * Set the height of the screen.
     * 
     * @param height
     *            the height
     */
    public void setHeight(float height) {
        this.height = MathUtils.clamp(height, 1, MAX_SCREEN_SIZE);
    }

    /**
     * Return whether queue mode is on.
     * 
     * @return true if queue mode is on
     */
    public boolean inQueueMode() {
        return queueMode;
    }

    /**
     * Set whether queue mode is on.
     * 
     * @param queueMode
     *            true if queue mode is on
     */
    public void setQueueMode(boolean queueMode) {
        this.queueMode = queueMode;
    }

    /**
     * Write NBT data that is both sent to the client, and is also saved to
     * disk.
     * 
     * @param tag
     *            the tag
     */
    protected void toSharedNbt(NBTTagCompound tag) {
        tag.setString("uri", getUri());
        tag.setFloat("width", getWidth());
        tag.setFloat("height", getHeight());
        tag.setBoolean("queueMode", inQueueMode());
    }

    /**
     * Read NBT data that is both sent to the client, and is also saved to disk.
     * 
     * @param tag
     *            the tag
     */
    protected void fromSharedNbt(NBTTagCompound tag) {
        setUri(tag.getString("uri"));
        setWidth(tag.getFloat("width"));
        setHeight(tag.getFloat("height"));
        setQueueMode(tag.getBoolean("queueMode"));
    }

    @Override
    public void writeSaveNBT(NBTTagCompound tag) {
        toSharedNbt(tag);
    }

    @Override
    public void readSaveNBT(NBTTagCompound tag) {
        fromSharedNbt(tag);
    }

}
