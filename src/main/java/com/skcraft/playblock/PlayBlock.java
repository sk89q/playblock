package com.skcraft.playblock;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import com.skcraft.playblock.client.ClientRuntime;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = PlayBlock.MOD_ID, name = PlayBlock.MOD_NAME)
public class PlayBlock {

    public static final String MOD_ID = "playblock";
    public static final String MOD_NAME = "PlayBlock";
    public static final String CHANNEL_ID = "PlayBlock";

    public static Logger log;

    @Instance
    public static PlayBlock instance;

    @SidedProxy(serverSide = "com.skcraft.playblock.SharedRuntime", clientSide = "com.skcraft.playblock.client.ClientRuntime")
    public static SharedRuntime runtime;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        log = event.getModLog();
        runtime.preInit(event);
    }

    @EventHandler
    public void load(FMLInitializationEvent event) {
        runtime.load(event);
    }

    @EventHandler
    public void serverStarted(FMLServerStartedEvent event) {
        runtime.serverStarted(event);
    }

    @EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        runtime.serverStopping(event);
    }

    /**
     * Get the runtime, which may be either a {@link ClientRuntime} or a
     * {@link SharedRuntime}.
     * 
     * @return the runtime
     */
    public static SharedRuntime getRuntime() {
        return runtime;
    }

    /**
     * Get the client runtime.
     * 
     * @return the client runtime
     */
    @SideOnly(Side.CLIENT)
    public static ClientRuntime getClientRuntime() {
        return (ClientRuntime) runtime;
    }

    public static void log(Level level, String message) {
        log.log(level, message);
    }

    public static void log(Level level, String message, Throwable t) {
        log.log(level, message);
        t.printStackTrace();
    }

    public static void logf(Level level, String format, Object... data) {
        String message = String.format(format, data);
        log(level, message);
    }

}
