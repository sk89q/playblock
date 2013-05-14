package com.sk89q.forge;

import java.util.ArrayList;
import java.util.List;


import net.minecraft.nbt.NBTTagCompound;


public abstract class AbstractNbtEntity implements NbtEntity {
    
    private List<NbtEntityListener> listeners = new ArrayList<NbtEntityListener>();

    public void fireNbtEvent(NBTTagCompound tag) {
        for (NbtEntityListener listener : listeners) {
            listener.nbtEvent(tag);
        }
    }

    @Override
    public void addNbtEntityListener(NbtEntityListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeNbtEntityListener(NbtEntityListener listener) {
        listeners.remove(listener);
    }

}
