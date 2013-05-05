package com.skcraft.playblock;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.logging.Level;

import com.skcraft.playblock.player.ProjectorTileEntity;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

/**
 * Handles packets for PlayBlock.
 * 
 * <p>Currently the packets only go client to server.</p>
 */
public class ServerPacketHandler implements IPacketHandler {

    @Override
    public void onPacketData(INetworkManager manager,
            Packet250CustomPayload packet, Player player) {

        DataInputStream stream = new DataInputStream(
                new ByteArrayInputStream(packet.data));
        
        int x = 0;
        int y = 0;
        int z = 0;
        
        try {
            x = stream.readInt();
            y = stream.readInt();
            z = stream.readInt();
        } catch (Throwable t) {
            PlayBlock.log(Level.WARNING, "Failed to read packet data", t);
        }
        
        if (player instanceof EntityPlayerMP) {
            EntityPlayerMP entityPlayer = ((EntityPlayerMP) player);
            World world = entityPlayer.worldObj;
            TileEntity tile = world.getBlockTileEntity(x, y, z);
    
            if (tile instanceof ProjectorTileEntity) {
                ((ProjectorTileEntity) tile).acceptClientUpdate(entityPlayer, stream);
            }
        } else {
            PlayBlock.log(Level.WARNING, "Expected EntityPlayerMP but got "
                    + player.getClass().getCanonicalName());
        }
    }

}
