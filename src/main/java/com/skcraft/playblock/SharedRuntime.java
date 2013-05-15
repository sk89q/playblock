package com.skcraft.playblock;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;

import com.skcraft.playblock.media.MediaResolver;
import com.skcraft.playblock.media.QueueManager;
import com.skcraft.playblock.media.QueueSupervisor;
import com.skcraft.playblock.projector.ProjectorBlock;
import com.skcraft.playblock.projector.ProjectorRemoteItem;
import com.skcraft.playblock.projector.ProjectorTileEntity;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

/**
 * Initializes everything.
 */
public class SharedRuntime {

    private Block projectorBlock;
    private Item projectorRemoteItem;
    private QueueManager queueManager;
    private QueueSupervisor queueSupervisor;
    private MediaResolver mediaResolver;
    private SharedConfiguration config;
    
    /**
     * Gets the configuration.
     * 
     * @return the configuration
     */
    public SharedConfiguration getConfig() {
        return config;
    }

    /**
     * Called on FML pre-initialization.
     * 
     * @param event the event
     */
    public void preInit(FMLPreInitializationEvent event) {
        config = new SharedConfiguration("PlayBlock.cfg");
    }

    /**
     * Called on FML initialization.
     * 
     * @param event the event
     */
    public void load(FMLInitializationEvent event) {
        queueManager = new QueueManager();
        mediaResolver = new MediaResolver();
        
        projectorBlock = new ProjectorBlock(getConfig().getInt("playblock.id", 3400), 0, Material.iron)
                .setHardness(0.5F).setStepSound(Block.soundGlassFootstep)
                .setLightValue(1.0F)
                .setBlockName(ProjectorBlock.INTERNAL_NAME)
                .setCreativeTab(CreativeTabs.tabMisc);

        GameRegistry.registerBlock(projectorBlock, ProjectorBlock.INTERNAL_NAME);
        GameRegistry.registerTileEntity(ProjectorTileEntity.class,
                ProjectorTileEntity.INTERNAL_NAME);
        LanguageRegistry.addName(projectorBlock, "PlayBlock Projector");
        
        projectorRemoteItem = new ProjectorRemoteItem(getConfig().getInt("playremote.id", 15000));
        
        getConfig().save();
    }

    /**
     * Called on server start.
     * 
     * @param event the event
     */
    public void serverStarted(FMLServerStartedEvent event) {
    }


    /**
     * Called on server stop.
     * 
     * @param event the event
     */
    public void serverStopping(FMLServerStoppingEvent event) {
    }

    public QueueManager getQueueManager() {
        return queueManager;
    }

    public MediaResolver getMediaResolver() {
        return mediaResolver;
    }

    public QueueSupervisor getQueueSupervisor() {
        return queueSupervisor;
    }

    public void setQueueSupervisor(QueueSupervisor queueSupervisor) {
        this.queueSupervisor = queueSupervisor;
    }

    public void showProjectorGui(EntityPlayer player, ProjectorTileEntity tileEntity) {
        // Overriden on the client
    }

}
