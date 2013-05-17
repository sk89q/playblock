package com.skcraft.playblock.projector;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import com.sk89q.forge.BehaviorList;
import com.sk89q.forge.BehaviorListener;
import com.sk89q.forge.BehaviorPayload;
import com.sk89q.forge.PayloadReceiver;
import com.sk89q.forge.TileEntityPayload;
import com.skcraft.playblock.PacketHandler;
import com.skcraft.playblock.network.PlayBlockPayload;
import com.skcraft.playblock.player.MediaPlayer;
import com.skcraft.playblock.player.MediaPlayerClient;
import com.skcraft.playblock.player.MediaPlayerHost;
import com.skcraft.playblock.queue.ExposedQueue;
import com.skcraft.playblock.queue.QueueBehavior;
import com.skcraft.playblock.util.AccessList;
import com.skcraft.playblock.util.DoubleThresholdRange;
import com.skcraft.playblock.util.DoubleThresholdRange.RangeTest;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * The tile entity for the projector block.
 */
public class ProjectorTileEntity extends TileEntity 
        implements BehaviorListener, PayloadReceiver, ExposedQueue {
    
    public static final String INTERNAL_NAME = "PlayBlockProjector";

    private final BehaviorList behaviors = new BehaviorList();
    
    private final MediaPlayer mediaPlayer;
    private final DoubleThresholdRange range;
    private final ProjectorOptions options;
    private final QueueBehavior queueBehavior;
    
    private final RangeTest rangeTest;
    private boolean withinRange = false;

    /**
     * Construct a new instance of the projector tile entity.
     */
    public ProjectorTileEntity() {
        behaviors.addBehaviorListener(this);
        behaviors.add(range = new DoubleThresholdRange());
        
        Side side = FMLCommonHandler.instance().getEffectiveSide();

        if (side == Side.CLIENT) {
            behaviors.add(mediaPlayer = new MediaPlayerClient());
            behaviors.add(queueBehavior = new QueueBehavior(null));
            rangeTest = range.createRangeTest();
        } else {
            behaviors.add(mediaPlayer = new MediaPlayerHost());
            behaviors.add(queueBehavior = new QueueBehavior((MediaPlayerHost) mediaPlayer));
            rangeTest = null;
        }
        
        behaviors.add(options = new ProjectorOptions(mediaPlayer, range));
        if (side != Side.CLIENT) {
            options.useAccessList(true);
        }
    }

    /**
     * Get the media player.
     * 
     * @return the media player
     */
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
    
    /**
     * Get the range manager.
     * 
     * @return the range manager, null if on the server
     */
    public DoubleThresholdRange getRange() {
        return range;
    }
    
    /**
     * Get the options controller.
     * 
     * @return options controller
     */
    public ProjectorOptions getOptions() {
        return options;
    }

    /**
     * Get the access list.
     * 
     * @return the access list, null if on the client
     */
    public AccessList getAccessList() {
        return getOptions().getAccessList();
    }

    /**
     * Get the queue behavior.
     * 
     * @return the queue behavior
     */
    @Override
    public QueueBehavior getQueueBehavior() {
        return queueBehavior;
    }

    /**
     * Get the local player is in range.
     * 
     * @return true if in range
     */
    public boolean inRange() {
        if (rangeTest == null) {
            throw new RuntimeException("Can't do range test on server");
        }
        
        return rangeTest.getCachedInRange();
    }

    @Override
    public void readPayload(EntityPlayer player, DataInputStream in) throws IOException {
        BehaviorPayload payload = new BehaviorPayload();
        payload.read(in);
        behaviors.readPayload(player, payload, in);
    }
    
    @Override
    public Packet getDescriptionPacket() {
        // Client -> Server
        NBTTagCompound tag = new NBTTagCompound();
        behaviors.writeNetworkedNBT(tag);
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, -1, tag);
    }

    @Override
    public void onDataPacket(INetworkManager net, Packet132TileEntityData packet) {
        // Only called on the client
        NBTTagCompound tag = packet.customParam1;
        behaviors.readNetworkedNBT(tag);
    }
    
    @Override
    public void networkedNbt(NBTTagCompound tag) {
        if (!this.worldObj.isRemote) {
            super.writeToNBT(tag); // Coordinates
            Packet132TileEntityData packet = 
                    new Packet132TileEntityData(xCoord, yCoord, zCoord, -1, tag);
            PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord, 250,
                    worldObj.provider.dimensionId, packet);
        }
    }

    @Override
    public void payloadSend(BehaviorPayload behaviorPayload, List<EntityPlayer> players) {
        PlayBlockPayload payload = new PlayBlockPayload(
                new TileEntityPayload(this, behaviorPayload));
        
        if (worldObj.isRemote) {
            PacketHandler.sendToServer(payload);
        } else {
            PacketHandler.sendToClient(payload, players);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        // Saving to disk
        super.writeToNBT(tag);
        behaviors.writeSaveNBT(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        // Saving to disk
        super.readFromNBT(tag);
        behaviors.readSaveNBT(tag);
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        if (this.worldObj.isRemote) {
            ((MediaPlayerClient) mediaPlayer).release();
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (this.worldObj.isRemote) {
            ((MediaPlayerClient) mediaPlayer).release();
        }
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void updateEntity() {
        if (this.worldObj.isRemote) {
            if (rangeTest.inRange(xCoord, yCoord, zCoord)) {
                ((MediaPlayerClient) mediaPlayer).enable();
            } else {
                ((MediaPlayerClient) mediaPlayer).disable();
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
