package com.sk89q.forge;

import net.minecraft.nbt.NBTTagCompound;

/**
 * A utility interface used to more easily keep a synchronized and persistent state
 * of an NBT-supporting object.
 */
public interface NbtEntity {

    /**
     * Write NBT data that needs to be saved to the world.
     * 
     * @param tag the tag
     */
    void toWorldSaveNbt(NBTTagCompound tag);
    
    /**
     * Read NBT data that has been retrieved from a saved world.
     * 
     * @param tag the tag
     */
    void fromWorldSaveNbt(NBTTagCompound tag);
    
    /**
     * Write NBT data that is sent to the client/server. The given NBT data should
     * contain the "full snapshot" to fully describe the object.
     * 
     * @param tag the tag
     */
    void toNetworkSnapshotNbt(NBTTagCompound tag);
    
    /**
     * Read NBT data that is sent to the client/server. The given NBT data should
     * contain the "full snapshot" to fully describe the object.
     * 
     * @param tag the tag
     */
    void fromNetworkSnapshotNbt(NBTTagCompound tag);
    
    /**
     * Read NBT data that has been sent from 
     * {@link NbtEntityListener#nbtEvent(NBTTagCompound)}.
     * 
     * @param tag the tag
     */
    void handleNetworkNbtEvent(NBTTagCompound tag);
    
    /**
     * Add a listener.
     * 
     * @param listener the listener
     */
    void addNbtEntityListener(NbtEntityListener listener);

    /**
     * Remove a listener.
     * 
     * @param listener the listener
     */
    void removeNbtEntityListener(NbtEntityListener listener);
    
}

