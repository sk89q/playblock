package com.sk89q.forge;

import java.util.ArrayList;
import java.util.List;


import net.minecraft.nbt.NBTTagCompound;


public abstract class AbstractBehavior implements Behavior {
    
    private List<BehaviorListener> listeners = new ArrayList<BehaviorListener>();

    public void fireNbtEvent(NBTTagCompound tag) {
        for (BehaviorListener listener : listeners) {
            listener.nbtEvent(tag);
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
