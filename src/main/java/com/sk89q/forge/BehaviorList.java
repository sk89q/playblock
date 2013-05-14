package com.sk89q.forge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Manages a list of {@link Behavior}s.
 */
public class BehaviorList implements List<Behavior>, Behavior, BehaviorListener {
    
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
        return list.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Behavior> c) {
        return list.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Behavior> c) {
        return list.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
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
    public Behavior get(int index) {
        return list.get(index);
    }

    @Override
    public Behavior set(int index, Behavior element) {
        return list.set(index, element);
    }

    @Override
    public void add(int index, Behavior element) {
        list.add(index, element);
    }

    @Override
    public Behavior remove(int index) {
        return list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<Behavior> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<Behavior> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List<Behavior> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    @Override
    public void toWorldSaveNbt(NBTTagCompound tag) {
        for (Behavior entity : this) {
            entity.toWorldSaveNbt(tag);
        }
    }

    @Override
    public void fromWorldSaveNbt(NBTTagCompound tag) {
        for (Behavior entity : this) {
            entity.fromWorldSaveNbt(tag);
        }
    }

    @Override
    public void toNetworkSnapshotNbt(NBTTagCompound tag) {
        for (Behavior entity : this) {
            entity.toNetworkSnapshotNbt(tag);
        }
    }

    @Override
    public void fromNetworkSnapshotNbt(NBTTagCompound tag) {
        for (Behavior entity : this) {
            entity.fromNetworkSnapshotNbt(tag);
        }
    }

    @Override
    public void handleNetworkNbtEvent(NBTTagCompound tag) {
        for (Behavior entity : this) {
            entity.handleNetworkNbtEvent(tag);
        }
    }

    @Override
    public void nbtEvent(NBTTagCompound tag) {
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
