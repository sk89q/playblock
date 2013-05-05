package com.skcraft.playblock.player;

import com.skcraft.playblock.util.Validate;

/**
 * Resolves media URIs.
 */
public class MediaResolver {

    /**
     * Try to clean up poorly copied and pasted URIs.
     * 
     * @param uri the URI
     * @return a cleaned up URI
     */
    public static String cleanUri(String uri) {
        Validate.notNull(uri);
        
        uri = uri.trim();
        
        if (uri.startsWith("\'") && uri.endsWith("\'")) {
            uri = uri.substring(1, uri.length() - 1);
        }
        
        if (uri.startsWith("\"") && uri.endsWith("\"")) {
            uri = uri.substring(1, uri.length() - 1);
        }
        
        // @TODO: Support other quotation marks
        
        uri = uri.trim();
        
        return uri;
    }
    
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
