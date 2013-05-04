package com.skcraft.playblock.player;

/**
 * Resolves media URIs.
 */
public class MediaResolver {

    /**
     * Checks if a URI is an valid MRL that is also safe.
     * 
     * @param uri the URI
     * @return true if valid
     */
    public static boolean isValidUri(String uri) {
        return uri != null && !uri.isEmpty(); // TODO: Validate URIs
    }

}
