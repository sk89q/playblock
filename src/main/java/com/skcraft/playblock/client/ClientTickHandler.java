package com.skcraft.playblock.client;

import java.util.EnumSet;

import com.skcraft.playblock.PlayBlock;

import net.minecraft.world.World;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ClientTickHandler implements ITickHandler {
    
    private World lastWorld;
    
    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {
        World world = FMLClientHandler.instance().getClient().theWorld;
        
        //This will release all renderers on world change. This includes
        //dimension change and logging out(world -> null).
        if(world != lastWorld) {
            if(lastWorld != null) {
                PlayBlock.getClientRuntime().getMediaManager().releaseAll();
            }
            lastWorld = world;
        }
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {
    }

    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.CLIENT);
    }

    @Override
    public String getLabel() {
        return "PlayBlock";
    }

}
