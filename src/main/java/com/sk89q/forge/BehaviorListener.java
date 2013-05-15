package com.sk89q.forge;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Listener for {@link Behavior} events.
 * 
 * <p>Users of {@link Behavior} should add itself as a listener for these events,
 * otherwise {@link Behavior}s may not function properly.</p>
 */
public interface BehaviorListener {
    
    /**
     * Called when NBT data needs to be sent across the network barrier and later
     * handled with {@link Behavior#handleNBTEvent(NBTTagCompound)}.
     * 
     * @see Behavior for an important discussion how the given data is shared
     * @param tag the tag
     */
    void nbtEvent(NBTTagCompound tag);
    
    /**
     * Called when a payload needs to be sent specifically to the same {@link Behavior}
     * on the other side of the connection (i.e. server->client).
     * 
     * @param payload the payload
     * @param player a list of players to send to, otherwise null 
     *               to broadcast appropriately to all parties that should receive it
     */
    void payloadSend(BehaviorPayload payload, List<EntityPlayer> player);

}
