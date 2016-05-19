package com.skcraft.playblock.media;

import com.skcraft.playblock.queue.QueueException;
import com.skcraft.playblock.util.Validate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A media resolver identifies types and media and returns metadata about media
 * when it can be retrieved. An example of metadata would be the length of a
 * clip.
 */
public class MediaResolver {

    private static final boolean ALLOW_ANY_URI = System.getProperty("playBlock.allowAnyUri", "false").equalsIgnoreCase("true");

    // VLC 2.0.x does not actually support RTMP
    private static final Pattern allowedUri = Pattern.compile("^(https?|ftps?|rtp|mmsh?|rtsp(u|t)?|rtmpe?|tcp|udp)://.+$", Pattern.CASE_INSENSITIVE);

    private final List<MediaProvider> providers = new ArrayList<MediaProvider>();

    /**
     * Create a new instance of the media resolver.
     */
    public MediaResolver() {
        // Add default resolvers
        providers.add(new YouTube());
    }

    /**
     * Lookup information about the given URI and return a non-null
     * {@link Media} object describing the media located at the given URI, as
     * best as possible.
     * 
     * @param uri
     *            the URI
     * @return information about the given URI, possibly none
     * @throws IOException
     *             on an error
     * @throws QueueException
     *             on non-I/O error
     */
    public Media lookup(String uri) throws IOException, QueueException {
        Validate.notNull(uri);

        MediaProvider mostConfident = null;
        int topConfidence = 0;
        Media info = null;

        for (MediaProvider provider : providers) {
            int confidence = provider.getConfidence(uri);
            if (confidence > topConfidence) {
                mostConfident = provider;
            }
        }

        if (mostConfident == null) {
            info = new Media(uri);
        } else {
            info = mostConfident.lookup(uri);
            if (info == null) {
                throw new NullPointerException("Got null MediaInfo from " + mostConfident.getClass().getCanonicalName() + "for URI '" + uri + "'");
            }
        }

        return info;
    }

    /**
     * Try to clean up poorly copied and pasted URIs.
     * 
     * @param uri
     *            the URI
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
     * @param uri
     *            the URI
     * @return true if valid
     */
    public static boolean canPlayUri(String uri) {
        Validate.notNull(uri);

        if (uri.isEmpty()) {
            return false;
        }

        // Override with -DplayBlock.allowAnyUri=true or Singleplayer
        if (ALLOW_ANY_URI) { // TODO: Check if single player
            return true;
        }

        return allowedUri.matcher(uri).matches();
    }

}
