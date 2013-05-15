package com.sk89q.forge;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Manages a list of {@link Behavior}s.
 */
public class BehaviorList implements Collection<Behavior>, Behavior, BehaviorListener {
    
    private List<Behavior> list = new ArrayList<Behavior>();
    private List<BehaviorListener> listeners = new ArrayList<BehaviorListener>();

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<Behavior> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(Behavior e) {
        e.addBehaviorListener(this);
        return list.add(e);
    }

    @Override
    public boolean remove(Object o) {
        if (o instanceof Behavior) {
            ((Behavior) o).removeBehaviorListener(this);
        }
        return list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Behavior> c) {
        boolean changed = false;
        for (Behavior e : c) {
            changed = add(e) || changed;
        }
        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        for (Object e : c) {
            if (e instanceof Behavior) {
                changed = remove(e) || changed;
            }
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean equals(Object o) {
        return list.equals(o);
    }

    @Override
    public int hashCode() {
        return list.hashCode();
    }

    @Override
    public void writeSaveNBT(NBTTagCompound tag) {
        for (Behavior entity : this) {
            entity.writeSaveNBT(tag);
        }
    }

    @Override
    public void readSaveNBT(NBTTagCompound tag) {
        for (Behavior entity : this) {
            entity.readSaveNBT(tag);
        }
    }

    @Override
    public void writeNetworkedNBT(NBTTagCompound tag) {
        for (Behavior entity : this) {
            entity.writeNetworkedNBT(tag);
        }
    }

    @Override
    public void readNetworkedNBT(NBTTagCompound tag) {
        for (Behavior entity : this) {
            entity.readNetworkedNBT(tag);
        }
    }

    @Override
    public void handleNBTEvent(NBTTagCompound tag) {
        for (Behavior entity : this) {
            entity.handleNBTEvent(tag);
        }
    }

    @Override
    public void readPayload(EntityPlayer player, BehaviorPayload payload,
            DataInputStream in) throws IOException {
        for (Behavior entity : this) {
            entity.readPayload(player, payload, in);
        }
    }

    @Override
    public void nbtEvent(NBTTagCompound tag) {
        for (BehaviorListener listener : listeners) {
            listener.nbtEvent(tag);
        }
    }

    @Override
    public void payloadSend(BehaviorPayload payload, List<EntityPlayer> player) {
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
