package com.sk89q.forge;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Listener for {@link Behavior} events.
 * 
 * <p>
 * Users of {@link Behavior} should add itself as a listener for these events,
 * otherwise {@link Behavior}s may not function properly.
 * </p>
 */
public interface BehaviorListener {

    /**
     * Sends a new network state packet that will later be received by
     * {@link Behavior#readNetworkedNBT(NBTTagCompound)}.
     * 
     * <p>
     * The packet can contain only partial data. It is the responsibility of
     * implementations of {@link Behavior#readNetworkedNBT(NBTTagCompound)} to
     * be aware of this.
     * </p>
     * 
     * @see Behavior for an important discussion how the given data is shared
     * @param tag
     *            the tag
     */
    void networkedNbt(NBTTagCompound tag);

    /**
     * Called when a payload needs to be sent specifically to the same
     * {@link Behavior} on the other side of the connection (i.e.
     * server->client).
     * 
     * @param payload
     *            the payload
     * @param players
     *            a list of players to send to, otherwise null to broadcast
     *            appropriately to all parties that should receive it
     */
    void payloadSend(BehaviorPayload payload, List<EntityPlayer> players);

}
