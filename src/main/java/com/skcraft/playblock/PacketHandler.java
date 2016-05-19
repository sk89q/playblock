package com.skcraft.playblock;

import com.sk89q.forge.PayloadReceiver;
import com.sk89q.forge.TileEntityPayload;
import com.skcraft.playblock.network.PlayBlockPayload;
import com.skcraft.playblock.projector.TileEntityProjector;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.util.List;

/**
 * Handles packets for PlayBlock.
 */
public class PacketHandler {

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onReceiveClient(FMLNetworkEvent.ClientCustomPacketEvent evt) {
        World world = Minecraft.getMinecraft().theWorld;
        EntityPlayer entityPlayer = Minecraft.getMinecraft().thePlayer;
        try {
            ByteBufInputStream in = new ByteBufInputStream(evt.getPacket().payload());

            // Read the container
            PlayBlockPayload container = new PlayBlockPayload();
            container.read(in);

            // Figure out what we are containing
            switch (container.getType()) {
            case TILE_ENTITY:
                handleTilePayload(world, entityPlayer, in);
                break;
            case TILE_ENTITY_NBT:
                handleNetworkedNBT(world, evt.getPacket().payload());
            }
        } catch (IOException e) {
            PlayBlock.log(Level.WARN, "Failed to read packet data from " + entityPlayer.getDisplayName(), e);
        }
    }

    @SubscribeEvent
    public void onReceiveServer(FMLNetworkEvent.ServerCustomPacketEvent evt) {
        EntityPlayer entityPlayer = ((NetHandlerPlayServer) evt.getHandler()).playerEntity;
        World world;

        // Get the world
        if (entityPlayer != null) {
            world = entityPlayer.worldObj;
        } else {
            PlayBlock.log(Level.WARN, "Received an update packet from an invalid player!");
            return;
        }

        try {
            ByteBufInputStream in = new ByteBufInputStream(evt.getPacket().payload());

            // Read the container
            PlayBlockPayload container = new PlayBlockPayload();
            container.read(in);

            // Figure out what we are containing
            switch (container.getType()) {
            case TILE_ENTITY:
                handleTilePayload(world, entityPlayer, in);
                break;
            case TILE_ENTITY_NBT:
                handleNetworkedNBT(world, evt.getPacket().payload());
            }
        } catch (IOException e) {
            PlayBlock.log(Level.WARN, "Failed to read packet data from " + entityPlayer.getDisplayName(), e);
        }
    }

    public void handleTilePayload(World world, EntityPlayer player, ByteBufInputStream in) throws IOException {
        TileEntityPayload tileContainer = new TileEntityPayload();
        tileContainer.read(in);

        // Get the tile and have it accept this payload
        int x = tileContainer.getX();
        int y = tileContainer.getY();
        int z = tileContainer.getZ();

        // We need to check if the chunk exists, otherwise an update packet
        // could be used to overload the server by loading/generating chunks
        if (world.getChunkProvider().getLoadedChunk(x >> 4, z >> 4) != null) {
            TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));

            if (tile instanceof PayloadReceiver) {
                ((PayloadReceiver) tile).readPayload(player, in);
            }
        } else {
            PlayBlock.log(Level.WARN, "Got update packet for non-existent chunk/block from " + player.getDisplayName());
        }
    }

    public void handleNetworkedNBT(World world, ByteBuf bytes) {
        NBTTagCompound tag = ByteBufUtils.readTag(bytes);
        int x = tag.getInteger("x");
        int y = tag.getInteger("y");
        int z = tag.getInteger("z");

        if (world.getChunkProvider().getLoadedChunk(x >> 4, z >> 4) != null) {
            TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));

            if (tile instanceof TileEntityProjector) {
                TileEntityProjector projector = (TileEntityProjector) tile;
                projector.getBehaviors().readNetworkedNBT(tag);
            }
        } else {
            PlayBlock.log(Level.WARN, "Got update packet for non-existent chunk/block!");
        }
    }

    /**
     * Send a payload to the server.
     * 
     * @param payload
     *            the payload
     */
    public static void sendToServer(PlayBlockPayload payload) {
        ByteBufOutputStream out = new ByteBufOutputStream(Unpooled.buffer());

        try {
            payload.write(out);
            out.flush();
        } catch (IOException e) {
            PlayBlock.log(Level.WARN, "Failed to send packet to the server");
            return;
        }

        FMLProxyPacket packet = new FMLProxyPacket(new PacketBuffer(out.buffer()), PlayBlock.CHANNEL_ID);
        SharedRuntime.networkWrapper.sendToServer(packet);
    }

    public static void sendToClient(PlayBlockPayload payload, List<EntityPlayer> players) {
        ByteBufOutputStream out = new ByteBufOutputStream(Unpooled.buffer());

        try {
            payload.write(out);
            out.flush();
        } catch (IOException e) {
            PlayBlock.log(Level.WARN, "Failed to build packet to send to the client");
            return;
        }

        FMLProxyPacket packet = new FMLProxyPacket(new PacketBuffer(out.buffer()), PlayBlock.CHANNEL_ID);

        if (players == null) {
            SharedRuntime.networkWrapper.sendToAll(packet);
        } else {
            for (EntityPlayer player : players) {
                if (player instanceof EntityPlayerMP) {
                    SharedRuntime.networkWrapper.sendTo(packet, (EntityPlayerMP) player);
                }
            }
        }
    }
}
