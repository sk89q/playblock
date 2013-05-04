package com.skcraft.playblock.util;

import java.io.File;
import java.net.URISyntaxException;
import java.security.CodeSource;

import com.skcraft.playblock.PlayBlock;

/**
 * System utility functions.
 */
public final class EnvUtils {

    private static final String systemName = System.getProperty("os.name")
            .toLowerCase();

    private EnvUtils() {
    }

    public static File join(File base, String... parts) {
        return new File(base, join(parts));
    }

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

    public static File getBundledLibsDir() {
        try {
            CodeSource codeSource = PlayBlock.class.getProtectionDomain()
                    .getCodeSource();
            File jarFile = new File(codeSource.getLocation().toURI().getPath());
            return new File(jarFile.getParentFile(), "playblock");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isJvm64bit() {
        return System.getProperty("sun.arch.data.model").equals("64");
    }

    public static File getProgramFiles() {
        String path = System.getenv("ProgramFiles");
        if (path == null) {
            return null;
        }
        return new File(path);
    }

    public static File getProgramFiles32() {
        String path = System.getenv("ProgramFiles(x86)");
        if (path == null) {
            return null;
        }
        return new File(path);
    }

    public static boolean isWindows() {
        return (systemName.indexOf("win") >= 0);
    }

    public static boolean isMac() {
        return (systemName.indexOf("mac") >= 0);
    }

    public static boolean isUnix() {
        return (systemName.indexOf("nix") >= 0
                || systemName.indexOf("nux") >= 0 || systemName.indexOf("aix") > 0);
    }

    public static boolean isSolaris() {
        return (systemName.indexOf("sunos") >= 0);
    }

}
