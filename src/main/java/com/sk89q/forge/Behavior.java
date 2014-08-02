package com.sk89q.forge;

import io.netty.buffer.ByteBufInputStream;

import java.io.EOFException;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

/**
 * An object that is managed so that its state can be easily saved to disk or
 * synchronized across a network barrier.
 * 
 * <p>
 * When a <tt>Behavior</tt> generates network events on one side of the
 * connection (for example, the server), those events should end up on the other
 * side and received by the same behavior on the other side. This means that the
 * same behavior could be applied to an entity, a tile entity, an item, or any
 * other object, but the behavior itself would not need to be aware of the
 * object managing the behavior.
 * </p>
 * 
 * <p>
 * For most of the events and calls, it is important to note that the same tag
 * is sent to every {@link Behavior} and it cannot be assumed that the given
 * data was meant specifically for this {@link Behavior}. Ideally, for NBT-based
 * events and method calls, tag key name collisions should be avoided by
 * choosing somewhat unique names for the NBT keys, but it is also important for
 * the managing object (the tile entity, entity, etc.) to not use conflicting
 * behaviors together, which is dependent on the programmer reading the
 * documentation/source appropriately. This shared approach to data is to reduce
 * the bandwidth overhead of tracking the owner of each set of data.
 * </p>
 * 
 * <p>
 * Objects managing <tt>Behavior</tt>s should make sure to add itself as a
 * {@link BehaviorListener} by calling
 * {@link #addBehaviorListener(BehaviorListener)}. If multiple
 * <tt>Behaviors</tt> need to be managed, consider using a {@link BehaviorList}.
 * </p>
 */
public interface Behavior {

    /**
     * Write NBT data that needs to be saved to the world.
     * 
     * <p>
     * This method can be used to write sensitive data that should not be made
     * available to the client.
     * </p>
     * 
     * @see Behavior for an important discussion how the given data is shared
     * @param tag
     *            the tag
     */
    void writeSaveNBT(NBTTagCompound tag);

    /**
     * Read NBT data that has been retrieved from a saved world.
     * 
     * <p>
     * This method does not need to validate data as if it had come from the
     * client, although some validation is required as world files could be
     * edited with a separate world editor, or corrupted via some means.
     * </p>
     * 
     * @see Behavior for an important discussion how the given data is shared
     * @param tag
     *            the tag
     */
    void readSaveNBT(NBTTagCompound tag);

    /**
     * Write the NBT tag that is going to be sent to the other side and handled
     * by a call to {@link #readNetworkedNBT(NBTTagCompound)} on the other side.
     * This tag is only called to derive a "full snapshot" of all necessary data
     * to restore the {@link Behavior}'s state from an empty slate.
     * 
     * @see Behavior for an important discussion how the given data is shared
     * @param tag
     *            the tag
     */
    void writeNetworkedNBT(NBTTagCompound tag);

    /**
     * Read the NBT tag that is going to be sent to the other side and handled
     * by a call to {@link #readNetworkedNBT(NBTTagCompound)} on the other side.
     * This tag is may called to derive a "full snapshot" of all necessary data
     * to restore the {@link Behavior}'s state from an empty slate, OR it may
     * only contain partial data as triggered by a call to
     * {@link BehaviorListener#networkedNbt(NBTTagCompound)}.
     * 
     * @see Behavior for an important discussion how the given data is shared
     * @param tag
     *            the tag
     */
    void readNetworkedNBT(NBTTagCompound tag);

    /**
     * Read a payload that is received from a client.
     * 
     * <p>
     * It is important to check the type of payload as multiple {@link Behavior}
     * s will all receive the same payload. Therefore, multiple {@link Behavior}
     * s cannot handle the same payload, or a {@link EOFException} will be
     * thrown.
     * </p>
     * 
     * @param player
     *            the player
     * @param payload
     *            the payload
     * @param in
     *            the input stream
     * @throws IOException
     *             thrown on I/O error
     */
    void readPayload(EntityPlayer player, BehaviorPayload payload, ByteBufInputStream in) throws IOException;

    /**
     * Add a listener.
     * 
     * @param listener
     *            the listener
     */
    void addBehaviorListener(BehaviorListener listener);

    /**
     * Remove a listener.
     * 
     * @param listener
     *            the listener
     */
    void removeBehaviorListener(BehaviorListener listener);

}
