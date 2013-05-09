package com.skcraft.playblock.util;

import static com.skcraft.playblock.util.EnvUtils.getProgramFiles;
import static com.skcraft.playblock.util.EnvUtils.getProgramFiles32;
import static com.skcraft.playblock.util.EnvUtils.isJvm64bit;
import static com.skcraft.playblock.util.EnvUtils.isMac;
import static com.skcraft.playblock.util.EnvUtils.isWindows;
import static com.skcraft.playblock.util.EnvUtils.join;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.client.EnumOSHelper;
import net.minecraft.client.Minecraft;

/**
 * Helps manage the various paths required for PlayBlock.
 */
public final class PlayBlockPaths {
    
    private static final String APP_DIR_NAME = "PlayBlock";

    private PlayBlockPaths() {
    }
    
    /**
     * Get the directory where the support files will reside on the system for
     * the current user of this computer.
     * 
     * @return the directory
     */
    public static File getApplicationDataDir() {
        File homeDir = new File(System.getProperty("user.home", "."));
        
        if (isWindows()) {
            String appData = System.getenv("APPDATA");

            if (appData != null) {
                return new File(appData, APP_DIR_NAME);
            } else {
                return new File(homeDir, APP_DIR_NAME);
            }
        } else if (isMac()) {
            return new File(homeDir, "Library/Application Support/" + APP_DIR_NAME);
        } else {
            return new File(homeDir, "." + APP_DIR_NAME.toLowerCase());
        }
    }
    
    /**
     * Gets the unmodified (often uppercase) version of a string if the current
     * platform is Windows or Mac OS X, otherwise make the string lowercase.
     * 
     * @param name the name
     * @return the new name
     */
    private static String getPlatformCasing(String name) {
        return (isWindows() || isMac()) ? name : name.toLowerCase();
    }
    
    /**
     * Get the path to the support libraries directory that contains further
     * sub-directories for each architecture.
     * 
     * @return the libraries directory
     */
    public static File getLibrariesDir() {
        return new File(getApplicationDataDir(), getPlatformCasing("Support"));
    }
    
    /**
     * Get the path to the support libraries directory that contains the native
     * library files.
     * 
     * @param is64Bit true if the directory for the 64-bit architecture is required
     * @return the libraries directory
     */
    public static File getNativeLibrariesDir(boolean is64Bit) {
        return new File(getLibrariesDir(), (is64Bit ? "x64" : "x32"));
    }
    
    /**
     * Get the path to the support libraries directory that contains the native
     * library files, based on the architecture of the running JVM.
     * 
     * @return the libraries directory
     */
    public static File getNativeLibrariesDir() {
        return getNativeLibrariesDir(EnvUtils.isJvm64bit());
    }
    
    /**
     * Get a list of search paths for native libraries.
     * 
     * @return a list of paths
     */
    public static Collection<File> getSearchPaths() {
        Set<File> searchPaths = new HashSet<File>();
        
        // Use the path to the central directory for this platform
        searchPaths.add(getNativeLibrariesDir());

        if (isWindows()) {
            File getProgramFiles = getProgramFiles();
            File programFiles32 = getProgramFiles32();

            if (getProgramFiles != null) {
                searchPaths.add(join(getProgramFiles, "VideoLAN", "VLC"));
            }
            
            if (programFiles32 != null) {
                searchPaths.add(join(programFiles32, "VideoLAN", "VLC"));
            }
            
            // Try registry
            String installDir = null;
            try {
                installDir = WinRegistry.readString(WinRegistry.HKEY_LOCAL_MACHINE,
                       "Software\\VideoLAN\\VLC", "InstallDir");
                if (installDir != null) {
                    File file = new File(installDir);
                    if (file.exists()) {
                        searchPaths.add(file);
                    }
                }
            } catch (Throwable t) {
            }
        } else if (isMac()) {
            // This may or may not work
            searchPaths.add(new File("/Applications/VLC.app/Contents/MacOS"));
            searchPaths.add(new File("/Applications/VLC.app/Contents/MacOS/lib"));
        } else {
            searchPaths.add(new File("/lib"));
            searchPaths.add(new File("/usr/local/lib"));
        }
        
        return searchPaths;
    }

}
