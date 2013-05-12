package com.skcraft.playblock.player;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import com.skcraft.playblock.PlayBlock;
import com.skcraft.playblock.media.Media;
import com.skcraft.playblock.media.MediaQueue;
import com.skcraft.playblock.media.MediaResolver;
import com.skcraft.playblock.media.PlayingMedia;
import com.skcraft.playblock.media.QueueListener;
import com.skcraft.playblock.media.QueueManager;
import com.skcraft.playblock.util.AccessList;
import com.skcraft.playblock.util.MathUtils;
import com.skcraft.playblock.util.Validate;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * The tile entity for the projector block.
 */
public class ProjectorTileEntity extends TileEntity implements QueueListener {
    
    public static final String INTERNAL_NAME = "PlayBlockProjector";

    private static final int MAX_RANGE = 64;
    private static final int MIN_BUFFER_RANGE = 5;
    private static final int MAX_SCREEN_SIZE = 64;
    private static final int MAX_VIDEO_SIZE = 850;

    private String uri = "";
    private float width = 1;
    private float height = 1;
    private float triggerRange = 0;
    private float fadeRange = MIN_BUFFER_RANGE;
    private boolean queueMode = false;

    // Server
    private AccessList accessList;
    private QueueManager queueManager;
    private MediaQueue queue;

    // Client
    private PlayingMedia playing;
    private long displayStartTime = 0;
    private MediaManager mediaManager;
    private MediaRenderer renderer;
    private boolean withinRange = false;
    private String lastUri;
    private float rendererWidth;
    private float rendererHeight;

    /**
     * Construct a new instance of the projector tile entity.
     */
    public ProjectorTileEntity() {
        Side side = FMLCommonHandler.instance().getEffectiveSide();

        if (side == Side.CLIENT) {
            mediaManager = PlayBlock.getClientRuntime().getMediaManager();
        } else {
            queueManager = PlayBlock.getRuntime().getQueueManager();
            accessList = new AccessList();
        }
    }

    /**
     * Construct a new instance of the projector tile entity from an existing
     * tile entity, copying the X, Y, and Z coordinates.
     */
    ProjectorTileEntity(ProjectorTileEntity old) {
        xCoord = old.xCoord;
        yCoord = old.yCoord;
        zCoord = old.zCoord;
    }

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
     * @param uri the URI
     */
    public void setUri(String uri) {
        Validate.notNull(uri);
        this.uri = MediaResolver.cleanUri(uri);
    }

    /**
     * Set the width of the screen.
     * 
     * @param width the width
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
     * @param height the height
     */
    public void setHeight(float height) {
        this.height = MathUtils.clamp(height, 1, MAX_SCREEN_SIZE);
    }

    /**
     * Gets the range (in blocks) at which the player will activate and start playing.
     * 
     * @see #getTriggerRangeSq() get the squared version, which is faster
     * @return the range in blocks
     */
    public float getTriggerRange() {
        return Math.round(Math.sqrt(triggerRange) * 100 / 100);
    }

    /**
     * Gets the range (in blocks) at which the player will activate and start playing.
     * 
     * @return the range in blocks, squared
     */
    public float getTriggerRangeSq() {
        return triggerRange;
    }

    /**
     * Sets the range (in blocks) at which the player will activate and start playing.
     * 
     * @param range the range in blocks
     */
    public void setTriggerRange(float range) {
        float v = MathUtils.clamp(range, 1, MAX_RANGE);
        triggerRange = v * v; // Store values squared
        ensureProperBuffer();
    }

    /**
     * Gets the range (in blocks) at which the player will stop playing if it is
     * currently playing.
     * 
     * @see #getFadeRangeSq() get the squared version, which is faster
     * @return the range
     */
    public float getFadeRange() {
        return Math.round(Math.sqrt(fadeRange) * 100 / 100);
    }

    /**
     * Gets the range (in blocks) at which the player will stop playing if it is
     * currently playing.
     * 
     * @return the range in blocks, squared
     */
    public float getFadeRangeSq() {
        return fadeRange;
    }

    /**
     * Sets the range (in blocks) at which the player will stop playing if it is
     * currently playing.
     * 
     * @param range range in blocks, squared
     */
    public void setFadeRange(float range) {
        float v = MathUtils.clamp(range, 1, MAX_RANGE + MIN_BUFFER_RANGE);
        fadeRange = v * v; // Store values squared
        ensureProperBuffer();
    }
    
