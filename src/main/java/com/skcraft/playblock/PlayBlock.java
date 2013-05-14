package com.skcraft.playblock;

import java.util.logging.Level;

import com.skcraft.playblock.client.ClientRuntime;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarted;
import cpw.mods.fml.common.Mod.ServerStopping;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = "com.skcraft.PlayBlock", name = "PlayBlock", version = "XX", useMetadata = true)
@NetworkMod(clientSideRequired = true, serverSideRequired = false,
    channels = PlayBlock.CHANNEL_ID, packetHandler = PacketHandler.class)
public class PlayBlock {

    public static final String LOGGER_ID = "PlayBlock";
    public static final String CHANNEL_ID = "PlayBlock";
    
    @Instance
    public static PlayBlock instance;

    @SidedProxy(serverSide = "com.skcraft.playblock.ServerRuntime",
                clientSide = "com.skcraft.playblock.client.ClientRuntime")
    public static SharedRuntime runtime;

    @PreInit
    public void preInit(FMLPreInitializationEvent event) {
        runtime.preInit(event);
    }

    @Init
    public void load(FMLInitializationEvent event) {
        runtime.load(event);
    }

    @ServerStarted
    public void serverStarted(FMLServerStartedEvent event) {
        runtime.serverStarted(event);
    }

    @ServerStopping
    public void serverStopping(FMLServerStoppingEvent event) {
        runtime.serverStopping(event);
    }

    /**
     * Get the runtime, which may be either a {@link ClientRuntime} or
     * a {@link SharedRuntime}.
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
    
    public static void logf(Level level, String format, Object ... data) {
        FMLLog.log(LOGGER_ID, level, format, data);
    }
    
    public static void logf(Level level, Throwable t, String format, Object ... data) {
        FMLLog.log(LOGGER_ID, level, t, format, data);
    }
    
    public static void log(Level level, String message, Throwable t) {
        logf(level, t, "%s", message);
    }
    
    public static void log(Level level, String message) {
        logf(level, "%s", message);
    }

}
