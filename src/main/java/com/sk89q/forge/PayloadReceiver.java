package com.sk89q.forge;

import java.io.DataInputStream;
import java.io.IOException;

import io.netty.buffer.ByteBufInputStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Indicates an object that will receive a payload targeted for it.
 */
public interface PayloadReceiver {

    /**
     * Process the payload, which will come from the client.
     * 
     * @param player
     *            the player
     * @param in
     *            stream
     * @throws IOException
     *             on I/O error
     */
    void readPayload(EntityPlayer player, ByteBufInputStream in) throws IOException;

}
