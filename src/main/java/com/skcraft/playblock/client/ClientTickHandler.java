package com.skcraft.playblock.client;

import net.minecraft.world.World;

import com.skcraft.playblock.PlayBlock;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class ClientTickHandler {

    private static World lastWorld;

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        if (event.phase == Phase.START) {
            tickStart();
        }
    }

    public static void tickStart() {
        World world = FMLClientHandler.instance().getClient().theWorld;

        // This will release all renderers on world change. This includes
        // dimension change and logging out(world -> null).
        if (world != lastWorld) {
            if (lastWorld != null) {
                PlayBlock.getClientRuntime().getMediaManager().releaseAll();
            }
            lastWorld = world;
        }
    }

}
