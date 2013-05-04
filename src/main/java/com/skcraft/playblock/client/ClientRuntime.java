package com.skcraft.playblock.client;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;

import com.skcraft.playblock.PlayBlock;
import com.skcraft.playblock.SharedRuntime;
import com.skcraft.playblock.player.MediaManager;
import com.skcraft.playblock.player.ProjectorGui;
import com.skcraft.playblock.player.ProjectorRenderer;
import com.skcraft.playblock.player.ProjectorTileEntity;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

/**
 * Client-side initialization.
 */
public class ClientRuntime extends SharedRuntime {

    private final MediaManager manager = new MediaManager();

    /**
     * Get the media manager.
     * 
     * @return the media manager
     */
    public MediaManager getMediaManager() {
        return manager;
    }

    @Override
    public void load(FMLInitializationEvent event) {
        super.load(event);

        ClientRegistry.bindTileEntitySpecialRenderer(
                ProjectorTileEntity.class, new ProjectorRenderer(manager));
    }

    @Override
    public void serverStopping(FMLServerStoppingEvent event) {
        super.serverStopping(event);

        manager.releaseAll();
    }

    @Override
    public void showProjectorGui(EntityPlayer player, ProjectorTileEntity tileEntity) {
        FMLClientHandler.instance().displayGuiScreen(player, new ProjectorGui(tileEntity));
    }

}
