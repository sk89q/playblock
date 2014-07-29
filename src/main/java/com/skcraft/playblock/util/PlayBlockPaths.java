package com.skcraft.playblock.util;

import static com.skcraft.playblock.util.EnvUtils.getProgramFiles;
import static com.skcraft.playblock.util.EnvUtils.getProgramFiles32;
import static com.skcraft.playblock.util.EnvUtils.join;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.skcraft.playblock.util.EnvUtils.Arch;

/**
 * Helps manage the various paths required for PlayBlock.
 */
public final class PlayBlockPaths {

    private static final String APP_DIR_NAME = "PlayBlock";

    private PlayBlockPaths() {
    }

    /**
     * Get the path to where Minecraft is stored.
     * 
     * <p>
     * This should not be the "true" path where Minecraft is stored for the
     * current (if applicable) instance, but rather the global one that is used
     * for a vanilla installation.
     * </p>
     * 
     * @return the path to Minecraft
     */
    public static File getMinecraftDir() {
        String homeDir = System.getProperty("user.home", ".");
        String appDir = "minecraft";
        File workingDir;

        switch (EnvUtils.getPlatform()) {
        case LINUX:
        case SOLARIS:
            workingDir = new File(homeDir, "." + appDir + "/");
            break;

        case WINDOWS:
            String applicationData = System.getenv("APPDATA");
            if (applicationData != null)
                workingDir = new File(applicationData, "." + appDir + "/");
            else
                workingDir = new File(homeDir, "." + appDir + "/");
            break;

        case MAC_OS_X:
            workingDir = new File(homeDir, "Library/Application Support/" + appDir);
            break;

        default:
            workingDir = new File(homeDir, appDir + "/");
        }

        return workingDir;
    }

    /**
     * Get the directory where the support files will reside on the system for
     * the current user of this computer.
     * 
     * @return the directory
     */
    public static File getPlayBlockDir() {
        // We use Minecraft's directory to make sure this has a high probability
        // of working without any special privileges
        return new File(getMinecraftDir(), "playblock");
    }

    /**
     * Get the path to the support libraries directory that contains further
     * sub-directories for each architecture.
     * 
     * @return the libraries directory
     */
    public static File getPlayBlockLibsDir() {
        return new File(getPlayBlockDir(), "lib");
    }

    /**
     * Get the path to the support libraries directory that contains the native
     * library files.
     * 
     * @param arch
     *            the architecture
     * @return the libraries directory
     */
    public static File getPlayBlockArchLibsDir(Arch arch) {
        return new File(getPlayBlockLibsDir(), arch.name().toLowerCase());
    }

    /**
     * Get the path to the support libraries directory that contains the native
     * library files, based on the architecture of the running JVM.
     * 
     * @return the libraries directory
     */
    public static File getPlayBlockArchLibsDir() {
        return getPlayBlockArchLibsDir(EnvUtils.getJvmArch());
    }

    /**
     * Returns whether the given path contains an installation of our libraries.
     * 
     * @param dir
     *            the directory
     * @return true if it contains the install
     */
    public static boolean containsInstall(File dir) {
        // Fudge it because we don't store an identifying file (yet)
        return new File(dir, "lib").exists() || new File(dir, "plugins").exists();
    }

    /**
     * Get a list of search paths for native libraries.
     * 
     * @return a list of paths
     */
    public static Collection<File> getSearchPaths() {
        Set<File> searchPaths = new HashSet<File>();

        String useSystemLibs = System.getProperty("playBlock.useSystemLibs", "true");

        // Prefer using the version of VLC that we have installed because we
        // know
        // that it works, and ignore system libraries because they might
        // override our
        // installation and not even work
        File ourInstallDir = getPlayBlockArchLibsDir();
        searchPaths.add(ourInstallDir);
        searchPaths.add(new File(ourInstallDir, "lib"));

        if ((!containsInstall(ourInstallDir) && !useSystemLibs.equalsIgnoreCase("false")) || useSystemLibs.equalsIgnoreCase("force")) {

            switch (EnvUtils.getPlatform()) {
            case WINDOWS:
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
                    installDir = WinRegistry.readString(WinRegistry.HKEY_LOCAL_MACHINE, "Software\\VideoLAN\\VLC", "InstallDir");
                    if (installDir != null) {
                        File file = new File(installDir);
                        if (file.exists()) {
                            searchPaths.add(file);
                        }
                    }
                } catch (Throwable t) {
                }

                break;

            case MAC_OS_X:
                // This may or may not work
                searchPaths.add(new File("/Applications/VLC.app/Contents/MacOS"));
                searchPaths.add(new File("/Applications/VLC.app/Contents/MacOS/lib"));

                break;

            case LINUX:
            case SOLARIS:
            case UNKNOWN:
                searchPaths.add(new File("/lib"));
                searchPaths.add(new File("/usr/local/lib"));
                break;
            }
        }

        return searchPaths;
    }

}
