package com.skcraft.playblock.projector;

import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import com.sk89q.forge.NbtEntityList;
import com.sk89q.forge.NbtEntityListener;
import com.sk89q.forge.TileEntityPayloadReceiver;
import com.skcraft.playblock.player.MediaPlayer;
import com.skcraft.playblock.player.MediaPlayerClient;
import com.skcraft.playblock.player.MediaPlayerHost;
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
        implements NbtEntityListener, TileEntityPayloadReceiver {
    
    public static final String INTERNAL_NAME = "PlayBlockProjector";

    private final NbtEntityList nbtEntities = new NbtEntityList();
    private final AccessList accessList;
    private final MediaPlayer mediaPlayer;
    private final DoubleThresholdRange range;
    private final RangeTest rangeTest;
    private boolean withinRange = false;

    /**
     * Construct a new instance of the projector tile entity.
     */
    public ProjectorTileEntity() {
        nbtEntities.addNbtEntityListener(this);
        nbtEntities.add(range = new DoubleThresholdRange());
        
        Side side = FMLCommonHandler.instance().getEffectiveSide();

        if (side == Side.CLIENT) {
            accessList = null;
            nbtEntities.add(mediaPlayer = new MediaPlayerClient());
            rangeTest = range.createRangeTest();
        } else {
            accessList = new AccessList();
            nbtEntities.add(mediaPlayer = new MediaPlayerHost());
            rangeTest = null;
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
     * Get the access list.
     * 
     * @return the access list, null if on the client
     */
    public AccessList getAccessList() {
        return accessList;
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
    public void readClientPayload(EntityPlayerMP player, DataInputStream in)
            throws IOException {
        if (getAccessList().checkAndForget(player)) {
            ProjectorUpdatePayload update = new ProjectorUpdatePayload();
            update.read(in);
            
            // These values are validated
            mediaPlayer.setUri(update.getUri());
            mediaPlayer.setWidth(update.getWidth());
            mediaPlayer.setHeight(update.getHeight());
            range.setTriggerRange(update.getTriggerRange());
            range.setFadeRange(update.getFadeRange());
    
            // Now let's send the updates to players around the area
            PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord, 250,
                    worldObj.provider.dimensionId, getDescriptionPacket());
        } else {
            player.sendChatToPlayer("Sorry, you don't have permission " +
            		"to modify that projector.");
        }
    }
    
    @Override
    public Packet getDescriptionPacket() {
        // Client -> Server
        NBTTagCompound tag = new NBTTagCompound();
        nbtEntities.toNetworkSnapshotNbt(tag);
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, -1, tag);
    }

    @Override
    public void onDataPacket(INetworkManager net, Packet132TileEntityData packet) {
        // Only called on the client
        NBTTagCompound tag = packet.customParam1;
        nbtEntities.fromNetworkSnapshotNbt(tag);
    }
    
    @Override
    public void nbtEvent(NBTTagCompound tag) {
        if (!this.worldObj.isRemote) {
            super.writeToNBT(tag); // Coordinates
            PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord, 250,
                    worldObj.provider.dimensionId, getDescriptionPacket());
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        // Saving to disk
        super.writeToNBT(tag);
        nbtEntities.toWorldSaveNbt(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        // Saving to disk
        super.readFromNBT(tag);
        nbtEntities.fromWorldSaveNbt(tag);
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
