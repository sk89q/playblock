package com.skcraft.playblock;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;

import com.sk89q.forge.ResponseTracker;
import com.skcraft.playblock.media.MediaResolver;
import com.skcraft.playblock.projector.ProjectorBlock;
import com.skcraft.playblock.projector.ProjectorTileEntity;
import com.skcraft.playblock.projector.RemoteItem;
import com.skcraft.playblock.queue.ExposedQueue;
import com.skcraft.playblock.queue.QueueManager;
import com.skcraft.playblock.queue.QueueSupervisor;
import com.skcraft.playblock.queue.SimpleQueueSupervisor;

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

    private ResponseTracker tracker = new ResponseTracker();
    private Block projectorBlock;
    private Item projectorRemoteItem;
    private QueueManager queueManager;
    private QueueSupervisor queueSupervisor;
    private MediaResolver mediaResolver;
    private SharedConfiguration config;
    
    /**
     * Get the response tracker.
     * 
     * @return the tracker
     */
    public ResponseTracker getTracker() {
        return tracker;
    }

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
        queueSupervisor = new SimpleQueueSupervisor(mediaResolver);
        
        projectorBlock = new ProjectorBlock(getConfig().getInt("playblock.id", 3400), Material.iron);

        GameRegistry.registerBlock(projectorBlock, ProjectorBlock.INTERNAL_NAME);
        GameRegistry.registerTileEntity(ProjectorTileEntity.class,
                ProjectorTileEntity.INTERNAL_NAME);
        LanguageRegistry.addName(projectorBlock, "PlayBlock Projector");
        
        projectorRemoteItem = new RemoteItem(getConfig().getInt("playremote.id", 15000));
        LanguageRegistry.addName(projectorRemoteItem, "PlayBlock Remote");
        
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

    public void showRemoteGui(EntityPlayer player, ExposedQueue queuable) {
        // Overriden on the client
    }

}
