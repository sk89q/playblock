package com.skcraft.playblock.queue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.TimerTask;

import com.skcraft.playblock.media.Media;
import com.skcraft.playblock.media.PlayingMedia;

/**
 * Handles a queue of media by keeping track of queued media and automatically
 * "advancing" to the next media file after each media file has elapsed its
 * expected length. Media is "advanced" by firing the callbacks on added
 * {@link QueueListener}s.
 */
public class MediaQueue {

    private final QueueManager queueManager;
    private final List<QueueListener> listeners = new ArrayList<QueueListener>();
    private final Queue<Media> queue = new LinkedList<Media>();
    private PlayingMedia playing;
    private MediaAdvancer timerTask;

    /**
     * Construct a new media queue.
     * 
     * @param queueManager
     *            the queue manager
     */
    MediaQueue(QueueManager queueManager) {
        this.queueManager = queueManager;
    }

    /**
     * Add a listener for events.
     * 
     * @param listener
     *            the listener
     */
    public synchronized void addQueueListener(QueueListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove a listener. If the listener isn't registered, then nothing will
     * happen.
     * 
     * @param listener
     *            the listener
     */
    public synchronized void removeQueueListener(QueueListener listener) {
        listeners.remove(listener);
    }

    /**
     * Fire {@link QueueListener#mediaComplete(Media)} events.
     * 
     * @param media
     *            the media
     */
    private synchronized void fireMediaComplete(Media media) {
        for (QueueListener listener : listeners) {
            listener.mediaComplete(media);
        }
    }

    /**
     * Fire {@link QueueListener#mediaAdvance(Media)} events.
     * 
     * @param media
     *            the media, or null
     */
    private synchronized void fireMediaAdvance(Media media) {
        for (QueueListener listener : listeners) {
            listener.mediaAdvance(media);
        }
    }

    /**
     * Advance to the next media and "complete" the currently playing media.
     * 
     * @param media
     *            the media, null to not play more media
     */
    private synchronized void advanceTo(Media media) {
        if (playing != null) {
            if (timerTask != null) {
                timerTask.cancel();
            }
            fireMediaComplete(playing.getMedia());
            playing = null;
        }

        if (media != null) {
            playing = PlayingMedia.fromNow(media);
            queueManager.getQueueTimer().schedule(timerTask = new MediaAdvancer(), media.getLength());
        }

        fireMediaAdvance(media);
    }

    /**
     * Advance to the next media in the queue, or stop playing if there's
     * nothing playing.
     */
    private synchronized void advanceNext() {
        advanceTo(queue.poll());
    }

    /**
     * Add media to the queue, or play immediately if the queue was empty.
     * 
     * <p>
     * The passed media must have its length known.
     * </p>
     * 
     * @param media
     *            the media
     */
    public synchronized void add(Media media) {
        if (media.getLength() == null) {
            throw new RuntimeException("Can't add media with unknown length");
        }

        if (playing == null) {
            advanceTo(media);
        } else {
            queue.add(media);
        }
    }

    /**
     * Get the currently playing media.
     * 
     * @return the currently playing media, or null if nothing is playing
     */
    public synchronized PlayingMedia getCurrentMedia() {
        return playing;
    }

    /**
     * Mark this queue for release, indicating that it is no longer to be used.
     * It is important that this method is called in order to release resources.
     * 
     * <p>
     * This will not fire any events on added {@link QueueListener}s.
     * </p>
     */
    public synchronized void release() {
        if (timerTask != null) {
            timerTask.cancel();
        }
        playing = null;
    }

    /**
     * Used to advance the media after the duration of the currently "playing"
     * media has elapsed.
     */
    private class MediaAdvancer extends TimerTask {
        @Override
        public void run() {
            advanceNext();
        }
    }

}
