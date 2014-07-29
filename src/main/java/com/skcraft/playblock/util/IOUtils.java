package com.skcraft.playblock.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * Various I/O utility functions.
 */
public final class IOUtils {

    private IOUtils() {
    }

    /**
     * Resolve an ordinal into a value in an enum.
     * 
     * @param e
     *            the enum class
     * @param ordinal
     *            the ordinal
     * @return the value
     * @throws IOException
     *             if the value doesn't exist
     */
    public static <T extends Enum<?>> T resolveOrdinal(Class<T> e, int ordinal) throws IOException {
        if (ordinal < 0) {
            throw new IOException("Invalid value for " + e.getCanonicalName());
        }
        T[] values = e.getEnumConstants();
        if (ordinal >= values.length) {
            throw new IOException("Invalid value for " + e.getCanonicalName());
        }
        return values[ordinal];
    }

    /**
     * Close something that is {@link Closeable} and ignore all thrown
     * {@link IOException}s.
     * 
     * @param closeable
     *            the closeable, which can be null
     */
    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }

}
