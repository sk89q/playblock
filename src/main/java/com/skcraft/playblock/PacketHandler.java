package com.skcraft.playblock;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

/**
 * Handles packets for PlayBlock.
 * 
 * <p>
 * Currently the packets only go client to server.
 * </p>
 */
public class PacketHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(PlayBlock.CHANNEL_ID);

    public static void registerMessages() {
        // TODO: rewrite networking system for 1.7
    }

    // @Override
    // public void onPacketData(INetworkManager manager,
    // Packet250CustomPayload packet, Player player) {
    //
    // EntityPlayer entityPlayer;
    // World world;
    //
    // // Get the player and world
    // if (player instanceof EntityPlayer) {
    // entityPlayer = ((EntityPlayer) player);
    // world = entityPlayer.worldObj;
    // } else {
    // PlayBlock.log(Level.WARNING, "Expected EntityPlayer but got "
    // + player.getClass().getCanonicalName());
    // return;
    // }
    //
    // try {
    // DataInputStream in = new DataInputStream(
    // new ByteArrayInputStream(packet.data));
    //
    // // Read the container
    // PlayBlockPayload container = new PlayBlockPayload();
    // container.read(in);
    //
    // // Figure out what we are containing
    // switch (container.getType()) {
    // case TILE_ENTITY:
    // // It's a tile entity!
    // TileEntityPayload tileContainer = new TileEntityPayload();
    // tileContainer.read(in);
    //
    // // Get the tile and have it accept this payload
    // int x = tileContainer.getX();
    // int y = tileContainer.getY();
    // int z = tileContainer.getZ();
    //
    // // We need to check if the chunk exists, otherwise an update packet
    // // could be used to overload the server by loading/generating chunks
    // if (world.blockExists(x, y, z)) {
    // TileEntity tile = world.getBlockTileEntity(x, y, z);
    //
    // if (tile instanceof PayloadReceiver) {
    // ((PayloadReceiver) tile)
    // .readPayload(entityPlayer, in);
    // }
    // } else {
    // PlayBlock.log(Level.WARNING,
    // "Got update packet for non-existent chunk/block from " +
    // entityPlayer.username);
    // }
    // }
    // } catch (IOException e) {
    // PlayBlock.log(Level.WARNING, "Failed to read packet data from " +
    // entityPlayer.username, e);
    // }
    // }

}
