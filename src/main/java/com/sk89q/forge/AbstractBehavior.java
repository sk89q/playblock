package com.sk89q.forge;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;


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
    public void readPayload(EntityPlayer player, BehaviorPayload payload,
            DataInputStream in) throws IOException {
    }

    @Override
    public void handleNBTEvent(NBTTagCompound tag) {
    }

    public void fireNbtEvent(NBTTagCompound tag) {
        for (BehaviorListener listener : listeners) {
            listener.nbtEvent(tag);
        }
    }

    public void firePayloadSend(BehaviorPayload payload, List<EntityPlayer> player) {
        for (BehaviorListener listener : listeners) {
            listener.payloadSend(payload, player);
        }
    }

    @Override
    public void addBehaviorListener(BehaviorListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeBehaviorListener(BehaviorListener listener) {
        listeners.remove(listener);
    }

}