    /**
     * This changes the fade distance appropriately to ensure that there is at least
     * a {@value #MIN_BUFFER_RANGE} block distance difference between the trigger
     * distance and the fade distance.
     */
    private void ensureProperBuffer() {
        float min = getTriggerRange() + MIN_BUFFER_RANGE;
        if (getFadeRange() < min) {
            // Do not call setFadeRange()!
            fadeRange = min * min; // Store values squared
        }
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
     * @param queueMode true if queue mode is on
     */
    public void setQueueMode(boolean queueMode) {
        // Server-only
        if (queueManager != null && this.queueMode != queueMode) {
            if (queueMode) {
                queue = queueManager.createQueue();
                queue.addQueueListener(this);
            } else {
                queue.release();
                queue = null;
            }
        }
        
        this.queueMode = queueMode;
    }

    // ----------------------------------------
    // Server <-> Client setting change
    // ----------------------------------------

    /**
     * Create the update packet that is sent to the server.
     * 
     * @return the update packet
     */
    public Packet getUpdatePacket() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(bytes);

        try {
            data.writeInt(xCoord);
            data.writeInt(yCoord);
            data.writeInt(zCoord);
            data.writeUTF(getUri());
            data.writeFloat(getWidth());
            data.writeFloat(getHeight());
            data.writeFloat(getTriggerRange());
            data.writeFloat(getFadeRange());
        } catch (IOException e) {
            PlayBlock.log(Level.WARNING, "Failed to send update packet to the server");
        }

        Packet250CustomPayload packet = new Packet250CustomPayload(
                PlayBlock.CHANNEL_ID, bytes.toByteArray());
        return packet;
    }

