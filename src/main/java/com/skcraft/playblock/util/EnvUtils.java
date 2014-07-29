package com.skcraft.playblock.util;

import java.io.File;

/**
 * System utility functions.
 */
public final class EnvUtils {

    /**
     * List of operating systems.
     */
    public enum Platform {
        WINDOWS, MAC_OS_X, LINUX, SOLARIS, UNKNOWN;
    }

    /**
     * List of CPU architectures.
     */
    public enum Arch {
        X86, X86_64, UNKNOWN;
    }

    private EnvUtils() {
    }

    /**
     * Join several path parts into one {@link File}.
     * 
     * @param base
     *            the base file
     * @param parts
     *            the path segments
     * @return the final path
     */
    public static File join(File base, String... parts) {
        return new File(base, join(parts));
    }

    /**
     * Join several path parts into one {@link File}.
     * 
     * @param parts
     *            the path segments
     * @return the final path
     */
    public static String join(String... parts) {
        StringBuilder b = new StringBuilder();
        boolean first = true;

        for (String part : parts) {
            if (!first) {
                b.append(File.separator);
            }
            b.append(part);
            first = false;
        }

        return b.toString();
    }

    /**
     * Get the path to Windows' Program Files directory.
     * 
     * @return the path or null
     */
    public static File getProgramFiles() {
        String path = System.getenv("ProgramFiles");
        if (path == null) {
            return null;
        }
        return new File(path);
    }

    /**
     * Get the path to Windows' Program Files directory for 32-bit programs on a
     * 64-bit system.
     * 
     * @return the path or null
     */
    public static File getProgramFiles32() {
        String path = System.getenv("ProgramFiles(x86)");
        if (path == null) {
            return null;
        }
        return new File(path);
    }

    /**
     * Get the platform.
     * 
     * @return the platform
     */
    public static Platform getPlatform() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win"))
            return Platform.WINDOWS;
        if (osName.contains("mac"))
            return Platform.MAC_OS_X;
        if (osName.contains("solaris") || osName.contains("sunos"))
            return Platform.SOLARIS;
        if (osName.contains("linux"))
            return Platform.LINUX;
        if (osName.contains("unix"))
            return Platform.LINUX;

        return Platform.UNKNOWN;
    }

    /**
     * Gets the architecture of the JVM.
     * 
     * <p>
     * Right now, this method assumes everything is x86 or x86-64.
     * </p>
     * 
     * @return the architecture
     */
    public static Arch getJvmArch() {
        return System.getProperty("sun.arch.data.model").equals("64") ? Arch.X86_64 : Arch.X86;
    }

}
