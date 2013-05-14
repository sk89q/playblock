package com.skcraft.playblock.client;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;

import com.skcraft.playblock.LKey;
import com.skcraft.playblock.PlayBlock;
import com.skcraft.playblock.SharedConfiguration;
import com.skcraft.playblock.SharedRuntime;
import com.skcraft.playblock.player.MediaManager;
import com.skcraft.playblock.projector.ProjectorGui;
import com.skcraft.playblock.projector.ProjectorRenderer;
import com.skcraft.playblock.projector.ProjectorTileEntity;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

/**
 * Client-side initialization.
 */
public class ClientRuntime extends SharedRuntime {
    
    private MediaManager manager;
    private SharedConfiguration options;
    
    /**
     * Get the media manager.
     * 
     * @return the media manager
     */
    public MediaManager getMediaManager() {
        return manager;
    }
    
    /**
     * Gets the client options.
     * 
     * @return the client options.
     */
    public SharedConfiguration getClientOptions() {
        return options;
    }
    
    @Override
    public void load(FMLInitializationEvent event) {
        super.load(event);
        options = new SharedConfiguration("PlayBlockSettings.txt");
        manager = new MediaManager();

        // Bind the key
        KeyBinding[] key = { new KeyBinding(LKey.PLAYBLOCK_OPTIONS.toString(), Keyboard.KEY_F4) };
        boolean[] repeating = { false };
        KeyBindingRegistry.registerKeyBinding(new GlobalKeyHandler(key, repeating));

        ClientRegistry.bindTileEntitySpecialRenderer(
                ProjectorTileEntity.class, new ProjectorRenderer(manager));
        
        TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);
        
        getClientOptions().save();
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
