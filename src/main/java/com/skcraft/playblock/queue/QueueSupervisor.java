package com.skcraft.playblock.queue;

import java.io.IOException;

import com.google.common.util.concurrent.ListenableFuture;
import com.skcraft.playblock.media.Media;

/**
 * Handles the not-so-simple adding media files to queues, which includes
 * looking up length information, and possibly other validation tasks.
 */
public interface QueueSupervisor {

    /**
     * Parse a given URI and add the media to a given queue, once length
     * information and other details have been validated.
     * 
     * <p>
     * On success, the returned future will be provided with a {@link Media}
     * that has been added to the queue. Otherwise, the future will contain the
     * exception thrown representing the error.
     * </p>
     * 
     * <p>
     * In theory, the only exception that should be thrown should be an instance
     * of {@link QueueException} or {@link IOException}.
     * </p>
     * 
     * @param queue
     *            the queue to add to
     * @param uri
     *            the URI
     * @return the future result
     */
    ListenableFuture<Media> submit(MediaQueue queue, String uri);

}
