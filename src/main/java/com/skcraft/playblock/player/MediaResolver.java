package com.skcraft.playblock.player;

import java.util.regex.Pattern;

import com.skcraft.playblock.util.Validate;

/**
 * Resolves media URIs.
 */
public class MediaResolver {
    
    private static final boolean ALLOW_ANY_URI =
            System.getProperty("playBlock.allowAnyUri", "false")
            .equalsIgnoreCase("true");

    // VLC 2.0.x does not actually support RTMP
    private static final Pattern allowedUri = Pattern
            .compile("^(https?|ftps?|rtp|mmsh?|rtsp(u|t)?|rtmpe?|tcp|udp)://.+$",
                    Pattern.CASE_INSENSITIVE);

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
     * Checks if a URI is allowed to play.
     * 
     * @param uri the URI
     * @return true if valid
     */
    public static boolean canPlayUri(String uri) {
        Validate.notNull(uri);
        
        if (uri.isEmpty()) {
            return false;
        }
        
        // Override with -DplayBlock.allowAnyUri=true
        if (ALLOW_ANY_URI) {
            return true;
        }
        
        return allowedUri.matcher(uri).matches(); // TODO: Validate URIs
    }

}
