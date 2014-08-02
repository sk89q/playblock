package com.sk89q.forge;

import io.netty.buffer.ByteBufInputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

/**
 * An abstract implementation of {@link Behavior} that allows the registration
 * of listeners and do-nothing implementations of all of {@link Behavior}'s
 * methods.
 */
public abstract class AbstractBehavior implements Behavior {

    private List<BehaviorListener> listeners = new ArrayList<BehaviorListener>();

    @Override
    public void writeSaveNBT(NBTTagCompound tag) {
    }

    @Override
    public void readSaveNBT(NBTTagCompound tag) {
    }

    @Override
    public void writeNetworkedNBT(NBTTagCompound tag) {
    }

    @Override
    public void readNetworkedNBT(NBTTagCompound tag) {
    }

    @Override
    public void readPayload(EntityPlayer player, BehaviorPayload payload, ByteBufInputStream in) throws IOException {
    }

    @Override
    public void addBehaviorListener(BehaviorListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeBehaviorListener(BehaviorListener listener) {
        listeners.remove(listener);
    }

    /**
     * Fire a networked NBT event that is to be handled by listeners.
     * 
     * @param tag
     *            the tag
     */
    public void fireNetworkedNbt(NBTTagCompound tag) {
        for (BehaviorListener listener : listeners) {
            listener.networkedNbt(tag);
        }
    }

    /**
     * Fire a payload send event that is handled by listeners.
     * 
     * @param payload
     *            the payload
     * @param players
     *            the player(s)
     * @see BehaviorListener#payloadSend(BehaviorPayload, List) for more
     *      information
     */
    public void firePayloadSend(BehaviorPayload payload, List<EntityPlayer> players) {
        for (BehaviorListener listener : listeners) {
            listener.payloadSend(payload, players);
        }
    }

}
