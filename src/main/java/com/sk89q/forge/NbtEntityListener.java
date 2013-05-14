package com.sk89q.forge;

import net.minecraft.nbt.NBTTagCompound;


/**
 * Listener for {@link NbtEntity} events.
 */
public interface NbtEntityListener {
    
    /**
     * Called when NBT data needs to be sent and later processed with
     * {@link NbtEntity#handleNetworkNbtEvent(NBTTagCompound)}.
     * 
     * @param tag the tag
     */
    void nbtEvent(NBTTagCompound tag);

}
