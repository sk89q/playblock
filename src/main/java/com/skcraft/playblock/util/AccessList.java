package com.skcraft.playblock.util;

import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Privates a very simple way to see who may have access to a block, depending
 * on the right click mechanic on blocks being blocked.
 */
public class AccessList {

    private static final int MAX_ACCESS_TIME = 1000 * 60 * 15;

    private Map<String, Long> allowed = new HashMap<String, Long>();

    public void allow(String name) {
        long now = System.currentTimeMillis();

        // Removed old entries
        Iterator<Map.Entry<String, Long>> it = allowed.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Long> entry = it.next();

            if (now - entry.getValue() > MAX_ACCESS_TIME) {
                it.remove();
            }
        }

        allowed.put(name, now);
    }

    public void allow(EntityPlayer player) {
        allow(player.getGameProfile().getId().toString());
    }

    public boolean checkAndForget(String name) {
        long now = System.currentTimeMillis();
        Long since = allowed.remove(name);

        if (since == null) {
            return false;
        }

        if (now - since > MAX_ACCESS_TIME) {
            return false;
        }

        return true;
    }

    public boolean checkAndForget(EntityPlayer player) {
        return checkAndForget(player.getGameProfile().getId().toString());
    }

}
