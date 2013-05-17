package com.skcraft.playblock;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.sk89q.forge.PayloadReceiver;
import com.sk89q.forge.TileEntityPayload;
import com.skcraft.playblock.network.PlayBlockPayload;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

/**
 * Handles packets for PlayBlock.
 * 
 * <p>Currently the packets only go client to server.</p>
 */
public class PacketHandler implements IPacketHandler {

    @Override
    public void onPacketData(INetworkManager manager,
            Packet250CustomPayload packet, Player player) {
        
        EntityPlayer entityPlayer;
        World world;
        
        // Get the player and world
        if (player instanceof EntityPlayer) {
            entityPlayer = ((EntityPlayer) player);
            world = entityPlayer.worldObj;
        } else {
            PlayBlock.log(Level.WARNING, "Expected EntityPlayer but got "
                    + player.getClass().getCanonicalName());
            return;
        }
        
        try {
            DataInputStream in = new DataInputStream(
                    new ByteArrayInputStream(packet.data));
            
            // Read the container
            PlayBlockPayload container = new PlayBlockPayload();
            container.read(in);
            
            // Figure out what we are containing
            switch (container.getType()) {
            case TILE_ENTITY:
                // It's a tile entity!
                TileEntityPayload tileContainer = new TileEntityPayload();
                tileContainer.read(in);

                // Get the tile and have it accept this payload
                int x = tileContainer.getX();
                int y = tileContainer.getY();
                int z = tileContainer.getZ();

                // We need to check if the chunk exists, otherwise an update packet
                // could be used to overload the server by loading/generating chunks
                if (world.blockExists(x, y, z)) {
                    TileEntity tile = world.getBlockTileEntity(x, y, z);
                    
                    if (tile instanceof PayloadReceiver) {
                        ((PayloadReceiver) tile)
                                .readPayload(entityPlayer, in);
                    }
                } else {
                    PlayBlock.log(Level.WARNING,
                            "Got update packet for non-existent chunk/block from " +
                                    entityPlayer.username);
                }
            }
        } catch (IOException e) {
            PlayBlock.log(Level.WARNING, "Failed to read packet data from " +
                                    entityPlayer.username, e);
        }
    }
    
    /**
     * Send a payload to the server.
     * 
     * @param payload the payload
     */
    public static void sendToServer(PlayBlockPayload payload) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bytes);

        try {
            payload.write(out);
            out.flush();
        } catch (IOException e) {
            PlayBlock.log(Level.WARNING, "Failed to send packet to the server");
            return;
        }

        Packet250CustomPayload packet = new Packet250CustomPayload(
                PlayBlock.CHANNEL_ID, bytes.toByteArray());
        
        PacketDispatcher.sendPacketToServer(packet);
    }

    public static void sendToClient(PlayBlockPayload payload, List<EntityPlayer> players) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bytes);

        try {
            payload.write(out);
            out.flush();
        } catch (IOException e) {
            PlayBlock.log(Level.WARNING, "Failed to build packet to send to the client");
            return;
        }

        Packet250CustomPayload packet = new Packet250CustomPayload(
                PlayBlock.CHANNEL_ID, bytes.toByteArray());
        
        if (players == null) {
            PacketDispatcher.sendPacketToAllPlayers(packet);
        } else {
            for (EntityPlayer player : players) {
                PacketDispatcher.sendPacketToPlayer(packet, 
                        ((Player) player));
            }
        }
    }

}
