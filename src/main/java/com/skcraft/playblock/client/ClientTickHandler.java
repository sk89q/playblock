package com.skcraft.playblock.client;

import net.minecraft.world.World;

import com.skcraft.playblock.PlayBlock;

import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

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
