package com.skcraft.playblock.queue;

import com.skcraft.playblock.media.Media;

/**
 * Listeners for queue-related events.
 */
public interface QueueListener {

    /**
     * Called when the currently playing media has completed.
     * 
     * @param media
     *            the media object just completed
     */
    void mediaComplete(Media media);

    /**
     * Called when the next media is ready to be played.
     * 
     * @param media
     *            the next media, or null
     */
    void mediaAdvance(Media media);

}
