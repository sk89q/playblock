package com.skcraft.playblock.player;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import com.skcraft.playblock.PlayBlock;
import com.skcraft.playblock.util.MathUtils;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * The tile entity for the projector block.
 */
public class ProjectorTileEntity extends TileEntity {
    
    public static final String INTERNAL_NAME = "PlayBlockProjector";

    private static final int MAX_TRIGGER_DISTANCE = 64 * 64;
    private static final int OFF_DISTANCE = 10 * 10;
    private static final int MAX_VIDEO_DIMENSION = 850;

    private String uri = "";
    private float width = 1;
    private float height = 1;
    private float triggerDistance = 0;

    private MediaManager mediaManager;
    private MediaRenderer renderer;

    /**
     * Construct a new instance of the projector tile entity.
     */
    public ProjectorTileEntity() {
        Side side = FMLCommonHandler.instance().getEffectiveSide();

        if (side == Side.CLIENT) {
            mediaManager = PlayBlock.getClientRuntime().getMediaManager();
        }
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
     * Set the width of the screen.
     * 
     * @param width the width
     */
    public void setWidth(float width) {
        this.width = width;
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
        this.height = height;
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
        this.uri = uri;
    }

    /**
     * Get the renderer asssignd to this tile entity.
     * 
     * @return the renderer, or possibly null
     */
    public MediaRenderer getRenderer() {
        return renderer;
    }

    /**
     * Gets the squared trigger distance.
     * 
     * @return the squared trigger distance, which is actually the value squared
     */
    public float getTriggerDistance() {
        return triggerDistance;
    }

    /**
     * Sets the squared trigger distance.
     * 
     * @param distance the trigger distance, which is actually the value squared
     */
    public void setTriggerDistance(float distance) {
        this.triggerDistance = distance;
    }

    /**
     * Acquire a renderer and start playing the video if possible.
     */
    private void setupRenderer() {
        int videoWidth = (int) Math.min(MAX_VIDEO_DIMENSION, width * 64);
        int videoHeight = (int) Math.min(MAX_VIDEO_DIMENSION, height * 64);
        renderer = mediaManager.acquireRenderer(videoWidth, videoHeight);

        if (renderer != null && MediaResolver.isValidUri(uri)) {
            renderer.playMedia(uri);
        }
    }

    /**
     * Detach the renderer from this instance and also stop the video.
     */
    private void release() {
        if (renderer != null) {
            mediaManager.release(renderer);
            renderer = null;
        }
    }

    @Override
    public void onChunkUnload() {
        release();
    }

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
        // Have to check to see whether this needs to activate
        if (this.worldObj.isRemote) {
            if (renderer != null) {
                double distance = Minecraft.getMinecraft().thePlayer
                        .getDistanceSq(xCoord, yCoord, zCoord);
                if (distance >= triggerDistance + OFF_DISTANCE) {
                    release();
                }
            } else if (mediaManager.isAvailable()
                    && mediaManager.hasNoRenderer()) {
                double distance = Minecraft.getMinecraft().thePlayer
                        .getDistanceSq(xCoord, yCoord, zCoord);
                if (distance <= triggerDistance) {
                    setupRenderer();
                }
            }
        }
    }

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
            data.writeUTF(uri);
            data.writeFloat(width);
            data.writeFloat(height);
            data.writeFloat(triggerDistance);
        } catch (IOException e) {
            PlayBlock.log(Level.WARNING, "Failed to send update packet to the server");
        }

        Packet250CustomPayload packet = new Packet250CustomPayload(
                PlayBlock.CHANNEL_ID, bytes.toByteArray());
        return packet;
    }

    public void handleUpdatePacket(DataInputStream stream) {
        try {
            uri = stream.readUTF();
            width = stream.readFloat();
            height = stream.readFloat();
            triggerDistance = stream.readFloat();
        } catch (Throwable t) {
            PlayBlock.log(Level.WARNING, "Failed to handle update packet sent from the client");
        }

        // Now lets send the updates to players around the area
        PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord, 250,
                worldObj.provider.dimensionId, getDescriptionPacket());
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToClientNBT(tag);
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, -1, tag);
    }

    @Override
    public void onDataPacket(INetworkManager net,
            Packet132TileEntityData packet) {
        // Process data from the server
        if (this.worldObj.isRemote) {
            readFromCientNBT(packet.customParam1);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        writeToClientNBT(tag);
    }

    /**
     * Write NBT tags that can also be sent to the client.
     * 
     * @param tag the tag
     */
    private void writeToClientNBT(NBTTagCompound tag) {
        tag.setString("uri", uri);
        tag.setFloat("width", width);
        tag.setFloat("height", height);
        tag.setFloat("distance", triggerDistance);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        readFromCientNBT(tag);
    }

    /**
     * Read NBT tags that can also be read from the client.
     * 
     * @param tag the tag
     */
    private void readFromCientNBT(NBTTagCompound tag) {
        this.uri = tag.getString("uri");
        this.width = MathUtils.clamp(tag.getFloat("width"), 1, 16);
        this.height = MathUtils.clamp(tag.getFloat("height"), 1, 16);
        this.triggerDistance = MathUtils.clamp(tag.getFloat("distance"), 1,
                MAX_TRIGGER_DISTANCE);

        if (renderer != null && MediaResolver.isValidUri(uri)) {
            renderer.playMedia(uri);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        // TODO: May want to use a less expansive render AABB
        return INFINITE_EXTENT_AABB;
    }

}
