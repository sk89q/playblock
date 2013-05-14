package com.sk89q.forge;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Listener for {@link Behavior} events.
 */
public interface BehaviorListener {
    
    /**
     * Called when NBT data needs to be sent and later processed with
     * {@link Behavior#handleNBTEvent(NBTTagCompound)}.
     * 
     * @param tag the tag
     */
    void nbtEvent(NBTTagCompound tag);

}
