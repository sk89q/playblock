package com.skcraft.playblock.player;

import static com.skcraft.playblock.util.EnvUtils.getPlatform;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.Level;

import uk.co.caprica.vlcj.player.MediaPlayerFactory;

import com.skcraft.playblock.PlayBlock;
import com.skcraft.playblock.util.EnvUtils;
import com.skcraft.playblock.util.EnvUtils.Arch;
import com.skcraft.playblock.util.EnvUtils.Platform;
import com.skcraft.playblock.util.PlayBlockPaths;
import com.sun.jna.NativeLibrary;

/**
 * Manages media player instances.
 */
public class MediaManager {

    private static final String[] INSTALL_MESSAGE = { "To view this video,", "install " + (EnvUtils.getJvmArch() == Arch.X86_64 ? "64-bit" : "32-bit") + " VLC", "by pressing F4.", };

    private final ExecutorService executor;
    private TextureCache textureCache = new TextureCache();
    private MediaPlayerFactory factory;
    private MediaRenderer activeRenderer;
    private float volume = 1;
    private EmbeddedInstaller installer;

    // Simple texture cache

    static {
        // In order to find VLC, we need to build a list of search paths,
        // which is not easy as it seems!
        for (File file : PlayBlockPaths.getSearchPaths()) {
            NativeLibrary.addSearchPath("libvlc", file.getAbsolutePath());
        }
    }

    /**
     * Construct a new media manager.
     */
    public MediaManager() {
        // In order to prevent freezing when changing media, the player will be
        // called from this dedicated thread
        executor = Executors.newFixedThreadPool(1);

        try {
            factory = new MediaPlayerFactory(getFactoryOptions());
        } catch (Throwable t) {
            PlayBlock.log(Level.WARN, "Failed to find VLC!", t);
        }

        volume = PlayBlock.getClientRuntime().getClientOptions().getFloat("volume", 1);
    }

    /**
     * Returns whether the VLC library loaded successfully.
     * 
     * @return true if it is available
     */
    public boolean isSupported() {
        return factory != null;
    }

    /**
     * Get the in-game installer.
     * 
     * @return the installer
     */
    public EmbeddedInstaller getInstaller() {
        if (installer == null) {
            installer = new EmbeddedInstaller();
        }

        return installer;
    }

    /**
     * Get the message shown on the screen for unsupported usres.
     * 
     * @return a list of lines
     */
    public String[] getUnsupportedMessage() {
        switch (getInstaller().getState()) {
        case NOT_INSTALLING:
            return INSTALL_MESSAGE;
        default:
            return new String[] { getInstaller().getStatusMessage() };
        }
    }

    /**
     * Generate the VLC options required.
     * 
     * @return the list of options
     */
    private String[] getFactoryOptions() {
        List<String> options = new ArrayList<String>();

        // Don't show a title on the video when media is played
        options.add("--no-video-title-show");

        // Mac OS X support
        if (getPlatform() == Platform.MAC_OS_X) {
            options.add("--vout=macosx");
        }

        String[] arr = new String[options.size()];
        options.toArray(arr);
        return arr;
    }

    /**
     * Returns whether a new player can be acquired and started.
     * 
     * <p>
     * For performance reasons, only one player can be playing at a time.
     * </p>
     * 
     * @return true true if there is a free player available
     */
    public boolean hasNoRenderer() {
        return activeRenderer == null;
    }

    /**
     * Acquire a new renderer and set it as the active renderer.
     * 
     * @param width
     *            the width of the video
     * @param height
     *            the height of the video
     * @return the renderer
     */
    public MediaRenderer acquireRenderer(int width, int height) {
        if (activeRenderer != null) {
            release(activeRenderer);
        }

        activeRenderer = newRenderer(width, height);
        return activeRenderer;
    }

    /**
     * Create a new renderer to render media on.
     * 
     * <p>
     * The rendered is tracked using this instance and it can be released
     * selectively or all together.
     * </p>
     * 
     * @param width
     *            the width of the player
     * @param height
     *            the height of the player
     * @return a new media renderer
     */
    protected MediaRenderer newRenderer(final int width, final int height) {
        if (!isSupported()) {
            return null;
        }

        int textureIndex = textureCache.createTexture(width, height);

        // Create and initialize the renderer
        MediaRenderer instance = new MediaRenderer(this, width, height, textureIndex);
        instance.initialize(factory, getVolume());

        return instance;
    }

    /**
     * Frees up a renderer.
     * 
     * @param instance
     *            the renderer
     */
    public void release(final MediaRenderer instance) {
        if (!isSupported()) {
            return;
        }

        if (instance.isReleased()) {
            return; // Don't re-release
        }

        // For thread safety reasons, mark the release flag
        instance.markForRelease();

        // Start releasing -- this does not block
        instance.release();

        // Release the texture for the screen
        int textureIndex = instance.getTextureIndex();
        if (textureIndex > 0) {
            textureCache.deleteTexture(textureIndex);
        }

        // Clear the active renderer
        if (instance == activeRenderer) {
            activeRenderer = null;
        }
    }

    /**
     * Execute a given {@link Runnable} in the dedicated thread for interacting
     * with VLC.
     * 
     * @param runnable
     *            the object to run
     */
    void executeThreadSafe(Runnable runnable) {
        executor.execute(runnable);
    }

    /**
     * Release all known renderers.
     */
    public void releaseAll() {
        if (activeRenderer != null) {
            release(activeRenderer);
        }
    }

    /**
     * Unload the manager and also any renderers.
     */
    public void unload() {
        releaseAll();

        if (factory != null) {
            factory.release();
            factory = null;
        }
    }

    /**
     * Set the volume of the player.
     * 
     * @param volume
     *            the volume
     */
    public void setVolume(float volume) {
        this.volume = volume;

        if (activeRenderer != null) {
            activeRenderer.setVolume(volume);
        }
    }

    /**
     * Return the volume of the player.
     * 
     * @return the volume
     */
    public float getVolume() {
        return volume;
    }

}
