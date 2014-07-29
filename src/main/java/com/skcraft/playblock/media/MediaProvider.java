package com.skcraft.playblock.media;

import java.io.IOException;

/**
 * Responsible for detecting media and returning relevant media information.
 */
public interface MediaProvider {

    static final int DEFAULT_CONFIDENCE = 100;
    static final int NO_CONFIDENCE = 0;

    /**
     * Get the confidence level of this resolver that is the best provider of
     * metadata for the given URI. A reasonable confidence level would be 100.
     * 
     * @param uri
     *            the uri
     * @return a non-negative integer indicating confidence, with 0 implying no
     *         confidence, and {@link Integer#MAX_VALUE} implying maximum
     *         confidence
     */
    int getConfidence(String uri);

    /**
     * Lookup information about the given URI and return a non-null
     * {@link Media} object describing the media located at the given URI, as
     * best as possible.
     * 
     * @param uri
     *            the URI
     * @return information about the given URI, possibly none
     * @throws IOException
     */
    Media lookup(String uri) throws IOException;

}