    /**
     * Read the incoming data of an update packet destined for this tile entity.
     * 
     * @param player the player 
     * @param stream the data stream
     */
    public void acceptClientUpdate(EntityPlayer player, DataInputStream stream) {
        if (getAccessList().checkAndForget(player)) {
            try {
                // These values are validated
                setUri(stream.readUTF());
                setWidth(stream.readFloat());
                setHeight(stream.readFloat());
                setTriggerRange(stream.readFloat());
                setFadeRange(stream.readFloat());
            } catch (Throwable t) {
                PlayBlock.log(Level.WARNING, "Failed to handle update packet sent from " +
                		"the client");
            }
    
            // Now let's send the updates to players around the area
            PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord, 250,
                    worldObj.provider.dimensionId, getDescriptionPacket());
        } else {
            player.sendChatToPlayer("Sorry, you don't have permission " +
            		"to modify that projector.");
        }
    }

    // ----------------------------------------
    // Server <-> Client synchronization
    // ----------------------------------------

    @Override
    public Packet getDescriptionPacket() {
        // Client -> Server
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        
        if (inQueueMode()) {
            PlayingMedia playing = queue.getCurrentMedia();
            if (playing != null) {
                tag.setString("playedUri", playing.getMedia().getUri());
                tag.setInteger("position", (int) playing.getPosition());
            }
        }
        
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, -1, tag);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onDataPacket(INetworkManager net, Packet132TileEntityData packet) {
        NBTTagCompound tag = packet.customParam1;
        
        // Server -> Client
        if (this.worldObj.isRemote) {
            if (tag.hasKey("uri")) {
                readFromNBT(tag);
            }
            
            if (inQueueMode()) {
                setPlayedUri(tag.getString("playedUri"), tag.getInteger("position"));
            } else {
                setPlayedUri(getUri(), -1);
            }
            
            if (hasRenderer()) {
                tryPlayingMedia();
            }
        }
    }
    
    private void sendNext(Media media) {
        NBTTagCompound tag = new NBTTagCompound();
        super.writeToNBT(tag);
        tag.setString("playedUri", media.getUri());
        tag.setInteger("position", -1); // We don't want the video to be skipped ahead
        
        // Now let's send the updates to players around the area
        PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord, 250,
                worldObj.provider.dimensionId, getDescriptionPacket());
    }

    // ----------------------------------------
    // Standard NBT data
    // ----------------------------------------

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setString("uri", getUri());
        tag.setFloat("width", getWidth());
        tag.setFloat("height", getHeight());
        tag.setFloat("triggerRange", getTriggerRange());
        tag.setFloat("fadeRange", getFadeRange());
        tag.setBoolean("queueMode", inQueueMode());
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        
        // This is called from client data
        setUri(tag.getString("uri"));
        setWidth(tag.getFloat("width"));
        setHeight(tag.getFloat("height"));
        setTriggerRange(tag.getFloat("triggerRange"));
        setFadeRange(tag.getFloat("fadeRange"));
        setQueueMode(tag.getBoolean("queueMode"));
    }

    // ----------------------------------------
    // Server
    // ----------------------------------------

    /**
     * Get the access list.
     * 
     * @return the access list
     */
    public AccessList getAccessList() {
        return accessList;
    }

    /**
     * Get the queue.
     * 
     * @return the queue, or null if there is no queue
     */
    public MediaQueue getQueue() {
        return queue;
    }

    @Override
    public void mediaComplete(Media media) {
    }

    @Override
    public void mediaAdvance(Media media) {
        if (this.worldObj != null) {
            sendNext(media);
        }
    }

    // ----------------------------------------
    // Client
    // ----------------------------------------
    
    /**
     * Set the URI that is actually played.
     * 
     * @param uri the URI
     * @param startTime the start time to offset the video, possibly negative
     */
    @SideOnly(Side.CLIENT)
    private void setPlayedUri(String uri, long startTime) {
        if (MediaResolver.canPlayUri(uri)) {
            playing = PlayingMedia.fromRelative(new Media(uri), startTime);
        } else {
            playing = null;
        }
    }

    /**
     * Get whether the URI can be played.
     * 
     * @return true if there is a playable URI
     */
    public boolean isPlayable() {
        return playing != null;
    }
    
    /**
     * Return whether a renderer has been assigned to this projector.
     * 
     * @return true if there is a renderer
     */
    private boolean hasRenderer() {
        return renderer != null;
    }

    /**
     * Get the renderer assigned to this tile entity.
     * 
     * @return the renderer, or possibly null
     */
    @SideOnly(Side.CLIENT)
    public MediaRenderer getRenderer() {
        return renderer;
    }

    /**
     * Return whether the client is within range of viewing this screen.
     * 
     * @return true if within range
     */
    public boolean isWithinRange() {
        return withinRange;
    }

    /**
     * Get the start time (in milliseconds) since the display started.
     * 
     * @return the play start time
     */
    public long getDisplayStartTime() {
        return displayStartTime;
    }

    /**
     * Acquire a renderer and start playing the video if possible.
     */
    @SideOnly(Side.CLIENT)
    private void setupRenderer() {
        int videoWidth = (int) Math.min(MAX_VIDEO_SIZE, width * 64);
        int videoHeight = (int) Math.min(MAX_VIDEO_SIZE, height * 64);
        renderer = mediaManager.acquireRenderer(videoWidth, videoHeight);
    }
    
    /**
     * Tries to play the media on this projector.
     * 
     * <p>A renderer will be acquired, or a new one will be setup if the width
     * and height have changed.</p>
     */
    @SideOnly(Side.CLIENT)
    private void tryPlayingMedia() {
        if (!hasRenderer()) {
            setupRenderer();
        } else if (rendererWidth != getWidth() || rendererHeight != getHeight()) {
            // Width or height change? Re-make the renderer
            release();
            setupRenderer();
        }

        if (playing != null) {
            // Store these values in case the renderer needs to change
            rendererWidth = getWidth();
            rendererHeight = getHeight();
            
            String uri = playing.getMedia().getUri();
            if (lastUri == null || !lastUri.equals(uri)) { // Only change the media if we need to
                lastUri = uri;
                renderer.playMedia(uri, playing.getCalculatedPosition(), !inQueueMode());
                displayStartTime = System.currentTimeMillis();
            }
        }
    }

    /**
     * Detach the renderer from this instance and also stop the video.
     */
    @SideOnly(Side.CLIENT)
    private void release() {
        if (renderer != null) {
            mediaManager.release(renderer);
            renderer = null;
            lastUri = null;
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onChunkUnload() {
        release();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void invalidate() {
        super.invalidate();
        release();
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void updateEntity() {
        if (this.worldObj.isRemote) {
            // Currently playing
            if (withinRange) {
                double distance = Minecraft.getMinecraft().thePlayer
                        .getDistanceSq(xCoord, yCoord, zCoord);
                
                // Passed the fade distance?
                if (distance >= getFadeRangeSq()) {
                    withinRange = false;
                    release();
                }
            
            // Currently not playing
            } else {
                if (mediaManager.hasNoRenderer()) {
                    double distance = Minecraft.getMinecraft().thePlayer
                            .getDistanceSq(xCoord, yCoord, zCoord);
                    
                    // Start the media
                    if (distance <= getTriggerRangeSq()) {
                        if (mediaManager.isSupported()) {
                            tryPlayingMedia();
                        }
                        
                        withinRange = true;
                    }
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        // TODO: May want to use a less expansive render AABB
        return INFINITE_EXTENT_AABB;
    }

}
